package org.jamocha.gui.tab;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;

public class AutoCompletionBox {

	JScrollPane list;

	JList alist;

	JFrame parent;

	Popup listFrame = null;

	boolean visible = false;

	int numElems = 0;

	Vector<String> strings = null;

	PopupFactory factory;

	public AutoCompletionBox(JFrame parent) {
		this.parent = parent;
		factory = PopupFactory.getSharedInstance();
		alist = new JList();
		list = new JScrollPane(alist);
		alist.setMinimumSize(new Dimension(70, 1));
		alist.setMaximumSize(new Dimension(1000, 100));
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

	public String getSelected() {
		return (String) alist.getSelectedValue();
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

}
