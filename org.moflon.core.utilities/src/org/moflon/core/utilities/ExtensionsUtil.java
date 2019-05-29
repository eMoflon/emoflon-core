package org.moflon.core.utilities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.moflon.core.utilities.LogUtils;

public class ExtensionsUtil {
	private static final Logger logger = Logger.getLogger(ExtensionsUtil.class);

	/**
	 * Collects all registered extensions with the given ID.
	 * 
	 * @param extensionID
	 *            the ID of the extension
	 * @param property
	 *            the name of the property
	 * @param extensionType
	 *            the extension type
	 * @return all extensions with the given ID as extensions of the given type
	 */
	public static <T> Collection<T> collectExtensions(final String extensionID, final String property,
			final Class<T> extensionType) {
		ArrayDeque<T> extensions = new ArrayDeque<T>();
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionID);
		try {
			for (IConfigurationElement e : config) {
				logger.debug("Evaluating extension");
				final Object o = e.createExecutableExtension(property);
				if (extensionType.isInstance(o)) {
					String priority_attribute = e.getAttribute("build_before_all");
					if(priority_attribute != null && priority_attribute.equalsIgnoreCase("true")) {
						extensions.push(extensionType.cast(o));
					}else {
						extensions.add(extensionType.cast(o));
					}
				}
			}
		} catch (CoreException ex) {
			LogUtils.error(logger, ex);
		}

		return extensions;
	}
}
