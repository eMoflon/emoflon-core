package emfcodegenerator.util.collections;

import emfcodegenerator.notification.SmartEMFNotification;

public class ListNotificationBuilder {
	private SmartEMFNotification currentChain;
	private boolean accumulate = false;
	
	public void enableAccumulation() {
		this.accumulate = true;
	}
	
	public boolean accumulates() {
		return accumulate;
	}
	
	public void flush() {
		if (currentChain != null) {
			currentChain.dispatch();
		}
		currentChain = null;
		accumulate = false;
	}
	
	public boolean add(SmartEMFNotification n) {
		if (n.getNotifier() == null) return false;
		if (currentChain == null) {
			currentChain = n;
			if (!accumulate) {
				flush();
			}
			return true;
		} else {
			return currentChain.add(n);
		}
	}
}
