/**
 * 
 */
package org.moflon.core.ui.visualisation;

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;

/**
 * Represents a class diagram.
 * 
 * The nodes of this diagram type are {@link EClass} instances.
 * 
 * @author Johannes Brandt
 *
 */
public class ClassDiagram extends Diagram<EClass> {

	public ClassDiagram(Collection<EClass> superset) {
		super(superset);
	}

	public ClassDiagram(Collection<EClass> superset, Collection<EClass> selection) {
		super(superset, selection);
	}

}
