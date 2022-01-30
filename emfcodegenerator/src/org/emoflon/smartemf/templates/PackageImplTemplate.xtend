package org.emoflon.smartemf.templates

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EReference
import org.emoflon.smartemf.templates.util.TemplateUtil

/**
 * Creator-class which generates the class-file for the SmartEMF-package-class.
 */
class PackageImplTemplate implements CodeTemplate{

	var GenPackage genPack
	var String path
	
	new(GenPackage genPack, String path) {
		this.genPack = genPack
		this.path = path
	}

	override createCode() {
		var code = '''
		package «TemplateUtil.getImplPrefix(genPack)»;
		
		«FOR clazz : TemplateUtil.getClassifier(genPack)»
		import «TemplateUtil.getFQName(clazz)»;
		«ENDFOR»
		
		import «TemplateUtil.getFactoryInterface(genPack)»;
		import «TemplateUtil.getMetadataPrefix(genPack)».«TemplateUtil.getPackageClassName(genPack)»;

		«FOR dependency : TemplateUtil.getDependentGenPackages(genPack)»
		import «TemplateUtil.getMetadataPrefix(genPack)».«TemplateUtil.getPackageClassName(dependency)»;
		«ENDFOR»

		import org.eclipse.emf.ecore.EAttribute;
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.emf.ecore.EEnum;
		import org.eclipse.emf.ecore.EDataType;
		import org.eclipse.emf.ecore.EPackage;
		import org.eclipse.emf.ecore.EReference;
		import org.eclipse.emf.ecore.EcorePackage;
		
		import org.eclipse.emf.ecore.impl.EPackageImpl;
		
		import org.emoflon.smartemf.runtime.SmartPackageImpl;

		public class «TemplateUtil.getPackageClassName(genPack)»Impl extends SmartPackageImpl
				implements «TemplateUtil.getPackageClassName(genPack)» {
					
			«FOR clazz : TemplateUtil.getEClasses(genPack)»
				private EClass «clazz.name.toFirstLower»EClass = null;
				«FOR feature : clazz.EStructuralFeatures»
					private «IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF» «clazz.name.toFirstLower»_«feature.name.toFirstLower»«IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF» = null;
				«ENDFOR»
			«ENDFOR»
			
			«FOR eenum : TemplateUtil.getEEnums(genPack)»
				private EEnum «eenum.name.toFirstLower»EEnum = null;
			«ENDFOR»
			
			«FOR datatype : TemplateUtil.getEDataTypes(genPack)»
				private EDataType «datatype.name.toFirstLower»EDataType = null;
			«ENDFOR»

			private «TemplateUtil.getPackageClassName(genPack)»Impl() {
				super(eNS_URI, «TemplateUtil.getFactoryInterface(genPack)».eINSTANCE);
			}
		
			private static boolean isRegistered = false;
			private boolean isCreated = false;
			private boolean isInitialized = false;
		
			public static «TemplateUtil.getPackageClassName(genPack)» init() {
				if (isRegistered)
					return («TemplateUtil.getPackageClassName(genPack)») EPackage.Registry.INSTANCE
							.getEPackage(«TemplateUtil.getPackageClassName(genPack)».eNS_URI);
		
				// Obtain or create and register package
				Object registered«TemplateUtil.getPackageClassName(genPack).toFirstUpper» = EPackage.Registry.INSTANCE.get(eNS_URI);
				«TemplateUtil.getPackageClassName(genPack)»Impl the«TemplateUtil.getPackageClassName(genPack)» = registered«TemplateUtil.getPackageClassName(genPack)» instanceof «TemplateUtil.getPackageClassName(genPack)»Impl
						? («TemplateUtil.getPackageClassName(genPack)»Impl) registered«TemplateUtil.getPackageClassName(genPack)»
						: new «TemplateUtil.getPackageClassName(genPack)»Impl();
		
				isRegistered = true;
		
				// Create package meta-data objects
				the«TemplateUtil.getPackageClassName(genPack)».createPackageContents();
		
				// Initialize created meta-data
				the«TemplateUtil.getPackageClassName(genPack)».initializePackageContents();
				
				// Inject internal eOpposites to unidirectional references
				the«TemplateUtil.getPackageClassName(genPack)».injectDynamicOpposites();
				
				// Inject external references into foreign packages
				the«TemplateUtil.getPackageClassName(genPack)».injectExternalReferences();
		
				// Mark meta-data to indicate it can't be changed
				the«TemplateUtil.getPackageClassName(genPack)».freeze();
		
				// Update the registry and return the package
				EPackage.Registry.INSTANCE.put(«TemplateUtil.getPackageClassName(genPack).toFirstUpper».eNS_URI,
						the«TemplateUtil.getPackageClassName(genPack)»);
						
				the«TemplateUtil.getPackageClassName(genPack).toFirstUpper».fetchDynamicEStructuralFeaturesOfSuperTypes();
				return the«TemplateUtil.getPackageClassName(genPack)»;
			}
		
			«FOR clazz : TemplateUtil.getEClasses(genPack)»
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
			
			«FOR eenum : TemplateUtil.getEEnums(genPack)»
				@Override
				public EEnum get«eenum.name.toFirstUpper»() {
					return «eenum.name.toFirstLower»EEnum;
				}
			«ENDFOR»
			
			«FOR datatype : TemplateUtil.getEDataTypes(genPack)»
				@Override
				public EDataType get«datatype.name.toFirstUpper»() {
					return «datatype.name.toFirstLower»EDataType;
				}
			«ENDFOR»
		
			/**
			 * <!-- begin-user-doc -->
			 * <!-- end-user-doc -->
			 * @generated
			 */
			@Override
			public «TemplateUtil.getFactoryInterface(genPack)» get«TemplateUtil.getFactoryName(genPack)»() {
				return («TemplateUtil.getFactoryInterface(genPack)») getEFactoryInstance();
			}
		
			public void createPackageContents() {
				if (isCreated)
					return;
				isCreated = true;
		
				// Create classes and their features
				«FOR clazz : TemplateUtil.getEClasses(genPack)»
					«clazz.name.toFirstLower»EClass = createEClass(«TemplateUtil.getLiteral(clazz)»);
					«FOR feature : clazz.EStructuralFeatures»
						«IF feature instanceof EReference»createEReference«ELSE»createEAttribute«ENDIF»(«clazz.name.toFirstLower»EClass, «TemplateUtil.getLiteral(feature)»);
						«clazz.name.toFirstLower»_«feature.name.toFirstLower»«IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF» = («IF feature instanceof EReference»EReference«ELSE»EAttribute«ENDIF») «clazz.name.toFirstLower»EClass.getEStructuralFeatures().get(«clazz.EStructuralFeatures.indexOf(feature)»);
					«ENDFOR»
					
				«ENDFOR»
				// Create enums
				«FOR clazz : TemplateUtil.getEEnums(genPack)»
					«clazz.name.toFirstLower»EEnum = createEEnum(«TemplateUtil.getLiteral(clazz)»);
				«ENDFOR»
				
				// Create data types
				«FOR datatype : TemplateUtil.getEDataTypes(genPack)»
					«datatype.name.toFirstLower»EDataType = createEDataType(«TemplateUtil.getLiteral(datatype)»);
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
				«FOR genPackage : TemplateUtil.getDependentGenPackages(genPack)»
					«TemplateUtil.getPackageClassName(genPack)» the«TemplateUtil.getPackageClassName(genPack)»Package = «TemplateUtil.getPackageClassName(genPack)».eINSTANCE;
				«ENDFOR»
		
				// Create type parameters
		
				// Set bounds for type parameters
		
				// Add supertypes to classes
				«FOR clazz : TemplateUtil.getEClasses(genPack)»
					«FOR superClazz : clazz.EAllSuperTypes»
						«clazz.name.toFirstLower»EClass.getESuperTypes().add(«IF superClazz.EPackage.equals(genPack.getEcorePackage)»this«ELSE»«TemplateUtil.getPackageClassName(superClazz.EPackage)».eINSTANCE«ENDIF».get«superClazz.name.toFirstUpper»());
					«ENDFOR»
					
				«ENDFOR»
		
				// Initialize classes, features, and operations; add parameters
				«FOR clazz : TemplateUtil.getEClasses(genPack)»
					initEClass(«clazz.name.toFirstLower»EClass, «clazz.name».class, "«clazz.name»", !IS_ABSTRACT, !IS_INTERFACE,
						IS_GENERATED_INSTANCE_CLASS);
					«FOR feature : clazz.EStructuralFeatures»
						«IF feature instanceof EReference»«val ref = feature as EReference»
						initEReference(get«clazz.name»_«ref.name.toFirstUpper»(), «IF ref.EType.EPackage.name.equals("ecore")»ecorePackage.get«ref.EType.name»()«ELSE»«TemplateUtil.getPackageClassName(ref)».get«ref.EType.name»()«ENDIF», «IF ref.EOpposite !== null»«TemplateUtil.getPackageClassName(ref)».get«ref.EType.name.toFirstUpper»_«ref.EOpposite.name.toFirstUpper»(),«ELSE» null,«ENDIF» 
							"«ref.name»", «(ref.defaultValue === null)?"null":ref.defaultValue», «ref.lowerBound», «ref.upperBound», «clazz.name».class, «(ref.isTransient)?"":"!"»IS_TRANSIENT, «(ref.isVolatile)?"":"!"»IS_VOLATILE, «(ref.isChangeable)?"":"!"»IS_CHANGEABLE, «(ref.isContainment)?"":"!"»IS_COMPOSITE, «(ref.isResolveProxies)?"":"!"»IS_RESOLVE_PROXIES,
							«(ref.isUnsettable)?"":"!"»IS_UNSETTABLE, «(ref.isUnique)?"":"!"»IS_UNIQUE, «(ref.isDerived)?"":"!"»IS_DERIVED, «(ref.isOrdered)?"":"!"»IS_ORDERED);
						«ELSE»«val atr = feature as EAttribute»
						initEAttribute(get«clazz.name»_«atr.name.toFirstUpper»(), «IF atr.EType.EPackage.name.equals("ecore")»ecorePackage.get«atr.EType.name»()«ELSE»«TemplateUtil.getPackageClassName(atr)».get«atr.EType.name.toFirstUpper»()«ENDIF»,
							"«atr.name»", «(atr.defaultValue === null)?"null":"\""+atr.defaultValue+"\""», «atr.lowerBound», «atr.upperBound», «clazz.name».class, «(atr.isTransient)?"":"!"»IS_TRANSIENT, «(atr.isVolatile)?"":"!"»IS_VOLATILE, «(atr.isChangeable)?"":"!"»IS_CHANGEABLE, «(atr.isUnsettable)?"":"!"»IS_UNSETTABLE, «(atr.isUnique)?"":"!"»IS_ID, IS_UNIQUE,
							«(atr.isDerived)?"":"!"»IS_DERIVED, «(atr.isOrdered)?"":"!"»IS_ORDERED);
						«ENDIF»
					«ENDFOR»
					
				«ENDFOR»
				
				// Initialize enums and add enum literals
				«FOR eenum : TemplateUtil.getEEnums(genPack)»
					initEEnum(«eenum.name.toFirstLower»EEnum, «eenum.name».class, "«eenum.name»");
					«FOR literal : eenum.ELiterals»
						addEEnumLiteral(«eenum.name.toFirstLower»EEnum, «eenum.EPackage.name».«eenum.name».«TemplateUtil.getLiteral(literal)»);
					«ENDFOR»
				«ENDFOR»
				
				// Initialize data types
				«FOR datatype : TemplateUtil.getEDataTypes(genPack)»
					initEDataType(«datatype.name.toFirstLower»EDataType, «datatype.instanceClassName».class, "«datatype.name»", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
				«ENDFOR»
				
				// Create resource
				createResource(eNS_URI);
			}
		
		} 
		
		'''
		
		TemplateUtil.writeToFile(path + TemplateUtil.getImplPrefix(genPack).replace(".", "/") + "/" + TemplateUtil.getPackageClassName(genPack) + "Impl.java", code);
		
	}
}
