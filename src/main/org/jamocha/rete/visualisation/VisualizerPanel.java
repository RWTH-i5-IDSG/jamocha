package org.jamocha.rete.visualisation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Rete;
import org.jamocha.rete.eventhandling.ModuleChangedEvent;
import org.jamocha.rete.eventhandling.ModuleChangedListener;
import org.jamocha.rete.eventhandling.ModulesChangeListener;
import org.jamocha.rete.modules.Module;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.Rule;

public class VisualizerPanel extends JPanel implements ClickListener, ListSelectionListener, MouseListener, ModuleChangedListener, ActionListener, ModulesChangeListener {

	class JCheckBoxList extends JPanel implements ActionListener{
		
		Vector<JCheckBox> boxes;
		List<ListSelectionListener> listeners;
		List<String> selected;
		JPanel panel;
		
		JCheckBoxList(Vector<String> items) {
			listeners = new ArrayList<ListSelectionListener>();
			selected = new ArrayList<String>();
			setList(items);
		}
		
		public void setList(Vector<String> items) {
			if (items == null) {
				items = new Vector<String>();
			}
			if (panel != null) this.remove(panel);
			panel = new JPanel();
			boxes=new Vector<JCheckBox>();
			loadItemsList(items);
			panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
			selectAll();
			this.add(panel);
			this.validate();
		}
		
		void loadItemsList(Vector<String> items){
			for (JCheckBox box : boxes) panel.remove(box);
			boxes.clear();
			for (String s : items) {
				JCheckBox newBox = new JCheckBox(s);
				newBox.addActionListener(this);
				boxes.add(newBox);
				panel.add(newBox);
			}
		}

		public void addListSelectionListener(ListSelectionListener l) {
			listeners.add(l);
		}
		
		protected void callListeners(){
			for (ListSelectionListener l: listeners) {
				l.valueChanged(new ListSelectionEvent(this,-1,-1,false));
			}
		}
		
		public void selectAll() {
			selected.clear();
			for (JCheckBox box : boxes) {
				box.setSelected(true);
				selected.add(box.getText());
			}
			callListeners();
		}

		public void actionPerformed(ActionEvent arg0) {
			JCheckBox box = (JCheckBox)arg0.getSource();
			String fooboo = box.getText();
			boolean inserted = box.isSelected();
			if (inserted) {
				selected.add(fooboo);
			} else {
				selected.remove(fooboo);
			}
			callListeners();
		}
		
		public List<String> getSelectedValues(){
			return selected;
		}
		
	}
	
	class RuleSelectorPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		JScrollPane scrollPane;
		JCheckBoxList list;
		int numRules;

		List<ListSelectionListener> listeners;

		public RuleSelectorPanel(Vector<String> rules) {
			listeners = new ArrayList<ListSelectionListener>();
			this.setLayout(new GridLayout(1,1));
			list = new JCheckBoxList(rules);
			show();
		}

		public void addListSelectionListener(ListSelectionListener listener) {
			list.addListSelectionListener(listener);
		}

		public void setRules(Vector<String> rules) {
			list.setList(rules);
		}

		
		public void show() {
			if (scrollPane != null) this.remove(scrollPane);
			scrollPane = new JScrollPane(list);
			this.add(scrollPane);
			this.validate();
			list.selectAll();
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
		
		public List<String> getSelectedRules() {
			return list.getSelectedValues();
		}

	}

	
	
	
	protected Visualizer miniMap, mainVis;
	protected JTextPane dump;
	protected JPanel optionsPanel;
	protected RuleSelectorPanel rulePanel;
	protected Rete engine;
	protected JToggleButton lineBtn;
	protected JToggleButton lineQuarterEllipse;
	protected Module module;
	protected SimpleAttributeSet actAttributes, even, odd;
	protected JComboBox moduleChooser;
	protected JPanel moduleChooserPanel;
	
