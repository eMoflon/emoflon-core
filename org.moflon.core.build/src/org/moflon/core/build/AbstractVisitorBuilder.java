package org.moflon.core.build;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.gervarro.eclipse.workspace.util.AntPatternCondition;
import org.gervarro.eclipse.workspace.util.RelevantElementCollectingBuilder;
import org.gervarro.eclipse.workspace.util.RelevantElementCollector;
import org.gervarro.eclipse.workspace.util.VisitorCondition;
import org.moflon.core.utilities.ProblemMarkerUtil;
import org.moflon.core.utilities.WorkspaceHelper;

/**
 * Implementation base for builders that use a {@link VisitorCondition} to
 * identify to which changes they react
 *
 * @author Gergely Varró - Initial implementation
 * @author Roland Kluge - Migration to eMoflon::Core and documentation
 *
 */
public abstract class AbstractVisitorBuilder extends RelevantElementCollectingBuilder {

	/**
	 * The default project comparator used by this class
	 */
	public static final Comparator<IProject> DEFAULT_PROJECT_COMPARATOR = new NameBasedProjectComparator();

	/**
	 * @deprecated Use {@link #getProjectComparator()} instead
	 */
	@Deprecated // Since 2018-08-13, static modifier and initialization are kept for backward
				// compatibility
	public static Comparator<IProject> PROJECT_COMPARATOR = DEFAULT_PROJECT_COMPARATOR;

	/**
	 * @deprecated Use {@link #getTriggerProjects()} instead
	 */
	@Deprecated // Since 2018-08-13
	protected final TreeSet<IProject> triggerProjects = new TreeSet<>(PROJECT_COMPARATOR);

	/**
	 * Passes the given visitor condition to
	 * {@link RelevantElementCollectingBuilder}
	 *
	 * The visitor condition determines to which resource changes in the current
	 * project ({@link #getProject()}) this builder reacts.
	 *
	 * @param condition
	 *            the visitor condition
	 */
	protected AbstractVisitorBuilder(final VisitorCondition condition) {
		super(condition);
		PROJECT_COMPARATOR = getProjectComparator();
	}

	/**
	 * Returns all registered trigger projects.
	 *
	 * @return the trigger projects
	 * @see #addTriggerProject(IProject)
	 */
	public Collection<IProject> getTriggerProjects() {
		return Collections.unmodifiableSet(triggerProjects);
	}

	/**
	 * Add a trigger project.
	 *
	 * @param project
	 *            the new trigger project
	 * @return whether the list of trigger projects has changed, i.e., whether the
	 *         given project was *not* registered as a trigger project
	 *
	 * @see #calculateInterestingProjects()
	 */
	public final boolean addTriggerProject(final IProject project) {
		return triggerProjects.add(project);
	}

	/**
	 * Returns the project comparator that is used by this builder for iterating
	 * over trigger projects
	 *
	 * @return the project comparator
	 */
	public Comparator<IProject> getProjectComparator() {
		return DEFAULT_PROJECT_COMPARATOR;
	}

	/**
	 * This method is invoked after processing all resources (or changes in
	 * incremental mode)
	 */
	@Override
	protected void postprocess(final RelevantElementCollector buildVisitor, final int originalKind,
			final Map<String, String> builderArguments, final IProgressMonitor monitor) {

		final int kind = correctBuildTrigger(originalKind);

		if (getCommand().isBuilding(kind)) {
			super.postprocess(buildVisitor, kind, builderArguments, monitor);
		}

		if (buildVisitor.getRelevantDeltas().isEmpty() && isAutoOrIncrementalBuild(kind)) {

			final SubMonitor subMonitor = SubMonitor.convert(monitor, getTriggerProjects().size());
			try {
				for (final IProject project : getTriggerProjects()) {
					final boolean buildPerformed = performBuildForFirstMatchingTriggerProject(builderArguments, project,
							subMonitor);
					if (buildPerformed)
						break;
				}
			} catch (final CoreException e) {
				throw new RuntimeException(e.getMessage(), e);
			}

		}
	}

	/**
	 * Builds the first trigger project for which the set of relevant deltas is
	 * non-empty.
	 *
	 * The relevant {@link IResourceDelta} is determined by filtering
	 * {@link #getDelta(IProject)} according to the configured trigger condition
	 * {@link #getTriggerCondition(IProject)}.
	 *
	 * @param builderArguments the builder arguments
	 * @param triggerProject the trigger project
	 * @param monitor that shall make work one unit within this method
	 * @return true if the trigger project was built
	 * @throws CoreException
	 */
	private boolean performBuildForFirstMatchingTriggerProject(final Map<String, String> builderArguments,
			final IProject triggerProject, final SubMonitor monitor) throws CoreException {

		final RelevantElementCollector relevantElementCollector = new RelevantElementCollector(triggerProject,
				getTriggerCondition(triggerProject)) {
			public boolean handleResourceDelta(final IResourceDelta delta) {
				final int deltaKind = delta.getKind();
				if (deltaKind == IResourceDelta.ADDED || deltaKind == IResourceDelta.CHANGED) {
					super.handleResourceDelta(delta);
				}
				return false;
			}
		};

		final IResourceDelta delta = getDelta(triggerProject);

		if (delta != null) {
			delta.accept(relevantElementCollector, IResource.NONE);
			if (!relevantElementCollector.getRelevantDeltas().isEmpty()) {
				// Perform a full build if a triggering project changed
				build(FULL_BUILD, builderArguments, monitor.split(1));
				return true;
			} else {
				monitor.worked(1);
			}
		} else {
			monitor.worked(1);
		}

		return false;
	}

