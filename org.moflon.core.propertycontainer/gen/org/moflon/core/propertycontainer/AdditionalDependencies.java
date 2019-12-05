/**
 */
package org.moflon.core.propertycontainer;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Additional Dependencies</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.AdditionalDependencies#getDescription <em>Description</em>}</li>
 * </ul>
 *
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getAdditionalDependencies()
 * @model
 * @generated
 */
public interface AdditionalDependencies extends EObject, PropertiesValue {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * The default value is <code>"[Used to add additional dependencies required for code generation]"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getAdditionalDependencies_Description()
	 * @model default="[Used to add additional dependencies required for code generation]" required="true" ordered="false"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.AdditionalDependencies#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

} // AdditionalDependencies
