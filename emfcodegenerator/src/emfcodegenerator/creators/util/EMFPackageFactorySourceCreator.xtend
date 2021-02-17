package emfcodegenerator.creators.util

import emfcodegenerator.inspectors.util.PackageInspector
import emfcodegenerator.EcoreGenmodelParser
import org.eclipse.emf.ecore.EClass
import java.util.TreeMap
import java.io.File
import java.io.FileWriter

/**
 * creates the implementation for the package factory
 */
class EMFPackageFactorySourceCreator extends EMFPackageFactoryInterfaceCreator {
	
	/**########################Attributes########################*/

	/**
	 * stores the name for the package-factory class
	 */
	var String class_name
	
	/**
	 * stores precedence of method declaration and the method body as the value.
	 */
	var TreeMap<String,String> method_declaration_to_body_map =
		new TreeMap<String,String>()

	/**########################Constructors########################*/

	/**
	 * Creates a new EMFPackageFactorySourceCreator
	 * @param gen_model EcoreGenmodelParser
	 * @param package_inspector PackageInspector
	 * @author Adrian Zwenger
	 */
	new(EcoreGenmodelParser gen_model, PackageInspector package_inspector){
		super(gen_model, package_inspector)
		this.class_name = this.interface_name + "Impl"
		//add the interface as an import
		this.add_import_as_String(this.package_declaration + "." + this.interface_name)
		this.add_import_as_String(this.package_declaration + ".*")
		this.add_import_as_String("org.eclipse.emf.ecore.EClass")
		this.add_import_as_String("org.eclipse.emf.ecore.EObject")
		this.add_import_as_String("org.eclipse.emf.ecore.EPackage")
		this.add_import_as_String("org.eclipse.emf.ecore.impl.EFactoryImpl")
		this.add_import_as_String("org.eclipse.emf.ecore.plugin.EcorePlugin")

		//change the package declaration to the proper one
		this.package_declaration += ".impl"
	}

	/**########################Getter Methods########################*/

	/**
	 * @inheritDoc
	 */
	override String get_getter_method_declaration_for_e_class(EClass e_class){
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		return "public" + super.get_getter_method_declaration_for_e_class(e_class)
	}

	/**
	 * Creates a getter-method body for a given EClass and returns it
	 * @param e_class EClass
	 * @param IDENTION String
	 * @return String
	 * @author Adrian Zwenger
	 */
	def private String create_getter_body_for_eclass(EClass e_class, String IDENTION){
		var reduced_type_decl =
			this.e_pak.get_reduced_type_arguments_declaration_for_eclass(e_class)
		var String body =
'''
«IDENTION»«IDENTION»«e_class.name»Impl«reduced_type_decl» «e_class.name» = new «e_class.name»Impl«reduced_type_decl»();
«IDENTION»«IDENTION»return «e_class.name»;
'''.toString
		return body
	}

	/**
	 * Package-factories do not have a public constructor. Instead they have an init()-command
	 * which is generated here.
	 * @param IDENTION String
	 * @return String
	 * @author Adrian Zwenger
	 */
	def private String create_factory_init_command(String IDENTION){
		var body =
'''
«IDENTION»«IDENTION»try {
«IDENTION»«IDENTION»«IDENTION»«this.interface_name» the«this.interface_name» = («this.interface_name») EPackage.Registry.INSTANCE.getEFactory(«this.e_pak.get_emf_package_class_name».eNS_URI);
«IDENTION»«IDENTION»«IDENTION»if (the«this.interface_name» != null) {
«IDENTION»«IDENTION»«IDENTION»«IDENTION»return the«this.interface_name»;
«IDENTION»«IDENTION»«IDENTION»}
«IDENTION»«IDENTION»} catch (Exception exception) {
«IDENTION»«IDENTION»«IDENTION»EcorePlugin.INSTANCE.log(exception);
«IDENTION»«IDENTION»}
«IDENTION»«IDENTION»return new «this.class_name»();
'''.toString
		return body
	}

