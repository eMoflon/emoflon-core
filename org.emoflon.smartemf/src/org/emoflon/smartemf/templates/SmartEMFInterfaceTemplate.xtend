package org.emoflon.smartemf.templates

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage
import org.eclipse.emf.ecore.EClass
import org.emoflon.smartemf.templates.util.TemplateUtil

class SmartEMFInterfaceTemplate implements CodeTemplate{
	
	var GenPackage genPack
	var EClass eClass
	var String path
	
	new(GenPackage genPack, EClass eClass, String path) {
		this.genPack = genPack
		this.eClass = eClass
		this.path = path
	}
	
	override createCode() {
		val className = eClass.name
		
		var code = '''
		package «TemplateUtil.getInterfaceSuffix(genPack)»;
		
		«FOR importedGenPack : TemplateUtil.getImportPackages(eClass)»
		import «TemplateUtil.getMetadataSuffix(importedGenPack)».«TemplateUtil.getPackageClassName(importedGenPack)»;
		«ENDFOR»
		
		import org.emoflon.smartemf.runtime.notification.SmartEMFNotification;
		import org.emoflon.smartemf.runtime.SmartObject;
		import org.emoflon.smartemf.runtime.collections.*;
		
		import org.eclipse.emf.common.util.EList;
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.emf.ecore.EObject;
		import org.eclipse.emf.ecore.EStructuralFeature;
		
		public interface «className» extends EObject«TemplateUtil.getSuperTypes(eClass)» {
			
		    «FOR feature : eClass.EAllStructuralFeatures»
		    public «TemplateUtil.getFieldTypeName(feature)» «TemplateUtil.getOrIs(feature)»«feature.name.toFirstUpper»();
		    
		    «IF !feature.isUnsettable»
		    public void set«feature.name.toFirstUpper»(«TemplateUtil.getFieldTypeName(feature)» value);
		    
			«ENDIF»
		    «ENDFOR»
		
		}
		'''
		TemplateUtil.writeToFile(path + TemplateUtil.getFQInterfaceName(genPack, eClass).replace(".", "/") + ".java", code);
	}
	
}