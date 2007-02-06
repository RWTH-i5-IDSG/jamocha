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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.jamocha.gui.icons.IconLoader;

/**
 * This class provides the Mainmenubar for the whole gui.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class JamochaMenuBar extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = 2908247560107956066L;

	private JamochaGui gui;

	private JMenu fileMenu;

	private JMenuItem fileMenuBatch;

	private JMenuItem fileMenuCloseGui;

	private JMenuItem fileMenuQuit;

	public JamochaMenuBar(JamochaGui gui) {
		super();
		this.gui = gui;

		// adding the file menu
		fileMenu = new JMenu("File");
		fileMenuBatch = new JMenuItem("Batch File ...", IconLoader
				.getImageIcon("cog"));
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
	}

	public void showCloseGui(boolean show) {
		fileMenuCloseGui.setVisible(show);
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == fileMenuQuit) {
			gui.setExitOnClose(true);
			gui.close();
		} else if (event.getSource() == fileMenuCloseGui) {
			gui.close();
		} else if (event.getSource() == fileMenuBatch) {
			JFileChooser chooser = new JFileChooser(gui.getPreferences().get("menubar.batchLastPath", ""));
			chooser.setMultiSelectionEnabled(false);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (file != null && file.isFile()) {
					gui.getPreferences().put("menubar.batchLastPath", file.getAbsolutePath());
					String path = file.getAbsolutePath();
					gui.getStringChannel().executeCommand(
							"(batch " + path + ")");
					JOptionPane.showMessageDialog(this, "Batch process started.\nPlease check the log for Messages.\nThe process might be running in the background for a while.");
				}
			}
		}
	}
}
