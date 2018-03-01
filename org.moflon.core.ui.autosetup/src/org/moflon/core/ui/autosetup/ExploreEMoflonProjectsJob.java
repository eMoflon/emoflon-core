package org.moflon.core.ui.autosetup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.gervarro.eclipse.workspace.util.WorkspaceTaskJob;
import org.moflon.core.build.BuildUtilities;
import org.moflon.core.build.ProjectBuilderTask;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.ProgressMonitorUtil;
import org.moflon.core.utilities.WorkspaceHelper;

final class ExploreEMoflonProjectsJob extends Job {
	private static final String JUNIT_TEST_LAUNCHER_FILE_NAME_PATTERN = "^.*[Test|TestSuite].*[.]launch$";

	private static final Logger logger = Logger.getLogger(ExploreEMoflonProjectsJob.class);

	private final List<Job> jobs;

	private WorkspaceInstaller workspaceInstaller;

	ExploreEMoflonProjectsJob(String name, List<Job> jobs, String label, WorkspaceInstaller workspaceInstaller) {
		super(name);
		this.jobs = jobs;
		this.workspaceInstaller = workspaceInstaller;
	}

	@Override
	protected final IStatus run(final IProgressMonitor monitor) {
		final IProject[] graphicalMoflonProjects = workspaceInstaller.getProjectsToBuild();
		final List<IProject> mweProjects = getProjectsWithMweWorkflows();
		if (mweProjects.size() > 0) {
			try {
				final ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
				final ILaunchConfigurationType type = manager
						.getLaunchConfigurationType("org.eclipse.emf.mwe2.launch.Mwe2LaunchConfigurationType");
				final ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
				if (configurations.length > 0) {
					// (1) Closing projects with textual syntax (without workspace lock)
					jobs.add(new CloseProjectsJob("Closing projects", mweProjects));
					// (2) Building projects with graphical syntax (with workspace lock)
					prepareIncrementalProjectBuilderJob(jobs, graphicalMoflonProjects);
					// (3) Launching MWE2 workflows to generate Xtext metamodels (without workspace
					// lock)
					final Job mweWorkflowLauncher = new LaunchConfigurationRunnerJob("Launching MWE2 workflows",
							configurations);
					mweWorkflowLauncher.setRule(ResourcesPlugin.getWorkspace().getRoot());
					jobs.add(mweWorkflowLauncher);

					// (4) Opening projects with textual syntax (without workspace lock)
					jobs.add(new OpenProjectsJob("Opening projects", mweProjects));
					// (5) Building projects with textual syntax (with workspace lock)
					prepareIncrementalProjectBuilderJob(jobs, mweProjects);
				} else {
					// Building projects (with workspace lock)
					prepareIncrementalProjectBuilderJob(jobs, graphicalMoflonProjects);
					prepareIncrementalProjectBuilderJob(jobs, mweProjects);
				}
			} catch (final CoreException e) {
				// Building projects with graphical syntax (with workspace lock)
				prepareIncrementalProjectBuilderJob(jobs, graphicalMoflonProjects);
			}
		} else {
			// Building projects with graphical syntax (with workspace lock)
			prepareIncrementalProjectBuilderJob(jobs, graphicalMoflonProjects);
		}

		enqueueJUnitTestJob(jobs);
		return Status.OK_STATUS;
	}

	/**
	 * Searches for all projects that contain JUnit test configurations and enqueues
	 * a job that invokes all of these configurations into the given list of jobs
	 * 
	 * @param jobs
	 *            the job list
	 */
	private void enqueueJUnitTestJob(final List<Job> jobs) {
		final List<IFile> launchConfigurations = new LinkedList<IFile>();
		for (final IProject testProjectCandidate : getAllOpenProjectsInWorkspace()) {
			try {
				final List<IFile> selectedLaunchConfigurations = Arrays.asList(testProjectCandidate.members()).stream()//
						.filter(m -> m instanceof IFile) //
						.map(m -> (IFile) m.getAdapter(IFile.class))//
						.filter(f -> f.getName().matches(JUNIT_TEST_LAUNCHER_FILE_NAME_PATTERN))//
						.collect(Collectors.toList());
				launchConfigurations.addAll(selectedLaunchConfigurations);
			} catch (final CoreException e) {
				LogUtils.error(logger, e);
			}
		}
		if (!launchConfigurations.isEmpty()) {
			final Job testConfigurationJob = new Job("Launching test configurations") {

				@Override
				protected IStatus run(final IProgressMonitor monitor) {
					final MultiStatus result = new MultiStatus(WorkspaceHelper.getPluginId(getClass()), IStatus.OK,
							"Test configurations executed succesfully", null);
					final ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();
					final SubMonitor subMonitor = SubMonitor.convert(monitor, launchConfigurations.size());
					for (final IFile file : launchConfigurations) {
						final ILaunchConfiguration config = mgr.getLaunchConfiguration(file);
						final LaunchInvocationTask launchInvocationTask = new LaunchInvocationTask(config);
						result.add(launchInvocationTask.run(subMonitor.split(1)));
						ProgressMonitorUtil.checkCancellation(subMonitor);
					}
					return result;
				}
			};
			testConfigurationJob.setRule(ResourcesPlugin.getWorkspace().getRoot());
			jobs.add(testConfigurationJob);
		}
	}

	private List<IProject> getAllOpenProjectsInWorkspace() {
		return WorkspaceHelper.getAllProjectsInWorkspace().stream().filter(p -> p.isAccessible())
				.collect(Collectors.toList());
	}

	private List<IProject> getProjectsWithMweWorkflows() {
		return getAllOpenProjectsInWorkspace().stream().filter(p -> containsMwe2Files(p)).collect(Collectors.toList());
	}

	private boolean containsMwe2Files(final IProject project) {
		final List<IResource> mwe2Resources = new ArrayList<>();
		try {
			project.accept(new IResourceVisitor() {

				@Override
				public boolean visit(IResource resource) throws CoreException {
					// Quit after first identified resource
					if (!mwe2Resources.isEmpty())
						return false;

					if (resource.getName().endsWith("." + WorkspaceHelper.MWE2_FILE_EXTENSION))
						mwe2Resources.add(resource);

					return true;
				}
			});
			return !mwe2Resources.isEmpty();
		} catch (final CoreException e) {
			return false;
		}
	}

	private final void prepareIncrementalProjectBuilderJob(final List<Job> jobs, final Collection<IProject> projects) {
		projects.toArray(new IProject[projects.size()]);
	}

	private final void prepareIncrementalProjectBuilderJob(final List<Job> jobs, final IProject[] projects) {
		final IBuildConfiguration[] buildConfigurations = BuildUtilities
				.getDefaultBuildConfigurations(Arrays.asList(projects));
		if (buildConfigurations.length > 0) {
			final ProjectBuilderTask builder = new ProjectBuilderTask(buildConfigurations);
			jobs.add(new WorkspaceTaskJob(builder));
		}
	}
}