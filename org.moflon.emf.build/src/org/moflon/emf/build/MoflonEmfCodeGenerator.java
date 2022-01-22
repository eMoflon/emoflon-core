package org.moflon.emf.build;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.SmartEMFGenerator;
import org.moflon.core.preferences.EMoflonPreferencesStorage;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
import org.moflon.core.propertycontainer.MoflonPropertiesContainerHelper;
import org.moflon.core.propertycontainer.PropertycontainerFactory;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.emf.codegen.CodeGenerator;



public class MoflonEmfCodeGenerator extends GenericMoflonProcess {
	private static final Logger logger = Logger.getLogger(MoflonEmfCodeGenerator.class);
	
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
			if (subMon.isCanceled())
				return Status.CANCEL_STATUS;
			subMon.worked(5);

			final IStatus inheritanceCheckStatus = checkForCyclicInheritance();
			if (subMon.isCanceled())
				return Status.CANCEL_STATUS;
			if (inheritanceCheckStatus.matches(IStatus.ERROR))
				return inheritanceCheckStatus;
			
			// Generate code
			subMon.subTask("Generating code for project " + getProjectName());
			
			//TODO:be able to change between SmartEMF and normal EMF
//			boolean istSmartEmf = !this.getGenModel().getRootExtendsClass().equals("org.eclipse.emf.ecore.impl.MinimalEObjectImpl$Container");
			//for testing purposes: if the genmodel name is of a specific model, then just create the old emf
//			if(genModelBuilderJob.getGenModel().getModelName().equals("SimpleEMFEcoreModel")) istSmartEmf = false;
			
			MoflonPropertiesContainerHelper helper = new MoflonPropertiesContainerHelper(getProject(), new NullProgressMonitor());
			MoflonPropertiesContainer container = helper.load();
			if(container.getCodeGenerator() == null) {
				container.setCodeGenerator(PropertycontainerFactory.eINSTANCE.createCodeGenerator());
			}
			
			// choose metamodel code generator
			switch(container.getCodeGenerator().getGenerator()) {
				case EMF: {
					//old emf model creation
					final CodeGenerator codeGenerator = new CodeGenerator();
					final IStatus codeGenerationStatus = codeGenerator.generateCode(genModel,
							new BasicMonitor.EclipseSubProgress(subMon, 30));
					if (subMon.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					if (codeGenerationStatus.matches(IStatus.ERROR)) {
						return codeGenerationStatus;
					}
					subMon.worked(5);	
					break;
				}
				case SMART_EMF: {
					//smartemf is used for model generation
					
					//Find the current workspace
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();			
					File ecoreFile = new File(root.findMember(getEcoreFile().getFullPath().toString()).getLocationURI());
					String ecorePath = ecoreFile.getAbsolutePath();
					EPackage ePack = (EPackage) (new ResourceSetImpl()).getResource(URI.createFileURI(ecorePath), true).getContents().get(0);
		
					if(ecoreFile.exists() && !ecoreFile.isDirectory()) {
						//paths of the files necessary for smartEMF extension
						final SmartEMFGenerator codeGenerator = new SmartEMFGenerator(ePack, genModel, ecorePath);
						codeGenerator.generate_all_model_code();
					} else {
						logger.warn("Problem when generating code: the genmodel file needs to be in the same folder as the ecore file.");
					}	
					
					//because of smartemf: the gen folder needs to be refreshed automatically; 
					//else the user will need to do this manually 
					getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
					
					break;
				}
				default: throw new RuntimeException("Cannot generate code for " + getGenModel().getRootExtendsClass());
			}
					
			final long tic = System.nanoTime();
			final double durationInSeconds = (tic - toc) / 1e9;
			logger.info(String.format(Locale.US, "Completed in %.3fs", durationInSeconds));

			return Status.OK_STATUS;
		} catch (final Exception e) {
			e.printStackTrace();
			return reportExceptionDuringCodeGeneration(e);
		}
	}

	/**
	 * Checks whether a {@link EClass} in any of the {@link GenPackage}s of the
	 * {@link GenModel} has a cyclic inheritance hierarchy
	 * 
	 * @return {@link IStatus} with {@link IStatus#ERROR} if a cycle exists
	 */
	private IStatus checkForCyclicInheritance() {
		final List<EClass> eClassifiersWithCyclicInheritance = Collections.synchronizedList(new LinkedList<>());
		genModel.getGenPackages().parallelStream()//
				.flatMap(genPackage -> genPackage.getGenClassifiers().parallelStream()).forEach(genClassifier -> {
					final EClassifier eClassifier = genClassifier.getEcoreClassifier();
					if (eClassifier instanceof EClass) {
						final EClass eClass = (EClass) eClassifier;
						final EList<EGenericType> superTypes = eClass.getEAllGenericSuperTypes();
						if (superTypes.parallelStream().map(EGenericType::getEClassifier)
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
