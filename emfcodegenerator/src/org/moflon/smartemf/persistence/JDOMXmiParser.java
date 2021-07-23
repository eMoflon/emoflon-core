package org.moflon.smartemf.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

public class JDOMXmiParser {
	
	protected Map<String, List<Consumer<EObject>>> waitingCrossRefs = new HashMap<>();
	protected Map<String, List<Consumer<EObject>>> waitingHRefs = new HashMap<>();
	protected Map<String, EObject> id2Object = new HashMap<>();
	protected Map<String, EObject> fqId2Object = new HashMap<>();
	protected Map<String, EPackage> ns2Package = new HashMap<>();
	protected Map<String, EFactory> ns2Factory = new HashMap<>();
	protected Map<String, Resource> loadedResources = new HashMap<>(); 
	protected URI initialUri = null;
	
	final public static String XMI_NS = "xmi";
	final public static String XSI_NS = "xsi";
	final public static String XSI_TYPE = "type";
	final public static String HREF_ATR = "href";
	
	public Map<String, Resource> getLoadedResources() {
		return loadedResources;
	}
	
	public Map<String, EObject> getFqId2ObjectMap() {
		return fqId2Object;
	}
	
	public void addWaitingHRefs(final Map<String, List<Consumer<EObject>>> hrefs) {
		waitingHRefs.putAll(hrefs);
	}
	
	public void load(final InputStream is, final Resource resource) throws IOException {
		SAXBuilder saxBuilder = new SAXBuilder();
		Document parsedFile = null;
		try {
			parsedFile = saxBuilder.build(is);
		} catch (JDOMException | IOException e) {
			throw new IOException(e.getMessage(), e.getCause());
		}
		
		if(initialUri == null)
			initialUri = resource.getURI();
		
		domTreeToModel(parsedFile, resource);
		loadedResources.put(resource.getURI().toString(), resource);
	}
	
