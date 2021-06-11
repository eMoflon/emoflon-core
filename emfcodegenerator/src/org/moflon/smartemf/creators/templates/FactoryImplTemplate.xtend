package org.moflon.smartemf.creators.templates

import java.io.File
import java.io.FileWriter
import org.moflon.smartemf.EcoreGenmodelParser
import org.moflon.smartemf.inspectors.util.PackageInspector
import org.moflon.smartemf.EMFCodeGenerationClass
import org.moflon.smartemf.creators.FileCreator

/**
 * creates the implementation for the package factory
 */
class FactoryImplTemplate extends EMFCodeGenerationClass implements FileCreator {
	/**
	 * The EPackages Inspector for which this factory-interface shall be created
	 */
	var protected PackageInspector e_pak

	/**
	 * stores the package declaration for the interface
	 */
	var protected String package_declaration

	/**
	 * Stores if this Creator was properly initialized.
	 */
	var protected boolean is_initialized = false

	/**
	 * Stores the path to the interface-file which will be created
	 */
	var protected String file_path
	/**
	 * stores the name for the package-factory class
	 */
	var String class_name

	new(EcoreGenmodelParser gen_model, PackageInspector package_inspector){
		super(gen_model)
		e_pak = package_inspector
		class_name = e_pak.get_emf_package_factory_class_name()+"Impl"
		package_declaration = e_pak.get_package_declaration_name()

	}
	
	def String createSrcCode() {
		return '''
		package «package_declaration».impl;
		
		«FOR clazz : e_pak.get_all_eclasses_in_package»
		import «package_declaration».«clazz.name»;
		«ENDFOR»
		«FOR clazz : e_pak.get_all_eenums_in_package»
		import «package_declaration».«clazz.name»;
		«ENDFOR»
		import «package_declaration».«e_pak.get_emf_package_factory_class_name()»;
		import «package_declaration».«e_pak.get_emf_e_package.name.toFirstUpper»Package;
		
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.emf.ecore.EDataType;
		import org.eclipse.emf.ecore.EObject;
		import org.eclipse.emf.ecore.EPackage;
		
		import org.eclipse.emf.ecore.impl.EFactoryImpl;
		
		import org.eclipse.emf.ecore.plugin.EcorePlugin;
		
		public class «class_name» extends EFactoryImpl implements «e_pak.get_emf_package_factory_class_name()» {


			public static «e_pak.get_emf_package_factory_class_name()» init() {
				try {
					«e_pak.get_emf_package_factory_class_name()» the«e_pak.get_emf_package_factory_class_name()» = («e_pak.get_emf_package_factory_class_name()») EPackage.Registry.INSTANCE
							.getEFactory(«e_pak.get_emf_e_package.name.toFirstUpper»Package.eNS_URI);
					if (the«e_pak.get_emf_package_factory_class_name()» != null) {
						return the«e_pak.get_emf_package_factory_class_name()»;
					}
				} catch (Exception exception) {
					EcorePlugin.INSTANCE.log(exception);
				}
				return new «class_name»();
			}
		
			public «class_name»() {
				super();
			}
		
			@Override
			public EObject create(EClass eClass) {
				switch (eClass.getClassifierID()) {
				«FOR clazz : e_pak.get_all_eclasses_in_package»
				case «e_pak.get_emf_e_package.name.toFirstUpper»Package.«SmartEMFObjectTemplate.getLiteral(clazz)»:
					return create«clazz.name.toFirstUpper»();
				«ENDFOR»
				default:
					throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
				}
			}
			
			«IF e_pak.get_all_eenums_in_package !== null && !e_pak.get_all_eenums_in_package.empty»
			@Override
			public Object createFromString(EDataType eDataType, String initialValue) {
				switch (eDataType.getClassifierID()) {
				«FOR clazz : e_pak.get_all_eenums_in_package»
				case «e_pak.get_emf_e_package.name.toFirstUpper»Package.«SmartEMFObjectTemplate.getLiteral(clazz)»:
					return create«clazz.name.toFirstUpper»FromString(eDataType, initialValue);
				«ENDFOR»
				default:
					throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
				}
			}

			@Override
			public String convertToString(EDataType eDataType, Object instanceValue) {
				switch (eDataType.getClassifierID()) {
				«FOR clazz : e_pak.get_all_eenums_in_package»
				case «e_pak.get_emf_e_package.name.toFirstUpper»Package.«SmartEMFObjectTemplate.getLiteral(clazz)»:
					return convert«clazz.name.toFirstUpper»ToString(eDataType, instanceValue);
				«ENDFOR»
				default:
					throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
				}
			}
			«ENDIF»
			
			«FOR clazz : e_pak.get_all_eclasses_in_package»
			@Override
			public «clazz.name» create«clazz.name.toFirstUpper»() {
				«clazz.name»Impl «clazz.name.toFirstLower» = new «clazz.name»Impl();
				return «clazz.name.toFirstLower»;
			}
			«ENDFOR»
			
			«IF e_pak.get_all_eenums_in_package !== null && !e_pak.get_all_eenums_in_package.empty»
			«FOR clazz : e_pak.get_all_eenums_in_package»
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
			«ENDIF»

			@Override
			public «e_pak.get_emf_e_package.name.toFirstUpper»Package get«e_pak.get_emf_e_package.name.toFirstUpper»Package() {
				return («e_pak.get_emf_e_package.name.toFirstUpper»Package) getEPackage();
			}
		
			@Deprecated
			public static «e_pak.get_emf_e_package.name.toFirstUpper»Package getPackage() {
				return «e_pak.get_emf_e_package.name.toFirstUpper»Package.eINSTANCE;
			}
		
		} //«class_name»
		'''
	}

	override initialize_creator(String fq_file_path, String IDENTION) {
		is_initialized = true
		file_path = fq_file_path
	}
	
	/**
	 * @inheritDoc
	 */
	override write_to_file() {
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		var factory_file = new File(this.file_path)
		factory_file.getParentFile().mkdirs()
		var factory_fw = new FileWriter(factory_file, false)
		factory_fw.write(createSrcCode())
		factory_fw.close()
	}
	
}
