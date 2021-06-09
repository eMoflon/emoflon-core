package org.moflon.smartemf.inspectors.util

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EGenericType
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.ETypeParameter
import org.moflon.smartemf.EMFCodeGenerationClass
import org.moflon.smartemf.EcoreGenmodelParser
import org.moflon.smartemf.inspectors.InspectedObjectType
import org.moflon.smartemf.inspectors.Inspector

/**
 * Inspects EPackages
 */
class PackageInspector extends EMFCodeGenerationClass implements Inspector{
	
	/**########################Attributes########################*/

	/**
	 * stores the path to the folder representing this package as a String
	 */
	var String path_to_folder

	/**
	 * stores the package declaration which should be used by classes in this package
	 */
	 var String package_declaration

	/**
	 * the EPackage to be inspected
	 */
	var EPackage e_pak

	/**
	 * HashSet storing the EClasses contained in the package
	 */
	var HashSet<EClass> e_classes = new HashSet<EClass>()

	/**
	 * HashSet storing the EDataTypes contained in the package
	 */
	var HashSet<EDataType> e_data_types = new HashSet<EDataType>()
	
	/**
	 * HashSet storing the EDataTypes contained in the package
	 */
	var HashSet<EEnum> e_enums = new HashSet<EEnum>()

	/**
	 * stores if the given package has sub package
	 */
	var boolean has_sub_package

	/**
	 * HashMap mapping EClass to a Set of ObjectFieldInspectors
	 * does not contain inherited object fields
	 */
	var HashMap<EClass,HashSet<AbstractObjectFieldInspector>> eclass_to_object_field_map =
		new HashMap<EClass,HashSet<AbstractObjectFieldInspector>>()

	/**
	 * HashMap mapping EClass to a Set of ObjectFieldInspectors
	 * does contain inherited object fields
	 */
	var HashMap<EClass,HashSet<AbstractObjectFieldInspector>> eclass_to_even_inherited_object_fields_map =
		new HashMap<EClass,HashSet<AbstractObjectFieldInspector>>()

	/**
	 * HashMap mapping EClass to a Set of EOperationInspectors
	 * does not contain inherited EOperations
	 */
	var HashMap<EClass,HashSet<EOperationInspector>> eclass_to_eoperation_map =
		new HashMap<EClass,HashSet<EOperationInspector>>()

	/**
	 * HashMap mapping EClass to a Set of EOperationInspectors
	 * does contain inherited EOperations
	 */
	var HashMap<EClass,HashSet<EOperationInspector>> eclass_to_even_inherited_eoperation_map =
		new HashMap<EClass,HashSet<EOperationInspector>>()

	/**
	 * if a class inherits from another, that classes package is needed to create its contents
	 */
	var HashSet<EPackage> package_dependency_set =
		new HashSet<EPackage>()

	/**
	 * stores code snippets needed to to initialize this package in the EMFPackage class
	 * the key represents a variables name and the key is a ordered list of code concerning the
	 * variable.<br>
	 * example:<br>
	 * <b>key</b> =  <em>the_SubPackage</em><br>
     * <b>value</b> = <em>
     ["Object registered_SubPackage = EPackage.Registry.INSTANCE.getEPackage(SubPackage.eNS_URI);",
     "SubPackageImpl the_SubPackage = (SubPackageImpl)
     ((registered_SubPackage instanceof SubPackageImpl) ?
     registered_SubPackage : SubPackage.eINSTANCE);"]</em><br>
	 */
	var HashMap<String,ArrayList<String>> init_snippet =
		new HashMap<String,ArrayList<String>>()

	/**
	 * maps EOperations to their respective EOperationInspector
	 */
	var HashMap<EOperation, EOperationInspector> eoperation_to_inspector_map = 
		new HashMap<EOperation, EOperationInspector>()

	/**
	 * maps EStructuralFeature to their respective Inspector
	 */
	var HashMap<EStructuralFeature, AbstractObjectFieldInspector> efeature_to_inspector_map =
		new HashMap<EStructuralFeature, AbstractObjectFieldInspector>()

	/**
	 * stores all EClasses as key and a HashMap as well which stores all the EClasses
	 *ETypeParameters and their designated variable name
	 */
	var HashMap<EClass,HashMap<ETypeParameter,String>> eclass_to_etypeparam_to_var_name_map = 
		new HashMap<EClass,HashMap<ETypeParameter,String>>()

