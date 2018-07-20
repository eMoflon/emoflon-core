/**
 * 
 */
package org.moflon.core.ui.visualisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

/**
 * Visualises UML Class Diagrams for Ecore metamodels.
 *
 */
public class EMoflonMetamodelVisualiser extends EMoflonEcoreVisualiser {

	@Override
	public boolean supportsSelection(List<EObject> superset, List<EObject> selection) {
		// If no element is selected, the whole editor content is to be displayed,
		// therefore it must be checked for the correct Ecore elements.
		List<EObject> ecoreSelection = selection.isEmpty() ? superset : selection;

		// An Ecore metamodel must contain EModelElements only. If it contains other
		// elements, the selection is not supported by this visualiser.
		return !ecoreSelection.stream()//
				.filter(e -> !(e instanceof EModelElement))//
				.filter(e -> !(e instanceof EGenericType))//
				.findAny()//
				.isPresent();
	}

	@Override
	public String getDiagramBody(IEditorPart editor, ISelection selection) {
		// TODO: refactor
		return maybeVisualiseMetamodel(editor)//
				.orElse(EMoflonPlantUMLGenerator.emptyDiagram());//
	}

	@Override
	protected String getDiagramBody(List<EObject> elements) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	// TODO: REFACTOR
	private Optional<String> maybeVisualiseMetamodel(IEditorPart editor) {
		return extractMetamodelElementsFromEditor(editor)
				.map(p -> EMoflonPlantUMLGenerator.visualiseEcoreElements(p.getLeft(), handleEOpposites(p.getRight())));
	}

	private Collection<EReference> handleEOpposites(Collection<EReference> refs) {
		Collection<EReference> refsWithOnlyOneEOpposite = new ArrayList<>();
		for (EReference eReference : refs) {
			if (eReference.getEOpposite() == null || !refsWithOnlyOneEOpposite.contains(eReference.getEOpposite()))
				refsWithOnlyOneEOpposite.add(eReference);
		}

		return refsWithOnlyOneEOpposite;
	}

	private Optional<Pair<Collection<EClass>, Collection<EReference>>> extractMetamodelElementsFromEditor(
			IEditorPart editor) {
		return Optional.of(editor)//
				.flatMap(this::multiSelectionInEcoreEditor)//
				.map(this::determineClassesAndRefsToVisualise)//
				.map(p -> p.getLeft().isEmpty() ? null : p);//
	}

	@SuppressWarnings("rawtypes")
	private Optional<List> multiSelectionInEcoreEditor(IEditorPart editor) {
		return Optional.of(editor.getSite().getSelectionProvider())//
				.flatMap(maybeCast(ISelectionProvider.class))//
				.map(ISelectionProvider::getSelection)//
				.flatMap(maybeCast(IStructuredSelection.class))//
				.map(IStructuredSelection::toList);//
	}

	private Pair<Collection<EClass>, Collection<EReference>> determineClassesAndRefsToVisualise(
			Collection<Object> selection) {
		Collection<EClass> chosenClassesfromResource = new ArrayList<EClass>();
		if (selection.size() == 1 && !resourceChosen(selection).isEmpty()) {
			TreeIterator<EObject> eAllContents = resourceChosen(selection).get(0).getAllContents();
			while (eAllContents.hasNext()) {
				EObject next = eAllContents.next();
				if (next instanceof EClass) {
					chosenClassesfromResource.add((EClass) next);
				}
			}

			return Pair.of(chosenClassesfromResource, determineReferencesToVisualize(chosenClassesfromResource));
		}

		else {
			Collection<EClass> chosenClasses = selection.stream()//
					.filter(EClass.class::isInstance)//
					.map(EClass.class::cast)//
					.collect(Collectors.toSet());//

			return Pair.of(chosenClasses, determineReferencesToVisualize(chosenClasses));
		}
	}

	private List<Resource> resourceChosen(Collection<Object> selection) {
		List<Resource> resourceChosen = selection.stream()//
				.filter(Resource.class::isInstance)//
				.map(Resource.class::cast)//
				.collect(Collectors.toList());//
		return resourceChosen;

	}

	private Collection<EReference> determineReferencesToVisualize(Collection<EClass> chosenClasses) {
		Collection<EReference> refs = chosenClasses.stream()//
				.flatMap(c -> c.getEReferences().stream())//
				.collect(Collectors.toSet());//
		return refs;
	}
}
