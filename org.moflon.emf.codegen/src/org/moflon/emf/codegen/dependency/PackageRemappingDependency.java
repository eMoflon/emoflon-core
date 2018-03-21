package org.moflon.emf.codegen.dependency;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;

/**
 * This dependency registers itself as resource factory for the file extension
 * of the URI given in the constructor.
 *
 * @author Gergely Varró - Initial implementation
 * @author Roland Kluge - Docu
 * 
 */
public class PackageRemappingDependency extends SimpleDependency implements Resource.Factory {
	
	/** Use this constant for the corresponding configuration option of the constructor */
	public static final boolean HANDLE_GENERATED_EPACKAGE_URIS = true;

	/** Use this constant for the corresponding configuration option of the constructor */
	public static final boolean USE_GENERATED_EPACKAGE_RESOURCE = true;
	
	/** Use this constant for the corresponding configuration option of the constructor */
	public static final boolean USE_ECORE_RESOURCE_FACTORY_OPTIONS = true;

	private final boolean handleGeneratedEPackageURIs;

	private final boolean useGeneratedEPackageResource;

	private final boolean useEcoreResourceFactoryOptions;

	public PackageRemappingDependency(URI uri) {
		this(uri, false, false);
	}

	/**
	 * Convenience constructor for {@link PackageRemappingDependency} with
	 * 'useEcoreResourceFactoryOptions' set to false
	 * 
	 * @param uri
	 *            the {@link URI} of the {@link Resource}
	 * @param handleGeneratedEPackageURIs
	 *            for
	 *            {@link SDMEnhancedEcoreResource#setHandleGeneratedEPackageURIs(boolean)}
	 * @param useGeneratedEPackageResource
	 *            for
	 *            {@link SDMEnhancedEcoreResource#setUseGeneratedEPackageResource(boolean)}
	 */
	public PackageRemappingDependency(final URI uri, final boolean handleGeneratedEPackageURIs,
			final boolean useGeneratedEPackageResource) {
		this(uri, handleGeneratedEPackageURIs, useGeneratedEPackageResource, false);
	}

	/**
	 * Configures this dependency
	 * 
	 * @param uri
	 *            the {@link URI} of the {@link Resource}
	 * @param handleGeneratedEPackageURIs
	 *            for
	 *            {@link SDMEnhancedEcoreResource#setHandleGeneratedEPackageURIs(boolean)}
	 * @param useGeneratedEPackageResource
	 *            for
	 *            {@link SDMEnhancedEcoreResource#setUseGeneratedEPackageResource(boolean)}
	 * @param useEcoreResourceFactoryOptions
	 *            if true, the Resource created by {@link #createResource(URI)} is
	 *            preconfigured with the same options as
	 *            {@link EcoreResourceFactoryImpl}
	 */
	public PackageRemappingDependency(final URI uri, final boolean handleGeneratedEPackageURIs,
			final boolean useGeneratedEPackageResource, final boolean useEcoreResourceFactoryOptions) {
		super(uri);
		this.handleGeneratedEPackageURIs = handleGeneratedEPackageURIs;
		this.useGeneratedEPackageResource = useGeneratedEPackageResource;
		this.useEcoreResourceFactoryOptions = useEcoreResourceFactoryOptions;
	}

	/**
	 * 
	 * This method temporarily registers this class as {@link Resource.Factory} for
	 * the {@link Resource} to be loaded (see
	 * {@link #registerThisClassAsFactoryForUri(ResourceSet)}. After
	 * loading/creating the {@link Resource}, the original factory is restored (if
	 * existed) (see
	 * {@link #restoryOriginalEcoreFactory(String, Map, org.eclipse.emf.ecore.resource.Resource.Factory)}).
	 */
	@Override
	public Resource getResource(ResourceSet resourceSet, boolean loadContent) {
		final Resource.Factory originalEcoreFactory = registerThisClassAsFactoryForUri(resourceSet);

		final Resource resource = super.getResource(resourceSet, loadContent);

		restoryOriginalEcoreFactory(resourceSet, originalEcoreFactory);

		return resource;
	}