	/**
	 * stores all classes contained in the package which have ETypeParameters
	 */
	var HashSet<EClass> eclasses_which_have_etypeparameters = 
		new HashSet<EClass>()

	/**
	 * stores if the contents of this Inspector have been inited
	 */
	var boolean isInited = false

	/**
	 * stores the String which is used to declare the type-parameters for a class.<br>
	 * <b>Example:</b>
	 <xmp>TestClassImpl<a extends classB, b> --> stored String = "<a extends classB, b>"</xmp>
	 */
	var HashMap<EClass,String> eclass_to_type_arguments_declaration =
		new HashMap<EClass,String>()

	/**
	 * stores the needed imports for the eclass if type arguments are needed
	 */
	var HashMap<EClass,HashSet<String>> eclass_to_needed_imports_for_type_arguments_map =
		new HashMap<EClass,HashSet<String>>()

	/**
	 * stores a reduced String which is used to declare the type-parameters for a class.<br>
	 * <b>Example:</b>
	 <xmp>TestClassImpl<a extends classB, b> --> stored String = "<a, b>"<xmp>
	 */
	var HashMap<EClass,String> eclass_to_type_to_reduced_arguments_declaration =
		new HashMap<EClass,String>()

	/**
	 * stores the specific imports the EMF-package-factory needs as well, which however,
	 * the EMF-package-class does not.
	 */
	var HashSet<String> needed_imports_for_package_factory =
		new HashSet<String>()

	/**########################Constructor########################*/
	
	/**
	 * construct a new PackagInspector
	 * @param e_package EPackag 
	 * @param gen_model EcoreGenmodelParser
	 * @author Adrian Zwenger
	 */
	new(EPackage e_package, EcoreGenmodelParser gen_model) {
		super(gen_model)
		this.e_pak = e_package
		this.e_classes = new HashSet<EClass>()//emf_model.get_epackage_and_contained_classes_map.get(e_pak)
		this.has_sub_package = !e_pak.ESubpackages.isEmpty()
		
	}

