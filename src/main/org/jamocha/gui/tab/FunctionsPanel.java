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
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.jamocha.gui.JamochaGui;
import org.jamocha.gui.TableModelQuickSort;
import org.jamocha.gui.TableRowModel;
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

/**
 * This Panel shows all functions currently in the Jamocha engine.
 * 
 * @author Nikolaus Koemm
 */
public class FunctionsPanel extends AbstractJamochaPanel implements
		ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 23;

	private final String SHOW_ALL = "all Functions";

	private JTextArea dumpAreaFunction;

	private JSplitPane pane;

	private JSplitPane listPane;

	private JList functionGroupList;

	private JTable functionsTable;

	private FunctionGroupDataModel funcGroupsDataModel;

	private FunctionsTableModel funcsDataModel;

	private StringChannel editorChannel;

	private JButton reloadButton;

	public FunctionsPanel(JamochaGui gui) {
		super(gui);
		setLayout(new BorderLayout());

		funcGroupsDataModel = new FunctionGroupDataModel();
		functionGroupList = new JList(funcGroupsDataModel);
		functionGroupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		functionGroupList.addListSelectionListener(this);
		funcsDataModel = new FunctionsTableModel();
		functionsTable = new JTable(funcsDataModel);
		functionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		functionsTable.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				TableColumnModel columnModel = functionsTable.getColumnModel();
				int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				int column = functionsTable
						.convertColumnIndexToModel(viewColumn);
				if (e.getClickCount() == 1 && column != -1) {
					int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
					boolean ascending = (shiftPressed == 0);
					TableModelQuickSort.sort(funcsDataModel, ascending, column);
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
		listPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(
				functionGroupList), new JScrollPane(functionsTable));
		listPane.setDividerLocation(gui.getPreferences().getInt(
				"functions.functiongroups_dividerlocation", 200));

		dumpAreaFunction = new JTextArea();
		dumpAreaFunction.setLineWrap(true);
		dumpAreaFunction.setWrapStyleWord(true);
		dumpAreaFunction.setEditable(false);
		dumpAreaFunction.setFont(new Font("Courier", Font.PLAIN, 12));

		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPane,
				new JScrollPane(dumpAreaFunction));
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

		initFunctionGroupsList();
		initFunctionsList(null);
	}

	@SuppressWarnings("unchecked")
	private void initFunctionGroupsList() {
		functionGroupList.setSelectedIndex(0);
		List<FunctionGroup> funcGroups = new LinkedList<FunctionGroup>(gui
				.getEngine().getFunctionMemory().getFunctionGroups().values());
		int n = funcGroups.size() - 1;
		boolean swap;
		do {
			swap = false;
			for (int i = 0; i < n; ++i) {
				if (funcGroups.get(i).getName().compareToIgnoreCase(
						funcGroups.get(i + 1).getName()) > 0) {
					FunctionGroup temp = funcGroups.get(i);
					funcGroups.set(i, funcGroups.get(i + 1));
					funcGroups.set(i + 1, temp);
					swap = true;
				}
			}
			--n;
		} while (swap);
		funcGroups.add(0, new FunctionGroup() {

			private static final long serialVersionUID = 1L;

			public String getName() {
				return SHOW_ALL;
			}

			public List listFunctions() {
				return null;
			}

			public void loadFunctions(FunctionMemory functionMem) {
			}

			public void addFunction(Function function) {
			}
		});
		funcGroupsDataModel.setFunctionGroups(funcGroups);
	}

	@SuppressWarnings("unchecked")
	private void initFunctionsList(String funcGroupName) {
		Collection c;
		if (funcGroupName == null || funcGroupName.equals(SHOW_ALL)) {
			c = gui.getEngine().getFunctionMemory().getAllFunctions();
		} else {
			c = gui.getEngine().getFunctionMemory().getFunctionsOfGroup(
					funcGroupName);
		}
		funcsDataModel.setFunctions(new ArrayList(c));

		TableModelQuickSort.sort(funcsDataModel, true, 0);
		functionsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
	}

	public void setFocus() {
		super.setFocus();
		initFunctionGroupsList();
		initFunctionsList(null);
	}

	public void close() {
		if (editorChannel != null) {
			gui.getEngine().getMessageRouter().closeChannel(editorChannel);
		}
		gui.getPreferences().putInt("functions.dividerlocation",
				pane.getDividerLocation());
		gui.getPreferences().putInt("functions.functiongroups_dividerlocation",
				listPane.getDividerLocation());
	}

	public void settingsChanged() {

	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(reloadButton)) {
			initFunctionGroupsList();
			initFunctionsList(null);
		}
	}

	private final class FunctionGroupDataModel extends AbstractListModel {

		private static final long serialVersionUID = 1L;

		List<FunctionGroup> funcGroups;

		public void setFunctionGroups(List<FunctionGroup> funcGroups) {
			this.funcGroups = funcGroups;
			fireContentsChanged(this, 0, funcGroups.size());
		}

		public Object getElementAt(int index) {
			return funcGroups.get(index).getName();
		}

		public int getSize() {
			if (funcGroups == null) {
				return 0;
			}
			return funcGroups.size();
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
				Function function = (Function) funcsDataModel
						.getRowAt(functionsTable.getSelectedRow());
				if (function != null) {
//					buffer.append(ParserFactory.getFormatter(true)
//							.format(function));
//					buffer.append("\n");
					// TODO fix me
				}
			}
			dumpAreaFunction.setText(buffer.toString());
			dumpAreaFunction.setCaretPosition(0);
		} else if (arg0.getSource() == functionGroupList) {
			initFunctionsList((String) functionGroupList.getSelectedValue());
		}
	}

}
