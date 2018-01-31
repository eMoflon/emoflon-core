package org.moflon.core.ui.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moflon.core.ui.AbstractCommandHandler;

/**
 * This handler touches the set of currently selected {@link IResource}s
 *
 * @author Roland Kluge - Initial implementation
 * @see #execute(ExecutionEvent)
 */
public class TouchResourceHandler extends AbstractCommandHandler
{
   @Override
   public Object execute(final ExecutionEvent event) throws ExecutionException
   {
      final IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);

      final Collection<IResource> resources = extractResouresFromSelection(selection);
      final WorkspaceJob job = new TouchResourceJob(resources);
      job.setUser(true);
      job.setRule(new MultiRule(resources.toArray(new IResource[resources.size()])));
      job.schedule();

      return null;
   }

   /**
    * Retrieves the list of {@link IResource}s that are selected in the given {@link IStructuredSelection}
    * @param selection the selection
    * @return the list of resources
    */
   private Collection<IResource> extractResouresFromSelection(final IStructuredSelection selection)
   {
      final Collection<IResource> resources = new ArrayList<>();
      if (selection instanceof StructuredSelection)
      {
         final StructuredSelection structuredSelection = (StructuredSelection) selection;
         for (final Iterator<?> selectionIterator = structuredSelection.iterator(); selectionIterator.hasNext();)
         {
            final Object element = selectionIterator.next();
            if (element instanceof IResource)
            {
               final IResource resource = (IResource) element;
               resources.add(resource);
            }
         }
      }
      return resources;
   }
}
