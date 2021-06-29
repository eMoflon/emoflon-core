package org.moflon.smartemf.runtime;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

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
import org.moflon.smartemf.runtime.notification.NotifyStatus;
import org.moflon.smartemf.runtime.notification.SmartEMFNotification;

public abstract class SmartObject implements MinimalSObjectContainer, InternalEObject {

	private Internal resource;
	private EObject eContainer;
	private EStructuralFeature eContainingFeature;
	private EClass staticClass;
	private URI proxyUri;
	
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
		if(resource instanceof SmartEMFResource)
			return (SmartEMFResource) resource;
		return null;
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
	public NotifyStatus resetContainment() {
		// if there is no eContainer, then this element is only contained within the resource and should be removed
		if(eContainer == null && resource != null) {
			resource.getContents().remove(this);
		}
		
		NotifyStatus status = setResource(null, true);
		if(eContainingFeature == null)
			return status;
		
//		EObject oldContainer = eContainer;
//		EStructuralFeature oldFeature = eContainingFeature;
		
		eContainer = null;
		eContainingFeature = null;
		
//		if(oldFeature.isMany()) {
//			if(((Collection<?>) oldContainer.eGet(oldFeature)).remove(this)) {
//				return NotifyStatus.SUCCESS_NOTIFICATION_SEND;
//			} else {
//				return NotifyStatus.FAILURE_NO_NOTIFICATION;
//			}
//		}
//		else {
////			oldContainer.eUnset(oldFeature);
//		}

		return status;
	}

	@Override
	/**
	 * sets new containment and cleans up the old
	 */
	public NotifyStatus setContainment(EObject eContainer, EStructuralFeature feature) {
		if(this.eContainer == null && eContainer == null)
			return NotifyStatus.SUCCESS_NOTIFICATION_SEND;
		if(this.eContainer != null && this.eContainer.equals(eContainer) && this.eContainingFeature.equals(feature))
			return NotifyStatus.SUCCESS_NOTIFICATION_SEND;
		
		NotifyStatus status = NotifyStatus.FAILURE_NO_NOTIFICATION;
		// clean up old containment
		// we don't use resetContainment here to optimize the number of generated notifications
		if(this.eContainer != null) {
			if(eContainingFeature.isMany()) {
				((Collection<?>) this.eContainer.eGet(eContainingFeature)).remove(this);
			}
			else {
				this.eContainer.eUnset(eContainingFeature);
			}
			status = NotifyStatus.SUCCESS_NOTIFICATION_SEND;
		}
		else {
			// if there is no eContainer, then this element is only contained within the resource and should be removed before setting the new eContainer
			if(resource != null) {
				resource.getContents().remove(this);
			}
			status = NotifyStatus.SUCCESS_NO_NOTIFICATION;
		}
		
		this.eContainer = eContainer;
		this.eContainingFeature = feature;
		status = setResource(eContainer.eResource(), true);
		return status;
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
	
	public NotifyStatus setResource(Resource resource, boolean sendNotification) {
		// stop if we encounter the same resource already
		if(eResource() == null && resource == null) 
	    	return NotifyStatus.SUCCESS_NO_NOTIFICATION;
	    
	    if(eResource() != null && eResource().equals(resource))
	    	return NotifyStatus.SUCCESS_NO_NOTIFICATION;
			
		// send remove messages to old adapters
		// TODO lfritsche: should we optimize this and only do this if the adapters of both resources differ?
		sendRemoveAdapterNotification(this);
		
		this.resource = (Internal) resource;
		
		Consumer<SmartObject> setResourceCall = (o) -> o.setResource(resource, true);
		
		NotifyStatus status = NotifyStatus.SUCCESS_NO_NOTIFICATION;
		if(resource != null) {
			if(sendNotification) {
    			// if container is null, then this element is a root element within a resource and notifications are handled there
    			if(eContainer() == null) {
					sendNotification(SmartEMFNotification.createAddNotification(resource, null, this, -1));
					status = NotifyStatus.SUCCESS_NOTIFICATION_SEND;
				}
				else
					if(eContainingFeature().isMany()) {
						sendNotification(SmartEMFNotification.createAddNotification(eContainer(), eContainingFeature(), this, -1));
						status = NotifyStatus.SUCCESS_NOTIFICATION_SEND;
					}

			}
			
			// if cascading is activated, we recursively generate add messages; else just this once
			SmartEMFResource smartResource = smartResource();
			if(smartResource == null ||  smartResource.getCascade())
				setResourceCall = (o) -> o.setResourceSilently(resource);
		}
		
		setResourceOfContainments(setResourceCall);
		
		return status;
	}
	
	protected abstract void setResourceOfContainments(Consumer<SmartObject> setResourceCall);
	
    public void setResourceSilently(Resource r) {
    	// stop if we encounter the same resource already
		if(eResource() == null && r == null) 
	    	return;
	    
	    if(eResource() != null && eResource().equals(r))
	    	return;
			
		// send remove messages to old adapters
		// TODO lfritsche: should we optimize this and only do this if the adapters of both resources differ?
		sendRemoveAdapterNotification(this);
			
		setResource(r, true);
		
		setResourceOfContainmentsSilently(r);
    }
    
    protected abstract void setResourceOfContainmentsSilently(Resource r);
	
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
		return new Setting() {
			
			@Override
			public void unset() {
				eUnset(feature);
			}
			
			@Override
			public void set(Object newValue) {
				eSet(feature, newValue);
			}
			
			@Override
			public boolean isSet() {
				return true;
			}
			
			@Override
			public EStructuralFeature getEStructuralFeature() {
				return feature;
			}
			
			@Override
			public EObject getEObject() {
				return (EObject) this;
			}
			
			@Override
			public Object get(boolean resolve) {
				return eGet(feature);
			}
		};
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
		setResource(resource, true);
		return notifications;
	}

	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class<?> baseClass,
			NotificationChain notifications) {
		throw new UnsupportedOperationException("Unsupported by SmartEMF");
	}
	
	public abstract void eInverseAdd(Object otherEnd, EStructuralFeature feature);
	
	public abstract void eInverseRemove(Object otherEnd, EStructuralFeature feature);

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
		return proxyUri;
	}

	@Override
	public void eSetProxyURI(URI uri) {
		this.proxyUri = uri;
	}

	@Override
	public boolean eIsProxy() {
		return proxyUri != null;
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
