package org.moflon.core.utilities;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;

public class EAInterfaceUriHelper {

	public final static String DELIM = "/";

	public static String getObjVarString(String objectVariableName) {
		return (objectVariableName + ":ObjectVariable");
	}

	public static String getLinkVarString(String targetName, String sourceName) {
		return ("source:" + targetName + "_target:" + sourceName + ":LinkVariable");
	}

	public static String getStoryNodeString(String storyNodeName) {
		return (storyNodeName + ":StoryNode");
	}

	public static String getStopNodeString(String stopNodeName) {
		return (stopNodeName + ":StopNode");
	}

	public static String getStartNodeString(String startNodeName) {
		return (startNodeName + ":StartNode");
	}

	public static String getActivityEdgeString(String activityEdgeSourceName, String activityEdgeTargetName) {
		return ("source:" + activityEdgeSourceName + "_target:" + activityEdgeTargetName + ":ActivityEdge");
	}

	public static String getActivityString() {
		return "Activity:Activity";
	}

	public static final String getEOperationString(EOperation eOperation) {
		final StringBuilder builder = new StringBuilder();
		builder.append(getEClassString(eOperation.getEContainingClass()));
		builder.append(DELIM);
		builder.append(getEOperationString(eOperation.getName()));
		builder.append(getEParameterString(eOperation.getEParameters()));
		builder.append(":");
		builder.append("EOperation");
		return builder.toString();
	}

	public static final String getEOperationString(String eOperationName) {
		return (eOperationName);
	}

	public static final String getEParameterString(EList<EParameter> eParameters) {
		final StringBuilder builder = new StringBuilder();
		builder.append("(");
		if (null != eParameters) {
			for (int i = 0; i < eParameters.size(); i++) {
				EParameter eParam = eParameters.get(i);
				if (i > 0) {
					builder.append(",");
				}
				builder.append(eParam.getName());
				builder.append(":");
				builder.append(eParam.getEType().getName());
			}
		}
		builder.append(")");
		return builder.toString();
	}

	public static final String getEClassString(EClass eClass) {
		final StringBuilder builder = new StringBuilder();
		builder.append(getEPackageString(eClass.getEPackage()));
		builder.append(DELIM);
		builder.append(getEClassString(eClass.getName()));
		return builder.toString();
	}

	public static final String getEClassString(String eClassName) {
		return (eClassName + ":EClass");
	}

	public static final String getEPackageString(EPackage ePackage) {
		EPackage eParentPackage = ePackage.getESuperPackage();
		if (eParentPackage != null) {
			final StringBuilder builder = new StringBuilder();
			builder.append(getEPackageString(eParentPackage));
			builder.append(DELIM);
			builder.append(getEPackageString(ePackage.getName()));
			return builder.toString();
		} else {
			return getEPackageString(ePackage.getName());
		}
	}

	public static final String getEPackageString(String ePackageName) {
		return (ePackageName + ":EPackage");
	}
}
