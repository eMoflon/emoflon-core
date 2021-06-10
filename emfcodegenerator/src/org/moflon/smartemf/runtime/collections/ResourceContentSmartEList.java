package org.moflon.smartemf.runtime.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.moflon.smartemf.runtime.notification.SmartEMFNotification;

public class DefaultSmartEList<T> extends LinkedList<T> implements EList<T>, InternalEList<T>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 188149705186560304L;

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
	public void move(int newPosition, T object) {
		move(newPosition, indexOf(object));
	}

	@Override
	public T move(int newPosition, int oldPosition) {
		T t = remove(oldPosition);
		add(newPosition, t);
		return t;
	}

}
