/**
 * 
 */
package org.moflon.core.ui.visualisation.metamodels;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.moflon.core.ui.visualisation.diagrams.Diagram;

/**
 * Represents a class diagram.
 * 
 * The nodes of this diagram type are {@link EClass} instances.
 * 
 * @author Johannes Brandt
 *
 */
public class ClassDiagram extends Diagram<EClass> {
	private boolean showDocumentation = false;

	/**
	 * Stores the documentation elements an the EClasses they are to be attached to.
	 */
	protected Map<EAnnotation, Optional<EClass>> docToEClasses;

	public ClassDiagram(Collection<EClass> superset) {
		super(superset);

		docToEClasses = new HashMap<>();
	}

	public ClassDiagram(Collection<EClass> superset, Collection<EClass> selection) {
		super(superset, selection);

		docToEClasses = new HashMap<>();
	}

	/**
	 * Getter for {@link #docToEClasses}.
	 * 
	 * @return A map of documentation elements to their respective EClass they are
	 *         attached to.
	 */
	public Map<EAnnotation, Optional<EClass>> getDoumentation() {
		return docToEClasses;
	}

	/**
	 * Setter for {@link #docToEClasses}.
	 * 
	 * @param docToEClasses The new mapping of documentation elements to EClasses.
	 */
	public void setDocumentation(Map<EAnnotation, Optional<EClass>> docToEClasses) {
		this.docToEClasses = docToEClasses;
	}

	public void setShowDocumentation(boolean value) {
		showDocumentation = value;
	}
	
	public boolean getShowDocumentation() {
		return showDocumentation;
	}
}
