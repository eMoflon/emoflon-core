package org.moflon.smartemf.util.collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class LinkedSmartESet<T> extends LinkedSmartEList<T> {

	public LinkedSmartESet(EObject eContainer, EReference feature) {
		super(eContainer, feature);
	}

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
