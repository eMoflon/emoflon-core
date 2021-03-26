package emfcodegenerator.util.collections;

import emfcodegenerator.notification.SmartEMFNotification;

public class ListNotificationBuilder {
	private SmartEMFNotification currentChain;
	private int accumulate = 0;
	
	/**
	 * Increments the accumulation counter.<br/>
	 * While the counter is greater than zero, if a notification is added to the current chain,<br/>
	 * it is not dispatched and it may be merged with the preceding notification.
	 */
	public void enableAccumulation() {
		this.accumulate++;
	}
	
	/**
	 * @return {@code true} if accumulation is enabled, i.e. the accumulation counter is greater than 0.
	 */
	public boolean accumulates() {
		return accumulate > 0;
	}
	
	/**
	 * Decrements the accumulation counter.
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
	 * Dispatches the current notification chain (if not null) and resets the accumulation counter to 0.
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
