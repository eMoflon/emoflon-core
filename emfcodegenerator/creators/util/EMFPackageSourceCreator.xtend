package emfcodegenerator.creators.util

import emfcodegenerator.EGenericTypeProcessor
import emfcodegenerator.EcoreGenmodelParser
import emfcodegenerator.creators.FileCreator
import emfcodegenerator.inspectors.InspectedObjectType
import emfcodegenerator.inspectors.ObjectFieldInspector
import emfcodegenerator.inspectors.util.AttributeInspector
import emfcodegenerator.inspectors.util.EOperationInspector
import emfcodegenerator.inspectors.util.PackageInspector
import emfcodegenerator.inspectors.util.ReferenceInspector
import java.io.File
import java.io.FileWriter
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedList
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EGenericType
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.ETypeParameter
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.EEnum

/**
 * Creator-class which generates the class-file for the SmartEMF-package-class.
 */
class EMFPackageSourceCreator extends EGenericTypeProcessor implements FileCreator{
	
	/**
	 * the declaration for the class. example:
	 * "public class MyPackagePackageImpl extends SmartPackage"
	 */
	var String class_declaration
	
	/**
	 * stores the String declaring in which package the created file is
	 */
	var String package_declaration

	/**
	 * stores the order in which the methods shall be written to the file.
	 */
	var ArrayList<String> method_declarations = new ArrayList<String>()
	
	/**
	 * HashMap where the keys are the declaration and the value the body of a method
	 */
	var HashMap<String,ArrayList<String>> methods = new HashMap<String,ArrayList<String>>()
	
	/**
	 * HashSet storing the object-fields the class will hold
	 */
	var HashSet<String> object_fields = new HashSet<String>()

	/**
	 * stores the order in which the EMF StructuralFeatures shall be created.
	 * the key is the EClass they belong to and the value is an ordered List of the features
	 */
	var HashMap<EClass,ArrayList<ObjectFieldInspector>> structural_feature_precedence_map =
		new HashMap<EClass,ArrayList<ObjectFieldInspector>>()

	/**
	 * stores the order in which the EMF EOperations shall be created.
	 * the key is the EClass they belong to and the value is an ordered List of the operations
	 */
	var HashMap<EClass,ArrayList<EOperationInspector>> eoperation_precedence_map =
		new HashMap<EClass,ArrayList<EOperationInspector>>()

	/**
	 * stores the fully-qualified path to the file. example: 
	 * "./src-gen/package/impl/MypackagePackageImpl.java"
	 */
	var String fq_file_name
	
	/**
	 * stores the String with which code shall be indented
	 */
	var String IDENTION
	
	/**
	 * boolean representing if this class was initialised
	 */
	var boolean is_initialized = false

	/**
	 * stores the EPackages on which this.e_pak is dependent as key and their Inspector as Value
	 */
	var package_dependency_map = new HashMap<EPackage,PackageInspector>()
	/**########################Constructors########################*/
	
	/**
	 * constructs a new EMFPackageSourceCreator
	 * @param package_inspector PackageInspector the package for which the file shall be created
	 * @param e_pak_map HashMap<EPackage,PackageInspector> a map containing EPackages as key and an
	 * corresponding PackageInspector as value
	 * @param gen_model EcoreGenmodelParser the needed ecore-xmi and genmode-xmi wrapper
	 * @author Adrian Zwenger
	 */
	new(PackageInspector package_inspector,
		HashMap<EPackage,PackageInspector> e_pak_map,
		EcoreGenmodelParser gen_model
	){
		super(gen_model, "generic_bound_", package_inspector)
		this.packages = e_pak_map
		this.e_pak = package_inspector
		
		//import most used classes and interfaces
		this.add_import_as_String("org.eclipse.emf.ecore.EAttribute")
		this.add_import_as_String("org.eclipse.emf.ecore.EReference")
		this.add_import_as_String("org.eclipse.emf.ecore.EClass")
		this.add_import_as_String("org.eclipse.emf.ecore.EPackage")
		this.add_import_as_String("org.eclipse.emf.ecore.EPackage")
		this.add_import_as_String("org.eclipse.emf.ecore.EDataType")
		this.add_import_as_String("org.eclipse.emf.ecore.EDataType")
		this.add_import_as_String("org.eclipse.emf.ecore.EClassifier")
		this.add_import_as_String("org.eclipse.emf.common.util.EList")
		this.add_import_as_String("org.eclipse.emf.ecore.ETypeParameter")
		this.add_import_as_String("org.eclipse.emf.ecore.EGenericType")
		this.add_import_as_String("org.eclipse.emf.ecore.EOperation")
		this.add_import_as_String("org.eclipse.emf.ecore.impl.EPackageImpl")

		//import the interface for this class
		this.add_import_as_String(
			this.e_pak.get_package_declaration_name +
			"." +
			this.e_pak.get_emf_package_factory_class_name
		)

		//this.add_import_as_String("java.util.HashMap")

		//import the interface for this package
		this.add_import_as_String(
			this.e_pak.get_package_declaration_name +
			"." + this.e_pak.get_emf_package_class_name()
		)

		//add general data-fields which are always present
		this.object_fields.add("private static boolean isInited = false;")
		this.object_fields.add("private boolean isCreated = false;")
		this.object_fields.add("private boolean isInitialized = false;")

		//add constructor to methods
		var constructor_declaration = 
			'''private «this.e_pak.get_emf_package_class_name»Impl()'''.toString()
		this.method_declarations.add(constructor_declaration)
		var body = new ArrayList<String>()
		body.add(
	'''super(eNS_URI, «this.e_pak.get_emf_package_factory_class_name».eINSTANCE);'''.toString()
		)
		this.methods.put(constructor_declaration, body)
	}
	
