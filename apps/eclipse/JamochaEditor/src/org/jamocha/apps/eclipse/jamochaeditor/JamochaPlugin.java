package org.jamocha.apps.eclipse.jamochaeditor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The Main Plugin Class
 */
public class JamochaPlugin extends AbstractUIPlugin {
	public static String PREF_COLOR_COMMENT = "jamocha_color_comment"; 
	public static String PREF_COLOR_STRING = "jamocha_color_string";
	public static String PREF_COLOR_FUNCTION = "jamocha_color_function";
	public static String PREF_COLOR_BRACKET = "jamocha_color_bracket";
	public static String PREF_BOOL_BRACKET = "jamocha_bool_bracket";

	
	//create a resource Bundle (needed to implement Code completion)
	private ResourceBundle resourceBundle;
	
	
	// T M P !
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	//return the plugin's resource bundle
	//the resource bundle is located in a file named JamochaEditorResources.properties
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				
				// adjust package also here:
				resourceBundle = ResourceBundle.getBundle("org.jamocha.apps.eclipse.jamochaeditor.JamochaEditorResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
			MessageDialog.openInformation(null, "Error", "Resource Bundle not found.");
		}
		return resourceBundle;
	}
	
	
	// Returns the string from the plugin's resource bundle,
	public static String getResourceString(String key) {
		ResourceBundle bundle = JamochaPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}
	
	
	
	
	
	
	// The plug-in ID
	public static final String PLUGIN_ID = "JamochaEditor";

	// The shared instance
	private static JamochaPlugin plugin;
	
	/**
	 * The constructor
	 */
	public JamochaPlugin() {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}
	
	public void initializeDefaultPluginPreferences() {
		IPreferenceStore store = getPreferenceStore();
		
		store.setDefault(PREF_BOOL_BRACKET, true);
		PreferenceConverter.setDefault(store, PREF_COLOR_BRACKET, new RGB(192, 192, 192));
		PreferenceConverter.setDefault(store, PREF_COLOR_COMMENT, new RGB(180,180,180));
		PreferenceConverter.setDefault(store, PREF_COLOR_STRING, new RGB(210,0,240));
		PreferenceConverter.setDefault(store, PREF_COLOR_FUNCTION, new RGB(0,180,220));

		savePluginPreferences();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static JamochaPlugin getDefault() {
		return plugin;
	}

}
