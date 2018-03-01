package org.moflon.emf.injection.unparsing;

import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.moflon.core.utilities.UtilityClassNotInstantiableException;
import org.moflon.emf.injection.injectionLanguage.ClassDeclaration;
import org.moflon.emf.injection.injectionLanguage.ClassInjectionDeclaration;
import org.moflon.emf.injection.injectionLanguage.InjectionFile;
import org.moflon.emf.injection.injectionLanguage.MethodDeclaration;
import org.moflon.emf.injection.injectionLanguage.RegularImport;
import org.moflon.emf.injection.injectionLanguage.StaticImport;

public final class InjectionFileUnparser {
	public InjectionFileUnparser() {
		throw new UtilityClassNotInstantiableException();
	}

	public static String unparseFile(final InjectionFile injectionFile) {
		final ClassDeclaration classDeclaration = injectionFile.getClassDeclaration();
		final StringBuilder fileContent = new StringBuilder();

		appendImports(injectionFile, fileContent);

		fileContent.append("partial class ");
		fileContent.append(classDeclaration.getClassName());
		fileContent.append(" {").append(InjectionConstants.NL).append(InjectionConstants.NL);

		appendMembersCode(classDeclaration, fileContent);

		fileContent.append(InjectionConstants.NL).append(InjectionConstants.NL);

		appendMethodDeclarations(classDeclaration, fileContent);

		fileContent.append("}");

		return fileContent.toString();
	}

	private static void appendMethodDeclarations(final ClassDeclaration classDeclaration,
			final StringBuilder fileContent) {
		for (final MethodDeclaration methodDeclaration : classDeclaration.getMethodDeclarations()) {
			appendMethodDeclaration(fileContent, methodDeclaration);
		}
	}

	private static void appendImports(final InjectionFile injectionFile, final StringBuilder fileContent) {
		for (final EObject animport : injectionFile.getImports()) {
			if (animport instanceof StaticImport) {
				fileContent.append(InjectionConstants.IMPORT_KEYWORD).append("static ")
						.append(StaticImport.class.cast(animport).getNamespace());
			} else if (animport instanceof RegularImport) {
				fileContent.append(InjectionConstants.IMPORT_KEYWORD).append(" ")
						.append(RegularImport.class.cast(animport).getNamespace());
			}
			fileContent.append(InjectionConstants.NL);
		}
		fileContent.append(InjectionConstants.NL);
	}

	private static void appendMethodDeclaration(final StringBuilder fileContent,
			final MethodDeclaration methodDeclaration) {
		fileContent.append(InjectionConstants.MODEL_KEYWORD).append(InjectionConstants.SPACE);
		fileContent.append(methodDeclaration.getMethodName()).append(InjectionConstants.SPACE);
		fileContent.append("(");
		fileContent.append(unparseParameterList(methodDeclaration));
		fileContent.append(")");
		fileContent.append(methodDeclaration.getBody());
		fileContent.append(InjectionConstants.NL).append(InjectionConstants.NL);
	}

	private static String unparseParameterList(final MethodDeclaration methodDeclaration) {
		return methodDeclaration.getParameters().stream()
				.map(parameterDeclaration -> parameterDeclaration.getParameterType() + " "
						+ parameterDeclaration.getParameterName())
				.collect(Collectors.joining(", "));
	}

	private static void appendMembersCode(final ClassDeclaration classDeclaration, final StringBuilder fileContent) {
		final ClassInjectionDeclaration classInjectionDeclaration = classDeclaration.getClassInjectionDeclaration();
		if (classInjectionDeclaration != null && classInjectionDeclaration.getBody() != null) {
			fileContent.append(InjectionConstants.MEMBERS_KEYWORD);
			fileContent.append(classInjectionDeclaration.getBody());
		}
	}
}
