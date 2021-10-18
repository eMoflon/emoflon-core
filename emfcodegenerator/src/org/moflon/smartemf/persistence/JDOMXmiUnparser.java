package org.moflon.smartemf.persistence;

import java.awt.Container;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.moflon.smartemf.runtime.SmartPackage;

public class JDOMXmiUnparser {

		Map<EPackage, Namespace> metamodel2NS = new HashMap<> ();
		Map<EObject, String> object2ID = new HashMap<>();
		Map<EObject, Set<Consumer<String>>> waitingCrossRefs = new HashMap<>();
	
		public void modelToJDOMTree(final Resource resource, final Document domTree) throws IOException {
			// Create tree by traversing model
			if(resource.getContents().size()> 1 || resource.getContents().size()< 1) {
				Element root = new Element(XmiParserUtil.XMI_ROOT_NODE, XmiParserUtil.XMI_NS, XmiParserUtil.XMI_URI);
				domTree.setRootElement(root);
				
				int idx = 0;
				for(EObject container : resource.getContents()) {
					root.getChildren().add(createDOMTree(container, container, null, "/"+idx, 0, false));
					idx++;
				}
			} else {
				EObject container = resource.getContents().get(0);
				domTree.setRootElement(createDOMTree(container, container, null, "/", 0, false));
			}
			
			// Resolve unresolved Cross-Refs -> These are HRefs to foreign models
			for(EObject href : waitingCrossRefs.keySet()) {
				if(href.eResource() == resource) {
					throw new IOException("Cross-reference to "+href+" could not be resolved during saving.");
				}
				
				// Explore and index foreign model
				if(!object2ID.containsKey(href)) {
					indexForeignResource(href.eResource());
				}
				
				String foreignID = object2ID.get(href);
				waitingCrossRefs.get(href).forEach(waiting -> waiting.accept(foreignID));
			}
			
			// Add default xmi namespaces and version attribute
			if(resource.getContents().size() <= 1) {
				Namespace xmiNS = Namespace.getNamespace(XmiParserUtil.XMI_NS, XmiParserUtil.XMI_URI);
				domTree.getRootElement().addNamespaceDeclaration(xmiNS);
			}
			
			Namespace xsiNS = Namespace.getNamespace(XmiParserUtil.XSI_NS, XmiParserUtil.XSI_URI);
			domTree.getRootElement().addNamespaceDeclaration(xsiNS);
			
			for(EPackage pkg : metamodel2NS.keySet()) {
				domTree.getRootElement().addNamespaceDeclaration(metamodel2NS.get(pkg));
			}
			
			domTree.getRootElement().getAttributes().add(new Attribute(XmiParserUtil.XMI_VERSION_ATR, XmiParserUtil.XMI_VERSION, Namespace.getNamespace(XmiParserUtil.XMI_NS, XmiParserUtil.XMI_URI)));
		}
		
