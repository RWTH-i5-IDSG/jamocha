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

package org.jamocha.application.gui.editor;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.jamocha.engine.Engine;

/**
 * The abstract class all editors should implement.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public abstract class AbstractJamochaEditor extends JFrame {

	protected Engine engine;

	public AbstractJamochaEditor(final Engine engine) {
		this.engine = engine;
		setTitle("Jamochaeditor");
		this.setSize(600, 400);
		setMinimumSize(new Dimension(600, 400));
		setLocationByPlatform(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent event) {
				close();
			}
		});
	}

	public void close() {
		setVisible(false);
		dispose();
	}

	public abstract void init();

}
