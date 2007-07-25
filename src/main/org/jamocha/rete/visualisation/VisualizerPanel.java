package org.jamocha.rete.visualisation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
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

import org.jamocha.rete.Module;
import org.jamocha.rete.Rete;
import org.jamocha.rete.eventhandling.ModuleChangedEvent;
import org.jamocha.rete.eventhandling.ModuleChangedListener;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.Rule;

public class VisualizerPanel extends JPanel implements ClickListener, ListSelectionListener, MouseListener, ModuleChangedListener {

	
	
	class RuleSelectorPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		JScrollPane scrollPane;
		JList list;
		int numRules;

		List<ListSelectionListener> listeners;

		public RuleSelectorPanel(Vector<String> rules) {
			listeners = new ArrayList<ListSelectionListener>();
			this.setLayout(new GridLayout(1,1));
			setRules(rules);
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
			numRules = (rules!=null)? rules.size() : 0;
			synchronize();
			show();
		}

		
		public void show() {
			if (scrollPane != null) this.remove(scrollPane);
			scrollPane = new JScrollPane(list);
			this.add(scrollPane);
			this.validate();
			list.setSelectionInterval(0, numRules-1);
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
			List<String> result = new ArrayList<String>();
			for (Object s : list.getSelectedValues()) result.add((String)s);
			return result;
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
	
	protected void setModule(Module module){
		if (this.module != null) this.module.removeModuleChangedListener(this);
		module.addModuleChangedListener(this);
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
		
		optionsPanel.setLayout(new GridLayout(1,1));
		
		rulePanel = new RuleSelectorPanel(null);
		rulePanel.addListSelectionListener(this);
		generateRulesList();
		optionsPanel.add(rulePanel);
		mainVis.setSelectedRules(rulePanel.getSelectedRules());
		miniMap.setSelectedRules(rulePanel.getSelectedRules());
		
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
		for (Object moduleObj : engine.getAgenda().getModules()) {
			Module module = (Module) moduleObj;
			for (Object ruleObj : module.getAllRules()) {
				String r = ((Defrule) ruleObj).getName();
				rules.add(r);
			}
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


	@Override
	public void factAdded(ModuleChangedEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void factRemoved(ModuleChangedEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void ruleAdded(ModuleChangedEvent ev) {
		reload();
	}


	@Override
	public void ruleRemoved(ModuleChangedEvent ev) {
		reload();
	}


	@Override
	public void templateAdded(ModuleChangedEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void templateRemoved(ModuleChangedEvent ev) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
