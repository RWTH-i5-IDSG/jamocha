package org.jamocha.rete.visualisation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

import org.jamocha.gui.icons.IconLoader;
import org.jamocha.rete.AlphaNodePredConstr;
import org.jamocha.rete.BaseAlpha2;
import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.EngineEvent;
import org.jamocha.rete.EngineEventListener;
import org.jamocha.rete.LIANode;
import org.jamocha.rete.ObjectTypeNode;
import org.jamocha.rete.RootNode;
import org.jamocha.rete.TerminalNode;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemoryImpl;

public class Visualiser implements ActionListener, MouseListener, EngineEventListener{
	protected JZoomableShapeContainer container;
	protected JMiniRadarShapeContainer radar;
	protected ViewGraphNode root;
	protected JButton zoomInButton, zoomOutButton, reloadButton;
	protected JToggleButton autoReloadButton;
	protected JTextArea dump;
	protected Rete engine;
	protected final int spaceHorizontal=10;
	protected final int spaceVertical=15;
	protected final int nodeHorizontal=45;
	protected final int nodeVertical=16;
	
	Color getBackgroundColorForNode(ViewGraphNode node) {
		Color bg=Color.black;
		if (node.getReteNode() instanceof TerminalNode) bg=Color.black;
		if (node.getReteNode() instanceof BaseJoin) bg=Color.green;
		if (node.getReteNode() instanceof LIANode) bg=Color.cyan;
		if (node.getReteNode() instanceof ObjectTypeNode) bg=Color.orange;
		if (node.getReteNode() instanceof AlphaNodePredConstr) bg=Color.red;
		if (node.getReteNode() instanceof BaseAlpha2) bg=Color.red;
		return bg;
	}
	
	Color getBorderColorForNode(ViewGraphNode node){
		return getBackgroundColorForNode(node).darker();
	}
	
	protected void addPrimitive(Primitive p){
		container.addPrimitive(p);
		radar.addPrimitive(p);
	}

	protected Shape makeShapeFromNode(ViewGraphNode act, LinkedList<ViewGraphNode> queue){
		Color bg=getBackgroundColorForNode(act);
		Color border=getBorderColorForNode(act);
		String desc="";
		if (act.getReteNode()!=null) desc=String.valueOf(act.getReteNode().getNodeId());
		Shape s;
		if (act.getReteNode()==null) { // ROOT NODE
			s=new Ellipse();
		} else if (act.getReteNode() instanceof BaseJoin || act.getReteNode() instanceof BaseAlpha2) {
			s=new Trapezoid();
		} else if (act.getReteNode() instanceof TerminalNode) {
			s=new RoundedRectangle();
		} else if (act.getReteNode() instanceof LIANode) {
			s=new Circle();
		} else{
			s=new Rectangle();
		}
			
		s.setBgcolor(bg);
		s.setBordercolor(border);
		s.setX((spaceHorizontal/2)+ (int)((float)(act.getX()*(spaceHorizontal+nodeHorizontal))/2.0));
		s.setY((spaceVertical/2)+act.getY()*(spaceVertical+nodeVertical));
		s.setWidth(nodeHorizontal);
		s.setHeight(nodeVertical);
		if (s instanceof Circle) s.incHeight(10);
		s.setText(desc);
		act.setShape(s);
		addPrimitive(s);
		for (Iterator<ViewGraphNode> it=act.getSuccessors().iterator();it.hasNext();) {
			ViewGraphNode n=it.next();
			queue.offer(n);
		}
		return s;
		
	}
	
	protected void createPrimitives(ViewGraphNode root){
		LinkedList<ViewGraphNode> queue=new LinkedList<ViewGraphNode>();
		queue.offer(root);
		while (!queue.isEmpty()) {
			ViewGraphNode act=queue.poll();
			Shape s=null;
			if (act.getShape()==null) {
				s=makeShapeFromNode(act, queue);
			} else {
				s=act.getShape();
			}
			if (act.isParentsChecked()) continue;
			act.setParentsChecked(true);
			for (Iterator<ViewGraphNode> it=act.getParents().iterator();it.hasNext();) {
				ViewGraphNode n=it.next();
				Shape s1=n.getShape();
				if (s1==null) s1=makeShapeFromNode(n,queue);
				ConnectorLine line=new ConnectorLine(s1,s);
				line.setColor(Color.blue);
				if (n.getReteNode() instanceof BaseJoin) line.setColor(Color.red);
				addPrimitive(line);
			}
		}
	}

