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
package org.jamocha.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jamocha.gui.icons.IconLoader;
import org.jamocha.gui.tab.AbstractJamochaPanel;
import org.jamocha.gui.tab.FactsPanel;
import org.jamocha.gui.tab.LogPanel;
import org.jamocha.gui.tab.RetePanel;
import org.jamocha.gui.tab.SettingsPanel;
import org.jamocha.gui.tab.ShellPanel;
import org.jamocha.rete.Rete;

/**
 * 
 * JamochaGui implements a GUI for the Jamocha Rule Engine
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 * @version 0.01
 */
public class JamochaGui extends JFrame implements ChangeListener {

	static final long serialVersionUID = 1L;

	static final Preferences preferences = Preferences.userRoot().node(
			"org.jamocha.gui");

	private Rete engine;

	private JTabbedPane tabbedPane;

	private List<AbstractJamochaPanel> panels = new LinkedList<AbstractJamochaPanel>();

	private boolean exitOnClose = false;

	/**
	 * Create a GUI-Instance for Jamocha.
	 * 
	 * @param engine
	 *            The Jamocha-engine that will be used in the GUI.
	 */
	public JamochaGui(Rete engine) {

		// set up the frame
		this.setLayout(new BorderLayout());
		this.setTitle("Jamocha");
		this.setSize(750, 550);

		// create a tabbed pane
		tabbedPane = new JTabbedPane();
		this.setMinimumSize(new Dimension(600, 400));
		this.add(tabbedPane, BorderLayout.CENTER);
		this.setJMenuBar(new JamochaMenuBar(this));
		this.setLocationByPlatform(true);

		// create a rete engine
		this.engine = engine;

		// create a shell tab and add it to the tabbed pane
		ShellPanel shellPanel = new ShellPanel(this);
		tabbedPane.addTab("Shell", IconLoader
				.getImageIcon("application_osx_terminal"), shellPanel,
				"Jamocha Shell");
		panels.add(shellPanel);
		FactsPanel factsPanel = new FactsPanel(this);
		tabbedPane.addTab("Facts", IconLoader.getImageIcon("database"),
				factsPanel, "View or modify Facts");
		panels.add(factsPanel);
		RetePanel retePanel = new RetePanel(this);
		tabbedPane.addTab("Rete", IconLoader.getImageIcon("eye"), retePanel,
				"View the Rete-network");
		panels.add(retePanel);
		LogPanel logPanel = new LogPanel(this);
		tabbedPane.addTab("Log", IconLoader.getImageIcon("monitor"), logPanel,
				"View alle messages from or to the Rete-engine");
		panels.add(logPanel);
		SettingsPanel settingsPanel = new SettingsPanel(this);
		tabbedPane.addTab("Settings", IconLoader.getImageIcon("wrench"),
				settingsPanel, "Settings for Jamocha");
		panels.add(settingsPanel);

		// add the tab pane to the frame
		add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addChangeListener(this);
		// add a listener to the frame to kill the engine when the GUI is closed
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
	}

	/**
	 * This sets if only the Gui is closed on exit or also the engine. By
	 * default only the Gui will be closed.
	 * 
	 * @param exitOnClose
	 *            If false, only the gui will be closed
	 */
	public void setExitOnClose(boolean exitOnClose) {
		this.exitOnClose = exitOnClose;
	}

	/**
	 * Get the current Jamocha-engine.
	 * 
	 * @return The Jamocha-engine.
	 */
	public Rete getEngine() {
		return engine;
	}

	/**
	 * Sets the GUI visible and calls setFocus() on the shellPanel.
	 * 
	 */
	public void showGui() {
		setMinimumSize(new Dimension(600, 400));
		setVisible(true);
		panels.get(0).setFocus();
	}

	/**
	 * Returns the preferences for the JamochaGui
	 * 
	 * @return The Preferences-Node
	 */
	public Preferences getPreferences() {
		return preferences;
	}

	/**
	 * Informs all Panels, that some Settings might have changed.
	 * 
	 */
	public void settingsChanged() {
		for (AbstractJamochaPanel panel : panels) {
			panel.settingsChanged();
		}
	}

	/**
	 * Closes the GUI and informs all Panels.
	 * 
	 */
	public void close() {
		for (AbstractJamochaPanel panel : panels) {
			panel.close();
		}
		setVisible(false);
		dispose();
		if (exitOnClose) {
			engine.close();
			System.exit(0);
		}
	}

	/**
	 * Calls setFocus() on the currently selected Component in the tabbedPane.
	 */
	public void stateChanged(ChangeEvent event) {
		((AbstractJamochaPanel) tabbedPane.getSelectedComponent()).setFocus();
	}
}
