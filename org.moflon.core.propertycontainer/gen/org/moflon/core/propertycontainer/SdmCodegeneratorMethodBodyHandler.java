/**
 */
package org.moflon.core.propertycontainer;

import org.eclipse.emf.ecore.EObject;
// <-- [user defined imports]
// [user defined imports] -->

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sdm Codegenerator Method Body Handler</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.SdmCodegeneratorMethodBodyHandler#getDescription <em>Description</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.SdmCodegeneratorMethodBodyHandler#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getSdmCodegeneratorMethodBodyHandler()
 * @model
 * @generated
 */
public interface SdmCodegeneratorMethodBodyHandler extends EObject {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * The default value is <code>"[Value determines the MethodBodyHandler that invokes the code generator for SDMs.]"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getSdmCodegeneratorMethodBodyHandler_Description()
	 * @model default="[Value determines the MethodBodyHandler that invokes the code generator for SDMs.]" required="true" ordered="false"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.SdmCodegeneratorMethodBodyHandler#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * The literals are from the enumeration {@link org.moflon.core.propertycontainer.SDMCodeGeneratorIds}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see org.moflon.core.propertycontainer.SDMCodeGeneratorIds
	 * @see #setValue(SDMCodeGeneratorIds)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getSdmCodegeneratorMethodBodyHandler_Value()
	 * @model default="1" required="true" ordered="false"
	 * @generated
	 */
	SDMCodeGeneratorIds getValue();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.SdmCodegeneratorMethodBodyHandler#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see org.moflon.core.propertycontainer.SDMCodeGeneratorIds
	 * @see #getValue()
	 * @generated
	 */
	void setValue(SDMCodeGeneratorIds value);
	// <-- [user code injected with eMoflon]

	// [user code injected with eMoflon] -->
} // SdmCodegeneratorMethodBodyHandler
