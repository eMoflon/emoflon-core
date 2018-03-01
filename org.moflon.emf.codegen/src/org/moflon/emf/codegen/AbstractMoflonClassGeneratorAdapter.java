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

import java.io.File;
import java.lang.reflect.Method;

import org.eclipse.emf.codegen.ecore.genmodel.GenBase;
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.jet.JETEmitter;
import org.eclipse.emf.codegen.util.ImportManager;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EOperation;
import org.moflon.emf.codegen.template.JavaClassGenerator;
import org.moflon.emf.injection.ide.InjectionManager;
import org.moflon.emf.injection.ide.InjectionUtilities;
import org.moflon.emf.injection.unparsing.InjectionConstants;

/**
 * This implementation base class is invoked during the code generation of a
 * Java class
 *
 * @author Gergely Varró
 * @author Roland Kluge
 *
 */
public abstract class AbstractMoflonClassGeneratorAdapter
		extends org.eclipse.emf.codegen.ecore.genmodel.generator.GenClassGeneratorAdapter {
	private JETEmitterDescriptor[] emitterDescriptors;

	/**
	 * Registers {@link JavaClassGenerator} as code-generating class for EMF
	 * classes.
	 *
	 * @param generatorAdapterFactory
	 */
	public AbstractMoflonClassGeneratorAdapter(final GeneratorAdapterFactory generatorAdapterFactory) {
		super(generatorAdapterFactory);
		emitterDescriptors = new JETEmitterDescriptor[] {
				new JETEmitterDescriptor("model/Class.javajet", JavaClassGenerator.class.getName()) };
	}

	public boolean hasGeneratedMethodBody(final EOperation eOperation) {
		return getAdapterFactory().getInjectionManager().hasModelCode(eOperation);
	}

	/**
	 * Generates the content of the method, described by the given EOperation.
	 */
	abstract public String getGeneratedMethodBody(EOperation eOperation);

	/**
	 * Returns the members code for the given EClass, depending on whether we
	 * currently generate the interface or the implementation of the EClass.
	 */
	public String getInjectedCode(final boolean isImplementation) {
		String code = "";
		final InjectionManager injectionManager = getAdapterFactory().getInjectionManager();
		if (injectionManager != null) {
			final GenClass genClass = (GenClass) generatingObject;
			final String fullyQualifiedClassName = isImplementation ? InjectionUtilities.getClassName(genClass)
					: InjectionUtilities.getInterfaceName(genClass);

			final String retrievedMembersCode = injectionManager.getMembersCode(fullyQualifiedClassName);
			if (retrievedMembersCode != null) {
				code = retrievedMembersCode;
			}
		}
		return buildMembersBlock(code);
	}

	public void handleImports(final boolean isImplementation) {
		final GenClass genClass = ((GenClass) generatingObject);
		final InjectionManager injectionManager = getAdapterFactory().getInjectionManager();
		if (injectionManager != null) {
			final ImportManager importManager = genClass.getGenModel().getImportManager();
			final String fullyQualifiedClassName = isImplementation ? InjectionUtilities.getClassName(genClass)
					: InjectionUtilities.getInterfaceName(genClass);
			for (final String imp : injectionManager.getImports(fullyQualifiedClassName)) {
				if (importManager instanceof InjectionHandlingImportManager) {
					((InjectionHandlingImportManager) importManager).injectedImports.add(imp);
				} else {
					importManager.addImport(imp);
				}
			}
		}
	}

	@Override
	public GeneratorAdapterFactory getAdapterFactory() {
		return (GeneratorAdapterFactory) adapterFactory;
	}

	@Override
	protected void ensureContainerExists(final URI workspacePath, final Monitor monitor) {
		if (EMFPlugin.IS_ECLIPSE_RUNNING) {
			super.ensureContainerExists(workspacePath, monitor);
		} else {
			URI platformResourceURI = URI.createPlatformResourceURI(workspacePath.toString(), true);
			URI normalizedURI = getURIConverter().normalize(platformResourceURI);
			if (normalizedURI.isFile()) {
				File file = new File(normalizedURI.toString());
				if (!file.exists()) {
					file.mkdirs();
				}
			}
		}
	}

	@Override
	protected JETEmitter getJETEmitter(final JETEmitterDescriptor[] jetEmitterDescriptors, final int id) {
		JETEmitter jetEmitter = super.getJETEmitter(jetEmitterDescriptors, id);
		if (!EMFPlugin.IS_ECLIPSE_RUNNING) {
			try {
				Class<?> clazz = getClass().getClassLoader().loadClass(jetEmitterDescriptors[id].className);
				Method method = clazz.getMethod("generate", Object.class);
				jetEmitter.setMethod(method);
			} catch (ClassNotFoundException e) {
				// Do nothing
			} catch (NoSuchMethodException e) {
				// Do nothing
			} catch (SecurityException e) {
				// Do nothing
			}
		}
		return jetEmitter;
	}

	@Override
	protected JETEmitterDescriptor[] getJETEmitterDescriptors() {
		return emitterDescriptors;
	}

	@Override
	protected void ensureProjectExists(final String workspacePath, final Object object, final Object projectType,
			final boolean force, final Monitor monitor) {
	}

	@Override
	protected void createImportManager(final String packageName, final String className) {
		importManager = new InjectionHandlingImportManager(packageName, true);
		importManager.addMasterImport(packageName, className);
		updateImportManager();
	}

	@Override
	protected void clearImportManager() {
		importManager = null;
		updateImportManager();
	}

	protected void updateImportManager() {
		if (generatingObject != null) {
			((GenBase) generatingObject).getGenModel().setImportManager(importManager);
		}
	}

	@Override
	protected Diagnostic doPreGenerate(final Object object, final Object projectType) {
		return Diagnostic.OK_INSTANCE;
	}

	@Override
	protected Diagnostic doPostGenerate(final Object object, final Object projectType) {
		return Diagnostic.OK_INSTANCE;
	}

	@Override
	protected void generateJava(final String targetPath, final String packageName, final String className,
			final JETEmitter jetEmitter, final Object[] arguments, final Monitor monitor) {
		Object argument = arguments[0];
		if (argument instanceof Object[]) {
			Object[] argumentArray = (Object[]) argument;
			Object[] newArgumentArray = new Object[argumentArray.length + 1];
			System.arraycopy(argumentArray, 0, newArgumentArray, 0, argumentArray.length);
			newArgumentArray[argumentArray.length] = this;
			super.generateJava(targetPath, packageName, className, jetEmitter, new Object[] { newArgumentArray },
					monitor);
		} else {
			super.generateJava(targetPath, packageName, className, jetEmitter, arguments, monitor);
		}
	}

	/**
	 * Builds a members block that is ready to be injected. It gets surrounded by
	 * whitespace and the comments to mark the block.
	 */
	private static String buildMembersBlock(final String code) {
		final StringBuffer block = new StringBuffer();
		block.append(InjectionConstants.INDENT).append(InjectionConstants.MEMBERS_BEGIN);
		block.append(InjectionConstants.NL).append(InjectionConstants.INDENT);
		block.append(code);
		block.append(InjectionConstants.NL).append(InjectionConstants.INDENT);
		block.append(InjectionConstants.MEMBERS_END);
		return block.toString();
	}

}