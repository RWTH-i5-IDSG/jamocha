/*
 * Copyright 2007 Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.gui.tab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.jamocha.gui.JamochaGui;
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.messagerouter.InterestType;
import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.rete.Function;

/**
 * The LogPanel uses an own Channel listing to all other channels to collect and
 * display log information.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class LogPanel extends AbstractJamochaPanel implements ActionListener,
		ListSelectionListener {

	private static final long serialVersionUID = 4811690181744862051L;

	private JSplitPane pane;

	private JTextArea detailView;

	private JTable logTable;

	private JButton clearButton;

	private LogTableModel dataModel = new LogTableModel();

	private LogTableCellRenderer cellRenderer;

	private StringChannel logChannel;

	private boolean running = true;

	public LogPanel(JamochaGui gui) {
		super(gui);
		setLayout(new BorderLayout());
		logChannel = gui.getEngine().getMessageRouter().openChannel("gui_log",
				InterestType.ALL);
		detailView = new JTextArea();
		detailView.setEditable(false);
		detailView.setFont(new Font("Courier", Font.PLAIN, 12));
		cellRenderer = new LogTableCellRenderer();
		logTable = new JTable(dataModel) {

			private static final long serialVersionUID = 1L;

			public TableCellRenderer getCellRenderer(int row, int column) {
				return cellRenderer;
			}
		};
		logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		logTable.getSelectionModel().addListSelectionListener(this);
		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(
				logTable), new JScrollPane(detailView));
		pane.setDividerLocation(gui.getPreferences().getInt(
				"log.dividerlocation", 300));
		add(pane, BorderLayout.CENTER);

		Thread logThread = new Thread() {
			public void run() {
				List<MessageEvent> msgEvents = new LinkedList<MessageEvent>();
				while (running) {
					logChannel.fillEventList(msgEvents);
					if (!msgEvents.isEmpty()) {
						dataModel.addEvents(msgEvents);
						msgEvents.clear();
					} else {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// Can be ignored
						}
					}
				}
				LogPanel.this.gui.getEngine().getMessageRouter().closeChannel(
						logChannel);
			}
		};
		logThread.start();
		clearButton = new JButton("Clear Log", IconLoader
				.getImageIcon("monitor"));
		clearButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		buttonPanel.add(clearButton);
		add(buttonPanel, BorderLayout.PAGE_END);
	}

	public void close() {
		running = false;
		gui.getPreferences().putInt("log.dividerlocation",
				pane.getDividerLocation());
	}

	public void settingsChanged() {

	}

	private final class LogMessageEvent extends MessageEvent {

		private static final long serialVersionUID = -5690784906495393031L;

		private Calendar datetime = Calendar.getInstance();

		private String typeFormatted;

		private int superType;

		public static final int TYPE_EVENT = 1;

		public static final int TYPE_WARNING = 2;

		public static final int TYPE_ERROR = 3;

		public LogMessageEvent(MessageEvent event) {
			this(event.getType(), event.getMessage(), event.getChannelId());
		}

		public LogMessageEvent(int type, Object message, String channelId) {
			super(type, message, channelId);
			switch (type) {
			case MessageEvent.ADD_NODE_ERROR:
				typeFormatted = "ERROR: Error adding node";
				superType = 3;
				break;
			case MessageEvent.ADD_RULE_EVENT:
				typeFormatted = "EVENT: added Rule";
				superType = 1;
				break;
			case MessageEvent.CLIPSPARSER_ERROR:
				typeFormatted = "ERROR: Error in CLIPSParser";
				superType = 3;
				break;
			case MessageEvent.CLIPSPARSER_REINIT:
				typeFormatted = "EVENT: CLIPSParser reinitialized";
				superType = 1;
				break;
			case MessageEvent.CLIPSPARSER_WARNING:
				typeFormatted = "WARNING: CLIPSParser-Warning";
				superType = 2;
				break;
			case MessageEvent.COMMAND:
				typeFormatted = "EVENT: incoming Command";
				superType = 1;
				break;
			case MessageEvent.ENGINE:
				typeFormatted = "EVENT: Engine-Message";
				superType = 1;
				break;
			case MessageEvent.ERROR:
				typeFormatted = "ERROR: unspecified Error";
				superType = 3;
				break;
			case MessageEvent.FUNCTION_INVALID:
				typeFormatted = "WARNING: invalid Function";
				superType = 2;
				break;
			case MessageEvent.FUNCTION_NOT_FOUND:
				typeFormatted = "WARNING: Function not found";
				superType = 2;
				break;
			case MessageEvent.INVALID_RULE:
				typeFormatted = "WARNING: invalid Rule";
				superType = 2;
				break;
			case MessageEvent.PARSE_ERROR:
				typeFormatted = "ERROR: Parse-Error";
				superType = 3;
				break;
			case MessageEvent.REMOVE_RULE_EVENT:
				typeFormatted = "EVENT: Rule removed";
				superType = 1;
				break;
			case MessageEvent.RESULT:
				typeFormatted = "EVENT: returned result";
				superType = 1;
				break;
			case MessageEvent.RULE_EXISTS:
				typeFormatted = "EVENT: Rule exists";
				superType = 1;
				break;
			case MessageEvent.TEMPLATE_NOTFOUND:
				typeFormatted = "WARNING: Template not found";
				superType = 2;
				break;
			default:
				typeFormatted = "Unknown Messagetype";
				superType = 1;
				break;
			}
		}

		public int getSuperType() {
			return superType;
		}

		public String getDatetimeFormatted() {
			StringBuilder res = new StringBuilder();
			res.append(datetime.get(Calendar.YEAR) + "/");
			res.append(((datetime.get(Calendar.MONTH) + 1 > 9) ? "" : "0")
					+ (datetime.get(Calendar.MONTH) + 1) + "/");
			res.append(((datetime.get(Calendar.DAY_OF_MONTH) > 9) ? "" : "0")
					+ datetime.get(Calendar.DAY_OF_MONTH) + " - ");
			res.append(((datetime.get(Calendar.HOUR_OF_DAY) > 9) ? "" : "0")
					+ datetime.get(Calendar.HOUR_OF_DAY) + ":");
			res.append(((datetime.get(Calendar.MINUTE) > 9) ? "" : "0")
					+ datetime.get(Calendar.MINUTE) + ":");
			res.append(((datetime.get(Calendar.SECOND) > 9) ? "" : "0")
					+ datetime.get(Calendar.SECOND));
			return res.toString();
		}

		public String getTypeFormatted() {
			return typeFormatted;
		}

	}

	private final class LogTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -6649805279420707106L;

		private Color colorError = Color.RED;

		private Color colorWarning = Color.ORANGE;

		private Color colorEvent = Color.BLUE;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			JComponent returnComponent = (JComponent) super
					.getTableCellRendererComponent(table, value, isSelected,
							hasFocus, row, column);
			switch (((LogTableModel) table.getModel()).getRow(row)
					.getSuperType()) {
			case LogMessageEvent.TYPE_ERROR:
				setForeground(colorError);
				break;
			case LogMessageEvent.TYPE_WARNING:
				setForeground(colorWarning);
				break;
			default:
				setForeground(colorEvent);
				break;
			}
			return returnComponent;

		}
	}

	private final class LogTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private List<LogMessageEvent> events = new LinkedList<LogMessageEvent>();

		private int maxEventCount = 1000;

		private void addEvents(List<MessageEvent> events) {

			logTable.getColumnModel().getColumn(0).setPreferredWidth(180);
			logTable.getColumnModel().getColumn(1).setPreferredWidth(180);
			logTable.getColumnModel().getColumn(2).setPreferredWidth(
					logTable.getWidth() - 360);
			for (MessageEvent event : events) {
				this.events.add(new LogMessageEvent(event));
			}
			while (this.events.size() > maxEventCount) {
				this.events.remove(0);
			}
			fireTableDataChanged();
		}

		private void clearEvents() {
			events.clear();
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Date - Time";
			case 1:
				return "Channel";
			case 2:
				return "Message-Type";
			default:
				return null;
			}
		}

		public int getColumnCount() {
			return 3;
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int aColumn) {
			return java.lang.String.class;
		}

		public int getRowCount() {
			return events.size();
		}

		public LogMessageEvent getRow(int row) {
			return events.get(events.size() - (row + 1));
		}

		public Object getValueAt(int row, int column) {
			LogMessageEvent event = getRow(row);
			if (event != null) {
				switch (column) {
				case 0:
					return event.getDatetimeFormatted();
				case 1:
					return event.getChannelId();
				case 2:
					return event.getTypeFormatted();
				}
			}
			return null;
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == clearButton) {
			dataModel.clearEvents();
			detailView.setText("");
		}
	}

	public void valueChanged(ListSelectionEvent arg0) {
		if (arg0.getSource() == logTable.getSelectionModel()) {
			StringBuilder buffer = new StringBuilder();
			if (logTable.getSelectedRow() > -1) {
				LogMessageEvent event = dataModel.getRow(logTable
						.getSelectedRow());
				buffer.append("Date-Time:    " + event.getDatetimeFormatted()
						+ "\nChannel:      " + event.getChannelId()
						+ "\nMessage-Type: " + event.getTypeFormatted()
						+ "\n\nMessage:\n========\n");
				Object message = event.getMessage();
				if (message instanceof Exception) {
					Exception ex = (Exception) message;
					StackTraceElement[] str = ex.getStackTrace();
					buffer.append(ex.getClass().getName() + ": "
							+ ex.getMessage());
					for (StackTraceElement strelem : str) {
						buffer.append("\n" + strelem);
					}
				} else if (message instanceof Function) {
					buffer.append("(" + ((Function) message).getName() + ")");
				} else if (message != null) {
					buffer.append(message.toString());
				}
			}
			detailView.setText(buffer.toString());
		}
	}

}
