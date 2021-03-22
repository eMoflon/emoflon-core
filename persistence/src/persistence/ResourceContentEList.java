package persistence;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.util.InternalEList;

import emfcodegenerator.util.collections.DefaultEList;

@SuppressWarnings("serial")
public class ResourceContentEList<E> extends DefaultEList<E> implements InternalEList<E> {

	@Override
	public E basicGet(int index) {
		return get(index);
	}

	@Override
	public List<E> basicList() {
		return Collections.unmodifiableList(this);
	}

	@Override
	public Iterator<E> basicIterator() {
		return iterator();
	}

	@Override
	public ListIterator<E> basicListIterator() {
		return listIterator();
	}

	@Override
	public ListIterator<E> basicListIterator(int index) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NotificationChain basicAdd(E object, NotificationChain notifications) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addUnique(E object) {
		add(object);
	}

	@Override
	public void addUnique(int index, E object) {
		add(index, object);
	}

	@Override
	public boolean addAllUnique(Collection<? extends E> collection) {
		return addAll(collection);
	}

	@Override
	public boolean addAllUnique(int index, Collection<? extends E> collection) {
		return addAll(index, collection);
	}

	@Override
	public E setUnique(int index, E object) {
		return set(index, object);
	}

}
