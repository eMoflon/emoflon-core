package org.emoflon.smartemf.runtime.notification;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;

public final class SmartEMFNotification implements Notification {

	private int eventType;
	private Object notifier;
	private EStructuralFeature feature;
	private Object oldValue;
	private Object newValue;
	private int position;

	private SmartEMFNotification(int eventType, Object notifier, EStructuralFeature feature, Object oldValue, Object newValue, int position) {
		this.eventType = eventType;
		this.notifier = notifier;
		this.feature = feature;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.position = position;
	}

	public static Notification createAddNotification(Object notifier, EStructuralFeature feature, Object newValue, int index) {
		return new SmartEMFNotification(ADD, notifier, feature, null, newValue, index);
	}

	public static Notification createAddManyNotification(Object notifier, EStructuralFeature feature, Object newValue, int index) {
		return new SmartEMFNotification(ADD_MANY, notifier, feature, null, newValue, index);
	}

	public static Notification createSetNotification(Object notifier, EStructuralFeature feature, Object oldValue, Object newValue, int index) {
		return new SmartEMFNotification(SET, notifier, feature, oldValue, newValue, index);
	}

	public static Notification createUnSetNotification(Object notifier, EStructuralFeature feature, Object oldValue, int index) {
		return new SmartEMFNotification(UNSET, notifier, feature, oldValue, null, index);
	}

	public static Notification createRemoveNotification(Object notifier, EStructuralFeature feature, Object oldValue, int index) {
		return new SmartEMFNotification(REMOVE, notifier, feature, oldValue, null, index);
	}

	public static Notification createRemoveManyNotification(Object notifier, EStructuralFeature feature, Object oldValue, int index) {
		return new SmartEMFNotification(REMOVE_MANY, notifier, feature, oldValue, null, index);
	}

	public static Notification createRemovingAdapterNotification(Object notifier, EStructuralFeature feature, Object oldValue, int index) {
		return new SmartEMFNotification(REMOVING_ADAPTER, notifier, feature, oldValue, null, index);
	}

	public static Notification createMoveNotification(Object notifier, EStructuralFeature feature, Object value, int oldIndex, int newIndex) {
		return new SmartEMFNotification(MOVE, notifier, feature, oldIndex, value, newIndex);
	}

	@Override
	public Object getNotifier() {
		return notifier;
	}

	@Override
	public int getEventType() {
		return eventType;
	}

	@Override
	public int getFeatureID(Class<?> expectedClass) {
		if (feature != null) {
			return feature.getFeatureID();
		}
		return NO_FEATURE_ID;
	}

	@Override
	public Object getFeature() {
		return feature;
	}

	@Override
	public Object getOldValue() {
		return oldValue;
	}

	@Override
	public Object getNewValue() {
		return newValue;
	}

	@Override
	public boolean wasSet() {
		switch (eventType) {
		case SET:
			return feature.isUnsettable() && position != NO_INDEX;
		case UNSET:
			return feature.isUnsettable() && position == NO_INDEX;
		case ADD:
		case ADD_MANY:
		case REMOVE:
		case REMOVE_MANY:
		case MOVE:
			return position > NO_INDEX;
		default:
			return false;
		}
	}

	@Override
	public boolean isTouch() {
		switch (eventType) {
		case RESOLVE:
		case REMOVING_ADAPTER:
			return true;
		case SET:
		case UNSET:
			return position != NO_INDEX && newValue == oldValue;
		case MOVE:
			return ((Integer) oldValue) == position;
		default:
			return false;
		}
	}

	@Override
	public boolean isReset() {
		switch (eventType) {
		case UNSET:
			return true;
		case SET:
			return newValue.equals(feature.getDefaultValue());
		default:
			return false;
		}
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	/**
	 * Returns whether the notification can be and has been merged with this one. <br/>
	 * Notifications can be merged when all these conditions are met:
	 * <ul>
	 * <li>They have the same notifier</li>
	 * <li>They have the same feature</li>
	 * <li>They have compatible event types:
	 * <ul>
	 * <li>{@link #SET SET}, {@link #UNSET UNSET}</li>
	 * <li>{@link #ADD ADD}, {@link #ADD_MANY ADD_MANY}</li>
	 * <li>{@link #REMOVE REMOVE}, {@link #REMOVE_MANY REMOVE_MANY}</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <tt>null</tt> is treated as a "nothing new happened" notification and will always be merged; the
	 * result of this merging is the unmodified old notification.
	 * 
	 * @param notification a notification that happened after this one (if order is relevant)
	 * @return whether the notification can be and has been merged with this one.
	 */
	public boolean merge(Notification notification) {
		// deactivated at the moment
		return false;
	}

	@Override
	public boolean getOldBooleanValue() {
		return (boolean) oldValue;
	}

	@Override
	public boolean getNewBooleanValue() {
		return (boolean) newValue;
	}

	@Override
	public byte getOldByteValue() {
		return (byte) oldValue;
	}

	@Override
	public byte getNewByteValue() {
		return (byte) newValue;
	}

	@Override
	public char getOldCharValue() {
		return (char) oldValue;
	}

	@Override
	public char getNewCharValue() {
		return (char) newValue;
	}

	@Override
	public double getOldDoubleValue() {
		return (double) oldValue;
	}

	@Override
	public double getNewDoubleValue() {
		return (double) newValue;
	}

	@Override
	public float getOldFloatValue() {
		return (float) oldValue;
	}

	@Override
	public float getNewFloatValue() {
		return (float) newValue;
	}

	@Override
	public int getOldIntValue() {
		return (int) oldValue;
	}

	@Override
	public int getNewIntValue() {
		return (int) newValue;
	}

	@Override
	public long getOldLongValue() {
		return (long) oldValue;
	}

	@Override
	public long getNewLongValue() {
		return (long) newValue;
	}

	@Override
	public short getOldShortValue() {
		return (short) oldValue;
	}

	@Override
	public short getNewShortValue() {
		return (short) newValue;
	}

	@Override
	public String getOldStringValue() {
		if(oldValue == null)
			return null;
		return oldValue.toString();
	}

	@Override
	public String getNewStringValue() {
		if(newValue == null)
			return null;
		return newValue.toString();
	}

	private String getEventTypeAsString() {
		switch (eventType) {
		case ADD:
			return "ADD";
		case ADD_MANY:
			return "ADD_MANY";
		case 0:
			return "CREATE (deprecated)";
		case MOVE:
			return "MOVE";
		case REMOVE:
			return "REMOVE";
		case REMOVE_MANY:
			return "REMOVE_MANY";
		case REMOVING_ADAPTER:
			return "REMOVING_ADAPTER";
		case RESOLVE:
			return "RESOLVE";
		case SET:
			return "SET";
		case UNSET:
			return "UNSET";
		default:
			return "user-defined (" + eventType + ")";
		}
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();

		b.append("SmartEMFNotification (");
		b.append("eventType: ");
		b.append(getEventTypeAsString());
		b.append(", notifier: ");
		b.append(notifier);
		b.append(", feature: ");
		b.append(feature);
		b.append(", oldValue: ");
		b.append(oldValue);
		b.append(", newValue: ");
		b.append(newValue);
		b.append(")");

		return b.toString();
	}
}
