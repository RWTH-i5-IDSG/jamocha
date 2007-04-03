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
package org.jamocha;

import java.util.LinkedList;
import java.util.List;

import org.jamocha.gui.JamochaGui;
import org.jamocha.parser.ParserFactory;
import org.jamocha.parser.ParserNotFoundException;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Shell;

/**
 * This is the main entry point when using Jamocha as standalone application.
 * Depending on the arguments this Class starts the GUI and / or the Shell.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
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
		String parser = "";
		if (null != args) {
			boolean inBatchFiles = false, inParser = false;
			for (int i = 0; i < args.length; ++i) {
				if (args[i].startsWith("-")) {
					inBatchFiles = inParser = false;
				}
				if (inBatchFiles) {
					batchFiles.add(args[i]);
				} else if (inParser) {
					parser = args[i];
				} else {
					if (args[i].equalsIgnoreCase("-gui")) {
						startGui = true;
					} else if (args[i].equalsIgnoreCase("-shell")) {
						startShell = true;
					} else if (args[i].equals("-batch")) {
						inBatchFiles = true;
					} else if (args[i].equals("-parser")) {
						inParser = true;
					}
				}
			}
		}
		Jamocha jamocha = null;
		try {
			jamocha = new Jamocha(new Rete(), startGui, startShell, parser,
					batchFiles);
		} catch (ParserNotFoundException e) {
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
	 * @param parserName
	 *            Name of the Parser to use. If none is given the default one
	 *            will be used. If the Parser is unknown a
	 *            <code>ParserNotFoundException</code> will be thrown.
	 * @throws ParserNotFoundException
	 *             if the specified Parser in <code>parserName</code> was not
	 *             found.
	 */
	public Jamocha(Rete engine, boolean startGui, boolean startShell,
			String parserName) throws ParserNotFoundException {
		this(engine, startGui, startShell, parserName, null);
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
	 * @param parserName
	 *            Name of the Parser to use. If none is given the default one
	 *            will be used. If the Parser is unknown a
	 *            <code>ParserNotFoundException</code> will be thrown.
	 * @param batchFiles
	 *            List of files that should be batch-processed at startup.
	 * @throws ParserNotFoundException
	 *             if the specified Parser in <code>parserName</code> was not
	 *             found.
	 */
	public Jamocha(Rete engine, boolean startGui, boolean startShell,
			String parserName, List<String> batchFiles)
			throws ParserNotFoundException {
		this.engine = engine;
		batchThread = new BatchThread(engine);
		batchThread.start();
		if (parserName != null && parserName.length() > 0) {
			ParserFactory.setDefaultParser(parserName);
		}
		if (startShell) {
			try {
				startShell();
			} catch (ParserNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
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

	/**
	 * Starts a single command line shell using <code>System.in</code> and
	 * <code>System.out</code>.
	 * 
	 * @throws ParserNotFoundException
	 *             if the specified Parser was not found
	 */
	public void startShell() throws ParserNotFoundException {
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
	 * <td>-parser [parsername]:</td>
	 * <td>Uses the given parser to parse the input. Default (at the moment) is
	 * clips.</td>
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
						+ "-parser [parsername]:"
						+ sep
						+ "     Uses the given parser to parse the input. Default is clips.");
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
