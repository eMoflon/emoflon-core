package org.moflon.emf.build;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.gervarro.eclipse.workspace.util.AntPatternCondition;
import org.moflon.core.build.AbstractVisitorBuilder;
import org.moflon.core.build.CleanVisitor;
import org.moflon.core.utilities.ErrorReporter;
import org.moflon.core.utilities.WorkspaceHelper;

/**
 * This builder triggers a basic code generation workflow for all Ecore models in /model
 *
 * @author Roland Kluge - Initial implementation
 *
 */
public class EMoflonEmfBuilder extends AbstractVisitorBuilder
{
   public static final Logger logger = Logger.getLogger(EMoflonEmfBuilder.class);

   protected boolean generateSDMs = true;

   public EMoflonEmfBuilder()
   {
      super(getVisitorCondition());
   }

   private static AntPatternCondition getVisitorCondition()
   {
      return new AntPatternCondition(new String[] { "model/*.ecore" });
   }

   @Override
   public ISchedulingRule getRule(final int kind, final Map<String, String> args)
   {
      return getProject();
   }

   @Override
   protected void clean(final IProgressMonitor monitor) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Cleaning " + getProject(), 4);

      final IProject project = getProject();

      deleteProblemMarkers();
      subMon.worked(1);

      // Remove generated code, but preserve .keep files for version control
      final CleanVisitor cleanVisitor = new CleanVisitor(getProject(), //
            new AntPatternCondition(new String[] { "gen/**" }), //
            new AntPatternCondition(new String[] { "gen/.keep*" }));
      project.accept(cleanVisitor, IResource.DEPTH_INFINITE, IResource.NONE);
   }

   public void handleErrorsInEclipse(final IStatus status, final IFile ecoreFile)
   {
      //      final String reporterClass = "org.moflon.compiler.sdm.democles.eclipse.EclipseErrorReporter";
      //      final ErrorReporter eclipseErrorReporter = (ErrorReporter) Platform.getAdapterManager().loadAdapter(ecoreFile, reporterClass);
      //      if (eclipseErrorReporter != null)
      //      {
      //         eclipseErrorReporter.report(status);
      //      } else
      //      {
      //         logger.debug("Could not load error reporter '" + reporterClass + "'");
      //      }
   }

   @Override
   protected void processResource(final IResource ecoreResource, final int kind, Map<String, String> args, final IProgressMonitor monitor)
   {
      if (isEcoreFile(ecoreResource))
      {
         final IFile ecoreFile = Platform.getAdapterManager().getAdapter(ecoreResource, IFile.class);
         try
         {
            //            final SubMonitor subMon = SubMonitor.convert(monitor, "Generating code for project " + getProject().getName(), 13);
            //
            //            final IProject project = getProject();
            //            MoflonProjectCreator.createFoldersIfNecessary(project, subMon.split(1));
            //            makeSourceFolderIfNecessary(WorkspaceHelper.getGenFolder(getProject()));
            //            makeSourceFolderIfNecessary(WorkspaceHelper.getInjectionFolder(getProject()));
            //
            //            // Compute project dependencies
            //            final IBuildConfiguration[] referencedBuildConfigs = project.getReferencedBuildConfigs(project.getActiveBuildConfig().getName(), false);
            //            for (final IBuildConfiguration referencedConfig : referencedBuildConfigs)
            //            {
            //               addTriggerProject(referencedConfig.getProject());
            //            }
            //
            //            // Remove markers and delete generated code
            //            deleteProblemMarkers();
            //            final CleanVisitor cleanVisitor = new CleanVisitor(project, //
            //                  new AntPatternCondition(new String[] { "gen/**" }), //
            //                  new AntPatternCondition(new String[] { "gen/.keep*" }));
            //            project.accept(cleanVisitor, IResource.DEPTH_INFINITE, IResource.NONE);
            //
            //            // Build
            //            final ResourceSet resourceSet = CodeGeneratorPlugin.createDefaultResourceSet();
            //            eMoflonEMFUtil.installCrossReferencers(resourceSet);
            //            subMon.worked(1);
            //
            //            final MoflonCodeGenerator codeGenerationTask = new MoflonCodeGenerator(ecoreFile, resourceSet, CoreActivator.getDefault().getPreferencesStorage());
            //
            //            final IStatus status = codeGenerationTask.run(subMon.split(1));
            //            handleErrorsAndWarnings(status, ecoreFile);
            //            subMon.worked(3);
            //
            //            final GenModel genModel = codeGenerationTask.getGenModel();
            //            if (genModel != null)
            //            {
            //               ExportedPackagesInManifestUpdater.updateExportedPackageInManifest(project, genModel);
            //
            //               PluginXmlUpdater.updatePluginXml(project, genModel, subMon.split(1));
            //               ResourcesPlugin.getWorkspace().checkpoint(false);
            //            }
            throw new CoreException(null);

         } catch (final CoreException e)
         {
            final IStatus status = new Status(e.getStatus().getSeverity(), WorkspaceHelper.getPluginId(getClass()), e.getMessage(), e);
            handleErrorsInEclipse(status, ecoreFile);
         }
      }
   }

   protected boolean isEcoreFile(final IResource ecoreResource)
   {
      return ecoreResource.getType() == IResource.FILE && "ecore".equals(ecoreResource.getFileExtension());
   }

   @Override
   protected final AntPatternCondition getTriggerCondition(final IProject project)
   {
      //      try
      //      {
      //TODO@rkluge: Add new nature here
      //         if (project.hasNature(WorkspaceHelper.REPOSITORY_NATURE_ID) || project.hasNature(WorkspaceHelper.INTEGRATION_NATURE_ID))
      //         {
      //            return new AntPatternCondition(new String[] { "gen/**" });
      //         } else if (project.hasNature(WorkspaceHelper.METAMODEL_NATURE_ID))
      //         {
      //            return new AntPatternCondition(new String[] { ".temp/*.moca.xmi" });
      //         }
      //      } catch (final CoreException e)
      //      {
      //         // Do nothing
      //      }
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

   private void handleInjectionWarningsAndErrors(final IStatus status)
   {
      final String reporterClass = "org.moflon.moca.inject.validation.InjectionErrorReporter";
      final ErrorReporter errorReporter = (ErrorReporter) Platform.getAdapterManager().loadAdapter(getProject(), reporterClass);
      if (errorReporter != null)
      {
         errorReporter.report(status);
      } else
      {
         logger.debug("Could not load error reporter '" + reporterClass + "'");
      }
   }
}
