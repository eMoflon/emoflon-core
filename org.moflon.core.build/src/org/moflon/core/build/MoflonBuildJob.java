package org.moflon.core.build;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.gervarro.eclipse.workspace.util.WorkspaceTaskJob;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.WorkspaceHelper;

public class MoflonBuildJob extends WorkspaceJob {
	private static final String JOB_NAME = "eMoflon Manual Build";

	private static final Logger logger = Logger.getLogger(MoflonBuildJob.class);

	private final List<IProject> projects;

	private int buildType;

	/**
	 * Constructor.
	 *
	 * @param name
	 *            the name to be handed to {@link WorkspaceJob}
	 * @param projects
	 *            the projects to be built
	 * @param buildType
	 *            the build type (as specified in {@link IncrementalProjectBuilder})
	 */
	public MoflonBuildJob(final List<IProject> projects, int buildType) {
		super(JOB_NAME);
		this.projects = projects;
		this.buildType = buildType;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		LogUtils.info(logger, "Manual build triggered (mode=%s, projects=%s)", mapBuildKindToName(this.buildType),
				this.projects);
		final MultiStatus resultStatus = new MultiStatus(WorkspaceHelper.getPluginId(getClass()), 0,
				"eMoflon Build Job failed", null);
		final List<Job> jobs = new ArrayList<>();
		final IBuildConfiguration[] buildConfigurations = BuildUtilities.getDefaultBuildConfigurations(projects);
		if (buildConfigurations.length > 0) {
			final ProjectBuilderTask metamodelBuilder = new ProjectBuilderTask(buildConfigurations, this.buildType);
			jobs.add(new WorkspaceTaskJob(metamodelBuilder));
		}

		try {
			TaskUtilities.processJobQueueAsUser(jobs);
		} catch (final CoreException e) {
			resultStatus.add(new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), IStatus.ERROR,
					getName() + "failed.", e));
		}

		return resultStatus.matches(Status.ERROR) ? resultStatus : Status.OK_STATUS;
	}

	/**
	 * Maps the Eclipse build type to a human-readable name
	 * 
	 * @param buildType
	 *            the build type
	 * @return the name
	 */
	private static String mapBuildKindToName(final int buildType) {
		switch (buildType) {
		case IncrementalProjectBuilder.AUTO_BUILD:
			return "auto";
		case IncrementalProjectBuilder.FULL_BUILD:
			return "full";
		case IncrementalProjectBuilder.CLEAN_BUILD:
			return "clean";
		case IncrementalProjectBuilder.INCREMENTAL_BUILD:
			return "incremental";
		default:
			return "Unknown build type: " + buildType;
		}
	}
}