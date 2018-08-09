package org.moflon.core.xtext.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class ResourceUtil {

	@Deprecated // Since 2018-08-09
	private static ResourceUtil instance;

	@Deprecated // Since 2018-08-09
	private ResourceUtil() {
	}

	/**
	 * @deprecated All methods are now static, so we do not need the synthetic
	 *             singleton
	 */
	@Deprecated // Since 2018-08-09
	public static ResourceUtil getInstance() {
		if (instance == null)
			instance = new ResourceUtil();
		return instance;
	}

	public static <R extends EObject> R getRootObject(EObject context, Class<R> clazz) {
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

	public static <E extends EObject> E getObjectFromResourceSet(URI uri, ResourceSet resourceSet, Class<E> clazz) {
		Resource res = getResource(uri, resourceSet, true);
		E scopingRoot = clazz.cast(res.getContents().get(0));
		return scopingRoot;
	}

	public static ResourceSet getResourceSet() {
		return getResourceSet("xmi");
	}

	public static Resource getResource(URI uri, ResourceSet resourceSet, boolean load) {
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

	public static Resource addToResource(URI uri, ResourceSet resourceSet, EObject obj) {
		Resource resource = getResource(uri, resourceSet, false);
		resource.getContents().clear();
		resource.getContents().add(obj);
		return resource;
	}

	public static void saveToResource(URI uri, ResourceSet resourceSet, EObject obj) {
		saveToResource(uri, resourceSet, obj, true);
	}

	public static void saveToResource(URI uri, ResourceSet resourceSet, EObject obj, boolean deleteOld) {
		try {
			Resource resource = addToResource(uri, resourceSet, obj);
			if (deleteOld) {
				IResource iResource = getIResource(uri);
				if (iResource.exists()) {
					iResource.delete(true, null);
				}
			}

			resource.save(Collections.EMPTY_MAP);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static IResource getIResource(URI uri) throws CoreException {
		IProject project = getProjectByURI(uri);
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		IPath path = getPathByURI(project, uri);
		if (path.toFile().isDirectory())
			return project.getFolder(path);
		else
			return project.getFile(path);
	}

	public static IProject getProjectByURI(URI uri) {
		String projectName = getProjectNameFromURI(uri);
		return org.moflon.core.utilities.WorkspaceHelper.getProjectByName(projectName);
	}

	public static IPath getPathByURI(IResource iResource, URI uri) {
		String[] segments = uri.segments();
		IPath path = iResource.getLocation();
		for (int index = 2; index < segments.length; ++index) {
			String segment = segments[index];
			path = path.append(segment);
		}
		return path;
	}

	public static String getProjectNameFromURI(URI uri) {
		return uri.segment(1);
	}

	public static String getProjectNameFromResource(Resource resource) {
		return getProjectNameFromURI(resource.getURI());
	}

	public static URI createURIFromResource(Resource resource, String folder, String file) {
		URI originUri = resource.getURI();
		return createURIFromOtherURI(originUri, getProjectNameFromURI(originUri), folder, file);
	}

	public static URI createPluginURI(String projectName, String path) {
		return URI.createPlatformPluginURI(projectName + "/" + path, false);
	}

	public static URI createResourceURI(String projectName, String path) {
		return URI.createPlatformResourceURI(projectName + "/" + path, false);
	}

	public static URI createURIFromOtherURI(URI originUri, String projectName, String folder, String file) {
		List<String> segments = Arrays.asList(originUri.toString().split("/"));
		if (segments.size() >= 3) {
			String prefix = segments.get(0) + "/" + segments.get(1) + "/";
			String path = prefix + projectName + "/" + folder + "/" + file;
			return URI.createURI(path);
		}
		return originUri;
	}

	public static ResourceSet getResourceSet(String ext) {
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		Object factory = m.getOrDefault(ext, new XMIResourceFactoryImpl());
		m.put(ext, factory);
		return resourceSet;
	}
}
