package org.jamocha.rete.visualisation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jamocha.gui.icons.IconLoader;
import org.jamocha.rete.Rete;
import org.jamocha.rete.eventhandling.EngineEvent;
import org.jamocha.rete.eventhandling.EngineEventListener;
import org.jamocha.rete.memory.WorkingMemoryImpl;
import org.jamocha.rete.modules.Module;
import org.jamocha.rete.nodes.AbstractBeta;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.LIANode;
import org.jamocha.rete.nodes.ObjectTypeNode;
import org.jamocha.rete.nodes.RootNode;
import org.jamocha.rete.nodes.SlotAlpha;
import org.jamocha.rete.nodes.TerminalNode;
import org.jamocha.rule.Rule;

/**
 * @author Josef Alexander Hahn
 * 
 * a class which can visualise a rete-network this class does not extend a
 * swing-class since it has a method show for creating and opening a window. but
 * you can get a JPanel by calling getVisualiserPanel. That JPanel-instance you
 * can embed somewhere.
 */
public class Visualiser implements ActionListener, MouseListener, EngineEventListener, ListSelectionListener {

	class RuleSelectorPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		JList list;

		List<ListSelectionListener> listeners;

		public RuleSelectorPanel(Vector<String> rules) {
			listeners = new ArrayList<ListSelectionListener>();
			setRules(rules);
			this.setLayout(new GridLayout(1, 1));
		}

		public void synchronize() {
			for (ListSelectionListener l : listeners) {
				list.addListSelectionListener(l);
			}
		}

		public void addListSelectionListener(ListSelectionListener listener) {
			listeners.add(listener);
			synchronize();
		}

		public void setRules(Vector<String> rules) {
			if (list != null)
				this.remove(list);
			list = new JList(rules);
			list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			if (rules != null)
				list.setSelectionInterval(0, rules.size() - 1);
			synchronize();
			show();
		}

		public void show() {
			this.add(list);
		}

