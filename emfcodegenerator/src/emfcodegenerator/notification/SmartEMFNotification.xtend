package emfcodegenerator.notification

import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.common.notify.Notification
import org.eclipse.emf.common.notify.NotificationChain
import org.eclipse.emf.common.notify.impl.NotificationChainImpl
import java.util.Collections
import java.util.Collection
import emfcodegenerator.util.collections.LinkedEList
import org.eclipse.emf.ecore.EObject

class SmartEMFNotification implements Notification, NotificationChainWorkaround {
	
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
	
	def static addToFeature(EObject owner, EStructuralFeature feature, Object object, int index) {
		val notification = new SmartEMFNotification(ADD, null, object)
		notification.notifier = owner
		notification.feature = feature
		notification.position = index
		return notification
	}
	
	def static set(EObject owner, EStructuralFeature feature, Object oldValue, Object newValue, int index) {
		val notification = new SmartEMFNotification(SET, oldValue, newValue)
		notification.notifier = owner
		notification.feature = feature
		notification.position = index
		return notification
	}
	
	def static removeFromFeature(EObject owner, EStructuralFeature feature, Object object, int index) {
		val notification = new SmartEMFNotification(if (feature.isMany) REMOVE else UNSET, object, null)
		notification.notifier = owner
		notification.feature = feature
		notification.position = index
		return notification
	}
	
	def static moveInList(EObject owner, EStructuralFeature feature, Object object, int oldIndex, int newIndex) {
		val notification = new SmartEMFNotification(MOVE, oldIndex, object)
		notification.notifier = owner
		notification.feature = feature
		notification.position = newIndex
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
	
	override getNewBooleanValue() {
		newValue as Boolean
	}
	
	override getNewByteValue() {
		newValue as Byte
	}
	
	override getNewCharValue() {
		newValue as Character
	}
	
	override getNewDoubleValue() {
		newValue as Double
	}
	
	override getNewFloatValue() {
		newValue as Float
	}
	
	override getNewIntValue() {
		newValue as Integer
	}
	
	override getNewLongValue() {
		newValue as Long
	}
	
	override getNewShortValue() {
		newValue as Short
	}
	
	override getNewStringValue() {
		newValue as String
	}
	
	override getNewValue() {
		newValue
	}
	
	override getNotifier() {
		notifier
	}
	
	override getOldBooleanValue() {
		oldValue as Boolean
	}
	
	override getOldByteValue() {
		oldValue as Byte
	}
	
	override getOldCharValue() {
		oldValue as Character
	}
	
	override getOldDoubleValue() {
		oldValue as Double
	}
	
	override getOldFloatValue() {
		oldValue as Float
	}
	
	override getOldIntValue() {
		oldValue as Integer
	}
	
	override getOldLongValue() {
		oldValue as Long
	}
	
	override getOldShortValue() {
		oldValue as Short
	}
	
	override getOldStringValue() {
		oldValue as String
	}
	
	override getOldValue() {
		oldValue
	}
	
	override getPosition() {
		position
	}
	
	override isReset() {
		eventType == UNSET || (eventType == SET && newValue == getFeatureDefaultValue())
	}
	
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
     * <li>They have compatible event types</li>
     * </ul>
     * <tt>null</tt> is treated as a "nothing new happened" notification and will always be merged; the result of this merging is the unmodified old notification.
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
					this.eventType = UNSET
					this.newValue = notification.newValue
					true
				}
				case SET -> SET,
				case UNSET -> SET: {
					this.eventType = SET
					this.newValue = notification.newValue
					true
				}
				case ADD -> ADD,
				case ADD -> ADD_MANY,
				case ADD_MANY -> ADD,
				case ADD_MANY -> ADD_MANY: {
					this.eventType = ADD_MANY
					this.position = Math.min(this.position, notification.position)
					this.newValue = mergeCollections(this.newValue, notification.newValue)
					true
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
				}
				default: false
			}
		}
	}
	
	private def mergeCollections(Object a, Object b) {
		val collA = if (a instanceof Collection) a else Collections.singleton(a)
		val collB = if (b instanceof Collection) b else Collections.singleton(b)
		val list = new LinkedEList<Object>()
		list.addAll(collA)
		list.addAll(collB)
		list
	}
	
	private def int[] mergeArrays(Object a, Object b) {
		if (a instanceof int[]) _mergeArrays(a, b)
		else _mergeArrays(Collections.singletonList(a as Integer), b)
	}
	
	private def int[] _mergeArrays(int[] a, Object b) {
		if (b instanceof int[]) mergeArrays(a, b)
		else __mergeArrays(a, Collections.singletonList(b as Integer))
	}
	
	private def int[] __mergeArrays(int[] a, int[] b) {
		a.addAll(b)
		a.sort(null)
		a
	}
	
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
	
	override add(Notification notification) {
		if (merge(notification)) {
			false
		} else if (next === null) {
			next = if (notification instanceof NotificationChain) {
				notification
			} else {
				val chain = new NotificationChainImpl()
				chain.add(notification)
				chain
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
		'''«super.toString» (eventType: «eventTypeToString» «if (isTouch) ", touch: true" else ""», position: «position»'''
		+ ''', notifier: «notifier», feature: «feature», oldValue: «oldValue», newValue: «newValue», wasSet: «wasSet()»)'''
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