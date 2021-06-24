package org.moflon.smartemf.creators;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Common methods for all classes which generate a file.
 * @author Adrian Zwenger
 */
public interface FileCreator {
	
	/**
	 * The list of invalid parameter names.
	 */
	final public static String[] keywords = new String[] {"class", "rule", "clone", "equals", "finalize",
		"getClass", "hashCode", "notify", "notifyAll", "toString", "wait",
		"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "EAttribute", "EBoolean", "EDataType", 
		"EClass", "EClassifier", "EDouble", "EFloat", "EInt", "else", "enum", "EPackage", "EReference", "EString", "extends", "final", "finally", "float", "for", "goto", "if", 
		"implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", 
		"strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"};
	
	final public static HashSet<String> blacklist = new HashSet<>(Arrays.asList(keywords));
	
	/**
	 * Initializes the creator and gathers all needed information for code generation.
	 * @param fq_file_path String fully qualified path and name of the file which shall be written
	 * @param IDENTION String with which the code shall be idented
	 */
	public void initialize_creator(String fq_file_path);
	
	/**
	 * Starts the writing process. The file is created.
	 */
	public void write_to_file();
}