	/**
	 * All EObjects contained in the package need a method which assembles them. This is done here.
	 * @param IDENTION String
	 * @return String
	 */
	def private String create_eobject_create_method(String IDENTION){
		var body =
'''
«IDENTION»«IDENTION»switch (eClass.getClassifierID()) {
'''.toString
		//create case for all classes
		for(e_class : this.e_pak.get_all_eclasses_in_package()){
			if(!e_class.isInterface && !e_class.isAbstract) body +=
'''
«IDENTION»«IDENTION»case «this.e_pak.get_emf_package_class_name».«emf_to_uppercase(e_class.name)»:
«IDENTION»«IDENTION»«IDENTION»return create«e_class.name»();
'''.toString
		}

		//add default case
		body +=
'''
«IDENTION»«IDENTION»default:
«IDENTION»«IDENTION»«IDENTION»throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
«IDENTION»«IDENTION»}
'''.toString
		return body
	}

	/**########################Public Methods########################*/

	/**
	 * @inheritDoc
	 */
	override initialize_creator(String fq_file_path, String IDENTION) {
		this.is_initialized = true
		this.file_path = fq_file_path
		this.IDENTION = IDENTION
		
		//create the constructor
		var declaration = '''«IDENTION»public «this.class_name»()'''.toString
		var String body = '''«IDENTION»«IDENTION»super();«System.lineSeparator»'''.toString
		this.method_declaration_to_body_map.put(declaration, body)

		//create the init method
		declaration = '''«IDENTION»public static «this.interface_name» init()'''.toString
		body = this.create_factory_init_command(IDENTION)
		this.method_declaration_to_body_map.put(declaration, body)

		//create Package getters
		declaration =
'''«IDENTION»public «this.e_pak.get_emf_package_class_name» get«this.e_pak.get_emf_package_class_name»()'''.toString
		body =
'''«IDENTION»«IDENTION»return («this.e_pak.get_emf_package_class_name») getEPackage();«System.lineSeparator»'''.toString
		this.method_declaration_to_body_map.put(declaration, body)

		declaration = 
'''
«IDENTION»@Deprecated
«IDENTION»public static «this.e_pak.get_emf_package_class_name» getPackage()'''.toString

		body = 
'''«IDENTION»«IDENTION»return «this.e_pak.get_emf_package_class_name».eINSTANCE;«System.lineSeparator»'''.toString

		this.method_declaration_to_body_map.put(declaration, body)

		//create create(EClass eClass)
		declaration = '''«IDENTION»@Override public EObject create(EClass eClass)'''
		body = this.create_eobject_create_method(IDENTION)
		this.method_declaration_to_body_map.put(declaration, body)
		
		//generate all getters for contained classes
		for(e_class : this.e_pak.get_all_eclasses_in_package){
			if(!e_class.isInterface && !e_class.isAbstract){
				declaration = IDENTION + this.get_getter_method_declaration_for_e_class(e_class)
				body = this.create_getter_body_for_eclass(e_class, IDENTION)
				this.method_declaration_to_body_map.put(declaration, body)
			}
		}
	}
	
	/**
	 * @inheritDoc
	 */
	override write_to_file() {
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		var factory_file = new File(this.file_path)
		factory_file.getParentFile().mkdirs()
		var factory_fw = new FileWriter(factory_file, false)
		
		//declare the package
		factory_fw.write(
			'''package «this.package_declaration»;«System.lineSeparator»'''.toString
		)

		//write imports
		for(needed_import : this.needed_imports){
			factory_fw.write(
				'''import «needed_import»;«System.lineSeparator»'''.toString
			)
		}
		factory_fw.write(System.lineSeparator)

		//declare the class
		factory_fw.write(
			'''public class «this.class_name» extends EFactoryImpl implements «this.interface_name» {'''.toString
		)
		factory_fw.write(System.lineSeparator)
		factory_fw.write(System.lineSeparator)

		//add all methods
		while(!this.method_declaration_to_body_map.isEmpty){
			var entry = this.method_declaration_to_body_map.pollFirstEntry
			factory_fw.write('''«entry.key» {«System.lineSeparator»'''.toString)
			factory_fw.write('''«entry.value»'''.toString)
			factory_fw.write(IDENTION + "}" + System.lineSeparator + System.lineSeparator)
		}
		
		//close the class
		factory_fw.write("}" + System.lineSeparator)

		factory_fw.close()
		this.is_initialized = false
	}
	
}
