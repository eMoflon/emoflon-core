package org.emoflon.smartemf.runtime.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class SmartESet<T> extends SmartCollection<T, LinkedHashSet<T>> {

	public SmartESet(EObject eContainer, EReference feature, boolean sendNotifications) {
		super(eContainer, feature, sendNotifications);		
	}
	
	public SmartESet(EObject eContainer, EReference feature) {
		super(eContainer, feature);
	}

	@Override
	protected void initializeCollection(EObject eContainer, EReference feature) {
		elements = new LinkedHashSet<T>();
	}

	@Override
	public void move(int newPosition, T object) {
		throw new UnsupportedOperationException("Not supported for Sets");
	}

	@Override
	public T move(int newPosition, int oldPosition) {
		throw new UnsupportedOperationException("Not supported for Sets");
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException("Not supported for Sets");
	}

	@Override
	public int indexOf(Object o) {
		throw new UnsupportedOperationException("Not supported for Sets");
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException("Not supported for Sets");
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException("Not supported for Sets");
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException("Not supported for Sets");
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("Not supported for Sets");
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException("Not supported for Sets");
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
				elements.remove(copiedElements[iteratorIndex - 1]);
				// since this SmartCollection behaves like a set, we do not have to insert the new element at the
				// same position as the removed one
				elements.add(element);
			}
		};
	}

}
