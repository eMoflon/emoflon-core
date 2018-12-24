package org.moflon.core.ui.autosetup.handbook;

import java.net.MalformedURLException;
import java.net.URL;

import org.moflon.core.ui.autosetup.handler.RegisterPsfUrlExtension;

public abstract class SokobanHandbookPsfUrlRegistration implements RegisterPsfUrlExtension {
	protected static final String PREFIX = "I. Sokoban Example: ";
	private String label;
	private String url;

	public SokobanHandbookPsfUrlRegistration(int part, String url) {
		this(PREFIX + part, url);
	}
	
	public SokobanHandbookPsfUrlRegistration(String label, String url) {
		this.url = url;
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public URL getUrl() {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}
}
