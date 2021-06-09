package emfcodegenerator.notification

import emfcodegenerator.util.collections.LinkedEList
import java.util.Collection
import java.util.Collections
import org.eclipse.emf.common.notify.Adapter
import org.eclipse.emf.common.notify.Notification
import org.eclipse.emf.common.notify.NotificationChain
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.common.util.EList

/**
 * This class implements the {@link Notification} and {@link NotificationChainWorkaround} interfaces.
 * All notifications that the generated classes send are of this type.
 * It supports notification merging and chaining.
 */
class SmartEMFNotification2 implements Notification {
	
	int eventType	
	int position
	Object oldValue	
	Object newValue
	EObject notifier
	EStructuralFeature feature
	NotificationChain next
	
	
	new(int eventType, Object oldValue, Object newValue) {
		this.eventType = eventType
		this.oldValue = oldValue
		this.newValue = newValue
	}
	
	new(Notification n) {
		this.eventType = n.eventType
		this.position = n.position
		this.oldValue = n.oldValue
		this.newValue = n.newValue
		this.notifier = n.notifier as EObject
		this.feature = n.feature as EStructuralFeature
	}
	
	/**
	 * Creates a SmartEMFNotification that represents an event where an object is added to a collection.
	 * @param owner the object that contains the collection
	 * @param feature the feature that the collection represents
	 * @param object the objects that is added to the collection
	 * @param index the object's position after adding
	 */
	def static addToFeature(EObject owner, EStructuralFeature feature, Object object, int index) {
		if (feature === null) return null
		val notification = new SmartEMFNotification(ADD, null, object)
		notification.notifier = owner
		notification.feature = feature
		notification.position = index
		return notification
	}
	
	/**
	 * Creates a SmartEMFNotification that represents an event where an attribute is set to a value.
	 * @param owner the object that contains the attribute
	 * @param feature the attribute
	 * @param oldValue the attribute's old value
	 * @param newValue the attribute's new value
	 * @param index if the attribute is a list, the position where in the list the value was set
	 */
	def static set(EObject owner, EStructuralFeature feature, Object oldValue, Object newValue, int index) {
		val notification = new SmartEMFNotification(SET, oldValue, newValue)
		notification.notifier = owner
		notification.feature = feature
		notification.position = index
		return notification
	}
	
	def static unset(EObject owner, EStructuralFeature feature, Object oldValue, Object newValue, int index) {
		val notification = new SmartEMFNotification(UNSET, oldValue, newValue)
		notification.notifier = owner
		notification.feature = feature
		notification.position = index
		return notification
	}
	
	/**
	 * Creates a SmartEMFNotification that represents an event where an object is removed from a collection.
	 * @param owner the object that contains the collection
	 * @param feature the feature that the collection represents
	 * @param object the objects that is removed from the collection
	 * @param index the object's position before removal
	 */
	def static removeFromFeature(EObject owner, EStructuralFeature feature, Object object, int index) {
		val notification = if (object instanceof Adapter && feature === null) {
			if (object instanceof Adapter.Internal) {
				object.unsetTarget(owner)
			}
			new SmartEMFNotification(REMOVING_ADAPTER, object, null)
		} else {
			new SmartEMFNotification(REMOVE, object, null)
		}
		notification.notifier = owner
		notification.feature = feature
		notification.position = index
		return notification
	}
	
	/**
	 * Creates a SmartEMFNotification that represents an event where an object is moved within a list.
	 * @param owner the object that contains the collection
	 * @param feature the feature that the collection represents
	 * @param object the objects that is moved within the collection
	 * @param oldIndex the object's old position
	 * @param newIndex the object's new position
	 */
	def static moveInList(EObject owner, EStructuralFeature feature, Object object, int oldIndex, int newIndex) {
		val notification = new SmartEMFNotification(MOVE, oldIndex, object)
		notification.notifier = owner
		notification.feature = feature
		notification.position = newIndex
		return notification
	}
	
	/**
	 * Creates a SmartEMFNotification from a {@link NotificationList}. Individual notifications may be merged.
	 * @param c the notification list
	 */
	def static SmartEMFNotification chainToNotification(NotificationList c) {
		val iter = c.iterator
		if (!iter.hasNext) return null
		val first = iter.next
		val notification = new SmartEMFNotification(first)
		while (iter.hasNext) {
			notification.add(new SmartEMFNotification(iter.next))
		}
		return notification
	}
	
