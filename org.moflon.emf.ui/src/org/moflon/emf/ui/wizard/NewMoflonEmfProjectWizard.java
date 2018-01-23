package org.moflon.emf.ui.wizard;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.moflon.core.build.MoflonProjectCreator;
import org.moflon.core.plugins.PluginProperties;
import org.moflon.core.plugins.PluginProducerWorkspaceRunnable;
import org.moflon.core.ui.AbstractMoflonProjectInfoPage;
import org.moflon.core.ui.AbstractMoflonWizard;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.MoflonUtil;
import org.moflon.core.utilities.WorkspaceHelper;
import org.moflon.emf.build.MoflonEmfNature;

public class NewMoflonEmfProjectWizard extends AbstractMoflonWizard
{
   private static final Logger logger = Logger.getLogger(NewMoflonEmfProjectWizard.class);

   public static final String NEW_REPOSITORY_PROJECT_WIZARD_ID = "org.moflon.emf.ui.wizard.NewMoflonEmfProjectWizard";

   protected AbstractMoflonProjectInfoPage projectInfo;

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
         pluginProperties.setDefaultValues();
         pluginProperties.put(PluginProperties.TYPE_KEY, PluginProperties.MOFLON_EMF_PROJECT);
         pluginProperties.put(PluginProperties.NAME_KEY, projectName);
         pluginProperties.put(PluginProperties.PLUGIN_ID_KEY, projectName);
         createProject(subMon, project, pluginProperties);
         subMon.worked(3);

         generateDefaultFiles(subMon, project);
         subMon.worked(3);

         ResourcesPlugin.getWorkspace().run(new PluginProducerWorkspaceRunnable(project, pluginProperties), subMon.split(1));
         subMon.worked(2);
      } catch (final Exception e)
      {
         LogUtils.error(logger, e);
      }
   }

   protected void generateDefaultFiles(final IProgressMonitor monitor, IProject project) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Creating default files", 1);
      String defaultEcoreFile = generateDefaultEPackageForProject(project.getName());
      WorkspaceHelper.addFile(project, MoflonUtil.getDefaultPathToEcoreFileInProject(project.getName()), defaultEcoreFile, subMon.split(1));
   }

   protected void createProject(IProgressMonitor monitor, IProject project, PluginProperties pluginProperties) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Creating project", 1);
      final MoflonProjectCreator createMoflonProject = new MoflonEmfProjectCreator(project, pluginProperties, new MoflonEmfNature());
      ResourcesPlugin.getWorkspace().run(createMoflonProject, subMon.split(1));
   }

   /**
    * Generates an XMI representation of the EPackage corresponding to the given project name
    * @param projectName the project name from which the conventional EPackage name etc. are derived
    * @return the raw XMI file content
    */
   private static String generateDefaultEPackageForProject(final String projectName)
   {
      final StringBuilder sb = new StringBuilder();
      sb.append("<?xml version=\"1.0\" encoding=\"ASCII\"?>");
      sb.append("<ecore:EPackage xmi:version=\"2.0\"");
      sb.append("  xmlns:xmi=\"http://www.omg.org/XMI\"");
      sb.append("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
      sb.append("  xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\"");
      sb.append("  name=\"" + MoflonUtil.lastSegmentOf(projectName) + "\"");
      sb.append("  nsURI=\"" + MoflonUtil.getDefaultURIToEcoreFileInPlugin(projectName) + "\"");
      sb.append("  nsPrefix=\"" + projectName + "\">");
      sb.append("</ecore:EPackage>");
      return sb.toString();
   }
}
