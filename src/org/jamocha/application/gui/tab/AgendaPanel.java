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
package org.jamocha.application.gui.tab;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.jamocha.application.gui.JamochaGui;
import org.jamocha.application.gui.TableRowModel;
import org.jamocha.application.gui.icons.IconLoader;
import org.jamocha.engine.agenda.Activation;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.formatter.Formatter;
import org.jamocha.formatter.HelpFormatter;
import org.jamocha.rules.Rule;

public class AgendaPanel extends AbstractJamochaPanel implements
		ListSelectionListener, ActionListener {

	private static final String GUI_AGENDA_DIVIDERLOCATION = "gui.agenda.dividerlocation";

	private static final long serialVersionUID = 1L;

	private final JSplitPane pane;

	private final JButton reloadButton;

	private final AgendaTableModel dataModel;

	private final JTable agendaTable;

	private final JTextArea dumpArea;

	public AgendaPanel(final JamochaGui gui) {
		super(gui);
		setLayout(new BorderLayout());
		dataModel = new AgendaTableModel();
		agendaTable = new JTable(dataModel);

		agendaTable.getSelectionModel().addListSelectionListener(this);
		agendaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		agendaTable.setShowHorizontalLines(true);
		agendaTable.setRowSelectionAllowed(true);
		agendaTable.getTableHeader().setReorderingAllowed(false);
		agendaTable
				.getTableHeader()
				.setToolTipText(
						"No manual sorting available since sorting is done according to the used strategy.");
		agendaTable.getSelectionModel().addListSelectionListener(this);
		dumpArea = new JTextArea();
		dumpArea.setLineWrap(true);
		dumpArea.setWrapStyleWord(true);
		dumpArea.setEditable(false);
		dumpArea.setFont(new Font("Courier", Font.PLAIN, 12));

		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(
				agendaTable), new JScrollPane(dumpArea));
		add(pane, BorderLayout.CENTER);
		pane.setDividerLocation(settings.getInt(GUI_AGENDA_DIVIDERLOCATION));
		reloadButton = new JButton("Reload Activationlist", IconLoader
				.getImageIcon("arrow_refresh"));
		reloadButton.addActionListener(this);
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		buttonPanel.add(reloadButton);
		add(buttonPanel, BorderLayout.PAGE_END);

		initPopupMenu();
	}

	private void initPopupMenu() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		settings.set(GUI_AGENDA_DIVIDERLOCATION, pane.getDividerLocation());
	}

	@Override
	public void setFocus() {
		super.setFocus();
		initActivationsList();
	}

	private void initActivationsList() {
		final Collection<Activation> activations = gui.getEngine().getAgendas()
				.getAgenda(gui.getEngine().findModule("MAIN")).getActivations();
		dataModel.setActivations(activations);
		agendaTable.getColumnModel().getColumn(0).setPreferredWidth(150);
		agendaTable.getColumnModel().getColumn(1).setPreferredWidth(
				agendaTable.getWidth() - 150);
	}

	private final class AgendaTableModel extends AbstractTableModel implements
			TableRowModel {

		private static final long serialVersionUID = 1L;

		private List<Activation> activations;

		private void setActivations(final Collection<Activation> activations) {
			this.activations = new ArrayList<Activation>(activations.size());
			for (final Activation activation : activations) {
				this.activations.add(activation);
			}
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(final int column) {
			switch (column) {
			case 0:
				return "Rule";
			case 1:
				return "Tuple";
			default:
				return null;
			}
		}

		public int getColumnCount() {
			return 2;
		}

		@Override
		public boolean isCellEditable(final int row, final int col) {
			return false;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Class getColumnClass(final int aColumn) {
			if (aColumn == 0) {
				return java.lang.String.class;
			} else if (aColumn == 1) {
				return java.lang.String.class;
			} else {
				return Class.class;
			}
		}

		public int getRowCount() {
			if (activations == null) {
				return 0;
			}
			return activations.size();
		}

		public Object getValueAt(final int row, final int column) {
			switch (column) {
			case 0:
				return activations.get(row).getRule().getName();
			case 1:
				return activations.get(row).getTuple().toString();
			}
			return null;
		}

		public Object getRowAt(final int row) {
			return activations.get(row);
		}

		public void setRowAt(final Object value, final int row) {
			activations.set(row, (Activation) value);
		}
	}

	public void valueChanged(final ListSelectionEvent event) {
		if (event.getSource().equals(agendaTable.getSelectionModel())) {
			final StringBuilder buffer = new StringBuilder();
			if (agendaTable.getSelectedRow() >= 0) {
				final Activation act = (Activation) dataModel
						.getRowAt(agendaTable.getSelectedRow());
				if (act != null) {
					final Rule rule = act.getRule();
					final Formatter formatter = new HelpFormatter();
					buffer.append(rule.format(formatter));
					buffer.append("\n\n");
					buffer.append("Aggregated Time: ").append(
							act.getTuple().getAggregateCreationTimestamp());
					buffer.append("\n");
					buffer.append("Fact-Tuple:\n");
					for (final Fact fact : act.getTuple()) {
						buffer.append("\n--------------------------\n");
						buffer.append(fact.format(formatter));
					}
				}
			}
			dumpArea.setText(buffer.toString());
			dumpArea.setCaretPosition(0);
		}

	}

	public void actionPerformed(final ActionEvent event) {
		if (event.getSource().equals(reloadButton)) {
			initActivationsList();
		}
	}

}