	/**
	 * Creates a SmartEMFNotification that represents an event where many objects are added to a collection.
	 * @param owner the object that contains the collection
	 * @param feature the feature that the collection represents
	 * @param objects the objects that are added to the collection
	 */
	def static addMany(EObject owner, EStructuralFeature feature, EList<?> objects) {
		if (objects.size == 0) return null;
		if (objects.size == 1) return addToFeature(owner, feature, objects.iterator.next as Object, NO_INDEX)
		
		val notification = new SmartEMFNotification(ADD_MANY, null, objects)
		notification.notifier = owner
		notification.feature = feature
		notification.position = NO_INDEX
		return notification
	}
	
	/**
	 * Creates a SmartEMFNotification that represents an event where many objects are removed from a collection.
	 * @param owner the object that contains the collection
	 * @param feature the feature that the collection represents
	 * @param objects the objects that are removed from the collection
	 */
	def static removeMany(EObject owner, EStructuralFeature feature, EList<?> objects) {
		if (objects.size == 0) return null;
		if (objects.size == 1) return removeFromFeature(owner, feature, objects.iterator.next as Object, NO_INDEX)
		
		val int[] arr = Collections.nCopies(objects.size, -1)
		val notification = new SmartEMFNotification(REMOVE_MANY, objects, arr)
		notification.notifier = owner
		notification.feature = feature
		notification.position = NO_INDEX
		return notification
	}
	
	override getFeature() {
		this.feature
	}
	
	def isFeatureUnsettable() {
		feature.isUnsettable
	}
	
	def getFeatureDefaultValue() {
		feature.getDefaultValue
	}
	
	override getEventType() {
		eventType
	}
	
	/* This may not be implemented correctly, but it seems to work for now */
	override getFeatureID(Class<?> expectedClass) {
		if (feature.getEContainingClass.getClass == expectedClass) {
			feature.getFeatureID
		} else {
			Notification.NO_FEATURE_ID
		}
	}
	
	/**
	 * @return the new value as a primitive boolean value
	 * @throws ClassCastException if the new value is not a {@link Boolean}.
	 */
	override getNewBooleanValue() {
		newValue as Boolean
	}
	
	/**
	 * @return the new value as a primitive byte value
	 * @throws ClassCastException if the new value is not a {@link Byte}.
	 */
	override getNewByteValue() {
		newValue as Byte
	}
	
	/**
	 * @return the new value as a primitive char value
	 * @throws ClassCastException if the new value is not a {@link Character}.
	 */
	override getNewCharValue() {
		newValue as Character
	}
	
	/**
	 * @return the new value as a primitive double value
	 * @throws ClassCastException if the new value is not a {@link Double}.
	 */
	override getNewDoubleValue() {
		newValue as Double
	}
	
	/**
	 * @return the new value as a primitive float value
	 * @throws ClassCastException if the new value is not a {@link Float}.
	 */
	override getNewFloatValue() {
		newValue as Float
	}
	
	/**
	 * @return the new value as a primitive int value
	 * @throws ClassCastException if the new value is not an {@link Integer}.
	 */
	override getNewIntValue() {
		newValue as Integer
	}
	
	/**
	 * @return the new value as a primitive long value
	 * @throws ClassCastException if the new value is not a {@link Long}.
	 */
	override getNewLongValue() {
		newValue as Long
	}
	
	/**
	 * @return the new value as a primitive short value
	 * @throws ClassCastException if the new value is not a {@link Short}.
	 */
	override getNewShortValue() {
		newValue as Short
	}
	
	/**
	 * @return the new value as a string
	 * @throws ClassCastException if the new value is not a {@link String}.
	 */
	override getNewStringValue() {
		newValue as String
	}
	
	/**
	 * @return the new value
	 */
	override getNewValue() {
		newValue
	}
	
	/**
	 * @return the object affected by the change
	 */
	override getNotifier() {
		notifier
	}
	
	/**
	 * @return the old value as a primitive boolean value
	 * @throws ClassCastException if the old value is not a {@link Boolean}.
	 */
	override getOldBooleanValue() {
		oldValue as Boolean
	}
	
	/**
	 * @return the old value as a primitive byte value
	 * @throws ClassCastException if the old value is not a {@link Byte}.
	 */
	override getOldByteValue() {
		oldValue as Byte
	}
	
	/**
	 * @return the old value as a primitive char value
	 * @throws ClassCastException if the old value is not a {@link Character}.
	 */
	override getOldCharValue() {
		oldValue as Character
	}
	
