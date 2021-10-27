package org.moflon.core.ui.visualisation.common;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.graphstream.graph.Graph;

public interface EMoflonVisualizer {
	
	final public static String PLUGIN_ID = "org.moflon.core.ui.EMoflonVisualizer";
	
	public int getPriority();
	
	public boolean selectionIsRelevant(final IWorkbenchPart part, final ISelection selection);
	
	public Graph transformSelection(final IWorkbenchPart part, final ISelection selection);
}
