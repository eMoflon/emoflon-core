package org.emoflon.smartemf.runtime.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.emoflon.smartemf.runtime.notification.SmartEMFNotification;

public class SmartEList<T> extends SmartCollection<T, LinkedList<T>> {

	public SmartEList(EObject eContainer, EReference feature) {
		super(eContainer, feature);
	}

	@Override
	protected void initializeCollection(EObject eContainer, EReference feature) {
		elements = new LinkedList<>();
	}

	@Override
	public void move(int newPosition, T object) {
		move(newPosition, elements.indexOf(object));
	}

	@Override
	public T move(int newPosition, int oldPosition) {
		T t = elements.remove(oldPosition);
		elements.add(newPosition, t);
		sendNotification(SmartEMFNotification.createMoveNotification(eContainer, feature, t, newPosition, newPosition));
		return t;
	}

	@Override
	public T get(int index) {
		return elements.get(index);
	}

	@Override
	public T set(int index, T element) {
		T t = elements.set(index, element);
		if (!feature.isContainment()) {
			sendNotification(SmartEMFNotification.createRemoveNotification(eContainer, feature, t, index));
		}
		sendNotification(SmartEMFNotification.createAddNotification(eContainer, feature, element, index));
		return t;
	}

	@Override
	public void add(int index, T element) {
//		elements.add(index, element);
//		sendNotification(SmartEMFNotification.createAddNotification(eContainer, feature, element, index));
		add(element);
	}

	@Override
	public T remove(int index) {
		T t = elements.remove(index);
//		sendNotification(SmartEMFNotification.createRemoveNotification(eContainer, feature, t, index));
//		return t;
		remove(t);
		return t;
	}

	@Override
	public int indexOf(Object o) {
		return elements.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return elements.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return elements.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return null;
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return elements.subList(fromIndex, toIndex);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		for (T t : c) {
			elements.add(index, t);
		}
		sendNotification(SmartEMFNotification.createAddManyNotification(eContainer, feature, c, index));
		return true;
	}

	@Override
	public T basicGet(int index) {
		return get(index);
	}

	@Override
	public List<T> basicList() {
		return this;
	}

	@Override
	public Iterator<T> basicIterator() {
		return iterator();
	}

	@Override
	public ListIterator<T> basicListIterator() {
		return listIterator();
	}

	@Override
	public ListIterator<T> basicListIterator(int index) {
		return listIterator(index);
	}

	@Override
	public Object[] basicToArray() {
		return toArray();
	}

	@Override
	public <T> T[] basicToArray(T[] array) {
		return toArray(array);
	}

	@Override
	public int basicIndexOf(Object object) {
		return indexOf(object);
	}

	@Override
	public int basicLastIndexOf(Object object) {
		return lastIndexOf(object);
	}

	@Override
	public boolean basicContains(Object object) {
		return contains(object);
	}

	@Override
	public boolean basicContainsAll(Collection<?> collection) {
		return containsAll(collection);
	}

	@Override
	public NotificationChain basicRemove(Object object, NotificationChain notifications) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NotificationChain basicAdd(T object, NotificationChain notifications) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addUnique(T object) {
		add(object);
	}

	@Override
	public void addUnique(int index, T object) {
		add(index, object);
	}

	@Override
	public boolean addAllUnique(Collection<? extends T> collection) {
		return addAll(collection);
	}

	@Override
	public boolean addAllUnique(int index, Collection<? extends T> collection) {
		return addAll(index, collection);
	}

	@Override
	public T setUnique(int index, T object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ReplacingIterator<T> replacingIterator() {
		return new ReplacingIterator<T>(this) {

			@Override
			public void replace(T element) {
				if (iteratorIndex <= 0)
					throw new NoSuchElementException("There is no last element to replace! Please call method next(), first!");
				elements.set(iteratorIndex - 1, element);
			}

		};
	}

}
