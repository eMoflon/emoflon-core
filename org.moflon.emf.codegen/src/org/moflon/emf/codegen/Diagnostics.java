package org.moflon.emf.codegen;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.Diagnostic;
import org.moflon.core.utilities.WorkspaceHelper;

public class Diagnostics {
	public static IStatus createStatusFromDiagnostic(final Diagnostic diagnostic) {
		if (!isOK(diagnostic)) {
			if (hasNoChildren(diagnostic)) {
				return createLeafStatus(diagnostic);
			} else {
				return createMultiStatus(diagnostic);
			}
		} else
			return Status.OK_STATUS;

	}

	private static IStatus createLeafStatus(final Diagnostic diagnostic) {
		return new Status(getStatusSeverity(diagnostic), getPluginId(), diagnostic.getMessage(),
				diagnostic.getException());
	}

	private static String getPluginId() {
		return WorkspaceHelper.getPluginId(Diagnostics.class);
	}

	private static MultiStatus createMultiStatus(final Diagnostic diagnostic) {
		final MultiStatus emfCodeGeneratorStatus = new MultiStatus(getPluginId(), getStatusSeverity(diagnostic),
				diagnostic.getMessage(), diagnostic.getException());
		for (final Diagnostic childDiagnostic : diagnostic.getChildren()) {
			if (!isOK(childDiagnostic)) {
				emfCodeGeneratorStatus.add(createStatusFromDiagnostic(childDiagnostic));
			}
		}
		return emfCodeGeneratorStatus;
	}

	public static boolean hasNoChildren(final Diagnostic diagnostic) {
		return diagnostic.getChildren().isEmpty();
	}

	private static boolean isOK(final Diagnostic diagnostic) {
		return Diagnostic.OK == diagnostic.getSeverity();
	}

	private static int getStatusSeverity(final Diagnostic diagnostic) {
		final int diagnosticSeverity = diagnostic.getSeverity();
		switch (diagnosticSeverity) {
		case Diagnostic.OK:
			return IStatus.OK;
		case Diagnostic.INFO:
			return Status.INFO;
		case Diagnostic.WARNING:
			return IStatus.WARNING;
		case Diagnostic.ERROR:
			return IStatus.ERROR;
		default:
			throw new IllegalArgumentException(String.format("Unknown severity: %s", diagnosticSeverity));
		}
	}
}
