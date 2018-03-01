package org.moflon.emf.injection.validation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * This adapter produces an {@link InjectionErrorReporter} for a given
 * {@link IProject}
 * 
 * @author Roland Kluge - Initial implementation
 *
 */
public class InjectionErrorReporterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(final Object adaptableObject, @SuppressWarnings("rawtypes") final Class adapterType) {
		if (adaptableObject instanceof IProject && InjectionErrorReporter.class == adapterType) {
			return new InjectionErrorReporter(IProject.class.cast(adaptableObject));
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] { InjectionErrorReporter.class };
	}
}
