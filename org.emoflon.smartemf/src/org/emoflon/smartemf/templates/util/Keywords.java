package org.emoflon.smartemf.templates.util;

import java.util.Arrays;
import java.util.HashSet;

public class Keywords {
	/**
	 * The list of invalid parameter names.
	 */
	final public static String[] keywords = new String[] { "class", "rule", "clone", "equals", "finalize", "getClass",
			"hashCode", "notify", "notifyAll", "toString", "wait", "abstract", "assert", "boolean", "break", "byte",
			"case", "catch", "char", "class", "const", "continue", "default", "do", "double", "EAttribute", "EBoolean",
			"EDataType", "EClass", "EClassifier", "EDouble", "EFloat", "EInt", "else", "enum", "EPackage", "EReference",
			"EString", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import",
			"instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public",
			"return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
			"transient", "try", "void", "volatile", "while" };

	final public static HashSet<String> blacklist = new HashSet<>(Arrays.asList(keywords));

}
