package org.moflon.core.xtext.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class ResourceUtil {
	private static ResourceUtil instance;

	private ResourceUtil() {
	}

	public static ResourceUtil getInstance() {
		if (instance == null)
			instance = new ResourceUtil();
		return instance;
	}

	public <R extends EObject> R getRootObject(EObject context, Class<R> clazz) {
		Stack<EObject> stack = new Stack<EObject>();
		stack.push(context);
		while (!stack.isEmpty()) {
			EObject element = stack.pop();
			if (element == null) {
				return null;
			} else if (clazz.isInstance(element))
				return clazz.cast(element);
			stack.push(element.eContainer());
		}
		return null;
	}

	public <E extends EObject> E getObjectFromResourceSet(URI uri, ResourceSet resourceSet, Class<E> clazz) {
		Resource res = getResource(uri, resourceSet, true);
		E scopingRoot = clazz.cast(res.getContents().get(0));
		return scopingRoot;
	}

	public ResourceSet getResourceSet() {
		return getResourceSet("xmi");
	}

	public Resource getResource(URI uri, ResourceSet resourceSet, boolean load) {
		try {
			Resource res = resourceSet.getResource(uri, false);
			if (res == null) {
				res = resourceSet.createResource(uri);
			}
			if (load)
				res.load(Collections.EMPTY_MAP);
			return res;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Resource addToResource(URI uri, ResourceSet resourceSet, EObject obj) {
		Resource resource = getResource(uri, resourceSet, false);
		resource.getContents().clear();
		resource.getContents().add(obj);
		return resource;
	}

	public void saveToResource(URI uri, ResourceSet resourceSet, EObject obj) {
		saveToResource(uri, resourceSet, obj, true);
	}

	public void saveToResource(URI uri, ResourceSet resourceSet, EObject obj, boolean deleteOld)
   {
      try
      {
    	 Resource resource = addToResource(uri, resourceSet, obj);
    	 if(deleteOld) {
    		 IResource iResource = WorkspaceHelper.INSTANCE.getIResource(uri);
    		 if(iResource.exists()) {
    			 iResource.delete(true, null);
    		 }
//    		 File file = new File(filePath);
//    		 if(file.exists()) {
//    			 file.delete();
//    		 }
    	 }
         
         resource.save(Collections.EMPTY_MAP);
      } catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

	public String getProjectNameFromURI(URI uri) {
		return uri.segment(1);
	}
	
	public String getProjectNameFromResource(Resource resource) {
		return getProjectNameFromURI(resource.getURI());
	}
	
	public URI createURIFromResource(Resource resource, String folder, String file) {
		URI originUri = resource.getURI();
        return createURIFromOtherURI(originUri, getProjectNameFromURI(originUri), folder, file);
	}
	
	public URI createPluginURI(String projectName, String path) {
		return URI.createPlatformPluginURI(projectName + "/" + path, false);
	}
	
	public URI createResourceURI(String projectName, String path) {
		return URI.createPlatformResourceURI(projectName + "/" + path, false);
	}
	
	public URI createURIFromOtherURI(URI originUri, String projectName, String folder, String file) {
			List<String> segments = Arrays.asList(originUri.toString().split("/"));
		if (segments.size() >= 3) {
			String prefix = segments.get(0) + "/" + segments.get(1) + "/";
			String path = prefix + projectName + "/" + folder + "/" + file;
			return URI.createURI(path);
		}
		return originUri;
	}

	public ResourceSet getResourceSet(String ext) {
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		Object factory = m.getOrDefault(ext, new XMIResourceFactoryImpl());
		m.put(ext, factory);
		return resourceSet;
	}
}
