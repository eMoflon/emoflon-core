package org.emoflon.smartemf;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.emoflon.smartemf.creators.FileCreator;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.emoflon.smartemf.creators.templates.SmartEMFObjectTemplate;

import org.emoflon.smartemf.creators.templates.SmartEMFInterfaceTemplate;
import org.emoflon.smartemf.creators.templates.PackageInterfaceTemplate;
import org.emoflon.smartemf.creators.templates.PackageImplTemplate;
import org.emoflon.smartemf.EcoreGenmodelParser;
import org.emoflon.smartemf.creators.templates.EEnumTemplate;
import org.emoflon.smartemf.creators.templates.FactoryInterfaceTemplate;
import org.emoflon.smartemf.creators.templates.FactoryImplTemplate;
import java.util.Collections;
import java.util.Set;
import org.emoflon.smartemf.creators.templates.util.PackageInformation;
import org.emoflon.smartemf.creators.templates.util.TemplateUtil;

/**
 * Class which generates the code
 */
public class SmartEMFGenerator{

	/**########################Attributes########################*/
	
	protected String generatedFileDir;
	
	protected Set<FileCreator> templates = Collections.synchronizedSet(new HashSet<>());

	protected EcoreGenmodelParser genmodel;
	/**
	 * HashMap storing EPackages and their PackagInspectors
	 */
	protected HashMap<EPackage, PackageInformation> packages = new HashMap<>();

	/**########################Constructor########################*/

	/**
	 * constructs a new EMFGenerator object
	 * @param ePackage to build from
	 * @param genmodel to build with
	 * @param path to the original Ecore of the corresponding project
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

			initPackageInterface(e_pak_inspector);
			initPackageImplementation(e_pak_inspector);
			initFactoryInterface(e_pak_inspector);
			initFactoryImplementation(e_pak_inspector);
			initSmartEMFInterfaces(e_pak_inspector);
			initSmartEMFImplementations(e_pak_inspector);
		}
	}
	
	protected void initPackageInterface(PackageInformation e_pak_inspector) {
		PackageInterfaceTemplate creator = new PackageInterfaceTemplate(e_pak_inspector, packages, genmodel, generatedFileDir);
		String path = e_pak_inspector.get_path_to_folder() + "/" +
				e_pak_inspector.get_emf_package_class_name() + ".java";
		creator.initialize_creator(path);
		templates.add(creator);
		
		for(org.eclipse.emf.ecore.EEnum eenum : e_pak_inspector.get_all_eenums_in_package()){
			EEnumTemplate eenum_creator = new EEnumTemplate(eenum, e_pak_inspector, generatedFileDir);
			eenum_creator.initialize_creator(e_pak_inspector.get_path_to_folder() + "/" + eenum.getName() + ".java");
			templates.add(eenum_creator);
		}
	}
	
	protected void initPackageImplementation(PackageInformation e_pak_inspector) {
		PackageImplTemplate creator = new PackageImplTemplate(e_pak_inspector, packages, genmodel, generatedFileDir);
		String path = e_pak_inspector.get_path_to_folder() + "/impl/" +
				e_pak_inspector.get_emf_package_class_name() + "Impl.java";
		creator.initialize_creator(path);
		templates.add(creator);
	}
	
	protected void initFactoryInterface(PackageInformation e_pak_inspector) {
		FactoryInterfaceTemplate creator = new FactoryInterfaceTemplate(genmodel, e_pak_inspector, generatedFileDir);
		String path = e_pak_inspector.get_path_to_folder() + "/" +
				e_pak_inspector.get_emf_package_factory_class_name() + ".java";

		creator.initialize_creator(path);
		templates.add(creator);
	}
	
	protected void initFactoryImplementation(PackageInformation e_pak_inspector) {
		FactoryImplTemplate creator = new FactoryImplTemplate(genmodel, e_pak_inspector, generatedFileDir);
		String path = e_pak_inspector.get_path_to_folder() + "/impl/" +
				e_pak_inspector.get_emf_package_factory_class_name() + "Impl.java";

		creator.initialize_creator(path);
		templates.add(creator);
	}
	
	protected void initSmartEMFInterfaces(PackageInformation e_pak_inspector) {
		for(EClass eClazz : e_pak_inspector.get_all_eclasses_in_package()){
			SmartEMFInterfaceTemplate interfaceTemplate = new SmartEMFInterfaceTemplate(eClazz);
			String path = packages.get(interfaceTemplate.getPackage()).get_path_to_folder() + "/" +
					interfaceTemplate.eClass.getName() + ".java";
			interfaceTemplate.initialize_creator(path);
			templates.add(interfaceTemplate);
		}
		
	}
	
	protected void initSmartEMFImplementations(PackageInformation e_pak_inspector) {
		for(EClass eClazz : e_pak_inspector.get_all_eclasses_in_package()){
			SmartEMFObjectTemplate implTemplate = new SmartEMFObjectTemplate(eClazz);
			String path = packages.get(implTemplate.getPackage()).get_path_to_folder() + "/impl/" +
					implTemplate.eClass.getName() + "Impl.java";
			implTemplate.initialize_creator(path);
			templates.add(implTemplate);
		}
		
	}

	public void generate_all_model_code(){
		TemplateUtil.uriStringToGenModelMap.clear();
		templates.parallelStream().forEach(template -> template.write_to_file());
	}
}