	/**
	 * Loads the corresponding Resource using this class as
	 * {@link Resource.Factory}.
	 * 
	 * This method temporarily registers this class as {@link Resource.Factory} for
	 * the {@link Resource} to be loaded (see
	 * {@link #registerThisClassAsFactoryForUri(ResourceSet)}. After
	 * loading/creating the {@link Resource}, the original factory is restored (if
	 * existed) (see
	 * {@link #restoryOriginalEcoreFactory(String, Map, org.eclipse.emf.ecore.resource.Resource.Factory)}).
	 */
	@Override
	public Resource getResource(final ResourceSet resourceSet, final boolean loadContent,
			final boolean forcePreemptiveCreate) {
		final Resource.Factory originalEcoreFactory = registerThisClassAsFactoryForUri(resourceSet);

		final Resource resource = super.getResource(resourceSet, loadContent, forcePreemptiveCreate);

		restoryOriginalEcoreFactory(resourceSet, originalEcoreFactory);

		return resource;
	}

	/**
	 * Resource factory implementation to be used in
	 * {@link #getResource(ResourceSet, boolean, boolean)}
	 */
	@Override
	public Resource createResource(final URI uri) {
		SDMEnhancedEcoreResource resource = null;
		if (this.getUri().equals(uri)) {
			if (useEcoreResourceFactoryOptions) {
				// Same options as in EcoreResourceFactoryImpl
				resource = new SDMEnhancedEcoreResource(uri) {
					@Override
					protected boolean useIDs() {
						return eObjectToIDMap != null || idToEObjectMap != null;
					}
				};
				resource.setEncoding("UTF-8");
				resource.getDefaultSaveOptions().put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
				resource.getDefaultSaveOptions().put(XMLResource.OPTION_LINE_WIDTH, 80);
				resource.getDefaultSaveOptions().put(XMLResource.OPTION_URI_HANDLER,
						new URIHandlerImpl.PlatformSchemeAware());
			} else {
				resource = new SDMEnhancedEcoreResource(uri);
			}
			resource.setHandleGeneratedEPackageURIs(handleGeneratedEPackageURIs);
			resource.setUseGeneratedEPackageResource(useGeneratedEPackageResource);
		}
		return resource;
	}

	/**
	 * Registers this class as {@link Resource.Factory} for the file extension of
	 * {@link #getUri()}. The original factory is returned.
	 * 
	 * @param resourceSet
	 *            the {@link ResourceSet} to update
	 * @return the original factory. May be null
	 */
	private Resource.Factory registerThisClassAsFactoryForUri(final ResourceSet resourceSet) {
		final String fileExtension = getUri().fileExtension();
		final Map<String, Object> extensionToFactoryMap = resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap();
		final Resource.Factory originalEcoreFactory = (Resource.Factory) extensionToFactoryMap.get(fileExtension);
		extensionToFactoryMap.put(fileExtension, this);
		return originalEcoreFactory;
	}

	/**
	 * Restores for the given file extension the 'originalEcoreFactory'. If
	 * 'originalEcoreFactory' is null, then the extension-to-factory mapping is
	 * removed.
	 * 
	 * @param resourceSet
	 *            the {@link ResourceSet} to update
	 * @param originalEcoreFactory
	 *            the original factory. May be null
	 */
	private void restoryOriginalEcoreFactory(final ResourceSet resourceSet,
			final Resource.Factory originalEcoreFactory) {
		final String fileExtension = this.getUri().fileExtension();
		final Map<String, Object> extensionToFactoryMap = resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap();
		if (originalEcoreFactory != null) {
			extensionToFactoryMap.put(fileExtension, originalEcoreFactory);
		} else {
			extensionToFactoryMap.remove(fileExtension);
		}
	}
}
