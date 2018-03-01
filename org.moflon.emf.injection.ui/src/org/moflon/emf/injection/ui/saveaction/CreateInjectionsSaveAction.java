package org.moflon.emf.injection.ui.saveaction;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.cleanup.CleanUpContext;
import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.cleanup.CleanUpRequirements;
import org.eclipse.jdt.ui.cleanup.ICleanUp;
import org.eclipse.jdt.ui.cleanup.ICleanUpFix;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * This save action forces saving injections on every save of a file.
 */
public class CreateInjectionsSaveAction implements ICleanUp {
	public static final String DESCRIPTION = "Create eMoflon injections";

	public static final String KEY_CREATE_INJECTIONS = "cleanup.moflon.createinjections";

	private CleanUpOptions options;

	private RefactoringStatus status;

	@Override
	public void setOptions(final CleanUpOptions options) {
		this.options = options;
	}

	@Override
	public String[] getStepDescriptions() {
		if (this.isEnabled()) {
			return new String[] { DESCRIPTION };
		}
		return null;
	}

	@Override
	public CleanUpRequirements getRequirements() {
		return new CleanUpRequirements(isEnabled(), isEnabled(), false, null);
	}

	@Override
	public ICleanUpFix createFix(final CleanUpContext context) throws CoreException {
		final ICompilationUnit compilationUnit = context.getCompilationUnit();
		if (compilationUnit == null)
			return null;
		if (!isEnabled())
			return null;

		return new CreateInjectionFix(compilationUnit);
	}

	@Override
	public RefactoringStatus checkPreConditions(final IJavaProject project, final ICompilationUnit[] compilationUnits,
			final IProgressMonitor monitor) throws CoreException {
		if (isEnabled()) {
			status = new RefactoringStatus();
		}
		return new RefactoringStatus();
	}

	@Override
	public RefactoringStatus checkPostConditions(final IProgressMonitor monitor) throws CoreException {
		if (status == null || status.isOK()) {
			return new RefactoringStatus();
		} else {
			return status;
		}
	}

	/**
	 * Returns whether this save action is enabled
	 */
	public boolean isEnabled() {
		return options.isEnabled(KEY_CREATE_INJECTIONS);
	}

}