	/**
	 * initializes this PackageInspector.<br>
	 * <ol>
	 *	<li>registers EClasses</li>
	 *	<li>EAttributes and EReferences</li>
	 *	<li>EOperations</li>
	 *	<li>inherited things</li>
	 *	<li>EDataTypes</li>
	 * </ol>
	 * @author Adrian Zwenger
	 */
	def void initialize(){
		this.isInited = true
		//register attributes, references, EOperations for the EClasses
		for(EClass e_class : emf_model.get_epackage_and_contained_classes_map.get(e_pak)){
			this.e_classes.add(e_class)

			//register the type-parameter declaration strings
			this.create_arguments_declaration_for_eclass(e_class)

			var object_fields = new HashSet<AbstractObjectFieldInspector>()
			var even_inherited_object_fields = new HashSet<AbstractObjectFieldInspector>()
			//var struct_features = new HashSet<EStructuralFeature>()

			var attributes_lookup = new HashSet<EAttribute>(e_class.EAttributes)
			var references_lookup = new HashSet<EReference>(e_class.EReferences)
			var operations_lookup = new HashSet<EOperation>(e_class.EOperations)

			//register EAttributes
			for(EAttribute attr : attributes_lookup){
				var AttributeInspector new_inspector
				if(
					PackageInspector.emf_model.get_struct_features_to_inspector_map.containsKey(
						attr
					)
				){
					new_inspector = 
						PackageInspector.emf_model
										.get_struct_features_to_inspector_map
										.get(attr) as AttributeInspector
				} else {
					new_inspector = new AttributeInspector(attr, PackageInspector.emf_model)
				}

				//generate the init commands if they were not beforehand
				if(!new_inspector.type_init_commands_are_generated)
					new_inspector.generate_init_code_for_package_class(PackageInspector.emf_model)

				//register all cross-dependencies
				this.package_dependency_set.addAll(
					new_inspector.get_meta_model_package_dependencies()
				)

				//store the Inspector
				object_fields.add(new_inspector)
				this.efeature_to_inspector_map.put(attr, new_inspector)
			}
			
			//register EReferences
			for(EReference e_ref : references_lookup){
				var ReferenceInspector new_inspector
				if(
					PackageInspector.emf_model.get_struct_features_to_inspector_map.containsKey(
						e_ref
					)
				){
					new_inspector =
						PackageInspector.emf_model
										.get_struct_features_to_inspector_map
								        .get(e_ref) as ReferenceInspector
				} else {
					new_inspector = new ReferenceInspector(e_ref, PackageInspector.emf_model)
				}

				if(!new_inspector.type_init_commands_are_generated)
						new_inspector.generate_init_code_for_package_class(
							PackageInspector.emf_model
						)
				this.package_dependency_set.addAll(
					new_inspector.get_meta_model_package_dependencies()
				)
				object_fields.add(new_inspector)
				this.efeature_to_inspector_map.put(e_ref, new_inspector)
			}

			//register inherited Attributes
			for(EAttribute attr : e_class.EAllAttributes){
				if(!attributes_lookup.contains(attr)){
					var AttributeInspector new_inspector
					if(
						PackageInspector.emf_model.get_struct_features_to_inspector_map.containsKey(
							attr
						)
					){
						new_inspector = 
							PackageInspector.emf_model
											.get_struct_features_to_inspector_map
											.get(attr) as AttributeInspector
					} else {
						new_inspector = new AttributeInspector(attr, PackageInspector.emf_model)
					}

					if(!new_inspector.type_init_commands_are_generated)
						new_inspector.generate_init_code_for_package_class(
							PackageInspector.emf_model
						)

					this.package_dependency_set.addAll(
						new_inspector.get_meta_model_package_dependencies()
					)

					even_inherited_object_fields.add(new_inspector)
					this.efeature_to_inspector_map.put(attr, new_inspector)
				}
			}

			//register inherited References			
			for(EReference e_ref : e_class.EAllReferences){
				if(!references_lookup.contains(e_ref)){
					var ReferenceInspector new_inspector
					if(
						PackageInspector.emf_model.get_struct_features_to_inspector_map.containsKey(
							e_ref
						)
					){
						new_inspector = PackageInspector.emf_model
														.get_struct_features_to_inspector_map
														.get(e_ref) as ReferenceInspector
					} else {
						new_inspector = new ReferenceInspector(e_ref, PackageInspector.emf_model)
					}

					if(!new_inspector.type_init_commands_are_generated)
						new_inspector.generate_init_code_for_package_class(
							PackageInspector.emf_model
						)

					this.package_dependency_set.addAll(
						new_inspector.get_meta_model_package_dependencies()
					)

					even_inherited_object_fields.add(new_inspector)
					this.efeature_to_inspector_map.put(e_ref, new_inspector)
				}
			}

			even_inherited_object_fields.addAll(object_fields)

			//register all classes and their attributes/references contained in package
			eclass_to_object_field_map.put(e_class, object_fields)
			eclass_to_even_inherited_object_fields_map.put(
				e_class, even_inherited_object_fields
			)

			//register Packages on which the EClass depends
			var dependant_packages = new HashSet<EPackage>()
			for(super_e_cl : e_class.ESuperTypes){
				if(super_e_cl.EPackage !== null && !super_e_cl.EPackage.equals(this.e_pak))
					dependant_packages.add(super_e_cl.EPackage)
			}
			this.package_dependency_set.addAll(dependant_packages)

			//register generic type parameters for class
			var generic_type_params_map = 
				PackageInspector.emf_model.get_generic_type_to_var_name_map_for_eclass(e_class)
			this.eclass_to_etypeparam_to_var_name_map.put(e_class, generic_type_params_map)
			if(!generic_type_params_map.keySet.isEmpty)
				this.eclasses_which_have_etypeparameters.add(e_class)

			//register the EOperations
			var e_operations = new HashSet<EOperationInspector>()
			for(EOperation e_op : e_class.EOperations){
				var inspector = new EOperationInspector(e_op, PackageInspector.emf_model)
				inspector.generate_init_code_for_package_class(PackageInspector.emf_model)
				e_operations.add(inspector)
				//operations_lookup.add(e_op)
				this.eoperation_to_inspector_map.put(e_op, inspector)
				this.package_dependency_set.addAll(inspector.get_meta_model_package_dependencies)
			}
			eclass_to_eoperation_map.put(e_class, e_operations)
			
			//register inherited EOperations
			var even_inherited_e_operations = new HashSet<EOperationInspector>()
			for(EOperation e_op : e_class.EAllOperations){
				if(!operations_lookup.contains(e_op)){
					var new_inspector = new EOperationInspector(e_op, PackageInspector.emf_model)
					new_inspector.generate_init_code_for_package_class(PackageInspector.emf_model)
					even_inherited_e_operations.add(new_inspector)
					this.eoperation_to_inspector_map.put(e_op, new_inspector)
					this.package_dependency_set.addAll(
						new_inspector.get_meta_model_package_dependencies
					)
				}
			}
			even_inherited_e_operations.addAll(e_operations)
			eclass_to_even_inherited_eoperation_map.put(e_class, even_inherited_e_operations)
		}

		this.package_dependency_set.addAll(this.e_pak.ESubpackages)
		set_path_to_folder_and_package_declaration()
		
		this.e_data_types =
			PackageInspector.emf_model.get_epackage_and_contained_e_data_types_map.get(e_pak)

		this.e_enums = PackageInspector.emf_model.get_epackage_and_contained_eenums_map.get(e_pak)

		//generate init for given this package snippet
		var snippets = new ArrayList<String>()
		var package_name = this.get_emf_package_class_name()

		snippets.add(
'''Object registered_«package_name» = EPackage.Registry.INSTANCE.getEPackage(«package_name».eNS_URI);'''
		)

		snippets.add(
'''«package_name»Impl the_«package_name» = («package_name»Impl) ((registered_«package_name» instanceof «package_name»Impl) ? registered_«package_name» : «package_name».eINSTANCE);'''.toString
		)

		this.init_snippet.put('''the_«package_name»'''.toString(), snippets)
	}

