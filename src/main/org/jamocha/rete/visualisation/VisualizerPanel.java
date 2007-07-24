package org.jamocha.rete.visualisation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jamocha.rete.Module;
import org.jamocha.rete.Rete;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.Rule;

public class VisualizerPanel extends JPanel implements ClickListener, ListSelectionListener {

	
	
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
	
	protected SimpleAttributeSet actAttributes, even, odd;
	
	public VisualizerPanel(Rete e) {
		
		engine = e;
		
		miniMap = new Visualizer(e);
		miniMap.enableToolTips(false);
		miniMap.enableAutoScale(true);
		miniMap.enableShowSelection(true);
		miniMap.setPreferredSize(new Dimension(150,120));
		
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
		
	}

	
	public void nodeClicked(String description) {
		try {
			dump.getDocument().insertString(dump.getDocument().getLength(), description + "\n", actAttributes);
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
	
	
	
}
