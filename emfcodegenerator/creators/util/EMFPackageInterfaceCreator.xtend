package emfcodegenerator.creators.util

import emfcodegenerator.EMFCodeGenerationClass
import emfcodegenerator.EcoreGenmodelParser
import emfcodegenerator.creators.FileCreator
import emfcodegenerator.inspectors.util.AbstractObjectFieldInspector
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
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.impl.EClassImpl
import org.eclipse.emf.ecore.EEnum

class EMFPackageInterfaceCreator extends EMFCodeGenerationClass implements FileCreator{

	/**########################Attributes########################*/

	var PackageInspector e_pak
	var HashMap<EPackage,PackageInspector> packages_to_inspector_map
	var ArrayList<String> package_object_fields = new ArrayList<String>()
	var ArrayList<String> package_method_declarations = new ArrayList<String>()
	var ArrayList<String> subinterface_literals = new ArrayList<String>()

	var String source_name
	var String IDENTION
	var String file_path
	var String interface_declaration
	var boolean is_initialized = false

	/**########################Constructors########################*/

	new(PackageInspector package_inspector, HashMap<EPackage,PackageInspector> e_pak_map, EcoreGenmodelParser gen_model){
		super(gen_model)
		this.e_pak = package_inspector
		this.packages_to_inspector_map = e_pak_map
		var interface_name = e_pak.get_emf_package_class_name()
		this.source_name = interface_name + "Impl"
		this.interface_declaration =
			'''public interface «interface_name» extends EPackage'''.toString

		this.add_import_as_String("org.eclipse.emf.ecore.EAttribute")
		this.add_import_as_String("org.eclipse.emf.ecore.EClass")
		this.add_import_as_String("org.eclipse.emf.ecore.EPackage")
		this.add_import_as_String("org.eclipse.emf.ecore.EDataType")
		
		this.package_object_fields.add('''String eNAME = "«e_pak.get_name»";'''.toString)
		this.package_object_fields.add('''String eNS_URI = "«e_pak.get_ens_uri»";'''.toString)
		this.package_object_fields.add('''String eNS_PREFIX = "«e_pak.get_ens_prefix»";'''.toString)
		this.package_object_fields.add(
			interface_name + " eINSTANCE = " + e_pak.get_package_declaration_name + ".impl." +
			this.source_name + ".init();"
			)
	}

	/**########################Generators########################*/

	/**
	 * Creates the part of the interface where all EReferences, EAttributes, EDataTypes and EClasses
	 * of the Package are assigned an ID. All EClasses and EDataTypes get a for this package unique
	 * Integer starting at 0. <br>
	 * The EStructuralFeatures get an ID relative to the EClass. If the EClass does not have super
	 * types it starts at 0, else it starts with an offset, where the offset represents the count of
	 * how many structural features it possesses. Same goes for EOperation where the Offset is the
	 * count of EOperations belonging to the super class.<br>
	 * Calls this.register_eclass_without_supers_object_fields and
	 * this.register_eclass_with_supers_object_fields
	 * @return ArrayList<String> each entry representing 1 line of code in order
	 */
	def private ArrayList<String> register_eclass_id_object_fields(){
		var ArrayList<String> object_fields = new ArrayList<String>()
		var int class_id = 0
		for(e_class : this.e_pak.get_all_eclasses_in_package()){
			//assign the class-ID for the current class
			object_fields.add("int " + emf_to_uppercase(e_class.name) + " = " + class_id++ + ";")
			//generate the ID's for the EOperations, EAttributes and EReferences
			if(e_class.ESuperTypes.isEmpty){
				object_fields.addAll(
					this.register_eclass_without_supers_object_fields(e_class)
				)
			} else {
				object_fields.addAll(
					this.register_eclass_with_supers_object_fields(e_class)
				)
			}
		}
		//register ID's for EDataTypes
		for(EDataType e_type : this.e_pak.get_all_edata_types_in_package()){
			object_fields.add(
				'''int «emf_to_uppercase(e_type.name)» = «class_id++»;'''.toString
			)
		}
		
		if(!this.e_pak.get_all_eenums_in_package().isEmpty)
			this.add_import_as_String("org.eclipse.emf.ecore.EEnum")
		//register ID's for EEnums
		for(EEnum e_enum : this.e_pak.get_all_eenums_in_package){
			object_fields.add(
				'''int «emf_to_uppercase(e_enum.name)» = «class_id++»;'''.toString
			)
		}
		return object_fields
	}

