package org.moflon.core.ui.visualisation.metamodels;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.moflon.core.ui.VisualiserUtilities;
import org.moflon.core.ui.handler.visualisation.AbbreviateLabelsHandler;
import org.moflon.core.ui.handler.visualisation.NeighbourhoodStrategyHandler;
import org.moflon.core.ui.handler.visualisation.ShowDocumentationHandler;
import org.moflon.core.ui.handler.visualisation.ShowModelDetailsHandler;
import org.moflon.core.ui.visualisation.EMoflonPlantUMLGenerator;
import org.moflon.core.ui.visualisation.common.EMoflonEcoreVisualiser;

/**
 * Visualises UML Class Diagrams for Ecore metamodels.
 *
 */
public class EMoflonMetamodelVisualiser extends EMoflonEcoreVisualiser<ClassDiagram> {

	private static final String DOCUMENTATION_KEY = "documentation";

	@Override
	public boolean supportsSelection(Collection<EObject> selection) {
		// An Ecore metamodel must contain EModelElements only. If it contains other
		// elements, the selection is not supported by this visualiser.
		return !VisualiserUtilities.hasModelElements(selection);
	}

	@Override
	protected String getDiagramBody(Collection<EObject> selection) {
		HashSet<EClass> allClasses = getAllElements().stream()//
				.filter(EClass.class::isInstance)//
				.map(EClass.class::cast)//
				.collect(Collectors.toCollection(HashSet::new));

		// For every selected EModelElement choose an appropriate EClass
		Collection<EClass> chosenClasses = resolveSelection(selection);

		// Create diagram and process it using the defined strategy.
		ClassDiagram diagram = strategy.apply(new ClassDiagram(allClasses, chosenClasses));
		diagram.setEdges(handleOpposites(diagram.getEdges()));
		diagram.setAbbreviateLabels(AbbreviateLabelsHandler.getVisPreference());
		diagram.setShowDocumentation(ShowDocumentationHandler.getVisPreference());
		diagram.setShowFullModelDetails(ShowModelDetailsHandler.getVisPreference());

		// extract and resolve documentation for selected AND neighboured EClasses
		HashSet<EAnnotation> chosenAnnotations = getAllElements().stream()//
				.filter(EAnnotation.class::isInstance)//
				.map(EAnnotation.class::cast)//
				.collect(Collectors.toCollection(HashSet::new));
		diagram.setDocumentation(resolveAnnotations(chosenAnnotations, diagram, selection));

		return EMoflonPlantUMLGenerator.visualiseEcoreElements(diagram);
	}

	protected void chooseStrategy() {
		strategy = ClassDiagramStrategies::determineEdgesForSelection;
		if (NeighbourhoodStrategyHandler.getVisPreference()) {
			strategy = strategy.andThen(ClassDiagramStrategies::expandNeighbourhoodBidirectional);
		}
	}

	private Collection<EClass> resolveSelection(Collection<EObject> selection) {
		HashSet<EClass> result = new HashSet<>(selection.size());

		// retrieve classes, and enclosing classes of operations, attributes...
		for (EObject eobject : selection) {
			EClass eclass = resolveObject(eobject);
			if (eclass != null && !result.contains(eclass)) {
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
				.filter(cls -> !result.contains(cls))//
				.forEach(result::add);

		return result;
	}

	/**
	 * Finds an EClass instance, which represents the specified EObject.
	 * 
	 * @param eobject The object for which an {@link EClass} representation is
	 *                required.
	 * @return The representing {@link EClass}. Typically resolved using the
	 *         containment relation. Is <code>null</code>, iff no representation can
	 *         be found.
	 */
	private EClass resolveObject(EObject eobject) {
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
		} else if (eobject instanceof EAnnotation) {
			EAnnotation eannot = (EAnnotation) eobject;
			if (eannot.getDetails().containsKey(DOCUMENTATION_KEY)) {
				eclass = resolveObject(eannot.getEModelElement());
			}
		}
		return eclass;
	}

	/**
	 * Maps annotation elements to the EClass they are attached to, respectively the
	 * EClass representing the EModelElement the annotation element is attached to.
	 * 
	 * An EAnnotation from the specified {@code allAnnotations} collection is only
	 * considered to be added to the returned mapping, if it contains the
	 * "documentation" key and the corresponding String value.
	 * 
	 * @param allAnnotations    The annotations that are to be mapped, if their
	 *                          representing EClass is contained in either the
	 *                          diagram selection of neighbourhood.
	 * @param diagram           The diagram containing the selected and
	 *                          neighbourhood EClasses.
	 * @param originalSelection If no representing EClass can be found, then the
	 *                          original selection is checked whether or not the
	 *                          annotation even has to be mapped.
	 * @return The mapping of EAnnotations to the EClasses. The EClass may be
	 *         <code>null</code>, if the EAnnotation is attached to an EPackage,
	 *         i.e. for which no EClass representation can be found. This is why
	 *         EAnnotations are not directly mapped to EClasses, but an Optional.
	 */
	private Map<EAnnotation, Optional<EClass>> resolveAnnotations(Collection<EAnnotation> allAnnotations,
			ClassDiagram diagram, Collection<EObject> originalSelection) {
		Map<EAnnotation, Optional<EClass>> result = new HashMap<>(allAnnotations.size());
		Collection<EClass> chosenClasses = diagram.getSelection();
		Collection<EClass> chosenNeighbourhood = diagram.getNeighbourhood();

		for (EAnnotation documenter : allAnnotations) {
			if (documenter.getDetails().get(DOCUMENTATION_KEY) != null) {
				EClass documentee = resolveObject(documenter);
				if (chosenClasses.contains(documentee) || chosenNeighbourhood.contains(documentee)
						|| isContained(originalSelection, documenter)) {
					result.put(documenter, Optional.ofNullable(documentee));
				}
			}
		}

		return result;
	}

	/**
	 * Checks whether or not the specified element is contained in the specified
	 * search space.
	 * 
	 * @param searchSpace All EObjects, that will be checked for containment.
	 * @param element     The element for which the check is performed.
	 * @return Result is <code>true</code>, iff any of the EObjects in the search
	 *         space, or one of their sub elements (eContents relation) is the
	 *         specified element.
	 */
	private boolean isContained(Collection<EObject> searchSpace, EObject element) {
		return searchSpace.stream()//
				.flatMap(obj -> Stream.concat(Stream.of(obj), obj.eContents().stream()))//
				.filter(obj -> obj != null)//
				.filter(obj -> obj == element)//
				.findFirst()//
				.isPresent();
	}
}
