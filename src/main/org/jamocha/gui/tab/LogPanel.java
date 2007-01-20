package org.jamocha.gui.tab;

import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import org.jamocha.gui.JamochaGui;
import org.jamocha.messagerouter.InterestType;
import org.jamocha.messagerouter.StringChannel;

public class LogPanel extends AbstractJamochaPanel {

	private JTextArea detailView;
	
	private JTable logTable;
	
	private StringChannel logChannel;
	
	public LogPanel(JamochaGui gui) {
		super(gui);
		logChannel = gui.getEngine().getMessageRouter().openChannel("gui_log", InterestType.ALL);
		logTable = new JTable();
		detailView = new JTextArea();
		detailView.setEditable(false);
		JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,logTable,detailView);
		pane.setDividerLocation(300);
		pane.setSize(this.getSize());
		add(pane);
	}
	
	public void close() {
		super.close();
		gui.getEngine().getMessageRouter().closeChannel(logChannel);
	}

}
