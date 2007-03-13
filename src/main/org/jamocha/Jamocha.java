/**
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

	private JamochaGui jamochaGui;

	private Shell shell;

	private Rete engine;

	/**
	 * @param args
	 *            For possible Arguments have a look at showUsage().
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

	Jamocha(Rete engine, boolean startGui, boolean startShell, String parserName)
			throws ParserNotFoundException {
		this(engine, startGui, startShell, parserName, null);
	}

	Jamocha(Rete engine, boolean startGui, boolean startShell,
			String parserName, List<String> batchFiles)
			throws ParserNotFoundException {
		this.engine = engine;
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
			if (batchFiles != null) {
				if (!batchFiles.isEmpty()) {
					getJamochaGui().processBatchFiles(batchFiles);
				}
			}
		}
	}

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

	public void startGui() {
		if (jamochaGui == null) {
			jamochaGui = new JamochaGui(engine);
			Thread guiThread = new Thread() {

				public void run() {
					jamochaGui.showGui();
				}

			};
			guiThread.start();
		}
	}

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
						+ "     Attention: This only works when a GUI is started."
						+ sep
						+ "-parser [parsername]:"
						+ sep
						+ "     Uses the given parser to parse the input. Default is clips.");
		System.exit(0);
	}

	public JamochaGui getJamochaGui() {
		return jamochaGui;
	}

	public Shell getShell() {
		return shell;
	}
}
