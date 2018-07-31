package org.moflon.core.ui.visualisation;

import java.util.Collection;
import org.eclipse.emf.ecore.EObject;
import org.moflon.core.ui.VisualiserUtilities;
import org.moflon.core.ui.visualisation.Configurator.StrategyPart;
import org.moflon.core.ui.visualisation.strategy.DiagramStrategy;
import org.moflon.core.ui.visualisation.strategy.ObjectDiagramStrategies;

/**
 * Visualises UML Object Diagrams for Ecore models.
 *
 */
public class EMoflonModelVisualiser extends EMoflonEcoreVisualiser<ObjectDiagram> {

	public EMoflonModelVisualiser() {
		super();

		// set default strategy
		strategy = getDefaultStrategy(StrategyPart.INIT)//
				.andThen(getDefaultStrategy(StrategyPart.NEIGHBOURHOOD));

		@SuppressWarnings("unused")
		boolean blabla = true;
	}

	@Override
	public boolean supportsSelection(Collection<EObject> selection) {
		// An Ecore model must contain EObjects only, which are not EModelElements. If
		// it contains other
		// elements, the selection is not supported by this visualiser.
		return !VisualiserUtilities.hasMetamodelElements(selection);
	}

	@Override
	public boolean supportsDiagramType(Class<?> diagramClass) {
		return ObjectDiagram.class == diagramClass;
	}

	@Override
	public DiagramStrategy<ObjectDiagram> getDefaultStrategy(StrategyPart part) {
		switch (part) {
		case INIT:
			return ObjectDiagramStrategies::determineEdgesForSelection;
		case NEIGHBOURHOOD:
			return ObjectDiagramStrategies::expandNeighbourhoodBidirectional;
		default:
			return super.getDefaultStrategy(part);
		}
	}

	@Override
	protected String getDiagramBody(Collection<EObject> selection) {
		Collection<EObject> allObjects = getAllElements();

		// Create diagram and process it using the defined strategy.
		ObjectDiagram diagram = strategy.apply(new ObjectDiagram(allObjects, selection));
		diagram.setEdges(handleOpposites(diagram.getEdges()));

		return EMoflonPlantUMLGenerator.visualiseModelElements(diagram, style);
	}
}
