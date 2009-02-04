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

import java.util.LinkedList;
import java.util.List;

import javax.swing.UIManager;

import org.jamocha.application.gui.JamochaMainFrame;
import org.jamocha.communication.BatchThread;
import org.jamocha.engine.Engine;

/**
 * @author Alexander Wilden
 * 
 * This is the main entry point when using Jamocha as standalone application.
 * Depending on the arguments this Class starts the GUI and / or the Shell.
 */
public class JamochaGui {

	/**
	 * The GUI (if started).
	 */
	private JamochaMainFrame jamochaGui;

	/**
	 * The Rete-engine used for this Jamocha instance.
	 */
	private Engine engine;

	/**
	 * A Thread working on Batch-processes in the background.
	 */
	private BatchThread batchThread;

	/**
	 * @param args
	 *            For possible Arguments have a look at {@link #showUsage()}
	 * @see #showUsage()
	 */
	public static void main(String[] args) {
		List<String> batchFiles = new LinkedList<String>();
		if (null != args) {
			boolean inBatchFiles = false;
			for (int i = 0; i < args.length; ++i) {
				if (args[i].startsWith("-")) {
					inBatchFiles = false;
				}
				if (inBatchFiles) {
					batchFiles.add(args[i]);
				} else {
					if (args[i].equals("-batch")) {
						inBatchFiles = true;
					}
				}
			}
		}
		JamochaGui jamocha  = new JamochaGui(batchFiles);
		// if no arguments were given or by another cause neither gui nor shell
		// were started, we show a usage guide.

		// we only show the quit-button
		jamocha.getJamochaGui().showCloseGuiMenuItem(false);
		// a click on the x on windows or red dot on mac quits everything
		jamocha.getJamochaGui().setExitOnClose(true);
		
	}

	/**
	 * Constructor for a new Jamocha-Object. A Jamocha-Object encapsulates the
	 * Rete engine and a possible Shell / GUI.
	 * <p>
	 * Additionally accepts a List of files that should be batch-processed at
	 * startup. This only works with the GUI!

	 * @param startGui
	 *            If <code>true</code> a GUI will be started.
	 * @param startShell
	 *            If <code>true</code> a simple commandline Shell working on
	 *            System.in and System.out will be started.
	 * @param mode
	 *            Name of the Mode to use. If none is given the default one will
	 *            be used. If the Mode is unknown a
	 *            <code>ModeNotFoundException</code> will be thrown.
	 * @param batchFiles
	 *            List of files that should be batch-processed at startup.
	 * @throws ModeNotFoundException
	 *             if the specified Mode in <code>mode</code> was not found.
	 */
	public JamochaGui(List<String> batchFiles) {
		this.engine = new Engine();
		batchThread = new BatchThread(engine);
		batchThread.start();
		startGui();
		batchThread.addBatchExecutionListener(jamochaGui);
		if (batchFiles != null) {
			if (!batchFiles.isEmpty()) {
				batchThread.processBatchFiles(batchFiles);
			}
		}
	}

	public void setGUITitle(String title) {
		if (jamochaGui != null) {
			jamochaGui.setTitle(title);
		}
	}

	public void batchFiles(List<String> batchFiles) {
		if (batchFiles != null) {
			batchThread.processBatchFiles(batchFiles);
		}
	}

	/**
	 * Returns the underlying Rete engine.
	 * 
	 * @return The Rete engin used by this Jamocha instance.
	 */
	public Engine getEngine() {
		return engine;
	}

	/**
	 * Starts a single GUI.
	 * 
	 */
	public void startGui() {
		if (jamochaGui == null) {
			try{ 
				UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() ); 
			}catch( Exception e ){
				e.printStackTrace();
			}
			jamochaGui = new JamochaMainFrame(engine, batchThread);
			Thread guiThread = new Thread("GUI Thread") {

				public void run() {
					jamochaGui.showGui();
				}

			};
			guiThread.start();
		}
	}

	/**
	 * Returns the one and only GUI (if started).
	 * 
	 * @return The Jamocha GUI.
	 */
	public JamochaMainFrame getJamochaGui() {
		return jamochaGui;
	}

}
