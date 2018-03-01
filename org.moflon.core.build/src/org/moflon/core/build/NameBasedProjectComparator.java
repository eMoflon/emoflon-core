package org.moflon.core.build;

import java.util.Comparator;

import org.eclipse.core.resources.IProject;

/**
 * A {@link Comparator} for {@link IProject}s that uses the projects' names for
 * comparison
 * 
 */
public final class NameBasedProjectComparator implements Comparator<IProject> {
	@Override
	public int compare(IProject left, IProject right) {
		return left.getName().compareTo(right.getName());
	}
}