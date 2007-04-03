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
package org.jamocha.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jamocha.BatchThread;
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.gui.tab.AbstractJamochaPanel;
import org.jamocha.gui.tab.FactsPanel;
import org.jamocha.gui.tab.FunctionsPanel;
import org.jamocha.gui.tab.LogPanel;
import org.jamocha.gui.tab.RetePanel;
import org.jamocha.gui.tab.RulesPanel;
import org.jamocha.gui.tab.SettingsPanel;
import org.jamocha.gui.tab.ShellPanel;
import org.jamocha.gui.tab.TemplatesPanel;
import org.jamocha.messagerouter.InterestType;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.rete.Rete;

/**
 * 
 * JamochaGui implements a GUI for the Jamocha Rule Engine
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class JamochaGui extends JFrame implements ChangeListener,
		ActionListener {

	static final long serialVersionUID = 1L;

	static final Preferences preferences = Preferences.userRoot().node(
			"org.jamocha.gui");

	private Rete engine;

	private JamochaMenuBar menuBar;

	private JButton batchResultsButton;

	private JLabel logoLabel;

	private JTabbedPane tabbedPane;

	private List<AbstractJamochaPanel> panels = new LinkedList<AbstractJamochaPanel>();

	private boolean exitOnClose = false;

	private StringChannel stringChannel;

	private BatchThread batchThread;

	/**
	 * Create a GUI-Instance for Jamocha.
	 * 
	 * @param engine
	 *            The Jamocha-engine that will be used in the GUI.
	 */
	public JamochaGui(Rete engine, BatchThread batchThread) {
		// set up the frame
		setLayout(new BorderLayout());
		setTitle("Jamocha");
		setLookAndFeel(SYSTEM_LOOK_AND_FEEL);
		setSizeAndLocation();

		// show logo
		JPanel logoPanel = new JPanel(new BorderLayout());
		logoLabel = new JLabel(IconLoader.getImageIcon("jamocha"));
		logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		logoLabel.setToolTipText("visit www.jamocha.org");
		logoLabel.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				BrowserControl.displayURL("http://www.jamocha.org");
			}
		});
		logoPanel.add(logoLabel, BorderLayout.EAST);

		// adding the button that indicates batch results
		JPanel batchResultsPanel = new JPanel();
		batchResultsButton = new JButton(IconLoader.getImageIcon("lorry_error"));
		batchResultsButton.setToolTipText("Click here to view batch results.");
		batchResultsButton.setVisible(false);
		batchResultsButton.addActionListener(this);
		batchResultsPanel.add(batchResultsButton);
		logoPanel.add(batchResultsPanel, BorderLayout.WEST);
		this.add(logoPanel, BorderLayout.NORTH);

		// create a tabbed pane
		tabbedPane = new JTabbedPane();
		this.add(tabbedPane, BorderLayout.CENTER);

		// add MenuBar
		menuBar = new JamochaMenuBar(this);
		this.setJMenuBar(menuBar);

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
		TemplatesPanel templatesPanel = new TemplatesPanel(this);
		tabbedPane.addTab("Templates", IconLoader.getImageIcon("brick"),
				templatesPanel, "View or modify Templates");
		panels.add(templatesPanel);
		RulesPanel rulesPanel = new RulesPanel(this);
		tabbedPane.addTab("Rules", IconLoader.getImageIcon("car"), rulesPanel,
				"View or modify Rules");
		panels.add(rulesPanel);
		FunctionsPanel functionsPanel = new FunctionsPanel(this);
		tabbedPane.addTab("Functions", IconLoader.getImageIcon("cog"),
				functionsPanel, "View Functions");
		panels.add(functionsPanel);
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
		this.batchThread = batchThread;
	}

	public void informOfNewBatchResults() {
		batchResultsButton.setVisible(true);
		batchResultsButton.setIcon(IconLoader.getImageIcon("lorry_error"));
	}
	
	public void processBatchFiles(List<String> files) {
		batchThread.processBatchFiles(files);
	}

	private void setSizeAndLocation() {
		int width = preferences.getInt("gui.width", 0);
		int height = preferences.getInt("gui.height", 0);
		int locx = preferences.getInt("gui.locx", -1);
		int locy = preferences.getInt("gui.locy", -1);
		if (locx == -1 || locy == -1) {
			this.setLocationByPlatform(true);
		} else {
			this.setLocation(locx, locy);
		}
		if (width <= 0 || height <= 0) {
			this.setSize(750, 550);
		} else {
			this.setSize(width, height);
		}
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

	public void showCloseGuiMenuItem(boolean show) {
		menuBar.showCloseGui(show);
	}

	public void showQuitMenuItem(boolean show) {
		menuBar.showQuit(show);
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
	 * Returns a StringChannel used for all Editors. Output will only go to the
	 * Log.
	 * 
	 * @return A StringChannel for the editors.
	 */
	public StringChannel getStringChannel() {
		if (stringChannel == null)
			stringChannel = getEngine().getMessageRouter().openChannel(
					"gui_string_channel", InterestType.NONE);
		return stringChannel;
	}

	/**
	 * Returns a StringChannel for File-Menu -> Batch entry. Output will be
	 * collected and a symbol at the top will indicate incoming results.
	 * 
	 * @return A StringChannel for the batch process.
	 */
	public StringChannel getBatchChannel() {
		return batchThread.getBatchChannel();
	}

	/**
	 * Sets the GUI visible and calls setFocus() on the shellPanel.
	 * 
	 */
	public void showGui() {
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
	 * Converts an Exception to a String namely turns the StackTrace to a
	 * String.
	 * 
	 * @param exception
	 *            The Exception
	 * @return A nice String representation of the Exception
	 */
	public static String exceptionToString(Exception exception) {
		StringBuilder res = new StringBuilder();
		StackTraceElement[] str = exception.getStackTrace();
		for (int i = 0; i < str.length; ++i) {
			res.append(str[i] + System.getProperty("line.separator"));
		}
		return res.toString();
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
		if (stringChannel != null)
			getEngine().getMessageRouter().closeChannel(stringChannel);
		// save position and size
		preferences.putInt("gui.width", getWidth());
		preferences.putInt("gui.height", getHeight());
		preferences.putInt("gui.locx", getX());
		preferences.putInt("gui.locy", getY());

		// inform other panels
		for (AbstractJamochaPanel panel : panels) {
			panel.close();
		}
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		setVisible(false);
		dispose();
		if (exitOnClose) {
			batchThread.stopThread();
			engine.close();
			System.exit(0);
		}
	}

	public final static String CROSS_PLATFORM_LOOK_AND_FEEL = "crossplatform";

	public final static String SYSTEM_LOOK_AND_FEEL = "system";

	public final static String METAL_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";

	public final static String WINDOWS_LOOK_AND_FEEL = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	public final static String GTK_LOOK_AND_FEEL = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

	public final static String MOTIF_LOOK_AND_FEEL = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

	public void setLookAndFeel(String lookAndFeelClassName) {
		try {
			if (lookAndFeelClassName.equals(CROSS_PLATFORM_LOOK_AND_FEEL))
				lookAndFeelClassName = UIManager
						.getCrossPlatformLookAndFeelClassName();
			else if (lookAndFeelClassName.equals(SYSTEM_LOOK_AND_FEEL))
				lookAndFeelClassName = UIManager
						.getSystemLookAndFeelClassName();

			UIManager.setLookAndFeel(lookAndFeelClassName);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception exc) {
			// look & feel not found, use the cross-platform look and feel
			try {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			} catch (Exception exc2) {
				// even the platform look and feel is not available, something
				// must be wrong with the
				// installation of the java-runtime-environment
			}
		}
	}

	/**
	 * Calls setFocus() on the currently selected Component in the tabbedPane.
	 */
	public void stateChanged(ChangeEvent event) {
		((AbstractJamochaPanel) tabbedPane.getSelectedComponent()).setFocus();
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == batchResultsButton) {
			BatchResultBrowser browser = new BatchResultBrowser(
					batchResultsButton);
			browser.setResults(batchThread.getBatchResults());
			browser.setVisible(true);
		}
	}

	
}
