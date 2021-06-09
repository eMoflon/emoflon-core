package emfcodegenerator.util.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.util.EList;

public class DefaultSmartEList<T> extends HashSet<T> implements EList<T>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 188149705186560304L;

	@Override
	public void move(int newPosition, T object) {
		throw new UnsupportedOperationException("Not supported within DefaultSmartEList");
	}

	@Override
	public T move(int newPosition, int oldPosition) {
		throw new UnsupportedOperationException("Not supported within DefaultSmartEList");
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException("Not supported within DefaultSmartEList");
	}

	@Override
	public T get(int index) {
		throw new UnsupportedOperationException("Not supported within DefaultSmartEList");
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException("Not supported within DefaultSmartEList");
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException("Not supported within DefaultSmartEList");
	}

	@Override
	public T remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object o) {
		throw new UnsupportedOperationException("Not supported within DefaultSmartEList");
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException("Not supported within DefaultSmartEList");
	}

	@Override
	public ListIterator<T> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException("Not supported within DefaultSmartEList");
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("Not supported within DefaultSmartEList");
	}

}
