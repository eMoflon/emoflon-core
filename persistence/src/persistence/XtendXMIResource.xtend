package persistence

import com.google.common.collect.HashBiMap
import com.google.common.collect.Iterators
import emfcodegenerator.util.SmartObject
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.util.HashMap
import java.util.List
import java.util.Map
import javax.xml.parsers.DocumentBuilderFactory
import org.eclipse.emf.common.notify.NotificationChain
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.InternalEObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.URIConverter
import org.eclipse.emf.ecore.resource.impl.ResourceImpl
import org.eclipse.emf.ecore.resource.impl.ResourceImpl.ContentsEList
import org.eclipse.emf.ecore.util.FeatureMap
import org.eclipse.emf.ecore.xmi.DOMHandler
import org.eclipse.emf.ecore.xmi.XMIResource
import org.eclipse.emf.ecore.xmi.impl.DefaultDOMHandlerImpl
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl
import org.eclipse.emf.ecore.xml.type.AnyType
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xml.sax.InputSource

/**
 * A simplified resource implementation that serializes to XMI. Ignores save options. Can enable notification cascading for its contents.
 */
class XtendXMIResource extends ResourceImpl implements XMIResource {
	
	public static val Resource.Factory FACTORY = [uri | new XtendXMIResource(uri)]
	
	val HashBiMap<String, EObject> idToEObjectBiMap = HashBiMap.create
	val HashMap<EObject, AnyType> eObjectToExtensionMap = new HashMap
	val domHelper = new DefaultDOMHandlerImpl
	val XMIRoot dummyRoot = [getContents]
	val defaultLoadOptions = new HashMap
	val defaultSaveOptions = new HashMap
	
    val EList<EObject> contents = new ContentsEList<EObject>(){
    	override inverseAdd(EObject object, NotificationChain chain) {
    		if (object instanceof InternalEObject) {
    			super.inverseAdd(object, chain)
    		} else if (object instanceof SmartObject) {
    			val notifications = object.eSetResource(XtendXMIResource.this, chain)
    			attached(object)
    			notifications
    		} else throw new UnsupportedOperationException("Unknown EObject implementation")
    	}
    	
    	override inverseRemove (EObject object, NotificationChain chain) {
    		if (object instanceof InternalEObject) {
    			super.inverseRemove(object, chain)
    		} else if (object instanceof SmartObject) {
    			if (isLoaded || unloadingContents !== null) {
    				detached(object)
    			}
    			object.eSetResource(null, chain)
    		} else throw new UnsupportedOperationException("Unknown EObject implementation")
    	}
    	
    	override contains(Object o) {
    		if (size <= 4 || o instanceof InternalEObject) {
    			super.contains(o)
    		} else if (o instanceof SmartObject) {
    			o.eDirectResource == this
    		} else {
    		  data.contains(o)
    		}
    	}
    }
	
	var String xmiVersion = XMIResource.VERSION_VALUE
	var String xmiNamespace = XMIResource.XMI_URI
	var String xmlVersion = "1.0"
	var String encoding = "ASCII"
	var String systemId
	var String publicId
	var String indentation = "  "
	var useZip = false
	var createIDsOnSave = true
	var useXPathIDs = true
	var cascadeNotifications = false
	
	new() {
		super()
	}
	
	new(URI uri) {
		super(uri)
	}
	
	override getContents() {
		contents
	}
	
	override getXMINamespace() {
		xmiNamespace
	}
	
	override getXMIVersion() {
		xmiVersion
	}
	
	override setXMINamespace(String namespace) {
		xmiNamespace = namespace
	}
	
	override setXMIVersion(String version) {
		xmiVersion = version
	}
	
	override getDOMHelper() {
		domHelper
	}
	
	override getDefaultLoadOptions() {
		defaultLoadOptions
	}
	
	override getDefaultSaveOptions() {
		defaultSaveOptions
	}
	
	override getEObjectToExtensionMap() {
		eObjectToExtensionMap
	}
	
	override getEObjectToIDMap() {
		idToEObjectBiMap.inverse
	}
	
	override getEncoding() {
		encoding
	}
	
	override getID(EObject eObject) {
		EObjectToIDMap.get(eObject)
	}
	
	override getURIFragment(EObject obj) {
		getID(obj)
	}
	
	override getEObjectByID(String id) {
		idToEObjectBiMap.get(id)
	}
	
	override getIDToEObjectMap() {
		idToEObjectBiMap
	}
	
