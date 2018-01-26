package org.moflon.core.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * Implementation base class for wizards in eMoflon
 *
 * @author Roland Kluge - Initial implementation
 *
 */
public abstract class AbstractMoflonWizard extends Wizard implements IWorkbenchWizard
{
   public AbstractMoflonWizard()
   {
      setNeedsProgressMonitor(true);
   }

   protected abstract void doFinish(final IProgressMonitor monitor) throws CoreException;

   @Override
   public boolean performFinish()
   {
      final IRunnableWithProgress op = new IRunnableWithProgress() {
         @Override
         public void run(final IProgressMonitor monitor) throws InvocationTargetException
         {
            final SubMonitor subMon = SubMonitor.convert(monitor, "Completing wizard", 1);
            try
            {
               doFinish(subMon.split(1));
            } catch (CoreException e)
            {
               throw new InvocationTargetException(e);
            }
         }
      };

      try
      {
         getContainer().run(true, false, op);
      } catch (InterruptedException e)
      {
         return false;
      } catch (InvocationTargetException e)
      {
         final Throwable realException = e.getTargetException();
         MessageDialog.openError(getShell(), "Error", realException.getMessage());
         return false;
      }

      return true;
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {

   }

}
