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
import org.jamocha.rete.visualisation.UnknownConnectorTypeException;
import org.jamocha.rete.visualisation.VisualizerPanel;
import org.jamocha.settings.SettingsChangedListener;

/**
 * This Panel shows the viewer
 * 
 * @author Josef Alexander Hahn
 */
public class ViewerPanel extends AbstractJamochaPanel {

	private static final long serialVersionUID = -5732131176258158968L;

	private static final String GUI_VIEWER_CONNECTORTYPE = "gui.viewer.connectortype";
	
	private class ViewerSettingsChangedListener implements SettingsChangedListener {

		public void settingsChanged(String propertyName) {
			readSettings();
		}
		
	}
	
	VisualizerPanel visualizer;
	
	public ViewerPanel(JamochaGui gui) {
		super(gui);
		visualizer = new VisualizerPanel(gui.getEngine());
		setLayout(new BorderLayout());
		this.add(visualizer, BorderLayout.CENTER);
		
		String[] settingNames = {GUI_VIEWER_CONNECTORTYPE};
		SettingsChangedListener listener = new ViewerSettingsChangedListener();
		settings.addListener(listener, settingNames);
		readSettings();
	}

	protected void readSettings() {
		String connType = settings.getString(GUI_VIEWER_CONNECTORTYPE);
		try {
			visualizer.setConnectorType(connType);
		} catch (UnknownConnectorTypeException e) {
			gui.getEngine().writeMessage(e.toString());
		}
	}

	@Override
	public void close() {
		settings.set(GUI_VIEWER_CONNECTORTYPE, visualizer.getConnectorType());		
	}
	
	


}
