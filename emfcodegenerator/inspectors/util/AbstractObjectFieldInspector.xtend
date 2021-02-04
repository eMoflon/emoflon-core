package emfcodegenerator.inspectors.util

import emfcodegenerator.EMFCodeGenerationClass
import org.eclipse.emf.ecore.EStructuralFeature
import emfcodegenerator.EcoreGenmodelParser
import java.util.ArrayList
import emfcodegenerator.EListTypeEnum
import java.util.LinkedList
import org.eclipse.emf.ecore.EGenericType
import java.util.HashSet
import org.eclipse.emf.ecore.impl.EPackageImpl
import emfcodegenerator.MultiplicityEnum
import org.eclipse.emf.ecore.ETypeParameter
import java.util.HashMap
import emfcodegenerator.EGenericTypeProcessor
import org.eclipse.emf.ecore.EClass
import emfcodegenerator.inspectors.ObjectFieldInspector

abstract class AbstractObjectFieldInspector extends EMFCodeGenerationClass implements ObjectFieldInspector{

	/**########################Attributes########################*/

	/**
	 * The inspected EStructuralFeature (EAttribute or EReference)
	 */
	var EStructuralFeature e_feature
	
	/**
	 * contains the declarations and initialisations to create the Attributes meta model in the
	 * Package.java file of the EMF project
	 */
	protected var ArrayList<String> type_init_commands = null
	
	/**
	 * the EGenericType's are generically named with this String and an counter appended
	 * starting at zero
	 */
	protected var String type_init_variable_name = "generic_parameter_"
	
	/**
	 * counter for the EGenericType variable names
	 */
	protected var static int  type_init_variable_count = 0

	/**
	 * flag which stores if the init-commands have been generated
	 */
	protected var boolean type_init_commands_are_generated = false

	/**
	 * stores which EList-type is needed to store this EReference
	 */
	protected var EListTypeEnum needed_elist_type = EListTypeEnum.NONE

	/**
	 * boolean representing if this reference needs a setter method
	 */
	protected var boolean needs_setter_method = true

	/**
	 * contains needed declarations of generic types to create the attributes meta model
	 */
	protected var LinkedList<EGenericType> generic_type_declarations = new LinkedList<EGenericType>()

	/**
	 * stores the EPackages on which this Attribute depends.
	 */
	protected var HashSet<EPackageImpl> meta_model_package_dependencies = new HashSet<EPackageImpl>()

	/**
	 * stores the needed import string for this EReference if it is to be used
	 */
	protected var String fq_import_name

	protected var String object_field_type_name
	
	protected var String default_value = "null"

	protected var boolean this_feautures_datatype_is_composed_of_multiple_generic_sub_types = false
	
	protected var HashMap<EGenericType,String> generictype_to_var_name_for_init_code_map =
		new HashMap<EGenericType,String>()

	protected var String generic_feauturetype_classifier_var_name = null

	/**########################Constructor########################*/
	
	new(EStructuralFeature e_feature, EcoreGenmodelParser gen_model){
		super(gen_model)
		this.init(e_feature)
	}
	
	new(EStructuralFeature e_feature, String super_package_name){
		super(super_package_name)
		this.init(e_feature)
	}
	
	/**
	 * sets following class attributes according to the EStruicturalFeature's qualities
	 * object_field_type_name, needs_setter_method, needed_elist_type*/
	private def void init(EStructuralFeature e_feature){
		this.e_feature = e_feature
		this.needed_elist_type = get_needed_elist_type(this.is_ordered(), this.is_unique())
		
		switch(e_feature.upperBound){
			case MultiplicityEnum.SINGLE_ELEMENT: {
				this.needs_setter_method = e_feature.isChangeable
			}
			case MultiplicityEnum.UNBOUNDED: {
				//unbounded fields do not get setters
				this.needs_setter_method = false
			}
			case MultiplicityEnum.UNSPECIFIED: {
				this.needs_setter_method = e_feature.isChangeable
			}
			case e_feature.upperBound > 1: {
				//although the changeable value is true EMF does not create a setter
				this.needs_setter_method = false
			}
			default: {
				println("unspecified boundaries for object field " + e_feature.name)
				throw new IllegalArgumentException(
					"Malformed upperBound for " + "EStructuralFeatureImpl: " + e_feature.name +
					" in EClass " +  e_feature.containerClass.name
					)
			}
		}
		if(e_feature.many) this.add_import_as_String("org.eclipse.emf.common.util.EList")
		this.object_field_type_name = this.register_full_object_field_type(this.e_feature.EGenericType, "") 
	}

