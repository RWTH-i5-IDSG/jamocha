/*
 * Copyright 2007 Alexander Wilden
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

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jamocha.gui.JamochaGui;
import org.jamocha.settings.JamochaSettings;

/**
 * This is an abstract panel that covers all common functions of the panels in
 * jamocha. Every panel in the tabbedPane must inherit from this class.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public abstract class AbstractJamochaPanel extends JPanel {

	/**
	 * The JamochaGui Object. We need it to get the engine or other future
	 * purposes.
	 */
	protected JamochaGui gui;
	protected JamochaSettings settings;

	/**
	 * The constructor expecting a JamochaGui as argument.
	 * 
	 * @param gui
	 *            The active JamocheGui.
	 */
	public AbstractJamochaPanel(JamochaGui gui) {
		this.gui = gui;
		settings = JamochaSettings.getInstance();
	}

	/**
	 * This function is called whenever this Panel gains the focus in the
	 * tabbedPane. A non abstract implementation of this class should override
	 * it and do whatever has to be done when gaining focus. The Shell for
	 * example sets the focus to the textarea and not to itself.
	 * 
	 */
	public void setFocus() {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				AbstractJamochaPanel.this.requestFocus();
			}

		});
	}

	/**
	 * This function is called when the gui is closed and must be implemented by
	 * every class that extends this class. Here all the necessary cleanup
	 * should be done when closing the gui.
	 * 
	 */
	public abstract void close();

}
