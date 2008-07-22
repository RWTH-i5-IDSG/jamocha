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
import java.util.Collections;
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
import org.jamocha.application.gui.editor.FactEditor;
import org.jamocha.application.gui.icons.IconLoader;
import org.jamocha.engine.RetractException;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.formatter.HelpFormatter;

/**
 * This Panel shows all facts currently in the Jamocha engine. You can assert
 * new facts or retract existing ones.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class FactsPanel extends AbstractJamochaPanel implements ActionListener,
		ListSelectionListener {

	private static final String GUI_FACTS_AUTOSORT_DIR = "gui.facts.autosort_dir";

	private static final String GUI_FACTS_AUTOSORT_BY = "gui.facts.autosort_by";

	private static final String GUI_FACTS_DIVIDERLOCATION = "gui.facts.dividerlocation";

	private static final long serialVersionUID = -5732131176258158968L;

	private final JSplitPane pane;

	private final JTable factsTable;

	private final FactsTableModel dataModel;

	private final JButton reloadButton;

	private final JButton assertButton;

	private final JTextArea dumpArea;

	public FactsPanel(final JamochaGui gui) {
		super(gui);
		setLayout(new BorderLayout());

		dataModel = new FactsTableModel();
		factsTable = new JTable(dataModel);
		factsTable.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				final TableColumnModel columnModel = factsTable
						.getColumnModel();
				final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				final int column = factsTable
						.convertColumnIndexToModel(viewColumn);
				if (e.getClickCount() == 1 && column != -1) {
					final int shiftPressed = e.getModifiers()
							& InputEvent.SHIFT_MASK;
					final boolean ascending = shiftPressed == 0;
					TableModelQuickSort.sort(dataModel, ascending, column);
				}
			}
		});

		factsTable.setShowHorizontalLines(true);
		factsTable.setRowSelectionAllowed(true);
		factsTable.getTableHeader().setReorderingAllowed(false);
		factsTable
				.getTableHeader()
				.setToolTipText(
						"Click to sort ascending. Click while pressing the shift-key down to sort descending");
		factsTable.getSelectionModel().addListSelectionListener(this);
		dumpArea = new JTextArea();
		dumpArea.setLineWrap(true);
		dumpArea.setWrapStyleWord(true);
		dumpArea.setEditable(false);
		dumpArea.setFont(new Font("Courier", Font.PLAIN, 12));

		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(
				factsTable), new JScrollPane(dumpArea));
		add(pane, BorderLayout.CENTER);
		pane.setDividerLocation(settings.getInt(GUI_FACTS_DIVIDERLOCATION));
		reloadButton = new JButton("Reload Facts", IconLoader
				.getImageIcon("database_refresh"));
		reloadButton.addActionListener(this);
		assertButton = new JButton("Assert Fact", IconLoader
				.getImageIcon("database_add"));
		assertButton.addActionListener(this);
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		buttonPanel.add(reloadButton);
		buttonPanel.add(assertButton);
		add(buttonPanel, BorderLayout.PAGE_END);

		initPopupMenu();
	}

	private void initFactsList() {
		final List<Fact> facts = gui.getEngine().getModules().getAllFacts();
		dataModel.setFacts(facts);
		factsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		factsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		factsTable.getColumnModel().getColumn(2).setPreferredWidth(
				factsTable.getWidth() - 200);
		final String autoSortBy = settings.getString(GUI_FACTS_AUTOSORT_BY);
		final String autoSortDir = settings.getString(GUI_FACTS_AUTOSORT_DIR);
		int col = -1;
		if (autoSortBy.equalsIgnoreCase("id")) {
			col = 0;
		} else if (autoSortBy.equalsIgnoreCase("template")) {
			col = 1;
		} else if (autoSortBy.equalsIgnoreCase("fact")) {
			col = 2;
		}
		if (col >= 0) {
			if (autoSortDir.contains("descending")) {
				TableModelQuickSort.sort(dataModel, false, col);
			} else {
				TableModelQuickSort.sort(dataModel, true, col);
			}
		}

	}

	private void initPopupMenu() {
		final JPopupMenu menu = new JPopupMenu();
		final JMenuItem retractItem = new JMenuItem("Retract selected Fact(s)",
				IconLoader.getImageIcon("database_delete"));
		retractItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent event) {
				final int[] selCols = factsTable.getSelectedRows();
				for (int i = 0; i < selCols.length; ++i) {
					final Long value = (Long) dataModel.getValueAt(selCols[i],
							0);
					try {
						gui.getEngine().retractById(value.longValue());
					} catch (final NumberFormatException e) {
						// ignore it
					} catch (final RetractException e) {
						e.printStackTrace();
					}
				}
				initFactsList();
			}
		});
		menu.add(retractItem);
		factsTable.setComponentPopupMenu(menu);
	}

	@Override
	public void setFocus() {
		super.setFocus();
		initFactsList();
	}

	@Override
	public void close() {
		settings.set(GUI_FACTS_DIVIDERLOCATION, pane.getDividerLocation());
	}

	public void actionPerformed(final ActionEvent event) {
		if (event.getSource().equals(reloadButton)) {
			initFactsList();
		} else if (event.getSource().equals(assertButton)) {
			final FactEditor editor = new FactEditor(gui.getEngine());
			editor.setStringChannel(gui.getStringChannel());
			editor.init();
		}
	}

	public void valueChanged(final ListSelectionEvent arg0) {
		if (arg0.getSource() == factsTable.getSelectionModel()) {
			final StringBuilder buffer = new StringBuilder();
			if (factsTable.getSelectedColumnCount() == 1
					&& factsTable.getSelectedRow() > -1) {
				final Fact fact = (Fact) dataModel.getRowAt(factsTable
						.getSelectedRow());
				if (fact != null) {
					buffer.append(fact.format(new HelpFormatter()));
				}
			}
			dumpArea.setText(buffer.toString());
			dumpArea.setCaretPosition(0);
		}
	}

	private final class FactsTableModel extends AbstractTableModel implements
			TableRowModel {

		private static final long serialVersionUID = 1L;

		private List<Fact> facts = Collections.emptyList();

		private void setFacts(final List<Fact> facts) {
			this.facts = facts;
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(final int column) {
			switch (column) {
			case 0:
				return "ID";
			case 1:
				return "Template";
			case 2:
				return "Fact";
			default:
				return null;
			}
		}

		public int getColumnCount() {
			return 3;
		}

		@Override
		public boolean isCellEditable(final int row, final int col) {
			return false;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Class getColumnClass(final int aColumn) {
			if (aColumn == 0) {
				return java.lang.Long.class;
			} else if (aColumn == 1) {
				return java.lang.String.class;
			} else if (aColumn == 2) {
				return java.lang.String.class;
			} else {
				return Class.class;
			}
		}

		public int getRowCount() {
			return facts.size();
		}

		public Object getValueAt(final int row, final int column) {
			final Fact fact = (Fact) getRowAt(row);
			switch (column) {
			case 0:
				return fact.getFactId();
			case 1:
				return fact.getTemplate().getName();
			case 2:
				return fact.toString();
			}
			return null;
		}

		@Override
		public void setValueAt(final Object aValue, final int row,
				final int column) {
			// we can't change fact values!
		}

		public Object getRowAt(final int row) {
			return facts.get(row);
		}

		public void setRowAt(final Object value, final int row) {
			facts.set(row, (Fact) value);

		}
	}

}
