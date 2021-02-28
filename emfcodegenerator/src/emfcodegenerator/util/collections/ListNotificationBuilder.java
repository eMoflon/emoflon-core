package emfcodegenerator.util.collections;

import emfcodegenerator.notification.SmartEMFNotification;

public class ListNotificationBuilder {
	private SmartEMFNotification currentChain;
	private boolean accumulate = false;
	
	/**
	 * Enables accumulation of notifications.<br/>
	 * When accumulation is enabled, notifications are not dispatched until {@link #flush()} is called.<br/>
	 * Until that point, if a notification is added to the current chain, it may be merged with the preceding notification.
	 */
	public void enableAccumulation() {
		this.accumulate = true;
	}
	
	/**
	 * @return {@code true} if accumulation is enabled
	 */
	public boolean accumulates() {
		return accumulate;
	}
	
	/**
	 * Dispatches the current notification chain and disables accumulation.
	 */
	public void flush() {
		if (currentChain != null) {
			currentChain.dispatch();
		}
		currentChain = null;
		accumulate = false;
	}
	
	/**
	 * If accumulation is enabled, adds a notification to the current chain.<br/>
	 * If accumulation is disabled, immediately dispatches the notification.<br/>
	 * If the notification has no notifier, ignores it.
	 * @param n - the notification to be added
	 * @return {@code true} if the notification was dispatched, else {@code false}
	 */
	public boolean add(SmartEMFNotification n) {
		if (n.getNotifier() == null) return false;
		if (currentChain == null) {
			currentChain = n;
			if (!accumulate) {
				flush();
			}
			return true;
		} else {
			currentChain.add(n);
			return false;
		}
	}
}
