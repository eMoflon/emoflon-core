package org.moflon.git.ui.handler;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.gervarro.eclipse.workspace.util.IWorkspaceTask;
import org.moflon.core.utilities.ProgressMonitorUtil;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.git.GitHelper;

class GitResetTask implements IWorkspaceTask
{
   private final Collection<IProject> projects;

   GitResetTask(Collection<IProject> projects)
   {
      this.projects = projects;
   }

   @Override
   public String getTaskName()
   {
      return "Reset and clean Git repositories";
   }

   public ISchedulingRule getRule() {
      return ResourcesPlugin.getWorkspace().getRoot();
   }

   @Override
   public void run(final IProgressMonitor monitor) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Resetting and cleaning Git repositories", 2 * this.projects.size());
      final MultiStatus status = new MultiStatus(WorkspaceHelper.getPluginId(getClass()), 0, "Problems during resetting and cleaning", null);
      for (final IProject project : this.projects)
      {
         final IStatus resetStatus = GitHelper.resetAndCleanContainingGitRepository(project, subMon);
         subMon.worked(1);
         status.add(resetStatus);
         ProgressMonitorUtil.checkCancellation(subMon);
         project.refreshLocal(IResource.DEPTH_INFINITE, subMon.split(1));
      }
      if (status.matches(IStatus.ERROR)) {
         throw new CoreException(status);
      }
   }
}