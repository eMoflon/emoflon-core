package org.moflon.core.ui.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moflon.core.ui.AbstractCommandHandler;

/**
 * This handler touches the set of currently selected {@link IResource}s
 *
 * @author Roland Kluge - Initial implementation
 * @see #execute(ExecutionEvent)
 */
public class TouchResourceHandler extends AbstractCommandHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Collection<IResource> resources = extractResouresFromSelection(event);
		final WorkspaceJob job = new TouchResourceJob(resources);
		job.setUser(true);
		job.setRule(new MultiRule(resources.toArray(new IResource[resources.size()])));
		job.schedule();

		return null;
	}

	/**
	 * Retrieves the list of {@link IResource}s that are selected
	 * 
	 * Currently, supported selection types are {@link IStructuredSelection} and {@link TextSelection}.
	 * 
	 * @param event
	 *            the event
	 * @return the list of resources
	 * @throws ExecutionException if extracting the selection from the given event fails 
	 */
	private Collection<IResource> extractResouresFromSelection(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		final Collection<IResource> resources = new ArrayList<>();
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (final Iterator<?> selectionIterator = structuredSelection.iterator(); selectionIterator.hasNext();) {
				final Object element = selectionIterator.next();
				if (element instanceof IResource) {
					final IResource resource = (IResource) element;
					resources.add(resource);
				}
			}
		} else if (selection instanceof ITextSelection) {
			return Arrays.asList(getEditedFile(event));
		}
		return resources;
	}
}
