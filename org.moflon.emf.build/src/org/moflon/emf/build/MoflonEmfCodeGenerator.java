package org.moflon.emf.build;

import java.util.Locale;

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
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.moflon.core.preferences.EMoflonPreferencesStorage;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
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
		return "Generating code";
	}

	@Override
	public IStatus processResource(final IProgressMonitor monitor) {
		try {
			final int totalWork = 5 + 10 + 10 + 15 + 35 + 30 + 5;
			final SubMonitor subMon = SubMonitor.convert(monitor, "Code generation task for " + getProject().getName(),
					totalWork);
			logger.info("Generating code for: " + getProject().getName());

			long toc = System.nanoTime();

			// Build or load GenModel
			final MonitoredGenModelBuilder genModelBuilderJob = new MonitoredGenModelBuilder(getResourceSet(),
					getAllResources(), getEcoreFile(), true, getMoflonProperties());
			final IStatus genModelBuilderStatus = genModelBuilderJob.run(subMon.split(15));
			if (subMon.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			if (genModelBuilderStatus.matches(IStatus.ERROR)) {
				return genModelBuilderStatus;
			}
			this.genModel = genModelBuilderJob.getGenModel();

			// Load injections
			final IProject project = getEcoreFile().getProject();

			final IStatus injectionStatus = createInjections(project, genModel);
			if (subMon.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			if (injectionStatus.matches(IStatus.ERROR)) {
				return injectionStatus;
			}

			// Generate code
			subMon.subTask("Generating code for project " + project.getName());
			final Descriptor codeGenerationEngine = new InjectionAwareGeneratorAdapterFactory(injectionManager);
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

			long tic = System.nanoTime();

			logger.info(String.format(Locale.US, "Completed in %.3fs", (tic - toc) / 1e9));

			return injectionStatus.isOK() ? Status.OK_STATUS : injectionStatus;
		} catch (final Exception e) {
			logger.debug(WorkspaceHelper.printStacktraceToString(e));
			return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), IStatus.ERROR,
					e.getClass().getName() + " occurred during eMoflon code generation. Message: '" + e.getMessage()
							+ "'. (Stacktrace is logged with level debug)",
					e);
		}
	}

	public final GenModel getGenModel() {
		return genModel;
	}

	public final InjectionManager getInjectorManager() {
		return injectionManager;
	}

	/**
	 * Returns the project name to be displayed
	 * @param moflonProperties the properties container to consult
	 * @return the project name
	 */
	protected String getFullProjectName(final MoflonPropertiesContainer moflonProperties) {
		return moflonProperties.getProjectName();
	}

	/**
	 * Loads the injections from the /injection folder
	 */
	private IStatus createInjections(final IProject project, final GenModel genModel) throws CoreException {
		final IFolder injectionFolder = WorkspaceHelper.addFolder(project, WorkspaceHelper.INJECTION_FOLDER,
				new NullProgressMonitor());
		final CodeInjector injector = new CodeInjectorImpl(project.getLocation().toOSString());

		final InjectionExtractor injectionExtractor = new XTextInjectionExtractor(injectionFolder, genModel);

		injectionManager = new InjectionManager(injectionExtractor, injector);
		final IStatus extractionStatus = injectionManager.extractInjections();
		return extractionStatus;
	}
}