	/**
	 * @return the old value as a primitive double value
	 * @throws ClassCastException if the old value is not a {@link Double}.
	 */
	override getOldDoubleValue() {
		oldValue as Double
	}
	
	/**
	 * @return the old value as a primitive float value
	 * @throws ClassCastException if the old value is not a {@link Float}.
	 */
	override getOldFloatValue() {
		oldValue as Float
	}
	
	/**
	 * @return the old value as a primitive int value
	 * @throws ClassCastException if the old value is not an {@link Integer}.
	 */
	override getOldIntValue() {
		oldValue as Integer
	}
	
	/**
	 * @return the old value as a primitive long value
	 * @throws ClassCastException if the old value is not a {@link Long}.
	 */
	override getOldLongValue() {
		oldValue as Long
	}
	
	/**
	 * @return the old value as a primitive short value
	 * @throws ClassCastException if the old value is not a {@link Short}.
	 */
	override getOldShortValue() {
		oldValue as Short
	}
	
	/**
	 * @return the old value as a string
	 * @throws ClassCastException if the old value is not a {@link String}.
	 */
	override getOldStringValue() {
		oldValue as String
	}
	
	/**
	 * @return the old value
	 */
	override getOldValue() {
		oldValue
	}
	
	/**
	 * @return the index in the list where the event occurred, or {@link Notification#NO_INDEX} if not applicable
	 */
	override getPosition() {
		position
	}
	
	/**
	 * @return {@code true} if the notification's feature has been reset to its default value, {@code false} otherwise
	 */
	override isReset() {
		eventType == UNSET || (eventType == SET && newValue == getFeatureDefaultValue())
	}
	
	/**
	 * @return whether the notification represents a state-changing event
	 */
	override isTouch() {
		switch (eventType) {
			case RESOLVE,
			case REMOVING_ADAPTER: true
			case SET,
			case UNSET: position != NO_INDEX && newValue == oldValue
			case MOVE: (oldValue as Integer) == position
			default: false
		}
	}
	
	/**
     * Returns whether the notification can be and has been merged with this one.
     * <br/>
     * Notifications can be merged when all these conditions are met: <ul>
     * <li>They have the same notifier</li>
     * <li>They have the same feature</li>
     * <li>They have compatible event types: <ul>
     *     <li>{@link #SET SET}, {@link #UNSET UNSET}</li>
     *     <li>{@link #ADD ADD}, {@link #ADD_MANY ADD_MANY}</li>
     *     <li>{@link #REMOVE REMOVE}, {@link #REMOVE_MANY REMOVE_MANY}</li>
     *     </ul>
     * </li>
     * </ul>
     * <tt>null</tt> is treated as a "nothing new happened" notification and will always be merged; the result of this merging is the unmodified old notification.
     * 
     * @param notification a notification that happened after this one (if order is relevant)
     * @return whether the notification can be and has been merged with this one.
     */
     //TODO When an object is moved and removed from the list immediately afterwards, should the notifications be merged? Does this ever happen in real code?
   override merge(Notification notification) {
		if (notification === null) {
			true
		} else if (getFeatureID(feature.getEContainingClass.getClass()) != notification.getFeatureID(feature.getEContainingClass.getClass())
				|| notifier != notification.notifier) {
		    false
		} else {
			val eventTypes = this.eventType -> notification.eventType
			switch (eventTypes) {
				case SET -> UNSET,
				case UNSET -> UNSET: {
					if (this.position == notification.position && this.newValue == notification.oldValue) {
						this.eventType = UNSET
						this.newValue = notification.newValue
						true
					} else false
				}
				case SET -> SET,
				case UNSET -> SET: {
					if (this.position == notification.position && this.newValue == notification.oldValue) {
						this.eventType = SET
						this.newValue = notification.newValue
						true
					} else false
				}
				case ADD -> ADD,
				case ADD -> ADD_MANY,
				case ADD_MANY -> ADD,
				case ADD_MANY -> ADD_MANY: {
					this.eventType = ADD_MANY
					this.position = Math.min(this.position, notification.position)
					this.newValue = mergeCollections(this.newValue, notification.newValue)
					true
					/* The merged notification has the following properties:
					 * - eventType is ADD_MANY
					 * - position is the first position  in the list where an item was added
					 * - newValue is a collection of the items that were added
					 */
				}
				case REMOVE -> REMOVE,
				case REMOVE -> REMOVE_MANY,
				case REMOVE_MANY -> REMOVE,
				case REMOVE_MANY -> REMOVE_MANY: {
					this.eventType = REMOVE_MANY
					if (!(this.newValue instanceof Integer) || this.newValue != this.position) {
						this.newValue = this.position
					}
					this.position = Math.min(this.position, notification.position)
					this.newValue = mergeArrays(this.newValue, notification.newValue)
					this.oldValue = mergeCollections(this.oldValue, notification.oldValue)
					true
					/* The merged notification has the following properties:
					 * - eventType is REMOVE_MANY
					 * - position is the first position in the list where an item was removed
					 * - newValue is an int[] containing the positions of the items they had at the time of removal;
					 *   depending on how a collection implements removeAll() or whether it even is a list,
					 *   these positions may not be meaningful
					 * - oldValue is a collection of the items that were removed
					 */
				}
				default: false
			}
		}
	}
	
