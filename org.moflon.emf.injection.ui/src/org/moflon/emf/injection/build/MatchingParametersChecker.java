package org.moflon.emf.injection.build;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.moflon.core.utilities.EcoreUtils;

/**
 * This component checks whether an EOperation has a given parameter sequence.
 */
public class MatchingParametersChecker {

	/**
	 * Returns whether the parameters of the given EOperation expose the given
	 * parameter types.
	 */
	public boolean haveMatchingParamters(EOperation eOperation, List<String> parameterTypes) {

		final List<EParameter> eParameters = eOperation.getEParameters();
		if (eParameters.size() != parameterTypes.size())
			return false;

		final Iterator<EParameter> eParamIterator = eParameters.iterator();
		final Iterator<String> paramTypeIterator = parameterTypes.iterator();

		boolean hasMatchingParameters = true;
		while (eParamIterator.hasNext() && hasMatchingParameters) {
			hasMatchingParameters = checkWhetherParameterMatchesType(eParamIterator.next(), paramTypeIterator.next());
		}

		return hasMatchingParameters;
	}

	/**
	 * Returns whether the given EParameter has the given type
	 * 
	 * @param eParameter
	 * @param parameterType
	 * @return
	 */
	private boolean checkWhetherParameterMatchesType(EParameter eParameter, String parameterType) {
		// Precondition: instance type name is fully qualified
		final String instanceTypeName = eParameter.getEType().getInstanceClassName();
		final String ecoreTypeName = EcoreUtils.getFQN(eParameter.getEType());

		// instanceTypeName has priority over ecoreTypeName
		final String metamodelTypeNameForComparison = (instanceTypeName != null) ? instanceTypeName : ecoreTypeName;

		final String qualifiedMetamodelTypeName = metamodelTypeNameForComparison;
		final String dequalifiedMetamodelTypeName = dequalifyClassName(metamodelTypeNameForComparison);

		final boolean hasMatchingParameters = parameterType.equals(qualifiedMetamodelTypeName)
				|| parameterType.equals(dequalifiedMetamodelTypeName);
		return hasMatchingParameters;
	}

	/**
	 * Removes the package prefix of a qualified class name.
	 * 
	 * If the class is unqualified, returns the input string
	 * 
	 * @param className
	 *            the potentially qualified class name
	 * @return the dequalified class name
	 */
	private String dequalifyClassName(final String className) {
		int indexOfLastDot = className.lastIndexOf(".");
		return className.substring(indexOfLastDot + 1);
	}
}