	override getPublicId() {
		publicId
	}
	
	override getSystemId() {
		systemId
	}
	
	override getXMLVersion() {
		xmlVersion
	}
	
	override load(Node node, Map<?, ?> options) throws IOException {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	override load(InputSource inputSource, Map<?, ?> options) throws IOException {
		if (!isLoaded) {
			val notification = setLoaded(true)
			isLoading = true
			
			if (errors !== null) errors.clear
			if (warnings !== null) warnings.clear
			
			try {
				doLoad(inputSource.getByteStream, options)
			} finally {
				isLoading = false
				if (notification !== null) eNotify(notification)
				setModified(false)
			}
		}
	}
	
	override save(Writer writer, Map<?, ?> options) throws IOException {
		//This does not detect if an object is removed and a different one is added - in these situations, createIDsOnSave must be true for IDs to be correct XPaths
		if (createIDsOnSave || idToEObjectBiMap.size != Iterators.size(getAllContents)) {
			createXPathIDs
		}
		val wri = new BufferedWriter(writer)
		wri.append(generateXMI)
		wri.flush
	}
	
	override save(Document document, Map<?, ?> options, DOMHandler handler) {
		//This does not detect if an object is removed and a different one is added - in these situations, createIDsOnSave must be true for IDs to be correct XPaths
		if (createIDsOnSave || idToEObjectBiMap.size != Iterators.size(getAllContents)) {
			createXPathIDs
		}
		
		val doc = document ?: DocumentBuilderFactory.newInstance.newDocumentBuilder.newDocument
		val hnd = handler ?: domHelper
		val save = new XMISaveImpl(new XMIHelperImpl(this))
		
		var opt = new HashMap
		if (options !== null) opt.putAll(options)
		if (!opt.isEmpty) {
			for (e : defaultSaveOptions.entrySet) {
				opt.putIfAbsent(e.key, e.value)
			}
		}		
		
		save.save(this, doc, opt, hnd)
	}
	
	protected override doSave(OutputStream str, Map<?, ?> options) {
		save(new OutputStreamWriter(str), options)
	}
	
	protected override doUnload() {
		val allContents = getAllProperContents(unloadingContents);
		if (!getContents.isEmpty) {
			getContents.clear
		}
		getErrors.clear
		getWarnings.clear
		while (allContents.hasNext) {
			allContents.next.eAdapters.clear
		}
		idToEObjectBiMap.clear
		eObjectToExtensionMap.clear
	}
	
	/**
	 * Generates an XMI representation of this reslource's contents.
	 */
	def CharSequence generateXMI() {
		val root = rootObject()
		val sb = new StringBuilder()
		
		var String rootTagName
		var String rootAttributes = createRootAttributes(root)
		
		if (root == dummyRoot) {
			rootTagName = "xmi:XMI"
		} else {
			val eobj = if (root.contents.isEmpty) {
				getContents.iterator.next
			} else {
				root.rootObject
			}
			rootTagName = rootName(eobj)
			rootAttributes += " " + xmiAttributes(eobj)
		}
		
		sb.append('''<?xml version="«xmlVersion»" encoding="«encoding»"?>«System.lineSeparator»''')
		sb.append(createDoctypeIfNecessary(rootTagName))
		sb.append('''<«rootTagName» «rootAttributes»>''')
		
		for (e : root.contents) {
			sb.append(toXMITag(e, 1))
		}
		
		sb.append('''«System.lineSeparator»</«rootTagName»>«System.lineSeparator»''')
		
		return sb
	}
	
	private def createDoctypeIfNecessary(String rootTagName) {
		val hasPublicId = publicId !== null && publicId != ""
		val hasSystemId = systemId !== null && systemId != ""
		if (hasPublicId) {
			'''<!DOCTYPE «rootTagName» PUBLIC "«publicId»"«if (hasSystemId) ''' "«systemId»"''' else ""»>«System.lineSeparator»'''
		} else if (hasSystemId) {
			'''<!DOCTYPE «rootTagName» SYSTEM "«systemId»">«System.lineSeparator»'''
		} else ""
	}
	
	private def XMIRoot rootObject() {
		if (getContents.size == 1) {
			[getContents.iterator.next.eContents]
		} else {
			dummyRoot
		}
	}
	
	private def CharSequence toXMITag(EObject object, int depth) {
		val indentation = this.indentation * depth
		val sb = new StringBuilder()
		val name = tagName(object, object.eContainingFeature)
		
		val contents = object.eContents.filter[x | if (x.eContainingFeature !== null) !x.eContainingFeature.isTransient else true]
		
		if (!contents.isEmpty) {
			sb.append('''«System.lineSeparator»«indentation»<«name»«xmiAttributes(object)»>''')
			for (e : contents) {
				sb.append(toXMITag(e, depth + 1))
			}
			sb.append('''«System.lineSeparator»«indentation»</«name»>''')
		} else {
			sb.append('''«System.lineSeparator»«indentation»<«name»«xmiAttributes(object)»/>''')
		}
		
		return sb
	}
	
	private def crossReference(EObject object, EReference ref) {
		val target = object.eGet(ref)
		if (target instanceof EObject) {
			'''«tagName(null, ref)»="«getID(target)»"'''
		} else  if (target instanceof List) {
			val sb = new StringBuilder('''«tagName(null, ref)»="''')
			for (t : target as List<EObject>) {
				sb.append(getID(t)).append(" ")
			}
			sb.setCharAt(sb.length - 1, '"')
			return sb
		}
	}
	
	private def operator_multiply(String string, int i) {
		val sb = new StringBuilder()
		for (var j = 0; j < i; j++) sb.append(string);
		return sb
	}
	
	private def String rootName(EObject object) {
		object.eClass.getEPackage.getName + ":" + object.eClass.getName
	}
	
	private def tagName(EObject object) {
		tagName(object, object.eContainingFeature)
	}
	
	private def String tagName(EObject object, EStructuralFeature feat) {
		if (feat !== null) {
			var String name = feat.getName()
			val first = name.charAt(0)
			name.replaceFirst(first.toString, Character.toLowerCase(first).toString)
		} else {
			rootName(object)
		}
	}
	
	private def String xmiAttributes(EObject object) {
		val eClass = object.eClass
		val sb = new StringBuilder
		val references = object.eClass.getEAllReferences.filter[x | !x.isContainment && !x.isContainer]
		
		for (a : eClass.getEAttributes.filter[x | !x.isTransient]) {
			if (!a.isUnsettable || object.eIsSet(a)) {
				if (!(a instanceof EReference)) {
					sb.append(''' «a.getName»="«object.eGet(a)»"''')
				}
			}
		}
		for (r : references) {
			sb.append(''' «crossReference(object, r)»''')
		}
		
		sb.toString
	}
	
	private def String createRootAttributes(XMIRoot root) {
		val pkg = getPackage(root)
		'''xmi:version="«xmiVersion»" xmlns:xmi="«xmiNamespace»" xmlns:«pkg.getNsPrefix»="«pkg.getNsURI»"'''
	}
	
	private def EPackage getPackage(XMIRoot root) {
		root.contents.iterator.next.eClass.getEPackage
	}
	
	protected override doLoad(InputStream str, Map<?, ?> options) {
		val opt = defaultLoadOptions ?: new HashMap
		if (options !== null) opt.putAll(options)
		
		val uriHandler = opt.get(OPTION_URI_HANDLER) as URIHandler
		val handlerURI = if (uriHandler instanceof URIHandlerImpl) {
			uriHandler.getBaseURI
		} else null
		try {
			if (str instanceof URIConverter.Loadable) {
				str.loadResource(this)
			} else {
				val loader = new SmartEMFXMILoad(new XMLHelperImpl(this))
				val handler = opt.get(OPTION_RESOURCE_HANDLER) as ResourceHandler
				
				if (handler !== null) handler.preLoad(this, str, opt)
				
				loader.load(this, str, opt)
				
				if (handler !== null) handler.postLoad(this, str, opt)
			}
		} finally {
			if (uriHandler !== null) uriHandler.setBaseURI(handlerURI)
		}
	}
	
	override setDoctypeInfo(String publicId, String systemId) {
		this.systemId = systemId
		this.publicId = publicId
	}
	
	override setEncoding(String encoding) {
		this.encoding = encoding
	}
	
	override setID(EObject eObject, String id) {
		idToEObjectBiMap.forcePut(id, eObject)
	}
	
	override setUseZip(boolean useZip) {
		this.useZip = useZip
	}
	
	override useZip() {
		useZip
	}
	
	override setXMLVersion(String version) {
		xmlVersion = version
	}
	
	/**
	 * Removes all entries from idToEObjectBiMap and replaces them with XPaths
	 */
	def createXPathIDs() {
		idToEObjectBiMap.clear
		val prefix = ""
		var count = 0
		for (o : getContents) {
			xPathForObject(o, prefix, count++, getContents().size() <= 1)
		}
	}
	
	/**
	 * Creates XPath-style IDs for EObjects. This is necessary for saving cross-references.
	 */
	private def void xPathForObject(EObject obj, String prefix, int i, boolean disableTopIndex) {
		val path = if (prefix == "") {
			"/" + if (disableTopIndex) "" else i
		} else {
			'''«prefix»/@«tagName(obj)».«i»'''
		}
		setID(obj, path)
		var j = 0
		val contents = obj.eContents.filter[x | x.eContainingFeature !== null && !x.eContainingFeature.isTransient]
		for (o : contents) {
			xPathForObject(o, path, j++, false)
		}
	}
	
	/**
	 * @param useXPathIDs whether XPath-style IDs are used
	 */
	def setUseXPathIDs(boolean useXPathIDs) {
		this.useXPathIDs = useXPathIDs
	}
	
	/**
	 * @return whether XPath-style IDs are used
	 */
	def getUseXPathIDs() {
		useXPathIDs
	}
	
	/**
	 * @param create whether IDs for cross-references should be generated when the resource is saved
	 */
	def createIDsOnSave(boolean create) {
		createIDsOnSave = create
		useXPathIDs = create
	}
	
	/**
	 * @return the indentation string
	 */
	def getIndentation() {
		indentation
	}
	
	/**
	 * @param indentation the string used to indent the generated XMI files. If null, use four spaces.
	 */
	 // This does not check whether the string only contains whitespace - maybe it should throw an exception if it does not
	def setIndentation(String indentation) {
		this.indentation = indentation ?: "   "
	}
	
	/**
	 * @return whether cascading of add-notifications is enabled.
	 */
	def getCascade() {
		cascadeNotifications
	}
	
	/**
	 * Enables or disables cascaded add-notifications for the objects in this resource.
	 * @param cascade whether cascaded add-notifications should be enabled
	 */
	def setCascade(boolean cascade) {
		cascadeNotifications = cascade
	}
	
	protected override getEObject(List<String> uriFragmentPath) {
		val size = uriFragmentPath.size
		var eObject = getEObjectForURIFragmentRootSegment(size == 0 ? "" : uriFragmentPath.get(0))
		
		for (var i = 1; i < size && eObject !== null; i++) {
			eObject = eObjectForURIFragmentSegment(eObject, uriFragmentPath.get(i))
		}
		
		return eObject
	}
	
	//adapted from org.eclipse.emf.ecore.impl.BasicEObjectImpl#eObjectForURIFragmentSegment
	private def EObject eObjectForURIFragmentSegment(EObject obj, String segment) {
		if (obj instanceof InternalEObject) {
			obj.eObjectForURIFragmentSegment(segment)
		} else {
			val lastIndex = segment.length - 1
			if (lastIndex == -1 || segment.charAt(0).toString != '@') {
				throw new IllegalArgumentException("Expecting @ at index 0 of '" + segment + "'")
			}
			
			val lastChar = segment.charAt(lastIndex)
			if (lastChar == ']') {
				val index = segment.indexOf('[')
				if (index >= 0) {
					val eRef = obj.eClass.getEStructuralFeature(segment.substring(1, index)) as EReference
					val predicate = segment.substring(index + 1, lastIndex)
					return eObjectForURIPredicate(predicate, eRef)
				} else {
					throw new IllegalArgumentException("Expecting [ in '" + segment + "'")
				}
			} else {
				var dotIndex = -1
				if (Character.isDigit(lastChar)) {
					dotIndex = segment.lastIndexOf('.', lastIndex - 1)
					if (dotIndex >= 0) {
						val eList = obj.eGet(obj.eClass.getEStructuralFeature(segment.substring(1, dotIndex))) as EList<?>
						val pos = Integer.parseInt(segment.substring(dotIndex + 1))
						if (pos < eList.size) {
							val res = eList.get(pos)
							return if (res instanceof FeatureMap.Entry) {
								res.value as EObject
							} else {
								res as EObject
							}
						}
					}
				}
				if (dotIndex < 0) return obj.eGet(obj.eClass.getEStructuralFeature(segment.substring(1))) as EObject
			}
			return null
		}
	}
	
	private def EObject eObjectForURIPredicate(String pred, EReference ref) {
		//This did not get called during testing
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
}