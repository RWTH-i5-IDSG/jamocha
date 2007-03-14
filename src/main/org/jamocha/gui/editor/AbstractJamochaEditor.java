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
package org.jamocha.gui.editor;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.jamocha.rete.Rete;

/**
 * The abstract class all editors should implement.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
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
