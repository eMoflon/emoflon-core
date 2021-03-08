package emfcodegenerator.util.collections

import java.util.HashSet
import java.util.Collection
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature
import emfcodegenerator.util.MinimalSObjectContainer
import emfcodegenerator.notification.SmartEMFNotification
import java.util.LinkedList

class  HashESet<E> extends HashSet<E> implements MinimalSObjectContainerCollection<E>{
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
	
	/**
	 * stores Notifications for merging or sends them immediately 
	 */
	val notifications = new ListNotificationBuilder
	
	/**########################Constructors########################*/

	/**
	 * Constructs new HashESet. The
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} are set as null.
	 * @author Adrian Zwenger
	 */
	new(){
		super()
	}

	/**
	 * Constructs new HashESet and sets the {@link #the_eContainer eContainer} and the
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
	 * Constructs new HashESet with the specified initial size and sets the
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-feature} to null.
	 * @param initial_size int
	 * @author Adrian Zwenger
	 */
	new(int initialsize){
		super(initialsize)
	}

	/**
	 * Constructs new HashESet with the specified initial size and sets the
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
	 * Constructs new HashESet with the specified initial size and sets the
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-feature} to null.
	 * @param initial_size int
	 * @param loadFactor float	
	 * @author Adrian Zwenger
	 */
	new(int initialsize, float loadFactor){
		super(initialsize, loadFactor)
	}

	/**
	 * Constructs new HashESet with the specified initial size and sets the
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} to the given values.
	 * @param initial_size int
	 * @param loadFactor float
	 * @param eContainer EObject
	 * @param eContaining_feature EStructuralFeature
	 * @author Adrian Zwenger
	 */
	new(int initial_size, float loadFactor, EObject eContainer, EStructuralFeature eContaining_feature){
		super(initial_size, loadFactor)
		this.the_eContainer = eContainer
		this.the_econtaining_feature = eContaining_feature
		this.is_containment_object = true
	}

	/**
	 * Constructs new HashESet from a given Collection. The
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} are set as null.
	 * @param c Collection<? extends E>
	 * @author Adrian Zwenger
	 */
	new(Collection<? extends E> c){
		super(c)
	}

	/**
	 * Constructs new HashESet from a given Collection and sets the
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

	/**
	 * if this list is a containment list, passed objects will have their containment set and returned.
	 * if it is not, the object will be returned unchanged
	 */
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
		throw new UnsupportedOperationException("HashESet does not support move, as it is an unordered set")
	}
	
	override move(int newPosition, int oldPosition) {
		throw new UnsupportedOperationException("HashESet does not support move, as it is an unordered set")
	}
	
	override add(E e) {
		notifications.add(SmartEMFNotification.addToFeature(eContainer, eContainingFeature, e, -1))
		return super.add(this.set_containment_to_passed_object(e))
	}
	
	override add(int index, E element) {
		throw new UnsupportedOperationException("HashESet does not support indexing.")
	}
	
	override addAll(Collection<? extends E> c) {
		var int old = this.size()
		notifications.enableAccumulation
		for(E e : c){
			this.add(e)
		}
		notifications.flush
		return old !== this.size()
	}
	
	override addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException("HashESet does not support addAll(int,Collection<? extends E>), as it is an unordered set")
	}
	
	override clear() {
		this.removeAll(new LinkedList(this))
		return;
	}
	
	override get(int index) {
		throw new UnsupportedOperationException("HashESet does not support get(int), as it is an unordered set")
	}
	
	override indexOf(Object o) {
		throw new UnsupportedOperationException(
			"HashESet does not support indexOf(Object), as it is an unordered set"
			)
	}
	
	override iterator() {
		return new SmartEMFCollectionIterator(super.iterator(), this)
	}
	
	override lastIndexOf(Object o) {
		throw new UnsupportedOperationException(
			"HashESet does not support lastIndexOf(Object), as it is an unordered set"
			)
	}
	
	override listIterator(){
		new SmartEMFListIterator.PseudoListIterator(iterator)
	}
	
	override listIterator(int index){
		new SmartEMFListIterator.PseudoListIterator(iterator, index)
	}
	
	override remove(Object o) {
		if(this.contains(o)) {
			notifications.add(SmartEMFNotification.removeFromFeature(eContainer, eContainingFeature, o, -1))
			return super.remove(this.remove_containment_to_passed_object(o as E))
		}
		return false
	}
	
	override remove(int index) {
		throw new UnsupportedOperationException(
			"HashESet does not support remove(int), as it is an unordered set"
			)
	}
	
	override removeAll(Collection<?> c) {
		var int old = this.size()
		notifications.enableAccumulation
		for(Object e : c) this.remove(e)
		notifications.flush
		return old !== this.size()
	}
	
	override retainAll(Collection<?> c) {
		var int old_size = this.size()
		notifications.enableAccumulation
		for(E obj : this) if(!(c.contains(obj))) this.remove(obj)
		notifications.flush
		return this.size !== old_size
	}
	
	override set(int index, E element) {
		throw new UnsupportedOperationException(
			"HashESet does not support set(int,E), as it is an unordered set"
			)
	}
	
	override subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException(
			"HashESet does not support subList(int, int), as it is an unordered set"
		)
	}
	
	override toArray() {
		var Object [] arr = newArrayOfSize(this.size)
		var index = 0
		for(E c : this){
			arr.set(index++, c)
		}
		return arr
	}
	
	override <T> toArray(T[] a) {
		var Object [] arr = a
		var index = 0
		for(E e : this){
			arr.set(index++, e)
		}
		return a
	}

}