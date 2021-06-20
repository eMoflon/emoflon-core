package org.moflon.smartemf.runtime.collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.moflon.smartemf.runtime.SmartObject;
import org.moflon.smartemf.runtime.notification.NotifyStatus;

public class LinkedSmartESet<T> extends SmartEList<T> {

	public LinkedSmartESet(EObject eContainer, EReference feature) {
		super(eContainer, feature);
	}

	// implements a duplicate-free list
	@Override
	public NotifyStatus addInternal(T e, boolean addToEOpposite) {
		if(!elements.contains(e))
			return super.addInternal(e, addToEOpposite);
		return NotifyStatus.FAILURE_NO_NOTIFICATION;
	}
	
	@Override
	public T set(int index, T element) {
		if(!elements.get(index).equals(element))
			return super.set(index, element);
		return null;
	}

	@Override
	public void add(int index, T element) {
		if(!elements.contains(element))
			super.add(index, element);
	}

}
