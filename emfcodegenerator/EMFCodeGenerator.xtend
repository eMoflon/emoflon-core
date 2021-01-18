package emfcodegenerator

import emfcodegenerator.creators.util.EMFPackageInterfaceCreator
import emfcodegenerator.creators.util.EMFPackageSourceCreator
import emfcodegenerator.creators.util.InterfaceCreator
import emfcodegenerator.creators.util.SourceCodeCreator
import emfcodegenerator.inspectors.util.PackageInspector
import java.util.HashMap
import java.util.HashSet
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.impl.EClassImpl
import emfcodegenerator.creators.util.EMFPackageFactoryInterfaceCreator
import emfcodegenerator.creators.util.EMFPackageFactorySourceCreator
import emfcodegenerator.creators.util.EEnumCreator

class EMFCodeGenerator extends EMFCodeGenerationClass{

	/**########################Attributes########################*/

	/**
	 * String used to indent code
	 */
	var String IDENTION = "    "

	/**
	 * HashMap mapping InterfaceCreator to PackageInspector
	 */	
	var HashSet<InterfaceCreator> interfaces = new HashSet<InterfaceCreator>()
	
	/**
	 * HashMap mapping InterfaceCreator to PackageInspector
	 */
	var HashSet<SourceCodeCreator> implementations = new HashSet<SourceCodeCreator>()

	/**
	 * HashMap storing EPackages and their PackagInspectors
	 */
	var HashMap<EPackage, PackageInspector> packages = new HashMap<EPackage, PackageInspector>()

	/**########################Constructor########################*/

	/**
	 * constructs a new EMFGenerator object
	 * @param ecore_xmi_path String to file
	 * @param genmodel_xmi_path String to file
	 */ 
	new(String ecore_xmi_path, String genmodel_xmi_path){
		super()
		EMFCodeGenerator.emf_model = new EcoreGenmodelParser(ecore_xmi_path, genmodel_xmi_path)
		this.packages = EMFCodeGenerator.emf_model.get_packages_to_package_inspector_map()
		for(EPackage e_pak : this.packages.keySet){
			var e_pak_inspector = this.packages.get(e_pak)
			if(!e_pak_inspector.is_initialized) {
				e_pak_inspector.initialize()
				this.packages = EMFCodeGenerator.emf_model.update_package_inspector(e_pak, e_pak_inspector)
			}

			//create the FileCreators for the EClasses
			for(EClassImpl e_cl : e_pak_inspector.get_all_eclasses_in_package){
				var i_creator = new InterfaceCreator(e_cl, emf_model,
										e_pak_inspector.get_object_field_inspectors_for_class(e_cl),
										e_pak_inspector.get_eoperation_inspector_for_class(e_cl),
										e_pak_inspector)
				interfaces.add(i_creator)
				var c_creator = new SourceCodeCreator(e_cl, emf_model,
										e_pak_inspector.get_all_object_field_inspectors_for_class(e_cl),
										e_pak_inspector.get_all_eoperation_inspector_for_class(e_cl),
										e_pak_inspector)
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
			var path = packages.get(new_interface.get_package) .get_path_to_folder + "/" +
					   new_interface.get_name + ".java"
			new_interface.initialize_creator(path, IDENTION)
			new_interface.write_to_file()
		}
	}

	/**
	 * generates all implementation files for the EClasses specified in the EMF-model
	 */
	def void generate_implementation(){
		for(SourceCodeCreator new_source : implementations){
			//initialise a file-writer
			var path = packages.get(new_source.get_package).get_path_to_folder + "/impl/" +
					   new_source.get_name + ".java"
			new_source.initialize_creator(path, IDENTION)
			new_source.write_to_file()
		}
	}
	
	def void generate_package_interfaces(){
		for(package_inspector : packages.values){
			var creator = new EMFPackageInterfaceCreator(package_inspector, packages, EMFCodeGenerator.emf_model)
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
			var creator = new EMFPackageSourceCreator(package_inspector, packages, EMFCodeGenerator.emf_model)
			var path = package_inspector.get_path_to_folder + "/impl/" +
					   package_inspector.get_emf_package_class_name + "Impl.java"

			creator.initialize_creator(path, this.IDENTION)
			creator.write_to_file()
		}
	}
	
	def void generate_package_factory_interfaces(){
		for(package_inspector : packages.values){
			var creator = new EMFPackageFactoryInterfaceCreator(EMFCodeGenerator.emf_model, package_inspector)
			var path = package_inspector.get_path_to_folder + "/" +
					   package_inspector.get_emf_package_factory_class_name + ".java"

			creator.initialize_creator(path, this.IDENTION)
			creator.write_to_file()
		}
	}
	
	def void generate_package_factory_implementations(){
		for(package_inspector : packages.values){
			var creator = new EMFPackageFactorySourceCreator(EMFCodeGenerator.emf_model, package_inspector)
			var path = package_inspector.get_path_to_folder + "/impl/" +
					   package_inspector.get_emf_package_factory_class_name + "Impl.java"

			creator.initialize_creator(path, this.IDENTION)
			creator.write_to_file()
		}
	}
}