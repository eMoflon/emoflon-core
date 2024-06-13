package org.moflon.core.build;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.gervarro.eclipse.workspace.autosetup.JavaProjectConfigurator;
import org.gervarro.eclipse.workspace.autosetup.PluginProjectConfigurator;
import org.gervarro.eclipse.workspace.autosetup.ProjectConfigurator;
import org.gervarro.eclipse.workspace.autosetup.WorkspaceAutoSetupModule;
import org.gervarro.eclipse.workspace.util.ProjectUtil;
import org.gervarro.eclipse.workspace.util.WorkspaceTask;
import org.moflon.core.build.nature.MoflonProjectConfigurator;
import org.moflon.core.build.nature.ProjectNatureAndBuilderConfiguratorTask;
import org.moflon.core.plugins.BuildPropertiesFileBuilder;
import org.moflon.core.plugins.PluginProperties;
import org.moflon.core.plugins.manifest.ManifestFileUpdater;
import org.moflon.core.plugins.manifest.ManifestFileUpdater.AttributeUpdatePolicy;
import org.moflon.core.plugins.manifest.PluginManifestConstants;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
import org.moflon.core.propertycontainer.MoflonPropertiesContainerHelper;
import org.moflon.core.utilities.MoflonConventions;
import org.moflon.core.utilities.WorkspaceHelper;

public abstract class MoflonProjectCreator extends WorkspaceTask implements ProjectConfigurator {
	private final IProject project;

	private final PluginProperties pluginProperties;

	private final MoflonProjectConfigurator projectConfigurator;

	public MoflonProjectCreator(final IProject project, final PluginProperties projectProperties,
			final MoflonProjectConfigurator projectConfigurator) {
		this.project = project;
		this.pluginProperties = projectProperties;
		this.projectConfigurator = projectConfigurator;
	}

	/**
	 * Returns the list of lines for the .gitignore file to be created in the
	 * project's root folder
	 *
	 * @return the list of lines
	 */
	protected abstract List<String> getGitignoreLines();

	/**
	 * Returns the ID of the nature to be added to .project
	 *
	 * @return the nature ID
	 * @throws CoreException
	 *                           if determining the nature ID fails
	 */
	protected abstract String getNatureId() throws CoreException;

	/**
	 * Returns the ID of the builder to be added to .project
	 *
	 * @return the builder ID
	 * @throws CoreException
	 *                           if determining the builder ID fails
	 */
	protected abstract String getBuilderId() throws CoreException;