	/**
	 * creates the generic parameter declaration for a given EGenericType. Example: the described
	 * EGenericType is an ArrayList<b extends ClassA<?>>,
	 * the output would be: "ArrayList<b extends ClassA<?>>" if called with empty String.
	 * registers needed imports too and registers them for the PackageFactory creators as well.
	 * Method is recursive
	 * @param generic EGenericType for which this method is called
	 * @param declaration String saves the recursion state up until then.
	 * Pass empty String on first call.
	 * @param container_eclass EClass which needs to be passed to check if a type parameter is an
	 * existing parameter established by the class by which the passed EGenericType is contained.
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String etype_param_declarationgetter(
		EGenericType generic, String declaration, EClass container_eclass
	){
		var new_declaration = declaration
		//get name of dependency and import them if needed
		if(generic.EClassifier !== null){
			new_declaration += generic.EClassifier.name
			var imports = this.add_import(generic.EClassifier)
			this.needed_imports_for_package_factory.addAll(imports)
			this.eclass_to_needed_imports_for_type_arguments_map.get(container_eclass)
																.addAll(imports)

		} else if (generic.ETypeParameter !== null) new_declaration += generic.ETypeParameter.name
		else new_declaration += "?"

		if(!generic.ETypeArguments.isEmpty){
			new_declaration += "<"
			var generics_iterator = generic.ETypeArguments.iterator
			while(generics_iterator.hasNext){
				new_declaration += this.etype_param_declarationgetter(
					generics_iterator.next, "", container_eclass
				)
				if(generics_iterator.hasNext) new_declaration += ","
			}
			new_declaration += ">"
		}

		if(generic.EUpperBound !== null){
			new_declaration += " extends "
			new_declaration += this.etype_param_declarationgetter(
				generic.EUpperBound, "", container_eclass
			)
		} else if (generic.ELowerBound !== null) {
			new_declaration += " super "
			new_declaration += this.etype_param_declarationgetter(
				generic.EUpperBound, "", container_eclass
			)
		}
		return new_declaration
	}

	/**
	 * creates the generic parameters declaration for a class. Example: "<a,b>"
	 * @param e_class EClass
	 * @return String
	 * @author Adrian Zwenger
	 */
	def private void create_arguments_declaration_for_eclass(EClass e_class){
		this.eclass_to_needed_imports_for_type_arguments_map.put(e_class, new HashSet<String>())
		var imports = new ArrayList<String>()
		var generic_type_declaration_string = ""
		var reduced_generic_type_declaration_string = ""
		
		if(!e_class.ETypeParameters.isEmpty) {
			generic_type_declaration_string += "<"
			reduced_generic_type_declaration_string += "<"
			var iterator = e_class.ETypeParameters.iterator
			while(iterator.hasNext){
				var entry = iterator.next
				generic_type_declaration_string += entry.name
				reduced_generic_type_declaration_string += entry.name
				if(!entry.EBounds.isEmpty){
					generic_type_declaration_string += " extends "
					var bound_iterator = entry.EBounds.iterator
					while(bound_iterator.hasNext){
						var the_bound = bound_iterator.next
						//import the bound if it is importable
						if(the_bound.EClassifier !== null)
							imports.addAll(this.add_import(the_bound.EClassifier))
						generic_type_declaration_string += 
							this.etype_param_declarationgetter(the_bound, "", e_class)
						if(bound_iterator.hasNext) generic_type_declaration_string += " & "
					}
				}
				if(iterator.hasNext){
					generic_type_declaration_string += ","
					reduced_generic_type_declaration_string += ","
				}
			}
			generic_type_declaration_string += ">"
			reduced_generic_type_declaration_string += ">"
		}

		this.eclass_to_type_arguments_declaration.put(e_class, generic_type_declaration_string)
		this.eclass_to_type_to_reduced_arguments_declaration.put(
			e_class, reduced_generic_type_declaration_string
		)
		this.eclass_to_needed_imports_for_type_arguments_map.get(e_class).addAll(imports)
	}

