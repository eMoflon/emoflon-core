package org.moflon.smartemf.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.xmi.DOMHandler;
import org.eclipse.emf.ecore.xmi.DOMHelper;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
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
//		throw new UnsupportedOperationException("TODO: auto-generated method stub");
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
	public void save(OutputStream outputStream, Map<?, ?> options) throws IOException {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}
	
	@Override
	public void load(InputStream inputStream, Map<?, ?> options) throws IOException {
		// Useless organizational crap
		if(isLoaded)
			return;

		Notification notification = setLoaded(true);
		isLoading = true;
		
		InputStream is = null;
		
		if(useZip() || (options != null && Boolean.TRUE.equals(options.get(Resource.OPTION_ZIP)))) {
			throw new UnsupportedOperationException("Zipped input streams are not supported!");
		} else {
			is = inputStream;
		}
		
		URIConverter.Cipher cipher = (options != null) ? (URIConverter.Cipher)options.get(Resource.OPTION_CIPHER) : null;
		if(cipher != null) {
			throw new UnsupportedOperationException("Encryption through cipher is not supported!");
		}
		
		if (inputStream instanceof URIConverter.Readable) {
			throw new UnsupportedOperationException("Input as URIConverter.Readable not supported!");
		}
				
		SAXBuilder saxBuilder = new SAXBuilder();
		Document parsedFile = null;
		try {
			parsedFile = saxBuilder.build(is);
		} catch (JDOMException | IOException e) {
			throw new IOException(e.getMessage(), e.getCause());
		}
		domTreeToModel(parsedFile);
		
		// Finish useless stuff
		isLoading = false;

        if (notification != null) {
          eNotify(notification);
        }

        setModified(false);
	}
	
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
	public void load(InputSource inputSource, Map<?, ?> options) throws IOException {
		load(inputSource.getByteStream(), options);
	}


	@Override
	public void delete(Map<?, ?> options) throws IOException {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");	
	}
	
	
