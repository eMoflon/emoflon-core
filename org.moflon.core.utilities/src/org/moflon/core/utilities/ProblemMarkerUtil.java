package org.moflon.core.utilities;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Utility methods for working with {@link IMarker}s
 *
 * @author Roland Kluge - Initial implementation
 *
 */
public final class ProblemMarkerUtil {
	/**
	 * Creates a problem marker for the given {@link IResource}, with message,
	 * severity and location description
	 * 
	 * @param resource
	 *            the resource
	 * @param message
	 *            the message
	 * @param severity
	 *            the severity
	 * @param location
	 *            the location description
	 * @throws CoreException
	 *             if creating the marker fails
	 */
	public static void createProblemMarker(final IResource resource, final String message, final int severity,
			final String location) throws CoreException {
		final IMarker marker = resource.createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.SEVERITY, severity);
		marker.setAttribute(IMarker.LOCATION, location);
	}

	/**
	 * Maps the given integer-valued severity level (e.g., {@link IStatus#ERROR}) to
	 * the corresponding level for {@link IMarker} levels (e.g.,
	 * {@link IMarker#SEVERITY_ERROR}
	 * 
	 * @param severity
	 *            the severity from {@link IStatus}
	 * @return the severity for {@link IMarker}s
	 * @throws CoreException
	 *             if the given level cannot be mapped
	 */
	public static final int convertStatusSeverityToMarkerSeverity(final int severity) throws CoreException {
		switch (severity) {
		case IStatus.ERROR:
			return IMarker.SEVERITY_ERROR;
		case IStatus.WARNING:
			return IMarker.SEVERITY_WARNING;
		case IStatus.INFO:
			return IMarker.SEVERITY_INFO;
		default:
			final IStatus invalidSeverityConversion = new Status(IStatus.ERROR,
					WorkspaceHelper.getPluginId(ProblemMarkerUtil.class),
					String.format("Cannot convert severity %d to a marker", severity));
			throw new CoreException(invalidSeverityConversion);
		}
	}
}
