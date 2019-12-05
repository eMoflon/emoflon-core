/**
 */
package org.moflon.core.propertycontainer;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>TGG Build Mode</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.TGGBuildMode#getBuildMode <em>Build Mode</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.TGGBuildMode#getDescription <em>Description</em>}</li>
 * </ul>
 *
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getTGGBuildMode()
 * @model
 * @generated
 */
public interface TGGBuildMode extends EObject {
	/**
	 * Returns the value of the '<em><b>Build Mode</b></em>' attribute.
	 * The default value is <code>"BuildMode.ALL"</code>.
	 * The literals are from the enumeration {@link org.moflon.core.propertycontainer.BuildMode}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Build Mode</em>' attribute.
	 * @see org.moflon.core.propertycontainer.BuildMode
	 * @see #setBuildMode(BuildMode)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getTGGBuildMode_BuildMode()
	 * @model default="BuildMode.ALL" required="true" ordered="false"
	 * @generated
	 */
	BuildMode getBuildMode();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.TGGBuildMode#getBuildMode <em>Build Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Build Mode</em>' attribute.
	 * @see org.moflon.core.propertycontainer.BuildMode
	 * @see #getBuildMode()
	 * @generated
	 */
	void setBuildMode(BuildMode value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * The default value is <code>"[Controls which TGG operationalizations are generated]"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getTGGBuildMode_Description()
	 * @model default="[Controls which TGG operationalizations are generated]" ordered="false"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.moflon.core.propertycontainer.TGGBuildMode#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

} // TGGBuildMode
