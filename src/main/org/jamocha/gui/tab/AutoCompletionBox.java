package org.jamocha.gui.tab;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.Popup;
import javax.swing.PopupFactory;

public class AutoCompletionBox {

	JList list;
	JFrame parent;
	Popup listFrame =null;
	boolean visible = false;
	int numElems=0;
	Vector<String> strings = null;
	
	PopupFactory factory;
	
	public AutoCompletionBox(JFrame parent) {
		this.parent = parent;
		factory = PopupFactory.getSharedInstance();
		list = new JList();
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void up() {
		int now = list.getSelectedIndex();
		now --;
		if (now < 0) now = strings.size()-1;
		list.setSelectedIndex(now);
	}
	
	public void down() {
		int now = list.getSelectedIndex();
		now ++;
		if (now >= strings.size()) now = 0;
		list.setSelectedIndex(now);
	}
	
	public void hide() {
		visible=false;
		if (listFrame != null)	listFrame.hide();
	}
	
	public void show(Vector<String> lst, int x, int y) {
		if (visible && numElems==lst.size() ) return;
		
		numElems = lst.size();
		visible=true;
		strings = lst;
		list.setListData(lst);
		list.setSelectedIndex(0);
		if (listFrame != null) listFrame.hide();
		listFrame = factory.getPopup(parent, list, x, y);
		listFrame.show();
	}
	
}
