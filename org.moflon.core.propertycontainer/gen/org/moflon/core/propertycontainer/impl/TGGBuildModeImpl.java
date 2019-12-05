/**
 */
package org.moflon.core.propertycontainer.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.moflon.core.propertycontainer.BuildMode;
import org.moflon.core.propertycontainer.PropertycontainerPackage;
import org.moflon.core.propertycontainer.TGGBuildMode;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>TGG Build Mode</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.impl.TGGBuildModeImpl#getBuildMode <em>Build Mode</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.impl.TGGBuildModeImpl#getDescription <em>Description</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TGGBuildModeImpl extends EObjectImpl implements TGGBuildMode {
	/**
	 * The default value of the '{@link #getBuildMode() <em>Build Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBuildMode()
	 * @generated
	 * @ordered
	 */
	protected static final BuildMode BUILD_MODE_EDEFAULT = BuildMode.ALL;

	/**
	 * The cached value of the '{@link #getBuildMode() <em>Build Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBuildMode()
	 * @generated
	 * @ordered
	 */
	protected BuildMode buildMode = BUILD_MODE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = "[Controls which TGG operationalizations are generated]";

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TGGBuildModeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PropertycontainerPackage.Literals.TGG_BUILD_MODE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BuildMode getBuildMode() {
		return buildMode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBuildMode(BuildMode newBuildMode) {
		BuildMode oldBuildMode = buildMode;
		buildMode = newBuildMode == null ? BUILD_MODE_EDEFAULT : newBuildMode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PropertycontainerPackage.TGG_BUILD_MODE__BUILD_MODE,
					oldBuildMode, buildMode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PropertycontainerPackage.TGG_BUILD_MODE__DESCRIPTION,
					oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case PropertycontainerPackage.TGG_BUILD_MODE__BUILD_MODE:
			return getBuildMode();
		case PropertycontainerPackage.TGG_BUILD_MODE__DESCRIPTION:
			return getDescription();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case PropertycontainerPackage.TGG_BUILD_MODE__BUILD_MODE:
			setBuildMode((BuildMode) newValue);
			return;
		case PropertycontainerPackage.TGG_BUILD_MODE__DESCRIPTION:
			setDescription((String) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case PropertycontainerPackage.TGG_BUILD_MODE__BUILD_MODE:
			setBuildMode(BUILD_MODE_EDEFAULT);
			return;
		case PropertycontainerPackage.TGG_BUILD_MODE__DESCRIPTION:
			setDescription(DESCRIPTION_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case PropertycontainerPackage.TGG_BUILD_MODE__BUILD_MODE:
			return buildMode != BUILD_MODE_EDEFAULT;
		case PropertycontainerPackage.TGG_BUILD_MODE__DESCRIPTION:
			return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (buildMode: ");
		result.append(buildMode);
		result.append(", description: ");
		result.append(description);
		result.append(')');
		return result.toString();
	}

} //TGGBuildModeImpl
