/**
 */
package org.moflon.core.propertycontainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>SDM Code Generator Ids</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.moflon.core.propertycontainer.PropertycontainerPackage#getSDMCodeGeneratorIds()
 * @model
 * @generated
 */
public enum SDMCodeGeneratorIds implements Enumerator {
	/**
	 * The '<em><b>DEMOCLES</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEMOCLES_VALUE
	 * @generated
	 * @ordered
	 */
	DEMOCLES(1, "DEMOCLES", "org.moflon.compiler.sdm.democles.DemoclesMethodBodyHandler"),

	/**
	 * The '<em><b>DEMOCLES ATTRIBUTES</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEMOCLES_ATTRIBUTES_VALUE
	 * @generated
	 * @ordered
	 */
	DEMOCLES_ATTRIBUTES(2, "DEMOCLES_ATTRIBUTES",
			"org.moflon.compiler.sdm.democles.attributes.AttributeConstraintCodeGeneratorConfig"),

	/**
	 * The '<em><b>DEMOCLES REVERSE NAVI</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEMOCLES_REVERSE_NAVI_VALUE
	 * @generated
	 * @ordered
	 */
	DEMOCLES_REVERSE_NAVI(3, "DEMOCLES_REVERSE_NAVI",
			"org.moflon.compiler.sdm.democles.reversenavigation.ReverseNavigationCodeGeneratorConfig");

	/**
	 * The '<em><b>DEMOCLES</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>DEMOCLES</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEMOCLES
	 * @model literal="org.moflon.compiler.sdm.democles.DemoclesMethodBodyHandler"
	 * @generated
	 * @ordered
	 */
	public static final int DEMOCLES_VALUE = 1;

	/**
	 * The '<em><b>DEMOCLES ATTRIBUTES</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>DEMOCLES ATTRIBUTES</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEMOCLES_ATTRIBUTES
	 * @model literal="org.moflon.compiler.sdm.democles.attributes.AttributeConstraintCodeGeneratorConfig"
	 * @generated
	 * @ordered
	 */
	public static final int DEMOCLES_ATTRIBUTES_VALUE = 2;

	/**
	 * The '<em><b>DEMOCLES REVERSE NAVI</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>DEMOCLES REVERSE NAVI</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DEMOCLES_REVERSE_NAVI
	 * @model literal="org.moflon.compiler.sdm.democles.reversenavigation.ReverseNavigationCodeGeneratorConfig"
	 * @generated
	 * @ordered
	 */
	public static final int DEMOCLES_REVERSE_NAVI_VALUE = 3;

	/**
	 * An array of all the '<em><b>SDM Code Generator Ids</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final SDMCodeGeneratorIds[] VALUES_ARRAY = new SDMCodeGeneratorIds[] { DEMOCLES, DEMOCLES_ATTRIBUTES,
			DEMOCLES_REVERSE_NAVI, };

	/**
	 * A public read-only list of all the '<em><b>SDM Code Generator Ids</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<SDMCodeGeneratorIds> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>SDM Code Generator Ids</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static SDMCodeGeneratorIds get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			SDMCodeGeneratorIds result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>SDM Code Generator Ids</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static SDMCodeGeneratorIds getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			SDMCodeGeneratorIds result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>SDM Code Generator Ids</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static SDMCodeGeneratorIds get(int value) {
		switch (value) {
		case DEMOCLES_VALUE:
			return DEMOCLES;
		case DEMOCLES_ATTRIBUTES_VALUE:
			return DEMOCLES_ATTRIBUTES;
		case DEMOCLES_REVERSE_NAVI_VALUE:
			return DEMOCLES_REVERSE_NAVI;
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
	private SDMCodeGeneratorIds(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
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

} //SDMCodeGeneratorIds
