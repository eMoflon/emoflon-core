package org.moflon.smartemf.runtime.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.moflon.smartemf.runtime.notification.SmartEMFNotification;

public abstract class SmartCollection<T, L extends Collection<T>> implements EList<T>{

	protected final EObject eContainer;
	protected final EReference feature;
	protected L elements;
	
	public SmartCollection(EObject eContainer, EReference feature) {
		this.eContainer = eContainer;
		this.feature = feature;
		initializeCollection(eContainer, feature);
	}
	
	protected abstract void initializeCollection(EObject eContainer, EReference feature);


	@Override
	public int size() {
		return elements.size();
	}


	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}


	@Override
	public boolean contains(Object o) {
		return elements.contains(o);
	}


	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}


	@Override
	public Object[] toArray() {
		return elements.toArray();
	}


	@Override
	public <T> T[] toArray(T[] a) {
		return elements.toArray(a);
	}
	
	protected boolean addWithoutNotification(T e) {
		return elements.add(e);
	}

	@Override
	public boolean add(T e) {
		boolean success = addWithoutNotification(e);
		if(success) {
			sendNotification(SmartEMFNotification.createAddNotification(eContainer, feature, e, 0));
		}
		return success;
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean success = false;
		Collection<T> newList = new LinkedList<>();
		for(T t : c) {
			success = success || addWithoutNotification(t);
			newList.add(t);
		}
		sendNotification(SmartEMFNotification.createAddManyNotification(eContainer, feature, newList, 0));
		return success;
	}

	protected boolean removeWithoutNotification(Object o) {
		return elements.remove(o);
	}


	@Override
	public boolean remove(Object o) {
		boolean success = removeWithoutNotification(o);
		sendNotification(SmartEMFNotification.createRemoveNotification(eContainer, feature, o, -1));
		return success;
	}


	@Override
	public boolean removeAll(Collection<?> c) {
		Collection<Object> newList = new LinkedList<>();
		for(Object t : c) {
			if(removeWithoutNotification(t)) {
				newList.add(t);
			}
		}
		sendNotification(SmartEMFNotification.createRemoveManyNotification(eContainer, feature, newList, -1));
		return !newList.isEmpty();
	}

	@Override
	public void clear() {
		Collection<Object> newList = new LinkedList<>();
		newList.addAll(elements);
		elements.clear();
		sendNotification(SmartEMFNotification.createRemoveManyNotification(eContainer, feature, newList, -1));
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return elements.containsAll(c);
	}


	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("retainAll is not supported by SmartCollections");
	}

	protected void sendNotification(Notification n) {
		Resource r = eContainer.eResource();
		if(r != null) {
			for(Adapter a : r.eAdapters()) {
				a.notifyChanged(n);
			}
		}
	}
	
	
}
