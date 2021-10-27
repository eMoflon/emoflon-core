package org.moflon.core.ui.visualisation.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
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

public class EMoflonEcoreVisualizer implements EMoflonVisualizer {
	
	/**
	 * Stores whether or not the superset of Ecore elements can be retrieved from
	 * currently associated editor.
	 */
	private boolean isEmptySelectionSupported = false;
	
	/**
	 * Stores a subset of Ecore elements, that are to be visualised..
	 */
	private Collection<EObject> latestSelection;
	
	private Map<EClass, Node> eClass2Node = new LinkedHashMap<>();
	private Map<String, Integer> edgeCounter = new HashMap<>();
	private Set<EReference> traversedEdges = new HashSet<>();
	private SpriteManager sman;
	

	@Override
	public int getPriority() {
		return 0;
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
		
		// An Ecore metamodel must contain EModelElements only. If it contains other
		// elements, the selection is not supported by this visualiser.
		return !VisualiserUtilities.hasModelElements(latestSelection);
	}

	@Override
	public Graph transformSelection(IWorkbenchPart part, ISelection selection) {
		eClass2Node = new LinkedHashMap<>();
		edgeCounter = new HashMap<>();
		
		Graph classDiagram = new MultiGraph("Metamodel Viewer");
		sman = new SpriteManager(classDiagram);
		classDiagram.setAttribute("ui.stylesheet", ClassDiagramStyleSheet.getStyleSheet());
		
		latestSelection.stream().filter(EClass.class::isInstance)//
			.map(EClass.class::cast)//
			.forEach(eClass -> {
				createNodeFromEClass(classDiagram, eClass);
			});
		
		
		for(EClass eClass : eClass2Node.keySet()) {
			for(EReference ref : eClass.getEAllReferences()) {
				createEdgeFromReference(classDiagram, eClass, ref);				
			}
		}
		
		for(EClass eClass : eClass2Node.keySet().stream().flatMap(eClass -> eClass.getEAllSuperTypes().stream()).collect(Collectors.toSet())) {
			if(!eClass2Node.keySet().contains(eClass)) {
				createNodeFromEClass(classDiagram, eClass);
			}
		}
		
		for(EClass eClass : eClass2Node.keySet()) {
			eClass.getESuperTypes().stream().forEach(superType -> {
				createEdgeFromInheritance(classDiagram, eClass, superType);
			});
		}
		return classDiagram;
	}
	
	protected Node createNodeFromEClass(final Graph graph, final EClass eClass) {
		Node n = graph.addNode(eClass.getName());
		eClass2Node.put(eClass, n);
		
		String header = "Class: "+eClass.getName();
		int width = header.length();
		Sprite classLabel = sman.addSprite(n.getId()+"_classLabel");
		classLabel.setAttribute("ui.label", header);
		classLabel.attachToNode(n.getId());
		
		int height = 1;
		List<Sprite> atrLabels = new LinkedList<>();
		for(EAttribute atr : eClass.getEAttributes()) {
			StringBuilder sb = new StringBuilder();
			sb.append("+ ");
			sb.append(atr.getName());
			sb.append(" : ");
			sb.append(atr.getEType().getName());
			String entry = sb.toString();
			if(entry.length()>width)
				width = entry.length();
			
			Sprite atrLabel = sman.addSprite(n.getId()+"_atrLabel"+height);
			atrLabel.setAttribute("ui.label", entry);
			atrLabel.attachToNode(n.getId());
			atrLabels.add(atrLabel);
			height++;
		}
		
		height = height*20;
		width = width*8+6;
		int top = height / 2;
		int bottom = -height / 2;
		int center = 0;
		int left = -width / 2;
		int right = width / 2;
		
		n.setAttribute("ui.style", "size-mode: normal; size: "+width+"px, "+height+"px, 0px; stroke-mode: none; z-index: 1;");
		
		if(eClass.isAbstract()) {
			classLabel.setAttribute("ui.style", "size-mode: normal; size: "+width+"px, "+20+"px, 0px; text-style: bold-italic; z-index: 3;");
		}else {
			classLabel.setAttribute("ui.style", "size-mode: normal; size: "+width+"px, "+20+"px, 0px; text-style: bold; z-index: 3;");
		}
		
		if(atrLabels.size() == 0) {
			classLabel.setPosition(0);
		} else {
			classLabel.setPosition(StyleConstants.Units.PX, 0, top-10, 0);
			
			int begin = top-30;
			for(Sprite sp : atrLabels) {
				sp.setAttribute("ui.style", "size-mode: normal; size: "+width+"px, "+20+"px, 0px; stroke-mode: none; text-alignment: right; z-index: 2;");
				sp.setPosition(StyleConstants.Units.PX, left, begin, 0);
				begin -= 20;
			}
		}
		
		return n;
	}
	
