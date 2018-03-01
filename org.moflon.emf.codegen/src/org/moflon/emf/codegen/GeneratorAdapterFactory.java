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

import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;
import org.eclipse.emf.common.notify.Adapter;
import org.moflon.emf.injection.ide.InjectionManager;

/**
 * This class provides the {@link InjectionManager}, which handles the code to
 * be placed inside a method body.
 *
 * It is embedded in the EMF code generation process inside the files
 * 'insert.javajetinc' and 'Header.javajetinc'.
 */
public class GeneratorAdapterFactory extends GenModelGeneratorAdapterFactory {
	protected InjectionManager injectionManager;

	@Override
	public Adapter createGenModelAdapter() {
		if (genModelGeneratorAdapter == null) {
			genModelGeneratorAdapter = new GenModelGeneratorAdapter(this) {
				@Override
				public boolean canGenerateModel(final Object object) {
					return false;
				}
			};
		}
		return genModelGeneratorAdapter;
	}

	@Override
	public Adapter createGenPackageAdapter() {
		if (genPackageGeneratorAdapter == null) {
			genPackageGeneratorAdapter = new GenPackageGeneratorAdapter(this);
		}
		return genPackageGeneratorAdapter;
	}

	public final InjectionManager getInjectionManager() {
		return injectionManager;
	}
}
