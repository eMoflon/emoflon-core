package org.moflon.emf.build;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.gervarro.eclipse.workspace.util.AntPatternCondition;
import org.moflon.core.build.AbstractVisitorBuilder;
import org.moflon.core.build.CleanVisitor;
import org.moflon.core.plugins.manifest.ExportedPackagesInManifestUpdater;
import org.moflon.core.plugins.manifest.PluginXmlUpdater;
import org.moflon.core.preferences.EMoflonPreferencesActivator;
import org.moflon.core.utilities.ClasspathUtil;
import org.moflon.core.utilities.ErrorReporter;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.core.utilities.eMoflonEMFUtil;

/**
 * This builder triggers a basic code generation workflow for all Ecore models in /model
 *
 * @author Roland Kluge - Initial implementation
 *
 */
public class MoflonEmfBuilder extends AbstractVisitorBuilder
{
   public static final Logger logger = Logger.getLogger(MoflonEmfBuilder.class);

   private static final String MOFLON_EMF_BUILDER_ID = "org.moflon.emf.build.MoflonEmfBuilder";

   /**
    * Initializes the visitor condition
    *
    * This builder gets triggered whenever any ecore file in /models changes
    */
   public MoflonEmfBuilder()
   {
      super(new AntPatternCondition(new String[] { "model/*.ecore" }));
   }

   public static String getId()
   {
      return MOFLON_EMF_BUILDER_ID;
   }

   /**
    * This builder locks the surrounding project
    */
   @Override
   public ISchedulingRule getRule(final int kind, final Map<String, String> args)
   {
      return getProject();
   }

   /**
    * The cleans generated code and problem markers
    */
   @Override
   protected void clean(final IProgressMonitor monitor) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Cleaning " + getProject(), 4);

      deleteProblemMarkers();
      subMon.worked(1);

      removeGeneratedCode(getProject());
      subMon.worked(3);
   }

   /**
    * Converts the given {@link Status} to problem markers in the Eclipse UI
    * @param status the status to be converted
    * @param file the file contains problems
    */
   public void handleErrorsInEclipse(final IStatus status, final IFile file)
   {
      final String reporterClass = "org.moflon.core.ui.errorhandling.MultiStatusAwareErrorReporter";
      final ErrorReporter eclipseErrorReporter = (ErrorReporter) Platform.getAdapterManager().loadAdapter(file, reporterClass);
      if (eclipseErrorReporter != null)
      {
         eclipseErrorReporter.report(status);
      } else
      {
         logger.error(String.format("Could not load error reporter '%s' to report status", reporterClass));
      }
   }

   @Override
   protected void processResource(final IResource ecoreResource, final int kind, Map<String, String> args, final IProgressMonitor monitor)
   {
      if (eMoflonEMFUtil.isEcoreFile(ecoreResource))
      {
         final IFile ecoreFile = Platform.getAdapterManager().getAdapter(ecoreResource, IFile.class);
         try
         {
            final SubMonitor subMon = SubMonitor.convert(monitor, "Generating code for project " + getProject().getName(), 13);

            final IProject project = getProject();
            createFoldersIfNecessary(project, subMon.split(1));
            ClasspathUtil.makeSourceFolderIfNecessary(WorkspaceHelper.getGenFolder(getProject()));
            ClasspathUtil.makeSourceFolderIfNecessary(WorkspaceHelper.getInjectionFolder(getProject()));

            // Compute project dependencies
            final IBuildConfiguration[] referencedBuildConfigs = project.getReferencedBuildConfigs(project.getActiveBuildConfig().getName(), false);
            for (final IBuildConfiguration referencedConfig : referencedBuildConfigs)
            {
               addTriggerProject(referencedConfig.getProject());
            }

            // Remove markers and delete generated code
            deleteProblemMarkers();
            removeGeneratedCode(project);

            // Build
            final ResourceSet resourceSet = eMoflonEMFUtil.createDefaultResourceSet();
            eMoflonEMFUtil.installCrossReferencers(resourceSet);
            subMon.worked(1);

            final MoflonEmfCodeGenerator codeGenerationTask = new MoflonEmfCodeGenerator(ecoreFile, resourceSet, EMoflonPreferencesActivator.getDefault().getPreferencesStorage());

            final IStatus status = codeGenerationTask.run(subMon.split(1));
            handleErrorsAndWarnings(status, ecoreFile);
            subMon.worked(3);

            final GenModel genModel = codeGenerationTask.getGenModel();
            if (genModel == null)
               throw new CoreException(new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), String.format("No GenModel found for '%s'", getProject())));

            ExportedPackagesInManifestUpdater.updateExportedPackageInManifest(project, genModel);

            PluginXmlUpdater.updatePluginXml(project, genModel, subMon.split(1));
            ResourcesPlugin.getWorkspace().checkpoint(false);


         } catch (final CoreException e)
         {
            final IStatus status = new Status(e.getStatus().getSeverity(), WorkspaceHelper.getPluginId(getClass()), e.getMessage(), e);
            handleErrorsInEclipse(status, ecoreFile);
         }
      }
   }

   @Override
   protected final AntPatternCondition getTriggerCondition(final IProject project)
   {
      return new AntPatternCondition(new String[0]);
   }

   /**
    * Handles errors and warning produced by the code generation task
    *
    * @param status the {@link IStatus} that contains the errors and warnings
    */
   protected void handleErrorsAndWarnings(final IStatus status, final IFile ecoreFile) throws CoreException
   {
      if (status.matches(IStatus.ERROR))
      {
         handleErrorsInEclipse(status, ecoreFile);
      }
      if (status.matches(IStatus.WARNING))
      {
         handleInjectionWarningsAndErrors(status);
      }
   }

   /**
    * Removes all contents in /gen, but preserves all versioning files
    * @param project the project to be cleaned
    * @throws CoreException if cleaning fails
    */
   private void removeGeneratedCode(final IProject project) throws CoreException
   {
      final CleanVisitor cleanVisitor = new CleanVisitor(project, //
            new AntPatternCondition(new String[] { "gen/**" }), //
            new AntPatternCondition(new String[] { "gen/.keep*" }));
      project.accept(cleanVisitor, IResource.DEPTH_INFINITE, IResource.NONE);
   }

   private void handleInjectionWarningsAndErrors(final IStatus status)
   {
      final String reporterClass = "org.moflon.emf.injection.validation.InjectionErrorReporter";
      final ErrorReporter errorReporter = (ErrorReporter) Platform.getAdapterManager().loadAdapter(getProject(), reporterClass);
      if (errorReporter != null)
      {
         errorReporter.report(status);
      } else
      {
         logger.debug("Could not load error reporter '" + reporterClass + "'");
      }
   }

   private static void createFoldersIfNecessary(final IProject project, final IProgressMonitor monitor) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Creating folders within project " + project, 4);

      WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getSourceFolder(project), subMon.split(1));
      WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getBinFolder(project), subMon.split(1));
      WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getGenFolder(project), subMon.split(1));
      WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getInjectionFolder(project), subMon.split(1));
   }

}
