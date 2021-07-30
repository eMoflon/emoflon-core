package org.moflon.smartemf.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.jdom2.Attribute;
import org.jdom2.Element;

public final class XmiParserUtil {
	
	final public static String XMI_NS = "xmi";
	final public static String XMI_ROOT_NODE = "XMI";
	final public static String XMI_URI = "http://www.omg.org/XMI";
	final public static String XMI_VERSION_ATR = "version";
	final public static String XMI_VERSION = "2.0";
	
	final public static String XSI_NS = "xsi";
	final public static String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
	final public static String XSI_TYPE = "type";
	
	final public static String HREF_ATR = "href";
	
	public static String resolveURIToPath(final URI uri, final String workspacePath) {
		String path = uri.devicePath();
		if(path.startsWith("/resource")) {
			path = path.replaceFirst("/resource", " ");
		}
		path = path.trim().replaceAll("%20", " ");
		
		File file = new File(path);
		
		if(file != null && file.exists())
			try {
				return file.getCanonicalPath();
			} catch (IOException e) {
				return null;
			}
		
		// Resolve workspace relative path
		String[] segments = URI.createURI(path).segments();
		String projectFolder = segments[0];
		String projectPath = null;
		try {
			projectPath = pathOfProjectInWorkspace(workspacePath, projectFolder);
		} catch (IOException e) {
			return null;
		}
		if(projectPath == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(projectPath);
		for(int i = 1; i<segments.length; i++) {
			sb.append("/"+segments[i]);
		}
		
		return sb.toString();
	}
	
	public static String resolveURIRelativeToBaseURI(final URI baseUri, final URI uri, final String workspacePath) {
		String filePath = resolveURIToPath(uri, workspacePath);
		if(filePath != null)
			return filePath;
		
		filePath = uri.devicePath();
		if(filePath.startsWith("/resource")) {
			filePath = filePath.replaceFirst("/resource", " ");
		}
		filePath = filePath.trim().replaceAll("%20", " ");
		
		// Try to resolve the relative uri using the working initial uri
		URI fileUri = URI.createFileURI(filePath);
		String fileRootSegment = fileUri.segment(0);
		File commonRoot = null;
		
		File queryFile = new File(baseUri.devicePath().trim().replace("%20", " "));
		while(queryFile!=null && queryFile.exists() && commonRoot == null) {
			if(queryFile.isDirectory()) {
				for(File containedFile : queryFile.listFiles()) {
					if(containedFile.getName().equals(fileRootSegment)) {
						commonRoot = queryFile;
						break;
					}
				}
			}
			Path queryPath = queryFile.toPath();
			if(queryPath.getParent() == null)
				break;
			
			queryFile = queryPath.getParent().toFile();
		}
		if(commonRoot == null)
			return null;
		
		String newValidPath = null;
		try {
			newValidPath = commonRoot.getCanonicalPath()+"/"+fileUri.devicePath();
		} catch (IOException e) {
			return null;
		}
		File file = new File(newValidPath);
		if(file == null || !file.exists()) {
			return null;
		}
		return newValidPath;
	}
	
	public static String pathOfProjectInWorkspace(final String workspacePath, final String projectName) throws IOException {
		File workspaceFolder = new File(workspacePath);
		if(!workspaceFolder.exists() || !workspaceFolder.isDirectory())
			return null;
		
		for(File subFolder : workspaceFolder.listFiles()) {
			if(!subFolder.isDirectory())
				continue;
			
			if(!subFolder.getName().equalsIgnoreCase(projectName))
				continue;
			
			for(File subsubFile : subFolder.listFiles()) {
				if(subsubFile.isDirectory())
					continue;
				
				if(subsubFile.getName().endsWith(".project")) {
					return subFolder.getCanonicalPath();
				}
			}
		}
		
		return null;
	}
	
	public static boolean isHyperref(final Element element) {
		if(element == null)
			return false;
		
		Attribute atr = element.getAttribute(HREF_ATR);
		if(atr == null)
			return false;
		
		return true;
	}
	
	public static Object stringToValue(final EFactory factory, final EAttribute atr, final String value) throws Exception {
		EcorePackage epack = EcorePackage.eINSTANCE;
		if(atr.getEAttributeType() == epack.getEString()) {
			return value;
		} else if(atr.getEAttributeType() == epack.getEBoolean()) {
			return ("true".equals(value)) ? true : false;
		} else if(atr.getEAttributeType() == epack.getEByte()) {
			return Byte.parseByte(value);
		} else if(atr.getEAttributeType() == epack.getEChar()) {
			return value.charAt(0);
		} else if(atr.getEAttributeType() == epack.getEDate()) {
			return DateFormat.getDateInstance().parse(value);
		} else if(atr.getEAttributeType() == epack.getEDouble()) {
			return Double.parseDouble(value);
		}  else if(atr.getEAttributeType() == epack.getEFloat()) {
			return Float.parseFloat(value);
		} else if(atr.getEAttributeType() == epack.getEInt()) {
			return Integer.parseInt(value);
		} else if(atr.getEAttributeType() == epack.getELong()) {
			return Long.parseLong(value);
		} else if(atr.getEAttributeType() == epack.getEShort()) {
			return Short.parseShort(value);
		} else {
			return factory.createFromString(atr.getEAttributeType(), value);
		}
	}
	
	public static String valueToString(final EFactory factory, final EAttribute atr, final Object value) throws IOException {
		EcorePackage epack = EcorePackage.eINSTANCE;
		if(atr.getEAttributeType() == epack.getEString()) {
			return (String) value;
		} else if(atr.getEAttributeType() == epack.getEBoolean()) {
			return String.valueOf(value);
		} else if(atr.getEAttributeType() == epack.getEByte()) {
			return String.valueOf(value);
		} else if(atr.getEAttributeType() == epack.getEChar()) {
			return String.valueOf(value);
		} else if(atr.getEAttributeType() == epack.getEDate()) {
			return DateFormat.getDateInstance().format(value);
		} else if(atr.getEAttributeType() == epack.getEDouble()) {
			return String.valueOf(value);
		}  else if(atr.getEAttributeType() == epack.getEFloat()) {
			return String.valueOf(value);
		} else if(atr.getEAttributeType() == epack.getEInt()) {
			return String.valueOf(value);
		} else if(atr.getEAttributeType() == epack.getELong()) {
			return String.valueOf(value);
		} else if(atr.getEAttributeType() == epack.getEShort()) {
			return String.valueOf(value);
		} else if(atr.getEAttributeType() == epack.getEFeatureMapEntry()) {
			return null;
		}else {
			return factory.convertToString(atr.getEAttributeType(), value);
		}
	}
}
