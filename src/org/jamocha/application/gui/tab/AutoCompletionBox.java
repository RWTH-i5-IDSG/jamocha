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

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Popup;
import javax.swing.PopupFactory;

public class AutoCompletionBox implements KeyListener, MouseListener {

	JScrollPane list;

	ShellPanel panel;

	JList alist;

	JFrame parent;

	Popup listFrame = null;

	boolean visible = false;

	int numElems = 0;

	Vector<String> strings = null;

	AutoCompletion ac;

	PopupFactory factory;

	public AutoCompletionBox(final JFrame parent, final AutoCompletion ac,
			final ShellPanel panel) {
		this.parent = parent;
		this.panel = panel;
		factory = PopupFactory.getSharedInstance();
		alist = new JList();
		list = new JScrollPane(alist);
		alist.setMinimumSize(new Dimension(70, 1));
		alist.setMaximumSize(new Dimension(1000, 100));

		alist.addKeyListener(this);
		alist.addMouseListener(this);

		this.ac = ac;
	}

	public boolean isVisible() {
		return visible;
	}

	public void up() {
		int now = alist.getSelectedIndex();
		now--;
		if (now < 0) {
			now = strings.size() - 1;
		}
		alist.setSelectedIndex(now);
	}

	public void down() {
		int now = alist.getSelectedIndex();
		now++;
		if (now >= strings.size()) {
			now = 0;
		}
		alist.setSelectedIndex(now);
	}

	public void hide() {
		visible = false;
		if (listFrame != null) {
			listFrame.hide();
		}
	}

	public String getSelected(boolean full) {
		final String s = (String) alist.getSelectedValue();
		if (!full) {
			return s;
		}
		return ac.getFullText(s);
	}

	public void show(final Vector<String> lst, final int x, final int y) {
		if (visible && numElems == lst.size()) {
			return;
		}

		numElems = lst.size();
		visible = true;
		strings = lst;
		alist.setListData(lst);
		alist.setSelectedIndex(0);
		if (listFrame != null) {
			listFrame.hide();
		}
		listFrame = factory.getPopup(parent, list, x, y);
		listFrame.show();
	}

	public void keyPressed(final KeyEvent e) {
		final boolean full = (e.getModifiers() & KeyEvent.CTRL_MASK) != 0;
		final String autoCompletionPrefix = panel.getAutoCompletionPrefix();
		final JTextArea outputArea = panel.getOutputArea();
		if (full) {
			fullInsertion(autoCompletionPrefix, outputArea);
		} else {
			normalInsertion(autoCompletionPrefix, outputArea);
		}
		panel.scrollToCursor();
		hide();
		outputArea.requestFocus();
	}

	private void normalInsertion(final String autoCompletionPrefix,
			final JTextArea outputArea) {
		final String txt = getSelected(false).substring(
				autoCompletionPrefix.length());
		final int cursorPos = outputArea.getCaretPosition();
		outputArea.insert(txt + " ", cursorPos);
		panel.setCursorPosition(cursorPos + txt.length() + 1);
	}

	private void fullInsertion(final String autoCompletionPrefix,
			final JTextArea outputArea) {
		final String txt = getSelected(true).substring(
				autoCompletionPrefix.length());
		final String shorttxt = getSelected(false).substring(
				autoCompletionPrefix.length());
		final int cursorPos = outputArea.getCaretPosition();
		outputArea.insert(txt, cursorPos);
		panel.setCursorPosition(cursorPos + shorttxt.length() + 1);
	}

	public void keyReleased(final KeyEvent arg0) {

	}

	public void keyTyped(final KeyEvent arg0) {

	}

	public void mouseClicked(final MouseEvent arg0) {
		if (arg0.getClickCount() > 1) {
			normalInsertion(panel.getAutoCompletionPrefix(), panel
					.getOutputArea());
			panel.scrollToCursor();
			hide();
			panel.getOutputArea().requestFocus();
		}
	}

	public void mouseEntered(final MouseEvent arg0) {

	}

	public void mouseExited(final MouseEvent arg0) {

	}

	public void mousePressed(final MouseEvent arg0) {

	}

	public void mouseReleased(final MouseEvent arg0) {

	}

}
