/**
 * 
 */
package org.moflon.core.ui.visualisation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.moflon.core.ui.VisualiserUtilities;

/**
 * Visualises UML Class Diagrams for Ecore metamodels.
 *
 */
public class EMoflonMetamodelVisualiser extends EMoflonEcoreVisualiser {

	@Override
	public boolean supportsSelection(List<EObject> selection) {
		// An Ecore metamodel must contain EModelElements only. If it contains other
		// elements, the selection is not supported by this visualiser.
		return !VisualiserUtilities.hasModelElements(selection);
	}

	@Override
	protected String getDiagramBody(List<EObject> elements) {
		List<EClass> classes = determineClassesToVisualise(elements);
		List<EReference> refs = handleEOpposites(determineReferencesToVisualise(classes));
		return EMoflonPlantUMLGenerator.visualiseEcoreElements(classes, refs);
	}

	private List<EClass> determineClassesToVisualise(List<EObject> selection) {
		// TODO: Fix selection bug where only EClasses will be visualised, but not
		// eclasses of operations / attributes and other lower-level EModelElements.
		// Instead, if the selection contains such an element, it should be replaced by
		// its defining EClass, if this EClass is not already present in the selection.
		// TODO: Fix bug where EPackages cannot be visualised. Instead, they should
		// represent their content, i.e. all EClass and elements contained in EClasses
		// (see above) should be visualised.
		return selection.stream()//
				.filter(EClass.class::isInstance)//
				.map(EClass.class::cast)//
				.collect(Collectors.toList());//
	}

	private List<EReference> determineReferencesToVisualise(List<EClass> chosenClasses) {
		return chosenClasses.stream()//
				.flatMap(c -> c.getEReferences().stream())//
				.collect(Collectors.toList());//
	}

	private List<EReference> handleEOpposites(List<EReference> refs) {
		List<EReference> refsWithOnlyOneEOpposite = new ArrayList<>();
		for (EReference eReference : refs) {
			if (eReference.getEOpposite() == null || !refsWithOnlyOneEOpposite.contains(eReference.getEOpposite()))
				refsWithOnlyOneEOpposite.add(eReference);
		}

		return refsWithOnlyOneEOpposite;
	}
}
