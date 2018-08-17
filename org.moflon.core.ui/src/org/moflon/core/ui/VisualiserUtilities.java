package org.moflon.core.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.moflon.core.ui.visualisation.common.EMoflonVisualiser;

/**
 * Utility class with the intend to support implementations of
 * {@link EMoflonVisualiser} in interacting with the Eclipse platform.
 * 
 * @author Johannes Brandt
 *
 */
public class VisualiserUtilities {

	/**
	 * <p>
	 * Checks whether or not the given editor currently has a file open with the
	 * given file extension.
	 * </p>
	 * 
	 * @param editor
	 *            whose file input shall be checked for the given file extension
	 * @param expectedExtension
	 *            that is to be compared with the extension of the currently opened
	 *            file in the editor. If <code>null</code> is given, an empty string
	 *            will be used for comparison.
	 * @return <code>true</code>: only if...
	 *         <ul>
	 *         <li>... the editor is not null</li>
	 *         <li>... the editor input is not null</li>
	 *         <li>... the editor input is of type {@link IFileEditorInput}</li>
	 *         <li>... there is a {@link IFile} opened in the editor</li>
	 *         <li>... the {@link IFile}'s extension is not null</li>
	 *         <li>... the {@link IFile}'s extension equals the given one</li>
	 *         </ul>
	 *         <code>false</code>: otherwise.
	 */
	public static boolean checkFileExtensionSupport(IEditorPart editor, String expectedExtension) {
		if (editor == null || editor.getEditorInput() == null
				|| !(editor.getEditorInput() instanceof IFileEditorInput)) {
			return false;
		}

		IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
		if (file == null || file.getFileExtension() == null) {
			return false;
		}

		final String checkedFileExtension = (expectedExtension != null) ? expectedExtension : "";
		return file.getFileExtension().equals(checkedFileExtension);
	}

	/**
	 * Retrieves all Ecore modeling elements from the given editor, if the editor
	 * handles Ecore modeling elements.
	 * 
	 * @param editor
	 *            The editor, of which all Ecore modeling elements are to be
	 *            extracted.
	 * @return <code>null</code> is returned, if the given editor is
	 *         <code>null</code> or does not implement the
	 *         {@link IEditingDomainProvider} interface, and if the
	 *         {@link EditingDomain} or the {@link ResourceSet} of the editor is
	 *         <code>null</code>. If there is a {@link ResourceSet} stored with the
	 *         editor, then all resources will be extracted and the resulting collection
	 *         of {@link EObject}s is returned. This collection can be empty, if no
	 *         {@link EObject}s are stored in the editor's resources, however, no
	 *         <code>null</code> references will be contained.
	 */
	public static Collection<EObject> extractEcoreElements(IEditorPart editor) {
		if (editor == null || !(editor instanceof IEditingDomainProvider)) {
			return null;
		}

		IEditingDomainProvider edp = (IEditingDomainProvider) editor;
		if (edp.getEditingDomain() == null || edp.getEditingDomain().getResourceSet() == null) {
			return null;
		}

		return expandResources(edp.getEditingDomain().getResourceSet());
	}

