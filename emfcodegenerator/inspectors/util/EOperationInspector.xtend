package emfcodegenerator.inspectors.util

import emfcodegenerator.EMFCodeGenerationClass
import emfcodegenerator.EcoreGenmodelParser
import emfcodegenerator.inspectors.InspectedObjectType
import java.util.ArrayList
import java.util.HashMap
import org.eclipse.emf.ecore.EGenericType
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EParameter
import org.eclipse.emf.ecore.ETypeParameter
import org.eclipse.emf.ecore.impl.EOperationImpl
import emfcodegenerator.inspectors.FeatureInspector
import org.eclipse.emf.ecore.EClass
import emfcodegenerator.EGenericTypeProcessor
import java.util.LinkedList
import java.util.HashSet
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EEnum

class EOperationInspector extends EMFCodeGenerationClass implements FeatureInspector{

	/**########################Attributes########################*/

	/**
	 * The EOPeration object to be inspected
	 */
	var EOperationImpl e_op
	
	/**
	 * stores the return type as a String. The default is "void".
	 */
	var String return_value = "void"

	/**
	 * stores the method parameters in a map. The parameter name is the key as names need to be
	 * unique but their type not
	 */
	var HashMap<String, String> operation_parameters = new HashMap<String, String>()

	/**
	 * ArrayList storing the parameter names as String
	 */
	var ArrayList<String> operation_parameters_names = new ArrayList<String>()

	/**
	 * stores the generic parameters as a ArrayList<String>
	 */
	var ArrayList<String> generic_parameters = new ArrayList<String>()

	/**
	 * Stores the Exception which can be thrown as Strings
	 */
	 var ArrayList<String> method_throwables = new ArrayList<String>()

	/**
	 * Stores the method declaration as String
	 */
	var String method_declaration

	/**
	 * stores the suffix for the EOperation getter method.<br>
	 * Example: (param1_type)_(param2_type)_(param3_type)
	 */
	var String getter_method_suffix = ""

	/**
	 * stores true if methods have already been generated
	 */
	var boolean getter_method_declaration_for_the_package_classes_is_generated
	
	/**
	 * stores the actual declaration as String
	 */
	var String getter_method_declaration_for_the_package_classes
	
	/**
	 * stores the LOC's needed in the Package classes
	 * "public void initializePackageContents()" method
	 */
	var ArrayList<String> type_init_commands = new ArrayList<String>()
	
	/**
	 * stores if the LOC's needed in the Package classes
	 * "public void initializePackageContents()" method have been generated
	 */
	var boolean type_init_commands_are_generated = false

	/**
	 * stores the static prefix which generated EOperation's variable names are composed of
	 * "public void initializePackageContents()" : prefix
	 */
	var static String init_code_var_name = "eoperation_"
	
	/**
	 * stores the static prefix which generated EOperation's variable names are composed of
	 * "public void initializePackageContents()" : suffix
	 */
	var static int init_code_var_index = 0
	
	/**
	 * stores the static prefix which generated EOperation's variable names are composed of
	 * "public void initializePackageContents()" : actual String for this instance
	 */
	var String my_init_code_var_name

	/**
	 * stores the EPackages on which this Attribute depends.
	 */
	protected var HashSet<EPackage> meta_model_package_dependencies = new HashSet<EPackage>()

	/**########################Constructor########################*/

