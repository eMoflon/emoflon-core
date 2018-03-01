package org.moflon.emf.injection.ide;

import java.util.List;

/**
 * Injector specification for members code and user imports into existing Java
 * code.
 */
public interface CodeInjector {
	/**
	 * Injects a members block into a file.
	 * 
	 * @param relativePath
	 *            the relative path to the file, starting from project root.
	 *            Example: "/gen/org/moflon/ide/core/CoreAdvisor.java"
	 * @param code
	 *            members code to inject.
	 */
	void injectMembersCode(String relativePath, String code);

	/**
	 * Injects an imports block into a file.
	 * 
	 * @param relativePath
	 *            the relative path to the file, starting from project root.
	 *            Example: "/gen/org/moflon/ide/core/CoreAdvisor.java"
	 * @param imports
	 *            the imports to inject.
	 */
	void injectImports(String relativePath, List<String> imports);
}