	/**########################Method Generators########################*/

	/**
	 * for each directly contained EClass of an EPackage a getter-methods must be generated.
	 * Each EClass, its EAttributes, EOperations and EReferences get getters as well. <br>
	 * The generated getters are
	 * automatically added to the {@link #methods methods} HashMap and the
	 * {@link #method_declaration method_declaration}
	 * ArrayList. <br>
	 * This method needs to be used on all contained EClass'es before generating other code,
	 * as the order in which EstructuralFeatures and EOperations need to be instanciated for
	 * the meta-model is determined here. Without it code generation will fail or worse, lead to
	 * unexplainable faulty runtime-behaviour when executing with the EMF-framework.<br>
	 * Called by {@link #initialize_creator(String, String) initialize_creator()}.
	 * @param e_class EClass the class for which the getters will be generated
	 * @author Adrian Zwenger
	 */
	def private void register_methods_and_data_fields_for_eclass(EClass e_class){
		//import the class (or rather its interface)
		this.add_import_as_String(this.e_pak.get_package_declaration_name + "." + e_class.name)

		var class_name = e_class.name
		var class_object_name = e_class.name + "EClass"
		
		//create the getter for the class itself
		var declaration = "public EClass get" + class_name + "()"
		var body = new ArrayList<String>()
		body.add("return " + class_object_name + ";")
		this.method_declarations.add(declaration)
		this.methods.put(declaration, body)

		//EMF stores StructuralFeatures in order and access them by knowing which index those
		//features have.
		//the feature count keeps track of the amount of EStructuralFeatures
		//(EAttributes/EReferences) a class has
		var feature_count = 0

		//ArrayList to store the precedence of Attributes and Operations
		//it is needed later for ECLass construction as their features etc need to be instanced in
		//order as well.
		var ArrayList<ObjectFieldInspector> structural_feature_precedence =
			new ArrayList<ObjectFieldInspector>()
		
		//Each class is stored in the package as an EClassifier. The data field is added here.
		this.object_fields.add('''private EClass «class_object_name» = null;'''.toString)

		//create all methods for the EAttributes and EReferences of one class
		for(EStructuralFeature e_feature : e_class.EStructuralFeatures){
			var inspector = this.e_pak.get_inspector_for_structural_feature(e_feature)
			var data_type =
				(inspector.get_inspected_object_type === InspectedObjectType.EATTRIBUTE) ?
					"EAttribute" :
					"EReference"

			declaration = 
				"public " +
				inspector.get_getter_method_declaration_for_the_package_classes()

			body = new ArrayList<String>()
			body.add('''return («data_type») this.'''.toString + class_object_name + 
					'''.getEStructuralFeatures().get(«feature_count»);'''.toString)
			
			this.method_declarations.add(declaration)
			this.methods.put(declaration, body)

			//add the feature contained in an ObjectFieldInspector to an ordered list
			//that way the correct order when generating these Features can be used
			structural_feature_precedence.add(inspector)
			//increment the feature count to prevent overuse of same ID for features of the same
			//class
			feature_count += 1
		}
		this.structural_feature_precedence_map.put(e_class, structural_feature_precedence)
		
		//reset feature count for EOperations as they are handled separately
		//from the EStructuralFeatures
		feature_count = 0
		//ordered list to store in which order the EOperations shall be generated
		var ArrayList<EOperationInspector> eoperation_precedence =
			new ArrayList<EOperationInspector>()

		for(EOperation e_op : e_class.EOperations){
			var inspector = this.e_pak.get_inspector_for_eoperation(e_op)
			declaration = "public " +
				inspector.get_getter_method_declaration_for_the_package_classes()

  			body = new ArrayList<String>()
			body.add('''return this.'''.toString + class_object_name + 
					'''.getEOperations().get(«feature_count»);'''.toString())

			this.method_declarations.add(declaration)
			this.methods.put(declaration, body)
			eoperation_precedence.add(inspector)
			feature_count += 1
		}
		this.eoperation_precedence_map.put(e_class, eoperation_precedence)
	}

	/**
	 * Creates the data fields and getters for all given EDataType's and registers them.
	 * Called by {@link #initialize_creator(String, String) initialize_creator()}.
	 * @param data_type EDataType the type which shall be registered
	 * @author Adrian Zwenger
	 */
	def private void register_methods_and_data_fields_for_edatatype(EDataType data_type){
		var variable_name = data_type.name.substring(0, 1).toLowerCase() +
							data_type.name.substring(1) + "EDataType"
		this.object_fields.add("private EDataType " + variable_name + " = null;")
		var declaration = "public EDataType get" + data_type.name + "()"
	  	this.method_declarations.add(declaration)
	  	var body = new ArrayList<String>()
	  	body.add("return this." + variable_name + ";")
	  	this.methods.put(declaration, body)
	}

