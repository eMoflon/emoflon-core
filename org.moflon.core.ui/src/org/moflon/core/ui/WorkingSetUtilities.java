package org.moflon.core.ui;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.moflon.core.utilities.UtilityClassNotInstantiableException;

/**
 * Utility methods for working with Working Sets
 * @author Roland Kluge - Initial implementation
 * @see http://help.eclipse.org/oxygen/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Fconcepts%2Fcworkset.htm
 */
public final class WorkingSetUtilities
{
   /**
    * Disabled constructor
    */
   private WorkingSetUtilities()
   {
      throw new UtilityClassNotInstantiableException();
   }

   /**
    * Adds the given project to the Working Set with the given name
    *
    * If no such working set exists, the project is added to the default working set (see {@link #getJavaWorkingSet()}
    * @param project the project
    * @param workingSetName the name of the Working Set
    */
   public static void addProjectToWorkingSet(final IProject project, final String workingSetName)
   {
      // Move project to appropriate working set
      final IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
      IWorkingSet workingSet = workingSetManager.getWorkingSet(workingSetName);
      if (workingSet == null)
      {
         workingSet = workingSetManager.createWorkingSet(workingSetName, new IAdaptable[] { project });
         workingSet.setId(getJavaWorkingSet());
         workingSetManager.addWorkingSet(workingSet);
      } else
      {
         addProjectToWorkingSet(project, workingSet);
      }
   }

   /**
    * Adds the given project to the given Working Set
    * @param project the project
    * @param workingSet the Working Set
    */
   public static void addProjectToWorkingSet(final IProject project, final IWorkingSet workingSet)
   {
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
    * @return the ID of the default Java working set
    */
   @SuppressWarnings("restriction")
   private static String getJavaWorkingSet()
   {
      return org.eclipse.jdt.internal.ui.workingsets.IWorkingSetIDs.JAVA;
   }

}
