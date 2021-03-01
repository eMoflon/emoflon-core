package persistence

import com.google.common.collect.HashBiMap
import com.google.common.collect.Iterators
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintStream
import java.io.Writer
import java.util.Collections
import java.util.HashMap
import java.util.Map
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceImpl
import org.eclipse.emf.ecore.xmi.DOMHandler
import org.eclipse.emf.ecore.xmi.XMIResource
import org.eclipse.emf.ecore.xmi.impl.DefaultDOMHandlerImpl
import org.eclipse.emf.ecore.xml.type.AnyType
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xml.sax.InputSource

class XtendXMIResource extends ResourceImpl implements XMIResource {
	
	public static val Resource.Factory FACTORY = [uri | new XtendXMIResource(uri)]
	
	val HashBiMap<String, EObject> idToEObjectBiMap = HashBiMap.create
	val HashMap<EObject, AnyType> eObjectToExtensionMap = new HashMap()
	val domHelper = new DefaultDOMHandlerImpl
	val XMIRoot dummyRoot = [getContents]
	
	var String xmiVersion = XMIResource.VERSION_VALUE
	var String xmiNamespace = XMIResource.XMI_URI
	var String xmlVersion = "1.0"
	var String encoding = "ASCII"
	var String systemId
	var String publicId
	var String indentation = "\t"
	var boolean useZip = false
	var boolean createIDsOnSave = true
	var boolean useXPathIDs = true
	
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
		return Collections.emptyMap
	}
	
	override getDefaultSaveOptions() {
		return Collections.emptyMap
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
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	override save(Document document, Map<?, ?> options, DOMHandler handler) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	protected override doSave(OutputStream str, Map<?, ?> options) {
		var PrintStream printer = if (str instanceof PrintStream) {
			str
		} else {
			new PrintStream(str)
		}
		//This does not detect if an object is removed and a different one is added - in these situations, createIDsOnSave must be true for IDs to be correct XPaths
		if (createIDsOnSave || idToEObjectBiMap.size != Iterators.size(getAllContents)) {
			createXPathIDs
		}
		printer.println(generateXMI())
		printer.flush
	}
	
	def CharSequence generateXMI() {
		val root = rootObject()
		val sb = new StringBuilder()
		
		var String rootTagName
		var String rootAttributes = createRootAttributes(root)
		
		if (root == dummyRoot) {
			
		} else {
			val eobj = if (root.contents.isEmpty) {
				getContents.iterator.next
			} else {
				root.rootObject
			}
			rootTagName = rootName(eobj)
			rootAttributes += " " + xmiAttributes(eobj)
		}
		
		sb.append('''<«rootTagName» «rootAttributes»>«System.lineSeparator»''')
		
		for (e : root.contents) {
			sb.append(toXMITag(e, 1) + System.lineSeparator)
		}
		
		sb.append('''</«rootTagName»>''')
		
		return sb
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
		
		if (!object.eContents.isEmpty) {
			sb.append('''«indentation»<«name» «xmiAttributes(object)»>«System.lineSeparator»''')
			for (e : object.eContents) {
				sb.append(toXMITag(e, depth + 1))
			}
			sb.append('''«indentation»</«name»>''')
		} else {
			sb.append('''«indentation»<«name» «xmiAttributes(object)»/>«System.lineSeparator»''')
		}
		
		return sb
	}
	
	private def String rootName(EObject object) {
		object.eClass().getName()
	}
	
	private def String tagName(EObject object) {
		var String name = object.eContainingFeature().getName()
		val first = name.charAt(0)
		name.replaceFirst(first.toString, Character.toLowerCase(first).toString)
	}
	
	private def String xmiAttributes(EObject object) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	private def String createRootAttributes(XMIRoot root) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	protected override doLoad(InputStream str, Map<?, ?> options) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
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
	
	/**
	 * Does nothing because compression is not implemented yet
	 */
	override setUseZip(boolean useZip) {
//		this.useZip = useZip
	}
	
	/**
	 * @return false
	 */
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
	
	def setIndentation(String indentation) {
		this.indentation = indentation
	}
	
}