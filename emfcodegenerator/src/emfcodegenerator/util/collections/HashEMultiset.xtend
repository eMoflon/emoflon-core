package emfcodegenerator.util.collections

import com.google.common.collect.HashMultiset
import com.google.common.collect.Multiset
import com.google.common.collect.Sets
import emfcodegenerator.notification.SmartEMFNotification
import emfcodegenerator.util.MinimalSObjectContainer
import emfcodegenerator.util.collections.SmartEMFListIterator.PseudoListIterator
import java.util.Collection
import java.util.Collections
import java.util.LinkedList
import java.util.function.Predicate
import java.util.function.UnaryOperator
import java.util.stream.Collectors
import java.util.stream.Stream
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature

class HashEMultiset<E> implements Multiset<E>, MinimalSObjectContainerCollection<E> {
	
	/**
	 * the HashMultiset that contains the contents of this collection
	 */
	var HashMultiset<E> backingHashMultiset
	
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
	 * Constructs new HashEMultiset and sets the {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} to the given values.
	 * @param eContainer EObject
	 * @param eContaining_feature EStructuralFeature
	 * @author Adrian Zwenger, Paul Schiffner
	 */
	new(EObject eContainer, EStructuralFeature eContaining_feature){
		super()
		backingHashMultiset = HashMultiset.create
		this.the_eContainer = eContainer
		this.the_econtaining_feature = eContaining_feature
		this.is_containment_object = true
	}
	
	/**
	 * Constructs new HashEMultiset from a given Collection and sets the
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} to the given values.
	 * @param c Collection<? extends E>
	 * @param eContainer EObject
	 * @param eContaining_feature EStructuralFeature
	 * @author Adrian Zwenger, Paul Schiffner
	 */
	new(Collection<? extends E> c, EObject eContainer, EStructuralFeature eContaining_feature){
		this.the_eContainer = eContainer
		this.the_econtaining_feature = eContaining_feature
		this.is_containment_object = true
		backingHashMultiset = HashMultiset.create(c)
	}
	
	/**
	 * Constructs new HashEMultiset from a given Collection. The
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} are set as null.
	 * @param c Collection<? extends E>
	 * @author Adrian Zwenger, Paul Schiffner
	 */
	new(Collection<? extends E> c){
		backingHashMultiset = HashMultiset.create(c)
	}
	
	/**
	 * Constructs new HashEMultiset. The
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} are set as null.
	 * @author Adrian Zwenger, Paul Schiffner
	 */
	new(){
		backingHashMultiset = HashMultiset.create
	}
	
	/**
	 * Constructs new HashEMultiset with the specified initial number
	 * of distinct elements and sets the
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-feature} to null.
	 * @param initial_size int
	 * @param eContainer EObject
	 * @param eContaining_feature EStructuralFeature
	 * @author Adrian Zwenger, Paul Schiffner
	 */
	new(int initialsize){
		backingHashMultiset = HashMultiset.create(initialsize)
	}
	
	/**
	 * Constructs new HashEMultiset with the specified initial number
	 * of distinct elements and sets the
	 * {@link #the_eContainer eContainer} and the
	 * {@link #the_econtaining_feature eContaining-Feature} to the given values.
	 * @param initial_size int
	 * @param eContainer EObject
	 * @param eContaining_feature EStructuralFeature
	 * @author Adrian Zwenger, Paul Schiffner
	 */
	new(int initial_size, EObject eContainer, EStructuralFeature eContaining_feature){
		this(initial_size)
		this.the_eContainer = eContainer
		this.the_econtaining_feature = eContaining_feature
		this.is_containment_object = true
	}
	
	override remove_containment_to_passed_object(E obj) {
		if(is_containment_object){
			try{
				(obj as MinimalSObjectContainer).reset_containment()
			} catch (ClassCastException e){
				throw new IllegalArgumentException(
			"The contents of this list do not implement MinimalSObjectContainer. Containments cannot be handled.", e
				)
			}
		}
		return obj
	}
	
	override set_containment_to_passed_object(E obj) {
		if(is_containment_object){
			try{
				(obj as MinimalSObjectContainer).set_containment(this.the_eContainer, this.the_econtaining_feature)
			} catch (ClassCastException e){
				throw new IllegalArgumentException(
			"The contents of this list do not implement MinimalSObjectContainer. Containments cannot be handled.", e
				)
			}
		}
		return obj
	}
	
	override move(int newPosition, E object) {
		throw new UnsupportedOperationException("HashEMultiset does not support move(int, E), as it is an unordered multiset")
	}
	
	override move(int newPosition, int oldPosition) {
		throw new UnsupportedOperationException("HashEMultiset does not support move(int, int), as it is an unordered multiset")
	}
	
	override add(E e) {
		val added = backingHashMultiset.add(this.set_containment_to_passed_object(e))
		addNotification[SmartEMFNotification.addToFeature(eContainer, eContainingFeature, e, -1)]
		added
	}
	
	override add(int index, E element) {
		throw new UnsupportedOperationException("HashEMultiset does not support add(int, E), as it is an unordered multiset")
	}
	
	override addAll(Collection<? extends E> c) {
		val oldSize = size
		notifications.enableAccumulation
		c.forEach[add]
		notifications.flush
		oldSize != size
	}
	
