package org.emoflon.smartemf.templates

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EReference
import org.emoflon.smartemf.templates.util.TemplateUtil

/**
 * This class generates the interface for the EMF-package class
 */
class PackageInterfaceTemplate implements CodeTemplate {
	
	var GenPackage genPack
	var String path
	
	new(GenPackage genPack, String path) {
		this.genPack = genPack
		this.path = path
	}
	
	override createCode() {
		var featureCounter = 0;
		var ePack = genPack.getEcorePackage
	
		var code = '''
		package «TemplateUtil.getMetadataPrefix(genPack)»;
		
		import java.lang.String;
		
		import org.eclipse.emf.ecore.EAttribute;
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.emf.ecore.EDataType;
		import org.eclipse.emf.ecore.EPackage;
		import org.eclipse.emf.ecore.EReference;
		
		import org.emoflon.smartemf.runtime.SmartPackage;
		
		public interface «TemplateUtil.getPackageClassName(genPack)» extends SmartPackage {

			String eNAME = "«ePack.name»";
			String eNS_URI = "«ePack.nsURI»";
			String eNS_PREFIX = "«ePack.nsPrefix»";
		
			«TemplateUtil.getPackageClassName(genPack)» eINSTANCE = «TemplateUtil.getImplPrefix(genPack)».«TemplateUtil.getPackageClassName(genPack)»Impl.init();
		
			«FOR clazz : TemplateUtil.getEClasses(genPack)»
				int «TemplateUtil.getLiteral(clazz)» = «clazz.classifierID»;
				«FOR feature : clazz.EStructuralFeatures»
					int «TemplateUtil.getLiteral(feature)» = «featureCounter++»;
				«ENDFOR»
				int «TemplateUtil.getLiteral(clazz)»_FEATURE_COUNT = «clazz.EStructuralFeatures.size + countSuperFeatures(clazz)»;
				int «TemplateUtil.getLiteral(clazz)»_OPERATION_COUNT = «clazz.EOperations.size + countSuperOperations(clazz)»;
				
			«ENDFOR»
			«FOR eenum : TemplateUtil.getEEnums(genPack)»
				int «TemplateUtil.getLiteral(eenum)» = «eenum.classifierID»;
			«ENDFOR»
			
			«FOR datatype : TemplateUtil.getEDataTypes(genPack)»
				int «TemplateUtil.getLiteral(datatype)» = «datatype.classifierID»;
			«ENDFOR»

			«FOR clazz : TemplateUtil.getEClasses(genPack)»
				EClass get«clazz.name»();
				«FOR feature : clazz.EStructuralFeatures»
					«IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF» get«clazz.name»_«feature.name.toFirstUpper»();
				«ENDFOR»
				
			«ENDFOR»
			«FOR eenum : TemplateUtil.getEEnums(genPack)»
				EEnum get«eenum.name.toFirstUpper»();
			«ENDFOR»
			«FOR datatype : TemplateUtil.getEDataTypes(genPack)»
				EDataType get«datatype.name.toFirstUpper»();
			«ENDFOR»
			
			«TemplateUtil.getFactoryInterface(genPack)» get«TemplateUtil.getFactoryName(genPack)»();
		
			interface Literals {
				
				«FOR clazz : TemplateUtil.getEClasses(genPack)»
					EClass «TemplateUtil.getLiteral(clazz)» = eINSTANCE.get«clazz.name»();
					
					«FOR feature : clazz.EStructuralFeatures»
						«IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF» «TemplateUtil.getLiteral(feature).toUpperCase» = eINSTANCE.get«clazz.name»_«feature.name.toFirstUpper»();
						
					«ENDFOR»
				«ENDFOR»
				
				«FOR eenum : TemplateUtil.getEEnums(genPack)»
					EEnum «TemplateUtil.getLiteral(eenum)» = eINSTANCE.get«eenum.name.toFirstUpper»();
				«ENDFOR»
				
				«FOR datatype : TemplateUtil.getEDataTypes(genPack)»
					EDataType «TemplateUtil.getLiteral(datatype)» = eINSTANCE.get«datatype.name»();
				«ENDFOR»
				
			}
		
		} 
		'''
		
		TemplateUtil.writeToFile(path + TemplateUtil.getMetadataPrefix(genPack).replace(".", "/") + "/" + TemplateUtil.getPackageClassName(genPack) + ".java", code);
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
}