		@SuppressWarnings("unchecked")
		protected Element createDOMTree(EObject root, EObject currentEObject, EReference containment, String id, int idx, boolean useSimple) throws IOException {
			EClass currentClass = currentEObject.eClass();
			EPackage metamodel = currentClass.getEPackage();
			
			Namespace ns = null;
			if(!metamodel2NS.containsKey(metamodel)) {
				ns = Namespace.getNamespace(metamodel.getName(), metamodel.getNsURI());
				metamodel2NS.put(metamodel, ns);
			} else {
				ns = metamodel2NS.get(metamodel);
			}
			
			String currentId = id;
			Element current = null;
			if(containment != null) {
				current = new Element(containment.getName());
				EPackage rootMetamodel = root.eClass().getEPackage();
				if(metamodel != rootMetamodel) {
					current.getAttributes().add(new Attribute(XmiParserUtil.XSI_TYPE, metamodel2NS.get(metamodel).getPrefix()+":"+currentClass.getName(), 
							Namespace.getNamespace(XmiParserUtil.XSI_NS, XmiParserUtil.XSI_URI)));
				}
				
				// This a workaround for a useless/annoying xml simplification that occurs when a child list is exactly of size 1, then the index is omitted.
				if(useSimple) {
					currentId = id + "/@" + containment.getName();
				} else {
					currentId = id + "/@" + containment.getName() + "." + idx;
				}
				
			} else {
				current = new Element(currentClass.getName(), ns);
			}
			
			object2ID.put(currentEObject, currentId);
			if(waitingCrossRefs.containsKey(currentEObject)) {
				for(Consumer<String> consumer : waitingCrossRefs.get(currentEObject)) {
					consumer.accept(currentId);
				}
				waitingCrossRefs.remove(currentEObject);
			}
			
			// Containments
			for(EReference containmentRef : currentClass.getEAllContainments()) {
				List<EObject> containees = new LinkedList<>();
				if(containmentRef.isMany()) {
					containees.addAll((Collection<? extends EObject>) currentEObject.eGet(containmentRef));
				} else {
					EObject containee = (EObject) currentEObject.eGet(containmentRef);
					if(containee != null)
						containees.add(containee);
				}
				int idIDX = 0;
				for(EObject containee : containees) {
					current.getChildren().add(createDOMTree(root, containee, containmentRef, currentId, idIDX, !containmentRef.isMany()));
					idIDX++;
				}
			}
			
			// Crossrefs
			for(EReference crossRef : currentClass.getEAllReferences().stream()
					.filter(ref -> !ref.isContainment())
					.filter(ref -> {
						if(!(metamodel instanceof SmartPackage)) {
							return true;
						} else {
							SmartPackage smartMetamodel = (SmartPackage) metamodel;
							return !smartMetamodel.isDynamicEStructuralFeature(currentClass, ref);
						}
					}).collect(Collectors.toList())) {
				
				LinkedList<EObject> refs = new LinkedList<>();
				if(crossRef.isMany()) {
					refs.addAll((Collection<? extends EObject>) currentEObject.eGet(crossRef));
				} else {
					EObject currentRef = (EObject) currentEObject.eGet(crossRef);
					if(currentRef != null)
						refs.add(currentRef);
				}
				if(refs.isEmpty())
					continue;
				
				
				Attribute refAtr = new Attribute(crossRef.getName(), "");
				PendingXMLCrossReference pendingCrossRefs = new PendingXMLCrossReference(current, refAtr, refs.size());
				if(refs.size()<2) {
					EObject refObj = refs.get(0);
					//Ignore dangling references
					if(refObj.eResource() == null)
						continue;
					
					if(object2ID.containsKey(refObj)) {
						pendingCrossRefs.insertID(object2ID.get(refObj), 0);
					} else {
						// Remember this crossRef and wait for traversal
						Set<Consumer<String>> otherPendingRefs = waitingCrossRefs.get(refObj);
						if(otherPendingRefs == null) {
							otherPendingRefs = new LinkedHashSet<>();
							waitingCrossRefs.put(refObj, otherPendingRefs);
						}
						
						if(refObj.eResource() == currentEObject.eResource()) {
							otherPendingRefs.add((crossRefID) -> {
								pendingCrossRefs.insertID(crossRefID, 0);
								if(pendingCrossRefs.isCompleted()) {
									pendingCrossRefs.writeBack();
								}
							});
						} else {
							// this is reference to a foreign element -> href!
							otherPendingRefs.add((crossRefID) -> {
								pendingCrossRefs.insertID(crossRefID, 0);
								EPackage otherMetamodel = refObj.eClass().getEPackage();
								if(!metamodel2NS.containsKey(otherMetamodel)) {
									Namespace otherNS =  Namespace.getNamespace(otherMetamodel.getName(), otherMetamodel.getNsURI());
									metamodel2NS.put(otherMetamodel, otherNS);
								}
								if(otherMetamodel != metamodel) {
									pendingCrossRefs.elementIsHref(true, 0, crossRef.getName(), metamodel2NS.get(otherMetamodel).getPrefix()+":"+refObj.eClass().getName());
								} else {
									pendingCrossRefs.elementIsHref(true, 0, crossRef.getName(), null);
								}
								
								if(pendingCrossRefs.isCompleted()) {
									pendingCrossRefs.writeBack();
								}
							});
						}
						
					}
					
					
				} else if(refs.size()>1) {
					int crossRefIdx = 0;
					for(EObject ref : refs) {
						//Ignore dangling references
						if(ref.eResource() == null)
							continue;
						
						if(object2ID.containsKey(ref)) {
							pendingCrossRefs.insertID(object2ID.get(ref), crossRefIdx);
						} else {
							// Remember this crossRef and wait for traversal
							Set<Consumer<String>> otherPendingRefs = waitingCrossRefs.get(ref);
							if(otherPendingRefs == null) {
								otherPendingRefs = new LinkedHashSet<>();
								waitingCrossRefs.put(ref, otherPendingRefs);
							}
							final int currentIdx = crossRefIdx;
							
							if(ref.eResource() == currentEObject.eResource()) {
								otherPendingRefs.add((crossRefID) -> {
									pendingCrossRefs.insertID(crossRefID,  currentIdx);
									if(pendingCrossRefs.isCompleted()) {
										pendingCrossRefs.writeBack();
									}
								});
							} else {
								// this is reference to a foreign element -> href!
								otherPendingRefs.add((crossRefID) -> {
									pendingCrossRefs.insertID(crossRefID,  currentIdx);
									EPackage otherMetamodel = ref.eClass().getEPackage();
									if(!metamodel2NS.containsKey(otherMetamodel)) {
										Namespace otherNS =  Namespace.getNamespace(otherMetamodel.getName(), otherMetamodel.getNsURI());
										metamodel2NS.put(otherMetamodel, otherNS);
									}
									if(otherMetamodel != metamodel) {
										pendingCrossRefs.elementIsHref(true, currentIdx, crossRef.getName(), metamodel2NS.get(otherMetamodel).getPrefix()+":"+ref.eClass().getName());
									} else {
										pendingCrossRefs.elementIsHref(true, currentIdx, crossRef.getName(), null);
									}
									
									if(pendingCrossRefs.isCompleted()) {
										pendingCrossRefs.writeBack();
									}
								});
							}
							
						}
						crossRefIdx++;	
					}
					
				}
				
				if(pendingCrossRefs.isCompleted())
					pendingCrossRefs.writeBack();
				
			}
			
			// Attributes
			EFactory factory = metamodel.getEFactoryInstance();
			for(EAttribute attribute : currentClass.getEAllAttributes()) {
				String value = XmiParserUtil.valueToString(factory, attribute, currentEObject.eGet(attribute));
				if(value == null)
					continue;
				
				Attribute refAtr = new Attribute(attribute.getName(), value);
				current.getAttributes().add(refAtr);
			}
			return current;
		}
		
