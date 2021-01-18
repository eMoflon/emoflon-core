package emfcodegenerator.util

import java.util.LinkedList
import org.eclipse.emf.common.util.EList

class LinkedEList<E> extends LinkedList<E> implements EList<E> {
	
	override move(int newPosition, Object object) {
		if(this.contains(object) && (newPosition<super.size) && (newPosition>0)){
			var E buffer = object as E
			super.remove(buffer)
			var LinkedList<E> first_half = super.subList(0, newPosition) as LinkedList<E>
			var LinkedList<E> second_half = super.subList(newPosition, super.size()) as LinkedList<E>
			first_half.addLast(buffer)
			first_half.addAll(second_half)
			super.clear()
			super.addAll(first_half as LinkedEList<E>)
		}
	}
	
	override move(int newPosition, int oldPosition) {
		if((newPosition<super.size) && (newPosition>0) && (oldPosition<super.size) && (oldPosition>0)){
			var E buffer = super.get(oldPosition)
			super.set(oldPosition, super.get(newPosition))
			super.set(newPosition, buffer)
			return buffer
		}
		throw new IllegalArgumentException("out of bounds")
	}
	
}