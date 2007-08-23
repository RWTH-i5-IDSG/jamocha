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
package org.jamocha.gui.tab;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.BackingStoreException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jamocha.gui.JamochaGui;
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.gui.tab.settings.AbstractSettingsPanel;
import org.jamocha.gui.tab.settings.EngineSettingsPanel;
import org.jamocha.gui.tab.settings.GUISettingsPanel;

/**
 * A Panel to change the settings of the jamocha rule engine or this gui.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class SettingsPanel extends AbstractJamochaPanel implements
		ActionListener {

	private static final long serialVersionUID = 1934727733895902279L;

	private JTabbedPane tabbedPane;

	private JButton saveButton;

	private List<AbstractSettingsPanel> panels = new LinkedList<AbstractSettingsPanel>();

	public SettingsPanel(JamochaGui gui) {
		super(gui);
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();

		EngineSettingsPanel engineSettingsPanel = new EngineSettingsPanel(gui);
		tabbedPane.addTab("Engine", null, engineSettingsPanel,
				"Engine Settings");
		panels.add(engineSettingsPanel);

		GUISettingsPanel guiSettingsPanel = new GUISettingsPanel(gui);
		tabbedPane.addTab("GUI", null, guiSettingsPanel, "GUI Settings");
		panels.add(guiSettingsPanel);

		add(tabbedPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		saveButton = new JButton("Save Changes", IconLoader
				.getImageIcon("disk"));
		saveButton.addActionListener(this);
		buttonPanel.add(saveButton);
		add(buttonPanel, BorderLayout.SOUTH);

		loadSettings();

	}

	private void loadSettings() {
		for (AbstractSettingsPanel panel : panels) {
			panel.loadSettings();
		}
	}

	public void close() {
	}

	public void settingsChanged() {

	}

	/**
	 * Sets the focus of this panel and by this sets the focus to the outputArea
	 * so that the user doesn't have to click on it before he can start typing.
	 * 
	 */
	@Override
	public void setFocus() {
		super.setFocus();
		for (AbstractSettingsPanel panel : panels) {
			panel.refresh();
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == saveButton) {
			for (AbstractSettingsPanel panel : panels) {
				panel.save();
				panel.loadSettings();
			}
			gui.settingsChanged();
			try {
				gui.getPreferences().flush();
			} catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
