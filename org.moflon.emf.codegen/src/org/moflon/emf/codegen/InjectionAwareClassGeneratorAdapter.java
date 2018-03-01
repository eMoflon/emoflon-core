package org.moflon.emf.codegen;

import org.eclipse.emf.ecore.EOperation;
import org.moflon.core.utilities.MoflonUtil;
import org.moflon.emf.codegen.template.JavaClassGenerator;
import org.moflon.emf.injection.ide.InjectionManager;

/**
 * This implementation base class is invoked during the code generation of a
 * Java class
 *
 * @author Gergely Varró
 * @author Roland Kluge
 *
 * @see JavaClassGenerator
 */
public class InjectionAwareClassGeneratorAdapter extends AbstractMoflonClassGeneratorAdapter {

	public InjectionAwareClassGeneratorAdapter(GeneratorAdapterFactory generatorAdapterFactory) {
		super(generatorAdapterFactory);
	}

	@Override
	public String getGeneratedMethodBody(EOperation eOperation) {
		String generatedMethodBody = null;

		final InjectionManager injectionManager = getAdapterFactory().getInjectionManager();
		if (injectionManager != null && injectionManager.hasModelCode(eOperation)) {
			final String modelCode = injectionManager.getModelCode(eOperation);
			generatedMethodBody = modelCode;
		}

		if (generatedMethodBody == null) {
			generatedMethodBody = MoflonUtil.DEFAULT_METHOD_BODY;
		}

		return generatedMethodBody;
	}

}