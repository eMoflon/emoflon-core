package org.moflon.core.utilities;

/**
 * This exception may be thrown in constructors of utility classes.
 */
public class UtilityClassNotInstantiableException extends RuntimeException {
	public UtilityClassNotInstantiableException() {
		super("Invalid trial to instantiate a utility class.");
	}

	private static final long serialVersionUID = -4283010951140137990L;
}
