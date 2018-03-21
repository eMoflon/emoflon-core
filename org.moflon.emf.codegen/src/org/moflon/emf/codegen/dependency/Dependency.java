package org.moflon.emf.codegen.dependency;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Generic interface
 * @author Gergely Varró - Initial implementation 
 * @author Roland Kluge - Docu
 */
public interface Dependency {
	/**
	 * Loads this dependency into the given {@link ResourceSet}
	 * @param resourceSet the resource set that shall hold the {@link Resource} to be loaded/created
	 * @param loadContent true if the {@link Resource} shall be loaded if necessary
	 * @return the created/loaded resource.
	 */
	Resource getResource(ResourceSet resourceSet, boolean loadContent);
}
