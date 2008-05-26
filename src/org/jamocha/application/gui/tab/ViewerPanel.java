/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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
package org.jamocha.application.gui.tab;

import java.awt.BorderLayout;

import org.jamocha.application.gui.JamochaGui;
import org.jamocha.application.gui.retevisualisation.UnknownConnectorTypeException;
import org.jamocha.application.gui.retevisualisation.VisualizerPanel;
import org.jamocha.settings.SettingsChangedListener;
import org.jamocha.settings.SettingsConstants;

/**
 * This Panel shows the viewer
 * 
 * @author Josef Alexander Hahn
 */
public class ViewerPanel extends AbstractJamochaPanel {

	private static final long serialVersionUID = -5732131176258158968L;

	private class ViewerSettingsChangedListener implements
			SettingsChangedListener {

		public void settingsChanged(final String propertyName) {
			readSettings();
		}

	}

	VisualizerPanel visualizer;

	public ViewerPanel(final JamochaGui gui) {
		super(gui);
		visualizer = new VisualizerPanel(gui.getEngine());
		setLayout(new BorderLayout());
		this.add(visualizer, BorderLayout.CENTER);

		final String[] settingNames = { SettingsConstants.GUI_VIEWER_CONNECTORTYPE };
		final SettingsChangedListener listener = new ViewerSettingsChangedListener();
		settings.addListener(listener, settingNames);
		readSettings();
	}

	protected void readSettings() {
		final String connType = settings
				.getString(SettingsConstants.GUI_VIEWER_CONNECTORTYPE);
		try {
			visualizer.setConnectorType(connType);
		} catch (final UnknownConnectorTypeException e) {
			gui.getEngine().writeMessage(e.toString());
		}
	}

	@Override
	public void close() {
		settings.set(SettingsConstants.GUI_VIEWER_CONNECTORTYPE, visualizer
				.getConnectorType());
	}

}
