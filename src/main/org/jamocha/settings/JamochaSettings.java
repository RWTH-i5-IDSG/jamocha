/*
 * Copyright 2007 Sebastian Reinartz, Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;

/**
 * @author Sebastian Reinartz, Alexander Wilden
 */
public class JamochaSettings {

	private static JamochaSettings singleton = null;

	private final Preferences preferences;

	private final Properties defaults;

	private Map<String, JamochaSetting> settings;

	private Set<SettingsChangedListener> listeners; // listeners

	private JamochaSettings() {
		listeners = new HashSet<SettingsChangedListener>();

		preferences = Preferences.userRoot().node("org/jamocha/gui");
		defaults = new Properties();
		String defaultPropName = "default.properties";
		try {
			defaultPropName = this.getClass().getPackage().getName().replace('.', '/') + "/" + defaultPropName;
			// System.out.println(defaultPropName);
		} catch (Exception any) {
			// ignore - likely class not in package.
		}
		InputStream propertyStream = this.getClass().getClassLoader().getResourceAsStream(defaultPropName);
		if (propertyStream != null) {
			try {
				defaults.load(propertyStream);
			} catch (IOException ioe) {
				System.err.println("Error reading default properties:" + defaultPropName);
				System.exit(-1);
			}
		}

		readSettings();
	}

	public static JamochaSettings getInstance() {
		if (singleton == null)
			singleton = new JamochaSettings();
		return singleton;
	}

	public Object get(String key) {
		return settings.get(key).currentValue;
	}

	public Object getDefault(String key) {
		return settings.get(key).defaultValue;
	}

	public boolean getBoolean(String key) {
		return (Boolean) get(key);
	}

	public double getDouble(String key) {
		return (Double) get(key);
	}

	public float getFloat(String key) {
		return (Float) get(key);
	}

	public int getInteger(String key) {
		return (Integer) get(key);
	}

	public long getLong(String key) {
		return (Long) get(key);
	}

	public void set(String key, String value) {
		preferences.put(key, value);
		JamochaSetting setting = settings.get(key);
		if (setting != null) {
			setting.setCurrentValue(value);
		}
	}

	private void readSettings() {
		this.settings = new HashMap<String, JamochaSetting>();
		Set keys = defaults.keySet();
		String[] splits;
		String type;
		String defaultValue;
		String currentValue;
		String friendlyName;
		for (Object key : keys) {
			// readout defaults:
			splits = defaults.getProperty(key.toString()).split(":");
			type = splits[0];
			defaultValue = splits[1];
			friendlyName = (splits.length > 2) ? splits[2] : "";
			// look for value in preferences:
			currentValue = preferences.get(key.toString(), defaultValue);

			addProperty(key.toString(), friendlyName, defaultValue, currentValue, type);
		}

	}

	/**
	 * adds a listener
	 */
	public void addListener(SettingsChangedListener listener) {
		listeners.add(listener);
	}

	/**
	 * removes a listener
	 */
	public void removeListener(SettingsChangedListener listener) {
		listeners.remove(listener);
	}

	private void addProperty(String name, String friendlyName, String defaultValue, String currentValue, String type) {
		this.settings.put(name, new JamochaSetting(name, friendlyName, defaultValue, currentValue, type));
	}

	// private class:
	private class JamochaSetting {

		// types
		static final int TYPE_INT = 0;

		static final int TYPE_LONG = 1;

		static final int TYPE_DOUBLE = 2;

		static final int TYPE_FLOAT = 3;

		static final int TYPE_STRING = 4;

		static final int TYPE_BOOLEAN = 5;

		// fields:
		private int type;

		String name;

		String friendlyName;

		Object defaultValue;

		Object currentValue;

		public JamochaSetting(String name, String friendlyName, String defaultValue, String currentValue, String type) {
			super();
			this.type = getType(type);
			this.name = name;
			this.friendlyName = friendlyName;
			this.defaultValue = getValue(defaultValue);
			this.currentValue = getValue(currentValue);
		}

		private int getType(String typeString) {
			if (typeString.equals("int")) {
				return TYPE_INT;
			} else if (typeString.equals("long")) {
				return TYPE_LONG;
			} else if (typeString.equals("double")) {
				return TYPE_DOUBLE;
			} else if (typeString.equals("float")) {
				return TYPE_FLOAT;
			} else if (typeString.equals("string")) {
				return TYPE_STRING;
			} else if (typeString.equals("boolean")) {
				return TYPE_BOOLEAN;
			}
			return -1;
		}

		private Object getValue(String value) {
			switch (type) {
			case TYPE_INT:
				return Integer.parseInt(value);
			case TYPE_LONG:
				return Long.parseLong(value);
			case TYPE_DOUBLE:
				return Double.parseDouble(value);
			case TYPE_FLOAT:
				return Float.parseFloat(value);
			case TYPE_STRING:
				return value;
			case TYPE_BOOLEAN:
				return Boolean.parseBoolean(value);
			default:
				return null;
			}
		}

		public void resetValue() {
			this.currentValue = this.defaultValue;
		}

		public void setCurrentValue(String newValue) {
			this.currentValue = getValue(newValue);
		}
	}

}
