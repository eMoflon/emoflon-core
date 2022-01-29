package org.emoflon.smartemf.templates

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage
import org.emoflon.smartemf.templates.util.TemplateUtil

/**
 * creates the implementation for the package factory
 */
class FactoryImplTemplate implements CodeTemplate{
	
	var GenPackage genPack
	var String path
	
	new(GenPackage genPack, String path) {
		this.genPack = genPack
		this.path = path
	}
	
	
	override createCode() {
		val className = genPack.getEcorePackage.name + "FactoryImpl"
		var code = '''
			package «TemplateUtil.getImplPrefix(genPack)»;
			
			«FOR clazz : TemplateUtil.getEClasses(genPack)»
				import «TemplateUtil.getFQName(clazz)»;
			«ENDFOR»
			import «TemplateUtil.getFactoryInterface(genPack)»;
			import «TemplateUtil.getPackageClassName(genPack)»;
			
			import org.eclipse.emf.ecore.EClass;
			import org.eclipse.emf.ecore.EDataType;
			import org.eclipse.emf.ecore.EObject;
			import org.eclipse.emf.ecore.EPackage;
			
			import org.eclipse.emf.ecore.impl.EFactoryImpl;
			
			import org.eclipse.emf.ecore.plugin.EcorePlugin;
			
			public class «className» extends EFactoryImpl implements «TemplateUtil.getFactoryInterface(genPack)» {
			
				public static «TemplateUtil.getFactoryInterface(genPack)» init() {
					try {
						«TemplateUtil.getFactoryInterface(genPack)» the«TemplateUtil.getFactoryInterface(genPack)» = («TemplateUtil.getFactoryInterface(genPack)») EPackage.Registry.INSTANCE
								.getEFactory(«TemplateUtil.getPackageClassName(genPack)».eNS_URI);
						if (the«TemplateUtil.getFactoryInterface(genPack)» != null) {
							return the«TemplateUtil.getFactoryInterface(genPack)»;
						}
					} catch (Exception exception) {
						EcorePlugin.INSTANCE.log(exception);
					}
					return new «className»();
				}
			
				public «className»() {
					super();
				}
			
				@Override
				public EObject create(EClass eClass) {
					switch (eClass.getClassifierID()) {
					«FOR clazz : TemplateUtil.getEClasses(genPack)»
						case «TemplateUtil.getPackageClassName(genPack)».«TemplateUtil.getLiteral(clazz)»:
							return create«clazz.name.toFirstUpper»();
					«ENDFOR»
					default:
						throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
					}
				}
				
				«IF TemplateUtil.hasEEnums(genPack)»
					@Override
					public Object createFromString(EDataType eDataType, String initialValue) {
						switch (eDataType.getClassifierID()) {
						«FOR clazz : TemplateUtil.getEEnums(genPack)»
							case «TemplateUtil.getPackageClassName(genPack)».«TemplateUtil.getLiteral(clazz)»:
								return create«clazz.name.toFirstUpper»FromString(eDataType, initialValue);
						«ENDFOR»
						default:
							throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
						}
					}
			
				@Override
				public String convertToString(EDataType eDataType, Object instanceValue) {
					switch (eDataType.getClassifierID()) {
					«FOR clazz : TemplateUtil.getEEnums(genPack)»
						case «TemplateUtil.getPackageClassName(genPack)».«TemplateUtil.getLiteral(clazz)»:
							return convert«clazz.name.toFirstUpper»ToString(eDataType, instanceValue);
					«ENDFOR»
					default:
						throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
					}
				}
				«ENDIF»
				
				«FOR clazz : TemplateUtil.getEClasses(genPack)»
				@Override
				public «"Container".equals(clazz.name)?TemplateUtil.getImplPrefix(genPack)+"."+clazz.name:clazz.name» create«clazz.name.toFirstUpper»() {
					«clazz.name»Impl «TemplateUtil.getValidName(clazz.name.toFirstLower)» = new «clazz.name»Impl();
					return «TemplateUtil.getValidName(clazz.name.toFirstLower)»;
				}
				«ENDFOR»
				
				«FOR clazz : TemplateUtil.getEEnums(genPack)»
					public «clazz.name» create«clazz.name.toFirstUpper»FromString(EDataType eDataType, String initialValue) {
						«clazz.name» result = «clazz.name».get(initialValue);
						if (result == null)
							throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
								
						return result;
					}
					
					public String convert«clazz.name.toFirstUpper»ToString(EDataType eDataType, Object instanceValue) {
						return instanceValue == null ? null : instanceValue.toString();
					}
				«ENDFOR»
			
				@Override
				public «TemplateUtil.getPackageClassName(genPack)» get«TemplateUtil.getPackageClassName(genPack)»() {
				return («TemplateUtil.getPackageClassName(genPack)») getEPackage();
				}
			} 
		'''
		
		TemplateUtil.writeToFile(path + TemplateUtil.getFactoryImpl(genPack) + ".java", code);
	}
}
