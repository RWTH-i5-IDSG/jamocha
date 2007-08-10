/*
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
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.rete.agenda.ConflictResolutionStrategy;
import org.jamocha.rete.modules.Module;

/**
 * This Panel allows changes in the engines settings.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class EngineSettingsPanel extends AbstractSettingsPanel implements
		ActionListener {

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
		generalSettingsPanel.setBorder(BorderFactory
				.createTitledBorder("General Settings"));

		// Evaluation
		addLabel(generalSettingsPanel, new JLabel("Evaluation"), gridbag, c, 0);
		JPanel evaluationPanel = new JPanel(new BorderLayout());

		evaluationCheckBox = new JCheckBox();
		evaluationCheckBox.addActionListener(this);
		evaluationPanel.add(evaluationCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, evaluationPanel, gridbag, c, 0);

		// Profile Assert
		addLabel(generalSettingsPanel, new JLabel("Profile Assert:"), gridbag,
				c, 1);
		JPanel profileAssertPanel = new JPanel(new BorderLayout());

		profileAssertCheckBox = new JCheckBox();
		profileAssertCheckBox.addActionListener(this);
		profileAssertPanel.add(profileAssertCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, profileAssertPanel, gridbag, c,
				1);

		// Profile Retract
		addLabel(generalSettingsPanel, new JLabel("Profile Retract:"), gridbag,
				c, 2);
		JPanel profileRetractPanel = new JPanel(new BorderLayout());

		profileRetractCheckBox = new JCheckBox();
		profileRetractCheckBox.addActionListener(this);
		profileRetractPanel.add(profileRetractCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, profileRetractPanel, gridbag,
				c, 2);

		// Profile Fire
		addLabel(generalSettingsPanel, new JLabel("Profile Fire:"), gridbag, c,
				3);
		JPanel profileFirePanel = new JPanel(new BorderLayout());

		profileFireCheckBox = new JCheckBox();
		profileFireCheckBox.addActionListener(this);
		profileFirePanel.add(profileFireCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, profileFirePanel, gridbag, c, 3);

		// Profile Add Activation
		addLabel(generalSettingsPanel, new JLabel("Profile Add Activation:"),
				gridbag, c, 4);
		JPanel profileAddActivationPanel = new JPanel(new BorderLayout());

		profileAddActivationCheckBox = new JCheckBox();
		profileAddActivationCheckBox.addActionListener(this);
		profileAddActivationPanel.add(profileAddActivationCheckBox,
				BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, profileAddActivationPanel,
				gridbag, c, 4);

		// Profile Remove Activation
		addLabel(generalSettingsPanel,
				new JLabel("Profile Remove Activation:"), gridbag, c, 5);
		JPanel profileRemoveActivationPanel = new JPanel(new BorderLayout());

		profileRemoveActivationCheckBox = new JCheckBox();
		profileRemoveActivationCheckBox.addActionListener(this);
		profileRemoveActivationPanel.add(profileRemoveActivationCheckBox,
				BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, profileRemoveActivationPanel,
				gridbag, c, 5);

		// Activations
		addLabel(generalSettingsPanel, new JLabel("Watch Activations:"),
				gridbag, c, 6);
		JPanel watchActivationsPanel = new JPanel(new BorderLayout());

		watchActivationsCheckBox = new JCheckBox();
		watchActivationsCheckBox.addActionListener(this);
		watchActivationsPanel.add(watchActivationsCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, watchActivationsPanel, gridbag,
				c, 6);

		// Facts
		addLabel(generalSettingsPanel, new JLabel("Watch Facts:"), gridbag, c,
				7);
		JPanel watchFactsPanel = new JPanel(new BorderLayout());

		watchFactsCheckBox = new JCheckBox();
		watchFactsCheckBox.addActionListener(this);
		watchFactsPanel.add(watchFactsCheckBox, BorderLayout.WEST);
		addInputComponent(generalSettingsPanel, watchFactsPanel, gridbag, c, 7);

		// Rules
		addLabel(generalSettingsPanel, new JLabel("Watch Rules:"), gridbag, c,
				8);
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
		moduleSettingsPanel.setBorder(BorderFactory
				.createTitledBorder("Strategy Settings"));

		// Setting for main (is the default)
		addLabel(moduleSettingsPanel, new JLabel("Strategy for MAIN:"),
				gridbag, c, 0);
		JPanel strategyPanelMain = new JPanel(new BorderLayout());

		strategySelectorMain = new JComboBox();
		strategySelectorMain.addActionListener(this);
		strategyPanelMain.add(strategySelectorMain, BorderLayout.WEST);
		addInputComponent(moduleSettingsPanel, strategyPanelMain, gridbag, c, 0);

		// Preselect the module
		addLabel(moduleSettingsPanel, new JLabel("Other Modules:"), gridbag, c,
				1);
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
		attentionField
				.setText("Changes you make here are lost after a restart.\nOnly the settings for MAIN-module are made persistent.");
		
		addInputComponent(moduleSettingsPanel, attentionField, gridbag, c, 3);

		mainPanel.add(moduleSettingsPanel);

		add(new JScrollPane(mainPanel));
	}

	@Override
	public void save() {
		gui.getPreferences().put("engine.evaluation",
				new Boolean(false).toString());
		gui.getPreferences().put("engine.profileAssert",
				new Boolean(false).toString());
		gui.getPreferences().put("engine.profileRetract",
				new Boolean(false).toString());
		gui.getPreferences().put("engine.profileFire",
				new Boolean(false).toString());
		gui.getPreferences().put("engine.profileAddActivation",
				new Boolean(false).toString());
		gui.getPreferences().put("engine.profileRemoveActivation",
				new Boolean(false).toString());
		gui.getPreferences().put("engine.watchActivations",
				new Boolean(false).toString());
		gui.getPreferences().put("engine.watchFacts",
				new Boolean(false).toString());
		gui.getPreferences().put("engine.watchRules",
				new Boolean(false).toString());
	}

	public void actionPerformed(ActionEvent event) {

		StringChannel guiStringChannel = gui.getStringChannel();

		if (event.getSource() == evaluationCheckBox) {
			if (evaluationCheckBox.isSelected())
				guiStringChannel.executeCommand("(watch evaluation)");
			else
				guiStringChannel.executeCommand("(unwatch evaluation)");
		} else if (event.getSource() == profileAssertCheckBox) {
			if (profileAssertCheckBox.isSelected())
				guiStringChannel.executeCommand("(profile assert)");
			else
				guiStringChannel.executeCommand("(unprofile assert)");
		} else if (event.getSource() == profileRetractCheckBox) {
			if (profileRetractCheckBox.isSelected())
				guiStringChannel.executeCommand("(profile retract)");
			else
				guiStringChannel.executeCommand("(unprofile retract)");
		} else if (event.getSource() == profileFireCheckBox) {
			if (profileFireCheckBox.isSelected())
				guiStringChannel.executeCommand("(profile fire)");
			else
				guiStringChannel.executeCommand("(unprofile fire)");
		} else if (event.getSource() == profileAddActivationCheckBox) {
			if (profileAddActivationCheckBox.isSelected())
				guiStringChannel.executeCommand("(profile add-activation)");
			else
				guiStringChannel.executeCommand("(unprofile add-activation)");
		} else if (event.getSource() == profileRemoveActivationCheckBox) {
			if (profileRemoveActivationCheckBox.isSelected())
				guiStringChannel.executeCommand("(profile remove-activation)");
			else
				guiStringChannel
						.executeCommand("(unprofile remove-activation)");
		} else if (event.getSource() == watchActivationsCheckBox) {
			if (profileRemoveActivationCheckBox.isSelected())
				guiStringChannel.executeCommand("(watch activations)");
			else
				guiStringChannel.executeCommand("(unwatch activations)");
		} else if (event.getSource() == watchFactsCheckBox) {
			if (profileRemoveActivationCheckBox.isSelected())
				guiStringChannel.executeCommand("(watch facts)");
			else
				guiStringChannel.executeCommand("(unwatch facts)");
		} else if (event.getSource() == watchRulesCheckBox) {
			if (profileRemoveActivationCheckBox.isSelected())
				guiStringChannel.executeCommand("(watch rules)");
			else
				guiStringChannel.executeCommand("(unwatch rules)");
		} else if (event.getSource().equals(moduleSelector)) {
			initStrategySelector();
		} else if (event.getSource().equals(strategySelector)) {
			if (strategySelector.getSelectedItem() != null
					&& moduleSelector.getSelectedItem() != null) {
				String strategyName = strategySelector.getSelectedItem()
						.toString();
				String moduleName = moduleSelector.getSelectedItem().toString();
				Module module = gui.getEngine().getModule(moduleName);
				try {
					gui.getEngine().getAgendas().getAgenda(module)
							.setConflictResolutionStrategy(
									ConflictResolutionStrategy
											.getStrategy(strategyName));
				} catch (InstantiationException e) {
					JOptionPane.showMessageDialog(this, e,
							"Error setting the strategy.",
							JOptionPane.ERROR_MESSAGE);
				} catch (IllegalAccessException e) {
					JOptionPane.showMessageDialog(this, e,
							"Error setting the strategy.",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public void refresh() {
		initStrategySelectorMain();
		initModuleSelector();
		initStrategySelector();
	}

	private void initModuleSelector() {
		moduleSelector.removeAllItems();
		Collection<Module> modules = gui.getEngine().getModules()
				.getModuleList();
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
			ConflictResolutionStrategy currentStrategy = gui.getEngine()
					.getAgendas().getAgenda(module)
					.getConflictResolutionStrategy();
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
		ConflictResolutionStrategy currentStrategy = gui.getEngine()
				.getAgendas().getAgenda(module).getConflictResolutionStrategy();
		Set<String> strategies = ConflictResolutionStrategy.getStrategies();
		for (String strategyName : strategies) {
			strategySelectorMain.addItem(strategyName);
			if (strategyName.equals(currentStrategy.getName())) {
				strategySelectorMain.setSelectedItem(strategyName);
			}
		}
	}
}