	/**
	 * Creates the data fields and getters for all given EEnum's and registers.
	 * Called by {@link #initialize_creator(String, String) initialize_creator()}.
	 * @param data_type EEnum the Enum which shall be registered
	 * @author Adrian Zwenger
	 */
	def private void register_methods_and_data_fields_for_eenums(EEnum data_type){
		this.add_import_as_String("org.eclipse.emf.ecore.EEnum")
		var variable_name = data_type.name + "EEnum"
		this.object_fields.add("private EEnum " + variable_name + " = null;")
		var declaration = "public EEnum get" + data_type.name + "()"
	  	this.method_declarations.add(declaration)
	  	var body = new ArrayList<String>()
	  	body.add("return this." + variable_name + ";")
	  	this.methods.put(declaration, body)
	}

	/**
	 * Generates the EPackage-init() method which is used instead of an public constructor<br>
	 * the init-method initializes the package and all other packages of the meta-model, creates
	 * and initializes their content. It doubles as a constructor for the meta-model.<br>
	 * Called by {@link #initialize_creator(String, String) initialize_creator()}.
	 * @author Adrian Zwenger
	 */
	def private void generate_e_package_init(){
		var p_name = this.e_pak.get_emf_package_class_name()
		//create method name
		var declaration =
			'''public static «p_name» init()'''.toString()

		//init an ArrayList for method content
		var body = new ArrayList<String>()

		//add verification if EPackage was already registered or if it still needs to be created
		body.add(
			'''if (isInited) return («p_name») '''.toString +
			'''EPackage.Registry.INSTANCE.getEPackage(«p_name».eNS_URI);'''.toString
			)

		//to init one EPackage, all EPackages need to be inited
		var e_packages = new HashSet<EPackage>(this.packages.keySet())
		//remove the current EPackage from set. that way it will not be inited twice
		e_packages.remove(this.e_pak.get_emf_e_package)

		//get the init snippet from this EPackage and register its variable
		var init_snippets = new HashMap<String,ArrayList<String>>()
		
		var snippets = new ArrayList<String>()
		var package_name = this.e_pak.get_emf_package_class_name()
		var this_package_var_name = "the_" + package_name
		snippets.add(
'''Object registered_«package_name» = EPackage.Registry.INSTANCE.getEPackage(«package_name».eNS_URI);'''.toString()
		)
		snippets.add(
'''«package_name»Impl the_«package_name» = («package_name»Impl) ((registered_«package_name» instanceof «package_name»Impl) ? registered_«package_name» : new «package_name»Impl());'''.toString
		)
		init_snippets.put('''the_«package_name»'''.toString(), snippets)

		//get or create and register all the EPackages
		body.addAll(snippets)
		//after the EPackage is inited, set the boolean to true
		body.add("isInited = true;")

		//create/register all the other EPackages
		for(epak : e_packages){
			
			var inspector = this.packages.get(epak)
			this.add_import_as_String(
				inspector.get_package_declaration_name + "." +
				inspector.get_emf_package_class_name
			)
			this.add_import_as_String(
				inspector.get_package_declaration_name + ".impl." +
				inspector.get_emf_package_class_name + "Impl"
			)
			var buffer = inspector.get_init_code_snippet()
			init_snippets.putAll(buffer)
			body.addAll(buffer.values.get(0))
		}

		var init_package_contents = new ArrayList<String>()
		//register all contents for all Packages
		for(variable : init_snippets.keySet){
			// Create package meta-data objects
			body.add(variable + ".createPackageContents();")
			// Initialise created meta-data
			init_package_contents.add(variable + ".initializePackageContents();")
		}
		//if not all packages are called with ".createPackageContents();" before initilaizing a
		//single one
		//packages will be registered and stored as NullPointer somehow.............
		body.addAll(init_package_contents)
		
		body.add('''EPackage.Registry.INSTANCE.put(«p_name».eNS_URI, «this_package_var_name»);''')

		body.add('''return «this_package_var_name»;'''.toString)
		this.method_declarations.add(declaration)
		this.methods.put(declaration, body)
	}

	/**
	 * Generates the parts where basic info of the package is set.
	 * It is the first section of the "void initializePackageContents()"-method.<br>
	 * Called by
	 * {@link #generate_e_package_init_package_contents() generate_e_package_init_package_contents()}.
	 * @author Adrian Zwenger
	 */
	def private ArrayList<String> generate_e_package_init_package_contents__package_init(){
		var body = new ArrayList<String>()
		body.add("if (this.isInitialized) return;")
		body.add("isInitialized = true;")
		body.add('''setName(«this.e_pak.get_emf_package_class_name».eNAME);'''.toString())
		body.add('''setNsPrefix(«this.e_pak.get_emf_package_class_name».eNS_PREFIX);'''.toString())
		body.add('''setNsURI(«this.e_pak.get_emf_package_class_name».eNS_URI);'''.toString())
		return body
	}

