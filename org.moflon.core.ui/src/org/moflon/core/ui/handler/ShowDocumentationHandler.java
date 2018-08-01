/**
 * 
 */
package org.moflon.core.ui.handler;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moflon.core.ui.AbstractCommandHandler;
import org.moflon.core.ui.visualisation.Configurator;
import org.moflon.core.ui.visualisation.EMoflonPlantUMLGenerator;

/**
 * @author Johannes Brandt
 *
 */
public class ShowDocumentationHandler extends AbstractCommandHandler {

	private static final Logger logger = LogManager.getLogger(ShowModelDetailsHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		logger.debug("Executing 'Show Model Details'.");
		
		// toggle the state of the command and retrieve the old value of the state
		// (before it was clicked)
		Command command = event.getCommand();
		boolean toggleState = !HandlerUtil.toggleCommandState(command);

		// retrieve / change / set configuration
		Configurator.getInstance().setDiagramStyle(EMoflonPlantUMLGenerator.SHOW_DOCUMENTATION, toggleState);
		
		// notify for change (indirectly, by setting focus to the editor, listeners are
		// fired, which in turn update the PlantUml view
		final IEditorPart linkedEditor = HandlerUtil.getActiveEditor(event);
		updateEditor(linkedEditor);

		return null;
	}
	
	private void updateEditor(IEditorPart editor) {
		if (editor != null) {
			editor.setFocus();
		}
		else {
			logger.warn("Could not find any appropriate editor instance to initiate an update of the PlantUml viewpart.");
		}
	}
}
