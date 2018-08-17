package org.moflon.core.ui.visualisation.models;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.moflon.core.ui.visualisation.diagrams.EdgeType;
import org.moflon.core.ui.visualisation.diagrams.VisualEdge;

/**
 * Contains various methods for manipulating {@link ObjectDiagram} instances.
 *
 * @author Johannes Brandt
 *
 */
public class ObjectDiagramStrategies {

	/**
	 * Computes all edges between selected objects in the given object diagram.
	 *
	 * This method will add each computed edge to the edges stored with the diagram.
	 * Only edges between selected objects will be computed and added.
	 *
	 * @param diagram
	 *            The object diagram containing the selection, for which the edges
	 *            are to be computed.
	 * @return The resulting object diagram.
	 */
	public static ObjectDiagram determineEdgesForSelection(ObjectDiagram diagram) {
		Collection<EObject> selection = diagram.getSelection();
		Collection<VisualEdge> edges = diagram.getEdges();

		determineOutboundEdgesBetween(selection, selection, edges);

		return diagram;
	}

	/**
	 * Expands the given {@link ObjectDiagram}'s neighbourhood by one degree,
	 * bidirectional.
	 *
	 * The given diagram's neighbourhood is expanded, by adding all neighbors of the
	 * current neighbourhood. The direction of the associations between objects is
	 * irrelevant. If no neighbourhood is defined with the given diagram, then the
	 * selection's neighbours are added to the neighbourhood.
	 *
	 * <p>
	 * <b>Note:</b> If a neighbourhood expansion of a degree greater than one is
	 * wished, this method can simple be chained.
	 * </p>
	 *
	 * @param diagram
	 *            The diagram, of which the neighbourhood is to be increased by a
	 *            degree of one.
	 * @return The diagram with the increased neighbourhood degree.
	 */
	public static ObjectDiagram expandNeighbourhoodBidirectional(ObjectDiagram diagram) {
		Collection<EObject> selection = diagram.getSelection();
		Collection<EObject> neighbourhood = diagram.getNeighbourhood();
		Collection<EObject> others = diagram.getSuperset().stream()//
				.filter(obj -> !selection.contains(obj))//
				.filter(obj -> !neighbourhood.contains(obj))//
				.collect(Collectors.toCollection(HashSet::new));
		Collection<VisualEdge> edges = diagram.getEdges();

		// find 1-neighbourhood edges
		if (neighbourhood.isEmpty()) {
			determineOutboundEdgesBetween(selection, others, edges);
			determineOutboundEdgesBetween(others, selection, edges);
		} else {
			determineOutboundEdgesBetween(neighbourhood, others, edges);
			determineOutboundEdgesBetween(others, neighbourhood, edges);
		}

		// update neighbourhood
		edges.stream()//
				.flatMap(edge -> Stream.of(edge.getSrc(), edge.getTrg()))//
				.filter(obj -> !selection.contains(obj))//
				.filter(obj -> !neighbourhood.contains(obj))//
				.forEach(neighbourhood::add);

		return diagram;
	}

	/**
	 * Determines all outbound edges from objects in <code>sourceElements</code> to
	 * objects in <code>targetElements</code>.
	 *
	 * @param sourceElements
	 *            The set of objects for which all outbound edges shall be
	 *            determined.
	 * @param targetElements
	 *            The set of objects which represent targets of all outbound edges
	 *            from the set of source elements.
	 * @param edges
	 *            All edges with an object from <code>sourceElements</code> as
	 *            source, and an object from <code>targetElements</code> as target.
	 */
	private static void determineOutboundEdgesBetween(Collection<EObject> sourceElements,
			Collection<EObject> targetElements, Collection<VisualEdge> edges) {
		for (EObject obj : sourceElements) {
			final EList<EObject> eCrossReferences = obj.eCrossReferences();
			final EList<EObject> eContents = obj.eContents();
			for (final EList<EObject> featureList : Arrays.asList(eCrossReferences, eContents)) {
				for (final EContentsEList.FeatureIterator<?> featureIterator = //
						(EContentsEList.FeatureIterator<?>) featureList.iterator(); //
						featureIterator.hasNext();) {
					EObject trg = (EObject) featureIterator.next();
					EReference eReference = (EReference) featureIterator.feature();
					if (targetElements.contains(trg))
						edges.add(new VisualEdge(eReference, EdgeType.LINK, obj, trg));
				}
			}
		}
	}
}
