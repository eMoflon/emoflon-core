package org.moflon.core.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

/**
 * Super class for all command handlers.
 *
 * @author Roland Kluge - Initial implementation
 */
public abstract class AbstractCommandHandler extends org.eclipse.core.commands.AbstractHandler {

	protected Logger logger;

	/**
	 * This value should be used as standard return value for the overridden
	 * {@link IHandler#execute(ExecutionEvent)}
	 */
	public static final Object DEFAULT_HANDLER_RESULT = null;

	/**
	 * Initializes the logger of the subclass according to the dynamic type of the
	 * subclass
	 */
	public AbstractCommandHandler() {
		this.logger = Logger.getLogger(this.getClass());
	}

	/**
	 * Tries to extract the project(s) from the given element.
	 *
	 * For files, the containing project is returned. For working sets, the
	 * contained projects are returned
	 *
	 * @param selectionIterator
	 * @return the containing projects (if exists), otherwise an empty list
	 */
	protected static List<IProject> getProjects(final Object element) {
		final List<IProject> projects = new ArrayList<>();
		if (element instanceof IResource) {
			final IResource resource = (IResource) element;
			final IProject project = resource.getProject();
			projects.add(project);
		} else if (element instanceof IJavaElement) {
			final IJavaElement javaElement = (IJavaElement) element;
			final IProject project = javaElement.getJavaProject().getProject();
			projects.add(project);
		} else if (element instanceof IWorkingSet) {
			final IWorkingSet workingSet = (IWorkingSet) element;
			for (final IAdaptable elementInWorkingSet : workingSet.getElements()) {
				if (elementInWorkingSet instanceof IProject) {
					projects.add(IProject.class.cast(elementInWorkingSet));
				} else if (elementInWorkingSet instanceof IJavaProject) {
					projects.add(IJavaProject.class.cast(elementInWorkingSet).getProject());
				}
			}
		}
		return projects;
	}

	/**
	 * Tries to extract the edited file from the given {@link ExecutionEvent}
	 * 
	 * @param event
	 *            the event to analyze
	 * @return the file of the active editor or <code>null</code> if there is no active editor
	 */
	protected static IFile getEditedFile(final ExecutionEvent event) {
		final IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
		if (activeEditor != null) {
			return (IFile) activeEditor.getEditorInput().getAdapter(IFile.class);
		}
		
		return null;
	}

	protected void openInEditor(final IFile targetFile) throws CoreException, PartInitException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(IMarker.LINE_NUMBER, Integer.valueOf(1));
		IMarker marker;

		marker = targetFile.createMarker(IMarker.TEXT);

		marker.setAttributes(map);
		IDE.openEditor(page, targetFile);
		marker.delete();
	}

	/**
	 * Extracts the edited file from the selection of the given
	 * {@link ExecutionEvent}
	 * 
	 * Currently, {@link StructuredSelection} and {@link ITextSelection} are
	 * supported selection types. For all other types, <code>null</code> is
	 * returned.
	 * 
	 * @param event
	 *            the event to analyze
	 * @return the file or <code>null</code>
	 * @throws ExecutionException
	 *             if extracting the selection from the given event fails
	 */
	protected IFile extractSelectedFile(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);

		if (selection instanceof StructuredSelection) {
			final StructuredSelection structuredSelection = (StructuredSelection) selection;
			final Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof IFile) {
				return (IFile) firstElement;
			} else if (firstElement instanceof ICompilationUnit) {
				return (IFile) ((ICompilationUnit) firstElement).getResource();
			}
		} else if (selection instanceof ITextSelection) {
			return getEditedFile(event);
		}

		return null;
	}

	protected static Collection<IProject> getProjectsFromSelection(final IStructuredSelection selection) {
		final List<IProject> projects = new ArrayList<>();
		if (selection instanceof StructuredSelection) {
			final StructuredSelection structuredSelection = (StructuredSelection) selection;
			for (final Iterator<?> selectionIterator = structuredSelection.iterator(); selectionIterator.hasNext();) {
				projects.addAll(getProjects(selectionIterator.next()));
			}
		}

		return projects;
	}
}
