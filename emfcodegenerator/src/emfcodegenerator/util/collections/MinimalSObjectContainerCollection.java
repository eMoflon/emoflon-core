package emfcodegenerator.util.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Supplier;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.InternalEList;

import emfcodegenerator.notification.SmartEMFNotification;
import emfcodegenerator.util.MinimalSObjectContainer;

interface MinimalSObjectContainerCollection<E> extends InternalEList<E>, MinimalSObjectContainer {
	E set_containment_to_passed_object(E obj);
	
	E remove_containment_to_passed_object(E obj);
	
	@Override
	default boolean addAllUnique(Collection<? extends E> coll) {
		return addAll(coll);
	}
	
	@Override
	default boolean addAllUnique(int index, Collection<? extends E> coll) {
		return addAll(index, coll);
	}
	
	@Override
	default void addUnique(E e) {
		add(e);
	}
	
	@Override
	default void addUnique(int index, E e) {
		add(index, e);
	}
	
	@Override
	default NotificationChain basicAdd(E object, NotificationChain notifications) {
		throw new UnsupportedOperationException("basicAdd is not implemented");
	}
	
	@Override
	default boolean basicContains(Object o) {
		return contains(o);
	}
	
	@Override
	default boolean basicContainsAll(Collection<?> coll) {
		return containsAll(coll);
	}
	
	@Override
	default E basicGet(int index) {
		return get(index);
	}
	
	@Override
	default int basicIndexOf(Object o) {
		return indexOf(o);
	}
	
	@Override
	default Iterator<E> basicIterator() {
		return iterator();
	}
	
	@Override
	default int basicLastIndexOf(Object o) {
		return lastIndexOf(o);
	}
	
	@Override
	default List<E> basicList() {
		return Collections.unmodifiableList(this);
	}
	
	@Override
	default ListIterator<E> basicListIterator() {
		return listIterator();
	}
	
	@Override
	default ListIterator<E> basicListIterator(int index) {
		return listIterator(index);
	}
	
	@Override
	default NotificationChain basicRemove(Object o, NotificationChain notifications) {
		throw new UnsupportedOperationException("basicRemove is not implemented");
	}
	
	@Override
	default Object[] basicToArray() {
		return toArray();
	}
	
	@Override
	default <T> T[] basicToArray(T[] arr) {
		return toArray(arr);
	}
	
	@Override
	default E setUnique(int index, E e) {
		return set(index, e);
	}
	
	/**
	 * @return the list's ListNotificationBuilder
	 */
	ListNotificationBuilder notificationBuilder();
	
	/**
	 * Checks if notifications should be generated. If yes, uses the supplied factory to generate one and adds it to the notification builder.
	 * @return whether a notification was generated
	 */
	default boolean addNotification(Supplier<SmartEMFNotification> notificationFactory) {
		EObject container = eContainer();
		if (container != null && container.eDeliver() && !Objects.requireNonNullElse(container.eAdapters(), Collections.EMPTY_LIST).isEmpty()) {
			return notificationBuilder().add(notificationFactory.get());
		} else {
			return false;
		}
	}
}
