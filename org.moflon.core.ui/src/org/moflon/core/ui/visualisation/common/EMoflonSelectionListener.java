package org.moflon.core.ui.visualisation.common;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.moflon.core.ui.EMoflonView;
import org.moflon.core.utilities.LogUtils;

public class EMoflonSelectionListener implements ISelectionListener {

	private static Logger logger = Logger.getLogger(EMoflonSelectionListener.class);
	protected final EMoflonView emoflonView;
	
	public EMoflonSelectionListener(final EMoflonView emoflonView) {
		this.emoflonView = emoflonView;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		for(EMoflonViewVisualizer visualizer : emoflonView.getVisualizerPlugins()) {
			if(visualizer.supportsSelection(part, selection)) {
				try {
					visualizer.renderView(emoflonView, part, selection);
				} catch(Exception e) {
					LogUtils.error(logger, e);
				}
				
			}
		}
	}


}
