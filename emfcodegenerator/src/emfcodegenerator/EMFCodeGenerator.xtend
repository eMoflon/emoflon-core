package emfcodegenerator

import emfcodegenerator.creators.util.EMFPackageInterfaceCreator
import emfcodegenerator.creators.util.EMFPackageSourceCreator
import emfcodegenerator.creators.util.InterfaceCreator
import emfcodegenerator.creators.util.SourceCodeCreator
import emfcodegenerator.inspectors.util.PackageInspector
import java.util.HashMap
import java.util.HashSet
import org.eclipse.emf.ecore.EPackage
import emfcodegenerator.creators.util.EMFPackageFactoryInterfaceCreator
import emfcodegenerator.creators.util.EMFPackageFactorySourceCreator
import emfcodegenerator.creators.util.EEnumCreator
import org.eclipse.emf.ecore.EClass
import java.io.File
import emfcodegenerator.creators.util.SmartEMFObjectCreator

/**
 * Class which generates the code
 */
class EMFCodeGenerator{

	/**########################Attributes########################*/

	/**
	 * String used to indent code
	 */
	var String IDENTION = "    "

	/**
	 * HashMap mapping InterfaceCreator to PackageInspector
	 */	
	var HashSet<InterfaceCreator> interfaces = new HashSet()
	
	/**
	 * HashMap mapping InterfaceCreator to PackageInspector
	 */
	var HashSet<SmartEMFObjectCreator> implementations = new HashSet()

	/**
	 * HashMap storing EPackages and their PackagInspectors
	 */
	var HashMap<EPackage, PackageInspector> packages = new HashMap()

	/**########################Constructor########################*/

	/**
	 * constructs a new EMFGenerator object
	 * @param ecore_xmi_path String to file
	 * @param genmodel_xmi_path String to file
	 */ 
	new(String ecore_xmi_path, String genmodel_xmi_path){
		super()
		EMFCodeGenerationClass.set_output_dir((new File(ecore_xmi_path)).parentFile.parent)
		EMFCodeGenerationClass.emf_model =
			new EcoreGenmodelParser(ecore_xmi_path, genmodel_xmi_path)
		this.packages = EMFCodeGenerationClass.emf_model.get_packages_to_package_inspector_map()
		for(EPackage e_pak : this.packages.keySet){
			var e_pak_inspector = this.packages.get(e_pak)
			if(!e_pak_inspector.is_initialized) {
				e_pak_inspector.initialize()
				this.packages =
					EMFCodeGenerationClass.emf_model.update_package_inspector(
						e_pak, e_pak_inspector
					)
			}

			//create the FileCreators for the EClasses
			for(EClass e_cl : e_pak_inspector.get_all_eclasses_in_package){
				var i_creator = new InterfaceCreator(
									e_cl,
									EMFCodeGenerationClass.emf_model,
									e_pak_inspector.get_object_field_inspectors_for_class(e_cl),
									e_pak_inspector.get_eoperation_inspector_for_class(e_cl),
									e_pak_inspector
									)

				interfaces.add(i_creator)

				var c_creator = new SmartEMFObjectCreator(e_cl)
//				var c_creator = new SourceCodeCreator(
//									e_cl,
//									EMFCodeGenerationClass.emf_model,
//									e_pak_inspector.get_all_object_field_inspectors_for_class(e_cl),
//									e_pak_inspector.get_all_eoperation_inspector_for_class(e_cl),
//									e_pak_inspector
//									)
				implementations.add(c_creator)
			}
		}
	}

	/**########################Generation########################*/

	/**
	 * generates all interface files for the EClasses specified in the EMF-model
	 */
	def void generate_interfaces(){
		for(InterfaceCreator new_interface : interfaces){
			//initialise a file-writer
			var path = packages.get(new_interface.get_package).get_path_to_folder + "/" +
					   new_interface.get_name + ".java"
			new_interface.initialize_creator(path, IDENTION)
			new_interface.write_to_file()
		}
	}

	/**
	 * generates all implementation files for the EClasses specified in the EMF-model
	 */
	def void generate_implementation(){
		for(SmartEMFObjectCreator new_source : implementations){
			//initialise a file-writer
			var path = packages.get(new_source.package).get_path_to_folder 
			new_source.writeToFile(path)
		}
	}
	
	def void generate_package_interfaces(){
		for(package_inspector : packages.values){
			var creator = new EMFPackageInterfaceCreator(
				package_inspector, packages, EMFCodeGenerationClass.emf_model
			)
			var path = package_inspector.get_path_to_folder + "/" +
					   package_inspector.get_emf_package_class_name + ".java"

			creator.initialize_creator(path, this.IDENTION)
			creator.write_to_file()
			
			for(eenum : package_inspector.get_all_eenums_in_package){
				var eenum_creator = new EEnumCreator(eenum, package_inspector)
				eenum_creator.initialize_creator(
					'''«package_inspector.get_path_to_folder»/«eenum.name».java'''.toString,
					"    "
				)
				eenum_creator.write_to_file()
			}
		}
	}
	
	def void generate_package_implementations(){
		for(package_inspector : packages.values){
			var creator = new EMFPackageSourceCreator(
				package_inspector, packages, EMFCodeGenerationClass.emf_model
			)
			var path = package_inspector.get_path_to_folder + "/impl/" +
					   package_inspector.get_emf_package_class_name + "Impl.java"

			creator.initialize_creator(path, this.IDENTION)
			creator.write_to_file()
		}
	}
	
	def void generate_package_factory_interfaces(){
		for(package_inspector : packages.values){
			var creator = new EMFPackageFactoryInterfaceCreator(
				EMFCodeGenerationClass.emf_model, package_inspector
			)
			var path = package_inspector.get_path_to_folder + "/" +
					   package_inspector.get_emf_package_factory_class_name + ".java"

			creator.initialize_creator(path, this.IDENTION)
			creator.write_to_file()
		}
	}
	
	def void generate_package_factory_implementations(){
		for(package_inspector : packages.values){
			var creator =new EMFPackageFactorySourceCreator(
				EMFCodeGenerationClass.emf_model, package_inspector
			)
			var path = package_inspector.get_path_to_folder + "/impl/" +
					   package_inspector.get_emf_package_factory_class_name + "Impl.java"

			creator.initialize_creator(path, this.IDENTION)
			creator.write_to_file()
		}
	}

	/**
	 * Generates all model-code for the given EMF-model
	 * @author Adrian Zwenger
	 */
	def void generate_all_model_code(){
		generate_interfaces()
		generate_implementation()
		generate_package_factory_interfaces()
		generate_package_factory_implementations()
		generate_package_interfaces()
		generate_package_implementations()
	}
}
