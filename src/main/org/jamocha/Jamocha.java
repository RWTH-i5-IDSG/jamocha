/*
 * Copyright 2007 Alexander Wilden
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
package org.jamocha;

import java.util.LinkedList;
import java.util.List;

import org.jamocha.gui.JamochaGui;
import org.jamocha.parser.ModeNotFoundException;
import org.jamocha.parser.ParserFactory;
import org.jamocha.rete.Rete;

/**
 * @author Alexander Wilden
 * 
 * This is the main entry point when using Jamocha as standalone application.
 * Depending on the arguments this Class starts the GUI and / or the Shell.
 */
public class Jamocha {

	/**
	 * The GUI (if started).
	 */
	private JamochaGui jamochaGui;

	/**
	 * The Shell (if started).
	 */
	private Shell shell;

	/**
	 * The Rete-engine used for this Jamocha instance.
	 */
	private Rete engine;

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
		boolean startGui = false;
		boolean startShell = false;
		List<String> batchFiles = new LinkedList<String>();
		String mode = "";
		if (null != args) {
			boolean inBatchFiles = false, inMode = false;
			for (int i = 0; i < args.length; ++i) {
				if (args[i].startsWith("-")) {
					inBatchFiles = inMode = false;
				}
				if (inBatchFiles) {
					batchFiles.add(args[i]);
				} else if (inMode) {
					mode = args[i];
				} else {
					if (args[i].equalsIgnoreCase("-gui")) {
						startGui = true;
					} else if (args[i].equalsIgnoreCase("-shell")) {
						startShell = true;
					} else if (args[i].equals("-batch")) {
						inBatchFiles = true;
					} else if (args[i].equals("-mode")) {
						inMode = true;
					}
				}
			}
		}
		Jamocha jamocha = null;
		try {
			jamocha = new Jamocha(new Rete(), startGui, startShell, mode,
					batchFiles);
		} catch (ModeNotFoundException e) {
			// This really is a fatal error so we stop everything.
			e.printStackTrace();
			System.exit(1);
		}
		// if no arguments were given or by another cause neither gui nor shell
		// were started, we show a usage guide.
		if (!startShell && !startGui) {
			jamocha.showUsage();
			return;
		} else if (!startShell) {
			// we only show the quit-button
			jamocha.getJamochaGui().showCloseGuiMenuItem(false);
			// a click on the x on windows or red dot on mac quits everything
			jamocha.getJamochaGui().setExitOnClose(true);
		}
	}

	/**
	 * Constructor for a new Jamocha-Object. A Jamocha-Object encapsulates the
	 * Rete engine and a possible Shell / GUI.
	 * 
	 * @param engine
	 *            The engine that should be used for the Shell / GUI.
	 * @param startGui
	 *            If <code>true</code> a GUI will be started.
	 * @param startShell
	 *            If <code>true</code> a simple commandline Shell working on
	 *            System.in and System.out will be started.
	 * @param mode
	 *            Name of the Mode to use. If none is given the default one will
	 *            be used. If the Mode is unknown a
	 *            <code>ModeNotFoundException</code> will be thrown.
	 * @throws ModeNotFoundException
	 *             if the specified Mode in <code>mode</code> was not found.
	 */
	public Jamocha(Rete engine, boolean startGui, boolean startShell,
			String mode) throws ModeNotFoundException {
		this(engine, startGui, startShell, mode, null);
	}

	/**
	 * Constructor for a new Jamocha-Object. A Jamocha-Object encapsulates the
	 * Rete engine and a possible Shell / GUI.
	 * <p>
	 * Additionally accepts a List of files that should be batch-processed at
	 * startup. This only works with the GUI!
	 * 
	 * @param engine
	 *            The engine that should be used for the Shell / GUI.
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
	public Jamocha(Rete engine, boolean startGui, boolean startShell,
			String mode, List<String> batchFiles) throws ModeNotFoundException {
		this.engine = engine;
		batchThread = new BatchThread(engine);
		batchThread.start();
		if (mode != null && mode.length() > 0) {
			ParserFactory.setDefaultMode(mode);
		}
		if (startShell) {
			startShell();
		}
		if (startGui) {
			startGui();
			batchThread.setGui(getJamochaGui());
		}
		if (batchFiles != null) {
			if (!batchFiles.isEmpty()) {
				batchThread.processBatchFiles(batchFiles);
			}
		}
	}
	
	public void setGUITitle(String title) {
		if(jamochaGui != null) {
			jamochaGui.setTitle(title);
		}
	}

	/**
	 * Returns the underlying Rete engine.
	 * 
	 * @return The Rete engin used by this Jamocha instance.
	 */
	public Rete getEngine() {
		return engine;
	}

	/**
	 * Starts a single command line shell using <code>System.in</code> and
	 * <code>System.out</code>.
	 * 
	 * @throws ParserNotFoundException
	 *             if the specified Parser was not found
	 */
	public void startShell() throws ModeNotFoundException {
		if (shell == null) {
			shell = new Shell(engine);

			Thread shellThread = new Thread() {

				public void run() {
					shell.run();
				}

			};
			shellThread.start();
		}
	}

	/**
	 * Starts a single GUI.
	 * 
	 */
	public void startGui() {
		if (jamochaGui == null) {
			jamochaGui = new JamochaGui(engine, batchThread);
			Thread guiThread = new Thread() {

				public void run() {
					jamochaGui.showGui();
				}

			};
			guiThread.start();
		}
	}

	/**
	 * Prints out usage information on <code>System.out</code>, if invalid or
	 * no arguments where given.
	 * <p>
	 * Give at least one of these:<table>
	 * <tr>
	 * <td>-gui:</td>
	 * <td>Starts a graphical user interface.</td>
	 * </tr>
	 * <tr>
	 * <td>-shell:</td>
	 * <td>Starts a simple Shell.</td>
	 * </tr>
	 * </table>
	 * <p>
	 * Optional arguments:<table>
	 * <tr>
	 * <td>-batch [batchfile...]:</td>
	 * <td>Processes a list of given files (separated by blanks) as
	 * batch-files.</td>
	 * </tr>
	 * <tr>
	 * <td>-mode [modename]:</td>
	 * <td>Uses the given mode for the Parser, Formatter and RuleCompiler.
	 * Default is sfp.</td>
	 * </tr>
	 * </table>
	 * 
	 */
	public void showUsage() {
		String sep = System.getProperty("line.separator");
		System.out
				.println("You have to pass one or more of the following arguments:"
						+ sep
						+ sep
						+ "-gui:"
						+ sep
						+ "     Starts a graphical user interface."
						+ sep
						+ "-shell:"
						+ sep
						+ "     Starts a simple Shell."
						+ sep
						+ sep
						+ "Optional arguments:"
						+ sep
						+ sep
						+ "-batch [batchfile...]:"
						+ sep
						+ "     Processes a list of given files (separated by blanks) as batch-files."
						+ sep
						+ "-mode [modename]:"
						+ sep
						+ "     Uses the given mode for the Parser, Formatter and RuleCompiler. Default is sfp.");
		System.exit(0);
	}

	/**
	 * Returns the one and only GUI (if started).
	 * 
	 * @return The Jamocha GUI.
	 */
	public JamochaGui getJamochaGui() {
		return jamochaGui;
	}

	/**
	 * Returns the one and only Shell (if started).
	 * 
	 * @return The Jamocha Shell.
	 */
	public Shell getShell() {
		return shell;
	}
}
