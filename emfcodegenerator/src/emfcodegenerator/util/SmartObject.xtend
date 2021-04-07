package emfcodegenerator.util

import emfcodegenerator.notification.NotificationList
import emfcodegenerator.notification.SmartEMFNotification
import emfcodegenerator.util.collections.HashESet
import emfcodegenerator.util.collections.LinkedEList
import java.lang.reflect.InvocationTargetException
import java.util.Collection
import java.util.Collections
import java.util.Iterator
import java.util.List
import java.util.function.Function
import java.util.stream.Collector
import org.eclipse.emf.common.notify.Adapter
import org.eclipse.emf.common.notify.Notification
import org.eclipse.emf.common.notify.NotificationChain
import org.eclipse.emf.common.util.DelegatingEList.UnmodifiableEList
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.common.util.TreeIterator
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.ecore.util.InternalEList
import persistence.XtendXMIResource

/**
 * SmartEMF base-class for all generated objects.
 */
class SmartObject implements MinimalSObjectContainer, EObject {

	var EList<Adapter> eAdapters
	var eDeliver = true
	
	/**
	 * constructs a new MinimalSObjectContainer. Expects the EMF runtime model as param
	 * @param EClass e_static_class
	 */
	new(EClass e_static_class){
		this.e_static_class = e_static_class
	}

	/**
	 * Returns the containing object, or null.
	 * An object is contained by another object if it appears in the contents of that object.
	 * The object will be contained by a containment feature of the containing object.
	 */
	override EObject eContainer(){
		this.the_eContainer
	}
	
	/**
	 * Description copied from interface: EObject
	 * Returns the particular feature of the container that actually holds the object, or null,
	 * if there is no container. Because of support for wildcard content, this feature may be an
	 * attribute representing a feature map; in this case the object is referenced by the
	 * containment feature of an entry in the map, i.e., the eContainmentFeature.
	 */
	override EStructuralFeature eContainingFeature(){
		return this.the_econtaining_feature
	}

	/**
	 * gets the EMF EClass model instance
	 */
	def EClass eStaticClass(){
		return e_static_class
	}

	/**
	 * returns the FeatureCount
	 * @return int
	 */
	def protected int eStaticFeatureCount(){
    	return eStaticClass().getFeatureCount()
  	}

	/**
	 * returns the meta class
	 */
	override EClass eClass(){
		return e_static_class
	}

	/**
	 * returns an iterator iterating over all contents of the class
	 */
	override TreeIterator<EObject> eAllContents(){
		EcoreUtil.getAllContents(Collections.singleton(this))
	}

	/**
	 * Returns the containment feature that properly contains the object, or null, if there is no
	 * container. Because of support for wildcard content, this feature may not be a direct feature
	 * of the container's class, but rather a feature of an entry in a feature map feature of the
	 * container's class.
	 */
	override EReference eContainmentFeature(){
		throw new UnsupportedOperationException("This operation is not permitted. kindly use eContainingFeature() instead.")
	}

	/**
	 * returns the direct contents of the EClass
	 */
	override EList<EObject> eContents(){
		val containments = eClass.getEAllContainments
		return containments.stream.map[x | toContentList(x)].collect(toChainingEList())
	}
		
	def Collector<List<EObject>, ?, EList<EObject>> toChainingEList() {
		return new Collector<List<EObject>, EList<EObject>, EList<EObject>>(){
			
			override accumulator() {
				[a, b | a.addAll(b)]
			}
			
			override characteristics() {
				Collections.singleton(Collector.Characteristics.IDENTITY_FINISH)
			}
			
			override combiner() {
				[a, b | a.addAll(b); a]
			}
			
			override finisher() {
				Function.identity
			}
			
			override supplier() {
				[new LinkedEList()]
			}
			
		}
	}
		
	private def List<EObject> toContentList(EReference reference) {
		val obj = eGet(reference);
		if (obj instanceof EList) {
			obj as EList<EObject>
		} else Collections.singletonList(obj as EObject)
	}

