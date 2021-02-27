package emfcodegenerator.notification;

import org.eclipse.emf.common.notify.NotificationChain;

/** This interface is necessary because {@code dispatch} is an Xtend keyword and cannot be a method name */
public interface NotificationChainWorkaround extends NotificationChain {
	void _dispatch();
	
	default void dispatch() {
		_dispatch();
	}
}
