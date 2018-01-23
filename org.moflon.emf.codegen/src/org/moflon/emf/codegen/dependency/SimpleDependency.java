package org.moflon.emf.codegen.dependency;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * A simple resource that immediately reads resources from a resource set using the stored URI.
 *
 */
public class SimpleDependency implements Dependency {
	protected final URI uri;

	public SimpleDependency(URI uri) {
		this.uri = uri;
	}

	@Override
	public Resource getResource(ResourceSet resourceSet, boolean loadContent) {
		Resource resource = resourceSet.getResource(uri, loadContent);
		return resource != null || loadContent ? resource : resourceSet.createResource(uri);
	}

	public Resource getResource(final ResourceSet resourceSet,
			final boolean loadContent, boolean forcePreemptiveCreate) {
		if (forcePreemptiveCreate) {
			resourceSet.createResource(uri);
		}
		return resourceSet.getResource(uri, loadContent);
	}
}
