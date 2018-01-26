package org.moflon.core.ui.visualisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.presentation.EcoreEditor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

public class EMoflonModelAndMetamodelVisualiser extends EMoflonVisualiser {
	private IEditorPart editor;

	@Override
	protected String getDiagramBody(IEditorPart editor, ISelection selection) {
		return maybeVisualiseMetamodel(editor).orElse(
			   maybeVisualiseModel(editor).orElse(
			   EMoflonPlantUMLGenerator.emptyDiagram()));
	}

	@SuppressWarnings("rawtypes")
	private Optional<List> multiSelectionInEcoreEditor(IEditorPart editor){
		return Optional.of(editor)
				.flatMap(maybeCast(EcoreEditor.class))
				.map(EcoreEditor::getSelection)
				.flatMap(maybeCast(IStructuredSelection.class))
				.map(IStructuredSelection::toList);
	}
	
	private Optional<String> maybeVisualiseMetamodel(IEditorPart editor) {
		return extractMetamodelElementsFromEditor(editor)
				.map(p -> EMoflonPlantUMLGenerator.visualiseEcoreElements(p.getLeft(), p.getRight()));
	}
	
	private Optional<String> maybeVisualiseModel(IEditorPart editor) {
		return  extractModelElementsFromEditor(editor)
					.map(p -> {
						if(checkForCorrespondenceModel(p.getLeft())) {
							return EMoflonPlantUMLGenerator.visualiseCorrModel(p.getLeft(),sourceTargetObjectsForCorrespondenceModel(p.getLeft(),"source"), sourceTargetObjectsForCorrespondenceModel(p.getLeft(),"target"), determineLinksToVisualizeForCorrModel(p.getLeft()));
						}
						else {
							return EMoflonPlantUMLGenerator.visualiseModelElements(p.getLeft(), p.getRight());
						}
					});
	}
	
	private Optional<Pair<Collection<EObject>, Collection<Pair<String, Pair<EObject, EObject>>>>>
	extractModelElementsFromEditor(IEditorPart editor2) {
		return Optional.of(editor)
				.flatMap(this::multiSelectionInEcoreEditor)
				.map(this::determineObjectsAndLinksToVisualise)
				.map(p -> p.getLeft().isEmpty()? null : p);
	}

	private Optional<Pair<Collection<EClass>, Collection<EReference>>> 
	extractMetamodelElementsFromEditor(IEditorPart editor) {
		return Optional.of(editor)
				.flatMap(this::multiSelectionInEcoreEditor)
				.map(this::determineClassesAndRefsToVisualise)
				.map(p -> p.getLeft().isEmpty()? null : p);
	}

