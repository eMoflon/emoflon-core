package org.moflon.core.utilities;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Utility methods for exception handling
 *
 * @author Roland Kluge - Migration from MoflonUtil
 */
public final class ExceptionUtil {
	/**
	 * Disabled utility class constructor
	 */
	private ExceptionUtil() {
		throw new UtilityClassNotInstantiableException();
	}

	/**
	 * Creates an {@link IStatus} that reports the given {@link Throwable}.
	 * @param reportingClass the class that reports the error (used for determining the plugin id)
	 * @param t the error to report
	 * @return the resulting {@link IStatus} having flag {@link IStatus#ERROR} and the message of the {@link Throwable} as status message
	 */
	public static IStatus createDefaultErrorStatus(final Class<?> reportingClass, final Throwable t) {
		return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(reportingClass), t.getMessage(), t);
	}

	/**
	 * Formats the given exception for debugging purposes.
	 *
	 * If available, the root cause and its stacktrace are formatted. Else, the
	 * reason of the exception is shown.
	 *
	 * @param e
	 *            the exception to be formatted
	 * @return the formatted exception
	 */
	public static String displayExceptionAsString(final Exception e) {
		try {
			final String message;
			if (null != e.getCause()) {
				message = "Cause: " + ExceptionUtils.getRootCauseMessage(e) + "\nStackTrace: "
						+ ExceptionUtils.getStackTrace(ExceptionUtils.getRootCause(e));
			} else {
				message = "Reason: " + e.getMessage();
			}
			return message;
		} catch (Exception new_e) {
			return e.getMessage();
		}
	}

	/**
	 * Throws a {@link CoreException} that contains an {@link IStatus} with the
	 * given message, plugin ID, and wrapped exception
	 *
	 * @param message
	 *            error message
	 * @param plugin
	 *            ID of throwing plugin
	 * @param lowLevelException
	 *            the optional wrapped exception (may be <code>null</code>).
	 * @throws CoreException
	 *             always
	 */
	public static void throwCoreExceptionAsError(final String message, final String plugin,
			final Exception lowLevelException) throws CoreException {
		final IStatus status;
		if (lowLevelException == null) {
			status = new Status(IStatus.ERROR, plugin, message);
		} else {
			status = new Status(IStatus.ERROR, plugin, 0, message, lowLevelException);
		}
		throw new CoreException(status);
	}
}
