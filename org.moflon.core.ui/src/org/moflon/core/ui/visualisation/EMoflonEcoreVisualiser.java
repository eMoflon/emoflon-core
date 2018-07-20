/**
 * 
 */
package org.moflon.core.ui.visualisation;

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
	 * Stores the superset of Ecore elements, that are to be visualised.
	 */
	private List<EObject> latestEditorContents;

	/**
	 * Stores a subset of Ecore elements, that are to be visualised.
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
		latestEditorContents = VisualiserUtilities.extractEcoreElements(editor);
		isEmptySelectionSupported = latestEditorContents != null;

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
		latestSelection = VisualiserUtilities.extractEcoreSelection(selection);
		if (latestSelection.isEmpty() && !isEmptySelectionSupported) {
			return false;
		}

		boolean isSupported = supportsSelection(latestEditorContents, latestSelection);
		return isSupported;
	}

	/**
	 * Checks whether or not a given Ecore selection, with respect to a superset of
	 * Ecore elements, is supposed to be supported by this Ecore visualiser.
	 * 
	 * @param superset
	 *            All Ecore elements that could be visualised. Contains the given
	 *            selection as a subset.
	 * @param selection
	 *            All Ecore elements that supposed to be visualised.
	 * @return <code>true</code> if the given selection is supported, otherwise
	 *         <code>false</code>.
	 */
	protected abstract boolean supportsSelection(List<EObject> superset, List<EObject> selection);

	@Override
	public String getDiagramBody(IEditorPart editor, ISelection selection) {
		if (latestSelection.isEmpty() && latestEditorContents.isEmpty()) {
			// If both editor and selection are empty, an empty diagram is returned.
			return EMoflonPlantUMLGenerator.emptyDiagram();
		} else if (latestSelection.isEmpty()) {
			// Instead of an empty selection the whole editor content is visualised.
			return getDiagramBody(latestEditorContents);
		} else {
			return getDiagramBody(latestSelection);
		}
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

}