	/**
	 * Returns a list view of the cross referenced objects; it is unmodifiable.
	 * This will be the list of EObjects determined by the contents of the reference features of
	 * this object's meta class, excluding containment features and their opposites.
	 */
	override EList<EObject> eCrossReferences(){
		val crossrefs = eClass.getEAllReferences.stream
			.filter[x | !x.isContainment && !x.isContainer]
			.map[x | toContentList(x)]
			.collect(toChainingEList())
		return new UnmodifiableEList(crossrefs)
	}

	override Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException {
		throw new UnsupportedOperationException()
	}

	/**
	 * supposed to return true if the current object is a proxy. As all model instances are not a proxy and all SmartObject instances are model
	 * instances, false is always returned.
	 * @return boolean
	 */
	override boolean eIsProxy(){
		return false
	}

	/**
	 * Returns the containing resource, or null.
	 * An object is contained in a resource if it, or one of it's containers, appears in the contents of that resource.
	 * An object must be contained by a resource in order to be serialized.
	 */
	override eResource() {
		return resource ?: if (eContainer !== null) eContainer.eResource else null;
	}
	
	/**
	 * @return this object's resource if it is a top-level object, {@code null} otherwise
	 */
	def eDirectResource() {
		resource
	}
	
	/**
	 * Returns the value of the given feature of this object.
	 */
	override Object eGet(EStructuralFeature eFeature){
		return null
	}

	override Object eGet(EStructuralFeature eFeature, boolean resolve){
		throw new UnsupportedOperationException("Feature not implemented")
	}
	
	def Object eGet(int featureID, boolean resolve, boolean coreType){
		throw new UnsupportedOperationException("Feature not implemented")
	}
	
	def void eSet(int featureID, Object newValue){
		throw new UnsupportedOperationException("Seems as the feature has not been registered.")
	}

	override eSet(EStructuralFeature feature, Object newValue) {
		throw new UnsupportedOperationException("Feature not implemented")
	}
	
	override eUnset(EStructuralFeature feature) {
		this.e_static_class.eUnset(feature)
	}

	def void eUnset(int featureID){
        	throw new UnsupportedOperationException("Seems as the feature has not been registered.")
    	}

    	def boolean eIsSet(int featureID){
    	    throw new UnsupportedOperationException("Seems as the feature has not been registered.")
    	}
		
	override boolean eIsSet(EStructuralFeature feature) {
		throw new RuntimeException("Feature might not be registered....")
//		System.out.println(feature.getName());
//		return false;
	}

	override eAdapters() {
		return eAdapters ?: {eAdapters = new HashESet(this); eAdapters}
	}
	
	override eDeliver() {
		return eDeliver
	}
	
	override eNotify(Notification n) {
		val chain = cascadeNotifications(n)
		if (eNotificationRequired) for (Adapter a : eAdapters) {
			a.notifyChanged(n)
		}
		chain.dispatch
	}
	
	override eSetDeliver(boolean deliver) {
		this.eDeliver = deliver
	}
	
	/**
	 * Analogous to and adapted from BasicEObjectImpl.eSetResource
	 */
	def eSetResource(Resource.Internal r, NotificationChain n) {
		val oldR = eResource() as Resource.Internal
		var NotificationChain chain = n
		
		if (oldR !== null && r !== null) {
			chain = (r.contents as InternalEList<?>).basicRemove(this, chain)
			oldR.detached(this)
		}
		if (eContainer !== null) {
			val ref = eContainer.eGet(eContainingFeature)
			if (ref instanceof EList) {
				ref.remove(this)
			} else {
				eContainer.eUnset(eContainingFeature)
			}
		}
		
		return chain
	}
	
	/**
	 * Analogous to BasicNotifierImpl.eNotificationRequired
	 * @return true when eDeliver is true and there is at least one adapter
	 */
	def eNotificationRequired() {
		!(eAdapters ?: Collections.emptyList).isEmpty && eDeliver
	}
	
