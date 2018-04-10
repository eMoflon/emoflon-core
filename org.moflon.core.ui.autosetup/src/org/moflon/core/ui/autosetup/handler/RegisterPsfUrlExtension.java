package org.moflon.core.ui.autosetup.handler;

import java.net.URL;

public interface RegisterPsfUrlExtension {
	/**
	 * Get the label to be displayed in the menu.
	 * 
	 * @return A string represented the label to be displayed for the PSF in the
	 *         menu.
	 */
	String getLabel();

	/**
	 * Get the URL to be displayed in the menu.
	 * 
	 * @return The URL to the PSF to be imported when the entry is selected.
	 */
	URL getUrl();
}
