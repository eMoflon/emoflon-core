package org.moflon.emf.injection.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.moflon.core.ui.AbstractCommandHandler;
import org.moflon.core.utilities.WorkspaceHelper;

/**
 * This component toggles between a Java class and its injection file (if
 * present).
 *
 * If the current selection is a Java file, the handler tries to find and open
 * the corresponding injection file. If the current selection is an injection
 * file, the handler opens the corresponding Java file.
 */
public class SwitchBetweenJavaAndInjectionFileHandler extends AbstractCommandHandler {

	private static final String JAVA_FILE_EXTENSION = "java";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IFile file = extractSelectedFile(event);

		try {
			if (isInjectionFile(file)) {
				final IPath javaFilePath = getPathToJavaFile(file);
				openInEditor(file.getProject().getFile(javaFilePath));
			} else if (isJavaFile(file)) {
				final IPath injectionFilePath = WorkspaceHelper.getPathToInjection(file);
				final IFile injectionFile = file.getProject().getFile(injectionFilePath);
				if (injectionFile.exists()) {
					openInEditor(injectionFile);
				} else {
					logger.debug("No injection for Java file " + file.getProjectRelativePath().toString());
				}
			}
		} catch (final CoreException e) {
			throw new ExecutionException(e.getMessage());
		}

		return null;
	}

	/**
	 * Returns the file name of the Java file for a given injection file.
	 *
	 * This method assumes that the first segment in the path is the injection
	 * folder ( {@link WorkspaceHelper#INJECTION_FOLDER}). The injection file name
	 * is obtained by replacing the first segment of the input file name with
	 * {@link WorkspaceHelper#GEN_FOLDER} and by replacing the file extension with
	 * {@link WorkspaceHelper#JAVA_FILE_EXTENSION}.
	 *
	 * The resulting path needs to be resolved against a project via
	 * {@link IProject#getFile(IPath)}.
	 *
	 * @param injectionFile
	 *            the injection file
	 * @return the path to the Java file
	 */
	public static IPath getPathToJavaFile(final IFile injectionFile) {
		final IPath packagePath = injectionFile.getProjectRelativePath().removeFirstSegments(1);
		final IPath pathToJavaFile = packagePath.removeFileExtension().addFileExtension(JAVA_FILE_EXTENSION);
		final IFolder genFolder = WorkspaceHelper.getGenFolder(injectionFile.getProject());
		final IPath fullJavaPath = genFolder.getProjectRelativePath().append(pathToJavaFile);
		return fullJavaPath;
	}

	/**
	 * Checks whether the given {@link IResource} appears to be a java file
	 * 
	 * @param resource
	 *            the resource to check. May be <code>null</code>
	 * @return true if the given resource is not null, an {@link IFile} and ends
	 *         with the default Java extension
	 */
	public static boolean isJavaFile(final IResource resource) {
		return WorkspaceHelper.isFile(resource) && resource.getName().endsWith("." + JAVA_FILE_EXTENSION);
	}

	/**
	 * Checks whether the given {@link IResource} appears to be a injection file
	 * 
	 * @param resource
	 *            the resource to check. May be <code>null</code>
	 * @return true if the given resource is not null, an {@link IFile} and ends
	 *         with the default Java extension
	 */
	public static boolean isInjectionFile(final IResource resource) {
		return WorkspaceHelper.isFile(resource)
				&& resource.getName().endsWith("." + WorkspaceHelper.INJECTION_FILE_EXTENSION);
	}
}
