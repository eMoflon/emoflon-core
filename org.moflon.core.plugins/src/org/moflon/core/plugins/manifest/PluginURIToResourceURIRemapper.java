package org.moflon.core.plugins.manifest;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.WorkspaceHelper;

/**
 * This class provides utility methods for mapping platform:/plugin to
 * platform:/resource URIs
 *
 * @author Roland Kluge - Initial implementation
 *
 */
public class PluginURIToResourceURIRemapper {
	private static final Logger logger = Logger.getLogger(PluginURIToResourceURIRemapper.class);

	/**
	 * Runs
	 * {@link PluginURIToResourceURIRemapper#createPluginToResourceMap(ResourceSet, IProject)}
	 * for any project in the current workspace.
	 *
	 * @param set
	 *            the resource set to be adapted
	 */
	public static final void createPluginToResourceMap(final ResourceSet set) {
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
			try {
				createPluginToResourceMap(set, project);
			} catch (IOException e) {
				LogUtils.error(logger, e);
			}
	}

	/**
	 * Adds to the given {@link ResourceSet} an {@link URIConverter} that
	 * platform:/plugin URIs to platform:/resource URIs for the given project.
	 *
	 * The project needs to be an accessible plugin project. Otherwise, nothing
	 * happens.
	 *
	 * @param set
	 *            the resource set to be adapted
	 * @param project
	 *            the
	 * @throws IOException
	 */
	public static void createPluginToResourceMap(final ResourceSet set, final IProject project) throws IOException {
		if (project.isAccessible()) {
			try {
				if (project.hasNature(WorkspaceHelper.PLUGIN_NATURE_ID)) {
					new ManifestFileUpdater().processManifest(project, manifest -> {
						String pluginId = project.getName();
						String symbolicName = (String) manifest.getMainAttributes()
								.get(PluginManifestConstants.BUNDLE_SYMBOLIC_NAME);

						if (symbolicName != null) {
							int strip = symbolicName.indexOf(";singleton:=");
							if (strip != -1)
								symbolicName = symbolicName.substring(0, symbolicName.indexOf(";singleton:="));

							pluginId = symbolicName;
						} else {
							logger.warn("Unable to extract plugin id from manifest of project " + project.getName()
									+ ". Falling back to project name.");
						}
						URI pluginURI = URI.createPlatformPluginURI(pluginId + "/", true);
						URI resourceURI = URI.createPlatformResourceURI(project.getName() + "/", true);
						set.getURIConverter().getURIMap().put(pluginURI, resourceURI);
						logger.debug("Created mapping: " + pluginURI + " -> " + resourceURI);
						return false;
					});

				}
			} catch (CoreException e) {
				logger.error("Failed to check nature for project " + project.getName());
			}

		}
	}

}