//	protected Map<Element, EObject> xmlElementToEObject = new HashMap<>();
	protected Map<String, List<Consumer<EObject>>> waitingCrossRefs = new HashMap<>();
	protected Map<String, EObject> id2Object = new HashMap<>();
	final public static String XMI_NS = "xmi";
	final public static String XSI_NS = "xsi";
	
	protected void domTreeToModel(final Document domTree) throws IOException {
		id2Object = new HashMap<>();
		waitingCrossRefs = new HashMap<>();
		Element root = domTree.getRootElement();
		
		// Load the corresponding metamodel and factory
		Namespace ns = root.getNamespace();
		String metamodelUri = ns.getURI();
		EPackage metamodel = EPackage.Registry.INSTANCE.getEPackage(metamodelUri);
		if(metamodel.eIsProxy()) {
			throw new IOException("No generated metamodel code found for: "+metamodelUri+", can not load model.");
		}
		EFactory factory = metamodel.getEFactoryInstance();
		
		// traverse tree and instantiate classes
		EObject eRoot = parseDomTree(root, metamodel, factory, null, "/", 0);
		contents.add(eRoot);
	}
	
	@SuppressWarnings("unchecked")
	protected EObject parseDomTree(Element root, EPackage metamodel, EFactory factory, EReference containment, String id, int idx) throws IOException {
		EClass rootClass = null;
		String currentId = id;
		if(containment == null) {
			rootClass = (EClass)metamodel.getEClassifier(root.getName());
		} else {
			Attribute typeATR = root.getAttribute("type");
			if(typeATR != null && typeATR.getNamespacePrefix().equals(XSI_NS)) {
				String[] exactType = typeATR.getValue().split(":");
				//TODO: Import foreign metamodels
				String metamodelNS = exactType[0];
				String className = exactType[1];
				rootClass = (EClass)metamodel.getEClassifier(className);
			} else {
				rootClass = containment.getEReferenceType();
			}
			currentId = id + "/@" + containment.getName() + "." + idx;
		}
		
		EObject eRoot = factory.create(rootClass);
		id2Object.put(currentId, eRoot);
		if(waitingCrossRefs.containsKey(root)) {
			waitingCrossRefs.get(root).forEach(waitingRef -> waitingRef.accept(eRoot));
		}
		Map<EStructuralFeature,Integer> element2Idx = new HashMap<>();
		for(Element element : root.getChildren()) {
			if(XMI_NS.equals(element.getNamespace().getPrefix()))
				continue;
			
			Optional<EStructuralFeature> featureOpt = rootClass.getEAllStructuralFeatures().stream().filter(sf -> sf.getName().equals(element.getName())).findFirst();
			if(!featureOpt.isPresent())
				throw new IOException("Unkown structual feature: "+element.getName());
			
			EStructuralFeature feature  = featureOpt.get();
			if(feature instanceof EAttribute)
				throw new IOException("Illegal use of EAttribute: "+element.getName());
			
			if(!element2Idx.containsKey(feature)) {
				element2Idx.put(feature, 0);
			}
			
			EReference ref = (EReference)feature;
			if(ref.isContainment()) {
				if(ref.isMany()) {
					EList<EObject> objs = (EList<EObject>) eRoot.eGet(ref);
					EObject child = parseDomTree(element, metamodel, factory, ref, currentId, element2Idx.get(feature));
					objs.add(child);
					element2Idx.replace(feature, element2Idx.get(feature)+1);
				} else {
					EObject child = parseDomTree(element, metamodel, factory, ref, currentId, element2Idx.get(feature));
					eRoot.eSet(ref, child);
					element2Idx.replace(feature, element2Idx.get(feature)+1);
				}
			} else {
//				if(ref.isMany()) {
//					EList<EObject> objs = (EList<EObject>) eRoot.eGet(ref);
//					if(xmlElementToEObject.containsKey(element)) {
//						objs.add(xmlElementToEObject.get(element));
//					} else {
//						// Remember this crossRef and wait for traversal
//						List<Consumer<EObject>> otherCrossRefs = waitingCrossRefs.get(element);
//						if(otherCrossRefs == null) {
//							otherCrossRefs = new LinkedList<>();
//							waitingCrossRefs.put(element, otherCrossRefs);
//						}
//						otherCrossRefs.add((eobj) -> {
//							objs.add(eobj);
//						});
//					}
//				} else {
//					if(xmlElementToEObject.containsKey(element)) {
//						eRoot.eSet(ref, xmlElementToEObject.get(element));
//					} else {
//						// Remember this crossRef and wait for traversal
//						List<Consumer<EObject>> otherCrossRefs = waitingCrossRefs.get(element);
//						if(otherCrossRefs == null) {
//							otherCrossRefs = new LinkedList<>();
//							waitingCrossRefs.put(element, otherCrossRefs);
//						}
//						otherCrossRefs.add((eobj) -> {
//							eRoot.eSet(ref, eobj);
//						});
//					}
//				}
				throw new IOException("XML DOM-Tree child: "+element.getName()+" is not in a containtment!");
			}
		}
		
		for(Attribute attribute : root.getAttributes()) {
			if(XMI_NS.equals(attribute.getNamespace().getPrefix()))
				continue;
			
			if(XSI_NS.equals(attribute.getNamespace().getPrefix()))
				continue;
			
			Optional<EStructuralFeature> featureOpt = rootClass.getEAllStructuralFeatures().stream().filter(sf -> sf.getName().equals(attribute.getName())).findFirst();
			if(!featureOpt.isPresent())
				throw new IOException("Unkown structual feature: "+attribute.getName());
			
			EStructuralFeature feature  = featureOpt.get();
			if(feature instanceof EReference) {
				EReference ref = (EReference)feature;
				
				if(ref.isMany()) {
					EList<EObject> objs = (EList<EObject>) eRoot.eGet(ref);
					if(id2Object.containsKey(attribute.getValue())) {
						objs.add(id2Object.get(attribute.getValue()));
					} else {
						// Remember this crossRef and wait for traversal
						List<Consumer<EObject>> otherCrossRefs = waitingCrossRefs.get(attribute.getValue());
						if(otherCrossRefs == null) {
							otherCrossRefs = new LinkedList<>();
							waitingCrossRefs.put(attribute.getValue(), otherCrossRefs);
						}
						otherCrossRefs.add((eobj) -> {
							objs.add(eobj);
						});
					}
				} else {
					if(id2Object.containsKey(attribute.getValue())) {
						eRoot.eSet(ref, id2Object.get(attribute.getValue()));
					} else {
						// Remember this crossRef and wait for traversal
						List<Consumer<EObject>> otherCrossRefs = waitingCrossRefs.get(attribute.getValue());
						if(otherCrossRefs == null) {
							otherCrossRefs = new LinkedList<>();
							waitingCrossRefs.put(attribute.getValue(), otherCrossRefs);
						}
						otherCrossRefs.add((eobj) -> {
							eRoot.eSet(ref, eobj);
						});
					}
				}
				
			} else {
				EAttribute eAttribute = (EAttribute) feature;
				
				switch(attribute.getAttributeType()) {
				case CDATA:
					eRoot.eSet(eAttribute, stringToValue(eAttribute, attribute.getValue()));
					break;
				case ENTITIES:
					throw new IOException("Unsupported XML attribute type: "+attribute.getAttributeType()+" for attribute: "+attribute.getName());
				case ENTITY:
					throw new IOException("Unsupported XML attribute type: "+attribute.getAttributeType()+" for attribute: "+attribute.getName());
				case ENUMERATION:
					throw new IOException("Unsupported XML attribute type: "+attribute.getAttributeType()+" for attribute: "+attribute.getName());
				case ID:
					throw new IOException("Unsupported XML attribute type: "+attribute.getAttributeType()+" for attribute: "+attribute.getName());
				case IDREF:
					throw new IOException("Unsupported XML attribute type: "+attribute.getAttributeType()+" for attribute: "+attribute.getName());
				case IDREFS:
					throw new IOException("Unsupported XML attribute type: "+attribute.getAttributeType()+" for attribute: "+attribute.getName());
				case NMTOKEN:
					throw new IOException("Unsupported XML attribute type: "+attribute.getAttributeType()+" for attribute: "+attribute.getName());
				case NMTOKENS:
					throw new IOException("Unsupported XML attribute type: "+attribute.getAttributeType()+" for attribute: "+attribute.getName());
				case NOTATION:
					throw new IOException("Unsupported XML attribute type: "+attribute.getAttributeType()+" for attribute: "+attribute.getName());
				case UNDECLARED:
					throw new IOException("Unsupported XML attribute type: "+attribute.getAttributeType()+" for attribute: "+attribute.getName());
				default:
					throw new IOException("Unsupported XML attribute type: "+attribute.getAttributeType()+" for attribute: "+attribute.getName());
				
				}
			}

		}
		return eRoot;
	}
	
	public static Object stringToValue(final EAttribute atr, final String value) throws IOException {
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
			throw new IOException("Unsupported attribute type: "+ atr.getEAttributeType().getName());
		} else if(atr.getEAttributeType() == epack.getEDouble()) {
			return Double.parseDouble(value);
		} else if(atr.getEAttributeType() == epack.getEEnumerator()) {
			throw new IOException("Unsupported attribute type: "+ atr.getEAttributeType().getName());
		} else if(atr.getEAttributeType() == epack.getEFloat()) {
			return Float.parseFloat(value);
		} else if(atr.getEAttributeType() == epack.getEInt()) {
			return Integer.parseInt(value);
		} else if(atr.getEAttributeType() == epack.getELong()) {
			return Long.parseLong(value);
		} else if(atr.getEAttributeType() == epack.getEShort()) {
			return Short.parseShort(value);
		} else {
			throw new IOException("Unsupported attribute type: "+ atr.getEAttributeType().getName());
		}
	}