	/**
	 * generates the parts dependent packages are obtained and set for use.
	 * is the second section of void initializePackageContents()
	 */
	def private ArrayList<String> generate_e_package_init_package_contents__add_package_dependencies(){
		var body = new ArrayList<String>()
		body.add("")
		body.add("//Obtain other dependent packages")
		
		// Obtain other dependent packages and add them
		for(dependent_pak : this.e_pak.get_e_package_dependencies){
			if(!dependent_pak.nsURI.toString.equals("http://www.eclipse.org/emf/2002/Ecore")){
				try{
					this.package_dependency_map.put(dependent_pak, packages.get(dependent_pak))
				} catch(Exception E){
					throw new RuntimeException(
						"package <" + dependent_pak.name + "> was not specified in the XMI-files"
					)
				} finally {
					var inspector = packages.get(dependent_pak)
					body.add(
						inspector.get_emf_package_class_name + " the" +
						inspector.get_emf_package_class_name +
						" = (" + inspector.get_emf_package_class_name +
						") EPackage.Registry.INSTANCE.getEPackage(" +
						inspector.get_emf_package_class_name + ".eNS_URI);"
					)
				}
			}
		}
		body.add("")
		body.add("//add sub-packages")
		
		// Add subpackages
		for(sup_package : this.e_pak.get_sub_packages){
			body.add(
				"getESubpackages().add(the" +
				this.package_dependency_map.get(sup_package).get_emf_package_class_name + ");"
			)
		}
		return body
	}

	/**
	 * Generates the parts where generic types are created and their bounds are set. Specifically
	 * the ones needed to set the generic-type parameters for the classes<br>
	 * It is the third section of the "void initializePackageContents()"-method.<br>
	 * Called by
	 * {@link #generate_e_package_init_package_contents() generate_e_package_init_package_contents()}.
	 * @return ArrayList<String> where each entry is one line of code
	 * @Author Adrian Zwenger
	 */
	def private ArrayList<String> generate_e_package_init_package_contents__create_type_params___for_eclasses(){
		// type parameter code needs to be generated first, because during generation more
		// package dependencies can be found as typer parameters can be recursively structured
		var type_parameter_body = new ArrayList<String>()
		type_parameter_body.add("")
		type_parameter_body.add("//Create type parameters for all EClasses")
		
		//code where type parameters are created
		var type_parameter_creation_block = new ArrayList<String>()

		//code where the type parameters are set up and bound if needed
		var type_parameter_set_up_block = new ArrayList<String>()

		//as they are generated at the same time and the creation block needs to be written first,
		//they are stored separately at first and then stored in order in the type_parameter_body
		//list

		var HashMap<ETypeParameter,String> etype_to_var_name_map =
			new HashMap<ETypeParameter,String>()
		
		for(e_class : this.e_pak.get_eclasses_which_have_etypeparameters()){
			var e_class_name = e_class.name

			for(type_parameter : e_class.ETypeParameters){

				var param_var_name =
					this.e_pak.get_generic_type_to_var_name_map_for_eclass(e_class)
							  .get(type_parameter)

		    	type_parameter_body.add(
'''ETypeParameter «param_var_name» = addETypeParameter(«e_class_name»EClass, "«type_parameter.name»");'''
				)

				etype_to_var_name_map.put(type_parameter, param_var_name)
			}
		}
		type_parameter_body.add("//Set Bounds for the type parameters")
		
		//Set bounds for type parameters
		for(type_param : etype_to_var_name_map.keySet){
			for(bound : type_param.EBounds) {
				var LinkedList<EGenericType> generic_bounds =
					this.traverse_generic_bounds(bound, new LinkedList<EGenericType>())
				var bounds_iterator = generic_bounds.descendingIterator
				while(bounds_iterator.hasNext){
					var generic_type = bounds_iterator.next
					
					var String get_classifier_command = ""
					if(this.generic_bound_to_var_name_map.keySet.contains(generic_type)){
						//create the command which gets the defining EClassifier for the generictype
						get_classifier_command =
							get_eclassifier_getter_command_for_egenerictype(generic_type)
						//create the generic
						//add the newly created EGenericType to the creation code-block
						type_parameter_creation_block.add(
'''EGenericType «this.generic_bound_to_var_name_map.get(generic_type)»  = createEGenericType(«get_classifier_command»);'''.toString()
						)
					}
					type_parameter_set_up_block.add(
						this.create_egeneric_type_bound_set_up_command(
							generic_type, etype_to_var_name_map, type_param
						)
					)
				}
			}
		}
		type_parameter_body.addAll(type_parameter_creation_block)
		type_parameter_body.addAll(type_parameter_set_up_block)
		return type_parameter_body
	}

