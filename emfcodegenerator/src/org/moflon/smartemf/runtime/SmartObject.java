package org.moflon.smartemf.runtime;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moflon.smartemf.persistence.SmartEMFResource;
import org.moflon.smartemf.runtime.collections.DefaultSmartEList;
import org.moflon.smartemf.runtime.notification.SmartEMFNotification;

public abstract class SmartObject implements MinimalSObjectContainer, InternalEObject {

	private SmartEMFResource resource;
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
		if(resource == null)
			return new DefaultSmartEList<>();
		else
			return resource.eAdapters();
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

	public SmartEMFResource smartResource() {
		return resource;
	}
	
	@Override
	public EReference eContainmentFeature() {
		return (EReference) eContainingFeature;
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
				Object obj = eGet(ref);
				if(obj != null)
					contents.add((EObject) obj);
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
		setResource(null);
		if(eContainingFeature == null)
			return;
		
		EObject oldContainer = eContainer;
		EStructuralFeature oldFeature = eContainingFeature;
		
		eContainer = null;
		eContainingFeature = null;
		
		if(oldFeature.isMany()) {
			((Collection<?>) oldContainer.eGet(oldFeature)).remove(this);
		}
		else {
			oldContainer.eUnset(oldFeature);
		}

	}

	@Override
	/**
	 * sets new containment and cleans up the old
	 */
	public void setContainment(EObject eContainer, EStructuralFeature feature) {
		// clean up old containment
		// we don't use resetContainment here to optimize the number of generated notifications
		if(this.eContainer != null) {
			if(eContainingFeature.isMany()) {
				((Collection<?>) this.eContainer.eGet(eContainingFeature)).remove(this);
			}
			else {
				this.eContainer.eUnset(eContainingFeature);
			}
		}
		
		this.eContainer = eContainer;
		this.eContainingFeature = feature;
		setResource(eContainer.eResource());
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
	
	protected void sendRemoveAdapterNotification(EObject obj) {
		if(resource != null) {
			for(Adapter a : resource.eAdapters()) {
				a.notifyChanged(SmartEMFNotification.createRemovingAdapterNotification(this, null, a, -1));
			}
		}
	}
	
	public void setResource(Resource resource) {
		this.resource = (SmartEMFResource) resource;
	}
	
    public abstract void setResourceSilently(Resource r);
	
    public abstract Object eGet(int featureID, boolean resolve, boolean coreType);

	@Override
	public boolean eNotificationRequired() {
		return true;
	}

	@Override
	public String eURIFragmentSegment(EStructuralFeature eFeature, EObject eObject) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public EObject eObjectForURIFragmentSegment(String uriFragmentSegment) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public void eSetClass(EClass eClass) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public Setting eSetting(EStructuralFeature feature) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public int eContainerFeatureID() {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public int eDerivedOperationID(int baseOperationID, Class<?> baseClass) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public NotificationChain eSetResource(Internal resource, NotificationChain notifications) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class<?> baseClass,
			NotificationChain notifications) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");

	}

	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class<?> baseClass,
			NotificationChain notifications) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");

	}

	@Override
	public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID,
			NotificationChain notifications) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public NotificationChain eBasicRemoveFromContainer(NotificationChain notifications) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public URI eProxyURI() {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public void eSetProxyURI(URI uri) {
	}

	@Override
	public EObject eResolveProxy(InternalEObject proxy) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public InternalEObject eInternalContainer() {
		return (InternalEObject) eContainer;
	}

	@Override
	public Internal eInternalResource() {
		return resource;
	}

	@Override
	public Internal eDirectResource() {
		return resource;
	}

	@Override
	public EStore eStore() {
		return null;
	}

	@Override
	public void eSetStore(EStore store) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public void eSet(int featureID, Object newValue) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public void eUnset(int featureID) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public boolean eIsSet(int featureID) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}

	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}
	
	@Override
	public Object eGet(EStructuralFeature eFeature, boolean resolve, boolean coreType) {
		return eGet(eFeature);
	}
}
