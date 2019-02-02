package org.moflon.core.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * The {@link #execute(ExecutionEvent)} method of this class opens the wizard
 * having the ID returned by {@link #getWizardId()}
 * 
 * This handler only works for {@link IStructuredSelection} objects
 * 
 * @author Roland Kluge - Initial implementation
 */
public abstract class AbstractOpenWizardHandler extends AbstractCommandHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		final ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		try {
			if (selection instanceof IStructuredSelection)
				UiUtilities.openWizard(getWizardId(), window, (IStructuredSelection) selection);
		} catch (final Exception e) {
			Logger.getRootLogger().info(
					String.format("Cannot initialize wizard with ID %s on selection %s", getWizardId(), selection));
		}

		return AbstractCommandHandler.DEFAULT_HANDLER_RESULT;
	}

	protected abstract String getWizardId();

}
