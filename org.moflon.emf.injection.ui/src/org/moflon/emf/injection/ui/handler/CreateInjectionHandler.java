package org.moflon.emf.injection.ui.handler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moflon.core.ui.AbstractCommandHandler;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.emf.injection.injectionLanguage.ClassDeclaration;
import org.moflon.emf.injection.injectionLanguage.InjectionFile;
import org.moflon.emf.injection.unparsing.InjectionFileBuilder;
import org.moflon.emf.injection.unparsing.InjectionFileUnparser;

/**
 * UI handler that triggers the extraction/update of .inject files.
 */
public class CreateInjectionHandler extends AbstractCommandHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		final IFile javaFile;
		if (selection instanceof IStructuredSelection) {
			javaFile = extractFileFromStructuredSelection(selection);
		} else if (selection instanceof ITextSelection) {
			final IEditorPart editor = HandlerUtil.getActiveEditor(event);
			javaFile = (IFile) editor.getEditorInput().getAdapter(IFile.class);
		} else {
			javaFile = null;
		}

		if (javaFile != null) {
			WorkspaceJob job = new WorkspaceJob("Extracting injection from " + javaFile) {

				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					return extractInjectionInteractively(javaFile, monitor);
				}
			};
			job.setUser(true);
			job.setRule(javaFile.getProject());
			job.schedule();
		}

		return null;
	}

	private IFile extractFileFromStructuredSelection(final ISelection selection) {
		IFile javaFile = null;
		final Object o = ((IStructuredSelection) selection).getFirstElement();
		if (o instanceof IFile) {
			javaFile = (IFile) o;
		} else if (o instanceof IJavaElement) {
			try {
				javaFile = getFileFromJavaElement((IJavaElement) o);
			} catch (final JavaModelException e) {
				logger.error("[Injection] Problem while reading Java file", e);
			}
		}
		return javaFile;
	}

	public IStatus extractInjectionNonInteractively(final IFile javaFile) {
		return this.extractInjection(javaFile, false, new NullProgressMonitor());
	}

	public IStatus extractInjectionInteractively(final IFile javaFile, IProgressMonitor monitor) {
		return this.extractInjection(javaFile, true, monitor);
	}

	private IStatus extractInjection(final IFile javaFile, final boolean runsInteractive,
			final IProgressMonitor monitor) {
		try {
			final SubMonitor subMon = SubMonitor.convert(monitor, "Extracting injection from " + javaFile, 100);
			// Stream needs to be re-opened for each check!
			if (isEmfUtilityClass(javaFile.getContents()) || isEmfUtilityInterface(javaFile.getContents())) {
				final String message = "It is not possible to create injections from EMF utility classes/interfaces.";
				if (runsInteractive) {
					MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Injection error", message);
					logger.info(message + ". File: '" + javaFile.getFullPath() + "'.");
				} else {
					logger.debug(message + ". File: '" + javaFile.getFullPath() + "'.");
				}
				subMon.worked(100);
			} else {
				final IPath fullInjectionPath = WorkspaceHelper.getPathToInjection(javaFile);
				final String fullyQualifiedClassname = WorkspaceHelper.getFullyQualifiedClassName(javaFile);
				final MultiStatus creationStatus = new MultiStatus(WorkspaceHelper.getPluginId(getClass()), 0,
						"Problem during injection extraction", null);
				final InjectionFile injectionFile = InjectionFileBuilder.createInjectionFile(javaFile, creationStatus);
				if (creationStatus.matches(IStatus.ERROR)) {
					return creationStatus;
				}

				subMon.worked(50);

				if (hasModelOrMembersCode(injectionFile)) {
					final String injContent = InjectionFileUnparser.unparseFile(injectionFile);
					final ByteArrayInputStream contentStream = new ByteArrayInputStream(injContent.getBytes());

					final IFile injectionIFile = javaFile.getProject().getFile(fullInjectionPath);
					final String className = javaFile.getName().replace(".java", "");
					LogUtils.info(logger, "Creating injection file for class %s (FQN='%s').", className,
							fullyQualifiedClassname);
					if (injectionIFile.exists()) {
						injectionIFile.setContents(contentStream, true, true, subMon.split(50));
					} else {
						final IPath pathToParentOfInjectionFile = injectionIFile.getProjectRelativePath()
								.removeLastSegments(1);
						WorkspaceHelper.createFolderIfNotExists(
								javaFile.getProject().getFolder(pathToParentOfInjectionFile), subMon.split(20));
						injectionIFile.create(contentStream, true, subMon.split(30));
					}
				} else {
					logger.debug("Not creating injection file for  " + javaFile.getFullPath()
							+ " because no model code was found.");
					subMon.worked(50);
				}
			}
			return Status.OK_STATUS;
		} catch (final CoreException ex) {
			final String message = "Unable to create injection code for file " + javaFile + ". Reason: " + ex;
			logger.error(message);
			return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), message, ex);
		}
	}

	private boolean hasModelOrMembersCode(InjectionFile injectionFile2) {
		final ClassDeclaration classDeclaration = injectionFile2.getClassDeclaration();
		return hasClassInjectionDeclaration(classDeclaration) || !classDeclaration.getMethodDeclarations().isEmpty();
	}

	private boolean hasClassInjectionDeclaration(final ClassDeclaration classDeclaration) {
		return classDeclaration.getClassInjectionDeclaration() != null
				&& classDeclaration.getClassInjectionDeclaration().getBody() != null;
	}

	private boolean isEmfUtilityClass(final InputStream javaContentStream) {
		final Pattern isEmfUtilClassPattern = Pattern.compile(
				".*public\\s+class\\s+[a-zA-Z1-9<>]+\\s+extends\\s+(AdapterFactoryImpl|Switch<T>|EPackageImpl|EFactoryImpl)[^a-zA-Z].*");
		final Scanner s = new Scanner(javaContentStream);
		String match = s.findWithinHorizon(isEmfUtilClassPattern, 0);
		s.close();
		return match != null;
	}

	private boolean isEmfUtilityInterface(final InputStream javaContentStream) {
		final Pattern isEmfUtilInterfacePattern = Pattern
				.compile(".*public\\s+interface\\s+[a-zA-Z1-9]+\\s+extends\\s+(EPackage|EFactory)[^a-zA-Z].*");
		final Scanner s = new Scanner(javaContentStream);
		String match = s.findWithinHorizon(isEmfUtilInterfacePattern, 0);
		s.close();
		return match != null;
	}

	private IFile getFileFromJavaElement(final IJavaElement javaElement) throws JavaModelException {
		IFile file = null;
		final IJavaElement unit = javaElement;
		final IResource resource = unit.getCorrespondingResource();
		if (resource instanceof IFile) {
			file = (IFile) resource;
		}
		return file;
	}
}
