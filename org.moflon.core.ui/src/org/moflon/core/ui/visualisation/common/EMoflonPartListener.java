package org.moflon.core.ui.visualisation.common;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.moflon.core.ui.EMoflonView;

public class EMoflonPartListener implements IPartListener {
	private static Logger logger = Logger.getLogger(EMoflonPartListener.class);
	
	protected final EMoflonView emoflonView;
	final protected Map<String, EMoflonSelectionListener> listeners;
	
	public EMoflonPartListener(final EMoflonView emoflonView, final Map<String, EMoflonSelectionListener> listeners) {
		this.emoflonView = emoflonView;
		this.listeners = listeners;
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		if(part == null || part.getSite() == null || part.getSite().getPage() == null)
			return;
		
		String id = part.getTitle()+part.getSite().getId()+part.getSite().getPage().getLabel();
		EMoflonSelectionListener listener = listeners.get(id);
		if(listener != null)
			return;
		
		listener = new EMoflonSelectionListener(emoflonView);
		listeners.put(id, listener);
		part.getSite().getPage().addPostSelectionListener(listener);
	}
	
	@Override
	public void partDeactivated(IWorkbenchPart part) {
		if(part == null || part.getSite() == null || part.getSite().getPage() == null)
			return;
		
		String id = part.getTitle()+part.getSite().getId()+part.getSite().getPage().getLabel();
		EMoflonSelectionListener listener = listeners.get(id);
		if(listener == null)
			return;
		
		part.getSite().getPage().removePostSelectionListener(listener);
		listeners.remove(id);
	}
	
	@Override
	public void partClosed(IWorkbenchPart part) {
		if(part == null || part.getSite() == null || part.getSite().getPage() == null)
			return;
		
		String id = part.getTitle()+part.getSite().getId()+part.getSite().getPage().getLabel();
		EMoflonSelectionListener listener = listeners.get(id);
		if(listener == null)
			return;
		
		part.getSite().getPage().removePostSelectionListener(listener);
		listeners.remove(id);
	}
	
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		if(part == null || part.getSite() == null || part.getSite().getPage() == null)
			return;
		
		String id = part.getTitle()+part.getSite().getId()+part.getSite().getPage().getLabel();
		EMoflonSelectionListener listener = listeners.get(id);
		if(listener != null)
			return;
		
		listener = new EMoflonSelectionListener(emoflonView);
		listeners.put(id, listener);
		part.getSite().getPage().addPostSelectionListener(listener);
	}
	
	@Override
	public void partActivated(IWorkbenchPart part) {
		if(part == null || part.getSite() == null || part.getSite().getPage() == null)
			return;
		
		String id = part.getTitle()+part.getSite().getId()+part.getSite().getPage().getLabel();
		EMoflonSelectionListener listener = listeners.get(id);
		if(listener != null)
			return;
		
		listener = new EMoflonSelectionListener(emoflonView);
		listeners.put(id, listener);
		part.getSite().getPage().addPostSelectionListener(listener);
	}

}
