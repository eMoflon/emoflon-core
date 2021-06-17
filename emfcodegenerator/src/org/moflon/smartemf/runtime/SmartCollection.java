package org.moflon.smartemf.runtime;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.InternalEList;
import org.moflon.smartemf.runtime.notification.SmartEMFNotification;

public abstract class SmartCollection<T, L extends Collection<T>> implements EList<T>, InternalEList<T>{

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
	public T get(int index) {
		Iterator<T> it = elements.iterator();
		int counter = 0;
		while(it.hasNext()) {
			T elt = it.next();
			if(counter == index) {
				return elt;
			}
		}
		throw new RuntimeException("No element found");
	}
	
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
	
	public boolean addWithoutNotification(T e, boolean addToEOpposite) {
		boolean success = elements.add(e);
		if(!success)
			return false;
		
		if(feature.isContainment()) {
			((SmartObject) e).setContainment(eContainer, feature);
			((SmartObject) e).setResource(eContainer.eResource());
		}
		if(addToEOpposite) {
			((SmartObject) e).eInverseAdd(eContainer, feature.getEOpposite());
		}
		return true;
	}

	@Override
	public boolean add(T e) {
		boolean success = addWithoutNotification(e, true);
		if(success) {
			sendNotification(SmartEMFNotification.createAddNotification(eContainer, feature, e, 0));
		}
		return success;
	}
	
	@Override
	public void add(int index, T element) {
		add(element);
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean success = false;
		Collection<T> newList = new LinkedList<>();
		for(T t : c) {
			success = success || addWithoutNotification(t, true);
			newList.add(t);
		}
		sendNotification(SmartEMFNotification.createAddManyNotification(eContainer, feature, newList, 0));
		return success;
	}

	public boolean removeWithoutNotification(Object o, boolean removeFromEOpposite) {
		boolean success = elements.remove(o);
		if(!success)
			return false;
		
		if(feature.isContainment()) {
			((SmartObject) o).resetContainment();
		}
		if(removeFromEOpposite) {
			((SmartObject) o).eInverseRemove(eContainer, feature.getEOpposite());
		}
		return true;
	}


	@Override
	public boolean remove(Object o) {
		boolean success = removeWithoutNotification(o, true);
		sendNotification(SmartEMFNotification.createRemoveNotification(eContainer, feature, o, -1));
		return success;
	}

	@Override
	public T remove(int index) {
		if(index >= elements.size())
			throw new IndexOutOfBoundsException(index);
		Iterator<T> it = elements.iterator();
		int counter = 0;
		while(it.hasNext()) {
			T elt = it.next();
			if(counter == index) {
				if(remove(elt))
					return elt;
				return null;
			}
		}
		throw new RuntimeException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Collection<Object> newList = new LinkedList<>();
		for(Object t : c) {
			if(removeWithoutNotification(t, true)) {
				newList.add(t);
			}
		}
		sendNotification(SmartEMFNotification.createRemoveManyNotification(eContainer, feature, newList, -1));
		return !newList.isEmpty();
	}
	
	@Override
	public boolean removeIf(Predicate<? super T> filter) {
		Collection<T> removed = new LinkedList<>();
		for(T e : elements) {
			if(filter.test(e)) {
				removed.add(e);
			}
		}
		return removeAll(removed);
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
		// if the feature is a containment, then notifications are handled when setting the resource
//		if(feature.isContainment())
//			return;
		
		Resource r = eContainer.eResource();
		if(r != null) {
			for(Adapter a : r.eAdapters()) {
				a.notifyChanged(n);
			}
		}
	}
}
