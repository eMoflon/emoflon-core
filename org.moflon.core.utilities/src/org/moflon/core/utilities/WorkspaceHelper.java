package org.moflon.core.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * A collection of useful helper methods when dealing with a workspace in an eclipse plugin.
 */
public class WorkspaceHelper
{

   private static final Logger logger = Logger.getLogger(WorkspaceHelper.class);

   public final static String PATH_SEPARATOR = "/";

   /**
    * To avoid problems with line endings in version controls systems, all resources serialized by eMoflon should have consistent line endings.
    *
    * The following code snippet shows how to use this option:
    * <pre>
    * Resource resource = ...;
    * HashMap<String, String> saveOptions = new HashMap<String, String>();
    * saveOptions.put(Resource.OPTION_LINE_DELIMITER, WorkspaceHelper.DEFAULT_DELIMITER_FOR_RESOURCE_SERIALIZATION);
    * resource.save(saveOptions);
    * </pre>
    */
   public static final String DEFAULT_RESOURCE_LINE_DELIMITER = "\n";

   /**
    * Constants for project structure
    */

   public final static String MODEL_FOLDER = "model";

   private static final String SOURCE_FOLDER = "src";

   private static final String BIN_FOLDER = "bin";

   private static final String LIB_FOLDER = "lib";

   public static final String GEN_FOLDER = "gen";

   public static final String INJECTION_FOLDER = "injection";

   public static final String INJECTION_FILE_EXTENSION = "inject";

   private static final String INSTANCES_FOLDER = "instances";

   private static final String KEEP_EMPTY_FOLDER_FILE_NAME_FOR_GIT = ".keep";

   public static final String GITIGNORE_FILENAME = ".gitignore";

   /**
    * Constants for Ecore
    */
   public static final String GEN_MODEL_EXT = ".genmodel";

   public static final String ECORE_FILE_EXTENSION = ".ecore";

   public static final String PLUGIN_ID_ECORE = "org.eclipse.emf.ecore";

   public static final String PLUGIN_ID_EMF_COMMON = "org.eclipse.emf.common";

   public static final String PLUGIN_ID_ECORE_XMI = "org.eclipse.emf.ecore.xmi";

   /**
    * Constants for XText
    */
   public static final String XTEXT_BUILDER_ID = "org.eclipse.xtext.ui.shared.xtextBuilder";

   public static final String XTEXT_NATURE_ID = "org.eclipse.xtext.ui.shared.xtextNature";

   public static final String MWE2_FILE_EXTENSION = "mwe2";

   /**
    * Constants misc
    */
   public static final String PLUGIN_NATURE_ID = "org.eclipse.pde.PluginNature"; // PDE.NATURE_ID

   public static final String PLUGIN_ID_ECLIPSE_RUNTIME = "org.eclipse.core.runtime";

   public static final String DEFAULT_LOG4J_DEPENDENCY = "org.apache.log4j" + ";bundle-version=\"1.2.15\"";

   public static final String ISSUE_TRACKER_URL = "https://github.com/eMoflon/emoflon-tool/issues";

   public final static String MOFLON_PROBLEM_MARKER_ID = "org.moflon.ide.marker.EMoflonProblem";

   public static final String INJECTION_PROBLEM_MARKER_ID = "org.moflon.ide.marker.InjectionProblem";


