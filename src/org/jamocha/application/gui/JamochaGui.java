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

package org.jamocha.application.gui;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jamocha.application.gui.icons.IconLoader;
import org.jamocha.application.gui.tab.AbstractJamochaPanel;
import org.jamocha.application.gui.tab.AgendaPanel;
import org.jamocha.application.gui.tab.FactsPanel;
import org.jamocha.application.gui.tab.FunctionsPanel;
import org.jamocha.application.gui.tab.LogPanel;
import org.jamocha.application.gui.tab.RulesPanel;
import org.jamocha.application.gui.tab.SettingsPanel;
import org.jamocha.application.gui.tab.ShellPanel;
import org.jamocha.application.gui.tab.TemplatesPanel;
import org.jamocha.application.gui.tab.ViewerPanel;
import org.jamocha.communication.BatchThread;
import org.jamocha.communication.messagerouter.InterestType;
import org.jamocha.communication.messagerouter.StringChannel;
import org.jamocha.engine.Engine;
import org.jamocha.settings.JamochaSettings;

/**
 * 
 * JamochaGui implements a GUI for the Jamocha Rule Engine
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class JamochaGui extends JFrame implements ChangeListener,
		ActionListener {

	private static final String GUI_LOCY = "gui.locy";

	private static final String GUI_LOCX = "gui.locx";

	private static final String GUI_HEIGHT = "gui.height";

	private static final String GUI_WIDTH = "gui.width";

	static final long serialVersionUID = 1L;

	private final Engine engine;

	private final JamochaMenuBar menuBar;

	private final JButton batchResultsButton;

	private final JLabel logoLabel;

	private final JTabbedPane tabbedPane;

	private final List<AbstractJamochaPanel> panels = new LinkedList<AbstractJamochaPanel>();

	private boolean exitOnClose = false;

	private StringChannel stringChannel;

	private final BatchThread batchThread;

	/**
	 * Create a GUI-Instance for Jamocha.
	 * 
	 * @param engine
	 *            The Jamocha-engine that will be used in the GUI.
	 */
	public JamochaGui(final Engine engine, final BatchThread batchThread) {
		// set up the frame
		setLayout(new BorderLayout());
		setTitle("Jamocha");
		// setLookAndFeel(SYSTEM_LOOK_AND_FEEL);
		setSizeAndLocation();

		// show logo
		final JPanel logoPanel = new JPanel(new BorderLayout());
		logoLabel = new JLabel(IconLoader.getImageIcon("jamocha"));
		logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		logoLabel.setToolTipText("visit www.jamocha.org");
		logoLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				BrowserControl.displayURL("http://www.jamocha.org",
						JamochaGui.this);
			}
		});
		logoPanel.add(logoLabel, BorderLayout.EAST);

		// adding the button that indicates batch results
		final JPanel batchResultsPanel = new JPanel();
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
		tabbedPane.setFocusTraversalKeysEnabled(true);

		// add MenuBar
		menuBar = new JamochaMenuBar(this);
		setJMenuBar(menuBar);

		// create a rete engine
		this.engine = engine;

		// create a shell tab and add it to the tabbed pane
		final AbstractJamochaPanel shellPanel = new ShellPanel(this);
		tabbedPane.addTab("Shell", IconLoader
				.getImageIcon("application_osx_terminal"), shellPanel,
				"Jamocha Shell");
		panels.add(shellPanel);

		final FactsPanel factsPanel = new FactsPanel(this);
		tabbedPane.addTab("Facts", IconLoader.getImageIcon("database"),
				factsPanel, "View or modify Facts");
		panels.add(factsPanel);

		final TemplatesPanel templatesPanel = new TemplatesPanel(this);
		tabbedPane.addTab("Templates", IconLoader.getImageIcon("brick"),
				templatesPanel, "View or modify Templates");
		panels.add(templatesPanel);

		final RulesPanel rulesPanel = new RulesPanel(this);
		tabbedPane.addTab("Rules", IconLoader.getImageIcon("car"), rulesPanel,
				"View or modify Rules");
		panels.add(rulesPanel);

		final FunctionsPanel functionsPanel = new FunctionsPanel(this);
		tabbedPane.addTab("Functions", IconLoader.getImageIcon("cog"),
				functionsPanel, "View Functions");
		panels.add(functionsPanel);

		final AgendaPanel agendaPanel = new AgendaPanel(this);
		tabbedPane.addTab("Agenda", IconLoader.getImageIcon("sport_8ball"),
				agendaPanel, "View all Activations");
		panels.add(agendaPanel);

		final ViewerPanel viewerPanel = new ViewerPanel(this);
		tabbedPane.addTab("Rete viewer", IconLoader.getImageIcon("eye"),
				viewerPanel, "View the Rete-network");
		panels.add(viewerPanel);

		final LogPanel logPanel = new LogPanel(this);
		tabbedPane.addTab("Log", IconLoader.getImageIcon("monitor"), logPanel,
				"View alle messages from or to the Rete-engine");
		panels.add(logPanel);

		final SettingsPanel settingsPanel = new SettingsPanel(this);
		tabbedPane.addTab("Settings", IconLoader.getImageIcon("wrench"),
				settingsPanel, "Settings for Jamocha");
		panels.add(settingsPanel);

		// add the tab pane to the frame
		add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addChangeListener(this);
		// add a listener to the frame to kill the engine when the GUI is closed
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				close();
			}
		});
		this.batchThread = batchThread;
	}

	public void informOfNewBatchResults() {
		batchResultsButton.setVisible(true);
		batchResultsButton.setIcon(IconLoader.getImageIcon("lorry_error"));
	}

	public void processBatchFiles(final List<String> files) {
		batchThread.processBatchFiles(files);
	}

	private void setSizeAndLocation() {
		final JamochaSettings prefs = JamochaSettings.getInstance();
		final int width = prefs.getInt(GUI_WIDTH);
		final int height = prefs.getInt(GUI_HEIGHT);
		final int locx = prefs.getInt(GUI_LOCX);
		final int locy = prefs.getInt(GUI_LOCY);
		if (locx == -1 || locy == -1) {
			setLocationByPlatform(true);
		} else {
			this.setLocation(locx, locy);
		}
		setMinimumSize(new Dimension(500, 300));
		setPreferredSize(new Dimension(750, 550));
		if (width <= 100 || height <= 50) {
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
	public void setExitOnClose(final boolean exitOnClose) {
		this.exitOnClose = exitOnClose;
	}

	public void showCloseGuiMenuItem(final boolean show) {
		menuBar.showCloseGui(show);
	}

	public void showQuitMenuItem(final boolean show) {
		menuBar.showQuit(show);
	}

	/**
	 * Get the current Jamocha-engine.
	 * 
	 * @return The Jamocha-engine.
	 */
	public Engine getEngine() {
		return engine;
	}

	/**
	 * Returns a StringChannel used for all Editors. Output will only go to the
	 * Log.
	 * 
	 * @return A StringChannel for the editors.
	 */
	public StringChannel getStringChannel() {
		if (stringChannel == null) {
			stringChannel = getEngine().getMessageRouter().openChannel(
					"gui_string_channel", InterestType.NONE);
		}
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
	 * Converts an Exception to a String namely turns the StackTrace to a
	 * String.
	 * 
	 * @param exception
	 *            The Exception
	 * @return A nice String representation of the Exception
	 */
	public static String exceptionToString(final Exception exception) {
		final StringBuilder res = new StringBuilder();
		final StackTraceElement[] str = exception.getStackTrace();
		for (int i = 0; i < str.length; ++i) {
			res.append(str[i] + System.getProperty("line.separator"));
		}
		return res.toString();
	}

	/**
	 * Closes the GUI and informs all Panels.
	 * 
	 */
	public void close() {
		if (stringChannel != null) {
			getEngine().getMessageRouter().closeChannel(stringChannel);
		}

		final JamochaSettings prefs = JamochaSettings.getInstance();
		// save position and size

		prefs.set(GUI_WIDTH, getWidth());
		prefs.set(GUI_HEIGHT, getHeight());
		prefs.set(GUI_LOCX, getX());
		prefs.set(GUI_LOCY, getY());

		// inform other panels
		for (final AbstractJamochaPanel panel : panels) {
			panel.close();
		}
		setVisible(false);
		dispose();
		batchThread.setGui(null);
		if (exitOnClose) {
			batchThread.stopThread();
			engine.clearAll();
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
			if (lookAndFeelClassName.equals(CROSS_PLATFORM_LOOK_AND_FEEL)) {
				lookAndFeelClassName = UIManager
						.getCrossPlatformLookAndFeelClassName();
			} else if (lookAndFeelClassName.equals(SYSTEM_LOOK_AND_FEEL)) {
				lookAndFeelClassName = UIManager
						.getSystemLookAndFeelClassName();
			}

			UIManager.setLookAndFeel(lookAndFeelClassName);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (final Exception exc) {
			// look & feel not found, use the cross-platform look and feel
			try {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			} catch (final Exception exc2) {
				// even the platform look and feel is not available, something
				// must be wrong with the
				// installation of the java-runtime-environment
			}
		}
	}

	@Override
	public Set<AWTKeyStroke> getFocusTraversalKeys(final int id) {
		if (id == KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS
				|| id == KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS) {
			return Collections.emptySet();
		} else {
			return super.getFocusTraversalKeys(id);
		}
	}

	/**
	 * Calls setFocus() on the currently selected Component in the tabbedPane.
	 */
	public void stateChanged(final ChangeEvent event) {
		((AbstractJamochaPanel) tabbedPane.getSelectedComponent()).setFocus();
	}

	public void actionPerformed(final ActionEvent event) {
		if (event.getSource() == batchResultsButton) {
			final BatchResultBrowser browser = new BatchResultBrowser(
					batchResultsButton);
			browser.setResults(batchThread.getBatchResults());
			browser.setVisible(true);
		}
	}

}
