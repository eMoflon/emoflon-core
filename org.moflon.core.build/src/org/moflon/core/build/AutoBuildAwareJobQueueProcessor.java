package org.moflon.core.build;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.WorkspaceHelper;

/**
 * This class manages the processing of a given {@link Job} queue
 * 
 * @author Roland Kluge - Initial implementation
 *
 */
public final class AutoBuildAwareJobQueueProcessor extends JobChangeAdapter {
	private static final Logger logger = Logger.getLogger(AutoBuildAwareJobQueueProcessor.class);

	private List<Job> jobs;

	private boolean runAsUserJobs;

	private boolean initialAutoBuildState;

	/**
	 * Starts processing the given {@link Job} queue either as user or in background
	 * 
	 * @throws CoreException
	 *             if processing failed
	 */
	public void run(final List<Job> jobs, final boolean runAsUserJobs) throws CoreException {
		this.jobs = jobs;
		this.runAsUserJobs = runAsUserJobs;
		this.initialAutoBuildState = TaskUtilities.switchAutoBuilding(false);
		startProcessing();
	}

	@Override
	public void done(final IJobChangeEvent event) {
		final IStatus result = event.getResult();
		if (!result.isOK()) {
			LogUtils.info(logger, "Job %s exited with non-OK status %s", event.getJob(),
					WorkspaceHelper.getSeverityAsString(result));
			TaskUtilities.switchAutoBuildingNoThrow(initialAutoBuildState);
		} else if (jobs.isEmpty()) {
			LogUtils.info(logger, "All jobs completed successfully.");
			TaskUtilities.switchAutoBuildingNoThrow(initialAutoBuildState);
		} else {
			LogUtils.debug(logger, "Job %s completed with status %s.", event.getJob(), result);
			logQueueStatus();
			scheduleNextJobInternal();
		}
	}

	/**
	 * Schedules the first {@link Job} (if exists)
	 */
	private void startProcessing() {
		if (this.jobs.isEmpty()) {
			LogUtils.info(logger, "No jobs to schedule.");
		} else {
			logQueueStatus();
			scheduleNextJobInternal();
		}
	}

	/**
	 * Removes the next {@link Job} from the queue and schedules it
	 * 
	 * @param jobs
	 * @param runAsUserJobs
	 * @param jobChangeAdapter
	 */
	private void scheduleNextJobInternal() {
		final Job nextJob = jobs.remove(0);
		LogUtils.debug(logger, "Scheduling job %s", nextJob);
		nextJob.addJobChangeListener(this);
		nextJob.setUser(runAsUserJobs);
		nextJob.schedule();
	}

	/**
	 * Print statistics of current queue
	 */
	private void logQueueStatus() {
		final int numRemainingJobs = jobs.size();
		final String jobCountSuffix = numRemainingJobs == 1 ? "" : "s";
		LogUtils.info(logger, "%d job%s in queue.", numRemainingJobs, jobCountSuffix);
	}
}