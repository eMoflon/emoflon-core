package org.moflon.core.ui.errorhandling;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Factory for {@link MultiStatusAwareErrorReporter} extension
 * 
 * @author Roland Kluge - Initial implementation
 */
public class MultiStatusAwareErrorReporterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(final Object adaptableObject, @SuppressWarnings("rawtypes") final Class adapterType) {
		if (adaptableObject instanceof IFile && MultiStatusAwareErrorReporter.class == adapterType) {
			return new MultiStatusAwareErrorReporter((IFile) adaptableObject);
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] { MultiStatusAwareErrorReporter.class };
	}
}
