package org.moflon.core.utilities;

import java.util.Objects;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;

/**
 * Utility methods working with Ecore.
 */
public class EcoreUtils {

	/**
	 * Checks whether the given EClasses are the same or one is a super class of the
	 * other.
	 * 
	 * @param class1
	 *            an EClass
	 * @param class2
	 *            another EClass
	 * @return true if and only if an object could be an instance of both classes
	 */
	public static boolean areTypesCompatible(final EClass class1, final EClass class2) {
		Objects.requireNonNull(class1);
		Objects.requireNonNull(class2);
		return class1.equals(class2) || class1.getEAllSuperTypes().contains(class2)
				|| class2.getEAllSuperTypes().contains(class1);
	}

	/**
	 * Checks whether the given EClasses are equal based on their fully qualified
	 * names. This is sometimes necessary if a weaker equality is desirable (classes
	 * are loaded from different resources).
	 * 
	 * @param class1
	 *            an EClass
	 * @param class2
	 *            another EClass
	 * @return true if and only if both classes have the same fully qualified name.
	 */
	public static boolean equalsFQN(final EClass class1, final EClass class2) {
		if (class1 == null || class2 == null) {
			return false;
		}
		return getFQN(class1).equals(getFQN(class2));
	}

	/**
	 * Determine fully qualified name of given element by iterating through the
	 * package hierarchy.
	 *
	 * @param ENamedElement
	 *            the named element
	 * @return the fully qualified name of the element
	 */
	public static String getFQN(final ENamedElement element) {
		String fqn = element.getName();
		ENamedElement e = element;
		while (e.eContainer() != null) {
			e = (ENamedElement) e.eContainer();
			fqn = e.getName() + "." + fqn;
		}
		return fqn;
	}
}
