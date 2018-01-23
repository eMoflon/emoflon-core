package org.moflon.emf.injection.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moflon.core.ui.AbstractCommandHandler;
import org.moflon.core.utilities.WorkspaceHelper;

/**
 * This component toggles between a Java class and its injection file (if present).
 *
 * If the current selection is a Java file, the handler tries to find and open the corresponding injection file. If the
 * current selection is an injection file, the handler opens the corresponding Java file.
 */
public class SwitchBetweenJavaAndInjectionFileHandler extends AbstractCommandHandler
{

   @Override
   public Object execute(final ExecutionEvent event) throws ExecutionException
   {
      final ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
      IFile file = null;
      if (selection instanceof StructuredSelection)
      {
         final StructuredSelection structuredSelection = (StructuredSelection) selection;
         final Object firstElement = structuredSelection.getFirstElement();
         if (firstElement instanceof IFile)
         {
            file = (IFile) firstElement;
         }
      } else if (selection instanceof ITextSelection)
      {
         file = getEditedFile(event);
      }
      try
      {
         if (file != null)
         {
            if (WorkspaceHelper.isInjectionFile(file))
            {
               IPath javaFilePath = WorkspaceHelper.getPathToJavaFile(file);
               openInEditor(file.getProject().getFile(javaFilePath));
            } else if (WorkspaceHelper.isJavaFile(file))
            {
               IPath injectionFilePath = WorkspaceHelper.getPathToInjection(file);
               IFile injectionFile = file.getProject().getFile(injectionFilePath);
               if (injectionFile.exists())
               {
                  openInEditor(injectionFile);
               }
               else {
                  logger.info("No injection for Java file " + file.getProjectRelativePath().toString());
               }
            }
         }
      } catch (CoreException e)
      {
         throw new ExecutionException(e.getMessage());
      }

      return null;
   }

}
