package org.moflon.core.ui.visualisation.common;


import net.sourceforge.plantuml.util.DiagramTextIntentProvider;

public class EMoflonIntentProvider extends DiagramTextIntentProvider{

	public EMoflonIntentProvider() {
		super(new EMoflonVisualiser());
	}
	
}