	/**
	 * returns true if this inspector has been initialized
	 * @return boolean
	 * @author Adrian Zwenger
	 */
	def boolean is_initialized(){
		return this.isInited
	}

	/**
	 * returns the String which is used to declare the type-parameters for a class.<br>
	 * <b>Example:</b>
	 <xmp>TestClassImpl<a extends classB, b> --> stored String = "<a extends classB, b>"</xmp>
	 * @param e_class EClass
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_type_arguments_declaration_for_eclass(EClass e_class){
		return this.eclass_to_type_arguments_declaration.get(e_class)
	}

	/**
	 * returns the reduced String which is used to declare the type-parameters for a class.<br>
	 * <b>Example:</b>
	 <xmp>TestClassImpl<a extends classB, b> --> stored String = "<a, b>"</xmp>
	 * @param e_class EClass
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_reduced_type_arguments_declaration_for_eclass(EClass e_class){
		return this.eclass_to_type_to_reduced_arguments_declaration.get(e_class)
	}

	/**
	 * returns a HashMap where the key is an EClass of this package and the key its generic
	 * type-arguments. Take a look at {@link #eclass_to_needed_imports_for_type_arguments_map
	 eclass_to_needed_imports_for_type_arguments_map } 
	 * @return HashMap<EClass, HashSet<String>>
	 * @author Adrian Zwenger
	 */
	def HashMap<EClass, HashSet<String>> get_eclass_to_needed_imports_for_type_arguments_map(){
		return eclass_to_needed_imports_for_type_arguments_map
	}

	/**########################Helper Methods########################*/
	
	/**
	 * traverses package hierarchy to set the folder path and the equivalent java representation
	 * <b>example:</b> <xmp>"./src-gen/org/util/mypackage" and "org.util.mypackage"</xmp>
	 * @author Adrian Zwenger
	 */
	def private void set_path_to_folder_and_package_declaration(){
		var path = e_pak.name
		var declaration = e_pak.name
		var e_package = e_pak
		while(e_package.ESuperPackage !== null){
			e_package = e_package.ESuperPackage
			path = e_package.name + "/" + path
			declaration = e_package.name + "." + declaration
		}
		var super_package = emf_model.get_super_package_name
		if(super_package === null){
			package_declaration = declaration
			path_to_folder = GENERATED_FILE_DIR + path
			return
		}
		var buffer = emf_model.get_super_package_name.split("\\.").join("/")
		path_to_folder = GENERATED_FILE_DIR + buffer + "/" + path
		package_declaration = super_package + "." + declaration
	}
	
	/**########################Getters########################*/

