package org.moflon.core.ui.autosetup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.moflon.core.utilities.ProgressMonitorUtil;

/**
 * This {@link Job} invokes the preconfigured set of
 * {@link ILaunchConfiguration}s upon {@link #run(IProgressMonitor)}
 * 
 * @author Roland Kluge - Initial implementation
 */
public final class LaunchConfigurationRunnerJob extends Job {
	private final ILaunchConfiguration[] configurations;

	public LaunchConfigurationRunnerJob(String name, ILaunchConfiguration[] configurations) {
		super(name);
		this.configurations = configurations;
	}

	@Override
	public IStatus run(final IProgressMonitor monitor) {
		final SubMonitor mweWorkflowExecutionMonitor = SubMonitor.convert(monitor, "Executing MWE2 workflows",
				configurations.length);
		try {
			for (int i = 0; i < configurations.length; i++) {
				final ILaunchConfiguration config = configurations[i];
				final LaunchInvocationTask launchInvocationTask = new LaunchInvocationTask(config);
				launchInvocationTask.run(mweWorkflowExecutionMonitor.split(1));
				ProgressMonitorUtil.checkCancellation(mweWorkflowExecutionMonitor);
			}
		} finally {
			SubMonitor.done(monitor);
		}
		return Status.OK_STATUS;
	}
}