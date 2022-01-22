/**
 */
package org.moflon.core.propertycontainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Used Code Gen</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getUsedCodeGen()
 * @model
 * @generated
 */
public enum UsedCodeGen implements Enumerator {
	/**
	 * The '<em><b>EMF</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #EMF_VALUE
	 * @generated
	 * @ordered
	 */
	EMF(0, "EMF", "EMF"),

	/**
	 * The '<em><b>SMART EMF</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SMART_EMF_VALUE
	 * @generated
	 * @ordered
	 */
	SMART_EMF(1, "SMART_EMF", "SMART_EMF");

	/**
	 * The '<em><b>EMF</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #EMF
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int EMF_VALUE = 0;

	/**
	 * The '<em><b>SMART EMF</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SMART_EMF
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SMART_EMF_VALUE = 1;

	/**
	 * An array of all the '<em><b>Used Code Gen</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final UsedCodeGen[] VALUES_ARRAY = new UsedCodeGen[] { EMF, SMART_EMF, };

	/**
	 * A public read-only list of all the '<em><b>Used Code Gen</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<UsedCodeGen> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Used Code Gen</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static UsedCodeGen get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			UsedCodeGen result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Used Code Gen</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static UsedCodeGen getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			UsedCodeGen result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Used Code Gen</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static UsedCodeGen get(int value) {
		switch (value) {
		case EMF_VALUE:
			return EMF;
		case SMART_EMF_VALUE:
			return SMART_EMF;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private UsedCodeGen(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getLiteral() {
		return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}

} //UsedCodeGen
