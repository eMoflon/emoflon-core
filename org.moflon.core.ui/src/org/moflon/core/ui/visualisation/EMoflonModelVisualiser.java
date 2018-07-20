/**
 * 
 */
package org.moflon.core.ui.visualisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.EContentsEList.FeatureIterator;
import org.moflon.core.ui.VisualiserUtilities;

/**
 * Visualises UML Object Diagrams for Ecore models.
 *
 */
public class EMoflonModelVisualiser extends EMoflonEcoreVisualiser {

	@Override
	public boolean supportsSelection(List<EObject> selection) {
		// An Ecore model must contain EObjects only, which are not EModelElements. If
		// it contains other
		// elements, the selection is not supported by this visualiser.
		return !VisualiserUtilities.hasMetamodelElements(selection);
	}

	@Override
	protected String getDiagramBody(List<EObject> elements) {
		List<EObject> objects = determineObjectsToVisualise(elements);
		List<VisualEdge> links = handleEOppositesForLinks(determineLinksToVisualise(objects));
		return EMoflonPlantUMLGenerator.visualiseModelElements(objects, links);
	}

	private List<EObject> determineObjectsToVisualise(List<EObject> selection) {
		return selection.stream()//
				.filter(EObject.class::isInstance)//
				.map(EObject.class::cast)//
				.collect(Collectors.toList());//
	}

	@SuppressWarnings("rawtypes")
	private List<VisualEdge> determineLinksToVisualise(List<EObject> chosenObjects) {
		HashSet<VisualEdge> links = new HashSet<>();
		for (EObject o : chosenObjects) {
			for (EContentsEList.FeatureIterator featureIterator = //
					(EContentsEList.FeatureIterator) o.eCrossReferences().iterator(); featureIterator.hasNext();) {
				addVisualEdge(featureIterator, chosenObjects, links, o);
			}
			for (EContentsEList.FeatureIterator featureIterator = //
					(EContentsEList.FeatureIterator) o.eContents().iterator(); featureIterator.hasNext();) {
				addVisualEdge(featureIterator, chosenObjects, links, o);
			}
		}

		return new ArrayList<>(links);
	}

	@SuppressWarnings("rawtypes")
	private void addVisualEdge(FeatureIterator featureIterator, Collection<EObject> chosenObjects,
			Collection<VisualEdge> refs, EObject src) {
		EObject trg = (EObject) featureIterator.next();
		EReference eReference = (EReference) featureIterator.feature();
		if (chosenObjects.contains(trg))
			refs.add(new VisualEdge(eReference, src, trg));
	}

	private List<VisualEdge> handleEOppositesForLinks(List<VisualEdge> links) {
		List<VisualEdge> linksWithOnlyOneEOpposite = new ArrayList<>();
		for (VisualEdge link : links) {
			if (!link.hasEOpposite() || !linksWithOnlyOneEOpposite.contains(link.findEOpposite(links).orElse(null)))
				linksWithOnlyOneEOpposite.add(link);
		}

		return linksWithOnlyOneEOpposite;
	}
}
