package org.moflon.git.ui.handler;

import java.util.Collection;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.gervarro.eclipse.workspace.util.IWorkspaceTask;
import org.gervarro.eclipse.workspace.util.WorkspaceTaskJob;
import org.moflon.core.ui.AbstractCommandHandler;

public class GitResetProjectHandler extends AbstractCommandHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		if (!(selection instanceof IStructuredSelection))
			return AbstractCommandHandler.DEFAULT_HANDLER_RESULT;

		final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

		final Collection<IProject> projects = AbstractCommandHandler.getProjectsFromSelection(structuredSelection);

		if (projects.size() == 0) {
			MessageDialog.openInformation(null, "Selection must contain a project",
					"You need at least one selection within your workspace to find the repository.");
			return AbstractCommandHandler.DEFAULT_HANDLER_RESULT;
		}

		if (!showWarningDialog())
			return null;

		final IWorkspaceTask task = new GitResetTask(projects);
		final WorkspaceTaskJob job = new WorkspaceTaskJob(task);
		job.setUser(true);
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.schedule();

		return AbstractCommandHandler.DEFAULT_HANDLER_RESULT;
	}

	private static boolean showWarningDialog() {
		final boolean userHasConfirmed = MessageDialog.openConfirm(null, "Confirm reset",
				"This will undo all changes and reset the project to HEAD. Proceed?");
		return userHasConfirmed;
	}
}
