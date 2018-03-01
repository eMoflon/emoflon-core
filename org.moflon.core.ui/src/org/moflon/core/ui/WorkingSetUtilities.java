package org.moflon.core.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.moflon.core.utilities.UtilityClassNotInstantiableException;

/**
 * Utility methods for working with Working Sets
 * 
 * @author Roland Kluge - Initial implementation
 * @see http://help.eclipse.org/oxygen/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Fconcepts%2Fcworkset.htm
 */
public final class WorkingSetUtilities {
	private static final IWorkingSet[] EMPTY_WORKING_SET_ARRAY = new IWorkingSet[0];

	/**
	 * Disabled constructor
	 */
	private WorkingSetUtilities() {
		throw new UtilityClassNotInstantiableException();
	}

	/**
	 * Adds the given project to the Working Set with the given name
	 *
	 * If no such working set exists, the project is added to the default working
	 * set (see {@link #getJavaWorkingSet()}
	 * 
	 * @param project
	 *            the project
	 * @param workingSetName
	 *            the name of the Working Set
	 */
	public static void addProjectToWorkingSet(final IProject project, final String workingSetName) {
		// Move project to appropriate working set
		final IWorkingSetManager workingSetManager = getWorkingSetManager();
		IWorkingSet workingSet = workingSetManager.getWorkingSet(workingSetName);
		if (workingSet == null) {
			workingSet = workingSetManager.createWorkingSet(workingSetName, new IAdaptable[] { project });
			workingSet.setId(getJavaWorkingSet());
			workingSetManager.addWorkingSet(workingSet);
		} else {
			addProjectToWorkingSet(project, workingSet);
		}
	}

	/**
	 * Adds the given project to the given Working Set
	 * 
	 * @param project
	 *            the project
	 * @param workingSet
	 *            the Working Set
	 */
	public static void addProjectToWorkingSet(final IProject project, final IWorkingSet workingSet) {
		// Add current contents of WorkingSet
		ArrayList<IAdaptable> newElements = new ArrayList<IAdaptable>();
		for (final IAdaptable element : workingSet.getElements())
			newElements.add(element);

		// Add newly created project
		newElements.add(project);

		// Set updated contents
		final IAdaptable[] newElementsArray = new IAdaptable[newElements.size()];
		workingSet.setElements(newElements.toArray(newElementsArray));
	}

	/**
	 * Convenience method for accessing the {@link IWorkingSetManager}
	 * 
	 * @return the Working Set manager of the current platform
	 */
	public static IWorkingSetManager getWorkingSetManager() {
		return PlatformUI.getWorkbench().getWorkingSetManager();
	}

	/**
	 * @return the ID of the default Java working set
	 */
	@SuppressWarnings("restriction")
	private static String getJavaWorkingSet() {
		return org.eclipse.jdt.internal.ui.workingsets.IWorkingSetIDs.JAVA;
	}

	@SuppressWarnings("restriction")
	private static String getResourceWorkingSetId() {
		return org.eclipse.jdt.internal.ui.workingsets.IWorkingSetIDs.RESOURCE;
	}

	/**
	 * Returns the set of selected working set of the given
	 * {@link IStructuredSelection}
	 *
	 * @param selection
	 *            the selection
	 * @param activePart
	 *            the active part
	 * @return the selected working sets Taken from
	 *         org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne
	 */
	@SuppressWarnings("restriction")
	public static IWorkingSet[] getSelectedWorkingSet(IStructuredSelection selection, IWorkbenchPart activePart) {
		IWorkingSet[] selected = getSelectedWorkingSet(selection);
		if (selected != null && selected.length > 0) {
			for (int i = 0; i < selected.length; i++) {
				if (!isValidWorkingSet(selected[i]))
					return EMPTY_WORKING_SET_ARRAY;
			}
			return selected;
		}

		if (!(activePart instanceof org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart))
			return EMPTY_WORKING_SET_ARRAY;

		org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart explorerPart = (org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart) activePart;
		if (explorerPart
				.getRootMode() == org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart.PROJECTS_AS_ROOTS) {
			// Get active filter
			IWorkingSet filterWorkingSet = explorerPart.getFilterWorkingSet();
			if (filterWorkingSet == null)
				return EMPTY_WORKING_SET_ARRAY;

			if (!isValidWorkingSet(filterWorkingSet))
				return EMPTY_WORKING_SET_ARRAY;

			return new IWorkingSet[] { filterWorkingSet };
		} else {
			// If we have been gone into a working set return the working set
			Object input = explorerPart.getViewPartInput();
			if (!(input instanceof IWorkingSet))
				return EMPTY_WORKING_SET_ARRAY;

			IWorkingSet workingSet = (IWorkingSet) input;
			if (!isValidWorkingSet(workingSet))
				return EMPTY_WORKING_SET_ARRAY;

			return new IWorkingSet[] { workingSet };
		}
	}

	/**
	 * Returns the set of selected working set of the given
	 * {@link IStructuredSelection}
	 *
	 * @param selection
	 *            the selection
	 * @return the selected working sets
	 *
	 *         Taken from org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne
	 */
	private static IWorkingSet[] getSelectedWorkingSet(IStructuredSelection selection) {
		if (!(selection instanceof ITreeSelection))
			return EMPTY_WORKING_SET_ARRAY;

		ITreeSelection treeSelection = (ITreeSelection) selection;
		if (treeSelection.isEmpty())
			return EMPTY_WORKING_SET_ARRAY;

		List<?> elements = treeSelection.toList();
		if (elements.size() == 1) {
			Object element = elements.get(0);
			TreePath[] paths = treeSelection.getPathsFor(element);
			if (paths.length != 1)
				return EMPTY_WORKING_SET_ARRAY;

			TreePath path = paths[0];
			if (path.getSegmentCount() == 0)
				return EMPTY_WORKING_SET_ARRAY;

			Object candidate = path.getSegment(0);
			if (!(candidate instanceof IWorkingSet))
				return EMPTY_WORKING_SET_ARRAY;

			IWorkingSet workingSetCandidate = (IWorkingSet) candidate;
			if (isValidWorkingSet(workingSetCandidate))
				return new IWorkingSet[] { workingSetCandidate };

			return EMPTY_WORKING_SET_ARRAY;
		}

		ArrayList<IWorkingSet> result = new ArrayList<>();
		for (Iterator<?> iterator = elements.iterator(); iterator.hasNext();) {
			Object element = iterator.next();
			if (element instanceof IWorkingSet && isValidWorkingSet((IWorkingSet) element)) {
				result.add((IWorkingSet) element);
			}
		}
		return result.toArray(new IWorkingSet[result.size()]);
	}

	/**
	 * Checks the given {@link IWorkingSet} for validity
	 * 
	 * @param workingSet
	 *            the working set
	 * @return validity
	 *
	 *         Taken from org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne
	 */
	private static boolean isValidWorkingSet(IWorkingSet workingSet) {
		String id = workingSet.getId();
		if (!getJavaWorkingSet().equals(id) && !getResourceWorkingSetId().equals(id))
			return false;

		if (workingSet.isAggregateWorkingSet())
			return false;

		return true;
	}

}
