/**
 */
package org.moflon.core.propertycontainer;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Gen Model Properties</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.GenModelProperties#isAutoReplaceGenModels <em>Auto Replace Gen Models</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.GenModelProperties#isGenerateNewGenModels <em>Generate New Gen Models</em>}</li>
 * </ul>
 *
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getGenModelProperties()
 * @model
 * @generated
 */
public interface GenModelProperties extends EObject {
	/**
	 * Returns the value of the '<em><b>Auto Replace Gen Models</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Auto Replace Gen Models</em>' attribute.
	 * @see #setAutoReplaceGenModels(boolean)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getGenModelProperties_AutoReplaceGenModels()
	 * @model default="true" required="true" ordered="false"
	 * @generated
	 */
	boolean isAutoReplaceGenModels();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.GenModelProperties#isAutoReplaceGenModels <em>Auto Replace Gen Models</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Auto Replace Gen Models</em>' attribute.
	 * @see #isAutoReplaceGenModels()
	 * @generated
	 */
	void setAutoReplaceGenModels(boolean value);

	/**
	 * Returns the value of the '<em><b>Generate New Gen Models</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Generate New Gen Models</em>' attribute.
	 * @see #setGenerateNewGenModels(boolean)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getGenModelProperties_GenerateNewGenModels()
	 * @model default="true"
	 * @generated
	 */
	boolean isGenerateNewGenModels();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.GenModelProperties#isGenerateNewGenModels <em>Generate New Gen Models</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Generate New Gen Models</em>' attribute.
	 * @see #isGenerateNewGenModels()
	 * @generated
	 */
	void setGenerateNewGenModels(boolean value);

} // GenModelProperties
