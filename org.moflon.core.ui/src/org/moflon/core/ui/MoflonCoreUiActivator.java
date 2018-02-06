package org.moflon.core.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.moflon.core.utilities.LogUtils;
import org.moflon.core.utilities.WorkspaceHelper;
import org.osgi.framework.BundleContext;

public class MoflonCoreUiActivator extends AbstractUIPlugin
{
   private static Logger logger = Logger.getLogger(MoflonCoreUiActivator.class);

   // The shared instance
   private static MoflonCoreUiActivator plugin;

   // Log4J file
   private static final String LOG4J_CONFIG_PROPERTIES = "log4jConfig.properties";

   // Default plugin-relative resources path
   private static final String RESOURCES_DEFAULT_FILES_PATH = "resources/defaultFiles/";

   // The config file used for logging in plugin
   private File loggingConfigurationFile;

   @Override
   public void start(final BundleContext context) throws Exception
   {
      super.start(context);
      plugin = this;

      setUpLogging();
   }

   @Override
   public void stop(final BundleContext context) throws Exception
   {
      plugin = null;
      super.stop(context);
   }

   /**
    * Returns the singleton of this class.
    *
    * The {@link #start(BundleContext)} method has to be called prior to invoking this method
    *
    * @return
    */
   public static MoflonCoreUiActivator getDefault() {
      if (plugin == null)
         throw new IllegalStateException(String.format("Singleton of class %s not initialized yet.", MoflonCoreUiActivator.class.getName()));

      return plugin;
   }

   /**
   * Initialize log and configuration file. Configuration file is created with default contents if necessary. Log4J is
   * setup properly and configured with a console and logfile appender.
   */
   private void setUpLogging()
   {
      // Create configFile if necessary also in plugin storage space
      loggingConfigurationFile = getPathInStateLocation(LOG4J_CONFIG_PROPERTIES).toFile();

      if (!loggingConfigurationFile.exists())
      {
         try
         {
            // Copy default configuration to state location
            URL defaultConfigFile = WorkspaceHelper.getPathRelToPlugIn(RESOURCES_DEFAULT_FILES_PATH + LOG4J_CONFIG_PROPERTIES,
                  WorkspaceHelper.getPluginId(getClass()));

            FileUtils.copyURLToFile(defaultConfigFile, loggingConfigurationFile);
         } catch (Exception e)
         {
            LogUtils.error(logger, e, "Unable to open default config file.");
         }
      }

      // Configure Log4J
      reconfigureLogging();
   }

   /**
    * Call to ensure that log4j is setup properly and configured. Can be called multiple times. Forces Plugin to be
    * loaded and started, ensuring that log file and config file exist.
    */
   public void reconfigureLogging()
   {
      try
      {
         configureLogging(loggingConfigurationFile.toURI().toURL());
      } catch (MalformedURLException e)
      {
         LogUtils.error(logger, e, "URL to configFile is malformed: " + loggingConfigurationFile);
      }
   }

   /**
    * Set up logging globally
    *
    * @param configFile
    *           URL to log4j property configuration file
    */
   public static boolean configureLogging(final URL configFile)
   {
      try
      {
         Logger root = Logger.getRootLogger();
         String configurationStatus = "";
         if (configFile != null)
         {
            // Configure system using config
            PropertyConfigurator.configure(configFile);
            configurationStatus = "Log4j successfully configured using " + configFile;
         } else
         {
            configurationStatus = "Set up logging without config file!";
         }

         // Set format and scheme for output
         Logger.getRootLogger().addAppender(new MoflonConsole(configFile));

         // Indicate success
         root.info("Logging to eMoflon console. Configuration: " + configurationStatus);
         return true;
      } catch (Exception e)
      {
         LogUtils.error(logger, e);
         return false;
      }
   }

   /**
    * @return Logging configuration file in state location of client (usually
    *         $workspace/.metadata/.plugins/org.moflon.ide.core/log4jConfig.properties)
    */
   public File getConfigFile()
   {
      return loggingConfigurationFile;
   }

   /**
    * Used when the plugin has to store resources on the client machine and eclipse installation + current workspace.
    * This location reserved for the plugin is called the "state location" and is usually in
    * pathToWorkspace/.metadata/pluginName
    *
    * @param filename
    *           Appended to the state location. This is the name of the resource to be saved.
    * @return path to location reserved for the plugin which can be used to store resources
    */
   private IPath getPathInStateLocation(final String filename)
   {
      return getStateLocation().append(filename);
   }
}
