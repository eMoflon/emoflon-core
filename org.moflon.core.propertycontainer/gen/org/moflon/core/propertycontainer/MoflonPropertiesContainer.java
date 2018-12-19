/**
 */
package org.moflon.core.propertycontainer;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;
// <-- [user defined imports]
// [user defined imports] -->

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Moflon Properties Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getDependencies <em>Dependencies</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getFactoryMappings <em>Factory Mappings</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getAdditionalDependencies <em>Additional Dependencies</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getMetaModelProject <em>Meta Model Project</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getReplaceGenModel <em>Replace Gen Model</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getTGGBuildMode <em>TGG Build Mode</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getImportMappings <em>Import Mappings</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getAdditionalUsedGenPackages <em>Additional Used Gen Packages</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getDescription <em>Description</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getProjectName <em>Project Name</em>}</li>
 * </ul>
 * </p>
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
	 * <p>
	 * If the meaning of the '<em>Dependencies</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
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
	 * <p>
	 * If the meaning of the '<em>Factory Mappings</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
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
	 * <p>
	 * If the meaning of the '<em>Additional Dependencies</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
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
	 * <p>
	 * If the meaning of the '<em>Meta Model Project</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
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
	 * Returns the value of the '<em><b>Replace Gen Model</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Replace Gen Model</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Replace Gen Model</em>' containment reference.
	 * @see #setReplaceGenModel(ReplaceGenModel)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer_ReplaceGenModel()
	 * @model containment="true" required="true"
	 * @generated
	 */
	ReplaceGenModel getReplaceGenModel();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getReplaceGenModel <em>Replace Gen Model</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Replace Gen Model</em>' containment reference.
	 * @see #getReplaceGenModel()
	 * @generated
	 */
	void setReplaceGenModel(ReplaceGenModel value);

	/**
	 * Returns the value of the '<em><b>TGG Build Mode</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>TGG Build Mode</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>TGG Build Mode</em>' containment reference.
	 * @see #setTGGBuildMode(TGGBuildMode)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMoflonPropertiesContainer_TGGBuildMode()
	 * @model containment="true" required="true"
	 * @generated
	 */
	TGGBuildMode getTGGBuildMode();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.MoflonPropertiesContainer#getTGGBuildMode <em>TGG Build Mode</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>TGG Build Mode</em>' containment reference.
	 * @see #getTGGBuildMode()
	 * @generated
	 */
	void setTGGBuildMode(TGGBuildMode value);

	/**
	 * Returns the value of the '<em><b>Import Mappings</b></em>' containment reference list.
	 * The list contents are of type {@link org.moflon.core.propertycontainer.ImportMappings}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Import Mappings</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
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
	 * <p>
	 * If the meaning of the '<em>Additional Used Gen Packages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
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
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
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
	 * <p>
	 * If the meaning of the '<em>Project Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
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
	// <-- [user code injected with eMoflon]

	// [user code injected with eMoflon] -->
} // MoflonPropertiesContainer
