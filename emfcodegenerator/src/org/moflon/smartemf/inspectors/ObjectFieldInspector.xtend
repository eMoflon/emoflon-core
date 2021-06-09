package emfcodegenerator.inspectors

import java.util.HashSet
import org.eclipse.emf.ecore.EStructuralFeature
import emfcodegenerator.util.collections.EListTypeEnum
import org.eclipse.emf.ecore.EPackage

/**
 * interface for how EAttribute- and EReference-Inspector shall look like
 */
interface ObjectFieldInspector extends FeatureInspector{
	
	/**
	 * returns the needed EList-type for implementation
	 * @retuen EListTypeEnum
	 * @author Adrian Zwenger
	 */
	def EListTypeEnum get_needed_elist_type_enum()
	
	/**
	 * gets the type-name of an object field. Example: java.lang.String -> String
	 * @retuen String
	 * @author Adrian Zwenger
	 */
	def String get_object_field_type_name()
	
	/** 
	 * returns the fully qualified name for imports. example: java.util.HashMap
	 * @retuen String
	 * @author Adrian Zwenger
	 */
	def String get_fq_import_name()

	/**
	 * returns the name even if it a blacklisted name
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_unfiltered_name()	
	
	/**
	 * returns if field is a tuple and thus needs to be stored in an EList
	 * @retuen boolean
	 * @author Adrian Zwenger
	 */
	def boolean is_a_tuple()
	
	/**
	 * returns if the contained element/elements is/are unique
	 * @retuen boolean
	 * @author Adrian Zwenger
	 */
	def boolean is_unique()
	
	/**
	 * returns if the contained element/elements is/are ordered
 	 * @retuen boolean
	 * @author Adrian Zwenger
	 */
	def boolean is_ordered()
	
	/**
	 * returns if the contained element/elements is/are unsettable
	 * @retuen boolean
	 * @author Adrian Zwenger
	 */
	def boolean is_unsettable()
	
	/**
	 * returns true if a setter method needs to be generated
	 * @retuen boolean
	 * @author Adrian Zwenger
	 */
	def boolean needs_setter_method()

	/**
	 * returns if the object field is changeable
	 * @retuen boolean
	 * @author Adrian Zwenger
	 */
	def boolean is_changeable()

	/**
	 * returns the EMF specified default value of the object-field
	 * @retuen String
	 * @author Adrian Zwenger
	 */
	def String get_default_value()

	/**
	 * returns the object-field which is being inspected
	 * @return EStructuralFeature
	 * @author Adrian Zwenger
	 */
	def EStructuralFeature get_inspected_object()

	/**
	 * returns if the init-commands have been generated
	 * @return boolean
	 * @author Adrian Zwenger
	 */
	def boolean type_init_commands_are_generated()

	/**
	 * Returns the object-fields dependencies on other Epackages.
	 * @return HashSet<EPackage>
	 * @author Adrian Zwenger
	 */
	def HashSet<EPackage> get_meta_model_package_dependencies()

	/**
	 * Returns the variable name which the inspected object-field gets in the Literals sub-interface
	 * of the EMF-package class
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_emf_package_literals_interface_var_name()
}
