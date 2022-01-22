/**
 */
package org.moflon.core.propertycontainer.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.moflon.core.propertycontainer.AdditionalDependencies;
import org.moflon.core.propertycontainer.AdditionalUsedGenPackages;
import org.moflon.core.propertycontainer.BuildMode;
import org.moflon.core.propertycontainer.CodeGenerator;
import org.moflon.core.propertycontainer.Dependencies;
import org.moflon.core.propertycontainer.FactoryMappings;
import org.moflon.core.propertycontainer.ImportMappings;
import org.moflon.core.propertycontainer.MetaModelProject;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
import org.moflon.core.propertycontainer.PropertiesMapping;
import org.moflon.core.propertycontainer.PropertiesValue;
import org.moflon.core.propertycontainer.PropertycontainerFactory;
import org.moflon.core.propertycontainer.PropertycontainerPackage;
import org.moflon.core.propertycontainer.ReplaceGenModel;
import org.moflon.core.propertycontainer.TGGBuildMode;
import org.moflon.core.propertycontainer.UsedCodeGen;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PropertycontainerPackageImpl extends EPackageImpl implements PropertycontainerPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dependenciesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass propertiesValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass additionalUsedGenPackagesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass importMappingsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass tggBuildModeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass factoryMappingsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass moflonPropertiesContainerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass replaceGenModelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass propertiesMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass additionalDependenciesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metaModelProjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass codeGeneratorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum buildModeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum usedCodeGenEEnum = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private PropertycontainerPackageImpl() {
		super(eNS_URI, PropertycontainerFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link PropertycontainerPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static PropertycontainerPackage init() {
		if (isInited)
			return (PropertycontainerPackage) EPackage.Registry.INSTANCE.getEPackage(PropertycontainerPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredPropertycontainerPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		PropertycontainerPackageImpl thePropertycontainerPackage = registeredPropertycontainerPackage instanceof PropertycontainerPackageImpl
				? (PropertycontainerPackageImpl) registeredPropertycontainerPackage
				: new PropertycontainerPackageImpl();

		isInited = true;

		// Create package meta-data objects
		thePropertycontainerPackage.createPackageContents();

		// Initialize created meta-data
		thePropertycontainerPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		thePropertycontainerPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(PropertycontainerPackage.eNS_URI, thePropertycontainerPackage);
		return thePropertycontainerPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDependencies() {
		return dependenciesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDependencies_Description() {
		return (EAttribute) dependenciesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPropertiesValue() {
		return propertiesValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPropertiesValue_Value() {
		return (EAttribute) propertiesValueEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAdditionalUsedGenPackages() {
		return additionalUsedGenPackagesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAdditionalUsedGenPackages_Description() {
		return (EAttribute) additionalUsedGenPackagesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getImportMappings() {
		return importMappingsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getImportMappings_Description() {
		return (EAttribute) importMappingsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTGGBuildMode() {
		return tggBuildModeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTGGBuildMode_BuildMode() {
		return (EAttribute) tggBuildModeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTGGBuildMode_Description() {
		return (EAttribute) tggBuildModeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFactoryMappings() {
		return factoryMappingsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFactoryMappings_Description() {
		return (EAttribute) factoryMappingsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMoflonPropertiesContainer() {
		return moflonPropertiesContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMoflonPropertiesContainer_Dependencies() {
		return (EReference) moflonPropertiesContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMoflonPropertiesContainer_FactoryMappings() {
		return (EReference) moflonPropertiesContainerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMoflonPropertiesContainer_AdditionalDependencies() {
		return (EReference) moflonPropertiesContainerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMoflonPropertiesContainer_MetaModelProject() {
		return (EReference) moflonPropertiesContainerEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMoflonPropertiesContainer_ReplaceGenModel() {
		return (EReference) moflonPropertiesContainerEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMoflonPropertiesContainer_TGGBuildMode() {
		return (EReference) moflonPropertiesContainerEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMoflonPropertiesContainer_ImportMappings() {
		return (EReference) moflonPropertiesContainerEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMoflonPropertiesContainer_AdditionalUsedGenPackages() {
		return (EReference) moflonPropertiesContainerEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMoflonPropertiesContainer_Description() {
		return (EAttribute) moflonPropertiesContainerEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMoflonPropertiesContainer_ProjectName() {
		return (EAttribute) moflonPropertiesContainerEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMoflonPropertiesContainer_CodeGenerator() {
		return (EReference) moflonPropertiesContainerEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getReplaceGenModel() {
		return replaceGenModelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getReplaceGenModel_Bool() {
		return (EAttribute) replaceGenModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getReplaceGenModel_Description() {
		return (EAttribute) replaceGenModelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPropertiesMapping() {
		return propertiesMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPropertiesMapping_Key() {
		return (EAttribute) propertiesMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPropertiesMapping_Value() {
		return (EAttribute) propertiesMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAdditionalDependencies() {
		return additionalDependenciesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAdditionalDependencies_Description() {
		return (EAttribute) additionalDependenciesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMetaModelProject() {
		return metaModelProjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMetaModelProject_Description() {
		return (EAttribute) metaModelProjectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMetaModelProject_MetaModelProjectName() {
		return (EAttribute) metaModelProjectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCodeGenerator() {
		return codeGeneratorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodeGenerator_Generator() {
		return (EAttribute) codeGeneratorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodeGenerator_Enforced() {
		return (EAttribute) codeGeneratorEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getBuildMode() {
		return buildModeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getUsedCodeGen() {
		return usedCodeGenEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertycontainerFactory getPropertycontainerFactory() {
		return (PropertycontainerFactory) getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated)
			return;
		isCreated = true;

		// Create classes and their features
		dependenciesEClass = createEClass(DEPENDENCIES);
		createEAttribute(dependenciesEClass, DEPENDENCIES__DESCRIPTION);

		propertiesValueEClass = createEClass(PROPERTIES_VALUE);
		createEAttribute(propertiesValueEClass, PROPERTIES_VALUE__VALUE);

		additionalUsedGenPackagesEClass = createEClass(ADDITIONAL_USED_GEN_PACKAGES);
		createEAttribute(additionalUsedGenPackagesEClass, ADDITIONAL_USED_GEN_PACKAGES__DESCRIPTION);

		importMappingsEClass = createEClass(IMPORT_MAPPINGS);
		createEAttribute(importMappingsEClass, IMPORT_MAPPINGS__DESCRIPTION);

		tggBuildModeEClass = createEClass(TGG_BUILD_MODE);
		createEAttribute(tggBuildModeEClass, TGG_BUILD_MODE__BUILD_MODE);
		createEAttribute(tggBuildModeEClass, TGG_BUILD_MODE__DESCRIPTION);

		factoryMappingsEClass = createEClass(FACTORY_MAPPINGS);
		createEAttribute(factoryMappingsEClass, FACTORY_MAPPINGS__DESCRIPTION);

		moflonPropertiesContainerEClass = createEClass(MOFLON_PROPERTIES_CONTAINER);
		createEReference(moflonPropertiesContainerEClass, MOFLON_PROPERTIES_CONTAINER__DEPENDENCIES);
		createEReference(moflonPropertiesContainerEClass, MOFLON_PROPERTIES_CONTAINER__FACTORY_MAPPINGS);
		createEReference(moflonPropertiesContainerEClass, MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_DEPENDENCIES);
		createEReference(moflonPropertiesContainerEClass, MOFLON_PROPERTIES_CONTAINER__META_MODEL_PROJECT);
		createEReference(moflonPropertiesContainerEClass, MOFLON_PROPERTIES_CONTAINER__REPLACE_GEN_MODEL);
		createEReference(moflonPropertiesContainerEClass, MOFLON_PROPERTIES_CONTAINER__TGG_BUILD_MODE);
		createEReference(moflonPropertiesContainerEClass, MOFLON_PROPERTIES_CONTAINER__IMPORT_MAPPINGS);
		createEReference(moflonPropertiesContainerEClass, MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_USED_GEN_PACKAGES);
		createEAttribute(moflonPropertiesContainerEClass, MOFLON_PROPERTIES_CONTAINER__DESCRIPTION);
		createEAttribute(moflonPropertiesContainerEClass, MOFLON_PROPERTIES_CONTAINER__PROJECT_NAME);
		createEReference(moflonPropertiesContainerEClass, MOFLON_PROPERTIES_CONTAINER__CODE_GENERATOR);

		replaceGenModelEClass = createEClass(REPLACE_GEN_MODEL);
		createEAttribute(replaceGenModelEClass, REPLACE_GEN_MODEL__BOOL);
		createEAttribute(replaceGenModelEClass, REPLACE_GEN_MODEL__DESCRIPTION);

		propertiesMappingEClass = createEClass(PROPERTIES_MAPPING);
		createEAttribute(propertiesMappingEClass, PROPERTIES_MAPPING__KEY);
		createEAttribute(propertiesMappingEClass, PROPERTIES_MAPPING__VALUE);

		additionalDependenciesEClass = createEClass(ADDITIONAL_DEPENDENCIES);
		createEAttribute(additionalDependenciesEClass, ADDITIONAL_DEPENDENCIES__DESCRIPTION);

		metaModelProjectEClass = createEClass(META_MODEL_PROJECT);
		createEAttribute(metaModelProjectEClass, META_MODEL_PROJECT__DESCRIPTION);
		createEAttribute(metaModelProjectEClass, META_MODEL_PROJECT__META_MODEL_PROJECT_NAME);

		codeGeneratorEClass = createEClass(CODE_GENERATOR);
		createEAttribute(codeGeneratorEClass, CODE_GENERATOR__GENERATOR);
		createEAttribute(codeGeneratorEClass, CODE_GENERATOR__ENFORCED);

		// Create enums
		buildModeEEnum = createEEnum(BUILD_MODE);
		usedCodeGenEEnum = createEEnum(USED_CODE_GEN);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized)
			return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		dependenciesEClass.getESuperTypes().add(this.getPropertiesValue());
		additionalUsedGenPackagesEClass.getESuperTypes().add(this.getPropertiesValue());
		importMappingsEClass.getESuperTypes().add(this.getPropertiesMapping());
		factoryMappingsEClass.getESuperTypes().add(this.getPropertiesMapping());
		additionalDependenciesEClass.getESuperTypes().add(this.getPropertiesValue());

		// Initialize classes, features, and operations; add parameters
		initEClass(dependenciesEClass, Dependencies.class, "Dependencies", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDependencies_Description(), ecorePackage.getEString(), "description",
				"[Automatically derived dependencies of this project]", 0, 1, Dependencies.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(propertiesValueEClass, PropertiesValue.class, "PropertiesValue", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPropertiesValue_Value(), ecorePackage.getEString(), "value", null, 1, 1,
				PropertiesValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(additionalUsedGenPackagesEClass, AdditionalUsedGenPackages.class, "AdditionalUsedGenPackages",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAdditionalUsedGenPackages_Description(), ecorePackage.getEString(), "description",
				"[Used to add additional GenPackages for code generation]", 1, 1, AdditionalUsedGenPackages.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(importMappingsEClass, ImportMappings.class, "ImportMappings", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getImportMappings_Description(), ecorePackage.getEString(), "description",
				"[Used to correct prefixes in imports for code generation]", 1, 1, ImportMappings.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(tggBuildModeEClass, TGGBuildMode.class, "TGGBuildMode", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTGGBuildMode_BuildMode(), this.getBuildMode(), "buildMode", "BuildMode.ALL", 1, 1,
				TGGBuildMode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getTGGBuildMode_Description(), ecorePackage.getEString(), "description",
				"[Controls which TGG operationalizations are generated]", 0, 1, TGGBuildMode.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(factoryMappingsEClass, FactoryMappings.class, "FactoryMappings", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getFactoryMappings_Description(), ecorePackage.getEString(), "description",
				"[Used to correct the name of a Factory if it does not comply with our normal naming conventions]", 1,
				1, FactoryMappings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(moflonPropertiesContainerEClass, MoflonPropertiesContainer.class, "MoflonPropertiesContainer",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getMoflonPropertiesContainer_Dependencies(), this.getDependencies(), null, "dependencies", null,
				0, -1, MoflonPropertiesContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMoflonPropertiesContainer_FactoryMappings(), this.getFactoryMappings(), null,
				"factoryMappings", null, 0, -1, MoflonPropertiesContainer.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMoflonPropertiesContainer_AdditionalDependencies(), this.getAdditionalDependencies(), null,
				"additionalDependencies", null, 0, -1, MoflonPropertiesContainer.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMoflonPropertiesContainer_MetaModelProject(), this.getMetaModelProject(), null,
				"metaModelProject", null, 1, 1, MoflonPropertiesContainer.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMoflonPropertiesContainer_ReplaceGenModel(), this.getReplaceGenModel(), null,
				"replaceGenModel", null, 1, 1, MoflonPropertiesContainer.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMoflonPropertiesContainer_TGGBuildMode(), this.getTGGBuildMode(), null, "tGGBuildMode", null,
				1, 1, MoflonPropertiesContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMoflonPropertiesContainer_ImportMappings(), this.getImportMappings(), null, "importMappings",
				null, 0, -1, MoflonPropertiesContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMoflonPropertiesContainer_AdditionalUsedGenPackages(), this.getAdditionalUsedGenPackages(),
				null, "additionalUsedGenPackages", null, 0, -1, MoflonPropertiesContainer.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEAttribute(getMoflonPropertiesContainer_Description(), ecorePackage.getEString(), "description",
				"[Properties to configure code generation]", 1, 1, MoflonPropertiesContainer.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getMoflonPropertiesContainer_ProjectName(), ecorePackage.getEString(), "projectName", null, 1, 1,
				MoflonPropertiesContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMoflonPropertiesContainer_CodeGenerator(), this.getCodeGenerator(), null, "codeGenerator",
				null, 1, 1, MoflonPropertiesContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(replaceGenModelEClass, ReplaceGenModel.class, "ReplaceGenModel", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getReplaceGenModel_Bool(), ecorePackage.getEBoolean(), "bool", "true", 1, 1,
				ReplaceGenModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getReplaceGenModel_Description(), ecorePackage.getEString(), "description",
				"[Set to false if you wish to maintain the GenModel in the project yourself]", 1, 1,
				ReplaceGenModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(propertiesMappingEClass, PropertiesMapping.class, "PropertiesMapping", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPropertiesMapping_Key(), ecorePackage.getEString(), "key", null, 1, 1,
				PropertiesMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getPropertiesMapping_Value(), ecorePackage.getEString(), "value", null, 1, 1,
				PropertiesMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(additionalDependenciesEClass, AdditionalDependencies.class, "AdditionalDependencies", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAdditionalDependencies_Description(), ecorePackage.getEString(), "description",
				"[Used to add additional dependencies required for code generation]", 1, 1,
				AdditionalDependencies.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(metaModelProjectEClass, MetaModelProject.class, "MetaModelProject", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMetaModelProject_Description(), ecorePackage.getEString(), "description",
				"[Name of the corresponding metamodel project in the current workspace]", 1, 1, MetaModelProject.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getMetaModelProject_MetaModelProjectName(), ecorePackage.getEString(), "MetaModelProjectName",
				null, 1, 1, MetaModelProject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(codeGeneratorEClass, CodeGenerator.class, "CodeGenerator", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCodeGenerator_Generator(), this.getUsedCodeGen(), "generator", null, 0, 1,
				CodeGenerator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodeGenerator_Enforced(), ecorePackage.getEBoolean(), "enforced", "false", 0, 1,
				CodeGenerator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(buildModeEEnum, BuildMode.class, "BuildMode");
		addEEnumLiteral(buildModeEEnum, BuildMode.ALL);
		addEEnumLiteral(buildModeEEnum, BuildMode.SIMULTANEOUS);
		addEEnumLiteral(buildModeEEnum, BuildMode.BACKWARD);
		addEEnumLiteral(buildModeEEnum, BuildMode.FORWARD);
		addEEnumLiteral(buildModeEEnum, BuildMode.FORWARD_AND_BACKWARD);

		initEEnum(usedCodeGenEEnum, UsedCodeGen.class, "UsedCodeGen");
		addEEnumLiteral(usedCodeGenEEnum, UsedCodeGen.EMF);
		addEEnumLiteral(usedCodeGenEEnum, UsedCodeGen.SMART_EMF);

		// Create resource
		createResource(eNS_URI);
	}

} //PropertycontainerPackageImpl
