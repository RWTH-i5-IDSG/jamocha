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

package org.jamocha.application.gui;

import java.awt.Component;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

/**
 * This Class provides system dependend access to the Browser. For Unix and
 * Linux this just ends up in trying out all possible browsers. This seems not
 * nice but there is no other solution to it at the moment (at least I didn't
 * find one. I there's a better one please tell me).
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class BrowserControl {

	@SuppressWarnings("unchecked")
	public static void displayURL(final String url, final Component opener) {
		final String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				final Class fileMgr = Class
						.forName("com.apple.eio.FileManager");
				final Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (osName.startsWith("Windows")) {
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);
			} else {
				// assume Unix or Linux
				final String[] browsers = { "firefox", "opera", "konqueror",
						"epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++) {
					if (Runtime.getRuntime().exec(
							new String[] { "which", browsers[count] })
							.waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else {
					Runtime.getRuntime().exec(new String[] { browser, url });
				}
			}
		} catch (final Exception e) {
			// just ignore it. We don't want to bother the user just because we
			// can't open an url ...
			JOptionPane.showMessageDialog(opener,
					"Jamocha could not open your webbrowser and browse to: \n\n"
							+ url + ".", "Error opening webbrowser.",
					JOptionPane.ERROR_MESSAGE);
		}

	}
}