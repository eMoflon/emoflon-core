package org.moflon.emf.codegen.dependency;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * A simple resource that immediately reads resources from a resource set using
 * the stored URI.
 * 
 * @author Gergely Varró - Initial implementation
 * @author Roland Kluge - Docu
 * 
 * @see #getResource(ResourceSet, boolean)
 */
public class SimpleDependency implements Dependency {
	
	/** Use this constant for the corresponding configuration option of the constructor */
	public static final boolean LOAD_CONTENT = true;

	/** Use this constant for the corresponding configuration option of the constructor */
	public static final boolean FORCE_PREEMPTIVE_CREATE = true;
	
	protected final URI uri;

	/**
	 * Configures the {@link URI} of this dependency
	 * @param uri the URI to use for creating/loading the corresponding {@link Resource}
	 */
	public SimpleDependency(final URI uri) {
		this.uri = uri;
	}

	/**
	 * Fetches the {@link Resource} with the configured {@link URI} ({@link #getUri()}) from the given {@link ResourceSet}.
	 * If the {@link ResourceSet} does not contain an appropriate {@link Resource} or if 'loadContent' is true, the corresponding {@link Resource} is created.
	 */
	@Override
	public Resource getResource(final ResourceSet resourceSet, final boolean loadContent) {
		final Resource resource = resourceSet.getResource(getUri(), loadContent);
		return (resource != null || loadContent) ? resource : resourceSet.createResource(getUri());
	}

	/**
	 * Similar to {@link #getResource(ResourceSet, boolean)}, but forces the
	 * resource creationg via {@link ResourceSet#createResource(URI)} if
	 * 'forcePreemptiveCreate' is true.
	 * 
	 * @param resourceSet the resource set to load this dependency into
	 * @param loadContent whether to load the resource from the configured URI (#getUri())
	 * @param forcePreemptiveCreate if true, the resource is force-created
	 * @return the loaded/created resource
	 */
	public Resource getResource(final ResourceSet resourceSet, final boolean loadContent,
			boolean forcePreemptiveCreate) {
		if (forcePreemptiveCreate) {
			resourceSet.createResource(getUri());
		}
		return resourceSet.getResource(getUri(), loadContent);
	}
	
	/**
	 * Returns the {@link URI} of the {@link Resource} to be loaded/created 
	 * @return the {@link URI}
	 */
	public URI getUri() {
		return uri;
	}
}