	/**
	 * generates the parts where generic types are created and their bounds are set
	 * is the third section of void initializePackageContents()
	 * @return ArrayList<String> where each entry is one line of code
	 */
	def private ArrayList<String> generate_e_package_init_package_contents__create_type_params___for_edatatypes(){
		var type_parameter_body = new ArrayList<String>()
		//code where type parameters are created
		var type_parameter_creation_block = new ArrayList<String>()
		//code where the type parameters are set up and bound if needed
		var type_parameter_set_up_block = new ArrayList<String>()
		//as they are generated at the same time and the creation block needs to be written first,
		//they are stored separately at first and then stored in order in the type_parameter_body
		//list
		type_parameter_creation_block.add("")
		type_parameter_creation_block.add("//Create type parameters for all EDataTypes")
		type_parameter_set_up_block.add(" //Set Bounds for the type parameters")
		for(edata_type : this.e_pak.get_all_edata_types_in_package()){
			if(!edata_type.ETypeParameters.isEmpty){
				var etype_to_var_name_map = new HashMap<ETypeParameter,String>()

				var data_type_name = edata_type.name.substring(0,1).toLowerCase +
									 edata_type.name.substring(1) + "EDataType"
				
				//create the ETypeParameters
				for(type_param : edata_type.ETypeParameters){
					var var_name = data_type_name + "_" + type_param.name
					etype_to_var_name_map.put(type_param, var_name)
					var entry = new StringBuilder("ETypeParameter ")
					entry.append(var_name)
					entry.append(" = addETypeParameter(")
					entry.append(data_type_name)
					entry.append(", ")
					entry.append('''"«type_param.name»"''')
					entry.append(");")
					type_parameter_creation_block.add(entry.toString())
				}
				
				//create the EGenericTypes and set their bounds
				for(type_param : etype_to_var_name_map.keySet){
					for(bound : type_param.EBounds){
						var LinkedList<EGenericType> generic_bounds =
							this.traverse_generic_bounds(bound, new LinkedList<EGenericType>())
						var bounds_iterator = generic_bounds.descendingIterator
						while(bounds_iterator.hasNext){
							var generic_type = bounds_iterator.next
							//create the EGenericType
							var entry = new StringBuilder("EGenericType ")
							entry.append(
								this.generic_bound_to_var_name_map.get(generic_type)
							)
							entry.append(" = createEGenericType(")
							entry.append(
								this.get_eclassifier_getter_command_for_egenerictype(generic_type)
							)
							entry.append(");")
							type_parameter_creation_block.add(entry.toString)
							type_parameter_set_up_block.add(
								create_egeneric_type_bound_set_up_command(
									generic_type, etype_to_var_name_map, type_param
								)
							)
						}
					}
				}

			}
		}
		type_parameter_body.addAll(type_parameter_creation_block)
		type_parameter_body.addAll(type_parameter_set_up_block)
		return type_parameter_body
	}

	/**
	 * generates the parts which init the EClasses of the Package
	 * is the fourth section of void initializePackageContents()
	 */
	def private ArrayList<String> generate_e_package_init_package_contents__init_eclasses(){
		//generate init code for the EClasses, their Attributes, References and Type params
		var body = new ArrayList<String>()
		body.add("")
		body.add("//init the EClasses")
		var init_block = new ArrayList<String>()
		for(e_class : this.e_pak.get_all_eclasses_in_package){
			init_block.add("//init the EClass " + e_class.name)
			var e_class_name = e_class.name + "EClass"/*e_class.name.substring(0,1).toLowerCase +
							   e_class.name.substring(1) + "EClass"*/
			//init the class
			//protected EClass initEClass(
			//	EClass c,
			//	Class<?> instanceClass,
			//	String name,
			//	boolean isAbstract,
			//	boolean isInterface,
			//	boolean isGenerated)
			var class_init_command = new StringBuilder("initEClass(")
			class_init_command.append(e_class_name)
			class_init_command.append(", ")
			var instance_class = e_class.name
			var is_generated = true
			if(e_class.instanceClass !== null){
				is_generated = false
				instance_class = e_class.instanceClass.typeName.replace("$", ".")
			}

			class_init_command.append(
'''«instance_class».class, "«e_class.name»", «e_class.isAbstract», «e_class.isInterface», «is_generated»);'''
			)

			init_block.add(class_init_command.toString)

			body.addAll(
				this.generate_e_package_init_package_contents__init_eclasses___add_super_classes(
					e_class,
					e_class_name
				)
			)
			
			init_block.addAll(
				this.generate_e_package_init_package_contents__init_eclasses___structural_features(
					e_class
				)
			)
			
			init_block.addAll(
				this.generate_e_package_init_package_contents__init_eclasses___eoperations(e_class)
			)
		}
		body.addAll(init_block)
		return body
	}

