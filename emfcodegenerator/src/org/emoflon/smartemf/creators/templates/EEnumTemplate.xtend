package org.emoflon.smartemf.creators.templates

import java.io.File
import java.io.FileWriter
import org.eclipse.emf.ecore.EEnum
import org.emoflon.smartemf.creators.FileCreator
import org.emoflon.smartemf.creators.templates.util.CodeFormattingUtil
import org.emoflon.smartemf.creators.templates.util.PackageInformation
import org.emoflon.smartemf.creators.templates.util.TemplateUtil

/**
 * This Creator can be used to generate the source-code of an
 * {@link EEnum EEnum} implementation.
 */
class EEnumTemplate implements FileCreator {
	
	/**
	 * The inspector for the EPackage which contains the EEnum
	 */
	var PackageInformation e_pak
	
	/**
	 * The EEnum for which the code shall be generated
	 */
	var EEnum e_enum
	
	/**
	 * the fq-path of the file to which shall be written
	 */
	var String file_path
	
	/**
	 * Stores if this Creator was properly initialized
	 */
	var boolean is_initialized = false
	
	/**
	 * Constructs a new EEnumCreator.
	 * @param eenum EEnum for which this Creator shall generate code.
	 * @param e_pak PackageInspector of the EPackage in which the EEnum is contained.
	 * @author Adrian Zwenger
	 */
	new(EEnum eenum, PackageInformation e_pak, String generatedFileDir) {
		this.e_enum = eenum
		this.e_pak = e_pak
	}

	def String createSrcCode() {
		return '''
			package «e_pak.get_package_declaration_name»;
			
			import java.lang.String;
			
			import java.util.Arrays;
			import java.util.Collections;
			import java.util.List;
			
			import org.eclipse.emf.common.util.Enumerator;
			
			public enum «e_enum.name» implements Enumerator {
				
				«FOR literal : e_enum.ELiterals SEPARATOR ','» «TemplateUtil.getLiteral(literal)»(«literal.value», "«literal.name»", "«literal.literal»")«ENDFOR»«IF e_enum.ELiterals !== null &&  !e_enum.ELiterals.empty»;«ENDIF»
				
				«FOR literal : e_enum.ELiterals»
					public static final int «literal.name»_VALUE = «literal.value»;
				«ENDFOR»
				
				private static final «e_enum.name»[] VALUES_ARRAY = new «e_enum.name»[] {«FOR literal : e_enum.ELiterals SEPARATOR ','»«TemplateUtil.getLiteral(literal)»«ENDFOR»};
			
				public static final List<«e_enum.name»> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));
			
				public static «e_enum.name» get(String literal) {
				for (int i = 0; i < VALUES_ARRAY.length; ++i) {
					«e_enum.name» result = VALUES_ARRAY[i];
					if (result.toString().equals(literal)) {
						return result;
					}
				}
				return null;
				}
			
				public static «e_enum.name» getByName(String name) {
				for (int i = 0; i < VALUES_ARRAY.length; ++i) {
					«e_enum.name» result = VALUES_ARRAY[i];
					if (result.getName().equals(name)) {
						return result;
					}
				}
				return null;
				}
			
				public static «e_enum.name» get(int value) {
					switch (value) {
					«FOR literal : e_enum.ELiterals»
						case «literal.name»_VALUE:
							return «TemplateUtil.getLiteral(literal)»;
					«ENDFOR»
					}
					return null;
				}
			
				private final int value;
			
				private final String name;
			
				private final String literal;
			
				private «e_enum.name»(int value, String name, String literal) {
				this.value = value;
				this.name = name;
				this.literal = literal;
				}
			
				@Override
				public int getValue() {
					return value;
				}
			
				@Override
				public String getName() {
				return name;
				}
			
				@Override
				public String getLiteral() {
					return literal;
				}
			
				@Override
				public String toString() {
				return literal;
				}
			
			} //«e_enum.name»
			
		'''
	}

	/**
	 * @inheritDoc
	 */
	override initialize_creator(String fq_file_path) {
		file_path = fq_file_path
		is_initialized = true
	}

	/**
	 * @inheritDoc
	 */
	override write_to_file() {
		if(!this.is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
			
		var file = new File(this.file_path)
		file.getParentFile().mkdirs()
		var fw = new FileWriter(file , false)
		fw.write(CodeFormattingUtil.format(createSrcCode))
		fw.close()
	}	
}
