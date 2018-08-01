package org.moflon.core.ui.visualisation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
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

		diagram = determineObjectNames(diagram);
		return EMoflonPlantUMLGenerator.visualiseModelElements(diagram, style);
	}

	/**
	 * Determines instance names for all EObjects in selection and neighbourhood
	 * collection in the specified diagram.
	 * 
	 * @param diagram
	 *            The diagram, for which the EObject instance names shall be
	 *            determined.
	 * @return The diagram with the EObject instance names.
	 */
	private ObjectDiagram determineObjectNames(ObjectDiagram diagram) {
		int noEClassCount = 1;
		Map<EObject, String> instanceNames = diagram.getInstanceNames();
		Map<EClass, Integer> instanceCounts = new HashMap<>();

		determineObjectNames(diagram.getSelection(), instanceNames, instanceCounts, noEClassCount);
		determineObjectNames(diagram.getNeighbourhood(), instanceNames, instanceCounts, noEClassCount);

		return diagram;
	}

	private void determineObjectNames(Collection<EObject> elements, Map<EObject, String> instanceNames,
			Map<EClass, Integer> instanceCounts, int noEClassCount) {
		for(EObject current : elements) {
			// use EClass name with lower case first letter, if no EClass: "o"
			String name = (current.eClass() != null) ? current.eClass().getName() : "o";
			name = (name == null || name.length() == 0) ? "o" : name;
			name = name.substring(0, 1).toLowerCase() + name.substring(1);

			// no EClass -> use global object counter
			if (current.eClass() == null) {
				instanceNames.put(current, name + noEClassCount);
				noEClassCount++;
				continue;
			}

			// determine and update instance count
			int instanceCount = 1;
			if (instanceCounts.containsKey(current.eClass())) {
				instanceCount = instanceCounts.get(current.eClass()) + 1;
			}
			instanceNames.put(current, name + instanceCount);
			instanceCounts.put(current.eClass(), instanceCount);
		}
	}
}
