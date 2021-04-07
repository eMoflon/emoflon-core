package emfcodegenerator.notification;

import org.eclipse.emf.common.notify.NotificationChain;

/**
 * This interface is necessary because {@code dispatch} is an Xtend keyword and cannot be a method name,
 * and therefore Xtend classes cannot directly implement {@link NotificationChain}.
 * @author paulschiffner
 * */
public interface NotificationChainWorkaround extends NotificationChain {
	/**
	 * Dispatches each notification to the appropriate notifier via 
	 * {@link org.eclipse.emf.common.notify.Notifier#eNotify Notifier.eNotify}.
	 */
	void _dispatch();
	
	default void dispatch() {
		_dispatch();
	}
}
