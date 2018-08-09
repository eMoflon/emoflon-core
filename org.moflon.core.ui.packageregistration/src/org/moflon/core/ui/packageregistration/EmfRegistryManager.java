/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.moflon.core.ui.packageregistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * This class allows to register and manage EMF metamodels
 *
 * @author Roland Kluge - Initial implementation
 *
 */
public class EmfRegistryManager {
	private static EmfRegistryManager instance = null;

	private HashMap<String, List<EPackage>> managedMetamodels = new HashMap<>();

	public static EmfRegistryManager getInstance() {
		if (instance == null) {
			instance = new EmfRegistryManager();
		}
		return instance;
	}

	public void registerMetamodel(String fileName) throws Exception {
		List<EPackage> ePackages = register(URI.createPlatformResourceURI(fileName, true), EPackage.Registry.INSTANCE);
		managedMetamodels.put(fileName, ePackages);
	}

	// The following methods are taken from org.eclipse.epsilon.emc.emf.EmfUtil

	public static List<EPackage> register(URI uri, EPackage.Registry registry) throws Exception {
		return register(uri, registry, true);
	}

	/**
	 * Register all the packages in the metamodel specified by the uri in the
	 * registry.
	 *
	 * @param uri
	 *            The URI of the metamodel
	 * @param registry
	 *            The registry in which the metamodel's packages are registered
	 * @param useUriForResource
	 *            If True, the URI of the resource created for the metamodel would
	 *            be overwritten with the URI of the last EPackage in the metamodel.
	 * @return A list of the EPackages registered.
	 * @throws Exception
	 *             If there is an error accessing the resources.
	 */
	public static List<EPackage> register(URI uri, EPackage.Registry registry, boolean useUriForResource)
			throws Exception {

		List<EPackage> ePackages = new ArrayList<EPackage>();

		initialiseResourceFactoryRegistry();

		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);

		Resource metamodel = resourceSet.createResource(uri);
		metamodel.load(Collections.EMPTY_MAP);

		setDataTypesInstanceClasses(metamodel);

		Iterator<EObject> it = metamodel.getAllContents();
		while (it.hasNext()) {
			Object next = it.next();
			if (next instanceof EPackage) {
				EPackage p = (EPackage) next;

				adjustNsAndPrefix(metamodel, p, useUriForResource);
				registry.put(p.getNsURI(), p);
				ePackages.add(p);
			}
		}

		return ePackages;

	}

	private static void initialiseResourceFactoryRegistry() {
		final Map<String, Object> etfm = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();

		if (!etfm.containsKey("*")) {
			etfm.put("*", new XMIResourceFactoryImpl());
		}

	}

	private static void adjustNsAndPrefix(Resource metamodel, EPackage p, boolean useUriForResource) {
		if (p.getNsURI() == null || p.getNsURI().trim().length() == 0) {
			if (p.getESuperPackage() == null) {
				p.setNsURI(p.getName());
			} else {
				p.setNsURI(p.getESuperPackage().getNsURI() + "/" + p.getName());
			}
		}

		if (p.getNsPrefix() == null || p.getNsPrefix().trim().length() == 0) {
			if (p.getESuperPackage() != null) {
				if (p.getESuperPackage().getNsPrefix() != null) {
					p.setNsPrefix(p.getESuperPackage().getNsPrefix() + "." + p.getName());
				} else {
					p.setNsPrefix(p.getName());
				}
			}
		}

		if (p.getNsPrefix() == null)
			p.setNsPrefix(p.getName());
		if (useUriForResource)
			metamodel.setURI(URI.createURI(p.getNsURI()));
	}

	protected static void setDataTypesInstanceClasses(Resource metamodel) {
		Iterator<EObject> it = metamodel.getAllContents();
		while (it.hasNext()) {
			EObject eObject = (EObject) it.next();
			if (eObject instanceof EEnum) {
				// ((EEnum) eObject).setInstanceClassName("java.lang.Integer");
			} else if (eObject instanceof EDataType) {
				EDataType eDataType = (EDataType) eObject;
				String instanceClass = "";
				if (eDataType.getName().equals("String")) {
					instanceClass = "java.lang.String";
				} else if (eDataType.getName().equals("Boolean")) {
					instanceClass = "java.lang.Boolean";
				} else if (eDataType.getName().equals("Integer")) {
					instanceClass = "java.lang.Integer";
				} else if (eDataType.getName().equals("Float")) {
					instanceClass = "java.lang.Float";
				} else if (eDataType.getName().equals("Double")) {
					instanceClass = "java.lang.Double";
				}
				if (instanceClass.trim().length() > 0) {
					eDataType.setInstanceClassName(instanceClass);
				}
			}
		}
	}
}
