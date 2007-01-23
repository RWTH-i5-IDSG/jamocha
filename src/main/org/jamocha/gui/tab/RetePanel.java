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
package org.jamocha.gui.tab;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;

import org.jamocha.gui.JamochaGui;
import org.jamocha.rete.visualisation.Visualiser;

/**
 * This class integrates the Visualiser view into the Jamocha GUI.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class RetePanel extends AbstractJamochaPanel {

	private static final long serialVersionUID = -651077761699385096L;

	/**
	 * The Visualiser Object.
	 */
	private Visualiser visualiser = null;

	/**
	 * The Panel containing the Visualiser.
	 */
	private JPanel visualiserPanel;

	/**
	 * The main constructor for a RetePanel.
	 * 
	 * @param engine
	 *            The Jamocha engine that should be used with this GUI.
	 */
	public RetePanel(JamochaGui gui) {
		super(gui);

		setLayout(new BorderLayout());

		visualiserPanel = new JPanel();
		visualiserPanel.setLayout(new CardLayout());
		add(visualiserPanel, BorderLayout.CENTER);

		initVisualiser();
	}

	/**
	 * Sets the visualiser to null when closing the GUI.
	 */
	@Override
	public void close() {
		visualiser = null;
	}

	public void settingsChanged() {

	}

	/**
	 * Initializes the Visualiser with the current Rete-network.
	 * 
	 */
	private void initVisualiser() {
		visualiser = new Visualiser(gui.getEngine());
		JPanel panel = visualiser.getVisualiserPanel();
		visualiserPanel.removeAll();
		visualiserPanel.add("view", panel);
		((CardLayout) visualiserPanel.getLayout()).last(visualiserPanel);
	}

}