	/**########################Shared Methods########################*/

	/**
	 * does the same as the overriden method, however it parses EUpperBound and ELowerBound and registers needed 
	 * objects for generation
	 */
	override register_full_object_field_type(EGenericType e_type, String declaration){
		//get generictype import string
		var String new_declaration = declaration
		//check if it is a generic type
		if(e_type.ETypeParameter !== null){
			var ETypeParameter generic_parameter = e_type.ETypeParameter
			new_declaration = declaration + generic_parameter.name
		}else if (e_type.EClassifier === null && e_type.ETypeParameter === null){
			//a proper type was not specified
			new_declaration += "?"
		} else if(e_type.ERawType.instanceTypeName !== null){
			var fq_import_string = e_type.ERawType.instanceTypeName
			//the EMF EList is to be replaced
			var buffer = fq_import_string.split("\\.")
			new_declaration = new_declaration + buffer.get(buffer.length -1)
			add_import(e_type.ERawType)
		} else {
			new_declaration = new_declaration + e_type.ERawType.name
			add_import(e_type.ERawType)
		}
		
		if(e_type.EUpperBound !== null || e_type.ELowerBound !== null){
			var e_bound = (e_type.EUpperBound !== null) ? e_type.EUpperBound : e_type.ELowerBound

			new_declaration += (e_type.EUpperBound !== null) ? " extends " : " super "
			var String param_name
			if(e_bound.EClassifier !== null){
				if(e_bound.EClassifier.instanceClass === null){
					//custom defined class (defined by ecore)
					param_name = e_bound.EClassifier.name
					this.add_import(e_bound.EClassifier)
				} else {
					//EDataType or EMF-Class --> get name from the instance-class
					var class_name = e_bound.EClassifier.instanceClass.name
					if(class_name.contains(".")){
						var buffer = class_name.split("\\.")
						param_name = buffer.get(buffer.size() - 1)
					} else param_name = class_name
				}
				new_declaration += param_name	
			} else if (e_bound.ETypeParameter !== null){
				new_declaration += e_bound.ETypeParameter.name
			}
		}
		
		
		//return if current etype does not contain more generictypes
		if(e_type.ETypeArguments.isEmpty()) return new_declaration
		//object_field_is_nested_type = true
		//if it does recursively iterate over all register them too
		var sub_e_type_iterator = e_type.ETypeArguments.iterator
		new_declaration += "<"
		while(sub_e_type_iterator.hasNext){
			var sub_e_type = sub_e_type_iterator.next()
			new_declaration = this.register_full_object_field_type(sub_e_type, new_declaration)
			if(sub_e_type_iterator.hasNext) new_declaration += ","
			this.generic_type_declarations.addFirst(sub_e_type)
		}
		new_declaration += ">"
		return new_declaration
	}

	/**
	 * returns the name of the EStructuralFeature type
	 * example:  ArrayList<MyAttribute<?>>
	 */
	//def String
	override get_object_field_type_name() {
		return object_field_type_name
	}

	/** returns if field is a tuple and thus needs to be stored in an EList*/
	//def boolean
	override is_a_tuple(){
		return this.e_feature.isMany
	}
	
	/** returns if the contained element/elements is/are unique */
	//def boolean
	override is_unique(){
		return this.e_feature.isUnique
	}
	
	/** returns if the contained element/elements is/are ordered */
	//def boolean
	override is_ordered(){
		return this.e_feature.isOrdered
	}
	
	/** returns if the contained element/elements is/are unsettable */
	//def boolean
	override is_unsettable(){
		return this.e_feature.isUnsettable
	}
	
	/** returns true if a setter method needs to be generated*/
	//def boolean
	override needs_setter_method(){
		return this.needs_setter_method
	}

	/**
	 * returns if the object field is changeable
	 */
	//def boolean
	override is_changeable(){
		return this.e_feature.isChangeable
	}
	
	/**
	 * returns the EStructuralFeature which is being inspected
	 * @return EStructuralFeature
	 */
	//def EStructuralFeature 
	override get_inspected_object(){
		return this.e_feature
	}

	/**
	 * returns an ArrayList where each entry represents a line of code needed to initialize
	 * the EAttribute in the Package-class
	 * @return ArrayList<String>
	 */
	//def ArrayList<String> 
	override get_type_init_commands(){
		if(this.type_init_commands_are_generated) return this.type_init_commands
		return null
	}