	public void load(final String uri, final ResourceSet rs) throws IOException{
		URI path = URI.createURI(uri);
		if(uri.contains("platform:/resource") || uri.contains("platform:/plugin"))
			throw new UnsupportedOperationException("Referencing and loading of global registry files such as platform:/plugin or plaform:/resource files is unsupported.");
		
		String filePath = path.devicePath();
		filePath = filePath.trim().replaceAll("%20", " ");

		if(filePath == null)
			throw new FileNotFoundException("No valid xmi file present at: "+path );
		
		File file = new File(filePath);
		if(file == null || !file.exists()) {
			// Try to resolve the relative uri using the working initial uri
			URI fileUri = URI.createFileURI(filePath);
			String fileRootSegment = fileUri.segment(0);
			File commonRoot = null;
			
			File queryFile = new File(initialUri.path().trim().replace("%20", " "));
			while(queryFile!=null && queryFile.exists()) {
				if(queryFile.isDirectory()) {
					for(File containedFile : queryFile.listFiles()) {
						if(containedFile.isDirectory() && containedFile.getName().equals(fileRootSegment)) {
							commonRoot = containedFile;
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
				throw new FileNotFoundException("Relative path "+filePath+" could not be resolved with the path of the initial uri "+initialUri.path().trim().replace("%20", " ")+"." );
			
			String newValidPath = commonRoot.getCanonicalPath()+"/"+fileUri.path();
			file = new File(newValidPath);
			if(file == null || !file.exists()) {
				throw new FileNotFoundException("Relative path "+filePath+" could not be resolved with the path of the initial uri "+initialUri.path().trim().replace("%20", " ")+". The following path does not exist: "+newValidPath);
			}
		}
			
		FileInputStream fis = new FileInputStream(file);
		Resource resource = new SmartEMFResource(URI.createURI(uri));
		
		JDOMXmiParser subParser = new JDOMXmiParser();
		subParser.addWaitingHRefs(waitingHRefs);
		subParser.load(fis, resource);
		fis.close();
		
		loadedResources.putAll(subParser.getLoadedResources());
		fqId2Object.putAll(subParser.getFqId2ObjectMap());
		rs.getResources().add(resource);
	}
	
	public void domTreeToModel(final Document domTree, final Resource resource) throws IOException {
		Element root = domTree.getRootElement();
		Namespace ns = root.getNamespace();
		List<Element> roots = new LinkedList<>();
		// If there is a root node belonging to the XMI Namespace, than we have multiple container objects wrapped within an XML dummy-node to ensure XML compliance
		if(XMI_NS.equals(ns.getPrefix())) {
			roots.addAll(root.getChildren());
		} else {
			roots.add(root);
		}
		
		int id = 0;
		for(Element subRoot : roots) {
			// Load the corresponding metamodel and factory
			Namespace subRootNS = subRoot.getNamespace();
			String metamodelUri = subRootNS.getURI();
			EPackage metamodel = EPackage.Registry.INSTANCE.getEPackage(metamodelUri);
			if(metamodel.eIsProxy()) {
				throw new IOException("No generated metamodel code found for: "+metamodelUri+", can not load model.");
			}
			EFactory factory = metamodel.getEFactoryInstance();
			ns2Package.put(subRootNS.getPrefix(), metamodel);
			ns2Factory.put(subRootNS.getPrefix(), factory);
			// find additional namespaces
			Set<Namespace> additionalNS = subRoot.getAdditionalNamespaces().stream()
				.filter(ns2 -> !ns2.getPrefix().equals(XMI_NS) && !ns2.getPrefix().equals(XSI_NS) && !ns2.getPrefix().equals(subRootNS.getPrefix()))
				.filter(ns2 -> !ns2Package.containsKey(ns2.getPrefix()))
				.collect(Collectors.toSet());
			
			for(Namespace ns2 : additionalNS) {
				EPackage additionalMetamodel = EPackage.Registry.INSTANCE.getEPackage(ns2.getURI());
				if(additionalMetamodel.eIsProxy()) {
					throw new IOException("No generated metamodel code found for: "+ns2.getURI()+", can not load model.");
				}
				EFactory additionalFactory = additionalMetamodel.getEFactoryInstance();
				ns2Package.put(ns2.getPrefix(), additionalMetamodel);
				ns2Factory.put(ns2.getPrefix(), additionalFactory);
			}
			
			// traverse tree and instantiate classes
			EObject eRoot = null;
			if(roots.size() > 1) {
				eRoot = parseDomTree(resource, subRoot, null, subRootNS.getPrefix(), resource.getURI().toString()+"#/"+id, "/"+id, 0);
				id++;
			} else {
				eRoot = parseDomTree(resource, subRoot, null, subRootNS.getPrefix(), resource.getURI().toString()+"#/", "/", 0);
			}
			resource.getContents().add(eRoot);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	protected EObject parseDomTree(final Resource resource, Element root, EReference containment, String namespace, String fqId, String id, int idx) throws IOException {
		EPackage metamodel = null;
		String currentNamespace = null;
		if(root.getNamespace().getPrefix() == null || root.getNamespace().getPrefix().isBlank()) {
			metamodel = ns2Package.get(namespace);
			currentNamespace = namespace;
		} else {
			metamodel = ns2Package.get(root.getNamespace().getPrefix());
			currentNamespace = root.getNamespace().getPrefix();
		}
		EFactory factory = ns2Factory.get(currentNamespace);
		
		EClass rootClass = null;
		String currentId = id;
		String currentFqId = fqId;
		String simpleId = null;
		String simpleFqId = null;
		
		if(containment == null) {
			rootClass = (EClass)metamodel.getEClassifier(root.getName());
		} else {
			Optional<Attribute> typeATR = root.getAttributes().stream()
					.filter(atr -> atr.getName().equals(XSI_TYPE) && atr.getNamespacePrefix().equals(XSI_NS)).findFirst();
			if(typeATR.isPresent()) {
				String[] exactType = typeATR.get().getValue().split(":");
				//This is obviously an element belonging to a foreign metamodel -> switch to suitable EPacakge and Factory but stay in the current namespace
				String metamodelNS = exactType[0];
				String className = exactType[1];
				metamodel = ns2Package.get(metamodelNS);
				factory = ns2Factory.get(metamodelNS);
				rootClass = (EClass)metamodel.getEClassifier(className);
			} else {
				rootClass = containment.getEReferenceType();
			}
			currentId = id + "/@" + containment.getName() + "." + idx;
			currentFqId = fqId + "/@" + containment.getName() + "." + idx;
			// This a workaround for a useless/annoying xml simplification that occurs when a child list is exactly of size 1, then the index is omitted.
			if(idx == 0) {
				simpleId = id + "/@" + containment.getName();
				simpleFqId = fqId + "/@" + containment.getName();
			}
		}
		
		EObject eRoot = factory.create(rootClass);
		
		id2Object.put(currentId, eRoot);
		fqId2Object.put(currentFqId, eRoot);
		if(simpleId != null) {
			id2Object.put(simpleId, eRoot);
			fqId2Object.put(simpleFqId, eRoot);
		}
			
		if(waitingCrossRefs.containsKey(currentId)) {
			waitingCrossRefs.get(currentId).forEach(waitingRef -> waitingRef.accept(eRoot));
		}
		if(simpleId != null && waitingCrossRefs.containsKey(simpleId)) {
			waitingCrossRefs.get(simpleId).forEach(waitingRef -> waitingRef.accept(eRoot));
		}
		
		if(waitingHRefs.containsKey(currentFqId)) {
			waitingHRefs.get(currentFqId).forEach(waitingRef -> waitingRef.accept(eRoot));
		}
		
		if(simpleFqId != null && waitingCrossRefs.containsKey(simpleFqId)) {
			waitingHRefs.get(simpleFqId).forEach(waitingRef -> waitingRef.accept(eRoot));
		}
		
		Map<EStructuralFeature,Integer> element2Idx = new HashMap<>();
		Map<EStructuralFeature, PendingEMFCrossReference> feature2CrossRef = new HashMap<>();
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
					EObject child = parseDomTree(resource, element, ref, currentNamespace, currentFqId, currentId, element2Idx.get(feature));
					objs.add(child);
					element2Idx.replace(feature, element2Idx.get(feature)+1);
				} else {
					EObject child = parseDomTree(resource, element, ref, currentNamespace, currentFqId, currentId, element2Idx.get(feature));
					eRoot.eSet(ref, child);
					element2Idx.replace(feature, element2Idx.get(feature)+1);
				}
			} else {
				if(isHyperref(element)) {
					PendingEMFCrossReference pendingCrossref = feature2CrossRef.get(ref);
					if(pendingCrossref == null) {
						pendingCrossref = new PendingEMFCrossReference(eRoot, ref, (int)root.getChildren().stream().filter(elt -> elt.getName().equals(ref.getName())).count());
						feature2CrossRef.put(ref, pendingCrossref);
					}
					Attribute href = element.getAttribute(HREF_ATR);
					String[] hrefPath = href.getValue().split("#");
					String modelUri = hrefPath[0];
					
					if(!loadedResources.containsKey(modelUri)) {
						load(modelUri, resource.getResourceSet());
					}
					
					if(fqId2Object.containsKey(href.getValue()))  {
						EObject hyperref = fqId2Object.get(href.getValue());
						
						if(ref.isMany()) {
							pendingCrossref.insertObject(hyperref, element2Idx.get(feature));
							element2Idx.replace(feature, element2Idx.get(feature)+1);
						} else {
							pendingCrossref.insertObject(hyperref, 0);
						}
						
						if(pendingCrossref.isCompleted()) {
							pendingCrossref.writeBack();
						}
					} else {
						List<Consumer<EObject>> pendingHRefs = waitingHRefs.get(href.getValue());
						if(pendingHRefs == null) {
							pendingHRefs = new LinkedList<>();
							waitingHRefs.put(href.getValue(), pendingHRefs);
						}
						
						// Make those variables final 'cause java ..
						final int currentIdx = element2Idx.get(feature);
						final PendingEMFCrossReference currentPending = pendingCrossref;
						pendingHRefs.add((eobj) -> {
							currentPending.insertObject(eobj, currentIdx);
							if(currentPending.isCompleted()) {
								currentPending.writeBack();
							}
						});
						element2Idx.replace(feature, element2Idx.get(feature)+1);
					}
				} else {
					throw new IOException("XML DOM-Tree child: "+element.getName()+" is neither, a hyperref nor in a containment!");
				}
				
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
				if(attribute.getValue().isBlank())
					continue;
				
				String[] entries = attribute.getValue().split(" ");
				final PendingEMFCrossReference pendingCrossRefs = new PendingEMFCrossReference(eRoot, ref, entries.length);
				for(int i = 0; i<entries.length; i++) {
					String entry = entries[i];
					if(ref.isMany()) {
						if(id2Object.containsKey(entry)) {
							pendingCrossRefs.insertObject(id2Object.get(entry), i);
						} else {
							// Remember this crossRef and wait for traversal
							List<Consumer<EObject>> otherCrossRefs = waitingCrossRefs.get(entry);
							if(otherCrossRefs == null) {
								otherCrossRefs = new LinkedList<>();
								waitingCrossRefs.put(entry, otherCrossRefs);
							}
							final int currentIdx = i;
							otherCrossRefs.add((eobj) -> {
								pendingCrossRefs.insertObject(eobj, currentIdx);
								if(pendingCrossRefs.isCompleted())
									pendingCrossRefs.writeBack();
							});
						}
					} else {
						if(id2Object.containsKey(entry)) {
							pendingCrossRefs.insertObject(id2Object.get(entry), 0);
						} else {
							// Remember this crossRef and wait for traversal
							List<Consumer<EObject>> otherCrossRefs = waitingCrossRefs.get(entry);
							if(otherCrossRefs == null) {
								otherCrossRefs = new LinkedList<>();
								waitingCrossRefs.put(entry, otherCrossRefs);
							}
							otherCrossRefs.add((eobj) -> {
								pendingCrossRefs.insertObject(eobj, 0);
								if(pendingCrossRefs.isCompleted())
									pendingCrossRefs.writeBack();
							});
						}
					}
				}
				if(pendingCrossRefs.isCompleted())
					pendingCrossRefs.writeBack();
				
			} else {
				EAttribute eAttribute = (EAttribute) feature;
				
				switch(attribute.getAttributeType()) {
				case CDATA:
					eRoot.eSet(eAttribute, stringToValue(factory, eAttribute, attribute.getValue()));
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
	
	public static boolean isHyperref(final Element element) {
		if(element == null)
			return false;
		
		Attribute atr = element.getAttribute(HREF_ATR);
		if(atr == null)
			return false;
		
		return true;
	}
	
	public static Object stringToValue(final EFactory factory, final EAttribute atr, final String value) throws IOException {
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
			return new SimpleDateFormat(value);
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
}

class PendingEMFCrossReference {
	final private EObject node;
	final private EReference reference;
	final private EObject[] crossRefs;
	private int insertedObjects = 0;
	
	public PendingEMFCrossReference(final EObject node, final EReference reference, int numOfRefs) {
		this.node = node;
		this.reference = reference;
		crossRefs = new EObject[numOfRefs];
	}
	
	public void insertObject(final EObject ref, int idx) {
		crossRefs[idx] = ref;
		insertedObjects++;
	}
	
	public boolean isCompleted() {
		return insertedObjects == crossRefs.length;
	}
	
	@SuppressWarnings("unchecked")
	public void writeBack() {
		if(reference.isMany()) {
			List<EObject> refs = (List<EObject>) node.eGet(reference);
			refs.addAll(Arrays.asList(crossRefs));
		} else {
			node.eSet(reference, crossRefs[0]);
		}
	}
}