//	############ Tedious stuff :( ############
	
	@Override
	public void setUseZip(boolean useZip) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	@Override
	public Map<Object, Object> getDefaultSaveOptions() {
		return defaultSaveOptions;
	}


	@Override
	public Map<Object, Object> getDefaultLoadOptions() {
		return defaultLoadOptions;
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
	public Map<String, EObject> getIDToEObjectMap() {
		return intrinsicIDToEObjectMap;
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


//	public static Resource.Factory FACTORY = new XMIResourceFactoryImpl();
//
//	private HashBiMap<String, EObject> idToEObjectBiMap = HashBiMap.create();
//	private Map<EObject, AnyType> eObjectToExtensionMap = new HashMap<>();
//	private DefaultDOMHandlerImpl domHelper = new DefaultDOMHandlerImpl();
//	private XMIRoot dummyRoot = new XMIRoot() {
//		@Override
//		public List<EObject> contents() {
//			return getContents();
//		}
//	};
//	private Map<Object, Object> defaultLoadOptions = new HashMap<>();
//	private Map<Object, Object> defaultSaveOptions = new HashMap<>();
//
//	private EList<EObject> contents = new ResourceContentSmartEList<>(this);
//
//	private String xmiVersion = XMIResource.VERSION_VALUE;
//	private String xmiNamespace = XMIResource.XMI_URI;
//	private String xmlVersion = "1.0";
//	private String encoding = "ASCII";
//	private String systemId;
//	private String publicId;
//	private String indentation = "  ";
//	private boolean useZip = false;
//	private boolean createIDsOnSave = true;
//	private boolean useXPathIDs = true;
//	private boolean cascadeNotifications = false;
//
//	public SmartEMFResource() {
//		super();
//	}
//
//	public SmartEMFResource(URI uri) {
//		super(uri);
//	}
//
//	@Override
//	public EList<EObject> getContents() {
//		return contents;
//	}
//
//	@Override
//	public String getXMINamespace() {
//		return xmiNamespace;
//	}
//
//	@Override
//	public String getXMIVersion() {
//		return xmiVersion;
//	}
//
//	@Override
//	public void setXMINamespace(String namespace) {
//		xmiNamespace = namespace;
//	}
//
//	@Override
//	public void setXMIVersion(String version) {
//		xmiVersion = version;
//	}
//
//	@Override
//	public DOMHelper getDOMHelper() {
//		return domHelper;
//	}
//
//	@Override
//	public Map<Object, Object> getDefaultLoadOptions() {
//		return defaultLoadOptions;
//	}
//
//	@Override
//	public Map<Object, Object> getDefaultSaveOptions() {
//		return defaultSaveOptions;
//	}
//
//	@Override
//	public Map<EObject, AnyType> getEObjectToExtensionMap() {
//		return eObjectToExtensionMap;
//	}
//
//	@Override
//	public Map<EObject, String> getEObjectToIDMap() {
//		return idToEObjectBiMap.inverse();
//	}
//
//	@Override
//	public String getEncoding() {
//		return encoding;
//	}
//
//	@Override
//	public String getID(EObject eObject) {
//		return getEObjectToIDMap().get(eObject);
//	}
//
//	@Override
//	public String getURIFragment(EObject obj) {
//		return getID(obj);
//	}
//
//	@Override
//	public EObject getEObjectByID(String id) {
//		return idToEObjectBiMap.get(id);
//	}
//
//	@Override
//	public Map<String, EObject> getIDToEObjectMap() {
//		return idToEObjectBiMap;
//	}
//
//	@Override
//	public String getPublicId() {
//		return publicId;
//	}
//
//	@Override
//	public String getSystemId() {
//		return systemId;
//	}
//
//	@Override
//	public String getXMLVersion() {
//		return xmlVersion;
//	}
//
//	@Override
//	public void load(Node node, Map<?, ?> options) throws IOException {
//		throw new UnsupportedOperationException("TODO: auto-generated method stub");
//	}
//
//	@Override
//	public void load(InputSource inputSource, Map<?, ?> options) throws IOException {
//		if (!isLoaded) {
//			Notification notification = setLoaded(true);
//			isLoading = true;
//
//			if (errors != null)
//				errors.clear();
//			if (warnings != null)
//				warnings.clear();
//
//			try {
//				doLoad(inputSource.getByteStream(), options);
//			} finally {
//				isLoading = false;
//				if (notification != null)
//					eNotify(notification);
//
//				setModified(false);
//			}
//		}
//	}
//
//	@Override
//	public void save(Writer writer, Map<?, ?> options) throws IOException {
//		// This does not detect if an object is removed and a different one is added - in these situations,
//		// createIDsOnSave must be true for IDs to be correct XPaths
//		if (createIDsOnSave || idToEObjectBiMap.size() != Iterators.size(getAllContents())) {
//			createXPathIDs();
//		}
//		BufferedWriter wri = new BufferedWriter(writer);
//		wri.append(generateXMI());
//		wri.flush();
//	}
//
//	@Override
//	public Document save(Document document, Map<?, ?> options, DOMHandler handler) {
//		// This does not detect if an object is removed and a different one is added - in these situations,
//		// createIDsOnSave must be true for IDs to be correct XPaths
//		if (createIDsOnSave || idToEObjectBiMap.size() != Iterators.size(getAllContents())) {
//			createXPathIDs();
//		}
//
//		Document doc = null;
//		try {
//			doc = (document != null) ? document : DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
//		} catch (ParserConfigurationException e1) {
//			e1.printStackTrace();
//		}
//		DOMHandler hnd = (handler != null) ? handler : domHelper;
//
//		XMISaveImpl save = new XMISaveImpl(new XMIHelperImpl(this));
//
//		Map<Object, Object> opt = new HashMap<>();
//		if (options != null)
//			opt.putAll(options);
//		if (!opt.isEmpty()) {
//			for (Entry<Object, Object> e : defaultSaveOptions.entrySet()) {
//				opt.putIfAbsent(e.getKey(), e.getValue());
//			}
//		}
//
//		return save.save(this, doc, opt, hnd);
//	}
//
//	@Override
//	protected void doSave(OutputStream str, Map<?, ?> options) throws IOException {
//		save(new OutputStreamWriter(str), options);
//	}
//
//	@Override
//	protected void doUnload() {
//		TreeIterator<EObject> allContents = getAllProperContents(unloadingContents);
//		if (!getContents().isEmpty()) {
//			getContents().clear();
//		}
//		getErrors().clear();
//		getWarnings().clear();
//		while (allContents.hasNext()) {
//			allContents.next().eAdapters().clear();
//		}
//		idToEObjectBiMap.clear();
//		eObjectToExtensionMap.clear();
//	}
//
//	/**
//	 * Generates an XMI representation of this resource's contents.
//	 */
//	protected CharSequence generateXMI() {
//		XMIRoot root = rootObject();
//		StringBuilder sb = new StringBuilder();
//
//		String rootTagName;
//		String rootAttributes = createRootAttributes(root);
//
//		if (root == dummyRoot) {
//			rootTagName = "xmi:XMI";
//		} else {
//			EObject eobj = null;
//			if (root.contents().isEmpty()) {
//				eobj = getContents().iterator().next();
//			} else {
//				eobj = root.rootObject();
//			}
//			rootTagName = rootName(eobj);
//			rootAttributes += " " + xmiAttributes(eobj);
//		}
//
//		sb.append("<?xml version=\"");
//		sb.append(xmlVersion);
//		sb.append("\" encoding=\"");
//		sb.append(encoding);
//		sb.append("\"?>");
//		sb.append(System.lineSeparator());
//		sb.append(createDoctypeIfNecessary(rootTagName));
//		sb.append("<" + rootTagName + " " + rootAttributes + ">");
//
//		for (EObject e : root.contents()) {
//			sb.append(toXMITag(e, 1));
//		}
//
//		sb.append(System.lineSeparator() + "</" + rootTagName + ">" + System.lineSeparator());
//
//		return sb;
//	}
//
//	private String createDoctypeIfNecessary(String rootTagName) {
//		boolean hasPublicId = publicId != null && "".equals(publicId);
//		boolean hasSystemId = systemId != null && "".equals(systemId);
//		if (hasPublicId) {
//			return "<!DOCTYPE " + rootTagName + " PUBLIC \"" + publicId + "\"" + ((hasSystemId) ? " \"" + systemId + "\"" : "") + ">"
//					+ System.lineSeparator();
//		} else if (hasSystemId) {
//			return "<!DOCTYPE " + rootTagName + " SYSTEM \"" + systemId + "\">" + System.lineSeparator();
//		} else
//			return "";
//	}
//
//	private XMIRoot rootObject() {
//		if (getContents().size() == 1) {
//			return new XMIRoot() {
//				@Override
//				public List<EObject> contents() {
//					return getContents();
//				}
//			};
//		} else {
//			return dummyRoot;
//		}
//	}
//
//	/**
//	 * Creates an XMI tag from a non-root object.
//	 */
//	private CharSequence toXMITag(EObject object, int depth) {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < depth; i++) {
//			sb.append(indentation);
//		}
//		String indentation = sb.toString();
//		sb = new StringBuilder();
//		String name = tagName(object, object.eContainingFeature());
//
//		List<EObject> contents = object.eContents().stream().filter(x -> {
//			if (x.eContainingFeature() != null) {
//				return !x.eContainingFeature().isTransient();
//			} else {
//				return true;
//			}
//		}).collect(Collectors.toList());
//
//		if (!contents.isEmpty()) {
//			sb.append(System.lineSeparator() + indentation + "<" + name + xmiAttributes(object) + ">");
//			for (EObject e : contents) {
//				sb.append(toXMITag(e, depth + 1));
//			}
//			sb.append(System.lineSeparator() + indentation + "</" + name + ">");
//		} else {
//			sb.append(System.lineSeparator() + indentation + "<" + name + xmiAttributes(object) + "/>");
//		}
//
//		return sb;
//	}
//
//	/**
//	 * Creates an XMI attribute from a cross reference.
//	 */
//	@SuppressWarnings("unchecked")
//	private CharSequence crossReference(EObject object, EReference ref) {
//		Object target = object.eGet(ref);
//
//		if (target == null)
//			return "";
//
//		if (target instanceof EObject) {
//			return tagName(null, ref) + "=\"" + getID((EObject) target) + "\"";
//		} else {
//			StringBuilder sb = new StringBuilder(tagName(null, ref) + "=\"");
//			for (EObject t : (List<EObject>) target) {
//				sb.append(getID(t)).append(" ");
//			}
//			sb.setCharAt(sb.length() - 1, '"');
//			return sb;
//		}
//	}
//
//	/**
//	 * Concatenates {@code string} {@code i} times to an empty StringBuilder
//	 */
//	private CharSequence operator_multiply(String string, int i) {
//		StringBuilder sb = new StringBuilder();
//		for (var j = 0; j < i; j++) {
//			sb.append(string);
//		}
//		return sb;
//	}
//
//	/**
//	 * @return the tag name for the root object
//	 */
//	private String rootName(EObject object) {
//		return object.eClass().getEPackage().getName() + ":" + object.eClass().getName();
//	}
//
//	private String tagName(EObject object) {
//		return tagName(object, object.eContainingFeature());
//	}
//
//	/**
//	 * @return the tag name for a non-root object
//	 */
//	private String tagName(EObject object, EStructuralFeature feat) {
//		if (feat != null) {
//			String name = feat.getName();
//			char first = name.charAt(0);
//
//			if (name.length() < 1)
//				return "";
//			if (name.length() < 2)
//				return "" + Character.toLowerCase(first);
//
//			return name = Character.toLowerCase(first) + name.substring(1, name.length());
//		} else {
//			return rootName(object);
//		}
//	}
//
//	/**
//	 * Creates XMI attributes from an object's attributes and cross-references
//	 */
//	private String xmiAttributes(EObject object) {
//		EClass eClass = object.eClass();
//		StringBuilder sb = new StringBuilder();
//		// TODO: I think EAllReference ist not correct here, but we'll see..
//		List<EReference> references = object.eClass().getEAllReferences().stream().filter(x -> !x.isContainment() && !x.isContainer())
//				.collect(Collectors.toList());
//
//		for (EAttribute a : eClass.getEAttributes().stream().filter(x -> !x.isTransient()).collect(Collectors.toList())) {
//			if (!a.isUnsettable() || object.eIsSet(a)) {
//				if (!(a instanceof EReference)) {
//					sb.append(" " + a.getName() + "=\"" + object.eGet(a) + "\"");
//				}
//			}
//		}
//		for (EReference r : references) {
//			sb.append(" " + crossReference(object, r));
//		}
//
//		return sb.toString();
//	}
//
//	/**
//	 * Creates the XML attributes of the root object
//	 */
//	private String createRootAttributes(XMIRoot root) {
//		EPackage pkg = getPackage(root);
//		if (pkg == null)
//			return "xmi:version=\"" + xmiVersion + "\" xmlns:xmi=\"" + xmiNamespace + "\"";
//
//		return "xmi:version=\"" + xmiVersion + "\" xmlns:xmi=\"" + xmiNamespace + "\" xmlns:" + pkg.getNsPrefix() + "=\"" + pkg.getNsURI() + "\"";
//	}
//
//	private EPackage getPackage(XMIRoot root) {
//		if (root.contents().isEmpty())
//			return null;
//		return root.contents().iterator().next().eClass().getEPackage();
//	}
//
//	@Override
//	protected void doLoad(InputStream str, Map<?, ?> options) {
//		Map<Object, Object> opt = (defaultLoadOptions != null) ? defaultLoadOptions : new HashMap<>();
//		if (options != null)
//			opt.putAll(options);
//
//		URIHandler uriHandler = (URIHandler) opt.get(OPTION_URI_HANDLER);
//		URI handlerURI = null;
//		if (uriHandler instanceof URIHandlerImpl) {
//			handlerURI = ((URIHandlerImpl) uriHandler).getBaseURI();
//		}
//
//		try {
//			if (str instanceof URIConverter.Loadable) {
//				((URIConverter.Loadable) str).loadResource(this);
//			} else {
//				SmartEMFXMILoad loader = new SmartEMFXMILoad(new XMLHelperImpl(this));
//				ResourceHandler handler = (ResourceHandler) opt.get(OPTION_RESOURCE_HANDLER);
//
//				if (handler != null)
//					handler.preLoad(this, str, opt);
//
//				loader.load(this, str, opt);
//
//				if (handler != null)
//					handler.postLoad(this, str, opt);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (uriHandler != null)
//				uriHandler.setBaseURI(handlerURI);
//		}
//	}
//
//	@Override
//	public void setDoctypeInfo(String publicId, String systemId) {
//		this.systemId = systemId;
//		this.publicId = publicId;
//	}
//
//	/*
//	 * non-ASCII encodings may not work correctly
//	 */
//	@Override
//	public void setEncoding(String encoding) {
//		this.encoding = encoding;
//	}
//
//	@Override
//	public void setID(EObject eObject, String id) {
//		idToEObjectBiMap.forcePut(id, eObject);
//	}
//
//	@Override
//	public void setUseZip(boolean useZip) {
//		this.useZip = useZip;
//	}
//
//	@Override
//	public boolean useZip() {
//		return useZip;
//	}
//
//	@Override
//	public void setXMLVersion(String version) {
//		xmlVersion = version;
//	}
//
//	/**
//	 * Removes all entries from idToEObjectBiMap and replaces them with XPaths
//	 */
//	protected void createXPathIDs() {
//		idToEObjectBiMap.clear();
//		String prefix = "";
//		int count = 0;
//		for (EObject o : getContents()) {
//			xPathForObject(o, prefix, count++, getContents().size() <= 1);
//		}
//	}
//
//	/**
//	 * Creates XPath-style IDs for EObjects. This is necessary for saving cross-references.
//	 */
//	private void xPathForObject(EObject obj, String prefix, int i, boolean disableTopIndex) {
//		String path = "";
//
//		if ("".equals(prefix)) {
//			path = "/" + ((disableTopIndex) ? "" : i);
//		} else {
//			path = prefix + "/@" + tagName(obj) + "." + i;
//		}
//
//		setID(obj, path);
//
//		int j = 0;
//		List<EObject> contents = obj.eContents().stream().filter(x -> x.eContainingFeature() != null && !x.eContainingFeature().isTransient())
//				.collect(Collectors.toList());
//		for (EObject o : contents) {
//			xPathForObject(o, path, j++, false);
//		}
//	}
//
//	/**
//	 * @param useXPathIDs whether XPath-style IDs are used
//	 */
//	protected void setUseXPathIDs(boolean useXPathIDs) {
//		this.useXPathIDs = useXPathIDs;
//	}
//
//	/**
//	 * @return whether XPath-style IDs are used
//	 */
//	protected boolean getUseXPathIDs() {
//		return useXPathIDs;
//	}
//
//	/**
//	 * @param create whether IDs for cross-references should be generated when the resource is saved
//	 */
//	protected void createIDsOnSave(boolean create) {
//		createIDsOnSave = create;
//		useXPathIDs = create;
//	}
//
//	/**
//	 * @return the indentation string
//	 */
//	protected String getIndentation() {
//		return indentation;
//	}
//
//	/**
//	 * @param indentation the string used to indent the generated XMI files. If null, use four spaces.
//	 */
//	// This does not check whether the string only contains whitespace - maybe it should throw an
//	// exception if it does not
//	protected void setIndentation(String indentation) {
//		this.indentation = (indentation != null) ? indentation : "   ";
//	}
//
//	/**
//	 * @return whether cascading of add-notifications is enabled.
//	 */
//	public boolean getCascade() {
//		return cascadeNotifications;
//	}
//
//	/**
//	 * Enables or disables cascaded add-notifications for the objects in this resource.
//	 * 
//	 * @param cascade whether cascaded add-notifications should be enabled
//	 */
//	protected void setCascade(boolean cascade) {
//		cascadeNotifications = cascade;
//	}
//
//	@Override
//	protected EObject getEObject(List<String> uriFragmentPath) {
//		int size = uriFragmentPath.size();
//		EObject eObject = getEObjectForURIFragmentRootSegment(size == 0 ? "" : uriFragmentPath.get(0));
//
//		for (var i = 1; i < size && eObject != null; i++) {
//			eObject = eObjectForURIFragmentSegment(eObject, uriFragmentPath.get(i));
//		}
//
//		return eObject;
//	}
//
//	// adapted from org.eclipse.emf.ecore.impl.BasicEObjectImpl#eObjectForURIFragmentSegment
//	private EObject eObjectForURIFragmentSegment(EObject obj, String segment) {
//		if (!(obj instanceof SmartObject)) {
//			return ((InternalEObject) obj).eObjectForURIFragmentSegment(segment);
//		} else {
//			int lastIndex = segment.length() - 1;
//			if (lastIndex == -1 || segment.charAt(0) != '@') {
//				throw new IllegalArgumentException("Expecting @ at index 0 of '" + segment + "'");
//			}
//
//			char lastChar = segment.charAt(lastIndex);
//			if (lastChar == ']') {
//				int index = segment.indexOf('[');
//				if (index >= 0) {
//					EReference eRef = (EReference) obj.eClass().getEStructuralFeature(segment.substring(1, index));
//					String predicate = segment.substring(index + 1, lastIndex);
//					return eObjectForURIPredicate(predicate, eRef);
//				} else {
//					throw new IllegalArgumentException("Expecting [ in '" + segment + "'");
//				}
//			} else {
//				int dotIndex = -1;
//				if (Character.isDigit(lastChar)) {
//					dotIndex = segment.lastIndexOf('.', lastIndex - 1);
//					if (dotIndex >= 0) {
//						EStructuralFeature feature = obj.eClass().getEStructuralFeature(segment.substring(1, dotIndex));
//						EList<?> eList = (EList<?>) obj.eGet(feature);
//						int pos = Integer.parseInt(segment.substring(dotIndex + 1));
//						if (pos < eList.size()) {
//							Object res;
//							// we need to get sure to retrieve the element at the correct position
//							// -> this is only granted for SmartEList
//							if (eList instanceof SmartEList)
//								res = eList.get(pos);
//							else
//								res = getEObjectFromIndexMap(obj, feature, pos);
//							if (res instanceof FeatureMap.Entry) {
//								return (EObject) ((FeatureMap.Entry) res).getValue();
//							} else {
//								return (EObject) res;
//							}
//						}
//					}
//				}
//				if (dotIndex < 0)
//					return (EObject) obj.eGet(obj.eClass().getEStructuralFeature(segment.substring(1)));
//			}
//			return null;
//		}
//	}
//
//	private EObject eObjectForURIPredicate(String pred, EReference ref) {
//		// This did not get called during testing
//		throw new UnsupportedOperationException("TODO: auto-generated method stub");
//	}
//
//	private HashMap<IndexKey, List<EObject>> indexMapForProxyResolution = new HashMap<>();
//
//	private class IndexKey {
//
//		private final EObject sourceObject;
//		private final EStructuralFeature feature;
//
//		private IndexKey(EObject sourceObject, EStructuralFeature feature) {
//			this.sourceObject = sourceObject;
//			this.feature = feature;
//		}
//
//		@Override
//		public int hashCode() {
//			return Objects.hash(this.sourceObject, this.feature);
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (obj == null)
//				return false;
//			if (!(obj instanceof IndexKey))
//				return false;
//			IndexKey other = (IndexKey) obj;
//			return Objects.equals(this.sourceObject, other.sourceObject) && Objects.equals(this.feature, other.feature);
//		}
//
//	}
//
//	private EObject getEObjectFromIndexMap(EObject sourceObject, EStructuralFeature feature, int position) {
//		IndexKey key = new IndexKey(sourceObject, feature);
//		List<EObject> list = indexMapForProxyResolution.get(key);
//		if (list == null || position >= list.size())
//			throw new NoSuchElementException("Cannot find object in index map!");
//		return list.get(position);
//	}
//
//	public void registerIndexMapForProxyResolutionEntry(EObject sourceObject, EStructuralFeature feature, EObject object) {
//		IndexKey key = new IndexKey(sourceObject, feature);
//		indexMapForProxyResolution.computeIfAbsent(key, k -> new LinkedList<>()).add(object);
//	}
//
//	public void clearIndexMapForProxyResolution() {
//		indexMapForProxyResolution.clear();
//	}

}