package org.moflon.core.plugins;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.moflon.core.plugins.manifest.ManifestFileUpdater;
import org.moflon.core.utilities.MoflonConventions;

/**
 * Data transfer object for properties generated together with the metamodel
 *
 * Instances of this class store the metadata of one generic eMoflon project.
 */
public class PluginProperties
{
   public static final String TYPE_KEY = "type";

   public static final String NAME_KEY = "name";

   public static final String PLUGIN_ID_KEY = "pluginId";

   public static final String WORKING_SET_KEY = "workingSet";

   public static final String DEPENDENCIES_KEY = "dependencies";

   public static final String NS_URI_KEY = "nsURI";

   /**
    * Inside MANIFEST.MF, dependencies are separated using this token
    */
   private static final String DEPENDENCIES_SEPARATOR = ",";
   
   private Map<String, String> data = new HashMap<>();

   public PluginProperties()
   {
      this(new HashMap<String, String>());
   }

   private PluginProperties(final Map<String, String> data)
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

   public String getType()
   {
      return get(PluginProperties.TYPE_KEY);
   }

   public String getNsUri()
   {
      return this.get(NS_URI_KEY);
   }

   public String getProjectName()
   {
      return this.get(NAME_KEY);
   }

   public Collection<String> getDependencies()
   {
      if (!this.data.containsKey(DEPENDENCIES_KEY))
         return null;

      final String dependenciesString = this.get(DEPENDENCIES_KEY);
      return dependenciesString.isEmpty() ? Collections.<String> emptyList() : Arrays.asList(dependenciesString.split(DEPENDENCIES_SEPARATOR));
   }

   public Collection<URI> getDependenciesAsURIs()
   {
      return getDependencies().stream()//
            .filter(dep -> !dep.equals(ManifestFileUpdater.IGNORE_PLUGIN_ID)) //
            .map(dep -> MoflonConventions.getDefaultResourceDependencyUri(dep)).collect(Collectors.toSet());
   }

   public void setDependencies(final List<String> dependencies)
   {
      this.put(DEPENDENCIES_KEY, dependencies.stream().collect(Collectors.joining(DEPENDENCIES_SEPARATOR)));
   }

   @Deprecated // Since 2017-03-13
   public void setDefaultValues()
   {
      // empty
   }

   @Override
   public String toString()
   {
      return "MetamodelProperties [data=" + data + "]";
   }

}