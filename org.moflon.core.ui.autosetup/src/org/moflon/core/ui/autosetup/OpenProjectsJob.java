package org.moflon.core.ui.autosetup;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.moflon.core.utilities.ProgressMonitorUtil;
import org.moflon.core.utilities.WorkspaceHelper;

/**
 * This {@link Job} opens the list of preconfigured projects (see {@link CloseProjectsJob#CloseProjectsJob(String, List)}) upon {@link #run(IProgressMonitor)}
 *
 * @author Roland Kluge - Initial implementation
 */
public final class OpenProjectsJob extends Job
{
   private final List<IProject> projects;

   public OpenProjectsJob(final String name, final List<IProject> projects)
   {
      super(name);
      this.projects = projects;
   }

   @Override
   protected IStatus run(IProgressMonitor monitor)
   {
      final SubMonitor openingMonitor = SubMonitor.convert(monitor, "Opening projects", projects.size());
      try
      {
         for (final IProject project : projects)
         {
            project.open(openingMonitor.split(1));
            ProgressMonitorUtil.checkCancellation(openingMonitor);
         }
      } catch (final CoreException e)
      {
         return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), e.getMessage(), e);
      } finally
      {
         SubMonitor.done(monitor);
      }
      return Status.OK_STATUS;
   }
}