/**
 * 
 */
package org.moflon.core.ui.handler.visualisation;

/**
 * Allows to enable the visualisation of the 1-neighbourhood of a selection for
 * metamodel and model visualiser.
 * 
 * @author Johannes Brandt
 *
 */
public class NeighbourhoodStrategyHandler extends VisConfigHandler {
	public static final String KEY = "1-NEIGHBOURHOOD";
	
	@Override
	protected String getKey() {
		return KEY;
	}	
	
	public static boolean getVisPreference() {
		return VisConfigHandler.getVisPreference(KEY);
	}
}
