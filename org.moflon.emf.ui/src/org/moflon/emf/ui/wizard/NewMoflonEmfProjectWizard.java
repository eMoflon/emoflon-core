package org.moflon.emf.ui.wizard;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IWorkingSet;
import org.moflon.core.build.MoflonProjectCreator;
import org.moflon.core.plugins.PluginProducerWorkspaceRunnable;
import org.moflon.core.plugins.PluginProperties;
import org.moflon.core.ui.AbstractMoflonProjectInfoPage;
import org.moflon.core.ui.AbstractMoflonWizard;
import org.moflon.core.ui.WorkingSetUtilities;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.MoflonConventions;
import org.moflon.core.utilities.MoflonUtil;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.emf.build.MoflonEmfNature;
import org.moflon.emf.codegen.MoflonGenModelBuilder;

/**
 * This wizard creates a new eMoflon EMF project
 * 
 * @author Roland Kluge - Initial implementation
 *
 * @see #doFinish(IProgressMonitor)
 */
public class NewMoflonEmfProjectWizard extends AbstractMoflonWizard
{
   private static final Logger logger = Logger.getLogger(NewMoflonEmfProjectWizard.class);

   /**
    * This is the ID that is also used in plugin.xml
    */
   public static final String NEW_REPOSITORY_PROJECT_WIZARD_ID = "org.moflon.emf.ui.wizard.NewMoflonEmfProjectWizard";

   protected AbstractMoflonProjectInfoPage projectInfo;

   /**
    * Configures this wizard to use the {@link NewMoflonEmfProjectInfoPage}
    */
   @Override
   public void addPages()
   {
      projectInfo = new NewMoflonEmfProjectInfoPage();
      addPage(projectInfo);
   }

   @Override
   protected void doFinish(final IProgressMonitor monitor) throws CoreException
   {
      try
      {
         final SubMonitor subMon = SubMonitor.convert(monitor, "Creating eMoflon EMF project", 8);

         final String projectName = projectInfo.getProjectName();

         final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
         final PluginProperties pluginProperties = new PluginProperties();
         pluginProperties.put(PluginProperties.NAME_KEY, projectName);
         pluginProperties.put(PluginProperties.PLUGIN_ID_KEY, projectName);
         createProject(subMon, project, pluginProperties);
         subMon.worked(3);

         generateDefaultFiles(subMon, project);
         subMon.worked(3);

         ResourcesPlugin.getWorkspace().run(new PluginProducerWorkspaceRunnable(project, pluginProperties), subMon.split(1));
         subMon.worked(2);

         addToWorkingSet(project);

      } catch (final Exception e)
      {
         LogUtils.error(logger, e);
      }
   }

   /**
    * Initializes and runs the {@link MoflonProjectCreator} for the current project
    * @param monitor the progress monitor
    * @param project the project being created
    * @param pluginProperties the metadata of the project
    * @throws CoreException if setting up the project fails
    */
   protected void createProject(final IProgressMonitor monitor, final IProject project, final PluginProperties pluginProperties) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Creating project", 1);
      final MoflonProjectCreator createMoflonProject = new MoflonEmfProjectCreator(project, pluginProperties, new MoflonEmfNature());
      ResourcesPlugin.getWorkspace().run(createMoflonProject, subMon.split(1));
   }

   /**
    * Stores the default Ecore file in the proper location
    * @param monitor the progress monitor
    * @param project the project being created currently
    * @throws CoreException if storing the file fails
    */
   protected void generateDefaultFiles(final IProgressMonitor monitor, final IProject project) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Creating default files", 1);
      final String projectName = project.getName();
      final String packageName = MoflonUtil.lastSegmentOf(projectName);
      final URI projectUri = MoflonGenModelBuilder.determineProjectUriBasedOnPreferences(project);
      final URI packageUri = URI.createURI(projectUri.toString() + MoflonConventions.getDefaultPathToEcoreFileInProject(projectName));
      
      if(projectInfo.generateDefaultEmfaticFile()) {
    	  final String defaultEmfaticFile = DefaultContentGenerator.generateDefaultEmfaticFileForProject(projectName, packageName, packageUri.toString());
    	  WorkspaceHelper.addFile(project, MoflonConventions.getDefaultPathToFileInProject(projectName, ".emf"), defaultEmfaticFile, subMon.split(1));
      } else {
    	  final String defaultEcoreFile = DefaultContentGenerator.generateDefaultEPackageForProject(projectName, packageName, packageUri.toString());
    	  WorkspaceHelper.addFile(project, MoflonConventions.getDefaultPathToEcoreFileInProject(projectName), defaultEcoreFile, subMon.split(1));
      }
   }

   /**
    * Adds the given project to the selected working set (if exists)
    * @param project the project being creatd
    */
   private void addToWorkingSet(final IProject project)
   {
      final IWorkingSet[] recentWorkingSet = WorkingSetUtilities.getSelectedWorkingSet(getSelection(), getActivePart());
      if (recentWorkingSet.length != 0)
      {
         WorkingSetUtilities.addProjectToWorkingSet(project, recentWorkingSet[0]);
      }
   }
}
