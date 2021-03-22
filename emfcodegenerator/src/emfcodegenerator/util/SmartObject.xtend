package emfcodegenerator.util

import com.google.common.collect.Sets
import emfcodegenerator.util.collections.LinkedEList
import java.lang.reflect.InvocationTargetException
import java.util.Collections
import java.util.List
import java.util.function.Function
import java.util.stream.Collector
import org.eclipse.emf.common.notify.Adapter
import org.eclipse.emf.common.notify.Notification
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.common.util.TreeIterator
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.resource.Resource

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
		return this.e_static_class.eAllContents
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
		val containments = eClass().getEAllContainments
		return containments.stream().map([x | toContentList(x)]).collect(toChainingEList())
	}
		
	def Collector<List<EObject>, ?, EList<EObject>> toChainingEList() {
		return new Collector<List<EObject>, EList<EObject>, EList<EObject>>(){
			
			override accumulator() {
				[a, b | a.addAll(b)]
			}
			
			override characteristics() {
				Sets.immutableEnumSet(Collector.Characteristics.CONCURRENT, Collector.Characteristics.IDENTITY_FINISH)
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
	 * The cross reference list's iterator will be of type EContentsEList.FeatureIterator,
	 * for efficient determination of the feature of each cross reference in the list
	 */
	override EList<EObject> eCrossReferences(){
		return this.e_static_class.eCrossReferences()
	}

	override Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException{
		throw new UnsupportedOperationException()
	}

	override boolean eIsProxy(){
		return this.e_static_class.eIsProxy()
	}

	override Resource eResource() {
		return this.e_static_class.eResource()
	}
	
	/**
	 * Returns the value of the given feature of this object.
	 */
	override Object eGet(EStructuralFeature eFeature){
		return null
	}

	override Object eGet(EStructuralFeature eFeature, boolean resolve){
		return this.eGet(eFeature)
	}
	
	def Object eGet(int featureID, boolean resolve, boolean coreType){
		return null
	}
	
    def void eSet(int featureID, Object newValue){
		throw new UnsupportedOperationException("Seems as the feature has not been registered.")
    }

	override eSet(EStructuralFeature feature, Object newValue) {
		this.e_static_class.eSet(feature, newValue)
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
//		throw new RuntimeException("Feature might not be registered....")
		System.out.println(feature.getName());
		return false;
	}

	override eAdapters() {
		return eAdapters ?: {eAdapters = new LinkedEList(this); eAdapters}
	}
	
	override eDeliver() {
		return eDeliver
	}
	
	override eNotify(Notification notification) {
		if (eNotificationRequired) for (Adapter a : eAdapters) {
			a.notifyChanged(notification)
		}
	}
	
	override eSetDeliver(boolean deliver) {
		this.eDeliver = deliver
	}
	
	/**
	 * Analogous to BasicNotifierImpl.eNotificationRequired
	 * @return true when eDeliver is true and there is at least one adapter
	 */
	def eNotificationRequired() {
		!(eAdapters ?: Collections.emptyList).isEmpty && eDeliver
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

	override is_containment_object() {
		this.is_containment_object
	}

	/**
	 * sets the eContainer and eContainingFeature back to null. Call this method if the object is
	 * not in a containment relationship anymore
	 * TODO: eNotification support ?
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

	/**
	 * TODO: eNotification support ?
	 */
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