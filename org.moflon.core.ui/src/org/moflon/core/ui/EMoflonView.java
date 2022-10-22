package org.moflon.core.ui;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.moflon.core.ui.visualisation.common.EMoflonViewVisualizer;
import org.moflon.core.ui.visualisation.common.EMoflonWindowListener;
import org.moflon.core.utilities.ExtensionsUtil;
import org.moflon.core.utilities.LogUtils;

public class EMoflonView extends ViewPart {
	private static Logger logger = Logger.getLogger(EMoflonView.class);
	
	private static EMoflonWindowListener listener;
	protected Collection<EMoflonViewVisualizer> visualizerPlugins;
		
	private Label label;
	public EMoflonView() {
		super();
		listener = new EMoflonWindowListener(this);
		try {
			visualizerPlugins = ExtensionsUtil.collectExtensions(EMoflonViewVisualizer.PLUGIN_ID, "class", EMoflonViewVisualizer.class);
		} catch(Exception e) {
			visualizerPlugins = new LinkedList<>();
			LogUtils.error(logger, e);
		}
		
	}

	@Override
	public void createPartControl(Composite parent) {
		PlatformUI.getWorkbench().addWindowListener(listener);
		
		label = new Label(parent, 0);
        label.setText("Hello World");
		
	}

	@Override
	public void setFocus() {
		label.setFocus();
	}
	
	public Collection<EMoflonViewVisualizer> getVisualizerPlugins() {
		return visualizerPlugins;
	}

}
