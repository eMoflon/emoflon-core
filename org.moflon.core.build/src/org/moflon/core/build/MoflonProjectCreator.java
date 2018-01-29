package org.moflon.core.build;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.gervarro.eclipse.workspace.autosetup.JavaProjectConfigurator;
import org.gervarro.eclipse.workspace.autosetup.PluginProjectConfigurator;
import org.gervarro.eclipse.workspace.autosetup.ProjectConfigurator;
import org.gervarro.eclipse.workspace.autosetup.WorkspaceAutoSetupModule;
import org.gervarro.eclipse.workspace.util.ProjectUtil;
import org.gervarro.eclipse.workspace.util.WorkspaceTask;
import org.moflon.core.build.nature.MoflonProjectConfigurator;
import org.moflon.core.build.nature.ProjectNatureAndBuilderConfiguratorTask;
import org.moflon.core.plugins.BuildPropertiesFileBuilder;
import org.moflon.core.plugins.PluginProperties;
import org.moflon.core.plugins.manifest.ManifestFileUpdater;
import org.moflon.core.plugins.manifest.ManifestFileUpdater.AttributeUpdatePolicy;
import org.moflon.core.plugins.manifest.PluginManifestConstants;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
import org.moflon.core.propertycontainer.MoflonPropertiesContainerHelper;
import org.moflon.core.propertycontainer.SDMCodeGeneratorIds;
import org.moflon.core.utilities.MoflonConventions;
import org.moflon.core.utilities.WorkspaceHelper;

public abstract class MoflonProjectCreator extends WorkspaceTask implements ProjectConfigurator
{
   private static final Logger logger = Logger.getLogger(MoflonProjectCreator.class);

   private IProject project;

   private PluginProperties pluginProperties;

   private MoflonProjectConfigurator projectConfigurator;

   public MoflonProjectCreator(final IProject project, final PluginProperties projectProperties, final MoflonProjectConfigurator projectConfigurator)
   {
      this.project = project;
      this.pluginProperties = projectProperties;
      this.projectConfigurator = projectConfigurator;
   }

   /**
    * Returns the method body code generator to use.
    * @return the code generator ID to use. May be <code>null</code>.
    */
   protected abstract SDMCodeGeneratorIds getCodeGeneratorHandler();

   protected abstract List<String> getGitignoreLines();

   protected abstract String getNatureId() throws CoreException;

   protected abstract String getBuilderId() throws CoreException;

