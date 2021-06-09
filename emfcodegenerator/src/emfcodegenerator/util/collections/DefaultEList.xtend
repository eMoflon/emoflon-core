package emfcodegenerator.util.collections

import emfcodegenerator.notification.SmartEMFNotification
import emfcodegenerator.util.MinimalSObjectContainer
import java.util.ArrayList
import java.util.Collection
import java.util.Collections
import java.util.LinkedList
import java.util.function.Predicate
import java.util.function.UnaryOperator
import java.util.stream.Collectors
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature

/**
 * SmartEMF implementation of an {@link ArrayList ArrayList}.</br>
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
	
	/**
	 * stores Notifications for merging or sends them immediately 
	 */
	val notifications = new ListNotificationBuilder
	
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
	
	override resetContainment() {
		this.is_containment_object = false
		this.the_eContainer = null
		this.the_econtaining_feature = null
		for(E object : this){
			try{
				(object as MinimalSObjectContainer).resetContainment()
			} catch (Exception e){
				throw new IllegalArgumentException(
			"The contents of this list do not implement MinimalSObjectContainer. Containments cannot be handled.", e
				)
			}
		}
	}

	override setContainment(EObject container, EStructuralFeature feature) {
		this.is_containment_object = true
		this.the_eContainer = container
		this.the_econtaining_feature = feature
		for(E object : this){
			try{
				(object as MinimalSObjectContainer).setContainment(container, feature)
			} catch (Exception e){
				throw new IllegalArgumentException(
			"The contents of this list do not implement MinimalSObjectContainer. Containments cannot be handled.", e
				)
			}
		}
	}

	override isContainmentObject(){
		return is_containment_object
	}

	override E set_containment_to_passed_object(E obj){
		if(is_containment_object){
			try{
				(obj as MinimalSObjectContainer).setContainment(this.the_eContainer, this.the_econtaining_feature)
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
				(obj as MinimalSObjectContainer).resetContainment()
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
		addNotification[SmartEMFNotification.moveInList(eContainer, eContainingFeature, get(newPosition), oldPosition, newPosition)]
		return obj;
	}
	
	/**
	 * adds the object and updates its containment if needed
	 */
	override add(E e) {
		val added = super.add(this.set_containment_to_passed_object(e))
		addNotification[SmartEMFNotification.addToFeature(eContainer, eContainingFeature, e, indexOf(e))]
		added
	}
	
	override add(int index, E element) {
		super.add(index, this.set_containment_to_passed_object(element))
		addNotification[SmartEMFNotification.addToFeature(eContainer, eContainingFeature, element, index)]
	}
	
	override addAll(Collection<? extends E> c) {
		var int old_size = this.size()
		if (!c.isEmpty) {
			notifications.enableAccumulation
		}
		for(E e : c){
			this.add(e)
		}
		notifications.flush
		return old_size !== this.size()
	}
	
	override addAll(int index, Collection<? extends E> c) {
		var int old_size = this.size()
		if(c.size()>0){
			notifications.enableAccumulation
			var int i = c.size() - 1
			while(i>=0) {
				this.add(index, c.get(i--))
			}
			notifications.flush
		}
		return old_size !== this.size()	
	}
	
	override clear() {
		this.removeAll(new LinkedList(this))
	}
	
	override iterator() {
		return new SmartEMFCollectionIterator(super.iterator, this)
	}
	
	override listIterator(){
		return new SmartEMFListIterator(super.listIterator, this)
	}
	
	override listIterator(int index){
		return new SmartEMFListIterator(super.listIterator(index), this)
	}
	
	override remove(Object o) {
		if(this.contains(o)){
			remove(indexOf(o))
			return true
		}
		return false
	}
	
	override remove(int index) {
		if(index<0 || index>=this.size()) throw new IndexOutOfBoundsException()
		val E obj = this.get(index)
		super.remove(index)
		addNotification[SmartEMFNotification.removeFromFeature(eContainer, eContainingFeature, obj, index)]
		return this.remove_containment_to_passed_object(obj)
	}
	
	override removeAll(Collection<?> c) {
		notifications.enableAccumulation
		var int old = this.size()
		for(Object e : c) this.remove(e)
		notifications.flush
		return old !== this.size()
	}
	
	override removeIf(Predicate<? super E> filter){
		val toBeRemoved = this.stream().filter(filter).collect(Collectors.toList)
		removeAll(toBeRemoved)
	}
	
	override replaceAll(UnaryOperator<E> operator){
		val LinkedList<Pair<Integer,E>> toBeReplaced = new LinkedList
		var index = 0
		for (E e : this) {
			val newE = operator.apply(e)
			if (e !== newE) {
				toBeReplaced.add(index -> newE)
			}
			index++
		}
		for (x : toBeReplaced) {
			set(x.getKey, x.getValue)
		}
	}
	
	override retainAll(Collection<?> c) {
		notifications.enableAccumulation
		var int old = this.size()
		for(Object e : this) if(!c.contains(e)) this.remove(e)
		notifications.flush
		return old !== this.size()
	}
	
	override set(int index, E element) {
		if(index<0 || index>=this.size()) throw new IndexOutOfBoundsException()
		val oldValue = this.get(index)
		addNotification[SmartEMFNotification.set(eContainer, eContainingFeature, oldValue, element, index)]
		this.remove_containment_to_passed_object(oldValue)
		super.set(index, this.set_containment_to_passed_object(element))
	}
	
	override notificationBuilder() {
		notifications
	}
}
