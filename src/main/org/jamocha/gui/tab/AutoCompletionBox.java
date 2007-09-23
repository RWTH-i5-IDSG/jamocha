package org.jamocha.gui.tab;

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

	public AutoCompletionBox(JFrame parent, AutoCompletion ac, ShellPanel panel) {
		this.parent = parent;
		this.panel = panel;
		factory = PopupFactory.getSharedInstance();
		alist = new JList();
		list = new JScrollPane(alist);
		alist.setMinimumSize(new Dimension(70, 1));
		alist.setMaximumSize(new Dimension(1000, 100));
		
		alist.addKeyListener(this);
		alist.addMouseListener(this);
		
		this.ac=ac;
	}

	public boolean isVisible() {
		return visible;
	}

	public void up() {
		int now = alist.getSelectedIndex();
		now--;
		if (now < 0)
			now = strings.size() - 1;
		alist.setSelectedIndex(now);
	}

	public void down() {
		int now = alist.getSelectedIndex();
		now++;
		if (now >= strings.size())
			now = 0;
		alist.setSelectedIndex(now);
	}

	public void hide() {
		visible = false;
		if (listFrame != null)
			listFrame.hide();
	}

	public String getSelected(boolean full) {
		String s = (String) alist.getSelectedValue();
		if (!full) return s;
		return ac.getFullText(s);
	}

	public void show(Vector<String> lst, int x, int y) {
		if (visible && numElems == lst.size())
			return;

		numElems = lst.size();
		visible = true;
		strings = lst;
		alist.setListData(lst);
		alist.setSelectedIndex(0);
		if (listFrame != null)
			listFrame.hide();
		listFrame = factory.getPopup(parent, list, x, y);
		listFrame.show();
	}

	
	public void keyPressed(KeyEvent e) {
		boolean full = ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0);
		String autoCompletionPrefix = panel.getAutoCompletionPrefix();
		JTextArea outputArea = panel.getOutputArea();
		if (full) {
			fullInsertion(autoCompletionPrefix, outputArea);
		} else {
			normalInsertion(autoCompletionPrefix, outputArea);
		}
		panel.scrollToCursor();
		this.hide();
		outputArea.requestFocus();
	}

	private void normalInsertion(String autoCompletionPrefix,
			JTextArea outputArea) {
		String txt = this.getSelected(
				false).substring(
						autoCompletionPrefix.length());
		int cursorPos = outputArea.getCaretPosition();
		outputArea.insert(txt + " ", cursorPos);
		panel.setCursorPosition(cursorPos + txt.length() + 1);
	}

	private void fullInsertion(String autoCompletionPrefix, JTextArea outputArea) {
		String txt = this.getSelected(true).substring(
				autoCompletionPrefix.length());
		String shorttxt = this.getSelected(false).substring(autoCompletionPrefix.length());
		int cursorPos = outputArea.getCaretPosition();
		outputArea.insert(txt, cursorPos);
		panel.setCursorPosition(cursorPos + shorttxt.length() + 1);
	}

	public void keyReleased(KeyEvent arg0) {
		
	}

	public void keyTyped(KeyEvent arg0) {
		
	}

	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getClickCount()>1) {
			normalInsertion(panel.getAutoCompletionPrefix(), panel.getOutputArea());
			panel.scrollToCursor();
			this.hide();
			panel.getOutputArea().requestFocus();
		}
	}

	public void mouseEntered(MouseEvent arg0) {
		
	}

	public void mouseExited(MouseEvent arg0) {
		
	}

	public void mousePressed(MouseEvent arg0) {
		
	}

	public void mouseReleased(MouseEvent arg0) {
		
	}

}
