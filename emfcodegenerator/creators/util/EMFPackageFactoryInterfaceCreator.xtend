package emfcodegenerator.creators.util

import emfcodegenerator.EcoreGenmodelParser
import emfcodegenerator.inspectors.util.PackageInspector
import emfcodegenerator.EMFCodeGenerationClass
import emfcodegenerator.creators.FileCreator
import java.util.HashMap
import org.eclipse.emf.ecore.EClass
import java.util.ArrayList
import java.io.File
import java.io.FileWriter

/**
 * creates the interface for the EMFPackageFactory
 */
class EMFPackageFactoryInterfaceCreator extends EMFCodeGenerationClass implements FileCreator{
	
	/**########################Attributes########################*/
	
	var protected PackageInspector e_pak
	var protected String interface_name
	var protected String package_declaration
	var protected boolean is_initialized = false
	var protected String file_path
	var protected String IDENTION
	var HashMap<EClass,String> e_class_to_getter_declaration_map =
		new HashMap<EClass,String>()
	var ArrayList<String> interface_body = new ArrayList<String>()

	/**########################Constructors########################*/

	new(EcoreGenmodelParser gen_model, PackageInspector package_inspector){
		super(gen_model)
		this.e_pak = package_inspector
		this.interface_name = this.e_pak.get_emf_package_factory_class_name()
		this.package_declaration = this.e_pak.get_package_declaration_name()
		this.add_import_as_String(this.e_pak.get_needed_imports_for_package_factory())
		for(e_class : this.e_pak.get_all_eclasses_in_package){
			e_class_to_getter_declaration_map.put(
				e_class,
				this.create_getter_declaration_for_e_class(e_class)
			)
		}
	}

	/**########################Methods########################*/

	def protected String get_getter_method_declaration_for_e_class(EClass e_class){
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		return this.e_class_to_getter_declaration_map.get(e_class)
	}

	def private String create_getter_declaration_for_e_class(EClass e_class){
		var type_decl = this.e_pak.get_type_arguments_declaration_for_eclass(e_class)
		var reduced_type_decl =
			this.e_pak.get_reduced_type_arguments_declaration_for_eclass(e_class)
		return '''«type_decl» «e_class.name»«reduced_type_decl» create«e_class.name»()'''.toString
	}

	/**########################Public Methods########################*/

	override initialize_creator(String fq_file_path, String IDENTION) {
		this.is_initialized = true
		this.file_path = fq_file_path
		this.IDENTION = IDENTION

		//add the package declaration		
		this.interface_body.add("package " + this.package_declaration + ";")
		this.interface_body.add(System.lineSeparator)

		//add the needed imports
		for(needed_import : this.needed_imports){
			this.interface_body.add(
				"import " + needed_import + ";"
			)
		}
		this.interface_body.add(System.lineSeparator)
		
		//declare the interface
		this.interface_body.add(
			'''public interface «this.interface_name» extends EFactory {'''.toString
		)
		this.interface_body.add(System.lineSeparator)
		
		//add data fields
		this.interface_body.add(
		'''«IDENTION»«this.interface_name» eINSTANCE = «this.package_declaration».impl.«this.interface_name»Impl.init();'''.toString
		)
		this.interface_body.add(System.lineSeparator)
		
		//add the getter method for the PackageFactory
		this.interface_body.add(
			'''«IDENTION»«this.interface_name» get«this.interface_name»();'''.toString
		)
		this.interface_body.add(System.lineSeparator)
		
		//add the rest of the getters
		for(method : this.e_class_to_getter_declaration_map.keySet){
			this.interface_body.add(
				'''«IDENTION»«this.e_class_to_getter_declaration_map.get(method)»;'''.toString
			)
			this.interface_body.add(System.lineSeparator)
		}
		this.interface_body.add("}")
		this.interface_body.add(System.lineSeparator)
	}
	
	override write_to_file() {
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		var factory_file = new File(this.file_path)
		factory_file.getParentFile().mkdirs()
		var factory_fw = new FileWriter(factory_file, false)
		for(line : this.interface_body) factory_fw.write(line)
		factory_fw.close()
		this.is_initialized = false
	}

}
