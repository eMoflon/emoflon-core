package org.moflon.smartemf.creators.templates

import java.io.File
import java.io.FileWriter
import java.util.HashMap
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.moflon.smartemf.EMFCodeGenerationClass
import org.moflon.smartemf.EcoreGenmodelParser
import org.moflon.smartemf.creators.FileCreator
import org.moflon.smartemf.inspectors.util.PackageInspector

/**
 * This class generates the interface for the EMF-package class
 */
class PackageInterfaceTemplate extends EMFCodeGenerationClass implements FileCreator{
	
	/**
	 * The inspector for the package
	 */
	var PackageInspector e_pak
	
	/**
	 * stores the fq-file name to which this interface shall be written to.
	 */
	var String file_path
	
	/**
	 * stores if this Creator was properly initialized
	 */
	var boolean is_initialized = false
	
	var featureCounter = 0;
	
	new(PackageInspector package_inspector, HashMap<EPackage,PackageInspector> e_pak_map, EcoreGenmodelParser gen_model){
		super(gen_model)
		e_pak = package_inspector
	}
	
	def String createSrcCode() {
		return '''
		package «e_pak.get_package_declaration_name»;
		
		import org.eclipse.emf.ecore.EAttribute;
		import org.eclipse.emf.ecore.EClass;
		«IF !e_pak.get_all_eenums_in_package.empty»
		import org.eclipse.emf.ecore.EEnum;
		«ENDIF»
		import org.eclipse.emf.ecore.EPackage;
		import org.eclipse.emf.ecore.EReference;
		
		public interface «e_pak.get_emf_package_class_name» extends EPackage {

			String eNAME = "«e_pak.get_emf_package_class_name»";
			String eNS_URI = "«e_pak.get_ens_uri»";
			String eNS_PREFIX = "«e_pak.get_ens_prefix»";
		
			«e_pak.get_emf_package_class_name» eINSTANCE = «e_pak.get_package_declaration_name».impl.«e_pak.get_emf_package_class_name»Impl.init();
		
			«FOR clazz : e_pak.get_all_eclasses_in_package»
			int «SmartEMFObjectTemplate.getLiteral(clazz)» = «clazz.classifierID»;
			«FOR feature : clazz.EStructuralFeatures»
			int «SmartEMFObjectTemplate.getLiteral(feature)» = «featureCounter++»;
			«ENDFOR»
			int «SmartEMFObjectTemplate.getLiteral(clazz)»_FEATURE_COUNT = «clazz.EStructuralFeatures.size + countSuperFeatures(clazz)»;
			int «SmartEMFObjectTemplate.getLiteral(clazz)»_OPERATION_COUNT = «clazz.EOperations.size + countSuperOperations(clazz)»;
			
			«ENDFOR»
			«FOR clazz : e_pak.get_all_eenums_in_package»
			int «SmartEMFObjectTemplate.getLiteral(clazz)» = «clazz.classifierID»;
			
			«ENDFOR»

			«FOR clazz : e_pak.get_all_eclasses_in_package»
			EClass get«clazz.name.toFirstUpper»();
			«FOR feature : clazz.EStructuralFeatures»
			«IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF» get«clazz.name»_«feature.name.toFirstUpper»();
			«ENDFOR»
			
			«ENDFOR»
			
			«FOR clazz : e_pak.get_all_eenums_in_package»
			EEnum get«clazz.name.toFirstUpper»();
			«ENDFOR»
			
			«e_pak.get_emf_e_package.name.toFirstUpper»Factory get«e_pak.get_emf_e_package.name.toFirstUpper»Factory();
		
			interface Literals {
				
				«FOR clazz : e_pak.get_all_eclasses_in_package»
				EClass «SmartEMFObjectTemplate.getLiteral(clazz)» = eINSTANCE.get«clazz.name.toFirstUpper»();
				
				«FOR feature : clazz.EStructuralFeatures»
				«IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF» «SmartEMFObjectTemplate.getLiteral(feature).toUpperCase» = eINSTANCE.get«clazz.name.toFirstUpper»_«feature.name.toFirstUpper»();
				
				«ENDFOR»
				«ENDFOR»
				
				«FOR clazz : e_pak.get_all_eenums_in_package»
				EEnum «SmartEMFObjectTemplate.getLiteral(clazz)» = eINSTANCE.get«clazz.name.toFirstUpper»();
				«ENDFOR»
			}
		
		} //«e_pak.get_emf_package_class_name»
		'''
	}
	
	static def countSuperFeatures(EClass clazz) {
		if(clazz.EAllSuperTypes === null || clazz.EAllSuperTypes.empty)
			return 0
		
		val featureList = clazz.EAllSuperTypes
					.filter[superClass | superClass.EStructuralFeatures !== null && !superClass.EStructuralFeatures.empty]
					.flatMap[superClass| superClass.EStructuralFeatures]
		if(featureList === null)
			return 0
			
		return featureList.size
	}
	
	static def countSuperOperations(EClass clazz) {
		if(clazz.EAllSuperTypes === null || clazz.EAllSuperTypes.empty)
			return 0
			
		val operationList = clazz.EAllSuperTypes
					.filter[superClass | superClass.EOperations !== null && !superClass.EOperations.empty]
					.flatMap[superClass| superClass.EOperations]
					
		if(operationList === null)
			return 0
			
		return operationList.size

	}
	
	override initialize_creator(String fq_file_path, String IDENTION) {
		file_path = fq_file_path
		is_initialized = true
	}
	
	override write_to_file() {	
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		var package_file = new File(this.file_path)
		package_file.getParentFile().mkdirs()
		var package_fw = new FileWriter(package_file , false)
		package_fw.write(createSrcCode())
		package_fw.close()
	}

}