	/**
	 * Checks, if the given {@link ISelection} object contains an Ecore selection.
	 * 
	 * An Ecore selection does not contain <code>null</code> references, only
	 * {@link EObject} and {@link Resource} references.
	 * 
	 * @param selection
	 *            The selection object which is to be checked.
	 * @return <code>true</code> if the given selection only contains
	 *         {@link EObject} and {@link Resource} references. <code>false</code>
	 *         otherwise.
	 */
	public static boolean isEcoreSelection(ISelection selection) {
		if (selection == null || !(selection instanceof IStructuredSelection)) {
			return false;
		}

		List<?> internalSelection = ((IStructuredSelection) selection).toList();
		if (internalSelection == null) {
			return false;
		}

		for (Object obj : internalSelection) {
			// an Ecore selection must not contain null references
			if (obj == null) {
				return false;
			}
			// an Ecore selection must contain Resources and EObjects only
			if (!(obj instanceof EObject) && !(obj instanceof Resource)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Retrieves the Ecore selection from the given {@link ISelection}, if possible.
	 * 
	 * <p>
	 * <b>Note:</b> Calls {@link VisualiserUtilities#isEcoreSelection(ISelection)},
	 * to check whether the given selection is an Ecore selection. If not,
	 * <code>null</code> is returned.
	 * </p>
	 * 
	 * @param selection
	 *            from which all elements are to be returned, if they are not
	 *            <code>null</code>. The given selection cannot be
	 *            <code>null</code>.
	 * @return <code>null</code> is returned, if the given selection is not an Ecore
	 *         selection. Otherwise the internal selection is extracted and
	 *         returned. The returned collection can be empty, if the selection is empty.
	 *         The wrapped collection won't contain any <code>null</code> references.
	 */
	public static Collection<EObject> extractEcoreSelection(ISelection selection) {
		if (!isEcoreSelection(selection)) {
			return null;
		}

		// retrieve internal selection
		List<?> internalSelection = ((IStructuredSelection) selection).toList();

		// expand resources and add them to result
		HashSet<EObject> result = internalSelection.stream()//
				.filter(e -> e != null)//
				.filter(Resource.class::isInstance)//
				.map(Resource.class::cast)//
				.flatMap(VisualiserUtilities::expandResource)//
				.collect(Collectors.toCollection(HashSet::new));

		// add remaining EObjects of selection, apart from those already contained in
		// resources, to the result.
		internalSelection.stream()//
				.filter(e -> e != null)//
				.filter(EObject.class::isInstance)//
				.map(EObject.class::cast)//
				.filter(e -> !result.contains(e))//
				.forEach(result::add);

		return result;
	}

	/**
	 * Extracts all {@link EObject} instances from a given {@link ResourceSet}.
	 * 
	 * <p>
	 * <b>Note:</b> There won't be any <code>null</code> references in the returned
	 * collection.
	 * </p>
	 * 
	 * <p>
	 * <b>Note:</b> There won't be any {@link EGenericType}, {@link EEnumLiteral} or
	 * {@link EDataType} references in the returned collection.
	 * </p>
	 * 
	 * @param resources
	 *            of which all contained elements are to be returned - cannot be
	 *            <code>null</code>
	 * @return the {@link EObject} instances contained in each of the given
	 *         resources
	 */
	public static Collection<EObject> expandResources(ResourceSet resources) {
		return resources.getResources().stream()//
				.filter(res -> res != null)//
				.flatMap(VisualiserUtilities::expandResource)//
				.collect(Collectors.toCollection(HashSet::new));
	}

	/**
	 * Extracts all {@link EObject} instances from a given {@link Resource}.
	 * 
	 * <p>
	 * <b>Note:</b> There won't be any <code>null</code> references in the returned
	 * stream.
	 * </p>
	 * 
	 * <p>
	 * <b>Note:</b> There won't be any {@link EGenericType}, {@link EEnumLiteral} or
	 * {@link EDataType} references in the returned stream.
	 * </p>
	 * 
	 * @param resource
	 *            The resource that is to be expanded.
	 * @return The stream of {@link EObject} instances in order of expansions.
	 */
	private static Stream<EObject> expandResource(Resource resource) {
		HashSet<EObject> elements = new HashSet<>();
		resource.getAllContents().forEachRemaining(elements::add);
		return elements.stream()//
				.filter(elem -> elem != null)//
				.filter(elem -> !(elem instanceof EGenericType))//
				.filter(elem -> !(elem instanceof EEnumLiteral))//
				.filter(elem -> !(elem instanceof EDataType))//
				.filter(elem -> !(elem instanceof Map.Entry));
	}

	/**
	 * States whether the given collection of elements contains any metamodel elements.
	 * 
	 * @param elements
	 *            The collection of elements that is to be checked for the existence of
	 *            metamodel elements. Neither this collection, nor any of its elements, must be
	 *            <code>null</code>.
	 * @return <code>true</code>, if the given collection contains at least one metamodel
	 *         element. <code>false</code> otherwise.
	 */
	public static boolean hasMetamodelElements(Collection<EObject> elements) {
		return elements.stream()//
				.filter(e -> e instanceof EModelElement || e instanceof EGenericType)//
				.findAny()//
				.isPresent();
	}

	/**
	 * States whether the given collection of elements contains any model elements.
	 * 
	 * @param elements
	 *            The collection of elements that is to be checked for the existence of
	 *            model elements. Neither this collection, nor any of its elements, must be
	 *            <code>null</code>.
	 * @return <code>true</code>, if the given collection contains at least one model
	 *         element. <code>false</code> otherwise.
	 */
	public static boolean hasModelElements(Collection<EObject> elements) {
		return elements.stream()//
				.filter(e -> !(e instanceof EModelElement))//
				.filter(e -> !(e instanceof EGenericType))//
				.findAny()//
				.isPresent();
	}
}
