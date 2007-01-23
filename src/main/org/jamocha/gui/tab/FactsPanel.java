/**
 * Copyright 2007 Karl-Heinz Krempels, Alexander Wilden
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

import org.jamocha.gui.JamochaGui;
import org.jamocha.gui.TableMap;
import org.jamocha.gui.TableSorter;
import org.jamocha.gui.editor.FactEditor;
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.rete.Fact;
import org.jamocha.rete.MultiSlot;
import org.jamocha.rete.Slot;
import org.jamocha.rete.exception.RetractException;

/**
 * This Panel shows all facts currently in the Jamocha engine. You can assert
 * new facts or retract existing ones.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class FactsPanel extends AbstractJamochaPanel implements ActionListener,
		ListSelectionListener {

	private static final long serialVersionUID = -5732131176258158968L;

	private JTable factsTable;

	private FactsTableModel dataModel;

	private JButton reloadButton;

	private JButton assertButton;

	private JTextArea dumpArea;

	private StringChannel editorChannel;

	public FactsPanel(JamochaGui gui) {
		super(gui);
		setLayout(new BorderLayout());

		dataModel = new FactsTableModel();
		TableSorter sorter = new TableSorter(new TableMap());
		((TableMap) sorter.getModel()).setModel(dataModel);
		factsTable = new JTable(sorter);
		sorter.addMouseListenerToHeaderInTable(factsTable);

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

		JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				new JScrollPane(factsTable), new JScrollPane(dumpArea));
		add(pane, BorderLayout.CENTER);
		pane.setDividerLocation(300);
		reloadButton = new JButton("Reload Facts", IconLoader
				.getImageIcon("database_refresh"));
		reloadButton.addActionListener(this);
		assertButton = new JButton("Assert Fact", IconLoader
				.getImageIcon("database_add"));
		assertButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		buttonPanel.add(reloadButton);
		buttonPanel.add(assertButton);
		add(buttonPanel, BorderLayout.PAGE_END);

		initPopupMenu();
	}

	@SuppressWarnings("unchecked")
	private void initFactsList() {
		List<Fact> facts = gui.getEngine().getAllFacts();
		dataModel.setFacts(facts);
		factsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		factsTable.getColumnModel().getColumn(1).setPreferredWidth(
				factsTable.getWidth() - 50);
	}

	private void initPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem retractItem = new JMenuItem("Retract selected Fact(s)",
				IconLoader.getImageIcon("database_delete"));
		retractItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				int[] selCols = factsTable.getSelectedRows();
				for (int i = 0; i < selCols.length; ++i) {
					Long value = (Long) dataModel.getValueAt(selCols[i], 0);
					try {
						gui.getEngine().retractById(value.longValue());
					} catch (NumberFormatException e) {
						// ignore it
					} catch (RetractException e) {
						e.printStackTrace();
					}
				}
				initFactsList();
			}
		});
		menu.add(retractItem);
		factsTable.setComponentPopupMenu(menu);
	}

	public void setFocus() {
		super.setFocus();
		initFactsList();
	}

	public void close() {
		if (editorChannel != null)
			gui.getEngine().getMessageRouter().closeChannel(editorChannel);
	}

	public void settingsChanged() {

	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(reloadButton)) {
			initFactsList();
		} else if (event.getSource().equals(assertButton)) {
			FactEditor editor = new FactEditor(gui.getEngine());
			if (editorChannel == null)
				editorChannel = gui.getEngine().getMessageRouter().openChannel(
						"facteditor_channel");
			editor.setStringChannel(editorChannel);
			editor.init();
		}
	}

	private final class FactsTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private List<Fact> facts = Collections.emptyList();

		private void setFacts(List<Fact> facts) {
			this.facts = facts;
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "ID";
			case 1:
				return "Fact";
			default:
				return null;
			}
		}

		public int getColumnCount() {
			return 2;
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int aColumn) {
			if (aColumn == 0)
				return java.lang.Number.class;
			else if (aColumn == 1)
				return java.lang.String.class;
			else
				return Class.class;
		}

		public int getRowCount() {
			return facts.size();
		}

		public Fact getRow(int row) {
			return facts.get(row);
		}

		public Object getValueAt(int row, int column) {
			Fact fact = getRow(row);
			switch (column) {
			case 0:
				return fact.getFactId();
			case 1:
				return fact.toFactString();
			}
			return null;
		}
	}

	public void valueChanged(ListSelectionEvent arg0) {
		if (arg0.getSource() == factsTable.getSelectionModel()) {
			if (factsTable.getSelectedColumnCount() == 1
					&& factsTable.getSelectedRow() > -1) {
				Fact fact = dataModel.getRow(factsTable.getSelectedRow());
				if (fact != null) {
					dumpArea.setText("f-" + fact.getFactId() + "("
							+ fact.getDeftemplate().getName());
					Slot[] slots = fact.getDeftemplate().getAllSlots();
					for (Slot slot : slots) {
						dumpArea.append("\n    (" + slot.getName() + " ");
						if (slot instanceof MultiSlot) {
							Object[] values = null;
							if (fact.getSlotValue(slot.getId()) instanceof Object[]) {
								values = (Object[]) fact.getSlotValue(slot
										.getId());
							}
							if (values != null) {
								for (int i = 0; i < values.length; ++i) {
									if (i > 0)
										dumpArea.append(" ");
									dumpArea.append("\"" + values[i] + "\"");
								}
							}
						} else {
							String value = fact.getSlotValue(slot.getId())
									.toString();
							if (!value.equals(""))
								dumpArea.append("\"" + value + "\"");
						}
						dumpArea.append(")");
					}
					dumpArea.append("\n)");
				} else {
					dumpArea.setText("");
				}
			} else {
				dumpArea.setText("");
			}
		}
	}

}
