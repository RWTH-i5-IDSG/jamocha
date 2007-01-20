package org.jamocha.gui.icons;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class IconLoader {

	private static Map<String, ImageIcon> _iconCache = new HashMap<String, ImageIcon>();

	public static ImageIcon getImageIcon(String name) {
		return getImageIcon(name, IconLoader.class, "png");
	}

	public static ImageIcon getImageIcon(String name, Class clazz) {
		return getImageIcon(name, clazz, "png");
	}

	public static synchronized ImageIcon getImageIcon(String name, Class clazz,
			String extension) {
		ImageIcon icon = _iconCache.get(name);
		if (null != icon) {
			return icon;
		}
		URL url = clazz.getResource("images/" + name + "." + extension);
		if (url != null) {
			icon = new ImageIcon(url);
			_iconCache.put(name, icon);
		}
		return icon;
	}

}
