package org.moflon.git.ui.handler;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jgit.lib.Repository;
import org.gervarro.eclipse.workspace.util.IWorkspaceTask;
import org.moflon.core.utilities.ProgressMonitorUtil;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.git.GitHelper;

class GitResetTask implements IWorkspaceTask {
	private final Collection<IProject> projects;

	GitResetTask(Collection<IProject> projects) {
		this.projects = projects;
	}

	@Override
	public String getTaskName() {
		return "Reset and clean Git repositories";
	}

	public ISchedulingRule getRule() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	@Override
	public void run(final IProgressMonitor monitor) throws CoreException {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Resetting and cleaning Git repositories",
				2 * this.projects.size());
		final MultiStatus status = new MultiStatus(WorkspaceHelper.getPluginId(getClass()), 0,
				"Problems during resetting and cleaning", null);

		final Set<Repository> repositories = collectRepositories(status);
		if (status.matches(IStatus.ERROR)) {
			throw new CoreException(status);
		}

		resetAndCleanRepositories(repositories, status, subMon.split(this.projects.size()));
		if (status.matches(IStatus.ERROR)) {
			throw new CoreException(status);
		}

		for (final IProject project : this.projects) {
			project.refreshLocal(IResource.DEPTH_INFINITE, subMon.split(1));
		}
	}

	/**
	 * Resets and cleans all of the listed repositories
	 * 
	 * @param repositories
	 *            the repositories
	 * @param status
	 *            used for collecting problems
	 * @param monitor
	 *            the progress monitor
	 */
	private void resetAndCleanRepositories(final Set<Repository> repositories, final MultiStatus status,
			final IProgressMonitor monitor) {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Resetting and cleaning Git repositories",
				repositories.size());
		for (final Repository repository : repositories) {
			final IStatus resetStatus = GitHelper.resetAndCleanGitRepository(repository, subMon);
			subMon.worked(1);
			status.add(resetStatus);
			ProgressMonitorUtil.checkCancellation(subMon);
		}
	}

	/**
	 * Retrieves the set of repositories that contain the configured projects
	 * 
	 * @param status
	 *            used for collection problems
	 * @return the list of repositories
	 */
	private Set<Repository> collectRepositories(final MultiStatus status) {
		return this.projects.stream().filter(GitHelper::isInGitRepository)
				.map(project -> GitHelper.findGitRepository(project, status)).collect(Collectors.toSet());
	}
}