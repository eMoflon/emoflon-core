package org.moflon.smartemf.creators.templates.util

import java.util.LinkedList
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EStructuralFeature

class TemplateUtil {
	static def getListTypeName(EStructuralFeature feature) {
		val isOrdered = feature.ordered
		val isUnique = feature.unique
		if(isOrdered && isUnique)
			return '''LinkedSmartESet'''
		if(isUnique)
			return '''SmartESet'''
		return '''SmartEList'''
	}
	
	static def getFieldTypeName(EStructuralFeature feature) {
		if(!feature.isMany) {
			switch feature.EType.name {
				case "EString" : return "String"
				case "EInt" : return "int"
				case "EBoolean" : return "boolean"
				case "EChar" : return "char"
				case "EDouble" : return "double"
			}
			return feature.EType.name
		}
		
		return '''«getListTypeName(feature)»<«feature.EType.name»>'''
	}
	
	static def splitNameAtUppercases(String name) {
		var splittedName = new LinkedList
		var from = 0
		for(var i=1; i<name.length; i++) {
			// only split if the last character was not also an uppercase letter
			if(!Character.isUpperCase(name.charAt(i-1)) && Character.isUpperCase(name.charAt(i))) {
				splittedName.add(name.substring(from, i))
				from = i;
			}
		}
		
		// add last segment
		splittedName.add(name.substring(from, name.length))
			
		return splittedName
	}
	
	// ids are always generated for the current eClass
	static def getLiteralID(ENamedElement feature) {
		val containingClass = feature.eContainer as EClassifier
		if(feature instanceof EStructuralFeature)
			return '''«FOR part : splitNameAtUppercases(containingClass.name) SEPARATOR "_"»«part.toUpperCase»«ENDFOR»__«FOR part : splitNameAtUppercases(feature.name) SEPARATOR "_"»«part.toUpperCase»«ENDFOR»'''		
		else {
			return '''«FOR part : splitNameAtUppercases(feature.name) SEPARATOR "_"»«part.toUpperCase»«ENDFOR»'''
		}
	}
	
	static def getLiteral(ENamedElement elt) {
		if(elt instanceof EStructuralFeature)
			return '''«FOR part : splitNameAtUppercases(elt.EContainingClass.name) SEPARATOR "_"»«part.toUpperCase»«ENDFOR»__«FOR part : splitNameAtUppercases(elt.name) SEPARATOR "_"»«part.toUpperCase»«ENDFOR»'''		
		else {
			return '''«FOR part : splitNameAtUppercases(elt.name) SEPARATOR "_"»«part.toUpperCase»«ENDFOR»'''
		}
	}
	
	static def getFQName(EPackage ePackage) {
		var currentPackage = ePackage
		var FQPackagePath = currentPackage.name
		while(currentPackage.eContainer !== null) {
			currentPackage = currentPackage.eContainer as EPackage
			FQPackagePath = currentPackage.name + "." + FQPackagePath
		}
		
		return FQPackagePath
	}
	
	static def getFQName(EClassifier eClass) {
		return getFQName(eClass.EPackage)
	}
	
	static def getPackageClassName(EPackage pkg) {
		return pkg.name.toFirstUpper + "Package"
	}
	
	static def getPackageClassName(EClassifier c) {
		return getPackageClassName(c.EPackage)
	}
	
	static def getPackageClassName(EStructuralFeature f) {
		return getPackageClassName(f.eContainer as EClassifier)
	}
	
	static def getValidName(String name) {
		val keywords = newLinkedList("import", "package", "class", "interface", "public", "private", "protected", "int", "double", "char", "boolean")
		if(keywords.contains(name)) {
			return "__" + name
		}
		else
			return name
	}
}