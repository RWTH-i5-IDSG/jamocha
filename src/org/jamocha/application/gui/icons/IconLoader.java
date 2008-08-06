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

package org.jamocha.application.gui.icons;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * The IconLoader loads all ImageIcons used in the GUI. To save memory it caches
 * used icons and loads them from the cache when reused.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class IconLoader {

	private static Map<String, ImageIcon> _iconCache = new HashMap<String, ImageIcon>();

	private IconLoader() {

	}

	public static ImageIcon getImageIcon(final String name) {
		return getImageIcon(name, IconLoader.class, "png");
	}

	@SuppressWarnings("unchecked")
	public static ImageIcon getImageIcon(final String name, final Class clazz) {
		return getImageIcon(name, clazz, "png");
	}

	@SuppressWarnings("unchecked")
	public static synchronized ImageIcon getImageIcon(final String name,
			final Class clazz, final String extension) {
		ImageIcon icon = _iconCache.get(name);
		if (null != icon) {
			return icon;
		}
		final URL url = clazz.getResource("images/" + name + "." + extension);
		if (url != null) {
			icon = new ImageIcon(url);
			_iconCache.put(name, icon);
		}
		return icon;
	}

}