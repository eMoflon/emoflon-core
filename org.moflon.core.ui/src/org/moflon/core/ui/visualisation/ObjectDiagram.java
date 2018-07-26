/**
 * 
 */
package org.moflon.core.ui.visualisation;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

/**
 * Represents an object diagram.
 * 
 * The nodes of this diagram type are {@link EObject} instances.
 * 
 * @author Johannes Brandt
 *
 */
public class ObjectDiagram extends Diagram<EObject> {

	public ObjectDiagram(Collection<EObject> superset) {
		super(superset);
	}

	public ObjectDiagram(Collection<EObject> superset, Collection<EObject> selection) {
		super(superset, selection);
	}

}
