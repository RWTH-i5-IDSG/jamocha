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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.jamocha.Constants;

/**
 * @author Sebastian Reinartz, Alexander Wilden
 */
public class JamochaSettings {

	private static JamochaSettings singleton = null;

	private final Preferences preferences;

	private final Properties defaults;

	private Map<String, JamochaSetting> settings;

	private Map<String, String> friendlyName2Name;

	private Map<String, List<SettingsChangedListener>> name2SettingsChangedListener;

	private JamochaSettings() {
		name2SettingsChangedListener = new HashMap<String, List<SettingsChangedListener>>();

		preferences = Preferences.userRoot().node("org/jamocha");
		defaults = new Properties();
		String defaultPropName = "default.properties";
		try {
			defaultPropName = this.getClass().getPackage().getName().replace(
					'.', '/')
					+ "/" + defaultPropName;
			// System.out.println(defaultPropName);
		} catch (Exception any) {
			// ignore - likely class not in package.
		}
		InputStream propertyStream = this.getClass().getClassLoader()
				.getResourceAsStream(defaultPropName);
		if (propertyStream != null) {
			try {
				defaults.load(propertyStream);
			} catch (IOException ioe) {
				System.err.println("Error reading default properties:"
						+ defaultPropName);
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

	public String getString(String key) {
		return (String) get(key);
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

	public int getInt(String key) {
		return (Integer) get(key);
	}

	public long getLong(String key) {
		return (Long) get(key);
	}

	public String getFriendlyName(String key) {
		JamochaSetting setting = settings.get(key);
		return (setting != null) ? setting.friendlyName : null;
	}

	public Set<String> getSettings() {
		return this.settings.keySet();
	}

	public boolean set(String key, String value) {
		preferences.put(key, value);
		// try to read settings:
		JamochaSetting setting = settings.get(key);
		// nor found? try to read friendlyNames:
		if (setting == null) {
			String name = friendlyName2Name.get(key);
			if (name != null) {
				setting = settings.get(name);
				key = name;
			}
		}
		// action:
		if (setting != null) {
			setting.setCurrentValue(value);
			informListeners(key);
			return true;
		}
		return false;
	}

	public boolean set(String key, int value) {
		return set(key, "" + value);
	}

	public boolean set(String key, boolean value) {
		return set(key, "" + value);
	}

	public boolean toDefault(String key) {
		JamochaSetting setting = settings.get(key);
		// nor found? try to read friendlyNames:
		if (setting == null) {
			String name = friendlyName2Name.get(key);
			if (name != null) {
				setting = settings.get(name);
				key = name;
			}
		}
		// action:
		if (setting != null) {
			setting.toDefault();
			preferences.put(key, setting.currentValue.toString());
			informListeners(key);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void readSettings() {
		// setup maps:
		this.settings = new HashMap<String, JamochaSetting>();
		this.friendlyName2Name = new HashMap<String, String>();

		// read defaults:
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

			addProperty(key.toString(), friendlyName, defaultValue,
					currentValue, type);
		}

	}

	public String getSettingsTable() {
		StringBuilder res = new StringBuilder();
		int firstColWidth = 0, secondColWidth = 0, thirdColWidth = 0, forthColWidth = 0;
		Collection<String> keys = friendlyName2Name.values();
		JamochaSetting sett;
		for (String key : keys) {
			sett = settings.get(key);
			firstColWidth = Math.max(firstColWidth, sett.friendlyName.length());
			secondColWidth = Math.max(secondColWidth, sett.getTypeString()
					.length());
			thirdColWidth = Math.max(thirdColWidth, sett.defaultValue
					.toString().length());
			forthColWidth = Math.max(forthColWidth, sett.currentValue
					.toString().length());
		}
		firstColWidth = Math.max(firstColWidth, "Name:".length());
		secondColWidth = Math.max(secondColWidth, "Type:".length());
		thirdColWidth = Math.max(thirdColWidth, "Default value:".length());
		forthColWidth = Math.max(forthColWidth, "Current value:".length());
		res.append("| Name:");
		appendCharChain(res, ' ', firstColWidth - 5);
		res.append(" | Type:");
		appendCharChain(res, ' ', secondColWidth - 5);
		res.append(" | Default value:");
		appendCharChain(res, ' ', thirdColWidth - 14);
		res.append(" | Current value:");
		appendCharChain(res, ' ', forthColWidth - 14);
		res.append(" |").append(Constants.LINEBREAK);
		appendCharChain(res, '=', res.length() - 1);
		res.append(Constants.LINEBREAK);
		String type;
		for (String key : keys) {
			sett = settings.get(key);
			res.append("| ").append(sett.friendlyName);
			appendCharChain(res, ' ', firstColWidth
					- sett.friendlyName.length());
			type = sett.getTypeString();
			res.append(" | ").append(type);
			appendCharChain(res, ' ', secondColWidth - type.length());
			res.append(" | ").append(sett.defaultValue);
			appendCharChain(res, ' ', thirdColWidth
					- sett.defaultValue.toString().length());
			res.append(" | ").append(sett.currentValue);
			appendCharChain(res, ' ', forthColWidth
					- sett.currentValue.toString().length());
			res.append(" |").append(Constants.LINEBREAK);
		}
		return res.toString();
	}

	/**
	 * @param listener
	 * @param settingNames
	 */
	public void addListener(SettingsChangedListener listener,
			String[] settingNames) {
		// traverse all settingNames:
		for (String settingName : settingNames) {
			// do we have a listener list for this setting?
			List<SettingsChangedListener> listeners = this.name2SettingsChangedListener
					.get(settingName);
			if (listeners == null) {
				listeners = new ArrayList<SettingsChangedListener>();
				this.name2SettingsChangedListener.put(settingName, listeners);
			}
			listeners.add(listener);
			listener.settingsChanged(settingName);
		}
	}

	/**
	 * 
	 * @param listener
	 */
	public void removeListener(SettingsChangedListener listener) {
		assert (false);
		// TODO: implement me!
	}

	protected void informListeners(String propertyName) {
		List<SettingsChangedListener> listeners = this.name2SettingsChangedListener
				.get(propertyName);
		if (listeners != null) {
			for (SettingsChangedListener l : listeners) {
				l.settingsChanged(propertyName);
			}
		}
	}

	private void addProperty(String name, String friendlyName,
			String defaultValue, String currentValue, String type) {
		this.settings.put(name, new JamochaSetting(name, friendlyName,
				defaultValue, currentValue, type));
		// do we hav a friendly name? -> add to second hashmap:
		if (!friendlyName.equals("") && friendlyName != null)
			this.friendlyName2Name.put(friendlyName, name);
	}

	protected void finalize() {
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	protected void appendCharChain(StringBuilder sb, char c, int length) {
		for (int i = 0; i < length; ++i)
			sb.append(c);
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

		public JamochaSetting(String name, String friendlyName,
				String defaultValue, String currentValue, String type) {
			super();
			this.type = getType(type);
			this.name = name;
			this.friendlyName = friendlyName;
			this.defaultValue = getValue(defaultValue);
			this.currentValue = getValue(currentValue);
		}

		public void toDefault() {
			currentValue = defaultValue;
		}

		public String getTypeString() {
			switch (type) {
			case TYPE_INT:
				return "Integer";
			case TYPE_LONG:
				return "Long";
			case TYPE_DOUBLE:
				return "Double";
			case TYPE_FLOAT:
				return "Float";
			case TYPE_STRING:
				return "String";
			case TYPE_BOOLEAN:
				return "Boolean";
			default:
				return "Unknown";
			}
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

		public Object setCurrentValue(String newValue) {
			this.currentValue = getValue(newValue);
			return this.currentValue;
		}
	}

}
