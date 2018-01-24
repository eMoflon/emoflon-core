package org.moflon.emf.injection.ui.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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

   private static final String JAVA_FILE_EXTENSION = "java";

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
               IPath javaFilePath = getPathToJavaFile(file);
               openInEditor(file.getProject().getFile(javaFilePath));
            } else if (isJavaFile(file))
            {
               IPath injectionFilePath = WorkspaceHelper.getPathToInjection(file);
               IFile injectionFile = file.getProject().getFile(injectionFilePath);
               if (injectionFile.exists())
               {
                  openInEditor(injectionFile);
               } else
               {
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
   /**
    * Returns the file name of the Java file for a given injection file.
    *
    * This method assumes that the first segment in the path is the injection folder (
    * {@link WorkspaceHelper#INJECTION_FOLDER}). The injection file name is obtained by replacing the first segment of
    * the input file name with {@link WorkspaceHelper#GEN_FOLDER} and by replacing the file extension with
    * {@link WorkspaceHelper#JAVA_FILE_EXTENSION}.
    *
    * The resulting path needs to be resolved against a project via {@link IProject#getFile(IPath)}.
    *
    * @param injectionFile
    *           the injection file
    * @return the path to the Java file
    */
   public static IPath getPathToJavaFile(final IFile injectionFile)
   {
      final IPath packagePath = injectionFile.getProjectRelativePath().removeFirstSegments(1);
      final IPath pathToJavaFile = packagePath.removeFileExtension().addFileExtension(JAVA_FILE_EXTENSION);
      final IFolder genFolder = WorkspaceHelper.getGenFolder(injectionFile.getProject());
      final IPath fullJavaPath = genFolder.getProjectRelativePath().append(pathToJavaFile);
      return fullJavaPath;
   }

   public static boolean isJavaFile(final IResource resource)
   {
      return resource != null && WorkspaceHelper.isFile(resource) && resource.getName().endsWith("." + JAVA_FILE_EXTENSION);
   }
}
