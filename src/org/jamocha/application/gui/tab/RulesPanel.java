/*
 * Copyright 2002-2008 The Jamocha Team
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
package org.jamocha.application.gui.tab;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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

import org.jamocha.application.gui.JamochaGui;
import org.jamocha.application.gui.TableModelQuickSort;
import org.jamocha.application.gui.TableRowModel;
import org.jamocha.application.gui.editor.RuleEditor;
import org.jamocha.application.gui.icons.IconLoader;
import org.jamocha.engine.modules.Module;
import org.jamocha.parser.ParserFactory;
import org.jamocha.rules.Rule;

/**
 * This Panel shows all rules currently in the Jamocha engine. You add new rules
 * or delete existing ones.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class RulesPanel extends AbstractJamochaPanel implements ActionListener,
		ListSelectionListener {

	private static final String GUI_RULES_DIVIDERLOCATION = "gui.rules.dividerlocation";

	private static final long serialVersionUID = -5732131176258158968L;

	private final JSplitPane pane;

	private final JTable rulesTable;

	private final RulesTableModel dataModel;

	private final JButton reloadButton;

	private final JButton addRuleButton;

	private final JTextArea dumpArea;

	public RulesPanel(final JamochaGui gui) {
		super(gui);
		setLayout(new BorderLayout());

		dataModel = new RulesTableModel();
		rulesTable = new JTable(dataModel);
		rulesTable.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				final TableColumnModel columnModel = rulesTable
						.getColumnModel();
				final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				final int column = rulesTable
						.convertColumnIndexToModel(viewColumn);
				if (e.getClickCount() == 1 && column != -1) {
					final int shiftPressed = e.getModifiers()
							& InputEvent.SHIFT_MASK;
					final boolean ascending = shiftPressed == 0;
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
		pane.setDividerLocation(settings.getInt(GUI_RULES_DIVIDERLOCATION));
		reloadButton = new JButton("Reload Rules", IconLoader
				.getImageIcon("arrow_refresh"));
		reloadButton.addActionListener(this);
		addRuleButton = new JButton("Add new Rule", IconLoader
				.getImageIcon("car_add"));
		addRuleButton.addActionListener(this);
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		buttonPanel.add(reloadButton);
		buttonPanel.add(addRuleButton);
		add(buttonPanel, BorderLayout.PAGE_END);

		initPopupMenu();
		initRulesList();
	}

	@SuppressWarnings("unchecked")
	private void initRulesList() {
		dataModel.clear();
		final Collection<Module> modules = gui.getEngine().getModules()
				.getModuleList();
		for (final Module module : modules) {
			final Collection rules = module.getAllRules();
			dataModel.addRules(rules);
		}
		rulesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		rulesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		rulesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		rulesTable.getColumnModel().getColumn(3).setPreferredWidth(
				rulesTable.getWidth() - 350);
	}

	private void initPopupMenu() {
		final JPopupMenu menu = new JPopupMenu();
		final JMenuItem retractItem = new JMenuItem("Delete selected Rule(s)",
				IconLoader.getImageIcon("car_delete"));
		retractItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent event) {
				final int[] selCols = rulesTable.getSelectedRows();
				for (int i = 0; i < selCols.length; ++i) {
					final Rule rule = (Rule) dataModel.getRowAt(selCols[i]);
					if (rule != null) {
						final Module module = rule.parentModule();
						if (module != null) {
							module.removeRule(rule);
						}
					}
				}
				initRulesList();
			}
		});
		menu.add(retractItem);
		rulesTable.setComponentPopupMenu(menu);
	}

	@Override
	public void setFocus() {
		super.setFocus();
		initRulesList();
	}

	@Override
	public void close() {
		settings.set(GUI_RULES_DIVIDERLOCATION, pane.getDividerLocation());
	}

	public void settingsChanged() {

	}

	public void actionPerformed(final ActionEvent event) {
		if (event.getSource().equals(reloadButton)) {
			initRulesList();
		} else if (event.getSource().equals(addRuleButton)) {
			final RuleEditor editor = new RuleEditor(gui.getEngine());
			editor.setStringChannel(gui.getStringChannel());
			editor.init();
		}
	}

	public void valueChanged(final ListSelectionEvent arg0) {
		if (arg0.getSource() == rulesTable.getSelectionModel()) {
			final StringBuilder buffer = new StringBuilder();
			if (rulesTable.getSelectedColumnCount() == 1
					&& rulesTable.getSelectedRow() > -1) {
				final Rule rule = (Rule) dataModel.getRowAt(rulesTable
						.getSelectedRow());
				if (rule != null) {
					buffer.append(ParserFactory.getFormatter(true).visit(rule));
				}
			}
			dumpArea.setText(buffer.toString());
			dumpArea.setCaretPosition(0);
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

		private void addRules(final Collection<Rule> rules) {
			for (final Rule rule : rules) {
				this.rules.add(rule);
			}
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(final int column) {
			switch (column) {
			case 0:
				return "Module";
			case 1:
				return "Name";
			case 2:
				return "Complexity";
			case 3:
				return "Comment";
			default:
				return null;
			}
		}

		public int getColumnCount() {
			return 4;
		}

		@Override
		public boolean isCellEditable(final int row, final int col) {
			return false;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Class getColumnClass(final int aColumn) {
			if (aColumn == 2) {
				return java.lang.Integer.class;
			}
			return java.lang.String.class;
		}

		public int getRowCount() {
			return rules.size();
		}

		public Object getValueAt(final int row, final int column) {
			final Rule rule = (Rule) getRowAt(row);
			switch (column) {
			case 0:
				return rule.parentModule().getName();
			case 1:
				return rule.getName();
			case 2:
				return rule.getComplexity();
			case 3:
				return rule.getDescription();
			}
			return null;
		}

		public Object getRowAt(final int row) {
			return rules.get(row);
		}

		public void setRowAt(final Object value, final int row) {
			rules.set(row, (Rule) value);
		}
	}

}
