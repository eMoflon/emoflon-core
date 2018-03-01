package org.moflon.core.ui.visualisation;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import net.sourceforge.plantuml.eclipse.utils.DiagramTextProvider;

public abstract class EMoflonVisualiser implements DiagramTextProvider {

	@Override
	public String getDiagramText(IEditorPart editor, ISelection selection) {
		return EMoflonPlantUMLGenerator.wrapInTags(getDiagramBody(editor, selection));
	}

	abstract protected String getDiagramBody(IEditorPart editor, ISelection selection);

	protected <T> Function<Object, Optional<T>> maybeCast(Class<T> type) {
		return (input) -> {
			if (type.isInstance(input)) {
				return Optional.of(type.cast(input));
			} else {
				return Optional.empty();
			}
		};
	}

	@Override
	public boolean supportsSelection(ISelection selection) {
		return true;
	}
}
