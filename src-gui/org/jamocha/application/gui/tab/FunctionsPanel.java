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
import java.awt.Point;
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

import org.jamocha.application.gui.JamochaMainFrame;
import org.jamocha.application.gui.TableModelQuickSort;
import org.jamocha.application.gui.TableRowModel;
import org.jamocha.application.gui.icons.IconLoader;
import org.jamocha.communication.messagerouter.StringChannel;
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.functions.FunctionGroup;
import org.jamocha.engine.functions.FunctionMemory;
import org.jamocha.engine.functions.FunctionNotFoundException;
import org.jamocha.formatter.HelpFormatter;

/**
 * This Panel shows all functions currently in the Jamocha engine.
 * 
 * @author Nikolaus Koemm
 */
public class FunctionsPanel extends AbstractJamochaPanel implements
		ActionListener, ListSelectionListener {

	private static final String GUI_FUNCTIONS_DIVIDERLOCATION = "gui.functions.dividerlocation";

	private static final String GUI_FUNCTIONS_FUNCTIONGROUPS_DIVIDERLOCATION = "gui.functions.functiongroups_dividerlocation";

	private static final long serialVersionUID = 23;

	private final String SHOW_ALL = "all Functions";

	private final JTextArea dumpAreaFunction;

	private final JSplitPane pane;

	private final JSplitPane listPane;

	private final JList functionGroupList;

	private final JTable functionsTable;

	private final FunctionGroupDataModel funcGroupsDataModel;

	private final FunctionsTableModel funcsDataModel;

	private StringChannel editorChannel;

	private final JButton reloadButton;

	public FunctionsPanel(final JamochaMainFrame gui) {
		super(gui);
		setLayout(new BorderLayout());

		funcGroupsDataModel = new FunctionGroupDataModel();
		functionGroupList = new JList(funcGroupsDataModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(final MouseEvent event) {
				final Point point = event.getPoint();
				final int index = locationToIndex(point);

				return funcGroupsDataModel.getFunctionDescriptionAt(index);
			}
		};
		functionGroupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		functionGroupList.addListSelectionListener(this);
		funcsDataModel = new FunctionsTableModel();
		functionsTable = new JTable(funcsDataModel);
		functionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		functionsTable.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				final TableColumnModel columnModel = functionsTable
						.getColumnModel();
				final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				final int column = functionsTable
						.convertColumnIndexToModel(viewColumn);
				if (e.getClickCount() == 1 && column != -1) {
					final int shiftPressed = e.getModifiers()
							& InputEvent.SHIFT_MASK;
					final boolean ascending = shiftPressed == 0;
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
		listPane.setDividerLocation(settings
				.getInt(GUI_FUNCTIONS_FUNCTIONGROUPS_DIVIDERLOCATION));

		dumpAreaFunction = new JTextArea();
		dumpAreaFunction.setLineWrap(true);
		dumpAreaFunction.setWrapStyleWord(true);
		dumpAreaFunction.setEditable(false);
		dumpAreaFunction.setFont(new Font("Courier", Font.PLAIN, 12));

		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPane,
				new JScrollPane(dumpAreaFunction));
		add(pane, BorderLayout.CENTER);
		pane.setDividerLocation(settings.getInt(GUI_FUNCTIONS_DIVIDERLOCATION));

		reloadButton = new JButton("Reload Functions", IconLoader
				.getImageIcon("arrow_refresh"));
		reloadButton.addActionListener(this);
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		buttonPanel.add(reloadButton);
		add(buttonPanel, BorderLayout.PAGE_END);

		initFunctionGroupsList();
		initFunctionsList(null);
	}

	private void initFunctionGroupsList() {
		functionGroupList.setSelectedIndex(0);
		final List<FunctionGroup> funcGroups = new LinkedList<FunctionGroup>(
				gui.getEngine().getFunctionMemory().getFunctionGroups()
						.values());
		int n = funcGroups.size() - 1;
		boolean swap;
		do {
			swap = false;
			for (int i = 0; i < n; ++i) {
				if (funcGroups.get(i).getName().compareToIgnoreCase(
						funcGroups.get(i + 1).getName()) > 0) {
					final FunctionGroup temp = funcGroups.get(i);
					funcGroups.set(i, funcGroups.get(i + 1));
					funcGroups.set(i + 1, temp);
					swap = true;
				}
			}
			--n;
		} while (swap);
		funcGroups.add(0, new FunctionGroup() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getName() {
				return SHOW_ALL;
			}

			@Override
			public String getDescription() {
				return "View the Functions of all Groups";
			}

			@Override
			public void loadFunctions(final FunctionMemory functionMem) {
			}
		});
		funcGroupsDataModel.setFunctionGroups(funcGroups);
	}

	@SuppressWarnings("unchecked")
	private void initFunctionsList(final String funcGroupName) {
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

	@Override
	public void setFocus() {
		super.setFocus();
		// initFunctionGroupsList();
		// initFunctionsList(null);
	}

	@Override
	public void close() {
		if (editorChannel != null) {
			gui.getEngine().getMessageRouter().closeChannel(editorChannel);
		}
		settings.set(GUI_FUNCTIONS_DIVIDERLOCATION, pane.getDividerLocation());
		settings.set(GUI_FUNCTIONS_FUNCTIONGROUPS_DIVIDERLOCATION, listPane
				.getDividerLocation());
	}

	public void settingsChanged() {

	}

	public void actionPerformed(final ActionEvent event) {
		if (event.getSource().equals(reloadButton)) {
			initFunctionGroupsList();
			initFunctionsList(null);
		}
	}

	private final class FunctionGroupDataModel extends AbstractListModel {

		private static final long serialVersionUID = 1L;

		List<FunctionGroup> funcGroups;

		public void setFunctionGroups(final List<FunctionGroup> funcGroups) {
			this.funcGroups = funcGroups;
			fireContentsChanged(this, 0, funcGroups.size());
		}

		public Object getElementAt(final int index) {
			return funcGroups.get(index).getName();
		}

		public String getFunctionDescriptionAt(final int index) {
			if (index < funcGroups.size() && index >= 0) {
				return funcGroups.get(index).getDescription();
			} else {
				return "";
			}
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

		private List<String> funcnameList = Collections.emptyList();

		private void setFunctions(final List<Function> funcList) {
			funcnameList = new LinkedList<String>();
			for (final Function func : funcList) {
				if (!funcnameList.contains(func.getName())) {
					funcnameList.add(func.getName());
					final List<String> aliases = (func).getAliases();
					for (final String alias : aliases) {
						funcnameList.add(alias);
					}
				}
			}
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(final int column) {
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

		@Override
		public boolean isCellEditable(final int row, final int col) {
			return false;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Class getColumnClass(final int aColumn) {
			if (aColumn == 0) {
				return java.lang.String.class;
			} else {
				return Class.class;
			}
		}

		public int getRowCount() {
			return funcnameList.size();
		}

		public Object getValueAt(final int row, final int column) {
			return getRowAt(row).toString();
		}

		public Object getRowAt(final int row) {
			return funcnameList.get(row);
		}

		public void setRowAt(final Object value, final int row) {
			funcnameList.set(row, value.toString());
		}
	}

	public void valueChanged(final ListSelectionEvent arg0) {
		if (arg0.getSource() == functionsTable.getSelectionModel()) {
			final StringBuilder buffer = new StringBuilder();
			if (functionsTable.getSelectedColumnCount() == 1
					&& functionsTable.getSelectedRow() > -1) {
				final String functionName = funcsDataModel.getRowAt(
						functionsTable.getSelectedRow()).toString();
				Function function;
				try {
					function = gui.getEngine().getFunctionMemory()
							.findFunction(functionName);
					buffer.append(function.format(new HelpFormatter()));
				} catch (final FunctionNotFoundException e) {
					buffer
							.append("there are problems while looking up the function "
									+ functionName);
				}
			}
			dumpAreaFunction.setText(buffer.toString());
			dumpAreaFunction.setCaretPosition(0);
		} else if (arg0.getSource() == functionGroupList) {
			initFunctionsList((String) functionGroupList.getSelectedValue());
		}
	}

}
