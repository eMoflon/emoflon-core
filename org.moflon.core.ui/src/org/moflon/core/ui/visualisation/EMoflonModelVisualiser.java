package org.moflon.core.ui.visualisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
		List<VisualEdge> links = handleOpposites(determineLinksToVisualise(objects));
		return EMoflonPlantUMLGenerator.visualiseModelElements(objects, links);
	}

	@SuppressWarnings("rawtypes")
	private List<EObject> determineObjectsToVisualise(List<EObject> selection) {
		HashSet<EObject> cache = new HashSet<>(selection);
		
		List<EObject> result = new ArrayList<>(selection);
		for (EObject o : selection) {
			// add parent if there is one
			if(o.eContainer() != null && cache.add(o.eContainer())) {
				result.add(o.eContainer());
			}
			
			for (EContentsEList.FeatureIterator featureIterator = //
					(EContentsEList.FeatureIterator) o.eCrossReferences().iterator(); featureIterator.hasNext();) {
				EObject trg = (EObject) featureIterator.next();
				if(cache.add(trg)) {
					result.add(trg);
				}
			}
			for (EContentsEList.FeatureIterator featureIterator = //
					(EContentsEList.FeatureIterator) o.eContents().iterator(); featureIterator.hasNext();) {
				EObject trg = (EObject) featureIterator.next();
				if(cache.add(trg)) {
					result.add(trg);
				}
			}
		}
		
		return result;
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
			refs.add(new VisualEdge(eReference, EdgeType.LINK,  src, trg));
	}
}
