package org.moflon.core.ui.visualisation.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.moflon.core.ui.EMoflonView;

public class EMoflonWindowListener implements IWindowListener {
	private static Logger logger = Logger.getLogger(EMoflonWindowListener.class);
	
	protected final EMoflonView emoflonView;
	
	Map<String, IPartListener> partListeners = Collections.synchronizedMap(new HashMap<>());
	Map<String, EMoflonSelectionListener> listeners = Collections.synchronizedMap(new HashMap<>());
	
	public EMoflonWindowListener(final EMoflonView emoflonView) {
		this.emoflonView = emoflonView;
	}
	
	@Override
	public void windowOpened(IWorkbenchWindow window) {
		if(window.getActivePage() == null)
			return;
		
		String id = window.getActivePage().getLabel();
		IPartListener listener = partListeners.get(id);
		if(listener != null)
			return;
		
		listener = new EMoflonPartListener(emoflonView, listeners);
		partListeners.put(id, listener);
		
		window.getActivePage().addPartListener(listener);
	}
	
	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		if(window.getActivePage() == null)
			return;
		
		String id = window.getActivePage().getLabel();
		IPartListener listener = partListeners.get(id);
		if(listener == null)
			return;
		
		window.getActivePage().removePartListener(listener);
		partListeners.remove(id);
	}
	
	@Override
	public void windowClosed(IWorkbenchWindow window) {
		if(window.getActivePage() == null)
			return;
		
		String id = window.getActivePage().getLabel();
		IPartListener listener = partListeners.get(id);
		if(listener == null)
			return;
		
		window.getActivePage().removePartListener(listener);
		partListeners.remove(id);
	}
	
	@Override
	public void windowActivated(IWorkbenchWindow window) {
		if(window.getActivePage() == null)
			return;
					
		String id = window.getActivePage().getLabel();
		IPartListener listener = partListeners.get(id);
		if(listener != null)
			return;
		
		listener = new EMoflonPartListener(emoflonView, listeners);
		partListeners.put(id, listener);
		
		window.getActivePage().addPartListener(listener);
	}

}
