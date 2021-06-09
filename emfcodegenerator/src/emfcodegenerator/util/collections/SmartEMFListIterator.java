package emfcodegenerator.util.collections;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.google.common.collect.Iterators;

/**
 * This ListIterator supports remove if the backing ListIterator does.
 * In this case, it manages containment relations when removing items.
 * 
 * @author paulschiffner
 */
public class SmartEMFListIterator<E> implements ListIterator<E> {

	private ListIterator<E> backingIterator;
	private MinimalSObjectContainerCollection<E> backingEList;
	private E currentObject;
	
	public SmartEMFListIterator(ListIterator<E> backingIterator, MinimalSObjectContainerCollection<E> backingEList) {
		this.backingIterator = backingIterator;
		this.backingEList = backingEList;
	}
	
	@Override
	public boolean hasNext() {
		return backingIterator.hasNext();
	}

	@Override
	public E next() {
		return currentObject = backingIterator.next();
	}

	@Override
	public boolean hasPrevious() {
		return backingIterator.hasPrevious();
	}

	@Override
	public E previous() {
		return currentObject = backingIterator.previous();
	}

	@Override
	public int nextIndex() {
		return backingIterator.nextIndex();
	}

	@Override
	public int previousIndex() {
		return backingIterator.previousIndex();
	}

	@Override
	public void remove() {
		backingIterator.remove();
		if (backingEList.isContainmentObject()) {
			backingEList.remove_containment_to_passed_object(currentObject);
		}
	}

	@Override
	public void set(E e) {
		throw new UnsupportedOperationException("This ListIterator does not support set");
	}

	@Override
	public void add(E e) {
		throw new UnsupportedOperationException("This ListIterator does not support add");
	}
	
	/**
	 * This is a read-only ListIterator for ELists that are not based on actual lists, such as
	 * HashESet and LinkedESet.
	 * It fakes the ListIterator features by storing previously encountered items on a stack.
	 * 
	 * @author paulschiffner
	 */
	public static class PseudoListIterator<E> implements ListIterator<E> {

		private Iterator<E> backingIterator;
		private LinkedList<E> previousItems;
		
		public PseudoListIterator(Iterator<E> backingIterator) {
			this.backingIterator = backingIterator;
			this.previousItems = new LinkedList<>();
		}
		
		public PseudoListIterator(Iterator<E> backingIterator, int index) {
			this(backingIterator);
			try {
				for (int i = 0; i < index; i++) next();
			} catch (NoSuchElementException e) {
				throw (IndexOutOfBoundsException)new IndexOutOfBoundsException().initCause(e);
			}
		}
		
		@Override
		public boolean hasNext() {
			return backingIterator.hasNext();
		}

		@Override
		public E next() {
			previousItems.push(backingIterator.next());
			return previousItems.peek();
		}

		@Override
		public boolean hasPrevious() {
			return !previousItems.isEmpty();
		}

		@Override
		public E previous() {
			E prev = previousItems.pop();
			backingIterator = Iterators.concat(Iterators.singletonIterator(prev), backingIterator);
			return prev;
		}

		@Override
		public int nextIndex() {
			return previousItems.size();
		}

		@Override
		public int previousIndex() {
			return previousItems.size() - 1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("This ListIterator does not support remove");
		}

		@Override
		public void set(E e) {
			throw new UnsupportedOperationException("This ListIterator does not support set");
		}

		@Override
		public void add(E e) {
			throw new UnsupportedOperationException("This ListIterator does not support add");
		}
		
	}

}
