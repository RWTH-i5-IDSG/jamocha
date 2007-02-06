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
package org.jamocha.gui.tab.settings;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


import org.jamocha.gui.JamochaGui;

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
	
	public EngineSettingsPanel(JamochaGui gui) {
		super(gui);
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		setLayout(gridbag);
		
		// Evaluation
		addLabel(this, new JLabel("Evaluation"), gridbag, c, 0);
		JPanel evaluationPanel = new JPanel(new BorderLayout());
		
		evaluationCheckBox = new JCheckBox();
		evaluationCheckBox.setEnabled(true);
		evaluationCheckBox.addActionListener(this);
		evaluationPanel.add(evaluationCheckBox,BorderLayout.WEST);
		addInputComponent(this, evaluationPanel, gridbag, c, 0);

		// Profile Assert
		addLabel(this, new JLabel("Profile Assert:"), gridbag, c, 1);
		JPanel profileAssertPanel = new JPanel(new BorderLayout());

		profileAssertCheckBox = new JCheckBox();
		profileAssertCheckBox.setEnabled(true);
		profileAssertCheckBox.addActionListener(this);
		profileAssertPanel.add(profileAssertCheckBox,BorderLayout.WEST);
		addInputComponent(this, profileAssertPanel, gridbag, c, 1);
		
		// Profile Retract
		addLabel(this, new JLabel("Profile Retract:"), gridbag, c, 2);
		JPanel profileRetractPanel = new JPanel(new BorderLayout());

		profileRetractCheckBox = new JCheckBox();
		profileRetractCheckBox.setEnabled(true);
		profileRetractCheckBox.addActionListener(this);
		profileRetractPanel.add(profileRetractCheckBox,BorderLayout.WEST);
		addInputComponent(this, profileRetractPanel, gridbag, c, 2);
		
		// Profile Fire
		addLabel(this, new JLabel("Profile Fire:"), gridbag, c, 3);
		JPanel profileFirePanel = new JPanel(new BorderLayout());

		profileFireCheckBox = new JCheckBox();
		profileFireCheckBox.setEnabled(true);
		profileFireCheckBox.addActionListener(this);
		profileFirePanel.add(profileFireCheckBox,BorderLayout.WEST);
		addInputComponent(this, profileFirePanel, gridbag, c, 3);
		
		// Profile Add Activation
		addLabel(this, new JLabel("Profile Add Activation:"), gridbag, c, 4);
		JPanel profileAddActivationPanel = new JPanel(new BorderLayout());

		profileAddActivationCheckBox = new JCheckBox();
		profileAddActivationCheckBox.setEnabled(true);
		profileAddActivationCheckBox.addActionListener(this);
		profileAddActivationPanel.add(profileAddActivationCheckBox,BorderLayout.WEST);
		addInputComponent(this, profileAddActivationPanel, gridbag, c, 4);
		
		// Profile Remove Activation
		addLabel(this, new JLabel("Profile Remove Activation:"), gridbag, c, 5);
		JPanel profileRemoveActivationPanel = new JPanel(new BorderLayout());

		profileRemoveActivationCheckBox = new JCheckBox();
		profileRemoveActivationCheckBox.setEnabled(true);
		profileRemoveActivationCheckBox.addActionListener(this);
		profileRemoveActivationPanel.add(profileRemoveActivationCheckBox,BorderLayout.WEST);
		addInputComponent(this, profileRemoveActivationPanel, gridbag, c, 5);
		
		// Activations
		addLabel(this, new JLabel(" Watch Activations:"), gridbag, c, 6);
		JPanel watchActivationsPanel = new JPanel(new BorderLayout());

		watchActivationsCheckBox = new JCheckBox();
		watchActivationsCheckBox.setEnabled(true);
		watchActivationsCheckBox.addActionListener(this);
		watchActivationsPanel.add(watchActivationsCheckBox,BorderLayout.WEST);
		addInputComponent(this, watchActivationsPanel, gridbag, c, 6);
		
		// Facts 
		addLabel(this, new JLabel("Watch Facts:"), gridbag, c, 7);
		JPanel watchFactsPanel = new JPanel(new BorderLayout());

		watchFactsCheckBox = new JCheckBox();
		watchFactsCheckBox.setEnabled(true);
		watchFactsCheckBox.addActionListener(this);
		watchFactsPanel.add(watchFactsCheckBox,BorderLayout.WEST);
		addInputComponent(this, watchFactsPanel, gridbag, c, 7);
		
		// Rules 
		addLabel(this, new JLabel("Watch Rules:"), gridbag, c, 8);
		JPanel watchRulesPanel = new JPanel(new BorderLayout());

		watchRulesCheckBox = new JCheckBox();
		watchRulesCheckBox.setEnabled(true);
		watchRulesCheckBox.addActionListener(this);
		watchRulesPanel.add(watchRulesCheckBox,BorderLayout.WEST);
		addInputComponent(this, watchRulesPanel, gridbag, c, 8);
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

		Boolean checkBoxSelection;

		if (event.getSource() == evaluationCheckBox) {
			checkBoxSelection = evaluationCheckBox.isSelected();
			System.out.println("Evaluation:" + checkBoxSelection.toString());
		} else if(event.getSource() == profileAssertCheckBox) {
			checkBoxSelection = profileAssertCheckBox.isSelected();
			System.out.println("Profile Assert:" + checkBoxSelection.toString());
		} else if(event.getSource() == profileRetractCheckBox) {
			checkBoxSelection = profileRetractCheckBox.isSelected();
			System.out.println("Profile Retract:" + checkBoxSelection.toString());
		} else if(event.getSource() == profileFireCheckBox) {
			checkBoxSelection = profileFireCheckBox.isSelected();
			System.out.println("Profile Fire:" + checkBoxSelection.toString());
		} else if(event.getSource() == profileAddActivationCheckBox) {
			checkBoxSelection = profileAddActivationCheckBox.isSelected();
			System.out.println("Profile Add Activation:" + checkBoxSelection.toString());
		} else if(event.getSource() == profileRemoveActivationCheckBox) {
			checkBoxSelection = profileRemoveActivationCheckBox.isSelected();
			System.out.println("Profile Remove Activation:" + checkBoxSelection.toString());
		} else if(event.getSource() == watchActivationsCheckBox) {
			checkBoxSelection = watchActivationsCheckBox.isSelected();
			System.out.println("Watch Actiations:" + checkBoxSelection.toString());
		} else if(event.getSource() == watchFactsCheckBox) {
			checkBoxSelection = watchFactsCheckBox.isSelected();
			System.out.println("Watch Facts:" + checkBoxSelection.toString());
		} else if(event.getSource() == watchRulesCheckBox) {
			checkBoxSelection = watchRulesCheckBox.isSelected();
			System.out.println("Watch Rules:" + checkBoxSelection.toString());
		}
	}
}
