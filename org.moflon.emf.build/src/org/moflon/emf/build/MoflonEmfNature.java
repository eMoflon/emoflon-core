package org.moflon.emf.build;

import org.moflon.core.build.nature.MoflonProjectConfigurator;

/**
 * Nature for pure eMoflon EMF projects
 *
 * @author Roland Kluge - Initial implementation
 */
public class MoflonEmfNature extends MoflonProjectConfigurator {
	private static final String MOFLON_EMF_NATURE_ID = "org.moflon.emf.build.MoflonEmfNature";

	@Override
	protected String getBuilderId() {
		return MoflonEmfBuilder.getId();
	}

	@Override
	protected String getNatureId() {
		return MOFLON_EMF_NATURE_ID;
	}

	public static String getId() {
		return MOFLON_EMF_NATURE_ID;
	}

}