	/**
	 * generates a part of the EClass init-block. It generates the super types and adds them to the
	 * Eclass.
	 * is called by this.generate_e_package_init_package_contents__init_eclasses
	 * @param EClassImpl for which the supers shall be created 
	 * @param String var name of the EClass
	 */
	def private ArrayList<String> generate_e_package_init_package_contents__init_eclasses___add_super_classes(EClass e_class, String e_class_name){
		var body = new ArrayList<String>()
		//init the SuperTypes
		//ReadMe: it seems EMF walks trough all EClasses and checks if one of them has 
		//type params. if not then all will be added to ESuperTypes
		//if one does have one, all are generated as Generic supers and added to EGenericSuperTypes
		if(!e_class.ESuperTypes.isEmpty){		
			for(super_class : e_class.ESuperTypes){
				var entry = new StringBuilder(e_class_name)
				entry.append(".getESuperTypes().add(")
				if(super_class.EPackage.nsURI.equals(EcorePackage.eNS_URI)){
					entry.append("ecorePackage.get")
				} else {
					if(this.e_pak.equals(super_class.EPackage)) entry.append("this.get")
					else entry.append(
						"the" +
						packages.get(super_class.EPackage).get_emf_package_class_name() +
						".get"
					)
				}
				//var super_class_name = super_class.name.substring(0,1).toUpperCase + 
				//						 super_class.name.substring(1)
				var super_class_name = super_class.name
				entry.append(super_class_name)
				entry.append("());")
				body.add(entry.toString())
			}
		}
		return body
	}
	
	/**
	 * generates a part of the EClass init-block. It generates the structural features
	 * is called by this.generate_e_package_init_package_contents__init_eclasses
	 */
	def private ArrayList<String> generate_e_package_init_package_contents__init_eclasses___structural_features(EClass e_class){
		var init_block = new ArrayList<String>()
		for(structural_feature : this.structural_feature_precedence_map.get(e_class)){
		  	//get the features type and make changes accordingly
			if(structural_feature.get_inspected_object_type == InspectedObjectType.EATTRIBUTE){
				//EAttribute initEAttribute(
				var att_inspector = structural_feature as AttributeInspector
				init_block.addAll(att_inspector.get_type_init_commands())
			} else {
				//EReference initEReference(
				var ref_inspector = structural_feature as ReferenceInspector
				init_block.addAll(ref_inspector.get_type_init_commands())
			}
		}
		if(!init_block.isEmpty) init_block.add(0, "//EStructuralFeatures for EClass " + e_class.name)
		return init_block
	}
	
	/**
	 * generates a part of the EClass init-block. It generates the EOperations
	 * is called by this.generate_e_package_init_package_contents__init_eclasses
	 */
	def private ArrayList<String> generate_e_package_init_package_contents__init_eclasses___eoperations(EClass e_class){
		var ArrayList<String> init_block = new ArrayList<String>()
		for(e_operation : this.eoperation_precedence_map.get(e_class)){
			e_operation.generate_init_code_for_package_class(EMFPackageSourceCreator.emf_model)
			init_block.addAll(e_operation.get_type_init_commands())
		}
		if(!init_block.isEmpty) init_block.add(0, "//EOperations for EClass " + e_class.name)
		return init_block
	}
	
	/**
	 * generates a part of the EClass init-block. It generates the EDataTypes
	 * is called by this.generate_e_package_init_package_contents
	 */
	def private ArrayList<String> generate_e_package_init_package_contents__init_edata_types(){
		var body = new ArrayList<String>()
		/* protected EDataType initEDataType(EDataType d,
                                  java.lang.Class<?> instanceClass,
                                  java.lang.String name,
                                  boolean isSerializable,
                                  boolean isGenerated)

			protected EDataType initEDataType(EDataType d,
                                  java.lang.Class<?> instanceClass,
                                  java.lang.String name,
                                  boolean isSerializable,
                                  boolean isGenerated,
                                  java.lang.String instanceTypeName)
		 */
		for(edatatype : this.e_pak.get_all_edata_types_in_package){
			var entry = new StringBuilder("initEDataType(")
			//the data type itself
			entry.append(edatatype.name.substring(0,1).toLowerCase)
			entry.append(edatatype.name.substring(1))
			entry.append("EDataType, ")
			//the class
			if(edatatype.instanceClass === null) 
				throw new IllegalArgumentException(
				"The following EDataType was specified without an instance-class." +
				"Code cannot be generated as model is faulty" + System.lineSeparator +
				edatatype.toString
				)
			entry.append(edatatype.instanceClass.name)
			entry.append(".class, ")
			//add EDataType name
			entry.append("\"" + edatatype.name + "\", ")
			//add isSerializable
			entry.append((edatatype.isSerializable) ? "" : "!")
			entry.append("IS_SERIALIZABLE, ")
			
			//add IS_GENERATED_INSTANCE_CLASS
			//TODO wann sind EDataTypes "IS_GENERATED_INSTANCE_CLASS"
			entry.append("!IS_GENERATED_INSTANCE_CLASS")
			
			//EDataType init code sometime uses weird constructors
			/*if(edatatype.instanceTypeName !== null && !edatatype.instanceTypeName.isNullOrEmpty){
				entry.append(", \"")
				entry.append(edatatype.instanceTypeName)
				entry.append("\"")
			}*/
			entry.append(");")

			body.add(entry.toString)
		}
		if(!body.isEmpty){
			body.add(0, "")
			body.add(1, "//init EDataTypes")
		}
		return body
	}

