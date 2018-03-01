package org.moflon.emf.injection.ide;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EOperation;

/**
 * Instances of this interface are responsible for extracting and storing
 * information from the .inject files.
 */
public interface InjectionExtractor {
	/**
	 * Runs the injection extractor
	 */
	IStatus extractInjections();

	/**
	 * Use this function to iterate over the imports via getImports.
	 * 
	 * @return A Set of relative paths to classes that have imports saved here.
	 */
	Collection<String> getImportsPaths();

	/**
	 * Retrieves the classes imported by the class with the given
	 * fullyQualifiedName. These are just the raw imports without the keyword
	 * 'import' or semicolons.
	 * 
	 * @param fullyQualifiedName
	 *            Fully qualified name of the class into which imported classes are
	 *            injected.
	 * @return list of classes imported by a class
	 */
	List<String> getImports(String fullyQualifiedName);

	/**
	 * Returns the joint list of all imports known to this injection extractor.
	 * 
	 * @return
	 */
	List<String> getAllImports();

	/**
	 * Returns whether this component holds model code for the given operation.
	 */
	boolean hasModelCode(EOperation eOperation);

	/**
	 * Get the model code for given method.
	 * 
	 * @param eOperation
	 *            The EOperation that identifies the method
	 * @return Code for the method
	 */
	String getModelCode(EOperation eOperation);

	/**
	 * Use this function to iterate over the imports via getMembersCode.
	 * 
	 * @return A Set of relative paths to classes that have members code saved here.
	 */
	Set<String> getMembersPaths();

	/**
	 * Retrieves the members code for given file.
	 * 
	 * @param fullyQualifiedName
	 *            Fully qualified name of the class into which the members code is
	 *            injected.<br>
	 *            Example: "DoubleLinkedList.DoubleLinkedList"
	 * @return Members code
	 */
	String getMembersCode(String fullyQualifiedName);

	/**
	 * Returns the members code corresponding to the given class.
	 */
	String getMembersCodeByClassName(String className);
}