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

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jamocha.gui.JamochaGui;

public class ShellSettingsPanel extends AbstractSettingsPanel {

	private static final long serialVersionUID = -7136144663514250335L;

	private JComboBox fonts;

	private JComboBox fontsizes;

	public ShellSettingsPanel(JamochaGui gui) {
		super(gui);
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		setLayout(gridbag);
		addLabel(this, new JLabel("Font:"), gridbag, c, 0);
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		Font allFonts[] = ge.getAllFonts();
		fonts = new JComboBox(allFonts);
		Font selFont = null;
		String selFontName = gui.getPreferences().get("shell.font","Courier");
		for( Font curFont : allFonts) {
			if(curFont.getFontName().equals(selFontName)) {
				selFont = curFont;
				break;
			}
		}
		if (selFont != null) {
			fonts.setSelectedItem(selFont);
		}
		fonts.setRenderer(new FontListCellRenderer());
		addInputComponent(this, fonts, gridbag, c, 0);

		addLabel(this, new JLabel("Fontsize:"), gridbag, c, 1);
		Integer[] sizes = new Integer[17];
		for (int i = 0; i < sizes.length; ++i) {
			sizes[i] = 8 + i;
		}
		fontsizes = new JComboBox(sizes);
		fontsizes.setSelectedItem(gui.getPreferences().getInt("shell.fontsize",
				12));
		addInputComponent(this, fontsizes, gridbag, c, 1);
	}

	@Override
	public void save() {
		gui.getPreferences().put("shell.font",
				((Font) fonts.getSelectedItem()).getFontName());
		gui.getPreferences().putInt("shell.fontsize",
				(Integer) fontsizes.getSelectedItem());
	}

	private class FontListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		public FontListCellRenderer() {
			super();
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			setFont(((Font) value).deriveFont(12.0f));
			setText(((Font) value).getFontName());
			return this;
		}
	}

}
