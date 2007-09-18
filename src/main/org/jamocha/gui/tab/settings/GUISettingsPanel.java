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
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jamocha.gui.JamochaGui;
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.settings.JamochaSettings;
import org.jamocha.settings.SettingsChangedListener;

/**
 * With this Panel the User can change the look of the Shell.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class GUISettingsPanel extends AbstractSettingsPanel implements
		ActionListener, ItemListener, ChangeListener, SettingsChangedListener {

	private static final String GUI_SHELL_AUTOCOMPLETION = "gui.shell.autocompletion";

	private static final String GUI_SHELL_BACKGROUNDCOLOR = "gui.shell.backgroundcolor";

	private static final String GUI_SHELL_FONTCOLOR = "gui.shell.fontcolor";

	private static final String GUI_SHELL_FONTSIZE = "gui.shell.fontsize";

	private static final String GUI_SHELL_FONT = "gui.shell.font";

	private static final long serialVersionUID = -7136144663514250335L;

	private JComboBox fonts;

	private JComboBox fontsizes;

	private JButton fontColorChooserButton;

	private JTextField fontColorChooserPreview;

	private JButton backgroundColorChooserButton;

	private JTextField backgroundColorChooserPreview;

	private JComboBox factSortOptionsCombo;

	private JCheckBox autoCompletion;

	private String[] interestedSettings = { GUI_SHELL_FONT,
			GUI_SHELL_FONTSIZE, GUI_SHELL_FONTCOLOR,
			GUI_SHELL_BACKGROUNDCOLOR, GUI_SHELL_AUTOCOMPLETION };

	public GUISettingsPanel(JamochaGui gui) {
		super(gui);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;

		// -------------------
		// Shell Settings
		// -------------------

		JPanel shellPanel = new JPanel();
		shellPanel.setLayout(gridbag);
		shellPanel.setBorder(BorderFactory.createTitledBorder("Shell"));

		// Font
		addLabel(shellPanel, new JLabel("Font:"), gridbag, c, 0);
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		Font allFonts[] = ge.getAllFonts();
		fonts = new JComboBox(filterFonts(allFonts));
		Font selFont = null;
		String selFontName = settings.getString(GUI_SHELL_FONT);
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
		fonts.addItemListener(this);
		JPanel fontsPanel = new JPanel(new BorderLayout());
		fontsPanel.add(fonts, BorderLayout.WEST);
		addInputComponent(shellPanel, fontsPanel, gridbag, c, 0);

		// Fontsize
		addLabel(shellPanel, new JLabel("Fontsize:"), gridbag, c, 1);
		Integer[] sizes = new Integer[17];
		for (int i = 0; i < sizes.length; ++i) {
			sizes[i] = 8 + i;
		}
		fontsizes = new JComboBox(sizes);
		fontsizes.setSelectedItem(settings.getInt(GUI_SHELL_FONTSIZE));
		fontsizes.addItemListener(this);
		JPanel fontsizesPanel = new JPanel(new BorderLayout());
		fontsizesPanel.add(fontsizes, BorderLayout.WEST);
		addInputComponent(shellPanel, fontsizesPanel, gridbag, c, 1);

		// Fontcolor
		addLabel(shellPanel, new JLabel("Fontcolor:"), gridbag, c, 2);
		JPanel fontColorChooserPanel = new JPanel(new FlowLayout(
				FlowLayout.LEFT));
		fontColorChooserPreview = new JTextField(5);
		fontColorChooserPreview.setEditable(false);
		fontColorChooserPreview.setBackground(new Color(settings
				.getInt(GUI_SHELL_FONTCOLOR)));
		fontColorChooserButton = new JButton("Choose Color", IconLoader
				.getImageIcon("color_swatch"));
		fontColorChooserButton.addActionListener(this);
		fontColorChooserPanel.add(fontColorChooserPreview);
		fontColorChooserPanel.add(fontColorChooserButton);
		addInputComponent(shellPanel, fontColorChooserPanel, gridbag, c, 2);

		// Backgroundcolor
		addLabel(shellPanel, new JLabel("Backgroundcolor:"), gridbag, c, 3);
		JPanel backgroundColorChooserPanel = new JPanel(new FlowLayout(
				FlowLayout.LEFT));
		backgroundColorChooserPreview = new JTextField(5);
		backgroundColorChooserPreview.setEditable(false);
		backgroundColorChooserPreview.setBackground(new Color(settings
				.getInt(GUI_SHELL_BACKGROUNDCOLOR)));
		backgroundColorChooserButton = new JButton("Choose Color", IconLoader
				.getImageIcon("color_swatch"));
		backgroundColorChooserButton.addActionListener(this);
		backgroundColorChooserPanel.add(backgroundColorChooserPreview);
		backgroundColorChooserPanel.add(backgroundColorChooserButton);
		addInputComponent(shellPanel, backgroundColorChooserPanel, gridbag, c,
				3);

		// Autocompletion
		addLabel(shellPanel, new JLabel(""), gridbag, c, 4);
		autoCompletion = new JCheckBox("Enable Auto-Completion");
		autoCompletion.setSelected(settings.getBoolean(GUI_SHELL_AUTOCOMPLETION));
		autoCompletion.addChangeListener(this);
		addInputComponent(shellPanel, autoCompletion, gridbag, c, 4);

		mainPanel.add(shellPanel);

		// -------------------
		// Factspanel Settings
		// -------------------

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		c.weightx = 1.0;
		JPanel factsPanel = new JPanel();
		factsPanel.setLayout(gridbag);
		factsPanel.setBorder(BorderFactory.createTitledBorder("Facts"));

		// Sort Facts on Load
		addLabel(factsPanel, new JLabel("Auto sort Facts by ID:"), gridbag, c,
				1);
		String[] factSortOptions = { "No sorting", "Sort ascending",
				"Sort descending" };
		factSortOptionsCombo = new JComboBox(factSortOptions);
		factSortOptionsCombo.setSelectedItem(gui.getPreferences().get(
				"facts.autoSort", "No sorting"));
		JPanel factSortOptionsPanel = new JPanel(new BorderLayout());
		factSortOptionsPanel.add(factSortOptionsCombo, BorderLayout.WEST);
		addInputComponent(factsPanel, factSortOptionsPanel, gridbag, c, 1);

		mainPanel.add(factsPanel);

		add(new JScrollPane(mainPanel));

		settings.addListener(this, interestedSettings);
	}

	@Override
	public void save() {
		gui.getPreferences().put("shell.font",
				((Font) fonts.getSelectedItem()).getFontName());
		gui.getPreferences().putInt("shell.fontsize",
				(Integer) fontsizes.getSelectedItem());
		gui.getPreferences().putInt("shell.fontcolor",
				(Integer) fontColorChooserPreview.getBackground().getRGB());
		gui.getPreferences().putInt(
				"shell.backgroundcolor",
				(Integer) backgroundColorChooserPreview.getBackground()
						.getRGB());
		gui.getPreferences().put("facts.autoSort",
				factSortOptionsCombo.getSelectedItem().toString());
		gui.getPreferences().putBoolean("shell.autocompletion",
				autoCompletion.isSelected());
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

	private Font[] filterFonts(Font[] fonts) {
		List<Font> res = new LinkedList<Font>();
		// TODO I want to replace it by another method: compare width of letter
		// i to the width of letter w or so. AW
		for (Font font : fonts) {
			if (font.getName().matches("(.*)[M|m]ono(.*)")
					|| font.getFamily().matches("(.*)[M|m]ono(.*)")
					|| font.getName().matches("Monaco")
					|| font.getName().matches("(.*)[C|c]ourier(.*)")) {
				res.add(font);
			}

		}
		fonts = new Font[res.size()];
		for (int i = 0; i < fonts.length; ++i) {
			fonts[i] = res.get(i);
		}
		return fonts;
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == fontColorChooserButton) {
			Color newColor = JColorChooser.showDialog(this,
					"Choose a Fontcolor", new Color(settings
							.getInt(GUI_SHELL_FONTCOLOR)));
			if (newColor != null) {
				fontColorChooserPreview.setBackground(newColor);
				settings.set(GUI_SHELL_FONTCOLOR, newColor.getRGB());
			}
		} else if (event.getSource() == backgroundColorChooserButton) {
			Color newColor = JColorChooser.showDialog(this,
					"Choose a Backgroundcolor", new Color(settings
							.getInt(GUI_SHELL_BACKGROUNDCOLOR)));
			if (newColor != null) {
				backgroundColorChooserPreview.setBackground(newColor);
				settings.set(GUI_SHELL_BACKGROUNDCOLOR, newColor.getRGB());
			}
		}
	}

	public void itemStateChanged(ItemEvent event) {
		if (event.getSource().equals(fonts)) {
			settings.set(GUI_SHELL_FONT, ((Font) fonts.getSelectedItem())
					.getFontName());
		} else if (event.getSource().equals(fontsizes)) {
			settings.set(GUI_SHELL_FONTSIZE, (Integer) fontsizes
					.getSelectedItem());
		}
	}

	public void stateChanged(ChangeEvent event) {
		if (event.getSource().equals(autoCompletion)) {
			settings.set(GUI_SHELL_AUTOCOMPLETION, autoCompletion
					.isSelected());
		}
	}

	public void settingsChanged(String propertyName) {
		// TODO Auto-generated method stub

	}

}
