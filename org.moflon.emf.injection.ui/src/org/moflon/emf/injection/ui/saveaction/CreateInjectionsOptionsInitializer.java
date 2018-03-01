package org.moflon.emf.injection.ui.saveaction;

import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.cleanup.ICleanUpOptionsInitializer;

public class CreateInjectionsOptionsInitializer implements ICleanUpOptionsInitializer {
	@Override
	public void setDefaultOptions(final CleanUpOptions options) {
		options.setOption(CreateInjectionsSaveAction.KEY_CREATE_INJECTIONS, CleanUpOptions.TRUE);
	}

}
