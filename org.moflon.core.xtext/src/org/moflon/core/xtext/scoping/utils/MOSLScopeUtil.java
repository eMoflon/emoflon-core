package org.moflon.core.xtext.scoping.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.moflon.core.xtext.utils.ResourceUtil;

public class MOSLScopeUtil {
	@Deprecated // Since 2018-08-09
	private static MOSLScopeUtil instance;

	/**
	 * @deprecated All methods are now static, so we do not need the synthetic
	 *             singleton
	 */
	@Deprecated // Since 2018-08-09
	public static MOSLScopeUtil getInstance() {
		if (instance == null)
			instance = new MOSLScopeUtil();
		return instance;
	}

	public static <E extends EObject> List<E> getObjectsFromResource(Resource resource, Class<E> clazz) {
		List<EObject> allContent = new ArrayList<>();
		resource.getAllContents().forEachRemaining(allContent::add);
		return allContent.parallelStream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
	}

	public static ResourceSet getResourceSet() {
		return ResourceUtil.getResourceSet("xmi");
	}

	public static Resource addToResource(URI uri, ResourceSet resourceSet, EObject obj) {
		Resource resource = ResourceUtil.getResource(uri, resourceSet, false);
		resource.getContents().clear();
		resource.getContents().add(obj);
		return resource;
	}

	public static void saveToResource(URI uri, ResourceSet resourceSet, EObject obj) {
		try {
			Resource resource = addToResource(uri, resourceSet, obj);
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
