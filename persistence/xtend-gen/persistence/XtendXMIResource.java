package persistence;

import com.google.common.base.Objects;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterators;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.xmi.DOMHandler;
import org.eclipse.emf.ecore.xmi.DOMHelper;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.DefaultDOMHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl;
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
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
  
  private final HashMap<Object, Object> defaultLoadOptions = new HashMap<Object, Object>();
  
  private final HashMap<Object, Object> defaultSaveOptions = new HashMap<Object, Object>();
  
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
    return this.defaultLoadOptions;
  }
  
  public Map<Object, Object> getDefaultSaveOptions() {
    return this.defaultSaveOptions;
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
  
  public String getURIFragment(final EObject obj) {
    return this.getID(obj);
  }
  
  public EObject getEObjectByID(final String id) {
    return this.idToEObjectBiMap.get(id);
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
    if ((this.createIDsOnSave || (this.idToEObjectBiMap.size() != Iterators.size(this.getAllContents())))) {
      this.createXPathIDs();
    }
    final BufferedWriter wri = new BufferedWriter(writer);
    wri.append(this.generateXMI());
    wri.flush();
  }
  
  public Document save(final Document document, final Map<?, ?> options, final DOMHandler handler) {
    try {
      Document _xblockexpression = null;
      {
        if ((this.createIDsOnSave || (this.idToEObjectBiMap.size() != Iterators.size(this.getAllContents())))) {
          this.createXPathIDs();
        }
        Document _elvis = null;
        if (document != null) {
          _elvis = document;
        } else {
          Document _newDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
          _elvis = _newDocument;
        }
        final Document doc = _elvis;
        DOMHandler _elvis_1 = null;
        if (handler != null) {
          _elvis_1 = handler;
        } else {
          _elvis_1 = this.domHelper;
        }
        final DOMHandler hnd = _elvis_1;
        XMIHelperImpl _xMIHelperImpl = new XMIHelperImpl(this);
        final XMISaveImpl save = new XMISaveImpl(_xMIHelperImpl);
        HashMap<Object, Object> opt = new HashMap<Object, Object>();
        if ((options != null)) {
          opt.putAll(options);
        }
        boolean _isEmpty = opt.isEmpty();
        boolean _not = (!_isEmpty);
        if (_not) {
          Set<Map.Entry<Object, Object>> _entrySet = this.defaultSaveOptions.entrySet();
          for (final Map.Entry<Object, Object> e : _entrySet) {
            opt.putIfAbsent(e.getKey(), e.getValue());
          }
        }
        _xblockexpression = save.save(this, doc, opt, hnd);
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected void doSave(final OutputStream str, final Map<?, ?> options) {
    try {
      OutputStreamWriter _outputStreamWriter = new OutputStreamWriter(str);
      this.save(_outputStreamWriter, options);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected void doUnload() {
    final TreeIterator<EObject> allContents = this.getAllProperContents(this.unloadingContents);
    boolean _isEmpty = this.getContents().isEmpty();
    boolean _not = (!_isEmpty);
    if (_not) {
      this.getContents().clear();
    }
    this.getErrors().clear();
    this.getWarnings().clear();
    while (allContents.hasNext()) {
      allContents.next().eAdapters().clear();
    }
    this.idToEObjectBiMap.clear();
    this.eObjectToExtensionMap.clear();
  }
  
  public CharSequence generateXMI() {
    final XMIRoot root = this.rootObject();
    final StringBuilder sb = new StringBuilder();
    String rootTagName = null;
    String rootAttributes = this.createRootAttributes(root);
    boolean _equals = Objects.equal(root, this.dummyRoot);
    if (_equals) {
      rootTagName = "xmi:XMI";
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
    _builder.append("<?xml version=\"");
    _builder.append(this.xmlVersion);
    _builder.append("\" encoding=\"");
    _builder.append(this.encoding);
    _builder.append("\"?>");
    String _lineSeparator = System.lineSeparator();
    _builder.append(_lineSeparator);
    sb.append(_builder);
    sb.append(this.createDoctypeIfNecessary(rootTagName));
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("<");
    _builder_1.append(rootTagName);
    _builder_1.append(" ");
    _builder_1.append(rootAttributes);
    _builder_1.append(">");
    sb.append(_builder_1);
    List<EObject> _contents = root.contents();
    for (final EObject e : _contents) {
      String _lineSeparator_1 = System.lineSeparator();
      CharSequence _xMITag = this.toXMITag(e, 1);
      String _plus_1 = (_lineSeparator_1 + _xMITag);
      sb.append(_plus_1);
    }
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("</");
    _builder_2.append(rootTagName);
    _builder_2.append(">");
    String _lineSeparator_2 = System.lineSeparator();
    _builder_2.append(_lineSeparator_2);
    sb.append(_builder_2);
    return sb;
  }
  
  private CharSequence createDoctypeIfNecessary(final String rootTagName) {
    CharSequence _xblockexpression = null;
    {
      final boolean hasPublicId = ((this.publicId != null) && (!Objects.equal(this.publicId, "")));
      final boolean hasSystemId = ((this.systemId != null) && (!Objects.equal(this.systemId, "")));
      CharSequence _xifexpression = null;
      if (hasPublicId) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("<!DOCTYPE ");
        _builder.append(rootTagName);
        _builder.append(" PUBLIC \"");
        _builder.append(this.publicId);
        _builder.append("\"");
        CharSequence _xifexpression_1 = null;
        if (hasSystemId) {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append(" ");
          _builder_1.append("\"");
          _builder_1.append(this.systemId, " ");
          _builder_1.append("\"");
          _xifexpression_1 = _builder_1;
        } else {
          _xifexpression_1 = "";
        }
        _builder.append(_xifexpression_1);
        _builder.append(">");
        String _lineSeparator = System.lineSeparator();
        _builder.append(_lineSeparator);
        _xifexpression = _builder;
      } else {
        CharSequence _xifexpression_2 = null;
        if (hasSystemId) {
          StringConcatenation _builder_2 = new StringConcatenation();
          _builder_2.append("<!DOCTYPE ");
          _builder_2.append(rootTagName);
          _builder_2.append(" SYSTEM \"");
          _builder_2.append(this.systemId);
          _builder_2.append("\">");
          String _lineSeparator_1 = System.lineSeparator();
          _builder_2.append(_lineSeparator_1);
          _xifexpression_2 = _builder_2;
        } else {
          _xifexpression_2 = "";
        }
        _xifexpression = _xifexpression_2;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
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
    final Function1<EObject, Boolean> _function = new Function1<EObject, Boolean>() {
      public Boolean apply(final EObject x) {
        boolean _isTransient = x.eContainingFeature().isTransient();
        return Boolean.valueOf((!_isTransient));
      }
    };
    final Iterable<EObject> contents = IterableExtensions.<EObject>filter(object.eContents(), _function);
    boolean _isEmpty = IterableExtensions.isEmpty(contents);
    boolean _not = (!_isEmpty);
    if (_not) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append(indentation);
      _builder.append("<");
      _builder.append(name);
      String _xmiAttributes = this.xmiAttributes(object);
      _builder.append(_xmiAttributes);
      _builder.append(">");
      String _lineSeparator = System.lineSeparator();
      _builder.append(_lineSeparator);
      sb.append(_builder);
      for (final EObject e : contents) {
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
    String _name = object.eClass().getEPackage().getName();
    String _plus = (_name + ":");
    String _name_1 = object.eClass().getName();
    return (_plus + _name_1);
  }
  
  private String tagName(final EObject object) {
    String _xblockexpression = null;
    {
      final EStructuralFeature feat = object.eContainingFeature();
      String _xifexpression = null;
      if ((feat != null)) {
        String _xblockexpression_1 = null;
        {
          String name = feat.getName();
          final char first = name.charAt(0);
          _xblockexpression_1 = name.replaceFirst(Character.valueOf(first).toString(), Character.valueOf(Character.toLowerCase(first)).toString());
        }
        _xifexpression = _xblockexpression_1;
      } else {
        _xifexpression = this.rootName(object);
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  private String xmiAttributes(final EObject object) {
    String _xblockexpression = null;
    {
      final EClass eClass = object.eClass();
      final EList<EReference> containments = eClass.getEAllContainments();
      final StringBuilder sb = new StringBuilder();
      final Function1<EStructuralFeature, Boolean> _function = new Function1<EStructuralFeature, Boolean>() {
        public Boolean apply(final EStructuralFeature x) {
          return Boolean.valueOf(((!containments.contains(x)) && (!x.isTransient())));
        }
      };
      Iterable<EStructuralFeature> _filter = IterableExtensions.<EStructuralFeature>filter(eClass.getEAllStructuralFeatures(), _function);
      for (final EStructuralFeature a : _filter) {
        boolean _eIsSet = object.eIsSet(a);
        if (_eIsSet) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append(" ");
          String _name = a.getName();
          _builder.append(_name, " ");
          _builder.append("=\"");
          Object _eGet = object.eGet(a);
          _builder.append(_eGet, " ");
          _builder.append("\"");
          sb.append(_builder);
        }
      }
      _xblockexpression = sb.toString();
    }
    return _xblockexpression;
  }
  
  private String createRootAttributes(final XMIRoot root) {
    String _xblockexpression = null;
    {
      final EPackage pkg = this.getPackage(root);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("xmi:version=\"");
      _builder.append(this.xmiVersion);
      _builder.append("\" xmlns:xmi=\"");
      _builder.append(this.xmiNamespace);
      _builder.append("\" xmlns:");
      String _nsPrefix = pkg.getNsPrefix();
      _builder.append(_nsPrefix);
      _builder.append("=\"");
      String _nsURI = pkg.getNsURI();
      _builder.append(_nsURI);
      _builder.append("\"");
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  private EPackage getPackage(final XMIRoot root) {
    return root.contents().iterator().next().eClass().getEPackage();
  }
  
  protected void doLoad(final InputStream str, final Map<?, ?> options) {
    try {
      InputSource _inputSource = new InputSource(str);
      this.load(_inputSource, options);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
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
  
  public void setUseZip(final boolean useZip) {
    this.useZip = useZip;
  }
  
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
    int _size = this.contents.size();
    final boolean needsIndices = (_size > 1);
    String _xifexpression = null;
    if (needsIndices) {
      _xifexpression = "/*";
    } else {
      _xifexpression = "";
    }
    final String prefix = _xifexpression;
    int count = 1;
    EList<EObject> _contents = this.getContents();
    for (final EObject o : _contents) {
      int _plusPlus = count++;
      this.xPathForObject(o, prefix, _plusPlus, needsIndices);
    }
  }
  
  private void xPathForObject(final EObject obj, final String prefix, final int i, final boolean needsIndex) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(prefix);
    _builder.append("/");
    String _tagName = this.tagName(obj);
    _builder.append(_tagName);
    CharSequence _xifexpression = null;
    if (needsIndex) {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("[");
      _builder_1.append(i);
      _builder_1.append("]");
      _xifexpression = _builder_1;
    }
    _builder.append(_xifexpression);
    final String path = _builder.toString();
    this.setID(obj, path);
    int j = 1;
    final Function1<EObject, Boolean> _function = new Function1<EObject, Boolean>() {
      public Boolean apply(final EObject x) {
        boolean _isTransient = x.eContainingFeature().isTransient();
        return Boolean.valueOf((!_isTransient));
      }
    };
    final Iterable<EObject> contents = IterableExtensions.<EObject>filter(obj.eContents(), _function);
    int _size = IterableExtensions.size(contents);
    final boolean needsIndices = (_size > 1);
    for (final EObject o : contents) {
      int _plusPlus = j++;
      this.xPathForObject(o, path, _plusPlus, needsIndices);
    }
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
  
  public boolean getCascade() {
    return this.cascadeNotifications;
  }
  
  public boolean setCascade(final boolean cascade) {
    return this.cascadeNotifications = cascade;
  }
}
