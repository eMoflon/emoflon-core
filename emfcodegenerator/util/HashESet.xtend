package emfcodegenerator.util

import org.eclipse.emf.common.util.EList
import java.util.HashSet
import java.util.Collection
import java.util.ListIterator

class  HashESet<E> extends HashSet<E> implements EList<E>{
	new(){super()}
	new(int initialCapacity){super(initialCapacity)}
	new(int initialCapacity, float loadFactor){super(initialCapacity, loadFactor)}
	new(Collection<? extends E> c){super(c)}

	override move(int newPosition, E object) {
		//HashSet's are not ordered
		throw new UnsupportedOperationException("HashSet's are not ordered")
	}
	
	override move(int newPosition, int oldPosition) {
		//HashSet's are not ordered
		throw new UnsupportedOperationException("HashSet's are not ordered")
	}
	
	override add(int index, E element) {
		//HashSet's are not ordered
		throw new UnsupportedOperationException("HashSet's are not ordered")
	}
	
	override addAll(int index, Collection<? extends E> c) {
		super.addAll(c)
	}
	
	override get(int index) {
		//HashSet's are not ordered
		throw new UnsupportedOperationException("HashSet's are not ordered")
	}
	
	override indexOf(Object o) {
		//HashSet's are not ordered
		throw new UnsupportedOperationException("HashSet's are not ordered")
	}
	
	override lastIndexOf(Object o) {
		//HashSet's are not ordered
		throw new UnsupportedOperationException("HashSet's are not ordered")
	}
	
	override listIterator() {
		return (super.iterator as ListIterator<E>)
	}
	
	override listIterator(int index) {
		return this.listIterator(index)
	}
	
	override remove(int index) {
		//HashSet's are not ordered
		throw new UnsupportedOperationException("HashSet's are not ordered")
	}
	
	override set(int index, Object element) {
		//HashSet's are not ordered
		throw new UnsupportedOperationException("HashSet's are not ordered")
	}
	
	override subList(int fromIndex, int toIndex) {
		//HashSet's are not ordered
		throw new UnsupportedOperationException("HashSet's are not ordered")
	}
}