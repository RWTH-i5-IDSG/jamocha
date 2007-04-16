/*
 * Copyright 2007 Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.gui.tab;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.jamocha.gui.JamochaGui;
import org.jamocha.gui.TableModelQuickSort;
import org.jamocha.gui.TableRowModel;
import org.jamocha.gui.editor.RuleEditor;
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.parser.ParserFactory;
import org.jamocha.rete.Module;
import org.jamocha.rule.Rule;

/**
 * This Panel shows all rules currently in the Jamocha engine. You add new rules
 * or delete existing ones.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class RulesPanel extends AbstractJamochaPanel implements ActionListener,
		ListSelectionListener {

	private static final long serialVersionUID = -5732131176258158968L;

	private JSplitPane pane;

	private JTable rulesTable;

	private RulesTableModel dataModel;

	private JButton reloadButton;

	private JButton addRuleButton;

	private JTextArea dumpArea;

	public RulesPanel(JamochaGui gui) {
		super(gui);
		setLayout(new BorderLayout());

		dataModel = new RulesTableModel();
		rulesTable = new JTable(dataModel);
		rulesTable.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				TableColumnModel columnModel = rulesTable.getColumnModel();
				int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				int column = rulesTable.convertColumnIndexToModel(viewColumn);
				if (e.getClickCount() == 1 && column != -1) {
					int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
					boolean ascending = (shiftPressed == 0);
					TableModelQuickSort.sort(dataModel, ascending, column);
				}
			}
		});

		rulesTable.setShowHorizontalLines(true);
		rulesTable.setRowSelectionAllowed(true);
		rulesTable.getTableHeader().setReorderingAllowed(false);
		rulesTable
				.getTableHeader()
				.setToolTipText(
						"Click to sort ascending. Click while pressing the shift-key down to sort descending");
		rulesTable.getSelectionModel().addListSelectionListener(this);
		dumpArea = new JTextArea();
		dumpArea.setLineWrap(true);
		dumpArea.setWrapStyleWord(true);
		dumpArea.setEditable(false);
		dumpArea.setFont(new Font("Courier", Font.PLAIN, 12));

		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(
				rulesTable), new JScrollPane(dumpArea));
		add(pane, BorderLayout.CENTER);
		pane.setDividerLocation(gui.getPreferences().getInt(
				"rules.dividerlocation", 300));
		reloadButton = new JButton("Reload Rules", IconLoader
				.getImageIcon("arrow_refresh"));
		reloadButton.addActionListener(this);
		addRuleButton = new JButton("Add new Rule", IconLoader
				.getImageIcon("car_add"));
		addRuleButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		buttonPanel.add(reloadButton);
		buttonPanel.add(addRuleButton);
		add(buttonPanel, BorderLayout.PAGE_END);

		initPopupMenu();
	}

	@SuppressWarnings("unchecked")
	private void initRulesList() {
		dataModel.clear();
		Collection<Module> modules = gui.getEngine().getAgenda().getModules();
		for (Module module : modules) {
			Collection rules = module.getAllRules();
			dataModel.addRules(rules);
		}
		rulesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		rulesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		rulesTable.getColumnModel().getColumn(2).setPreferredWidth(
				rulesTable.getWidth() - 250);
	}

	private void initPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem retractItem = new JMenuItem("Delete selected Rule(s)",
				IconLoader.getImageIcon("car_delete"));
		retractItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				int[] selCols = rulesTable.getSelectedRows();
				for (int i = 0; i < selCols.length; ++i) {
					Rule rule = (Rule) dataModel.getRowAt(selCols[i]);
					if (rule != null) {
						Module module = rule.getModule();
						if (module != null) {
							module.removeRule(rule, gui.getEngine(), gui
									.getEngine().getWorkingMemory());
						}
					}
				}
				initRulesList();
			}
		});
		menu.add(retractItem);
		rulesTable.setComponentPopupMenu(menu);
	}

	public void setFocus() {
		super.setFocus();
		initRulesList();
	}

	public void close() {
		gui.getPreferences().putInt("rules.dividerlocation",
				pane.getDividerLocation());
	}

	public void settingsChanged() {

	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(reloadButton)) {
			initRulesList();
		} else if (event.getSource().equals(addRuleButton)) {
			RuleEditor editor = new RuleEditor(gui.getEngine());
			editor.setStringChannel(gui.getStringChannel());
			editor.init();
		}
	}

	public void valueChanged(ListSelectionEvent arg0) {
		if (arg0.getSource() == rulesTable.getSelectionModel()) {
			StringBuilder buffer = new StringBuilder();
			if (rulesTable.getSelectedColumnCount() == 1
					&& rulesTable.getSelectedRow() > -1) {
				Rule rule = (Rule) dataModel.getRowAt(rulesTable
						.getSelectedRow());
				if (rule != null) {
					buffer.append(ParserFactory.getFormatter().formatRule(rule));
				}
			}
			dumpArea.setText(buffer.toString());
		}
	}

	private final class RulesTableModel extends AbstractTableModel implements
			TableRowModel {

		private static final long serialVersionUID = 1L;

		private List<Rule> rules = Collections.emptyList();

		private void clear() {
			rules = new LinkedList<Rule>();
			fireTableDataChanged();
		}

		private void addRules(Collection<Rule> rules) {
			for (Rule rule : rules) {
				this.rules.add(rule);
			}
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Module";
			case 1:
				return "Name";
			case 2:
				return "Comment";
			default:
				return null;
			}
		}

		public int getColumnCount() {
			return 3;
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int aColumn) {
			return java.lang.String.class;
		}

		public int getRowCount() {
			return rules.size();
		}

		public Object getValueAt(int row, int column) {
			Rule rule = (Rule) getRowAt(row);
			switch (column) {
			case 0:
				return rule.getModule().getModuleName();
			case 1:
				return rule.getName();
			case 2:
				return rule.getDescription();
			}
			return null;
		}

		public Object getRowAt(int row) {
			return rules.get(row);
		}

		public void setRowAt(Object value, int row) {
			rules.set(row, (Rule) value);
		}
	}

}
