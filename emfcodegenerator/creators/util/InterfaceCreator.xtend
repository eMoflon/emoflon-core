package emfcodegenerator.creators.util

import emfcodegenerator.EcoreGenmodelParser
import emfcodegenerator.creators.ModelFileCreator
import emfcodegenerator.inspectors.ObjectFieldInspector
import emfcodegenerator.inspectors.util.EOperationInspector
import emfcodegenerator.inspectors.util.PackageInspector
import java.io.File
import java.io.FileWriter
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EGenericType
import org.eclipse.emf.ecore.impl.EClassImpl
import emfcodegenerator.inspectors.util.AbstractObjectFieldInspector

/**
 * creates the interface file for an EClass
 */
class InterfaceCreator extends ModelFileCreator {

	/**########################Attributes########################*/

	/**
	 * if the EClass uses Generics, in this String the declaration will be stored.
	 * Example: "<E,G>"
	 */
	var protected String generic_type_declaration_string = ""

	/**
	 * ArrayList containing all method-declarations as String
	 */
	var protected ArrayList<String> method_declarations

	/**
	 * String containing the interface declaration. example: "public interface myclass"
	 */
	var String interface_declaration
	
	protected var generic_super_types_map = new HashMap<EClass, EGenericType>()

	/**########################Constructors########################*/

	/**
	 * Creates a new InterfaceCreator
	 * @param eclass EClassImpl the EClass for which a file shall be created
	 * @param gen_model EcoreGenmodelParser the wrapper for the ecore-xmi and genmodel-xmi files
	 * @param e_data_fields HashSet<ObjectFieldInspector> the ObjectFieldInspector's for the contained Attributes/References
	 * @param e_operations HashSet<EOperationInspector> the EOperationInspector's for all the EOperations
	 */
	new(EClassImpl eclass, EcoreGenmodelParser gen_model,
		HashSet<AbstractObjectFieldInspector> e_data_fields,
		HashSet<EOperationInspector> e_operations,
		PackageInspector e_pak){
		super(eclass, gen_model, e_data_fields, e_operations, e_pak)

		this.generic_type_declaration_string = this.e_pak.get_type_arguments_declaration_for_eclass(e_class)
		this.add_import_as_String(this.e_pak.get_eclass_to_needed_imports_for_type_arguments_map.get(this.e_class))
		for(generic_super_type : this.e_class.EGenericSuperTypes){
			generic_super_types_map.put(generic_super_type.EClassifier as EClass, generic_super_type)
		}
	}

	/**########################Declaration-Generation########################*/

	/**
	 * creates the generic parameter declaration for a given EGenericType. Example: the described
	 * EGenericType is an {@literal ArrayList<b extends ClassA<?>>},
	 * the output would be: "{@literal ArrayList<b extends ClassA<?>>}" if called with empty String.
	 * Method is recursive
	 * @param generic EGenericType generic for which this method is called
	 * @param declaration String declaration saves the recursion state up until then.
	 * 		  expects empty String on first call
	 * @return String
	 * @author Adrian Zwenger
	 */
	def protected String etype_param_declarationgetter(EGenericType generic, String declaration){
		var new_declaration = declaration
		//get name of dependency and import them if needed
		if(generic.EClassifier !== null){
			new_declaration += generic.EClassifier.name
			this.add_import(generic.EClassifier)
		} else if (generic.ETypeParameter !== null) new_declaration += generic.ETypeParameter.name
		else new_declaration += "?"
		if(!generic.ETypeArguments.isEmpty){
			new_declaration += "<"
			var generics_iterator = generic.ETypeArguments.iterator
			while(generics_iterator.hasNext){
				new_declaration += this.etype_param_declarationgetter(generics_iterator.next, "")
				if(generics_iterator.hasNext) new_declaration += ","
			}
			new_declaration += ">"
		}
		if(generic.EUpperBound !== null){
			new_declaration += " extends "
			new_declaration += this.etype_param_declarationgetter(generic.EUpperBound, "")
		} else if (generic.ELowerBound !== null) {
			new_declaration += " super "
			new_declaration += this.etype_param_declarationgetter(generic.EUpperBound, "")
		}
		return new_declaration
	}

	/**
	 * This method creates the declaration for the interface including extension and
	 * implementation flags
	 * Example: public interface MyInterface extends SomeObject implements Stuff, OtherStuff
	 * @returns String
	 */
	def private String create_interface_declaration(){
		var declaration = "public interface " + e_class.name
		declaration += this.generic_type_declaration_string

		if(e_class.ESuperTypes.isEmpty()){
			// EMF interfaces extend the EObject class if it does not extend
			// other classes or implements other interfaces
			declaration = declaration + " extends EObject, MinimalSObjectContainer"
			//implement EObject interface to keep basic EMF compatibility
			add_import_as_String("org.eclipse.emf.ecore.EObject")
			//implement MinimalSObjectContainer for access to containment features for EReferences
			add_import_as_String("emfcodegenerator.util.MinimalSObjectContainer")
		}
		else if (!e_class.ESuperTypes.isEmpty()){
			// if the interface does extend class(es), they need to be declared
			declaration = declaration + " extends "
			var iterator = e_class.ESuperTypes.iterator
			while(iterator.hasNext){
				var super_class = iterator.next
				// import the super package
				add_import_as_String(create_import_name_for_ereference_or_eclass(super_class))
				
				if(generic_super_types_map.keySet.contains(super_class)){
					declaration += 
						this.etype_param_declarationgetter(generic_super_types_map.get(super_class), "")
				} else {
					declaration = declaration + super_class.name
				}
				if(iterator.hasNext) declaration = declaration + ", "// only add "," if needed
			}
		}
		return declaration
	}

