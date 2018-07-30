package org.moflon.core.ui.visualisation;

import java.util.Collection;
import java.util.HashSet;
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
import org.moflon.core.ui.visualisation.Configurator.StrategyPart;
import org.moflon.core.ui.visualisation.strategy.ClassDiagramStrategies;
import org.moflon.core.ui.visualisation.strategy.DiagramStrategy;

/**
 * Visualises UML Class Diagrams for Ecore metamodels.
 *
 */
public class EMoflonMetamodelVisualiser extends EMoflonEcoreVisualiser<ClassDiagram> {

	public EMoflonMetamodelVisualiser() {
		super();

		// set default strategy
		strategy = getDefaultStrategy(StrategyPart.INIT)//
				.andThen(getDefaultStrategy(StrategyPart.NEIGHBOURHOOD));

		@SuppressWarnings("unused")
		boolean blabla = true;
	}

	@Override
	public boolean supportsSelection(Collection<EObject> selection) {
		// An Ecore metamodel must contain EModelElements only. If it contains other
		// elements, the selection is not supported by this visualiser.
		return !VisualiserUtilities.hasModelElements(selection);
	}

	@Override
	public boolean supportsDiagramType(Class<?> diagramClass) {
		return ClassDiagram.class == diagramClass;
	}

	@Override
	public DiagramStrategy<ClassDiagram> getDefaultStrategy(StrategyPart part) {
		switch (part) {
		case INIT:
			return ClassDiagramStrategies::determineEdgesForSelection;
		case NEIGHBOURHOOD:
			return DiagramStrategy.identity();
		default:
			return super.getDefaultStrategy(part);
		}
	}

	@Override
	protected String getDiagramBody(Collection<EObject> selection) {
		HashSet<EClass> allClasses = getAllElements().stream()//
				.filter(EClass.class::isInstance)//
				.map(EClass.class::cast)//
				.collect(Collectors.toCollection(HashSet::new));

		// For every selected EModelElement choose an appropriate EClass to represent
		// it.
		Collection<EClass> chosenClasses = resolveSelection(selection);

		// Create diagram and process it using the defined strategy.
		ClassDiagram diagram = strategy.apply(new ClassDiagram(allClasses, chosenClasses));
		diagram.setEdges(handleOpposites(diagram.getEdges()));

		return EMoflonPlantUMLGenerator.visualiseEcoreElements(diagram, style);
	}

	private Collection<EClass> resolveSelection(Collection<EObject> selection) {
		HashSet<EClass> result = new HashSet<>(selection.size());

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
}
