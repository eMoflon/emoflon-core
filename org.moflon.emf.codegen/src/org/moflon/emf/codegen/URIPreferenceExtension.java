package org.moflon.emf.codegen;

import org.eclipse.emf.common.util.URI;
import org.moflon.core.preferences.PlatformUriType;

/**
 * Interface that allows extensions to declare their preferred platform:/ {@link URI} type
 *   
 * @author Anthony Anjorin - Initial implementation
 * @author Roland Kluge - Docu
 */
public interface URIPreferenceExtension {
	
	/**
	 * Returns the preferred platform:/ {@link URI} type to be used inside the eMoflon build process
	 * @return the {@link URI} type
	 */
	public PlatformUriType getPlatformURIType();
}
