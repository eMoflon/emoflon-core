package org.moflon.smartemf.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.DOMHandler;
import org.eclipse.emf.ecore.xmi.DOMHelper;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.DefaultDOMHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.moflon.smartemf.runtime.collections.ResourceContentSmartEList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterators;

/**
 * A simplified resource implementation that serializes to XMI. Ignores save options. Can enable notification cascading for its contents.
 */
public class SmartEMFResource extends ResourceImpl implements XMIResource, Resource.Internal {
	
	public static Resource.Factory FACTORY = new XMIResourceFactoryImpl();
	
	private HashBiMap<String, EObject> idToEObjectBiMap = HashBiMap.create();
	private Map<EObject, AnyType> eObjectToExtensionMap = new HashMap<>();
	private DefaultDOMHandlerImpl domHelper = new DefaultDOMHandlerImpl();
	private XMIRoot dummyRoot = new XMIRoot() {
		@Override
		public List<EObject> contents() {
			return getContents();
		}
	};
	private Map<Object, Object> defaultLoadOptions = new HashMap<>();
	private Map<Object, Object> defaultSaveOptions = new HashMap<>();
	
    private EList<EObject> contents = new ResourceContentSmartEList<>(this);
	
    private String xmiVersion = XMIResource.VERSION_VALUE;
    private String xmiNamespace = XMIResource.XMI_URI;
    private String xmlVersion = "1.0";
    private String encoding = "ASCII";
    private String systemId;
    private String publicId;
    private String indentation = "  ";
    private boolean useZip = false;
    private boolean createIDsOnSave = true;
    private boolean useXPathIDs = true;
    private boolean cascadeNotifications = false;
	
	public SmartEMFResource() {
		super();
	}
	
	public SmartEMFResource(URI uri) {
		super(uri);
	}
	
	@Override
	public EList<EObject> getContents() {
		return contents;
	}
	
	@Override
	public String getXMINamespace() {
		return xmiNamespace;
	}
	
	@Override 
	public String getXMIVersion() {
		return xmiVersion;
	}

	@Override 
	public void setXMINamespace(String namespace) {
		xmiNamespace = namespace;
	}
	
	@Override 
	public void setXMIVersion(String version) {
		xmiVersion = version;
	}
	
	@Override 
	public DOMHelper getDOMHelper() {
		return domHelper;
	}
	
	@Override 
	public Map<Object, Object> getDefaultLoadOptions() {
		return defaultLoadOptions;
	}
	
	@Override 
	public Map<Object, Object> getDefaultSaveOptions() {
		return defaultSaveOptions;
	}
	
	@Override 
	public Map<EObject, AnyType> getEObjectToExtensionMap() {
		return eObjectToExtensionMap;
	}
	
	@Override
	public Map<EObject, String> getEObjectToIDMap() {
		return idToEObjectBiMap.inverse();
	}
	
	@Override
	public String getEncoding() {
		return encoding;
	}
	
	@Override
	public String getID(EObject eObject) {
		return getEObjectToIDMap().get(eObject);
	}
	
	@Override
	public String getURIFragment(EObject obj) {
		return getID(obj);
	}
	
	@Override
	public EObject getEObjectByID(String id) {
		return idToEObjectBiMap.get(id);
	}
	
	@Override
	public Map<String, EObject> getIDToEObjectMap() {
		return idToEObjectBiMap;
	}
	
	@Override
	public String getPublicId() {
		return publicId;
	}
	
	@Override
	public String getSystemId() {
		return systemId;
	}
	
	@Override
	public String getXMLVersion() {
		return xmlVersion;
	}
	
	@Override
	public void load(Node node, Map<?, ?> options) throws IOException {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}
	
