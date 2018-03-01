package org.moflon.core.ui.handler;

import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.moflon.core.utilities.ProgressMonitorUtil;
import org.moflon.core.utilities.WorkspaceHelper;

/**
 * This job touches a set of {@link IResource}s during
 * {@link #runInWorkspace(IProgressMonitor)}.
 *
 * @author Roland Kluge - Initial implementation
 * @see IResource#touch(IProgressMonitor)
 */
public class TouchResourceJob extends WorkspaceJob {
	/**
	 * The set of resources to be touched
	 */
	private final Collection<IResource> resources;

	public TouchResourceJob(final Collection<IResource> resources) {
		super("Touching selected resources");
		this.resources = resources;
	}

	@Override
	public IStatus runInWorkspace(final IProgressMonitor monitor) {
		final SubMonitor subMon = SubMonitor.convert(monitor, getName(), resources.size());
		final MultiStatus status = new MultiStatus(WorkspaceHelper.getPluginId(getClass()), 0,
				"Problems while touching resources", null);
		for (final IResource resource : resources) {
			try {
				resource.touch(subMon.split(1));
			} catch (final CoreException e) {
				status.add(new Status(IStatus.WARNING, WorkspaceHelper.getPluginId(getClass()),
						"Problem while touching " + resource));
			}
			ProgressMonitorUtil.checkCancellation(subMon);
		}
		return status.isOK() ? Status.OK_STATUS : status;
	}
}