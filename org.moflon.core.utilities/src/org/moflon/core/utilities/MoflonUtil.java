package org.moflon.core.utilities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * A collection of useful helper methods.
 *
 */
public class MoflonUtil {

	/**
	 * Marker for code passages generated through eMoflon/EMF that are eligible for
	 * extracting injections.
	 */
	public static final String EOPERATION_MODEL_COMMENT = "// [user code injected with eMoflon]";

	/**
	 * Code corresponding to the default implementation of a java method. Is used,
	 * when no SDM implementation could be retrieved.
	 */
	public final static String DEFAULT_METHOD_BODY = "\n" + EOPERATION_MODEL_COMMENT
			+ "\n\n// TODO: implement this method here but do not remove the injection marker \nthrow new UnsupportedOperationException();";

	private static final Logger logger = Logger.getLogger(MoflonUtil.class);

	/**
	 * Derive the java data type of a given Ecore data type.
	 *
	 * @param eCoreType
	 *            the name of the Ecore data type class (e.g. EString)
	 * @return the name of the java type class (e.g. String)
	 */
	public static String eCoreTypeToJavaType(final String eCoreType) throws IllegalArgumentException {
		String javaType = "";

		// Derive the java data type from the Ecore class name
		try {
			final EClassifier eClassifier = EcorePackage.eINSTANCE.getEClassifier(eCoreType);
			if (eClassifier != null) {
				javaType = eClassifier.getInstanceClass().getSimpleName();
			} else {
				javaType = eCoreType;
			}
		} catch (Exception e) {
			logger.debug("Cannot derive Java data type from the given Ecore data type = '" + eCoreType
					+ "'. Using Ecore type instead.");

			javaType = eCoreType;
		}

		return javaType;
	}

	/**
	 * Determine fully qualified name of given element by iterating through package
	 * hierarchy.
	 *
	 * @param ENamedElement
	 * @return
	 * @deprecated Use EcoreUtils.getFQN instead.
	 */
	public static String getFQN(final ENamedElement element) {
		return EcoreUtils.getFQN(element);
	}

	public static String handlePrefixForBooleanAttributes(final String packageName, final String attribute) {
		final String is = "is";
		final String prefix = ".is" + StringUtils.capitalize(attribute);

		switch (packageName) {
		case "uml":
			// For UML only return prefix if the attribute does not already start with an
			// "is"
			return attribute.startsWith(is) ? "." + attribute : prefix;

		default:
			return prefix;
		}
	}

	/**
	 * Returns the last segment of a fully-qualified name
	 *
	 * Example: For the name 'x.y.z.A', the last segment is 'A'
	 *
	 * @param name
	 *            the fully-qualified name
	 * @return the last segment of the name
	 */
	public static String lastSegmentOf(final String name) {
		int startOfLastSegment = name.lastIndexOf(".");

		if (startOfLastSegment == -1)
			startOfLastSegment = 0;
		else
			startOfLastSegment++;

		return name.substring(startOfLastSegment);
	}

	/**
	 * Returns the capitalized last segment of the given fully-qualified name
	 *
	 * @param name
	 *            the name
	 * @return the result of capitalizing {@link #lastSegmentOf(String)}
	 */
	public static String lastCapitalizedSegmentOf(final String name) {
		return StringUtils.capitalize(lastSegmentOf(name));
	}

	/**
	 * Returns all segments but the last one of the given fully-qualified name
	 *
	 * Example: For the name 'x.y.z.A', the result is 'x.y.z'
	 *
	 * @param name
	 *            the fully-qualified name
	 * @return all but the last segment
	 */
	public static String allSegmentsButLast(final String name) {
		int startOfLastSegment = name.lastIndexOf(".");
		return startOfLastSegment == -1 ? "" : name.substring(0, startOfLastSegment);
	}

	/**
	 * This function replaces the first matching prefix of the given package name
	 * with the corresponding value of the package name map
	 *
	 * @param fullyQualifiedPackageName
	 *            the package name to be transformed
	 * @param packageNameMap
	 *            a map from source package name prefix to target package name
	 *            prefix
	 * @return the transformed package
	 */
	public static String transformPackageNameUsingImportMapping(final String fullyQualifiedPackageName,
			final Map<String, String> packageNameMap) {
		// Break path up into all segments
		List<String> inputSegments = Arrays.asList(fullyQualifiedPackageName.split(Pattern.quote(".")));
		for (int i = inputSegments.size(); i >= 1; --i) {
			final String currentPrefix = buildQualifiedName(inputSegments.subList(0, i));
			if (packageNameMap.containsKey(currentPrefix)) {
				String suffixToKeep = buildQualifiedName(inputSegments.subList(i, inputSegments.size()));
				return packageNameMap.get(currentPrefix) + (suffixToKeep.isEmpty() ? "" : "." + suffixToKeep);
			}
		}

		// No prefix match - return input
		return fullyQualifiedPackageName;
	}

	/**
	 * Joins the given list of segments using '.', resulting in a qualified name
	 *
	 * @param segments
	 *            the segments
	 * @return the qualified name
	 */
	private static String buildQualifiedName(final List<String> segments) {
		return segments.stream().collect(Collectors.joining("."));
	}
}
