/**
 */
package org.moflon.core.propertycontainer.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.moflon.core.propertycontainer.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PropertycontainerFactoryImpl extends EFactoryImpl implements PropertycontainerFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static PropertycontainerFactory init() {
		try {
			PropertycontainerFactory thePropertycontainerFactory = (PropertycontainerFactory) EPackage.Registry.INSTANCE
					.getEFactory(PropertycontainerPackage.eNS_URI);
			if (thePropertycontainerFactory != null) {
				return thePropertycontainerFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new PropertycontainerFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PropertycontainerFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case PropertycontainerPackage.DEPENDENCIES:
			return createDependencies();
		case PropertycontainerPackage.PROPERTIES_VALUE:
			return createPropertiesValue();
		case PropertycontainerPackage.ADDITIONAL_USED_GEN_PACKAGES:
			return createAdditionalUsedGenPackages();
		case PropertycontainerPackage.IMPORT_MAPPINGS:
			return createImportMappings();
		case PropertycontainerPackage.FACTORY_MAPPINGS:
			return createFactoryMappings();
		case PropertycontainerPackage.MOFLON_PROPERTIES_CONTAINER:
			return createMoflonPropertiesContainer();
		case PropertycontainerPackage.GEN_MODEL_PROPERTIES:
			return createGenModelProperties();
		case PropertycontainerPackage.PROPERTIES_MAPPING:
			return createPropertiesMapping();
		case PropertycontainerPackage.ADDITIONAL_DEPENDENCIES:
			return createAdditionalDependencies();
		case PropertycontainerPackage.META_MODEL_PROJECT:
			return createMetaModelProject();
		case PropertycontainerPackage.CODE_GENERATOR:
			return createCodeGenerator();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
		case PropertycontainerPackage.USED_CODE_GEN:
			return createUsedCodeGenFromString(eDataType, initialValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case PropertycontainerPackage.USED_CODE_GEN:
			return convertUsedCodeGenToString(eDataType, instanceValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Dependencies createDependencies() {
		DependenciesImpl dependencies = new DependenciesImpl();
		return dependencies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertiesValue createPropertiesValue() {
		PropertiesValueImpl propertiesValue = new PropertiesValueImpl();
		return propertiesValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AdditionalUsedGenPackages createAdditionalUsedGenPackages() {
		AdditionalUsedGenPackagesImpl additionalUsedGenPackages = new AdditionalUsedGenPackagesImpl();
		return additionalUsedGenPackages;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ImportMappings createImportMappings() {
		ImportMappingsImpl importMappings = new ImportMappingsImpl();
		return importMappings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FactoryMappings createFactoryMappings() {
		FactoryMappingsImpl factoryMappings = new FactoryMappingsImpl();
		return factoryMappings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MoflonPropertiesContainer createMoflonPropertiesContainer() {
		MoflonPropertiesContainerImpl moflonPropertiesContainer = new MoflonPropertiesContainerImpl();
		return moflonPropertiesContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GenModelProperties createGenModelProperties() {
		GenModelPropertiesImpl genModelProperties = new GenModelPropertiesImpl();
		return genModelProperties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertiesMapping createPropertiesMapping() {
		PropertiesMappingImpl propertiesMapping = new PropertiesMappingImpl();
		return propertiesMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AdditionalDependencies createAdditionalDependencies() {
		AdditionalDependenciesImpl additionalDependencies = new AdditionalDependenciesImpl();
		return additionalDependencies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MetaModelProject createMetaModelProject() {
		MetaModelProjectImpl metaModelProject = new MetaModelProjectImpl();
		return metaModelProject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CodeGenerator createCodeGenerator() {
		CodeGeneratorImpl codeGenerator = new CodeGeneratorImpl();
		return codeGenerator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UsedCodeGen createUsedCodeGenFromString(EDataType eDataType, String initialValue) {
		UsedCodeGen result = UsedCodeGen.get(initialValue);
		if (result == null)
			throw new IllegalArgumentException(
					"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertUsedCodeGenToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PropertycontainerPackage getPropertycontainerPackage() {
		return (PropertycontainerPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static PropertycontainerPackage getPackage() {
		return PropertycontainerPackage.eINSTANCE;
	}

} //PropertycontainerFactoryImpl
