package org.moflon.smartemf.creators.templates

import java.io.File
import java.io.FileWriter
import org.moflon.smartemf.EcoreGenmodelParser
import org.moflon.smartemf.creators.FileCreator
import org.moflon.smartemf.creators.templates.util.CodeFormattingUtil
import org.moflon.smartemf.creators.templates.util.PackageInformation

/**
 * creates the interface for the package factory
 */
class FactoryInterfaceTemplate implements FileCreator{
	/**
	 * The EPackages Inspector for which this factory-interface shall be created
	 */
	var protected PackageInformation e_pak

	/**
	 * The name which this interface will have
	 */
	var protected String interface_name

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

	new(EcoreGenmodelParser gen_model, PackageInformation package_inspector, String generatedFileDir){
		e_pak = package_inspector
		interface_name = e_pak.get_emf_package_factory_class_name()
		package_declaration = e_pak.get_package_declaration_name()
	}
	
	def String createSrcCode() {
		return '''
		package «package_declaration»;
		
		import org.eclipse.emf.ecore.EFactory;
		
		public interface «interface_name» extends EFactory {

			«interface_name» eINSTANCE = «package_declaration».impl.«interface_name»Impl.init();
			
			«FOR clazz : e_pak.get_all_eclasses_in_package»
			«clazz.name» create«clazz.name.toFirstUpper»();
			
			«ENDFOR»
			
			«e_pak.get_emf_e_package.name.toFirstUpper»Package get«e_pak.get_emf_e_package.name.toFirstUpper»Package();
		
		} //«interface_name»
		
		'''
	}

	override initialize_creator(String fq_file_path) {
		this.is_initialized = true
		this.file_path = fq_file_path
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
		factory_fw.write(CodeFormattingUtil.format(createSrcCode))
		factory_fw.close()
	}

}