	/**
	 * generates a part of the EClass init-block. It generates the EEnums
	 * is called by this.generate_e_package_init_package_contents
	 */
	def private ArrayList<String> generate_e_package_init_package_contents__init_eenums(){
		var body = new ArrayList<String>()
		for(EEnum eenum : this.e_pak.get_all_eenums_in_package){
			this.add_import_as_String(this.e_pak.get_package_declaration_name + "." + eenum.name)
			body.add(
				'''initEEnum(«eenum.name»EEnum, «eenum.name».class, "«eenum.name»");'''.toString
			)
			for(entry : eenum.ELiterals){
				body.add(
		'''addEEnumLiteral(«eenum.name»EEnum, «eenum.name».«entry.name.toUpperCase»);'''.toString()
				)
			}
		}
		return body
	}

	/**
	 * Generates the method which is used to initialize the contents of a package after they
	 * have been created. It is here, where the GenericTypeParameters are created, instanced,
	 * and used to assemble the EMF-meta-model instances.<br><br>
	 *
     * This method is called by {@link #initialize_creator(String, String) initialize_creator()}
	 * and calls following methods to accomplish its goal:
	 * <ul>
	 *	<li> {@link #generate_e_package_init_package_contents__package_init()
	 		  generate_e_package_init_package_contents__package_init()} </li>
	 *	<li> {@link #generate_e_package_init_package_contents__create_type_params___for_eclasses()
			  generate_e_package_init_package_contents__create_type_params___for_eclasses()} </li>
	 *	<li> {@link #generate_e_package_init_package_contents__create_type_params___for_edatatypes()
	 		  generate_e_package_init_package_contents__create_type_params___for_edatatypes()} </li>
	 *	<li> {@link #generate_e_package_init_package_contents__add_package_dependencies()
	 		  generate_e_package_init_package_contents__add_package_dependencies()} </li>
	 *	<li> {@link #generate_e_package_init_package_contents__init_eclasses()
	          generate_e_package_init_package_contents__init_eclasses()} </li>
	 *	<li> {@link #generate_e_package_init_package_contents__init_edata_types()
	 		  generate_e_package_init_package_contents__init_edata_types()} </li>
	 *	<li> {@link #generate_e_package_init_package_contents__init_eenums()
	 		  generate_e_package_init_package_contents__init_eenums()} </li>
	 * </ul>
	 * @author Adrian Zwenger
	 */
	def private void generate_e_package_init_package_contents(){
		var declaration = "public void initializePackageContents()"
		var body = new ArrayList<String>()

		body.addAll(this.generate_e_package_init_package_contents__package_init())

		//type_params needed to be generated first but added later, as new package dependencies
		//may be found during generation
		var type_parameter_body = this.generate_e_package_init_package_contents__create_type_params___for_eclasses()
		type_parameter_body.addAll(
			this.generate_e_package_init_package_contents__create_type_params___for_edatatypes()
		)

		body.addAll(this.generate_e_package_init_package_contents__add_package_dependencies())

		//add the type params
		body.addAll(type_parameter_body)

		//init the EClasses, their super-classes, EAttributes, EReferences and EOperations
		body.addAll(this.generate_e_package_init_package_contents__init_eclasses())
		
		body.addAll(this.generate_e_package_init_package_contents__init_edata_types())
		
		body.addAll(this.generate_e_package_init_package_contents__init_eenums())
		
		if(this.e_pak.get_emf_e_package.ESuperPackage === null)
			body.add("createResource(this.eNS_URI);")

		method_declarations.add(declaration)
		methods.put(declaration, body)
	}

	/**
	 * Generates the createPackageContents-method used by EMF to create and register all contents
	 * of the package.<br><br>
	 * The contents of a Package can be:
	 * <ol>
	 *	<li> Interfaces </li> 
	 *  <li> Abstract Classes </li>
	 *  <li> Classes based on {@link emfcodegenerator.util.SmartObject SmartObject}</li>
	 *  <ul>
	 *		<li> {@link org.eclipse.emf.ecore.EAttribute EAttributes}</li>
	 *		<li> {@link org.eclipse.emf.ecore.EReference EReferences} </li> 
	 *		<li> {@link org.eclipse.emf.ecore.EOperation EOperations} </li>
	 *	</ul>
	 * <li> EEnums </li>
	 * <li> EDataTypes (this framework does support EDataType generation. However, the user must
	 * ensure compatibility and that it works properly</li>
	 * </ol>
 	 * Called by {@link #initialize_creator(String, String) initialize_creator()}.<br>
	 * @author Adrian Zwenger
	 */
	def private void generate_e_package_create_package_contents(){
		var declaration = "public void createPackageContents()"
		var body = new ArrayList<String>()

		body.add("if (this.isCreated) return;")
		body.add("this.isCreated = true;")

		for(ecl : this.e_pak.get_all_eclasses_in_package()){
			var entry = new StringBuilder("this.")
			var local_class_var_name = ecl.name + "EClass"

			//create the EClass
			body.add(
		'''«local_class_var_name» = this.createEClass(«emf_to_uppercase(ecl.name)»);'''.toString()
			)

			/* 
			 * create the EAttributes and EReferences, which are not directly inherited in order
			 * the order in which things need to be processed is stored in the value of
			 * {@link #structural_feature_precedence_map structural_feature_precedence_map}
			 */
			for(obj_field : this.structural_feature_precedence_map.get(ecl)){
				entry = new StringBuilder("this.")
				entry.append(
					(obj_field.get_inspected_object_type === InspectedObjectType.EATTRIBUTE) ?
					"createEAttribute(" : "createEReference("
				)
				
				entry.append(
'''«local_class_var_name», «this.e_pak.get_emf_package_class_name()».«emf_to_uppercase(ecl.name)»__«emf_to_uppercase(obj_field.get_name)»);'''
				)
				
				body.add(entry.toString)
			}

			//create EOperations
			for(e_op : this.eoperation_precedence_map.get(ecl)){
				body.add(
'''this.createEOperation(«local_class_var_name», «this.e_pak.get_emf_package_class_name()».«emf_to_uppercase(ecl.name)»___«emf_to_uppercase(e_op.get_name)»);'''.toString
				)
			}
		}

		//create custom EDataTypes
		for(e_data_type : this.e_pak.get_all_edata_types_in_package()){
			body.add(
'''this.«e_data_type.name.substring(0, 1).toLowerCase()»«e_data_type.name.substring(1)»EDataType = createEDataType(«emf_to_uppercase(e_data_type.name)»);'''
			)
		}
		
		//create EEnums
		for(eenum : this.e_pak.get_all_eenums_in_package){
			body.add(
		'''this.«eenum.name»EEnum = createEEnum(«emf_to_uppercase(eenum.name)»);'''.toString()
			)
		}

		this.method_declarations.add(declaration)
		this.methods.put(declaration, body)
	}

