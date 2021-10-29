package org.moflon.core.ui.visualisation;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.InteractiveElement;
import org.moflon.core.ui.visualisation.common.EMoflonVisualizer;
import org.moflon.core.utilities.ExtensionsUtil;

public class ModelViewer extends ViewPart{

	private Composite parent;
	private Graph graph;
	private Composite composite;
	private Viewer graphStreamViewer;
	private DefaultView graphStreamView;
//	private Frame graphStreamFrame;
	private GraphicElement selectedElement = null;
	
	private final ModelChangedListener modelChangedListener = new ModelChangedListener();

	private EnumSet<InteractiveElement> types = EnumSet.of(InteractiveElement.NODE);

	@Override
	public void createPartControl(Composite parent) {
		parent.getDisplay().asyncExec( new Runnable() {

			@Override
			public void run() {
				ModelViewer.this.parent = parent;
				composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
				composite.setLayout(new GridLayout(1, false));
				composite.setVisible(true);
				
				graph = new MultiGraph("Empty Graph");
				Node n = graph.addNode("Empty_Node");
				n.setAttribute("ui.label", "Nothing to visualize");
				n.setAttribute("ui.style", "text-size: 32; size-mode: fit; shape: box; fill-mode: none;");
				graphStreamViewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
//				graphStreamFrame = SWT_AWT.new_Frame(composite);
				graphStreamView = (DefaultView) graphStreamViewer.addDefaultView(true);
//				graphStreamFrame.add(graphStreamView);
				
				graphStreamViewer.getDefaultView().enableMouseOptions();

				addInputListeners();
				addMenuActions();
				addListeners();
				
//				graphStreamView.resizeFrame(parent.getBounds().width, parent.getBounds().height);
				graphStreamView.getCamera().setAutoFitView(true);
				Toolkit.computeLayout(graph);
				
				Point3 graphCenter = calcGeometricCenter();
				graphStreamView.getCamera().setViewCenter(graphCenter.x, graphCenter.y, graphStreamView.getCamera().getViewCenter().z);
			}
			
		});
	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().removePostSelectionListener(modelChangedListener);
	}

	@Override
	public void setFocus() {
		if(composite == null)
			return;
		
		composite.setFocus();
	}
	
	@Override
	public void init(final IViewSite site, final IMemento memento) throws PartInitException {
		super.init(site, memento);
		System.setProperty("org.graphstream.ui", "swing"); 
	}

	private void addInputListeners() {
		graphStreamView.addMouseWheelListener(new MouseWheelListener() {
		    @Override
		    public void mouseWheelMoved(MouseWheelEvent e) {
		        e.consume();
		        int i = e.getWheelRotation();
		        double factor = Math.pow(1.25, i);
		        Camera cam = graphStreamView.getCamera();
		        cam.setViewPercent(cam.getViewPercent() * factor);
		    }
		});
		
		graphStreamView.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				GraphicElement element = graphStreamView.findGraphicElementAt(types, e.getX()*1.5, e.getY()*1.5);
				if(element != null){
					if(selectedElement == null || !selectedElement.equals(element)) {
						selectedElement = element;
					}
		        }
				e.consume();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				selectedElement = null;
				e.consume();
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
			
		});
		
		graphStreamView.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				Point3 dragged3D = graphStreamView.getCamera().transformPxToGu(e.getX()*1.5, e.getY()*1.5);

				if(selectedElement != null){
					Node node = graph.getNode(selectedElement.getId());
					node.setAttribute("layout.frozen");
					node.setAttribute("x", dragged3D.x);
					node.setAttribute("y", dragged3D.y);
					node.setAttribute("z", 0.0);
					e.consume();
		        }
			}

			@Override
			public void mouseMoved(MouseEvent e) {}
			
		});
	}
	
	private void addMenuActions() {
		final IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		Action autoLayout = new Action() {
			@Override
			public void run() {
				parent.getDisplay().asyncExec( new Runnable() {
					@Override
					public void run() {
						activateLayout();
					}
				});
				
			}
		};
		autoLayout.setText("AutoLayout");
		toolBarManager.add(autoLayout);
		
		
		final IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		final MenuManager editorSelectionActionMenu = new MenuManager("Model Viewer");
		editorSelectionActionMenu.add(new Action() {}); // will be removed, needed for the submenu to actually show
		editorSelectionActionMenu.setRemoveAllWhenShown(true);
		editorSelectionActionMenu.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(final IMenuManager menu) {
				menu.add(autoLayout);
			}
		});
		menu.add(editorSelectionActionMenu);	
	}
	
	private void addListeners() {
		getSite().getPage().addPostSelectionListener(modelChangedListener);
	}
	
	public void updateGraphView(final Graph newGraph) {
		parent.getDisplay().asyncExec( new Runnable() {

			@Override
			public void run() {
				composite.dispose();
				
				graph = newGraph;
				
				composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
				composite.setLayout(new GridLayout(1, false));
				composite.setVisible(true);
				
				graphStreamViewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
//				graphStreamFrame = SWT_AWT.new_Frame(composite);
				graphStreamView = (DefaultView) graphStreamViewer.addDefaultView(true);
//				graphStreamFrame.add((DefaultView)graphStreamView);

				graphStreamViewer.getDefaultView().enableMouseOptions();
				
				addInputListeners();
				
//				graphStreamView.resizeFrame(parent.getBounds().width, parent.getBounds().height);
				
//				activateLayout();
			}
			
		});
		
	}

	private class ModelChangedListener implements ISelectionListener{
		@Override
		public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
			Collection<EMoflonVisualizer> plugins = collectVisualizerExtensions();
			for(EMoflonVisualizer plugin : plugins) {
				if(plugin.selectionIsRelevant(part, selection)) {
					Graph graph = plugin.transformSelection(part, selection);
					if(graph != null)
						updateGraphView(graph);
					
					return;
				}
			}
		}
	}
	
	/**
	 * Collects the eMoflon visualizer extensions.
	 * 
	 * @return the extensions
	 */
	private Collection<EMoflonVisualizer> collectVisualizerExtensions() {
		return ExtensionsUtil.collectExtensions(EMoflonVisualizer.PLUGIN_ID, "class",
				EMoflonVisualizer.class);
	}
	
	private void activateLayout() {
//		graphStreamView.resizeFrame(parent.getBounds().width, parent.getBounds().height);
		
		Toolkit.computeLayout(graph);
		
		graphStreamView.getCamera().setAutoFitView(true);
		graphStreamView.getCamera().resetView();
		graphStreamView.getCamera().setViewPercent(2.5);
		
		Point3 graphCenter = calcGeometricCenter();
		graphStreamView.getCamera().setViewCenter(graphCenter.x, graphCenter.y, graphStreamView.getCamera().getViewCenter().z);
		
		composite.requestLayout();
//		composite.layout();
//		composite.redraw();
		composite.setVisible(true);
	}
	
	private Point3 calcGeometricCenter() {
		int num = 0;
		double x = 0;
		double y = 0;
		double z = 0;
		for(Node n : graph.nodes().collect(Collectors.toList())) {
			Point3 pos = Toolkit.nodePointPosition(n);
			x += pos.x;
			y += pos.y;
			z += pos.z;
			num++;
		}
		
		return new Point3(x/num, y/num, z/num);
	}

}
