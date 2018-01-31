package org.moflon.git;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
public final class GitHelper
{
   private static final Logger logger = Logger.getLogger(GitHelper.class);

   private static final String GIT_FOLDER = "/.git";

   /**
    * Disabled constructor
    */
   private GitHelper() {
      throw new UtilityClassNotInstantiableException();
   }

   /**
    * Resets and cleans the Git repository that contains the given project.
    *
    * If no such repository exists, an appropriate status message is returned
    *
    * The effect of this method is equal to <code>git reset --hard && git clean -fxd</code>
    *
    * @param project the project from which the git repository is to be searched
    * @param monitor the monitor to be used
    * @return
    */
   public static IStatus resetAndCleanContainingGitRepository(IProject project, IProgressMonitor monitor)
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Resetting Git repo for " + project, 4);
      IPath path = project.getLocation().makeAbsolute();
      File gitFolder = null;
      do
      {
         gitFolder = new File(path + GIT_FOLDER);

         if (path.isRoot() && !gitFolder.exists())
         {
            return new Status(IStatus.WARNING, WorkspaceHelper.getPluginId(GitHelper.class),
                  String.format("Could not find any .git folder in %s or its parents", project.getLocation().makeAbsolute()));
         }

         path = path.removeLastSegments(1);
      } while (!gitFolder.exists());
      subMon.worked(1);

      Repository rep = null;
      try
      {
         rep = FileRepositoryBuilder.create(gitFolder);
      } catch (final IOException e)
      {
         return new Status(IStatus.WARNING, WorkspaceHelper.getPluginId(GitHelper.class),
               String.format("Exception while opening git repository in %s", gitFolder), e);
      }
      subMon.worked(1);
      ProgressMonitorUtil.checkCancellation(subMon);

      final Git git = new Git(rep);
      try
      {

         final ResetCommand resetCmd = git.reset();
         resetCmd.setMode(ResetType.HARD);

         final CleanCommand cleanCmd = git.clean();
         cleanCmd.setCleanDirectories(true);
         cleanCmd.setIgnore(false);

         try
         {
            logger.debug("Resetting " + rep);
            resetCmd.call();
         } catch (final Exception e)
         {
            return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(GitHelper.class), String.format("Failed to reset %s", rep), e);
         }
         subMon.worked(1);
         ProgressMonitorUtil.checkCancellation(subMon);

         try
         {
            logger.debug("Cleaning " + rep);
            cleanCmd.call();
         } catch (final Exception e)
         {
            return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(GitHelper.class), String.format("Failed to clean %s", rep), e);
         }
         subMon.worked(1);
         ProgressMonitorUtil.checkCancellation(subMon);

         LogUtils.info(logger, "Resetting and cleaning of %s successful", rep);
      } finally
      {
         git.close();
      }

      return Status.OK_STATUS;
   }
}
