/**
 * 
 */
package org.moflon.core.ui.visualisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
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
		Pair<Collection<EClass>, Collection<EReference>> p = determineClassesAndRefsToVisualise(elements);
		return EMoflonPlantUMLGenerator.visualiseEcoreElements(p.getLeft(), handleEOpposites(p.getRight()));
	}

	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	// TODO: REFACTOR

	private Collection<EReference> handleEOpposites(Collection<EReference> refs) {
		Collection<EReference> refsWithOnlyOneEOpposite = new ArrayList<>();
		for (EReference eReference : refs) {
			if (eReference.getEOpposite() == null || !refsWithOnlyOneEOpposite.contains(eReference.getEOpposite()))
				refsWithOnlyOneEOpposite.add(eReference);
		}

		return refsWithOnlyOneEOpposite;
	}

	private Pair<Collection<EClass>, Collection<EReference>> determineClassesAndRefsToVisualise(
			Collection<EObject> selection) {
		// TODO: Fix selection bug where only EClasses will be visualised, but not
		// eclasses of operations / attributes and other lower-level EModelElements.
		// Instead, if the selection contains such an element, it should be replaced by
		// its defining EClass, if this EClass is not already present in the selection.
		// TODO: Fix bug where EPackages cannot be visualised. Instead, they should
		// represent their content, i.e. all EClass and elements contained in EClasses
		// (see above) should be visualised.
		Collection<EClass> chosenClasses = selection.stream()//
				.filter(EClass.class::isInstance)//
				.map(EClass.class::cast)//
				.collect(Collectors.toSet());//

		return Pair.of(chosenClasses, determineReferencesToVisualize(chosenClasses));
	}

	private Collection<EReference> determineReferencesToVisualize(Collection<EClass> chosenClasses) {
		Collection<EReference> refs = chosenClasses.stream()//
				.flatMap(c -> c.getEReferences().stream())//
				.collect(Collectors.toSet());//
		return refs;
	}
}
