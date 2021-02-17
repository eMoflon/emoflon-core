package emfcodegenerator.creators

import emfcodegenerator.EMFCodeGenerationClass
import emfcodegenerator.EcoreGenmodelParser
import emfcodegenerator.inspectors.util.AbstractObjectFieldInspector
import emfcodegenerator.inspectors.util.EOperationInspector
import emfcodegenerator.inspectors.util.PackageInspector
import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EClass

/**
 * Abstract class with shared data-fields and methods for the
 * {@link emfcodegenerator.creators.util.InterfaceCreator InterfaceCreator}
 * {@link emfcodegenerator.creators.util.SourceCodeCreator SourceCodeCreator}.
 * @author Adrian Zwenger
 */
abstract class ModelFileCreator extends EMFCodeGenerationClass implements FileCreator{
	
	/**
	 * The EClass for which an interface or an implementation shall be created
	 */
	var protected EClass e_class

	/**
	 * a HashSet containing ObjectFieldInspector-objects for all Attributes and References the
	 * EClass contains
	 */
	var protected HashSet<AbstractObjectFieldInspector> e_data_fields

	/**
	 * A HashSet containing EOperationInspector-objects for all EOPerations the
	 * EClass contains 
	 */
	var protected HashSet<EOperationInspector> e_operations

	/**
	 * String storing the declaration for the package in which the class/interface is located
	 */
	var protected String package_declaration

	/**
	 * Boolean representing if the class was initialised
	 */
	var protected boolean is_initialized = false

	/**
	 * String representing the path where to file is located
	 */
	var protected String fq_file_path
	
	/**
	 * String representing the characters to be used to indent code
	 */
	var protected String IDENTION

	/**
	 * a PackageInspector instance which inspects the EPackage in hwich the EClass is contained
	 */
	var protected PackageInspector e_pak
	
	/**########################Constructor########################*/
	
	/**
	 * Creates a new ModelFileCreator
	 * @param eclass EClassImpl the EClass for which a file shall be created
	 * @param gen_model EcoreGenmodelParser the wrapper for the ecore-xmi and genmodel-xmi files
	 * @param e_data_fields HashSet<ObjectFieldInspector> the ObjectFieldInspector's for the
	 contained Attributes/References
	 * @param e_operations HashSet<EOperationInspector> the EOperationInspector's for all the
	 EOperations
	 * @author Adrian Zwenger
	 */
	new(EClass eclass, EcoreGenmodelParser gen_model,
		HashSet<AbstractObjectFieldInspector> e_data_fields,
		HashSet<EOperationInspector> e_operations,
		PackageInspector e_pak){
		super(gen_model)
		this.e_pak = e_pak
		this.e_class = eclass

		if(!e_pak.get_all_eclasses_in_package().contains(e_class)) 
			throw new IllegalArgumentException(
			'''The EClass «e_class.name» is not contained in EPackage «e_pak.get_name()»'''
			)

		// register the file path
		var file_path_relative_to_package_hierarchy =
			emf_model.get_object_to_class_name_map().get(e_class)
		//register the package declaration
		package_declaration =
			'''«convert_fqdn_file_path_to_package_name(file_path_relative_to_package_hierarchy)»'''
		//create the declaration for the interface
		
		this.e_data_fields = new HashSet<AbstractObjectFieldInspector>(e_data_fields)
		this.e_operations  = new HashSet<EOperationInspector>(e_operations)
		
	}

	/**########################Helper Methods########################*/

	/**
	 * Takes fqdn file-path without file-extension (relative to root folder of project) and returns
	 * the corresponding package where the file is loaded.<br>
	 * Example: <br>
	 * <b>Input</b> = "package/subpackage/myClass"<br>
	 * <b>Output</b> = "package.subpackage"
	 * @param fq_file_name String path to file without file-extension
	 * @return String containing the path package of the java file
	 * @author Adrian Zwenger
	 */
	def private String convert_fqdn_file_path_to_package_name(String fq_file_name){
		var package_path = fq_file_name.replace(GENERATED_FILE_DIR, "").split("/")
		//example for a file path: package/subpackage/myClass
		//thus splitting at every "/" and removing the GENERATED_FILE_PATH part
		return String.join(".", Arrays.copyOfRange(package_path, 0, package_path.size - 1)
		)
	}

	/**
	 * adds imports as a String, however it checks first if it needs to be imported or if it is
	 * in the same package and thus does not need to be imported.
	 * @param String import_string
	 */
	override add_import_as_String(String import_string){
		var string_import_as_list = import_string.split("\\.")
		var import_package = String.join(".", Arrays.copyOfRange(string_import_as_list, 0,
									     string_import_as_list.size - 1))
		if(!import_package.equals(this.package_declaration)){
			super.add_import_as_String(import_string)
		}
	}

	/**########################Getters########################*/

	/**
	 * returns all method declarations in order of creation.
	 * @return ArrayList<String> containing method_declarations
	 * @author Adrian Zwenger
	 */
	abstract def ArrayList<String> get_method_declarations()

	/**
	 * Returns the EPackage in which the class/interface is contained.
	 * @return EPackage of the class/interface
	 * @author Adrian Zwenger
	 */
	abstract def EPackage get_package()
	
	/**
	 * returns the name of the interface/class
	 * @return String representing the interfaces/classes name
	 * @author Adrian Zwenger
	 */
	abstract def String get_name()

	/**
	 * returns the String which is needed to declare which package an interface/class belongs to.
	 * @param String representing the String needed for declaring the package of the file
	 * @author Adrian Zwenger
	 */
	abstract def String get_package_declaration()

	/**
	 * Returns the classes or interfaces signature/declaration as String
	 * @return String representing the declaration needed for the class/interface itself
	 * @author Adrian Zwenger
	 */
	abstract def String get_declaration()

	/**########################Control Flow########################*/

	override toString(){
		return this.e_class.toString()
	}
}
