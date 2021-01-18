package emfcodegenerator.inspectors

import java.util.HashSet

/**
 * Interface defining the shared methods by all Inspectors
 */
interface Inspector {
	/** returns the name, where the name is capitalised*/
	def String get_name_with_first_letter_capitalized()

	def String get_name()
	
	def HashSet<String> get_needed_imports()

	def InspectedObjectType get_inspected_object_type()
}