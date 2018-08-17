/**
 * 
 */
package org.moflon.core.ui.visualisation.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.moflon.core.ui.visualisation.diagrams.Diagram;

/**
 * Represents an object diagram.
 * 
 * The nodes of this diagram type are {@link EObject} instances.
 * 
 * @author Johannes Brandt
 *
 */
public class ObjectDiagram extends Diagram<EObject> {

	/**
	 * Provides an instance name for an EObject.
	 */
	protected Map<EObject, String> eObjectsToNames;

	public Map<EObject, String> geteObjectsToNames() {
		return eObjectsToNames;
	}

	public ObjectDiagram(Collection<EObject> superset) {
		super(superset);

		eObjectsToNames = new HashMap<>();
	}

	public ObjectDiagram(Collection<EObject> superset, Collection<EObject> selection) {
		super(superset, selection);

		eObjectsToNames = new HashMap<>();
	}

	/**
	 * Getter for {@link #eObjectsToNames}.
	 * 
	 * @return The mapping of EObjects to their respective instance name.
	 */
	public Map<EObject, String> getInstanceNames() {
		return eObjectsToNames;
	}

	/**
	 * Setter for {@link #eObjectsToNames}.
	 * 
	 * @param eObjectsToNames
	 *            The new mapping of EObjects to their respective instance name.
	 */
	public void setInstanceNames(Map<EObject, String> eObjectsToNames) {
		this.eObjectsToNames = eObjectsToNames;
	}
}