	protected void setModule(Module module){
		//TODO: Module event handling reactivation
		if (this.module != null) this.module.removeModuleChangedEventListener(this);
		module.addModuleChangedEventListener(this);
		this.module = module;
	}

	
	public VisualizerPanel(Rete e) {
		
		engine = e;
		setModule(e.findModule("MAIN"));
		miniMap = new Visualizer(e);
		miniMap.enableToolTips(false);
		miniMap.enableAutoScale(true);
		miniMap.enableShowSelection(true);
		miniMap.setPreferredSize(new Dimension(240,160));
		
		mainVis = new Visualizer(e);
		mainVis.enableToolTips(true);
		mainVis.enableAutoScale(false);
		
		mainVis.addViewportChangedListener(miniMap);
		miniMap.addViewportChangedListener(mainVis);
		miniMap.enableViewportByClick(true,mainVis);
		
		
		dump = new JTextPane();
		JScrollPane scrollDump = new JScrollPane(dump);
		even = new SimpleAttributeSet();
		odd = new SimpleAttributeSet();
		StyleConstants.setForeground(even, Color.blue);
		StyleConstants.setForeground(odd, Color.green.darker());
		actAttributes = even;
		
		mainVis.setClickListener(this);
		
		optionsPanel = new JPanel();
		optionsPanel.setPreferredSize(new Dimension(150,120));
		
		optionsPanel.setLayout(new BoxLayout(optionsPanel,BoxLayout.Y_AXIS));
		
		rulePanel = new RuleSelectorPanel(null);
		rulePanel.addListSelectionListener(this);
		
		moduleChooserPanel = new JPanel();
		
	
		engine.getModules().addModulesChangeListener(this);
		loadModuleList();
		
		generateRulesList();
		optionsPanel.add(rulePanel);
		optionsPanel.add(new JLabel("Module:"));
		optionsPanel.add(moduleChooser);
		mainVis.setSelectedRules(rulePanel.getSelectedRules());
		miniMap.setSelectedRules(rulePanel.getSelectedRules());
		miniMap.repaint();
		
		JSplitPane splitMainAndOptionsToUpper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainVis, optionsPanel);
		JSplitPane splitMiniAndDumpToBottom = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, miniMap, scrollDump);
		
		splitMainAndOptionsToUpper.setResizeWeight(1.0);
		
		JSplitPane splitUpperAndLower = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitMainAndOptionsToUpper, splitMiniAndDumpToBottom);
		splitUpperAndLower.setResizeWeight(1.0);
		
		this.setLayout(new BorderLayout());
		this.add(splitUpperAndLower,BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		
		//
		ButtonGroup lineChooser = new ButtonGroup();
		lineBtn = new JToggleButton("Lines");
		lineQuarterEllipse = new JToggleButton("Quarter Ellipses");
		lineChooser.add(lineBtn);
		lineChooser.add(lineQuarterEllipse);

		lineBtn.addMouseListener(this);
		lineQuarterEllipse.addMouseListener(this);
		lineQuarterEllipse.setSelected(true);		
		
		buttonPanel.add(lineBtn);
		buttonPanel.add(lineQuarterEllipse);
		
		this.add(buttonPanel, BorderLayout.PAGE_END);

	}
	
	public void loadModuleList(){
		Vector<String> modules = new Vector<String>();
		for (Module module : engine.getModules().getModuleList()) {
			modules.add(module.getModuleName());
		}
		JComboBox oldChooser = moduleChooser;
		String toSelect = null;
		if (oldChooser != null) {
			toSelect = (String) oldChooser.getSelectedItem();
			moduleChooserPanel.remove(oldChooser);
		}
		moduleChooser = new JComboBox(modules);
		
		boolean selectedGoodModule = false;
		if (toSelect!= null) {
			for (String mod : modules) {
				if (mod.equals(toSelect)) {
					selectedGoodModule = true;
					moduleChooser.setSelectedItem(mod);
				}
			}
		}
		if (!selectedGoodModule) {
			for (String mod : modules) {
				if (mod.equals(Constants.MAIN_MODULE)) {
					selectedGoodModule = true;
					moduleChooser.setSelectedItem(mod);
				}
			}
		}
		
		moduleChooser.setMaximumSize(new Dimension(4000,40));
		moduleChooser.addActionListener(this);
		moduleChooserPanel.add(moduleChooser);
		
		moduleSelected((String)moduleChooser.getSelectedItem());
	}

	protected void moduleSelected(String mod) {
		setModule(engine.getModule(mod));
		reload();
	}
	
	public void reload() {
		generateRulesList();
		mainVis.reload();
		miniMap.reload();
	}
	
	public void nodeClicked(String description) {
		try {
			dump.getDocument().insertString(dump.getDocument().getLength(), description, actAttributes);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (actAttributes == even) {
			actAttributes = odd;
		} else {
			actAttributes = even;
		}
		
	}

	protected void generateRulesList() {
		Vector<String> rules = new Vector<String>();
		for (Rule ruleObj : module.getAllRules()) {
			String r = ((Defrule) ruleObj).getName();
			rules.add(r);
		}
		rulePanel.setRules(rules);
	}

	public void valueChanged(ListSelectionEvent e) {
		mainVis.setSelectedRules(rulePanel.getSelectedRules());
		miniMap.setSelectedRules(rulePanel.getSelectedRules());
	}


	public void mouseClicked(MouseEvent arg0) {
		Component c = arg0.getComponent();
		if (c == lineBtn){
			miniMap.setLineStyle( VisualizerSetup.LINE );
			mainVis.setLineStyle( VisualizerSetup.LINE );
		} else if (c == lineQuarterEllipse){
			miniMap.setLineStyle( VisualizerSetup.QUARTERELLIPSE );
			mainVis.setLineStyle( VisualizerSetup.QUARTERELLIPSE );
		}
		
	}


	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void factAdded(ModuleChangedEvent ev) {
		// TODO Auto-generated method stub
		
	}


	public void factRemoved(ModuleChangedEvent ev) {
		// TODO Auto-generated method stub
		
	}


	public void ruleAdded(ModuleChangedEvent ev) {
		reload();
	}


	public void ruleRemoved(ModuleChangedEvent ev) {
		reload();
	}


	public void templateAdded(ModuleChangedEvent ev) {
		// TODO Auto-generated method stub
		
	}


	public void templateRemoved(ModuleChangedEvent ev) {
		// TODO Auto-generated method stub
		
	}


	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == moduleChooser) {
			moduleSelected((String)moduleChooser.getSelectedItem());
		}
	}

	public void evModuleAdded(Module newModule) {
		moduleChooser.addItem(newModule.getModuleName());
	}

	public void evModuleRemoved(Module oldModule) {
		moduleChooser.removeItem(oldModule.getModuleName());
	}
	
	
	
}