	/**########################Public methods########################*/

	/**
	 * control flow for code generation<br>
	 * <ol>
	 *	<li>sets target file path and IDENTION string</li>
	 *	<li>generates package and class declarations</li>
	 *	<li>generates all members and methods for all EClasses in the package</li>
	 *	<li>generates all members and methods for all EDataTypes in the package</li>
	 *	<li>generates all members and methods for all EEnums in the package</li>
	 *	<li>generates the "init()"-method</li>
	 *	<li>generates the "createPackageContents()"-method</li>
	 *	<li>generates the "initializePackageContents()"-method</li>
	 *	<li>flags this Creator as initialized</li>
	 * </ol>
	 */
	override initialize_creator(String fq_file_path, String IDENTION) {
		this.class_declaration = "public class " +
								 this.e_pak.get_name_with_first_letter_capitalized + "Package"+
								 "Impl extends EPackageImpl implements " +
								 this.e_pak.get_emf_package_class_name()
		this.package_declaration = "package " + this.e_pak.get_package_declaration_name + ".impl;"

		this.IDENTION = IDENTION
		this.fq_file_name = fq_file_path

		for(EClass e_class : this.e_pak.get_all_eclasses_in_package())
			this.register_methods_and_data_fields_for_eclass(e_class)

		for(data_type : this.e_pak.get_all_edata_types_in_package())
			this.register_methods_and_data_fields_for_edatatype(data_type)

		for(eenum : this.e_pak.get_all_eenums_in_package())
			this.register_methods_and_data_fields_for_eenums(eenum)

		this.generate_e_package_init()
		this.generate_e_package_create_package_contents()
		this.generate_e_package_init_package_contents()
		this.is_initialized = true
	}
	
	override write_to_file() {
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		
		var package_file = new File(this.fq_file_name)
		package_file.getParentFile().mkdirs()
		var package_fw = new FileWriter(package_file , false)
		var import_block = new StringBuilder()
		var data_fields = new StringBuilder()
		var method_block = new StringBuilder()

		for(import_string : this.get_needed_imports()){
			import_block.append('''import «import_string»;«System.lineSeparator»'''.toString)
		}import_block.append(System.lineSeparator)
		
		for(declaration : this.method_declarations){
			var method = new StringBuilder(IDENTION)
			method.append(
				'''«declaration» { «System.lineSeparator»'''.toString
			)

			var iterator = this.methods.get(declaration).iterator

			while(iterator.hasNext){
				method.append(
					'''«IDENTION»«IDENTION»«iterator.next»«System.lineSeparator»'''.toString()
				)
			}

			method.append('''«IDENTION»}«System.lineSeparator»«System.lineSeparator»'''.toString)
			method_block.append(method.toString)
		}

		for(declaration : this.object_fields){
			data_fields.append('''«IDENTION»«declaration»«System.lineSeparator»'''.toString)
		}data_fields.append(System.lineSeparator)

		package_fw.write(
			'''«this.package_declaration»«System.lineSeparator»«System.lineSeparator»'''.toString()
		)
		package_fw.write(import_block.toString)
		package_fw.write(
			'''«this.class_declaration» {«System.lineSeparator»«System.lineSeparator»'''.toString()
		)
		package_fw.write(data_fields.toString)
		package_fw.write(method_block.toString)
		package_fw.write("}" + System.lineSeparator)
		package_fw.close()
	}
	
	override boolean equals(Object other){
		if(!(other instanceof EMFPackageSourceCreator)) return false
		return this.e_pak.equals((other as EMFPackageSourceCreator).e_pak)
	}
	
	override int hashCode(){
		return this.e_pak.hashCode()
	}
}