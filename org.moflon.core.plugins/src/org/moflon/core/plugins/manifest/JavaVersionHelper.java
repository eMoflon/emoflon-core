package org.moflon.core.plugins.manifest;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * This helper returns the Java version that was used to build its package. It
 * can be used to determine the Java version used at compile time.
 */
public class JavaVersionHelper {

	/**
	 * This method finds the Java version specified in the plug-in project
	 * containing it. It searches the MANIFEST.MF file and queries the Java version
	 * from the "Bundle-RequiredExecutionEnvironment".
	 * 
	 * @return Java version specified in the plug-in project containing this class.
	 */
	private String getJavaSpecVer() {
		// path of either this class file or the JAR containing this class file
		String helperPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

		// workaround for Windows-based systems
		if (runsOnWindows() && helperPath.startsWith("/")) {
			// removes the leading slash (that is never correct ...). thank you Java and thank you Windows!
			helperPath = helperPath.substring(1);
		}

		String javaVersion = "";
		try {
			// Running in deployed mode -> MANIFEST.MF must be read from JAR file
			if (helperPath.endsWith(".jar")) {
				final JarInputStream jarStream = new JarInputStream(new FileInputStream(helperPath));
				final Manifest mf = jarStream.getManifest();
				final Attributes attributes = mf.getMainAttributes();
				javaVersion = attributes.getValue("Bundle-RequiredExecutionEnvironment");
				jarStream.close();
			} else {
				// Running in development mode -> MANIFEST.MF can directly be read
				final String manifestPath = helperPath + "META-INF/MANIFEST.MF";
				final String manifestContent = Files.readString(Path.of(manifestPath));
				final int indexContent = manifestContent.indexOf("Bundle-RequiredExecutionEnvironment: ");
				final String line = manifestContent.substring(indexContent,
						manifestContent.indexOf(System.lineSeparator(), indexContent));
				javaVersion = line.substring(line.indexOf(":") + 2);
			}
		} catch (final Exception e) {
			// fall back solution if file could not be read
			javaVersion = "JavaSE-21";
		}

		return javaVersion;
	}

	public static String getJavaVersion() {
		return new JavaVersionHelper().getJavaSpecVer();
	}

	private boolean runsOnWindows() {
		String os = System.getProperty("os.name");
		return os.contains("win") || os.contains("Win");
	}

}
