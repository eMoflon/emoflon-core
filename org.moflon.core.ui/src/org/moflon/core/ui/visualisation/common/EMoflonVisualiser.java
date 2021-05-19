package org.moflon.core.ui.visualisation.common;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.moflon.core.ui.visualisation.EMoflonPlantUMLGenerator;
import org.moflon.core.ui.visualisation.metamodels.EMoflonMetamodelVisualiser;
import org.moflon.core.ui.visualisation.models.EMoflonModelVisualiser;
import org.moflon.core.utilities.ExceptionUtil;
import org.moflon.core.utilities.ExtensionsUtil;

import net.sourceforge.plantuml.eclipse.utils.DiagramTextProvider;

final public class EMoflonVisualiser implements DiagramTextProvider {

	private static final Logger logger = Logger.getLogger(EMoflonVisualiser.class);
	private static final int MAX_SIZE = 1000;
	
	private List<EMoflonDiagramTextProvider> textProvider = new LinkedList<>();
	
	public EMoflonVisualiser() {
		textProvider.addAll(ExtensionsUtil.collectExtensions(EMoflonDiagramTextProvider.PLUGIN_ID, "class", EMoflonDiagramTextProvider.class));
		textProvider.add(new EMoflonModelVisualiser());
		textProvider.add(new EMoflonMetamodelVisualiser());
	}
	
	@Override
	public String getDiagramText(IEditorPart editor, ISelection selection) {
		Optional<String> diagram = Optional.empty();
		EMoflonDiagramTextProvider provider = selectProvider(editor, selection);
		
		if(provider == null)
			return EMoflonPlantUMLGenerator.wrapInTags(EMoflonPlantUMLGenerator.errorDiagram());
		
		try {
			String d = provider.getDiagramBody(editor, selection);
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
	
	public EMoflonDiagramTextProvider selectProvider(IEditorPart editor, ISelection selection) {
		for(EMoflonDiagramTextProvider provider : textProvider) {
			if(provider.supportsEditor(editor) && provider.supportsSelection(selection))
				return provider;
		}
		return null;
	}

	@Override
	public boolean supportsSelection(ISelection selection) {
		for(EMoflonDiagramTextProvider provider : textProvider) {
			if(provider.supportsSelection(selection))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean supportsEditor(IEditorPart editor) {
		for(EMoflonDiagramTextProvider provider : textProvider) {
			if(provider.supportsEditor(editor))
				return true;
		}
		return false;
	}
}
