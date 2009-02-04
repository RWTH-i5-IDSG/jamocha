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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jamocha.application.gui.JamochaGui;
import org.jamocha.application.gui.icons.IconLoader;
import org.jamocha.application.gui.tab.settings.AbstractSettingsPanel;
import org.jamocha.application.gui.tab.settings.EngineSettingsPanel;
import org.jamocha.application.gui.tab.settings.GUISettingsPanel;

/**
 * A Panel to change the settings of the jamocha rule engine or this gui.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class SettingsPanel extends AbstractJamochaPanel implements
		ActionListener {

	private static final long serialVersionUID = 1934727733895902279L;

	private final JTabbedPane tabbedPane;

	private final JButton allDefaultButton;

	private final List<AbstractSettingsPanel> panels = new LinkedList<AbstractSettingsPanel>();

	public SettingsPanel(final JamochaGui gui) {
		super(gui);
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();

		final EngineSettingsPanel engineSettingsPanel = new EngineSettingsPanel(
				gui);
		tabbedPane.addTab("Engine", null, engineSettingsPanel,
				"Engine Settings");
		panels.add(engineSettingsPanel);

		final GUISettingsPanel guiSettingsPanel = new GUISettingsPanel(gui);
		tabbedPane.addTab("GUI", null, guiSettingsPanel, "GUI Settings");
		panels.add(guiSettingsPanel);

		for (final AbstractSettingsPanel panel : panels) {
			panel.refresh();
		}

		add(tabbedPane, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
				5, 1));
		allDefaultButton = new JButton("All back to default", IconLoader
				.getImageIcon("cog_go"));
		allDefaultButton.addActionListener(this);
		buttonPanel.add(allDefaultButton);
		add(buttonPanel, BorderLayout.SOUTH);

	}

	@Override
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
		for (final AbstractSettingsPanel panel : panels) {
			panel.refresh();
		}
	}

	public void actionPerformed(final ActionEvent event) {
		if (event.getSource() == allDefaultButton) {
			final int sel = JOptionPane.showConfirmDialog(this,
					"Do you really want to set everything to default?",
					"Attention", JOptionPane.YES_NO_OPTION);
			if (sel == JOptionPane.YES_OPTION) {
				for (final AbstractSettingsPanel panel : panels) {
					panel.setDefaults();
				}
			}
		}
	}

}
