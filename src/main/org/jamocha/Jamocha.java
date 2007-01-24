package org.jamocha;

import org.jamocha.gui.JamochaGui;
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
		boolean guiStarted = false;
		boolean shellStarted = false;
		Jamocha jamocha = new Jamocha(new Rete());
		if (null != args) {
			for (int i = 0; i < args.length; ++i) {
				if (args[i].equalsIgnoreCase("-gui")) {
					jamocha.startGui();
					guiStarted = true;
				} else if (args[i].equalsIgnoreCase("-shell")) {
					jamocha.startShell();
					shellStarted = true;
				}
			}
		}
		// if no arguments were given or by another cause neither gui nor shell
		// were started, we show a usage guide.
		if (!shellStarted && !guiStarted) {
			jamocha.showUsage();
		} else if (!shellStarted) {
			jamocha.getJamochaGui().setExitOnClose(true);
		}
	}

	Jamocha(Rete engine) {
		this.engine = engine;
	}

	public void startShell() {
		if (shell == null) {
			Thread shellThread = new Thread() {

				public void run() {
					shell = new Shell(engine);
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
						+ "-gui:   starts a graphical user interface."
						+ sep
						+ "-shell: starts a simple Shell.");
		System.exit(0);
	}

	public JamochaGui getJamochaGui() {
		return jamochaGui;
	}

	public Shell getShell() {
		return shell;
	}
}