		public/* LONG_OBJECT */boolean isSelected(Rule r) {
			String s = r.getName();

			for (Object o : list.getSelectedValues()) {
				String ssel = (String) o;
				if (ssel.equals(s))
					return true;
			}
			return false;

		}

	}

	protected JZoomableShapeContainer container;

	protected JMiniRadarShapeContainer radar;

	protected ViewGraphNode root;

	protected JButton zoomInButton, zoomOutButton, reloadButton;

	protected JScrollPane scrollPane;

	protected JToggleButton autoReloadButton;

	protected RuleSelectorPanel rulePanel;

	protected JTextPane dump;

	protected JFrame myFrame;

	protected Rete engine;

	protected boolean dumpEmpty = true;

	protected final int spaceHorizontal = 10;

	protected final int spaceVertical = 15;

	protected final int nodeHorizontal = 45;

	protected final int nodeVertical = 16;

	protected SimpleAttributeSet even, odd, actAttributes;

	protected Hashtable coordinates = new Hashtable();

	protected Color getBackgroundColorForNode(ViewGraphNode node) {
		Color bg = Color.black;
		if (node.getReteNode() instanceof TerminalNode)
			bg = Color.black;
		if (node.getReteNode() instanceof AbstractBeta)
			bg = Color.green;
		if (node.getReteNode() instanceof LIANode)
			bg = Color.cyan;
		if (node.getReteNode() instanceof ObjectTypeNode)
			bg = Color.orange;
		if (node.getReteNode() instanceof SlotAlpha)
			bg = Color.red;
		return bg;
	}

	protected Color getBorderColorForNode(ViewGraphNode node) {
		return getBackgroundColorForNode(node).darker();
	}

	protected void addPrimitive(Shape p) {
		container.addPrimitive(p);
		radar.addPrimitive(p);
	}

	protected void addPrimitive(ConnectorLine p) {
		container.addPrimitive(p);
		radar.addPrimitive(p);
	}

	protected void getCorrespondingTerminalNodes(ViewGraphNode root, Set<BaseNode> target) {
		BaseNode n = root.getReteNode();
		if (n instanceof TerminalNode)
			target.add(n);
		Iterator it = root.childs.iterator();
		while (it.hasNext()) {
			ViewGraphNode succ = (ViewGraphNode) it.next();
			getCorrespondingTerminalNodes(succ, target);
		}
	}

	protected void setMyFrame(JFrame frame) {
		myFrame = frame;
	}

	@SuppressWarnings("unchecked")
	protected Shape makeShapeFromNode(ViewGraphNode act, LinkedList<ViewGraphNode> queue) {

		Color bg = getBackgroundColorForNode(act);
		Color border = getBorderColorForNode(act);
		String desc = "";
		BaseNode reteNode = act.getReteNode();
		HashSet terminalNodes = new HashSet();
		getCorrespondingTerminalNodes(act, terminalNodes);

		boolean drawMe = false;
		for (Object tNode : terminalNodes) {
			TerminalNode terminal = (TerminalNode) tNode;
			if (drawMe = (rulePanel.isSelected(terminal.getRule())))
				break;
		}

		if (reteNode != null)
			desc = String.valueOf(reteNode.getNodeId());
		Shape s;
		if (reteNode == null) { // ROOT NODE
			s = new Ellipse();
		} else if (reteNode instanceof AbstractBeta || act.getReteNode() instanceof SlotAlpha) {
			s = new Trapezoid();
		} else if (reteNode instanceof TerminalNode) {
			s = new RoundedRectangle();
		} else if (reteNode instanceof LIANode) {
			s = new Ellipse();
		} else {
			s = new Rectangle();
		}

		if (!drawMe) {
			int red = bg.getRed();
			int green = bg.getGreen();
			int blue = bg.getBlue();
			Color containerBg = Color.GRAY;
			int containerred = containerBg.getRed();
			int containergreen = containerBg.getGreen();
			int containerblue = containerBg.getBlue();
			red = red / 8 + 7 * containerred / 8;
			green = green / 8 + 7 * containergreen / 8;
			blue = blue / 8 + 7 * containerblue / 8;
			bg = new Color(red, green, blue).brighter();
			border = bg;
		}

		s.setActivated(drawMe);

		s.setBgcolor(bg);
		s.setBordercolor(border);

		int x = (spaceHorizontal / 2) + (int) ((float) (act.getX() * (spaceHorizontal + nodeHorizontal)) / 2.0);
		int y = (spaceVertical / 2) + act.getY() * (spaceVertical + nodeVertical);
		String key = x + "," + y;
		// if there is already a node at the given location, we shift it right
		while (this.coordinates.containsKey(key)) {
			x = x + ((spaceHorizontal + nodeHorizontal) * 2);
			key = x + "," + y;
		}
		coordinates.put(key, s);
		s.setX(x);
		s.setY(y);
		s.setWidth(nodeHorizontal);
		s.setHeight(nodeVertical);
		String longdesc = "";
		if (reteNode == null) {
			longdesc = "Root Node\n";
		} else {
			longdesc = reteNode.toPPString();
		}
		longdesc += "Used by rules:";
		Iterator iter = terminalNodes.iterator();
		while (iter.hasNext()) {
			TerminalNode t = (TerminalNode) iter.next();

			longdesc += t.getRule().getName();
			if (iter.hasNext())
				longdesc += ";";
		}
		s.setLongDescription(longdesc);
		s.setText(desc);
		act.setShape(s);
		addPrimitive(s);
		for (Iterator<ViewGraphNode> it = act.getSuccessors().iterator(); it.hasNext();) {
			ViewGraphNode n = it.next();
			queue.offer(n);
		}
		return s;
	}

	protected void createPrimitives(ViewGraphNode root) {
		/*
		 * increase the y coordinate of some nodes until no node has parents
		 * with higher y coordinate
		 */
		boolean needCheck = true;
		LinkedList<ViewGraphNode> queue = new LinkedList<ViewGraphNode>();
		while (needCheck) {
			needCheck = false;

			queue.offer(root);
			while (!queue.isEmpty()) {
				ViewGraphNode act = queue.poll();
				for (Iterator<ViewGraphNode> it = act.getSuccessors().iterator(); it.hasNext();) {
					ViewGraphNode succ = it.next();
					queue.add(succ);
				}
				for (Iterator<ViewGraphNode> it = act.getParents().iterator(); it.hasNext();) {
					ViewGraphNode parent = it.next();
					if (parent.getY() >= act.getY()) {
						act.y = parent.y + 1;
						needCheck = true;
					}
				}
			}
		}

		/* make shapes from the ViewGraphNodes */

		// reuse our old queue
		queue.clear();
		queue.offer(root);
		while (!queue.isEmpty()) {
			ViewGraphNode act = queue.poll();
			Shape s = null;
			if (act.getShape() == null) {
				s = makeShapeFromNode(act, queue);
			} else {
				s = act.getShape();
			}
			if (act.isParentsChecked())
				continue;
			act.setParentsChecked(true);

			if (s == null)
				continue;

			for (Iterator<ViewGraphNode> it = act.getParents().iterator(); it.hasNext();) {
				ViewGraphNode n = it.next();
				Shape s1 = n.getShape();
				if (s1 == null)
					s1 = makeShapeFromNode(n, queue);
				ConnectorLine line = new ConnectorLine(s1, s);
				line.setColor(Color.blue);
				if (n.getReteNode() instanceof AbstractBeta)
					line.setColor(Color.red);
				if (n.getReteNode() instanceof LIANode)
					line.setColor(Color.red);

				if (!s.isActivated()) {
					// line.setColor(Color.DARK_GRAY.brighter().brighter());
					line.setColor(Color.LIGHT_GRAY);
				}

				addPrimitive(line);
			}
		}

	}

	/**
	 * @param engine
	 *            the rete-engine which should become visualised
	 */
	public Visualiser(Rete engine) {
		this.engine = engine;
		container = new JZoomableShapeContainer();
		container.addMouseListener(this);
		rulePanel = new RuleSelectorPanel(null);
		radar = new JMiniRadarShapeContainer();
		radar.setMasterShapeContainer(container);
		radar.setNormalizedFontHeight(nodeVertical);
		container.setRadarShapeContainer(radar);
		calculateMainContainerFont();
		even = new SimpleAttributeSet();
		odd = new SimpleAttributeSet();
		StyleConstants.setForeground(even, Color.blue);
		StyleConstants.setForeground(odd, Color.green.darker());
		actAttributes = even;
		rulePanel.addListSelectionListener(this);
		reloadView();
	}

	protected void calculateMainContainerFont() {
		int dpi = container.getToolkit().getScreenResolution();
		int ppMainContainer = (int) ((nodeVertical * container.getZoomFactor() / dpi) * 72);
		container.setFont(new Font("SansSerif", Font.PLAIN, ppMainContainer));
	}

	/**
	 * @return a JPanel, which contains the whole visualiser. you can embed it.
	 */
	public JPanel getVisualiserPanel() {
		JPanel panel = new JPanel();
		// we set the preferred size so that when (view) command is executed,
		// the
		// window displays correct without having to resize the window
		panel.setPreferredSize(new Dimension(700, 450));
		JPanel toolBox = new JPanel();
		GridLayout toolBoxLayout = new GridLayout(2, 1);
		toolBox.setLayout(toolBoxLayout);

		// Zoom Buttons
		zoomInButton = new JButton("Zoom In", IconLoader.getImageIcon("magnifier_zoom_in", Visualiser.class));
		zoomOutButton = new JButton("Zoom Out", IconLoader.getImageIcon("magnifier_zoom_out", Visualiser.class));
		zoomInButton.addActionListener(this);
		zoomOutButton.addActionListener(this);

		// Dump Field
		dump = new JTextPane();
		scrollPane = new JScrollPane(dump);
		dump.setText("This is the node dump area. Click on a node and you will get some information here\n");
		scrollPane.setAutoscrolls(true);
		JPanel dumpPanel = new JPanel(new BorderLayout());
		dumpPanel.add(scrollPane);

		// Sidebar (Where Toolbox and Dump Field is; NOT the radar)
		JPanel sideBar = new JPanel(new BorderLayout());
		sideBar.add(toolBox, BorderLayout.WEST);
		sideBar.add(dumpPanel, BorderLayout.CENTER);

		JSplitPane mainSplitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, rulePanel, container);

		// Main Window with two Splitters (between radar, sidebar and main)
		panel.setLayout(new BorderLayout());
		JSplitPane sideSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, radar, sideBar);
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitPane2, sideSplitPane);
		mainSplitPane.setResizeWeight(1.0);
		mainSplitPane.setOneTouchExpandable(true);
		sideSplitPane.setOneTouchExpandable(true);
		panel.add(mainSplitPane, BorderLayout.CENTER);

		// adding the buttons to buttonPanel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		buttonPanel.add(zoomInButton);
		buttonPanel.add(zoomOutButton);
		autoReloadButton = new JToggleButton("Automatic Reload", IconLoader.getImageIcon("arrow_refresh"));
		autoReloadButton.setSelected(false);
		autoReloadButton.addActionListener(this);
		buttonPanel.add(autoReloadButton);
		reloadButton = new JButton("Reload View", IconLoader.getImageIcon("arrow_refresh"));
		reloadButton.addActionListener(this);
		buttonPanel.add(reloadButton);
		panel.add(buttonPanel, BorderLayout.PAGE_END);

		return panel;
	}

	/**
	 * Creates a window, embeds the visualiser in it and shows that window.
	 */
	public void show() {
		JFrame frame = new JFrame(getCaption(new Date()));
		frame.getContentPane().add(getVisualiserPanel(), BorderLayout.CENTER);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		frame.setSize(700, 500);
		this.setMyFrame(frame);
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == zoomInButton) {
			container.zoomIn();
			calculateMainContainerFont();
			container.repaint();
		} else if (arg0.getSource() == zoomOutButton) {
			container.zoomOut();
			calculateMainContainerFont();
			container.repaint();
		} else if (arg0.getSource() == reloadButton) {
			reloadView();
		} else if (arg0.getSource() == autoReloadButton) {
			if (autoReloadButton.isSelected()) {
				reloadButton.setEnabled(false);
				engine.addEngineEventListener(this);
				reloadView();
			} else {
				reloadButton.setEnabled(true);
				engine.removeEngineEventListener(this);
			}
		}
	}

	protected String getCaption(Date date) {
		return "Jamocha - Rete Network - " + DateFormat.getInstance().format(date);
	}

	protected void updateView() {
		this.coordinates.clear();
		RootNode root = ((WorkingMemoryImpl) engine.getWorkingMemory()).getRootNode();
		ViewGraphNode t = ViewGraphNode.buildFromRete(root);
		this.root = t;
		container.removeAllPrimitives();
		radar.removeAllPrimitives();
		createPrimitives(t);
	}

	protected void reloadView() {
		Vector<String> rules = new Vector<String>();
		for (Object moduleObj : engine.getAgenda().getModules()) {
			Module module = (Module) moduleObj;
			for (Object ruleObj : module.getAllRules()) {
				Rule r = (Rule) ruleObj;
				rules.add(r.getName());
			}
		}

		rulePanel.setRules(rules);
		updateView();
		if (myFrame != null) {
			myFrame.setTitle(getCaption(new Date()));
		}
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void eventOccurred(EngineEvent event) {
		reloadView();
	}

	public void mousePressed(MouseEvent event) {
		Shape shape = container.getShapeAtPosition(event.getX(), event.getY());
		if (dumpEmpty) {
			dump.setText("");
			dumpEmpty = false;
		}
		if (shape == null)
			return;
		try {
			dump.getDocument().insertString(dump.getDocument().getLength(), shape.getLongDescription() + "\n", actAttributes);
			if (actAttributes == even) {
				actAttributes = odd;
			} else {
				actAttributes = even;
			}

		} catch (BadLocationException e) {

		}
		;

	}

	public void valueChanged(ListSelectionEvent e) {
		updateView();
	}
}