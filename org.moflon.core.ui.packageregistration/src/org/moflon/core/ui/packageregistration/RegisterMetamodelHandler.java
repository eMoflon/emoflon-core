/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.moflon.core.ui.packageregistration;

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moflon.core.ui.AbstractCommandHandler;

/**
 * This class provides the front-end capabilities for registering an EMF model.
 *
 * @author Dimitrios Kolovos - initial API and implementation
 * @author Roland Kluge - Adjustment to eMoflon::Core
 */
public class RegisterMetamodelHandler extends AbstractCommandHandler {

	/**
	 * Invokes {@link #registerMetamodelInFile(IFile)} for each {@link IFile} in the selection
	 * @param event the event to handle
	 * @param {@link AbstractCommandHandler#DEFAULT_HANDLER_RESULT}
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);

		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			final Iterator<?> it = structuredSelection.iterator();
			while (it.hasNext()) {
				final IFile file = (IFile) it.next();
				registerMetamodelInFile(file);
			}
		}

		return AbstractCommandHandler.DEFAULT_HANDLER_RESULT;
	}

	/**
	 * Uses {@link EmfRegistryManager} to register the given {@link IFile}
	 * @param file the file to register
	 */
	private void registerMetamodelInFile(final IFile file) {
		final String fileName = file.getFullPath().toOSString();
		try {
			EmfRegistryManager.getInstance().registerMetamodel(fileName);
			logger.info("Metamodel " + fileName + " registered successfully");
		} catch (final Exception ex) {
			logger.error("Metamodel " + fileName + " could not be registered", ex);
		}
	}

}
