/**
 */
package org.moflon.core.propertycontainer;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.moflon.core.propertycontainer.PropertycontainerFactory
 * @model kind="package"
 * @generated
 */
public interface PropertycontainerPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "propertycontainer";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "platform:/plugin/org.moflon.core.propertycontainer/model/Propertycontainer.ecore";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.moflon.core.propertycontainer";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	PropertycontainerPackage eINSTANCE = org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.moflon.core.propertycontainer.impl.PropertiesValueImpl <em>Properties Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.moflon.core.propertycontainer.impl.PropertiesValueImpl
	 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getPropertiesValue()
	 * @generated
	 */
	int PROPERTIES_VALUE = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES_VALUE__VALUE = 0;

	/**
	 * The number of structural features of the '<em>Properties Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES_VALUE_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Properties Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES_VALUE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.moflon.core.propertycontainer.impl.DependenciesImpl <em>Dependencies</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.moflon.core.propertycontainer.impl.DependenciesImpl
	 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getDependencies()
	 * @generated
	 */
	int DEPENDENCIES = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCIES__VALUE = PROPERTIES_VALUE__VALUE;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCIES__DESCRIPTION = PROPERTIES_VALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Dependencies</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCIES_FEATURE_COUNT = PROPERTIES_VALUE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Dependencies</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCIES_OPERATION_COUNT = PROPERTIES_VALUE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.moflon.core.propertycontainer.impl.AdditionalUsedGenPackagesImpl <em>Additional Used Gen Packages</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.moflon.core.propertycontainer.impl.AdditionalUsedGenPackagesImpl
	 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getAdditionalUsedGenPackages()
	 * @generated
	 */
	int ADDITIONAL_USED_GEN_PACKAGES = 2;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIONAL_USED_GEN_PACKAGES__VALUE = PROPERTIES_VALUE__VALUE;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIONAL_USED_GEN_PACKAGES__DESCRIPTION = PROPERTIES_VALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Additional Used Gen Packages</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIONAL_USED_GEN_PACKAGES_FEATURE_COUNT = PROPERTIES_VALUE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Additional Used Gen Packages</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIONAL_USED_GEN_PACKAGES_OPERATION_COUNT = PROPERTIES_VALUE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.moflon.core.propertycontainer.impl.PropertiesMappingImpl <em>Properties Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.moflon.core.propertycontainer.impl.PropertiesMappingImpl
	 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getPropertiesMapping()
	 * @generated
	 */
	int PROPERTIES_MAPPING = 8;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES_MAPPING__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES_MAPPING__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Properties Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES_MAPPING_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Properties Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES_MAPPING_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.moflon.core.propertycontainer.impl.ImportMappingsImpl <em>Import Mappings</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.moflon.core.propertycontainer.impl.ImportMappingsImpl
	 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getImportMappings()
	 * @generated
	 */
	int IMPORT_MAPPINGS = 3;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT_MAPPINGS__KEY = PROPERTIES_MAPPING__KEY;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT_MAPPINGS__VALUE = PROPERTIES_MAPPING__VALUE;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT_MAPPINGS__DESCRIPTION = PROPERTIES_MAPPING_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Import Mappings</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT_MAPPINGS_FEATURE_COUNT = PROPERTIES_MAPPING_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Import Mappings</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT_MAPPINGS_OPERATION_COUNT = PROPERTIES_MAPPING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.moflon.core.propertycontainer.impl.TGGBuildModeImpl <em>TGG Build Mode</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.moflon.core.propertycontainer.impl.TGGBuildModeImpl
	 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getTGGBuildMode()
	 * @generated
	 */
	int TGG_BUILD_MODE = 4;

	/**
	 * The feature id for the '<em><b>Build Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TGG_BUILD_MODE__BUILD_MODE = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TGG_BUILD_MODE__DESCRIPTION = 1;

	/**
	 * The number of structural features of the '<em>TGG Build Mode</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TGG_BUILD_MODE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>TGG Build Mode</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TGG_BUILD_MODE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.moflon.core.propertycontainer.impl.FactoryMappingsImpl <em>Factory Mappings</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.moflon.core.propertycontainer.impl.FactoryMappingsImpl
	 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getFactoryMappings()
	 * @generated
	 */
	int FACTORY_MAPPINGS = 5;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FACTORY_MAPPINGS__KEY = PROPERTIES_MAPPING__KEY;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FACTORY_MAPPINGS__VALUE = PROPERTIES_MAPPING__VALUE;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FACTORY_MAPPINGS__DESCRIPTION = PROPERTIES_MAPPING_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Factory Mappings</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FACTORY_MAPPINGS_FEATURE_COUNT = PROPERTIES_MAPPING_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Factory Mappings</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FACTORY_MAPPINGS_OPERATION_COUNT = PROPERTIES_MAPPING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl <em>Moflon Properties Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl
	 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getMoflonPropertiesContainer()
	 * @generated
	 */
	int MOFLON_PROPERTIES_CONTAINER = 6;

	/**
	 * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOFLON_PROPERTIES_CONTAINER__DEPENDENCIES = 0;

	/**
	 * The feature id for the '<em><b>Factory Mappings</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOFLON_PROPERTIES_CONTAINER__FACTORY_MAPPINGS = 1;

	/**
	 * The feature id for the '<em><b>Additional Dependencies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_DEPENDENCIES = 2;

	/**
	 * The feature id for the '<em><b>Meta Model Project</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOFLON_PROPERTIES_CONTAINER__META_MODEL_PROJECT = 3;

	/**
	 * The feature id for the '<em><b>Replace Gen Model</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOFLON_PROPERTIES_CONTAINER__REPLACE_GEN_MODEL = 4;

	/**
	 * The feature id for the '<em><b>TGG Build Mode</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOFLON_PROPERTIES_CONTAINER__TGG_BUILD_MODE = 5;

	/**
	 * The feature id for the '<em><b>Import Mappings</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOFLON_PROPERTIES_CONTAINER__IMPORT_MAPPINGS = 6;

	/**
	 * The feature id for the '<em><b>Additional Used Gen Packages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_USED_GEN_PACKAGES = 7;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOFLON_PROPERTIES_CONTAINER__DESCRIPTION = 8;

	/**
	 * The feature id for the '<em><b>Project Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOFLON_PROPERTIES_CONTAINER__PROJECT_NAME = 9;

	/**
	 * The number of structural features of the '<em>Moflon Properties Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOFLON_PROPERTIES_CONTAINER_FEATURE_COUNT = 10;

	/**
	 * The number of operations of the '<em>Moflon Properties Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOFLON_PROPERTIES_CONTAINER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.moflon.core.propertycontainer.impl.ReplaceGenModelImpl <em>Replace Gen Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.moflon.core.propertycontainer.impl.ReplaceGenModelImpl
	 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getReplaceGenModel()
	 * @generated
	 */
	int REPLACE_GEN_MODEL = 7;

	/**
	 * The feature id for the '<em><b>Bool</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_GEN_MODEL__BOOL = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_GEN_MODEL__DESCRIPTION = 1;

	/**
	 * The number of structural features of the '<em>Replace Gen Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_GEN_MODEL_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Replace Gen Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPLACE_GEN_MODEL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.moflon.core.propertycontainer.impl.AdditionalDependenciesImpl <em>Additional Dependencies</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.moflon.core.propertycontainer.impl.AdditionalDependenciesImpl
	 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getAdditionalDependencies()
	 * @generated
	 */
	int ADDITIONAL_DEPENDENCIES = 9;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIONAL_DEPENDENCIES__VALUE = PROPERTIES_VALUE__VALUE;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIONAL_DEPENDENCIES__DESCRIPTION = PROPERTIES_VALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Additional Dependencies</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIONAL_DEPENDENCIES_FEATURE_COUNT = PROPERTIES_VALUE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Additional Dependencies</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDITIONAL_DEPENDENCIES_OPERATION_COUNT = PROPERTIES_VALUE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.moflon.core.propertycontainer.impl.MetaModelProjectImpl <em>Meta Model Project</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.moflon.core.propertycontainer.impl.MetaModelProjectImpl
	 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getMetaModelProject()
	 * @generated
	 */
	int META_MODEL_PROJECT = 10;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META_MODEL_PROJECT__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Meta Model Project Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META_MODEL_PROJECT__META_MODEL_PROJECT_NAME = 1;

	/**
	 * The number of structural features of the '<em>Meta Model Project</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META_MODEL_PROJECT_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Meta Model Project</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META_MODEL_PROJECT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.moflon.core.propertycontainer.BuildMode <em>Build Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.moflon.core.propertycontainer.BuildMode
	 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getBuildMode()
	 * @generated
	 */
	int BUILD_MODE = 11;

	/**
	 * Returns the meta object for class '{@link org.moflon.core.propertycontainer.Dependencies <em>Dependencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Dependencies</em>'.
	 * @see org.moflon.core.propertycontainer.Dependencies
	 * @generated
	 */
	EClass getDependencies();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.Dependencies#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.moflon.core.propertycontainer.Dependencies#getDescription()
	 * @see #getDependencies()
	 * @generated
	 */
	EAttribute getDependencies_Description();

	/**
	 * Returns the meta object for class '{@link org.moflon.core.propertycontainer.PropertiesValue <em>Properties Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Properties Value</em>'.
	 * @see org.moflon.core.propertycontainer.PropertiesValue
	 * @generated
	 */
	EClass getPropertiesValue();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.PropertiesValue#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.moflon.core.propertycontainer.PropertiesValue#getValue()
	 * @see #getPropertiesValue()
	 * @generated
	 */
	EAttribute getPropertiesValue_Value();

	/**
	 * Returns the meta object for class '{@link org.moflon.core.propertycontainer.AdditionalUsedGenPackages <em>Additional Used Gen Packages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Additional Used Gen Packages</em>'.
	 * @see org.moflon.core.propertycontainer.AdditionalUsedGenPackages
	 * @generated
	 */
	EClass getAdditionalUsedGenPackages();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.AdditionalUsedGenPackages#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.moflon.core.propertycontainer.AdditionalUsedGenPackages#getDescription()
	 * @see #getAdditionalUsedGenPackages()
	 * @generated
	 */
	EAttribute getAdditionalUsedGenPackages_Description();

	/**
	 * Returns the meta object for class '{@link org.moflon.core.propertycontainer.ImportMappings <em>Import Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Import Mappings</em>'.
	 * @see org.moflon.core.propertycontainer.ImportMappings
	 * @generated
	 */
	EClass getImportMappings();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.ImportMappings#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.moflon.core.propertycontainer.ImportMappings#getDescription()
	 * @see #getImportMappings()
	 * @generated
	 */
	EAttribute getImportMappings_Description();

	/**
	 * Returns the meta object for class '{@link org.moflon.core.propertycontainer.TGGBuildMode <em>TGG Build Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>TGG Build Mode</em>'.
	 * @see org.moflon.core.propertycontainer.TGGBuildMode
	 * @generated
	 */
	EClass getTGGBuildMode();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.TGGBuildMode#getBuildMode <em>Build Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Build Mode</em>'.
	 * @see org.moflon.core.propertycontainer.TGGBuildMode#getBuildMode()
	 * @see #getTGGBuildMode()
	 * @generated
	 */
	EAttribute getTGGBuildMode_BuildMode();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.TGGBuildMode#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.moflon.core.propertycontainer.TGGBuildMode#getDescription()
	 * @see #getTGGBuildMode()
	 * @generated
	 */
	EAttribute getTGGBuildMode_Description();

	/**
	 * Returns the meta object for class '{@link org.moflon.core.propertycontainer.FactoryMappings <em>Factory Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Factory Mappings</em>'.
	 * @see org.moflon.core.propertycontainer.FactoryMappings
	 * @generated
	 */
	EClass getFactoryMappings();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.FactoryMappings#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.moflon.core.propertycontainer.FactoryMappings#getDescription()
	 * @see #getFactoryMappings()
	 * @generated
	 */
	EAttribute getFactoryMappings_Description();

	/**
	 * Returns the meta object for class '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer <em>Moflon Properties Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Moflon Properties Container</em>'.
	 * @see org.moflon.core.propertycontainer.MoflonPropertiesContainer
	 * @generated
	 */
	EClass getMoflonPropertiesContainer();

	/**
	 * Returns the meta object for the containment reference list '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getDependencies <em>Dependencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Dependencies</em>'.
	 * @see org.moflon.core.propertycontainer.MoflonPropertiesContainer#getDependencies()
	 * @see #getMoflonPropertiesContainer()
	 * @generated
	 */
	EReference getMoflonPropertiesContainer_Dependencies();

	/**
	 * Returns the meta object for the containment reference list '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getFactoryMappings <em>Factory Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Factory Mappings</em>'.
	 * @see org.moflon.core.propertycontainer.MoflonPropertiesContainer#getFactoryMappings()
	 * @see #getMoflonPropertiesContainer()
	 * @generated
	 */
	EReference getMoflonPropertiesContainer_FactoryMappings();

	/**
	 * Returns the meta object for the containment reference list '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getAdditionalDependencies <em>Additional Dependencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Additional Dependencies</em>'.
	 * @see org.moflon.core.propertycontainer.MoflonPropertiesContainer#getAdditionalDependencies()
	 * @see #getMoflonPropertiesContainer()
	 * @generated
	 */
	EReference getMoflonPropertiesContainer_AdditionalDependencies();

	/**
	 * Returns the meta object for the containment reference '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getMetaModelProject <em>Meta Model Project</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Meta Model Project</em>'.
	 * @see org.moflon.core.propertycontainer.MoflonPropertiesContainer#getMetaModelProject()
	 * @see #getMoflonPropertiesContainer()
	 * @generated
	 */
	EReference getMoflonPropertiesContainer_MetaModelProject();

	/**
	 * Returns the meta object for the containment reference '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getReplaceGenModel <em>Replace Gen Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Replace Gen Model</em>'.
	 * @see org.moflon.core.propertycontainer.MoflonPropertiesContainer#getReplaceGenModel()
	 * @see #getMoflonPropertiesContainer()
	 * @generated
	 */
	EReference getMoflonPropertiesContainer_ReplaceGenModel();

	/**
	 * Returns the meta object for the containment reference '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getTGGBuildMode <em>TGG Build Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>TGG Build Mode</em>'.
	 * @see org.moflon.core.propertycontainer.MoflonPropertiesContainer#getTGGBuildMode()
	 * @see #getMoflonPropertiesContainer()
	 * @generated
	 */
	EReference getMoflonPropertiesContainer_TGGBuildMode();

	/**
	 * Returns the meta object for the containment reference list '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getImportMappings <em>Import Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Import Mappings</em>'.
	 * @see org.moflon.core.propertycontainer.MoflonPropertiesContainer#getImportMappings()
	 * @see #getMoflonPropertiesContainer()
	 * @generated
	 */
	EReference getMoflonPropertiesContainer_ImportMappings();

	/**
	 * Returns the meta object for the containment reference list '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getAdditionalUsedGenPackages <em>Additional Used Gen Packages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Additional Used Gen Packages</em>'.
	 * @see org.moflon.core.propertycontainer.MoflonPropertiesContainer#getAdditionalUsedGenPackages()
	 * @see #getMoflonPropertiesContainer()
	 * @generated
	 */
	EReference getMoflonPropertiesContainer_AdditionalUsedGenPackages();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.moflon.core.propertycontainer.MoflonPropertiesContainer#getDescription()
	 * @see #getMoflonPropertiesContainer()
	 * @generated
	 */
	EAttribute getMoflonPropertiesContainer_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getProjectName <em>Project Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Project Name</em>'.
	 * @see org.moflon.core.propertycontainer.MoflonPropertiesContainer#getProjectName()
	 * @see #getMoflonPropertiesContainer()
	 * @generated
	 */
	EAttribute getMoflonPropertiesContainer_ProjectName();

	/**
	 * Returns the meta object for class '{@link org.moflon.core.propertycontainer.ReplaceGenModel <em>Replace Gen Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Replace Gen Model</em>'.
	 * @see org.moflon.core.propertycontainer.ReplaceGenModel
	 * @generated
	 */
	EClass getReplaceGenModel();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.ReplaceGenModel#isBool <em>Bool</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bool</em>'.
	 * @see org.moflon.core.propertycontainer.ReplaceGenModel#isBool()
	 * @see #getReplaceGenModel()
	 * @generated
	 */
	EAttribute getReplaceGenModel_Bool();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.ReplaceGenModel#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.moflon.core.propertycontainer.ReplaceGenModel#getDescription()
	 * @see #getReplaceGenModel()
	 * @generated
	 */
	EAttribute getReplaceGenModel_Description();

	/**
	 * Returns the meta object for class '{@link org.moflon.core.propertycontainer.PropertiesMapping <em>Properties Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Properties Mapping</em>'.
	 * @see org.moflon.core.propertycontainer.PropertiesMapping
	 * @generated
	 */
	EClass getPropertiesMapping();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.PropertiesMapping#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see org.moflon.core.propertycontainer.PropertiesMapping#getKey()
	 * @see #getPropertiesMapping()
	 * @generated
	 */
	EAttribute getPropertiesMapping_Key();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.PropertiesMapping#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.moflon.core.propertycontainer.PropertiesMapping#getValue()
	 * @see #getPropertiesMapping()
	 * @generated
	 */
	EAttribute getPropertiesMapping_Value();

	/**
	 * Returns the meta object for class '{@link org.moflon.core.propertycontainer.AdditionalDependencies <em>Additional Dependencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Additional Dependencies</em>'.
	 * @see org.moflon.core.propertycontainer.AdditionalDependencies
	 * @generated
	 */
	EClass getAdditionalDependencies();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.AdditionalDependencies#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.moflon.core.propertycontainer.AdditionalDependencies#getDescription()
	 * @see #getAdditionalDependencies()
	 * @generated
	 */
	EAttribute getAdditionalDependencies_Description();

	/**
	 * Returns the meta object for class '{@link org.moflon.core.propertycontainer.MetaModelProject <em>Meta Model Project</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Meta Model Project</em>'.
	 * @see org.moflon.core.propertycontainer.MetaModelProject
	 * @generated
	 */
	EClass getMetaModelProject();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.MetaModelProject#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.moflon.core.propertycontainer.MetaModelProject#getDescription()
	 * @see #getMetaModelProject()
	 * @generated
	 */
	EAttribute getMetaModelProject_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.moflon.core.propertycontainer.MetaModelProject#getMetaModelProjectName <em>Meta Model Project Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Meta Model Project Name</em>'.
	 * @see org.moflon.core.propertycontainer.MetaModelProject#getMetaModelProjectName()
	 * @see #getMetaModelProject()
	 * @generated
	 */
	EAttribute getMetaModelProject_MetaModelProjectName();

	/**
	 * Returns the meta object for enum '{@link org.moflon.core.propertycontainer.BuildMode <em>Build Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Build Mode</em>'.
	 * @see org.moflon.core.propertycontainer.BuildMode
	 * @generated
	 */
	EEnum getBuildMode();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	PropertycontainerFactory getPropertycontainerFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.moflon.core.propertycontainer.impl.DependenciesImpl <em>Dependencies</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.moflon.core.propertycontainer.impl.DependenciesImpl
		 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getDependencies()
		 * @generated
		 */
		EClass DEPENDENCIES = eINSTANCE.getDependencies();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEPENDENCIES__DESCRIPTION = eINSTANCE.getDependencies_Description();

		/**
		 * The meta object literal for the '{@link org.moflon.core.propertycontainer.impl.PropertiesValueImpl <em>Properties Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.moflon.core.propertycontainer.impl.PropertiesValueImpl
		 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getPropertiesValue()
		 * @generated
		 */
		EClass PROPERTIES_VALUE = eINSTANCE.getPropertiesValue();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTIES_VALUE__VALUE = eINSTANCE.getPropertiesValue_Value();

		/**
		 * The meta object literal for the '{@link org.moflon.core.propertycontainer.impl.AdditionalUsedGenPackagesImpl <em>Additional Used Gen Packages</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.moflon.core.propertycontainer.impl.AdditionalUsedGenPackagesImpl
		 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getAdditionalUsedGenPackages()
		 * @generated
		 */
		EClass ADDITIONAL_USED_GEN_PACKAGES = eINSTANCE.getAdditionalUsedGenPackages();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADDITIONAL_USED_GEN_PACKAGES__DESCRIPTION = eINSTANCE.getAdditionalUsedGenPackages_Description();

		/**
		 * The meta object literal for the '{@link org.moflon.core.propertycontainer.impl.ImportMappingsImpl <em>Import Mappings</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.moflon.core.propertycontainer.impl.ImportMappingsImpl
		 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getImportMappings()
		 * @generated
		 */
		EClass IMPORT_MAPPINGS = eINSTANCE.getImportMappings();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPORT_MAPPINGS__DESCRIPTION = eINSTANCE.getImportMappings_Description();

		/**
		 * The meta object literal for the '{@link org.moflon.core.propertycontainer.impl.TGGBuildModeImpl <em>TGG Build Mode</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.moflon.core.propertycontainer.impl.TGGBuildModeImpl
		 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getTGGBuildMode()
		 * @generated
		 */
		EClass TGG_BUILD_MODE = eINSTANCE.getTGGBuildMode();

		/**
		 * The meta object literal for the '<em><b>Build Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TGG_BUILD_MODE__BUILD_MODE = eINSTANCE.getTGGBuildMode_BuildMode();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TGG_BUILD_MODE__DESCRIPTION = eINSTANCE.getTGGBuildMode_Description();

		/**
		 * The meta object literal for the '{@link org.moflon.core.propertycontainer.impl.FactoryMappingsImpl <em>Factory Mappings</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.moflon.core.propertycontainer.impl.FactoryMappingsImpl
		 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getFactoryMappings()
		 * @generated
		 */
		EClass FACTORY_MAPPINGS = eINSTANCE.getFactoryMappings();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FACTORY_MAPPINGS__DESCRIPTION = eINSTANCE.getFactoryMappings_Description();

		/**
		 * The meta object literal for the '{@link org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl <em>Moflon Properties Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.moflon.core.propertycontainer.impl.MoflonPropertiesContainerImpl
		 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getMoflonPropertiesContainer()
		 * @generated
		 */
		EClass MOFLON_PROPERTIES_CONTAINER = eINSTANCE.getMoflonPropertiesContainer();

		/**
		 * The meta object literal for the '<em><b>Dependencies</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MOFLON_PROPERTIES_CONTAINER__DEPENDENCIES = eINSTANCE.getMoflonPropertiesContainer_Dependencies();

		/**
		 * The meta object literal for the '<em><b>Factory Mappings</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MOFLON_PROPERTIES_CONTAINER__FACTORY_MAPPINGS = eINSTANCE
				.getMoflonPropertiesContainer_FactoryMappings();

		/**
		 * The meta object literal for the '<em><b>Additional Dependencies</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_DEPENDENCIES = eINSTANCE
				.getMoflonPropertiesContainer_AdditionalDependencies();

		/**
		 * The meta object literal for the '<em><b>Meta Model Project</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MOFLON_PROPERTIES_CONTAINER__META_MODEL_PROJECT = eINSTANCE
				.getMoflonPropertiesContainer_MetaModelProject();

		/**
		 * The meta object literal for the '<em><b>Replace Gen Model</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MOFLON_PROPERTIES_CONTAINER__REPLACE_GEN_MODEL = eINSTANCE
				.getMoflonPropertiesContainer_ReplaceGenModel();

		/**
		 * The meta object literal for the '<em><b>TGG Build Mode</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MOFLON_PROPERTIES_CONTAINER__TGG_BUILD_MODE = eINSTANCE.getMoflonPropertiesContainer_TGGBuildMode();

		/**
		 * The meta object literal for the '<em><b>Import Mappings</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MOFLON_PROPERTIES_CONTAINER__IMPORT_MAPPINGS = eINSTANCE
				.getMoflonPropertiesContainer_ImportMappings();

		/**
		 * The meta object literal for the '<em><b>Additional Used Gen Packages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MOFLON_PROPERTIES_CONTAINER__ADDITIONAL_USED_GEN_PACKAGES = eINSTANCE
				.getMoflonPropertiesContainer_AdditionalUsedGenPackages();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MOFLON_PROPERTIES_CONTAINER__DESCRIPTION = eINSTANCE.getMoflonPropertiesContainer_Description();

		/**
		 * The meta object literal for the '<em><b>Project Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MOFLON_PROPERTIES_CONTAINER__PROJECT_NAME = eINSTANCE.getMoflonPropertiesContainer_ProjectName();

		/**
		 * The meta object literal for the '{@link org.moflon.core.propertycontainer.impl.ReplaceGenModelImpl <em>Replace Gen Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.moflon.core.propertycontainer.impl.ReplaceGenModelImpl
		 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getReplaceGenModel()
		 * @generated
		 */
		EClass REPLACE_GEN_MODEL = eINSTANCE.getReplaceGenModel();

		/**
		 * The meta object literal for the '<em><b>Bool</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REPLACE_GEN_MODEL__BOOL = eINSTANCE.getReplaceGenModel_Bool();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REPLACE_GEN_MODEL__DESCRIPTION = eINSTANCE.getReplaceGenModel_Description();

		/**
		 * The meta object literal for the '{@link org.moflon.core.propertycontainer.impl.PropertiesMappingImpl <em>Properties Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.moflon.core.propertycontainer.impl.PropertiesMappingImpl
		 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getPropertiesMapping()
		 * @generated
		 */
		EClass PROPERTIES_MAPPING = eINSTANCE.getPropertiesMapping();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTIES_MAPPING__KEY = eINSTANCE.getPropertiesMapping_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTIES_MAPPING__VALUE = eINSTANCE.getPropertiesMapping_Value();

		/**
		 * The meta object literal for the '{@link org.moflon.core.propertycontainer.impl.AdditionalDependenciesImpl <em>Additional Dependencies</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.moflon.core.propertycontainer.impl.AdditionalDependenciesImpl
		 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getAdditionalDependencies()
		 * @generated
		 */
		EClass ADDITIONAL_DEPENDENCIES = eINSTANCE.getAdditionalDependencies();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADDITIONAL_DEPENDENCIES__DESCRIPTION = eINSTANCE.getAdditionalDependencies_Description();

		/**
		 * The meta object literal for the '{@link org.moflon.core.propertycontainer.impl.MetaModelProjectImpl <em>Meta Model Project</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.moflon.core.propertycontainer.impl.MetaModelProjectImpl
		 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getMetaModelProject()
		 * @generated
		 */
		EClass META_MODEL_PROJECT = eINSTANCE.getMetaModelProject();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute META_MODEL_PROJECT__DESCRIPTION = eINSTANCE.getMetaModelProject_Description();

		/**
		 * The meta object literal for the '<em><b>Meta Model Project Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute META_MODEL_PROJECT__META_MODEL_PROJECT_NAME = eINSTANCE.getMetaModelProject_MetaModelProjectName();

		/**
		 * The meta object literal for the '{@link org.moflon.core.propertycontainer.BuildMode <em>Build Mode</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.moflon.core.propertycontainer.BuildMode
		 * @see org.moflon.core.propertycontainer.impl.PropertycontainerPackageImpl#getBuildMode()
		 * @generated
		 */
		EEnum BUILD_MODE = eINSTANCE.getBuildMode();

	}

} //PropertycontainerPackage
