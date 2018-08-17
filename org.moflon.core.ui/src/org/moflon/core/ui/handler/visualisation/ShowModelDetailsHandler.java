/**
 * 
 */
package org.moflon.core.ui.handler.visualisation;

/**
 * Handles the "Show Model Details" command.
 * 
 * @author Johannes Brandt
 *
 */
public class ShowModelDetailsHandler extends VisConfigHandler {
	public static final String KEY = "SHOW_MODEL_DETAILS";

	@Override
	protected String getKey() {
		return KEY;
	}

	public static boolean getVisPreference() {
		return VisConfigHandler.getVisPreference(KEY);
	}
}