package persistence;

import com.google.common.base.Objects;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterators;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.xmi.DOMHandler;
import org.eclipse.emf.ecore.xmi.DOMHelper;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.DefaultDOMHandlerImpl;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import persistence.XMIRoot;

@SuppressWarnings("all")
public class XtendXMIResource extends ResourceImpl implements XMIResource {
  public static final Resource.Factory FACTORY = new Resource.Factory() {
    public Resource createResource(final URI uri) {
      return new XtendXMIResource(uri);
    }
  };
  
  private final HashBiMap<String, EObject> idToEObjectBiMap = HashBiMap.<String, EObject>create();
  
  private final HashMap<EObject, AnyType> eObjectToExtensionMap = new HashMap<EObject, AnyType>();
  
  private final DefaultDOMHandlerImpl domHelper = new DefaultDOMHandlerImpl();
  
  private final XMIRoot dummyRoot = new XMIRoot() {
    public List<EObject> contents() {
      return XtendXMIResource.this.getContents();
    }
  };
  
  private String xmiVersion = XMIResource.VERSION_VALUE;
  
  private String xmiNamespace = XMIResource.XMI_URI;
  
  private String xmlVersion = "1.0";
  
  private String encoding = "ASCII";
  
  private String systemId;
  
  private String publicId;
  
  private String indentation = "\t";
  
  private boolean useZip = false;
  
  private boolean createIDsOnSave = true;
  
  private boolean useXPathIDs = true;
  
  public XtendXMIResource() {
    super();
  }
  
  public XtendXMIResource(final URI uri) {
    super(uri);
  }
  
  public String getXMINamespace() {
    return this.xmiNamespace;
  }
  
  public String getXMIVersion() {
    return this.xmiVersion;
  }
  
  public void setXMINamespace(final String namespace) {
    this.xmiNamespace = namespace;
  }
  
  public void setXMIVersion(final String version) {
    this.xmiVersion = version;
  }
  
  public DOMHelper getDOMHelper() {
    return this.domHelper;
  }
  
  public Map<Object, Object> getDefaultLoadOptions() {
    return Collections.<Object, Object>emptyMap();
  }
  
  public Map<Object, Object> getDefaultSaveOptions() {
    return Collections.<Object, Object>emptyMap();
  }
  
  public Map<EObject, AnyType> getEObjectToExtensionMap() {
    return this.eObjectToExtensionMap;
  }
  
  public Map<EObject, String> getEObjectToIDMap() {
    return this.idToEObjectBiMap.inverse();
  }
  
  public String getEncoding() {
    return this.encoding;
  }
  
  public String getID(final EObject eObject) {
    return this.getEObjectToIDMap().get(eObject);
  }
  
  public Map<String, EObject> getIDToEObjectMap() {
    return this.idToEObjectBiMap;
  }
  
  public String getPublicId() {
    return this.publicId;
  }
  
  public String getSystemId() {
    return this.systemId;
  }
  
  public String getXMLVersion() {
    return this.xmlVersion;
  }
  
  public void load(final Node node, final Map<?, ?> options) throws IOException {
    throw new UnsupportedOperationException("TODO: auto-generated method stub");
  }
  
  public void load(final InputSource inputSource, final Map<?, ?> options) throws IOException {
    throw new UnsupportedOperationException("TODO: auto-generated method stub");
  }
  
  public void save(final Writer writer, final Map<?, ?> options) throws IOException {
    throw new UnsupportedOperationException("TODO: auto-generated method stub");
  }
  
  public Document save(final Document document, final Map<?, ?> options, final DOMHandler handler) {
    throw new UnsupportedOperationException("TODO: auto-generated method stub");
  }
  
  protected void doSave(final OutputStream str, final Map<?, ?> options) {
    PrintStream _xifexpression = null;
    if ((str instanceof PrintStream)) {
      _xifexpression = ((PrintStream)str);
    } else {
      _xifexpression = new PrintStream(str);
    }
    PrintStream printer = _xifexpression;
    if ((this.createIDsOnSave || (this.idToEObjectBiMap.size() != Iterators.size(this.getAllContents())))) {
      this.createXPathIDs();
    }
    printer.println(this.generateXMI());
    printer.flush();
  }
  
  public CharSequence generateXMI() {
    final XMIRoot root = this.rootObject();
    final StringBuilder sb = new StringBuilder();
    String rootTagName = null;
    String rootAttributes = this.createRootAttributes(root);
    boolean _equals = Objects.equal(root, this.dummyRoot);
    if (_equals) {
    } else {
      EObject _xifexpression = null;
      boolean _isEmpty = root.contents().isEmpty();
      if (_isEmpty) {
        _xifexpression = this.getContents().iterator().next();
      } else {
        _xifexpression = root.rootObject();
      }
      final EObject eobj = _xifexpression;
      rootTagName = this.rootName(eobj);
      String _rootAttributes = rootAttributes;
      String _xmiAttributes = this.xmiAttributes(eobj);
      String _plus = (" " + _xmiAttributes);
      rootAttributes = (_rootAttributes + _plus);
    }
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    _builder.append(rootTagName);
    _builder.append(" ");
    _builder.append(rootAttributes);
    _builder.append(">");
    String _lineSeparator = System.lineSeparator();
    _builder.append(_lineSeparator);
    sb.append(_builder);
    List<EObject> _contents = root.contents();
    for (final EObject e : _contents) {
      CharSequence _xMITag = this.toXMITag(e, 1);
      String _lineSeparator_1 = System.lineSeparator();
      String _plus_1 = (_xMITag + _lineSeparator_1);
      sb.append(_plus_1);
    }
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("</");
    _builder_1.append(rootTagName);
    _builder_1.append(">");
    sb.append(_builder_1);
    return sb;
  }
  
