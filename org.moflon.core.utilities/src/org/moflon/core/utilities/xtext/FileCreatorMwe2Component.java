package org.moflon.core.utilities.xtext;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.emf.mwe.core.WorkflowContext;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.eclipse.emf.mwe.core.lib.AbstractWorkflowComponent2;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor;

/**
 * This workflow component creates a file with the at the given path
 * ({@link #setFilePath(String)} with the given content
 * {@link #setFileContent(String)}
 * 
 * Usage example within an .mwe2 file: component = FileCreatorMwe2Component {
 * filePath = "${rootPath}/${projectName}/src-gen/.keepsrcgen" fileContent =
 * "Dummy file to protect empty folder in Git.\n" }
 * 
 * @author Roland Kluge - Initial implementation
 *
 */
public class FileCreatorMwe2Component extends AbstractWorkflowComponent2 {
	private String filePath;

	private String fileContent;

	/**
	 * The absolute or relative path of the file to create
	 * 
	 * Relative paths are relative to the surrounding project of the .mwe2 file
	 */
	public void setFilePath(final String filePath) {
		this.filePath = filePath;

	}

	/**
	 * The content of the file to be created
	 * 
	 * @param fileContent
	 */
	public void setFileContent(final String fileContent) {
		this.fileContent = fileContent;
	}

	/**
	 * Creates a file at the configured path ({@link #setFilePath(String)}) using
	 * the configured content {@link #setFileContent(String)}
	 */
	@Override
	protected void invokeInternal(final WorkflowContext context, final ProgressMonitor monitor, final Issues issues) {
		final File file = new File(this.filePath);
		PrintStream printStream = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			printStream = new PrintStream(file);
			printStream.append(this.fileContent);
		} catch (final IOException e) {
			issues.addError(this,
					String.format("Failed to creating file '%s'. Reason: '%s'", this.filePath, e.getMessage()));
		} finally {
	        if (printStream != null) {
	            printStream.close();
	        }
		}
	}

}
