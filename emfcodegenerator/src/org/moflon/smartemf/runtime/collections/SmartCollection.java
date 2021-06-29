package org.moflon.smartemf.runtime.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.InternalEList;
import org.moflon.smartemf.runtime.SmartObject;
import org.moflon.smartemf.runtime.notification.NotifyStatus;
import org.moflon.smartemf.runtime.notification.SmartEMFNotification;

public abstract class SmartCollection<T, L extends Collection<T>> implements EList<T>, InternalEList<T> {

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
		while (it.hasNext()) {
			T elt = it.next();
			if (counter == index) {
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

	public NotifyStatus addInternal(T e, boolean addToEOpposite) {
		boolean success = elements.add(e);
		if (!success)
			return NotifyStatus.FAILURE_NO_NOTIFICATION;

		NotifyStatus resultStatus;
		if (feature.isContainment()) {
			resultStatus = ((SmartObject) e).setContainment(eContainer, feature);
		} else
			resultStatus = NotifyStatus.SUCCESS_NO_NOTIFICATION;

		if (addToEOpposite) {
			((SmartObject) e).eInverseAdd(eContainer, feature.getEOpposite());
		}
		return resultStatus;
	}

	@Override
	public boolean add(T e) {
		NotifyStatus status = addInternal(e, true);
		if (status == NotifyStatus.SUCCESS_NO_NOTIFICATION) {
			sendNotification(SmartEMFNotification.createAddNotification(eContainer, feature, e, 0));
		}
		return true;
	}

	@Override
	public void add(int index, T element) {
		add(element);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		NotifyStatus status = NotifyStatus.FAILURE_NO_NOTIFICATION;
		Collection<T> newList = new LinkedList<>();
		for (T t : c) {
			NotifyStatus addStatus = addInternal(t, true);
			if (addStatus != NotifyStatus.FAILURE_NO_NOTIFICATION)
				status = addStatus;
			newList.add(t);
		}

		// if feature is containment then set resource should have sent this notification already
		if (!feature.isContainment())
			sendNotification(SmartEMFNotification.createAddManyNotification(eContainer, feature, newList, 0));

		return status != NotifyStatus.FAILURE_NO_NOTIFICATION;
	}

	public NotifyStatus removeInternal(Object o, boolean removeFromEOpposite, boolean sendRemoveNotification) {
		boolean success = elements.remove(o);
		if (!success)
			return NotifyStatus.FAILURE_NO_NOTIFICATION;

		if (sendRemoveNotification) {
			sendNotification(SmartEMFNotification.createRemoveNotification(eContainer, feature, o, -1));
		}

		NotifyStatus status = NotifyStatus.SUCCESS_NO_NOTIFICATION;
		if (feature.isContainment()) {
			status = ((SmartObject) o).resetContainment();
		}
		if (removeFromEOpposite) {
			((SmartObject) o).eInverseRemove(eContainer, feature.getEOpposite());
		}
		return status;
	}

	@Override
	public boolean remove(Object o) {
		NotifyStatus status = removeInternal(o, true, false);
		if (status == NotifyStatus.SUCCESS_NO_NOTIFICATION)
			sendNotification(SmartEMFNotification.createRemoveNotification(eContainer, feature, o, -1));
		return status != NotifyStatus.FAILURE_NO_NOTIFICATION;
	}

	@Override
	public T remove(int index) {
		if (index >= elements.size())
			throw new IndexOutOfBoundsException(index);
		Iterator<T> it = elements.iterator();
		int counter = 0;
		while (it.hasNext()) {
			T elt = it.next();
			if (counter == index) {
				if (remove(elt))
					return elt;
				return null;
			}
		}
		throw new RuntimeException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Collection<Object> newList = new LinkedList<>();
		NotifyStatus status = NotifyStatus.FAILURE_NO_NOTIFICATION;
		for (Object t : c) {
			NotifyStatus removeStatus = removeInternal(t, true, false);
			if (removeStatus != NotifyStatus.FAILURE_NO_NOTIFICATION) {
				newList.add(t);
				status = removeStatus;
			}
		}
		if (status == NotifyStatus.SUCCESS_NO_NOTIFICATION)
			sendNotification(SmartEMFNotification.createRemoveManyNotification(eContainer, feature, newList, -1));
		return !newList.isEmpty();
	}

	@Override
	public boolean removeIf(Predicate<? super T> filter) {
		Collection<T> removed = new LinkedList<>();
		for (T e : elements) {
			if (filter.test(e)) {
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
		if (r != null) {
			for (Adapter a : r.eAdapters()) {
				a.notifyChanged(n);
			}
		}
	}

	public abstract ReplacingIterator<T> replacingIterator();

}
