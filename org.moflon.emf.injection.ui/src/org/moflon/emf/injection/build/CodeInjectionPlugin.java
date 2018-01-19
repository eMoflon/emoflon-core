package org.moflon.emf.injection.build;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;

public final class CodeInjectionPlugin {
	
	public static final String getInterfaceName(final GenClass genClass) {
		return genClass.getGenPackage().getInterfacePackageName() + "." + genClass.getInterfaceName();
	}

	public static final String getClassName(final GenClass genClass) {
		return genClass.getGenPackage().getClassPackageName() + "." + genClass.getClassName();
	}
}
