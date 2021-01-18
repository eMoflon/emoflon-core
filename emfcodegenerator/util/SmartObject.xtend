package emfcodegenerator.util

import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.common.util.TreeIterator
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EOperation
import java.lang.reflect.InvocationTargetException
import org.eclipse.emf.common.notify.Notification
import org.eclipse.emf.ecore.resource.Resource


/**
 * kind of wrapper. TODO: check if this works
 */
class SmartObject implements EObject{

	/**
	 * EMF EClass runtime instance
	 */
	var EClass e_static_class
	
	/**
	 * constructs a new MinimalSObjectContainer. Expects the EMF runtime model as param
	 * @param EClass e_static_class
	 */
	new(EClass e_static_class){
		this.e_static_class = e_static_class
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
	 * Returns the containing object, or null.
	 * An object is contained by another object if it appears in the contents of that object.
	 * The object will be contained by a containment feature of the containing object.
	 */
	override EObject eContainer(){
		this.e_static_class.eContainer()
	}

	/**
	 * Description copied from interface: EObject
	 * Returns the particular feature of the container that actually holds the object, or null,
	 * if there is no container. Because of support for wildcard content, this feature may be an
	 * attribute representing a feature map; in this case the object is referenced by the
	 * containment feature of an entry in the map, i.e., the eContainmentFeature.
	 */
	override EStructuralFeature eContainingFeature(){
		return this.e_static_class.eContainingFeature()
	}

	/**
	 * Returns the containment feature that properly contains the object, or null, if there is no
	 * container. Because of support for wildcard content, this feature may not be a direct feature
	 * of the container's class, but rather a feature of an entry in a feature map feature of the
	 * container's class.
	 */
	override EReference eContainmentFeature(){
		this.e_static_class.eContainmentFeature()
	}

	/**
	 * returns the direct contents of the EClass
	 */
	override EList<EObject> eContents(){
		return this.e_static_class.eContents()
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

	/**
	 * Returns the value of the given feature of this object.
	 */
	override Object eGet(EStructuralFeature eFeature){
		return this.e_static_class.eGet(eFeature)
	}

	override Object eGet(EStructuralFeature eFeature, boolean resolve){
		return this.e_static_class.eGet(eFeature, resolve)
	}
	
	def Object eGet(int featureID, boolean resolve, boolean coreType){
		return null
	}
	
	override Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException{
		return this.e_static_class.eInvoke(operation, arguments)
	}

	override boolean eIsProxy(){
		return this.e_static_class.eIsProxy()
	}
	
	override boolean eIsSet(EStructuralFeature feature) {
		return this.e_static_class.eIsSet(feature)
	}
	
	override Resource eResource() {
		return this.e_static_class.eResource()
	}
	
	override eSet(EStructuralFeature feature, Object newValue) {
		this.e_static_class.eSet(feature, newValue)
	}
	
	override eUnset(EStructuralFeature feature) {
		this.e_static_class.eUnset(feature)
	}
	
    def void eSet(int feautureID, Object newValue){
		throw new UnsupportedOperationException("This method was not implemented for SmartEMF")
    }


    def void eUnset(int feautureID){
        throw new UnsupportedOperationException("This method was not implemented for SmartEMF")
    }

    def boolean eIsSet(int feautureID){
        throw new UnsupportedOperationException("This method was not implemented for SmartEMF")
    }
	
	override eAdapters() {
		//taken from BasicNotifierImpl
		//BasicEObject.Container extends BasicNotifierImpl and does not override eAdapters
		//return ECollections.emptyEList();
		return this.e_static_class.eAdapters()
	}
	
	override eDeliver() {
		return this.e_static_class.eDeliver()
	}
	
	override eNotify(Notification notification) {
		this.e_static_class.eNotify(notification)
	}
	
	override eSetDeliver(boolean deliver) {
		this.e_static_class.eSetDeliver(deliver)
	}
	
	
}