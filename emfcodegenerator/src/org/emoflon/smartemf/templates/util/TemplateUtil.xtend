package org.emoflon.smartemf.templates.util

import java.io.File
import java.io.FileWriter
import java.util.Collection
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedList
import java.util.Map
import org.eclipse.core.resources.IWorkspaceRoot
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.emf.codegen.ecore.genmodel.GenModel
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory
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
import org.eclipse.emf.ecore.InternalEObject
import org.eclipse.emf.ecore.impl.EPackageImpl
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.EcoreUtil
import org.moflon.core.utilities.ProxyResolver

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
		for(genpkg : genModel.genPackages) {
			val pkgURI = genpkg.NSURI
			val genModelURI = pkgURI.replace(".ecore", ".genmodel")
			val resourceURI = genModelURI.replace("platform:/", "platform:/resource/")
			uriStringToGenModelMap.put(resourceURI, genModel)
		}
	}
	
	static def getGenModel(EPackage ePack) {
		var ePackage = ePack
		while(ePackage.ESuperPackage != null) {
			ePackage = ePackage.ESuperPackage
		}
		val uri = EcoreUtil.getURI(ePackage).trimFragment
		val platformURI = uri.toString
		
		// create resource and plugin uris if possible
		var pkgURI = ePackage.nsURI
		if(!pkgURI.contains("platform:/") && platformURI.contains("platform:/")) {
			pkgURI = platformURI
		}
		var genModelURI = pkgURI.replace(".ecore", ".genmodel")
		var resourceURI = ""
		var pluginURI = ""
		if(genModelURI.contains("/resource/")) {
			resourceURI = genModelURI
			pluginURI = genModelURI.replace("/resource/", "/plugin/")
		}
		if(genModelURI.contains("/plugin/")) {
			pluginURI = genModelURI
			resourceURI = genModelURI.replace("/plugin/", "/resource/")
		}
		
		if(!genModelURI.contains("/resource/") && !genModelURI.contains("/plugin/")) {
			resourceURI = genModelURI.replace("platform:/", "platform:/resource/")
			pluginURI = genModelURI.replace("platform:/", "platform:/plugin/")
		}
		
		// try to load models from resource
		var genModel = loadGenModelFromResource(platformURI.replace(".ecore", ".genmodel"))
		if(genModel != null) {
			return genModel;
		}
		
		genModel = loadGenModelFromResource(genModelURI)
		if(genModel != null) {
			return genModel;
		}
		genModel = loadGenModelFromResource(resourceURI)
		if(genModel != null) {
			return genModel;
		}
 		genModel = loadGenModelFromResource(pluginURI)
		if(genModel != null) {
			return genModel;
		}
	
		
		// if loading was unsuccesful -> crawGenModel from file or workspace
		genModel = crawlGenModel(ePackage)
		if(genModel != null) {
			uriStringToGenModelMap.put(resourceURI, genModel)
			uriStringToGenModelMap.put(pluginURI, genModel)
			uriStringToGenModelMap.put(ePackage.nsURI, genModel)
			return genModel
		}
		
		// if not found until here -> create dummy genPackage and genmodels
		genModel = GenModelFactory.eINSTANCE.createGenModel();
		var genPack = GenModelFactory.eINSTANCE.createGenPackage();
		genModel.genPackages.add(genPack)
		genPack.ecorePackage = ePackage
		
		// save dummy genmodels in map
		uriStringToGenModelMap.put(resourceURI, genModel)
		uriStringToGenModelMap.put(pluginURI, genModel)
		
		return genModel
	}
	
	def static loadGenModelFromResource(String uri) {
		// try to find the cached value if it exists
		if(uriStringToGenModelMap.containsKey(uri)) {
			return uriStringToGenModelMap.get(uri)
		}
		
		// load resource
		val rs = new ResourceSetImpl
		val resource = rs.createResource(URI.createURI(uri))
		try {
			resource.load(null)
			if(resource.contents.isEmpty)
				return null
				
			val content = resource.contents.get(0)
			if(content instanceof GenModel) {
				// get rid of eproxies
				EcoreUtil.resolveAll(content)
				
				val genPkg = content.genPackages.get(0)
				if(genPkg.getEcorePackage.eIsProxy) {
					val intEPkg = genPkg.getEcorePackage as InternalEObject
					genPkg.ecorePackage = ProxyResolver.resolvePackage(intEPkg.eProxyURI.trimFragment)
					println()
				}
				
				// save result for later
				uriStringToGenModelMap.put(uri, content)
				return content
			}
		}
		catch(Exception e) {
		}
	}
	
	def static crawlGenModel(EPackage ePackage) {
		if(ePackage.class.equals(EPackageImpl)) {
		}
		
		// get source path of package
		var path = ePackage.class.protectionDomain.codeSource.location.path
		var packageFile = new File(path)
		if(!packageFile.exists) {
			return null
		}
		var projectFolder = packageFile.parentFile
		
		// if file is a jar -> extract genmodels from it and search for epackage
		if(packageFile.isFile && packageFile.name.endsWith(".jar")) {
			val root = ResourcesPlugin.getWorkspace().getRoot();			
			val tmpPath = root.getLocation().toPortableString() + "/.tmp/";
			
			var jarExtractor = new JarExtractor(tmpPath, packageFile.absolutePath)
			var genModels = jarExtractor.extractGenModels
			for(genModel : genModels) {
				if(ePackage.nsURI.equals(genModel.genPackages.get(0).getEcorePackage.nsURI)) {
					// if genmodel has been found -> replace genModels ePackages with our one
					genModel.genPackages.get(0).ecorePackage = ePackage
					return genModel
				}
			}
			return null
		}

		// project folder should contain a META-INF folder which identifies it
		var projectFound = false
		while(projectFolder != null && projectFolder.exists && !projectFound) {
			for(file : projectFolder.listFiles) {
				if(file.name == "META-INF") {
					projectFound = true
				}
			}
			projectFolder = projectFolder.parentFile
		}
		
		// if META-INF was not found -> exit
		if(projectFolder == null || !projectFolder.exists) {
			return null		
		}
		
		// we assume that there is a model folder at the root of the project
		var File modelFolder = null
		for(file : projectFolder.listFiles) {
			if(file.name.equals("model")) {
				modelFolder = file
			}
		}
		if(modelFolder == null) {
			return null
		}
		
		// if modelFolder was found -> search for genModels there
		return crawlGenModel(ePackage, modelFolder) 
	}
	
	static def GenModel crawlGenModel(EPackage ePackage, File modelFolder) {
		var GenModel genModel = null
		// search for genmodels recursively through all subfolders of /model
		for(file : modelFolder.listFiles) {
			if(file.directory) {
				genModel = crawlGenModel(ePackage, file);
				if(genModel != null) {
					return genModel
				}
			}
			else {
				// if genmodel was found -> load it and check if the uris match
				if(file.name.endsWith(".genmodel")) {
					var uri = URI.createFileURI(file.absolutePath);
					var rs = new ResourceSetImpl
					var genModelResource = rs.createResource(uri)
					try {
						genModelResource.load(null)
						var content = genModelResource.contents.get(0)
						if(content instanceof GenModel) {
							genModel = content as GenModel
							var ecorePackage = genModel.ecoreGenPackage
							if(ecorePackage.equals(ePackage)) {
								return genModel
							}
							if(ePackage.nsURI.equals(ecorePackage.NSURI)) {
								return genModel
							}
						}
					}
					catch(Exception e) {
						e.printStackTrace
					}
				}	
			}
		}
		return null
	}
	
	static def getFQName(GenPackage genPackage) {
//		return getFQName(genPackage.getEcorePackage)
		return getInterfaceSuffix(genPackage)
	}
	
	static def getFQName(EPackage ePackage) {
		val genPackage = getGenPack(ePackage)
		return getFQName(genPackage)
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
		var fqName = TemplateUtil.getMetadataSuffix(genPackage)
		fqName += "." + genPackage.getEcorePackage.name.toFirstUpper + "Factory"
		return fqName
	}
	
	def static getFactoryImpl(GenPackage genPackage) {
		var fqName = TemplateUtil.getImplSuffix(genPackage)
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
		return !getEEnums(genPackage).isEmpty
	}
	
	def static getEEnums(GenPackage genPackage) {
		val ePack = genPackage.getEcorePackage
		return ePack.EClassifiers.filter[c | c instanceof EEnum].map[c | c as EEnum]
	}
	
	def static getClassifier(GenPackage genPack) {
		return genPack.getEcorePackage.EClassifiers
	}
	
	def static getEClasses(GenPackage genPack) {
		val classes = getClassifier(genPack).filter(c | c instanceof EClass).map[c | c as EClass]
		return classes
	}
	
	def static getSuperEClasses(GenPackage genPack) {
		val typeToSuperTypes = new HashMap
		for(c : getEClasses(genPack)) {
			val sTypes = c.EAllSuperTypes.map[ProxyResolver.resolve(c) as EClass]
			typeToSuperTypes.put(c, sTypes)
		}
		return typeToSuperTypes
	}
	
		
	def static getImportPackages(EClass eClass) {
		var packages = eClass.EAllSuperTypes.map[c|ProxyResolver.resolve(c)].map[c|c.EPackage].toSet
		packages.add(ProxyResolver.resolve(eClass).EPackage)
		val genpackages = packages.map[p|getGenPack(p)]
		return genpackages
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
				val resClass = ProxyResolver.resolve(superclasses)
				dependentPackages.add(resClass.EPackage)
			}
		}
		// remove the current package from this list
		dependentPackages.remove(ePackage)
		dependentPackages.remove(EcorePackage.eINSTANCE)
		var dependentGenPackages = dependentPackages.map[p|getGenPack(p)].toSet
		return dependentGenPackages
	}
	
	def static getGenPack(EPackage ePackage) {
		var genModel = getGenModel(ePackage)
		if(genModel == null)
			return null
		var genPack = searchGenPackage(genModel.genPackages, ePackage)
		return genPack
	}
	
	def static GenPackage searchGenPackage(Collection<GenPackage> genpacks, EPackage epack) {
		for(genpack : genpacks) {
			if(epack.nsURI.equals(genpack.getEcorePackage().nsURI)) {
				return genpack;
			}
			val foundGenPack = searchGenPackage(genpack.subGenPackages, epack);
			if(foundGenPack != null) {
				return foundGenPack
			}
		}
		return null
	}
	
	def static getSuperTypes(EClass eClass) {
		return '''«FOR s : eClass.ESuperTypes», «TemplateUtil.getFQName(ProxyResolver.resolve(s))»«ENDFOR»'''
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
		return getClassifier(genPackage).filter[c|c instanceof EDataType && !(c instanceof EEnum)].map[c | c as EDataType]
	}
	
	def static getFQInterfaceName(GenPackage genPack, EClass eClass) {
		return TemplateUtil.getInterfaceSuffix(genPack) + "." + eClass.name
	}
	
	def static getFQImplName(GenPackage genPack, EClass eClass) {
		return TemplateUtil.getImplSuffix(genPack) + "." + eClass.name
	}
	
	def static getFactoryName(GenPackage genPack) {
		return genPack.getEcorePackage.name.toFirstUpper + "Factory"
	}
	
	def static getFeatureName(EStructuralFeature feature) {
		if(feature.name.toLowerCase.equals("class")) {
			return "Class_"
		}
		return feature.name.toFirstUpper
	}
	
	def static getClassName(EClassifier ec) {
		if(ec.name.toLowerCase.equals("class")) {
			return "Class_"
		}
		return ec.name
	}
	
}