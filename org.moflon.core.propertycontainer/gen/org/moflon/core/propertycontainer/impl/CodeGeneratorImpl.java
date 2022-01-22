/**
 */
package org.moflon.core.propertycontainer.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.moflon.core.propertycontainer.CodeGenerator;
import org.moflon.core.propertycontainer.PropertycontainerPackage;
import org.moflon.core.propertycontainer.UsedCodeGen;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Code Generator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.moflon.core.propertycontainer.impl.CodeGeneratorImpl#getGenerator <em>Generator</em>}</li>
 *   <li>{@link org.moflon.core.propertycontainer.impl.CodeGeneratorImpl#isEnforced <em>Enforced</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CodeGeneratorImpl extends EObjectImpl implements CodeGenerator {
	/**
	 * The default value of the '{@link #getGenerator() <em>Generator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGenerator()
	 * @generated
	 * @ordered
	 */
	protected static final UsedCodeGen GENERATOR_EDEFAULT = UsedCodeGen.EMF;

	/**
	 * The cached value of the '{@link #getGenerator() <em>Generator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGenerator()
	 * @generated
	 * @ordered
	 */
	protected UsedCodeGen generator = GENERATOR_EDEFAULT;

	/**
	 * The default value of the '{@link #isEnforced() <em>Enforced</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEnforced()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ENFORCED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isEnforced() <em>Enforced</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEnforced()
	 * @generated
	 * @ordered
	 */
	protected boolean enforced = ENFORCED_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CodeGeneratorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PropertycontainerPackage.Literals.CODE_GENERATOR;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public UsedCodeGen getGenerator() {
		return generator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setGenerator(UsedCodeGen newGenerator) {
		UsedCodeGen oldGenerator = generator;
		generator = newGenerator == null ? GENERATOR_EDEFAULT : newGenerator;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PropertycontainerPackage.CODE_GENERATOR__GENERATOR,
					oldGenerator, generator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isEnforced() {
		return enforced;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEnforced(boolean newEnforced) {
		boolean oldEnforced = enforced;
		enforced = newEnforced;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PropertycontainerPackage.CODE_GENERATOR__ENFORCED,
					oldEnforced, enforced));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case PropertycontainerPackage.CODE_GENERATOR__GENERATOR:
			return getGenerator();
		case PropertycontainerPackage.CODE_GENERATOR__ENFORCED:
			return isEnforced();
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
		case PropertycontainerPackage.CODE_GENERATOR__GENERATOR:
			setGenerator((UsedCodeGen) newValue);
			return;
		case PropertycontainerPackage.CODE_GENERATOR__ENFORCED:
			setEnforced((Boolean) newValue);
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
		case PropertycontainerPackage.CODE_GENERATOR__GENERATOR:
			setGenerator(GENERATOR_EDEFAULT);
			return;
		case PropertycontainerPackage.CODE_GENERATOR__ENFORCED:
			setEnforced(ENFORCED_EDEFAULT);
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
		case PropertycontainerPackage.CODE_GENERATOR__GENERATOR:
			return generator != GENERATOR_EDEFAULT;
		case PropertycontainerPackage.CODE_GENERATOR__ENFORCED:
			return enforced != ENFORCED_EDEFAULT;
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
		result.append(" (generator: ");
		result.append(generator);
		result.append(", enforced: ");
		result.append(enforced);
		result.append(')');
		return result.toString();
	}

} //CodeGeneratorImpl
