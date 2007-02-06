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
import java.util.Collection;
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

import org.jamocha.gui.JamochaGui;
import org.jamocha.gui.TableMap;
import org.jamocha.gui.TableSorter;
import org.jamocha.gui.editor.TemplateEditor;
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.rete.Module;
import org.jamocha.rete.MultiSlot;
import org.jamocha.rete.Slot;
import org.jamocha.rete.Template;

/**
 * This Panel shows all Templates of all Modules currently in the Engine. You
 * can add new Templates or delete old ones.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 * @version 0.01
 */
public class TemplatesPanel extends AbstractJamochaPanel implements
		ListSelectionListener, ActionListener {

	private static final long serialVersionUID = -5732131176258158968L;

	private JSplitPane pane;

	private JTable templatesTable;

	private TemplatesTableModel dataModel;

	private JButton reloadButton;

	private JButton createNewButton;

	private JTextArea dumpArea;

	public TemplatesPanel(JamochaGui gui) {
		super(gui);
		setLayout(new BorderLayout());

		dataModel = new TemplatesTableModel();
		TableSorter sorter = new TableSorter(new TableMap());
		((TableMap) sorter.getModel()).setModel(dataModel);
		templatesTable = new JTable(sorter);
		sorter.addMouseListenerToHeaderInTable(templatesTable);

		templatesTable.setShowHorizontalLines(true);
		templatesTable.setRowSelectionAllowed(true);
		templatesTable.getTableHeader().setReorderingAllowed(false);
		templatesTable
				.getTableHeader()
				.setToolTipText(
						"Click to sort ascending. Click while pressing the shift-key down to sort descending");
		templatesTable.getSelectionModel().addListSelectionListener(this);
		dumpArea = new JTextArea();
		dumpArea.setLineWrap(true);
		dumpArea.setWrapStyleWord(true);
		dumpArea.setEditable(false);
		dumpArea.setFont(new Font("Courier", Font.PLAIN, 12));

		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(
				templatesTable), new JScrollPane(dumpArea));
		add(pane, BorderLayout.CENTER);
		pane.setDividerLocation(gui.getPreferences().getInt(
				"facts.dividerlocation", 300));
		reloadButton = new JButton("Reload Templates", IconLoader
				.getImageIcon("table_refresh"));
		reloadButton.addActionListener(this);
		createNewButton = new JButton("Create new Template", IconLoader
				.getImageIcon("table_add"));
		createNewButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		buttonPanel.add(reloadButton);
		buttonPanel.add(createNewButton);
		add(buttonPanel, BorderLayout.PAGE_END);

		initPopupMenu();
	}

	@SuppressWarnings("unchecked")
	private void initTemplatesList() {
		dataModel.clear();
		Collection<Module> modules = gui.getEngine().getAgenda().getModules();
		// dataModel.setTemplates(modules);
		for (Module module : modules) {
			Collection templates = module.getTemplates();
			dataModel.addTemplates(templates, module);
		}
		templatesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		templatesTable.getColumnModel().getColumn(1).setPreferredWidth(
				templatesTable.getWidth() - 100);
	}

	private void initPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem removeItem = new JMenuItem("Remove selected Template(s)",
				IconLoader.getImageIcon("table_delete"));
		removeItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				int[] selCols = templatesTable.getSelectedRows();
				for (int i = 0; i < selCols.length; ++i) {
					String modName = (String) dataModel.getValueAt(selCols[i],
							0);
					gui.getEngine().getAgenda().findModule(modName)
							.removeTemplate(
									dataModel.getRow(selCols[i]).getTemplate(),
									gui.getEngine(),
									gui.getEngine().getWorkingMemory());
				}
				initTemplatesList();
			}
		});
		menu.add(removeItem);
		templatesTable.setComponentPopupMenu(menu);
	}

	public void setFocus() {
		super.setFocus();
		initTemplatesList();
	}

	public void close() {
		gui.getPreferences().putInt("templates.dividerlocation",
				pane.getDividerLocation());
	}

	public void settingsChanged() {

	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(reloadButton)) {
			initTemplatesList();
		} else if (event.getSource().equals(createNewButton)) {
			TemplateEditor editor = new TemplateEditor(gui.getEngine());
			editor.setStringChannel(gui.getStringChannel());
			editor.init();
		}
	}

	private final class TemplatesTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private List<ExtTemplate> templates = new LinkedList<ExtTemplate>();

		private void clear() {
			templates = new LinkedList<ExtTemplate>();
			fireTableDataChanged();
		}

		private void addTemplates(Collection<Template> templates, Module module) {
			for (Template template : templates) {
				ExtTemplate exttemp = new ExtTemplate(template, module);
				this.templates.add(exttemp);
			}
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Module";
			case 1:
				return "Template";
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
			return templates.size();
		}

		public ExtTemplate getRow(int row) {
			return templates.get(row);
		}

		public Object getValueAt(int row, int column) {
			ExtTemplate template = getRow(row);
			switch (column) {
			case 0:
				return template.getModule().getModuleName();
			case 1:
				return template.getTemplate().getName();
			}
			return null;
		}
	}

	private class ExtTemplate {

		private Template template;

		private Module module;

		private ExtTemplate(Template template, Module module) {
			this.template = template;
			this.module = module;
		}

		private Template getTemplate() {
			return template;
		}

		private Module getModule() {
			return module;
		}

	}

	public void valueChanged(ListSelectionEvent arg0) {
		if (arg0.getSource() == templatesTable.getSelectionModel()) {
			StringBuilder buffer = new StringBuilder();
			if (templatesTable.getSelectedColumnCount() == 1
					&& templatesTable.getSelectedRow() > -1) {
				ExtTemplate template = dataModel.getRow(templatesTable
						.getSelectedRow());
				if (template != null) {
					buffer.append("(" + template.getModule().getModuleName()
							+ "::" + template.getTemplate().getName());
					Slot[] slots = template.getTemplate().getAllSlots();
					for (Slot slot : slots) {
						buffer.append("\n    (");
						if (slot instanceof MultiSlot)
							buffer.append("multislot " + slot.getName() + ")");
						else {
							buffer.append("slot " + slot.getName()
									+ "\n        (type "
									+ slot.getValueType().toString()
									+ ")\n    )");

						}
					}
					buffer.append("\n)");
				}
			}
			dumpArea.setText(buffer.toString());
		}
	}

}