	/**
	 * returns the path to the folder where all Classes in package are located
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_path_to_folder(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return path_to_folder
	}

	/**
	 * Returns a HashSet containing ObjectFieldInspectors for a given class
	 * @param e_cl EClass
	 * @return HashSet<AbstractObjectFieldInspector>
	 * @author Adrian Zwenger
	 */
	def HashSet<AbstractObjectFieldInspector> get_object_field_inspectors_for_class(EClass e_cl){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return eclass_to_object_field_map.get(e_cl)
	}

	/**
	 * Returns a HashSet containing absolutely all ObjectFieldInspectors for even inherited features
	 * a given class
	 * @param e_cl EClass
	 * @return HashSet<AbstractObjectFieldInspector>
	 * @author Adrian Zwenger
	 */
	def HashSet<AbstractObjectFieldInspector> get_all_object_field_inspectors_for_class(
		EClass e_cl
	){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return eclass_to_even_inherited_object_fields_map.get(e_cl)
	}

	/**
	 * Returns a HashSet containing EOperationInspectors for a given class
	 * @param e_cl EClass
	 * @return HashSet<EOperationInspector>
	 * @author Adrian Zwenger
	 */
	def HashSet<EOperationInspector> get_eoperation_inspector_for_class(EClass e_cl){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return eclass_to_eoperation_map.get(e_cl)
	}

	/**
	 * Returns a HashSet containing all EOperationInspectors (even inherited ones) for a given class
	 * @param e_cl EClass
	 * @return HashSet<EOperationInspector>
	 * @author Adrian Zwenger
	 */
	def HashSet<EOperationInspector> get_all_eoperation_inspector_for_class(EClass e_cl){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return eclass_to_even_inherited_eoperation_map.get(e_cl)
	}

	/**
	 * Returns a HashSet containing all EClasses in this package
	 * @return HashSet<EClass>
	 * @author Adrian Zwenger
	 */
	def HashSet<EClass> get_all_eclasses_in_package(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return e_classes
	}

	/**
	 * Returns all custom EMF datatypes contained in this package in an HashSet
	 * @return HashSet<EDataType>
	 * @author Adrian Zwenger
	 */
	def HashSet<EDataType> get_all_edata_types_in_package(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.e_data_types
	}
	
	/**
	 * Returns all custom EMF datatypes contained in this package in an HashSet
	 * @return HashSet<EEnum>
	 * @author Adrian Zwenger
	 */
	def HashSet<EEnum> get_all_eenums_in_package(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.e_enums
	}

	/**
	 * returns true if this package contains sub-packages
	 * @return boolean
	 * @author Adrian Zwenger
	 */
	def boolean has_sub_packages(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return has_sub_package
	}

	/**
	 * Returns the package declaration as String.<br>
	 * <b>example:</b> <xmp>or.util.fake.package</xmp>
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_package_declaration_name(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return package_declaration
	}

	/**
	 * Returns the package declaration for the implementations as String
	 * <b>example:</b> <xmp>or.util.fake.package.impl</xmp>
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_package_declaration_name_for_implementations(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return package_declaration + ".impl"
	}

	/**
	 * All EMF generated packages have a class which is used to initialise the model. The name of
	 * said class is returned here
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_emf_package_class_name(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.get_name_with_first_letter_capitalized + "Package"
	}
	
	/**
	 * All EMF generated packages have a class which is used to create parts of the model.
	 * The name of said class is returned here
	 * @return String
	 * @author Adrian Zwenger
	 */
	def String get_emf_package_factory_class_name(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.get_name_with_first_letter_capitalized + "Factory"
	}

	/**
	 * returns the regular name of this package
	 * @return String
	 * @author Adrian Zwenger
	 */
	override String get_name(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return e_pak.name
	}

	/**
	 * returns the NS-URI of the inspected EPackage
	 * @return String
	 * @author Adrian Zwenger
 	 */
	def String get_ens_uri(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return e_pak.nsURI
	}

	/**
	 * returns the NS-prefix of the inspected EPackage
	 * @return String
	 * @author Adrian Zwenger
 	 */
	def String get_ens_prefix(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return e_pak.nsPrefix
	}
	
	/**
	 * returns the packages name with a capitalised first letter
	 * @return String
	 * @author Adrian Zwenger
 	 */
	override get_name_with_first_letter_capitalized() {
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.e_pak.name.substring(0, 1).toUpperCase() + this.e_pak.name.substring(1);
	}

