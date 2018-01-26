/*
 * Copyright (c) 2010-2012 Gergely Varro
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gergely Varro - Initial API and implementation
 */
package org.moflon.emf.codegen.dependency;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl;

class SDMEnhancedResourceHelper extends XMIHelperImpl {

	SDMEnhancedResourceHelper(XMLResource resource) {
		super(resource);
	}

	@Override
	protected URI getHREF(Resource otherResource, EObject obj) {
		String fragment = otherResource.getURIFragment(obj);
		EObject current = obj;
		while (current.eContainer() != null) {
			current = current.eContainer();
		}
		if (current instanceof EPackage) {
			EPackage ePackageInFile = (EPackage) current;
			return URI.createURI(ePackageInFile.getNsURI()).appendFragment(fragment);
		} else {
			return otherResource.getURI().appendFragment(fragment);
		}
	}

	public URI deresolve(URI uri) {
		return uri;
	}
}
