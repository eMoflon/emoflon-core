package org.moflon.core.ui.autosetup.handbook;

import java.net.MalformedURLException;
import java.net.URL;

import org.moflon.core.ui.autosetup.handler.RegisterPsfUrlExtension;

public class RegisterPsfUrlForSokobanGui implements RegisterPsfUrlExtension {

	@Override
	public String getLabel() {
		return "Sokoban GUI";
	}

	@Override
	public URL getUrl() {
		try {
			return new URL(
					"https://raw.githubusercontent.com/eMoflon/emoflon-ibex-examples/sokoban-for-handbook-core/projectSet.psf");
		} catch (final MalformedURLException e) {
			// This shall never happen
			throw new IllegalArgumentException(e);
		}
	}
}
