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
import org.moflon.codegen.CodeGenerator;
import org.moflon.codegen.InjectionAwareGeneratorAdapterFactory;
import org.moflon.core.build.GenericMoflonProcess;
import org.moflon.core.build.MonitoredGenModelBuilder;
import org.moflon.core.preferences.EMoflonPreferencesStorage;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
import org.moflon.core.propertycontainer.MoflonPropertiesContainerHelper;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.emf.injection.build.CodeInjector;
import org.moflon.emf.injection.build.CodeInjectorImpl;
import org.moflon.emf.injection.build.InjectionExtractor;
import org.moflon.emf.injection.build.InjectionManager;
import org.moflon.emf.injection.build.XTextInjectionExtractor;

public class EMoflonEmfCodeGenerator extends GenericMoflonProcess
{
   private static final Logger logger = Logger.getLogger(EMoflonEmfCodeGenerator.class);

   private InjectionManager injectionManager;

   private GenModel genModel;

   public EMoflonEmfCodeGenerator(final IFile ecoreFile, final ResourceSet resourceSet, final EMoflonPreferencesStorage preferencesStorage)
   {
      super(ecoreFile, resourceSet, preferencesStorage);
   }

   @Override
   public String getTaskName()
   {
      return "Generating code";
   }

   @Override
   public IStatus processResource(final IProgressMonitor monitor)
   {
      try
      {
         final int totalWork = 5 + 10 + 10 + 15 + 35 + 30 + 5;
         final SubMonitor subMon = SubMonitor.convert(monitor, "Code generation task for " + getProject().getName(), totalWork);
         logger.info("Generating code for: " + getProject().getName());

         long toc = System.nanoTime();

         // (3) Build or load GenModel
         final MonitoredGenModelBuilder genModelBuilderJob = new MonitoredGenModelBuilder(getResourceSet(), getAllResources(), getEcoreFile(), true,
               getMoflonProperties());
         final IStatus genModelBuilderStatus = genModelBuilderJob.run(subMon.split(15));
         if (subMon.isCanceled())
         {
            return Status.CANCEL_STATUS;
         }
         if (genModelBuilderStatus.matches(IStatus.ERROR))
         {
            return genModelBuilderStatus;
         }
         this.genModel = genModelBuilderJob.getGenModel();

         // (4) Load injections
         final IProject project = getEcoreFile().getProject();

         final IStatus injectionStatus = createInjections(project, genModel);
         if (subMon.isCanceled())
         {
            return Status.CANCEL_STATUS;
         }
         if (injectionStatus.matches(IStatus.ERROR))
         {
            return injectionStatus;
         }

         // (6) Generate code
         subMon.subTask("Generating code for project " + project.getName());
         final Descriptor codeGenerationEngine = new InjectionAwareGeneratorAdapterFactory(injectionManager);
         final CodeGenerator codeGenerator = new CodeGenerator(codeGenerationEngine);
         final IStatus codeGenerationStatus = codeGenerator.generateCode(genModel, new BasicMonitor.EclipseSubProgress(subMon, 30));
         if (subMon.isCanceled())
         {
            return Status.CANCEL_STATUS;
         }
         if (codeGenerationStatus.matches(IStatus.ERROR))
         {
            return codeGenerationStatus;
         }
         subMon.worked(5);

         long tic = System.nanoTime();

         logger.info(String.format(Locale.US, "Completed in %.3fs", (tic - toc) / 1e9));

         return injectionStatus.isOK() ? Status.OK_STATUS : injectionStatus;
      } catch (final Exception e)
      {
         logger.debug(WorkspaceHelper.printStacktraceToString(e));
         return new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), IStatus.ERROR,
               e.getClass().getName() + " occurred during eMoflon code generation. Message: '" + e.getMessage() + "'. (Stacktrace is logged with level debug)",
               e);
      }
   }

   public final GenModel getGenModel()
   {
      return genModel;
   }

   public final InjectionManager getInjectorManager()
   {
      return injectionManager;
   }

   protected String getFullProjectName(final MoflonPropertiesContainer moflonProperties)
   {
      final String metaModelProjectName = moflonProperties.getMetaModelProject().getMetaModelProjectName();
      final String fullProjectName;
      if (MoflonPropertiesContainerHelper.UNDEFINED_METAMODEL_NAME.equals(metaModelProjectName))
      {
         fullProjectName = moflonProperties.getProjectName();
      } else
      {
         fullProjectName = metaModelProjectName + "::" + moflonProperties.getProjectName();
      }
      return fullProjectName;
   }

   /**
    * Loads the injections from the /injection folder
    */
   private IStatus createInjections(final IProject project, final GenModel genModel) throws CoreException
   {
      IFolder injectionFolder = WorkspaceHelper.addFolder(project, WorkspaceHelper.INJECTION_FOLDER, new NullProgressMonitor());
      CodeInjector injector = new CodeInjectorImpl(project.getLocation().toOSString());

      InjectionExtractor injectionExtractor = new XTextInjectionExtractor(injectionFolder, genModel);

      injectionManager = new InjectionManager(injectionExtractor, injector);
      return injectionManager.extractInjections();
   }
}
