/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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
package org.jamocha.application.gui.tab.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jamocha.application.gui.JamochaGui;
import org.jamocha.settings.JamochaSettings;

/**
 * The abstract class for all settings panels.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public abstract class AbstractSettingsPanel extends JPanel {

	protected JamochaGui gui;

	protected JamochaSettings settings;

	public AbstractSettingsPanel(final JamochaGui gui) {
		this.gui = gui;
		settings = JamochaSettings.getInstance();
	}

	/**
	 * This function is called whenever the specific SettingsPanel gains the
	 * focus and is used to initialize the comboboxes etc. with possibly new
	 * values.
	 */
	public abstract void refresh();

	public abstract void setDefaults();

	public void addLabel(final JPanel parent, final JLabel label,
			final GridBagLayout gridbag, final GridBagConstraints c,
			final int row) {
		c.gridx = 0;
		c.gridy = row;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(label, c);
		parent.add(label);
	}

	public void addInputComponent(final JPanel parent, final JComponent comp,
			final GridBagLayout gridbag, final GridBagConstraints c,
			final int row) {
		c.gridx = 1;
		c.gridy = row;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(comp, c);
		parent.add(comp);
	}

}
