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

import org.jamocha.application.gui.JamochaGui;
import org.jamocha.application.gui.icons.IconLoader;
import org.jamocha.communication.events.MessageEvent;
import org.jamocha.communication.messagerouter.InterestType;
import org.jamocha.communication.messagerouter.StringChannel;
import org.jamocha.parser.Expression;
import org.jamocha.parser.ParserFactory;

/**
 * The LogPanel uses an own Channel listing to all other channels to collect and
 * display log information.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class LogPanel extends AbstractJamochaPanel implements ActionListener,
		ListSelectionListener {

	private static final String GUI_LOG_DIVIDERLOCATION = "gui.log.dividerlocation";

	private static final long serialVersionUID = 4811690181744862051L;

	private final JSplitPane pane;

	private final JTextArea detailView;

	private final JTable logTable;

	private final JButton clearButton;

	private final LogTableModel dataModel = new LogTableModel();

	private final LogTableCellRenderer cellRenderer;

	private final StringChannel logChannel;

	private boolean running = true;

	public LogPanel(final JamochaGui gui) {
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

			@Override
			public TableCellRenderer getCellRenderer(final int row,
					final int column) {
				return cellRenderer;
			}
		};
		logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		logTable.getSelectionModel().addListSelectionListener(this);
		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(
				logTable), new JScrollPane(detailView));
		pane.setDividerLocation(settings.getInt(GUI_LOG_DIVIDERLOCATION));
		add(pane, BorderLayout.CENTER);

		final Thread logThread = new Thread() {
			@Override
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
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		buttonPanel.add(clearButton);
		add(buttonPanel, BorderLayout.PAGE_END);
	}

	@Override
	public void close() {
		running = false;
		settings.set(GUI_LOG_DIVIDERLOCATION, pane.getDividerLocation());
	}

	public void settingsChanged() {

	}

	private final class LogMessageEvent extends MessageEvent {

		private static final long serialVersionUID = -5690784906495393031L;

		private final Calendar datetime = Calendar.getInstance();

		private String typeFormatted;

		private int superType;

		public static final int TYPE_EVENT = 1;

		public static final int TYPE_WARNING = 2;

		public static final int TYPE_ERROR = 3;

		public LogMessageEvent(final MessageEvent event) {
			this(event.getType(), event.getMessage(), event.getChannelId());
		}

		public LogMessageEvent(final MessageEvent.MessageEventType type,
				final Object message, final String channelId) {
			super(type, message, channelId);
			switch (type) {
			case COMMAND:
				typeFormatted = "EVENT: incoming Command";
				superType = 1;
				break;
			case ENGINE:
				typeFormatted = "EVENT: Engine-Message";
				superType = 1;
				break;
			case ERROR:
				typeFormatted = "ERROR: unspecified Error";
				superType = 3;
				break;
			case PARSE_ERROR:
				typeFormatted = "ERROR: Parse-Error";
				superType = 3;
				break;
			case RESULT:
				typeFormatted = "EVENT: returned result";
				superType = 1;
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
			final StringBuilder res = new StringBuilder();
			res.append(datetime.get(Calendar.YEAR) + "/");
			res.append((datetime.get(Calendar.MONTH) + 1 > 9 ? "" : "0")
					+ (datetime.get(Calendar.MONTH) + 1) + "/");
			res.append((datetime.get(Calendar.DAY_OF_MONTH) > 9 ? "" : "0")
					+ datetime.get(Calendar.DAY_OF_MONTH) + " - ");
			res.append((datetime.get(Calendar.HOUR_OF_DAY) > 9 ? "" : "0")
					+ datetime.get(Calendar.HOUR_OF_DAY) + ":");
			res.append((datetime.get(Calendar.MINUTE) > 9 ? "" : "0")
					+ datetime.get(Calendar.MINUTE) + ":");
			res.append((datetime.get(Calendar.SECOND) > 9 ? "" : "0")
					+ datetime.get(Calendar.SECOND));
			return res.toString();
		}

		public String getTypeFormatted() {
			return typeFormatted;
		}

	}

	private final class LogTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -6649805279420707106L;

		private final Color colorError = Color.RED;

		private final Color colorWarning = Color.ORANGE;

		private final Color colorEvent = Color.BLUE;

		@Override
		public Component getTableCellRendererComponent(final JTable table,
				final Object value, final boolean isSelected,
				final boolean hasFocus, final int row, final int column) {
			final JComponent returnComponent = (JComponent) super
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

		private final List<LogMessageEvent> events = new LinkedList<LogMessageEvent>();

		private final int maxEventCount = 1000;

		private void addEvents(final List<MessageEvent> events) {

			logTable.getColumnModel().getColumn(0).setPreferredWidth(180);
			logTable.getColumnModel().getColumn(1).setPreferredWidth(180);
			logTable.getColumnModel().getColumn(2).setPreferredWidth(
					logTable.getWidth() - 360);
			for (final MessageEvent event : events) {
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
		public String getColumnName(final int column) {
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

		@Override
		public boolean isCellEditable(final int row, final int col) {
			return false;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Class getColumnClass(final int aColumn) {
			return java.lang.String.class;
		}

		public int getRowCount() {
			return events.size();
		}

		public LogMessageEvent getRow(final int row) {
			return events.get(events.size() - (row + 1));
		}

		public Object getValueAt(final int row, final int column) {
			final LogMessageEvent event = getRow(row);
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

	public void actionPerformed(final ActionEvent event) {
		if (event.getSource() == clearButton) {
			dataModel.clearEvents();
			detailView.setText("");
		}
	}

	public void valueChanged(final ListSelectionEvent arg0) {
		if (arg0.getSource() == logTable.getSelectionModel()) {
			final StringBuilder buffer = new StringBuilder();
			if (logTable.getSelectedRow() > -1) {
				final LogMessageEvent event = dataModel.getRow(logTable
						.getSelectedRow());
				buffer.append("Date-Time:    " + event.getDatetimeFormatted()
						+ "\nChannel:      " + event.getChannelId()
						+ "\nMessage-Type: " + event.getTypeFormatted()
						+ "\n\nMessage:\n========\n");
				final Object message = event.getMessage();
				if (message instanceof Exception) {
					Throwable ex = (Throwable) message;
					buffer.append("List of Exception messages:\n");
					while (ex.getCause() != null) {
						if (ex.getMessage() != null) {
							buffer.append("\n- ").append(ex.getMessage());
						}
						ex = ex.getCause();
					}
					buffer.append("\n- ").append(ex.getMessage());
					buffer.append("\n\nStacktrace of innermost cause:\n\n");
					final StackTraceElement[] str = ex.getStackTrace();
					buffer.append(ex.getClass().getName()).append("\n");
					for (final StackTraceElement strelem : str) {
						buffer.append("\n").append(strelem);
					}
					// boolean first = true;
					// do {
					// if (!first) {
					// buffer.append("\n\ncaused by:\n");
					// }
					// StackTraceElement[] str = ex.getStackTrace();
					// buffer.append(ex.getClass().getName() + ": "
					// + ex.getMessage());
					// for (StackTraceElement strelem : str) {
					// buffer.append("\n").append(strelem);
					// }
					// first = false;
					// } while ((ex = ex.getCause()) != null);
				} else if (message instanceof Expression) {
					buffer.append(((Expression) message).format(ParserFactory
							.getFormatter(true)));
				} else if (message != null) {
					buffer.append(message.toString());
				}
			}
			detailView.setText(buffer.toString());
			detailView.setCaretPosition(0);
		}
	}
}