	/**
	 * Returns the set of {@link AntPatternCondition} that filters a given
	 * {@link IResourceDelta} (of a trigger project) for relevant resource change
	 * (see {@link RelevantElementCollector#getRelevantDeltas()}. modifications
	 *
	 * @param project
	 *            the currently processed trigger project
	 * @return the trigger condition that is specific to the given trigger project
	 */
	abstract protected AntPatternCondition getTriggerCondition(final IProject project);

	/**
	 * Creates a substitute for the given build kind if based on additional
	 * information.
	 *
	 * @param kind
	 *            the build kind
	 * @return the corrected build kind
	 */
	protected int correctBuildTrigger(final int kind) {
		if (kind == INCREMENTAL_BUILD && getContext().getRequestedConfigs().length == 0) {
			return AUTO_BUILD;
		} else {
			return kind;
		}
	}

	/**
	 * Returns true if the given build kind is equal to
	 * {@link IncrementalProjectBuilder#INCREMENTAL_BUILD} or
	 * {@link IncrementalProjectBuilder#AUTO_BUILD}
	 *
	 * @param kind
	 *            the build kind
	 * @return if the given build kind reflects an incremental or auto-build
	 */
	protected static final boolean isAutoOrIncrementalBuild(final int kind) {
		return kind == INCREMENTAL_BUILD || kind == AUTO_BUILD;
	}

	/**
	 * Returns true if the given build kind is equal to
	 * {@link IncrementalProjectBuilder#FULL_BUILD}
	 *
	 * @param kind
	 *            the build kind
	 * @return if the given build kind reflects a full build
	 */
	protected static final boolean isFullBuild(final int kind) {
		return kind == FULL_BUILD;
	}

	protected void processResourceDelta(final IResourceDelta delta, final int kind, final Map<String, String> args,
			final IProgressMonitor monitor) {
		if (delta.getKind() != IResourceDelta.REMOVED) {
			super.processResourceDelta(delta, kind, args, monitor);
		}
	}

	/**
	 * Returns the list of trigger projects.
	 *
	 * A trigger project is also called interesting project and will be part of the
	 * list of projects returned by {@link #build(int, Map, IProgressMonitor)}
	 */
	@Override
	protected final IProject[] calculateInterestingProjects() {
		IProject[] result = new IProject[triggerProjects.size()];
		return triggerProjects.toArray(result);
	}

	/**
	 * Creates an error message for each leaf status (i.e., non-{@link MultiStatus})
	 * in the given {@link Status} object.
	 *
	 * The status is only reported if it is not {@link IStatus#OK}.
	 *
	 * @param status
	 *            the status to report
	 * @param resource
	 *            the resource to which the error markers shall be attached
	 * @throws CoreException
	 */
	protected void processProblemStatus(final IStatus status, final IResource resource) throws CoreException {
		if (status.isOK()) {
			return;
		}
		if (status.isMultiStatus()) {
			final MultiStatus multiStatus = (MultiStatus) status;
			for (final IStatus child : multiStatus.getChildren()) {
				processProblemStatus(child, resource);
			}
		} else {
			ProblemMarkerUtil.createProblemMarker(resource, status.getMessage(),
					ProblemMarkerUtil.convertStatusSeverityToMarkerSeverity(status.getSeverity()),
					resource.getProjectRelativePath().toString());
		}
	}

	/**
	 * Removes all problem markers of the types
	 * {@link WorkspaceHelper#MOFLON_PROBLEM_MARKER_ID} and
	 * {@link WorkspaceHelper#INJECTION_PROBLEM_MARKER_ID} from the current project
	 * (see {@link #getProject()}.
	 *
	 * @throws CoreException
	 *             if removing the problem markers fails
	 */
	protected void deleteProblemMarkers() throws CoreException {
		getProject().deleteMarkers(WorkspaceHelper.MOFLON_PROBLEM_MARKER_ID, false, IResource.DEPTH_INFINITE);
		getProject().deleteMarkers(WorkspaceHelper.INJECTION_PROBLEM_MARKER_ID, false, IResource.DEPTH_INFINITE);
	}
}
