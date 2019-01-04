package org.moflon.core.ui.visualisation.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.moflon.core.ui.VisualiserUtilities;
import org.moflon.core.ui.handler.visualisation.AbbreviateLabelsHandler;
import org.moflon.core.ui.handler.visualisation.NeighbourhoodStrategyHandler;
import org.moflon.core.ui.handler.visualisation.ShowModelDetailsHandler;
import org.moflon.core.ui.visualisation.EMoflonPlantUMLGenerator;
import org.moflon.core.ui.visualisation.common.EMoflonEcoreVisualiser;

/**
 * Visualises UML Object Diagrams for Ecore models.
 *
 */
public class EMoflonModelVisualiser extends EMoflonEcoreVisualiser<ObjectDiagram> {
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
		diagram = determineObjectNames(diagram);
		diagram.setAbbreviateLabels(AbbreviateLabelsHandler.getVisPreference());
		diagram.setShowFullModelDetails(ShowModelDetailsHandler.getVisPreference());

		return EMoflonPlantUMLGenerator.visualiseModelElements(diagram);
	}

	/**
	 * Determines instance names for all EObjects in selection and neighbourhood
	 * collection in the specified diagram.
	 * 
	 * @param diagram The diagram, for which the EObject instance names shall be
	 *                determined.
	 * @return The diagram with the EObject instance names.
	 */
	private ObjectDiagram determineObjectNames(ObjectDiagram diagram) {
		Map<EObject, String> labels = diagram.getInstanceNames();

		Collection<EObject> allObjects = new ArrayList<>();
		allObjects.addAll(diagram.getSelection());
		allObjects.addAll(diagram.getNeighbourhood());

		determineObjectNames(allObjects, labels);

		return diagram;
	}

	private void determineObjectNames(Collection<EObject> elements, Map<EObject, String> labels) {
		for (EObject current : elements) {
			labels.put(current, getLabel(current) + "_" + getIndex(current));
		}
	}

	private String getLabel(EObject current) {
		if(current.eContainingFeature() != null) {
			return current.eContainingFeature().getName();
		} else {
			return "root";
		}
	}

	private String getIndex(EObject current) {
		if (current.eContainer() == null) {
			Resource r = current.eResource();
			ResourceSet rs = r.getResourceSet();
			return rs.getResources().indexOf(r) + "_" + r.getContents().indexOf(current);
		} else {
			EObject container = current.eContainer();
			String baseIndex = getIndex(container);
			return baseIndex + "_" + container.eContents().indexOf(current);
		}
	}

	@Override
	protected void chooseStrategy() {
		strategy = ObjectDiagramStrategies::determineEdgesForSelection;
		if (NeighbourhoodStrategyHandler.getVisPreference()) {
			strategy = strategy.andThen(ObjectDiagramStrategies::expandNeighbourhoodBidirectional);
		}
	}
}