	public Visualiser(Rete engine) {
		this.engine=engine;
		container = new JZoomableShapeContainer();
		container.addMouseListener(this);
		dump=new JTextArea();
	    radar=new JMiniRadarShapeContainer();
		radar.setMasterShapeContainer(container);
		container.setRadarShapeContainer(radar);
		reloadView();
	}
	
	
	public JPanel getVisualiserPanel() {
		JPanel panel = new JPanel();
		
		// ToolBox (Zoom button and so on)
		JPanel toolBox=new JPanel();
		GridLayout toolBoxLayout=new GridLayout(2,1);
		toolBox.setLayout(toolBoxLayout);
		zoomInButton=new JButton("Zoom In",IconLoader.getImageIcon("magnifier_zoom_in",Visualiser.class));
		zoomOutButton=new JButton("Zoom Out",IconLoader.getImageIcon("magnifier_zoom_out",Visualiser.class));
		
		zoomInButton.addActionListener(this);
		zoomOutButton.addActionListener(this);

		
	
		// Sidebar (Where Toolbox and InfoPanel is; NOT the radar)
		JPanel sideBar=new JPanel(new BorderLayout());

		sideBar.add(toolBox,BorderLayout.WEST);
		

	    JScrollPane scrollPane = new JScrollPane(dump);
	    JPanel dumpPanel = new JPanel(new BorderLayout());
        dumpPanel.add(scrollPane);
		
		sideBar.add(dumpPanel,BorderLayout.CENTER);
		
		
		dump.setText("This is the node dump area. Click on a node and you will get some information here\n--------------------\n");
		
		
		
		// Main Window with two Splitters (between radar, sidebar and main)
		panel.setLayout(new BorderLayout());
		JSplitPane sideSplitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,radar,sideBar);
		JSplitPane mainSplitPane=new JSplitPane(JSplitPane.VERTICAL_SPLIT,container,sideSplitPane);
		mainSplitPane.setResizeWeight(1.0);
		mainSplitPane.setOneTouchExpandable(true);
		sideSplitPane.setOneTouchExpandable(true);
		panel.add(mainSplitPane,BorderLayout.CENTER);
		
		// adding the reload button
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,5,1));
		buttonPanel.add(zoomInButton);
		buttonPanel.add(zoomOutButton);
		
		
		autoReloadButton = new JToggleButton("Automatic Reload",IconLoader.getImageIcon("arrow_refresh"));
		autoReloadButton.setSelected(false);
		autoReloadButton.addActionListener(this);
		buttonPanel.add(autoReloadButton);
		
		
		
		
		// create the button that clears the output area
		
		
		reloadButton = new JButton("Reload View",IconLoader.getImageIcon("arrow_refresh"));
		reloadButton.addActionListener(this);
		buttonPanel.add(reloadButton);
		panel.add(buttonPanel, BorderLayout.PAGE_END);
		
		return panel;
	}

	public void show(){
		JFrame frame = new JFrame("Sumatra - Rete Network - "+new Date());
		frame.getContentPane().add(getVisualiserPanel(),BorderLayout.CENTER);
		frame.pack();
		frame.setLocationByPlatform(true);
        frame.setVisible(true);
		frame.setSize(700,500);
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource()==zoomInButton) {
			container.zoomIn();
		} else if (arg0.getSource()==zoomOutButton) {
			container.zoomOut();
		} else if (arg0.getSource()==reloadButton) {
			reloadView();
		} else  if (arg0.getSource()==autoReloadButton) {
			if (autoReloadButton.isSelected()) {
				reloadButton.setEnabled(false);
				engine.addEngineEventListener(this);
			} else {
				reloadButton.setEnabled(true);
				engine.removeEngineEventListener(this);
			}
		}
		
	}

	private void reloadView() {
		RootNode root=((WorkingMemoryImpl)engine.getWorkingMemory()).getRootNode();
		ViewGraphNode t=ViewGraphNode.buildFromRete(root);
		this.root=t;
		container.removeAllPrimitives();
		radar.removeAllPrimitives();
		createPrimitives(t);

	}
	
	
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent event) {
		Shape shape=container.getShapeAtPosition(event.getX(), event.getY());
		if (shape==null) return;
		String nodeInfo=shape.getText();
		dump.setText(dump.getText()+nodeInfo+"\n");
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void eventOccurred(EngineEvent event) {
		// TODO Auto-generated method stub
		reloadView();
		
	}

}