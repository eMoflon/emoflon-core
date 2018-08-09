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
import org.moflon.core.ui.visualisation.ClassDiagram;
import org.moflon.core.ui.visualisation.Configurator;
import org.moflon.core.ui.visualisation.ObjectDiagram;
import org.moflon.core.ui.visualisation.Configurator.StrategyPart;
import org.moflon.core.ui.visualisation.strategy.ClassDiagramStrategies;
import org.moflon.core.ui.visualisation.strategy.DiagramStrategy;
import org.moflon.core.ui.visualisation.strategy.ObjectDiagramStrategies;

/**
 * Allows to enable the visualisation of the 1-neighbouhood of a selection for
 * metamodel and model visualiser.
 * 
 * @author Johannes Brandt
 *
 */
public class NeighbourhoodStrategyHandler extends AbstractCommandHandler {

	private static final Logger logger = LogManager.getLogger(NeighbourhoodStrategyHandler.class);

	private DiagramStrategy<ClassDiagram> classToggleActive;
	private DiagramStrategy<ObjectDiagram> objectToggleActive;
	private DiagramStrategy<ClassDiagram> classToggleInactive;
	private DiagramStrategy<ObjectDiagram> objectToggleInactive;

	public NeighbourhoodStrategyHandler() {
		super();

		// initialize strategies
		classToggleActive = ClassDiagramStrategies::expandNeighbourhoodBidirectional;
		classToggleInactive = DiagramStrategy.identity();
		objectToggleActive = ObjectDiagramStrategies::expandNeighbourhoodBidirectional;
		objectToggleInactive = DiagramStrategy.identity();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		logger.debug("Executing 'Show Model Details'.");

		// toggle the state of the command and retrieve the old value of the state
		// (before it was clicked)
		Command command = event.getCommand();
		boolean toggleState = !HandlerUtil.toggleCommandState(command);

		// retrieve / change / set configuration
		setStrategy(toggleState);

		// notify for change (indirectly, by setting focus to the editor, listeners are
		// fired, which in turn update the PlantUml view
		final IEditorPart linkedEditor = HandlerUtil.getActiveEditor(event);
		updateView(linkedEditor);

		return null;
	}

	/**
	 * Instructs the configurator to set the desired strategy, depending on the
	 * specified toggle state.
	 * 
	 * @param toggleState
	 *            Iff <code>true</code>, the 1-neighbourhood strategy is chosen.
	 */
	private void setStrategy(boolean toggleState) {
		if (toggleState) {
			Configurator.getInstance().setDiagramStrategy(ClassDiagram.class, StrategyPart.NEIGHBOURHOOD,
					classToggleActive);
			Configurator.getInstance().setDiagramStrategy(ObjectDiagram.class, StrategyPart.NEIGHBOURHOOD,
					objectToggleActive);
		} else {
			Configurator.getInstance().setDiagramStrategy(ClassDiagram.class, StrategyPart.NEIGHBOURHOOD,
					classToggleInactive);
			Configurator.getInstance().setDiagramStrategy(ObjectDiagram.class, StrategyPart.NEIGHBOURHOOD,
					objectToggleInactive);
		}
	}

	private void updateView(IEditorPart editor) {
		if (editor != null) {
			editor.setFocus();
		} else {
			logger.warn(
					"Could not find any appropriate editor instance to initiate an update of the PlantUml viewpart.");
		}
	}
}
