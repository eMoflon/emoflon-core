package emfcodegenerator.util

import java.util.ArrayList
import org.eclipse.emf.common.util.EList

class DefaultEList<E> extends ArrayList<E> implements EList<E> {
	
	override move(int newPosition, E object) {
		if(this.contains(object) && (newPosition<super.size) && (newPosition>0)){
			var E buffer = object as E
			super.remove(buffer)
			var ArrayList<E> first_half = super.subList(0, newPosition) as ArrayList<E>
			var ArrayList<E> second_half = super.subList(newPosition, super.size()) as ArrayList<E>
			first_half.add(buffer)
			first_half.addAll(second_half)
			super.clear()
			super.addAll(first_half as ArrayList<E>)
		}
		throw new IllegalArgumentException("out of bounds or object not contained")
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