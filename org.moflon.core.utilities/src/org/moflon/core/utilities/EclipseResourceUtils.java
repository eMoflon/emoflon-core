package org.moflon.core.utilities;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

/**
 * This utility class contains a collection of utility methods for working with
 * resources ({@link IFile}, {@link IFolder}, {@link IResource}).
 * 
 * @author Roland Kluge - Initial implementation
 *
 */
public final class EclipseResourceUtils {
	private EclipseResourceUtils() {
		throw new UtilityClassNotInstantiableException();
	}

	/**
	 * Returns the basename of the given {@link IResource}
	 * 
	 * The basename is obtained by removing everything after the last '.' in the
	 * name of the resource If the last '.' occurs at position 0, then the name is
	 * returned unchanged to protect hidden files.
	 */
	public static String getBasename(final IResource resource) {
		final String name = resource.getName();
		final int dotIndex = name.lastIndexOf('.');
		if (dotIndex > 0) // Not >= 0 to avoid trimming names of hidden files
		{
			return name.substring(0, dotIndex);
		} else {
			return name;
		}
	}

}
