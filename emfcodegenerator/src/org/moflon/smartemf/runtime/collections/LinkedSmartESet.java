package org.moflon.smartemf.runtime.collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.moflon.smartemf.runtime.SmartObject;

public class LinkedSmartESet<T extends SmartObject> extends SmartEList<T> {

	public LinkedSmartESet(EObject eContainer, EReference feature) {
		super(eContainer, feature);
	}

	// implements a duplicate-free list
	@Override
	protected boolean addWithoutNotification(T e) {
		if(!elements.contains(e))
			return super.addWithoutNotification(e);
		return false;
	}
	
	@Override
	public T set(int index, T element) {
		if(!elements.get(index).equals(element))
			return super.set(index, element);
		return null;
	}

	@Override
	public void add(int index, T element) {
		if(!elements.get(index).equals(element))
			super.add(index, element);
	}

}
