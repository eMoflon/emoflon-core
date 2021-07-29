package org.moflon.smartemf.persistence;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class PendingEMFCrossReference {
	final private EObject node;
	final private EReference reference;
	final private EObject[] crossRefs;
	private int insertedObjects = 0;
	
	public PendingEMFCrossReference(final EObject node, final EReference reference, int numOfRefs) {
		this.node = node;
		this.reference = reference;
		crossRefs = new EObject[numOfRefs];
	}
	
	public void insertObject(final EObject ref, int idx) {
		crossRefs[idx] = ref;
		insertedObjects++;
	}
	
	public boolean isCompleted() {
		return insertedObjects == crossRefs.length;
	}
	
	@SuppressWarnings("unchecked")
	public void writeBack() {
		if(reference.isMany()) {
			List<EObject> refs = (List<EObject>) node.eGet(reference);
			refs.addAll(Arrays.asList(crossRefs));
		} else {
			node.eSet(reference, crossRefs[0]);
		}
	}
}