   /**
    * Adds a file to project root, retrieving its contents from the specified location
    *
    * @param project
    *           the project to which the file will be added
    * @param fileName
    *           name of the new file relative to the project
    * @param pathToContent
    *           path to a file relative to the project, that contains the contents of the new file to be created
    * @param pluginID
    *           id of plugin that is adding the file
    * @param monitor
    *           a progress monitor, or null if progress reporting is not desired
    * @throws CoreException
    * @throws URISyntaxException
    * @throws IOException
    */
   public static void addFile(final IProject project, final String fileName, final URL pathToContent, final String pluginID, final IProgressMonitor monitor)
         throws CoreException, URISyntaxException, IOException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "", 1);

      final IFile projectFile = project.getFile(fileName);
      final InputStream contents = pathToContent.openStream();
      try
      {
         projectFile.create(contents, true, subMon.split(1));
      } finally {
         IOUtils.closeQuietly(contents);
      }
   }

   /**
    * Adds and fills a file to project root, containing specified contents as a string
    *
    * If the file does not exist, it is created.
    * If it exists, its contents are overwritten
    *
    * @param project
    *           Name of project the file should be added to
    * @param fileName
    *           Name of file to add to project
    * @param contents
    *           What the file should contain as a String
    * @param monitor
    *           Monitor to indicate progress
    * @throws CoreException
    */
   public static void addFile(final IProject project, final String fileName, final String contents, final IProgressMonitor monitor) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "", 1);
      IFile projectFile = project.getFile(fileName);
      ByteArrayInputStream source = new ByteArrayInputStream(contents.getBytes());
      if (projectFile.exists())
      {
         projectFile.setContents(source, true, true, subMon.split(1));
      } else
      {
         projectFile.create(source, true, subMon.split(1));
      }
   }

   /**
    * Creates the given file (if not exists) and stores the given contents in it.
    *
    * If the file exists, its content is replaced with the given content.
    *
    * @param file
    * @param contents
    * @param monitor
    *           the monitor that reports on the progress
    * @throws CoreException
    */
   private static void addFile(final IFile file, final String contents, final IProgressMonitor monitor) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Add file", 1);
      final ByteArrayInputStream source = new ByteArrayInputStream(contents.getBytes());
      if (file.exists())
      {
         file.setContents(source, IFile.FORCE | IFile.KEEP_HISTORY, subMon.split(1));
      } else
      {
         file.create(source, true, subMon.split(1));
      }
   }

   /**
    * Creates a default ".keep"-file in the given folder.
    *
    * The name consists of the prefix .keep and the parent folder name to avoid warnings about identically named files that some build tools issue
    *
    * @param folder the folder to keep in version control system
    * @param monitor the progress monitor
    */
   public static void createKeepFile(final IFolder folder, final IProgressMonitor monitor)
   {
      final String filename = KEEP_EMPTY_FOLDER_FILE_NAME_FOR_GIT + folder.getName();
      try
      {
         final SubMonitor subMon = SubMonitor.convert(monitor, String.format("Creating %s", filename), 1);
         final IFile keepFile = folder.getFile(filename);
         if (!keepFile.exists())
         {
            keepFile.create(new ByteArrayInputStream(new String("Dummy file to protect empty folder in Git.\n").getBytes()), true, subMon.split(1));
         }
      } catch (CoreException e)
      {
         LogUtils.warn(logger, "Error during creation of file %s in folder %s .", filename, folder);
      }
   }

   /**
    * Creates the given file with the given content if the file does not exist yet.
    *
    * If the file already exists, nothing happens.
    *
    * @param gitignoreFile the file to be created
    * @param lines the contents of the new file
    * @param monitor the progress monitor
    * @throws CoreException if creating the file fails
    */
   public static void createGitignoreFileIfNotExists(final IFile gitignoreFile, final List<String> lines, final IProgressMonitor monitor) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Creating file " + gitignoreFile, 1);

      if (!gitignoreFile.exists())
      {
         final String genFolderGitIgnoreFileContents = StringUtils.join(lines, "\n");
         gitignoreFile.create(new ByteArrayInputStream(genFolderGitIgnoreFileContents.getBytes()), true, subMon.split(1));
      }
   }

   /**
    * Adds a new folder with name 'folderName' to project
    *
    * @param project
    *           the project on which the folder will be added
    * @param folderName
    *           name of the new folder
    * @param monitor
    *           a progress monitor, or null if progress reporting is not desired
    * @return newly created folder
    * @throws CoreException
    */
   public static IFolder addFolder(final IProject project, final String folderName, final IProgressMonitor monitor) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "", 1);

      final IFolder projFolder = project.getFolder(folderName);
      if (!projFolder.exists())
         projFolder.create(true, true, subMon.split(1));
      return projFolder;
   }

   /**
    * Creates a folder denoted by the path inside the given project.
    *
    * @param project
    * @param path
    *           the path, separated with {@link WorkspaceHelper#PATH_SEPARATOR}
    * @param monitor
    * @throws CoreException
    */
   public static void addAllFolders(final IProject project, final String path, final IProgressMonitor monitor) throws CoreException
   {
      final String[] folders = path.split(PATH_SEPARATOR);
      final SubMonitor subMon = SubMonitor.convert(monitor, "Add folders", folders.length);
      StringBuilder currentFolder = new StringBuilder();
      for (String folder : folders)
      {
         currentFolder.append(PATH_SEPARATOR).append(folder);
         addFolder(project, currentFolder.toString(), subMon.split(1));
      }
   }

   /**
    * Returns whether the given resource is of type {@link IResource#FOLDER}
    */
   public static boolean isFolder(final IResource resource)
   {
      return resource.getType() == IResource.FOLDER;
   }


   /**
    * Returns a handle to the /bin folder of the project
    *
    * @see WorkspaceHelper#BIN_FOLDER
    */
   public static IFolder getBinFolder(IProject project)
   {
      return project.getFolder(BIN_FOLDER);
   }

   /**
    * Returns a handle to the /src folder of the project
    *
    * @see WorkspaceHelper#SOURCE_FOLDER
    */
   public static IFolder getSourceFolder(IProject project)
   {
      return project.getFolder(SOURCE_FOLDER);
   }

   /**
    * Returns a handle to the /gen folder of the project
    *
    * @see WorkspaceHelper#GEN_FOLDER
    */
   public static IFolder getGenFolder(final IProject project)
   {
      return project.getFolder(GEN_FOLDER);
   }

   /**
    * Returns a handle to the /model folder of the project
    *
    * @see WorkspaceHelper#MODEL_FOLDER
    */
   public static IFolder getModelFolder(final IProject project)
   {
      return project.getFolder(MODEL_FOLDER);
   }

   /**
    * Returns a handle to the /instances folder of the project
    *
    * @see WorkspaceHelper#INSTANCES_FOLDER
    */
   public static IFolder getInstancesFolder(final IProject project)
   {
      return project.getFolder(INSTANCES_FOLDER);
   }

   /**
    * Returns a handle to the /lib folder of the project
    *
    * @see WorkspaceHelper#LIB_FOLDER
    */
   public static IFolder getLibFolder(final IProject project)
   {
      return project.getFolder(LIB_FOLDER);
   }

   /**
    * Returns a handle to the /injection folder of the project
    *
    * @see WorkspaceHelper#INJECTION_FOLDER
    */
   public static IFolder getInjectionFolder(final IProject project)
   {
      return project.getFolder(INJECTION_FOLDER);
   }

   /**
    * Creates the given folder (and any missing intermediate folders) if it does not exist yet.
    *
    * @param folder
    * @param monitor
    */
   public static void createFolderIfNotExists(final IFolder folder, final IProgressMonitor monitor) throws CoreException
   {
      final IPath projectRelativePath = folder.getProjectRelativePath();
      final int segmentCount = projectRelativePath.segmentCount();
      final SubMonitor subMon = SubMonitor.convert(monitor, "Creating " + folder, segmentCount);
      for (int i = segmentCount - 1; i >= 0; --i)
      {
         final IFolder subFolder = folder.getProject().getFolder(projectRelativePath.removeLastSegments(i));
         if (!subFolder.exists())
            subFolder.create(true, true, subMon.split(1));
      }
   }

   /**
    * Returns the description of the given project with the given nature ID added to the project's list of natures
    */
   public static IProjectDescription getDescriptionWithAddedNature(final IProject project, final String natureId, final IProgressMonitor monitor)
         throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Create description with added natures", 1);

      IProjectDescription description = project.getDescription();

      List<String> natures = new ArrayList<>(Arrays.asList(description.getNatureIds()));

      if (!natures.contains(natureId))
      {
         natures.add(natureId);
         description.setNatureIds(natures.toArray(new String[natures.size()]));
      }

      subMon.worked(1);

      return description;
   }

   /**
    * Adds natureId to project
    *
    * @param project
    *           Handle to existing project
    * @param natureId
    *           ID of nature to be added
    * @param monitor
    *           a progress monitor, or null if progress reporting is not desired
    * @throws CoreException
    *            if unable to add nature
    */
   public static void addNature(final IProject project, final String natureId, final IProgressMonitor monitor) throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Add nature to project", 2);

      IProjectDescription description = getDescriptionWithAddedNature(project, natureId, subMon.split(1));
      project.setDescription(description, subMon.split(1));
   }


   /**
    * Creates a file at pathToFile with specified contents fileContent. All folders in the path are created if
    * necessary.
    *
    * @param project
    *           Project containing file to be created
    * @param pathToFile
    *           Project relative path to file to be created
    * @param fileContent
    *           String content of file to be created
    * @param monitor
    * @throws CoreException
    */
   public static void addAllFoldersAndFile(final IProject project, final IPath pathToFile, final String fileContent, final IProgressMonitor monitor)
         throws CoreException
   {
      final SubMonitor subMon = SubMonitor.convert(monitor, "Adding file " + pathToFile + " to project " + project, 2);
      final IPath pathWithoutFileSegment = pathToFile.removeLastSegments(1);

      addAllFolders(project, pathWithoutFileSegment.toString(), subMon.split(1));

      addFile(project.getFile(pathToFile), fileContent, subMon.split(1));
   }

   /**
    * Checks whether the given project has the {@link #PLUGIN_NATURE_ID}. If the check throws an exception,
    * <code>false</code> is returned gracefully.
    */
   public static boolean isPluginProjectNoThrow(final IProject project)
   {
      try
      {
         return project.hasNature(PLUGIN_NATURE_ID);
      } catch (Exception e)
      {
         return false;
      }
   }

   /**
    * Returns whether the given resource is of type {@link IResource#FILE}
    *
    * The resource may be <code>null</code>.
    *
    * @return true if the given resource is an {@link IFile}
    */
   public static boolean isFile(final IResource resource)
   {
      return resource != null && resource.getType() == IResource.FILE;
   }

   public static IProject getProjectByName(final String projectName)
   {
      return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
   }

   /**
    * Returns the project with the given plugin id in the workspace.
    *
    * If no such project can be found, null is returned.
    *
    * @param pluginId
    *           the plugin id
    * @return the project with the plugin id or null if not such project exists
    */
   public static IProject getProjectByPluginId(final String pluginId)
   {
      return getAllProjectsInWorkspace().stream().filter(project -> {
         return doesProjectHavePluginId(project, pluginId);
      }).findAny().orElse(null);
   }

   private static boolean doesProjectHavePluginId(final IProject project, final String desiredPluginId)
   {
      IPluginModelBase pluginModel = PluginRegistry.findModel(project);
      if (pluginModel != null && pluginModel.getBundleDescription() != null)
      {
         final String actualPluginId = pluginModel.getBundleDescription().getSymbolicName();
         return desiredPluginId.equals(actualPluginId);
      } else
      {
         return false;
      }
   }

   /**
    * Returns the list of all projects in the workspace
    */
   public static List<IProject> getAllProjectsInWorkspace()
   {
      return Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects());
   }

   /**
    * Returns the file name of the injection file for a given Java file.
    *
    * This method assumes that the first segment in the path is the source folder (e.g.,"/src"). The injection file name
    * is obtained by replacing the first segment of the input file name with {@link WorkspaceHelper#INJECTION_FOLDER}
    * and by replacing the file extension with {@link WorkspaceHelper#INJECTION_FILE_EXTENSION}.
    *
    * The resulting path needs to be resolved against a project via {@link IProject#getFile(IPath)}.
    *
    * @param javaFile
    *           the Java file
    * @return the path to the injection file
    */
   public static IPath getPathToInjection(final IFile javaFile)
   {
      final IPath packagePath = javaFile.getProjectRelativePath().removeFirstSegments(1);
      final IPath pathToInjection = packagePath.removeFileExtension().addFileExtension(INJECTION_FILE_EXTENSION);
      final IFolder injectionFolder = javaFile.getProject().getFolder(WorkspaceHelper.INJECTION_FOLDER);
      final IPath fullInjectionPath = injectionFolder.getProjectRelativePath().append(pathToInjection);
      return fullInjectionPath;
   }

   public static String getFullyQualifiedClassName(final IFile javaFile)
   {
      final IPath packagePath = javaFile.getProjectRelativePath().removeFirstSegments(1);
      final IPath pathToJavaFile = packagePath.removeFileExtension();
      final String fullyQualifiedClassName = pathToJavaFile.toPortableString().replace("/", ".");
      return fullyQualifiedClassName;
   }

   /**
    * Returns a file handle for the EAP file in the given metamodel project.
    *
    * The file need not exist and needs to be checked using {@link IFile#exists()}.
    *
    * @param metamodelProject
    * @return the file handle. Never null.
    */
   public static IFile getEapFileFromMetamodelProject(final IProject metamodelProject)
   {
      return metamodelProject.getFile(metamodelProject.getName().concat(".eap"));
   }

   /**
    * Returns a handle to the default location of a metamodel file ("ecore file") of a repository or integration
    * project.
    *
    * @param project
    *           the project of which to extract the ecore file
    */
   public static IFile getDefaultEcoreFile(final IProject project)
   {
      String ecoreFileName = MoflonUtil.lastCapitalizedSegmentOf(project.getName());
      return project.getFolder(MODEL_FOLDER).getFile(ecoreFileName + ECORE_FILE_EXTENSION);
   }

   /**
    * Prints the stacktrace of the given {@link Throwable} to a string.
    *
    * If t is null, then the result is the empty string.
    */
   public static String printStacktraceToString(final Throwable t)
   {
      if (null == t)
         return "";

      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      t.printStackTrace(new PrintStream(stream));
      return new String(stream.toByteArray());
   }

   /**
    * Returns the symbolic name (aka. plugin ID) of the bundle containing the given class.
    * @param clazz the class whose bundle is searched
    * @return the symbolic name or null if the class does not belong to a bundle
    */
   public static String getPluginId(final Class<?> clazz)
   {
      final Bundle bundle = FrameworkUtil.getBundle(clazz);
      return bundle == null ? null : bundle.getSymbolicName();
   }

   /**
    * Returns a string that represents the severity of the given status object
    *
    * The code of this method has been extracted from {@link Status#toString()}.
    * @param status the status
    * @return the severity (as string)
    * @see IStatus#getSeverity()
    */
   public static String getSeverityAsString(final IStatus status)
   {
      final int severity = status.getSeverity();
      if (severity == IStatus.OK)
      {
         return "OK";
      } else if (severity == IStatus.ERROR)
      {
         return "ERROR";
      } else if (severity == IStatus.WARNING)
      {
         return "WARNING";
      } else if (severity == IStatus.INFO)
      {
         return "INFO";
      } else if (severity == IStatus.CANCEL)
      {
         return "CANCEL";
      } else
      {
         return "severity=" + severity;
      }
   }
}
