package org.moflon.smartemf.creators.templates.util

import java.util.HashMap
import java.util.LinkedList
import java.util.Map
import org.eclipse.emf.codegen.ecore.genmodel.GenModel
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EStructuralFeature
import org.moflon.smartemf.creators.FileCreator
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import java.util.Collections

class TemplateUtil {
	
	public static Map<String, GenModel> uriStringToGenModelMap = Collections.synchronizedMap(new HashMap())
	
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
		if("EFeatureMapEntry".equals(feature.EType.name))
//			return "org.eclipse.emf.ecore.util.FeatureMap"
			return "java.util.HashMap<Object, Object>"
			
		
		if(feature.EType.name.contains("MapEntry"))
//			return "org.eclipse.emf.common.util.EMap<Object, Object>"
			return "java.util.HashMap<Object, Object>"
			
		if(!feature.isMany) {
			switch feature.EType.name {
				case "EString" : return "java.lang.String"
				case "EInt" : return "int"
				case "EBoolean" : return "boolean"
				case "EChar" : return "char"
				case "EDouble" : return "double"
				case "EDate" : return "java.util.Date"
			}
			return getFQName(feature.EType)
		}
			
		return '''«getListTypeName(feature)»<«getFQName(feature.EType)»>'''
	}
	
	static def splitNameAtUppercases(String name) {
		var splittedName = new LinkedList
		var from = 0
		for(var i=1; i<name.length-1; i++) {
			// only split if current char is uppercase and the next lowercase
			if(Character.isUpperCase(name.charAt(i)) && !Character.isUpperCase(name.charAt(i+1))) {
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
	
	static def getPrefix(EPackage ePackage) {
		val genModel = getGenModel(ePackage)
		if(genModel === null)
			return ""
		
		for(gp : genModel.allGenPackagesWithClassifiers) {
			return gp.basePackage + "."
		}
	}
	
	static def getGenModel(EPackage ePackage) {
		val pkgURI = ePackage.nsURI
		val genModelURI = pkgURI.replace(".ecore", ".genmodel")
		val resourceURI = genModelURI.replace("platform:/", "platform:/resource/")
		val pluginURI = resourceURI.replace("/resource/", "/plugin/")
		
		if(uriStringToGenModelMap.containsKey(resourceURI)) {
			return uriStringToGenModelMap.get(resourceURI)
		}
		
		if(uriStringToGenModelMap.containsKey(pluginURI)) {
			return uriStringToGenModelMap.get(pluginURI)
		}
		
		val rs = new ResourceSetImpl
		val r = rs.createResource(URI.createURI(resourceURI))
		try {
			r.load(null)
			if(r.contents.isEmpty)
				return null
				
			val content = r.contents.get(0)
			if(content instanceof GenModel) {
				uriStringToGenModelMap.put(resourceURI, content)
				return content
			}
		}
		catch(Exception e) {
			
		}
		
		val pluginResource = rs.createResource(URI.createURI(pluginURI))
		try {
			pluginResource.load(null)
			if(pluginResource.contents.isEmpty)
				return null
				
			val content = pluginResource.contents.get(0)
			if(content instanceof GenModel) {
				uriStringToGenModelMap.put(pluginURI, content)
				return content
			}
		}
		catch(Exception e) {
			
		}
		
		uriStringToGenModelMap.put(resourceURI, null)
		uriStringToGenModelMap.put(pluginURI, null)
		
		return null
	}
	
	static def getFQName(EPackage ePackage) {
		if(ePackage.EClassifiers.get(0).instanceClassName !== null) {
			val someClazzFQN = getFQName(ePackage.EClassifiers.get(0))
			val dotIdx = someClazzFQN.lastIndexOf(".")
			return someClazzFQN.substring(0,dotIdx)
		}
		
		var currentPackage = ePackage
		var FQPackagePath = currentPackage.name
		while(currentPackage.eContainer !== null) {
			currentPackage = currentPackage.eContainer as EPackage
			FQPackagePath = currentPackage.name + "." + FQPackagePath
		}
		
		return getPrefix(ePackage) + FQPackagePath
	}
	
	static def String getFQName(EClassifier eClass) {
		if(eClass.instanceClassName !== null) {
			return eClass.instanceClassName
		} else {
			return getFQName(eClass.EPackage) + "." + eClass.name
		}
		
	}
	
	static def getPackageClassName(EPackage pkg) {
		if(!"EPackageImpl".equals(pkg.class.simpleName)) {
			val className = pkg.class.simpleName
			val implIdx = className.lastIndexOf("Impl")
			return className.substring(0, implIdx)
		}
		return pkg.name.toFirstUpper + "Package"
	}
	
	static def getPackageClassName(EClassifier c) {
		return getPackageClassName(c.EPackage)
	}
	
	static def getPackageClassName(EStructuralFeature f) {
		return getPackageClassName(f.eContainer as EClassifier)
	}
	
	static def getValidName(String name) {
		if(FileCreator.blacklist.contains(name)) {
			return "__" + name
		}
		else
			return name
	}
}