   @Override
   public void run(final IProgressMonitor monitor) throws CoreException
   {
      if (!project.exists())
      {
         final String projectName = pluginProperties.getProjectName();
         final SubMonitor subMon = SubMonitor.convert(monitor, "Creating project " + projectName, 13);

         // (1) Create project
         final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
         project.create(description, IWorkspace.AVOID_UPDATE, subMon.split(1));
         project.open(IWorkspace.AVOID_UPDATE, subMon.split(1));

         // (2) Configure natures and builders (.project file)
         final JavaProjectConfigurator javaProjectConfigurator = new JavaProjectConfigurator();
         final MoflonProjectConfigurator moflonProjectConfigurator = this.projectConfigurator;
         final PluginProjectConfigurator pluginProjectConfigurator = new PluginProjectConfigurator();
         final ProjectNatureAndBuilderConfiguratorTask natureAndBuilderConfiguratorTask = new ProjectNatureAndBuilderConfiguratorTask(project, false);
         natureAndBuilderConfiguratorTask.updateNatureIDs(moflonProjectConfigurator, true);
         natureAndBuilderConfiguratorTask.updateNatureIDs(javaProjectConfigurator, true);
         natureAndBuilderConfiguratorTask.updateBuildSpecs(javaProjectConfigurator, true);
         natureAndBuilderConfiguratorTask.updateBuildSpecs(moflonProjectConfigurator, true);
         natureAndBuilderConfiguratorTask.updateNatureIDs(pluginProjectConfigurator, true);
         natureAndBuilderConfiguratorTask.updateBuildSpecs(pluginProjectConfigurator, true);
         WorkspaceTask.executeInCurrentThread(natureAndBuilderConfiguratorTask, IWorkspace.AVOID_UPDATE, subMon.split(1));

         // (3) Create folders and files in project
         createFoldersIfNecessary(project, subMon.split(4));
         addGitignoreFile(project, subMon.split(2));
         addGitKeepFiles(project, subMon.split(2));

         // (4) Create MANIFEST.MF file
         logger.debug("Adding MANIFEST.MF");
         validatePluginProperties();
         new ManifestFileUpdater().processManifest(project, manifest -> {
            boolean changed = false;
            changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.MANIFEST_VERSION, "1.0", AttributeUpdatePolicy.KEEP);
            changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_MANIFEST_VERSION, "2", AttributeUpdatePolicy.KEEP);
            changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_NAME, pluginProperties.get(PluginProperties.NAME_KEY),
                  AttributeUpdatePolicy.KEEP);
            changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_SYMBOLIC_NAME,
                  pluginProperties.get(PluginProperties.PLUGIN_ID_KEY) + ";singleton:=true", AttributeUpdatePolicy.KEEP);
            changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_VERSION, "1.0", AttributeUpdatePolicy.KEEP);
            changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_VENDOR, "", AttributeUpdatePolicy.KEEP);
            changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_ACTIVATION_POLICY, "lazy", AttributeUpdatePolicy.KEEP);
            changed |= ManifestFileUpdater.updateAttribute(manifest, PluginManifestConstants.BUNDLE_EXECUTION_ENVIRONMENT,
                  pluginProperties.get(PluginProperties.JAVA_VERION), AttributeUpdatePolicy.KEEP);
            return changed;
         });

         // (5) Create build.properties file
         logger.debug("Adding build.properties");
         new BuildPropertiesFileBuilder().createBuildProperties(project, subMon.split(1));

         // (6) Configure Java settings (.classpath file)
         final IJavaProject javaProject = JavaCore.create(project);
         final IClasspathEntry srcFolderEntry = JavaCore.newSourceEntry(WorkspaceHelper.getSourceFolder(project).getFullPath());

         // Integration projects contain a lot of (useful?) boilerplate code in /gen, which requires to ignore warnings such as 'unused variable', 'unused import' etc.
         final IClasspathAttribute[] genFolderClasspathAttributes = pluginProperties.isIntegrationProject()
               ? new IClasspathAttribute[] { JavaCore.newClasspathAttribute("ignore_optional_problems", "true") }
               : new IClasspathAttribute[] {};
         final IClasspathEntry genFolderEntry = JavaCore.newSourceEntry(WorkspaceHelper.getGenFolder(project).getFullPath(), new IPath[0], new IPath[0], null,
               genFolderClasspathAttributes);
         final IClasspathEntry injectionFolderEntry = JavaCore.newSourceEntry(WorkspaceHelper.getInjectionFolder(project).getFullPath(), new IPath[0],
               new IPath[0], null, genFolderClasspathAttributes);
         final IClasspathEntry jreContainerEntry = JavaCore.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER"));
         final IClasspathEntry pdeContainerEntry = JavaCore.newContainerEntry(new Path("org.eclipse.pde.core.requiredPlugins"));
         javaProject.setRawClasspath(new IClasspathEntry[] { srcFolderEntry, genFolderEntry, injectionFolderEntry, jreContainerEntry, pdeContainerEntry },
               WorkspaceHelper.getBinFolder(project).getFullPath(), true, subMon.split(1));

         // (7) Create Moflon properties file (moflon.properties.xmi)
         final MoflonPropertiesContainer moflonProperties = MoflonPropertiesContainerHelper.loadOrCreatePropertiesContainer(getProject(), MoflonConventions.getDefaultMoflonPropertiesFile(getProject()));
         initializeMoflonProperties(moflonProperties);
         MoflonPropertiesContainerHelper.save(moflonProperties, subMon.split(1));
      }
   }

   /**
    * Initializes the contents of the file {@link MoflonConventions#MOFLON_CONFIG_FILE}.
    *
    * The file will be saved afterwards.
    *
    * When overriding this method, subclasses should invoke the parent class's {@link #initializeMoflonProperties(MoflonPropertiesContainer)} in any case!
    *
    * @param moflonProperties the properties container
    */
   protected void initializeMoflonProperties(final MoflonPropertiesContainer moflonProperties)
   {
      final SDMCodeGeneratorIds codeGeneratorHandler = getCodeGeneratorHandler();
      if (codeGeneratorHandler != null)
      {
         moflonProperties.getSdmCodegeneratorHandlerId().setValue(codeGeneratorHandler);
      }
   }

   /**
    * Returns the handle to the project that shall be created.
    * Of course, the project need not exist yet.
    *
    * @return the handle to the project to create
    */
   public final IProject getProject()
   {
      return project;
   }

   /**
    * Returns the properties of the plugin project to create
    * @return the plugin properties
    */
   public PluginProperties getPluginProperties()
   {
      return pluginProperties;
   }

   private void validatePluginProperties() throws CoreException
   {
      validateNotNull(pluginProperties, PluginProperties.NAME_KEY);
      validateNotNull(pluginProperties, PluginProperties.PLUGIN_ID_KEY);
      validateNotNull(pluginProperties, PluginProperties.JAVA_VERION);
   }

   private void validateNotNull(final PluginProperties pluginProperties, final String key) throws CoreException
   {
      if (!pluginProperties.containsKey(key))
         throw new CoreException(
               new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()), String.format("Key %s not found in %s", key, pluginProperties)));
   }

   /**
    * Adds a default .gitignore file to the given project to prevent adding generated files to the repository
    *
    * The contents of the created file are fetched from {@link #getGitignoreLines()}
    *
    * @param project the project for which to generate the .gitignore file
    * @param monitor the progress monitor
    */
   public void addGitignoreFile(final IProject project, final IProgressMonitor monitor) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Creating .gitignore file for " + project, 1);

      WorkspaceHelper.createGitignoreFileIfNotExists(project.getFile(WorkspaceHelper.GITIGNORE_FILENAME), //
            getGitignoreLines(), subMon.split(1));
   }

   public void createFoldersIfNecessary(final IProject project, final IProgressMonitor monitor) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Creating folders within project " + project, 9);

      WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getSourceFolder(project), subMon.split(1));
      WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getBinFolder(project), subMon.split(1));
      WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getGenFolder(project), subMon.split(1));
      WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getLibFolder(project), subMon.split(1));
      WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getModelFolder(project), subMon.split(1));
      WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getInstancesFolder(project), subMon.split(1));
      WorkspaceHelper.createFolderIfNotExists(WorkspaceHelper.getInjectionFolder(project), subMon.split(1));
   }

   /**
    * Adds dummy files to folders that are / may be empty after project initialization.
    *
    * The dummy files are required because Git does not support versioning empty folders (unlike SVN).
    *
    * @param project the project for which .keep files shall be produced
    * @param monitor the progress monitor
    */
   protected void addGitKeepFiles(final IProject project, final IProgressMonitor monitor)
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Creating .keep* files for Git within project " + project, 3);

      WorkspaceHelper.createKeepFile(WorkspaceHelper.getSourceFolder(project), subMon.split(1));
      WorkspaceHelper.createKeepFile(WorkspaceHelper.getGenFolder(project), subMon.split(1));
      WorkspaceHelper.createKeepFile(WorkspaceHelper.getModelFolder(project), subMon.split(1));
   }

   @Override
   public String getTaskName()
   {
      return "Creating Moflon project";
   }

   @Override
   public ISchedulingRule getRule()
   {
      return ResourcesPlugin.getWorkspace().getRoot();
   }

   @Override
   public String[] updateNatureIDs(String[] natureIDs, final boolean added) throws CoreException
   {
      final String natureId = getNatureId();
      if (added)
      {
         if (ProjectUtil.indexOf(natureIDs, natureId) < 0)
         {
            natureIDs = Arrays.copyOf(natureIDs, natureIDs.length + 1);
            natureIDs[natureIDs.length - 1] = natureId;
         }
      } else
      {
         int naturePosition = ProjectUtil.indexOf(natureIDs, natureId);
         if (naturePosition >= 0)
         {
            natureIDs = WorkspaceAutoSetupModule.remove(natureIDs, naturePosition);
         }
      }
      return natureIDs;
   }

   @Override
   public ICommand[] updateBuildSpecs(final IProjectDescription description, ICommand[] buildSpecs, final boolean added) throws CoreException
   {
      final String builderId = getBuilderId();

      if (added)
      {
         int javaBuilderPosition = ProjectUtil.indexOf(buildSpecs, "org.eclipse.jdt.core.javabuilder");
         int moflonBuilderPosition = ProjectUtil.indexOf(buildSpecs, builderId);
         if (moflonBuilderPosition < 0)
         {
            final ICommand manifestBuilder = description.newCommand();
            manifestBuilder.setBuilderName(builderId);
            buildSpecs = Arrays.copyOf(buildSpecs, buildSpecs.length + 1);
            moflonBuilderPosition = buildSpecs.length - 1;
            buildSpecs[moflonBuilderPosition] = manifestBuilder;
         }
         if (javaBuilderPosition < moflonBuilderPosition)
         {
            final ICommand moflonBuilder = buildSpecs[moflonBuilderPosition];
            System.arraycopy(buildSpecs, javaBuilderPosition, buildSpecs, javaBuilderPosition + 1, moflonBuilderPosition - javaBuilderPosition);
            moflonBuilderPosition = javaBuilderPosition++;
            buildSpecs[moflonBuilderPosition] = moflonBuilder;
         }
      } else
      {
         int moflonBuilderPosition = ProjectUtil.indexOf(buildSpecs, builderId);
         if (moflonBuilderPosition >= 0)
         {
            ICommand[] oldBuilderSpecs = buildSpecs;
            buildSpecs = new ICommand[oldBuilderSpecs.length - 1];
            if (moflonBuilderPosition > 0)
            {
               System.arraycopy(oldBuilderSpecs, 0, buildSpecs, 0, moflonBuilderPosition);
            }
            if (moflonBuilderPosition == buildSpecs.length)
            {
               System.arraycopy(oldBuilderSpecs, moflonBuilderPosition + 1, buildSpecs, moflonBuilderPosition, buildSpecs.length - moflonBuilderPosition);
            }
         }
      }
      return buildSpecs;
   }
}
