package org.moflon.core.ui.visualisation.common;

import java.util.Optional;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.moflon.core.ui.visualisation.EMoflonPlantUMLGenerator;
import org.moflon.core.utilities.ExceptionUtil;

import net.sourceforge.plantuml.eclipse.utils.DiagramTextProvider;

public abstract class EMoflonVisualiser implements DiagramTextProvider {

	private static final Logger logger = Logger.getLogger(EMoflonVisualiser.class);
	private static final int MAX_SIZE = 500;
	
	@Override
	public String getDiagramText(IEditorPart editor, ISelection selection) {
		Optional<String> diagram = Optional.empty();
		try {
			String d = getDiagramBody(editor, selection);
			if(d == null || d.split("\n").length > MAX_SIZE)
				diagram = Optional.of(EMoflonPlantUMLGenerator.toBigDiagram());
			else
				diagram = Optional.of(d);
		} catch(Exception e) {
			logger.error(e + ExceptionUtil.displayExceptionAsString(e));
			e.printStackTrace();
		}
		
		return EMoflonPlantUMLGenerator.wrapInTags(diagram.orElse(EMoflonPlantUMLGenerator.errorDiagram()));
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
