package persistence

import com.google.common.collect.HashBiMap
import com.google.common.collect.Iterators
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.util.HashMap
import java.util.Map
import javax.xml.parsers.DocumentBuilderFactory
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceImpl
import org.eclipse.emf.ecore.xmi.DOMHandler
import org.eclipse.emf.ecore.xmi.XMIResource
import org.eclipse.emf.ecore.xmi.impl.DefaultDOMHandlerImpl
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl
import org.eclipse.emf.ecore.xml.type.AnyType
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xml.sax.InputSource

class XtendXMIResource extends ResourceImpl implements XMIResource {
	
	public static val Resource.Factory FACTORY = [uri | new XtendXMIResource(uri)]
	
	val HashBiMap<String, EObject> idToEObjectBiMap = HashBiMap.create
	val HashMap<EObject, AnyType> eObjectToExtensionMap = new HashMap
	val domHelper = new DefaultDOMHandlerImpl
	val XMIRoot dummyRoot = [getContents]
	val defaultLoadOptions = new HashMap
	val defaultSaveOptions = new HashMap
	
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
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
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
			sb.append(System.lineSeparator + toXMITag(e, 1))
		}
		
		sb.append('''</«rootTagName»>«System.lineSeparator»''')
		
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
		val indentation = new StringBuilder()
		for (var i = 0; i < depth; i++) {
			indentation.append(this.indentation)
		}
		val sb = new StringBuilder()
		val name = tagName(object)
		
		val contents = object.eContents.filter[x | !x.eContainingFeature.isTransient]
		
		if (!contents.isEmpty) {
			sb.append('''«indentation»<«name»«xmiAttributes(object)»>«System.lineSeparator»''')
			for (e : contents) {
				sb.append(toXMITag(e, depth + 1))
			}
			sb.append('''«indentation»</«name»>''')
		} else {
			sb.append('''«indentation»<«name»«xmiAttributes(object)»/>«System.lineSeparator»''')
		}
		
		return sb
	}
	
	private def String rootName(EObject object) {
		object.eClass.getEPackage.getName + ":" + object.eClass.getName
	}
	
	private def String tagName(EObject object) {
		val feat = object.eContainingFeature()
		
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
		val containments = eClass.getEAllContainments
		val sb = new StringBuilder
		
		for (a : eClass.getEAllStructuralFeatures.filter[x | !containments.contains(x) && !x.isTransient]) {
			if (object.eIsSet(a)) {
				//TODO handle references
				sb.append(''' «a.getName»="«object.eGet(a)»"''')
			}
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
		load(new InputSource(str), options)
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
		val needsIndices = contents.size > 1
		val prefix = if (needsIndices) {
			"/*"
		} else ""
		var count = 1
		for (o : getContents) {
			xPathForObject(o, prefix, count++, needsIndices)
		}
	}
	
	private def void xPathForObject(EObject obj, String prefix, int i, boolean needsIndex) {
		val path = '''«prefix»/«tagName(obj)»«if (needsIndex)'''[«i»]'''»'''
		setID(obj, path)
		var j = 1
		val contents = obj.eContents.filter[x | !x.eContainingFeature.isTransient]
		val needsIndices = contents.size > 1
		for (o : contents) {
			xPathForObject(o, path, j++, needsIndices)
		}
	}
	
	def setUseXPathIDs(boolean useXPathIDs) {
		this.useXPathIDs = useXPathIDs
	}
	
	def getUseXPathIDs() {
		useXPathIDs
	}
	
	def createIDsOnSave(boolean create) {
		createIDsOnSave = create
		useXPathIDs = create
	}
	
	def getIndentation() {
		indentation
	}
	
	// This does not check whether the string only contains whitespace - maybe it should throw an exception if it does not
	def setIndentation(String indentation) {
		this.indentation = indentation
	}
	
	def getCascade() {
		cascadeNotifications
	}
	
	def setCascade(boolean cascade) {
		cascadeNotifications = cascade
	}
	
}