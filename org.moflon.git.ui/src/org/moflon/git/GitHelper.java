package org.moflon.git;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jgit.api.CleanCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.ProgressMonitorUtil;
import org.moflon.core.utilities.UtilityClassNotInstantiableException;
import org.moflon.core.utilities.WorkspaceHelper;;

/**
 * Utility methods for working with Git
 *
 * @author Lars Fritsche - Initial implementation
 * @author Roland Kluge
 *
 */
public final class GitHelper {
	private static final Logger logger = Logger.getLogger(GitHelper.class);

	private static final String GIT_FOLDER = "/.git";

	/**
	 * Disabled constructor
	 */
	private GitHelper() {
		throw new UtilityClassNotInstantiableException();
	}

	/**
	 * @param project
	 *            the project to check
	 * @return true if the project is contained in a Git repository
	 */
	public static boolean isInGitRepository(final IProject project) {
		return findGitRepositoryRoot(project) != null;
	}

	/**
	 * Resets and cleans the given Git repository
	 *
	 * The repository should exist
	 *
	 * The effect of this method is equal to
	 * <code>git reset --hard && git clean -fxd</code>
	 *
	 * @param rep
	 *            the repository to reset and clean
	 * @param monitor
	 *            the monitor to be used
	 * @return the success or failure status
	 */
	public static IStatus resetAndCleanGitRepository(final Repository rep, final IProgressMonitor monitor) {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Resetting Git repository " + rep, 2);
		final Git git = new Git(rep);
		try {
			final ResetCommand resetCmd = git.reset();
			resetCmd.setMode(ResetType.HARD);

			final CleanCommand cleanCmd = git.clean();
			cleanCmd.setCleanDirectories(true);
			cleanCmd.setIgnore(false);

			try {
				logger.debug("Resetting " + rep);
				resetCmd.call();
			} catch (final Exception e) {
				return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(GitHelper.class),
						String.format("Failed to reset %s", rep), e);
			}
			subMon.worked(1);
			ProgressMonitorUtil.checkCancellation(subMon);

			try {
				logger.debug("Cleaning " + rep);
				cleanCmd.call();
			} catch (final Exception e) {
				return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(GitHelper.class),
						String.format("Failed to clean %s", rep), e);
			}
			subMon.worked(1);
			ProgressMonitorUtil.checkCancellation(subMon);

			LogUtils.info(logger, "Resetting and cleaning of %s successful", rep);
		} finally {
			git.close();
		}

		return Status.OK_STATUS;
	}

	/**
	 * Retrieves the Git repository containing the given project.
	 * 
	 * @param project
	 *            the project
	 * @param multiStatus
	 *            used to report problems
	 * @return the repository or <code>null</code> if the project is not inside a
	 *         Git repository
	 */
	public static Repository findGitRepository(final IProject project, final MultiStatus multiStatus) {
		final IPath pathToRepository = findGitRepositoryRoot(project);

		if (pathToRepository == null) {
			multiStatus.add(new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(GitHelper.class),
					String.format("Not a git repository: %s", project.getLocation().makeAbsolute())));
			return null;
		}

		final File gitFolder = new File(pathToRepository + GIT_FOLDER);

		try {
			final Repository rep = FileRepositoryBuilder.create(gitFolder);
			return rep;
		} catch (final IOException e) {
			multiStatus.add(new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(GitHelper.class),
					String.format("Exception while opening git repository in %s", gitFolder), e));
			return null;
		}
	}

	/**
	 * Finds the folder containing the Git metadata folder .git in the parents of
	 * the given project
	 *
	 * @param project
	 *            the project
	 * @return the folder or <code>null</code> if no such folder exists
	 */
	private static IPath findGitRepositoryRoot(final IProject project) {
		IPath pathToRepository = project.getLocation().makeAbsolute();
		{
			File gitFolder = null;
			do {
				gitFolder = new File(pathToRepository + GIT_FOLDER);

				if (gitFolder.exists()) {
					return pathToRepository;
				}

				pathToRepository = pathToRepository.removeLastSegments(1);
			} while (!pathToRepository.isRoot());
		}
		return null;
	}
}
