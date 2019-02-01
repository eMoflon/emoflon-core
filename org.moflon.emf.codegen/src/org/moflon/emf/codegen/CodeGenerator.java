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
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory.Descriptor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Monitor;

public class CodeGenerator {
	private final GeneratorAdapterFactory.Descriptor descriptor;
	private String taskName = "eMoflon Code Generator";

	public CodeGenerator(final Descriptor descriptor) {
		this.descriptor = descriptor;
	}

	/**
	 * Assigns a descriptive name to this code generator. If this method is not
	 * called, a default name is used.
	 * 
	 * @param taskName
	 *                     the descriptive name
	 */
	public void setTaskName(final String taskName) {
		this.taskName = taskName;
	}

	public final IStatus generateCode(final GenModel genModel, final Monitor monitor) {
		final Generator generator = new Generator();
		generator.getAdapterFactoryDescriptorRegistry().addDescriptor(GenModelPackage.eNS_URI, descriptor);
		generator.setInput(genModel);
		genModel.setCanGenerate(true);

		final Diagnostic diagnostic = generator.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE,
				this.taskName, monitor);
		return Diagnostics.createStatusFromDiagnostic(diagnostic);
	}
}
