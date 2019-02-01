## Basic steps for creating the update site
1. Delete all jars in /features and /plugins starting with org.moflon.*, as well as artifacts.jar and content.jar
    * You may use the task *clean* in the ant script *AntUtilitiesBuild.xml* for this purpose.
2. Build the update site:
    * Right-click *site.xml*, *Plug-In Tools->Build Site*
    * **IMPORTANT** It is **absolutely crucial** to reset the *site.xml* file after every build of the update site
      * This can be achieved in Eclipse via *Right-click site.xml -> Replace With -> HEAD revision*
    * When opening *site.xml*, the entries per category should
       * SHOULD look like this: "org.moflon.core.feature (2.32.0.qualifier)"
       * SHOULD NOT look like this: "features/org.moflon.core.feature_2.32.0.201702211758.jar"
    * If this rule is not obeyed to, the generated features are not moved into the appropriate categories.
3. For releasing a snapshot
    * Make sure that you have checked out the following repo as project into your workspace (this should be the case if you used our PSF file to checkout your workspace):
    https://github.com/eMoflon/emoflon-core-updatesite/
    * Invoke the launcher *.launch/ant releaseSnapshot(emoflon-core).launch*
     * It will package all relevant artifacts and extract the archive to P/emoflon-core-updatesite/snapshot/updatesite
    * Push *P/emoflon-core-updatesite*.
