package org.moflon.smartemf.runtime.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.common.notify.impl.NotifyingListImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.InternalEList;
import org.moflon.smartemf.persistence.SmartEMFResource;
import org.moflon.smartemf.runtime.SmartObject;
import org.moflon.smartemf.runtime.notification.SmartEMFNotification;

public final class ResourceContentSmartEList<T extends EObject> extends LinkedList<T> implements EList<T>, InternalEList<T>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 188149705186560304L;

	private SmartEMFResource resource;
	
	public ResourceContentSmartEList(SmartEMFResource r) {
		this.resource = (SmartEMFResource) r;
	}
	
	@Override
	public boolean add(T e) {
		if(e instanceof SmartObject) {
			((SmartObject) e).setResource(resource, true);
			return super.add(e);
		} else {
			e.eAdapters().addAll(resource.eAdapters());
			resetContainment(e);
			
			((InternalEObject) e).eSetResource(resource, null);
			boolean success = super.add(e);
			sendAddNotification(e);
			return success;
		}
	}

	private void resetContainment(T e) {
		EObject oldContainer = e.eContainer();
		if(oldContainer != null) {
			if(e.eContainingFeature().isMany()) {
				Object getResult = oldContainer.eGet(e.eContainingFeature());
				((Collection<?>) getResult).remove(e);
			}
			else {
				oldContainer.eUnset(e.eContainingFeature());
			}
		}
	}
	
	@Override
	public void add(int index, T element) {
		if(element instanceof SmartObject) {
			((SmartObject) element).setResource(resource, true);
			super.add(index, element);

		} else  {
			element.eAdapters().addAll(resource.eAdapters());
			resetContainment(element);
			
			((InternalEObject) element).eSetResource(resource, null);
			super.add(index, element);
			sendAddNotification(element);
		}
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		Collection<T> iObjs = new LinkedList<>();
		for(T t : c) {
			if(t instanceof SmartObject) {
				((SmartObject) t).setResource(resource, true);
			} else {
				t.eAdapters().addAll(resource.eAdapters());
				iObjs.add(t);
			}
		}
		iObjs.forEach(this::resetContainment);
		iObjs.forEach(t -> ((InternalEObject) t).eSetResource(resource, null));
		boolean success = super.addAll(c) || super.addAll(iObjs);
		iObjs.forEach(this::sendAddNotification);
		return success;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		Collection<T> iObjs = new LinkedList<>();
		for(T t : c) {
			if(t instanceof SmartObject) {
				((SmartObject) t).setResource(resource, true);
			} else { 
				t.eAdapters().addAll(resource.eAdapters());
				iObjs.add(t);
			}
		}
		iObjs.forEach(this::resetContainment);
		iObjs.forEach(t -> ((InternalEObject) t).eSetResource(resource, null));
		boolean success =  super.addAll(index, c) || super.addAll(index, iObjs);
		iObjs.forEach(this::sendAddNotification);
		return success;
	}
	
	@Override
	public void clear() {
		for(T t : this) {
			if(t instanceof SmartObject) {
				((SmartObject) t).setResource(null, true);
			} else {
				t.eAdapters().removeAll(resource.eAdapters());
				((InternalEObject) t).eSetResource(null, null);
			}
		}
		super.clear();
	}
	
	@Override
	public T remove(int index) {
		Object o =  get(index);
		if(o instanceof SmartObject) {
			((SmartObject) o).setResource(null, true);
		} else {
			((EObject) o).eAdapters().removeAll(resource.eAdapters());
			((InternalEObject) o).eSetResource(null, null);
		}
		return super.remove(index);
	}
	
	@Override
	public boolean remove(Object o) {
		if(o instanceof SmartObject) {
			((SmartObject) o).setResource(null, true);
		} else {
			((EObject) o).eAdapters().removeAll(resource.eAdapters());
			((InternalEObject) o).eSetResource(null, null);
		}
		return super.remove(o);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		for(Object t : c) {
			if(t instanceof SmartObject) {
				((SmartObject) t).setResource(null, true);
			} else {
				((EObject) t).eAdapters().removeAll(resource.eAdapters());
				((InternalEObject) t).eSetResource(null, null);
			}
		}
		return super.removeAll(c);
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
	public NotificationChain basicRemove(Object object, NotificationChain notifications) {
		remove(object);
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
		return addAll(index, collection);
	}

	@Override
	public T setUnique(int index, T object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void move(int newPosition, T object) {
		move(newPosition, indexOf(object));
	}

	@Override
	public T move(int newPosition, int oldPosition) {
		T t = remove(oldPosition);
		add(newPosition, t);
		return t;
	}
	
	protected void sendAddNotification(EObject obj) {
		for(Adapter a : resource.eAdapters()) {
			a.notifyChanged(SmartEMFNotification.createAddNotification(resource, null, obj, -1));
		}
	}

}
