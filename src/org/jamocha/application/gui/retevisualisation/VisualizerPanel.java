/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.application.gui.retevisualisation;

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

import org.jamocha.Constants;
import org.jamocha.communication.events.ModuleChangedEvent;
import org.jamocha.communication.events.ModuleChangedListener;
import org.jamocha.communication.events.ModulesChangedEvent;
import org.jamocha.communication.events.ModulesChangedEventListener;
import org.jamocha.communication.events.ModulesChangedEvent.ModulesChangedEventType;
import org.jamocha.engine.Engine;
import org.jamocha.engine.modules.Module;
import org.jamocha.rules.Defrule;
import org.jamocha.rules.Rule;

public class VisualizerPanel extends JPanel implements ClickListener,
		ListSelectionListener, MouseListener, ModuleChangedListener,
		ActionListener {

	private static final long serialVersionUID = 1L;

	class JCheckBoxList extends JPanel implements ActionListener {

		private static final long serialVersionUID = 1L;

		Vector<JCheckBox> boxes;

		List<ListSelectionListener> listeners;

		List<String> selected;

		JPanel panel;

		JCheckBoxList(final Vector<String> items) {
			listeners = new ArrayList<ListSelectionListener>();
			selected = new ArrayList<String>();
			setList(items);
		}

		public void setList(Vector<String> items) {
			if (items == null) {
				items = new Vector<String>();
			}
			if (panel != null) {
				this.remove(panel);
			}
			panel = new JPanel();
			boxes = new Vector<JCheckBox>();
			loadItemsList(items);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			selectAll();
			this.add(panel);
			validate();
		}

		void loadItemsList(final Vector<String> items) {
			for (final JCheckBox box : boxes) {
				panel.remove(box);
			}
			boxes.clear();
			for (final String s : items) {
				final JCheckBox newBox = new JCheckBox(s);
				newBox.addActionListener(this);
				boxes.add(newBox);
				panel.add(newBox);
			}
		}

		public void addListSelectionListener(final ListSelectionListener l) {
			listeners.add(l);
		}

		protected void callListeners() {
			for (final ListSelectionListener l : listeners) {
				l.valueChanged(new ListSelectionEvent(this, -1, -1, false));
			}
		}

		public void selectAll() {
			selected.clear();
			for (final JCheckBox box : boxes) {
				box.setSelected(true);
				selected.add(box.getText());
			}
			callListeners();
		}

		public void actionPerformed(final ActionEvent arg0) {
			final JCheckBox box = (JCheckBox) arg0.getSource();
			final String fooboo = box.getText();
			final boolean inserted = box.isSelected();
			if (inserted) {
				selected.add(fooboo);
			} else {
				selected.remove(fooboo);
			}
			callListeners();
		}

		public List<String> getSelectedValues() {
			return selected;
		}

	}

	class RuleSelectorPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		JScrollPane scrollPane;

		JCheckBoxList list;

		int numRules;

		List<ListSelectionListener> listeners;

		public RuleSelectorPanel(final Vector<String> rules) {
			listeners = new ArrayList<ListSelectionListener>();
			setLayout(new GridLayout(1, 1));
			list = new JCheckBoxList(rules);
			show();
		}

		public void addListSelectionListener(
				final ListSelectionListener listener) {
			list.addListSelectionListener(listener);
		}

		public void setRules(final Vector<String> rules) {
			list.setList(rules);
		}

		@Override
		public void show() {
			if (scrollPane != null) {
				this.remove(scrollPane);
			}
			scrollPane = new JScrollPane(list);
			this.add(scrollPane);
			validate();
			list.selectAll();
		}

		public/* LONG_OBJECT */boolean isSelected(final Rule r) {
			final String s = r.getName();

			for (final Object o : list.getSelectedValues()) {
				final String ssel = (String) o;
				if (ssel.equals(s)) {
					return true;
				}
			}
			return false;

		}

		public List<String> getSelectedRules() {
			return list.getSelectedValues();
		}

	}

	class VisualizerModulesChangeListener implements
			ModulesChangedEventListener {

		public void modulesChanged(final ModulesChangedEvent event) {
			if (event.getType() == ModulesChangedEventType.MODULE_ADDED) {
				moduleChooser.addItem(event.getModule().getName());
			} else if (event.getType() == ModulesChangedEventType.MODULE_REMOVED) {
				moduleChooser.removeItem(event.getModule().getName());
			}
		}

	}

	protected Visualizer miniMap, mainVis;

	protected JTextPane dump;

	protected JPanel optionsPanel;

	protected RuleSelectorPanel rulePanel;

	protected Engine engine;

	protected JToggleButton lineBtn;

	protected JToggleButton lineQuarterEllipse;

	protected Module module;

	protected SimpleAttributeSet actAttributes, even, odd;

	protected JComboBox moduleChooser;

	protected JPanel moduleChooserPanel;

	protected void setModule(final Module module) {
		// TODO: Module event handling reactivation
		if (this.module != null) {
			this.module.removeModuleChangedEventListener(this);
		}
		module.addModuleChangedEventListener(this);
		this.module = module;
	}

	public VisualizerPanel(final Engine e) {

		engine = e;
		setModule(e.findModule("MAIN"));
		miniMap = new Visualizer(e);
		miniMap.enableToolTips(false);
		miniMap.enableAutoScale(true);
		miniMap.enableShowSelection(true);
		miniMap.setPreferredSize(new Dimension(240, 160));

		mainVis = new Visualizer(e);
		mainVis.enableToolTips(true);
		mainVis.enableAutoScale(false);

		mainVis.addViewportChangedListener(miniMap);
		miniMap.addViewportChangedListener(mainVis);
		miniMap.enableViewportByClick(true, mainVis);

		dump = new JTextPane();
		final JScrollPane scrollDump = new JScrollPane(dump);
		even = new SimpleAttributeSet();
		odd = new SimpleAttributeSet();
		StyleConstants.setForeground(even, Color.blue);
		StyleConstants.setForeground(odd, Color.green.darker());
		actAttributes = even;

		mainVis.setClickListener(this);

		optionsPanel = new JPanel();
		optionsPanel.setPreferredSize(new Dimension(150, 120));

		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

		rulePanel = new RuleSelectorPanel(null);
		rulePanel.addListSelectionListener(this);

		moduleChooserPanel = new JPanel();

		loadModuleList();
		engine.getModules().addModulesChangeListener(
				new VisualizerModulesChangeListener());

		generateRulesList();
		optionsPanel.add(rulePanel);
		optionsPanel.add(new JLabel("Module:"));
		optionsPanel.add(moduleChooser);
		mainVis.setSelectedRules(rulePanel.getSelectedRules());
		miniMap.setSelectedRules(rulePanel.getSelectedRules());
		miniMap.repaint();

		final JSplitPane splitMainAndOptionsToUpper = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, mainVis, optionsPanel);
		final JSplitPane splitMiniAndDumpToBottom = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, miniMap, scrollDump);

		splitMainAndOptionsToUpper.setResizeWeight(1.0);

		final JSplitPane splitUpperAndLower = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, splitMainAndOptionsToUpper,
				splitMiniAndDumpToBottom);
		splitUpperAndLower.setResizeWeight(1.0);

		setLayout(new BorderLayout());
		this.add(splitUpperAndLower, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));

		//
		final ButtonGroup lineChooser = new ButtonGroup();
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

	public void loadModuleList() {
		final Vector<String> modules = new Vector<String>();
		for (final Module module : engine.getModules().getModuleList()) {
			modules.add(module.getName());
		}
		final JComboBox oldChooser = moduleChooser;
		String toSelect = null;
		if (oldChooser != null) {
			toSelect = (String) oldChooser.getSelectedItem();
			moduleChooserPanel.remove(oldChooser);
		}
		moduleChooser = new JComboBox(modules);

		boolean selectedGoodModule = false;
		if (toSelect != null) {
			for (final String mod : modules) {
				if (mod.equals(toSelect)) {
					selectedGoodModule = true;
					moduleChooser.setSelectedItem(mod);
				}
			}
		}
		if (!selectedGoodModule) {
			for (final String mod : modules) {
				if (mod.equals(Constants.MAIN_MODULE)) {
					selectedGoodModule = true;
					moduleChooser.setSelectedItem(mod);
				}
			}
		}

		moduleChooser.setMaximumSize(new Dimension(4000, 40));
		moduleChooser.addActionListener(this);
		moduleChooserPanel.add(moduleChooser);

		moduleSelected((String) moduleChooser.getSelectedItem());
	}

	protected void moduleSelected(final String mod) {
		setModule(engine.getModule(mod));
		reload();
	}

	public void reload() {
		generateRulesList();
		mainVis.reload();
		miniMap.reload();
	}

	public void nodeClicked(final String description) {
		try {
			dump.getDocument().insertString(dump.getDocument().getLength(),
					description, actAttributes);
		} catch (final BadLocationException e) {
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
		final Vector<String> rules = new Vector<String>();
		for (final Rule ruleObj : module.getAllRules()) {
			final String r = ((Defrule) ruleObj).getName();
			rules.add(r);
		}
		rulePanel.setRules(rules);
	}

	public void valueChanged(final ListSelectionEvent e) {
		mainVis.setSelectedRules(rulePanel.getSelectedRules());
		miniMap.setSelectedRules(rulePanel.getSelectedRules());
	}

	public void mouseClicked(final MouseEvent arg0) {
		final Component c = arg0.getComponent();
		if (c == lineBtn) {
			try {
				setConnectorType("lines");
			} catch (final UnknownConnectorTypeException e) {
				engine.writeMessage(e.toString());
			}
		} else if (c == lineQuarterEllipse) {
			try {
				setConnectorType("quarterellipse");
			} catch (final UnknownConnectorTypeException e) {
				engine.writeMessage(e.toString());
			}
		}

	}

	public void mouseEntered(final MouseEvent arg0) {

	}

	public void mouseExited(final MouseEvent arg0) {

	}

	public void mousePressed(final MouseEvent arg0) {

	}

	public void mouseReleased(final MouseEvent arg0) {

	}

	public void factAdded(final ModuleChangedEvent ev) {

	}

	public void factRemoved(final ModuleChangedEvent ev) {

	}

	public void ruleAdded(final ModuleChangedEvent ev) {
		reload();
	}

	public void ruleRemoved(final ModuleChangedEvent ev) {
		reload();
	}

	public void templateAdded(final ModuleChangedEvent ev) {

	}

	public void templateRemoved(final ModuleChangedEvent ev) {

	}

	public void actionPerformed(final ActionEvent arg0) {
		if (arg0.getSource() == moduleChooser) {
			moduleSelected((String) moduleChooser.getSelectedItem());
		}
	}

	public void setConnectorType(final String connType)
			throws UnknownConnectorTypeException {
		if (connType.equalsIgnoreCase("lines")) {
			miniMap.setLineStyle(VisualizerSetup.LINE);
			mainVis.setLineStyle(VisualizerSetup.LINE);
			lineBtn.setSelected(true);
			lineQuarterEllipse.setSelected(false);
		} else if (connType.equalsIgnoreCase("quarterellipse")) {
			miniMap.setLineStyle(VisualizerSetup.QUARTERELLIPSE);
			mainVis.setLineStyle(VisualizerSetup.QUARTERELLIPSE);
			lineBtn.setSelected(false);
			lineQuarterEllipse.setSelected(true);
		} else {
			throw new UnknownConnectorTypeException(connType);
		}

	}

	public String getConnectorType() {
		if (mainVis.getLineStyle() == VisualizerSetup.LINE) {
			return "lines";
		}
		if (mainVis.getLineStyle() == VisualizerSetup.QUARTERELLIPSE) {
			return "quarterellipse";
		}

		return "unknown";
	}

}
