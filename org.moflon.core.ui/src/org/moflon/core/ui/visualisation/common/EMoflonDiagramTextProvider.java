package org.moflon.core.ui.visualisation.common;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

public interface EMoflonDiagramTextProvider {
	
	final public static String PLUGIN_ID = "org.moflon.core.ui.DiagramTextProvider";

	public String getDiagramBody(IEditorPart editorPart, ISelection selection);

	public boolean supportsSelection(ISelection selection);
	
	public boolean supportsEditor(IEditorPart editor);
	
	public default <T> Function<Object, Optional<T>> maybeCast(Class<T> type) {
		return (input) -> {
			if (type.isInstance(input)) {
				return Optional.of(type.cast(input));
			} else {
				return Optional.empty();
			}
		};
	}
	
}
