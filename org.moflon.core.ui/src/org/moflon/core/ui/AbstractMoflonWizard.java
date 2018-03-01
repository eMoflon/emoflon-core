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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * Implementation base class for wizards in eMoflon
 *
 * @author Roland Kluge - Initial implementation
 *
 */
public abstract class AbstractMoflonWizard extends Wizard implements IWorkbenchWizard {
	private IStructuredSelection selection;
	private IWorkbench workbench;

	public AbstractMoflonWizard() {
		setNeedsProgressMonitor(true);
	}

	protected abstract void doFinish(final IProgressMonitor monitor) throws CoreException;

	/**
	 * Invokes {@link #doFinish(IProgressMonitor)} in a separate task
	 */
	@Override
	public boolean performFinish() {
		final IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor monitor) throws InvocationTargetException {
				final SubMonitor subMon = SubMonitor.convert(monitor, "Completing wizard", 1);
				try {
					doFinish(subMon.split(1));
				} catch (final CoreException e) {
					throw new InvocationTargetException(e);
				}
			}
		};

		try {
			getContainer().run(true, false, op);
		} catch (final InterruptedException e) {
			return false;
		} catch (final InvocationTargetException e) {
			final Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Initializes the properties returned by {@link #getWorkbench()} and
	 * {@link #getSelection()}
	 */
	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	/**
	 *
	 * @return the {@link IWorkbench} that was passed to
	 *         {@link #init(IWorkbench, IStructuredSelection)}
	 */
	protected IWorkbench getWorkbench() {
		return workbench;
	}

	/**
	 * @return the {@link IStructuredSelection} that was passed to
	 *         {@link #init(IWorkbench, IStructuredSelection)}
	 */
	protected IStructuredSelection getSelection() {
		return selection;
	}

	/**
	 * Returns the active workbench window based on the current workbench
	 * ({@link #getWorkbench()}
	 * 
	 * @return
	 *
	 * 		Taken from org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne
	 */
	protected IWorkbenchPart getActivePart() {
		IWorkbenchWindow activeWindow = getWorkbench().getActiveWorkbenchWindow();
		if (activeWindow != null) {
			IWorkbenchPage activePage = activeWindow.getActivePage();
			if (activePage != null) {
				return activePage.getActivePart();
			}
		}
		return null;
	}
}
