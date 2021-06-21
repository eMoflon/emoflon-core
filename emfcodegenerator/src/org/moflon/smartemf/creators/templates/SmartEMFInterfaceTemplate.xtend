package org.moflon.smartemf.creators.templates

import java.io.File
import java.io.FileWriter
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EcorePackage
import org.moflon.smartemf.creators.FileCreator
import org.moflon.smartemf.creators.templates.util.CodeFormattingUtil
import org.moflon.smartemf.creators.templates.util.TemplateUtil

class SmartEMFInterfaceTemplate implements FileCreator{
	
	public val EClass eClass
	
	/**
	 * stores the fq-file name to which this interface shall be written to.
	 */
	var String file_path
	
	/**
	 * stores if this Creator was properly initialized
	 */
	var boolean is_initialized = false
	
	new(EClass eClass) {
		this.eClass = eClass
	}
	
	def String createCode() {
		val className = eClass.name
		val ePackage = eClass.EPackage
		val packageClassName = ePackage.name.toFirstUpper + "Package"
		val FQPackagePath = TemplateUtil.getFQName(ePackage)
		
		return '''
		package «FQPackagePath»;
		
		«FOR packages : getImportPackages()»
		import «TemplateUtil.getFQName(packages)».«packages.name.toFirstUpper»Package;
		«ENDFOR»
		
		import org.moflon.smartemf.runtime.notification.SmartEMFNotification;
		import org.moflon.smartemf.runtime.SmartObject;
		import org.moflon.smartemf.runtime.collections.*;
		
		import org.eclipse.emf.common.util.EList;
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.emf.ecore.EObject;
		import org.eclipse.emf.ecore.EStructuralFeature;
		
		public interface «className» extends EObject«getSuperTypes()» {
			
		    «FOR feature : eClass.EAllStructuralFeatures»
		    public «TemplateUtil.getFieldTypeName(feature)» «getOrIs(feature)»«feature.name.toFirstUpper»();
		    
		    «IF !feature.isUnsettable»
		    public void set«feature.name.toFirstUpper»(«TemplateUtil.getFieldTypeName(feature)» value);
		    
			«ENDIF»
		    «ENDFOR»
		
		}
		
		
		'''
	}
	
	def getSuperTypes() {
		return '''«FOR s : eClass.ESuperTypes», «TemplateUtil.getFQName(s)»«ENDFOR»'''
	}
	
	def getPackage() {
		return eClass.EPackage
	}
	
	def getOrIs(EStructuralFeature feature) {
		if(feature.EType.equals(EcorePackage.Literals.EBOOLEAN))
			return "is"
		else
			return "get"
	}
	
	def getImportPackages() {
		var packages = eClass.EAllSuperTypes.map[c|c.EPackage].toSet
		packages.add(eClass.EPackage)
		return packages
	}
	
	def getImportTypes() {
		// estructural feature types
		val types = eClass.EAllStructuralFeatures.map[c|c.EType].filter[c|!c.EPackage.equals(EcorePackage.eINSTANCE)].toSet
		types.addAll(eClass.ESuperTypes)
		// add this eclass
		types.add(eClass)
		types.remove(eClass)
		return types
	}
	
	override initialize_creator(String fq_file_path) {
		file_path = fq_file_path
		is_initialized = true;
	}
	
	override write_to_file() {
		if(!is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
			
//		var class_file = new File('''«path»/«eClass.name».java''')
		var class_file = new File(file_path)
		class_file.getParentFile().mkdirs()
		var class_fw = new FileWriter(class_file , false)
		class_fw.write(CodeFormattingUtil.format(createCode))
		class_fw.close
	}
	
}