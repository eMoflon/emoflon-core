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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

public class SDMEnhancedEcoreResource extends XMIResourceImpl {
	public static final String READ_ONLY = "READ_ONLY";
	public static final String SAVE_GENERATED_PACKAGE_CROSSREF_URIS = "SAVE_GENERATED_PACKAGE_CROSSREF_URIS";
	public static final boolean ADD = true;
	public static final boolean REMOVE = false;

	/**
	 * Provides access to {@link org.eclipse.emf.ecore.EModelElement
	 * <em>EModelElements</em>} of this <code>ECoreResource</code> via the
	 * <b>generated</b> {@link org.eclipse.emf.ecore.EPackage#getNsURI() namespace
	 * URIs</em>}
	 * 
	 * @see org.eclipse.emf.ecore.EPackage#getNsURI()
	 */
	private boolean handleGeneratedEPackageURIs;

	/**
	 * Whether to use the generated {@link org.eclipse.emf.ecore.EModelElement
	 * <em>EModelElements</em>} instead of the file-based ones contained in this
	 * <code>ECoreResource</code> (or vice versa).
	 */
	private boolean useGeneratedEPackageResource;

	private boolean overrideHelper;

	public SDMEnhancedEcoreResource(URI uri) {
		super(uri);
	}

	public final boolean isHandleGeneratedEPackageURIs() {
		return handleGeneratedEPackageURIs;
	}

	public final void setHandleGeneratedEPackageURIs(boolean handleGeneratedEPackageURIs) {
		boolean oldHandleGeneratedEPackageURIs = this.handleGeneratedEPackageURIs;
		this.handleGeneratedEPackageURIs = handleGeneratedEPackageURIs;
		if (oldHandleGeneratedEPackageURIs != this.handleGeneratedEPackageURIs) {
			if (this.handleGeneratedEPackageURIs && !this.useGeneratedEPackageResource) {
				handleAllEPackages(ADD);
			} else if (!this.handleGeneratedEPackageURIs) {
				handleAllEPackages(REMOVE);
			}
		}
	}

	public final boolean isUseGeneratedEPackageResource() {
		return useGeneratedEPackageResource;
	}

	public final void setUseGeneratedEPackageResource(boolean useGeneratedEPackageResource) {
		boolean oldUseGeneratedEPackageResource = this.useGeneratedEPackageResource;
		this.useGeneratedEPackageResource = useGeneratedEPackageResource;
		if (oldUseGeneratedEPackageResource != this.useGeneratedEPackageResource) {
			if (this.handleGeneratedEPackageURIs) {
				handleAllEPackages(!this.useGeneratedEPackageResource);
			}
		}
	}

	public EObject getEObject(String uriFragment) {
		EObject result = super.getEObject(uriFragment);

		if (handleGeneratedEPackageURIs && useGeneratedEPackageResource) {
			// Replace EObject in file with a proxy from the generated EPackage resource
			if (isLoaded && !isLoading && result instanceof EModelElement) {
				URI proxyURI = calculateGeneratedEPackageURI(result);
				if (proxyURI != null) {
					InternalEObject proxy = (InternalEObject) EcorePackage.eINSTANCE.getEcoreFactory()
							.create(result.eClass());
					proxy.eSetProxyURI(proxyURI.appendFragment(uriFragment));
					return proxy;
				}
			}
		}
		return result;
	}

	static final URI calculateGeneratedEPackageURI(EObject eObject) {
		for (EObject current = eObject; current != null; current = current.eContainer()) {
			if (current instanceof EPackage) {
				EPackage ePackageInFile = (EPackage) current;
				return URI.createURI(ePackageInFile.getNsURI());
			}
		}
		return null;
	}

	protected XMLSave createXMLSave(Map<?, ?> options) {
		if (options != null) {
			Object optionValue = options.get(SAVE_GENERATED_PACKAGE_CROSSREF_URIS);
			overrideHelper = optionValue instanceof Boolean ? (Boolean) optionValue : false;
		}
		return createXMLSave();
	}

	protected XMLHelper createXMLHelper() {
		return overrideHelper ? new SDMEnhancedResourceHelper(this) : super.createXMLHelper();
	}

	public void save(Map<?, ?> options) throws IOException {
		Object readOnlyOption = options != null && options.containsKey(READ_ONLY) ? options.get(READ_ONLY)
				: defaultSaveOptions != null ? defaultSaveOptions.get(READ_ONLY) : null;
		if (readOnlyOption != null && readOnlyOption instanceof Boolean && ((Boolean) readOnlyOption).booleanValue()) {
			return;
		} else {
			super.save(options);
		}
	}

	public void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		super.doLoad(inputStream, options);
		if (handleGeneratedEPackageURIs && !useGeneratedEPackageResource) {
			handleAllEPackages(ADD);
		}
	}

	public void doUnload() {
		if (handleGeneratedEPackageURIs && !useGeneratedEPackageResource) {
			handleAllEPackages(REMOVE);
		}
		super.doUnload();
	}

	private final void handleAllEPackages(final boolean add) {
		for (EObject eObject : getContents()) {
			if (EcorePackage.Literals.EPACKAGE.isInstance(eObject)) {
				for (PackageIterator i = new PackageIterator((EPackage) eObject); i.hasNext();) {
					handleEPackage(i.next(), add);
				}
			}
		}
	}

	private final void handleEPackage(EPackage ePackage, boolean add) {
		URI uri = URI.createURI(ePackage.getNsURI());
		Map<URI, URI> uriMap = getResourceSet().getURIConverter().getURIMap();
		if (add) {
			uriMap.put(uri, getURI());
		} else {
			uriMap.remove(uri);
		}
	}
}
