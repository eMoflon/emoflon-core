/*
 * generated by Xtext 2.10.0
 */
package org.moflon.emf.injection


/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
class InjectionLanguageStandaloneSetup extends InjectionLanguageStandaloneSetupGenerated {

	def static void doSetup() {
		new InjectionLanguageStandaloneSetup().createInjectorAndDoEMFRegistration()
	}
}