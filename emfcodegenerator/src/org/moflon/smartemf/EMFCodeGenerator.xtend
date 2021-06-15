package org.moflon.smartemf

import java.io.File
import java.util.HashMap
import java.util.HashSet
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.moflon.smartemf.creators.templates.SmartEMFObjectTemplate
import org.moflon.smartemf.inspectors.util.PackageInspector

import static org.moflon.smartemf.EMFCodeGenerationClass.*
import org.moflon.smartemf.creators.templates.SmartEMFInterfaceTemplate
import org.moflon.smartemf.creators.templates.PackageInterfaceTemplate
import org.moflon.smartemf.creators.templates.PackageImplTemplate
import org.moflon.smartemf.creators.templates.EEnumTemplate
import org.moflon.smartemf.creators.templates.FactoryInterfaceTemplate
import org.moflon.smartemf.creators.templates.FactoryImplTemplate
import java.util.Collections
import java.util.Set

/**
 * Class which generates the code
 */
class EMFCodeGenerator{

	/**########################Attributes########################*/

	/**
	 * String used to indent code
	 */
	var String INDENTATION = "    "

	/**
	 * HashMap mapping InterfaceCreator to PackageInspector
	 */	
	var Set<SmartEMFInterfaceTemplate> interfaces = Collections.synchronizedSet(new HashSet)
	
	/**
	 * HashMap mapping InterfaceCreator to PackageInspector
	 */
	var Set<SmartEMFObjectTemplate> implementations = Collections.synchronizedSet(new HashSet)

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
				var i_creator = new SmartEMFInterfaceTemplate(e_cl)

				interfaces.add(i_creator)

				var c_creator = new SmartEMFObjectTemplate(e_cl)
//				var c_creator = new SourceCodeCreator(
//									e_cl,
//									EMFCodeGenerationClass.emf_model,
//									e_pak_inspector.get_all_object_field_inspectors_for_class(e_cl),
//									e_pak_inspector.get_all_eoperation_inspector_for_class(e_cl),
//									e_pak_inspector
//									)
				implementations.add(c_creator)
			}
			
