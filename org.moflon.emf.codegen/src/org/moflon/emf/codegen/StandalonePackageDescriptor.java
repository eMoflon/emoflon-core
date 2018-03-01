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
package org.moflon.emf.codegen;

import java.lang.reflect.Field;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Descriptor;

public class StandalonePackageDescriptor implements Descriptor {
	private final String className;

	public StandalonePackageDescriptor(final String className) {
		this.className = className;
	}

	@Override
	public EPackage getEPackage() {
		try {
			Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
			Field eInstanceField = clazz.getField("eINSTANCE");
			Object result = eInstanceField.get(null);
			return (EPackage) result;
		} catch (ClassNotFoundException | SecurityException | IllegalArgumentException | IllegalAccessException
				| ClassCastException | NoSuchFieldException e) {
			// Do nothing
		}
		return null;
	}

	@Override
	public EFactory getEFactory() {
		return null;
	}
}
