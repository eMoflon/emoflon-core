package org.moflon.core.ui.autosetup.handler;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.moflon.core.ui.AbstractCommandHandler;
import org.moflon.core.ui.autosetup.WorkspaceInstaller;

public class InstallPsfFileHandler extends AbstractCommandHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.MULTI);
		dialog.setFilterExtensions(new String[] { "*.psf", "*.PSF" });
		dialog.setText("Choose your PSF file(s)");

		String open = dialog.open();
		if (open != null) {
			final String parentPath = dialog.getFilterPath();
			final List<String> selectedFileNames = Arrays.asList(dialog.getFileNames());
			final List<File> psfFiles = selectedFileNames.stream().map(s -> new File(new File(parentPath), s))
					.collect(Collectors.toList());

			new WorkspaceInstaller().installPsfFiles(psfFiles);
		}
		return null;
	}

}
