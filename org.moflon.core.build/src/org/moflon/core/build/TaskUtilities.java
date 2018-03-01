package org.moflon.core.build;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.UtilityClassNotInstantiableException;

/**
 * This class contains utility methods related Eclipse workspace tasks.
 *
 * @author Gergely Varró - Initial implementation
 * @author Roland Kluge - Fine-tuning
 */
public class TaskUtilities {

	private static final Logger logger = Logger.getLogger(TaskUtilities.class);

	// Disabled constructor
	private TaskUtilities() {
		new UtilityClassNotInstantiableException();
	}

	/**
	 * Processes the given queue of jobs (in order) by scheduling one job after the
	 * other.
	 *
	 * Autobuilding is switched off before processing the queue and reset to its
	 * original state after processing the queue has completed
	 *
	 * The jobs are run as user.
	 *
	 * @param jobs
	 *            the sequence of jobs to run (in order)
	 * @throws CoreException
	 */
	public static void processJobQueueAsUser(final List<Job> jobs) throws CoreException {
		processJobQueueInternal(jobs, true);
	}

	/**
	 * Processes the given queue of jobs (in order) by scheduling one job after the
	 * other.
	 *
	 * Autobuilding is switched off before processing the queue and reset to its
	 * original state after processing the queue has completed
	 *
	 * The jobs are **not** run as user.
	 *
	 * @param jobs
	 *            the sequence of jobs to run (in order)
	 * @throws CoreException
	 */
	public static void processJobQueueInBackground(final List<Job> jobs) throws CoreException {
		processJobQueueInternal(jobs, false);
	}

	/**
	 * Processes the given queue of jobs (in order) by scheduling one job after the
	 * other.
	 *
	 * Autobuilding is switched off before processing the queue and reset to its
	 * original state after processing the queue has completed
	 * 
	 * @param jobs
	 *            the sequence of jobs to run (in order)
	 * @throws CoreException
	 */
	private static void processJobQueueInternal(final List<Job> jobs, final boolean runAsUserJobs)
			throws CoreException {
		new AutoBuildAwareJobQueueProcessor().run(jobs, runAsUserJobs);
	}

	/**
	 * Convenience method for {@link #switchAutoBuilding(boolean)}, which catches
	 * and logs a possible {@link CoreException}.
	 * 
	 * @param isAutoBuilding
	 *            the desired auto-building flag state
	 */
	public static void switchAutoBuildingNoThrow(final boolean isAutoBuilding) {
		try {
			TaskUtilities.switchAutoBuilding(isAutoBuilding);
		} catch (final CoreException e) {
			LogUtils.error(logger, e);
		}
	}

	/**
	 * Tries to set the Auto Build flag of the workspace to newAutoBuildValue.
	 *
	 * @param newAutoBuildValue
	 *            the desired new auto-building flag state
	 * @return the previous auto-building flag
	 * @throws CoreException
	 */
	public static final boolean switchAutoBuilding(final boolean newAutoBuildValue) throws CoreException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IWorkspaceDescription description = workspace.getDescription();
		final boolean oldAutoBuildValue = description.isAutoBuilding();
		if (oldAutoBuildValue != newAutoBuildValue) {
			description.setAutoBuilding(newAutoBuildValue);
			workspace.setDescription(description);
		}
		return oldAutoBuildValue;
	}
}
