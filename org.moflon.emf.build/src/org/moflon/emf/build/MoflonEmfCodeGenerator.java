package org.moflon.emf.build;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory.Descriptor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.moflon.core.preferences.EMoflonPreferencesStorage;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.emf.codegen.CodeGenerator;
import org.moflon.emf.codegen.InjectionAwareGeneratorAdapterFactory;
import org.moflon.emf.injection.build.CodeInjectorImpl;
import org.moflon.emf.injection.build.XTextInjectionExtractor;
import org.moflon.emf.injection.ide.CodeInjector;
import org.moflon.emf.injection.ide.InjectionExtractor;
import org.moflon.emf.injection.ide.InjectionManager;

public class MoflonEmfCodeGenerator extends GenericMoflonProcess {
	private static final Logger logger = Logger.getLogger(MoflonEmfCodeGenerator.class);

	private InjectionManager injectionManager;

	private GenModel genModel;

	public MoflonEmfCodeGenerator(final IFile ecoreFile, final ResourceSet resourceSet,
			final EMoflonPreferencesStorage preferencesStorage) {
		super(ecoreFile, resourceSet, preferencesStorage);
	}

	@Override
	public String getTaskName() {
		return "Generating code for project " + getProjectName();
	}

	@Override
	public IStatus processResource(final IProgressMonitor monitor) {
		try {
			final int totalWork = 15 + 5 + 30 + 5;
			final SubMonitor subMon = SubMonitor.convert(monitor, "Code generation task for " + getProjectName(),
					totalWork);
			LogUtils.info(logger, "Generating code for project %s", getProjectName());

			final long toc = System.nanoTime();

			// Build or load GenModel
			final MonitoredGenModelBuilder genModelBuilderJob = new MonitoredGenModelBuilder(getResourceSet(),
					getAllResources(), getEcoreFile(), true, getMoflonProperties());
			final IStatus genModelBuilderStatus = genModelBuilderJob.run(subMon.split(15));
			if (subMon.isCanceled())
				return Status.CANCEL_STATUS;
			if (genModelBuilderStatus.matches(IStatus.ERROR))
				return genModelBuilderStatus;
			this.setGenModel(genModelBuilderJob.getGenModel());

			final IStatus injectionStatus = loadInjections();
			if (subMon.isCanceled())
				return Status.CANCEL_STATUS;
			if (injectionStatus.matches(IStatus.ERROR))
				return injectionStatus;
			subMon.worked(5);

			final IStatus inheritanceCheckStatus = checkForCyclicInheritance();
			if (subMon.isCanceled())
				return Status.CANCEL_STATUS;
			if (inheritanceCheckStatus.matches(IStatus.ERROR))
				return inheritanceCheckStatus;

			// Generate code
			subMon.subTask("Generating code for project " + getProjectName());
			final Descriptor codeGenerationEngine = new InjectionAwareGeneratorAdapterFactory(
					this.getInjectorManager());
			final CodeGenerator codeGenerator = new CodeGenerator(codeGenerationEngine);
			final IStatus codeGenerationStatus = codeGenerator.generateCode(genModel,
					new BasicMonitor.EclipseSubProgress(subMon, 30));
			if (subMon.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			if (codeGenerationStatus.matches(IStatus.ERROR)) {
				return codeGenerationStatus;
			}
			subMon.worked(5);

			final long tic = System.nanoTime();
			final double durationInSeconds = (tic - toc) / 1e9;
			logger.info(String.format(Locale.US, "Completed in %.3fs", durationInSeconds));

			return injectionStatus.isOK() ? Status.OK_STATUS : injectionStatus;
		} catch (final Exception e) {
			return reportExceptionDuringCodeGeneration(e);
		}
	}

	private IStatus loadInjections() throws CoreException {
		final IProject project = getProject();
		final InjectionManager injectionManager = createInjectionManager(project);
		this.setInjectorManager(injectionManager);
		final IStatus injectionStatus = createInjections(project);
		return injectionStatus;
	}

	/**
	 * Checks whether a {@link EClass} in any of the {@link GenPackage}s of the
	 * {@link GenModel} has a cyclic inheritance hierarchy
	 * 
	 * @return {@link IStatus} with {@link IStatus#ERROR} if a cycle exists
	 */
	private IStatus checkForCyclicInheritance() {
		final List<EClass> eClassifiersWithCyclicInheritance = new ArrayList<>();
		genModel.getGenPackages().stream()//
				.flatMap(genPackage -> genPackage.getGenClassifiers().stream()).forEach(genClassifier -> {
					final EClassifier eClassifier = genClassifier.getEcoreClassifier();
					if (eClassifier instanceof EClass) {
						final EClass eClass = (EClass) eClassifier;
						final EList<EGenericType> superTypes = eClass.getEAllGenericSuperTypes();
						if (superTypes.stream().map(EGenericType::getEClassifier)
								.anyMatch(superType -> eClass.equals(superType))) {
							eClassifiersWithCyclicInheritance.add(eClass);
						}
					}
				});

		if (!eClassifiersWithCyclicInheritance.isEmpty()) {
			final String pluralSuffix = eClassifiersWithCyclicInheritance.size() > 1 ? "es" : "";
			final String joinedEClassNames = eClassifiersWithCyclicInheritance.stream().map(EClass::getName)
					.collect(Collectors.joining(","));
			return new Status(IStatus.ERROR, getPluginId(), String.format(
					"Inheritance hierarchy of the EClass%s [%s] contains cycles.", pluralSuffix, joinedEClassNames));
		}
		return Status.OK_STATUS;
	}

	/**
	 * Configures the {@link GenModel} to be used during code generation
	 * 
	 * @param genModel
	 *                     the {@link GenModel}
	 */
	protected final void setGenModel(final GenModel genModel) {
		this.genModel = genModel;
	}

	/**
	 * Returns the {@link GenModel} configured with {@link #setGenModel(GenModel)}
	 * 
	 * @return the {@link GenModel}
	 */
	public final GenModel getGenModel() {
		return genModel;
	}

	/**
	 * Set the {@link InjectionManager} to use
	 * 
	 * @param injectionManager
	 *                             the injection manager
	 */
	protected void setInjectorManager(final InjectionManager injectionManager) {
		this.injectionManager = injectionManager;
	}

	/**
	 * Returns the {@link InjectionManager} of this code generator.
	 * 
	 * The injection manager is initialized during the code generation.
	 * 
	 * @return the injection manager or <code>null</code> if it has not been
	 *         initialized yet
	 */
	public final InjectionManager getInjectorManager() {
		return injectionManager;
	}

	/**
	 * Loads the injections from the /injection folder using the injection manager
	 * returned from {@link #getInjectorManager()}
	 */
	protected IStatus createInjections(final IProject project) throws CoreException {
		final IStatus extractionStatus = getInjectorManager().extractInjections();
		return extractionStatus;
	}

	/**
	 * Creates the injection manager to be used for this build process
	 *
	 * The resulting injection manager still needs to be set using
	 * {@link #setInjectorManager(InjectionManager)}
	 * 
	 * @param project
	 *                    the current project
	 * @return
	 * @throws CoreException
	 */
	protected InjectionManager createInjectionManager(final IProject project) throws CoreException {
		final IFolder injectionFolder = WorkspaceHelper.addFolder(project, WorkspaceHelper.INJECTION_FOLDER,
				new NullProgressMonitor());
		final CodeInjector injector = new CodeInjectorImpl(project.getLocation().toOSString());

		final InjectionExtractor injectionExtractor = new XTextInjectionExtractor(injectionFolder, this.getGenModel());

		final InjectionManager injectionManager = new InjectionManager(injectionExtractor, injector);
		return injectionManager;
	}

	/**
	 * Reports a summary of the given exception in the returned status and the
	 * stacktrace of the exception to the logger.
	 * 
	 * @param exception
	 *                      the exception to report
	 * @return the error status
	 */
	private IStatus reportExceptionDuringCodeGeneration(final Exception exception) {
		final String shortMessage = String.format("%s during eMoflon code generation. Message: '%s'.",
				exception.getClass().getName(), exception.getMessage());
		logger.debug(shortMessage);
		logger.debug(WorkspaceHelper.printStacktraceToString(exception));
		return new Status(IStatus.ERROR, getPluginId(), IStatus.ERROR,
				String.format("%s (Stacktrace is logged with level debug)", shortMessage), exception);
	}

	/**
	 * @return the ID of the plugin containing this class
	 */
	private String getPluginId() {
		return WorkspaceHelper.getPluginId(getClass());
	}

	private String getProjectName() {
		return getProject().getName();
	}
}
