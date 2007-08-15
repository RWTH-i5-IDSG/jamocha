package org.jamocha.gui.tab;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.Popup;

public class AutoCompletionBox {

	JList list;
	JFrame parent;
	Popup listFrame;
	
	public AutoCompletionBox(JFrame parent) {
		this.parent = parent;
		
		list = new JList();
		//listFrame.
	}
	
	public void hide() {
		listFrame.hide();
		//listFrame.setVisible(false);
	}
	
	public void show(Vector<String> lst) {
		list.setListData(lst);
		listFrame.show();
		//listFrame.setVisible(true);
	}
	
}
