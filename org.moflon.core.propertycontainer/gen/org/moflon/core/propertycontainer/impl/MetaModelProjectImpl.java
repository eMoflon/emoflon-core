/**
 */
package org.moflon.core.propertycontainer.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.moflon.core.propertycontainer.MetaModelProject;
import org.moflon.core.propertycontainer.PropertycontainerPackage;
// <-- [user defined imports]
// [user defined imports] -->

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Meta Model Project</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.impl.MetaModelProjectImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.impl.MetaModelProjectImpl#getMetaModelProjectName <em>Meta Model Project Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MetaModelProjectImpl extends EObjectImpl implements MetaModelProject {
	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = "[Name of the corresponding metamodel project in the current workspace]";

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
	 * The default value of the '{@link #getMetaModelProjectName() <em>Meta Model Project Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMetaModelProjectName()
	 * @generated
	 * @ordered
	 */
	protected static final String META_MODEL_PROJECT_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMetaModelProjectName() <em>Meta Model Project Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMetaModelProjectName()
	 * @generated
	 * @ordered
	 */
	protected String metaModelProjectName = META_MODEL_PROJECT_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MetaModelProjectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PropertycontainerPackage.Literals.META_MODEL_PROJECT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.META_MODEL_PROJECT__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMetaModelProjectName() {
		return metaModelProjectName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMetaModelProjectName(String newMetaModelProjectName) {
		String oldMetaModelProjectName = metaModelProjectName;
		metaModelProjectName = newMetaModelProjectName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PropertycontainerPackage.META_MODEL_PROJECT__META_MODEL_PROJECT_NAME, oldMetaModelProjectName,
					metaModelProjectName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case PropertycontainerPackage.META_MODEL_PROJECT__DESCRIPTION:
			return getDescription();
		case PropertycontainerPackage.META_MODEL_PROJECT__META_MODEL_PROJECT_NAME:
			return getMetaModelProjectName();
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
		case PropertycontainerPackage.META_MODEL_PROJECT__DESCRIPTION:
			setDescription((String) newValue);
			return;
		case PropertycontainerPackage.META_MODEL_PROJECT__META_MODEL_PROJECT_NAME:
			setMetaModelProjectName((String) newValue);
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
		case PropertycontainerPackage.META_MODEL_PROJECT__DESCRIPTION:
			setDescription(DESCRIPTION_EDEFAULT);
			return;
		case PropertycontainerPackage.META_MODEL_PROJECT__META_MODEL_PROJECT_NAME:
			setMetaModelProjectName(META_MODEL_PROJECT_NAME_EDEFAULT);
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
		case PropertycontainerPackage.META_MODEL_PROJECT__DESCRIPTION:
			return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
		case PropertycontainerPackage.META_MODEL_PROJECT__META_MODEL_PROJECT_NAME:
			return META_MODEL_PROJECT_NAME_EDEFAULT == null ? metaModelProjectName != null
					: !META_MODEL_PROJECT_NAME_EDEFAULT.equals(metaModelProjectName);
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

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (description: ");
		result.append(description);
		result.append(", MetaModelProjectName: ");
		result.append(metaModelProjectName);
		result.append(')');
		return result.toString();
	}
	// <-- [user code injected with eMoflon]

	// [user code injected with eMoflon] -->
} //MetaModelProjectImpl
