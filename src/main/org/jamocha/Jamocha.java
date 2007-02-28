package org.jamocha;

import java.util.LinkedList;
import java.util.List;

import org.jamocha.gui.JamochaGui;
import org.jamocha.parser.ParserNotFoundException;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Shell;

public class Jamocha {

	private JamochaGui jamochaGui;

	private Shell shell;

	private Rete engine;

	/**
	 * @param args
	 *            In args can be one or more of the following Strings: -shell:
	 *            start the normal Shell with System.in and System.out -gui :
	 *            start the graphical user interface for Jamocha with different
	 *            tabs and nice, included Shell.
	 */
	public static void main(String[] args) {
		boolean startGui = false;
		boolean startShell = false;
		Jamocha jamocha = new Jamocha(new Rete());
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
		if (startShell) {
			try {
				jamocha.startShell(parser);
			} catch (ParserNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		if (startGui) {
			jamocha.startGui(parser);
			if (!batchFiles.isEmpty()) {
				jamocha.getJamochaGui().processBatchFiles(batchFiles);
			}
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

	Jamocha(Rete engine) {
		this.engine = engine;
	}

	public void startShell(String parserName) throws ParserNotFoundException {
		if (shell == null) {
			if (parserName.length() > 0) {
				shell = new Shell(engine, parserName);
			} else {
				shell = new Shell(engine);
			}
			Thread shellThread = new Thread() {

				public void run() {
					shell.run();
				}

			};
			shellThread.start();
		}
	}

	public void startGui(String parserName) {
		if (jamochaGui == null) {
			if (parserName.length() > 0) {
				jamochaGui = new JamochaGui(engine, parserName);
			} else {
				jamochaGui = new JamochaGui(engine);
			}
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
