package org.moflon.smartemf.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.xmi.DOMHandler;
import org.eclipse.emf.ecore.xmi.DOMHelper;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.moflon.smartemf.runtime.collections.ResourceContentSmartEList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * A simplified resource implementation that serializes to XMI. Ignores save options. Can enable
 * notification cascading for its contents.
 */
public class SmartEMFResource extends UnlockedResourceImpl implements XMIResource{
	
	protected boolean cascadeNotifications = false;
	
	private EList<EObject> contents = new ResourceContentSmartEList<>(this);
	
	public SmartEMFResource(final URI uri) {
		this.uri = uri;
	}

	public boolean getCascade() {
		return cascadeNotifications;
	}

	protected void setCascade(boolean cascade) {
		cascadeNotifications = cascade;
	}

	@Override
	public EList<EObject> getContents() {
		return contents;
	}

	
//	############ The meat and potatoes ############

	@Override
	public void save(Map<?, ?> options) throws IOException {
		File file = new File(uri.toFileString());
		FileOutputStream fos = new FileOutputStream(file);
		save(fos, options);
		fos.close();
	}

	@Override
	public void save(OutputStream outputStream, Map<?, ?> options) throws IOException {
				
		URIConverter.Cipher cipher = (options != null) ? (URIConverter.Cipher)options.get(Resource.OPTION_CIPHER) : null;
		if(cipher != null) {
			throw new UnsupportedOperationException("Encryption through cipher is not supported!");
		}
		
		if (outputStream instanceof URIConverter.Writeable) {
			throw new UnsupportedOperationException("Output as URIConverter.Writeable not supported!");
		}
		
		OutputStream os = null;
		if(useZip() || (options != null && Boolean.TRUE.equals(options.get(Resource.OPTION_ZIP)))) {
			throw new UnsupportedOperationException("Zipped input streams are not supported!");
		} else {
			os = outputStream;
		}
		
		Document unparsedFile = new Document();
		JDOMXmiUnparser unparser = new JDOMXmiUnparser();
		unparser.modelToJDOMTree(this, unparsedFile);
		
		XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(unparsedFile, os);
        
        setModified(false);
	}
	
	@Override
	public void load(Map<?, ?> options) throws IOException {
		File file = new File(uri.toFileString());
		if(!file.exists())
			throw new IOException("No valid xmi file present at: "+uri);
		
		FileInputStream fis = new FileInputStream(file);
		load(fis, options);
		fis.close();
	}
	
	@Override
	public void load(InputSource inputSource, Map<?, ?> options) throws IOException {
		load(inputSource.getByteStream(), options);
	}
	
	@Override
	public void load(InputStream inputStream, Map<?, ?> options) throws IOException {
		// Useless organizational crap
		if(isLoaded)
			return;

		Notification notification = setLoaded(true);
		isLoading = true;
		
		URIConverter.Cipher cipher = (options != null) ? (URIConverter.Cipher)options.get(Resource.OPTION_CIPHER) : null;
		if(cipher != null) {
			throw new UnsupportedOperationException("Encryption through cipher is not supported!");
		}
		
		if (inputStream instanceof URIConverter.Readable) {
			throw new UnsupportedOperationException("Input as URIConverter.Readable not supported!");
		}
		
		InputStream is = null;
		if(useZip() || (options != null && Boolean.TRUE.equals(options.get(Resource.OPTION_ZIP)))) {
			throw new UnsupportedOperationException("Zipped input streams are not supported!");
		} else {
			is = inputStream;
		}
				
		SAXBuilder saxBuilder = new SAXBuilder();
		Document parsedFile = null;
		try {
			parsedFile = saxBuilder.build(is);
		} catch (JDOMException | IOException e) {
			throw new IOException(e.getMessage(), e.getCause());
		}
		
		JDOMXmiParser parser = new JDOMXmiParser();
		parser.domTreeToModel(parsedFile, this);
		
		// Finish useless stuff
		isLoading = false;

        if (notification != null) {
          eNotify(notification);
        }

        setModified(false);
	}

// 	############ All implemented getters and setters #############
	
	@Override
	public Map<Object, Object> getDefaultSaveOptions() {
		return defaultSaveOptions;
	}


	@Override
	public Map<Object, Object> getDefaultLoadOptions() {
		return defaultLoadOptions;
	}
	
	@Override
	public Map<String, EObject> getIDToEObjectMap() {
		return intrinsicIDToEObjectMap;
	}
	
//  ############ Unsupported Load and Save operations ############
	
	@Override
	public org.w3c.dom.Document save(org.w3c.dom.Document document, Map<?, ?> options, DOMHandler handler) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	@Override
	public void save(Writer writer, Map<?, ?> options) throws IOException {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}
	
	@Override
	public void load(Node node, Map<?, ?> options) throws IOException {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	@Override
	public void delete(Map<?, ?> options) throws IOException {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");	
	}
	
//	############ Unimplemented Getters and Setters ############
	
	@Override
	public void setUseZip(boolean useZip) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	@Override
	public String getPublicId() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public String getSystemId() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public void setDoctypeInfo(String publicId, String systemId) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public String getEncoding() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public void setEncoding(String encoding) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public String getXMLVersion() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public void setXMLVersion(String version) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	@Override
	public Map<EObject, String> getEObjectToIDMap() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public String getID(EObject eObject) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public void setID(EObject eObject, String id) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public Map<EObject, AnyType> getEObjectToExtensionMap() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public DOMHelper getDOMHelper() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public String getXMIVersion() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public void setXMIVersion(String version) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public String getXMINamespace() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}


	@Override
	public void setXMINamespace(String namespace) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

}