//			e_pak_inspector.get_all_eclasses_in_package.parallelStream.forEach([e_cl | {
//				val i_creator = new SmartEMFInterfaceTemplate(e_cl)
//				val c_creator = new SmartEMFObjectTemplate(e_cl)
//				interfaces.add(i_creator)
//				implementations.add(c_creator)
//			}])
		}
	}

	/**########################Generation########################*/

	/**
	 * generates all interface files for the EClasses specified in the EMF-model
	 */
	def void generate_interfaces(){
		for(SmartEMFInterfaceTemplate new_interface : interfaces){
			//initialise a file-writer
			var path = packages.get(new_interface.package).get_path_to_folder
			new_interface.writeToFile(path)
		}
//		interfaces.parallelStream.forEach( interface | {
//			interface.writeToFile(packages.get(interface.package).get_path_to_folder)
//		})
	}

	/**
	 * generates all implementation files for the EClasses specified in the EMF-model
	 */
	def void generate_implementation(){
		for(SmartEMFObjectTemplate new_source : implementations){
			//initialise a file-writer
			var path = packages.get(new_source.package).get_path_to_folder 
			new_source.writeToFile(path)
		}
//		implementations.parallelStream.forEach( implementation | {
//			implementation.writeToFile(packages.get(implementation.package).get_path_to_folder)
//		})
	}
	
	def void generate_package_interfaces(){
		for(package_inspector : packages.values){
			var creator = new PackageInterfaceTemplate(
				package_inspector, packages, EMFCodeGenerationClass.emf_model
			)
			var path = package_inspector.get_path_to_folder + "/" +
					   package_inspector.get_emf_package_class_name + ".java"

			creator.initialize_creator(path, this.INDENTATION)
			creator.write_to_file()
			
			for(eenum : package_inspector.get_all_eenums_in_package){
				var eenum_creator = new EEnumTemplate(eenum, package_inspector)
				eenum_creator.initialize_creator(
					'''«package_inspector.get_path_to_folder»/«eenum.name».java'''.toString,
					"    "
				)
				eenum_creator.write_to_file()
			}
		}

//		packages.values.parallelStream.forEach( package_inspector | {
//			val creator = new PackageInterfaceTemplate(
//				package_inspector, packages, EMFCodeGenerationClass.emf_model
//			)
//			val path = package_inspector.get_path_to_folder + "/" +
//					   package_inspector.get_emf_package_class_name + ".java"
//
//			creator.initialize_creator(path, this.INDENTATION)
//			creator.write_to_file()
//			
//			for(eenum : package_inspector.get_all_eenums_in_package){
//				val eenum_creator = new EEnumTemplate(eenum, package_inspector)
//				eenum_creator.initialize_creator(
//					'''«package_inspector.get_path_to_folder»/«eenum.name».java'''.toString,
//					"    "
//				)
//				eenum_creator.write_to_file()
//			}
//		})
	}
	
	def void generate_package_implementations(){
		for(package_inspector : packages.values){
			var creator = new PackageImplTemplate(
				package_inspector, packages, EMFCodeGenerationClass.emf_model
			)
			var path = package_inspector.get_path_to_folder + "/impl/" +
					   package_inspector.get_emf_package_class_name + "Impl.java"

			creator.initialize_creator(path, this.INDENTATION)
			creator.write_to_file()
		}
		
//		packages.values.parallelStream.forEach( package_inspector | {
//			val creator = new PackageImplTemplate(
//				package_inspector, packages, EMFCodeGenerationClass.emf_model
//			)
//			val path = package_inspector.get_path_to_folder + "/impl/" +
//					   package_inspector.get_emf_package_class_name + "Impl.java"
//
//			creator.initialize_creator(path, this.INDENTATION)
//			creator.write_to_file()
//		})
	}
	
	def void generate_package_factory_interfaces(){
		for(package_inspector : packages.values){
			var creator = new FactoryInterfaceTemplate(
				EMFCodeGenerationClass.emf_model, package_inspector
			)
			var path = package_inspector.get_path_to_folder + "/" +
					   package_inspector.get_emf_package_factory_class_name + ".java"

			creator.initialize_creator(path, this.INDENTATION)
			creator.write_to_file()
		}
		
//		packages.values.parallelStream.forEach( package_inspector | {
//			val creator = new FactoryInterfaceTemplate(
//				EMFCodeGenerationClass.emf_model, package_inspector
//			)
//			val path = package_inspector.get_path_to_folder + "/" +
//					   package_inspector.get_emf_package_factory_class_name + ".java"
//
//			creator.initialize_creator(path, this.INDENTATION)
//			creator.write_to_file()
//		})
	}
	
	def void generate_package_factory_implementations(){
		for(package_inspector : packages.values){
			var creator =new FactoryImplTemplate(
				EMFCodeGenerationClass.emf_model, package_inspector
			)
			var path = package_inspector.get_path_to_folder + "/impl/" +
					   package_inspector.get_emf_package_factory_class_name + "Impl.java"

			creator.initialize_creator(path, this.INDENTATION)
			creator.write_to_file()
		}
		
//		packages.values.parallelStream.forEach( package_inspector | {
//			val creator =new FactoryImplTemplate(
//				EMFCodeGenerationClass.emf_model, package_inspector
//			)
//			val path = package_inspector.get_path_to_folder + "/impl/" +
//					   package_inspector.get_emf_package_factory_class_name + "Impl.java"
//
//			creator.initialize_creator(path, this.INDENTATION)
//			creator.write_to_file()
//		})
	}

	/**
	 * Generates all model-code for the given EMF-model
	 * @author Adrian Zwenger
	 */
	def void generate_all_model_code(){
//		val generators = new LinkedList
//		generators.add([generate_interfaces])
//		generators.add([generate_implementation])
//		generators.add([generate_package_factory_interfaces])
//		generators.add([generate_package_factory_implementations])
//		generators.add([generate_package_interfaces()])
//		generators.add([generate_package_implementations])
//		generators.parallelStream.forEach([])
		
		generate_interfaces()
		generate_implementation()
		generate_package_factory_interfaces()
		generate_package_factory_implementations()
		generate_package_interfaces()
		generate_package_implementations()
	}
}
