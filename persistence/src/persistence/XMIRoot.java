package persistence;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * This represents the root element of an XMI file. It may either be a top-level object
 * or an auxiliary node containing multiple top-level objects.
 * @author paulschiffner
 */
public interface XMIRoot {
	List<EObject> contents();
	
	default EObject rootObject() {
		return contents().iterator().next().eContainer();
	}
}
