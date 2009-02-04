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
import javax.swing.table.TableColumnModel;

import org.jamocha.application.gui.JamochaMainFrame;
import org.jamocha.application.gui.TableModelQuickSort;
import org.jamocha.application.gui.TableRowModel;
import org.jamocha.application.gui.editor.TemplateEditor;
import org.jamocha.application.gui.icons.IconLoader;
import org.jamocha.engine.modules.Module;
import org.jamocha.engine.workingmemory.elements.Deftemplate;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;
import org.jamocha.parser.ParserFactory;

/**
 * This Panel shows all Templates of all Modules currently in the Engine. You
 * can add new Templates or delete old ones.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */

// TODO: it would be better to use ClipsFormatter for printing templates and so
// on, wouldnt it? jh
public class TemplatesPanel extends AbstractJamochaPanel implements
		ListSelectionListener, ActionListener {

	private static final String GUI_TEMPLATES_DIVIDERLOCATION = "gui.templates.dividerlocation";

	private static final long serialVersionUID = -5732131176258158968L;

	private final JSplitPane pane;

	private final JTable templatesTable;

	private final TemplatesTableModel dataModel;

	private final JButton reloadButton;

	private final JButton createNewButton;

	private final JTextArea dumpArea;

	public TemplatesPanel(final JamochaMainFrame gui) {
		super(gui);
		setLayout(new BorderLayout());

		dataModel = new TemplatesTableModel();
		templatesTable = new JTable(dataModel);
		templatesTable.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				final TableColumnModel columnModel = templatesTable
						.getColumnModel();
				final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				final int column = templatesTable
						.convertColumnIndexToModel(viewColumn);
				if (e.getClickCount() == 1 && column != -1) {
					final int shiftPressed = e.getModifiers()
							& InputEvent.SHIFT_MASK;
					final boolean ascending = shiftPressed == 0;
					TableModelQuickSort.sort(dataModel, ascending, column);
				}
			}
		});

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
		pane.setDividerLocation(settings.getInt(GUI_TEMPLATES_DIVIDERLOCATION));
		reloadButton = new JButton("Reload Templates", IconLoader
				.getImageIcon("arrow_refresh"));
		reloadButton.addActionListener(this);
		createNewButton = new JButton("Create new Template", IconLoader
				.getImageIcon("brick_add"));
		createNewButton.addActionListener(this);
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		buttonPanel.add(reloadButton);
		buttonPanel.add(createNewButton);
		add(buttonPanel, BorderLayout.PAGE_END);

		initPopupMenu();
	}

	@SuppressWarnings("unchecked")
	private void initTemplatesList() {
		dataModel.clear();
		final Collection<Module> modules = gui.getEngine().getModules()
				.getModuleList();
		// dataModel.setTemplates(modules);
		for (final Module module : modules) {
			final Collection templates = module.getTemplates();
			dataModel.addTemplates(templates, module);
		}
		templatesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		templatesTable.getColumnModel().getColumn(1).setPreferredWidth(
				templatesTable.getWidth() - 100);
	}

	private void initPopupMenu() {
		final JPopupMenu menu = new JPopupMenu();
		final JMenuItem removeItem = new JMenuItem(
				"Remove selected Template(s)", IconLoader
						.getImageIcon("brick_delete"));
		removeItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent event) {
				final int[] selCols = templatesTable.getSelectedRows();
				for (int i = 0; i < selCols.length; ++i) {
					final String modName = (String) dataModel.getValueAt(
							selCols[i], 0);
					gui.getEngine().findModule(modName).removeTemplate(
							((ExtTemplate) dataModel.getRowAt(selCols[i]))
									.getTemplate());
				}
				initTemplatesList();
			}
		});
		menu.add(removeItem);
		templatesTable.setComponentPopupMenu(menu);
	}

	@Override
	public void setFocus() {
		super.setFocus();
		initTemplatesList();
	}

	@Override
	public void close() {
		settings.set(GUI_TEMPLATES_DIVIDERLOCATION, pane.getDividerLocation());
	}

	public void settingsChanged() {

	}

	public void actionPerformed(final ActionEvent event) {
		if (event.getSource().equals(reloadButton)) {
			initTemplatesList();
		} else if (event.getSource().equals(createNewButton)) {
			final TemplateEditor editor = new TemplateEditor(gui.getEngine());
			editor.setStringChannel(gui.getStringChannel());
			editor.init();
		}
	}

	public void valueChanged(final ListSelectionEvent arg0) {
		if (arg0.getSource() == templatesTable.getSelectionModel()) {
			final StringBuilder buffer = new StringBuilder();
			if (templatesTable.getSelectedColumnCount() == 1
					&& templatesTable.getSelectedRow() > -1) {
				final ExtTemplate template = (ExtTemplate) dataModel
						.getRowAt(templatesTable.getSelectedRow());
				if (template != null) {
					buffer.append("(").append(template.getModule().getName())
							.append("::").append(
									template.getTemplate().getName());
					String description = null;
					if (template.getTemplate() instanceof Deftemplate) {
						description = ((Deftemplate) template.getTemplate()).getDescription();
					}
					if (description != null && description.length() > 0) {
						buffer.append("\n   Description: ").append(description)
								.append("\n");
					}
					final TemplateSlot[] slots = template.getTemplate()
							.getAllSlots();
					for (final TemplateSlot slot : slots) {
						buffer.append("\n    (");
						if (slot.isSilent()) {
							buffer.append("silent ");
						}
						if (slot.isMultiSlot()) {
							buffer.append("multislot ").append(slot.getName());
						} else {
							buffer.append("slot ").append(slot.getName());
						}
						buffer.append("\n        (type ").append(
								slot.getValueType().toString()).append(")");
						if (slot.getDefaultExpression() != null) {
							buffer.append("\n        (default ");
							buffer.append(slot.getDefaultExpression().format(
									ParserFactory.getFormatter()));
							buffer.append(")");
						}
						buffer.append("\n    )");
					}
					buffer.append("\n)");
				}
			}
			dumpArea.setText(buffer.toString());
		}
	}

	private final class TemplatesTableModel extends AbstractTableModel
			implements TableRowModel {

		private static final long serialVersionUID = 1L;

		private List<ExtTemplate> templates = new LinkedList<ExtTemplate>();

		private void clear() {
			templates = new LinkedList<ExtTemplate>();
			fireTableDataChanged();
		}

		private void addTemplates(final Collection<Template> templates,
				final Module module) {
			for (final Template template : templates) {
				final ExtTemplate exttemp = new ExtTemplate(template, module);
				this.templates.add(exttemp);
			}
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(final int column) {
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
			return templates.size();
		}

		public Object getValueAt(final int row, final int column) {
			final ExtTemplate template = (ExtTemplate) getRowAt(row);
			switch (column) {
			case 0:
				return template.getModule().getName();
			case 1:
				return template.getTemplate().getName();
			}
			return null;
		}

		public Object getRowAt(final int row) {
			return templates.get(row);
		}

		public void setRowAt(final Object value, final int row) {
			templates.set(row, (ExtTemplate) value);
		}
	}

	private class ExtTemplate {

		private final Template template;

		private final Module module;

		private ExtTemplate(final Template template, final Module module) {
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

}
