package org.moflon.core.preferences;

/**
 * Lists all types of Ecore URIs.
 * 
 * @author Roland Kluge - Initial implementation
 */
public enum PlatformUriType {
	/**
	 * Placeholder for platform:/resource URIs
	 */
	RESOURCE,
	/**
	 * Placeholder for platform:/plugin URIs
	 */
	PLUGIN;

	/**
	 * The default URI type to use
	 */
	public static final PlatformUriType DEFAULT = PLUGIN;
}
