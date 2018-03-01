package org.moflon.emf.codegen.dependency;

import org.eclipse.emf.common.util.URI;
import org.moflon.core.utilities.eMoflonEMFUtil;

public class DependencyTypes {

	public static final int getDependencyType(final URI namespaceURI) {
		if (namespaceURI.isPlatformResource() && namespaceURI.segmentCount() >= 2) {
			return DependencyTypes.WORKSPACE_PROJECT;
		}
		if (namespaceURI.isPlatformPlugin() && namespaceURI.segmentCount() >= 2) {
			return eMoflonEMFUtil.getWorkspaceProject(namespaceURI) != null ? DependencyTypes.WORKSPACE_PLUGIN_PROJECT
					: DependencyTypes.DEPLOYED_PLUGIN;
		}
		return DependencyTypes.UNKNOWN;
	}

	public static final int UNKNOWN = 0;
	public static final int DEPLOYED_PLUGIN = 1;
	public static final int WORKSPACE_PLUGIN_PROJECT = 2;
	public static final int WORKSPACE_PROJECT = 3;
	public static final int DEPENDENCY_TYPE_COUNT = 3;

}
