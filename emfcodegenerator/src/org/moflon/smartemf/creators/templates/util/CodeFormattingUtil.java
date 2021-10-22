package org.moflon.smartemf.creators.templates.util;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class CodeFormattingUtil {

	public static String format(String code) {
		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);
		TextEdit textEdit = codeFormatter.format(CodeFormatter.K_UNKNOWN, code, 0, code.length(), 0, null);
		if(textEdit == null)
			return code;
		
		IDocument doc = new Document(code);
		try {
			textEdit.apply(doc);
		} catch (MalformedTreeException | BadLocationException e) {
			return code;
		}
		return doc.get();
	}

}
