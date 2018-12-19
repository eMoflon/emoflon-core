/**
 */
package org.moflon.core.propertycontainer;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage
 * @generated
 */
public interface PropertycontainerFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	PropertycontainerFactory eINSTANCE = org.moflon.core.propertycontainer.impl.PropertycontainerFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Dependencies</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Dependencies</em>'.
	 * @generated
	 */
	Dependencies createDependencies();

	/**
	 * Returns a new object of class '<em>Properties Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Properties Value</em>'.
	 * @generated
	 */
	PropertiesValue createPropertiesValue();

	/**
	 * Returns a new object of class '<em>Additional Used Gen Packages</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Additional Used Gen Packages</em>'.
	 * @generated
	 */
	AdditionalUsedGenPackages createAdditionalUsedGenPackages();

	/**
	 * Returns a new object of class '<em>Import Mappings</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Import Mappings</em>'.
	 * @generated
	 */
	ImportMappings createImportMappings();

	/**
	 * Returns a new object of class '<em>TGG Build Mode</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>TGG Build Mode</em>'.
	 * @generated
	 */
	TGGBuildMode createTGGBuildMode();

	/**
	 * Returns a new object of class '<em>Factory Mappings</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Factory Mappings</em>'.
	 * @generated
	 */
	FactoryMappings createFactoryMappings();

	/**
	 * Returns a new object of class '<em>Moflon Properties Container</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Moflon Properties Container</em>'.
	 * @generated
	 */
	MoflonPropertiesContainer createMoflonPropertiesContainer();

	/**
	 * Returns a new object of class '<em>Replace Gen Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Replace Gen Model</em>'.
	 * @generated
	 */
	ReplaceGenModel createReplaceGenModel();

	/**
	 * Returns a new object of class '<em>Properties Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Properties Mapping</em>'.
	 * @generated
	 */
	PropertiesMapping createPropertiesMapping();

	/**
	 * Returns a new object of class '<em>Additional Dependencies</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Additional Dependencies</em>'.
	 * @generated
	 */
	AdditionalDependencies createAdditionalDependencies();

	/**
	 * Returns a new object of class '<em>Meta Model Project</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Meta Model Project</em>'.
	 * @generated
	 */
	MetaModelProject createMetaModelProject();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	PropertycontainerPackage getPropertycontainerPackage();

} //PropertycontainerFactory
