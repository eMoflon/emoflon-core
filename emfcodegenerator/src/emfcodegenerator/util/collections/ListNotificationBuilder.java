package emfcodegenerator.util.collections;

import emfcodegenerator.notification.SmartEMFNotification;

public class ListNotificationBuilder {
	private SmartEMFNotification currentChain;
	private int accumulate = 0;
	
	/**
	 * Enables accumulation of notifications.<br/>
	 * When accumulation is enabled, notifications are not dispatched until {@link #flush()} is called.<br/>
	 * Until that point, if a notification is added to the current chain, it may be merged with the preceding notification.<br/>
	 * {@link #flush()} must be called as often as this method before notifications are dispatched.
	 */
	public void enableAccumulation() {
		this.accumulate++;
	}
	
	/**
	 * @return {@code true} if accumulation is enabled
	 */
	public boolean accumulates() {
		return accumulate > 0;
	}
	
	/**
	 * Lowers the accumulation counter by 1.
	 * If it reaches 0, dispatches the current notification chain (if not null).
	 */
	public void flush() {
		if (accumulates()) {
			accumulate--;
		}
		if (currentChain != null && !accumulates()) {
			currentChain.dispatch();
			currentChain = null;
		}
	}
	
	/**
	 * Dispatches the current notification chain (if not null) and disables accumulation.
	 */
	public void forceFlush() {
		if (currentChain != null) {
			currentChain.dispatch();
			currentChain = null;
		}
		accumulate = 0;
	}
	
	/**
	 * If accumulation is enabled, adds a notification to the current chain.<br/>
	 * If accumulation is disabled, immediately dispatches the notification.<br/>
	 * If the notification is null or has no notifier, ignores it.
	 * @param n - the notification to be added
	 * @return {@code true} if the notification was dispatched, else {@code false}
	 */
	public boolean add(SmartEMFNotification n) {
		if (n == null || n.getNotifier() == null) return false;
		if (currentChain == null) {
			currentChain = n;
			if (!accumulates()) {
				flush();
			}
			return true;
		} else {
			currentChain.add(n);
			return false;
		}
	}
}
