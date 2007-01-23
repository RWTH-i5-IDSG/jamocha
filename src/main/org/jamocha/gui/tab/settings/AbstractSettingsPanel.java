package org.jamocha.gui.tab.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jamocha.gui.JamochaGui;

public abstract class AbstractSettingsPanel extends JPanel {

	protected JamochaGui gui;

	public AbstractSettingsPanel(JamochaGui gui) {
		this.gui = gui;
	}
	
	public abstract void save();

	public void addLabel(JPanel parent, JLabel label, GridBagLayout gridbag,
			GridBagConstraints c, int row) {
		c.gridx = 0;
		c.gridy = row;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(label, c);
		parent.add(label);
	}

	public void addInputComponent(JPanel parent, JComponent comp,
			GridBagLayout gridbag, GridBagConstraints c, int row) {
		c.gridx = 1;
		c.gridy = row;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(comp, c);
		parent.add(comp);
	}

}
