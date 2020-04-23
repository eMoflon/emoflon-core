package org.moflon.core.utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

/**
 * Utility methods for working with EMF
 *
 * @author Anthony Anjorin
 * @author Gergely Varró
 * @author Roland Kluge
 */
public class eMoflonEMFUtil {
	private static final Logger logger = Logger.getLogger(eMoflonEMFUtil.class);

	private static Map<EClassifier, String> clazzNameCache = new HashMap<EClassifier, String>();

	/**
	 * Disabled utility class constructor
	 */
	private eMoflonEMFUtil() {
		throw new UtilityClassNotInstantiableException();
	}

	/**
	 * Creates a {@link ResourceSetImpl} and performs
	 * {@link #initializeDefault(ResourceSetImpl)} on it.
	 *
	 * @return the resource set
	 */
	public static final ResourceSet createDefaultResourceSet() {
		final ResourceSetImpl set = new ResourceSetImpl();
		initializeDefault(set);
		return set;
	}

	/**
	 * Performs the default initialization of the given {@link ResourceSet}
	 *
	 * @param set
	 *            the {@link ResourceSet} to initialize
	 */
	public static void initializeDefault(final ResourceSet set) {
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
				new XMIResourceFactoryImpl());
	}

	/**
	 * Adds an {@link ECrossReferenceAdapter} to the adapters of the given
	 * {@link ResourceSet} if no adapter exists, yet.
	 *
	 * @param resourceSet
	 *            the resource set to be adapted.
	 */
	public static final void installCrossReferencers(final ResourceSet resourceSet) {
		// Add adapter for reverse navigation along unidirectional links
		ECrossReferenceAdapter adapter = ECrossReferenceAdapter.getCrossReferenceAdapter(resourceSet);
		if (adapter == null) {
			try {
				resourceSet.eAdapters().add(new ECrossReferenceAdapter());
			} catch (Exception e) {
				LogUtils.error(logger, e);
			}
		}
	}

	/**
	 * This method ensures that the given EPackage can be used for reading and
	 * writing XMI files. This is required before loading/saving or working with the
	 * package. In a plugin context, this might be automatically carried out via an
	 * appropriate extension point.
	 *
	 * Alternatively, you may simply pass the eINSTANCE static attribute of the
	 * tailored EPackage to a method, or use the standard EMF package registering
	 * mechanism
	 * {@see org.eclipse.emf.ecore.resource.ResourceSet#getPackageRegistry()}
	 */
	public static void init(final EPackage metamodel) {
		metamodel.getName();
	}

	/**
	 * Utility method to get a resource from a file.
	 *
	 * This method delegates to
	 * {@link #getResourceFromFileIntoDefaultResourceSet(String)} using
	 * {@link IFile#getLocation()}.
	 *
	 * @see #getResourceFromFileIntoDefaultResourceSet(String)
	 */
	public static Resource getResourceFromFileIntoDefaultResourceSet(final IFile modelFile) {
		return getResourceFromFileIntoDefaultResourceSet(modelFile.getLocation().toString());
	}

	/**
	 * Utility method to get a resource from a file into a fresh {@link ResourceSet}
	 *
	 * This method behaves equivalently to the follow code instructions:
	 *
	 * <pre>
	 * final ResourceSet resourceSet = eMoflonEMFUtil.createDefaultResourceSet();
	 * return getResourceFromFileIntoResourceSet(pathToModelFile, resourceSet);
	 * </pre>
	 *
	 * @param pathToModelFile
	 *            relative or absolute path to an XMI file
	 * @return the loaded resource
	 */
	public static Resource getResourceFromFileIntoDefaultResourceSet(final String pathToModelFile) {
		final ResourceSet resourceSet = eMoflonEMFUtil.createDefaultResourceSet();
		return getResourceFromFileIntoResourceSet(pathToModelFile, resourceSet);
	}

	/**
	 * Utility method to get a resource from a file into a given {@link ResourceSet}
	 *
	 * The method returns a {@link Resource} that results from the following
	 * initialization sequence:
	 *
	 * <pre>
	 * installCrossReferencers(resourceSet);
	 * Resource resource = resourceSet.getResource(eMoflonEMFUtil.createFileURI(pathToModelFile, true), true);
	 * </pre>
	 *
	 * @param pathToModelFile
	 *            relative or absolute path to an XMI file
	 * @param resourceSet
	 *            the resource set to use
	 * @return the loaded resource
	 */
	public static Resource getResourceFromFileIntoResourceSet(final String pathToModelFile,
			final ResourceSet resourceSet) {
		final URI uri = eMoflonEMFUtil.createFileURI(pathToModelFile, true);

		return getResourceFromFileIntoResourceSet(uri, resourceSet);
	}

	/**
	 * Utility method to get a resource from a file into a given {@link ResourceSet}
	 *
	 * The method returns a {@link Resource} that results from the following
	 * initialization sequence:
	 *
	 * <pre>
	 * installCrossReferencers(resourceSet);
	 * Resource resource = resourceSet.getResource(eMoflonEMFUtil.createFileURI(pathToModelFile, true), true);
	 * </pre>
	 *
	 * @param uri
	 *            the {@link URI} of the resource
	 * @param resourceSet
	 *            the resource set to use
	 *
	 * @return the loaded resource
	 */
	public static Resource getResourceFromFileIntoResourceSet(final URI uri, final ResourceSet resourceSet) {
		installCrossReferencers(resourceSet);

		final Resource resource = resourceSet.getResource(uri, true);

		return resource;
	}

	/**
	 * Loads a model if its metamodel has been initialized already and there are no
	 * dependencies to other models.
	 *
	 * The result of this method is equivalent to (i) creating a fresh ResourceSet
	 * ({@link #createDefaultResourceSet()}), (ii) calling
	 * {@link #loadModel(String, ResourceSet)} with the given path and the fresh
	 * {@link ResourceSet}
	 *
	 * @param pathToXMIFile
	 *            absolute or relative path to XMI file.
	 * @return the root element of the loaded model
	 * @throws RuntimeException
	 *             if there is no resource to load
	 */
	public static EObject loadModel(final String pathToXMIFile) {
		final ResourceSet resourceSet = eMoflonEMFUtil.createDefaultResourceSet();
		return loadModel(pathToXMIFile, resourceSet);
	}

	/**
	 * Loads a model if its metamodel has been initialized already and there are no
	 * dependencies to other models.
	 *
	 * The result of this method is equivalent to (i) calling
	 * {@link #getResourceFromFileIntoResourceSet(String, ResourceSet)} with the
	 * given path and {@link ResourceSet}, (ii) returning the first element of
	 * {@link Resource#getContents()}.
	 *
	 * @param pathToXMIFile
	 *            absolute or relative path to XMI file.
	 * @param resourceSet
	 *            the resource set to use
	 * @return the root element of the loaded model
	 * @throws RuntimeException
	 *             if there is no resource to load
	 */
	public static EObject loadModel(final String pathToXMIFile, final ResourceSet resourceSet) {
		final Resource resource = getResourceFromFileIntoResourceSet(pathToXMIFile, resourceSet);
		if (resource == null) {
			throw new IllegalArgumentException(String.format("Cannot load resource from %s", pathToXMIFile));
		} else if (resource.getContents().isEmpty()) {
			return null;
		} else {
			return resource.getContents().get(0);
		}
	}

	/**
	 * Loads a model if its metamodel has been initialized already and there are no
	 * dependencies to other models.
	 *
	 * The result of this method is equivalent to (i) calling
	 * {@link #getResourceFromFileIntoResourceSet(String, ResourceSet)} with the
	 * given path and {@link ResourceSet}, (ii) returning the first element of
	 * {@link Resource#getContents()}.
	 *
	 * @param pathToXMIFile
	 *            absolute or relative path to XMI file.
	 * @param resourceSet
	 *            the resource set to use
	 * @return the root element of the loaded model
	 * @throws RuntimeException
	 *             if there is no resource to load
	 */
	public static EObject loadModel(final URI uri, final ResourceSet resourceSet) {
		final Resource resource = getResourceFromFileIntoResourceSet(uri, resourceSet);
		if (resource == null) {
			throw new IllegalArgumentException(String.format("Cannot load resource from %s", uri));
		} else if (resource.getContents().isEmpty()) {
			return null;
		} else {
			return resource.getContents().get(0);
		}
	}

	/**
	 * Loads a model, initializes its metamodel and resolves dependencies from a Jar
	 * file.
	 *
	 * The URI to load the model from is constructed as follows: Given a
	 * "C:/Users/user/lib.jar" as path to the jar and "/model/myModel.xmi", the
	 * constructed URI is "jar:file:C:/Users/user/lib.jar!/model/myModel.jar".
	 *
	 * @param metamodel
	 *            the metamodel that should be initialized
	 * @param pathToJarFile
	 *            path to the jar file (absolute or relative)
	 * @param pathToResourceInJarFile
	 *            path to the model inside the jar
	 * @param resourceSet
	 *            contains other models to resolve dependencies
	 * @return the root element of the loaded model
	 */
	public static EObject loadModelFromJarFile(final EPackage metamodel, final String pathToJarFile,
			final String pathToResourceInJarFile, final ResourceSet resourceSet) {
		final URI uri = createInJarURI(pathToJarFile, pathToResourceInJarFile);
		return eMoflonEMFUtil.loadModel(uri, resourceSet);
	}

	/**
	 * Creates an URI for resources inside a Jar file.
	 *
	 * The created URI is equivalent to
	 * "jar:file:[pathToJarFile]![pathToResourceInJarFile]".
	 *
	 * @param pathToJarFile
	 *            path to the jar file (absolute or relative)
	 * @param pathToResourceInJarFile
	 *            path to resource inside the jar
	 * @return the built URI
	 */
	public static URI createInJarURI(final String pathToJarFile, final String pathToResourceInJarFile) {
		return URI.createURI("jar:file:" + pathToJarFile + "!" + pathToResourceInJarFile);
	}

	/**
	 * Use to save a model to the given XMI file path, but only if it has changed.
	 *
	 * @param resourceSet
	 * @param rootElementOfModel
	 * @param pathToXMIFile
	 *            Absolute or relative path to XMI file in which to save the model.
	 */
	static public void saveModel(final ResourceSet resourceSet, final EObject rootElementOfModel,
			final String pathToXMIFile) {
		final URI fileURI = createFileURI(pathToXMIFile, false);

		Resource resource = rootElementOfModel.eResource();
		if (resource == null) {
			// Create a default resource
			resource = resourceSet.createResource(fileURI);
			resource.getContents().add(rootElementOfModel);
		}

		final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
		saveOptions.put(Resource.OPTION_LINE_DELIMITER, WorkspaceHelper.DEFAULT_RESOURCE_LINE_DELIMITER);

		if (fileURI.equals(resource.getURI())) {
			try {
				resource.save(saveOptions);
			} catch (final IOException e) {
				LogUtils.error(logger, e);
			}
		} else {
			Map<URI, URI> uriMapping = resource.getResourceSet().getURIConverter().getURIMap();
			uriMapping.put(resource.getURI(), fileURI);
			try {
				resource.save(saveOptions);
			} catch (final IOException e) {
				LogUtils.error(logger, e);
			}
			uriMapping.remove(resource.getURI());
		}
	}

	/**
	 * Use to save a model to the given XMI file path.
	 *
	 * This method is useful to save resources for inspection and debugging.
	 *
	 * @param rootElementOfModel
	 *            the root element to be placed in the contents of the created
	 *            temporary {@link Resource}
	 * @param pathToXMIFile
	 *            absolute or relative path to XMI file in which to save the model.
	 */
	static public void saveModel(final EObject rootElementOfModel, final String pathToXMIFile) {
		saveModel(createDefaultResourceSet(), rootElementOfModel, pathToXMIFile);
	}

	/**
	 * Create and return a file URI for the given path.
	 *
	 * @param pathToXMIFile
	 * @param mustExist
	 *            true when loading (i.e., the file must exist) and false when
	 *            saving (i.e., the file may be newly created).
	 * @return the preconfigured URI
	 * @throws IllegalArgumentException
	 *             is mustExist is true and no file can be found at the given path
	 */
	static public URI createFileURI(final String pathToXMIFile, final boolean mustExist) {
		final File filePath = new File(pathToXMIFile);

		if (!filePath.exists() && mustExist)
			throw new IllegalArgumentException(pathToXMIFile + " does not exist.");

		return URI.createFileURI(filePath.getAbsolutePath());
	}

	/**
	 * If possible, use
	 * {@link eMoflonEMFUtil#getOppositeReferenceTyped(EObject, Class, String)}
	 */
	public static List<?> getOppositeReference(final EObject target, final Class<?> sourceType,
			final String targetRoleName) {
		Collection<Setting> settings = getInverseReferences(target);

		List<EObject> returnList = new ArrayList<EObject>();
		for (Setting setting : settings) {
			EObject candidate = getCandidateObject(sourceType, targetRoleName, setting);
			if (candidate != null)
				returnList.add(candidate);
		}

		EObject eContainer = target.eContainer();
		if (eContainer != null) {
			Setting setting = (((InternalEObject) eContainer).eSetting(target.eContainmentFeature()));
			EObject candidate = getCandidateObject(sourceType, targetRoleName, setting);
			if (candidate != null)
				returnList.add(candidate);
		}

		return returnList;
	}

	/**
	 * This method only works when you have registered an appropriate adapter right
	 * after loading your model! Further documentation can be found here:
	 * http://sdqweb.ipd.kit.edu/wiki/EMF_Reverse_Lookup_/
	 * _navigating_unidirectional_references_bidirectional
	 *
	 * @param target
	 *            the target of this reference
	 * @param sourceType
	 *            the type of the opposite objects you are looking for
	 * @return a list of all opposite objects
	 */
	@SuppressWarnings("unchecked")
	public static <T extends EObject> List<T> getOppositeReferenceTyped(final EObject target, final Class<T> sourceType,
			final String targetRoleName) {
		Collection<Setting> settings = getInverseReferences(target);

		List<T> returnList = new ArrayList<T>();
		for (Setting setting : settings) {
			EObject candidate = getCandidateObject(sourceType, targetRoleName, setting);
			if (candidate != null)
				returnList.add((T) candidate);
		}

		EObject eContainer = target.eContainer();
		if (eContainer != null) {
			Setting setting = (((InternalEObject) eContainer).eSetting(target.eContainmentFeature()));
			EObject candidate = getCandidateObject(sourceType, targetRoleName, setting);
			if (candidate != null)
				returnList.add((T) candidate);
		}

		return returnList;
	}

	public static List<EStructuralFeature> getAllOppositeReferences(final EObject target) {
		Collection<Setting> settings = getInverseReferences(target);

		List<EStructuralFeature> returnList = new ArrayList<>();
		for (Setting setting : settings) {
			EStructuralFeature candidate = setting.getEStructuralFeature();
			if (candidate != null)
				returnList.add(candidate);
		}

		EObject eContainer = target.eContainer();
		if (eContainer != null) {
			Setting setting = (((InternalEObject) eContainer).eSetting(target.eContainmentFeature()));
			EStructuralFeature candidate = setting.getEStructuralFeature();
			if (candidate != null)
				returnList.add(candidate);
		}

		return returnList;
	}

	/**
	 * Utility method for deleting an {@link EObject}
	 *
	 * Equivalent to {@link EcoreUtil#delete(EObject, boolean)}, using
	 * <code>true</code> for the second parameter
	 *
	 * @param object
	 *            the object to remove
	 */
	public static void remove(final EObject object) {
		EcoreUtil.delete(object, true);
	}

	/**
	 * This method removes objects more efficiently than {@link #remove(EObject)}
	 */
	public static void unsetAllReferences(final EObject object) {
		for (EStructuralFeature feature : getAllReferences(object)) {
			object.eUnset(feature);
		}

		ArrayList<Setting> settings = new ArrayList<>();
		settings.addAll(getInverseReferences(object));
		for (Setting setting : settings) {
			EStructuralFeature feature = setting.getEStructuralFeature();
			removeOppositeReference(setting.getEObject(), object, feature.getName());
		}

		if (object.eContainer() != null) {
			removeOppositeReference(object.eContainer(), object, object.eContainmentFeature().getName());
		}

	}

	@SuppressWarnings({ "unchecked" })
	public static void addOppositeReference(final EObject source, final EObject target, final String targetRole) {
		EStructuralFeature reference = source.eClass().getEStructuralFeature(targetRole);
		if (!reference.isMany()) {
			source.eSet(reference, target);
		} else
			((Collection<EObject>) source.eGet(reference)).add(target);
	}

	public static void removeOppositeReference(final EObject source, final EObject target, final String targetRole) {
		removeEdge(source, target, targetRole);
	}

	public static void removeEdge(final EObject source, final EObject target, final String targetRole) {
		EStructuralFeature reference = source.eClass().getEStructuralFeature(targetRole);
		if (!reference.isMany()) {
			source.eSet(reference, null);
		} else {
			((Collection<?>) source.eGet(reference)).remove(target);
		}
	}

	/**
	 * Returns the cross reference (not containment reference) in the containment
	 * tree below 'obj' whose name attribute equals 'name'.
	 */
	public static EStructuralFeature getReference(final EObject obj, final String name) {
		for (EContentsEList.FeatureIterator<?> featureIterator = (EContentsEList.FeatureIterator<?>) obj
				.eCrossReferences().iterator(); featureIterator.hasNext();) {
			featureIterator.next();
			EReference eReference = (EReference) featureIterator.feature();
			if (eReference.getName().equals(name))
				return eReference;
		}

		return null;
	}

	/**
	 * Returns the containment reference in the containment tree below 'container'
	 * whose name attribute equals 'name'.
	 */
	public static EStructuralFeature getContainment(final EObject container, final String name) {
		for (EContentsEList.FeatureIterator<?> featureIterator = (EContentsEList.FeatureIterator<?>) container
				.eContents().iterator(); featureIterator.hasNext();) {
			featureIterator.next();
			EReference eReference = (EReference) featureIterator.feature();
			if (eReference.getName().equals(name))
				return eReference;
		}

		return null;
	}

	/**
	 * Calculates the set of all outgoing references of a given EObject.
	 *
	 * @param object
	 * @return set of references
	 */
	public static Set<EStructuralFeature> getAllReferences(final EObject object) {
		final EList<EStructuralFeature> references = new BasicEList<>();

		// Collect outgoing cross references - excluding containment edges
		for (EContentsEList.FeatureIterator<?> featureIterator = (EContentsEList.FeatureIterator<?>) object
				.eCrossReferences().iterator(); featureIterator.hasNext();) {
			featureIterator.next();
			references.add(featureIterator.feature());
		}

		// Collect outgoing containment edges
		for (EContentsEList.FeatureIterator<?> featureIterator = (EContentsEList.FeatureIterator<?>) object.eContents()
				.iterator(); featureIterator.hasNext();) {
			featureIterator.next();
			references.add(featureIterator.feature());
		}
		return new HashSet<EStructuralFeature>(references);
	}

	/**
	 * Extracts a name from the given {@code child}.
	 *
	 * If {@code child} is <code>null</code>, the result is empty.
	 *
	 * @param child
	 * @return
	 */
	public static String getName(final EObject child) {
		if (child == null)
			return "null";

		final EStructuralFeature nameFeature = child.eClass().getEStructuralFeature("name");

		Object name = "";
		if (nameFeature != null)
			name = child.eGet(nameFeature);

		if (name instanceof String && !name.equals(""))
			return (String) name;
		else
			return child.toString();
	}

	/**
	 * Builds an identifier String for the given EObject. This identifier starts
	 * with
	 * <ul>
	 * <li>the attribute of the EObject as a String, if the EObject does only have
	 * one attribute.</li>
	 * <li>the attribute called 'name' of the EObject, if it has such an
	 * attribute</li>
	 * <li>any attribute of the EObject, but String attributes are preferred</li>
	 * </ul>
	 * The identifier ends with " : " followed by the type of the EObject. <br>
	 * Example: A MocaTree Node with the name "foo" will result in "foo : Node" <br>
	 * If the EObject does not have any attributes or all attributes have the value
	 * null, this function will only return the type of the EObject.
	 */
	public static String getIdentifier(final EObject eObject) {
		boolean success = false;
		List<EAttribute> attributes = eObject.eClass().getEAllAttributes();
		StringBuilder identifier = new StringBuilder();

		success = tryGetSingleAttribute(eObject, attributes, identifier);

		if (!success)
			success = tryGetNameAttribute(eObject, attributes, identifier);

		if (!success)
			success = tryGetAnyAttribute(eObject, attributes, identifier);

		if (success)
			identifier.append(" : ");

		identifier.append(eObject.eClass().getName());

		return identifier.toString();
	}

	/**
	 * Creates a new resource for the given {@link EObject}. The URI of the new
	 * resource is the NS URI of its containing {@link EPackage}. The resource is
	 * added to the given {@link ResourceSet} afterwards.
	 *
	 * @param object
	 *            the object to insert
	 * @param set
	 *            the resource set that shall contain the given object
	 *
	 * @throws IllegalArgumentException
	 *             if the object is already inside a {@link Resource}
	 */
	public static void createParentResourceAndInsertIntoResourceSet(final EObject object, final ResourceSet set) {
		if (object.eResource() != null) {
			throw new IllegalStateException("The given object must not be inside a resource yet");
		}

		final Resource resource = new ResourceImpl();
		resource.setURI(URI.createURI(object.eClass().getEPackage().getNsURI()));
		resource.getContents().add(object);
		set.getResources().add(resource);
	}

	/**
	 * Transforms each {@link Diagnostic} message to an {@link IStatus} message and
	 * returns all of them as result
	 *
	 * @param resourceSet
	 *            the {@link ResourceSet} from which to collect the
	 *            {@link Diagnostic}s
	 * @param taskName
	 *            the current task
	 * @param monitor
	 *            the {@link IProgressMonitor}
	 * @return the converted {@link IStatus}
	 */
	public static final IStatus validateResourceSet(final ResourceSet resourceSet, final String taskName,
			final IProgressMonitor monitor) {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Checking errors in the " + taskName + " task",
				resourceSet.getResources().size());
		final MultiStatus status = new MultiStatus(WorkspaceHelper.getPluginId(eMoflonEMFUtil.class), IStatus.OK,
				taskName + " failed", null);
		for (final Resource resource : resourceSet.getResources()) {
			for (final Diagnostic diagnostic : resource.getErrors()) {
				final Exception exception = diagnostic instanceof Exception ? (Exception) diagnostic : null;
				status.add(new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(eMoflonEMFUtil.class), IStatus.ERROR,
						diagnostic.getMessage(), exception));
			}
			subMon.worked(1);
		}
		return status;
	}

	/**
	 * Determines the EMF {@link URI} of the given project
	 *
	 * If the project is an Eclipse plugin, a platform:/plugin URI is returned.
	 * Otherwise, a platform:/resource URI is returned
	 *
	 * @param project
	 *            the project
	 * @return the {@link URI} of the project
	 */
	public static final URI lookupProjectURI(final IProject project) {
		final IPluginModelBase pluginModel = PluginRegistry.findModel(project);
		final boolean isPluginProject = pluginModel != null;
		if (isPluginProject) {
			final String pluginID = extractSymbolicName(pluginModel);
			return URI.createPlatformPluginURI(pluginID + "/", true);
		} else {
			return URI.createPlatformResourceURI(project.getName() + "/", true);
		}
	}

	/**
	 * Determines the EMF {@link URI} of the given project, always using platform:/resource
	 * URI as default
	 *
	 * @param project
	 *            the project
	 * @return the {@link URI} of the project
	 */
	public static final URI lookupProjectURIAsPlatformResource(final IProject project) {
		return URI.createPlatformResourceURI(project.getName() + "/", true);
	}

	/**
	 * Returns the project in the workspace having the given URI
	 *
	 * @param namespaceURI
	 *            the URI of the project
	 * @return the project or <code>null</code> if no project exists for the given
	 *         URI
	 */
	public static final IProject getWorkspaceProject(final URI namespaceURI) {
		if (namespaceURI.segmentCount() < 2) {
			throw new IllegalArgumentException(
					String.format("Unsupported URI: %s. Must have at least two segments.", namespaceURI));
		}
		if (!namespaceURI.isPlatformPlugin() && !namespaceURI.isPlatformResource()) {
			throw new IllegalArgumentException(String.format(
					"Unsupported URI: %s. Must have type platform:/resource or platform:/plugin.", namespaceURI));
		}

		if (namespaceURI.isPlatformResource()) {
			return ResourcesPlugin.getWorkspace().getRoot().getProject(namespaceURI.segment(1));
		}
		if (namespaceURI.isPlatformPlugin()) {
			for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
				IPluginModelBase pluginModel = PluginRegistry.findModel(project);
				if (pluginModel != null && namespaceURI.segment(1).equals(extractSymbolicName(pluginModel))) {
					return project;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the number of elements in the containment tree routed at 'model'.
	 *
	 * The resulting count is equivalent to the maximum number of invocations of for
	 * the {@link Iterator} returned by {@link EObject#eAllContents()}
	 *
	 * @param model
	 *            the model of which the elements shall be counted
	 * @return the element count
	 */
	public static int getNodeCount(final EObject model) {
		int i = 0;
		TreeIterator<EObject> iterator = model.eAllContents();
		while (iterator.hasNext()) {
			i++;
			iterator.next();
		}

		return i;
	}

	/**
	 * Returns the number of {@link EReference}s in the given {@code model}.
	 *
	 * The resulting count is equivalent to adding up the results for
	 * {@link #getAllReferences(EObject)} for all elements below the given element
	 * (see {@link EObject#eAllContents()}
	 *
	 * @param model
	 *            the model to traverse
	 * @return the number of {@link EReference}s
	 */
	public static int getEdgeCount(final EObject model) {

		int i = 0;
		TreeIterator<EObject> iterator = model.eAllContents();
		while (iterator.hasNext()) {
			EObject node = iterator.next();
			Set<EStructuralFeature> references = eMoflonEMFUtil.getAllReferences(node);
			i += references.size();
		}
		return i;
	}

	/**
	 * Returns all {@link EClass} (recursively) of the given {@link EPackage}.
	 *
	 * @param ePackage
	 *            the package
	 * @return the list of all {@link EClass}
	 */
	public static final List<EClass> getEClasses(final EPackage ePackage) {
		final List<EClass> result = new LinkedList<EClass>();
		for (final TreeIterator<EObject> iterator = ePackage.eAllContents(); iterator.hasNext();) {
			final EObject eObject = iterator.next();
			if (EcorePackage.eINSTANCE.getEClass().isInstance(eObject)) {
				result.add((EClass) eObject);
				iterator.prune();
			}
		}
		return result;
	}

	/**
	 * Adds a mapping from platform:/plugin to platform:/resource for the given
	 * {@link IProject} to the given {@link ResourceSet}
	 *
	 * @param set
	 *            the {@link ResourceSet} to update
	 * @param project
	 *            the project for which the mapping shall be added
	 * @throws CoreException
	 *             if the project's metadata cannot be read
	 */
	public static final void createPluginToResourceMapping(final ResourceSet set, final IProject project)
			throws CoreException {
		if (project.isAccessible() && project.hasNature(WorkspaceHelper.PLUGIN_NATURE_ID)) {
			final IPluginModelBase pluginModel = findPluginModel(project);
			if (pluginModel != null) {
				final String pluginID = extractSymbolicName(pluginModel);
				final URI pluginURI = URI.createPlatformPluginURI(pluginID + "/", true);
				final URI resourceURI = URI.createPlatformResourceURI(project.getName() + "/", true);
				set.getURIConverter().getURIMap().put(pluginURI, resourceURI);
			}
		}
	}

	/**
	 * Adds a mapping from platform:/plugin to platform:/resource for all projects
	 * in the workspace to the given {@link ResourceSet} Invokes
	 * {@link #createPluginToResourceMapping(ResourceSet, IProject)} for each
	 * project in the workspace
	 *
	 * @param set
	 *            the resource set to update
	 * @param monitor
	 *            the progress monitor
	 * @throws CoreException
	 *             if the conversion fails for some project
	 */
	public static final void createPluginToResourceMapping(final ResourceSet set, final IProgressMonitor monitor)
			throws CoreException {
		final IProject[] workspaceProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		final SubMonitor subMon = SubMonitor.convert(monitor, "Register plugin to resource mapping",
				workspaceProjects.length);
		for (final IProject project : workspaceProjects) {
			createPluginToResourceMapping(set, project);
			subMon.worked(1);
		}
	}

	/**
	 * Convenience method for
	 * {@link #createPluginToResourceMapping(ResourceSet, IProgressMonitor)} without
	 * progress monitor
	 *
	 * @param set
	 *            the resource set
	 * @throws CoreException
	 *             if the conversion fails for some project
	 */
	public static final void createPluginToResourceMapping(final ResourceSet set) throws CoreException {
		createPluginToResourceMapping(set, new NullProgressMonitor());
	}

	/**
	 * @param name
	 *            Use an empty StringBuilder as input. If this function returns
	 *            true, this parameter has been filled, if it returns false, nothing
	 *            happened.
	 * @return Indicates the success of this function and if the last parameter
	 *         contains output.
	 */
	private static boolean tryGetSingleAttribute(final EObject eObject, final List<EAttribute> attributes,
			final StringBuilder name) {
		boolean success = false;
		if (attributes.size() == 1) {
			Object obj = eObject.eGet(attributes.get(0));
			if (obj != null) {
				name.append(obj.toString());
				success = true;
			}
		}
		return success;
	}

	/**
	 * @param name
	 *            Use an empty StringBuilder as input. If this function returns
	 *            true, this parameter has been filled, if it returns false, nothing
	 *            happened.
	 * @return Indicates the success of this function and if the last parameter
	 *         contains output.
	 */
	private static boolean tryGetNameAttribute(final EObject eObject, final List<EAttribute> attributes,
			final StringBuilder name) {
		boolean success = false;
		for (EAttribute feature : attributes) {
			if (feature.getName().equals("name")) {
				Object obj = eObject.eGet(feature);
				if (obj != null) {
					name.append(obj.toString());
					success = true;
					break;
				}
			}
		}
		return success;
	}

	/**
	 * @param name
	 *            Use an empty StringBuilder as input. If this function returns
	 *            true, this parameter has been filled, if it returns false, nothing
	 *            happened.
	 * @return Indicates the success of this function and if the last parameter
	 *         contains output.
	 */
	private static boolean tryGetAnyAttribute(final EObject eObject, final List<EAttribute> attributes,
			final StringBuilder name) {
		boolean success = false;
		String nonStringName = null;
		String stringName = null;
		for (EAttribute feature : attributes) {
			Object obj = eObject.eGet(feature);
			if (obj == null)
				continue;
			if (obj instanceof String) {
				stringName = (String) obj;
				break;
			} else {
				nonStringName = obj.toString();
			}
		}
		if (stringName != null && !stringName.equals("null")) {
			name.append(stringName);
			success = true;
		} else if (nonStringName != null && !nonStringName.equals("null")) {
			name.append(nonStringName);
			success = true;
		}
		return success;
	}

	private static EObject getCandidateObject(final Class<?> sourceType, final String targetRoleName,
			final Setting setting) {
		if (setting.getEStructuralFeature().getName().equals(targetRoleName)) {
			EClassifier clazz = setting.getEObject().eClass();
			String clazzName = getClazzNameWithPackagePrefix(clazz);

			if (clazzName.equals(sourceType.getName()) || checkInheritance(sourceType, clazz))
				return setting.getEObject();
		}

		return null;
	}

	private static Collection<Setting> getInverseReferences(final EObject target) {
		ECrossReferenceAdapter adapter = getCRAdapter(target);
		return adapter.getNonNavigableInverseReferences(target, true);
	}

	private static String getClazzNameWithPackagePrefix(final EClassifier clazz) {
		String clazzName = clazzNameCache.get(clazz);

		if (clazzName == null) {
			clazzName = clazz.getInstanceClass().getPackage().getName() + "." + clazz.getName();
			clazzNameCache.put(clazz, clazzName);
		}
		return clazzName;
	}

	private static boolean checkInheritance(final Class<?> superclass, final EClassifier subclass) {
		for (EClass sup : ((EClass) subclass).getEAllSuperTypes()) {
			String clazzName = getClazzNameWithPackagePrefix(sup);
			if (clazzName.equals(superclass.getName()))
				return true;
		}
		return false;
	}

	private static ECrossReferenceAdapter getCRAdapter(final EObject target) {
		// Determine context
		Notifier context = null;

		EObject root = EcoreUtil.getRootContainer(target, true);
		Resource resource = root.eResource();

		if (resource != null) {
			ResourceSet resourceSet = resource.getResourceSet();
			if (resourceSet != null)
				context = resourceSet;
			else
				context = resource;
		} else
			context = root;

		// Retrieve adapter and create+add on demand
		ECrossReferenceAdapter adapter = ECrossReferenceAdapter.getCrossReferenceAdapter(context);
		if (adapter == null) {
			adapter = new ECrossReferenceAdapter();
			context.eAdapters().add(adapter);
		}

		return adapter;
	}

	/**
	 * Extracts the symbolic name of the bundle described by the given
	 * {@link IPluginModelBase}
	 *
	 * @param pluginModel
	 *            the plugin model
	 * @return the symbolic name
	 * @throws Exception if the symbolic name cannot be extracted from the model
	 */
	private static String extractSymbolicName(final IPluginModelBase pluginModel) {
		if (pluginModel == null || pluginModel.getBundleDescription() == null) {
//			throw new IllegalArgumentException("Cannot extract symbolic name from bundle.");
			return pluginModel.toString();
		}

		return pluginModel.getBundleDescription().getSymbolicName();
	}

	/**
	 * Extracts the {@link IPluginModelBase} for the given project
	 *
	 * @param project
	 *            the projec
	 * @return the plugin model or <code>null</code> if the project has no plugin
	 *         model
	 */
	private static IPluginModelBase findPluginModel(final IProject project) {
		final IPluginModelBase pluginModel = PluginRegistry.findModel(project);
		return pluginModel;
	}

	/**
	 * Performs the standard and some eMoflon-specific validation steps on the given
	 * {@link GenModel}
	 *
	 * @param genModel
	 *            the generator model to validate
	 * @return the validation status
	 */
	public static IStatus validateGenModel(final GenModel genModel) {
		final MultiStatus combinedValidationStatus = new MultiStatus(WorkspaceHelper.getPluginId(eMoflonEMFUtil.class),
				0, "Validation problems in GenModel", null);
		final IStatus defaultValidationResult = genModel.validate();
		combinedValidationStatus.merge(defaultValidationResult);

		genModel.getAllGenPackagesWithClassifiers().stream()//
				.filter(eMoflonEMFUtil::containsClassifierWithSameName)//
				.map(eMoflonEMFUtil::createSameNameValidationProblem)//
				.forEach(status -> combinedValidationStatus.merge(status));

		return combinedValidationStatus.isOK() ? Status.OK_STATUS : combinedValidationStatus;
	}

	/**
	 * Returns an {@link IStatus} that marks the given {@link GenPackage} as
	 * containing a homonymous {@link EClassifier}
	 *
	 * @param genPackage
	 *            the package to mark
	 * @return the marker
	 */
	private static Status createSameNameValidationProblem(GenPackage genPackage) {
		return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(eMoflonEMFUtil.class),
				String.format("An EPackage and its contained class may not have the same name (package URI: %s).",
						genPackage.getNSURI()));
	}

	/**
	 * Returns true iff the given {@link GenPackage} contains an {@link EClassifier}
	 * with the same name as the package.
	 *
	 * @param genPackage
	 *            the package to check
	 * @return the search result
	 */
	private static boolean containsClassifierWithSameName(final GenPackage genPackage) {
		return genPackage.getGenClassifiers().stream()
				.filter(genClassifier -> genClassifier.getName().equals(genPackage.getPackageName())).findAny()
				.isPresent();
	}

}
