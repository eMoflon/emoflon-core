package emfcodegenerator.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;

public class AdapterList implements EList<Adapter> {

	private Collection<Adapter> adapters = new LinkedList<>();
	
	@Override
	public int size() {
		return adapters.size();
	}

	@Override
	public boolean isEmpty() {
		return adapters.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return adapters.contains(o);
	}

	@Override
	public Iterator<Adapter> iterator() {
		return adapters.iterator();
	}

	@Override
	public Object[] toArray() {
		return adapters.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return adapters.toArray(a);
	}

	@Override
	public boolean add(Adapter e) {
		if(!adapters.contains(e)) 
			return adapters.add(e);
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return adapters.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return adapters.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Adapter> c) {
		boolean changed = false;
		for(Adapter a : c) {
			changed = changed || add(a);
		}
		return changed;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Adapter> c) {
		boolean changed = false;
		for(Adapter a : c) {
			changed = changed || add(a);
		}
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for(Object a : c) {
			changed = changed || remove(a);
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		adapters.clear();
	}

	@Override
	public Adapter get(int index) {
		return null;
	}

	@Override
	public Adapter set(int index, Adapter element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(int index, Adapter element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Adapter remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<Adapter> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<Adapter> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Adapter> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void move(int newPosition, Adapter object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Adapter move(int newPosition, int oldPosition) {
		throw new UnsupportedOperationException();
	}

}
