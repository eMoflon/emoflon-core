package emfcodegenerator.util.collections

import org.eclipse.emf.common.util.EList
import emfcodegenerator.util.MinimalSObjectContainer

/**
 * This interface defines which features a Collection for SmartEMF must implement.
 * TODO: eNotification support
 * @author Adrian Zwenger
 */
package interface MinimalSObjectContainerCollection<E> extends EList<E>, MinimalSObjectContainer {

	/**
	 * Sets the containment of an passed object to the same values as the
	 * MinimalSObjectContainerCollection.
	 */
	def E set_containment_to_passed_object(E obj)

	/**
	 * Removes the containment flags from an passed Object if this MinimalSObjectContainerCollection
	 * is in an containment relationship.
	 * Not to be called by a regular user.
	 */
	def E remove_containment_to_passed_object(E obj)
}