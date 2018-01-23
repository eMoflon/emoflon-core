package org.moflon.core.build;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.UtilityClassNotInstantiableException;
import org.moflon.core.utilities.WorkspaceHelper;

/**
 * This class contains utility methods related Eclipse workspace tasks.
 * 
 * @author Roland Kluge - Initial implementation
 */
public class TaskUtilities
{

   private static final Logger logger = Logger.getLogger(TaskUtilities.class);

   // Disabled constructor
   private TaskUtilities()
   {
      new UtilityClassNotInstantiableException();
   }

   /**
    * Processes the given queue of jobs (in order) by scheduling one job after the other.
    * 
    * Autobuilding is switched off before processing the queue and reset to its original state after processing the queue has completed
    * 
    * The jobs are run as user.
    * 
    * @param jobs the sequence of jobs to run (in order)
    * @throws CoreException
    */
   public static void processJobQueueAsUser(final List<Job> jobs) throws CoreException
   {
      processJobQueueInternal(jobs, true);
   }

   /**
    * Processes the given queue of jobs (in order) by scheduling one job after the other.
    * 
    * Autobuilding is switched off before processing the queue and reset to its original state after processing the queue has completed
    * 
    * The jobs are **not** run as user.
    * 
    * @param jobs the sequence of jobs to run (in order)
    * @throws CoreException
    */
   public static void processJobQueueInBackground(final List<Job> jobs) throws CoreException
   {
      processJobQueueInternal(jobs, false);
   }

   /**
    * Processes the given queue of jobs (in order) by scheduling one job after the other.
    * 
    * Autobuilding is switched off before processing the queue and reset to its original state after processing the queue has completed
    * @param jobs the sequence of jobs to run (in order)
    * @throws CoreException
    */
   private static void processJobQueueInternal(final List<Job> jobs, final boolean runAsUserJobs) throws CoreException
   {
      if (jobs.size() > 0)
      {
         final boolean isAutoBuilding = TaskUtilities.switchAutoBuilding(false);
         final JobChangeAdapter jobExecutor = new JobChangeAdapter() {

            @Override
            public void done(final IJobChangeEvent event)
            {
               final IStatus result = event.getResult();
               if (result.isOK() && !jobs.isEmpty())
               {
                  final int numRemainingJobs = jobs.size();
                  final Job nextJob = jobs.remove(0);
                  LogUtils.debug(logger, "Job %s completed with status %s.", event.getJob().toString(), result);
                  LogUtils.info(logger, "%d job(s) remaining.", numRemainingJobs);
                  LogUtils.debug(logger, "Scheduling job %s", nextJob);
                  nextJob.addJobChangeListener(this);
                  nextJob.setUser(runAsUserJobs);
                  nextJob.schedule();
               } else
               {
                  LogUtils.info(logger, "All jobs complete. Last status: %s", WorkspaceHelper.getSeverityAsString(result));
                  try
                  {
                     final boolean isSwitchingAutoBuildModeRequired = isAutoBuilding != ResourcesPlugin.getWorkspace().isAutoBuilding();
                     if (isSwitchingAutoBuildModeRequired)
                     {
                        TaskUtilities.switchAutoBuilding(isAutoBuilding);
                     }
                  } catch (CoreException e)
                  {
                     LogUtils.error(logger, e);
                  }
               }
            }

         };
         LogUtils.info(logger, "%d job(s) remaining.", jobs.size());
         final Job firstJob = jobs.remove(0);
         LogUtils.debug(logger, "Scheduling job %s", firstJob);
         firstJob.addJobChangeListener(jobExecutor);
         firstJob.setUser(runAsUserJobs);
         firstJob.schedule();
      }
   }

   /**
    * Tries to set the Auto Build flag of the workspace to newAutoBuildValue.
    * 
    * @param newAutoBuildValue the desired new auto-building flag state
    * @return the previous auto-building flag
    * @throws CoreException
    */
   public static final boolean switchAutoBuilding(final boolean newAutoBuildValue) throws CoreException
   {
      final IWorkspace workspace = ResourcesPlugin.getWorkspace();
      final IWorkspaceDescription description = workspace.getDescription();
      final boolean oldAutoBuildValue = description.isAutoBuilding();
      if (oldAutoBuildValue ^ newAutoBuildValue)
      {
         description.setAutoBuilding(newAutoBuildValue);
         workspace.setDescription(description);
      }
      return oldAutoBuildValue;
   }
}
