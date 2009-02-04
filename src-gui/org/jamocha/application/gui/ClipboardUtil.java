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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * 
 * A Class for Clipboard access.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class ClipboardUtil implements ClipboardOwner {

	/**
	 * The singleton ClipboardUtil.
	 */
	private static ClipboardUtil _instance = null;

	/**
	 * A private Constructur so that we can use singletons.
	 * 
	 */
	private ClipboardUtil() {

	}

	/**
	 * Returns the single ClipboardUtil instance.
	 * 
	 * @return The Clipboard singleton object.
	 */
	public static ClipboardUtil getInstance() {
		if (_instance == null) {
			_instance = new ClipboardUtil();
		}
		return _instance;
	}

	/**
	 * Called on lost ownership.
	 */
	public void lostOwnership(final Clipboard aClipboard,
			final Transferable aContents) {
		// do nothing
	}

	/**
	 * Place a String on the clipboard and make this class the owner of the
	 * Clipboard's contents.
	 */
	public void setClipboardContents(final String aString) {
		final StringSelection stringSelection = new StringSelection(aString);
		final Clipboard clipboard = Toolkit.getDefaultToolkit()
				.getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	/**
	 * Get the String residing on the clipboard.
	 * 
	 * @return any text found on the Clipboard. If none found, return the empty
	 *         String.
	 */
	public String getClipboardContents() {
		String result = "";
		final Clipboard clipboard = Toolkit.getDefaultToolkit()
				.getSystemClipboard();
		final Transferable contents = clipboard.getContents(null);
		final boolean hasTransferableText = contents != null
				&& contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result = (String) contents
						.getTransferData(DataFlavor.stringFlavor);
			} catch (final UnsupportedFlavorException ex) {
				ex.printStackTrace();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}
