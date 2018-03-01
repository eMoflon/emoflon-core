package org.moflon.emf.injection.validation;

import org.eclipse.core.runtime.IStatus;
import org.moflon.core.utilities.WorkspaceHelper;

/**
 * An {@link IStatus} that contains information about the validation of
 * injections.
 *
 */
public class InjectionValidationStatus implements IStatus {
	private final InjectionValidationMessage message;

	public InjectionValidationStatus(final InjectionValidationMessage injectionValidationMessage) {
		this.message = injectionValidationMessage;
	}

	@Override
	public IStatus[] getChildren() {
		return new IStatus[0];
	}

	@Override
	public int getCode() {
		return 0;
	}

	@Override
	public Throwable getException() {
		return null;
	}

	@Override
	public String getMessage() {
		return this.message.getMessage();
	}

	@Override
	public String getPlugin() {
		return WorkspaceHelper.getPluginId(getClass());
	}

	@Override
	public int getSeverity() {
		return this.message.getSeverity();
	}

	@Override
	public boolean isMultiStatus() {
		return false;
	}

	@Override
	public boolean isOK() {
		return false;
	}

	@Override
	public boolean matches(final int severityMask) {
		return (getSeverity() & severityMask) != 0;
	}

	@Override
	public String toString() {
		return "Injection Status [m=" + this.message + "]";
	}

	public InjectionValidationMessage getInjectionMessage() {
		return this.message;
	}
}
