package org.moflon.core.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.moflon.core.utilities.UtilityClassNotInstantiableException;

/**
 * Collection of utility/convenience methods regarding the Eclipse build process
 *
 * @author Roland Kluge - Initial implementation
 *
 */
public final class BuildUtilities {
	/**
	 * Disabled utility class constructor
	 */
	private BuildUtilities() {
		throw new UtilityClassNotInstantiableException();
	}

	public static final IBuildConfiguration[] getDefaultBuildConfigurations(final Collection<IProject> projects) {
		final List<IBuildConfiguration> result = new ArrayList<>(projects.size());
		for (final IProject project : projects) {
			try {
				result.add(project.getBuildConfig(IBuildConfiguration.DEFAULT_CONFIG_NAME));
			} catch (final CoreException e) {
				// Do nothing (i.e., ignore erroneous projects)
			}
		}
		return result.toArray(new IBuildConfiguration[result.size()]);
	}

}
