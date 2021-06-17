package org.moflon.smartemf;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.moflon.smartemf.creators.templates.SmartEMFObjectTemplate;

import org.moflon.smartemf.creators.templates.SmartEMFInterfaceTemplate;
import org.moflon.smartemf.creators.templates.PackageInterfaceTemplate;
import org.moflon.smartemf.creators.templates.PackageImplTemplate;
import org.moflon.smartemf.creators.templates.EEnumTemplate;
import org.moflon.smartemf.creators.templates.FactoryInterfaceTemplate;
import org.moflon.smartemf.creators.templates.FactoryImplTemplate;
import java.util.Collections;
import java.util.Set;
import org.moflon.smartemf.creators.templates.util.PackageInformation;
import org.moflon.smartemf.creators.templates.util.TemplateUtil;

/**
 * Class which generates the code
 */
public class SmartEMFGenerator{

	/**########################Attributes########################*/
	
	protected String generatedFileDir;

	/**
	 * HashMap mapping InterfaceCreator to PackageInspector
	 */	
	protected Set<SmartEMFInterfaceTemplate> interfaces = Collections.synchronizedSet(new HashSet<>());
	
	/**
	 * HashMap mapping InterfaceCreator to PackageInspector
	 */
	protected Set<SmartEMFObjectTemplate> implementations = Collections.synchronizedSet(new HashSet<>());

	protected EcoreGenmodelParser genmodel;
	/**
	 * HashMap storing EPackages and their PackagInspectors
	 */
	protected HashMap<EPackage, PackageInformation> packages = new HashMap<>();

	/**########################Constructor########################*/

	/**
	 * constructs a new EMFGenerator object
	 * @param ecore_xmi_path String to file
	 * @param genmodel_xmi_path String to file
	 */ 
	public SmartEMFGenerator(EPackage ePackage, GenModel genmodel, String ecore_xmi_path){
		generatedFileDir = ((new File(ecore_xmi_path)).getParentFile().getParent());
		this.genmodel = new EcoreGenmodelParser(ePackage, genmodel, generatedFileDir);
		packages = this.genmodel.get_packages_to_package_inspector_map();
		
		for(EPackage e_pak : packages.keySet()){
			PackageInformation e_pak_inspector = packages.get(e_pak);
			if(!e_pak_inspector.is_initialized()) {
				e_pak_inspector.initialize();
				packages = this.genmodel.update_package_inspector(e_pak, e_pak_inspector);
			}

			//create the FileCreators for the EClasses
			for(EClass e_cl : e_pak_inspector.get_all_eclasses_in_package()){
				var i_creator = new SmartEMFInterfaceTemplate(e_cl);

				interfaces.add(i_creator);

				var c_creator = new SmartEMFObjectTemplate(e_cl);
//				var c_creator = new SourceCodeCreator(
//									e_cl,
//									EMFCodeGenerationClass.emf_model,
//									e_pak_inspector.get_all_object_field_inspectors_for_class(e_cl),
//									e_pak_inspector.get_all_eoperation_inspector_for_class(e_cl),
//									e_pak_inspector
//									)
				implementations.add(c_creator);
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
	protected void generate_interfaces(){
		for(SmartEMFInterfaceTemplate new_interface : interfaces){
			//initialise a file-writer
			var path = packages.get(new_interface.getPackage()).get_path_to_folder() + "/" +
					   new_interface.eClass.getName() + ".java";
			new_interface.initialize_creator(path);
			new_interface.write_to_file();
		}
//		interfaces.parallelStream.forEach( interface | {
//			interface.writeToFile(packages.get(interface.package).get_path_to_folder)
//		})
	}

	/**
	 * generates all implementation files for the EClasses specified in the EMF-model
	 */
	protected void generate_implementation(){
		for(SmartEMFObjectTemplate new_source : implementations){
			//initialise a file-writer
			var path = packages.get(new_source.getPackage()).get_path_to_folder() + "/impl/" +
					   new_source.eClass.getName() + "Impl.java";
			new_source.initialize_creator(path);
			new_source.write_to_file();
		}
//		implementations.parallelStream.forEach( implementation | {
//			implementation.writeToFile(packages.get(implementation.package).get_path_to_folder)
//		})
	}
	
	protected void generate_package_interfaces(){
		for(PackageInformation package_inspector : packages.values()){
			PackageInterfaceTemplate creator = new PackageInterfaceTemplate(package_inspector, packages, genmodel, generatedFileDir);
			String path = package_inspector.get_path_to_folder() + "/" +
					   package_inspector.get_emf_package_class_name() + ".java";

			creator.initialize_creator(path);
			creator.write_to_file();
			
			for(org.eclipse.emf.ecore.EEnum eenum : package_inspector.get_all_eenums_in_package()){
				EEnumTemplate eenum_creator = new EEnumTemplate(eenum, package_inspector, generatedFileDir);
				eenum_creator.initialize_creator(package_inspector.get_path_to_folder() + "/" + eenum.getName() + ".java");
				eenum_creator.write_to_file();
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
	
	protected void generate_package_implementations(){
		for(PackageInformation package_inspector : packages.values()){
			PackageImplTemplate creator = new PackageImplTemplate(package_inspector, packages, genmodel, generatedFileDir);
			
			String path = package_inspector.get_path_to_folder() + "/impl/" +
					   package_inspector.get_emf_package_class_name() + "Impl.java";

			creator.initialize_creator(path);
			creator.write_to_file();
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
	
	protected void generate_package_factory_interfaces(){
		for(PackageInformation package_inspector : packages.values()){
			FactoryInterfaceTemplate creator = new FactoryInterfaceTemplate(genmodel, package_inspector, generatedFileDir);
			String path = package_inspector.get_path_to_folder() + "/" +
					   package_inspector.get_emf_package_factory_class_name() + ".java";

			creator.initialize_creator(path);
			creator.write_to_file();
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
	
	protected void generate_package_factory_implementations(){
		for(PackageInformation package_inspector : packages.values()){
			FactoryImplTemplate creator = new FactoryImplTemplate(genmodel, package_inspector, generatedFileDir);
			String path = package_inspector.get_path_to_folder() + "/impl/" +
					   package_inspector.get_emf_package_factory_class_name() + "Impl.java";

			creator.initialize_creator(path);
			creator.write_to_file();
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
	public void generate_all_model_code(){
		TemplateUtil.uriStringToGenModelMap.clear();
		
//		val generators = new LinkedList
//		generators.add([generate_interfaces])
//		generators.add([generate_implementation])
//		generators.add([generate_package_factory_interfaces])
//		generators.add([generate_package_factory_implementations])
//		generators.add([generate_package_interfaces()])
//		generators.add([generate_package_implementations])
//		generators.parallelStream.forEach([])
		
		generate_interfaces();
		generate_implementation();
		generate_package_factory_interfaces();
		generate_package_factory_implementations();
		generate_package_interfaces();
		generate_package_implementations();
	}
}
