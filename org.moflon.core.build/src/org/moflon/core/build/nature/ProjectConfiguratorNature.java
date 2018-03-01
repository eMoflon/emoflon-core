package org.moflon.core.build.nature;

import java.util.Arrays;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.gervarro.eclipse.workspace.autosetup.ProjectConfigurator;
import org.gervarro.eclipse.workspace.util.ProjectUtil;
import org.gervarro.eclipse.workspace.util.WorkspaceTask;

public abstract class ProjectConfiguratorNature implements IProjectNature, ProjectConfigurator {
	private IProject project;

	@Override
	public String[] updateNatureIDs(final String[] natureIDs, final boolean added) throws CoreException {
		return natureIDs;
	}

	@Override
	public ICommand[] updateBuildSpecs(final IProjectDescription description, final ICommand[] buildSpecs,
			final boolean added) throws CoreException {
		return buildSpecs;
	}

	@Override
	public void configure() throws CoreException {
		final ProjectNatureAndBuilderConfiguratorTask configuratorTask = new ProjectNatureAndBuilderConfiguratorTask(
				project, false);
		configuratorTask.updateBuildSpecs(this, true);
		WorkspaceTask.executeInCurrentThread(configuratorTask, IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
	}

	@Override
	public void deconfigure() throws CoreException {
		final ProjectNatureAndBuilderConfiguratorTask configuratorTask = new ProjectNatureAndBuilderConfiguratorTask(
				project, false);
		configuratorTask.updateBuildSpecs(this, false);
		WorkspaceTask.executeInCurrentThread(configuratorTask, IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
	}

	@Override
	public final IProject getProject() {
		return project;
	}

	@Override
	public final void setProject(final IProject project) {
		this.project = project;
	}

	// Utility methods
	protected String[] insertAtEnd(String[] ids, String interestingID) {
		ids = Arrays.copyOf(ids, ids.length + 1);
		ids[ids.length - 1] = interestingID;
		return ids;
	}

	protected boolean containsNatureID(String[] ids, String interestingID) {
		return ProjectUtil.indexOf(ids, interestingID) >= 0;
	}
}
