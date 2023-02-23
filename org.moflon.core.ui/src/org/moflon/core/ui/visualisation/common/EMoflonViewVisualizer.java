package org.moflon.core.ui.visualisation.common;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.moflon.core.ui.EMoflonView;

public interface EMoflonViewVisualizer {
	
	final public static String PLUGIN_ID = "org.moflon.core.ui.EMoflonViewVisualizer";
	
	public boolean supportsSelection(IWorkbenchPart part, ISelection selection);
	
	public boolean renderView(EMoflonView emoflonView, IWorkbenchPart part, ISelection selection);
	
}
