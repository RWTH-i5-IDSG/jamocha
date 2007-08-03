/*
 * Copyright 2007 Alexander Wilden
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
package org.jamocha.gui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jamocha.gui.icons.IconLoader;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.parser.JamochaType;
import org.jamocha.rete.Rete;
import org.jamocha.rete.modules.Module;

/**
 * An editor for Templates.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class TemplateEditor extends AbstractJamochaEditor implements
		ActionListener {

	private static final long serialVersionUID = 6037731034903564707L;

	private JPanel contentPanel;

	private JPanel templatePanel;

	private JButton addSlotButton;

	private JButton cancelButton;

	private JButton assertButton;

	private JTextField nameField;

	private JComboBox moduleBox;

	private JButton reloadButtonDumpAreaTemplate;

	private JTextArea dumpAreaTemplate = new JTextArea();

	private StringChannel channel;

	private GridBagLayout gridbag;

	private GridBagConstraints gridbagConstraints;

	private List<EditorRow> rows = new LinkedList<EditorRow>();

	public TemplateEditor(Rete engine) {
		super(engine);
		setSize(600, 500);
		setLayout(new BorderLayout());
		setTitle("Create new Template");
		contentPanel = new JPanel(new BorderLayout());
		add(contentPanel, BorderLayout.CENTER);
		cancelButton = new JButton("Cancel", IconLoader.getImageIcon("cancel"));
		cancelButton.addActionListener(this);
		assertButton = new JButton("Create Template", IconLoader
				.getImageIcon("brick_add"));
		assertButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		buttonPanel.add(cancelButton);
		buttonPanel.add(assertButton);
		add(buttonPanel, BorderLayout.SOUTH);

		nameField = new JTextField(15);
		Collection modules = engine.getAgenda().getModules();
		String[] moduleNames = new String[modules.size()];
		int i = 0;
		for (Object obj : modules) {
			moduleNames[i++] = ((Module) obj).getModuleName();
		}
		moduleBox = new JComboBox(moduleNames);

		addSlotButton = new JButton("Add Slot", IconLoader.getImageIcon("add"));
		addSlotButton.addActionListener(this);
		addSlotButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JPanel topPanel = new JPanel();
		topPanel.setBorder(BorderFactory
				.createTitledBorder("General Template Settings"));
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 1));
		JPanel innerTopPanel = new JPanel(new GridLayout(2, 2));
		innerTopPanel.add(new JLabel("Template-name:"));
		innerTopPanel.add(nameField);
		innerTopPanel.add(new JLabel("Template-Module:"));
		innerTopPanel.add(moduleBox);
		topPanel.add(innerTopPanel, BorderLayout.WEST);
		topPanel.add(addSlotButton);
		add(topPanel, BorderLayout.NORTH);

		dumpAreaTemplate.setEditable(false);
		dumpAreaTemplate.setFont(new Font("Courier", Font.PLAIN, 12));
		dumpAreaTemplate.setRows(5);

		JPanel dumpAreaPanel = new JPanel();
		dumpAreaPanel.setLayout(new BoxLayout(dumpAreaPanel, BoxLayout.Y_AXIS));
		dumpAreaPanel.setBorder(BorderFactory
				.createTitledBorder("Template Preview"));
		dumpAreaPanel.add(new JScrollPane(dumpAreaTemplate));
		reloadButtonDumpAreaTemplate = new JButton("Reload Template Preview",
				IconLoader.getImageIcon("arrow_refresh"));
		reloadButtonDumpAreaTemplate.addActionListener(this);
		dumpAreaPanel.add(reloadButtonDumpAreaTemplate);

		contentPanel.add(dumpAreaPanel, BorderLayout.SOUTH);
	}

	public void setStringChannel(StringChannel channel) {
		this.channel = channel;
	}

	public void init() {
		// initialize the Panels
		templatePanel = new JPanel();
		templatePanel.setBorder(BorderFactory
				.createTitledBorder("Set the Slots for the Template"));
		contentPanel.add(new JScrollPane(templatePanel), BorderLayout.CENTER);
		initTemplatePanel();
		setVisible(true);
	}

	private void initTemplatePanel() {

		gridbag = new GridBagLayout();
		gridbagConstraints = new GridBagConstraints();
		gridbagConstraints.weightx = 1.0;
		templatePanel.setLayout(gridbag);
		gridbagConstraints.anchor = GridBagConstraints.WEST;
		gridbagConstraints.gridx = 0;
		gridbagConstraints.gridy = 0;
		templatePanel.add(new JLabel());
		gridbagConstraints.gridx = 1;
		templatePanel.add(new JLabel());
		gridbagConstraints.gridy = 2;
		templatePanel.add(new JLabel("Type:"));
		gridbagConstraints.gridy = 3;
		templatePanel.add(new JLabel("Name:"));
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == assertButton) {
			channel.executeCommand(getCurrentDeftemplateString(false));
			JOptionPane.showMessageDialog(this,
					"Template created.\nPlease check the log for Messages.");
		} else if (event.getSource() == cancelButton) {
			close();
		} else if (event.getSource() == reloadButtonDumpAreaTemplate) {
			dumpAreaTemplate.setText(getCurrentDeftemplateString(true));
		} else if (event.getSource() == addSlotButton) {
			EditorRow row = new EditorRow(new DeleteButton(IconLoader
					.getImageIcon("delete"), rows.size()), new JLabel("Slot "
					+ (rows.size() + 1)), getNewTypesCombo(), new JTextField());
			row.deleteButton.addActionListener(this);
			addRemoveButton(templatePanel, row.deleteButton, gridbag,
					gridbagConstraints, (rows.size() + 1));
			addLabel(templatePanel, row.rowLabel, gridbag, gridbagConstraints,
					(rows.size() + 1));
			addTypesCombo(templatePanel, row.typeBox, gridbag,
					gridbagConstraints, (rows.size() + 1));
			addNameField(templatePanel, row.nameField, gridbag,
					gridbagConstraints, (rows.size() + 1));
			rows.add(row);
			templatePanel.revalidate();
		} else if (event.getSource() instanceof DeleteButton) {
			DeleteButton deleteButton = (DeleteButton) event.getSource();
			rows.remove(deleteButton.getRow());
			templatePanel.removeAll();
			initTemplatePanel();
			for (int i = 0; i < rows.size(); ++i) {
				EditorRow editorRow = rows.get(i);
				editorRow.deleteButton.setRow(i);
				editorRow.rowLabel.setText("Slot " + (i + 1));
				addRemoveButton(templatePanel, editorRow.deleteButton, gridbag,
						gridbagConstraints, i + 1);
				addLabel(templatePanel, editorRow.rowLabel, gridbag,
						gridbagConstraints, i + 1);
				addTypesCombo(templatePanel, editorRow.typeBox, gridbag,
						gridbagConstraints, i + 1);
				addNameField(templatePanel, editorRow.nameField, gridbag,
						gridbagConstraints, i + 1);
			}
			templatePanel.repaint();
			templatePanel.revalidate();
		}
	}

	private String getCurrentDeftemplateString(boolean print) {
		StringBuilder res = new StringBuilder("(deftemplate "
				+ moduleBox.getSelectedItem() + "::" + nameField.getText());
		if (print)
			res.append("\n");
		for (EditorRow row : rows) {
			res.append("    (");
			if (row.typeBox.getSelectedItem().toString().equals(
					JamochaType.LIST.toString())) {
				res.append("multislot " + row.nameField.getText() + ")");
			} else {
				res.append("slot " + row.nameField.getText());
				if (!row.typeBox.getSelectedItem().equals("UNDEFINED")) {
					if (print)
						res.append("\n");
					res.append("        (type "
							+ row.typeBox.getSelectedItem().toString() + ")");
					if (print)
						res.append("\n    ");
				}
				res.append(")");
			}
			if (print)
				res.append("\n");
		}
		res.append(")");
		return res.toString();
	}

	private void addRemoveButton(JPanel parent, JButton button,
			GridBagLayout gridbag, GridBagConstraints c, int row) {
		c.gridx = 0;
		c.gridy = row;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(button, c);
		parent.add(button);
	}

	private void addLabel(JPanel parent, JLabel label, GridBagLayout gridbag,
			GridBagConstraints c, int row) {
		c.gridx = 1;
		c.gridy = row;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(label, c);
		parent.add(label);
	}

	private void addTypesCombo(JPanel parent, JComboBox combo,
			GridBagLayout gridbag, GridBagConstraints c, int row) {
		c.gridx = 2;
		c.gridy = row;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(combo, c);
		parent.add(combo);
	}

	private void addNameField(JPanel parent, JTextField field,
			GridBagLayout gridbag, GridBagConstraints c, int row) {
		c.gridx = 3;
		c.gridy = row;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		field.setColumns(30);
		gridbag.setConstraints(field, c);
		parent.add(field);
	}

	private JComboBox getNewTypesCombo() {
		String[] types = { "UNDEFINED", "STRING", "BOOLEAN", "LONG", "DOUBLE",
				"LIST", "OBJECT", "FACT" };
		JComboBox box = new JComboBox(types);
		return box;
	}

	private class EditorRow {

		private DeleteButton deleteButton;

		private JLabel rowLabel;

		private JComboBox typeBox;

		private JTextField nameField;

		private EditorRow(DeleteButton deleteButton, JLabel rowLabel,
				JComboBox typeBox, JTextField nameField) {
			this.deleteButton = deleteButton;
			this.rowLabel = rowLabel;
			this.typeBox = typeBox;
			this.nameField = nameField;
		}

	}

	private class DeleteButton extends JButton {

		private static final long serialVersionUID = 1L;

		private int row;

		private DeleteButton(ImageIcon icon, int row) {
			super(icon);
			this.row = row;
		}

		private int getRow() {
			return row;
		}

		private void setRow(int row) {
			this.row = row;
		}
	}

}
