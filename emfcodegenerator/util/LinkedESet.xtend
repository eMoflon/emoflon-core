package emfcodegenerator.util

import org.eclipse.emf.common.util.EList
import java.util.Collection
import java.util.ArrayList

class LinkedESet<E> extends ArrayList<E> implements EList<E>{

	override move(int newPosition, E object) {
		if(newPosition>0 && newPosition<super.size){
			if(!super.contains(object)) throw new IllegalArgumentException("Object not in LinkedESet")
			super.remove(object)
			this.add(newPosition, object)
		}
		throw new IllegalArgumentException("out of bounds")
	}

	override move(int newPosition, int oldPosition) {
		if((newPosition<super.size) && (newPosition>0) && (oldPosition<super.size) && (oldPosition>0)){
			var E buffer = super.get(oldPosition)
			super.remove(oldPosition)
			this.add(newPosition, buffer)
			return buffer
		}
		throw new IllegalArgumentException("out of bounds")
	}

	//override adding and check for uniqueness
	override add(int index, E element){
		if(super.contains(element)) return;
		super.add(index, element)
	}

	override add(E e){
		if(super.contains(e)) return false
		return super.add(e)
	}

	override addAll(int index, Collection<? extends E> c){
		for(E e : c) if(super.contains(e)) c.remove(e)
		return super.addAll(index, c)
	}
	
	override addAll(Collection<? extends E> c){
		for(E e : c) if(super.contains(e)) c.remove(e)
		return super.addAll(c)
	}

}