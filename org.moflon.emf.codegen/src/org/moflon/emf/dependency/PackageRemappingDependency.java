package org.moflon.emf.dependency;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;

/**
 * This dependency registers itself as resource factory for the file extension of the URI given in the constructor.
 *
 */
public class PackageRemappingDependency extends SimpleDependency implements Resource.Factory
{
   private final boolean handleGeneratedEPackageURIs;

   private final boolean useGeneratedEPackageResource;

   private final boolean useEcoreResourceFactoryOptions;

   public PackageRemappingDependency(URI uri)
   {
      this(uri, false, false);
   }

   public PackageRemappingDependency(URI uri, boolean handleGeneratedEPackageURIs, boolean useGeneratedEPackageResource)
   {
	   this(uri, handleGeneratedEPackageURIs, useGeneratedEPackageResource, false);
   }

   public PackageRemappingDependency(URI uri, boolean handleGeneratedEPackageURIs, boolean useGeneratedEPackageResource,
		   boolean useEcoreResourceFactoryOptions)
   {
      super(uri);
      this.handleGeneratedEPackageURIs = handleGeneratedEPackageURIs;
      this.useGeneratedEPackageResource = useGeneratedEPackageResource;
      this.useEcoreResourceFactoryOptions = useEcoreResourceFactoryOptions;
   }

   @Override
   /* From Dependency */
   public Resource getResource(ResourceSet resourceSet, boolean loadContent)
   {
      String fileExtension = uri.fileExtension();

      Map<String, Object> extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();

      Resource.Factory originalEcoreFactory = (Resource.Factory) extensionToFactoryMap.get(fileExtension);
      extensionToFactoryMap.put(fileExtension, this);

      Resource resource = super.getResource(resourceSet, loadContent);

      if (originalEcoreFactory != null)
      {
         extensionToFactoryMap.put(fileExtension, originalEcoreFactory);
      } else
      {
         extensionToFactoryMap.remove(fileExtension);
      }

      return resource;
   }

   public Resource getResource(final ResourceSet resourceSet, final boolean loadContent, final boolean forcePreemptiveCreate)
   {
      String fileExtension = uri.fileExtension();
      Map<String, Object> extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
      Resource.Factory originalEcoreFactory = (Resource.Factory) extensionToFactoryMap.get(fileExtension);
      extensionToFactoryMap.put(fileExtension, this);

	   Resource resource = super.getResource(resourceSet, loadContent, forcePreemptiveCreate);

	   if (originalEcoreFactory != null) {
		   extensionToFactoryMap.put(fileExtension, originalEcoreFactory);
	   } else {
		   extensionToFactoryMap.remove(fileExtension);
	   }
	   return resource;
   }

   @Override
   /* From Resource.Factory */
   public Resource createResource(URI uri) {
	   SDMEnhancedEcoreResource resource = null;
	   if (this.uri.equals(uri)) {
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
			   resource.getDefaultSaveOptions().put(XMLResource.OPTION_URI_HANDLER, new URIHandlerImpl.PlatformSchemeAware());
		   } else {
			   resource = new SDMEnhancedEcoreResource(uri);
		   }
		   resource.setHandleGeneratedEPackageURIs(handleGeneratedEPackageURIs);
		   resource.setUseGeneratedEPackageResource(useGeneratedEPackageResource);
	   }
	   return resource;
   }
}
