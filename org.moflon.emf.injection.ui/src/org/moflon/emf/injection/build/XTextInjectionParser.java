package org.moflon.emf.injection.build;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.moflon.emf.injection.ui.internal.InjectionActivator;

import com.google.inject.Injector;

/**
 * A standalone Xtext-based parser for inject files
 * 
 * Courtesy to
 * "https://wiki.eclipse.org/Xtext/FAQ#How_do_I_load_my_model_in_a_standalone_Java_application.C2.A0.3F"
 *
 * @author Roland Kluge - Initial implementation
 */
public class XTextInjectionParser {
	private XtextResourceSet resourceSet;

	public XTextInjectionParser() {
		final Injector injector = InjectionActivator.getInstance()
				.getInjector(InjectionActivator.ORG_MOFLON_EMF_INJECTION_INJECTIONLANGUAGE);
		this.resourceSet = injector.getInstance(XtextResourceSet.class);
		this.resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
	}

	/**
	 * Parses a resource specified by an URI and returns the resulting object tree
	 * root element.
	 * 
	 * @param uri
	 *            URI of resource to be parsed
	 * @return root model object
	 * @throws IOException
	 */
	public EObject parse(final URI uri) throws IOException {
		final Resource resource = resourceSet.createResource(uri);
		resource.load(null);
		return resource.getContents().get(0);
	}
}