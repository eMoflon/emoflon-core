package org.moflon.emf.injection.unparsing;

import org.moflon.core.utilities.UtilityClassNotInstantiableException;

/**
 * This class contains constants relevant to injection processing
 */
public final class InjectionConstants {
	public static final String IMPORT_KEYWORD = "import ";
	public static final String MEMBERS_KEYWORD = "@members";
	public static final String MODEL_KEYWORD = "@model";
	public static final String NL = "\n";
	public static final String MEMBERS_END = "// [user code injected with eMoflon] -->";
	public static final String MEMBERS_BEGIN = "// <-- [user code injected with eMoflon]";
	public static final String USER_IMPORTS_END = "// [user defined imports] -->";
	public static final String USER_IMPORTS_BEGIN = "// <-- [user defined imports]";
	public static final String CODE_END_TOKEN = "-->";
	public static final String CODE_BEGIN_TOKEN = "<--";
	public static final String SPACE = " ";
	public static final String INDENT = "\t";
	/**
	 * Injection files prefixed with this string are not processed
	 */
	public static final String IGNORE_FILE_PREFIX = ".";

	private InjectionConstants() {
		throw new UtilityClassNotInstantiableException();
	}
}
