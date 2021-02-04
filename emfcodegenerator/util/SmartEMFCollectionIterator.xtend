package emfcodegenerator.util

import java.util.Iterator

class SmartEMFCollectionIterator<E> implements Iterator<E> {

	var Iterator<E> base_itr

	new(Iterator<E> base_itr){
		this.base_itr = base_itr
	}

	override hasNext() {
		this.base_itr.hasNext
	}
	
	override next() {
		return this.base_itr.next()
	}

	override remove(){
		//remove via Iterator should not be used as the internal Iterator of collections does not
		//reset containment fields
		//thus overridden here and exception thrown
		throw new UnsupportedOperationException("Iterator.remove() is not supported")
	}
}
