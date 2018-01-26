package org.moflon.core.utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
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
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class eMoflonEMFUtil
{
   private static final Logger logger = Logger.getLogger(eMoflonEMFUtil.class);

   private static Map<EClassifier, String> clazzNames = new HashMap<EClassifier, String>();

   /**
    * Disabled utility class constructor
    */
   private eMoflonEMFUtil()
   {
      throw new UtilityClassNotInstantiableException();
   }

   /**
    * Creates a {@link ResourceSetImpl} and performs {@link #initializeDefault(ResourceSetImpl)} on it.
    * @return the resource set
    */
   public static final ResourceSet createDefaultResourceSet()
   {
      final ResourceSetImpl set = new ResourceSetImpl();
      initializeDefault(set);
      return set;
   }

   /**
    * Performs the default initialization of the given {@link ResourceSet}
    * @param set the {@link ResourceSet} to initialize
    */
   public static void initializeDefault(final ResourceSet set)
   {
      set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
   }

   /**
    * Adds an {@link ECrossReferenceAdapter} to the adapters of the given {@link ResourceSet} if no adapter exists, yet.
    *
    * @param resourceSet
    *           the resource set to be adapted.
    */
   public static final void installCrossReferencers(final ResourceSet resourceSet)
   {
      // Add adapter for reverse navigation along unidirectional links
      ECrossReferenceAdapter adapter = ECrossReferenceAdapter.getCrossReferenceAdapter(resourceSet);
      if (adapter == null)
      {
         try
         {
            resourceSet.eAdapters().add(new ECrossReferenceAdapter());
         } catch (Exception e)
         {
            LogUtils.error(logger, e);
         }
      }
   }

   /**
    * Use this method to initialize the given EPackage. This is required before loading/saving or working with the
    * package. In a plugin context, this might be automatically carried out via an appropriate extension point.
    *
    * @deprecated simply pass the eINSTANCE static attribute of the tailored EPackage to a method, or use the standard
    *             EMF package registering mechanism
    *             {@see org.eclipse.emf.ecore.resource.ResourceSet#getPackageRegistry()}
    */
   @Deprecated
   public static void init(final EPackage metamodel)
   {
      metamodel.getName();
   }

   /* Methods for loading models */

   /**
    * Utility method to get a resource from a file.
    *
    * This method delegates to {@link #getResourceFromFileIntoDefaultResourceSet(String)} using
    * {@link IFile#getLocation()}.
    *
    * @see #getResourceFromFileIntoDefaultResourceSet(String)
    */
   public static Resource getResourceFromFileIntoDefaultResourceSet(final IFile modelFile)
   {
      return getResourceFromFileIntoDefaultResourceSet(modelFile.getLocation().toString());
   }

   /**
    * Utility method to get a resource from a file.
    *
    * The method returns a resource, which results from the following initialization sequence:
    *
    * <pre>
    * ResourceSet rs = eMoflonEMFUtil.createDefaultResourceSet();
    * installCrossReferencers(rs);
    * Resource res = rs.getResource(eMoflonEMFUtil.createFileURI(pathToModelFile, true), true);
    * </pre>
    *
    * @param pathToModelFile
    * @return
    */
   public static Resource getResourceFromFileIntoDefaultResourceSet(final String pathToModelFile)
   {
      ResourceSet rs = eMoflonEMFUtil.createDefaultResourceSet();
      installCrossReferencers(rs);

      Resource res = rs.getResource(eMoflonEMFUtil.createFileURI(pathToModelFile, true), true);

      return res;
   }

   /**
    * Use to load a model if its metamodel has been initialized already and there are no dependencies to other models.
    *
    * @param pathToXMIFile
    *           Absolute or relative path to XMI file.
    * @return the root element of the loaded model
    * @deprecated use standard EMF method for loading the model by passing an URI and {@code true} to the
    *             {@link org.eclipse.emf.ecore.resource.ResourceSet#getResource(URI,boolean) getResource} method and
    *             then install cross-referencers by the {@link installCrossReferencers(ResourceSet)
    *             installCrossReferencers()} method.
    *             <p>
    *             For example use instead:
    *             <p>
    *             <blockquote>
    *
    *             <pre>
    *
    *             ResourceSet rs = eMoflonEMFUtil.createDefaultResourceSet();
    *
    *             Resource res = rs.getResource(eMoflonEMFUtil.createFileURI("path/to/foo.xmi", true), true);
    *
    *             EObject foo = res.getContents().get(0);
    *             </pre>
    *
    *             </blockquote>
    *
    *             NOTE: You might consider to use {@link #getResourceFromFileIntoDefaultResourceSet(String)}.
    *
    * @see #getResourceFromFileIntoDefaultResourceSet(String)
    */
   @Deprecated
   public static EObject loadModel(final String pathToXMIFile)
   {
      return loadModelWithDependencies(pathToXMIFile, createDefaultResourceSet());
   }

   /**
    * Use to load a model if its metamodel has been initialized already and dependencies to other models are to be
    * resolved using the supplied resourceSet.
    *
    * @param pathToXMIFile
    *           Absolute or relative path to XMI file.
    * @param resourceSet
    *           Contains other models to resolve dependencies.
    * @return the root element of the loaded model with resolved dependencies.
    * @deprecated use standard EMF method for loading the model by passing an URI and {@code true} to the
    *             {@link org.eclipse.emf.ecore.resource.ResourceSet#getResource(URI,boolean) getResource} method and
    *             then install cross-referencers by the {@link installCrossReferencers(ResourceSet)
    *             installCrossReferencers()} method
    */
   @Deprecated
   public static EObject loadModelWithDependencies(final String pathToXMIFile, final ResourceSet resourceSet)
   {
      return loadModelWithDependenciesAndCrossReferencer(createFileURI(pathToXMIFile, true), resourceSet);
   }

   /**
    * Use to load a model and initialize its metamodel
    *
    * @param metamodel
    *           Metamodel (for initialization)
    * @param pathToXMIFile
    *           Absolute or relative path to XMI file
    *
    * @return the root element of the loaded model
    * @deprecated init metamodel by simply referring to the eINSTANCE static attribute of a tailored EPackage, use
    *             standard EMF method for loading the model by passing an URI and {@code true} to the
    *             {@link org.eclipse.emf.ecore.resource.ResourceSet#getResource(URI,boolean) getResource} method and
    *             then install cross-referencers by the {@link installCrossReferencers(ResourceSet)
    *             installCrossReferencers()} method
    */
   @Deprecated
   public static EObject loadAndInitModel(final EPackage metamodel, final String pathToXMIFile)
   {
      init(metamodel);
      return loadModelWithDependencies(pathToXMIFile, createDefaultResourceSet());
   }

   /**
    * Use to load a model, initialize its metamodel, and resolve dependencies
    *
    * @param metamodel
    *           Metamodel (for initialization)
    * @param pathToXMIFile
    *           Absolute or relative path to XMI file
    * @param dependencies
    *           Contains other models to resolve dependencies.
    * @return the root element of the loaded model
    * @deprecated init metamodel by simply referring to the eINSTANCE static attribute of a tailored EPackage, use
    *             standard EMF method for loading the model by passing an URI and {@code true} to the
    *             {@link org.eclipse.emf.ecore.resource.ResourceSet#getResource(URI,boolean) getResource} method and
    *             then install cross-referencers by the {@link installCrossReferencers(ResourceSet)
    *             installCrossReferencers()} method
    */
   @Deprecated
   public static EObject loadAndInitModelWithDependencies(final EPackage metamodel, final String pathToXMIFile, final ResourceSet dependencies)
   {
      init(metamodel);
      return loadModelWithDependencies(pathToXMIFile, dependencies);
   }

   /**
    * Loads a model, initializes its metamodel and resolves dependencies from a Jar file.
    *
    * The URI to load the model from is constructed as follows: Given a "C:/Users/user/lib.jar" as jar file and
    * "/model/myModel.xmi", the constructed URI is "jar:file:C:/Users/user/lib.jar!/model/myModel.jar".
    *
    * @param metamodel
    *           the metamodel that should be initialized
    * @param pathToJarFile
    *           path to the jar file (absolute or relative)
    * @param pathToResourceInJarFile
    *           path to the model inside the jar
    * @param dependencies
    *           contains other models to resolve dependencies
    * @return the root element of the loaded model
    * @deprecated init metamodel by simply referring to the eINSTANCE static attribute of a tailored EPackage, use
    *             standard EMF method for loading the model by passing an URI and {@code true} to the
    *             {@link org.eclipse.emf.ecore.resource.ResourceSet#getResource(URI,boolean) getResource} method and
    *             then install cross-referencers by the {@link installCrossReferencers(ResourceSet)
    *             installCrossReferencers()} method
    */
   @Deprecated
   public static EObject loadAndInitModelFromJarFileWithDependencies(final EPackage metamodel, final File jarFile, final String pathToResourceInJarFile,
         final ResourceSet dependencies)
   {
      return loadAndInitModelFromJarFileWithDependencies(metamodel, jarFile.getAbsolutePath(), pathToResourceInJarFile, dependencies);
   }

   /**
    * Loads a model, initializes its metamodel and resolves dependencies from a Jar file.
    *
    * The URI to load the model from is constructed as follows: Given a "C:/Users/user/lib.jar" as path to the jar and
    * "/model/myModel.xmi", the constructed URI is "jar:file:C:/Users/user/lib.jar!/model/myModel.jar".
    *
    * @param metamodel
    *           the metamodel that should be initialized
    * @param pathToJarFile
    *           path to the jar file (absolute or relative)
    * @param pathToResourceInJarFile
    *           path to the model inside the jar
    * @param resourceSet
    *           contains other models to resolve dependencies
    * @return the root element of the loaded model
    * @deprecated init metamodel by simply referring to the eINSTANCE static attribute of a tailored EPackage, use
    *             standard EMF method for loading the model by passing an URI and {@code true} to the
    *             {@link org.eclipse.emf.ecore.resource.ResourceSet#getResource(URI,boolean) getResource} method and
    *             then install cross-referencers by the {@link installCrossReferencers(ResourceSet)
    *             installCrossReferencers()} method
    */
   @Deprecated
   public static EObject loadAndInitModelFromJarFileWithDependencies(final EPackage metamodel, final String pathToJarFile, final String pathToResourceInJarFile,
         final ResourceSet resourceSet)
   {
      init(metamodel);
      final URI uri = createInJarURI(pathToJarFile, pathToResourceInJarFile);
      return eMoflonEMFUtil.loadModelWithDependenciesAndCrossReferencer(uri, resourceSet);
   }

   /**
    * Creates an URI for resources inside a Jar file.
    *
    * The created URI is equivalent to "jar:file:[pathToJarFile]![pathToResourceInJarFile]".
    *
    * @param pathToJarFile
    *           path to the jar file (absolute or relative)
    * @param pathToResourceInJarFile
    *           path to resource inside the jar
    * @return the built URI
    */
   public static URI createInJarURI(final String pathToJarFile, final String pathToResourceInJarFile)
   {
      return URI.createURI("jar:file:" + pathToJarFile + "!" + pathToResourceInJarFile);
   }

   /**
    * Use this method directly only if you know what you are doing! The corresponding metamodel must be initialized
    * already. The model is loaded with all dependencies, and a cross reference adapter is added to enable inverse
    * navigation.
    *
    * @param uriToModelResource
    *           URI of resource containing model
    * @param resourceSet
    *           Contains other models to resolve dependencies
    * @return the root element of the loaded model
    * @deprecated use standard EMF method for loading the model by passing an URI and {@code true} to the
    *             {@link org.eclipse.emf.ecore.resource.ResourceSet#getResource(URI,boolean) getResource} method and
    *             then install cross-referencers by the {@link installCrossReferencers(ResourceSet)
    *             installCrossReferencers()} method (Note that a cross-referencer should be installed exactly once on a
    *             resource set.)
    */
   @Deprecated
   public static EObject loadModelWithDependenciesAndCrossReferencer(final URI uriToModelResource, final ResourceSet resourceSet)
   {
      if (resourceSet == null)
      {
         throw new IllegalArgumentException("The resource set passed as 'resourceSet' must not be null!");
      }

      // Get the resource (load on demand)
      Resource resource = resourceSet.getResource(uriToModelResource, true);

      // Add adapter for reverse navigation along unidirectional links
      ECrossReferenceAdapter adapter = ECrossReferenceAdapter.getCrossReferenceAdapter(resourceSet);
      if (adapter == null)
      {
         try
         {
            resourceSet.eAdapters().add(new ECrossReferenceAdapter());
         } catch (Exception e)
         {
            LogUtils.error(logger, e);
         }
      }

      // Return root model element
      return resource.getContents().get(0);
   }

   /**
    * Use this method directly only if you know what you are doing! This method not only loads a model but also adds a
    * URIMap-entry, mapping the model's default "nsURI" to the URI from loading the resource in the supplied file. The
    * corresponding metamodel must be initialized already.
    *
    * @param pathToXMIFile
    *           Absolute or relative path to XMI file
    * @param dependencies
    *           Contains other models to resolve dependencies
    * @return the root element of the loaded model
    * @throws IOException
    *            if uri does not point to a valid/loadable xmi document
    * @throws IllegalStateException
    *            if something else goes wrong (e.g. xmi document could not be parsed correctly)
    */
   @Deprecated
   public static EObject loadModelAndAddUriMapping(final String pathToXMIFile, final ResourceSet dependencies) throws IOException
   {

      File file = new File(pathToXMIFile);
      URI resourceURI = createFileURI(pathToXMIFile, true);

      try
      {
         // Retrieve package URI from XMI file (this must be done before
         // loading!)
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = dbf.newDocumentBuilder();
         Document doc = docBuilder.parse(file);
         doc.getDocumentElement().normalize();
         NodeList packageElements = doc.getElementsByTagName("ecore:EPackage");
         Node epackageNode = packageElements.item(0);
         NamedNodeMap attributes = epackageNode.getAttributes();
         Node uriAttribute = attributes.getNamedItem("nsURI");
         String nsUriAsString = uriAttribute.getNodeValue();

         URI packageURI = URI.createURI(nsUriAsString);

         // Create mapping
         dependencies.getURIConverter().getURIMap().put(packageURI, resourceURI);
         logger.debug(String.format("Adding URI mapping: %1$s -> %2$s", packageURI, resourceURI));
      } catch (ParserConfigurationException e)
      {
         throw new IllegalStateException(e);
      } catch (SAXException e)
      {
         throw new IllegalStateException(e);
      }

      return loadModelWithDependenciesAndCrossReferencer(resourceURI, dependencies);
   }

   /**
    * Copies the model stored in the resource at the from URL into a resource at the to URL
    *
    * @param resourceSet
    *           the resource set in which the copy operation is performed
    * @param from
    *           the resource URL, which serves as the source of the copy operation
    * @param to
    *           the resource URL, which serves as the target of the copy operation
    * @return the loaded or newly created resource with the to URL
    */
   public static Resource copy(final ResourceSet resourceSet, final URI from, final URI to)
   {
      final Map<URI, URI> uriMap = resourceSet.getURIConverter().getURIMap();
      uriMap.put(to, from);
      final Resource resource = resourceSet.getResource(to, true);
      uriMap.remove(to);
      return resource;
   }

   /*
    *
    *
    *
    * Methods for saving models
    */

   /**
    * Use to save a model to the given XMI file path, but only if it has changed.
    *
    * @param resourceSet
    * @param rootElementOfModel
    * @param pathToXMIFile
    *           Absolute or relative path to XMI file in which to save the model.
    */
   static public void saveModel(final ResourceSet resourceSet, final EObject rootElementOfModel, final String pathToXMIFile)
   {
      Resource resource = rootElementOfModel.eResource();
      URI fileURI = createFileURI(pathToXMIFile, false);

      if (resource == null)
      {
         // Create a default resource
         resource = resourceSet.createResource(fileURI);
         resource.getContents().add(rootElementOfModel);
      }

      Map<Object, Object> saveOptions = new HashMap<Object, Object>();
      saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
      saveOptions.put(Resource.OPTION_LINE_DELIMITER, WorkspaceHelper.DEFAULT_RESOURCE_LINE_DELIMITER);

      if (fileURI.equals(resource.getURI()))
      {
         try
         {
            resource.save(saveOptions);
         } catch (IOException e)
         {
            LogUtils.error(logger, e);
         }
      } else
      {
         Map<URI, URI> uriMapping = resource.getResourceSet().getURIConverter().getURIMap();
         uriMapping.put(resource.getURI(), fileURI);
         try
         {
            resource.save(saveOptions);
         } catch (IOException e)
         {
            LogUtils.error(logger, e);
         }
         uriMapping.remove(resource.getURI());
      }
   }

   /**
    * Use to save a model to the given XMI file path.
    *
    * @param rootElementOfModel
    * @param pathToXMIFile
    *           Absolute or relative path to XMI file in which to save the model.
    * @deprecated use the {@link #saveModel(ResourceSet, EObject, String) saveModel} method with a resource set created
    *             by the {@link #createDefaultResourceSet() createDefaultResourceSet()}} static method. Enforcing the
    *             explicit creation of a resource set yields to a more conscious (re)use of resource sets.
    */
   @Deprecated
   static public void saveModel(final EObject rootElementOfModel, final String pathToXMIFile)
   {
      saveModel(createDefaultResourceSet(), rootElementOfModel, pathToXMIFile);
   }

   /*
    *
    *
    *
    * EMF Helper Methods
    */

   /**
    * Create and return a file URI for the given path.
    *
    * @param pathToXMIFile
    * @param mustExist
    *           Set true when loading (the file must exist) and false when saving (file can be newly created).
    * @return
    */
   static public URI createFileURI(final String pathToXMIFile, final boolean mustExist)
   {
      File filePath = new File(pathToXMIFile);
      if (!filePath.exists() && mustExist)
         throw new IllegalArgumentException(pathToXMIFile + " does not exist.");

      return URI.createFileURI(filePath.getAbsolutePath());
   }

   /**
    * If possible, prefer {@link eMoflonEMFUtil#getOppositeReferenceTyped(EObject, Class, String) instead!} This method
    * will be marked as deprecated in the near future.
    */
   public static List<?> getOppositeReference(final EObject target, final Class<?> sourceType, final String targetRoleName)
   {
      Collection<Setting> settings = getInverseReferences(target);

      List<EObject> returnList = new ArrayList<EObject>();
      for (Setting setting : settings)
      {
         EObject candidate = getCandidateObject(sourceType, targetRoleName, setting);
         if (candidate != null)
            returnList.add(candidate);
      }

      EObject eContainer = target.eContainer();
      if (eContainer != null)
      {
         Setting setting = (((InternalEObject) eContainer).eSetting(target.eContainmentFeature()));
         EObject candidate = getCandidateObject(sourceType, targetRoleName, setting);
         if (candidate != null)
            returnList.add(candidate);
      }

      return returnList;
   }

   /**
    * This method only works when you have registered an appropriate adapter right after loading your model! Further
    * documentation can be found here: http://sdqweb.ipd.kit.edu/wiki/EMF_Reverse_Lookup_/
    * _navigating_unidirectional_references_bidirectional
    *
    * @param target
    *           the target of this reference
    * @param sourceType
    *           the type of the opposite objects you are looking for
    * @return a list of all opposite objects
    */
   @SuppressWarnings("unchecked")
   public static <T extends EObject> List<T> getOppositeReferenceTyped(final EObject target, final Class<T> sourceType, final String targetRoleName)
   {
      Collection<Setting> settings = getInverseReferences(target);

      List<T> returnList = new ArrayList<T>();
      for (Setting setting : settings)
      {
         EObject candidate = getCandidateObject(sourceType, targetRoleName, setting);
         if (candidate != null)
            returnList.add((T) candidate);
      }

      EObject eContainer = target.eContainer();
      if (eContainer != null)
      {
         Setting setting = (((InternalEObject) eContainer).eSetting(target.eContainmentFeature()));
         EObject candidate = getCandidateObject(sourceType, targetRoleName, setting);
         if (candidate != null)
            returnList.add((T) candidate);
      }

      return returnList;
   }

   public static List<EStructuralFeature> getAllOppositeReferences(final EObject target)
   {
      Collection<Setting> settings = getInverseReferences(target);

      List<EStructuralFeature> returnList = new ArrayList<>();
      for (Setting setting : settings)
      {
         EStructuralFeature candidate = setting.getEStructuralFeature();
         if (candidate != null)
            returnList.add(candidate);
      }

      EObject eContainer = target.eContainer();
      if (eContainer != null)
      {
         Setting setting = (((InternalEObject) eContainer).eSetting(target.eContainmentFeature()));
         EStructuralFeature candidate = setting.getEStructuralFeature();
         if (candidate != null)
            returnList.add(candidate);
      }

      return returnList;
   }

   private static EObject getCandidateObject(final Class<?> sourceType, final String targetRoleName, final Setting setting)
   {
      if (setting.getEStructuralFeature().getName().equals(targetRoleName))
      {
         EClassifier clazz = setting.getEObject().eClass();
         String clazzName = getClazzNameWithPackagePrefix(clazz);

         if (clazzName.equals(sourceType.getName()) || checkInheritance(sourceType, clazz))
            return setting.getEObject();
      }

      return null;
   }

   private static Collection<Setting> getInverseReferences(final EObject target)
   {
      ECrossReferenceAdapter adapter = getCRAdapter(target);
      return adapter.getNonNavigableInverseReferences(target, true);
   }

   public static String getClazzNameWithPackagePrefix(final EClassifier clazz)
   {
      String clazzName = clazzNames.get(clazz);

      if (clazzName == null)
      {
         clazzName = clazz.getInstanceClass().getPackage().getName() + "." + clazz.getName();
         clazzNames.put(clazz, clazzName);
      }
      return clazzName;
   }

   public static boolean checkInheritance(final Class<?> superclass, final EClassifier subclass)
   {
      for (EClass sup : ((EClass) subclass).getEAllSuperTypes())
      {
         String clazzName = getClazzNameWithPackagePrefix(sup);
         if (clazzName.equals(superclass.getName()))
            return true;
      }
      return false;
   }

   private static ECrossReferenceAdapter getCRAdapter(final EObject target)
   {
      // Determine context
      Notifier context = null;

      EObject root = EcoreUtil.getRootContainer(target, true);
      Resource resource = root.eResource();

      if (resource != null)
      {
         ResourceSet resourceSet = resource.getResourceSet();
         if (resourceSet != null)
            context = resourceSet;
         else
            context = resource;
      } else
         context = root;

      // Retrieve adapter and create+add on demand
      ECrossReferenceAdapter adapter = ECrossReferenceAdapter.getCrossReferenceAdapter(context);
      if (adapter == null)
      {
         adapter = new ECrossReferenceAdapter();
         context.eAdapters().add(adapter);
      }

      return adapter;
   }

   public static void remove(final EObject object)
   {
      EcoreUtil.delete(object, true);
   }

   /*
    * This method is thought to be a more efficient way to delete objects from a model than remove(EObject)
    */
   public static void unsetAllReferences(final EObject object)
   {
      for (EStructuralFeature feature : getAllReferences(object))
      {
         object.eUnset(feature);
      }

      ArrayList<Setting> settings = new ArrayList<>();
      settings.addAll(getInverseReferences(object));
      for (Setting setting : settings)
      {
         EStructuralFeature feature = setting.getEStructuralFeature();
         removeOppositeReference(setting.getEObject(), object, feature.getName());
      }

      if (object.eContainer() != null)
      {
         removeOppositeReference(object.eContainer(), object, object.eContainmentFeature().getName());
      }

   }

   @SuppressWarnings({ "unchecked" })
   public static void addOppositeReference(final EObject source, final EObject target, final String targetRole)
   {
      EStructuralFeature reference = source.eClass().getEStructuralFeature(targetRole);
      if (!reference.isMany())
      {
         source.eSet(reference, target);
      } else
         ((Collection<EObject>) source.eGet(reference)).add(target);
   }

   public static void removeOppositeReference(final EObject source, final EObject target, final String targetRole)
   {
      removeEdge(source, target, targetRole);
   }

   public static void removeEdge(final EObject source, final EObject target, final String targetRole)
   {
      EStructuralFeature reference = source.eClass().getEStructuralFeature(targetRole);
      if (!reference.isMany())
      {
         source.eSet(reference, null);
      } else
      {
         ((Collection<?>) source.eGet(reference)).remove(target);
      }
   }

   /**
    * Returns the cross reference (not containment reference) in the containment tree below 'obj' whose name attribute equals 'name'.
    */
   public static EStructuralFeature getReference(final EObject obj, final String name)
   {
      for (EContentsEList.FeatureIterator<?> featureIterator = (EContentsEList.FeatureIterator<?>) obj.eCrossReferences().iterator(); featureIterator
            .hasNext();)
      {
         featureIterator.next();
         EReference eReference = (EReference) featureIterator.feature();
         if (eReference.getName().equals(name))
            return eReference;
      }

      return null;
   }

   /**
    * Returns the containment reference in the containment tree below 'container' whose name attribute equals 'name'.
    */
   public static EStructuralFeature getContainment(final EObject container, final String name)
   {
      for (EContentsEList.FeatureIterator<?> featureIterator = (EContentsEList.FeatureIterator<?>) container.eContents().iterator(); featureIterator.hasNext();)
      {
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
   public static Set<EStructuralFeature> getAllReferences(final EObject object)
   {
      final EList<EStructuralFeature> references = new BasicEList<>();

      // Collect outgoing cross references - excluding containment edges
      for (EContentsEList.FeatureIterator<?> featureIterator = (EContentsEList.FeatureIterator<?>) object.eCrossReferences().iterator(); featureIterator
            .hasNext();)
      {
         featureIterator.next();
         references.add(featureIterator.feature());
      }

      // Collect outgoing containment edges
      for (EContentsEList.FeatureIterator<?> featureIterator = (EContentsEList.FeatureIterator<?>) object.eContents().iterator(); featureIterator.hasNext();)
      {
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
   public static String getName(final EObject child)
   {
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
    * Builds an identifier String for the given EObject. This identifier starts with
    * <ul>
    * <li>the attribute of the EObject as a String, if the EObject does only have one attribute.</li>
    * <li>the attribute called 'name' of the EObject, if it has such an attribute</li>
    * <li>any attribute of the EObject, but String attributes are preferred</li>
    * </ul>
    * The identifier ends with " : " followed by the type of the EObject. <br>
    * Example: A MocaTree Node with the name "foo" will result in "foo : Node" <br>
    * If the EObject does not have any attributes or all attributes have the value null, this function will only return
    * the type of the EObject.
    */
   public static String getIdentifier(final EObject eObject)
   {
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
    * @param name
    *           Use an empty StringBuilder as input. If this function returns true, this parameter has been filled, if
    *           it returns false, nothing happened.
    * @return Indicates the success of this function and if the last parameter contains output.
    */
   private static boolean tryGetSingleAttribute(final EObject eObject, final List<EAttribute> attributes, final StringBuilder name)
   {
      boolean success = false;
      if (attributes.size() == 1)
      {
         Object obj = eObject.eGet(attributes.get(0));
         if (obj != null)
         {
            name.append(obj.toString());
            success = true;
         }
      }
      return success;
   }

   /**
    * @param name
    *           Use an empty StringBuilder as input. If this function returns true, this parameter has been filled, if
    *           it returns false, nothing happened.
    * @return Indicates the success of this function and if the last parameter contains output.
    */
   private static boolean tryGetNameAttribute(final EObject eObject, final List<EAttribute> attributes, final StringBuilder name)
   {
      boolean success = false;
      for (EAttribute feature : attributes)
      {
         if (feature.getName().equals("name"))
         {
            Object obj = eObject.eGet(feature);
            if (obj != null)
            {
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
    *           Use an empty StringBuilder as input. If this function returns true, this parameter has been filled, if
    *           it returns false, nothing happened.
    * @return Indicates the success of this function and if the last parameter contains output.
    */
   private static boolean tryGetAnyAttribute(final EObject eObject, final List<EAttribute> attributes, final StringBuilder name)
   {
      boolean success = false;
      String nonStringName = null;
      String stringName = null;
      for (EAttribute feature : attributes)
      {
         Object obj = eObject.eGet(feature);
         if (obj == null)
            continue;
         if (obj instanceof String)
         {
            stringName = (String) obj;
            break;
         } else
         {
            nonStringName = obj.toString();
         }
      }
      if (stringName != null && !stringName.equals("null"))
      {
         name.append(stringName);
         success = true;
      } else if (nonStringName != null && !nonStringName.equals("null"))
      {
         name.append(nonStringName);
         success = true;
      }
      return success;
   }

   /**
    * @deprecated The semantics of this method is absolutely unclear! You may use the following method instead:
    *             createParentResourceAndInsertIntoResourceSet
    */
   @Deprecated
   public static Resource addToResourceSet(final ResourceSet set, final EObject object)
   {
      Resource resource = object.eResource();
      if (resource == null)
      {
         resource = new ResourceImpl();
         resource.setURI(URI.createURI(object.eClass().getEPackage().getNsURI()));
      }

      resource.getContents().add(object);
      set.getResources().add(resource);

      return resource;
   }

   /**
    * Creates a new resource for the given {@link EObject}. The URI of the new resource is the NS URI of its containing
    * {@link EPackage}. The resource is added to the given {@link ResourceSet} afterwards.
    *
    * @param object
    * @param set
    *
    * @throws IllegalArgumentException
    *            if the object is already inside a {@link Resource}
    */
   public static void createParentResourceAndInsertIntoResourceSet(final EObject object, final ResourceSet set)
   {
      if (object.eResource() != null)
      {
         throw new IllegalStateException("The given object must not be inside a resource yet");
      }

      final Resource resource = new ResourceImpl();
      resource.setURI(URI.createURI(object.eClass().getEPackage().getNsURI()));
      resource.getContents().add(object);
      set.getResources().add(resource);
   }

   /**
    * Returns the number of elements in the containment tree routed at 'model'.
    *
    * @param model
    * @return
    */
   public static int getNodeCount(final EObject model)
   {
      int i = 0;
      TreeIterator<EObject> iterator = model.eAllContents();
      while (iterator.hasNext())
      {
         i++;
         iterator.next();
      }

      return i;
   }

   /**
    * Returns the number of {@link EReference}s in the given {@code model}.
    *
    * @param model
    * @return
    */
   public static int getEdgeCount(final EObject model)
   {

      int i = 0;
      TreeIterator<EObject> iterator = model.eAllContents();
      while (iterator.hasNext())
      {
         EObject node = iterator.next();
         Set<EStructuralFeature> references = eMoflonEMFUtil.getAllReferences(node);
         i += references.size();
      }
      return i;
   }

   /**
    * Loads the genmodel from the given project, assuming the default genmodel path as returned by
    * {@link MoflonUtil#getDefaultPathToGenModelInProject(String)}.
    *
    * @return the genmodel (if exists)
    */
   public static GenModel extractGenModelFromProject(final IProject currentProject)
   {
      String pathInsideProject = MoflonUtil.getDefaultPathToGenModelInProject(currentProject.getName());
      IFile projectGenModelFile = currentProject.getFile(pathInsideProject);
      String pathToGenmodel = projectGenModelFile.getRawLocation().toOSString();
      ResourceSet set = createDefaultResourceSet();
      Resource genModelResource = set.getResource(URI.createFileURI(pathToGenmodel), true);
      GenModel genmodel = (GenModel) genModelResource.getContents().get(0);
      return genmodel;
   }

   /**
    * Transforms each {@link Diagnostic} message to an {@link IStatus} message and returns all of them as result
    * @param resourceSet the {@link ResourceSet} from which to collect the {@link Diagnostic}s
    * @param taskName the current task
    * @param monitor the {@link IProgressMonitor}
    * @return the converted {@link IStatus}
    */
   public static final IStatus validateResourceSet(final ResourceSet resourceSet, final String taskName, final IProgressMonitor monitor)
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Checking errors in the " + taskName + " task", resourceSet.getResources().size());
      final MultiStatus status = new MultiStatus(WorkspaceHelper.getPluginId(eMoflonEMFUtil.class), IStatus.OK, taskName + " failed", null);
      for (final Resource resource : resourceSet.getResources())
      {
         for (final Diagnostic diagnostic : resource.getErrors())
         {
            final Exception exception = diagnostic instanceof Exception ? (Exception) diagnostic : null;
            status.add(new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(eMoflonEMFUtil.class), IStatus.ERROR, diagnostic.getMessage(), exception));
         }
         subMon.worked(1);
      }
      return status;
   }

   public static final void createPluginToResourceMapping(final ResourceSet set, final IProject project) throws CoreException
   {
      if (project.isAccessible() && project.hasNature(WorkspaceHelper.PLUGIN_NATURE_ID))
      {
         final IPluginModelBase pluginModel = PluginRegistry.findModel(project);
         if (pluginModel != null)
         {
            // Plugin projects in the workspace
            final String pluginID = pluginModel.getBundleDescription().getSymbolicName();
            final URI pluginURI = URI.createPlatformPluginURI(pluginID + "/", true);
            final URI resourceURI = URI.createPlatformResourceURI(project.getName() + "/", true);
            set.getURIConverter().getURIMap().put(pluginURI, resourceURI);
         }
      }
   }

   public static final void createPluginToResourceMapping(final ResourceSet set, final IProgressMonitor monitor) throws CoreException
   {
      final IProject[] workspaceProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      final SubMonitor subMon = SubMonitor.convert(monitor, "Register plugin to resource mapping", workspaceProjects.length);
      for (final IProject project : workspaceProjects)
      {
         createPluginToResourceMapping(set, project);
         subMon.worked(1);
      }
   }

   public static final void createPluginToResourceMapping(final ResourceSet set) throws CoreException
   {
      createPluginToResourceMapping(set, new NullProgressMonitor());
   }

   public static final URI lookupProjectURI(final IProject project)
   {
      IPluginModelBase pluginModel = PluginRegistry.findModel(project);
      if (pluginModel != null)
      {
         // Plugin projects in the workspace
         String pluginID = pluginModel.getBundleDescription().getSymbolicName();
         return URI.createPlatformPluginURI(pluginID + "/", true);
      } else
      {
         // Regular projects in the workspace
         return URI.createPlatformResourceURI(project.getName() + "/", true);
      }
   }

   public static final URI getDefaultProjectRelativeEcoreFileURI(final IProject project)
   {
      final String ecoreFileName = MoflonUtil.lastCapitalizedSegmentOf(project.getName());
      return URI.createURI(WorkspaceHelper.MODEL_FOLDER + "/" + ecoreFileName + WorkspaceHelper.ECORE_FILE_EXTENSION);
   }

   public static final URI getDefaultEcoreFileURI(final IProject project)
   {
      return getDefaultProjectRelativeEcoreFileURI(project).resolve(URI.createPlatformResourceURI(project.getName() + "/", true));
   }

   public static final IProject getWorkspaceProject(final URI namespaceURI)
   {
      assert namespaceURI.segmentCount() >= 2 && namespaceURI.isPlatformPlugin() || namespaceURI.isPlatformResource();
      if (namespaceURI.isPlatformResource() && namespaceURI.segmentCount() >= 2)
      {
         return ResourcesPlugin.getWorkspace().getRoot().getProject(namespaceURI.segment(1));
      }
      if (namespaceURI.isPlatformPlugin() && namespaceURI.segmentCount() >= 2)
      {
         for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
         {
            IPluginModelBase pluginModel = PluginRegistry.findModel(project);
            if (pluginModel != null && namespaceURI.segment(1).equals(pluginModel.getBundleDescription().getSymbolicName()))
            {
               return project;
            }
         }
      }
      return null;
   }

   public static final List<EClass> getEClasses(final EPackage ePackage)
   {
      final List<EClass> result = new LinkedList<EClass>();
      for (final TreeIterator<EObject> iterator = ePackage.eAllContents(); iterator.hasNext();)
      {
         final EObject eObject = iterator.next();
         if (EcorePackage.eINSTANCE.getEClass().isInstance(eObject))
         {
            result.add((EClass) eObject);
            iterator.prune();
         }
      }
      return result;
   }

   /**
    * Returns true iff the given resource is a file and has an "ecore" extension
    * @param ecoreResource the resource to check
    * @return check result
    */
   public static boolean isEcoreFile(final IResource ecoreResource)
   {
      return ecoreResource.getType() == IResource.FILE && "ecore".equals(ecoreResource.getFileExtension());
   }

}
