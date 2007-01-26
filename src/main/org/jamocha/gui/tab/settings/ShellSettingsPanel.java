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

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jamocha.gui.JamochaGui;
import org.jamocha.gui.icons.IconLoader;

public class ShellSettingsPanel extends AbstractSettingsPanel implements
		ActionListener {

	private static final long serialVersionUID = -7136144663514250335L;

	private JComboBox fonts;

	private JComboBox fontsizes;

	private JButton fontColorChooserButton;

	private JTextField fontColorChooserPreview;

	private JButton backgroundColorChooserButton;

	private JTextField backgroundColorChooserPreview;

	public ShellSettingsPanel(JamochaGui gui) {
		super(gui);
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		setLayout(gridbag);
		
		// Font
		addLabel(this, new JLabel("Font:"), gridbag, c, 0);
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		Font allFonts[] = ge.getAllFonts();
		fonts = new JComboBox(allFonts);
		Font selFont = null;
		String selFontName = gui.getPreferences().get("shell.font", "Courier");
		for (Font curFont : allFonts) {
			if (curFont.getFontName().equals(selFontName)) {
				selFont = curFont;
				break;
			}
		}
		if (selFont != null) {
			fonts.setSelectedItem(selFont);
		}
		fonts.setRenderer(new FontListCellRenderer());
		addInputComponent(this, fonts, gridbag, c, 0);

		// Fontsize
		addLabel(this, new JLabel("Fontsize:"), gridbag, c, 1);
		Integer[] sizes = new Integer[17];
		for (int i = 0; i < sizes.length; ++i) {
			sizes[i] = 8 + i;
		}
		fontsizes = new JComboBox(sizes);
		fontsizes.setSelectedItem(gui.getPreferences().getInt("shell.fontsize",
				12));
		addInputComponent(this, fontsizes, gridbag, c, 1);

		// Fontcolor
		addLabel(this, new JLabel("Fontcolor:"), gridbag, c, 2);
		JPanel fontColorChooserPanel = new JPanel(new FlowLayout());
		fontColorChooserPreview = new JTextField(5);
		fontColorChooserPreview.setEditable(false);
		fontColorChooserPreview.setBackground(new Color(gui.getPreferences()
				.getInt("shell.fontcolor", Color.WHITE.getRGB())));
		fontColorChooserButton = new JButton("Choose Color", IconLoader
				.getImageIcon("color_swatch"));
		fontColorChooserButton.addActionListener(this);
		fontColorChooserPanel.add(fontColorChooserPreview);
		fontColorChooserPanel.add(fontColorChooserButton);
		addInputComponent(this, fontColorChooserPanel, gridbag, c, 2);

		// Backgroundcolor
		addLabel(this, new JLabel("Backgroundcolor:"), gridbag, c, 3);
		JPanel backgroundColorChooserPanel = new JPanel(new FlowLayout());
		backgroundColorChooserPreview = new JTextField(5);
		backgroundColorChooserPreview.setEditable(false);
		backgroundColorChooserPreview.setBackground(new Color(gui.getPreferences()
				.getInt("shell.backgroundcolor", Color.BLACK.getRGB())));
		backgroundColorChooserButton = new JButton("Choose Color", IconLoader
				.getImageIcon("color_swatch"));
		backgroundColorChooserButton.addActionListener(this);
		backgroundColorChooserPanel.add(backgroundColorChooserPreview);
		backgroundColorChooserPanel.add(backgroundColorChooserButton);
		addInputComponent(this, backgroundColorChooserPanel, gridbag, c, 3);
	}

	@Override
	public void save() {
		gui.getPreferences().put("shell.font",
				((Font) fonts.getSelectedItem()).getFontName());
		gui.getPreferences().putInt("shell.fontsize",
				(Integer) fontsizes.getSelectedItem());
		gui.getPreferences().putInt("shell.fontcolor",
				(Integer) fontColorChooserPreview.getBackground().getRGB());
		gui.getPreferences().putInt("shell.backgroundcolor",
				(Integer) backgroundColorChooserPreview.getBackground().getRGB());
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

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == fontColorChooserButton) {
			Color newColor = JColorChooser.showDialog(this,
					"Choose a Fontcolor", new Color(gui.getPreferences()
							.getInt("shell.fontcolor", Color.WHITE.getRGB())));
			if (newColor != null) {
				fontColorChooserPreview.setBackground(newColor);
			}
		}
		else if(event.getSource() == backgroundColorChooserButton) {
			Color newColor = JColorChooser.showDialog(this,
					"Choose a Backgroundcolor", new Color(gui.getPreferences()
							.getInt("shell.backgroundcolor", Color.BLACK.getRGB())));
			if (newColor != null) {
				backgroundColorChooserPreview.setBackground(newColor);
			}
		}
	}

}
