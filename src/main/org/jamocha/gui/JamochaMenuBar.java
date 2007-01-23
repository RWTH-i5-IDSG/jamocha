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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jamocha.gui.icons.IconLoader;
import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.rete.Function;

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
		fileMenuBatch = new JMenuItem("Batch File ...",IconLoader.getImageIcon("cog"));
		fileMenuBatch.addActionListener(this);
		fileMenuCloseGui = new JMenuItem("Close Gui",IconLoader.getImageIcon("application_delete"));
		fileMenuCloseGui.addActionListener(this);
		fileMenuQuit = new JMenuItem("Quit",IconLoader.getImageIcon("door_in"));
		fileMenuQuit.addActionListener(this);
		fileMenu.add(fileMenuBatch);
		fileMenu.addSeparator();
		fileMenu.add(fileMenuCloseGui);
		fileMenu.add(fileMenuQuit);
		add(fileMenu);
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == fileMenuQuit ) {
			gui.setExitOnClose(true);
			gui.close();
		} else if(event.getSource() == fileMenuCloseGui) {
			gui.close();
		} else if (event.getSource() == fileMenuBatch ) {
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(false);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				try {
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					StringChannel batchChannel = gui.getEngine().getMessageRouter().openChannel(
							"gui_batchchannel");
					StringBuilder buffer = new StringBuilder();
					while (reader.ready()) {
						buffer.append(reader.readLine());
					}
					batchChannel.executeCommand(buffer.toString(), true);
					List<MessageEvent> events = new LinkedList<MessageEvent>();
					batchChannel.fillEventList(events);
					buffer = new StringBuilder();
					for (MessageEvent mevent : events) {
						if (mevent.getMessage() instanceof Function) {
							buffer.append(((Function) mevent.getMessage())
									.getName() + System.getProperty("line.separator"));
						} else
							buffer.append(mevent.getMessage() + System.getProperty("line.separator"));
					}
					JDialog dialog = new JDialog(gui,"Result:");
					dialog.setSize(400, 300);
					dialog.setLocationByPlatform(true);
					JTextArea area = new JTextArea(buffer.toString());
					area.setEditable(false);
					dialog.add(new JScrollPane(area));
					dialog.setVisible(true);
					gui.getEngine().getMessageRouter().closeChannel(batchChannel);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
