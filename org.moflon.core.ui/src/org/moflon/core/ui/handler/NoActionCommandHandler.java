package org.moflon.core.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.moflon.core.ui.AbstractCommandHandler;

/**
 * Null implementation of {@link AbstractCommandHandler}
 * 
 * @author Roland Kluge - Initial implementation
 */
public class NoActionCommandHandler extends AbstractCommandHandler {

	/**
	 * Does nothing
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		return AbstractCommandHandler.DEFAULT_HANDLER_RESULT;
	}

}
