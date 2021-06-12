package org.moflon.emf.codegen.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
import org.moflon.core.propertycontainer.MoflonPropertiesContainerHelper;
import org.moflon.core.propertycontainer.UsedCodeGen;
import org.moflon.core.ui.AbstractCommandHandler;
import org.moflon.emf.codegen.CodeGenerator;
import org.moflon.smartemf.EMFCodeGenerator;

public class SwitchCodeGeneratorHandler extends AbstractCommandHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Collection<Object> selections = extractFromSelection(event);
		UsedCodeGen usedCodeGen = null;
		for(Object o : selections) {
			if(o instanceof IJavaProject) {
				IJavaProject project = (IJavaProject) o;
				usedCodeGen = alterProperties(project.getProject(), usedCodeGen);
			}
			
			if(o instanceof IFile) {
				IFile f = (IFile) o;
				usedCodeGen = alterProperties(f.getProject(), usedCodeGen);
			}
		}
		
		return AbstractCommandHandler.DEFAULT_HANDLER_RESULT;
	}

	
	private UsedCodeGen alterProperties(IProject project, UsedCodeGen codeGen) {
		UsedCodeGen usedCodeGen = codeGen;
		MoflonPropertiesContainerHelper helper = new MoflonPropertiesContainerHelper(project, new NullProgressMonitor());
		MoflonPropertiesContainer container = helper.load();
		
		if(codeGen == null) {
			UsedCodeGen currentCodeGen = container.getUsedCodeGen();
			usedCodeGen = UsedCodeGen.get((currentCodeGen.getValue() + 1) % UsedCodeGen.VALUES.size());
		}
		
		container.setUsedCodeGen(usedCodeGen);
		helper.save();
		
		return usedCodeGen;
	}
	
	/**
	 * Retrieves the list of {@link IResource}s that are selected
	 * 
	 * Currently, supported selection types are {@link IStructuredSelection} and {@link TextSelection}.
	 * 
	 * @param event
	 *            the event
	 * @return the list of resources
	 * @throws ExecutionException if extracting the selection from the given event fails 
	 */
	private Collection<Object> extractFromSelection(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		final Collection<Object> objects = new ArrayList<>();
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (final Iterator<?> selectionIterator = structuredSelection.iterator(); selectionIterator.hasNext();) {
				final Object element = selectionIterator.next();
				if (element instanceof IResource) {
					final IResource resource = (IResource) element;
					objects.add(resource);
				}
				if(element instanceof IJavaProject) {
					objects.add(element);
				}
			}
		} else if (selection instanceof ITextSelection) {
			return Arrays.asList(getEditedFile(event));
		}
		return objects;
	}
}