	/**
	 * Constructs a new EOperationInspector
	 */
	new(EOperation operation, EcoreGenmodelParser gen_model) {
		super(gen_model)
		this.e_op = operation as EOperationImpl
		//get return_value name
		if(e_op.EGenericType !== null){
			//set return value name and add dependencies to import list
			return_value = this.register_full_object_field_type(e_op.EGenericType, "", false)
			
		}
		//get Parameters
		for(EParameter param : e_op.EParameters){
			operation_parameters_names.add(param.name)
			operation_parameters.put(param.name,
				this.register_full_object_field_type(param.EGenericType, "", false))
		}
		//get generics 
		for(ETypeParameter type_parameter : e_op.ETypeParameters) {
			//generic_parameters.add(type_parameter.name)
			var declaration = ""
			//this.register_full_object_field_type(generic., "")//
			declaration += type_parameter.name
			if(!type_parameter.EBounds.empty){
				declaration += " extends "
				var iterator = type_parameter.EBounds.iterator
				while(iterator.hasNext){
					var next_item = iterator.next
					declaration += this.register_full_object_field_type(next_item, "", true)
					if(iterator.hasNext) declaration += " & "
				}
			}
			generic_parameters.add(declaration)
		}
		//get throwables
		for(EGenericType exception : e_op.EGenericExceptions){
			method_throwables.add(this.register_full_object_field_type(exception, "", false))
		}
		this.method_declaration = create_method_signiture()
		create_getter_method_suffix()
		this.my_init_code_var_name = init_code_var_name + init_code_var_index++
	}
	
