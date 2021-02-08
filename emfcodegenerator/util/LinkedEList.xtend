package emfcodegenerator.util

import java.util.LinkedList
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature
import java.util.Collection
import java.util.function.Predicate
import java.util.function.UnaryOperator
import java.util.Collections

class LinkedEList<E> extends LinkedList<E> implements EList<E>, MinimalSObjectContainer {

	/**########################Attributes########################*/

	/**
	 * the container of this class or null. needed when inspecting references which
	 * have the containment flag set to true
	 */
	var EObject the_eContainer = null

	/**
	 * the EStructuralFeature which contains this class as an target or null if Containment is false
	 */
	var EStructuralFeature the_econtaining_feature = null

	/**
	 * stores if this list is used to store a Containment
	 */
	var boolean is_containment_object = false
	
		/**########################Constructors########################*/

	/**
	 * Constructs new LinkedEList and sets the {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} to the given values.
	 * @param eContainer EObject
	 * @param eContaining_feature EStructuralFeature
	 * @author Adrian Zwenger
	 */
	new(EObject eContainer, EStructuralFeature eContaining_feature){
		super()
		this.the_eContainer = eContainer
		this.is_containment_object = true
		this.the_econtaining_feature = eContaining_feature
	}
	
	/**
	 * Constructs new LinkedEList from a given Collection and sets the
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} to the given values.
	 * @param c Collection<? extends E>
	 * @param eContainer EObject
	 * @param eContaining_feature EStructuralFeature
	 * @author Adrian Zwenger
	 */
	new(Collection<? extends E> c, EObject eContainer, EStructuralFeature eContaining_feature){
		super(c)
		this.the_eContainer = eContainer
		this.the_econtaining_feature = eContaining_feature
		this.is_containment_object = true
	}
	
	/**
	 * Constructs new LinkedEList from a given Collection. The
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} are set as null.
	 * @param c Collection<? extends E>
	 * @author Adrian Zwenger
	 */
	new(Collection<? extends E> c){
		super(c)
	}
	
	/**
	 * Constructs new LinkedEList. The
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} are set as null.
	 * @author Adrian Zwenger
	 */
	new(){
		super()
	}

	/**########################MinimalSObjectContainer methods########################*/
	
	override eContainer() {
		return this.the_eContainer
	}
	
	override eContainingFeature() {
		return this.the_econtaining_feature
	}
	
	override reset_containment() {
		this.is_containment_object = false
		this.the_eContainer = null
		this.the_econtaining_feature = null
		for(E object : this){
			try{
				(object as MinimalSObjectContainer).reset_containment()
			} catch (Exception e){
				throw new IllegalArgumentException(
			"The contents of this list do not implement MinimalSObjectContainer. Containments cannot be handled."
				)
			}
		}
	}

	override set_containment(EObject container, EStructuralFeature feature) {
		this.is_containment_object = true
		this.the_eContainer = container
		this.the_econtaining_feature = feature
		for(E object : this){
			try{
				(object as MinimalSObjectContainer).set_containment(container, feature)
			} catch (Exception e){
				throw new IllegalArgumentException(
			"The contents of this list do not implement MinimalSObjectContainer. Containments cannot be handled."
				)
			}
		}
	}

	override is_containment_object(){
		return is_containment_object
	}

	/**
	 * if this list is a containment list, passed objects will have their containment set and returned.
	 * if it is not, the object will be returned unchanged
	 */
	def private E set_containment_to_passed_object(E obj){
		if(is_containment_object){
			try{
				(obj as MinimalSObjectContainer).set_containment(this.the_eContainer, this.the_econtaining_feature)
			} catch (Exception E){
				throw new IllegalArgumentException(
			"The contents of this list do not implement MinimalSObjectContainer. Containments cannot be handled."
				)
			}
		}
		return obj
	}
	
	/**
	 * if this list is a containment list, the containment will be removed from the passed object and it will be returned
	 * if it is not, the object will be returned unchanged
	 */
	def private E remove_containment_to_passed_object(E obj){
		if(is_containment_object){
			try{
				(obj as MinimalSObjectContainer).reset_containment()
			} catch (Exception E){
				throw new IllegalArgumentException(
			"The contents of this list do not implement MinimalSObjectContainer. Containments cannot be handled."
				)
			}
		}
		return obj
	}

	/**########################EList methods which need to be implemented########################*/

	override move(int newPosition, Object object) {
		if(this.contains(object) && (newPosition<super.size) && (newPosition>=0)){
			this.move(newPosition, this.indexOf(object))
		}
	}
	
