package org.moflon.core.ui.visualisation.metamodels;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EClass;
import org.moflon.core.ui.visualisation.diagrams.EdgeType;
import org.moflon.core.ui.visualisation.diagrams.VisualEdge;

/**
 * Contains various methods for manipulating {@link ClassDiagram} instances.
 * 
 * @author Johannes Brandt
 *
 */
public class ClassDiagramStrategies {

	/**
	 * Computes all edges between selected classes in the given class diagram.
	 * 
	 * This method will add each computed edge to the edges stored with the diagram.
	 * Only edges between selected classes will be computed and added.
	 * 
	 * @param diagram
	 *            The class diagram containing the selection, for which the edges
	 *            are to be computed.
	 * @return The resulting class diagram.
	 */
	public static ClassDiagram determineEdgesForSelection(ClassDiagram diagram) {
		Collection<EClass> selection = diagram.getSelection();
		Collection<VisualEdge> edges = diagram.getEdges();

		determineOutboundEdgesBetween(selection, selection, edges);

		return diagram;
	}

	/**
	 * Expands the given {@link ClassDiagram}'s neighbourhood by one degree,
	 * bidirectional.
	 * 
	 * The given diagram's neighbourhood is expanded, by adding all neighbors of the
	 * current neighbourhood. The direction of the associations between classes is
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
	public static ClassDiagram expandNeighbourhoodBidirectional(ClassDiagram diagram) {
		Collection<EClass> selection = diagram.getSelection();
		Collection<EClass> neighbourhood = diagram.getNeighbourhood();
		Collection<EClass> others = diagram.getSuperset().stream()//
				.filter(cls -> !selection.contains(cls))//
				.filter(cls -> !neighbourhood.contains(cls))//
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
				.flatMap(edge -> Stream.of((EClass) edge.getSrc(), (EClass) edge.getTrg()))//
				.filter(cls -> !selection.contains(cls))//
				.filter(cls -> !neighbourhood.contains(cls))//
				.forEach(neighbourhood::add);

		return diagram;
	}

	/**
	 * Determines all outbound edges from classes in <code>sourceElements</code> to
	 * classes in <code>targetElements</code>.
	 * 
	 * @param sourceElements
	 *            The set of classes for which all outbound edges shall be
	 *            determined.
	 * @param targetElements
	 *            The set of classes which represent targets of all outbound edges
	 *            from the set of source elements.
	 * @param edges
	 *            All edges with a class from <code>sourceElements</code> as source,
	 *            and a class from <code>targetElements</code> as target.
	 */
	private static void determineOutboundEdgesBetween(Collection<EClass> sourceElements,
			Collection<EClass> targetElements, Collection<VisualEdge> edges) {
		// search references
		sourceElements.stream()//
				.flatMap(cls -> cls.getEReferences().stream())//
				.filter(ref -> targetElements.contains(ref.getEReferenceType()))//
				.map(ref -> new VisualEdge(ref, EdgeType.REFERENCE, ref.getEContainingClass(), ref.getEReferenceType()))//
				.forEach(edges::add);//

		// search generalisations
		for (EClass c : sourceElements) {
			for (EClass s : c.getESuperTypes()) {
				if (targetElements.contains(s)) {
					edges.add(new VisualEdge(null, EdgeType.GENERALISATION, c, s));
				}
			}
		}
	}
}
