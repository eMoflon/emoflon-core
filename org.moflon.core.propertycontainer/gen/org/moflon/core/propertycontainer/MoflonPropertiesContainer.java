/**
 */
package org.moflon.core.propertycontainer;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Moflon Properties Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getDependencies <em>Dependencies</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getFactoryMappings <em>Factory Mappings</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getAdditionalDependencies <em>Additional Dependencies</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getMetaModelProject <em>Meta Model Project</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getGenModelProps <em>Gen Model Props</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getImportMappings <em>Import Mappings</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getAdditionalUsedGenPackages <em>Additional Used Gen Packages</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getDescription <em>Description</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getProjectName <em>Project Name</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getCodeGenerator <em>Code Generator</em>}</li>
 * </ul>
 *
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer()
 * @model
 * @generated
 */
public interface MoflonPropertiesContainer extends EObject {
	/**
	 * Returns the value of the '<em><b>Dependencies</b></em>' containment reference list.
	 * The list contents are of type {@link org.moflon.core.propertycontainer.Dependencies}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dependencies</em>' containment reference list.
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer_Dependencies()
	 * @model containment="true"
	 * @generated
	 */
	EList<Dependencies> getDependencies();

	/**
	 * Returns the value of the '<em><b>Factory Mappings</b></em>' containment reference list.
	 * The list contents are of type {@link org.moflon.core.propertycontainer.FactoryMappings}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Factory Mappings</em>' containment reference list.
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer_FactoryMappings()
	 * @model containment="true"
	 * @generated
	 */
	EList<FactoryMappings> getFactoryMappings();

	/**
	 * Returns the value of the '<em><b>Additional Dependencies</b></em>' containment reference list.
	 * The list contents are of type {@link org.moflon.core.propertycontainer.AdditionalDependencies}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Additional Dependencies</em>' containment reference list.
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer_AdditionalDependencies()
	 * @model containment="true"
	 * @generated
	 */
	EList<AdditionalDependencies> getAdditionalDependencies();

	/**
	 * Returns the value of the '<em><b>Meta Model Project</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Meta Model Project</em>' containment reference.
	 * @see #setMetaModelProject(MetaModelProject)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer_MetaModelProject()
	 * @model containment="true" required="true"
	 * @generated
	 */
	MetaModelProject getMetaModelProject();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getMetaModelProject <em>Meta Model Project</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Meta Model Project</em>' containment reference.
	 * @see #getMetaModelProject()
	 * @generated
	 */
	void setMetaModelProject(MetaModelProject value);

	/**
	 * Returns the value of the '<em><b>Gen Model Props</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gen Model Props</em>' containment reference.
	 * @see #setGenModelProps(GenModelProperties)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer_GenModelProps()
	 * @model containment="true" required="true"
	 * @generated
	 */
	GenModelProperties getGenModelProps();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getGenModelProps <em>Gen Model Props</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gen Model Props</em>' containment reference.
	 * @see #getGenModelProps()
	 * @generated
	 */
	void setGenModelProps(GenModelProperties value);

	/**
	 * Returns the value of the '<em><b>Import Mappings</b></em>' containment reference list.
	 * The list contents are of type {@link org.moflon.core.propertycontainer.ImportMappings}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Import Mappings</em>' containment reference list.
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer_ImportMappings()
	 * @model containment="true"
	 * @generated
	 */
	EList<ImportMappings> getImportMappings();

	/**
	 * Returns the value of the '<em><b>Additional Used Gen Packages</b></em>' containment reference list.
	 * The list contents are of type {@link org.moflon.core.propertycontainer.AdditionalUsedGenPackages}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Additional Used Gen Packages</em>' containment reference list.
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer_AdditionalUsedGenPackages()
	 * @model containment="true"
	 * @generated
	 */
	EList<AdditionalUsedGenPackages> getAdditionalUsedGenPackages();

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * The default value is <code>"[Properties to configure code generation]"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer_Description()
	 * @model default="[Properties to configure code generation]" required="true" ordered="false"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Project Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Project Name</em>' attribute.
	 * @see #setProjectName(String)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer_ProjectName()
	 * @model required="true" ordered="false"
	 * @generated
	 */
	String getProjectName();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getProjectName <em>Project Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Project Name</em>' attribute.
	 * @see #getProjectName()
	 * @generated
	 */
	void setProjectName(String value);

	/**
	 * Returns the value of the '<em><b>Code Generator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Code Generator</em>' containment reference.
	 * @see #setCodeGenerator(CodeGenerator)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer_CodeGenerator()
	 * @model containment="true" required="true"
	 * @generated
	 */
	CodeGenerator getCodeGenerator();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getCodeGenerator <em>Code Generator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Code Generator</em>' containment reference.
	 * @see #getCodeGenerator()
	 * @generated
	 */
	void setCodeGenerator(CodeGenerator value);

} // MoflonPropertiesContainer
