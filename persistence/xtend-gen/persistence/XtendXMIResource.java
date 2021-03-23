package persistence;

import com.google.common.base.Objects;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterators;
import emfcodegenerator.util.collections.DefaultEList;
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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.DOMHandler;
import org.eclipse.emf.ecore.xmi.DOMHelper;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.DefaultDOMHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl;
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import persistence.SmartEMFXMILoad;
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
  
  private final DefaultEList<EObject> contents = new DefaultEList<EObject>();
  
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
  
  public EList<EObject> getContents() {
    return this.contents;
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
    if ((!this.isLoaded)) {
      final Notification notification = this.setLoaded(true);
      this.isLoading = true;
      if ((this.errors != null)) {
        this.errors.clear();
      }
      if ((this.warnings != null)) {
        this.warnings.clear();
      }
      try {
        this.doLoad(inputSource.getByteStream(), options);
      } finally {
        this.isLoading = false;
        if ((notification != null)) {
          this.eNotify(notification);
        }
        this.setModified(false);
      }
    }
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
      sb.append(this.toXMITag(e, 1));
    }
    StringConcatenation _builder_2 = new StringConcatenation();
    String _lineSeparator_1 = System.lineSeparator();
    _builder_2.append(_lineSeparator_1);
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
    final StringBuilder indentation = this.operator_multiply(this.indentation, depth);
    final StringBuilder sb = new StringBuilder();
    final String name = this.tagName(object, object.eContainingFeature());
    final Function1<EObject, Boolean> _function = new Function1<EObject, Boolean>() {
      public Boolean apply(final EObject x) {
        boolean _xifexpression = false;
        EStructuralFeature _eContainingFeature = x.eContainingFeature();
        boolean _tripleNotEquals = (_eContainingFeature != null);
        if (_tripleNotEquals) {
          boolean _isTransient = x.eContainingFeature().isTransient();
          _xifexpression = (!_isTransient);
        } else {
          _xifexpression = true;
        }
        return Boolean.valueOf(_xifexpression);
      }
    };
    final Iterable<EObject> contents = IterableExtensions.<EObject>filter(object.eContents(), _function);
    boolean _isEmpty = IterableExtensions.isEmpty(contents);
    boolean _not = (!_isEmpty);
    if (_not) {
      StringConcatenation _builder = new StringConcatenation();
      String _lineSeparator = System.lineSeparator();
      _builder.append(_lineSeparator);
      _builder.append(indentation);
      _builder.append("<");
      _builder.append(name);
      String _xmiAttributes = this.xmiAttributes(object);
      _builder.append(_xmiAttributes);
      _builder.append(">");
      sb.append(_builder);
      for (final EObject e : contents) {
        sb.append(this.toXMITag(e, (depth + 1)));
      }
      StringConcatenation _builder_1 = new StringConcatenation();
      String _lineSeparator_1 = System.lineSeparator();
      _builder_1.append(_lineSeparator_1);
      _builder_1.append(indentation);
      _builder_1.append("</");
      _builder_1.append(name);
      _builder_1.append(">");
      sb.append(_builder_1);
    } else {
      StringConcatenation _builder_2 = new StringConcatenation();
      String _lineSeparator_2 = System.lineSeparator();
      _builder_2.append(_lineSeparator_2);
      _builder_2.append(indentation);
      _builder_2.append("<");
      _builder_2.append(name);
      String _xmiAttributes_1 = this.xmiAttributes(object);
      _builder_2.append(_xmiAttributes_1);
      _builder_2.append("/>");
      sb.append(_builder_2);
    }
    return sb;
  }
  
  private CharSequence crossReference(final EObject object, final EReference ref) {
    CharSequence _xblockexpression = null;
    {
      final Object target = object.eGet(ref);
      CharSequence _xifexpression = null;
      if ((target instanceof EObject)) {
        StringConcatenation _builder = new StringConcatenation();
        String _tagName = this.tagName(null, ref);
        _builder.append(_tagName);
        _builder.append("=\"");
        String _iD = this.getID(((EObject)target));
        _builder.append(_iD);
        _builder.append("\"");
        _xifexpression = _builder;
      } else {
        if ((target instanceof List)) {
          StringConcatenation _builder_1 = new StringConcatenation();
          String _tagName_1 = this.tagName(null, ref);
          _builder_1.append(_tagName_1);
          _builder_1.append("=\"");
          final StringBuilder sb = new StringBuilder(_builder_1);
          for (final EObject t : ((List<EObject>) target)) {
            sb.append(this.getID(t)).append(" ");
          }
          int _length = sb.length();
          int _minus = (_length - 1);
          sb.setCharAt(_minus, '\"');
          return sb;
        }
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  private StringBuilder operator_multiply(final String string, final int i) {
    final StringBuilder sb = new StringBuilder();
    for (int j = 0; (j < i); j++) {
      sb.append(string);
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
    return this.tagName(object, object.eContainingFeature());
  }
  
  private String tagName(final EObject object, final EStructuralFeature feat) {
    String _xifexpression = null;
    if ((feat != null)) {
      String _xblockexpression = null;
      {
        String name = feat.getName();
        final char first = name.charAt(0);
        _xblockexpression = name.replaceFirst(Character.valueOf(first).toString(), Character.valueOf(Character.toLowerCase(first)).toString());
      }
      _xifexpression = _xblockexpression;
    } else {
      _xifexpression = this.rootName(object);
    }
    return _xifexpression;
  }
  
  private String xmiAttributes(final EObject object) {
    String _xblockexpression = null;
    {
      final EClass eClass = object.eClass();
      final StringBuilder sb = new StringBuilder();
      final Function1<EReference, Boolean> _function = new Function1<EReference, Boolean>() {
        public Boolean apply(final EReference x) {
          return Boolean.valueOf(((!x.isContainment()) && (!x.isContainer())));
        }
      };
      final Iterable<EReference> references = IterableExtensions.<EReference>filter(object.eClass().getEAllReferences(), _function);
      final Function1<EAttribute, Boolean> _function_1 = new Function1<EAttribute, Boolean>() {
        public Boolean apply(final EAttribute x) {
          boolean _isTransient = x.isTransient();
          return Boolean.valueOf((!_isTransient));
        }
      };
      Iterable<EAttribute> _filter = IterableExtensions.<EAttribute>filter(eClass.getEAttributes(), _function_1);
      for (final EAttribute a : _filter) {
        if (((!a.isUnsettable()) || object.eIsSet(a))) {
          if ((!(a instanceof EReference))) {
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
      }
      for (final EReference r : references) {
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append(" ");
        CharSequence _crossReference = this.crossReference(object, r);
        _builder_1.append(_crossReference, " ");
        sb.append(_builder_1);
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
      HashMap<Object, Object> _elvis = null;
      if (this.defaultLoadOptions != null) {
        _elvis = this.defaultLoadOptions;
      } else {
        HashMap<Object, Object> _hashMap = new HashMap<Object, Object>();
        _elvis = _hashMap;
      }
      final HashMap<Object, Object> opt = _elvis;
      if ((options != null)) {
        opt.putAll(options);
      }
      Object _get = opt.get(XMLResource.OPTION_URI_HANDLER);
      final XMLResource.URIHandler uriHandler = ((XMLResource.URIHandler) _get);
      URI _xifexpression = null;
      if ((uriHandler instanceof URIHandlerImpl)) {
        _xifexpression = ((URIHandlerImpl)uriHandler).getBaseURI();
      } else {
        _xifexpression = null;
      }
      final URI handlerURI = _xifexpression;
      try {
        if ((str instanceof URIConverter.Loadable)) {
          ((URIConverter.Loadable)str).loadResource(this);
        } else {
          XMLHelperImpl _xMLHelperImpl = new XMLHelperImpl(this);
          final SmartEMFXMILoad loader = new SmartEMFXMILoad(_xMLHelperImpl);
          Object _get_1 = opt.get(XMLResource.OPTION_RESOURCE_HANDLER);
          final XMLResource.ResourceHandler handler = ((XMLResource.ResourceHandler) _get_1);
          if ((handler != null)) {
            handler.preLoad(this, str, opt);
          }
          loader.load(this, str, opt);
          if ((handler != null)) {
            handler.postLoad(this, str, opt);
          }
        }
      } finally {
        if ((uriHandler != null)) {
          uriHandler.setBaseURI(handlerURI);
        }
      }
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
    final String prefix = "";
    int count = 0;
    EList<EObject> _contents = this.getContents();
    for (final EObject o : _contents) {
      int _plusPlus = count++;
      int _size = this.getContents().size();
      boolean _lessEqualsThan = (_size <= 1);
      this.xPathForObject(o, prefix, _plusPlus, _lessEqualsThan);
    }
  }
  
  private void xPathForObject(final EObject obj, final String prefix, final int i, final boolean disableTopIndex) {
    String _xifexpression = null;
    boolean _equals = Objects.equal(prefix, "");
    if (_equals) {
      Object _xifexpression_1 = null;
      if (disableTopIndex) {
        _xifexpression_1 = "";
      } else {
        _xifexpression_1 = Integer.valueOf(i);
      }
      _xifexpression = ("/" + _xifexpression_1);
    } else {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append(prefix);
      _builder.append("/@");
      String _tagName = this.tagName(obj);
      _builder.append(_tagName);
      _builder.append(".");
      _builder.append(i);
      _xifexpression = _builder.toString();
    }
    final String path = _xifexpression;
    this.setID(obj, path);
    int j = 0;
    final Function1<EObject, Boolean> _function = new Function1<EObject, Boolean>() {
      public Boolean apply(final EObject x) {
        return Boolean.valueOf(((x.eContainingFeature() != null) && (!x.eContainingFeature().isTransient())));
      }
    };
    final Iterable<EObject> contents = IterableExtensions.<EObject>filter(obj.eContents(), _function);
    for (final EObject o : contents) {
      int _plusPlus = j++;
      this.xPathForObject(o, path, _plusPlus, false);
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
  
  protected EObject getEObject(final List<String> uriFragmentPath) {
    final int size = uriFragmentPath.size();
    String _xifexpression = null;
    if ((size == 0)) {
      _xifexpression = "";
    } else {
      _xifexpression = uriFragmentPath.get(0);
    }
    EObject eObject = this.getEObjectForURIFragmentRootSegment(_xifexpression);
    for (int i = 1; ((i < size) && (eObject != null)); i++) {
      eObject = this.eObjectForURIFragmentSegment(eObject, uriFragmentPath.get(i));
    }
    return eObject;
  }
  
  private EObject eObjectForURIFragmentSegment(final EObject obj, final String segment) {
    int _length = segment.length();
    final int lastIndex = (_length - 1);
    if (((lastIndex == (-1)) || (!Objects.equal(Character.valueOf(segment.charAt(0)).toString(), "@")))) {
      throw new IllegalArgumentException((("Expecting @ at index 0 of \'" + segment) + "\'"));
    }
    final char lastChar = segment.charAt(lastIndex);
    boolean _equals = Objects.equal(Character.valueOf(lastChar), "]");
    if (_equals) {
      final int index = segment.indexOf("[");
      if ((index >= 0)) {
        EStructuralFeature _eStructuralFeature = obj.eClass().getEStructuralFeature(segment.substring(1, index));
        final EReference eRef = ((EReference) _eStructuralFeature);
        final String predicate = segment.substring((index + 1), lastIndex);
        return this.eObjectForURIPredicate(predicate, eRef);
      } else {
        throw new IllegalArgumentException((("Expecting [ in \'" + segment) + "\'"));
      }
    } else {
      int dotIndex = (-1);
      boolean _isDigit = Character.isDigit(lastChar);
      if (_isDigit) {
        dotIndex = segment.lastIndexOf(".", (lastIndex - 1));
        if ((dotIndex >= 0)) {
          Object _eGet = obj.eGet(obj.eClass().getEStructuralFeature(segment.substring(1, dotIndex)));
          final EList<?> eList = ((EList<?>) _eGet);
          final int pos = Integer.parseInt(segment.substring((dotIndex + 1)));
          int _size = eList.size();
          boolean _lessThan = (pos < _size);
          if (_lessThan) {
            final Object res = eList.get(pos);
            EObject _xifexpression = null;
            if ((res instanceof FeatureMap.Entry)) {
              Object _value = ((FeatureMap.Entry)res).getValue();
              _xifexpression = ((EObject) _value);
            } else {
              _xifexpression = ((EObject) res);
            }
            return _xifexpression;
          }
        }
      }
      if ((dotIndex < 0)) {
        Object _eGet_1 = obj.eGet(obj.eClass().getEStructuralFeature(segment.substring(1)));
        return ((EObject) _eGet_1);
      }
    }
    return null;
  }
  
  private EObject eObjectForURIPredicate(final String pred, final EReference ref) {
    throw new UnsupportedOperationException("TODO: auto-generated method stub");
  }
}
