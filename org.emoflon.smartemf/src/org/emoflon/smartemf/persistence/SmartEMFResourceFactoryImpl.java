package org.emoflon.smartemf.persistence;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

public class SmartEMFResourceFactoryImpl implements Resource.Factory {

	private final String workspacePath;
	/**
	 * Constructor for XtendXMIResourceFactoryImpl
	 */
	public SmartEMFResourceFactoryImpl(final String workspacePath) {
		super();
		this.workspacePath = workspacePath;
	}

	@Override
	public Resource createResource(URI uri) {
		return new SmartEMFResource(uri, workspacePath);
	}
}
