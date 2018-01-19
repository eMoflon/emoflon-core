package org.moflon.core.preferences;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * This activator manages the eMoflon preferences store
 * @author Roland Kluge - Initial implementation
 * @see #getDefault()
 * @see #getPreferencesStorage()
 */
public class EMoflonPreferencesActivator extends Plugin
{
   private static EMoflonPreferencesActivator plugin;
   private EMoflonPreferencesStorage preferencesStorage;

   @Override
   public void start(BundleContext context) throws Exception
   {
      super.start(context);
      plugin = this;
      plugin.preferencesStorage = new EMoflonPreferencesStorage();
   }

   @Override
   public void stop(BundleContext context) throws Exception
   {
      plugin = null;
      super.stop(context);
   }

   public static EMoflonPreferencesActivator getDefault()
   {
      return plugin;
   }

   /**
    * Returns the platform-independent preferences storage for the eMoflon build process
    * @return
    */
   public EMoflonPreferencesStorage getPreferencesStorage()
   {
      return this.preferencesStorage;
   }
}
