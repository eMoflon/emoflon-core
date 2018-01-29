package org.moflon.core.utilities;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;

/**
 * This class captures all conventions used by the EMF build process of eMoflon
 *
 * Conventions include
 * * The Bundle-SymbolicName and the project name of projects created with eMoflon are identical
 * * The generated Ecore file is named after the last segment of the project name
 *   * For instance, in project P/x.y.z.mymodel, the Ecore file is located here: P/x.y.z.mymodel/model/Mymodel.ecore
 * * The default NS URIs use the platform:/resource schema.
 *   * For instance, the metamodel of project P/x.y.z.mymodel should have
 *     (i)   root package 'mymodel',
 *     (ii)  NS prefix 'x.y.z.mymodel' and
 *     (iii) NS URI 'platform:/resource/x.y.z.mymodel/model/Mymodel.ecore'
 *
 * @author Roland Kluge - Initial implementation
 */
public class MoflonConventions
{

   public static URI getDefaultURIToEcoreFileInPlugin(final String pluginID)
   {
      return URI.createPlatformResourceURI("/" + pluginID + "/" + MoflonConventions.getDefaultPathToEcoreFileInProject(pluginID), true);
   }

   public static String getDefaultNameOfFileInProjectWithoutExtension(final String projectName)
   {
      return MoflonUtil.lastCapitalizedSegmentOf(projectName);
   }

   public static String getDefaultPathToFileInProject(final String projectName, final String ending)
   {
      return "model/" + getDefaultNameOfFileInProjectWithoutExtension(projectName) + ending;
   }

   public static String getDefaultPathToGenModelInProject(final String projectName)
   {
      return getDefaultPathToFileInProject(projectName, ".genmodel");
   }

   public static String getDefaultPathToEcoreFileInProject(final String projectName)
   {
      return getDefaultPathToFileInProject(projectName, ".ecore");
   }

   public static final URI getDefaultProjectRelativeEcoreFileURI(final IProject project)
   {
      final String ecoreFileName = MoflonUtil.lastCapitalizedSegmentOf(project.getName());
      return URI.createURI(WorkspaceHelper.MODEL_FOLDER + "/" + ecoreFileName + WorkspaceHelper.ECORE_FILE_EXTENSION);
   }

   public static final URI getDefaultEcoreFileURI(final IProject project)
   {
      return getDefaultProjectRelativeEcoreFileURI(project).resolve(URI.createPlatformResourceURI(project.getName() + "/", true));
   }

}
