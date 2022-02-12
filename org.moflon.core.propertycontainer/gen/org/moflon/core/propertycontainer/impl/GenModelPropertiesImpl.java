/**
 */
package org.moflon.core.propertycontainer.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.moflon.core.propertycontainer.GenModelProperties;
import org.moflon.core.propertycontainer.PropertycontainerPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Gen Model Properties</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.impl.GenModelPropertiesImpl#isAutoReplaceGenModels <em>Auto Replace Gen Models</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.impl.GenModelPropertiesImpl#isGenerateNewGenModels <em>Generate New Gen Models</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GenModelPropertiesImpl extends EObjectImpl implements GenModelProperties {
	/**
	 * The default value of the '{@link #isAutoReplaceGenModels() <em>Auto Replace Gen Models</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAutoReplaceGenModels()
	 * @generated
	 * @ordered
	 */
	protected static final boolean AUTO_REPLACE_GEN_MODELS_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isAutoReplaceGenModels() <em>Auto Replace Gen Models</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAutoReplaceGenModels()
	 * @generated
	 * @ordered
	 */
	protected boolean autoReplaceGenModels = AUTO_REPLACE_GEN_MODELS_EDEFAULT;

	/**
	 * The default value of the '{@link #isGenerateNewGenModels() <em>Generate New Gen Models</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isGenerateNewGenModels()
	 * @generated
	 * @ordered
	 */
	protected static final boolean GENERATE_NEW_GEN_MODELS_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isGenerateNewGenModels() <em>Generate New Gen Models</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isGenerateNewGenModels()
	 * @generated
	 * @ordered
	 */
	protected boolean generateNewGenModels = GENERATE_NEW_GEN_MODELS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GenModelPropertiesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PropertycontainerPackage.Literals.GEN_MODEL_PROPERTIES;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isAutoReplaceGenModels() {
		return autoReplaceGenModels;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAutoReplaceGenModels(boolean newAutoReplaceGenModels) {
		boolean oldAutoReplaceGenModels = autoReplaceGenModels;
		autoReplaceGenModels = newAutoReplaceGenModels;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.GEN_MODEL_PROPERTIES__AUTO_REPLACE_GEN_MODELS, oldAutoReplaceGenModels,
					autoReplaceGenModels));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isGenerateNewGenModels() {
		return generateNewGenModels;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setGenerateNewGenModels(boolean newGenerateNewGenModels) {
		boolean oldGenerateNewGenModels = generateNewGenModels;
		generateNewGenModels = newGenerateNewGenModels;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.GEN_MODEL_PROPERTIES__GENERATE_NEW_GEN_MODELS, oldGenerateNewGenModels,
					generateNewGenModels));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case PropertycontainerPackage.GEN_MODEL_PROPERTIES__AUTO_REPLACE_GEN_MODELS:
			return isAutoReplaceGenModels();
		case PropertycontainerPackage.GEN_MODEL_PROPERTIES__GENERATE_NEW_GEN_MODELS:
			return isGenerateNewGenModels();
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
		case PropertycontainerPackage.GEN_MODEL_PROPERTIES__AUTO_REPLACE_GEN_MODELS:
			setAutoReplaceGenModels((Boolean) newValue);
			return;
		case PropertycontainerPackage.GEN_MODEL_PROPERTIES__GENERATE_NEW_GEN_MODELS:
			setGenerateNewGenModels((Boolean) newValue);
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
		case PropertycontainerPackage.GEN_MODEL_PROPERTIES__AUTO_REPLACE_GEN_MODELS:
			setAutoReplaceGenModels(AUTO_REPLACE_GEN_MODELS_EDEFAULT);
			return;
		case PropertycontainerPackage.GEN_MODEL_PROPERTIES__GENERATE_NEW_GEN_MODELS:
			setGenerateNewGenModels(GENERATE_NEW_GEN_MODELS_EDEFAULT);
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
		case PropertycontainerPackage.GEN_MODEL_PROPERTIES__AUTO_REPLACE_GEN_MODELS:
			return autoReplaceGenModels != AUTO_REPLACE_GEN_MODELS_EDEFAULT;
		case PropertycontainerPackage.GEN_MODEL_PROPERTIES__GENERATE_NEW_GEN_MODELS:
			return generateNewGenModels != GENERATE_NEW_GEN_MODELS_EDEFAULT;
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
		result.append(" (autoReplaceGenModels: ");
		result.append(autoReplaceGenModels);
		result.append(", generateNewGenModels: ");
		result.append(generateNewGenModels);
		result.append(')');
		return result.toString();
	}

} //GenModelPropertiesImpl
