package org.moflon.core.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.moflon.core.plugins.manifest.ManifestFileUpdater;
import org.moflon.core.plugins.manifest.ManifestFileUpdater.AttributeUpdatePolicy;
import org.moflon.core.plugins.manifest.PluginManifestConstants;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.WorkspaceHelper;

public class PluginProducerWorkspaceRunnable implements IWorkspaceRunnable {
	private static final Logger logger = Logger.getLogger(PluginProducerWorkspaceRunnable.class);

	protected static final String DEFAULT_BUNDLE_MANIFEST_VERSION = "2";

	protected static final String DEFAULT_MANIFEST_VERSION = "1.0";

	protected static final String DEFAULT_BUNDLE_VENDOR = "";

	protected static final String DEFAULT_BUNDLE_VERSION = "0.0.1.qualifier";

	protected static final String SCHEMA_BUILDER_NAME = "org.eclipse.pde.SchemaBuilder";

	protected static final String MANIFEST_BUILDER_NAME = "org.eclipse.pde.ManifestBuilder";

	private ManifestFileUpdater manifestFileBuilder = new ManifestFileUpdater();

	private BuildPropertiesFileBuilder buildPropertiesFileBuilder = new BuildPropertiesFileBuilder();

	private IProject project;

	private PluginProperties projectProperties;

	public PluginProducerWorkspaceRunnable(final IProject project, final PluginProperties projectProperties) {
		this.project = project;
		this.projectProperties = projectProperties;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws CoreException {
		configureManifest(monitor);
		addContainerToBuildPath(project, "org.eclipse.pde.core.requiredPlugins");
	}

	/**
	 * Returns the project, configured in the constructor
	 * 
	 * @return
	 */
	protected IProject getProject() {
		return this.project;
	}

	/**
	 * Returns the project properties as configured in the constructor
	 * 
	 * @return
	 */
	protected PluginProperties getProjectProperties() {
		return this.projectProperties;
	}

	protected void configureManifest(final IProgressMonitor monitor) throws CoreException {
		final SubMonitor subMon = SubMonitor.convert(monitor,
				String.format("Setting up plugin project %s", getProject().getName()), 2);

		registerPluginBuildersAndAddNature(getProject(), subMon.split(1));

		manifestFileBuilder.processManifest(getProject(), manifest -> {
			boolean changed = false;
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.MANIFEST_VERSION,
					DEFAULT_MANIFEST_VERSION, AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_MANIFEST_VERSION,
					DEFAULT_BUNDLE_MANIFEST_VERSION, AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_NAME,
					getProjectProperties().get(PluginProperties.NAME_KEY), AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_SYMBOLIC_NAME,
					getProjectProperties().get(PluginProperties.PLUGIN_ID_KEY) + ";singleton:=true",
					AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_VERSION,
					DEFAULT_BUNDLE_VERSION, AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_VENDOR,
					DEFAULT_BUNDLE_VENDOR, AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_ACTIVATION_POLICY,
					"lazy", AttributeUpdatePolicy.KEEP);
			changed |= ManifestFileUpdater.updateAttribute(manifest,
					PluginManifestConstants.BUNDLE_EXECUTION_ENVIRONMENT, "JavaSE-1.8", AttributeUpdatePolicy.KEEP);

			changed |= ManifestFileUpdater.updateDependencies(manifest, Arrays
					.asList(new String[] { WorkspaceHelper.PLUGIN_ID_ECORE, WorkspaceHelper.PLUGIN_ID_ECORE_XMI }));

			changed |= ManifestFileUpdater.updateDependencies(manifest, ManifestFileUpdater
					.extractDependencies(getProjectProperties().get(PluginProperties.DEPENDENCIES_KEY)));

			return changed;
		});

		buildPropertiesFileBuilder.createBuildProperties(getProject(), subMon.split(1));
	}

	protected static void registerPluginBuildersAndAddNature(final IProject currentProject,
			final IProgressMonitor monitor) throws CoreException {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Registering plugin builders and add plugin nature", 2);

		final IProjectDescription description = WorkspaceHelper.getDescriptionWithAddedNature(currentProject,
				WorkspaceHelper.PLUGIN_NATURE_ID, subMon.split(1));

		final List<ICommand> oldBuilders = new ArrayList<>(Arrays.asList(description.getBuildSpec()));

		final List<ICommand> newBuilders = new ArrayList<>();
		if (!containsBuilder(description, MANIFEST_BUILDER_NAME)) {
			final ICommand manifestBuilder = description.newCommand();
			manifestBuilder.setBuilderName(MANIFEST_BUILDER_NAME);
			newBuilders.add(manifestBuilder);
		}

		if (!containsBuilder(description, SCHEMA_BUILDER_NAME)) {
			final ICommand schemaBuilder = description.newCommand();
			schemaBuilder.setBuilderName(SCHEMA_BUILDER_NAME);
			newBuilders.add(schemaBuilder);
		}

		// Add old builders after the plugin builders
		newBuilders.addAll(oldBuilders);

		description.setBuildSpec(newBuilders.toArray(new ICommand[newBuilders.size()]));
		currentProject.setDescription(description, subMon.split(1));
	}

	/**
	 * Returns whether the given description contains a builder with the given name
	 * 
	 * @param description
	 *            the description to analyze
	 * @param name
	 *            the name of the builder to look for
	 * @return whether description contains a builder with the given name
	 */
	protected static boolean containsBuilder(final IProjectDescription description, final String name) {
		return Arrays.asList(description.getBuildSpec()).stream().anyMatch(c -> c.getBuilderName().equals(name));
	}

	/**
	 * Adds the given container to the build path of the given project if it
	 * contains no entry with the same name, yet.
	 */
	protected static void addContainerToBuildPath(final IProject project, final String container) {
		final IJavaProject iJavaProject = JavaCore.create(project);
		try {
			// Get current entries on the classpath
			Collection<IClasspathEntry> classpathEntries = new ArrayList<>(
					Arrays.asList(iJavaProject.getRawClasspath()));

			addContainerToBuildPath(classpathEntries, container);

			setBuildPath(iJavaProject, classpathEntries);
		} catch (JavaModelException e) {
			LogUtils.error(logger, e, "Unable to set classpath variable");
		}
	}

	/**
	 * Adds the given container to the list of build path entries (if not included,
	 * yet)
	 */
	private static void addContainerToBuildPath(final Collection<IClasspathEntry> classpathEntries,
			final String container) {
		IClasspathEntry entry = JavaCore.newContainerEntry(new Path(container));
		for (IClasspathEntry iClasspathEntry : classpathEntries) {
			if (iClasspathEntry.getPath().equals(entry.getPath())) {
				// No need to add variable - already on classpath
				return;
			}
		}

		classpathEntries.add(entry);
	}

	private static void setBuildPath(final IJavaProject javaProject, final Collection<IClasspathEntry> entries)
			throws JavaModelException {
		final SubMonitor subMon = SubMonitor.convert(new NullProgressMonitor(), "Set build path", 1);
		// Create new buildpath
		IClasspathEntry[] newEntries = new IClasspathEntry[entries.size()];
		entries.toArray(newEntries);

		// Set new classpath with added entries
		javaProject.setRawClasspath(newEntries, subMon.split(1));
	}
}