	/**
	 * returns if the init-commands have been generated
	 * @return boolean
	 */
	//def boolean
	override type_init_commands_are_generated(){
		return this.type_init_commands_are_generated
	}
	
	/**
	 * returns a HashSet filled with EPackages on which this Feature depends during initialization 
	 * in the package class 
	 * @return HashSet<EPackageImpl>
	 */
	//def HashSet<EPackageImpl> 
	override get_meta_model_package_dependencies(){
		return this.meta_model_package_dependencies
	}

	/** returns the needed EList-type for implementation*/
	//def EListTypeEnum 
	override get_needed_elist_type_enum(){
		return this.needed_elist_type
	}

	override get_name(){
		return this.e_feature.name
	}

	/**
	 * returns the attributes name where the first letter us capitalised
	 * @return String
	 */
	override get_name_with_first_letter_capitalized(){
		return this.e_feature.name.substring(0, 1).toUpperCase() + this.e_feature.name.substring(1)
	}

	/**
	 * returns the EMF specified default value of the object-field
	 * @return String
	 */
	//def String 
	override get_default_value(){
		return this.default_value
	}

	protected def ArrayList<String> generate_needed_generic_types_for_init_code(){
		if(AbstractObjectFieldInspector.emf_model === null)
			throw new IllegalAccessException(
			"Cannot create Init-Code without valid EcoreGenmodelParser instance."
			)
		var body = new ArrayList<String>()
		
		if(!generic_type_declarations.isEmpty){
			this.this_feautures_datatype_is_composed_of_multiple_generic_sub_types = true
			var the_container_eclass = this.e_feature.eContainer as EClass
			var type_params_to_var_name = AbstractObjectFieldInspector.emf_model.get_generic_type_to_var_name_map_for_eclass(the_container_eclass)
			
			var generic_type_processor = new EGenericTypeProcessor(
				AbstractObjectFieldInspector.emf_model, 
				"generic_type_",
				AbstractObjectFieldInspector.emf_model.get_packages_to_package_inspector_map.get(
					(this.e_feature.eContainer as EClass).EPackage
				)
			)
			
			var generic_bounds = 
				generic_type_processor.traverse_generic_bounds_for_object_fields(
					this.e_feature.EGenericType, new LinkedList<EGenericType>(), the_container_eclass
				)
			
			var iterator = generic_bounds.descendingIterator
			
			while(iterator.hasNext){
				var generic = iterator.next
				if(
					!type_params_to_var_name.containsKey(generic.ETypeParameter)
				){
					var entry = new StringBuilder("EGenericType ")
					entry.append(generic_type_processor.generic_bound_to_var_name_map.get(generic))
					entry.append(" = createEGenericType(")
					entry.append(generic_type_processor.get_eclassifier_getter_command_for_egenerictype(generic as EGenericType))
					entry.append(");")
					body.add(entry.toString)
				}
			}
			
			//for EstructuralFeatures the top-most element in the EGenericType-hierarchy must not be
			//processed.
			var top_most_element = generic_bounds.removeFirst
			//var top_most_element = generic_bounds.first
			this.generic_feauturetype_classifier_var_name =
				generic_type_processor.generic_bound_to_var_name_map.get(top_most_element)
			iterator = generic_bounds.descendingIterator

			while(iterator.hasNext){
				var generic = iterator.next
				body.add(
					generic_type_processor.create_egeneric_type_bound_set_up_command_for_object_fields(
						generic,
						type_params_to_var_name
					)
				)
			}
		}
		
		return body
	}

	//def String
	/**
	 * All EStructuralFeatures have an entry in the Literals interface in their respective
	 * package class. This method returns the designated variable name which is used in said
	 * interface.<br>
	 * example: <em>CLASS_A_MY_FEATURE</em> is the entry for a feature called myFeature contained in ClassA
	 * @return String
	 * @author Adrian Zwenger
	 */
	override get_emf_package_literals_interface_var_name(){
	return
'''«emf_to_uppercase((this.e_feature.eContainer as EClass).name)»_«emf_to_uppercase(this.get_name)»'''
	}

	/**########################Abstract Methods########################*/

	/** returns the fully qualified name for imports. example: java.util.HashMap */
	//abstract def String
	override get_fq_import_name(){
		return this.fq_import_name
	}

	/**########################General Methods########################*/
	
	override boolean equals(Object other){
		if(!(other instanceof AbstractObjectFieldInspector)) return false
		return this.e_feature.equals((other as AbstractObjectFieldInspector).e_feature)
	}

	override int hashCode(){
		return this.e_feature.hashCode()
	}
}