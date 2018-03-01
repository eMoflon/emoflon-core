package org.moflon.core.utilities;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * This class wraps a {@link CoreException}, which is a checked exception that
 * cannot be thrown in some contexts.
 */
public class UncheckedCoreException extends RuntimeException {

	/**
	 * Constructor that stores the given {@link CoreException}
	 */
	public UncheckedCoreException(final CoreException wrappedException) {
		super(wrappedException);
	}

	/**
	 * Constructor that creates a new {@link CoreException} using the given plugin
	 * ID and error message.
	 */
	public UncheckedCoreException(final String errorMessage, final String pluginId) {
		this(new CoreException(new Status(IStatus.ERROR, pluginId, errorMessage)));
	}

	/**
	 * Returns the wrapped exception
	 */
	public CoreException getWrappedException() {
		return (CoreException) this.getCause();
	}

	private static final long serialVersionUID = 424344754161787168L;

}
