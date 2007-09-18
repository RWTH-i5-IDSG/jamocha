package org.jamocha.gui.tab;

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

import org.jamocha.formatter.Formatter;
import org.jamocha.formatter.HelpFormatter;
import org.jamocha.gui.JamochaGui;
import org.jamocha.gui.TableRowModel;
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.rete.Fact;
import org.jamocha.rete.agenda.Activation;
import org.jamocha.rule.Rule;

public class AgendaPanel extends AbstractJamochaPanel implements
		ListSelectionListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private JSplitPane pane;

	private JButton reloadButton;

	private AgendaTableModel dataModel;

	private JTable agendaTable;

	private JTextArea dumpArea;

	public AgendaPanel(JamochaGui gui) {
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
		pane.setDividerLocation(gui.getPreferences().getInt(
				"agendapanel.dividerlocation", 300));
		reloadButton = new JButton("Reload Activationlist", IconLoader
				.getImageIcon("arrow_refresh"));
		reloadButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
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
		gui.getPreferences().putInt("agendapanel.dividerlocation",
				pane.getDividerLocation());
	}

	@Override
	public void setFocus() {
		super.setFocus();
		initActivationsList();
	}

	private void initActivationsList() {
		Collection<Activation> activations = gui.getEngine().getAgendas()
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

		private void setActivations(Collection<Activation> activations) {
			this.activations = new ArrayList<Activation>(activations.size());
			for (Activation activation : activations) {
				this.activations.add(activation);
			}
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(int column) {
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

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int aColumn) {
			if (aColumn == 0)
				return java.lang.String.class;
			else if (aColumn == 1)
				return java.lang.String.class;
			else
				return Class.class;
		}

		public int getRowCount() {
			if (activations == null)
				return 0;
			return activations.size();
		}

		public Object getValueAt(int row, int column) {
			switch (column) {
			case 0:
				return activations.get(row).getRule().getName();
			case 1:
				return activations.get(row).getTuple().toString();
			}
			return null;
		}

		public Object getRowAt(int row) {
			return activations.get(row);
		}

		public void setRowAt(Object value, int row) {
			activations.set(row, (Activation) value);
		}
	}

	public void valueChanged(ListSelectionEvent event) {
		if (event.getSource().equals(agendaTable.getSelectionModel())) {
			StringBuilder buffer = new StringBuilder();
			if (agendaTable.getSelectedRow() >= 0) {
				Activation act = (Activation) dataModel.getRowAt(agendaTable
						.getSelectedRow());
				if (act != null) {
					Rule rule = act.getRule();
					Formatter formatter = new HelpFormatter();
					buffer.append(rule.format(formatter));
					buffer.append("\n\n");
					buffer.append("Aggregated Time: ").append(
							act.getAggregatedTime());
					buffer.append("\n");
					buffer.append("Fact-Tuple:\n");
					Fact[] facts = act.getTuple().getFacts();
					for (Fact fact : facts) {
						buffer.append("\n--------------------------\n");
						buffer.append(fact.format(formatter));
					}
				}
			}
			dumpArea.setText(buffer.toString());
			dumpArea.setCaretPosition(0);
		}

	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(reloadButton)) {
			initActivationsList();
		}
	}

}
