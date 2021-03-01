package persistence;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

public interface XMIRoot {
	List<EObject> contents();
	
	default EObject rootObject() {
		return contents().iterator().next().eContainer();
	}
}
