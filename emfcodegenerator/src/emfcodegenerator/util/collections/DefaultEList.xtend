package emfcodegenerator.util.collections

import java.util.ArrayList
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature
import java.util.Collection
import java.util.Collections
import emfcodegenerator.util.MinimalSObjectContainer

/**
 * SmartEMF implementation of an {@link java.util.ArrayList ArrayList}.</br>
 * Goal of this Collection is to offer a Collection with Containment feature management abilities.
 */
class DefaultEList<E> extends ArrayList<E> implements MinimalSObjectContainerCollection<E> {

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
	 * Constructs new DefaultEList and sets the {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} to the given values.
	 * @param eContainer EObject
	 * @param eContaining_feature EStructuralFeature
	 * @author Adrian Zwenger
	 */
	new(EObject eContainer, EStructuralFeature eContaining_feature){
		super()
		this.the_eContainer = eContainer
		this.the_econtaining_feature = eContaining_feature
		this.is_containment_object = true
	}
	
	/**
	 * Constructs new DefaultEList from a given Collection and sets the
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
	 * Constructs new DefaultEList from a given Collection. The
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} are set as null.
	 * @param c Collection<? extends E>
	 * @author Adrian Zwenger
	 */
	new(Collection<? extends E> c){
		super(c)
	}
	
	/**
	 * Constructs new DefaultEList. The
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} are set as null.
	 * @author Adrian Zwenger
	 */
	new(){
		super()
	}
	
	/**
	 * Constructs new DefaultEList with the specified initial size and sets the
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} to the given values.
	 * @param initial_size int
	 * @param eContainer EObject
	 * @param eContaining_feature EStructuralFeature
	 * @author Adrian Zwenger
	 */
	new(int initial_size, EObject eContainer, EStructuralFeature eContaining_feature){
		super(initial_size)
		this.the_eContainer = eContainer
		this.the_econtaining_feature = eContaining_feature
		this.is_containment_object = true
	}
	
	/**
	 * Constructs new DefaultEList with the specified initial size and sets the
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-feature} to null.
	 * @param initial_size int
	 * @param eContainer EObject
	 * @param eContaining_feature EStructuralFeature
	 * @author Adrian Zwenger
	 */
	new(int initialsize){
		super(initialsize)
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
			"The contents of this list do not implement MinimalSObjectContainer. Containments cannot be handled.", e
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
			"The contents of this list do not implement MinimalSObjectContainer. Containments cannot be handled.", e
				)
			}
		}
	}

	override is_containment_object(){
		return is_containment_object
	}

	override E set_containment_to_passed_object(E obj){
		if(is_containment_object){
			try{
				(obj as MinimalSObjectContainer).set_containment(this.the_eContainer, this.the_econtaining_feature)
			} catch (Exception e){
				throw new IllegalArgumentException(
			"The contents of this list do not implement MinimalSObjectContainer. Containments cannot be handled.", e
				)
			}
		}
		return obj
	}

	/**
	 * if this list is a containment list, the containment will be removed from the passed object and it will be returned
	 * if it is not, the object will be returned unchanged
	 */
	override E remove_containment_to_passed_object(E obj){
		if(is_containment_object){
			try{
				(obj as MinimalSObjectContainer).reset_containment()
			} catch (Exception e){
				throw new IllegalArgumentException(
			"The contents of this list do not implement MinimalSObjectContainer. Containments cannot be handled.", e
				)
			}
		}
		return obj
	}
	
	/**########################EList methods########################*/
	
	override move(int newPosition, E object) {
		if(this.contains(object))
			this.move(newPosition, this.indexOf(object))
	}
	
	override move(int newPosition, int oldPosition) {
		if(newPosition<0 || oldPosition<0 || newPosition>=this.size || oldPosition>=this.size)
			throw new IndexOutOfBoundsException()
		var E obj = this.get(oldPosition)
		var int j = oldPosition
		var int k = newPosition
		Collections.rotate(this.subList(j < k ? j : k, (j < k ? k : j) + 1), j < k ? -1 : 1);
		return obj;
	}
	
	/**
	 * adds the object and updates its containment if needed
	 */
	override add(E e) {
		super.add(this.set_containment_to_passed_object(e));
	}
	
	override add(int index, E element) {
		super.add(index, this.set_containment_to_passed_object(element))
	}
	
	override addAll(Collection<? extends E> c) {
		var int old_size = this.size()
		for(E e : c){
			this.add(e)
		}
		return old_size !== this.size()
	}
	
	override addAll(int index, Collection<? extends E> c) {
		var int old_size = this.size()
		if(c.size()>0){
			var int i = c.size() - 1
			while(i>=0) {
				this.add(index, c.get(i--))
			}
		}
		return old_size !== this.size()	
	}
	
	override clear() {
		this.removeAll()
	}
	
	override contains(Object o) {
		return super.contains(o)
	}
	
	override containsAll(Collection<?> c) {
		return super.containsAll(c)
	}
	
	override get(int index) {
		return super.get(index)
	}
	
	override indexOf(Object o) {
		return super.indexOf(o)
	}
	
	override isEmpty() {
		return super.isEmpty()
	}
	
	override iterator() {
		return new SmartEMFCollectionIterator(super.iterator, this)
	}
	
	override lastIndexOf(Object o) {
		return super.lastIndexOf(o)
	}
	
	override listIterator(){
		//listiterators will not be supported as it allows modification of list
		//modification to the list while it holds a containment will lead to
		//unresolved containment handling. Thus use is not permitted.
		//if it is wished a custom list needs to be implemented in future
		throw new UnsupportedOperationException("use of listIterators is not supported")
	}
	
	override listIterator(int index){
		//listiterators will not be supported as it allows modification of list
		//modification to the list while it holds a containment will lead to
		//unresolved containment handling. Thus use is not permitted.
		//if it is wished a custom list needs to be implemented in future
		throw new UnsupportedOperationException("use of listIterators is not supported")
	}
	
	override remove(Object o) {
		if(this.contains(o)){
			super.remove(o)
			this.remove_containment_to_passed_object(o as E)
			return true
		}
		return false
	}
	
	override remove(int index) {
		if(index<0 || index>=this.size()) throw new IndexOutOfBoundsException()
		var E obj = this.get(index)
		super.remove(index)
		return this.remove_containment_to_passed_object(obj)
	}
	
	override removeAll(Collection<?> c) {
		var int old = this.size()
		for(Object e : c) this.remove(e)
		return old !== this.size()
	}
	
	override retainAll(Collection<?> c) {
		var int old = this.size()
		for(Object e : this) if(!c.contains(e)) this.remove(e)
		return old !== this.size()
	}
	
	override set(int index, E element) {
		if(index<0 || index>=this.size()) throw new IndexOutOfBoundsException()
		this.remove_containment_to_passed_object(this.get(index))
		super.set(index, this.set_containment_to_passed_object(element))
	}
	
	override size() {
		return super.size()
	}
	
	override subList(int fromIndex, int toIndex) {
		return super.subList(fromIndex,toIndex)
	}
	
	override toArray() {
		return super.toArray()
	}
	
	override <T> toArray(T[] a) {
		super.toArray(a)
	}

}
