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

package org.jamocha.application.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jamocha.Constants;
import org.jamocha.application.gui.icons.IconLoader;
import org.jamocha.settings.JamochaSettings;

/**
 * This class provides the Mainmenubar for the whole gui.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class JamochaMenuBar extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = 2908247560107956066L;

	private final JamochaGui gui;

	private final JMenu fileMenu;

	private final JMenuItem fileMenuBatch;

	private final JMenuItem fileMenuCloseGui;

	private final JMenuItem fileMenuQuit;

	private final JMenu helpMenu;

	private final JMenuItem helpMenuAbout;

	private final JMenuItem helpMenuDocu;

	private JDialog aboutDialog;

	public JamochaMenuBar(final JamochaGui gui) {
		super();
		this.gui = gui;

		// adding the file menu
		fileMenu = new JMenu("File");
		fileMenuBatch = new JMenuItem("Batch File ...", IconLoader
				.getImageIcon("lorry"));
		fileMenuBatch.addActionListener(this);

		fileMenuCloseGui = new JMenuItem("Close Gui", IconLoader
				.getImageIcon("disconnect"));
		fileMenuCloseGui.addActionListener(this);

		fileMenuQuit = new JMenuItem("Quit", IconLoader.getImageIcon("door_in"));
		fileMenuQuit.addActionListener(this);

		fileMenu.add(fileMenuBatch);
		fileMenu.addSeparator();
		fileMenu.add(fileMenuCloseGui);
		fileMenu.add(fileMenuQuit);
		add(fileMenu);

		// adding the help menu
		helpMenu = new JMenu("Help");
		helpMenuDocu = new JMenuItem("Online Documentation", IconLoader
				.getImageIcon("help"));
		helpMenuDocu.addActionListener(this);

		helpMenuAbout = new JMenuItem("About", IconLoader
				.getImageIcon("comment"));
		helpMenuAbout.addActionListener(this);

		helpMenu.add(helpMenuDocu);
		helpMenu.add(helpMenuAbout);
		add(helpMenu);
	}

	public void showCloseGui(final boolean show) {
		fileMenuCloseGui.setVisible(show);
	}

	public void showQuit(final boolean show) {
		fileMenuQuit.setVisible(show);
	}

	public void actionPerformed(final ActionEvent event) {
		if (event.getSource() == fileMenuQuit) {
			gui.setExitOnClose(true);
			gui.close();
		} else if (event.getSource() == fileMenuCloseGui) {
			gui.setExitOnClose(false);
			gui.close();
		} else if (event.getSource() == fileMenuBatch) {
			final JamochaSettings prefs = JamochaSettings.getInstance();
			final JFileChooser chooser = new JFileChooser(prefs
					.getString("gui.menubar.batchLastPath"));
			chooser.setMultiSelectionEnabled(false);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				final File file = chooser.getSelectedFile();
				if (file != null && file.isFile()) {
					prefs.set("gui.menubar.batchLastPath", file
							.getAbsolutePath());

					final List<String> files = new ArrayList<String>(1);
					files.add(file.getPath());
					gui.processBatchFiles(files);
					JOptionPane
							.showMessageDialog(
									this,
									"Batch process started.\nPlease check the log for Messages.\nThe process might be running in the background for a while.");
				}
			}
		} else if (event.getSource() == helpMenuDocu) {
			BrowserControl
					.displayURL(
							"http://www.jamocha.org/wiki/pmwiki.php?n=Documentation.Documentation",
							gui);
		} else if (event.getSource() == helpMenuAbout) {
			final String aboutText = "\u00A9 2007 by Jamocha Developer Group.\n\n"
					+ "Version "
					+ Constants.VERSION
					+ " ("
					+ Constants.VERSION_NAME
					+ ")\n\n"
					+ "Jamocha is an open source rule engine released under the Apache Software License.\n\n"
					+ "For more information visit http://www.jamocha.org\n\n"
					+ "Credits for the Icons used in the GUI go to Marc James (http://www.famfamfam.com/lab/icons/silk/). They are released under a Creative Commons Attribution 2.5 License.\n\n"
					+ "Credits for the Jamocha-Logo go to Frank R\u00FCttgers (http://www.xele.de).\n\n"
					+ "Developers (in alphabetical order):\n"
					+ "- Alexander Wilden\n"
					+ "- Ananda Sumadha Markus Widyadharma\n"
					+ "- Andreas Eberhart\n"
					+ "- Benjamin Zimmermann\n"
					+ "- Christian Ebert\n"
					+ "- Christoph Emonds\n"
					+ "- Emmanuel Bonnet\n"
					+ "- Josef-Alexander Hahn\n"
					+ "- Karl-Heinz Krempels\n"
					+ "- Martin Krebs\n"
					+ "- Michael Neale\n"
					+ "- Nikolaus Koemm\n"
					+ "- Ory Chowaw-Liebman\n"
					+ "- Peter Lin\n"
					+ "- Sebastian Reinartz\n"
					+ "- Sven Lilienthal\n"
					+ "- Tim Niemueller\n"
					+ "- Ulrich Loup\n"
					+ "- Volker Wetzelaer";
			aboutDialog = new JDialog(gui, "About Jamocha", true);
			aboutDialog.setLocationByPlatform(true);
			aboutDialog.setSize(500, 400);
			aboutDialog.setLayout(new BorderLayout());
			final JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			logoPanel.add(new JLabel(IconLoader.getImageIcon("jamocha")));
			logoPanel.add(new JLabel("one engine for all your rules"));
			aboutDialog.add(logoPanel, BorderLayout.NORTH);
			final JTextArea aboutArea = new JTextArea(aboutText);
			aboutArea.setBorder(BorderFactory.createEmptyBorder());
			aboutArea.setLineWrap(true);
			aboutArea.setWrapStyleWord(true);
			aboutArea.setEditable(false);
			aboutDialog.add(new JScrollPane(aboutArea,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
					BorderLayout.CENTER);
			final JPanel closePanel = new JPanel(new FlowLayout(
					FlowLayout.RIGHT));
			final JButton closeButton = new JButton("close");
			closeButton.addActionListener(new ActionListener() {

				public void actionPerformed(final ActionEvent arg0) {
					aboutDialog.dispose();
				}

			});
			closePanel.add(closeButton);
			aboutDialog.add(closePanel, BorderLayout.SOUTH);
			aboutDialog.setVisible(true);
		}
	}
}
