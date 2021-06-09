package org.moflon.smartemf.creators.templates

import java.io.File
import java.io.FileWriter
import java.util.ArrayList
import org.eclipse.emf.ecore.EEnum
import org.moflon.smartemf.EMFCodeGenerationClass
import org.moflon.smartemf.creators.FileCreator
import org.moflon.smartemf.inspectors.util.PackageInspector

/**
 * This Creator can be used to generate the source-code of an
 * {@link EEnum EEnum} implementation.
 */
class EEnumCreator extends EMFCodeGenerationClass implements FileCreator {
	
	/**
	 * The inspector for the EPackage which contains the EEnum
	 */
	var PackageInspector e_pak
	
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
	 * Stores the entries of the EEnum
	 */
	var ArrayList<String> literals_declaration = new ArrayList<String>()
	
	/**
	 * Constructs a new EEnumCreator.
	 * @param eenum EEnum for which this Creator shall generate code.
	 * @param e_pak PackageInspector of the EPackage in which the EEnum is contained.
	 * @author Adrian Zwenger
	 */
	new(EEnum eenum, PackageInspector e_pak) {
		super(PackageInspector.emf_model)
		this.e_enum = eenum
		this.e_pak = e_pak
	}

	/**
	 * This method generates the code.
	 * @param IDENTION String with which code shall be idented.
	 * @author Adrian Zwenger
	 */
	def private void init(String IDENTION){
		var iterator = this.e_enum.ELiterals.iterator
		var literals_block = new ArrayList<String>()
		var values_block = new ArrayList<String>()
		var values_array =
'''«IDENTION»private static final «this.e_enum.name»[] VALUES_ARRAY = new «this.e_enum.name»[] { '''
		
		var get_by_int_method =
'''
«IDENTION»public static «this.e_enum.name» get(int value) {
«IDENTION»«IDENTION»switch(value) {
'''
		while(iterator.hasNext){
			var literal = iterator.next
			var a =
'''
«IDENTION»«literal.name.toUpperCase»(«literal.value», "«literal.name»", "«literal.name»")'''

			a += ((iterator.hasNext) ? "," : ";") + System.lineSeparator
			literals_block.add(a.toString)

			a =
'''
«IDENTION»public static final int «literal.name.toUpperCase»_VALUE = «literal.value»;
'''
			values_block.add(a.toString)

			values_array += literal.name.toUpperCase + ", "
			
			get_by_int_method +=
'''
«IDENTION»«IDENTION»«IDENTION»case «literal.name.toUpperCase»_VALUE: return «literal.name.toUpperCase»;
'''
		}

		values_array += "};" + System.lineSeparator
		var values_list =
'''
«IDENTION»public static final List<«this.e_enum.name»> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));
'''

		get_by_int_method +=
'''
«IDENTION»«IDENTION»}
«IDENTION»«IDENTION»return null;
«IDENTION»}
'''
		var generic_methods =
'''
«IDENTION»private final int value;
«IDENTION»private final String name;
«IDENTION»private final String literal;
«IDENTION»public static «this.e_enum.name» getByName(String name) {
«IDENTION»«IDENTION»for (int i = 0; i < VALUES_ARRAY.length; ++i) {
«IDENTION»«IDENTION»«IDENTION»«this.e_enum.name» result = VALUES_ARRAY[i];
«IDENTION»«IDENTION»«IDENTION»if (result.getName().equals(name)) return result;
«IDENTION»«IDENTION»}
«IDENTION»«IDENTION»return null;
«IDENTION»}
«IDENTION»public static «this.e_enum.name» get(String literal) {
«IDENTION»«IDENTION»for (int i = 0; i < VALUES_ARRAY.length; ++i) {
«IDENTION»«IDENTION»«IDENTION»«this.e_enum.name» result = VALUES_ARRAY[i];
«IDENTION»«IDENTION»«IDENTION»if (result.toString().equals(literal)) return result;
«IDENTION»«IDENTION»}
«IDENTION»«IDENTION»return null;
«IDENTION»}
«IDENTION»private «this.e_enum.name»(int value, String name, String literal) {
«IDENTION»«IDENTION»this.value = value;
«IDENTION»«IDENTION»this.name = name;
«IDENTION»«IDENTION»this.literal = literal;
«IDENTION»}
«IDENTION»public int getValue() {
«IDENTION»«IDENTION»return value;
«IDENTION»}
«IDENTION»public String getName() {
«IDENTION»«IDENTION»return name;
«IDENTION»}
«IDENTION»public String getLiteral() {
«IDENTION»«IDENTION»return literal;
«IDENTION»}
«IDENTION»@Override
«IDENTION»public String toString() {
«IDENTION»«IDENTION»return literal;
«IDENTION»}
'''
		this.literals_declaration.addAll(literals_block)
		this.literals_declaration.addAll(values_block)
		this.literals_declaration.add(values_array)
		this.literals_declaration.add(values_list)
		this.literals_declaration.add(get_by_int_method)
		this.literals_declaration.add(generic_methods)
	}

	/**
	 * @inheritDoc
	 */
	override initialize_creator(String fq_file_path, String IDENTION) {
		this.file_path = fq_file_path
		this.is_initialized = true
		this.init(IDENTION)
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
		fw.write(
'''
package «this.e_pak.get_package_declaration_name»;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.Enumerator;
public enum «this.e_enum.name» implements Enumerator {
'''
		)

		for(entry : this.literals_declaration){
			fw.write(entry)
		}

		fw.write("}" + System.lineSeparator)
		fw.close()
		this.is_initialized = false
	}	
}
