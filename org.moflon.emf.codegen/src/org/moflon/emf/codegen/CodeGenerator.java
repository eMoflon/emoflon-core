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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory.Descriptor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Monitor;

public class CodeGenerator {
	private final GeneratorAdapterFactory.Descriptor descriptor;

	public CodeGenerator(final Descriptor descriptor) {
		this.descriptor = descriptor;
	}

	public final IStatus generateCode(final GenModel genModel, final Monitor monitor) {
		final Generator generator = new Generator();
		generator.getAdapterFactoryDescriptorRegistry().addDescriptor(GenModelPackage.eNS_URI, descriptor);
		generator.setInput(genModel);
		genModel.setCanGenerate(true);

		final Diagnostic diagnostic = generator.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE,
				"EMoflon Code Generation", monitor);
		final int severity = diagnostic.getSeverity();
		if (Diagnostic.OK != severity) {
			return new Status(severity, "org.eclipse.emf.common", severity, diagnostic.getMessage(),
					diagnostic.getException());
		}
		return new Status(IStatus.OK, "org.eclipse.emf.common", "Code generation succeeded");
	}
}
