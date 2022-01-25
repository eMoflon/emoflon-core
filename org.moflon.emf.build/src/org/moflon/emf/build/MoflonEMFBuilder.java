package org.moflon.emf.build;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.gervarro.eclipse.workspace.util.AntPatternCondition;
import org.moflon.core.build.CleanVisitor;
import org.moflon.core.plugins.manifest.ExportedPackagesInManifestUpdater;
import org.moflon.core.plugins.manifest.PluginXmlUpdater;
import org.moflon.core.preferences.EMoflonPreferencesActivator;
import org.moflon.core.utilities.ClasspathUtil;
import org.moflon.core.utilities.ErrorReporter;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.core.utilities.eMoflonEMFUtil;

public class MoflonEMFBuilder extends IncrementalProjectBuilder {
	public static final Logger logger = Logger.getLogger(MoflonEMFBuilder.class);

	private static final String MOFLON_EMF_BUILDER_ID = "org.moflon.emf.build.MoflonEMFBuilder";

	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();
		IFolder folder = project.getFolder("model");
		Collection<IResource> resources = Arrays.asList(folder.members());
		Collection<IResource> ecoreResources = resources.stream().filter(r -> r.getName().endsWith(".ecore")).collect(Collectors.toList());
		
		IResourceDelta delta = getDelta(project);
		if(delta!=null) {
			for(IResourceDelta rDelta : delta.getAffectedChildren()) {
				// if changed resource was in the build folder -> ignore it
				if(!rDelta.getFullPath().toString().endsWith("/bin")) {
					buildEcoreFiles(ecoreResources, monitor);					
				}
			}
		}
		else {
			// build ecore if delta was null as it will probably be the initial build
			buildEcoreFiles(ecoreResources, monitor);					
		}
		return new IProject[] {project};
	}
	
	private void buildEcoreFiles(Collection<IResource> resources, final IProgressMonitor monitor) throws CoreException {
		final IProject project = getProject();

		// Remove markers and delete generated code
		deleteProblemMarkers();
		removeGeneratedCode(project);
		
		for(IResource resource : resources) {
			final IFile ecoreFile = Platform.getAdapterManager().getAdapter(resource, IFile.class);
			final MultiStatus emfBuilderStatus = new MultiStatus(WorkspaceHelper.getPluginId(getClass()), 0,
					"Problems during EMF code generation", null);
			try {
				final SubMonitor subMon = SubMonitor.convert(monitor,
						"Generating code for project " + getProject().getName(), 13);
				
				createFoldersIfNecessary(project, subMon.split(1));
				ClasspathUtil.makeSourceFolderIfNecessary(WorkspaceHelper.getGenFolder(getProject()));
				
				// Compute project dependencies
//				final IBuildConfiguration[] referencedBuildConfigs = project
//						.getReferencedBuildConfigs(project.getActiveBuildConfig().getName(), false);
//				for (final IBuildConfiguration referencedConfig : referencedBuildConfigs) {
//					addTriggerProject(referencedConfig.getProject());
//				}
				
				
				// Build
				final ResourceSet resourceSet = eMoflonEMFUtil.createDefaultResourceSet();
				eMoflonEMFUtil.installCrossReferencers(resourceSet);
				subMon.worked(1);
				
				final MoflonEmfCodeGenerator codeGenerationTask = new MoflonEmfCodeGenerator(ecoreFile, resourceSet,
						EMoflonPreferencesActivator.getDefault().getPreferencesStorage());
				
				final IStatus status = codeGenerationTask.run(subMon.split(1));
				subMon.worked(3);
				emfBuilderStatus.add(status);
				
				if (!emfBuilderStatus.isOK())
					return;
				
				final GenModel genModel = codeGenerationTask.getGenModel();
				if (genModel == null) {
					emfBuilderStatus.add(new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()),
							String.format("No GenModel found for '%s'", getProject())));
				} else {
					ExportedPackagesInManifestUpdater.updateExportedPackageInManifest(project, genModel);
					
					PluginXmlUpdater.updatePluginXml(project, genModel, subMon.split(1));
				}
				
			} catch (final CoreException e) {
				emfBuilderStatus.add(new Status(e.getStatus().getSeverity(), WorkspaceHelper.getPluginId(getClass()),
						e.getMessage(), e));
			} finally {
				handleErrorsInEclipse(emfBuilderStatus, ecoreFile);
			}
			
		}
		ResourcesPlugin.getWorkspace().checkpoint(false);
	}

	/**
	 * Removes all contents in /gen, but preserves all versioning files
	 *
	 * @param project the project to be cleaned
	 * @throws CoreException if cleaning fails
	 */
	private void removeGeneratedCode(final IProject project) throws CoreException {
		final CleanVisitor cleanVisitor = new CleanVisitor(project, //
				new AntPatternCondition(new String[] { "gen/**" }), //
				new AntPatternCondition(new String[] { "gen/.keep*" }));
		project.accept(cleanVisitor, IResource.DEPTH_INFINITE, IResource.NONE);
	}

	private static void createFoldersIfNecessary(final IProject project, final IProgressMonitor monitor)
			throws CoreException {
		final SubMonitor subMon = SubMonitor.convert(monitor, "Creating folders within project " + project, 4);

		WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getSourceFolder(project), subMon.split(1));
		WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getBinFolder(project), subMon.split(1));
		WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getGenFolder(project), subMon.split(1));
	}
	
	/**
	 * Removes all problem markers of the types
	 * {@link WorkspaceHelper#MOFLON_PROBLEM_MARKER_ID} and
	 * {@link WorkspaceHelper#INJECTION_PROBLEM_MARKER_ID} from the current project
	 * (see {@link #getProject()}.
	 *
	 * @throws CoreException
	 *             if removing the problem markers fails
	 */
	protected void deleteProblemMarkers() throws CoreException {
		getProject().deleteMarkers(WorkspaceHelper.MOFLON_PROBLEM_MARKER_ID, false, IResource.DEPTH_INFINITE);
		getProject().deleteMarkers(WorkspaceHelper.INJECTION_PROBLEM_MARKER_ID, false, IResource.DEPTH_INFINITE);
	}
	
	/**
	 * Converts the given {@link Status} to problem markers in the Eclipse UI
	 *
	 * @param status the status to be converted
	 * @param file   the file contains problems
	 */
	public void handleErrorsInEclipse(final IStatus status, final IFile file) {
		final String reporterClass = "org.moflon.core.ui.errorhandling.MultiStatusAwareErrorReporter";
		final ErrorReporter eclipseErrorReporter = (ErrorReporter) Platform.getAdapterManager().loadAdapter(file,
				reporterClass);
		if (eclipseErrorReporter != null) {
			eclipseErrorReporter.report(status);
		} else {
			logger.error(String.format("Could not load error reporter '%s' to report status", reporterClass));
		}
	}

	public static String getId() {
		return MOFLON_EMF_BUILDER_ID;
	}
}
