package org.moflon.core.ui.autosetup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.moflon.core.utilities.ExceptionUtil;
import org.moflon.core.utilities.UtilityClassNotInstantiableException;
import org.moflon.core.utilities.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class for working with PSF files.
 */
public final class PsfFileUtils {

	private static Logger logger = Logger.getLogger(PsfFileUtils.class);

	private PsfFileUtils() {
		throw new UtilityClassNotInstantiableException();
	}

	public static String joinPsfFile(Collection<File> files) throws IOException, CoreException {
		if (files == null || files.isEmpty())
			throw new IllegalArgumentException("Illegal input (empty or null): " + files);

		final Iterator<File> iter = files.iterator();
		final File file1 = iter.next();
		if (files.size() == 1)
			return FileUtils.readFileToString(file1);

		final Document document1 = XMLUtils.parseXmlDocument(file1);
		final Node root1 = document1.getChildNodes().item(0);
		while (iter.hasNext()) {
			final File file2 = iter.next();
			final Document document2 = XMLUtils.parseXmlDocument(file2);
			final Node root2 = document2.getChildNodes().item(0);
			final NodeList childNodes2 = root2.getChildNodes();
			for (int i = 0; i < childNodes2.getLength(); ++i) {
				final Node childNode2 = childNodes2.item(i);
				final Node clonedChildNode2 = childNode2.cloneNode(true);
				document1.adoptNode(clonedChildNode2); // Update/set metadata appropriately for new document
				root1.appendChild(clonedChildNode2); // Insert at appropriate place in new document
			}
		}

		final String joinedPsfFiles = XMLUtils.formatXmlString(document1, new NullProgressMonitor());
		return joinedPsfFiles;

	}

	public static List<String> extractPsfFileContents(final List<String> absolutePathsToPSF) throws IOException {
		final List<String> psfContents = new ArrayList<>();
		for (final String absolutePathToPSF : absolutePathsToPSF) {
			psfContents.add(FileUtils.readFileToString(new File(absolutePathToPSF)));
		}
		return psfContents;
	}

	public static Optional<String> extractPsfFileContent(final URL url) {
		try {
			URLConnection connection = url.openConnection();
			connection.setReadTimeout(60 * 1000);
			String enc = connection.getContentEncoding();
			if (enc == null)
				enc = ResourcesPlugin.getEncoding();
			return Optional.of(read(connection.getInputStream(), enc));
		} catch (Exception e) {
			logger.error(ExceptionUtil.displayExceptionAsString(e));
			// Fail silently but return empty optional
		}

		return Optional.empty();
	}

	public static String read(InputStream is, String encoding) throws IOException {
		if (is == null)
			throw new IOException("Stream is null");

		BufferedReader reader = null;
		try {
			StringBuffer buffer = new StringBuffer();
			char[] part = new char[2048];
			int read = 0;
			reader = new BufferedReader(new InputStreamReader(is, encoding));
			while ((read = reader.read(part)) != -1)
				buffer.append(part, 0, read);

			return buffer.toString();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					// silently ignored
				}
			}
		}
	}
}
