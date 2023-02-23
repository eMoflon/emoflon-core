package org.moflon.core.plugins.manifest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

/**
 * This class mimics the behavior of JDK's {@link Manifest#write}, which fails
 * to close its output stream.
 * 
 * For more information on the format of Manifest files, see {@link http
 * ://www.cs.mun.ca/~michael/java/jdk1.1-beta2-docs/guide/jar/manifest.html}.
 */
public class ManifestWriter {

	private static final String NL = "\r\n";

	/**
	 * Copied from {@link Manifest#write(OutputStream)}
	 * 
	 * Bugfix: Output stream is closed properly
	 */
	public void write(final Manifest manifest, final OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		// Write out the main attributes for the manifest
		writeMain(manifest.getMainAttributes(), dos);
		// Now write out the pre-entry attributes
		Iterator<Map.Entry<String, Attributes>> it = manifest.getEntries().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Attributes> e = it.next();
			StringBuffer buffer = new StringBuffer("Name: ");
			String value = e.getKey();
			value = linebreaks(value);
			if (value != null) {
				byte[] vb = value.getBytes("UTF8");
				value = createEmptyString(vb);
			}
			buffer.append(value);
			buffer.append(NL);
			dos.writeBytes(buffer.toString());
			write(e.getValue(), dos);
		}
		dos.flush();
		dos.close();
	}

	/**
	 * Copy of {@link Attributes#writeMain}
	 */
	private void writeMain(final Attributes attributes, final DataOutputStream out) throws IOException {
		// write out the *-Version header first, if it exists
		String vername = Name.MANIFEST_VERSION.toString();
		String version = attributes.getValue(vername);
		if (version == null) {
			vername = Name.SIGNATURE_VERSION.toString();
			version = attributes.getValue(vername);
		}

		if (version != null) {
			out.writeBytes(vername + ": " + version + NL);
		}

		// write out all attributes except for the version
		// we wrote out earlier
		Iterator<Map.Entry<Object, Object>> it = attributes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Object, Object> e = it.next();
			String name = ((Name) e.getKey()).toString();
			if ((version != null) && !(name.equalsIgnoreCase(vername))) {

				StringBuffer buffer = new StringBuffer(name);
				buffer.append(": ");

				String value = (String) e.getValue();
				value = linebreaks(value);
				if (value != null) {
					byte[] vb = value.getBytes("UTF8");
					value = createEmptyString(vb);
				}
				buffer.append(value);

				buffer.append(NL);
				out.writeBytes(buffer.toString());
			}
		}
		out.writeBytes(NL);
	}

	/**
	 * Replaces all ',' with ",$newline " to break all dependencies into individual
	 * lines.
	 * 
	 * @param oldValue Input string.
	 * @return Modified string (with line breaks after ',')
	 */
	private String linebreaks(final String oldValue) {
		// TODO: Find longer strings than 70 characters and break them at '.'
		// ^This also needs modifications in, e.g., 'ManifestFileUpdater.java'.

		// This does **not** add multiple newlines!
		return oldValue.replaceAll(",", "," + NL + " ");
	}

	private void write(final Attributes attributes, final DataOutputStream os) throws IOException {
		Iterator<Map.Entry<Object, Object>> it = attributes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Object, Object> e = it.next();
			StringBuffer buffer = new StringBuffer(((Name) e.getKey()).toString());
			buffer.append(": ");

			String value = (String) e.getValue();
			value = linebreaks(value);
			if (value != null) {
				byte[] vb = value.getBytes("UTF8");
				value = createEmptyString(vb);
			}
			buffer.append(value);

			buffer.append(NL);
			os.writeBytes(buffer.toString());
		}
		os.writeBytes(NL);
	}

	private static String createEmptyString(final byte[] vb) {
		final String value = new String(vb, 0, vb.length, Charset.forName("UTF-8"));
		// original Oracle code:
		// value = new String(vb, 0, 0, vb.length);
		return value;
	}
}
