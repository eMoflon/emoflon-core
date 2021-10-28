package org.moflon.core.ui.visualisation.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.moflon.core.ui.VisualiserUtilities;
import org.moflon.core.ui.visualisation.metamodels.ClassDiagramStyleSheet;

public class EMoflonXmiVisualizer implements EMoflonVisualizer {
	
	/**
	 * Stores a subset of Ecore elements, that are to be visualised..
	 */
	private Collection<EObject> latestSelection;
	private Map<EObject, Node> object2node;
	private Map<String, Integer> objectLabels;
	private Map<String, Integer> edgeLabels;
	private SpriteManager sman;

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public boolean selectionIsRelevant(IWorkbenchPart part, ISelection selection) {
		if(!(part instanceof IEditorPart))
			return false;
		
		IEditorPart editor = (IEditorPart) part;
		
		// check if editor currently has Ecore related model loaded
		boolean hasEcoreFileLoaded = VisualiserUtilities.checkFileExtensionSupport(editor, "ecore")
						|| VisualiserUtilities.checkFileExtensionSupport(editor, "xmi");

		// Check if the editor internally handles Ecore EObjects.
		// Since some editors allow to load both .ecore and .xmi Resources at the same
		// time, it is not possible to check for specific elements from Ecore metamodels
		// or models. This has to be done in #supportsSelection(...), when it is clear
		// whether the selection is empty or not.
		Collection<EObject> extracted = VisualiserUtilities.extractEcoreElements(editor);
		if(extracted == null || extracted.isEmpty())
			return false;

		// if only one of the above conditions is true, there is still a possibility
		// that a given selection might be supported
		if(!hasEcoreFileLoaded)
			return false;
		
		// only Ecore selections are supported
		if (!VisualiserUtilities.isEcoreSelection(selection)) {
			return false;
		}

		// empty Ecore selections are supported only if the editor can provide Ecore
		// elements, this is checked and remembered in supportsEditor(...)
		Collection<EObject> ecoreSelection = VisualiserUtilities.extractEcoreSelection(selection);
		if (ecoreSelection == null || ecoreSelection.isEmpty()) {
			latestSelection = extracted;
		} else {
			latestSelection = ecoreSelection;
		}
		
		/// An Ecore model must contain EObjects only, which are not EModelElements. If
		// it contains other
		// elements, the selection is not supported by this visualiser.
		return !VisualiserUtilities.hasMetamodelElements(latestSelection);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Graph transformSelection(IWorkbenchPart part, ISelection selection) {
		object2node = new HashMap<>();
		objectLabels = new HashMap<>();
		edgeLabels = new HashMap<>();
		Graph model = new MultiGraph("XMI-Model Viewer");
		model.setAttribute("ui.stylesheet", ClassDiagramStyleSheet.getStyleSheet());
		sman = new SpriteManager(model);
		
		for(EObject obj : latestSelection) {
			createNodeFromEObject(model, obj);
		}
		
		for(EObject obj : latestSelection) {
			EClass eClass = obj.eClass();
			for(EReference ref : eClass.getEAllReferences()) {
				if(obj.eGet(ref) == null)
					continue;
				
				if(ref.isMany()) {
					List<EObject> trgs = (List<EObject>) obj.eGet(ref);
					for(EObject trg : trgs) {
						createEdgeFromReference(model, obj, trg, ref);
					}
				} else {
					EObject trg = (EObject) obj.eGet(ref);
					createEdgeFromReference(model, obj, trg, ref);
				}
			}
		}
		
//		// Put the container object in the origin of the coordinate system
//		EObject root = latestSelection.iterator().next().eResource().getContents().iterator().next();
//		Node rootNode = object2node.get(root);
//			
//		rootNode.setAttribute("layout.frozen");
//		rootNode.setAttribute("x", 0);
//		rootNode.setAttribute("y", 0);
//		rootNode.setAttribute("z", 0.0);
//		
//		object2node.values().forEach(eObj->model.addEdge(rootNode.getId()+"->"+eObj.getId(), rootNode, eObj, true));
		
		return model;
	}
	
	protected Node createNodeFromEObject(final Graph graph, final EObject obj) {
		String name = createUniqueObjectLabel(obj);
		Node n = graph.addNode(name);
		object2node.put(obj, n);
		
		EClass eClass = obj.eClass();
		String type = eClass.getName();
		String label = name + " : " +type;
		
		if(label.length() > 20) {
			setComplexLabel(n, name, type);
		} else {
			n.setAttribute("ui.style", "z-index: 1;");
			n.setAttribute("ui.label", label);
		}
		
		return n;
	}
	
	protected String createUniqueObjectLabel(final EObject obj) {
		String label = null;
		EClass eClass = obj.eClass();
		Optional<EAttribute> nameAtr = eClass.getEAllAttributes().stream().filter(atr -> atr.getName().equalsIgnoreCase("name") && atr.getEType() == EcorePackage.Literals.ESTRING).findFirst();
		if(nameAtr.isPresent() && obj.eGet(nameAtr.get()) != null && !((String)obj.eGet(nameAtr.get())).isEmpty()) {
			label = (String) obj.eGet(nameAtr.get());
		} else {
			label = eClass.getName();
		}
		
		if(objectLabels.containsKey(label)) {
			int num = objectLabels.get(label);
			objectLabels.replace(label, num+1);
			label = label + "_" + num;
			
		} else {
			objectLabels.put(label, 1);
		}

		return label;
	}
	
	protected void setComplexLabel(final Node n, final String name, final String type) {
		int width = ((name.length()>type.length())?name.length():type.length()) * 8 + 6;
		final int height = 40;
		int top = height / 2;
		int left = -width / 2;
		
		n.setAttribute("ui.style", "size-mode: normal; size: "+(width+4)+"px, "+(height+4)+"px, 0px; z-index: 1;");
		
		Sprite objName = sman.addSprite(n.getId()+"_name");
		objName.setAttribute("ui.style", "size-mode: normal; size: "+width+"px, "+20+"px, 0px; stroke-mode: none; z-index: 2;");
		objName.setPosition(StyleConstants.Units.PX, 0, top-10, 0);
		objName.setAttribute("ui.label", name + " :");
		objName.attachToNode(n.getId());
		
		Sprite objType = sman.addSprite(n.getId()+"_type");
		objType.setAttribute("ui.style", "size-mode: normal; size: "+width+"px, "+20+"px, 0px; stroke-mode: none; z-index: 2;");
		objType.setPosition(StyleConstants.Units.PX, 0, top-30, 0);
		objType.setAttribute("ui.label", type);
		objType.attachToNode(n.getId());
	}
	
	protected Edge createEdgeFromReference(final Graph graph, final EObject src, final EObject trg, final EReference ref) {
		Edge edge = null;
		Node srcNode = object2node.get(src);
		Node trgNode = object2node.get(trg);
		if(trgNode != null) {
			edge = graph.addEdge(createUniqueEdgeLabel(srcNode, trgNode, ref), srcNode, trgNode, true);
		} else {
			trgNode = createNodeFromEObject(graph, trg);
			edge = graph.addEdge(createUniqueEdgeLabel(srcNode, trgNode, ref), srcNode, trgNode, true);
		}
		edge.setAttribute("ui.style", "arrow-shape: arrow; arrow-size: 10px, 6px; z-index: 0;");
		edge.setAttribute("ui.label", ref.getName());
		
		return edge;
	}
	
	protected String createUniqueEdgeLabel(final Node src, final Node trg, final EReference ref) {
		String label = src.getId() +"-"+ref.getName()+"->"+trg.getId();
		if(edgeLabels.containsKey(label)) {
			int num = edgeLabels.get(label);
			edgeLabels.replace(label, num+1);
			label = label + "_#" + num;
		} else {
			edgeLabels.put(label, 1);
		}
		return label;
	}
}
