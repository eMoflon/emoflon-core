/*
 * Copyright (c) 2010-2012 Gergely Varro
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gergely Varro - Initial API and implementation
 */
package org.moflon.emf.build;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.gervarro.eclipse.task.ITask;
import org.moflon.core.preferences.EMoflonPreferencesStorage;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
import org.moflon.core.propertycontainer.MoflonPropertiesContainerHelper;
import org.moflon.core.utilities.MoflonConventions;
import org.moflon.core.utilities.WorkspaceHelper;

/**
 * This class defines a generic build process for eMoflon projects.
 *
 * @see #run(IProgressMonitor)
 */
public abstract class GenericMoflonProcess implements ITask {
	private final IFile ecoreFile;

	private final ResourceSet resourceSet;

	private final EMoflonPreferencesStorage preferencesStorage;

	private List<Resource> resources;

	private MoflonPropertiesContainer moflonProperties;

	public GenericMoflonProcess(final IFile ecoreFile, final ResourceSet resourceSet,
			final EMoflonPreferencesStorage preferencesStorage) {
		this.ecoreFile = ecoreFile;
		this.resourceSet = resourceSet;
		this.preferencesStorage = preferencesStorage;
	}

	/**
	 * This method is called inside {@link #run(IProgressMonitor)} after loading
	 * eMoflon properties and the metamodel
	 * 
	 * @param monitor
	 * @return
	 */
	abstract public IStatus processResource(final IProgressMonitor monitor);

	/**
	 * Loads moflon.properties.xmi and the project's meta-model from the specified
	 * Ecore file (see constructor).
	 *
	 * The control flow then continues to
	 * {@link GenericMoflonProcess#processResource(IProgressMonitor)}.
	 *
	 * @see #processResource(IProgressMonitor)
	 */
	@Override
	public final IStatus run(final IProgressMonitor monitor) {
		final SubMonitor subMon = SubMonitor.convert(monitor, getTaskName(), 10);

		if (!getEcoreFile().exists())
			return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()),
					String.format("Ecore file does not exist. Expected location: '%s'", getEcoreFile()));

		try {
			// (1) Loads moflon.properties file
			final IProject project = getEcoreFile().getProject();
			this.moflonProperties = MoflonPropertiesContainerHelper.loadOrCreatePropertiesContainer(getProject(),
					MoflonConventions.getDefaultMoflonPropertiesFile(project));

			subMon.worked(1);
			if (subMon.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
		} catch (final WrappedException wrappedException) {
			final Exception exception = wrappedException.exception();
			return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), exception.getMessage(),
					exception);
		} catch (final RuntimeException runtimeException) {
			return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), runtimeException.getMessage(),
					runtimeException);
		}

		// (2) Load metamodel
		final MonitoredMetamodelLoader metamodelLoader = new MonitoredMetamodelLoader(getResourceSet(), getEcoreFile(),
				getMoflonProperties());
		final IStatus metamodelLoaderStatus = metamodelLoader.run(subMon.split(2));
		if (subMon.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		if (metamodelLoaderStatus.matches(IStatus.ERROR)) {
			return metamodelLoaderStatus;
		}
		this.resources = metamodelLoader.getResources();

		return processResource(subMon.split(7));
	}

	public final IFile getEcoreFile() {
		return ecoreFile;
	}

	public final IProject getProject() {
		return this.ecoreFile.getProject();
	}

	public final ResourceSet getResourceSet() {
		return this.resourceSet;
	}

	/**
	 * Returns the configured preferences storage
	 */
	public EMoflonPreferencesStorage getPreferencesStorage() {
		return this.preferencesStorage;
	}

	/**
	 * Returns the stored properties.
	 *
	 * May be called only after executing {@link #run(IProgressMonitor)} and within
	 * {@link #processResource(IProgressMonitor)}.
	 */
	public final MoflonPropertiesContainer getMoflonProperties() {
		return moflonProperties;
	}

	public final Resource getEcoreResource() {
		return resources.get(0);
	}

	public final List<Resource> getAllResources() {
		return resources;
	}
}
