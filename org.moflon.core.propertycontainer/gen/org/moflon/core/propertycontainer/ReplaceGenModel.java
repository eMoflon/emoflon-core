/**
 */
package org.moflon.core.propertycontainer;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Replace Gen Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.ReplaceGenModel#isBool <em>Bool</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.ReplaceGenModel#getDescription <em>Description</em>}</li>
 * </ul>
 *
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getReplaceGenModel()
 * @model
 * @generated
 */
public interface ReplaceGenModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Bool</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bool</em>' attribute.
	 * @see #setBool(boolean)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getReplaceGenModel_Bool()
	 * @model default="true" required="true" ordered="false"
	 * @generated
	 */
	boolean isBool();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.ReplaceGenModel#isBool <em>Bool</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bool</em>' attribute.
	 * @see #isBool()
	 * @generated
	 */
	void setBool(boolean value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * The default value is <code>"[Set to false if you wish to maintain the GenModel in the project yourself]"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getReplaceGenModel_Description()
	 * @model default="[Set to false if you wish to maintain the GenModel in the project yourself]" required="true" ordered="false"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.ReplaceGenModel#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

} // ReplaceGenModel
