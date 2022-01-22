/**
 */
package org.moflon.core.propertycontainer;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Code Generator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.CodeGenerator#getGenerator <em>Generator</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.CodeGenerator#isEnforced <em>Enforced</em>}</li>
 * </ul>
 *
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getCodeGenerator()
 * @model
 * @generated
 */
public interface CodeGenerator extends EObject {
	/**
	 * Returns the value of the '<em><b>Generator</b></em>' attribute.
	 * The literals are from the enumeration {@link org.moflon.core.propertycontainer.UsedCodeGen}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Generator</em>' attribute.
	 * @see org.moflon.core.propertycontainer.UsedCodeGen
	 * @see #setGenerator(UsedCodeGen)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getCodeGenerator_Generator()
	 * @model
	 * @generated
	 */
	UsedCodeGen getGenerator();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.CodeGenerator#getGenerator <em>Generator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Generator</em>' attribute.
	 * @see org.moflon.core.propertycontainer.UsedCodeGen
	 * @see #getGenerator()
	 * @generated
	 */
	void setGenerator(UsedCodeGen value);

	/**
	 * Returns the value of the '<em><b>Enforced</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Enforced</em>' attribute.
	 * @see #setEnforced(boolean)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getCodeGenerator_Enforced()
	 * @model default="false"
	 * @generated
	 */
	boolean isEnforced();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.CodeGenerator#isEnforced <em>Enforced</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Enforced</em>' attribute.
	 * @see #isEnforced()
	 * @generated
	 */
	void setEnforced(boolean value);

} // CodeGenerator
