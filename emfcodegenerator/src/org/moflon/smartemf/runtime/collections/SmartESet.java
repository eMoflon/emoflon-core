package org.moflon.smartemf.runtime.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class HashSmartESet<T> extends SmartCollection<T, HashSet<T>> {

	public HashSmartESet(EObject eContainer, EReference feature) {
		super(eContainer, feature);
	}

	@Override
	protected void initializeCollection(EObject eContainer, EReference feature) {
		elements = new HashSet<T>();
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
	public T get(int index) {
		throw new UnsupportedOperationException("Not supported for Sets");
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException("Not supported for Sets");
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException("Not supported for Sets");
	}

	@Override
	public T remove(int index) {
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

}
