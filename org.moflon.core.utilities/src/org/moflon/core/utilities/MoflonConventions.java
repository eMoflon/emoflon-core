package org.moflon.core.utilities;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;

/**
 * This class captures all conventions used by the EMF build process of eMoflon
 *
 * Conventions include * The Bundle-SymbolicName and the project name of
 * projects created with eMoflon are identical * The generated Ecore file is
 * named after the last segment of the project name * For instance, in project
 * P/x.y.z.mymodel, the Ecore file is located here:
 * P/x.y.z.mymodel/model/Mymodel.ecore * The default NS URIs use the
 * platform:/resource schema. * For instance, the metamodel of project
 * P/x.y.z.mymodel should have (i) root package 'mymodel', (ii) NS prefix
 * 'x.y.z.mymodel' and (iii) NS URI
 * 'platform:/resource/x.y.z.mymodel/model/Mymodel.ecore'
 *
 * @author Roland Kluge - Initial implementation
 */
public class MoflonConventions {
	public static final String MOFLON_CONFIG_FILE = "moflon.properties.xmi";

	/**
	 * Returns an URI for dependencies that follow the {@link MoflonConventions}
	 *
	 * The default URI is created from the following path:
	 * /[pluginId]/{@link #getDefaultPathToEcoreFileInProject(String)}
	 *
	 * @param pluginId
	 *            the ID of the plugin
	 * @return the platform:/resource URI to the metamodel of the given plugin
	 */
	public static URI getDefaultResourceDependencyUri(final String pluginId) {
		return URI.createPlatformResourceURI(
				"/" + pluginId + "/" + MoflonConventions.getDefaultPathToEcoreFileInProject(pluginId), true);
	}

	/**
	 * Returns an URI for dependencies that follow the {@link MoflonConventions}
	 *
	 * The default URI is created from the following path:
	 * /[pluginId]/{@link #getDefaultPathToEcoreFileInProject(String)}
	 *
	 * @param pluginId
	 *            the ID of the plugin
	 * @return the platform:/plugin URI to the metamodel of the given plugin
	 */
	public static URI getDefaultPluginDependencyUri(final String pluginId) {
		return URI.createPlatformPluginURI("/" + pluginId + "/" + getDefaultPathToEcoreFileInProject(pluginId), true);
	}

	/**
	 * Returns the default name of the eMoflon-related files in a project. By
	 * convention, the result is equal to the last segment (.-separated) with the
	 * first letter capitalized.
	 * 
	 * @param projectName
	 *            the project name
	 * @return the default file name
	 */
	public static String getDefaultNameOfFileInProjectWithoutExtension(final String projectName) {
		return MoflonUtil.lastCapitalizedSegmentOf(projectName);
	}

	/**
	 * The default path is
	 * /model/[{@link #getDefaultNameOfFileInProjectWithoutExtension(String)}][suffix]
	 * 
	 * @param projectName
	 *            the project name
	 * @param suffix
	 *            the file extension
	 * @return the default path to the model
	 */
	public static String getDefaultPathToFileInProject(final String projectName, final String suffix) {
		return WorkspaceHelper.MODEL_FOLDER + "/" + getDefaultNameOfFileInProjectWithoutExtension(projectName) + suffix;
	}

	/**
	 * Equivalent to {@link #getDefaultPathToFileInProject(String, String)} with
	 * .genmodel suffix
	 * 
	 * @param projectName
	 *            the project name
	 * @return the default path to the generator model
	 */
	public static String getDefaultPathToGenModelInProject(final String projectName) {
		return getDefaultPathToFileInProject(projectName, ".genmodel");
	}

	/**
	 * Equivalent to {@link #getDefaultPathToFileInProject(String, String)} with
	 * .ecore suffix
	 * 
	 * @param projectName
	 *            the project name
	 * @return the default path to the Ecore model
	 */
	public static String getDefaultPathToEcoreFileInProject(final String projectName) {
		return getDefaultPathToFileInProject(projectName, ".ecore");
	}

	/**
	 * Returns the project-relative URI corresponding to
	 * {@link #getDefaultPathToEcoreFileInProject(String)}
	 * 
	 * @param project
	 *            the project
	 * @return the URI
	 */
	public static URI getDefaultProjectRelativeEcoreFileURI(final IProject project) {
		return URI.createURI(getDefaultPathToEcoreFileInProject(project.getName()));
	}

	/**
	 * @deprecated Use {@link #getDefaultProjectRelativeEcoreFileURI(IProject)}
	 */
	@Deprecated // Since 2017-03-13
	public static URI getDefaultURIToEcoreFileInPlugin(final String projectName) {
		final IProject project = WorkspaceHelper.getProjectByName(projectName);
		return getDefaultProjectRelativeEcoreFileURI(project);
	}

	/**
	 * Returns handle to the default location of the moflon.properties.xmi file
	 * 
	 * @param project
	 *            the project
	 * @return the handle
	 */
	public static IFile getDefaultMoflonPropertiesFile(final IProject project) {
		return project.getFile(MOFLON_CONFIG_FILE);
	}

	/**
	 * Returns a handle to the default location of a Ecore file of an eMoflon
	 * project
	 *
	 * @param project
	 *            the project of which to extract the ecore file
	 */
	public static IFile getDefaultEcoreFile(final IProject project) {
		return project.getFile(new Path(getDefaultPathToEcoreFileInProject(project.getName())));
	}

}