	protected def NotificationChain cascadeNotifications(Notification n) {
		if (!getCascade() && #[Notification.ADD, Notification.ADD_MANY].contains(n.eventType)) {
			return new NotificationList(n)
		}
		val chain = if (n instanceof NotificationChain) {
			n
		} else {
			new NotificationList(n)
		}
		switch (n.eventType) {
			case Notification.ADD, case Notification.REMOVE, case Notification.SET, case Notification.UNSET: {
				val eobj = if (#[Notification.REMOVE, Notification.UNSET].contains(n.eventType)) {
					n.oldValue
				} else {
					n.newValue
				}
				if (eobj === null || !(eobj instanceof EObject)) return chain
				val iter = (eobj as EObject).eAllContents
				if (iter.hasNext) {
					chain.add(childNotifications(iter, n.eventType))
				} else {
					return chain
				}
			}
			case Notification.ADD_MANY, case Notification.REMOVE_MANY: {
				val list = (if (n.eventType == Notification.ADD_MANY) {
					n.newValue
				} else {
					n.oldValue
				}) as Collection<EObject>
				if (list === null || list.isEmpty) return chain
				for (eobj : list) {
					val iter = (eobj as EObject).eAllContents
					chain.add(childNotifications(iter, n.eventType))
				}
			}
		}
		return chain
	}
		
	/**
	 * @return if this object should generate cascaded add-notifications
	 */
	def boolean getCascade() {
		val res = eResource
		if (res !== null && res instanceof XtendXMIResource) {
			return (res as XtendXMIResource).getCascade
		} else {
			return false
		}	
	}
	
	private def Notification childNotifications(Iterator<EObject> children, int eventType) {
		var SmartEMFNotification notification = null
		while (children.hasNext) {
			val content = children.next as EObject
			val container = content.eContainer
			val feature = content.eContainingFeature
			val index = -1 //iterating over the list to find the index is slow - only implement if actually needed
			switch (eventType) {
				case Notification.ADD, case Notification.ADD_MANY, case Notification.SET: {
					if (notification === null) {
						notification = SmartEMFNotification.addToFeature(container, feature, content, index)
					} else {
						notification.add(SmartEMFNotification.addToFeature(container, feature, content, index))
					}
				}
				case Notification.REMOVE, case Notification.REMOVE_MANY, case Notification.UNSET: {
					if (notification === null) {
						notification = SmartEMFNotification.removeFromFeature(container, feature, content, index)
					} else {
						notification.add(SmartEMFNotification.removeFromFeature(container, feature, content, index))
					}
				}
			}
		}
		return notification
	}

	/**########################MinimalSObjectContainer########################*/
	
	
	/**
	 * EMF EClass model instance
	 */
	var EClass e_static_class

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
	 * stores true if this object is part of a containment relationship
	 */
	var boolean is_containment_object = false
		
	/**
	 * the resource that directly contains this object 
	 */
	var	Resource resource

	override is_containment_object() {
		this.is_containment_object
	}

	/**
	 * sets the eContainer and eContainingFeature back to null. Call this method if the object is
	 * not in a containment relationship anymore
	 */
	override void reset_containment(){
		this.is_containment_object = false
		
		if(!this.the_econtaining_feature.isMany){
			this.the_eContainer.eUnset(this.the_econtaining_feature)
		} /* else {
			var EList<?> the_list = (this.the_eContainer.eGet(this.the_econtaining_feature) as EList<?>)
			while(the_list.contains(this)) the_list.remove(this)
		} */
		
		this.the_eContainer = null
		this.the_econtaining_feature = null
	}

	override void set_containment(EObject container, EStructuralFeature feature){
		if(this.the_eContainer !== null) this.reset_containment()
		
		this.is_containment_object = true
		this.the_eContainer = container
		this.the_econtaining_feature = feature;
		
		if (!eContainingFeature.isMany) {
			eContainer.eSet(eContainingFeature, this)
		}
	}
	
	static def toStringIfNotNull(Object obj) {
		(obj ?: "null").toString
	}
	
}
