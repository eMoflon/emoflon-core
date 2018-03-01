package org.moflon.core.build.nature;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;

/**
 * An eMoflon-specific override of
 * {@link org.gervarro.eclipse.workspace.autosetup.ProjectNatureAndBuilderConfiguratorTask}.
 *
 * @author Gergely Varró - Initial implementation
 * @author Roland Kluge
 *
 */
public class ProjectNatureAndBuilderConfiguratorTask
		extends org.gervarro.eclipse.workspace.autosetup.ProjectNatureAndBuilderConfiguratorTask {

	/**
	 * Passes the given values to the super constructor
	 */
	public ProjectNatureAndBuilderConfiguratorTask(final IProject project, final boolean forceOverwrite)
			throws CoreException {
		super(project, forceOverwrite);
	}

	@Override
	public void run(final IProgressMonitor monitor) throws CoreException {
		final IProject project = getProject();
		final IProjectDescription description = project.getDescription();
		if (!Arrays.deepEquals(natureIDs, description.getNatureIds())) {
			final IWorkspace workspace = project.getWorkspace();
			final IStatus status = workspace.validateNatureSet(natureIDs);
			if (status.matches(IStatus.CANCEL)) {
				throw new OperationCanceledException();
			}
			if (status.isOK() || status.matches(IStatus.INFO | IStatus.WARNING)) {
				description.setNatureIds(natureIDs);
				description.setBuildSpec(buildSpecs);
				project.setDescription(description, monitor);
			}
		} else if (!Arrays.deepEquals(buildSpecs, description.getBuildSpec())) {
			description.setBuildSpec(buildSpecs);
			project.setDescription(description, monitor);
		}
	}

}
