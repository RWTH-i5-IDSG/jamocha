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

package org.jamocha.application.gui.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jamocha.application.gui.icons.IconLoader;
import org.jamocha.communication.messagerouter.StringChannel;
import org.jamocha.engine.Engine;
import org.jamocha.engine.modules.Module;

/**
 * A simple editor for rules.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class RuleEditor extends AbstractJamochaEditor implements ActionListener {

	private static final long serialVersionUID = 1L;

	private final JButton cancelButton;

	private final JButton addButton;

	private final JTextField nameField;

	private final JTextField commentField;

	private final JTextArea ruleLhsArea;

	private final JTextArea ruleRhsArea;

	private final JComboBox moduleBox;

	private StringChannel channel;

	public RuleEditor(final Engine engine) {
		super(engine);
		setSize(600, 500);
		setLayout(new BorderLayout());
		setTitle("Add new Rule");
		cancelButton = new JButton("Cancel", IconLoader.getImageIcon("cancel"));
		cancelButton.addActionListener(this);
		addButton = new JButton("Add Rule", IconLoader.getImageIcon("car_add"));
		addButton.addActionListener(this);
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		buttonPanel.add(cancelButton);
		buttonPanel.add(addButton);
		add(buttonPanel, BorderLayout.SOUTH);

		nameField = new JTextField(15);
		commentField = new JTextField(15);
		final Collection<Module> modules = engine.getModules().getModuleList();
		final String[] moduleNames = new String[modules.size()];
		int i = 0;
		for (final Module mod : modules) {
			moduleNames[i++] = mod.getName();
		}
		moduleBox = new JComboBox(moduleNames);

		final JPanel topPanel = new JPanel();
		topPanel.setBorder(BorderFactory
				.createTitledBorder("General Rule Settings"));
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 1));
		final JPanel innerTopPanel = new JPanel(new GridLayout(3, 2));
		innerTopPanel.add(new JLabel("Name:"));
		innerTopPanel.add(nameField);
		innerTopPanel.add(new JLabel("Comment:"));
		innerTopPanel.add(commentField);
		innerTopPanel.add(new JLabel("Module:"));
		innerTopPanel.add(moduleBox);
		topPanel.add(innerTopPanel, BorderLayout.WEST);
		add(topPanel, BorderLayout.NORTH);

		final JPanel centerPanel = new JPanel(new GridLayout(2, 1));
		// rule left hand side
		final JPanel ruleLhsPanel = new JPanel(new BorderLayout());
		ruleLhsPanel.setBorder(BorderFactory
				.createTitledBorder("Left Hand Side (Premisse)"));
		ruleLhsArea = new JTextArea();
		ruleLhsPanel.add(new JScrollPane(ruleLhsArea));

		// rule right hand side
		final JPanel ruleRhsPanel = new JPanel(new BorderLayout());
		ruleRhsPanel.setBorder(BorderFactory
				.createTitledBorder("Right Hand Side (Action)"));
		ruleRhsArea = new JTextArea();
		ruleRhsPanel.add(new JScrollPane(ruleRhsArea));

		centerPanel.add(ruleLhsPanel);
		centerPanel.add(ruleRhsPanel);

		add(centerPanel, BorderLayout.CENTER);
	}

	public void setStringChannel(final StringChannel channel) {
		this.channel = channel;
	}

	@Override
	public void init() {
		setVisible(true);
	}

	public void actionPerformed(final ActionEvent event) {
		if (event.getSource().equals(addButton)) {
			final StringBuilder buffer = new StringBuilder();
			buffer.append("(defrule " + nameField.getText() + " \""
					+ commentField.getText() + "\"");
			String ruleLhs = ruleLhsArea.getText().trim();
			if (!ruleLhs.startsWith("(")) {
				ruleLhs = "(" + ruleLhs + ")";
			}
			String ruleRhs = ruleRhsArea.getText().trim();
			if (!ruleRhs.startsWith("(")) {
				ruleRhs = "(" + ruleRhs + ")";
			}
			buffer.append("\n" + ruleLhs + "\n => \n" + ruleRhs + "\n");
			buffer.append(")");
			channel.executeCommand(buffer.toString());
			JOptionPane.showMessageDialog(this,
					"Rule defined.\nPlease check the log for Messages.");
		} else if (event.getSource() == cancelButton) {
			close();
		}
	}

}
