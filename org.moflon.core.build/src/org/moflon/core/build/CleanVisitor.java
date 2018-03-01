package org.moflon.core.build;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.gervarro.eclipse.workspace.util.VisitorCondition;

/**
 * A visitor that recursively deletes all resources that match an inclusion
 * condition and that do not match the exclusion condition
 *
 * @author Gergely Varró - Initial implementation
 * @author Roland Kluge - Exclusion condition and documentation
 */
public final class CleanVisitor implements IResourceVisitor {
	private final IProject project;

	private final VisitorCondition inclusionCondition;

	private final VisitorCondition exclusionCondition;

	/**
	 * Creates a visitor without exclusion condition
	 * 
	 * @param project
	 *            the project for which the visitor is used
	 * @param inclusionCondition
	 *            the inclusion condition
	 */
	public CleanVisitor(final IProject project, final VisitorCondition inclusionCondition) {
		this(project, inclusionCondition, new NullVisitorCondition());
	}

	/**
	 * Creates a visitor with inclusion exclusion condition
	 * 
	 * @param project
	 *            the project for which the visitor is used
	 * @param inclusionCondition
	 *            the inclusion condition
	 * @param exclusionCondition
	 *            the exclusion condition
	 */
	public CleanVisitor(final IProject project, final VisitorCondition inclusionCondition,
			final VisitorCondition exclusionCondition) {
		this.project = project;
		this.inclusionCondition = inclusionCondition;
		this.exclusionCondition = exclusionCondition;
	}

	@Override
	public boolean visit(final IResource resource) {
		final int resourceType = resource.getType();
		if (resourceType == IResource.PROJECT) {
			return resource.isAccessible() && resource.getProject() == project;
		} else if (resourceType != IResource.ROOT) {
			final String path = resource.getProjectRelativePath().toString();

			if (this.exclusionCondition.isExactMatch(path))
				return false;

			final boolean exactInclusionMatchFound = inclusionCondition.isExactMatch(path);
			if (exactInclusionMatchFound) {
				if (resource.isAccessible()) {
					try {
						resource.delete(true, new NullProgressMonitor());
					} catch (final CoreException e) {
						// Do nothing
					}
				}
				return false;
			} else {
				return inclusionCondition.isPrefixMatch(path);
			}
		}
		return false;

	}

	/**
	 * Returns the configured inclusion condition
	 */
	public VisitorCondition getInclusionCondition() {
		return inclusionCondition;
	}

	/**
	 * Returns the configured exclusion condition
	 */
	public VisitorCondition getExclusionCondition() {
		return exclusionCondition;
	}

	/**
	 * Null implementation of {@link VisitorCondition} that always returns false
	 */
	private static final class NullVisitorCondition implements VisitorCondition {
		@Override
		public boolean isPrefixMatch(String path) {
			return false;
		}

		@Override
		public boolean isExactMatch(String path) {
			return false;
		}
	}
}