		protected void indexForeignResource(final Resource resource) {
			if(resource.getContents().size() < 2) {
				indexForeignModel(resource.getContents().get(0), null, resource.getURI().toString()+"#/", 0, false);
			} else {
				int idx = 0;
				for(EObject container : resource.getContents()) {
					indexForeignModel(container, null, resource.getURI().toString()+"#/"+idx++, 0, false);
				}
			}
			
		}
		
		@SuppressWarnings("unchecked")
		protected void indexForeignModel(final EObject root, final EReference containment, final String id, int idx, boolean useSimple) {
			EClass rootClass = root.eClass();
			String rootId = id;
			if(containment != null) {
				if(useSimple) {
					rootId = id + "/@" + containment.getName();
				} else {
					rootId = id + "/@" + containment.getName() + "." + idx;
				}
			}
			object2ID.put(root, rootId);
			
			// Containments
			for(EReference containmentRef : rootClass.getEAllContainments()) {
				List<EObject> containees = new LinkedList<>();
				if(containmentRef.isMany()) {
					containees.addAll((Collection<? extends EObject>) root.eGet(containmentRef));
				} else {
					EObject containee = (EObject) root.eGet(containmentRef);
					if(containee != null)
						containees.add(containee);
				}
				int idIDX = 0;
				for(EObject containee : containees) {
					indexForeignModel(containee, containmentRef, rootId, idIDX, !containmentRef.isMany());
					idIDX++;
				}
			}
		}

}