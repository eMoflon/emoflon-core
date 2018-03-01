package org.moflon.emf.injection.validation;

import java.util.List;

import org.eclipse.core.runtime.IStatus;

public class MissingEOperationValidationMessage extends InjectionValidationMessage {

	public MissingEOperationValidationMessage(final String methodName, final List<String> paramTypes,
			final String className, final String fileName) {
		super("Cannot find the EOperation " + getMethodSignature(methodName, paramTypes) + " in class '" + className
				+ "'.", fileName, IStatus.WARNING);
	}

	private static String getMethodSignature(final String methodName, final List<String> paramTypes) {
		final StringBuilder sb = new StringBuilder();

		sb.append(methodName);
		sb.append("(");
		for (int i = 0; i < paramTypes.size(); i++) {
			sb.append(paramTypes.get(i));
			if (i < paramTypes.size() - 1)
				sb.append(", ");
		}
		sb.append(")");

		return sb.toString();
	}
}
