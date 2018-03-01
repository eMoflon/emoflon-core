package org.moflon.core.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.moflon.core.ui.AbstractCommandHandler;
import org.moflon.core.ui.MoflonCoreUiActivator;

/**
 * Triggers a reload of the logging configuration.
 */
public class ReconfigureLoggingHandler extends AbstractCommandHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		MoflonCoreUiActivator.getDefault().reconfigureLogging();
		return null;
	}

}
