package org.moflon.core.ui.visualisation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.moflon.core.ui.VisualiserUtilities;

/**
 * Abstract implementation for the visualisation of Ecore metamodels and models.
 * 
 * @author Johannes Brandt (initial contribution)
 *
 */
public abstract class EMoflonEcoreVisualiser extends EMoflonVisualiser {

	/**
	 * Stores whether or not the superset of Ecore elements can be retrieved from
	 * currently associated editor.
	 */
	private boolean isEmptySelectionSupported = false;

	/**
	 * Stores a subset of Ecore elements, that are to be visualised..
	 */
	private List<EObject> latestSelection;

	@Override
	public boolean supportsEditor(IEditorPart editor) {
		// check if editor currently has Ecore related model loaded
		boolean hasEcoreFileLoaded = VisualiserUtilities.checkFileExtensionSupport(editor, "ecore")
				|| VisualiserUtilities.checkFileExtensionSupport(editor, "xmi");
		// || VisualiserUtilities.checkFileExtensionSupport(editor, "genmodel");

		// Check if the editor internally handles Ecore EObjects.
		// Since some editors allow to load both .ecore and .xmi Resources at the same
		// time, it is not possible to check for specific elements from Ecore metamodels
		// or models. This has to be done in #supportsSelection(...), when it is clear
		// whether the selection is empty or not.
		latestSelection = VisualiserUtilities.extractEcoreElements(editor);
		isEmptySelectionSupported = latestSelection != null;

		// if only one of the above conditions is true, there is still a possibility
		// that a given selection might be supported
		return hasEcoreFileLoaded || isEmptySelectionSupported;
	}

	@Override
	public boolean supportsSelection(ISelection selection) {
		// only Ecore selections are supported
		if (!VisualiserUtilities.isEcoreSelection(selection)) {
			return false;
		}

		// empty Ecore selections are supported only if the editor can provide Ecore
		// elements, this is checked and remembered in supportsEditor(...)
		List<EObject> ecoreSelection = VisualiserUtilities.extractEcoreSelection(selection);
		if (ecoreSelection == null || ecoreSelection.isEmpty()) {
			return false;
		}
		latestSelection = ecoreSelection;

		boolean isSupported = supportsSelection(latestSelection);
		return isSupported;
	}

	/**
	 * Checks whether or not a given Ecore selection is supposed to be supported by
	 * this Ecore visualiser.
	 * 
	 * @param selection
	 *            All Ecore elements that are supposed to be visualised.
	 * @return <code>true</code> if the given selection is supported, otherwise
	 *         <code>false</code>.
	 */
	protected abstract boolean supportsSelection(List<EObject> selection);

	@Override
	public String getDiagramBody(IEditorPart editor, ISelection selection) {
		// In order to save processing time latestSelection already contains the
		// best fit for an ecore selection and does not have to be recalculated from
		// editor and selection. It is determined during the calls of
		// supportsEdidtor(...) and supportsSelection(...).
		// Note that if a specific selection is null, empty, or simply not supported,
		// latestSelection is supposed to contain the whole editor output.
		// This in turn can be again null, empty or not supported, because the check for
		// editor support is quite tolerant. This is why this has to be checked here
		// again.
		if (latestSelection == null || latestSelection.isEmpty()) {
			return EMoflonPlantUMLGenerator.emptyDiagram();
		}
		if (VisualiserUtilities.hasMetamodelElements(latestSelection)
				&& VisualiserUtilities.hasModelElements(latestSelection)) {
			return EMoflonPlantUMLGenerator.emptyDiagram();
		}
		return getDiagramBody(latestSelection);
	}

	/**
	 * Calculates the diagram text for the given Ecore elements.
	 * 
	 * @param elements
	 *            The Ecore elements that are to be visualised.
	 * @return The generated diagram text describing the given elements using the
	 *         PlantUML DSL.
	 */
	protected abstract String getDiagramBody(List<EObject> elements);

	/**
	 * For a given list of {@link VisualEdge} instances, return only one
	 * {@link VisualEdge} for every bidirectional association.
	 * 
	 * For example, if two classes cl1 and cl2 do share a bidirectional association
	 * cl1 <-> cl2, only one {@link VisualEdge} instance will be returned,
	 * describing only one navigation direction, e.g. cl1 <- cl2. In short, for
	 * every bidirectional association, the opposing edge will not be returned. No
	 * guarantees can be made which direction of a bidirectional association will be
	 * returned. If there is a unidirectional {@link VisualEdge}, it will be returned.
	 * 
	 * @param edges
	 *            The list of edges, each one representing one navigation direction
	 *            of an association, which may contain the opposing navigation
	 *            direction of a bidirectional association.
	 * @return The list of edges without any opposing navigation direction.
	 */
	protected List<VisualEdge> handleOpposites(List<VisualEdge> edges) {
		List<VisualEdge> edgesWithoutEOpposite = new ArrayList<>();
		for (VisualEdge edge : edges) {
			if (!edge.hasEOpposite() || !edgesWithoutEOpposite.contains(edge.findEOpposite(edges).orElse(null)))
				edgesWithoutEOpposite.add(edge);
		}

		return edgesWithoutEOpposite;
	}
}
