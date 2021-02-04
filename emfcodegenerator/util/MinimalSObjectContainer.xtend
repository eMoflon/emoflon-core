package emfcodegenerator.util

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature

abstract interface MinimalSObjectContainer {

	/**
	 * the container of this class or null. needed when inspecting references which
	 * have the containment flag set to true
	 */
	//var EObject the_eContainer = null

	/**
	 * the EStructuralFeature which contains this class as an target or null if Containment is false
	 */
	//var EStructuralFeature the_econtaining_feature = null

	/**
	 * Returns the containing object, or null.
	 * An object is contained by another object if it appears in the contents of that object.
	 * The object will be contained by a containment feature of the containing object.
	 */
	abstract def EObject eContainer()

	/**
	 * Description copied from interface: EObject
	 * Returns the particular feature of the container that actually holds the object, or null,
	 * if there is no container. Because of support for wildcard content, this feature may be an
	 * attribute representing a feature map; in this case the object is referenced by the
	 * containment feature of an entry in the map, i.e., the eContainmentFeature.
	 */
	abstract def EStructuralFeature eContainingFeature()

	/**
	 * sets the eContainer and eContainingFeature back to null. Call this method if the object is
	 * in a containment relationship anymore
	 */
	def abstract void reset_containment()

	def abstract void set_containment(EObject container, EStructuralFeature feature)
	
	def abstract boolean is_containment_object()
}