	/**
	 * Called by this.register_eclass_id_object_fields to register ID's to all EStructuralFeatures
	 * of given EClass which inherits or extends other classes/interfaces.
	 * @param EClass e_class the EClass for which the entries shall be generated
	 * @return ArrayList<String> contains LOC in order.
	 */
	def private ArrayList<String> register_eclass_with_supers_object_fields(EClass e_class){
		var ArrayList<String> object_fields = new ArrayList<String>()
		var ArrayList<EClass> all_super_types = new ArrayList<EClass>(e_class.ESuperTypes)

		var EClassImpl the_super_class = null
		var PackageInspector e_pak_of_super_class = null
		if (all_super_types.isEmpty)
			throw new IllegalArgumentException("expected EClass with Super-Types")

		var String feature_id_offset = ""
		var String operation_id_offset = ""
		the_super_class = all_super_types.get(0) as EClassImpl
		if(EMFPackageInterfaceCreator.emf_model.eclass_is_registered(the_super_class)){
			all_super_types.remove(0)
			e_pak_of_super_class = 
					this.packages_to_inspector_map.get(the_super_class.EPackage)
			//import the emf-package-classes interface 
			this.add_import_as_String(
				e_pak_of_super_class.get_package_declaration_name + "." +
				e_pak_of_super_class.get_emf_package_class_name()
			)
			feature_id_offset = e_pak_of_super_class.get_emf_package_class_name + "." +
								emf_to_uppercase(the_super_class.name) + "_FEATURE_COUNT + "
			operation_id_offset = e_pak_of_super_class.get_emf_package_class_name + "." +
								  emf_to_uppercase(the_super_class.name) + "_OPERATION_COUNT + "
		}
		else the_super_class = null

		//get needed attributes, ereferences and eoperations for which entries shall be generated
		//inherited fields not included
		var all_structural_features = new HashSet<ObjectFieldInspector>()
		all_structural_features.addAll(this.e_pak.get_object_field_inspectors_for_class(e_class))
		var all_e_operations = new HashSet<EOperationInspector>()
		all_e_operations.addAll(this.e_pak.get_eoperation_inspector_for_class(e_class))
		for(ecl : all_super_types){
			var ecl_impl = ecl as EClassImpl
			if(EMFPackageInterfaceCreator.emf_model.eclass_is_registered(ecl_impl)){
				var containing_package = this.packages_to_inspector_map.get(ecl_impl.EPackage)
				all_structural_features.addAll(
					containing_package.get_all_object_field_inspectors_for_class(ecl_impl)
				)
				all_e_operations.addAll(
					containing_package.get_all_eoperation_inspector_for_class(ecl_impl)
				)
			} else {
				for(EAttribute e_attr : ecl_impl.EAllAttributes){
					if(EMFPackageInterfaceCreator.emf_model.get_struct_features_to_inspector_map.containsKey(e_attr)){
						all_structural_features.add(EMFPackageInterfaceCreator.emf_model.get_struct_features_to_inspector_map.get(e_attr))
					} else {
						all_structural_features.add(new AttributeInspector(e_attr, EMFPackageInterfaceCreator.emf_model))
					}
				}
				for(EReference e_ref : ecl_impl.EAllReferences){
					if(EMFPackageInterfaceCreator.emf_model.get_struct_features_to_inspector_map.containsKey(e_ref)){
						all_structural_features.add(EMFPackageInterfaceCreator.emf_model.get_struct_features_to_inspector_map.get(e_ref))
					} else {
						all_structural_features.add(new ReferenceInspector(e_ref, EMFPackageInterfaceCreator.emf_model))
					}
				}
				for(EOperation e_op : ecl_impl.EAllOperations)
					all_e_operations.add(new EOperationInspector(e_op, EMFPackageInterfaceCreator.emf_model))
			}
		}
		
		var int feature_id = 0
		var int operation_id = 0
		var class_name = emf_to_uppercase(e_class.name)

		//structural feature entries
		if(the_super_class !== null){
			var all_inherited_data_fields =
				e_pak_of_super_class.get_all_object_field_inspectors_for_class(the_super_class)
			all_structural_features.removeAll(all_inherited_data_fields)
			for(data_field : all_inherited_data_fields){
				var entry = new StringBuilder("int " + class_name + "__")
				var data_field_name = emf_to_uppercase(data_field.get_name)
				entry.append(data_field_name)
				entry.append(" = ")
				entry.append(e_pak_of_super_class.get_emf_package_class_name)
				entry.append(".")
				entry.append(emf_to_uppercase(the_super_class.name))
				entry.append("__")
				entry.append(data_field_name)
				entry.append(";")
				object_fields.add(entry.toString)
			}
		}

		for(data_field : all_structural_features){
			object_fields.add('''int «class_name»__«emf_to_uppercase(data_field.get_name)» = «feature_id_offset»«feature_id++»;'''.toString())
		}
		object_fields.add(
			'''int «class_name»_FEATURE_COUNT = «feature_id_offset»«feature_id»;'''.toString()
		)
		
		//EOperation entries
		if(the_super_class !== null){
			var all_inherited_operations =
				e_pak_of_super_class.get_all_eoperation_inspector_for_class(the_super_class)
			all_structural_features.removeAll(all_inherited_operations)
			for(data_field : all_inherited_operations){
				var entry = new StringBuilder("int " + class_name + "__")
				var data_field_name = emf_to_uppercase(data_field.get_name)
				entry.append(data_field_name)
				entry.append(" = ")
				entry.append(e_pak_of_super_class.get_emf_package_class_name)
				entry.append(".")
				entry.append(emf_to_uppercase(the_super_class.name))
				entry.append("___")
				entry.append(data_field_name)
				entry.append(";")
				object_fields.add(entry.toString)
			}
		}

		for(e_operation : all_e_operations){
			object_fields.add('''int «class_name»___«emf_to_uppercase(e_operation.get_name)» = «operation_id_offset»«operation_id++»;'''.toString())
		}
		object_fields.add(
			'''int «class_name»_OPERATION_COUNT = «operation_id_offset»«operation_id»;'''.toString()
		)
		
		return object_fields
	}
	
