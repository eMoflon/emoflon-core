package org.moflon.core.ui.visualisation;

import java.util.Collection;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.moflon.core.ui.VisualiserUtilities;
import org.moflon.core.ui.visualisation.strategy.ObjectDiagramStrategies;

/**
 * Visualises UML Object Diagrams for Ecore models.
 *
 */
public class EMoflonModelVisualiser extends EMoflonEcoreVisualiser {

	/**
	 * Allows chained operations on a diagram. Should at least be {@link Function#identity()}.
	 */
	private Function<ObjectDiagram, ObjectDiagram> strategy;
	
	public EMoflonModelVisualiser() {
		super();
		
		// set default strategy
		strategy = ObjectDiagramStrategies::determineEdgesForSelection;
		strategy = strategy.andThen(ObjectDiagramStrategies::expandNeighbourhoodBidirectional);
	}
	
	@Override
	public boolean supportsSelection(Collection<EObject> selection) {
		// An Ecore model must contain EObjects only, which are not EModelElements. If
		// it contains other
		// elements, the selection is not supported by this visualiser.
		return !VisualiserUtilities.hasMetamodelElements(selection);
	}

	@Override
	protected String getDiagramBody(Collection<EObject> selection) {
		Collection<EObject> allObjects = getAllElements();
		
		// Create diagram and process it using the defined strategy.
		ObjectDiagram diagram = strategy.apply(new ObjectDiagram(allObjects, selection));
		diagram.setEdges(handleOpposites(diagram.getEdges()));
		
		return EMoflonPlantUMLGenerator.visualiseModelElements(diagram);
	}
}
