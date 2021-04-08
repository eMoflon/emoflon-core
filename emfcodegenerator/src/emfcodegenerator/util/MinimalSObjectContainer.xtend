package emfcodegenerator.util

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature

/**
 * Interface which all SmartEMF classes which need to support the containment-feature must implement
 */
abstract interface MinimalSObjectContainer {

	/**
	 * Returns the containing object, or null.
	 * An object is contained by another object if it appears in the contents of that object.
	 * The object will be contained by a containment feature of the containing object.
	 * @return EObject which contains this MinimalSObjectContainer
	 */
	abstract def EObject eContainer()

	/**
	 * Description copied from interface: EObject
	 * Returns the particular feature of the container that actually holds the object, or null,
	 * if there is no container. Because of support for wildcard content, this feature may be an
	 * attribute representing a feature map; in this case the object is referenced by the
	 * containment feature of an entry in the map, i.e., the eContainmentFeature.
	 * @return EStructuralFeature
	 */
	abstract def EStructuralFeature eContainingFeature()

	/**
	 * sets the eContainer and eContainingFeature back to null. Call this method if the object is
	 * not in a containment relationship anymore
	 * @author Adrian Zwenger
	 */
	def abstract void reset_containment()

	/**
	 * Sets the containment to the newly given arguements.
	 * @param containter EObject
	 * @param feature EStructuralFeature
	 * @author Adrian Zwenger
	 */
	def abstract void set_containment(EObject container, EStructuralFeature feature)
	
	/**
	 * returns true if this object is currently in a containment relationship
	 * @param boolean
	 * @author Adrian Zwenger
	 */
	def abstract boolean is_containment_object()
}