	/**
	 * Returns a list that contains a or (if a is a Collection) its contents
	 * and b of (if b is a Collection) its contents
	 */
	private def mergeCollections(Object a, Object b) {
		val Collection<Object> collA = if (a instanceof Collection) a else Collections.singleton(a)
		val Collection<Object> collB = if (b instanceof Collection) b else Collections.singleton(b)
		val list = new LinkedEList<Object>()
		list.addAll(collA)
		list.addAll(collB)
		list
	}
	
	/**
	 * Returns an int[] that contains the contents of a and b.
	 * If a or b are of type {@link Integer}, they are treated as single-element int[]s.
	 * 
	 * @throws ClassCastException if a or b is neither of type Integer nor of type int[]
	 */
	private def int[] mergeArrays(Object a, Object b) {
		val aIsArr = a instanceof int[]
		val bIsArr = b instanceof int[]
		val lengthA = if (aIsArr) (a as int[]).length else 1
		val lengthB = if (bIsArr) (b as int[]).length else 1
		val int[] merged = newIntArrayOfSize(lengthA + lengthB)
		
		if (aIsArr) {
			System.arraycopy(a, 0, merged, 0, lengthA)
		} else {
			merged.set(0, a as Integer)
		}
		
		if (bIsArr) {
			System.arraycopy(b, 0, merged, lengthA, lengthB)
		} else {
			merged.set(lengthA, b as Integer)
		}
		
		return merged
	}
	
	/**
	 * @return whether the notifier's feature was considered set before the change occurred.
	 */
	override wasSet() {
		switch (eventType) {
			case SET: isFeatureUnsettable && position != NO_INDEX
			case UNSET: isFeatureUnsettable && position == NO_INDEX
			case ADD,
			case ADD_MANY,
			case REMOVE,
			case REMOVE_MANY,
			case MOVE: position > NO_INDEX
			default: false
		}
	}
	
  /**
   * Adds a notification to the chain.
   * @return whether the notification was added.
   */
  override add(Notification notification) {
		if (merge(notification)) {
			false
		} else if (next === null) {
			next = if (notification instanceof NotificationChain) {
				notification
			} else {
				new SmartEMFNotification(notification)
			}
			true
		} else {
			next.add(notification)
		}
	}
	
	override _dispatch() {
		if (notifier !== null && eventType != -1) {
			notifier.eNotify(this)
			if (next !== null) next.dispatch()
		}
	}
	
	override toString() {
		val str = '''«super.toString» (eventType: «eventTypeToString» «if (isTouch) ", touch: true" else ""», position: «position»'''
		+ ''', notifier: «notifier», feature: «feature ?: "(no feature)"», oldValue: «oldValue», newValue: «newValue», wasSet: «wasSet()»)'''
		
		if (next !== null) {
			return str + System.lineSeparator + next.toString
		} else {
			return str
		}
	}
	
	private def eventTypeToString() {
		switch (eventType) {
			case ADD: "ADD"
			case ADD_MANY: "ADD_MANY"
			case 0: "CREATE (deprecated)"
			case MOVE: "MOVE"
			case REMOVE: "REMOVE"
			case REMOVE_MANY: "REMOVE_MANY"
			case REMOVING_ADAPTER: "REMOVING_ADAPTER"
			case RESOLVE: "RESOLVE"
			case SET: "SET"
			case UNSET: "UNSET"
			default: "user-defined (" + eventType + ")"
		}
	}

}