	/**
	 * Returns true if optional compiler warnings in /gen shall be ignored The
	 * default behavior is not to ignore warnings.
	 */
	protected boolean shallIgnoreGenWarnings() {
		return false;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws CoreException {
		if (!project.exists()) {
			final String projectName = pluginProperties.getProjectName();
			final SubMonitor subMon = SubMonitor.convert(monitor, "Creating project " + projectName, 13);

			// (1) Create project
			final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
			project.create(description, IWorkspace.AVOID_UPDATE, subMon.split(1));
			project.open(IWorkspace.AVOID_UPDATE, subMon.split(1));

			// (2) Configure natures and builders (.project file)
			final JavaProjectConfigurator javaProjectConfigurator = new JavaProjectConfigurator();
			final MoflonProjectConfigurator moflonProjectConfigurator = this.projectConfigurator;
			final PluginProjectConfigurator pluginProjectConfigurator = new PluginProjectConfigurator();
			final ProjectNatureAndBuilderConfiguratorTask natureAndBuilderConfiguratorTask = new ProjectNatureAndBuilderConfiguratorTask(
					project, false);
			natureAndBuilderConfiguratorTask.updateNatureIDs(moflonProjectConfigurator, true);
			natureAndBuilderConfiguratorTask.updateNatureIDs(javaProjectConfigurator, true);
			natureAndBuilderConfiguratorTask.updateBuildSpecs(javaProjectConfigurator, true);
			natureAndBuilderConfiguratorTask.updateBuildSpecs(moflonProjectConfigurator, true);
			natureAndBuilderConfiguratorTask.updateNatureIDs(pluginProjectConfigurator, true);
			natureAndBuilderConfiguratorTask.updateBuildSpecs(pluginProjectConfigurator, true);
			WorkspaceTask.executeInCurrentThread(natureAndBuilderConfiguratorTask, IWorkspace.AVOID_UPDATE,
					subMon.split(1));

			// (3) Create folders and files in project
			createFoldersIfNecessary(project, subMon.split(4));
			addGitignoreFile(project, subMon.split(2));
			addGitKeepFiles(project, subMon.split(2));

			// (4) Create MANIFEST.MF file
			createManifestFile();

			// (5) Create build.properties file
			new BuildPropertiesFileBuilder().createBuildProperties(project, subMon.split(1));

			// (6) Configure Java settings (.classpath file)
			final IJavaProject javaProject = JavaCore.create(project);
			final IClasspathEntry srcFolderEntry = JavaCore
					.newSourceEntry(WorkspaceHelper.getSourceFolder(project).getFullPath());

			// Integration projects contain a lot of (useful?) boilerplate code in /gen,
			// which requires to ignore warnings such as 'unused variable', 'unused import'
			// etc.
			final IClasspathAttribute[] genFolderClasspathAttributes = shallIgnoreGenWarnings()
					? new IClasspathAttribute[] { JavaCore.newClasspathAttribute("ignore_optional_problems", "true") }
					: new IClasspathAttribute[] {};
			final IClasspathEntry genFolderEntry = JavaCore.newSourceEntry(
					WorkspaceHelper.getGenFolder(project).getFullPath(), new IPath[0], new IPath[0], null,
					genFolderClasspathAttributes);
			final IClasspathEntry jreContainerEntry = JavaCore
					.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER"));
			final IClasspathEntry pdeContainerEntry = JavaCore
					.newContainerEntry(new Path("org.eclipse.pde.core.requiredPlugins"));
			javaProject.setRawClasspath(
					new IClasspathEntry[] { srcFolderEntry, genFolderEntry, jreContainerEntry,
							pdeContainerEntry },
					WorkspaceHelper.getBinFolder(project).getFullPath(), true, subMon.split(1));

			// (7) Create Moflon properties file (moflon.properties.xmi)
			MoflonPropertiesContainerHelper moflonPropertiesContainerHelper = new MoflonPropertiesContainerHelper(getProject(), new NullProgressMonitor());
			final MoflonPropertiesContainer moflonProperties = moflonPropertiesContainerHelper.load();
			moflonPropertiesContainerHelper.save();
		}
	}

	/**
	 * Initializes the Manifest.MF file
	 * 
	 * @throws CoreException
	 *                           if an error occurs
	 */
	private void createManifestFile() throws CoreException {
		validatePluginProperties();

		new ManifestFileUpdater().processManifest(project, manifest -> {
			boolean changed = false;
			final String name = pluginProperties.get(PluginProperties.NAME_KEY);
			final String pluginId = pluginProperties.get(PluginProperties.PLUGIN_ID_KEY);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.MANIFEST_VERSION, "1.0",
					AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_MANIFEST_VERSION,
					"2", AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_NAME, name,
					AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_SYMBOLIC_NAME,
					pluginId + ";singleton:=true", AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_VERSION,
					"0.0.1.qualifier", AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_VENDOR, "",
					AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_ACTIVATION_POLICY,
					"lazy", AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest,
					PluginManifestConstants.BUNDLE_EXECUTION_ENVIRONMENT, "JavaSE-21", AttributeUpdatePolicy.FORCE);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.AUTOMATIC_MODULE_NAME,
					pluginId, AttributeUpdatePolicy.KEEP);
			return changed;
		});
	}

	/**
	 * Returns the handle to the project that shall be created. Of course, the
	 * project need not exist yet.
	 *
	 * @return the handle to the project to create
	 */
	public final IProject getProject() {
		return project;
	}

	/**
	 * Returns the properties of the plugin project to create
	 *
	 * @return the plugin properties
	 */
	public PluginProperties getPluginProperties() {
		return pluginProperties;
	}

	/**
	 * Validates the presence of the necessary keys for this builder
	 *
	 * @throws CoreException
	 *                           if a required key-value mapping is missing
	 */
	private void validatePluginProperties() throws CoreException {
		validateNotNull(getPluginProperties(), PluginProperties.NAME_KEY);
		validateNotNull(getPluginProperties(), PluginProperties.PLUGIN_ID_KEY);
	}

	/**
	 * Validates that the given key is present in the given properties
	 *
	 * @param pluginProperties
	 *                             the properties to check
	 * @param key
	 *                             the key to check
	 * @throws CoreException
	 *                           if there is no mapping for the given key in the
	 *                           given properties
	 */
	private void validateNotNull(final PluginProperties pluginProperties, final String key) throws CoreException {
		if (!pluginProperties.containsKey(key))
			throw new CoreException(new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()),
					String.format("Key %s not found in %s", key, pluginProperties)));
	}

	/**
	 * Adds a default .gitignore file to the given project to prevent adding
	 * generated files to the repository
	 *
	 * The contents of the created file are fetched from
	 * {@link #getGitignoreLines()}
	 *
	 * @param project
	 *                    the project for which to generate the .gitignore file
	 * @param monitor
	 *                    the progress monitor
	 */
	public void addGitignoreFile(final IProject project, final IProgressMonitor monitor) throws CoreException {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Creating .gitignore file for " + project, 1);

		WorkspaceHelper.createGitignoreFileIfNotExists(project.getFile(WorkspaceHelper.GITIGNORE_FILENAME), //
				getGitignoreLines(), subMon.split(1));
	}

	public void createFoldersIfNecessary(final IProject project, final IProgressMonitor monitor) throws CoreException {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Creating folders within project " + project, 9);

		WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getSourceFolder(project), subMon.split(1));
		WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getBinFolder(project), subMon.split(1));
		WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getGenFolder(project), subMon.split(1));
		WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getLibFolder(project), subMon.split(1));
		WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getModelFolder(project), subMon.split(1));
		WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getInstancesFolder(project), subMon.split(1));
	}

	/**
	 * Adds dummy files to folders that are / may be empty after project
	 * initialization.
	 *
	 * The dummy files are required because Git does not support versioning empty
	 * folders (unlike SVN).
	 *
	 * @param project
	 *                    the project for which .keep files shall be produced
	 * @param monitor
	 *                    the progress monitor
	 */
	protected void addGitKeepFiles(final IProject project, final IProgressMonitor monitor) {
		// Nothing to do in this class
	}

	@Override
	public String getTaskName() {
		return "Creating Moflon project";
	}

	@Override
	public ISchedulingRule getRule() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	@Override
	public String[] updateNatureIDs(String[] natureIDs, final boolean added) throws CoreException {
		final String natureId = getNatureId();
		if (added) {
			if (ProjectUtil.indexOf(natureIDs, natureId) < 0) {
				natureIDs = Arrays.copyOf(natureIDs, natureIDs.length + 1);
				natureIDs[natureIDs.length - 1] = natureId;
			}
		} else {
			final int naturePosition = ProjectUtil.indexOf(natureIDs, natureId);
			if (naturePosition >= 0) {
				natureIDs = WorkspaceAutoSetupModule.remove(natureIDs, naturePosition);
			}
		}
		return natureIDs;
	}

	@Override
	public ICommand[] updateBuildSpecs(final IProjectDescription description, ICommand[] buildSpecs,
			final boolean added) throws CoreException {
		final String builderId = getBuilderId();

		if (added) {
			int javaBuilderPosition = ProjectUtil.indexOf(buildSpecs, "org.eclipse.jdt.core.javabuilder");
			int moflonBuilderPosition = ProjectUtil.indexOf(buildSpecs, builderId);
			if (moflonBuilderPosition < 0) {
				final ICommand manifestBuilder = description.newCommand();
				manifestBuilder.setBuilderName(builderId);
				buildSpecs = Arrays.copyOf(buildSpecs, buildSpecs.length + 1);
				moflonBuilderPosition = buildSpecs.length - 1;
				buildSpecs[moflonBuilderPosition] = manifestBuilder;
			}
			if (javaBuilderPosition < moflonBuilderPosition) {
				final ICommand moflonBuilder = buildSpecs[moflonBuilderPosition];
				System.arraycopy(buildSpecs, javaBuilderPosition, buildSpecs, javaBuilderPosition + 1,
						moflonBuilderPosition - javaBuilderPosition);
				moflonBuilderPosition = javaBuilderPosition++;
				buildSpecs[moflonBuilderPosition] = moflonBuilder;
			}
		} else {
			final int moflonBuilderPosition = ProjectUtil.indexOf(buildSpecs, builderId);
			if (moflonBuilderPosition >= 0) {
				final ICommand[] oldBuilderSpecs = buildSpecs;
				buildSpecs = new ICommand[oldBuilderSpecs.length - 1];
				if (moflonBuilderPosition > 0) {
					System.arraycopy(oldBuilderSpecs, 0, buildSpecs, 0, moflonBuilderPosition);
				}
				if (moflonBuilderPosition == buildSpecs.length) {
					System.arraycopy(oldBuilderSpecs, moflonBuilderPosition + 1, buildSpecs, moflonBuilderPosition,
							buildSpecs.length - moflonBuilderPosition);
				}
			}
		}
		return buildSpecs;
	}
}
