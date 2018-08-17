/**
 * 
 */
package org.moflon.core.ui.visualisation.diagrams;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a generic diagram.
 * 
 * Stores nodes and the edges connecting them. Edges are represented by
 * {@link VisualEdge} instances.
 * 
 * @author Johannes Brandt
 *
 * @param <T> The node type.
 */
public class Diagram<T> {
	/**
	 * Stores all elements that may be visualised.
	 */
	final protected Collection<T> superset;

	/**
	 * Stores all selected elements of the diagram. Should be distinct from
	 * {@link Diagram#neighbourhood}.
	 */
	protected Collection<T> selection;

	/**
	 * Stores the neighbourhood of {@link Diagram#selection}. Should be distinct
	 * from {@link Diagram#selection}.
	 */
	protected Collection<T> neighbourhood;

	/**
	 * Stores the edges that are to be visualised.
	 */
	protected Collection<VisualEdge> edges;

	private boolean abbreviateLabels = false;

	private boolean showFullModelDetails = false;

	/**
	 * Parameterized constructor.
	 * 
	 * @param superset All elements of the diagram. Cannot be changed once
	 *                 initialized.
	 */
	public Diagram(Collection<T> superset) {
		this.superset = superset;
		this.selection = new HashSet<>();
		this.neighbourhood = new HashSet<>();
		this.edges = new HashSet<>();
	}

	/**
	 * Parameterized constructor.
	 * 
	 * @param superset  All elements of the diagram. Cannot be changed once
	 *                  initialized.
	 * @param selection The selected elements of the diagram.
	 */
	public Diagram(Collection<T> superset, Collection<T> selection) {
		this.superset = superset;
		this.selection = selection;
		this.neighbourhood = new HashSet<>();
		this.edges = new HashSet<>();
	}

	/**
	 * Getter for {@link Diagram#superset}.
	 * 
	 * @return All nodes of the diagram.
	 */
	public Collection<T> getSuperset() {
		return superset;
	}

	/**
	 * Getter for {@link Diagram#selection}.
	 * 
	 * @return The selected nodes of the diagram.
	 */
	public Collection<T> getSelection() {
		return selection;
	}

	/**
	 * Setter for {@link Diagram#selection}.
	 * 
	 * @param selection The new selection of nodes.
	 */
	public void setSelection(Collection<T> selection) {
		this.selection = selection;
	}

	/**
	 * Getter for {@link Diagram#neighbourhood}.
	 * 
	 * @return All nodes that are considered to be in the neighbourhood.
	 */
	public Collection<T> getNeighbourhood() {
		return neighbourhood;
	}

	/**
	 * Setter for {@link Diagram#neighbourhood}.
	 * 
	 * @param neighbourhood The new neighbourhood.
	 */
	public void setNeighbourhood(Collection<T> neighbourhood) {
		this.neighbourhood = neighbourhood;
	}

	/**
	 * Getter for {@link #edges}.
	 * 
	 * @return The edges of this diagram.
	 */
	public Collection<VisualEdge> getEdges() {
		return edges;
	}

	/**
	 * Setter for {@link #edges}.
	 * 
	 * @param edges The new edges for this diagram.
	 */
	public void setEdges(Collection<VisualEdge> edges) {
		this.edges = edges;
	}

	public void setAbbreviateLabels(boolean value) {
		abbreviateLabels = value;
	}

	public boolean getAbbreviateLabels() {
		return abbreviateLabels;
	}

	public boolean getShowFullModelDetails() {
		return showFullModelDetails ;
	}
	
	public void setShowFullModelDetails(boolean value) {
		showFullModelDetails = value;
	}
}
