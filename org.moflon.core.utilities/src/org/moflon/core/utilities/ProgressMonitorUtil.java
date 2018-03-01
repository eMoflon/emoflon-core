package org.moflon.core.utilities;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

/**
 * Utility methods for working with {@link IProgressMonitor}s
 *
 * @author Roland Kluge - Initial implementation
 *
 */
public final class ProgressMonitorUtil {
	/**
	 * Disabled
	 */
	private ProgressMonitorUtil() {
		throw new UtilityClassNotInstantiableException();
	}

	/**
	 * Checks whether the given {@link IProgressMonitor} has been canceled and, if
	 * yes, throws an {@link OperationCanceledException} to indicate cancellation
	 * 
	 * @param monitor
	 *            the monitor to be checked
	 */
	public static final void checkCancellation(final IProgressMonitor monitor) {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}
}
