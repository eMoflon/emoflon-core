package org.emoflon.smartemf.templates

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage
import org.emoflon.smartemf.templates.util.TemplateUtil

/**
 * creates the interface for the package factory
 */
class FactoryInterfaceTemplate implements CodeTemplate{
	var GenPackage genPack
	var String path
	
	new(GenPackage genPack, String path) {
		this.genPack = genPack
		this.path = path
	}
	
	override createCode() {
		val className = genPack.getEcorePackage.name.toFirstUpper + "Factory"
		
		var code = '''
		package «TemplateUtil.getMetadataSuffix(genPack)»;
		
		«FOR clazz : TemplateUtil.getEClasses(genPack)»
			import «TemplateUtil.getFQName(clazz)»;
		«ENDFOR»
		
		import org.eclipse.emf.ecore.EFactory;
		
		public interface «className» extends EFactory {

			«className» eINSTANCE = «TemplateUtil.getImplSuffix(genPack)».«className»Impl.init();
			
			«FOR clazz : TemplateUtil.getEClasses(genPack).filter[c|!c.abstract]»
			«clazz.name» create«clazz.name.toFirstUpper»();
			
			«ENDFOR»
			
			«TemplateUtil.getPackageClassName(genPack)» get«TemplateUtil.getPackageClassName(genPack).toFirstUpper»();
		
		}
		'''
		TemplateUtil.writeToFile(path + TemplateUtil.getFactoryInterface(genPack).replace(".", "/") + ".java", code);
	}

}
