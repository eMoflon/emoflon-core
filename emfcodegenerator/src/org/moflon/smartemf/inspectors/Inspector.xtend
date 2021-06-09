package org.moflon.smartemf.inspectors

import java.util.Set

/**
 * Interface defining the shared methods by all Inspectors
 */
interface Inspector {
	
	/**
	 * The list of invalid parameter names.
	 */
	static val blacklist = #[
		"class", "rule", "clone", "equals", "finalize",	"getClass", "hashCode", "notify", "notifyAll", "toString", "wait",
		"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "EAttribute", "EBoolean", "EDataType", 
		"EClass", "EClassifier", "EDouble", "EFloat", "EInt", "else", "enum", "EPackage", "EReference", "EString", "extends", "final", "finally", "float", "for", "goto", "if", 
		"implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", 
		"strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
	]
	
	/**
	 * returns the name, where the name is capitalised
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_name_with_first_letter_capitalized()

	/**
	 * returns the name
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_name()

	/**
	 * returns needed imports as Strings contained in an HashSet<String>
	 * @return HashSet<String>
	 * @author Adrian Zwenger
	 */
	def Set<String> get_needed_imports()

	/**
	 * Returns Enum signifying which EMF-object type is being inspected
	 * @return InspectedObjectType
	 * @author Adrian Zwenger
	 */
	def InspectedObjectType get_inspected_object_type()
}
