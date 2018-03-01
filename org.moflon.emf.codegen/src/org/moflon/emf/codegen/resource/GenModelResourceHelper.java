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
package org.moflon.emf.codegen.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl;

class GenModelResourceHelper extends XMIHelperImpl {

	GenModelResourceHelper(GenModelResource resource) {
		super(resource);
	}

	public GenModelResource getResource() {
		return (GenModelResource) super.getResource();
	}

	public URI deresolve(URI uri) {
		return super.deresolve(uri);
	}
}