	@Override
	public void load(InputSource inputSource, Map<?, ?> options) throws IOException {
		if (!isLoaded) {
			Notification notification = setLoaded(true);
			isLoading = true;
			
			if (errors != null) errors.clear();
			if (warnings != null) warnings.clear();
			
			try {
				doLoad(inputSource.getByteStream(), options);
			} finally {
				isLoading = false;
				if (notification != null) 
					eNotify(notification);
				
				setModified(false);
			}
		}
	}
	
	@Override
	public void save(Writer writer, Map<?, ?> options) throws IOException {
		//This does not detect if an object is removed and a different one is added - in these situations, createIDsOnSave must be true for IDs to be correct XPaths
		if (createIDsOnSave || idToEObjectBiMap.size() != Iterators.size(getAllContents())) {
			createXPathIDs();
		}
		BufferedWriter wri = new BufferedWriter(writer);
		wri.append(generateXMI());
		wri.flush();
	}
	
	@Override
	public Document save(Document document, Map<?, ?> options, DOMHandler handler) {
		//This does not detect if an object is removed and a different one is added - in these situations, createIDsOnSave must be true for IDs to be correct XPaths
		if (createIDsOnSave || idToEObjectBiMap.size() != Iterators.size(getAllContents())) {
			createXPathIDs();
		}
		
		Document doc = null;
		try {
			doc = (document != null) ? document : DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		DOMHandler hnd = (handler!= null) ? handler : domHelper;
		
		XMISaveImpl save = new XMISaveImpl(new XMIHelperImpl(this));
		
		Map<Object, Object> opt = new HashMap<>();
		if (options != null) 
			opt.putAll(options);
		if (!opt.isEmpty()) {
			for (Entry<Object, Object> e : defaultSaveOptions.entrySet()) {
				opt.putIfAbsent(e.getKey(), e.getValue());
			}
		}		
		
		return save.save(this, doc, opt, hnd);
	}
	
	@Override
	protected void doSave(OutputStream str, Map<?, ?> options) throws IOException {
		save(new OutputStreamWriter(str), options);
	}
	
	@Override
	protected void doUnload() {
		TreeIterator<EObject> allContents = getAllProperContents(unloadingContents);
		if (!getContents().isEmpty()) {
			getContents().clear();
		}
		getErrors().clear();
		getWarnings().clear();
		while (allContents.hasNext()) {
			allContents.next().eAdapters().clear();
		}
		idToEObjectBiMap.clear();
		eObjectToExtensionMap.clear();
	}
	
	/**
	 * Generates an XMI representation of this resource's contents.
	 */
	protected CharSequence generateXMI() {
		XMIRoot root = rootObject();
		StringBuilder sb = new StringBuilder();
		
		String rootTagName;
		String rootAttributes = createRootAttributes(root);
		
		if (root == dummyRoot) {
			rootTagName = "xmi:XMI";
		} else {
			EObject eobj = null; 	
			if (root.contents().isEmpty()) {
				eobj = getContents().iterator().next();
			} else {
				eobj = root.rootObject();
			}
			rootTagName = rootName(eobj);
			rootAttributes += " " + xmiAttributes(eobj);
		}
		
		sb.append("<?xml version=\"");
		sb.append(xmlVersion);
		sb.append("\" encoding=\"");
		sb.append(encoding);
		sb.append("\"?>");
		sb.append(System.lineSeparator());
		sb.append(createDoctypeIfNecessary(rootTagName));
		sb.append("<"+rootTagName+" "+rootAttributes+">");
		
		for (EObject e : root.contents()) {
			sb.append(toXMITag(e, 1));
		}
		
		sb.append(System.lineSeparator() + "</" + rootTagName + ">" + System.lineSeparator());
		
		return sb;
	}
	
	private String createDoctypeIfNecessary(String rootTagName) {
		boolean hasPublicId = publicId != null && "".equals(publicId);
		boolean hasSystemId = systemId != null && "".equals(systemId);
		if (hasPublicId) {
			return "<!DOCTYPE " + rootTagName + " PUBLIC \"" + publicId + "\"" + ((hasSystemId) ? " \""+systemId+"\"" : "") + ">"+System.lineSeparator();
		} else if (hasSystemId) {
			return "<!DOCTYPE " + rootTagName + " SYSTEM \"" + systemId + "\">"+System.lineSeparator();
		} else 
			return "";
	}
	
	private XMIRoot rootObject() {
		if (getContents().size() == 1) {
			return new XMIRoot() {
				@Override
				public List<EObject> contents() {
					return getContents();
				}
			};
		} else {
			return dummyRoot;
		}
	}
	
	/**
	 * Creates an XMI tag from a non-root object.
	 */
	private CharSequence toXMITag(EObject object, int depth) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<depth; i++) {
			sb.append(indentation);
		}
		String indentation = sb.toString();
		sb = new StringBuilder();
		String name = tagName(object, object.eContainingFeature());
		
