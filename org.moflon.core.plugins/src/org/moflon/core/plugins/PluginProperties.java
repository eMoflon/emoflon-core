package org.moflon.core.plugins;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.moflon.core.plugins.manifest.ManifestFileUpdater;
import org.moflon.core.utilities.MoflonUtil;
import org.moflon.core.utilities.WorkspaceHelper;


/**
 * Data transfer object for properties generated together with the metamodel
 *
 * Instances of this class store the metadata of one repository project.
 */
public class PluginProperties
{

   public static final String TYPE_KEY = "type";

   public static final String REPOSITORY_PROJECT = "repository";

   public static final String INTEGRATION_PROJECT = "integration";

   public static final String MOFLON_EMF_PROJECT = "basicemf";

   public static final String NAME_KEY = "name";

   public static final String PLUGIN_ID_KEY = "pluginId";

   public static final String WORKING_SET_KEY = "workingSet";

   public static final String IS_PLUGIN_KEY = "isPlugin";

   public static final String JAVA_VERION = "javaVersion";

   public static final String DEPENDENCIES_KEY = "dependencies";

   public static final String NS_URI_KEY = "nsURI";

   public static final String EXPORT_FLAG_KEY = "exportProject";

   public static final String VALIDATED_FLAG_KEY = "isValidated";

   public static final String HAS_ATTRIBUTE_CONSTRAINTS_KEY = "hasAttributeConstraints";

   private static final Logger logger = Logger.getLogger(PluginProperties.class);

   private static final String METAMODEL_PROJECT_NAME_KEY = "metamodelProject";

   private Map<String, String> data = new HashMap<>();

   public PluginProperties()
   {
      this(new HashMap<String, String>());
   }

   public PluginProperties(final Map<String, String> data)
   {
      this.data = new HashMap<>(data);
   }

   public boolean containsKey(final String key)
   {
      return this.data.containsKey(key);
   }

   public String get(final String key)
   {
      return this.data.get(key);
   }

   public void put(final String key, final String value)
   {
      this.data.put(key, value);
   }

   /**
    * Returns the corresponding repository project (if exists).
    *
    * Never returns null, but the resulting project handle may not point to an existing project.
    *
    * @deprecated The name is misleading - actually, the returned project may also be an integration project. Use
    *             {@link #getProject()} instead
    */
   @Deprecated
   public IProject getRepositoryProject()
   {
      return getProject();
   }

   /**
    * Returns the {@link IProject} with the name returned from {@link #getProjectName()} (if exists).
    *
    * Never returns null, but the resulting project handle may not point to an existing project.
    */
   public IProject getProject()
   {
      return WorkspaceHelper.getProjectByName(this.getProjectName());
   }

   public void setMetamodelProjectName(final String metamodelProjectName)
   {
      this.put(METAMODEL_PROJECT_NAME_KEY, metamodelProjectName);
   }

   public String getMetamodelProjectName()
   {
      return this.get(METAMODEL_PROJECT_NAME_KEY);
   }

   public String getType()
   {
      return get(PluginProperties.TYPE_KEY);
   }

   public boolean isRepositoryProject()
   {
      return REPOSITORY_PROJECT.equals(this.getType());
   }

   public boolean isIntegrationProject()
   {
      return INTEGRATION_PROJECT.equals(this.getType());
   }

   public String getNsUri()
   {
      return this.get(NS_URI_KEY);
   }

   public String getProjectName()
   {
      return this.get(NAME_KEY);
   }

   public boolean isExported()
   {
      return !"false".equals(this.get(EXPORT_FLAG_KEY));
   }

   public Collection<String> getDependencies()
   {
      if (!this.data.containsKey(DEPENDENCIES_KEY))
         return null;

      return this.get(DEPENDENCIES_KEY).isEmpty() ? Collections.<String> emptyList() : Arrays.asList(this.get(DEPENDENCIES_KEY).split(","));
   }

   public Collection<URI> getDependenciesAsURIs()
   {
      return getDependencies().stream().filter(dep -> !dep.equals(ManifestFileUpdater.IGNORE_PLUGIN_ID))
            .map(dep -> MoflonUtil.getDefaultURIToEcoreFileInPlugin(dep)).collect(Collectors.toSet());
   }

   public void setDefaultValues()
   {
      if (!this.containsKey(JAVA_VERION))
         this.put(JAVA_VERION, "JavaSE-1.8");
   }

   public boolean hasAttributeConstraints()
   {
      return "true".equals(this.get(HAS_ATTRIBUTE_CONSTRAINTS_KEY));
   }

   public void setHasAttributeConstraints(final boolean hasAttributeConstraints)
   {
      this.put(HAS_ATTRIBUTE_CONSTRAINTS_KEY, Boolean.toString(hasAttributeConstraints));
   }

   /**
    * Reads the given properties and produces a mapping from project to properties.
    */
   public static Map<String, PluginProperties> createPropertiesMap(final Properties properties)
   {
      Map<String, PluginProperties> projectMap = new HashMap<>();
      for (Object key : properties.keySet())
      {
         int indexOfDelimiter = ((String) key).lastIndexOf(".");
         String projectId = ((String) key).substring(0, indexOfDelimiter);
         String property = ((String) key).substring(indexOfDelimiter + 1);
         String value = properties.getProperty((String) key);

         if (!projectMap.containsKey(projectId))
         {
            PluginProperties metamodelProperties = new PluginProperties();
            metamodelProperties.setDefaultValues();
            metamodelProperties.put(PluginProperties.PLUGIN_ID_KEY, projectId);
            metamodelProperties.put(PluginProperties.NAME_KEY, projectId);
            projectMap.put(projectId, metamodelProperties);
         }
         projectMap.get(projectId).put(property, value);
      }
      return projectMap;
   }

   /**
    * Tries to parse the given file as a properties file and produces a mapping from repository project to its
    * properties
    *
    */
   public static Map<String, PluginProperties> readEAProperties(final IFile propertyFile) throws CoreException
   {
      Properties properties = new Properties();

      try
      {
         InputStream streamToPropertiesFile = propertyFile.getContents();
         properties.load(streamToPropertiesFile);
         streamToPropertiesFile.close();
         logger.debug("Properties loaded: " + properties);
      } catch (Exception e)
      {
         logger.warn("Unable to load properties file or file not existing: " + e);

         createMarkerForMissingExportedFiles(propertyFile);
         throw new CoreException(
               new Status(IStatus.WARNING, WorkspaceHelper.getPluginId(PluginProperties.class), "Unable to load properties file or file not existing: " + e));
      }

      Map<String, PluginProperties> projectMap = createPropertiesMap(properties);

      logger.debug("Parsed project map: " + projectMap);
      return projectMap;
   }

   public static void createMarkerForMissingExportedFiles(final IFile propertyFile) throws CoreException
   {
      IMarker marker = propertyFile.getProject().createMarker(IMarker.PROBLEM);
      marker.setAttribute(IMarker.MESSAGE,
            "Cannot find any exported files to build. Please note that by convention, your active EAP file MUST have the same name as your project!");
      marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
      marker.setAttribute(IMarker.LOCATION, propertyFile.getProjectRelativePath().toString());
   }

   public void setDependencies(final List<String> dependencies)
   {
      this.put(DEPENDENCIES_KEY, dependencies.stream().collect(Collectors.joining(",")));
   }

   @Override
   public String toString()
   {
      return "MetamodelProperties [data=" + data + "]";
   }

}