	override move(int newPosition, int oldPosition) {
		//https://docs.oracle.com/javase/6/docs/api/java/util/Collections.html#rotate%28java.util.List,%20int%29
		if(newPosition<0 || oldPosition<0 || newPosition>=this.size || oldPosition>=this.size)
			throw new IndexOutOfBoundsException()
		var E obj = this.get(oldPosition)
		var int j = oldPosition
		var int k = newPosition
		Collections.rotate(this.subList(j < k ? j : k, (j < k ? k : j) + 1), j < k ? -1 : 1);
		return obj;
	}

	/**########################methods which need to be re-implemented########################*/
	
	override add(int index, E obj){
		//this.set_containment_to_passed_object(obj)
		super.add(index, this.set_containment_to_passed_object(obj))
	}
	
	override add(E obj){
		super.add(this.set_containment_to_passed_object(obj))
	}
	
	override addFirst(E obj){
		super.addFirst(this.set_containment_to_passed_object(obj))
	}

	override addLast(E obj){
		super.addLast(this.set_containment_to_passed_object(obj))
	}

	override addAll(Collection<? extends E> c) {
		var int old = this.size()
		for(E e: c) this.add(this.set_containment_to_passed_object(e))
		return old !== this.size()
	}

	override clear(){
		this.removeAll
	}

	override offer(E obj){
		super.offer(this.set_containment_to_passed_object(obj))
	}

	override offerFirst(E obj){
		super.offer(this.set_containment_to_passed_object(obj))
	}

	override offerLast(E obj){
		super.offerLast(this.set_containment_to_passed_object(obj))
	}
	
	override peekFirst(){
		return this.remove_containment_to_passed_object(super.peekFirst())
	}
	
	override peekLast(){
		return this.remove_containment_to_passed_object(super.peekLast())
	}

	override peek(){
		return this.remove_containment_to_passed_object(super.peek())
	}
	
	override pollFirst(){
		return this.remove_containment_to_passed_object(super.pollFirst())
	}
	
	override pollLast(){
		return this.remove_containment_to_passed_object(super.pollLast())
	}

	override poll(){
		return this.remove_containment_to_passed_object(super.poll())
	}
	
	override pop(){
		return this.remove_containment_to_passed_object(super.pop())
	}
	
	override push(E obj){
		super.push(this.set_containment_to_passed_object(obj))
	}
	
	override remove(){
		return this.remove_containment_to_passed_object(super.remove())
	}
	
	override remove(Object obj){
		if(this.contains(obj)){
			super.remove(obj)
			this.remove_containment_to_passed_object(obj as E)
			return true
		}
		return false
	}
	
	override remove(int index){
		return this.remove_containment_to_passed_object(super.remove(index))
	}
	
	override removeFirst(){
		return this.remove_containment_to_passed_object(super.removeFirst())
	}
	
	override removeFirstOccurrence(Object o){
		return this.remove(o)
	}
	
	override removeLast(){
		return this.remove_containment_to_passed_object(super.removeLast())
	}
	
	override removeLastOccurrence(Object o){
		var int index = super.lastIndexOf(o)
		if(index > -1){
			this.remove(index)
			return true
		}
		return false
	}
	
	override removeAll(Collection<?> c){
		var int old = this.size()
		for(Object o : c){
			this.remove(o)
		}
		return old !== this.size()
	}
	
	override removeRange(int a, int b){
		var int index = a
		while(index < b) this.remove(index++)
	}
	
	override set(int index, E obj){
		this.remove_containment_to_passed_object(this.get(index))
		super.set(index, this.set_containment_to_passed_object(obj))
	}
	
	override retainAll(Collection<?> c){
		var int old = this.size()
		for(E obj : this){
			if(!(c.contains(obj))){
				this.remove(obj)
			}
		}
		return old !== this.size()
	}
	
	override removeIf(Predicate<? super E> filter){
		var int old = this.size()
		for(E obj : this){
			if(filter.test(obj))
				this.remove(obj)
		}
		return old !== this.size()
	}
	
	override replaceAll(UnaryOperator<E> operator){
		throw new UnsupportedOperationException("Warning! replaceAll(UnaryOperator<E> operator) does not support Containment management")
		//super.replaceAll(operator)
	}
	override iterator(){
		return new SmartEMFCollectionIterator(super.iterator)
	}
	
	override listIterator(){
		//listiterators will not be supported as it allows modification of list
		//modification to the list while it holds a containment will lead to
		//unresolved containment handling. Thus use is not permitted.
		//if it is wished a custom list needs to be implemented in future
		throw new UnsupportedOperationException("use of listIterators are not supported")
	}
	
	override listIterator(int index){
		//listiterators will not be supported as it allows modification of list
		//modification to the list while it holds a containment will lead to
		//unresolved containment handling. Thus use is not permitted.
		//if it is wished a custom list needs to be implemented in future
		throw new UnsupportedOperationException("use of listIterators are not supported")
	}
}