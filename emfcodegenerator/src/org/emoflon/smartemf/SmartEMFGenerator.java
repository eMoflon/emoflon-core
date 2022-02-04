package org.emoflon.smartemf;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.emoflon.smartemf.templates.CodeTemplate;
import org.emoflon.smartemf.templates.EEnumTemplate;
import org.emoflon.smartemf.templates.FactoryImplTemplate;
import org.emoflon.smartemf.templates.FactoryInterfaceTemplate;
import org.emoflon.smartemf.templates.PackageImplTemplate;
import org.emoflon.smartemf.templates.PackageInterfaceTemplate;
import org.emoflon.smartemf.templates.SmartEMFInterfaceTemplate;
import org.emoflon.smartemf.templates.SmartEMFObjectTemplate;
import org.emoflon.smartemf.templates.util.TemplateUtil;

/**
 * Class which generates the code
 */
public class SmartEMFGenerator{

	/**########################Attributes########################*/
	
	private String path;
	private Collection<CodeTemplate> templates;
	private GenModel genModel;

	/**########################Constructor########################*/

	/**
	 * constructs a new EMFGenerator object
	 * @param ePackage to build from
	 * @param genmodel to build with
	 * @param path to the original Ecore of the corresponding project
	 */ 
	public SmartEMFGenerator(IProject project, EPackage ePackage, GenModel genmodel) {
		String projectPath = project.getLocation().makeAbsolute().toString();
		
		// if modeldirectory contains project name -> don't duplicate it
		if(genmodel.getModelDirectory().contains(project.getName())) {
			File f = new File(projectPath);
			path = f.getParentFile().getAbsolutePath() + "/" + genmodel.getModelDirectory() + "/";
		}
		else {
			path = projectPath + "/" + genmodel.getModelDirectory()+ "/";			
		}

		templates = new LinkedList<>();
		this.genModel = genmodel;
		for(GenPackage genPkg : getGenPackages(genmodel)){
			initPackageInterface(genPkg);
			initPackageImplementation(genPkg);
			initFactoryInterface(genPkg);
			initFactoryImplementation(genPkg);
			initSmartEMFInterfaces(genPkg);
			initSmartEMFImplementations(genPkg);
		}
	}
	
	private Collection<GenPackage> getGenPackages(GenModel genmodel) {
		Collection<GenPackage> visited = new HashSet<>();
		Collection<GenPackage> unvisited = new HashSet<>();
		unvisited.addAll(genmodel.getGenPackages());
		while(!unvisited.isEmpty()) {
			GenPackage next = unvisited.iterator().next();
			visited.add(next);
			unvisited.remove(next);
			
			unvisited.addAll(next.getSubGenPackages());
		}
		return visited;
	}
	
	private void initPackageInterface(GenPackage genPkg) {
		PackageInterfaceTemplate creator = new PackageInterfaceTemplate(genPkg, path);
		templates.add(creator);
		
		for(org.eclipse.emf.ecore.EEnum eenum : TemplateUtil.getEEnums(genPkg)){
			EEnumTemplate eenum_creator = new EEnumTemplate(genPkg, eenum, path);
			templates.add(eenum_creator);
		}
	}
	
	private void initPackageImplementation(GenPackage genPkg) {
		PackageImplTemplate creator = new PackageImplTemplate(genPkg, path);
		templates.add(creator);
	}
	
	private void initFactoryInterface(GenPackage genPkg) {
		FactoryInterfaceTemplate creator = new FactoryInterfaceTemplate(genPkg, path);
		templates.add(creator);
	}
	
	private void initFactoryImplementation(GenPackage genPkg) {
		FactoryImplTemplate creator = new FactoryImplTemplate(genPkg, path);
		templates.add(creator);
	}
	
	private void initSmartEMFInterfaces(GenPackage genPkg) {
		for(EClassifier classifier : genPkg.getEcorePackage().getEClassifiers()){
			if(classifier instanceof EClass eClass) {
				SmartEMFInterfaceTemplate interfaceTemplate = new SmartEMFInterfaceTemplate(genPkg, eClass, path);
				templates.add(interfaceTemplate);				
			}
		}
		
	}
	
	private void initSmartEMFImplementations(GenPackage genPkg) {
		for(EClassifier classifier : genPkg.getEcorePackage().getEClassifiers()){
			if(classifier instanceof EClass eClass) {
				if(eClass.isInterface()) {
					continue;
				}
				SmartEMFObjectTemplate implTemplate = new SmartEMFObjectTemplate(genPkg, eClass, path);
				templates.add(implTemplate);
			}
		}
	}

	public void generateModelCode() {
		TemplateUtil.uriStringToGenModelMap.clear();
		TemplateUtil.registerGenModel(genModel);
		templates.stream().forEach(template -> template.createCode());
	}
}
