package org.jamocha;

import org.jamocha.gui.JamochaGui;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Shell;

public class Jamocha {

	private static boolean guiStarted = false;

	private static boolean shellStarted = false;

	private static Rete engine;

	/**
	 * @param args
	 *            In args can be one or more of the following Strings: -shell:
	 *            start the normal Shell with System.in and System.out -gui :
	 *            start the graphical user interface for Jamocha with different
	 *            tabs and nice, included Shell.
	 */
	public static void main(String[] args) {
		engine = new Rete();
		if (null != args) {
			for (int i = 0; i < args.length; ++i) {
				if (args[i].equalsIgnoreCase("-gui")) {
					if (!guiStarted) {
						startGui();
					}
				} else if (args[i].equalsIgnoreCase("-shell")) {
					if (!shellStarted) {
						startShell();
					}
				}
			}
		}
		// if no arguments were given or by another cause neither gui nor shell
		// was started, we show a usage guide.
		if (!shellStarted && !guiStarted) {
			showUsage();
		}
	}

	private static void startShell() {
		Thread shellThread = new Thread() {

			public void run() {
				Shell shell = new Shell(engine);
				shell.run();
			}

		};
		shellThread.start();
		shellStarted = true;
	}

	private static void startGui() {
		Thread guiThread = new Thread() {

			public void run() {
				JamochaGui jamocha = new JamochaGui(engine);
				jamocha.showGui();
			}

		};
		guiThread.start();
		guiStarted = true;
	}

	private static void showUsage() {
		String sep = System.getProperty("line.separator");
		System.out
				.println("You have to pass one or more of the following arguments:"
						+ sep
						+ sep
						+ "-gui:   starts a graphical user interface."
						+ sep
						+ "-shell: starts a simple Shell.");
		System.exit(0);
	}
}
