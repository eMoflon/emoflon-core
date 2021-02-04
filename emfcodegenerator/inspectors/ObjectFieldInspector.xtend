package emfcodegenerator.inspectors

import emfcodegenerator.EListTypeEnum
import java.util.HashSet
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.impl.EPackageImpl

/**
 * interface for how EAttribute- and EReference-Inspector shall look like
 */
interface ObjectFieldInspector extends FeatureInspector{
	/** returns the needed EList-type for implementation*/
	def EListTypeEnum get_needed_elist_type_enum()
	
	/** gets the type-name of an object field. Example: java.lang.String -> String */
	def String get_object_field_type_name()
	
	/** returns the fully qualified name for imports. example: java.util.HashMap */
	def String get_fq_import_name()

	/** returns if field is a tuple and thus needs to be stored in an EList*/
	def boolean is_a_tuple()
	
	/** returns if the contained element/elements is/are unique */
	def boolean is_unique()
	
	/** returns if the contained element/elements is/are ordered */
	def boolean is_ordered()
	
	/** returns if the contained element/elements is/are unsettable */
	def boolean is_unsettable()
	
	/** returns true if a setter method needs to be generated*/
	def boolean needs_setter_method()

	/**
	 * returns if the object field is changeable
	 */
	def boolean is_changeable()

	/**
	 * returns the EMF specified default value of the object-field
	 * @retuen String
	 */
	def String get_default_value()

	/**
	 * returns the object-field which is being inspected
	 */
	def EStructuralFeature get_inspected_object()

	/**
	 * returns an ArrayList where each entry represents a line of code needed to initialize
	 * the EAttribute in the Package-class
	 * @return ArrayList<String>
	 */
	//def ArrayList<String> get_type_init_commands()
	
	/**
	 * returns if the init-commands have been generated
	 * @return boolean
	 */
	def boolean type_init_commands_are_generated()

	def HashSet<EPackageImpl> get_meta_model_package_dependencies()

	/**
	 * stuff */
	def String get_emf_package_literals_interface_var_name()
}