	/**
	 * does the same as the same-name method which is inherited, however it parses EBounds and registers needed 
	 * objects for generation
	 */
	def String register_full_object_field_type(EGenericType e_type, String declaration, boolean traverse_ebounds_to){
		//get generictype import string
		var String new_declaration = declaration
		//check if it is a generic type
		if(e_type.ETypeParameter !== null){
			var ETypeParameter type_parameter = e_type.ETypeParameter
			new_declaration += type_parameter.name
			if(!type_parameter.EBounds.empty && traverse_ebounds_to){
				new_declaration += " extends "
				var iterator = type_parameter.EBounds.iterator
				while(iterator.hasNext){
					var next_item = iterator.next
					new_declaration += this.register_full_object_field_type(next_item, "", traverse_ebounds_to)
					if(iterator.hasNext) new_declaration += " & "
				}
			}
		}else if (e_type.EClassifier === null && e_type.ETypeParameter === null){
			//a proper type was not specified
			new_declaration += "?"
		} else if(e_type.ERawType.instanceTypeName !== null){
			var fq_import_string = e_type.ERawType.instanceTypeName
			//the EMF EList is to be replaced
			var buffer = fq_import_string.split("\\.")
			new_declaration = new_declaration + buffer.get(buffer.length -1)
			this.add_import(e_type.ERawType)
		} else {
			new_declaration = new_declaration + e_type.ERawType.name
			this.add_import(e_type.ERawType)
		}
		
		if(e_type.EUpperBound !== null || e_type.ELowerBound !== null){
			var e_bound = (e_type.EUpperBound !== null) ? e_type.EUpperBound : e_type.ELowerBound

			new_declaration += (e_type.EUpperBound !== null) ? " extends " : " super "
			var String param_name
			if(e_bound.EClassifier !== null){
				this.add_import(e_bound.EClassifier)
				if(e_bound.EClassifier.instanceClass === null){
					//custom defined class (defined by ecore)
					param_name = e_bound.EClassifier.name
					
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
			new_declaration = this.register_full_object_field_type(sub_e_type, new_declaration, traverse_ebounds_to)
			if(sub_e_type_iterator.hasNext) new_declaration += ","
		}
		new_declaration += ">"
		return new_declaration
	}

	/**########################Methods########################*/

	/**
	 * returns the method signature as a String
	 */
	def private String create_method_signiture(){
		var declaration = '''«return_value» «e_op.name»('''.toString
		var new_declaration = "<"
		var iterator = generic_parameters.iterator()
		
		if(!this.generic_parameters.isEmpty){
			while(iterator.hasNext()){
				var generic_name = iterator.next
				new_declaration += (iterator.hasNext) ? generic_name + "," : generic_name
			}
			declaration = new_declaration + "> " + declaration
		}
		iterator = operation_parameters_names.iterator
		while(iterator.hasNext){
			var key = iterator.next
			declaration += '''«operation_parameters.get(key)» «key»'''.toString
			if(iterator.hasNext) declaration += ", "
		}
		declaration += ")"

		if(method_throwables.isEmpty) return declaration
		
		declaration += " throws "
		iterator = method_throwables.iterator()
		while(iterator.hasNext){
			declaration += iterator.next
			if(iterator.hasNext) declaration += ", "
		}
		return declaration
	}

	/**
	 * returns the method declaration
	 */
	def String get_method_declaration(){
		return method_declaration
	}

	/**
	 * returns a map where the key is the methods declaration and the value is the implementation
	 */
	def HashMap<String,String> get_method_implementation(String IDENTION){
		var HashMap<String,String> map = new HashMap<String,String>()
		map.put(IDENTION +'''public «get_method_declaration»'''.toString, 
				IDENTION + IDENTION + '''throw new UnsupportedOperationException("Not Implemented");'''.toString)
		return map
	}
	
	/**
	 * returns the EOperation's name with the first letter capitalized */
	override get_name_with_first_letter_capitalized() {
		return this.e_op.name.substring(0,1).toUpperCase + this.e_op.name.substring(1)
	}
	
	/**
	 * returns the name which the EOperation has
	 */
	override get_name() {
		this.e_op.name
	}

	/**
	 * returns an Enum identifying this inspector as an EOperationInspector
	 */
	override get_inspected_object_type() {
		return InspectedObjectType.EOPERATION
	}

	/**
	 * returns true if this EOPeration has a return type
	 */
	def boolean has_return_value(){
		return this.e_op.EGenericType !== null
	}
	
	/**
	 * EOperations have the input type for their parameters as a suffix.
	 * This method returns and generates that suffix
	 */
	def private String create_getter_method_suffix(){
		if(this.getter_method_suffix !== null) return this.getter_method_suffix
		var iterator = this.e_op.EParameters.iterator
		if(iterator.hasNext){
			var entry = new StringBuilder("_")
			while(iterator.hasNext){
				var parameter = iterator.next
				if(parameter.EGenericType.EClassifier !== null){
					if(parameter.EGenericType.EClassifier.instanceClass === null){
						//custom defined class (defined by ecore)
						entry.append(parameter.EGenericType.EClassifier.name)
					} else {
						//EDataType or EMF-Class --> get name from the instance-class
						var class_name = parameter.EGenericType.EClassifier.instanceClass.name
						if(class_name.contains(".")){
							var buffer = class_name.split("\\.")
							entry.append(buffer.get(buffer.size() - 1))
						} else entry.append(class_name)
					}
				} else if(parameter.EGenericType.ERawType.instanceClass !== null) {
					var class_name = parameter.EGenericType.ERawType.instanceClass.class.name
					if(class_name.contains(".")){
						var buffer = class_name.split("\\.")
						entry.append(buffer.get(buffer.size() - 1))
					} else entry.append(class_name)
				}
				if(iterator.hasNext) entry.append("_")
			}
			this.getter_method_suffix = entry.toString
		} else this.getter_method_suffix = ""
		return this.getter_method_suffix
	}
	
	/**
	 * returns the getter method declaration
	 */
	override get_getter_method_declaration_for_the_package_classes() {
		if(this.getter_method_declaration_for_the_package_classes_is_generated)
			return "EOperation " + this.getter_method_declaration_for_the_package_classes
		var entry = this.get_getter_method_declaration_for_the_package_classes__stump_only()
		return "EOperation " + entry.toString()
	}
	
	/**
	 * returns a declaration stump only. Example: getclassA___myop()
	 */
	override get_getter_method_declaration_for_the_package_classes__stump_only(){
		if(this.getter_method_declaration_for_the_package_classes_is_generated)
			return this.getter_method_declaration_for_the_package_classes
		var entry = new StringBuilder("get")
		entry.append((this.e_op.eContainer as EClass).name)
		entry.append("__")
		entry.append(this.get_name_with_first_letter_capitalized())
		entry.append(this.create_getter_method_suffix())
		entry.append("()")
		this.getter_method_declaration_for_the_package_classes = entry.toString
		this.getter_method_declaration_for_the_package_classes_is_generated = true
		return entry.toString()
	}

	/**
	 * returns the entry in the Literals sub-interface in the Interface for the Package-class
	 */
	override get_literals_entry_for_package_classes() {
		var entry = new StringBuilder("EOperation ")
		entry.append(emf_to_uppercase((this.e_op.eContainer as EClass).name))
		entry.append("___")
		entry.append(emf_to_uppercase(this.get_name))
		entry.append(" = eINSTANCE.")
		entry.append(this.get_getter_method_declaration_for_the_package_classes__stump_only())
		entry.append(";")
		return entry.toString()
	}

	/**
	 * generates the code to init EOperations for "public void initializePackageContents()"
	 */
	override generate_init_code_for_package_class(EcoreGenmodelParser gen_model) {
		//do no regenerate if code has already been generated
		if(this.type_init_commands_are_generated === true) return;
		this.type_init_commands_are_generated = true
		
		//use new Parser only if is really a new one
		if(gen_model !== null && !gen_model.equals(EOperationInspector.emf_model))
			EOperationInspector.emf_model = gen_model
		else if(gen_model === null && EOperationInspector.emf_model === null)
			//throw Exception if this instance is invalid
			throw new IllegalArgumentException()

		var body = new ArrayList<String>() //ArrayList which will contain all code
		body.add("//creating EOperation: " + this.method_declaration)
		var type_parameter_creation_block = new ArrayList<String>()
		//contains code in which type params and similar are declared and created
		//must precede other code

		var type_parameter_set_up_block = new ArrayList<String>()
		//contains code in which the declared objects in type_parameter_creation_block are properly
		//set up and bounded if need be by using a EGenericTypeProcessor

		var StringBuilder entry

		/*
		 * initEOperation(
		 * 		EOperation eOperation, <-- get from package
		 * 		EClassifier type,	<-- return type EClassifier
		 * 		String name,	<-- get from EOPeration itself
		 * 		int lowerBound, int upperBound, boolean isUnique,
		 * 		boolean isOrdered
		 * )
		 */

		//do basic set up which is always done.
		entry = new StringBuilder("EOperation ")
		entry.append(this.my_init_code_var_name)
		entry.append(" = initEOperation(")
		entry.append(this.get_getter_method_declaration_for_the_package_classes__stump_only())
		entry.append(", null, ")
		entry.append("\"" + this.e_op.name + "\", ")
		entry.append(this.e_op.lowerBound)
		entry.append(", ")
		entry.append(this.e_op.upperBound)
		entry.append(", ")
		entry.append(((this.e_op.isUnique) ? "" : "!") + "IS_UNIQUE, ")
		entry.append(((this.e_op.isOrdered) ? "" : "!") + "IS_ORDERED")
		entry.append(");")
		body.add(entry.toString)

		//###############################create ETypeParameters (generics of method)
		var etype_to_var_name_map = new  HashMap<ETypeParameter,String>()
		var container_class = this.e_op.eContainer as EClass
		
		//EGenericTypeProcessor is used to traverse EBounds, register them and generate code to
		//either declare or set them up
		var bound_processor = new EGenericTypeProcessor(
			EOperationInspector.emf_model,
			"e_op_type_param_bound_",
			EOperationInspector.emf_model.get_packages_to_package_inspector_map.get(
				container_class.EPackage
			)
		)
		//register the EOperations ETypeParameters
		for(etype_param : this.e_op.ETypeParameters){
			//declaration and instancing of parameter
			entry = new StringBuilder("ETypeParameter ")
			//create unique name
			var param_var_name = EOperationInspector.init_code_var_name +
								 etype_param.name + "_" +
								 EOperationInspector.init_code_var_index++
	    	entry.append(param_var_name)
	    	entry.append(" = addETypeParameter(")
	    	entry.append(this.get_getter_method_declaration_for_the_package_classes__stump_only())
	    	entry.append(", ")
	    	entry.append('''"«etype_param.name»");'''.toString)
	    	//add to the creation block
	    	type_parameter_creation_block.add(entry.toString)
	    	//add the ETypeparameter and its newly generated variable name to registry
			etype_to_var_name_map.put(etype_param, param_var_name)
		}

		//create Bound-objects and generate the set up code for all the previously registered
		//ETypeParameters
		for(type_param : etype_to_var_name_map.keySet){
			for(bound : type_param.EBounds){
				//traverse bound hierarchy and store as LinkedList which retains the hierarchy in a
				//flattened form
				var generic_bounds =
					bound_processor.traverse_generic_bounds(bound, new LinkedList<EGenericType>())
				//read bottom up as Bounds need to be set bottom up
				var bounds_iterator = generic_bounds.descendingIterator
				while(bounds_iterator.hasNext){
					var current_bound = bounds_iterator.next
					var String get_classifier_command = ""
					if(
						bound_processor.generic_bound_to_var_name_map.keySet.contains(current_bound)
					){
						//create the command which gets the defining EClassifier for the bound
						get_classifier_command = bound_processor
							.get_eclassifier_getter_command_for_egenerictype(current_bound)
						
						var var_name = 
							bound_processor.generic_bound_to_var_name_map.get(current_bound)
						//create the generic
						//add the newly created EGenericType to the creation code-block
						type_parameter_creation_block.add(
							this.generate_egenerictype_declaration_entry(
								var_name, get_classifier_command
							)
						)
					}
					//generate the set_up code for the bound
					type_parameter_set_up_block.add(
						bound_processor.create_egeneric_type_bound_set_up_command(
							current_bound, etype_to_var_name_map, type_param
						)
					)
				}
			}
		}
		
		//###############################create the return value
		
		var return_obj = this.e_op.EGenericType
		//traverse hierarchy for the return_obj
		var generic_bounds = bound_processor.traverse_generic_bounds_for_object_fields(
			return_obj,
			new LinkedList<EGenericType>(),
			this.e_op,
			false
		)

		var return_obj_var_name = bound_processor.generic_bound_to_var_name_map.get(return_obj)

		var bounds_iterator = generic_bounds.descendingIterator
		while(bounds_iterator.hasNext){
			var current_bound = bounds_iterator.next
			var String get_classifier_command = ""
			if(bound_processor.generic_bound_to_var_name_map.keySet.contains(current_bound)){
				//create the command which gets the defining EClassifier for the type/generic
				get_classifier_command =
					bound_processor.get_eclassifier_getter_command_for_egenerictype(current_bound)
				
				var var_name = bound_processor.generic_bound_to_var_name_map.get(current_bound)
				//create the generic
				//add the newly created EGenericType to the creation code-block
				type_parameter_creation_block.add(
					this.generate_egenerictype_declaration_entry(var_name, get_classifier_command)
				)
			}
			//generate the set_up code for the bound as long as it has not reached the return_obj
			//yet. as set up of it must be handled individually
			if(!current_bound.equals(return_obj))
				type_parameter_set_up_block.add(
					bound_processor.create_egeneric_type_bound_set_up_command_for_object_fields(
						current_bound, etype_to_var_name_map
					)
				)
		}
		
		type_parameter_set_up_block.add(
			"initEOperation(" + this.my_init_code_var_name + ", " + return_obj_var_name + ");"
		)
		
		//###############################create Parameters of method
		/*	addEParameter(
		 * 		EOperation owner, EClassifier type, java.lang.String name,
		 * 		int lowerBound, int upperBound, 
		 * 		boolean isUnique, boolean isOrdered)
		 */
		/*
		 * addEParameter(
		 * 		EOperation owner, EGenericType type, java.lang.String name,
		 * 		int lowerBound, int upperBound,
		 * 		boolean isUnique, boolean isOrdered) 
		 */
		var the_containing_eclass = this.e_op.eContainer as EClass
		var the_etype_parameters_of_the_containing_eclass_to_var_name_map = 
			EOperationInspector.emf_model
				.get_generic_type_to_var_name_map_for_eclass(the_containing_eclass)
		for(parameter : this.e_op.EParameters){
			var first_part_of_command = new StringBuilder("addEParameter(")
			var rest_of_command = new StringBuilder()
				rest_of_command.append('''"«parameter.name»", ''')
				rest_of_command.append(parameter.lowerBound)
				rest_of_command.append(", ")
				rest_of_command.append(parameter.upperBound)
				rest_of_command.append(((parameter.isUnique) ? ", " : ", !") + "IS_UNIQUE, ")
				rest_of_command.append(((parameter.isOrdered) ? "" : "!") + "IS_ORDERED);")
			
			first_part_of_command.append(this.my_init_code_var_name)
			first_part_of_command.append(", ")
			if(parameter.EGenericType.ETypeParameter !== null){
				//case a) param is a ETypeParameter
				first_part_of_command.append("createEGenericType(")
				if(etype_to_var_name_map.containsKey(parameter.EGenericType.ETypeParameter)){
					//it is an ETypeParameter previously created for this EOperation
					first_part_of_command.append(
						etype_to_var_name_map.get(
							parameter.EGenericType.ETypeParameter
						)
					)
				} else if(
					the_etype_parameters_of_the_containing_eclass_to_var_name_map
						.containsKey(parameter.EGenericType.ETypeParameter)
				){
					//it is am ETypeParameter previously created for the container EClass of this
					//EOperation
					first_part_of_command.append(
						the_etype_parameters_of_the_containing_eclass_to_var_name_map
							.get(parameter.EGenericType.ETypeParameter)
					)
				} else {
					throw new UnsupportedOperationException("Unsupported EParameter type.")
				}
				first_part_of_command.append("), ")
				type_parameter_set_up_block.add(
					first_part_of_command.toString +
					rest_of_command.toString
				)
			} else if(parameter.EGenericType.EClassifier !== null) {
				//case b) param is an EClass, either EMF or user specified
				var the_classifier = parameter.EGenericType.EClassifier
				var String classifier_getter_method =
					this.get_eclassifier_getter_method_for_eclass(the_classifier)
				
				//add generic type params or directly add parameter
				//EClassifiers as EParameter's can have ETypeArguments and those arguments can have
				//Arguments themselves and or EBounds (hierarchy needs to be traversed)
				if(!parameter.EGenericType.ETypeArguments.isEmpty){
					//the classifier needs parameters which need to be traversed
					var bounds_list =
						bound_processor.traverse_generic_bounds_for_object_fields(
							parameter.EGenericType, new LinkedList<EGenericType>(), this.e_op, true
						)

					//create the object which will be used to add the EParameter
					type_parameter_creation_block.add(
						this.generate_egenerictype_declaration_entry(
							bound_processor.generic_bound_to_var_name_map.get(
								bounds_list.removeFirst
							),
							classifier_getter_method
						)
					)

					var iterator = bounds_list.descendingIterator

					while(iterator.hasNext){
						var element = iterator.next
						//create the bounds

						type_parameter_creation_block.add(
							this.generate_egenerictype_declaration_entry(
								bound_processor.generic_bound_to_var_name_map.get(element),
								bound_processor.get_eclassifier_getter_command_for_egenerictype(
									element
								)
							)
						)

						//set the bounds
						//an empty map may be passed, because the bound_processor has already
						//registered all needed generic types.
						type_parameter_set_up_block.add(
							bound_processor.create_egeneric_type_bound_set_up_command_for_object_fields(
								element, new HashMap<ETypeParameter,String>()
							)
						)
					}
				}
				first_part_of_command.append(classifier_getter_method + ", ")
				type_parameter_set_up_block.add(
					first_part_of_command.toString +
					rest_of_command.toString
				)
			}
		}
		//###############################create Exceptions of method
		/*
		 * addEException(EOperation owner, EClassifier exception) 
		 * addEException(EOperation owner, EGenericType exception) 
		 */
		
		var exception_count = 0
		for(EGenericType exception : this.e_op.EGenericExceptions){
			entry = new StringBuilder("addEException(")
			entry.append(this.my_init_code_var_name)
			entry.append(", ")
			if(bound_processor.generic_bound_to_var_name_map.containsKey(exception)){
				entry.append(bound_processor.generic_bound_to_var_name_map.get(exception))
				entry.append(");")
			} else if(exception.ETypeParameter !== null){
				entry.append("createEGenericType(")
				if(etype_to_var_name_map.containsKey(exception.ETypeParameter)){
					//thrown exception is a previously created generic type
					entry.append(etype_to_var_name_map.get(exception.ETypeParameter))
				}
				else if(
					the_etype_parameters_of_the_containing_eclass_to_var_name_map
						.containsKey(exception.ETypeParameter)
				){
					//thrown exception was specified by the class
					entry.append(
						the_etype_parameters_of_the_containing_eclass_to_var_name_map.get(
							exception.ETypeParameter
						)
					)
				} else {
					throw new UnsupportedOperationException("Unsupported EException type.")
				}
				//add the EException to the EOperation
				entry.append("));")
			} else if(exception.EClassifier !== null) {
				var the_classifier = exception.EClassifier
				var classifier_getter_method = 
					this.get_eclassifier_getter_method_for_eclass(the_classifier)
				
				var exception_classifier_name =
					this.my_init_code_var_name + "_" + exception_count++

				//create the top-level generic for this EException
				type_parameter_creation_block.add(
					this.generate_egenerictype_declaration_entry(
						exception_classifier_name, classifier_getter_method
					)
				)

				if(exception.ETypeArguments === null) {
					//the EException only needs the EClassifier
					entry.append(classifier_getter_method)
					entry.append(");")
				} else {
					//the EException has generic sub-types which need to be set up
					for(EGenericType type_argument : exception.ETypeArguments){
						var all_needed_type_parameters = new HashMap<ETypeParameter,String>()
						//type params from this EOperation
						all_needed_type_parameters.putAll(etype_to_var_name_map)
						//type params from the containing class
						all_needed_type_parameters.putAll(
							the_etype_parameters_of_the_containing_eclass_to_var_name_map
						)

						var argument_bounds =
							bound_processor.traverse_generic_bounds_for_object_fields(
								type_argument, new LinkedList<EGenericType>(), this.e_op, true
							)

						var top_level_type_param = argument_bounds.removeFirst
						var iterator = argument_bounds.descendingIterator
						if(
							top_level_type_param.ETypeParameter !== null &&
							all_needed_type_parameters.containsKey(top_level_type_param.ETypeParameter) &&
							!iterator.hasNext
						){
							type_parameter_set_up_block.add(
								exception_classifier_name + 
								".getETypeArguments().add(createEGenericType(" +
								all_needed_type_parameters.get(top_level_type_param.ETypeParameter) +
								"));"
							)
						} else {
							while(iterator.hasNext){
								var bound = iterator.next
								if(
									bound.ETypeParameter !== null &&
									all_needed_type_parameters.containsKey(bound.ETypeParameter)
								){
									type_parameter_set_up_block.add(
										bound_processor
											.create_egeneric_type_bound_set_up_command_for_object_fields(
												bound, all_needed_type_parameters
											)
									)
								} else {
									//create the generic
									var bound_name =
										bound_processor.generic_bound_to_var_name_map.get(bound)
									var bound_classifier =
										bound_processor
											.get_eclassifier_getter_command_for_egenerictype(bound)
									type_parameter_creation_block.add(
										this.generate_egenerictype_declaration_entry(
											bound_name, bound_classifier
										)
									)
									type_parameter_set_up_block.add(
										bound_processor
											.create_egeneric_type_bound_set_up_command_for_object_fields(
												bound, all_needed_type_parameters
											)
									)
								}
							}
							//create the ETypeArgument
							type_parameter_creation_block.add(
								this.generate_egenerictype_declaration_entry(
									bound_processor.generic_bound_to_var_name_map
										.get(top_level_type_param),
									bound_processor
										.get_eclassifier_getter_command_for_egenerictype(
											top_level_type_param
										)
								)
							)
							//add the ETypeArgument
							type_parameter_set_up_block.add(
								exception_classifier_name + 
								".getETypeArguments().add(" +
								bound_processor.generic_bound_to_var_name_map
									.get(top_level_type_param) +
								");"
							)
						}
					}
					entry.append(exception_classifier_name)
					entry.append(");")
				}	
			}
			type_parameter_set_up_block.add(entry.toString)
		}
		
		this.meta_model_package_dependencies.addAll(bound_processor.get_package_dependencies.keySet)
		this.add_import_as_String(bound_processor.get_needed_imports)
		
		body.addAll(type_parameter_creation_block)
		body.addAll(type_parameter_set_up_block)
		this.type_init_commands = body
	}

	/**
	 * creates the declaration for a EGenericType if given the variable name and the getter method
	 * for the EClassifier
	 */
	def private String generate_egenerictype_declaration_entry(
		String var_name, String classifier_getter_method
	){
		return
		'''EGenericType «var_name» = createEGenericType(«classifier_getter_method»);'''.toString
	}

	/**
	 * creates the Classifier-getter method for a passed classifier.
	 * @param EClassifier the_classifier
	 * @return String getter method. example: "ecorePackage.getEClass()"
	 */
	def private String get_eclassifier_getter_method_for_eclass(EClassifier the_classifier){
		var String classifier_getter_method
		if(
			EOperationInspector.emf_model.eclass_is_registered(the_classifier) ||
	     	(
	     		the_classifier instanceof EEnum &&
	     		EOperationInspector.emf_model.get_packages_to_package_inspector_map
	     							         .containsKey(the_classifier.EPackage)
		     )
		){
			//the EClass is user-specified
			var package_dependency = the_classifier.EPackage
			var package_inspector = 
				EOperationInspector.emf_model
					.get_packages_to_package_inspector_map().get(package_dependency)
			
			//init the package if it wasn't done yet
			if(!package_inspector.is_initialized){
				package_inspector.initialize
				EOperationInspector.emf_model
					.update_package_inspector(package_dependency, package_inspector)
			}

			this.meta_model_package_dependencies.add(package_dependency)
			classifier_getter_method = 
				"the" + package_inspector.get_emf_package_class_name() +
				".get" + the_classifier.name + "()"
		} else if (
			EcorePackage.eINSTANCE.equals(the_classifier.EPackage)
		){
			classifier_getter_method = "ecorePackage.get" + the_classifier.name + "()"
		} else {
			throw new IllegalArgumentException(
				"Unknown Classifier:" + System.lineSeparator + the_classifier.toString
			)
		}
		return classifier_getter_method
	}

	/**
	 * returns an ArrayList<String> which contains LOC for EOperation initialization in the Package
	 * class for the "public void initializePackageContents()" method
	 */
	override get_type_init_commands(){
		if(this.type_init_commands_are_generated) return this.type_init_commands
		return new ArrayList<String>()
	}

	/**
	 * returns a HashSet<EPackage> which contains all EPackages on which this EOPeration depends
	 */
	def HashSet<EPackage> get_meta_model_package_dependencies(){
		return this.meta_model_package_dependencies
	}

	/**########################General Methods########################*/

	override boolean equals(Object other){
		if(!(other instanceof EOperationInspector)) return false
		return this.e_op.equals((other as EOperationInspector).e_op)
	}
	
	override int hashCode(){
		return this.e_op.hashCode()
	}
	
}