	private Pair<Collection<EClass>, Collection<EReference>> 
	determineClassesAndRefsToVisualise(Collection<Object> selection){
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
			Collection<EClass> chosenClasses = selection.stream()
					.filter(EClass.class::isInstance)
					.map(EClass.class::cast)
					.collect(Collectors.toSet());

			return Pair.of(chosenClasses, determineReferencesToVisualize(chosenClasses));
		}
	}
	
	private Pair<Collection<EObject>, Collection<Pair<String, Pair<EObject, EObject>>>> 
	determineObjectsAndLinksToVisualise(Collection<Object> selection) {
		Collection<EObject> chosenObjectsfromResource = new ArrayList<EObject>();
		if (selection.size() == 1 && !resourceChosen(selection).isEmpty()) {
			TreeIterator<EObject> eAllContents = resourceChosen(selection).get(0).getAllContents();
			while (eAllContents.hasNext()) {
				EObject next = eAllContents.next();
				if (next instanceof EObject)
					chosenObjectsfromResource.add(next);
			}
			
			return Pair.of(chosenObjectsfromResource, determineLinksToVisualize(chosenObjectsfromResource));
		}

		else {
			Collection<EObject> chosenObjects = selection.stream()
					.filter(EObject.class::isInstance)
					.map(EObject.class::cast)
					.collect(Collectors.toSet());

			return Pair.of(chosenObjects, determineLinksToVisualize(chosenObjects));
		}
	}
	
	private Collection<EReference> determineReferencesToVisualize(Collection<EClass> chosenClasses){
		Collection<EReference> refs = chosenClasses
				.stream()
				.flatMap(c -> c.getEReferences().stream())
				.collect(Collectors.toSet());
		return refs;	
	}
	
	@SuppressWarnings("rawtypes")
	private Collection<Pair<String, Pair<EObject, EObject>>> determineLinksToVisualize(Collection<EObject> chosenObjects) {
		Collection<Pair<String, Pair<EObject, EObject>>> refs = new HashSet<>();
		for (EObject o : new ArrayList<EObject>(chosenObjects)) {
			for (EContentsEList.FeatureIterator featureIterator = (EContentsEList.FeatureIterator) o.eCrossReferences().iterator(); featureIterator.hasNext();) {
				EObject eObject = (EObject) featureIterator.next();
				EReference eReference = (EReference) featureIterator.feature();
				if(chosenObjects.contains(eObject))
					refs.add(Pair.of(eReference.getName(), Pair.of(o, eObject)));
			}
			for (EContentsEList.FeatureIterator featureIterator = (EContentsEList.FeatureIterator) o.eContents().iterator(); featureIterator.hasNext();) {
				EObject eObject = (EObject) featureIterator.next();
				EReference eReference = (EReference) featureIterator.feature();
				if(chosenObjects.contains(eObject))
					refs.add(Pair.of(eReference.getName(), Pair.of(o, eObject)));
			}
		}
		return refs;
	}
	private List<Resource> resourceChosen(Collection<Object> selection){
		List<Resource> resourceChosen = selection
				.stream()
				.filter(Resource.class::isInstance)
				.map(Resource.class::cast)
				.collect(Collectors.toList());
		return resourceChosen;
		
	}
	
	private boolean checkForCorrespondenceModel(Collection<EObject> chosenObjectsfromResource) {
		// We do not expect any links to be present
		if(!determineLinksToVisualize(chosenObjectsfromResource).isEmpty())
			return false;
		
		// We expect all objects to be of the form <--src--corr--trg-->
		Iterator<EObject> eAllContents = chosenObjectsfromResource.iterator();
		while (eAllContents.hasNext()) {
			EObject next = eAllContents.next();
			if(!objectIsACorrespondenceLink(next)) 
				return false;
		}
		
		return true;
	}
	
	private boolean objectIsACorrespondenceLink(EObject next) {
		return next.eClass().getEStructuralFeature("source") != null &&
			   next.eClass().getEStructuralFeature("target") != null;
	}
	
	private Collection<EObject> sourceTargetObjectsForCorrespondenceModel(Collection<EObject> chosenObjectsfromResource, String sourceOrTarget){
		Collection<EObject> sourceOrTargetObjects = new ArrayList<EObject>();
		Iterator<EObject> eAllContents = chosenObjectsfromResource.iterator();
		while (eAllContents.hasNext()) {
			EObject next = eAllContents.next();
			sourceOrTargetObjects.add( (EObject) next.eGet(next.eClass().getEStructuralFeature(sourceOrTarget)));
		}
		
		return sourceOrTargetObjects;
	}
	

	private Collection<Pair<String, Pair<EObject, EObject>>> determineLinksToVisualizeForCorrModel(Collection<EObject> chosenObjectsfromResource){
		Collection<EObject> correspondenceObjects = new ArrayList<EObject>();
		correspondenceObjects.addAll(sourceTargetObjectsForCorrespondenceModel(chosenObjectsfromResource,"source"));
		correspondenceObjects.addAll(sourceTargetObjectsForCorrespondenceModel(chosenObjectsfromResource,"target"));
		return determineLinksToVisualize(correspondenceObjects);
	}
	
	@Override
	public boolean supportsEditor(IEditorPart editor) {
		this.editor = editor;
		return extractMetamodelElementsFromEditor(editor).isPresent() || 
				extractModelElementsFromEditor(editor).isPresent();
	}
	
	@Override
	public boolean supportsSelection(ISelection selection) {
		return true;
	}
}
