package org.emoflon.smartemf.runtime.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.InternalEList;
import org.emoflon.smartemf.persistence.SmartEMFResource;
import org.emoflon.smartemf.runtime.SmartObject;
import org.emoflon.smartemf.runtime.notification.SmartEMFNotification;

public final class ResourceContentSmartEList<T extends EObject> extends LinkedHashSet<T> implements EList<T>, InternalEList<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 188149705186560304L;

	private SmartEMFResource resource;

	public ResourceContentSmartEList(SmartEMFResource r) {
		this.resource = (SmartEMFResource) r;
	}

	@Override
	public boolean add(T element) {
		if (contains(element))
			return false;

		if (element instanceof SmartObject) {
			resetContainment(element, !resource.equals(element.eResource()));
			((SmartObject) element).setResource(resource, true);
			return super.add(element);
		} else {
			element.eAdapters().addAll(resource.eAdapters());
//			resetContainment(element,!resource.equals(element.eResource()));

			NotificationChain notificationChain = ((InternalEObject) element).eSetResource(resource, null);
			sendNotifications(notificationChain);
			boolean success = super.add(element);
			sendAddNotification(element);
			return success;
		}
	}

	@Override
	public void add(int index, T element) {
//		if (contains(element))
//			return;
//
//		if (element instanceof SmartObject) {
//			resetContainment(element, !resource.equals(element.eResource()));
//			((SmartObject) element).setResource(resource, true);
//			super.add(index, element);
//		} else {
//			element.eAdapters().addAll(resource.eAdapters());
//			resetContainment(element, !resource.equals(element.eResource()));
//
//			((InternalEObject) element).eSetResource(resource, null);
//			super.add(index, element);
//			sendAddNotification(element);
//		}
		throw new UnsupportedOperationException("Indexbased modifications are not supported by SmartEMF");
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean success = false;
		Collection<T> objs = new LinkedList<>(c);
		for (T t : objs)
			success = this.add(t) || success;

		return success;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
//		Collection<T> objs = new LinkedList<>(c);
//		for (T t : objs)
//			this.add(index++, t);
//
//		return !c.isEmpty();
		throw new UnsupportedOperationException("Indexbased modifications are not supported by SmartEMF");
	}

	private void resetContainment(T e, boolean removeRecursively) {
		EObject oldContainer = e.eContainer();
		if (oldContainer != null) {
			if (e.eContainingFeature().isMany()) {
				Object getResult = oldContainer.eGet(e.eContainingFeature());
				if(removeRecursively)
					((SmartCollection<?, ?>) getResult).remove(e);
				else
					((SmartCollection<?, ?>) getResult).removeWithoutContainerResetting(e);
			} else {
				if(removeRecursively) {
					oldContainer.eUnset(e.eContainingFeature());
				}
				else {
					Resource tmp = e.eResource();
					((SmartObject) e).setResourceWithoutChecks(null);
					oldContainer.eUnset(e.eContainingFeature());
					sendRemoveNotification(oldContainer);
					((SmartObject) e).setResourceWithoutChecks((Internal) tmp);
				}
			}
		} else {
			// if there is no eContainer, then this element is only contained within the resource and should be removed before setting the new eContainer
			if (e.eResource() != null) {
				e.eResource().getContents().remove(e);
			}
		}
	}

	@Override
	public void clear() {
		for (T t : this) {
			sendRemoveNotification((EObject) t);
			if (t instanceof SmartObject) {
				((SmartObject) t).setResource(null, true);
			} else {
				t.eAdapters().removeAll(resource.eAdapters());
				NotificationChain notificationChain = ((InternalEObject) t).eSetResource(null, null);
				sendNotifications(notificationChain);
			}
		}
		super.clear();
	}

	@Override
	public T remove(int index) {
//		Object o = get(index);
//		sendRemoveNotification((EObject) o);
//		if (o instanceof SmartObject) {
//			((SmartObject) o).setResource(null, true);
//		} else {
//			((EObject) o).eAdapters().removeAll(resource.eAdapters());
//			((InternalEObject) o).eSetResource(null, null);
//		}
//		return super.remove(index);
		throw new UnsupportedOperationException("Indexbased modifications are not supported by SmartEMF");
	}

	@Override
	public boolean remove(Object o) {
		boolean success = super.remove(o);
		if (success)
			sendRemoveNotification((EObject) o);

		if (o instanceof SmartObject) {
			((SmartObject) o).setResource(null, true);
		} else {
			((EObject) o).eAdapters().removeAll(resource.eAdapters());
			NotificationChain notificationChain = ((InternalEObject) o).eSetResource(null, null);
			sendNotifications(notificationChain);
		}

		return success;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean success = false;

		Collection<?> objs = new LinkedList<>(c);
		for (Object t : objs)
			success =  this.remove(t) || success;

		return success;
	}

	@Override
	public T basicGet(int index) {
		return get(index);
	}

	@Override
	public List<T> basicList() {
		return this;
	}

	@Override
	public Iterator<T> basicIterator() {
		return iterator();
	}

	@Override
	public ListIterator<T> basicListIterator() {
		return listIterator();
	}

	@Override
	public ListIterator<T> basicListIterator(int index) {
		return listIterator(index);
	}

	@Override
	public Object[] basicToArray() {
		return toArray();
	}

	@Override
	public <T> T[] basicToArray(T[] array) {
		return toArray(array);
	}

	@Override
	public int basicIndexOf(Object object) {
		return indexOf(object);
	}

	@Override
	public int basicLastIndexOf(Object object) {
		return lastIndexOf(object);
	}

	@Override
	public boolean basicContains(Object object) {
		return contains(object);
	}

	@Override
	public boolean basicContainsAll(Collection<?> collection) {
		return containsAll(collection);
	}

	@Override
	public NotificationChain basicRemove(Object o, NotificationChain notifications) {
		boolean success = super.remove(o);
		if (success)
			sendRemoveNotification((EObject) o);

		if (o instanceof SmartObject) {
			((SmartObject) o).setResource(null, true);
		} else {
//			((EObject) o).eAdapters().removeAll(resource.eAdapters());
//			((InternalEObject) o).eSetResource(null, null);
		}

		return notifications;
	}

	@Override
	public NotificationChain basicAdd(T object, NotificationChain notifications) {
		add(object);
		return notifications;
	}

	@Override
	public void addUnique(T object) {
		add(object);
	}

	@Override
	public void addUnique(int index, T object) {
		add(index, object);
	}

	@Override
	public boolean addAllUnique(Collection<? extends T> collection) {
		return addAll(collection);
	}

	@Override
	public boolean addAllUnique(int index, Collection<? extends T> collection) {
//		return addAll(index, collection);
		throw new UnsupportedOperationException("Indexbased modifications are not supported by SmartEMF");
	}

	@Override
	public T setUnique(int index, T object) {
		throw new UnsupportedOperationException("Indexbased modifications are not supported by SmartEMF");
	}

	@Override
	public void move(int newPosition, T object) {
//		move(newPosition, indexOf(object));
		throw new UnsupportedOperationException("Indexbased modifications are not supported by SmartEMF");
	}

	@Override
	public T move(int newPosition, int oldPosition) {
//		T t = remove(oldPosition);
//		add(newPosition, t);
//		return t;
		throw new UnsupportedOperationException("Indexbased modifications are not supported by SmartEMF");
	}

	protected void sendAddNotification(EObject obj) {
		for (Adapter a : resource.eAdapters()) {
			a.notifyChanged(SmartEMFNotification.createAddNotification(resource, null, obj, -1));
		}
	}

	protected void sendRemoveNotification(EObject obj) {
		for (Adapter a : resource.eAdapters()) {
			a.notifyChanged(SmartEMFNotification.createRemoveNotification(resource, null, obj, -1));
		}
	}

	protected void sendNotifications(NotificationChain notificationChain) {
		// should only be called when there are no SmartEMF objects in use
		if (notificationChain != null)
			notificationChain.dispatch();
	}

	@Override
	public T get(int index) {
		int counter = 0;
		for(T t : this) {
			if(counter == index)
				return t;
			counter++;
		}
		throw new IndexOutOfBoundsException(index);
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException("Indexbased modifications are not supported by SmartEMF");
	}

	@Override
	public int indexOf(Object o) {
		int counter = 0;
		for(T t : this) {
			if(t.equals(o)) {
				return counter;
			}
			counter++;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return indexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException("ListIterators are not supported by SmartEMF");
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException("ListIterators are not supported by SmartEMF");
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("Sublists are not supported by SmartEMF");
	}

}