  private XMIRoot rootObject() {
    XMIRoot _xifexpression = null;
    int _size = this.getContents().size();
    boolean _equals = (_size == 1);
    if (_equals) {
      final XMIRoot _function = new XMIRoot() {
        public List<EObject> contents() {
          return XtendXMIResource.this.getContents().iterator().next().eContents();
        }
      };
      _xifexpression = _function;
    } else {
      _xifexpression = this.dummyRoot;
    }
    return _xifexpression;
  }
  
  private CharSequence toXMITag(final EObject object, final int depth) {
    final StringBuilder indentation = new StringBuilder();
    for (int i = 0; (i < depth); i++) {
      indentation.append(this.indentation);
    }
    final StringBuilder sb = new StringBuilder();
    final String name = this.tagName(object);
    boolean _isEmpty = object.eContents().isEmpty();
    boolean _not = (!_isEmpty);
    if (_not) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append(indentation);
      _builder.append("<");
      _builder.append(name);
      _builder.append(" ");
      String _xmiAttributes = this.xmiAttributes(object);
      _builder.append(_xmiAttributes);
      _builder.append(">");
      String _lineSeparator = System.lineSeparator();
      _builder.append(_lineSeparator);
      sb.append(_builder);
      EList<EObject> _eContents = object.eContents();
      for (final EObject e : _eContents) {
        sb.append(this.toXMITag(e, (depth + 1)));
      }
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append(indentation);
      _builder_1.append("</");
      _builder_1.append(name);
      _builder_1.append(">");
      sb.append(_builder_1);
    } else {
      StringConcatenation _builder_2 = new StringConcatenation();
      _builder_2.append(indentation);
      _builder_2.append("<");
      _builder_2.append(name);
      _builder_2.append(" ");
      String _xmiAttributes_1 = this.xmiAttributes(object);
      _builder_2.append(_xmiAttributes_1);
      _builder_2.append("/>");
      String _lineSeparator_1 = System.lineSeparator();
      _builder_2.append(_lineSeparator_1);
      sb.append(_builder_2);
    }
    return sb;
  }
  
  private String rootName(final EObject object) {
    return object.eClass().getName();
  }
  
  private String tagName(final EObject object) {
    String _xblockexpression = null;
    {
      String name = object.eContainingFeature().getName();
      final char first = name.charAt(0);
      _xblockexpression = name.replaceFirst(Character.valueOf(first).toString(), Character.valueOf(Character.toLowerCase(first)).toString());
    }
    return _xblockexpression;
  }
  
  private String xmiAttributes(final EObject object) {
    throw new UnsupportedOperationException("TODO: auto-generated method stub");
  }
  
  private String createRootAttributes(final XMIRoot root) {
    throw new UnsupportedOperationException("TODO: auto-generated method stub");
  }
  
  protected void doLoad(final InputStream str, final Map<?, ?> options) {
    throw new UnsupportedOperationException("TODO: auto-generated method stub");
  }
  
  public void setDoctypeInfo(final String publicId, final String systemId) {
    this.systemId = systemId;
    this.publicId = publicId;
  }
  
  public void setEncoding(final String encoding) {
    this.encoding = encoding;
  }
  
  public void setID(final EObject eObject, final String id) {
    this.idToEObjectBiMap.forcePut(id, eObject);
  }
  
  /**
   * Does nothing because compression is not implemented yet
   */
  public void setUseZip(final boolean useZip) {
  }
  
  /**
   * @return false
   */
  public boolean useZip() {
    return this.useZip;
  }
  
  public void setXMLVersion(final String version) {
    this.xmlVersion = version;
  }
  
  /**
   * Removes all entries from idToEObjectBiMap and replaces them with XPaths
   */
  public void createXPathIDs() {
    this.idToEObjectBiMap.clear();
  }
  
  public boolean setUseXPathIDs(final boolean useXPathIDs) {
    return this.useXPathIDs = useXPathIDs;
  }
  
  public boolean getUseXPathIDs() {
    return this.useXPathIDs;
  }
  
  public boolean createIDsOnSave(final boolean create) {
    boolean _xblockexpression = false;
    {
      this.createIDsOnSave = create;
      _xblockexpression = this.useXPathIDs = create;
    }
    return _xblockexpression;
  }
  
  public String getIndentation() {
    return this.indentation;
  }
  
  public String setIndentation(final String indentation) {
    return this.indentation = indentation;
  }
}
