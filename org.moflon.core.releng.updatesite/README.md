## Basic steps for creating the update site
1. Delete all jars in /features and /plugins starting with org.moflon.*, as well as artifacts.jar and content.jar
2. Build the update site:
  * Right-click *site.xml*, *Plug-In Tools->Build Site*
  * **IMPORTANT** It is **absolutely crucial** to reset the *site.xml* file after every build of the update site
    * When opening *site.xml*, the entries per category should
       * SHOULD look like this: "org.moflon.core.feature (2.32.0.qualifier)"
       * SHOULD NOT look like this: "features/org.moflon.core.feature_2.32.0.201702211758.jar"
    * If this rule is not obeyed to, the generated features are not moved into the appropriate categories.
3. (optional) Sign the created JAR files
  * For this, you need *org.moflon.releng.signing* from https://github.com/eMoflon/emoflon-releng.
  * You will need the eMoflon keystore for this step.
4. Push all changes to deploy.