package org.moflon.core.ui;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.moflon.core.utilities.LogUtils;

/**
 * Utilities for working with the Eclipse ID
 *
 * @author Roland Kluge - Initial implementation
 */
public class UiUtilities {
	private static Logger logger = Logger.getLogger(UiUtilities.class);

	/**
	 * Opens the specified wizard
	 *
	 * @param wizardId
	 * @param window
	 * @throws CoreException
	 *
	 * @see {@link #openWizard(String, IWorkbenchWindow, IStructuredSelection)}
	 */
	public static void openWizard(final String wizardId, final IWorkbenchWindow window) throws CoreException {
		openWizard(wizardId, window, null);
	}

	/**
	 * Opens the specified wizard and initializes it with the given selection.
	 *
	 * @param wizardId
	 * @param window
	 * @param selection
	 * @throws CoreException
	 */
	public static void openWizard(final String wizardId, final IWorkbenchWindow window,
			final IStructuredSelection selection) throws CoreException {
		// Search for wizard
		final IWorkbenchWizard wizard = window.getWorkbench().getNewWizardRegistry().findWizard(wizardId)
				.createWizard();

		// Initialize and open dialogue
		wizard.init(window.getWorkbench(), selection);
		final WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();

	}

	/**
	 * Opens an appropriate editor for the given file
	 * 
	 * @param window
	 *            the current window
	 * @param file
	 *            the file to open
	 */
	public static void openFileInEditor(final IWorkbenchWindow window, final File file) {
		final IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
		try {
			IDE.openEditorOnFileStore(window.getActivePage(), fileStore);
		} catch (final PartInitException e) {
			LogUtils.error(logger, e, "Unable to open file: " + file.getAbsolutePath());
		}
	}

	/**
	 * Returns an {@link ImageDescriptor} for an icon located at the given relative
	 * path within the given plugin
	 * 
	 * @param pluginId
	 *            the plugin ID
	 * @param pluginRelativePathToIcon
	 *            the plugin-relative path to the icon
	 * @return the {@link ImageDescriptor} of the icon
	 */
	public static ImageDescriptor getImage(final String pluginId, final String pluginRelativePathToIcon) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, pluginRelativePathToIcon);
	}

	/**
	 * Open the default editor for a file in the current workbench page
	 * 
	 * @param file
	 *            the file to open
	 */
	public static void openDefaultEditorForFile(final IFile file) {
		Display.getDefault().asyncExec(() -> {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
			try {
				page.openEditor(new FileEditorInput(file), desc.getId());
			} catch (final Exception e) {
				LogUtils.error(logger, e, "Unable to open " + file);
			}
		});
	}

}
