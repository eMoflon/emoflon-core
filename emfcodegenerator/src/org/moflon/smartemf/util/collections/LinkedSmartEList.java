package emfcodegenerator.util.collections;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import emfcodegenerator.notification.SmartEMFNotification;

public class LinkedSmartEList<T> extends SmartCollection<T, LinkedList<T>> {

	public LinkedSmartEList(EObject eContainer, EReference feature) {
		super(eContainer, feature);
	}

	@Override
	protected void initializeCollection(EObject eContainer, EReference feature) {
		elements = new LinkedList<>();
	}

	@Override
	public void move(int newPosition, T object) {
		move(newPosition, elements.indexOf(object));
	}

	@Override
	public T move(int newPosition, int oldPosition) {
		T t = elements.remove(oldPosition);
		elements.add(newPosition, t);
		sendNotification(SmartEMFNotification.createMoveNotification(eContainer, feature, t, newPosition, newPosition));
		return t;
	}

	@Override
	public T get(int index) {
		return elements.get(index);
	}

	@Override
	public T set(int index, T element) {
		T t = elements.set(index, element);
		if(!feature.isContainment()) {
			sendNotification(SmartEMFNotification.createRemoveNotification(eContainer, feature, t, index));
		}
		sendNotification(SmartEMFNotification.createAddNotification(eContainer, feature, element, index));
		return t;
	}

	@Override
	public void add(int index, T element) {
		elements.add(index, element);
		sendNotification(SmartEMFNotification.createAddNotification(eContainer, feature, element, index));
	}

	@Override
	public T remove(int index) {
		T t = elements.remove(index);
		sendNotification(SmartEMFNotification.createRemoveNotification(eContainer, feature, t, index));
		return t;
	}

	@Override
	public int indexOf(Object o) {
		return elements.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return elements.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return elements.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return null;
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return elements.subList(fromIndex, toIndex);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		for(T t : c) {
			elements.add(index, t);
		}
		sendNotification(SmartEMFNotification.createAddManyNotification(eContainer, feature, c, index));
		return true;
	}
}
