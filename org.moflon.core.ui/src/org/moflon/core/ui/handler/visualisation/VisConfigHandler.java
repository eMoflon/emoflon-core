package org.moflon.core.ui.handler.visualisation;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moflon.core.ui.AbstractCommandHandler;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public abstract class VisConfigHandler extends AbstractCommandHandler {
	private static final String PREFERENCE_ID = "EMOFLON_VIS_CONFIGURATION";

	protected void updateEditor(IEditorPart editor) {
		if (editor != null) {
			editor.setFocus();
		} else {
			logger.warn("Could not find any appropriate editor instance to "
					+ "initiate an update of the PlantUml viewpart.");
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		logger.debug("Executing " + getKey());

		// Toggle the state of the command and retrieve the old value of the state
		// (before it was clicked)
		Command command = event.getCommand();
		boolean toggleState = !HandlerUtil.toggleCommandState(command);

		// Retrieve / change / set configuration
		setVisPreference(toggleState);

		// Notify for change (indirectly, by setting focus to the editor, listeners are
		// fired, which in turn update the PlantUml view
		final IEditorPart linkedEditor = HandlerUtil.getActiveEditor(event);
		updateEditor(linkedEditor);

		return null;
	}

	protected void setVisPreference(boolean state) {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode(PREFERENCE_ID);

		Preferences preference = preferences.node(getKey());
		preference.putBoolean(getKey(), state);
		try {
			// Forces the application to save the preferences
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	protected static boolean getVisPreference(String key) {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode(PREFERENCE_ID);

		Preferences preference = preferences.node(key);
		return preference.getBoolean(key, false);
	}

	protected abstract String getKey();
}
