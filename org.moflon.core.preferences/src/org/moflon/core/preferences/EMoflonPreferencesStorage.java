package org.moflon.core.preferences;

/**
 * Platform-independent preferences storage for eMoflon
 * @author Roland Kluge - Initial implementation
 */
public class EMoflonPreferencesStorage
{
   public static final int REACHABILITY_MAX_ADORNMENT_SIZE_INFINITY = 0;
   
   public static final int DEFAULT_REACHABILITY_MAX_ADORNMENT_SIZE = REACHABILITY_MAX_ADORNMENT_SIZE_INFINITY;
   
   public static final int DEFAULT_VALIDATION_TIMEOUT_MILLIS = 30000;

   public static final boolean DEFAULT_REACHABILITIY_IS_ENABLED = true;
   
   /**
    * Stores the configured validation timeout in milliseconds. 'null' if not set.
    */
   private int validationTimeout;

   /**
    * Stores the configured maximum adornment size. 'null' if not set 
    */
   private int maximumAdornmentSize;

   /**
    * Stores whether reachability analysis is active. 'null' if not set 
    */
   private boolean reachabilityEnabled;


   /**
    * Sets the validation timeout in milliseconds
    * @param validationTimeout the new validation timeout
    */
   public void setValidationTimeout(final int validationTimeout)
   {
      this.validationTimeout = validationTimeout;
   }

   public int getValidationTimeout()
   {
      return this.validationTimeout;
   }
   
   /**
    * Sets the maximum size of adornments that should be analyzed using the reachability analysis
    * @param maximumAdornmentSize the maximum adornment size
    */
   public void setReachabilityMaximumAdornmentSize(final int maximumAdornmentSize)
   {
      this.maximumAdornmentSize = maximumAdornmentSize;
   }

   public int getMaximumAdornmentSize()
   {
      return this.maximumAdornmentSize;
   }
   
   /**
    * Enables or disables the reachability analysis
    * @param reachabilityEnabled true if the reachability analysis shall be enabled, false otherwise 
    */
   public void setReachabilityEnabled(final boolean reachabilityEnabled)
   {
      this.reachabilityEnabled = reachabilityEnabled;
   }
   
   public boolean getReachabilityEnabled()
   {
      return reachabilityEnabled;
   }
}