	/**
	 * Called by this.register_eclass_id_object_fields to register ID's to all EStructuralFeatures
	 * of given EClass whithout supertypes.
	 * @param EClass e_class the EClass for which the entries shall be generated
	 * @return ArrayList<String> contains LOC in order.
	 */
	def private ArrayList<String> register_eclass_without_supers_object_fields(EClass e_class){
		var ArrayList<String> object_fields = new ArrayList<String>()
		var feature_count = 0
		var obj_field_id = 0
		var int op_count = 0
		var all_operations = new HashSet<EOperation>(e_class.EAllOperations)

		for(att : this.e_pak.get_object_field_inspectors_for_class(e_class)){
			object_fields.add(
				"int " + emf_to_uppercase(e_class.name) + "__" + emf_to_uppercase(att.get_name) +
				" = " + obj_field_id.toString + ";"
			)
			feature_count += 1
			obj_field_id += 1
		}
		//add feature-count
		object_fields.add(
			"int " + emf_to_uppercase(e_class.name) + "_FEATURE_COUNT = " + feature_count + ";"
		)
				
		for(EOperation e_op : all_operations){
			//var e_op_inspector = new EOperationInspector(e_op, EMFPackageInterfaceCreator.emf_model)
			var declaration = new StringBuilder("int ")
			declaration.append(emf_to_uppercase(e_class.name))
			declaration.append("___")
			declaration.append(emf_to_uppercase(e_op.name))
			declaration.append(" = ")
			declaration.append(op_count.toString)
			declaration.append(";")
			object_fields.add(declaration.toString)
			op_count += 1
		}//iterate over all EOperations
		//add operation-count
		object_fields.add(
			"int " + emf_to_uppercase(e_class.name) +
			"_OPERATION_COUNT = " + op_count + ";"
		)
		//adding EOPeration declarations if a class does not have Supertypes

		return object_fields
	}

	/**
	 * For all entities contained in an EPackage getters need to be generated. This contains
	 * EClasses, EAttributes, EReferences, EOperations and EDataTypes.
	 * @return ArrayList containing LOC in order.
	 */
	def private ArrayList<String> register_method_declaration(){
		var declarations = new ArrayList<String>()
		this.add_import_as_String("org.eclipse.emf.ecore.EReference")
		this.add_import_as_String("org.eclipse.emf.ecore.EAttribute")
		this.add_import_as_String("org.eclipse.emf.ecore.EOperation")
		for(e_class : this.e_pak.get_all_eclasses_in_package()){
			//var e_class_name = e_class.name.substring(0,1).toUpperCase + e_class.name.substring(1)
			var e_class_name = e_class.name
			declarations.add("EClass get" + e_class_name + "();")
			
			for(AbstractObjectFieldInspector inspector : this.e_pak.get_object_field_inspectors_for_class(e_class)){
				declarations.add(inspector.get_getter_method_declaration_for_the_package_classes() + ";")
			}

			for(EOperationInspector inspector : this.e_pak.get_eoperation_inspector_for_class(e_class)){
				var method = inspector.get_getter_method_declaration_for_the_package_classes
				declarations.add(method + ";")
			}
		}

		for(EDataType e_type : this.e_pak.get_all_edata_types_in_package()){
			declarations.add(
				'''EDataType get«e_type.name»();'''.toString
			)
		}

		for(EEnum e_type : this.e_pak.get_all_eenums_in_package()){
			declarations.add(
				'''EEnum get«e_type.name»();'''.toString
			)
		}
		return declarations
	}

