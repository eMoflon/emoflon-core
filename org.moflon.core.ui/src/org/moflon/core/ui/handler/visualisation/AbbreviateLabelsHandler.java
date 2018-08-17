/**
 * 
 */
package org.moflon.core.ui.handler.visualisation;

/**
 * Handles the "Abbreviate labels" command.
 * 
 * @author Johannes Brandt
 *
 */
public class AbbreviateLabelsHandler extends VisConfigHandler {
	public static final String KEY = "ABBR_LABELS";
	
	@Override
	protected String getKey() {
		return KEY;
	}	
	
	public static boolean getVisPreference() {
		return VisConfigHandler.getVisPreference(KEY);
	}
}
