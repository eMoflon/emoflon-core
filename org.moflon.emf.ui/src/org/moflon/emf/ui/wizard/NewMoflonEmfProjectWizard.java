package org.moflon.emf.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

	protected void generateDefaultFiles(final IProgressMonitor monitor, IProject project) throws CoreException {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Creating default files", 1);
		String defaultEcoreFile = generateDefaultEPackageForProject(project.getName());
		WorkspaceHelper.addFile(project, MoflonConventions.getDefaultPathToEcoreFileInProject(project.getName()),
				defaultEcoreFile, subMon.split(1));
	}

	protected void createProject(IProgressMonitor monitor, IProject project, PluginProperties pluginProperties)
			throws CoreException {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Creating project", 1);
		final MoflonProjectCreator createMoflonProject = new MoflonEmfProjectCreator(project, pluginProperties,
				new MoflonEmfNature());
		ResourcesPlugin.getWorkspace().run(createMoflonProject, subMon.split(1));
	}

	/**
	 * Generates an XMI representation of the EPackage corresponding to the given
	 * project name
	 *
	 * @param projectName
	 *            the project name from which the conventional EPackage name etc.
	 *            are derived
	 * @return the raw XMI file content
	 */
	private static String generateDefaultEPackageForProject(final String projectName) {
		final String packageName = MoflonUtil.lastSegmentOf(projectName);
		final URI packageUri = MoflonConventions.getDefaultResourceDependencyUri(projectName);
		final List<String> lines = new ArrayList<>();
		lines.add("<?xml version=\"1.0\" encoding=\"ASCII\"?>");
		lines.add("<ecore:EPackage xmi:version=\"2.0\"");
		lines.add("  xmlns:xmi=\"http://www.omg.org/XMI\"");
		lines.add("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		lines.add("  xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\"");
		lines.add("  name=\"" + packageName + "\"");
		lines.add("  nsURI=\"" + packageUri + "\"");
		lines.add("  nsPrefix=\"" + projectName + "\">");
		lines.add("  <eAnnotations source=\"http://www.eclipse.org/emf/2002/GenModel\">");
		lines.add("    <details key=\"documentation\" value=\"TODO: Add documentation for " + packageName
				+ ". Hint: You may copy this element in the Ecore editor to add documentation to EClasses, EOperations, ...\"/>");
		lines.add("  </eAnnotations>");
		lines.add("</ecore:EPackage>");
		return StringUtils.join(lines, WorkspaceHelper.DEFAULT_RESOURCE_LINE_DELIMITER);
	}
}
