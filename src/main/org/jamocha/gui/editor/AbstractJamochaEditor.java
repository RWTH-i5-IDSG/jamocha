package org.jamocha.gui.editor;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.jamocha.rete.Rete;

public abstract class AbstractJamochaEditor extends JFrame {

	protected Rete engine;

	public AbstractJamochaEditor(Rete engine) {
		this.engine = engine;
		this.setTitle("Jamochaeditor");
		this.setSize(600, 400);
		this.setMinimumSize(new Dimension(600, 400));
		this.setLocationByPlatform(true);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				close();
			}
		});
	}

	public void close() {
		this.setVisible(false);
		this.dispose();
	}

	public abstract void init();

}