	/**
	 * Each Package interface holds a sub interface called Literals in which literals are declared
	 * whose call will return the representing EObject.<br>
	 * sample entry: EAttribute MY_ATTRIBUTE = eIINSTANCE.getclassA_My_attribute();
	 * @return ArrayList<String> contains LOC for each literal
	 */
	def private ArrayList<String>register_literals_interface_entries(){
		var declarations = new ArrayList<String>()

		for(e_class : this.e_pak.get_all_eclasses_in_package()){
			var class_name = emf_to_uppercase(e_class.name)
			//var e_class_name = e_class.name.substring(0,1).toUpperCase + e_class.name.substring(1)
			var e_class_name = e_class.name
			var method_stump = '''eINSTANCE.get«e_class_name»'''.toString
			declarations.add(
				"EClass " + class_name + " = " + method_stump + "();"
			)

			for(AbstractObjectFieldInspector inspector : this.e_pak.get_object_field_inspectors_for_class(e_class)){
				var method = inspector.get_literals_entry_for_package_classes()
				declarations.add(method)
			}

			for(EOperationInspector inspector : this.e_pak.get_eoperation_inspector_for_class(e_class)){
				var method = inspector.get_literals_entry_for_package_classes
				declarations.add(method)
			}
		}

		for(EDataType e_type : this.e_pak.get_all_edata_types_in_package()){
			declarations.add(
				'''EDataType «emf_to_uppercase(e_type.name)» = eINSTANCE.get«e_type.name»();'''.toString
			)
		}
		
		for(EEnum e_type : this.e_pak.get_all_eenums_in_package()){
			declarations.add(
				'''EEnum «emf_to_uppercase(e_type.name)» = eINSTANCE.get«e_type.name»();'''.toString
			)
		}

		return declarations
	}

	/**
	 * generates all content needed to write the Interface to a file
	 */
	def private void generate_content(){
		this.package_object_fields.addAll(register_eclass_id_object_fields())
		this.package_method_declarations.addAll(register_method_declaration())
		this.subinterface_literals.addAll(this.register_literals_interface_entries())
	}

	/**########################Public methods########################*/

	override void initialize_creator(String fq_file_path, String IDENTION){
		this.file_path = fq_file_path
		this.IDENTION = IDENTION
		generate_content()
		this.is_initialized = true
	}
	
	override write_to_file() {
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		var package_file = new File(this.file_path)
		package_file.getParentFile().mkdirs()
		var package_fw = new FileWriter(package_file , false)
		
		var import_block = new StringBuilder()
		var data_fields = new StringBuilder()
		var method_declaration = new StringBuilder()
		var literals_interface = new StringBuilder()
		var package_declaration_string = 
			"package " + this.e_pak.get_package_declaration_name + ";" +
			System.lineSeparator + System.lineSeparator

		var interface_declaration_string = this.interface_declaration + " {" + System.lineSeparator
		
		for(import_string : this.get_needed_imports()){
			import_block.append("import ")
			import_block.append(import_string)
			import_block.append(";")
			import_block.append(System.lineSeparator)
		} import_block.append(System.lineSeparator)
		
		for(obj_field : this.package_object_fields){
			data_fields.append(this.IDENTION)
			data_fields.append(obj_field)
			data_fields.append(System.lineSeparator)
		}
		
		for(method : this.package_method_declarations){
			method_declaration.append(this.IDENTION)
			method_declaration.append(method)
			method_declaration.append(System.lineSeparator)
		}
		
		literals_interface.append(IDENTION)
		literals_interface.append("interface Literals {")
		literals_interface.append(System.lineSeparator)
		for(entry : this.subinterface_literals){
			literals_interface.append(IDENTION)
			literals_interface.append(IDENTION)
			literals_interface.append(entry)
			literals_interface.append(System.lineSeparator)
		}
		literals_interface.append(IDENTION)
		literals_interface.append("}")
		literals_interface.append(System.lineSeparator)

		package_fw.write(package_declaration_string)
		package_fw.write(import_block.toString)
		package_fw.write(interface_declaration_string)
		package_fw.write(data_fields.toString)
		package_fw.write(method_declaration.toString)
		package_fw.write(literals_interface.toString)
		package_fw.write("}" + System.lineSeparator + System.lineSeparator)

		package_fw.close()
	}
	
	override boolean equals(Object other){
		if(!(other instanceof EMFPackageInterfaceCreator)) return false
		return this.e_pak.equals((other as EMFPackageInterfaceCreator).e_pak)
	}
	
	override int hashCode(){
		return this.e_pak.hashCode()
	}
}
