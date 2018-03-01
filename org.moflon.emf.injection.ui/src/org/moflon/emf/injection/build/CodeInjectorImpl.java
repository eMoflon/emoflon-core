package org.moflon.emf.injection.build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.moflon.emf.injection.ide.CodeInjector;
import org.moflon.emf.injection.unparsing.InjectionConstants;

/**
 * This class is responsible for dirty injection of members and imports code.
 * 
 */
public class CodeInjectorImpl implements CodeInjector {
	private final String projectPath;

	public CodeInjectorImpl(final String projectPath) {
		this.projectPath = projectPath;
	}

	@Override
	public void injectMembersCode(final String relativePath, final String code) {
		final String filePath = toAbsolutePath(relativePath);
		final java.io.File file = new java.io.File(filePath);
		injectCodeToFile(file, code);
	}

	@Override
	public void injectImports(final String relativePath, final List<String> imports) {
		final String filePath = toAbsolutePath(relativePath);
		final java.io.File file = new java.io.File(filePath);
		injectImportsToFile(file, imports);
	}

	/**
	 * Converts a relative path to an absolute path
	 */
	private String toAbsolutePath(final String relativePath) {
		final StringBuilder sb = new StringBuilder();
		sb.append(projectPath);
		sb.append(relativePath);
		return sb.toString();
	}

	/**
	 * Injects the given code to given File. <br>
	 * Old injected code will be overwritten.
	 * 
	 * @param file
	 *            File to inject the code in
	 * @param code
	 *            Code to inject
	 */
	private void injectCodeToFile(final java.io.File file, final String code) {
		final String newMembersCodeBlock = buildMembersBlock(code);

		try {
			// Read file
			final List<String> fileContent = readFileToLines(file);

			replaceMembersCodeBlock(newMembersCodeBlock, fileContent);

			// Write file
			writeVectorToFile(fileContent, file);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void replaceMembersCodeBlock(final String newMembersCodeBlock, final List<String> fileContent) {
		deleteOldMembersCodeBlock(fileContent);
		appendCodeBlock(newMembersCodeBlock, fileContent);
	}

	/**
	 * Deletes the old members code block from given file content.
	 */
	private void deleteOldMembersCodeBlock(final List<String> fileContent) {
		boolean oldInjection = false;
		for (int i = 0; i < fileContent.size(); i++) {
			final String line = fileContent.get(i).trim();

			// Check if we are currently in an old injection
			if (oldInjection) {

				if (line.startsWith(InjectionConstants.MEMBERS_END))
					oldInjection = false;

				// Delete old injection content and border
				fileContent.remove(i);
				i--;
			} else {
				if (line.startsWith(InjectionConstants.MEMBERS_BEGIN)) {
					oldInjection = true;
					fileContent.remove(i);
					i--;
				}
			}
		}
	}

	private void appendCodeBlock(final String newMembersCodeBlock, final List<String> fileContent) {
		fileContent.add(fileContent.size() - 1, newMembersCodeBlock);
	}

	/**
	 * Writes an Vector of Strings into a file
	 */
	private void writeVectorToFile(final List<String> fileContent, final File file) throws IOException {
		final BufferedWriter out = new BufferedWriter(new FileWriter(file));
		for (final String content : fileContent)
			out.write(content + "\n");
		out.close();
	}

	/**
	 * Builds a members block that is ready to be injected. It gets surrounded by
	 * whitespace and the comments to mark the block.
	 */
	private String buildMembersBlock(final String code) {
		final StringBuffer block = new StringBuffer();
		block.append("\t");
		block.append(InjectionConstants.MEMBERS_BEGIN);
		block.append("\n\t");
		block.append(code);
		block.append("\n");
		block.append("\t");
		block.append(InjectionConstants.MEMBERS_END);
		return block.toString();
	}

	/**
	 * Reads a file into a Vector of Strings. Each line becomes a new entry in the
	 * Vector.
	 */
	private List<String> readFileToLines(final File file) throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(file));
		final List<String> fileContent = new ArrayList<String>();
		String tmp;
		while ((tmp = reader.readLine()) != null)
			fileContent.add(tmp);
		reader.close();

		return fileContent;
	}

	/**
	 * Injects the given imports into the given File. <br>
	 * Old injected imports will be overwritten.
	 * 
	 * @param file
	 *            File to write in
	 * @param imports
	 *            Imports to inject
	 */
	private void injectImportsToFile(final File file, final List<String> imports) {
		final String toInsert = buildImportsBlock(imports);

		try {
			// Read file
			final List<String> fileContent = readFileToLines(file);

			// Edit content
			boolean oldInjection = false;
			boolean done = false;
			int importSpot = 0;
			for (int i = 0; i < fileContent.size(); i++) {
				final String line = fileContent.get(i).trim();
				switch (identifyLine(line)) {
				case PACKAGE:
					// save the index behind the package. If there is no import line,
					// the imports will be injected there
					importSpot = i + 1;
					break;
				case IMPORT:
					if (oldInjection) {
						// delete old injected imports
						fileContent.remove(i);
						i--;
					} else
						// Save the index behind this import
						// In the end, we will have the index behind
						// the last import
						importSpot = i + 1;
					break;
				case IMPORT_BEGIN:
				case IMPORT_END:
					// delete old injection
					fileContent.remove(i);
					i--;
					// entered/left old injected block
					oldInjection = !oldInjection;
					break;
				case CLASS_BEGIN:
					// we are behind the imports, inject imports and stop searching
					fileContent.add(importSpot, toInsert);
					done = true;
					break;
				default:
				}
				if (done)
					break;
			}

			// Write file
			writeVectorToFile(fileContent, file);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builds an imports block that is ready to be injected. This block contains
	 * whitespace and the marks to identify the block later on.
	 */
	private static String buildImportsBlock(final List<String> qualifiedImports) {
		final StringBuffer block = new StringBuffer();
		block.append(InjectionConstants.NL);
		block.append(InjectionConstants.USER_IMPORTS_BEGIN);
		block.append(InjectionConstants.NL);
		for (final String importExpression : qualifiedImports) {
			block.append(InjectionConstants.IMPORT_KEYWORD);
			block.append(InjectionConstants.SPACE);
			block.append(importExpression);
			block.append(";");
			block.append(InjectionConstants.NL);
		}
		block.append(InjectionConstants.NL).append(InjectionConstants.USER_IMPORTS_END);
		return block.toString();
	}

	/**
	 * Identifies which kind of line is given by looking at the first words.
	 */
	private LineBegin identifyLine(final String line) {
		if (line.startsWith("package"))
			return LineBegin.PACKAGE;
		if (line.startsWith("import"))
			return LineBegin.IMPORT;
		if (line.startsWith(InjectionConstants.USER_IMPORTS_BEGIN))
			return LineBegin.IMPORT_BEGIN;
		if (line.startsWith(InjectionConstants.USER_IMPORTS_END))
			return LineBegin.IMPORT_END;
		if (line.startsWith("public class") || line.startsWith("public abstract class")
				|| line.startsWith("public interface"))
			return LineBegin.CLASS_BEGIN;

		return LineBegin.IGNORE;
	}

	/**
	 * Used to parse the beginning of a file in order to inject imports.
	 */
	public enum LineBegin {
		IMPORT_BEGIN, IMPORT_END, CLASS_BEGIN, IMPORT, PACKAGE, IGNORE
	}
}
