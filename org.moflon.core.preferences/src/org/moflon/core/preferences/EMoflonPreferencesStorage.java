package org.moflon.core.preferences;

/**
 * Platform-independent preferences storage for eMoflon
 * 
 * @author Roland Kluge - Initial implementation
 */
public class EMoflonPreferencesStorage {
   
   /**
    * Indicates unbounded adornment size for {@link #getMaximumAdornmentSize()} 
    */
	public static final int REACHABILITY_MAX_ADORNMENT_SIZE_INFINITY = 0;

	/**
	 * Default value for {@link #getMaximumAdornmentSize()}
	 */
	public static final int DEFAULT_REACHABILITY_MAX_ADORNMENT_SIZE = REACHABILITY_MAX_ADORNMENT_SIZE_INFINITY;

	/**
	 * Default value for {@link #getValidationTimeout()}
	 */
	public static final int DEFAULT_VALIDATION_TIMEOUT_MILLIS = 30000;

	/**
	 * Default value for {@link #getReachabilityEnabled()}
	 */
	public static final boolean DEFAULT_REACHABILITIY_IS_ENABLED = true;
	
	/**
	 * Default value for {@link #getPreferredGenModelPlatformUriType()}
	 */
	public static final PlatformUriType DEFAULT_PLATFORM_URI_TYPE = PlatformUriType.PLUGIN;

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
	 * Stores the preferred {@link PlatformUriType}
	 */
   private PlatformUriType preferredPlatformUriType = DEFAULT_PLATFORM_URI_TYPE;

	/**
	 * Sets the validation timeout in milliseconds
	 * 
	 * @param validationTimeout
	 *            the new validation timeout
	 */
	public void setValidationTimeout(final int validationTimeout) {
		this.validationTimeout = validationTimeout;
	}

	/**
	 * Returns the timeout for the reachability validation (in milliseconds)
	 * @return the validation timeout
	 */
	public int getValidationTimeout() {
		return this.validationTimeout;
	}

	/**
	 * Sets the maximum size of adornments that should be analyzed using the
	 * reachability analysis
	 * 
	 * @param maximumAdornmentSize
	 *            the maximum adornment size
	 */
	public void setReachabilityMaximumAdornmentSize(final int maximumAdornmentSize) {
		this.maximumAdornmentSize = maximumAdornmentSize;
	}

	/**
	 * @return the maximum size of adornments to analyze using reachability analysis
	 * @see #setReachabilityMaximumAdornmentSize(int)
	 */
	public int getMaximumAdornmentSize() {
		return this.maximumAdornmentSize;
	}

	/**
	 * Enables or disables the reachability analysis
	 * 
	 * @param reachabilityEnabled
	 *            true if the reachability analysis shall be enabled, false
	 *            otherwise
	 */
	public void setReachabilityEnabled(final boolean reachabilityEnabled) {
		this.reachabilityEnabled = reachabilityEnabled;
	}

	/**
	 * @return  true if the reachability analysis shall be enabled, false
    *            otherwise
	 * @see #setReachabilityEnabled(boolean)
	 */
	public boolean getReachabilityEnabled() {
		return reachabilityEnabled;
	}
	
	/**
	 * Configures the preferred {@link PlatformUriType}
	 * @param preferredPlatformUriType the preferred {@link PlatformUriType}. Must not be <code>null</code>.
	 */
	public void setPreferredPlatformUriType(final PlatformUriType preferredPlatformUriType)
   {
	   if (preferredPlatformUriType == null)
	      throw new IllegalArgumentException("Only non-null values allowed for preferred platform URI type.");
	   
      this.preferredPlatformUriType = preferredPlatformUriType;
   }

	/**
	 * Returns the {@link PlatformUriType} that shall be used by the build process to represent resources in workspace projects.
	 * @return the preferred URI type
	 */
   public PlatformUriType getPreferredGenModelPlatformUriType()
   {
      return this.preferredPlatformUriType;
   }
}
