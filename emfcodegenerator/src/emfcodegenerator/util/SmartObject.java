package emfcodegenerator.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import emfcodegenerator.util.collections.DefaultSmartEList;
import emfcodegenerator.util.collections.HashSmartESet;
import emfcodegenerator.util.collections.LinkedSmartEList;

public abstract class SmartObject implements MinimalSObjectContainer, EObject {

	private Resource resource;
	private EObject eContainer;
	private EStructuralFeature eContainingFeature;
	private EClass staticClass;
	
	public SmartObject(EClass staticClass) {
		this.staticClass = staticClass;
	}
	
	@Override
	/**
	 * returns the adapters of the containing resource
	 */
	public EList<Adapter> eAdapters() {
		return eResource().eAdapters();
	}

	@Override
	/**
	 * returns whether message should be delivered, which is redirected to the containing resource
	 */
	public boolean eDeliver() {
		return eResource().eDeliver();
	}

	@Override
	/**
	 * sets deliver within the containing resource
	 */
	public void eSetDeliver(boolean deliver) {
		eResource().eSetDeliver(deliver);
	}

	@Override
	public void eNotify(Notification notification) {
		
	}

	@Override
	public EClass eClass() {
		return staticClass;
	}

	@Override
	public Resource eResource() {
		return resource;
	}

	@Override
	public EReference eContainmentFeature() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public EList<EObject> eContents() {
		EList<EObject> contents = new DefaultSmartEList<>();
		for(EReference ref : staticClass.getEAllContainments()) {
			if(ref.isMany()) {
				contents.addAll((Collection<? extends EObject>) eGet(ref));
			}
			else {
				contents.add((EObject) eGet(ref));
			}
		}
		return contents;
	}

	@Override
	public TreeIterator<EObject> eAllContents() {
		return EcoreUtil.getAllContents(Collections.singleton(this))
;
	}

	@Override
	public boolean eIsProxy() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EList<EObject> eCrossReferences() {
		EList<EObject> contents = new DefaultSmartEList<EObject>();
		for(EReference ref : staticClass.getEAllReferences()) {
			if(ref.isContainment())
				continue;
			
			if(ref.isMany()) {
				contents.addAll((Collection<? extends EObject>) eGet(ref));
			}
			else {
				contents.add((EObject) eGet(ref));
			}
		}
		return contents;
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
		return eGet(feature);
	}

	@Override
	public boolean eIsSet(EStructuralFeature feature) {
		Object obj = eGet(feature);
		if(feature.isMany()) {
			return !((Collection<?>) obj).isEmpty();
		}
		else {
			return obj != null;
		}
	}

	@Override
	public Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException {
		throw new UnsupportedOperationException("Invocations of EOperations are not supported");
	}

	@Override
	public EObject eContainer() {
		return eContainer;
	}

	@Override
	public EStructuralFeature eContainingFeature() {
		return eContainingFeature;
	}

	@Override
	/**
	 * revokes current containment relationship
	 */
	public void resetContainment() {
		if(eContainingFeature.isMany()) {
			((Collection<?>) eContainer.eGet(eContainingFeature)).remove(this);
		}
		else {
			eContainer.eUnset(eContainingFeature);
		}
		eContainer = null;
		eContainingFeature = null;
	}

	@Override
	public void setContainment(EObject eContainer, EStructuralFeature feature) {
		this.eContainer = eContainer;
		this.eContainingFeature = feature;
	}

	@Override
	public boolean isContainmentObject() {
		return eContainer != null;
	}
	
	protected void sendNotification(Notification n) {
		if(resource != null) {
			for(Adapter a : resource.eAdapters()) {
				a.notifyChanged(n);
			}
		}
	}
}
