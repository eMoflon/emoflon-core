/**
 */
package org.moflon.core.propertycontainer;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Meta Model Project</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.MetaModelProject#getDescription <em>Description</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.MetaModelProject#getMetaModelProjectName <em>Meta Model Project Name</em>}</li>
 * </ul>
 *
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMetaModelProject()
 * @model
 * @generated
 */
public interface MetaModelProject extends EObject {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * The default value is <code>"[Name of the corresponding metamodel project in the current workspace]"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMetaModelProject_Description()
	 * @model default="[Name of the corresponding metamodel project in the current workspace]" required="true" ordered="false"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.MetaModelProject#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Meta Model Project Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Meta Model Project Name</em>' attribute.
	 * @see #setMetaModelProjectName(String)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getMetaModelProject_MetaModelProjectName()
	 * @model required="true" ordered="false"
	 * @generated
	 */
	String getMetaModelProjectName();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.MetaModelProject#getMetaModelProjectName <em>Meta Model Project Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Meta Model Project Name</em>' attribute.
	 * @see #getMetaModelProjectName()
	 * @generated
	 */
	void setMetaModelProjectName(String value);

} // MetaModelProject