	override addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException("HashEMultiset does not support addAll(int, Collection<? extends E>), as it is an unordered multiset")
	}
	
	override clear() {
		removeAll(HashMultiset.create(backingHashMultiset))
	}
	
	override contains(Object o) {
		backingHashMultiset.contains(o)
	}
	
	override containsAll(Collection<?> c) {
		backingHashMultiset.containsAll(c)
	}
	
	override get(int index) {
		throw new UnsupportedOperationException("HashEMultiset does not support get(int), as it is an unordered multiset")
	}
	
	override indexOf(Object o) {
		throw new UnsupportedOperationException("HashEMultiset does not support indexOf(Object), as it is an unordered multiset")
	}
	
	override isEmpty() {
		backingHashMultiset.isEmpty
	}
	
	override iterator() {
		new SmartEMFCollectionIterator(backingHashMultiset.iterator, this)
	}
	
	override lastIndexOf(Object o) {
		throw new UnsupportedOperationException("HashEMultiset does not support lastIndexOf(Object), as it is an unordered multiset")
	}
	
	override listIterator() {
		new PseudoListIterator(backingHashMultiset.iterator)
	}
	
	override listIterator(int index) {
		new PseudoListIterator(backingHashMultiset.iterator, index)
	}
	
	override remove(Object o) {
		val removed = backingHashMultiset.remove(o)
		if (removed) {
			addNotification[SmartEMFNotification.removeFromFeature(eContainer, eContainingFeature, o, -1)]
		}
		return removed
	}
	
	override remove(int index) {
		throw new UnsupportedOperationException("HashEMultiset does not support remove(int), as it is an unordered multiset")
	}
	
	override removeAll(Collection<?> c) {
		notifications.enableAccumulation
		val oldSize = size
		c.forEach[x | remove(x, count(x))]
		notifications.flush
		oldSize != size
	}
	
	override removeIf(Predicate<? super E> p) {
		val toBeRemoved = stream.filter(p).collect(Collectors.toList)
		removeAll(toBeRemoved)
	}
	
	/*
	 * This assumes the UnaryOperator might not always return the same result when applied to the same object.
	 * If it does, then a version that only calls apply once per distinct element is faster.
	 */
	override replaceAll(UnaryOperator<E> o) {
		val replacements = stream
			.map[x | x -> o.apply(x)]
			.filter[x | x.key != x.value]
			.collect(Collectors.toList)
			
		notifications.enableAccumulation
		
		replacements.stream
			.map[key]
			.forEach[remove]
		
		replacements.stream
			.map[value]
			.forEach[add]
			
		notifications.flush
	}
	
	override retainAll(Collection<?> c) {
		val toBeRetained = Sets.newHashSet(c)
		val toBeRemoved = new LinkedList(Sets.difference(this.elementSet, toBeRetained))
		removeAll(toBeRemoved)
		val oldSize = size
		
		oldSize != size
	}
	
	override set(int index, E element) {
		throw new UnsupportedOperationException("HashEMultiset does not support set(index, E), as it is an unordered multiset")
	}
	
	override size() {
		backingHashMultiset.size
	}
	
	override subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("HashEMultiset does not support subList(int, int), as it is an unordered multiset")
	}
	
	override toArray() {
		backingHashMultiset.toArray
	}
	
	override <T> toArray(T[] a) {
		backingHashMultiset.toArray(a)
	}
	
	override eContainer() {
		the_eContainer
	}
	
	override eContainingFeature() {
		the_econtaining_feature 
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
	
	override is_containment_object() {
		is_containment_object
	}
	
	// Multiset methods
	
	/**
	 * Adds a number of occurrences of an element to this multiset.
	 * 
	 * @return the count of elements before adding
	 */
	override add(E element, int count) {
		val before = backingHashMultiset.count(element)
		if (count == 0) return before
		val addedElements = Stream.generate([element])
			.limit(count)
			.collect(Collectors.toCollection([new HashEMultiset]))
		backingHashMultiset.add(element, count)
		addNotification[SmartEMFNotification.addManyIdentical(eContainer, eContainingFeature, addedElements)]	
		return before
	}
	
	/**
	 * @return the number of occurrences of an element in this multiset
	 */
	override count(Object element) {
		backingHashMultiset.count(element)
	}
	
	/**
	 * @return an unmodifiable view of the set of distinct elements in this multiset
	 */
	override elementSet() {
		Collections.unmodifiableSet(backingHashMultiset.elementSet)
	}
	
	/**
	 * @return an unmodifiable view of the contents of this multiset,
	 * grouped into {@link Multiset.Entry} instances,
	 * each providing an element of the multiset and the count of that element.
	 */
	override entrySet() {
		Collections.unmodifiableSet(backingHashMultiset.entrySet)
	}
	
	/**
	 * Removes a number of occurrences of the specified element from this multiset.
	 * 
	 * @return the count of elements before removal
	 */
	override remove(Object element, int count) {
		val before = backingHashMultiset.count(element)
		if (count == 0) return before
		val removedElements = Stream.generate([element])
			.limit(count)
			.collect(Collectors.toCollection([new HashEMultiset]))
		backingHashMultiset.remove(element, count)
		addNotification[SmartEMFNotification.removeManyIdentical(eContainer, eContainingFeature, removedElements)]
		return before
	}
	
	/**
	 * Adds or removes the necessary occurrences of an element
	 * such that the element attains the desired count.
	 * 
	 * @return the count of elements before it was set
	 */
	override setCount(E element, int count) {
		val before = backingHashMultiset.count(element)
		val diff = count - before
		if (diff > 0) {
			add(element, diff)
		} else if (diff < 0) {
			remove(element, -diff)
		}
		return before
	}
	
	/**
	 * Conditionally sets the count of an element to a new value,
	 * as described in {@code setCount(Object, int)},
	 * provided that the element has the expected current count.
	 * 
	 * @return if oldCount matched the actual count before it was set
	 */
	override setCount(E element, int oldCount, int newCount) {
		val before = backingHashMultiset.count(element)
		if (before != oldCount) return false
		if (before == newCount) return true
		setCount(element, newCount)
		return true
	}
	
	override spliterator() {
		backingHashMultiset.spliterator
	}
	
	override notificationBuilder() {
		notifications
	}	
}