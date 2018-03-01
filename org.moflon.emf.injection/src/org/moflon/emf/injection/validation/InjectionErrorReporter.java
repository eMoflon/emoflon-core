package org.moflon.emf.injection.validation;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.moflon.core.utilities.ErrorReporter;
import org.moflon.core.utilities.WorkspaceHelper;

/**
 * Extracts and reports validation errors in injection files.
 * 
 * This reporter also adds appropriate problem markers.
 */
public class InjectionErrorReporter implements ErrorReporter {
	private static final Logger logger = Logger.getLogger(InjectionErrorReporter.class);

	private final IProject project;

	public InjectionErrorReporter(final IProject project) {
		this.project = project;
	}

	@Override
	public void report(final IStatus status) {
		if (status == null)
			return;

		for (final IStatus validationStatus : status.getChildren()) {
			if (!validationStatus.isOK() && validationStatus.getClass().equals(InjectionValidationStatus.class)) {
				final InjectionValidationStatus injectionStatus = (InjectionValidationStatus) validationStatus;
				this.logMessage(injectionStatus);
				this.createMarker(injectionStatus);
			}

			if (validationStatus.isMultiStatus()) {
				report(validationStatus);
			}
		}
	}

	private void logMessage(final InjectionValidationStatus injectionStatus) {
		final String loggingMessage = "[Injection Validation] " + injectionStatus.getMessage() + ". Project: "
				+ this.project.getName() + ". File: " + injectionStatus.getInjectionMessage().getLocation();
		switch (injectionStatus.getSeverity()) {
		case IStatus.WARNING:
			logger.error(loggingMessage);
			break;
		case IStatus.ERROR:
			logger.error(loggingMessage);
			break;
		}
	}

	private void createMarker(final InjectionValidationStatus injectionStatus) {
		final String location = injectionStatus.getInjectionMessage().getLocation();
		try {
			final IMarker marker = this.project.getFile(location)
					.createMarker(WorkspaceHelper.INJECTION_PROBLEM_MARKER_ID);
			marker.setAttribute(IMarker.MESSAGE, injectionStatus.getInjectionMessage().getMessage());
			marker.setAttribute(IMarker.LOCATION, location);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			switch (injectionStatus.getSeverity()) {
			case IStatus.WARNING:
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				break;
			case IStatus.ERROR:
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				break;
			}
		} catch (final CoreException e) {
			e.printStackTrace();
		}
	}

}
