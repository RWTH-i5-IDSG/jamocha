package org.jamocha;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.Preferences;

public class JamochaSettings {

	private static JamochaSettings singleton = null;

	private final Preferences preferences;

	private final Properties defaults;
	
	private Map<String,JamochaSetting> settings;

	private JamochaSettings() {
		preferences = Preferences.userRoot().node("org/jamocha/gui");
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
		settings = readSettings();
	}

	public static JamochaSettings getInstance() {
		if (singleton == null)
			singleton = new JamochaSettings();
		return singleton;
	}

	public String get(String key) {
		return preferences.get(key, getDefault(key));
	}

	public String getDefault(String key) {
		return getDefaultValue(key);
	}

	public boolean getBoolean(String key) {
		return preferences.getBoolean(key, getDefaultBoolean(key));
	}

	public boolean getDefaultBoolean(String key) {
		return Boolean.parseBoolean(getDefaultValue(key));
	}

	public double getDouble(String key) {
		return preferences.getDouble(key, getDefaultDouble(key));
	}

	public double getDefaultDouble(String key) {
		return Double.parseDouble(getDefaultValue(key));
	}

	public float getFloat(String key) {
		return preferences.getFloat(key, getDefaultFloat(key));
	}

	public float getDefaultFloat(String key) {
		return Float.parseFloat(getDefaultValue(key));
	}

	public int getInteger(String key) {
		return preferences.getInt(key, getDefaultInteger(key));
	}

	public int getDefaultInteger(String key) {
		return Integer.parseInt(getDefaultValue(key));
	}

	public long getLong(String key) {
		return preferences.getLong(key, getDefaultLong(key));
	}

	public long getDefaultLong(String key) {
		return Long.parseLong(getDefaultValue(key));
	}

	public byte[] getByteArray(String key) {
		return preferences.getByteArray(key, getDefaultByteArray(key));
	}

	public byte[] getDefaultByteArray(String key) {
		return getDefaultValue(key).getBytes();
	}

	public void set(String key, String value) {
		preferences.put(key, value);
	}

	public void set(String key, boolean value) {
		preferences.putBoolean(key, value);
	}

	public void set(String key, double value) {
		preferences.putDouble(key, value);
	}

	public void set(String key, float value) {
		preferences.putFloat(key, value);
	}

	public void set(String key, int value) {
		preferences.putInt(key, value);
	}

	public void set(String key, long value) {
		preferences.putLong(key, value);
	}

	public void set(String key, byte[] value) {
		preferences.putByteArray(key, value);
	}

	private String getDefaultValue(String key) {
		if (defaults.containsKey(key)) {
			String[] temp = defaults.getProperty(key).split(":");
			return temp[temp.length - 1];
		}
		return "";
	}

	private Map readSettings() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class JamochaSetting {
		
		String defaultValue;
		
		String currentValue;
		
		int type;
		
	}

}
