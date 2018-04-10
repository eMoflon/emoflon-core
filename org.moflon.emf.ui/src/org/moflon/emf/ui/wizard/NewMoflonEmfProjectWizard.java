package org.moflon.emf.ui.wizard;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IWorkingSet;
import org.moflon.core.build.MoflonProjectCreator;
import org.moflon.core.plugins.PluginProducerWorkspaceRunnable;
import org.moflon.core.plugins.PluginProperties;
import org.moflon.core.ui.AbstractMoflonProjectInfoPage;
import org.moflon.core.ui.AbstractMoflonWizard;
import org.moflon.core.ui.WorkingSetUtilities;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.MoflonConventions;
import org.moflon.core.utilities.MoflonUtil;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.emf.build.MoflonEmfNature;
import org.moflon.emf.codegen.MoflonGenModelBuilder;

public class NewMoflonEmfProjectWizard extends AbstractMoflonWizard {
	private static final Logger logger = Logger.getLogger(NewMoflonEmfProjectWizard.class);

	public static final String NEW_REPOSITORY_PROJECT_WIZARD_ID = "org.moflon.emf.ui.wizard.NewMoflonEmfProjectWizard";

	protected AbstractMoflonProjectInfoPage projectInfo;

	@Override
	public void addPages() {
		projectInfo = new NewMoflonEmfProjectInfoPage();
		addPage(projectInfo);
	}

	@Override
	protected void doFinish(final IProgressMonitor monitor) throws CoreException {
		try {
			final SubMonitor subMon = SubMonitor.convert(monitor, "Creating eMoflon EMF project", 8);

			final String projectName = projectInfo.getProjectName();

			final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			final PluginProperties pluginProperties = new PluginProperties();
			pluginProperties.put(PluginProperties.NAME_KEY, projectName);
			pluginProperties.put(PluginProperties.PLUGIN_ID_KEY, projectName);
			createProject(subMon, project, pluginProperties);
			subMon.worked(3);

			generateDefaultFiles(subMon, project);
			subMon.worked(3);

			ResourcesPlugin.getWorkspace().run(new PluginProducerWorkspaceRunnable(project, pluginProperties),
					subMon.split(1));
			subMon.worked(2);

			// Add to most recent working set
			final IWorkingSet[] recentWorkingSet = WorkingSetUtilities.getSelectedWorkingSet(getSelection(),
					getActivePart());
			if (recentWorkingSet.length != 0) {
				WorkingSetUtilities.addProjectToWorkingSet(project, recentWorkingSet[0]);
			}

		} catch (final Exception e) {
			LogUtils.error(logger, e);
		}
	}

	protected void generateDefaultFiles(final IProgressMonitor monitor, final IProject project) throws CoreException {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Creating default files", 1);
		final String projectName = project.getName();
      final String packageName = MoflonUtil.lastSegmentOf(projectName);
      final URI projectUri = MoflonGenModelBuilder.determineProjectUriBasedOnPreferences(project);
      final URI packageUri = URI.createURI(projectUri.toString() + MoflonConventions.getDefaultPathToEcoreFileInProject(projectName));
      final String defaultEcoreFile = DefaultEPackageContentGenerator.generateDefaultEPackageForProject(projectName, packageName, packageUri.toString());
		WorkspaceHelper.addFile(project, MoflonConventions.getDefaultPathToEcoreFileInProject(projectName),
				defaultEcoreFile, subMon.split(1));
	}

	protected void createProject(IProgressMonitor monitor, IProject project, PluginProperties pluginProperties)
			throws CoreException {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Creating project", 1);
		final MoflonProjectCreator createMoflonProject = new MoflonEmfProjectCreator(project, pluginProperties,
				new MoflonEmfNature());
		ResourcesPlugin.getWorkspace().run(createMoflonProject, subMon.split(1));
	}
}
