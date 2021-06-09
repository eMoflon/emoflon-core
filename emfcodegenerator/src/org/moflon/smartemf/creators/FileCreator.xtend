package org.moflon.smartemf.creators

/**
 * Common methods for all classes which generate a file.
 * @author Adrian Zwenger
 */
interface FileCreator {
	
	/**
	 * The list of invalid parameter names.
	 */
	static val blacklist = #[
		"class", "rule", "clone", "equals", "finalize",
		"getClass", "hashCode", "notify", "notifyAll", "toString", "wait",
		"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "EAttribute", "EBoolean", "EDataType", 
		"EClass", "EClassifier", "EDouble", "EFloat", "EInt", "else", "enum", "EPackage", "EReference", "EString", "extends", "final", "finally", "float", "for", "goto", "if", 
		"implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", 
		"strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
	]
	
	/**
	 * Initializes the creator and gathers all needed information for code generation.
	 * @param fq_file_path String fully qualified path and name of the file which shall be written
	 * @param IDENTION String with which the code shall be idented
	 */
	def void initialize_creator(String fq_file_path, String IDENTION)
	
	/**
	 * Starts the writing process. The file is created.
	 */
	def void write_to_file()
}