	/**
	 * returns Enum identifying this Inspector as a PackageInspector
	 * @return InspectedObjectType
	 * @author Adrian Zwenger
 	 */
	override get_inspected_object_type() {
		return InspectedObjectType.EPACKAGE
	}

	/**
	 * Returns the ESuperPackage of the EPackage which is being inspected.
	 * @return EPackage
	 * @author Adrian Zwenger
	 */
	def EPackage get_super_epackage(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.e_pak.ESuperPackage
	}

	/**
	 * returns a HashSet<Epackage> with all sub-packages of zhe inspected Package
	 * @return HashSet<Epackage>
	 * @author Adrian Zwenger
	 */
	def HashSet<EPackage> get_sub_packages(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return new HashSet<EPackage>(this.e_pak.ESubpackages)
	}
	
	/**
	 * returns the code snippet to obtain or create and register an EPackage instance for this
	 * EPackage as an HashMap. the key is the variables name in the snippet and the ArrayList
	 * contains the needed code-lines as entry
	 * @return HashMap<String,ArrayList<String>>
 	 * @author Adrian Zwenger
	 */
	def HashMap<String,ArrayList<String>> get_init_code_snippet(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return new HashMap<String,ArrayList<String>>(this.init_snippet)
	}

	/**
	 * returns the EPackage which this PackageInspector is inspecting
	 * @return EPackage
	 * @author Adrian Zwenger
	 */
	def EPackage get_emf_e_package(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.e_pak
	}
	
	/**
	 * checks PackageInspectors for equality
	 */
	override boolean equals(Object other){
		if(!(other instanceof PackageInspector)) return false
		return this.e_pak.equals((other as PackageInspector).e_pak)
	}

	/**
	 * returns true if the given EPackage is the inspected one
	 */
	def boolean equals(EPackage e_pak){
		return this.e_pak.equals(e_pak)
	}
	
	/**
	 * returns the hash-code for this Inspector
	 */
	override int hashCode(){
		return this.e_pak.hashCode()
	}

	/**
	 * returns a HashSet<EPackage> on which the inspected Package depends. Does not contain all
	 * dependencies contained in EGenericTypes or similar, but takes super-packages and class
	 * inheritance related dependencies into account
 	 * @return HashSet<EPackage>
	 * @author Adrian Zwenger
	 */
	def HashSet<EPackage> get_e_package_dependencies(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.package_dependency_set
	}
	
	/**
	 * takes EClass and returns a HashMap<ETypeParameter,String>, where the key is an ETypeParameter
	 * of the EClass and the designated variable name as value. Returns null if EClass is not
	 * registered.
	 * @param EClass e_class
	 * @return HashMap<ETypeParameter,String>
	 * @author Adrian Zwenger
	 */
	def get_generic_type_to_var_name_map_for_eclass(EClass e_class){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.eclass_to_etypeparam_to_var_name_map.get(e_class)
	}

	/**
	 * returns an AbstractObjectFieldInspector for a given EStructuralFeature contained in this
	 * Package. Null if not.
	 * @param EStructuralFeature e_feature
	 * @return AbstractObjectFieldInspector
	 */
	def get_inspector_for_structural_feature(EStructuralFeature e_feature){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.efeature_to_inspector_map.get(e_feature)
	}

	/**
	 * Returns an EOperationInspector for given EOperation if contained in the Package.
	 * @param e_op EOperation
	 * @return EOperationInspector
	 * @author Adrian Zwenger
	 */
	def EOperationInspector get_inspector_for_eoperation(EOperation e_op){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.eoperation_to_inspector_map.get(e_op)
	}

	/**
	 * returns a HashSet with all EClasses contained in the Package
	 * @return HashSet<EClass>
	 * @author Adrian Zwenger
	 */
	def HashSet<EClass> get_eclasses_which_have_etypeparameters(){
		if(!this.isInited)
			throw new RuntimeException("PackageInspector has not been inited before use")
		return this.eclasses_which_have_etypeparameters
	}

	/**
	 * Returns the needed imports for the EMF-package-factory class.
	 * @return HashSet<String>
	 * @author Adrian Zwenger
	 */
	def HashSet<String> get_needed_imports_for_package_factory(){
		return this.needed_imports_for_package_factory
	}
}
