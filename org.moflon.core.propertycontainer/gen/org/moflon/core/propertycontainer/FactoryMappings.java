/**
 */
package org.moflon.core.propertycontainer;

// <-- [user defined imports]
// [user defined imports] -->

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Factory
 * Mappings</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.moflon.core.propertycontainer.FactoryMappings#getDescription
 * <em>Description</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getFactoryMappings()
 * @model
 * @generated
 */
public interface FactoryMappings extends PropertiesMapping {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute. The default
	 * value is
	 * <code>"[Used to correct the name of a Factory if it does not comply with our normal naming conventions]"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getFactoryMappings_Description()
	 * @model default="[Used to correct the name of a Factory if it does not comply
	 *        with our normal naming conventions]" required="true" ordered="false"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the
	 * '{@link org.moflon.core.propertycontainer.FactoryMappings#getDescription
	 * <em>Description</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @param value
	 *            the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);
	// <-- [user code injected with eMoflon]

	// [user code injected with eMoflon] -->
} // FactoryMappings
