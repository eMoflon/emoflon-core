package org.moflon.smartemf.creators.templates

import java.io.File
import java.io.FileWriter
import java.util.HashMap
import java.util.HashSet
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EcorePackage
import org.moflon.smartemf.EcoreGenmodelParser
import org.moflon.smartemf.creators.FileCreator
import org.moflon.smartemf.creators.templates.util.CodeFormattingUtil
import org.moflon.smartemf.creators.templates.util.PackageInformation
import org.moflon.smartemf.creators.templates.util.TemplateUtil

/**
 * Creator-class which generates the class-file for the SmartEMF-package-class.
 */
class PackageImplTemplate implements FileCreator{
	
	val PackageInformation e_pak
	var boolean isInitialized = false
	var String fqFileName
	val HashSet<EPackage> dependentPackages

	new(PackageInformation package_inspector, HashMap<EPackage,PackageInformation> e_pak_map, EcoreGenmodelParser gen_model, String generatedFileDir){
		e_pak = package_inspector
		dependentPackages = getDependentPackages()
	}
	
	def String createSrcCode() {
		return '''
		package «e_pak.get_package_declaration_name».impl;
		
		«FOR clazz : e_pak.get_all_eclasses_in_package»
		import «e_pak.get_package_declaration_name».«clazz.name»;
		«ENDFOR»
		«FOR clazz : e_pak.get_all_eenums_in_package»
		import «e_pak.get_package_declaration_name».«clazz.name»;
		«ENDFOR»
		
		import «e_pak.get_package_declaration_name».«e_pak.get_emf_e_package.name.toFirstUpper»Factory;
		import «e_pak.get_package_declaration_name».«e_pak.get_emf_e_package.name.toFirstUpper»Package;

		«FOR dependency : dependentPackages»
		import «TemplateUtil.getFQName(dependency)».«TemplateUtil.getPackageClassName(dependency)»;
		«ENDFOR»

		import org.eclipse.emf.ecore.EAttribute;
		import org.eclipse.emf.ecore.EClass;
		«IF !e_pak.get_all_eenums_in_package.empty»
		import org.eclipse.emf.ecore.EEnum;
		«ENDIF»
		import org.eclipse.emf.ecore.EPackage;
		import org.eclipse.emf.ecore.EReference;
		
		import org.eclipse.emf.ecore.impl.EPackageImpl;
		
		import org.moflon.smartemf.runtime.SmartPackageImpl;

		public class «e_pak.get_emf_e_package.name.toFirstUpper»PackageImpl extends SmartPackageImpl
				implements «e_pak.get_emf_e_package.name.toFirstUpper»Package {
					
			«FOR clazz : e_pak.get_all_eclasses_in_package»
				private EClass «clazz.name.toFirstLower»EClass = null;
				«FOR feature : clazz.EStructuralFeatures»
				private «IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF» «clazz.name.toFirstLower»_«feature.name.toFirstLower»«IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF» = null;
				«ENDFOR»
			«ENDFOR»
			
			«FOR clazz : e_pak.get_all_eenums_in_package»
				private EEnum «clazz.name.toFirstLower»EEnum = null;
			«ENDFOR»

			private «e_pak.get_emf_e_package.name.toFirstUpper»PackageImpl() {
				super(eNS_URI, «e_pak.get_emf_e_package.name.toFirstUpper»Factory.eINSTANCE);
			}
		
			private static boolean isRegistered = false;
			private boolean isCreated = false;
			private boolean isInitialized = false;
		
			public static «e_pak.get_emf_e_package.name.toFirstUpper»Package init() {
				if (isRegistered)
					return («e_pak.get_emf_e_package.name.toFirstUpper»Package) EPackage.Registry.INSTANCE
							.getEPackage(«e_pak.get_emf_e_package.name.toFirstUpper»Package.eNS_URI);
		
				// Obtain or create and register package
				Object registered«e_pak.get_emf_e_package.name.toFirstUpper»Package = EPackage.Registry.INSTANCE.get(eNS_URI);
				«e_pak.get_emf_e_package.name.toFirstUpper»PackageImpl the«e_pak.get_emf_e_package.name.toFirstUpper»Package = registered«e_pak.get_emf_e_package.name.toFirstUpper»Package instanceof «e_pak.get_emf_e_package.name.toFirstUpper»PackageImpl
						? («e_pak.get_emf_e_package.name.toFirstUpper»PackageImpl) registered«e_pak.get_emf_e_package.name.toFirstUpper»Package
						: new «e_pak.get_emf_e_package.name.toFirstUpper»PackageImpl();
		
				isRegistered = true;
		
				// Create package meta-data objects
				the«e_pak.get_emf_e_package.name.toFirstUpper»Package.createPackageContents();
		
				// Initialize created meta-data
				the«e_pak.get_emf_e_package.name.toFirstUpper»Package.initializePackageContents();
				
				// Inject external references into foreign packages
				the«e_pak.get_emf_e_package.name.toFirstUpper»Package.injectExternalReferences();
		
				// Mark meta-data to indicate it can't be changed
				the«e_pak.get_emf_e_package.name.toFirstUpper»Package.freeze();
		
				// Update the registry and return the package
				EPackage.Registry.INSTANCE.put(«e_pak.get_emf_e_package.name.toFirstUpper»Package.eNS_URI,
						the«e_pak.get_emf_e_package.name.toFirstUpper»Package);
				return the«e_pak.get_emf_e_package.name.toFirstUpper»Package;
			}
		
			«FOR clazz : e_pak.get_all_eclasses_in_package»
			@Override
			public EClass get«clazz.name»() {
				return «clazz.name.toFirstLower»EClass;
			}
			«FOR feature : clazz.EStructuralFeatures»
			@Override
			public «IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF» get«clazz.name»_«feature.name.toFirstUpper»() {
			return «clazz.name.toFirstLower»_«feature.name.toFirstLower»«IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF»;	
			}
			«ENDFOR»
			«ENDFOR»
			
			«FOR clazz : e_pak.get_all_eenums_in_package»
			@Override
			public EEnum get«clazz.name.toFirstUpper»() {
				return «clazz.name.toFirstLower»EEnum;
			}
			«ENDFOR»
		
			/**
			 * <!-- begin-user-doc -->
			 * <!-- end-user-doc -->
			 * @generated
			 */
			@Override
			public «e_pak.get_emf_e_package.name.toFirstUpper»Factory get«e_pak.get_emf_e_package.name.toFirstUpper»Factory() {
				return («e_pak.get_emf_e_package.name.toFirstUpper»Factory) getEFactoryInstance();
			}
		
			public void createPackageContents() {
				if (isCreated)
					return;
				isCreated = true;
		
				// Create classes and their features
				«FOR clazz : e_pak.get_all_eclasses_in_package»
				«clazz.name.toFirstLower»EClass = createEClass(«TemplateUtil.getLiteral(clazz)»);
				«FOR feature : clazz.EStructuralFeatures»
				«IF feature instanceof EReference»createEReference«ELSE»createEAttribute«ENDIF»(«clazz.name.toFirstLower»EClass, «TemplateUtil.getLiteral(feature)»);
				«clazz.name.toFirstLower»_«feature.name.toFirstLower»«IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF» = («IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF») «clazz.name.toFirstLower»EClass.getEStructuralFeatures().get(«clazz.EStructuralFeatures.indexOf(feature)»);
				«ENDFOR»
				
				«ENDFOR»
				// Create enums
				«FOR clazz : e_pak.get_all_eenums_in_package»
				«clazz.name.toFirstLower»EEnum = createEEnum(«TemplateUtil.getLiteral(clazz)»);
				«ENDFOR»
			}
		
			public void initializePackageContents() {
				if (isInitialized)
					return;
				isInitialized = true;
		
				// Initialize package
				setName(eNAME);
				setNsPrefix(eNS_PREFIX);
				setNsURI(eNS_URI);
				
				// Obtain other dependent packages
				«FOR ePackage : dependentPackages»
				«TemplateUtil.getPackageClassName(ePackage)» the«ePackage.name»Package = «TemplateUtil.getPackageClassName(ePackage)».eINSTANCE;
				«ENDFOR»
		
				// Create type parameters
		
				// Set bounds for type parameters
		
				// Add supertypes to classes
				«FOR clazz : e_pak.get_all_eclasses_in_package»
				«FOR superClazz : clazz.EAllSuperTypes»
				«clazz.name.toFirstLower»EClass.getESuperTypes().add(«IF superClazz.EPackage.equals(e_pak.get_emf_e_package)»this«ELSE»«TemplateUtil.getPackageClassName(superClazz.EPackage)».eINSTANCE«ENDIF».get«superClazz.name.toFirstUpper»());
				«ENDFOR»
				
				«ENDFOR»
		
				// Initialize classes, features, and operations; add parameters
				«FOR clazz : e_pak.get_all_eclasses_in_package»
				initEClass(«clazz.name.toFirstLower»EClass, «clazz.name».class, "«clazz.name»", !IS_ABSTRACT, !IS_INTERFACE,
					IS_GENERATED_INSTANCE_CLASS);
				«FOR feature : clazz.EStructuralFeatures»
				«IF feature instanceof EReference»«val ref = feature as EReference»
				initEReference(get«clazz.name»_«ref.name.toFirstUpper»(), «IF ref.EType.EPackage.name.equals("ecore")»ecorePackage.get«ref.EType.name»()«ELSE»«getPackageName(ref)».get«ref.EType.name»()«ENDIF», «IF ref.EOpposite !== null»«getPackageName(ref)».get«ref.EType.name.toFirstUpper»_«ref.EOpposite.name.toFirstUpper»(),«ELSE» null,«ENDIF» 
					"«ref.name»", «(ref.defaultValue === null)?"null":ref.defaultValue», «ref.lowerBound», «ref.upperBound», «clazz.name».class, «(ref.isTransient)?"":"!"»IS_TRANSIENT, «(ref.isVolatile)?"":"!"»IS_VOLATILE, «(ref.isChangeable)?"":"!"»IS_CHANGEABLE, «(ref.isContainment)?"":"!"»IS_COMPOSITE, «(ref.isResolveProxies)?"":"!"»IS_RESOLVE_PROXIES,
					«(ref.isUnsettable)?"":"!"»IS_UNSETTABLE, «(ref.isUnique)?"":"!"»IS_UNIQUE, «(ref.isDerived)?"":"!"»IS_DERIVED, «(ref.isOrdered)?"":"!"»IS_ORDERED);
				«ELSE»«val atr = feature as EAttribute»
				initEAttribute(get«clazz.name»_«atr.name.toFirstUpper»(), «IF atr.EType.EPackage.name.equals("ecore")»ecorePackage.get«atr.EType.name»()«ELSE»«getPackageName(atr)».get«atr.EType.name.toFirstUpper»()«ENDIF»,
					"«atr.name»", «(atr.defaultValue === null)?"null":"\""+atr.defaultValue+"\""», «atr.lowerBound», «atr.upperBound», «clazz.name».class, «(atr.isTransient)?"":"!"»IS_TRANSIENT, «(atr.isVolatile)?"":"!"»IS_VOLATILE, «(atr.isChangeable)?"":"!"»IS_CHANGEABLE, «(atr.isUnsettable)?"":"!"»IS_UNSETTABLE, «(atr.isUnique)?"":"!"»IS_ID, IS_UNIQUE,
					«(atr.isDerived)?"":"!"»IS_DERIVED, «(atr.isOrdered)?"":"!"»IS_ORDERED);
				«ENDIF»
				«ENDFOR»
								
				«ENDFOR»
				
				// Initialize enums and add enum literals
				«FOR clazz : e_pak.get_all_eenums_in_package»
				initEEnum(«clazz.name.toFirstLower»EEnum, «clazz.name».class, "«clazz.name»");
				«FOR literal : clazz.ELiterals»
				addEEnumLiteral(«clazz.name.toFirstLower»EEnum, «clazz.EPackage.name».«clazz.name».«TemplateUtil.getLiteral(literal)»);
				«ENDFOR»
				«ENDFOR»
				
				// Create resource
				createResource(eNS_URI);
			}
		
		} //«e_pak.get_emf_e_package.name.toFirstUpper»PackageImpl
		
		'''
	}
	
	def getPackageName(EStructuralFeature feature) {
		if(feature.EType.EPackage.equals(e_pak.get_emf_e_package)) {
			return "this"
		}
		else {
			return '''the«feature.EType.EPackage.name»Package'''
		}
	}
	
	def getDependentPackages() {
		val dependentPackages = new HashSet
		val ePackage = e_pak.get_emf_e_package
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
		return dependentPackages
	}
	
	override initialize_creator(String fq_file_path) {
		fqFileName = fq_file_path
		isInitialized = true
	}
	
	override write_to_file() {
		if(!isInitialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
		
		var package_file = new File(fqFileName)
		package_file.getParentFile().mkdirs()
		var package_fw = new FileWriter(package_file , false)
		package_fw.write(CodeFormattingUtil.format(createSrcCode))
		package_fw.close()
	}

}
