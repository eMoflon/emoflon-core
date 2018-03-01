package org.moflon.core.utilities;

import org.eclipse.core.runtime.IStatus;

/**
 * A generic interface for any component that extracts and reports errors from
 * an {@link IStatus} object.
 */
public interface ErrorReporter {

	/**
	 * Extracts and reports errors from the given status.
	 * 
	 * @param status
	 *            the status
	 */
	public void report(IStatus status);
}
