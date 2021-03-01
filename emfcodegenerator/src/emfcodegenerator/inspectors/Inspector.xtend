package emfcodegenerator.inspectors

import java.util.Set

/**
 * Interface defining the shared methods by all Inspectors
 */
interface Inspector {
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