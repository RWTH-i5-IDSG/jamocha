/*
 * Copyright 2007 Karl-Heinz Krempels, Sebastian Reinartz, Alexander Wilden
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
package org.jamocha.gui.tab.settings;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jamocha.gui.JamochaGui;
import org.jamocha.rete.agenda.ConflictResolutionStrategy;
import org.jamocha.rete.modules.Module;
import org.jamocha.settings.JamochaSettings;

/**
 * This Panel allows changes in the engines settings.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Sebastian Reinartz <sebastian@beggendorf.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class EngineSettingsPanel extends AbstractSettingsPanel implements ActionListener {

	private static final long serialVersionUID = -7136144663514250335L;

	private JCheckBox evaluationCheckBox;

	private JCheckBox profileAssertCheckBox;

	private JCheckBox profileRetractCheckBox;

	private JCheckBox profileFireCheckBox;

	private JCheckBox profileAddActivationCheckBox;

	private JCheckBox profileRemoveActivationCheckBox;

	private JCheckBox watchActivationsCheckBox;

	private JCheckBox watchFactsCheckBox;

	private JCheckBox watchRulesCheckBox;

	private JComboBox strategySelectorMain;

	private JComboBox moduleSelector;

	private JComboBox strategySelector;

	public EngineSettingsPanel(JamochaGui gui) {
		super(gui);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;

		// -------------------
		// General Settings Settings
		// -------------------

		JPanel generalSettingsPanel = new JPanel();
		generalSettingsPanel.setLayout(gridbag);
		generalSettingsPanel.setBorder(BorderFactory.createTitledBorder("General Settings"));

		// Evaluation
		addLabel(generalSettingsPanel, new JLabel("Evaluation"), gridbag, c, 0);
		JPanel evaluationPanel = new JPanel(new BorderLayout());

		evaluationCheckBox = new JCheckBox();
		evaluationCheckBox.addActionListener(this);
		evaluationPanel.add(evaluationCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, evaluationPanel, gridbag, c, 0);

		// Profile Assert
		addLabel(generalSettingsPanel, new JLabel("Profile Assert:"), gridbag, c, 1);
		JPanel profileAssertPanel = new JPanel(new BorderLayout());

		profileAssertCheckBox = new JCheckBox();
		profileAssertCheckBox.addActionListener(this);
		profileAssertPanel.add(profileAssertCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, profileAssertPanel, gridbag, c, 1);

		// Profile Retract
		addLabel(generalSettingsPanel, new JLabel("Profile Retract:"), gridbag, c, 2);
		JPanel profileRetractPanel = new JPanel(new BorderLayout());

		profileRetractCheckBox = new JCheckBox();
		profileRetractCheckBox.addActionListener(this);
		profileRetractPanel.add(profileRetractCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, profileRetractPanel, gridbag, c, 2);

		// Profile Fire
		addLabel(generalSettingsPanel, new JLabel("Profile Fire:"), gridbag, c, 3);
		JPanel profileFirePanel = new JPanel(new BorderLayout());

		profileFireCheckBox = new JCheckBox();
		profileFireCheckBox.addActionListener(this);
		profileFirePanel.add(profileFireCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, profileFirePanel, gridbag, c, 3);

		// Profile Add Activation
		addLabel(generalSettingsPanel, new JLabel("Profile Add Activation:"), gridbag, c, 4);
		JPanel profileAddActivationPanel = new JPanel(new BorderLayout());

		profileAddActivationCheckBox = new JCheckBox();
		profileAddActivationCheckBox.addActionListener(this);
		profileAddActivationPanel.add(profileAddActivationCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, profileAddActivationPanel, gridbag, c, 4);

		// Profile Remove Activation
		addLabel(generalSettingsPanel, new JLabel("Profile Remove Activation:"), gridbag, c, 5);
		JPanel profileRemoveActivationPanel = new JPanel(new BorderLayout());

		profileRemoveActivationCheckBox = new JCheckBox();
		profileRemoveActivationCheckBox.addActionListener(this);
		profileRemoveActivationPanel.add(profileRemoveActivationCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, profileRemoveActivationPanel, gridbag, c, 5);

		// Activations
		addLabel(generalSettingsPanel, new JLabel("Watch Activations:"), gridbag, c, 6);
		JPanel watchActivationsPanel = new JPanel(new BorderLayout());

		watchActivationsCheckBox = new JCheckBox();
		watchActivationsCheckBox.addActionListener(this);
		watchActivationsPanel.add(watchActivationsCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, watchActivationsPanel, gridbag, c, 6);

		// Facts
		addLabel(generalSettingsPanel, new JLabel("Watch Facts:"), gridbag, c, 7);
		JPanel watchFactsPanel = new JPanel(new BorderLayout());

		watchFactsCheckBox = new JCheckBox();
		watchFactsCheckBox.addActionListener(this);
		watchFactsPanel.add(watchFactsCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, watchFactsPanel, gridbag, c, 7);

		// Rules
		addLabel(generalSettingsPanel, new JLabel("Watch Rules:"), gridbag, c, 8);
		JPanel watchRulesPanel = new JPanel(new BorderLayout());

		watchRulesCheckBox = new JCheckBox();
		watchRulesCheckBox.addActionListener(this);
		watchRulesPanel.add(watchRulesCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, watchRulesPanel, gridbag, c, 8);

		mainPanel.add(generalSettingsPanel);

		// -------------------
		// Module Settings
		// -------------------

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		c.weightx = 1.0;

		JPanel moduleSettingsPanel = new JPanel();
		moduleSettingsPanel.setLayout(gridbag);
		moduleSettingsPanel.setBorder(BorderFactory.createTitledBorder("Strategy Settings"));

		// Setting for main (is the default)
		addLabel(moduleSettingsPanel, new JLabel("Strategy for MAIN:"), gridbag, c, 0);
		JPanel strategyPanelMain = new JPanel(new BorderLayout());

		strategySelectorMain = new JComboBox();
		strategySelectorMain.addActionListener(this);
		strategyPanelMain.add(strategySelectorMain, BorderLayout.WEST);
		addInputComponent(moduleSettingsPanel, strategyPanelMain, gridbag, c, 0);

		// Preselect the module
		addLabel(moduleSettingsPanel, new JLabel("Other Modules:"), gridbag, c, 1);
		JPanel modulePanel = new JPanel(new BorderLayout());

		moduleSelector = new JComboBox();
		moduleSelector.addActionListener(this);
		modulePanel.add(moduleSelector, BorderLayout.WEST);
		addInputComponent(moduleSettingsPanel, modulePanel, gridbag, c, 1);

		// select the strategy to use
		addLabel(moduleSettingsPanel, new JLabel(""), gridbag, c, 2);
		JPanel strategyPanel = new JPanel(new BorderLayout());

		strategySelector = new JComboBox();
		strategySelector.addActionListener(this);
		strategyPanel.add(strategySelector, BorderLayout.WEST);
		addInputComponent(moduleSettingsPanel, strategyPanel, gridbag, c, 2);

		mainPanel.add(moduleSettingsPanel);

		add(new JScrollPane(mainPanel));

		// hint for other modules
		addLabel(moduleSettingsPanel, new JLabel("Attention:"), gridbag, c, 3);
		JTextArea attentionField = new JTextArea();
		attentionField.setEditable(false);
		attentionField.setBorder(BorderFactory.createEmptyBorder());
		attentionField.setBackground(gui.getBackground());
		attentionField.setText("Changes you make here are lost after a restart.\nOnly the settings for MAIN-module are made persistent.");

		addInputComponent(moduleSettingsPanel, attentionField, gridbag, c, 3);

		mainPanel.add(moduleSettingsPanel);

		add(new JScrollPane(mainPanel));
	}

	@Override
	public void refresh() {
		initStrategySelectorMain();
		initModuleSelector();
		initStrategySelector();

		// check boxes:
		evaluationCheckBox.setSelected(settings.getBoolean(JamochaSettings.ENGINE_GENERAL_SETTINGS_EVALUATION));
		profileAssertCheckBox.setSelected(settings.getBoolean(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_ASSERT));
		profileRetractCheckBox.setSelected(settings.getBoolean(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_RETRACT));
		profileFireCheckBox.setSelected(settings.getBoolean(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_FIRE));
		profileAddActivationCheckBox.setSelected(settings.getBoolean(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_ADD_ACTIVATION));
		profileRemoveActivationCheckBox.setSelected(settings.getBoolean(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_REMOVE_ACTIVATION));
		watchActivationsCheckBox.setSelected(settings.getBoolean(JamochaSettings.ENGINE_GENERAL_SETTINGS_WATCH_ACTIVATIONS));
		watchFactsCheckBox.setSelected(settings.getBoolean(JamochaSettings.ENGINE_GENERAL_SETTINGS_WATCH_FACTS));
		watchRulesCheckBox.setSelected(settings.getBoolean(JamochaSettings.ENGINE_GENERAL_SETTINGS_WATCH_RULES));

	}

	public void actionPerformed(ActionEvent event) {

		// checkboxes:
		if (event.getSource() == evaluationCheckBox) {
			performSettingsChange(JamochaSettings.ENGINE_GENERAL_SETTINGS_EVALUATION, evaluationCheckBox.isSelected());
		} else if (event.getSource() == profileAssertCheckBox) {
			performSettingsChange(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_ASSERT, profileAssertCheckBox.isSelected());
		} else if (event.getSource() == profileRetractCheckBox) {
			performSettingsChange(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_RETRACT, profileRetractCheckBox.isSelected());
		} else if (event.getSource() == profileFireCheckBox) {
			performSettingsChange(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_FIRE, profileFireCheckBox.isSelected());
		} else if (event.getSource() == profileAddActivationCheckBox) {
			performSettingsChange(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_ADD_ACTIVATION, profileAddActivationCheckBox.isSelected());
		} else if (event.getSource() == profileRemoveActivationCheckBox) {
			performSettingsChange(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_REMOVE_ACTIVATION, profileRemoveActivationCheckBox.isSelected());
		} else if (event.getSource() == watchActivationsCheckBox) {
			performSettingsChange(JamochaSettings.ENGINE_GENERAL_SETTINGS_WATCH_ACTIVATIONS, watchActivationsCheckBox.isSelected());
		} else if (event.getSource() == watchFactsCheckBox) {
			performSettingsChange(JamochaSettings.ENGINE_GENERAL_SETTINGS_WATCH_FACTS, watchFactsCheckBox.isSelected());
		} else if (event.getSource() == watchRulesCheckBox) {
			performSettingsChange(JamochaSettings.ENGINE_GENERAL_SETTINGS_WATCH_RULES, watchRulesCheckBox.isSelected());
		}
		// combo boxes:
		else if (event.getSource().equals(moduleSelector)) {
			initStrategySelector();
		} else if (event.getSource().equals(strategySelector)) {
			if (strategySelector.getSelectedItem() != null && moduleSelector.getSelectedItem() != null) {
				String strategyName = strategySelector.getSelectedItem().toString();
				String moduleName = moduleSelector.getSelectedItem().toString();
				Module module = gui.getEngine().getModule(moduleName);
				try {
					gui.getEngine().getAgendas().getAgenda(module).setConflictResolutionStrategy(ConflictResolutionStrategy.getStrategy(strategyName));
				} catch (InstantiationException e) {
					JOptionPane.showMessageDialog(this, e, "Error setting the strategy.", JOptionPane.ERROR_MESSAGE);
				} catch (IllegalAccessException e) {
					JOptionPane.showMessageDialog(this, e, "Error setting the strategy.", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private void performSettingsChange(String settingName, boolean newValue) {
		// TODO: do we should use chanel to perform changes or write directly to
		// settings object?
		// care for threads!!
		StringBuilder command = new StringBuilder();
		// build statement:
		command.append("(set ");
		command.append(settingName);
		command.append(" ");
		command.append(newValue);
		command.append(" )");

		// execute:
		gui.getStringChannel().executeCommand(command.toString());
	}

	private void initModuleSelector() {
		moduleSelector.removeAllItems();
		Collection<Module> modules = gui.getEngine().getModules().getModuleList();
		for (Module module : modules) {
			if (!module.getModuleName().equals("MAIN"))
				moduleSelector.addItem(module.getModuleName());
		}
		if (moduleSelector.getItemCount() > 0) {
			moduleSelector.setSelectedIndex(0);
			moduleSelector.setEnabled(true);
		} else {
			moduleSelector.setEnabled(false);
		}
	}

	@SuppressWarnings("static-access")
	private void initStrategySelector() {
		strategySelector.removeAllItems();
		if (moduleSelector.getSelectedItem() != null) {
			strategySelector.setEnabled(true);
			String moduleName = moduleSelector.getSelectedItem().toString();
			Module module = gui.getEngine().getModule(moduleName);
			ConflictResolutionStrategy currentStrategy = gui.getEngine().getAgendas().getAgenda(module).getConflictResolutionStrategy();
			Set<String> strategies = ConflictResolutionStrategy.getStrategies();
			for (String strategyName : strategies) {
				strategySelector.addItem(strategyName);
				if (strategyName.equals(currentStrategy.getName())) {
					strategySelector.setSelectedItem(strategyName);
				}
			}
		} else {
			strategySelector.setEnabled(false);
		}
	}

	@SuppressWarnings("static-access")
	private void initStrategySelectorMain() {
		strategySelectorMain.removeAllItems();
		Module module = gui.getEngine().getModule("MAIN");
		ConflictResolutionStrategy currentStrategy = gui.getEngine().getAgendas().getAgenda(module).getConflictResolutionStrategy();
		Set<String> strategies = ConflictResolutionStrategy.getStrategies();
		for (String strategyName : strategies) {
			strategySelectorMain.addItem(strategyName);
			if (strategyName.equals(currentStrategy.getName())) {
				strategySelectorMain.setSelectedItem(strategyName);
			}
		}
	}

	@Override
	public void setDefaults() {
		settings.toDefault(JamochaSettings.ENGINE_GENERAL_SETTINGS_EVALUATION);
		settings.toDefault(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_ASSERT);
		settings.toDefault(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_RETRACT);
		settings.toDefault(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_FIRE);
		settings.toDefault(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_ADD_ACTIVATION);
		settings.toDefault(JamochaSettings.ENGINE_GENERAL_SETTINGS_PROFILE_REMOVE_ACTIVATION);
		settings.toDefault(JamochaSettings.ENGINE_GENERAL_SETTINGS_WATCH_ACTIVATIONS);
		settings.toDefault(JamochaSettings.ENGINE_GENERAL_SETTINGS_WATCH_FACTS);
		settings.toDefault(JamochaSettings.ENGINE_GENERAL_SETTINGS_WATCH_RULES);

		refresh();
	}
}
