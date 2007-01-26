/**
 * Copyright 2007 Karl-Heinz Krempels, Alexander Wilden
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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;

import org.jamocha.gui.ClipboardUtil;
import org.jamocha.gui.JamochaGui;
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.StreamChannel;
import org.jamocha.rete.Constants;

/**
 * This class provides a panel with a command line interface to Jamocha.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class ShellPanel extends AbstractJamochaPanel implements ActionListener,
		FocusListener, AdjustmentListener {

	private static final long serialVersionUID = 1777454004380892575L;

	/**
	 * Flag for the ChannelListener and the eventThread to know if they should
	 * go on working.
	 */
	private boolean running = true;

	/**
	 * The Queue for incoming KeyEvents. We process them in an own Thread to
	 * prevent strange, concurrent behaviours.
	 */
	private Queue<KeyEvent> keyEventQueue = new ConcurrentLinkedQueue<KeyEvent>();

	/**
	 * The Area to display our keypresses and results from the engine.
	 */
	private JTextArea outputArea;

	/**
	 * The scrollpane for the outputArea.
	 */
	private JScrollPane scrollPane;

	private boolean ignoreScrollEvent = false;

	/**
	 * The Button that clears the console window.
	 */
	private JButton clearButton;

	/**
	 * The current, temporary offset inside the history.
	 */
	private int history_offset = 0;

	/**
	 * The maximum size of the history. The boundary is to prevent memory
	 * problems. A history of less than zero means unbounded history.
	 */
	private final int history_max_size = 100;

	/**
	 * A history limited to history_max_size entries.
	 */
	private List<String> history = new LinkedList<String>();

	private int lastPromptIndex = 0;

	//private static final String SHELL_CURSOR = "\u220E";

	private static final String SHELL_CURSOR = "_";
	
	private int cursorPosition = 0;

	private String cursorSubString = "";

	private Timer cursorTimer = new Timer(500, this);

	private boolean cursorShowing = false;

	/**
	 * The Shells channel to the Rete engine.
	 */
	private StreamChannel channel;

	/**
	 * The Writer we use to write to the channel via a PipedIn(Out)putStream.
	 */
	private Writer outWriter;

	/**
	 * A Buffer for the last command, that wasn't send to the engine in total.
	 */
	private StringBuilder lastIncompleteCommand = new StringBuilder();

	/**
	 * The Thread listening for results from the Rete engine on our channel.
	 */
	private Thread channelListener;

	/**
	 * The main constructor for a ShellPanel.
	 * 
	 * @param engine
	 *            The Jamocha engine that should be used with this GUI.
	 */
	public ShellPanel(JamochaGui gui) {
		super(gui);
		// GUI construction
		// create the output area
		outputArea = new JTextArea();
		outputArea.setEditable(false);
		outputArea.setLineWrap(true);
		outputArea.setWrapStyleWord(true);
		outputArea.addFocusListener(this);
		// set the font and the colors
		settingsChanged();
		this.addFocusListener(this);
		// create a scroll pane to embedd the output area
		scrollPane = new JScrollPane(outputArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(this);
		// Assemble the GUI
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

		// create the button that clears the output area
		clearButton = new JButton("Clear Shell", IconLoader
				.getImageIcon("application_osx_terminal"));
		clearButton.addActionListener(this);
		JPanel clearButtonPanel = new JPanel();
		clearButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
		clearButtonPanel.add(clearButton);
		add(clearButtonPanel, BorderLayout.PAGE_END);

		// initialize the channel to the engine
		PipedOutputStream outStream = new PipedOutputStream();
		PipedInputStream inStream = new PipedInputStream();
		outWriter = new PrintWriter(outStream);
		try {
			inStream.connect(outStream);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		channel = gui.getEngine().getMessageRouter().openChannel("JamochaGui",
				inStream);

		printPrompt();
		moveCursorToEnd();
		startTimer();

		// initialize the channellistener for outputs from the engine
		initChannelListener();

		// initialize the keylistener for key events
		initKeyListener();

		// initialize the mouselistener for the context menu
		initPopupMenu();
	}

	/**
	 * Prints the prompt in the outputArea.
	 * 
	 */
	private synchronized void printPrompt() {
		outputArea.append(Constants.SHELL_PROMPT);
		lastPromptIndex = getOffset();
		outputArea.setCaretPosition(outputArea.getDocument().getLength());
	}

	private synchronized void printMessage(String message, boolean lineBreak) {
		if (lineBreak) {
			message += System.getProperty("line.separator");
		}
		outputArea.insert(message, cursorPosition);

		cursorPosition = cursorPosition + message.length();
		if (lineBreak) {
			lastPromptIndex = getOffset();
		}
	}

	private synchronized void moveCursorToEnd() {
		moveCursorTo(getOffset());
	}

	private synchronized void moveCursorTo(int newPosition) {
		hideCursor();
		cursorPosition = newPosition;
		showCursor();
	}

	private synchronized void showCursor() {
		if (!cursorShowing) {
			int currOffset = getOffset();
			if (currOffset >= (cursorPosition + SHELL_CURSOR.length())) {
				try {
					cursorSubString = outputArea.getText(cursorPosition,
							SHELL_CURSOR.length());
				} catch (BadLocationException e) {
					// Shouldn't happen
					e.printStackTrace();
				}
				outputArea.replaceRange(SHELL_CURSOR, cursorPosition,
						cursorPosition + cursorSubString.length());
			} else {
				int offset = currOffset - cursorPosition;
				if (offset < 0)
					offset = 0;
				try {
					cursorSubString = outputArea
							.getText(cursorPosition, offset);
				} catch (BadLocationException e) {
					// Shouldn't happen
					e.printStackTrace();
				}
				outputArea.replaceRange(SHELL_CURSOR, cursorPosition,
						cursorPosition + offset);
			}
		}
		cursorShowing = true;
	}

	private synchronized void hideCursor() {
		if (cursorShowing) {
			try {
				outputArea.replaceRange(cursorSubString, cursorPosition,
						cursorPosition + SHELL_CURSOR.length());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		cursorShowing = false;
	}

	private synchronized void removeChar() {
		stopTimer();
		hideCursor();
		outputArea.replaceRange("", cursorPosition - 1, cursorPosition);
		cursorPosition--;
		showCursor();
		startTimer();
	}

	private synchronized void removeLine() {
		outputArea.replaceRange("", lastPromptIndex, getOffset());
		cursorPosition = getOffset();
	}

	private synchronized void startTimer() {
		if (!cursorTimer.isRunning())
			cursorTimer.start();
	}

	private synchronized void stopTimer() {
		cursorTimer.stop();
	}

	/**
	 * Initializes and starts the ChannelListener that waits for results or
	 * errors from the Jamocha engine.
	 * 
	 */
	private void initChannelListener() {
		channelListener = new Thread() {

			/**
			 * Simply runs the ChannelListener and lets it process Events.
			 */
			public void run() {
				List<MessageEvent> msgEvents = new ArrayList<MessageEvent>();
				boolean printPrompt = false;

				while (running) {
					channel.fillEventList(msgEvents);
					if (!msgEvents.isEmpty()) {
						stopTimer();
						StringBuilder buffer = new StringBuilder();
						for (MessageEvent event : msgEvents) {
							if (event.getType() == MessageEvent.PARSE_ERROR
									|| event.getType() == MessageEvent.ERROR
									|| event.getType() == MessageEvent.RESULT) {

								printPrompt = true;
								lastIncompleteCommand = new StringBuilder();
							}
							if (event.getType() == MessageEvent.ERROR) {
								buffer.append(exceptionToString(
										(Exception) event.getMessage()).trim()
										+ System.getProperty("line.separator"));
							}
							if (event.getType() != MessageEvent.COMMAND
									&& !event.getMessage().toString()
											.equals("")) {
								buffer.append(event.getMessage().toString()
										.trim()
										+ System.getProperty("line.separator"));
							}
						}
						msgEvents.clear();
						hideCursor();
						printMessage(buffer.toString().trim(), true);
						if (printPrompt) {
							printPrompt();
							moveCursorTo(lastPromptIndex);
						}
						showCursor();
						ignoreScrollEvent = true;
						printPrompt = false;
						startTimer();
					} else {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// Can be ignored
						}
					}
				}
				try {
					outWriter.close();
				} catch (IOException e) {
					// we silently ignore it
					e.printStackTrace();
				}
				gui.getEngine().getMessageRouter().closeChannel(channel);
			}

			/**
			 * Converts an Exception to a String namely turns the StackTrace to
			 * a String.
			 * 
			 * @param exception
			 *            The Exception
			 * @return A nice String representation of the Exception
			 */
			private String exceptionToString(Exception exception) {
				StringBuilder res = new StringBuilder();
				StackTraceElement[] str = exception.getStackTrace();
				for (int i = 0; i < str.length; ++i) {
					res.append(str[i] + System.getProperty("line.separator"));
				}
				return res.toString();
			}
		};
		channelListener.start();
	}

	/**
	 * Initializes the KeyListener to catch all Keypresses.
	 * 
	 */
	private void initKeyListener() {
		KeyAdapter adapter = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				keyEventQueue.offer(e);
			}
		};
		addKeyListener(adapter);
		outputArea.addKeyListener(adapter);

		Thread eventThread = new Thread() {

			public void run() {
				while (running) {
					if (!keyEventQueue.isEmpty()) {
						KeyEvent e = keyEventQueue.poll();
						int delta = 1;
						switch (e.getKeyCode()) {
						case KeyEvent.VK_DOWN:
						case KeyEvent.VK_KP_DOWN:
							delta = -1;
						case KeyEvent.VK_UP:
						case KeyEvent.VK_KP_UP:
							stopTimer();
							hideCursor();
							// Here we walk through the history
							history_offset += delta;
							if (history_offset <= 0) {
								history_offset = 0;
								if (lastPromptIndex < getOffset()) {
									removeLine();
								}
							} else {
								if (history_offset > history.size()) {
									history_offset = history.size();
								}
								if (lastPromptIndex < getOffset()) {
									removeLine();
								}
								int index = history.size() - history_offset;
								if (index >= 0 && history.size() > 0) {
									String tmp = history.get(index);
									printMessage(tmp, false);
								}
							}
							moveCursorToEnd();
							startTimer();
							break;
						case KeyEvent.VK_ENTER:
							stopTimer();
							hideCursor();
							if (lastPromptIndex < getOffset()) {
								String currLine = "";
								try {
									try {
										currLine = outputArea.getText(
												lastPromptIndex, getOffset()
														- lastPromptIndex);
									} catch (BadLocationException e1) {
										e1.printStackTrace();
									}
									lastIncompleteCommand
											.append(currLine
													+ System
															.getProperty("line.separator"));
									if (currLine.length() > 0) {
										addToHistory(currLine);
										outWriter.write(currLine);
										outWriter.flush();
									}
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
							printMessage("", true);
							moveCursorToEnd();
							startTimer();
							break;
						case KeyEvent.VK_BACK_SPACE:
							if (cursorPosition > lastPromptIndex) {
								removeChar();
							}
							break;
						// Moving the Cursor in the current line
						case KeyEvent.VK_RIGHT:
						case KeyEvent.VK_KP_RIGHT:
							if (!e.isShiftDown()) {
								stopTimer();
								hideCursor();
								if (cursorPosition < getOffset()) {
									moveCursorTo(cursorPosition + 1);
								}
								showCursor();
								startTimer();
							}
							break;

						case KeyEvent.VK_LEFT:
						case KeyEvent.VK_KP_LEFT:
							if (!e.isShiftDown()) {
								stopTimer();
								hideCursor();
								if (cursorPosition > lastPromptIndex) {
									moveCursorTo(cursorPosition - 1);
								}
								showCursor();
								startTimer();
							}
							break;
						// ignore special keys
						case KeyEvent.VK_ALT:
						case KeyEvent.VK_CONTROL:
						case KeyEvent.VK_META:
						case KeyEvent.VK_SHIFT:
							break;
						default:
							if (!e.isControlDown() && !e.isMetaDown()) {
								// simple character
								printMessage(String.valueOf(e.getKeyChar()),
										false);
							} else {
								// paste from clipboard
								if (e.getKeyChar() == 'v'
										|| e.getKeyCode() == KeyEvent.VK_V) {
									String clipContent = ClipboardUtil
											.getInstance()
											.getClipboardContents();
									if (clipContent != null) {
										printMessage(clipContent, false);
									}
								}
								// copy to clipboard
								else if (e.getKeyChar() == 'c'
										|| e.getKeyCode() == KeyEvent.VK_C) {
									ClipboardUtil.getInstance()
											.setClipboardContents(
													outputArea
															.getSelectedText());
								}
							}
							break;
						}
						e.consume();
						ignoreScrollEvent = true;
						startTimer();
					} else {
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// ignored
						}
					}
				}
			}
		};
		eventThread.start();
	}

	/**
	 * A function that safely adds single lines to the history. If a String
	 * contains more than one line it will be splitted line by line.
	 * 
	 * If the history size is greater than history_max_size we remove elements
	 * that are old and too much.
	 * 
	 * @param historyString
	 *            The String that should be added to the history.
	 */
	private void addToHistory(String historyString) {
		String[] lines = historyString.split(System
				.getProperty("line.separator"));
		for (int i = 0; i < lines.length; ++i) {
			if (!lines[i].equals("")) {
				history.add(lines[i]);
			}
		}
		// remove items as long as there are too much of them
		// and a valid history_max_size is set
		while (history.size() > history_max_size && history_max_size >= 0) {
			history.remove(0);
		}
		// reset the history index after a command
		history_offset = 0;
	}

	/**
	 * initializing the contextmenu.
	 * 
	 */
	private void initPopupMenu() {

		JPopupMenu menu = new JPopupMenu();
		JMenuItem copyMenu = new JMenuItem("Copy", IconLoader
				.getImageIcon("page_copy"));
		copyMenu.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				ClipboardUtil.getInstance().setClipboardContents(
						outputArea.getSelectedText());
			}
		});
		JMenuItem pasteMenu = new JMenuItem("Paste", IconLoader
				.getImageIcon("paste_plain"));
		pasteMenu.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				String clipContent = ClipboardUtil.getInstance()
						.getClipboardContents();
				if (clipContent != null) {
					printMessage(clipContent, false);
				}
			}
		});
		JMenuItem selectCommandMenu = new JMenuItem("Select current line");
		selectCommandMenu.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				outputArea.setSelectionStart(lastPromptIndex);
				outputArea.setSelectionEnd(getOffset());
			}
		});
		JMenuItem selectAllMenu = new JMenuItem("Select all");
		selectAllMenu.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				outputArea.setSelectionStart(0);
				outputArea.setSelectionEnd(getOffset());
			}
		});
		menu.add(copyMenu);
		menu.add(pasteMenu);
		menu.addSeparator();
		menu.add(selectCommandMenu);
		menu.add(selectAllMenu);
		outputArea.setComponentPopupMenu(menu);

	}

	/**
	 * Clears the outputArea and sets a new prompt. If parts of a command are
	 * already in the channel these commandparts will be printed again.
	 * 
	 */
	private synchronized void clearArea() {
		stopTimer();
		hideCursor();
		outputArea.setText("");
		lastPromptIndex = 0;
		cursorPosition = 0;
		if (lastIncompleteCommand.length() > 0) {
			printPrompt();
			cursorPosition = getOffset();
			printMessage(lastIncompleteCommand.toString().trim(), true);
		} else
			printPrompt();
		moveCursorToEnd();
		setFocus();
		startTimer();
	}

	/**
	 * Returns the offset of the downmost line in the outputArea.
	 * 
	 * @return The total Offset.
	 */
	private synchronized int getOffset() {
		try {
			return outputArea.getLineEndOffset(outputArea.getLineCount() - 1);
		} catch (BadLocationException e1) {
			// This should never happen as we have at least one line
		}
		return 0;
	}

	/**
	 * Sets the focus of this panel and by this sets the focus to the outputArea
	 * so that the user doesn't have to click on it before he can start typing.
	 * 
	 */
	@Override
	public void setFocus() {
		super.setFocus();
		ignoreScrollEvent = true;
	}

	/**
	 * Close this Panel.
	 * 
	 */
	@Override
	public void close() {
		stopTimer();
		running = false;
	}

	public void settingsChanged() {
		outputArea
				.setFont(new Font(gui.getPreferences().get("shell.font",
						"Courier"), gui.getPreferences().getInt(
						"shell.fontstyle", Font.PLAIN), gui.getPreferences()
						.getInt("shell.fontsize", 12)));
		outputArea.setBackground(new Color(gui.getPreferences().getInt(
				"shell.backgroundcolor", Color.BLACK.getRGB())));
		outputArea.setForeground(new Color(gui.getPreferences().getInt(
				"shell.fontcolor", Color.WHITE.getRGB())));
		outputArea.setBorder(BorderFactory.createLineBorder(outputArea
				.getBackground(), 2));
	}

	/**
	 * Catches events for Buttons and the Timer in this Panel.
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(clearButton)) {
			clearArea();
		} else if (event.getSource().equals(cursorTimer)) {
			if (cursorShowing) {
				hideCursor();
			} else {
				showCursor();
			}
		}
	}

	public void focusGained(FocusEvent event) {
		if (event.getSource() == this || event.getSource() == outputArea) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					showCursor();
					startTimer();
				}
			});
		}
	}

	public void focusLost(FocusEvent event) {
		if (event.getSource() == this || event.getSource() == outputArea) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					stopTimer();
					hideCursor();
				}
			});
		}
	}

	public void adjustmentValueChanged(AdjustmentEvent event) {
		if (!ignoreScrollEvent)
			stopTimer();
		ignoreScrollEvent = false;
	}

}
