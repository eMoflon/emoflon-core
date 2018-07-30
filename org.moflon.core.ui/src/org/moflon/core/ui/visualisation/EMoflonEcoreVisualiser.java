package org.moflon.core.ui.visualisation;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.moflon.core.ui.VisualiserUtilities;
import org.moflon.core.ui.visualisation.strategy.DiagramStrategy;

/**
 * Abstract implementation for the visualisation of Ecore metamodels and models.
 * 
 * @author Johannes Brandt (initial contribution)
 *
 */
public abstract class EMoflonEcoreVisualiser<T> extends EMoflonVisualiser implements ConfigurableVisualiser<T> {

	/**
	 * Stores whether or not the superset of Ecore elements can be retrieved from
	 * currently associated editor.
	 */
	private boolean isEmptySelectionSupported = false;

	/**
	 * Stores a subset of Ecore elements, that are to be visualised..
	 */
	private Collection<EObject> latestSelection;

	/**
	 * Resembles the superset of all elements that could potentially be visualised.
	 * Typically determined by all the elements currently handled by the associated
	 * editor.
	 */
	private Collection<EObject> allElements;

	/**
	 * Diagram style bits - used in PlantUML diagram text generation.
	 */
	protected int style = EMoflonPlantUMLGenerator.SHOW_MODEL_DETAILS;

	/**
	 * Allows chained operations on a diagram with node type {@link Code T}. Should
	 * at least be {@link UnaryOperator#identity()}.
	 */
	protected DiagramStrategy<T> strategy;

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
		allElements = VisualiserUtilities.extractEcoreElements(editor);
		latestSelection = allElements;
		isEmptySelectionSupported = allElements != null;

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
		Collection<EObject> ecoreSelection = VisualiserUtilities.extractEcoreSelection(selection);
		if (ecoreSelection == null || ecoreSelection.isEmpty()) {
			return false;
		}
		latestSelection = ecoreSelection;
		// in case no elements can be extracted from the editor, allElements is set to
		// the selection
		if (!isEmptySelectionSupported) {
			allElements = ecoreSelection;
		}

		boolean isSupported = supportsSelection(latestSelection);
		return isSupported;
	}

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
		String result = EMoflonPlantUMLGenerator.emptyDiagram();
		if (latestSelection == null || latestSelection.isEmpty()) {
			return result;
		}
		if (VisualiserUtilities.hasMetamodelElements(latestSelection)
				&& VisualiserUtilities.hasModelElements(latestSelection)) {
			return result;
		}

		synchronized (this) {
			result = getDiagramBody(latestSelection);
		}
		return result;
	}

	@Override
	public synchronized void setDiagramStyle(int style) {
		this.style = style;
	}

	@Override
	public synchronized void setDiagramStrategy(DiagramStrategy<T> strategy) {
		if (strategy == null) {
			throw new IllegalArgumentException("Strategy cannot be null!");
		}
		this.strategy = strategy;
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
	protected abstract boolean supportsSelection(Collection<EObject> selection);

	/**
	 * Calculates the diagram text for the given Ecore elements.
	 * 
	 * @param elements
	 *            The Ecore elements that are to be visualised.
	 * @return The generated diagram text describing the given elements using the
	 *         PlantUML DSL.
	 */
	protected abstract String getDiagramBody(Collection<EObject> elements);

	/**
	 * Getter for {@link #allElements}.
	 * 
	 * @return All elements that can potentially be visualised.
	 */
	protected Collection<EObject> getAllElements() {
		return allElements;
	}

	/**
	 * For a given list of {@link VisualEdge} instances, return only one
	 * {@link VisualEdge} for every bidirectional association.
	 * 
	 * For example, if two classes cl1 and cl2 do share a bidirectional association
	 * cl1 <-> cl2, only one {@link VisualEdge} instance will be returned,
	 * describing only one navigation direction, e.g. cl1 <- cl2. In short, for
	 * every bidirectional association, the opposing edge will not be returned. No
	 * guarantees can be made which direction of a bidirectional association will be
	 * returned. If there is a unidirectional {@link VisualEdge}, it will be
	 * returned.
	 * 
	 * @param edges
	 *            The list of edges, each one representing one navigation direction
	 *            of an association, which may contain the opposing navigation
	 *            direction of a bidirectional association.
	 * @return The list of edges without any opposing navigation direction.
	 */
	protected Collection<VisualEdge> handleOpposites(Collection<VisualEdge> edges) {
		HashSet<VisualEdge> edgesWithoutEOpposite = new HashSet<>();
		for (VisualEdge edge : edges) {
			if (!edge.hasEOpposite() || !edgesWithoutEOpposite.contains(edge.findEOpposite(edges).orElse(null)))
				edgesWithoutEOpposite.add(edge);
		}

		return edgesWithoutEOpposite;
	}
}
