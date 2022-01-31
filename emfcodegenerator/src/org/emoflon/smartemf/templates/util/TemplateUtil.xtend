package org.emoflon.smartemf.templates.util

import java.io.File
import java.io.FileWriter
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedList
import java.util.Map
import org.eclipse.emf.codegen.ecore.genmodel.GenModel
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl

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
			
		if(!feature.isMany || feature instanceof EAttribute) {
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
			
		return '''«getListTypeName(feature)»<«getValidGenericArg(getFQName(feature.EType))»>'''
	}
	
	static def getValidGenericArg(String arg) {
		switch(arg) {
			case "boolean" : return "Boolean"
			case "int"     : return "Integer"
			case "double"  : return "Double"
			case "float"   : return "Float"
			case "char"    : return "Char"
			default : return arg   
		}
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
			if(gp.basePackage != null && !gp.basePackage.isEmpty)
				return gp.basePackage + "."
			else
				return ""
		}
	}
	
	static def registerGenModel(GenModel genModel) {
		val pkgURI = genModel.genPackages.get(0).NSURI
		val genModelURI = pkgURI.replace(".ecore", ".genmodel")
		val resourceURI = genModelURI.replace("platform:/", "platform:/resource/")
		uriStringToGenModelMap.put(resourceURI, genModel)
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
	
	static def getFQName(GenPackage genPackage) {
		return getFQName(genPackage.getEcorePackage)
	}
	
	static def getFQName(EPackage ePackage) {
		if(ePackage.EClassifiers.get(0).instanceClassName !== null) {
			val someClazzFQN = getFQName(ePackage.EClassifiers.get(0))
			if(!someClazzFQN.contains(".")) {
				return "";
			}
			val dotIdx = someClazzFQN.lastIndexOf(".")
			return someClazzFQN.substring(0,dotIdx)
		}
		
		var currentPackage = ePackage
		var FQPackagePath = currentPackage.name
		while(currentPackage.eContainer !== null) {
			currentPackage = currentPackage.eContainer as EPackage
			FQPackagePath = currentPackage.name + "." + FQPackagePath
		}
		
		var path = getPrefix(ePackage) + FQPackagePath + getInterfaceSuffix(ePackage)
		return path
	}
	
	static def String getInterfaceSuffix(EPackage ePackage) {
		val genModel = getGenModel(ePackage)
		if(genModel === null)
			return ""
		
		for(gp : genModel.allGenPackagesWithClassifiers) {
			if(gp.interfacePackageSuffix != null && !gp.interfacePackageSuffix.isEmpty)
				return "." + gp.interfacePackageSuffix
			else
				return ""
		}
	}
	
	static def String getFQName(EClassifier eClass) {
		if(eClass.instanceClassName !== null) {
			return eClass.instanceClassName
		} else {
			return getFQName(eClass.EPackage) +  "." + eClass.name
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
	
	static def getPackageClassName(GenPackage genPackage) {
		return getPackageClassName(genPackage.getEcorePackage)
	}
	
	static def getPackageClassName(EClassifier c) {
		return getPackageClassName(c.EPackage)
	}
	
	static def getPackageClassName(EStructuralFeature f) {
		return getPackageClassName(f.eContainer as EClassifier)
	}
	
	def static getInterfaceSuffix(GenPackage genPackage) {
		var prefix = ""
		if(genPackage.basePackage != null && !genPackage.basePackage.isEmpty) 
			prefix += genPackage.basePackage + "." 
		prefix += genPackage.getEcorePackage.name
		if(genPackage.interfacePackageSuffix != null && !genPackage.interfacePackageSuffix.isEmpty)
			prefix += "." + genPackage.interfacePackageSuffix 
		return prefix
	}
	
	def static getImplSuffix(GenPackage genPackage) {
		var prefix = ""
		if(genPackage.basePackage != null && !genPackage.basePackage.isEmpty) 
			prefix += genPackage.basePackage + "." 
		prefix += genPackage.getEcorePackage.name
		if(genPackage.classPackageSuffix == null) {
			prefix += ".impl"
		} 
		else if(!genPackage.classPackageSuffix.isEmpty)
			prefix += "." + genPackage.classPackageSuffix
		return prefix
	}
	
	def static getMetadataSuffix(GenPackage genPackage) {
		var prefix = ""
		if(genPackage.basePackage != null && !genPackage.basePackage.isEmpty) 
			prefix += genPackage.basePackage + "." 
		prefix += genPackage.getEcorePackage.name
		if(genPackage.metaDataPackageSuffix != null && !genPackage.metaDataPackageSuffix.isEmpty)
			prefix += "." + genPackage.metaDataPackageSuffix
		return prefix
	}
	
	def static getFactoryInterface(GenPackage genPackage) {
		var fqName = org.emoflon.smartemf.templates.util.TemplateUtil.getMetadataSuffix(genPackage)
		fqName += "." + genPackage.getEcorePackage.name.toFirstUpper + "Factory"
		return fqName
	}
	
	def static getFactoryImpl(GenPackage genPackage) {
		var fqName = org.emoflon.smartemf.templates.util.TemplateUtil.getImplSuffix(genPackage)
		fqName += "." + genPackage.getEcorePackage.name.toFirstUpper + "FactoryImpl"
		return fqName
	}

	static def writeToFile(String filePath, String code) {
		var file = new File(filePath)
		file.getParentFile().mkdirs()
		var fw = new FileWriter(file , false)
//		fw.write(CodeFormattingUtil.format(code))
		fw.write(code)
		fw.close()
	}	
	
	static def getValidName(String name) {
		if(Keywords.blacklist.contains(name)) {
			return "__" + name
		}
		else
			return name
	}
	
	def static hasEEnums(GenPackage genPackage) {
		return  getEEnums(genPackage).isEmpty
	}
	
	def static getEEnums(GenPackage genPackage) {
		val ePack = genPackage.getEcorePackage
		return ePack.EClassifiers.filter[c | c instanceof EEnum].map[c | c as EEnum]
	}
	
	def static getClassifier(GenPackage genPack) {
		return genPack.getEcorePackage.EClassifiers
	}
	
	def static getEClasses(GenPackage genPack) {
		return getClassifier(genPack).filter(c | c instanceof EClass).map[c | c as EClass]
	}
	
		
	def static getImportPackages(EClass eClass) {
		var packages = eClass.EAllSuperTypes.map[c|c.EPackage].toSet
		packages.add(eClass.EPackage)
		return packages.map[p|getGenPack(p)]
	}
	
	def static getImportTypes(EClass eClass) {
		// estructural feature types
		val types = eClass.EAllStructuralFeatures.map[c|c.EType].filter[c|!c.EPackage.equals(EcorePackage.eINSTANCE)].toSet
		// add this eclass
		types.add(eClass)
		return types
	}
	
	
	def static getDependentGenPackages(GenPackage genPack) {
		val dependentPackages = new HashSet
		val ePackage = genPack.getEcorePackage
		for(classes : ePackage.EClassifiers.filter[c|c instanceof EClass].map[c|c as EClass]) {
			for(feature : classes.EAllStructuralFeatures) {
				if(feature instanceof EAttribute) {
					dependentPackages.add(feature.EType.EPackage)
				}
				if(feature instanceof EReference) {
					dependentPackages.add(feature.EType.EPackage)
				}
			}
			for(superclasses : classes.ESuperTypes) {
				dependentPackages.add(superclasses.EPackage)
			}
		}
		// remove the current package from this list
		dependentPackages.remove(ePackage)
		dependentPackages.remove(EcorePackage.eINSTANCE)
		var dependentGenPackages = dependentPackages.map[p|getGenPack(p)]
		return dependentGenPackages
	}
	
	def static getGenPack(EPackage ePackage) {
		var genModel = getGenModel(ePackage)
		if(genModel == null)
			return null
		return genModel.genPackages.get(0)
	}
	
	def static getSuperTypes(EClass eClass) {
		return '''«FOR s : eClass.ESuperTypes», «TemplateUtil.getFQName(s)»«ENDFOR»'''
	}
	
	def static getPackage(EClass eClass) {
		return eClass.EPackage
	}
	
	def static getOrIs(EStructuralFeature feature) {
		if(feature.EType.equals(EcorePackage.Literals.EBOOLEAN))
			return "is"
		else
			return "get"
	}
	
	def static getEDataTypes(GenPackage genPackage) {
		return getClassifier(genPackage).filter[c|c instanceof EDataType].map[c | c as EDataType]
	}
	
	def static getFQInterfaceName(GenPackage genPack, EClass eClass) {
		return org.emoflon.smartemf.templates.util.TemplateUtil.getInterfaceSuffix(genPack) + "." + eClass.name
	}
	
	def static getFQImplName(GenPackage genPack, EClass eClass) {
		return org.emoflon.smartemf.templates.util.TemplateUtil.getImplSuffix(genPack) + "." + eClass.name
	}
	
	def static getFactoryName(GenPackage genPack) {
		return genPack.getEcorePackage.name.toFirstUpper + "Factory"
	}
	
}