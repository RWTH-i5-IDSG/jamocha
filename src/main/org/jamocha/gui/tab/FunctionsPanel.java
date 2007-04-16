/*
 * Copyright 2007 Nikolaus Koemm
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
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
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.parser.ParserFactory;
import org.jamocha.rete.Function;

/**
 * This Panel shows all functions currently in the Jamocha engine.
 * 
 * @author Nikolaus Koemm
 */
public class FunctionsPanel extends AbstractJamochaPanel implements
		ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 23;

	private JTextArea dumpAreaFunction;

	private JSplitPane pane;

	private JTable functionsTable;

	private FunctionsTableModel dataModel;

	private StringChannel editorChannel;

	private JButton reloadButton;

	public FunctionsPanel(JamochaGui gui) {
		super(gui);
		setLayout(new BorderLayout());

		dataModel = new FunctionsTableModel();
		functionsTable = new JTable(dataModel);
		functionsTable.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				TableColumnModel columnModel = functionsTable.getColumnModel();
				int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				int column = functionsTable
						.convertColumnIndexToModel(viewColumn);
				if (e.getClickCount() == 1 && column != -1) {
					int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
					boolean ascending = (shiftPressed == 0);
					TableModelQuickSort.sort(dataModel, ascending, column);
				}
			}
		});

		functionsTable.setShowHorizontalLines(false);
		functionsTable.setRowSelectionAllowed(true);
		functionsTable.getTableHeader().setReorderingAllowed(false);
		functionsTable
				.getTableHeader()
				.setToolTipText(
						"Click to sort ascending. Click while pressing the shift-key down to sort descending");
		functionsTable.getSelectionModel().addListSelectionListener(this);
		dumpAreaFunction = new JTextArea();
		dumpAreaFunction.setLineWrap(true);
		dumpAreaFunction.setWrapStyleWord(true);
		dumpAreaFunction.setEditable(false);
		dumpAreaFunction.setFont(new Font("Courier", Font.PLAIN, 12));

		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(
				functionsTable), new JScrollPane(dumpAreaFunction));
		add(pane, BorderLayout.CENTER);
		pane.setDividerLocation(gui.getPreferences().getInt(
				"functions.dividerlocation", 300));

		reloadButton = new JButton("Reload Functions", IconLoader
				.getImageIcon("arrow_refresh"));
		reloadButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		buttonPanel.add(reloadButton);
		add(buttonPanel, BorderLayout.PAGE_END);

		initFunctionsList();
	}

	@SuppressWarnings("unchecked")
	private void initFunctionsList() {
		Collection c = gui.getEngine().getAllFunctions();
		Function[] func = (Function[]) c.toArray(new Function[0]);
		List<Function> funcs = new ArrayList<Function>();
		boolean larger = false;
		funcs.add(0, func[0]);
		for (int idx = 1; idx <= func.length - 1; idx++) {
			int bound = funcs.size();
			larger = true;
			for (int indx = 0; indx < bound; indx++) {
				int cmpvalue = func[idx].getName().compareTo(
						funcs.get(indx).getName());
				if (cmpvalue < 0) {
					funcs.add(indx, func[idx]);
					indx = bound;
					larger = false;
				} else if (cmpvalue == 0) {
					indx = bound;
					larger = false;
				}
			}
			if (larger) {
				funcs.add(func[idx]);
			}

		}
		dataModel.setFunctions(funcs);
		functionsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
	}

	public void setFocus() {
		super.setFocus();
		initFunctionsList();
	}

	public void close() {
		if (editorChannel != null)
			gui.getEngine().getMessageRouter().closeChannel(editorChannel);
		gui.getPreferences().putInt("functions.dividerlocation",
				pane.getDividerLocation());
	}

	public void settingsChanged() {

	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(reloadButton)) {
			initFunctionsList();
		}
	}

	private final class FunctionsTableModel extends AbstractTableModel
			implements TableRowModel {

		private static final long serialVersionUID = 1L;

		private List<Function> funclist = Collections.emptyList();

		private void setFunctions(List<Function> funclist) {
			this.funclist = funclist;
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Functions";
			default:
				return null;
			}
		}

		public int getColumnCount() {
			return 1;
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int aColumn) {
			if (aColumn == 0)
				return java.lang.String.class;
			else
				return Class.class;
		}

		public int getRowCount() {
			return funclist.size();
		}

		public Object getValueAt(int row, int column) {
			String functionname = ((Function) getRowAt(row)).getName();
			return functionname;
		}

		public Object getRowAt(int row) {
			return funclist.get(row);
		}

		public void setRowAt(Object value, int row) {
			funclist.set(row, (Function) value);
		}
	}

	public void valueChanged(ListSelectionEvent arg0) {
		if (arg0.getSource() == functionsTable.getSelectionModel()) {
			StringBuilder buffer = new StringBuilder();
			if (functionsTable.getSelectedColumnCount() == 1
					&& functionsTable.getSelectedRow() > -1) {
				Function function = (Function) dataModel
						.getRowAt(functionsTable.getSelectedRow());
				if (function != null) {
					buffer.append(ParserFactory.getFormatter(true).formatFunction(
							function));
					buffer.append("\n");
				}
			}
			dumpAreaFunction.setText(buffer.toString());
		}
	}

}
