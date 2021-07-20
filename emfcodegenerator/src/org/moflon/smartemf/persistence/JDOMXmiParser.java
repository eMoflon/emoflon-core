package org.moflon.smartemf.persistence;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class JDOMXmiParser {
	protected Map<String, List<Consumer<EObject>>> waitingCrossRefs = new HashMap<>();
	protected Map<String, EObject> id2Object = new HashMap<>();
	protected Map<String, EPackage> ns2Package = new HashMap<>();
	protected Map<String, EFactory> ns2Factory = new HashMap<>();
	final public static String XMI_NS = "xmi";
	final public static String XSI_NS = "xsi";
	final public static String XSI_TYPE = "type";
	
	public void domTreeToModel(final Document domTree, final Resource resource) throws IOException {
		id2Object = new HashMap<>();
		waitingCrossRefs = new HashMap<>();
		ns2Package = new HashMap<>();
		ns2Factory = new HashMap<>();
		
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
				eRoot = parseDomTree(subRoot, null, subRootNS.getPrefix(), "/"+id, 0);
				id++;
			} else {
				eRoot = parseDomTree(subRoot, null, subRootNS.getPrefix(), "/", 0);
			}
			resource.getContents().add(eRoot);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	protected EObject parseDomTree(Element root, EReference containment, String namespace, String id, int idx) throws IOException {
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
		String simpleId = null;
		
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
			
			// This a workaround for a useless/annoying xml simplification that occurs when a child list is exactly of size 1, then the index is omitted.
			if(idx == 0) {
				simpleId = id + "/@" + containment.getName();
			}
		}
		
		EObject eRoot = factory.create(rootClass);
		
		id2Object.put(currentId, eRoot);
		if(simpleId != null)
			id2Object.put(simpleId, eRoot);
		
		if(waitingCrossRefs.containsKey(currentId)) {
			waitingCrossRefs.get(currentId).forEach(waitingRef -> waitingRef.accept(eRoot));
		}
		if(simpleId != null && waitingCrossRefs.containsKey(simpleId)) {
			waitingCrossRefs.get(simpleId).forEach(waitingRef -> waitingRef.accept(eRoot));
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
					EObject child = parseDomTree(element, ref, currentNamespace, currentId, element2Idx.get(feature));
					objs.add(child);
					element2Idx.replace(feature, element2Idx.get(feature)+1);
				} else {
					EObject child = parseDomTree(element, ref, currentNamespace, currentId, element2Idx.get(feature));
					eRoot.eSet(ref, child);
					element2Idx.replace(feature, element2Idx.get(feature)+1);
				}
			} else {
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
				
				String[] entries = attribute.getValue().split(" ");
				
				for(String entry : entries) {
					if(ref.isMany()) {
						EList<EObject> objs = (EList<EObject>) eRoot.eGet(ref);
						if(id2Object.containsKey(entry)) {
							objs.add(id2Object.get(entry));
						} else {
							// Remember this crossRef and wait for traversal
							List<Consumer<EObject>> otherCrossRefs = waitingCrossRefs.get(entry);
							if(otherCrossRefs == null) {
								otherCrossRefs = new LinkedList<>();
								waitingCrossRefs.put(entry, otherCrossRefs);
							}
							otherCrossRefs.add((eobj) -> {
								objs.add(eobj);
							});
						}
					} else {
						if(id2Object.containsKey(entry)) {
							eRoot.eSet(ref, id2Object.get(entry));
						} else {
							// Remember this crossRef and wait for traversal
							List<Consumer<EObject>> otherCrossRefs = waitingCrossRefs.get(entry);
							if(otherCrossRefs == null) {
								otherCrossRefs = new LinkedList<>();
								waitingCrossRefs.put(entry, otherCrossRefs);
							}
							otherCrossRefs.add((eobj) -> {
								eRoot.eSet(ref, eobj);
							});
						}
					}
				}
				
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
