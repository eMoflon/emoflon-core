package org.moflon.core.ui.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moflon.core.propertycontainer.MoflonPropertiesContainer;
import org.moflon.core.propertycontainer.MoflonPropertiesContainerHelper;
import org.moflon.core.propertycontainer.PropertycontainerFactory;
import org.moflon.core.propertycontainer.UsedCodeGen;
import org.moflon.core.ui.AbstractCommandHandler;

public class SwitchCodeGeneratorHandler extends AbstractCommandHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String currentState = event.getParameter("org.moflon.core.ui.codegenerator");
		if (currentState == null)
			logger.error("No code generator specified!");

		UsedCodeGen choosedCodeGen = UsedCodeGen.get(currentState);
		if (choosedCodeGen == null)
			logger.error(currentState + " is not a valid code generator!");

		Collection<Object> selections = extractFromSelection(event);
		for (Object o : selections) {
			if (o instanceof IJavaProject project) {
				try {
					if(project.getProject().hasNature("org.moflon.emf.build.MoflonEmfNature") || project.getProject().hasNature("org.emoflon.ibex.tgg.ide.nature")) {
						setProperties(project.getProject(), choosedCodeGen);						
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}

			if (o instanceof IFile f) {
				setProperties(f.getProject(), choosedCodeGen);
			}
		}

		// after changing codegen -> build
		return new BuildHandler().execute(event);
	}

	private void setProperties(IProject project, UsedCodeGen codeGen) {
		MoflonPropertiesContainerHelper helper = new MoflonPropertiesContainerHelper(project, new NullProgressMonitor());
		MoflonPropertiesContainer container = helper.load();
		if(container.getCodeGenerator() == null) {
			container.setCodeGenerator(PropertycontainerFactory.eINSTANCE.createCodeGenerator());
			container.getCodeGenerator().setGenerator(codeGen);
		}
		
		// if enforced is true => do not override it
		if(!container.getCodeGenerator().isEnforced()) {
			container.getCodeGenerator().setGenerator(codeGen);;			
		}
		helper.save();
	}

	/**
	 * Retrieves the list of {@link IResource}s that are selected
	 * 
	 * Currently, supported selection types are {@link IStructuredSelection} and
	 * {@link TextSelection}.
	 * 
	 * @param event the event
	 * @return the list of resources
	 * @throws ExecutionException if extracting the selection from the given event
	 *                            fails
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
				if (element instanceof IJavaProject) {
					objects.add(element);
				}
			}
		} else if (selection instanceof ITextSelection) {
			return Arrays.asList(getEditedFile(event));
		}
		return objects;
	}
}
