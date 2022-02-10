package org.emoflon.smartemf.templates

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage
import org.eclipse.emf.ecore.EEnum
import org.emoflon.smartemf.templates.util.TemplateUtil

class EEnumTemplate implements CodeTemplate{
	
	var GenPackage genPack
	var EEnum eNum 
	var String path
	
	new(GenPackage genPack, EEnum eNum, String path) {
		this.genPack = genPack
		this.eNum = eNum
		this.path = path
	}
	
	
	override createCode() {
		var code = '''
			package «TemplateUtil.getInterfaceSuffix(genPack)»;
			
			import java.lang.String;
			
			import java.util.Arrays;
			import java.util.Collections;
			import java.util.List;
			
			import org.eclipse.emf.common.util.Enumerator;
			
			public enum «eNum.name» implements Enumerator {
				
				«FOR literal : eNum.ELiterals SEPARATOR ','» «TemplateUtil.getLiteral(literal)»(«literal.value», "«literal.name»", "«literal.literal»")«ENDFOR»«IF eNum.ELiterals !== null &&  !eNum.ELiterals.empty»;«ENDIF»
				
				«FOR literal : eNum.ELiterals»
					public static final int «literal.name»_VALUE = «literal.value»;
				«ENDFOR»
				
				private static final «eNum.name»[] VALUES_ARRAY = new «eNum.name»[] {«FOR literal : eNum.ELiterals SEPARATOR ','»«TemplateUtil.getLiteral(literal)»«ENDFOR»};
			
				public static final List<«eNum.name»> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));
			
				public static «eNum.name» get(String literal) {
				for (int i = 0; i < VALUES_ARRAY.length; ++i) {
					«eNum.name» result = VALUES_ARRAY[i];
					if (result.toString().equals(literal)) {
						return result;
					}
				}
				return null;
				}
			
				public static «eNum.name» getByName(String name) {
				for (int i = 0; i < VALUES_ARRAY.length; ++i) {
					«eNum.name» result = VALUES_ARRAY[i];
					if (result.getName().equals(name)) {
						return result;
					}
				}
				return null;
				}
			
				public static «eNum.name» get(int value) {
					switch (value) {
					«FOR literal : eNum.ELiterals»
						case «literal.name»_VALUE:
							return «TemplateUtil.getLiteral(literal)»;
					«ENDFOR»
					}
					return null;
				}
			
				private final int value;
			
				private final String name;
			
				private final String literal;
			
				private «eNum.name»(int value, String name, String literal) {
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
			
			}
			
		'''
		TemplateUtil.writeToFile(path + TemplateUtil.getInterfaceSuffix(genPack).replace(".", "/") + "/" + eNum.name + ".java", code);
	}
}
