package org.moflon.emf.build;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class MonitoredMetamodelLoader extends GenericMonitoredResourceLoader {
	private static final String TASK_NAME = "Metamodel loading";

	public MonitoredMetamodelLoader(final ResourceSet resourceSet, final IFile ecoreFile) {
		super(resourceSet, ecoreFile);
	}

	@Override
	public String getTaskName() {
		return TASK_NAME;
	}

	protected IStatus preprocessResourceSet(final IProgressMonitor monitor) {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Preprocessing resource set", 40);
		final IStatus preprocessingStatus = super.preprocessResourceSet(subMon.split(15));
		if (preprocessingStatus.matches(IStatus.ERROR | IStatus.CANCEL)) {
			return preprocessingStatus;
		}
		return preprocessingStatus;
	}
}
