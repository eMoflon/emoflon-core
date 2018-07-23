package org.moflon.core.ui.visualisation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
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
		List<VisualEdge> refs = handleOpposites(determineReferencesToVisualise(classes));
		return EMoflonPlantUMLGenerator.visualiseEcoreElements(classes, refs);
	}

	private List<EClass> determineClassesToVisualise(List<EObject> selection) {
		ArrayList<EClass> result = new ArrayList<>(selection.size());

		HashSet<EClass> cache = new HashSet<>();

		// retrieve classes, and enclosing classes of operations, attributes...
		// TODO: resolve EDataType as well?
		for (EObject eobject : selection) {
			EClass eclass = null;

			if (eobject instanceof EClass) {
				eclass = (EClass) eobject;
			} else if (eobject instanceof EStructuralFeature) {
				// EReference and EAttribute
				EStructuralFeature efeature = (EStructuralFeature) eobject;
				eclass = efeature.getEContainingClass();
			} else if (eobject instanceof EOperation) {
				EOperation eoperation = (EOperation) eobject;
				eclass = eoperation.getEContainingClass();
			} else if (eobject instanceof EParameter) {
				EParameter eparameter = (EParameter) eobject;
				eclass = eparameter.getEOperation().getEContainingClass();
			} else if (eobject instanceof EGenericType) {
				EGenericType etype = (EGenericType) eobject;
				eclass = etype.getEClassifier() instanceof EClass ? (EClass) etype.getEClassifier() : null;
			}

			if (eclass != null && cache.add(eclass)) {
				result.add(eclass);
			}
		}

		// expand EPackages, retrieve classes and add them to result
		selection.stream()//
				.filter(EPackage.class::isInstance)//
				.map(EPackage.class::cast)//
				.flatMap(epackage -> Stream.concat(Stream.of(epackage), epackage.getESubpackages().stream()))//
				.flatMap(epackage -> epackage.getEClassifiers().stream())//
				.filter(EClass.class::isInstance)//
				.map(EClass.class::cast)//
				.filter(cache::add)//
				.forEach(result::add);

		return result;
	}

	private List<VisualEdge> determineReferencesToVisualise(List<EClass> chosenClasses) {
		HashSet<EClass> cache = new HashSet<>(chosenClasses);

		// Gather all references in between selected classes and between selected classes and non-selectéd classes.
		List<VisualEdge> result = chosenClasses.stream()//
				.flatMap(c -> c.getEReferences().stream())//
				.filter(ref -> cache.contains(ref.getEContainingClass()) || cache.contains(ref.getEReferenceType()))
				.map(ref -> new VisualEdge(ref, EdgeType.REFERENCE, ref.getEContainingClass(), ref.getEReferenceType()))//
				.collect(Collectors.toList());//
		// TODO: Add support for unidirectional refs with source not in and target in
		// list of chosen classes.
		
		// Gather all generalisation references.
		chosenClasses.stream()//
			.filter(c -> !c.getESuperTypes().isEmpty())//
			.forEach(c -> c.getESuperTypes().forEach(s -> result.add(new VisualEdge(null, EdgeType.GENERALISATION, c, s))));
		// TODO: Add support for base class outside of selection and super class inside of selection.

		return result;
	}
}
