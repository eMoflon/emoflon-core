package org.moflon.core.ui.autosetup;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.team.internal.ui.wizards.ImportProjectSetOperation;
import org.eclipse.ui.IWorkingSet;
import org.gervarro.eclipse.workspace.util.WorkspaceTask;
import org.gervarro.eclipse.workspace.util.WorkspaceTaskJob;
import org.moflon.core.build.TaskUtilities;
import org.moflon.core.utilities.ExceptionUtil;
import org.moflon.core.utilities.LogUtils;

@SuppressWarnings("restriction")
public abstract class WorkspaceInstaller
{
   protected static final Logger logger = Logger.getLogger(WorkspaceInstaller.class);

   private static final String MASTER_BRANCH = "master";

   public void installPsfFiles(final List<File> psfFiles)
   {
      final String label = joinBasenames(psfFiles);
      installPsfFiles(psfFiles, label);
   }

   public void installPsfFiles(final List<File> psfFiles, final String label)
   {
      installPsfFiles(psfFiles, label, MASTER_BRANCH);
   }

   public void installPsfFiles(final List<File> psfFiles, final String label, final String customBranch)
   {
      try
      {
         prepareWorkspace();

         // We extract the contents beforehand because the following action may delete them if we load PSF files
         // directly from the workspace
         final String psfContent;
         if (customBranch == null || MASTER_BRANCH.equals(customBranch))
         {
            psfContent = PsfFileUtils.joinPsfFile(psfFiles);
         } else
         {
            psfContent = PsfFileUtils.joinPsfFile(psfFiles).replaceAll(Pattern.quote("," + MASTER_BRANCH + ","), "," + customBranch + ",");
         }

         final int numberOfProjects = StringUtils.countMatches(psfContent, "<project ");
         final int numberOfWorkingSets = StringUtils.countMatches(psfContent, "<workingSets ");
         final String joinedPaths = joinBasenames(psfFiles);
         logger.info(String.format("Checking out %d projects and %d working sets in %s.", numberOfProjects, numberOfWorkingSets, joinedPaths));

         final ImportProjectSetOperation importProjectSetOperation = new ImportProjectSetOperation(null, psfContent,
               psfFiles.size() > 1 ? null : psfFiles.get(0).getAbsolutePath(), new IWorkingSet[0]) {

            @Override
            public String getJobName()
            {
               return "Checking out projects";
            }

            @Override
            public ISchedulingRule getSchedulingRule()
            {
               return ResourcesPlugin.getWorkspace().getRoot();
            }

            @Override
            public void done(final IJobChangeEvent event)
            {
               if (event.getResult().isOK())
               {
                  final WorkspaceTask buildConfiguratorTask = new WorkspaceTask() {

                     @Override
                     public void run(IProgressMonitor monitor) throws CoreException
                     {
                        performBuildAndTest(label);
                     }

                     @Override
                     public String getTaskName()
                     {
                        return "Configuring build and test process";
                     }

                     @Override
                     public ISchedulingRule getRule()
                     {
                        return ResourcesPlugin.getWorkspace().getRoot();
                     }
                  };
                  new WorkspaceTaskJob(buildConfiguratorTask).schedule();
               }
            }

         };
         importProjectSetOperation.run();
      } catch (final InterruptedException e)
      {
         // Operation cancelled by the user on the GUI
         throw new OperationCanceledException();
      } catch (final InvocationTargetException | IOException e)
      {
         LogUtils.error(logger, e);
         return;
      } catch (final CoreException e)
      {
         final String message = "Sorry, I was unable to check out the projects in the PSF file.\n"//
               + "  If you did not explicitly cancel then please check the following (most probable first):\n"//
               + "      (1) Ensure that the Git repositories appearing in the error message below are clean or do not exist (Window/Perspective/Open Perspective/Other.../Git)\n" //
               + "      (2) If possible, start with an empty, fresh workspace. Although the PSF import offers to delete the projects this may fail, especially on Windows.\n"//
               + "      (3) Are you sure you have access to all the projects (if they do not support anonymous access)?\n"//
               + "      (4) The PSF file might be outdated - please check for an update of the test plugin\n"//
               + "      (5) If it's quite late in the night, our server might be down performing a back-up - try again in a few hours.\n"//
               + "      (6) If none of these helped, write us a mail to contact@emoflon.org :)\n" //
               + "\n" //
               + "Exception of type " + e.getClass().getName() + ", Message: " + ExceptionUtil.displayExceptionAsString(e);
         logger.error(message);
         return;
      }
   }

   private final void performBuildAndTest(final String label)
   {
      final List<Job> jobs = new ArrayList<Job>();

      enqueuePreprocessingJobs(jobs);

      final Job moflonProjectExplorerJob = new ExploreEMoflonProjectsJob("Exploring eMoflon projects", jobs, label, this);
      moflonProjectExplorerJob.setRule(ResourcesPlugin.getWorkspace().getRoot());
      jobs.add(moflonProjectExplorerJob);
      jobs.add(new Job("Good bye.") {
         @Override
         protected IStatus run(IProgressMonitor monitor)
         {
            logger.info("Code generation jobs completed successfully.");
            return Status.OK_STATUS;
         }
      });

      try
      {
         TaskUtilities.processJobQueueInBackground(jobs);
         logger.info("Code generation jobs scheduled.");
      } catch (final CoreException e)
      {
         LogUtils.error(logger, e);
      }
      logger.info("End of automatic workspace configuration reached. Please wait for the code generation jobs to finish. Bye bye.");
   }

   /**
    * Adds jobs to the queue that shall be invoked prior to building the projects in the workspace
    * @param jobs the job queue
    */
   protected void enqueuePreprocessingJobs(final List<Job> jobs)
   {
      // Nothing to do here
   }

   // This is required to avoid NPEs when checking out plugin projects (a problem with JDT)
   private static void prepareWorkspace()
   {
      try
      {
         JavaModelManager.getExternalManager().createExternalFoldersProject(new NullProgressMonitor());
      } catch (final CoreException e)
      {
         LogUtils.error(logger, e);
      }

   }

   private static String joinBasenames(final List<File> files)
   {
      return files.stream().map(f -> f.getName()).collect(Collectors.joining(", "));
   }

   protected abstract IProject[] getProjectsToBuild();
}
