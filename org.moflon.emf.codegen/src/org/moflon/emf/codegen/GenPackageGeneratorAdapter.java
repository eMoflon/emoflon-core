package org.moflon.emf.codegen;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.codegen.ecore.CodeGenEcorePlugin;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;

public class GenPackageGeneratorAdapter
		extends org.eclipse.emf.codegen.ecore.genmodel.generator.GenPackageGeneratorAdapter {

	public GenPackageGeneratorAdapter(GeneratorAdapterFactory generatorAdapterFactory) {
		super(generatorAdapterFactory);
	}

	@Override
	protected void ensureProjectExists(String workspacePath, Object object, Object projectType, boolean force,
			Monitor monitor) {
	}

	// FIXME [greg]: This is a bug fix for EMF and should be removed if the latest
	// version of EMF/Eclipse contains it,
	// see:
	// http://git.eclipse.org/c/emf/org.eclipse.emf.git/commit/?id=d76ada0cf9fcaf5b652cde554a2e7706ddbf91f0
	@Override
	protected void generatePackageSerialization(GenPackage genPackage, Monitor monitor) {
		if (genPackage.hasClassifiers() && genPackage.isLoadingInitialization()) {
			monitor = createMonitor(monitor, 1);

			try {
				monitor.beginTask("", 2);

				final GenModel genModel = genPackage.getGenModel();
				String targetPathName = genModel.getModelDirectory() + "/"
						+ genPackage.getClassPackageName().replace('.', '/') + "/"
						+ genPackage.getSerializedPackageFilename();
				message = CodeGenEcorePlugin.INSTANCE.getString("_UI_GeneratingPackageSerialization_message",
						new Object[] { targetPathName });
				monitor.subTask(message);

				URI targetFile = toURI(targetPathName);
				ensureContainerExists(targetFile.trimSegments(1), createMonitor(monitor, 1));

				final ResourceSet originalSet = genModel.eResource().getResourceSet();
				EPackage originalPackage = genPackage.getEcorePackage();

				ResourceSet outputSet = new ResourceSetImpl();
				outputSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
						.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new EcoreResourceFactoryImpl());
				URI targetURI = toPlatformResourceURI(targetFile);
				Resource outputResource = outputSet.createResource(targetURI);

				// Copy the package, excluding unwanted annotations.
				//
				EcoreUtil.Copier copier = new EcoreUtil.Copier() {
					private static final long serialVersionUID = 1L;

					@Override
					protected void copyContainment(EReference reference, EObject object, EObject copyEObject) {
						if (reference == EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS) {
							List<EAnnotation> result = ((EModelElement) copyEObject).getEAnnotations();
							result.clear();

							for (EAnnotation eAnnotation : ((EModelElement) object).getEAnnotations()) {
								if (!genModel.isSuppressedAnnotation(eAnnotation.getSource())) {
									result.add((EAnnotation) copy(eAnnotation));
								}
							}
							return;
						}
						super.copyContainment(reference, object, copyEObject);
					}
				};
				EPackage outputPackage = (EPackage) copier.copy(originalPackage);
				copier.copyReferences();
				outputResource.getContents().add(outputPackage);
				collapseEmptyPackages(outputPackage);

				// This URI handler redirects cross-document references to correct
				// namespace-based values.
				//
				XMLResource.URIHandler uriHandler = new URIHandlerImpl.PlatformSchemeAware() {
					private EPackage getContainingPackage(EObject object) {
						while (object != null) {
							if (object instanceof EPackage) {
								return (EPackage) object;
							}
							object = object.eContainer();
						}
						return null;
					}

					private String getRelativeFragmentPath(Resource resource, EObject base, String path) {
						String basePath = resource.getURIFragment(base);
						if (basePath != null && path.startsWith(basePath)) {
							int i = basePath.length();
							if (path.length() == i) {
								return "";
							} else if (path.charAt(i) == '/') {
								return path.substring(i);
							}
						}
						return null;
					}

					private EPackage getNonEmptySuperPackage(EPackage ePackage) {
						EPackage result = ePackage.getESuperPackage();
						while (result != null && result.getEClassifiers().isEmpty()) {
							result = result.getESuperPackage();
						}
						return result;
					}

					private URI redirect(URI uri) {
						if (uri != null && !uri.isCurrentDocumentReference() && uri.hasFragment()) {
							URI base = uri.trimFragment();
							String fragment = uri.fragment();
							Resource resource = originalSet.getResource(base, false);
							if (resource != null) {
								EObject object = resource.getEObject(fragment);
								if (object != null) {
									EPackage ePackage = getContainingPackage(object);
									if (ePackage != null) {
										String relativePath = getRelativeFragmentPath(resource, ePackage, fragment);
										if (relativePath != null) {
											StringBuilder path = new StringBuilder();
											EPackage superPackage = getNonEmptySuperPackage(ePackage);
											while (superPackage != null) {
												path.insert(0, '/');
												path.insert(1, ePackage.getName());
												ePackage = superPackage;
												superPackage = getNonEmptySuperPackage(ePackage);
											}
											path.insert(0, '/');
											path.append(relativePath);
											return URI.createURI(ePackage.getNsURI()).appendFragment(path.toString());
										}
									}
								}
							}
						}
						return uri;
					}

					@Override
					public URI deresolve(URI uri) {
						return super.deresolve(redirect(uri));
					}

					@Override
					public URI resolve(URI uri) {
						return super.resolve(redirect(uri));
					}

					@Override
					public void setBaseURI(URI uri) {
						super.setBaseURI(redirect(uri));
					}
				};
				Map<Object, Object> options = new HashMap<Object, Object>();
				options.put(XMLResource.OPTION_URI_HANDLER, uriHandler);
				options.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
				options.put(Resource.OPTION_LINE_DELIMITER, Resource.OPTION_LINE_DELIMITER_UNSPECIFIED);

				try {
					outputResource.save(options);
				} catch (IOException exception) {
					// DMS handle this well.
					CodeGenEcorePlugin.INSTANCE.log(exception);
				}
			} finally {
				monitor.done();
			}
		} else {
			monitor.worked(1);
		}
	}
}