	/**
	 * Takes an ObjectFieldInspector and generates a getter method-declaration for it
	 * @param ObjectFieldInspector inspecting the object-field
	 * @return String containing the method-declaration
	 */
	def protected static String create_getter_method_stump(ObjectFieldInspector e_attr){
		var String var_type = e_attr.get_object_field_type_name()
		if(e_attr.is_a_tuple()) var_type = '''EList<«var_type»>'''.toString()
		return '''«var_type» get«e_attr.get_name_with_first_letter_capitalized()»()'''.toString()
	}

	/**
	 * Takes an ObjectFieldInspector and generates a setter method-declaration for it
	 * @param ObjectFieldInspector inspecting the object-field
	 * @return String containing the method-declaration
	 */
	def protected static String create_setter_method_stump(ObjectFieldInspector e_attr){
		var String var_type = e_attr.get_object_field_type_name()
		if(e_attr.is_a_tuple()) var_type = '''EList<«var_type»>'''.toString()
		return '''void set«e_attr.get_name_with_first_letter_capitalized()»(«var_type» value)'''.toString()
	}

	/**
	 * Takes an ObjectFieldInspector and generates an unset method-declaration for it
	 * @param ObjectFieldInspector inspecting the object-field
	 * @return String containing the method-declaration
	 */
	def protected static String create_unset_method_stump(ObjectFieldInspector e_attr){
		return '''void unset«e_attr.get_name_with_first_letter_capitalized»()'''.toString()
	}
	
	/**
	 * Takes an ObjectFieldInspector and generates an isset method-declaration for it
	 * @param ObjectFieldInspector inspecting the object-field
	 * @return String containing the method-declaration
	 */
	def protected static String create_isset_method_stump(ObjectFieldInspector e_attr){
		return '''boolean isSet«e_attr.get_name_with_first_letter_capitalized»()'''.toString()
	}

	/**########################Getters########################*/

	/**
	 * returns all method declarations
	 */
	override get_method_declarations(){
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		return method_declarations
	}

	/**
	 * Returns the interface declaration
	 */
	override get_declaration(){
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		return interface_declaration
	}

	/**
	 * returns the EPackage in which the interface is. Can be run if the Creator is not initialised yet
	 */
	override get_package(){
		return e_class.EPackage
	}
	
	/**
	 * returns the name of the interface
	 */
	override get_name() {
		return e_class.name
	}
	
	/**
	 * returns the String which is needed to declare to package an interface belongs to
	 */	
	override get_package_declaration(){
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		return package_declaration
	}

	/**########################Control Flow########################*/

	/**
	 * generates the declarations needed for the methods and stores them in a HashSet
	 */
	def private void generate_method_declarations(){
		for(data_field : e_data_fields){
			method_declarations.add(create_getter_method_stump(data_field))
			if(data_field.needs_setter_method())
				method_declarations.add(create_setter_method_stump(data_field))
			if(data_field.is_unsettable()){
				method_declarations.add(create_isset_method_stump(data_field))
				if(data_field.needs_setter_method())
					method_declarations.add(create_unset_method_stump(data_field))
			}
			this.add_import_as_String(data_field.get_needed_imports)
		}
		for(EOperationInspector e_op : e_operations){
			method_declarations.add(e_op.get_method_declaration())
			this.add_import_as_String(e_op.get_needed_imports)
		}
	}

	/**
	 * prepares the Creator for parsing and assembling
	 */
	override initialize_creator(String fq_file_path, String IDENTION){
		this.fq_file_path = fq_file_path
		this.IDENTION = IDENTION
		method_declarations = new ArrayList<String>()
		interface_declaration = create_interface_declaration()
		generate_method_declarations()
		this.is_initialized = true
	}

	/**
	 * starts the writing process writes the interface/class source-code contents to a file
	 */
	override write_to_file(){
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		var interface_file = new File(this.fq_file_path)
		interface_file.getParentFile().mkdirs()
		var interface_fw = new FileWriter(interface_file , false)

		//write package declaration
		interface_fw.write("package " + this.get_package_declaration +";")
		interface_fw.write(System.lineSeparator + System.lineSeparator)

		//add imports
		for(needed_import : this.get_needed_imports()){
			interface_fw.write('''import «needed_import»;'''.toString)
			interface_fw.write(System.lineSeparator)
		}
		interface_fw.write(System.lineSeparator)

		//declare the interface
		interface_fw.write(this.get_declaration + " {")
		interface_fw.write(System.lineSeparator + System.lineSeparator)

		//Write the method declaration
		for(method : this.get_method_declarations()){
			interface_fw.write(IDENTION + method + ";")
			interface_fw.write(System.lineSeparator)
		}

		//close interface
		interface_fw.write(System.lineSeparator + "}" + System.lineSeparator)
		interface_fw.close()
		this.is_initialized = false	
	}

	override boolean equals(Object other){
		if(!(other instanceof InterfaceCreator)) return false
		return this.e_class.equals((other as InterfaceCreator).e_class)
	}
	
	override int hashCode(){
		return this.e_class.hashCode()
	}
}
