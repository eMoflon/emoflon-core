package org.moflon.emf.injection.build;

import java.io.File;
import java.util.List;

/**
 * Converts a class name to a file path. The converter prepends a given source
 * folder (e.g., 'gen', 'src') to the generated relative path.
 */
public class ClassNameToPathConverter {
	private String sourceFolder;
	private char fileSeparator;

	/**
	 * Creates a converter with the system file separator
	 * (java.io.File.fileSeparator) and the given source root folder ('src', 'gen',
	 * ...).
	 * 
	 * @param sourceFolder
	 */
	public ClassNameToPathConverter(final String sourceFolder) {
		this(sourceFolder, File.separatorChar);
	}

	/**
	 * Creates a converter with the provided file separator and the given source
	 * root folder ('src', 'gen', ...).
	 * 
	 * @param sourceFolder
	 * @param fileSeparator
	 */
	public ClassNameToPathConverter(final String sourceFolder, final char fileSeparator) {
		this.sourceFolder = sourceFolder;
		this.fileSeparator = fileSeparator;
	}

	/**
	 * Produces a project-relative path from the given qualified class name.
	 *
	 * For instance, the class name "foo.bar.Baz" is converted to
	 * "src/foo/bar/Baz.java" if the file separator is '/' and the source folder is
	 * 'src'.
	 *
	 * @param className
	 *            the class name
	 */
	public String toPath(final String className) {
		final String relativePath = className.replace('.', this.fileSeparator) + ".java";
		final String projectRelativePath = this.fileSeparator + this.sourceFolder + this.fileSeparator + relativePath;
		return projectRelativePath;
	}

	/**
	 * Build the file path for dirty injection.
	 *
	 * @param folders
	 *            Containing folders.
	 * @param className
	 *            Name of the class.
	 * @return Complete String representing the path to the .java file
	 */
	public String buildPathToJavaFile(List<String> folders, String className) {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.fileSeparator);
		sb.append(this.sourceFolder);
		for (int i = 0; i < folders.size(); i++) {
			sb.append(this.fileSeparator);
			sb.append(folders.get(i));
		}
		sb.append(this.fileSeparator);
		sb.append(className);
		sb.append(".java");
		return sb.toString();
	}

}
