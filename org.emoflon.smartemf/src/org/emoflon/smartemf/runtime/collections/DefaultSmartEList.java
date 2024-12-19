package org.emoflon.smartemf.runtime.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.EContentsEList.FeatureIterator;
import org.emoflon.smartemf.runtime.SmartObject;
import org.emoflon.smartemf.runtime.notification.SmartEMFNotification;
import org.eclipse.emf.ecore.util.InternalEList;

public final class DefaultSmartEList<T> extends LinkedList<T> implements EList<T>, InternalEList<T> {

	private EStructuralFeature feature = null;

	public DefaultSmartEList() {

	}

	public DefaultSmartEList(EStructuralFeature feature) {
		this.feature = feature;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 188149705186560304L;

	@Override
	public boolean add(T e) {
		return super.add(e);
	}

	@Override
	public void add(int index, T element) {
		super.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return super.addAll(index, c);
	}

	@Override
	public void clear() {
		super.clear();
	}

	@Override
	public T remove(int index) {
		return super.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		return super.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return super.removeAll(c);
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
	public Iterator<T> iterator() {
		return basicIterator();
	}

	@Override
	public Iterator<T> basicIterator() {
		Iterator<T> listIterator = super.iterator();
		Iterator<T> featureIterator = new EContentsEList.FeatureIterator<T>() {

			@Override
			public boolean hasNext() {
				return listIterator.hasNext();
			}

			@Override
			public T next() {
				// TODO Auto-generated method stub
				return listIterator.next();
			}

			@Override
			public EStructuralFeature feature() {
				return feature;
			}
		};
		return featureIterator;
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