		List<EObject> contents = object.eContents().stream()
			.filter(x -> {
				if(x.eContainingFeature() != null) {
					return !x.eContainingFeature().isTransient();
				} else {
					return true;
				}
		}).collect(Collectors.toList());
		
		if (!contents.isEmpty()) {
			sb.append(System.lineSeparator() + indentation + "<" + name + xmiAttributes(object) + ">");
			for (EObject e : contents) {
				sb.append(toXMITag(e, depth + 1));
			}
			sb.append(System.lineSeparator() + indentation + "</" + name + ">");
		} else {
			sb.append(System.lineSeparator() + indentation + "<" + name + xmiAttributes(object) + "/>");
		}
		
		return sb;
	}
	
	/**
	 * Creates an XMI attribute from a cross reference.
	 */
	private CharSequence crossReference(EObject object, EReference ref) {
		Object target = object.eGet(ref);
		
		if(target == null)
			return "";
					
		if (target instanceof EObject) {
			return tagName(null, ref) + "=\"" + getID((EObject)target) + "\"";
		} else {
			StringBuilder sb = new StringBuilder(tagName(null, ref)+ "=\"");
			for (EObject t : (List<EObject>)target) {
				sb.append(getID(t)).append(" ");
			}
			sb.setCharAt(sb.length() - 1, '"');
			return sb;
		}
	}
	
	/**
	 * Concatenates {@code string} {@code i} times to an empty StringBuilder
	 */
	private CharSequence operator_multiply(String string, int i) {
		StringBuilder sb = new StringBuilder();
		for (var j = 0; j < i; j++) {
			sb.append(string);
		}
		return sb;
	}
	
	/**
	 * @return the tag name for the root object
	 */
	private String rootName(EObject object) {
		return object.eClass().getEPackage().getName() + ":" + object.eClass().getName();
	}
	
	private String tagName(EObject object) {
		return tagName(object, object.eContainingFeature());
	}
	
	/**
	 * @return the tag name for a non-root object
	 */
	private String tagName(EObject object, EStructuralFeature feat) {
		if (feat != null) {
			String name = feat.getName();
			char first = name.charAt(0);
			
			if(name.length()<1)
				return "";
			if(name.length()<2)
				return "" + Character.toLowerCase(first);
			
			return name = Character.toLowerCase(first) + name.substring(1, name.length());
		} else {
			return rootName(object);
		}
	}
	
	/**
	 * Creates XMI attributes from an object's attributes and cross-references
	 */
	private String xmiAttributes(EObject object) {
		EClass eClass = object.eClass();
		StringBuilder sb = new StringBuilder();
		//TODO: I think EAllReference ist not correct here, but we'll see..
		List<EReference> references = object.eClass().getEAllReferences().stream()
				.filter(x -> !x.isContainment() && !x.isContainer()).collect(Collectors.toList());
		
		for (EAttribute a : eClass.getEAttributes().stream()
				.filter(x -> !x.isTransient()).collect(Collectors.toList())) {
			if (!a.isUnsettable() || object.eIsSet(a)) {
				if (!(a instanceof EReference)) {
					sb.append(" "+ a.getName() + "=\"" + object.eGet(a) + "\"");
				}
			}
		}
		for (EReference r : references) {
			sb.append(" " + crossReference(object, r));
		}
		
		return sb.toString();
	}
	
	/**
	 * Creates the XML attributes of the root object
	 */
	private String createRootAttributes(XMIRoot root) {
		EPackage pkg = getPackage(root);
		if(pkg == null)
			return "xmi:version=\"" + xmiVersion + "\" xmlns:xmi=\"" + xmiNamespace + "\"";

		return "xmi:version=\"" + xmiVersion + "\" xmlns:xmi=\"" + xmiNamespace + "\" xmlns:" + pkg.getNsPrefix() + "=\"" + pkg.getNsURI() + "\"";
	}
	
	private EPackage getPackage(XMIRoot root) {
		if(root.contents().isEmpty())
			return null;
		return root.contents().iterator().next().eClass().getEPackage();
	}
	
	@Override
	protected void doLoad(InputStream str, Map<?, ?> options) {
		Map<Object, Object> opt = (defaultLoadOptions != null)? defaultLoadOptions : new HashMap<>();
		if (options != null) 
			opt.putAll(options);
		
		URIHandler uriHandler = (URIHandler) opt.get(OPTION_URI_HANDLER);
		URI handlerURI = null;
		if (uriHandler instanceof URIHandlerImpl) {
			handlerURI = ((URIHandlerImpl)uriHandler).getBaseURI();
		}
		
		try {
			if (str instanceof URIConverter.Loadable) {
				((URIConverter.Loadable)str).loadResource(this);
			} else {
				SmartEMFXMILoad loader = new SmartEMFXMILoad(new XMLHelperImpl(this));
				ResourceHandler handler = (ResourceHandler)opt.get(OPTION_RESOURCE_HANDLER);
				
				if (handler != null)
					handler.preLoad(this, str, opt);
				
				loader.load(this, str, opt);
				
				if (handler != null) 
					handler.postLoad(this, str, opt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (uriHandler != null) 
				uriHandler.setBaseURI(handlerURI);
		}
	}
	
	@Override 
	public void setDoctypeInfo(String publicId, String systemId) {
		this.systemId = systemId;
		this.publicId = publicId;
	}
	
	/*
	 * non-ASCII encodings may not work correctly
	 */
	@Override 
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	@Override 
	public void setID(EObject eObject, String id) {
		idToEObjectBiMap.forcePut(id, eObject);
	}
	
	@Override 
	public void setUseZip(boolean useZip) {
		this.useZip = useZip;
	}
	
	@Override 
	public boolean useZip() {
		return useZip;
	}
	
	@Override 
	public void setXMLVersion(String version) {
		xmlVersion = version;
	}
	
	/**
	 * Removes all entries from idToEObjectBiMap and replaces them with XPaths
	 */
	protected void createXPathIDs() {
		idToEObjectBiMap.clear();
		String prefix = "";
		int count = 0;
		for (EObject o : getContents()) {
			xPathForObject(o, prefix, count++, getContents().size() <= 1);
		}
	}
	
	/**
	 * Creates XPath-style IDs for EObjects. This is necessary for saving cross-references.
	 */
	private void xPathForObject(EObject obj, String prefix, int i, boolean disableTopIndex) {
		String path = ""; 
		
		if ("".equals(prefix)) {
			path = "/" + ((disableTopIndex) ? "" : i);
		} else {
			path = prefix + "/@" + tagName(obj) + "." + i;
		}
		
		setID(obj, path);
		
		int j = 0;
		List<EObject> contents = obj.eContents().stream()
				.filter(x -> x.eContainingFeature() != null && !x.eContainingFeature().isTransient())
				.collect(Collectors.toList());
		for (EObject o : contents) {
			xPathForObject(o, path, j++, false);
		}
	}
	
	/**
	 * @param useXPathIDs whether XPath-style IDs are used
	 */
	protected void setUseXPathIDs(boolean useXPathIDs) {
		this.useXPathIDs = useXPathIDs;
	}
	
	/**
	 * @return whether XPath-style IDs are used
	 */
	protected boolean getUseXPathIDs() {
		return useXPathIDs;
	}
	
	/**
	 * @param create whether IDs for cross-references should be generated when the resource is saved
	 */
	protected void createIDsOnSave(boolean create) {
		createIDsOnSave = create;
		useXPathIDs = create;
	}
	
	/**
	 * @return the indentation string
	 */
	protected String getIndentation() {
		return indentation;
	}
	
	/**
	 * @param indentation the string used to indent the generated XMI files. If null, use four spaces.
	 */
	 // This does not check whether the string only contains whitespace - maybe it should throw an exception if it does not
	protected void setIndentation(String indentation) {
		this.indentation = (indentation != null) ? indentation : "   ";
	}
	
	/**
	 * @return whether cascading of add-notifications is enabled.
	 */
	public boolean getCascade() {
		return cascadeNotifications;
	}
	
	/**
	 * Enables or disables cascaded add-notifications for the objects in this resource.
	 * @param cascade whether cascaded add-notifications should be enabled
	 */
	protected void setCascade(boolean cascade) {
		cascadeNotifications = cascade;
	}
	
	@Override
	protected EObject getEObject(List<String> uriFragmentPath) {
		int size = uriFragmentPath.size();
		EObject eObject = getEObjectForURIFragmentRootSegment(size == 0 ? "" : uriFragmentPath.get(0));
		
		for (var i = 1; i < size && eObject != null; i++) {
			eObject = eObjectForURIFragmentSegment(eObject, uriFragmentPath.get(i));
		}
		
		return eObject;
	}
	
	//adapted from org.eclipse.emf.ecore.impl.BasicEObjectImpl#eObjectForURIFragmentSegment
	private EObject eObjectForURIFragmentSegment(EObject obj, String segment) {
		if (obj instanceof InternalEObject) {
			return ((InternalEObject) obj).eObjectForURIFragmentSegment(segment);
		} else {
			int lastIndex = segment.length() - 1;
			if (lastIndex == -1 || segment.charAt(0) != '@') {
				throw new IllegalArgumentException("Expecting @ at index 0 of '" + segment + "'");
			}
			
			char lastChar = segment.charAt(lastIndex);
			if (lastChar == ']') {
				int index = segment.indexOf('[');
				if (index >= 0) {
					EReference eRef = (EReference) obj.eClass().getEStructuralFeature(segment.substring(1, index));
					String predicate = segment.substring(index + 1, lastIndex);
					return eObjectForURIPredicate(predicate, eRef);
				} else {
					throw new IllegalArgumentException("Expecting [ in '" + segment + "'");
				}
			} else {
				int dotIndex = -1;
				if (Character.isDigit(lastChar)) {
					dotIndex = segment.lastIndexOf('.', lastIndex - 1);
					if (dotIndex >= 0) {
						EList<?> eList = (EList<?>) obj.eGet(obj.eClass().getEStructuralFeature(segment.substring(1, dotIndex)));
						int pos = Integer.parseInt(segment.substring(dotIndex + 1));
						if (pos < eList.size()) {
							Object res = eList.get(pos);
							if (res instanceof FeatureMap.Entry) {
								return (EObject) ((FeatureMap.Entry)res).getValue();
							} else {
								return (EObject) res;
							}
						}
					}
				}
				if (dotIndex < 0) 
					return (EObject) obj.eGet(obj.eClass().getEStructuralFeature(segment.substring(1)));
			}
			return null;
		}
	}
	
	private EObject eObjectForURIPredicate(String pred, EReference ref) {
		//This did not get called during testing
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}
	
}