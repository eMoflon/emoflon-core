package org.moflon.core.ui.visualisation.diagrams;

/**
 * Used to distinguish classes of edges.
 * 
 * {@link EdgeType#GENERALISATION} identifies generalisation dependencies in
 * class diagrams, {@link EdgeType#REFERENCE} identifies references in class
 * diagrams, and {@link EdgeType#LINK} is used to identify cross-references or
 * containment-relations in object diagrams.
 * 
 * @author Johannes Brandt (initial contribution)
 *
 */
public enum EdgeType {
	GENERALISATION, REFERENCE, LINK;
}
