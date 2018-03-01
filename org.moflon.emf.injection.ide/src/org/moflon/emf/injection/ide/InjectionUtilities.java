package org.moflon.emf.injection.ide;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.moflon.core.utilities.UtilityClassNotInstantiableException;

/**
 * Convenience functions regarding injection handling
 *
 * @author Roland Kluge - Initial implementation
 */
public final class InjectionUtilities {

	/**
	 * Disabled utility class constructor
	 */
	private InjectionUtilities() {
		throw new UtilityClassNotInstantiableException();
	}

	/**
	 * Returns the fully-qualified name of the interface class belonging to the
	 * given {@link GenClass}
	 * 
	 * @param genClass
	 *            the {@link GenClass}
	 * @return the fully-qualified name
	 */
	public static final String getInterfaceName(final GenClass genClass) {
		return genClass.getGenPackage().getInterfacePackageName() + "." + genClass.getInterfaceName();
	}

	/**
	 * Returns the fully-qualified name of the implementation class belonging to the
	 * given {@link GenClass}
	 * 
	 * @param genClass
	 *            the {@link GenClass}
	 * @return the fully-qualified name
	 */
	public static final String getClassName(final GenClass genClass) {
		return genClass.getGenPackage().getClassPackageName() + "." + genClass.getClassName();
	}
}
