/**
 * 
 */
package org.moflon.core.ui.visualisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.EContentsEList.FeatureIterator;

/**
 * Visualises UML Object Diagrams for Ecore models.
 *
 */
public class EMoflonModelVisualiser extends EMoflonEcoreVisualiser {

	@Override
	public boolean supportsSelection(List<EObject> superset, List<EObject> selection) {
		// If no element is selected, the whole editor content is to be displayed,
		// therefore it must be checked for the correct Ecore elements.
		List<EObject> ecoreSelection = selection.isEmpty() ? superset : selection;

		// An Ecore model must contain EObjects only, which are not EModelElements. If
		// it contains other
		// elements, the selection is not supported by this visualiser.
		return !ecoreSelection.stream()//
				.filter(e -> e instanceof EModelElement || e instanceof EGenericType)//
				.findAny()//
				.isPresent();
	}

	@Override
	protected String getDiagramBody(List<EObject> elements) {
		Pair<Collection<EObject>, Collection<VisualEdge>> p = determineObjectsAndLinksToVisualise(elements);
		return EMoflonPlantUMLGenerator.visualiseModelElements(p.getLeft(), p.getRight());
	}

	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	// TODO: REFACTOR

	private Pair<Collection<EObject>, Collection<VisualEdge>> determineObjectsAndLinksToVisualise(
			Collection<EObject> selection) {

		Collection<EObject> chosenObjects = selection.stream()//
				.filter(EObject.class::isInstance)//
				.map(EObject.class::cast)//
				.collect(Collectors.toSet());//

		return Pair.of(chosenObjects, determineLinksToVisualize(chosenObjects));

	}

	@SuppressWarnings("rawtypes")
	private Collection<VisualEdge> determineLinksToVisualize(Collection<EObject> chosenObjects) {
		Collection<VisualEdge> links = new HashSet<>();
		for (EObject o : new ArrayList<EObject>(chosenObjects)) {
			for (EContentsEList.FeatureIterator featureIterator = //
					(EContentsEList.FeatureIterator) o.eCrossReferences().iterator(); featureIterator.hasNext();) {
				addVisualEdge(featureIterator, chosenObjects, links, o);
			}
			for (EContentsEList.FeatureIterator featureIterator = //
					(EContentsEList.FeatureIterator) o.eContents().iterator(); featureIterator.hasNext();) {
				addVisualEdge(featureIterator, chosenObjects, links, o);
			}
		}

		return handleEOppositesForLinks(links);
	}

	@SuppressWarnings("rawtypes")
	private void addVisualEdge(FeatureIterator featureIterator, Collection<EObject> chosenObjects,
			Collection<VisualEdge> refs, EObject src) {
		EObject trg = (EObject) featureIterator.next();
		EReference eReference = (EReference) featureIterator.feature();
		if (chosenObjects.contains(trg))
			refs.add(new VisualEdge(eReference, src, trg));
	}

	private Collection<VisualEdge> handleEOppositesForLinks(Collection<VisualEdge> links) {
		Collection<VisualEdge> linksWithOnlyOneEOpposite = new ArrayList<>();
		for (VisualEdge link : links) {
			if (!link.hasEOpposite() || !linksWithOnlyOneEOpposite.contains(link.findEOpposite(links).orElse(null)))
				linksWithOnlyOneEOpposite.add(link);
		}

		return linksWithOnlyOneEOpposite;
	}
}
