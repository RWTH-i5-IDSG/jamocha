/*
 * Copyright 2007 Karl-Heinz Krempels, Alexander Wilden
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
package org.jamocha.gui.tab;

import java.awt.BorderLayout;

import org.jamocha.gui.JamochaGui;
import org.jamocha.rete.visualisation.VisualizerPanel;

/**
 * This Panel shows the viewer
 * 
 * @author Josef Alexander Hahn
 */
public class ViewerPanel extends AbstractJamochaPanel {

	private static final long serialVersionUID = -5732131176258158968L;

	VisualizerPanel visualizer;
	
	public ViewerPanel(JamochaGui gui) {
		super(gui);
		visualizer = new VisualizerPanel(gui.getEngine());
		setLayout(new BorderLayout());
		this.add(visualizer, BorderLayout.CENTER);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void settingsChanged() {
		// TODO Auto-generated method stub
		
	}
	
	


}
