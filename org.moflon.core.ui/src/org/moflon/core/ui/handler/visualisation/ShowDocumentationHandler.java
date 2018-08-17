package org.moflon.core.ui.handler.visualisation;

/**
 * @author Johannes Brandt
 *
 */
public class ShowDocumentationHandler extends VisConfigHandler {
	public static final String KEY = "SHOW_DOCUMENTATION";

	@Override
	protected String getKey() {
		return KEY;
	}

	public static boolean getVisPreference() {
		return VisConfigHandler.getVisPreference(KEY);
	}
}