	protected Edge createEdgeFromReference(final Graph graph, final EClass src, final EReference ref) {
		if(traversedEdges.contains(ref))
			return null;
		
		Edge edge = null;
		if(eClass2Node.containsKey(ref.getEType()) && !ref.isContainment()) {
			edge = graph.addEdge(createUniqueEdgeLabel(ref), eClass2Node.get(src), eClass2Node.get((EClass)ref.getEType()), true);
			edge.setAttribute("ui.style", "arrow-shape: arrow; arrow-size: 14px, 8px; z-index: 0;");
		} else if (!eClass2Node.containsKey(ref.getEType()) && !ref.isContainment()) {
			edge = graph.addEdge(createUniqueEdgeLabel(ref), eClass2Node.get(src), createNodeFromEClass(graph, (EClass)ref.getEType()), true);
			edge.setAttribute("ui.style", "arrow-shape: arrow; arrow-size: 14px, 8px; z-index: 0;");
		} else if (eClass2Node.containsKey(ref.getEType()) && ref.isContainment()) {
			edge = graph.addEdge(createUniqueEdgeLabel(ref), eClass2Node.get((EClass)ref.getEType()), eClass2Node.get(src), true);
			edge.setAttribute("ui.style", "arrow-shape: diamond; arrow-size: 18px, 14px; z-index: 0;");
		} else {
			edge = graph.addEdge(createUniqueEdgeLabel(ref), createNodeFromEClass(graph, (EClass)ref.getEType()), eClass2Node.get(src), true);
			edge.setAttribute("ui.style", "arrow-shape: diamond; arrow-size: 18px; z-index: 0;");
		}
		edge.setAttribute("ui.label", ref.getName());
		
//		if(ref.isContainment()) {
//			Sprite containment = sman.addSprite(edge.getId()+"_containment");
//			containment.setAttribute("ui.style", "shape: diamond; sprite-orientation: from; size: 20px; stroke-mode: plain;");
//			containment.setPosition(0.1);
//			containment.attachToEdge(edge.getId());
//			
//		} else if(ref.getEOpposite() != null) {
//			Sprite source = sman.addSprite(edge.getId()+"_source");
//			source.setAttribute("ui.style", "shape: arrow; sprite-orientation: from; size: 20px; stroke-mode: plain;");
//			source.setPosition(0.1);
//			source.attachToEdge(edge.getId());
//		}
//		
//		Sprite target = sman.addSprite(edge.getId()+"_target");
//		target.setAttribute("ui.style", "shape: arrow; sprite-orientation: to; size: 20px; stroke-mode: plain;");
//		target.setPosition(0.9);
//		target.attachToEdge(edge.getId());
		
		if(!ref.isContainment() && ref.getEOpposite() != null) {
			Edge reverseEdge = graph.addEdge(createUniqueEdgeLabel(ref), eClass2Node.get((EClass)ref.getEType()), eClass2Node.get(src), true);
			reverseEdge.setAttribute("ui.style", "arrow-shape: arrow; arrow-size: 14px, 8px; z-index: 0;");
			traversedEdges.add(ref.getEOpposite());
			reverseEdge.setAttribute("ui.label", ref.getEOpposite().getName());
		}
		
		traversedEdges.add(ref);
		return edge;
	}
	
	protected Edge createEdgeFromInheritance(final Graph graph, final EClass subClass, final EClass baseClass) {
		String label = "inheritance: "+subClass.getName()+", from: "+baseClass.getName();
		Edge edge = graph.addEdge(label, eClass2Node.get(subClass), eClass2Node.get(baseClass), true);
		
		edge.setAttribute("ui.style", "arrow-shape: arrow; arrow-size: 14px, 10px; fill-color: white; stroke-color: black; stroke-mode: plain; stroke-width: 1.5px; z-index: 0;");

		
		return edge;
	}
	
	protected String createUniqueEdgeLabel(final EReference ref) {
		String label = null;
		if(edgeCounter.containsKey(ref.getName())) {
			label = ref.getName()+"_"+edgeCounter.get(ref.getName());
			edgeCounter.replace(ref.getName(), edgeCounter.get(ref.getName())+1);
		} else {
			label = ref.getName()+"_"+0;
			edgeCounter.put(ref.getName(), 1);
		}
		return label;
	}
	
	public static String createDivider(int length) {
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<length; i++) {
			builder.append("-");
		}
		return builder.toString();
	}

}
