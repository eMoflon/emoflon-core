package emfcodegenerator.inspectors

import emfcodegenerator.EcoreGenmodelParser
import java.util.ArrayList

/**
 * Interface defining the shared methods in between EAttribute-, EReference- and
 * EOperation-Inspectors
 */
interface FeatureInspector extends Inspector{
		
	/**
	 * The Package class creates getters for all Object types which need to be generated.
	 * This method shall do that.
	 * Example: "EReference getclassA_Reference_to_class_b();"
	 * @return String
	 */
	def String get_getter_method_declaration_for_the_package_classes()
	
	/**
	 * The Package class creates getters for all Object types which need to be generated.
	 * This method shall do that.
	 * Example: "getclassA_Reference_to_class_b();"
	 * @return String
	 */
	def String get_getter_method_declaration_for_the_package_classes__stump_only()
	/**
	 * The Package-interface has a sub-interface called Literals. Each feature has an entry there.
	 * This method generates that entry
	 */
	def String get_literals_entry_for_package_classes()
	
	/**
	 * generates the needed code needed to initialize
	 * the EAttribute/EReference in the Package-class
	 * if the Inspector was initialized witout an EcoreGenmodelParser, one needs be passed. Thus
	 * passing a parser is mandatory
	 * @param EcoreGenmodelParser
	 */
	def void generate_init_code_for_package_class(EcoreGenmodelParser gen_model)
	
	/**
	 * returns an ArrayList where each entry represents a line of code needed to initialize
	 * an EAttribute, EReference or EOperation in the Package-class
	 * @return ArrayList<String>
	 */
	def ArrayList<String> get_type_init_commands()
	
}
