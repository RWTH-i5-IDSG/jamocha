/*
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

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;

import org.jamocha.Constants;
import org.jamocha.gui.ClipboardUtil;
import org.jamocha.gui.JamochaGui;
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.StreamChannel;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.functions.Function;
import org.jamocha.settings.SettingsChangedListener;
import org.jamocha.settings.SettingsConstants;

/**
 * This class provides a panel with a command line interface to Jamocha.
 * 
 * @author Karl-Heinz Krempels <krempels@cs.rwth-aachen.de>
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class ShellPanel extends AbstractJamochaPanel implements ActionListener,
		AdjustmentListener, SettingsChangedListener {

	private static final long serialVersionUID = 1777454004380892575L;

	private AutoCompletion autoCompletion;

	private AutoCompletionBox autoCompletionBox;

	private String autoCompletionPrefix;

	private boolean enableAutoCompletion = false;

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

	/**
	 * The line the user currently wrote something to.
	 */
	private String history_activeline = "";

	/**
	 * The last position of the Prompt or in case of a new line the position of
	 * the beginning of the new line.
	 */
	private int lastPromptIndex = 0;

	/**
	 * The Symbol for the blinking Cursor.
	 */
	private static final String SHELL_CURSOR = "_";

	/**
	 * The current position of the Cursor inside of the outputArea.
	 */
	private int cursorPosition = 0;

	/**
	 * The String "below" the Cursor at the current Position.
	 */
	private String cursorSubString = "";

	/**
	 * The Timer that makes the Cursor blinking. Set a smaller delay (first
	 * parameter) to let it blink faster.
	 */
	private Timer cursorTimer = new Timer(400, this);

	/**
	 * A Flag indicating if the Shell currently shows the Cursor or the
	 * cursorSubString.
	 */
	private boolean cursorShowing = false;

	/**
	 * last position of the scrollbar
	 */
	private int lastScrollBarPosition = 0;

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

	private boolean channelListenerPaused = false;

	private String[] interestedSettings = { 
			SettingsConstants.GUI_SHELL_FONT,
			SettingsConstants.GUI_SHELL_FONTSIZE,
			SettingsConstants.GUI_SHELL_FONTCOLOR,
			SettingsConstants.GUI_SHELL_BACKGROUNDCOLOR,
			SettingsConstants.GUI_SHELL_AUTOCOMPLETION };

	private void initAutoCompletion() {
		autoCompletion.addToken("multislot");
		autoCompletion.addToken("slot");
		for (Function function : gui.getEngine().getFunctionMemory()
				.getAllFunctions()) {
			StringBuilder longName = new StringBuilder();
			longName.append(function.getName());
			for (int i = 0; i < function.getDescription().getParameterCount(); i++) {
				longName.append(" ").append(
						function.getDescription().getParameterName(i).replace(
								" ", "_"));
			}
			longName.append(")");
			autoCompletion.addToken(function.getName(), longName.toString());
		}
	}

	/**
	 * The main constructor for a ShellPanel.
	 * 
	 * @param engine
	 *            The Jamocha engine that should be used with this GUI.
	 */
	public ShellPanel(JamochaGui gui) {
		super(gui);
		autoCompletion = new AutoCompletion();
		autoCompletionBox = new AutoCompletionBox(gui, autoCompletion, this);
		// GUI construction
		// create the output area
		outputArea = new JTextArea() {
			private static final long serialVersionUID = 1L;

			public Set<AWTKeyStroke> getFocusTraversalKeys(int id) {
				if (id == KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS
						|| id == KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS) {
					return Collections.emptySet();
				} else
					return super.getFocusTraversalKeys(id);
			}
		};
		outputArea.setEditable(false);
		outputArea.setLineWrap(true);
		outputArea.setWrapStyleWord(true);

		// create a scroll pane to embed the output area
		scrollPane = new JScrollPane(outputArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(this);
		scrollPane.setAutoscrolls(false);
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
		initChannel();

		initAutoCompletion();

		printPrompt();
		moveCursorToEnd();
		showCursor();
		startTimer();

		// initialize the channellistener for outputs from the engine
		initChannelListener();

		// initialize the keylistener for key events
		initKeyListener();

		// initialize the mouselistener for the context menu
		initPopupMenu();

		settings.addListener(this, interestedSettings);
	}

	private void initChannel() {
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
	}

	/**
	 * Prints the prompt in the outputArea.
	 * 
	 */
	private synchronized void printPrompt() {
		outputArea.append(Constants.SHELL_PROMPT);
		lastPromptIndex = getOffset();
		scrollToCursor();
	}

	/**
	 * Prints a messge at the current cursorPosition in the outputArea.
	 * 
	 * @param message
	 *            The message to print.
	 * @param lineBreak
	 *            If true a linebreak is added at the end.
	 */
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

	/**
	 * Moves the Cursor to the End of the outputArea.
	 * 
	 */
	private synchronized void moveCursorToEnd() {
		if (cursorPosition != getOffset())
			moveCursorTo(getOffset());
	}

	/**
	 * Moves the Cursor to the specified Position.
	 * 
	 * @param newPosition
	 *            The new Position for the Cursor.s
	 */
	private synchronized void moveCursorTo(int newPosition) {
		cursorPosition = newPosition;
	}

	/**
	 * Shows the Cursor if it was previously hiding and otherwise does nothing.
	 * 
	 */
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

	/**
	 * Hide the Cursor if it was previously visible and otherwise does nothing.
	 * 
	 */
	private synchronized void hideCursor() {
		if (cursorShowing) {
			try {
				outputArea.replaceRange(cursorSubString, cursorPosition,
						cursorPosition + SHELL_CURSOR.length());
			} catch (Exception e) {
				// ignore it
			}
		}
		cursorShowing = false;
	}

	/**
	 * Removes a Character on the left side of the Cursor.
	 * 
	 */
	private synchronized void removeCharLeft() {
		outputArea.replaceRange("", cursorPosition - 1, cursorPosition);
		cursorPosition--;
	}

	/**
	 * Removes a Character on the right side (below) of the Cursor.
	 * 
	 */
	private synchronized void removeCharRight() {
		outputArea.replaceRange("", cursorPosition, cursorPosition + 1);
	}

	/**
	 * Removes the current line completely.
	 * 
	 */
	private synchronized void removeLine() {
		outputArea.replaceRange("", lastPromptIndex, getOffset());
		cursorPosition = getOffset();
	}

	/**
	 * Starts the cursorTimer.
	 * 
	 */
	private synchronized void startTimer() {
		if (!cursorTimer.isRunning())
			cursorTimer.start();
	}

	/**
	 * Pauses the cursorTimer.
	 * 
	 */
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
					if (!channelListenerPaused) {
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
									String msg = ((Exception) event
											.getMessage()).getMessage();
									if (msg == null) {
										msg = "An unknown error occured. Please check the Log.";
									}
									buffer
											.append(msg.trim()
													+ System
															.getProperty("line.separator"));
								}
								if (event.getType() != MessageEvent.COMMAND
										&& event.getMessage() != null
										&& !event.getMessage().toString()
												.equals("")
										&& !event.getMessage().equals(
												JamochaValue.NIL)) {
									buffer
											.append(event.getMessage()
													.toString().trim()
													+ System
															.getProperty("line.separator"));
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
							printPrompt = false;
							startTimer();
						} else {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// Can be ignored
							}
						}
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
				e.consume();
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
						stopTimer();
						hideCursor();
						switch (e.getKeyCode()) {
						case KeyEvent.VK_DOWN:
						case KeyEvent.VK_KP_DOWN:
							if (autoCompletionBox.isVisible()) {
								autoCompletionBox.down();
								break;
							}
							delta = -1;
						case KeyEvent.VK_UP:
						case KeyEvent.VK_KP_UP:
							if (autoCompletionBox.isVisible()) {
								autoCompletionBox.up();
							} else {

								// Here we walk through the history
								int old_offset = history_offset;
								history_offset += delta;
								if (history_offset <= 0) {
									history_offset = 0;
									if (lastPromptIndex < getOffset()) {
										removeLine();
									}
									printMessage(history_activeline, false);
								} else {
									if (history_offset > history.size()) {
										history_offset = history.size();
									}
									// save the currently typed stuff
									if (delta == 1 && old_offset < 1) {
										String currLine = "";
										try {
											currLine = outputArea.getText(
													lastPromptIndex,
													getOffset()
															- lastPromptIndex);
										} catch (BadLocationException e1) {
											e1.printStackTrace();
										}
										history_activeline = currLine;
									}
									if (lastPromptIndex < getOffset()
											&& history.size() > 0) {
										removeLine();
									}
									int index = history.size() - history_offset;
									if (index >= 0 && history.size() > 0) {

										String tmp = history.get(index);
										printMessage(tmp, false);
									}

								}
								moveCursorToEnd();
								scrollToCursor();
							}
							break;
						case KeyEvent.VK_ENTER:
							if (autoCompletionBox.isVisible()) {
								autoCompletionBox.keyPressed(e);
							} else {

								moveCursorToEnd();

								if (lastPromptIndex < getOffset()) {
									String currLine = "";
									try {
										try {
											currLine = outputArea.getText(
													lastPromptIndex,
													getOffset()
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
								history_activeline = "";
								printMessage("", true);
								moveCursorToEnd();
								scrollToCursor();
							}
							break;
						// delete a char on the left side of the cursor
						case KeyEvent.VK_BACK_SPACE:
							if (cursorPosition > lastPromptIndex) {
								removeCharLeft();
							}
							scrollToCursor();
							break;
						// delete a char on the right side of the cursor
						case KeyEvent.VK_DELETE:
							if (cursorPosition < getOffset()) {
								removeCharRight();
							}
							scrollToCursor();
							break;
						// Moving the Cursor in the current line
						case KeyEvent.VK_RIGHT:
						case KeyEvent.VK_KP_RIGHT:

							if (!e.isShiftDown()) {
								if (cursorPosition < getOffset()) {
									moveCursorTo(cursorPosition + 1);
								}
								scrollToCursor();
							}
							break;

						case KeyEvent.VK_LEFT:
						case KeyEvent.VK_KP_LEFT:
							if (!e.isShiftDown()) {
								if (cursorPosition > lastPromptIndex) {
									moveCursorTo(cursorPosition - 1);
								}
								scrollToCursor();
							}
							break;
						case KeyEvent.VK_TAB:
							handleFindPath();
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
								scrollToCursor();
							} else {
								// paste from clipboard
								if (e.getKeyChar() == 'v'
										|| e.getKeyCode() == KeyEvent.VK_V) {
									String clipContent = ClipboardUtil
											.getInstance()
											.getClipboardContents();
									if (clipContent != null) {
										printMessage(clipContent, false);
										scrollToCursor();
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
						// e.consume();
						showCursor();
						handleAutoCompletion();
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

	protected void handleAutoCompletion() {
		if (!enableAutoCompletion)
			return;
		int i = 0;
		Vector<String> suggestions = null;
		try {
			while (outputArea.getText().charAt(cursorPosition - i) != '('
					&& outputArea.getText().charAt(cursorPosition - i) != ' ')
				i++;
			if (outputArea.getText().charAt(cursorPosition - i) == '(') {
				autoCompletionPrefix = outputArea.getText(cursorPosition - i
						+ 1, i - 1);
				suggestions = autoCompletion
						.getAllBeginningWith(autoCompletionPrefix);
			} else {
				suggestions = new Vector<String>();
			}
		} catch (Exception e) {
			suggestions = new Vector<String>();
		}

		if (!suggestions.isEmpty()) {
			Caret c = outputArea.getCaret();
			DefaultCaret bc = (DefaultCaret) c;
			int x = bc.x + outputArea.getLocationOnScreen().x;
			int y = bc.y + bc.height + outputArea.getLocationOnScreen().y;
			autoCompletionBox.show(suggestions, x, y);

		} else {
			autoCompletionBox.hide();
		}

	}

	protected void handleFindPath() {
		String currLine = "";
		try {
			currLine = outputArea.getText(lastPromptIndex, getOffset()
					- lastPromptIndex);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		int offset = currLine.lastIndexOf(" ") + 1;
		currLine = currLine.substring(offset);
		offset = currLine.lastIndexOf(File.separator) + 1;
		File path = new File("." + File.separator);
		String remainder = currLine.substring(offset, currLine.length());
		if (remainder.length() > 0) {
			if (offset > 0) {
				path = new File(currLine.substring(0, offset));
			}
			File[] children = path.listFiles();
			if (children != null) {
				String childName;
				for (File child : children) {
					childName = child.getName();
					if (childName.startsWith(remainder)) {
						if (child.isDirectory())
							childName += File.separator;
						printMessage(childName.substring(remainder.length()),
								false);
						scrollToCursor();
						return;
					}
				}
			}
		}
	}

	void scrollToCursor() {
		outputArea.setCaretPosition(cursorPosition);
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
		JMenuItem clearLineMenu = new JMenuItem("Clear Line");
		clearLineMenu.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				removeLine();
			}
		});
		JMenuItem clearShellMenu = new JMenuItem("Clear Shell");
		clearShellMenu.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				clearArea();
			}
		});
		JMenuItem resetShellMenu = new JMenuItem("Reset Shell");
		resetShellMenu.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				stopTimer();
				hideCursor();
				channelListenerPaused = true;
				lastIncompleteCommand = new StringBuilder();
				clearArea();
				gui.getEngine().getMessageRouter().closeChannel(channel);
				initChannel();
				channelListenerPaused = false;
				showCursor();
				startTimer();
			}
		});
		menu.add(copyMenu);
		menu.add(pasteMenu);
		menu.addSeparator();
		menu.add(selectCommandMenu);
		menu.add(selectAllMenu);
		menu.addSeparator();
		menu.add(clearLineMenu);
		menu.add(clearShellMenu);
		menu.addSeparator();
		menu.add(resetShellMenu);
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
		lastScrollBarPosition = 0;
		if (lastIncompleteCommand.length() > 0) {
			printPrompt();
			cursorPosition = getOffset();
			printMessage(lastIncompleteCommand.toString().trim(), true);
		} else
			printPrompt();
		moveCursorToEnd();
		setFocus();
		showCursor();
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

	public void adjustmentValueChanged(AdjustmentEvent event) {
		if (event.getValue() < lastScrollBarPosition)
			stopTimer();
		else {
			lastScrollBarPosition = event.getValue();
			startTimer();
		}
	}

	public void settingsChanged(String propertyName) {
		if (propertyName.startsWith(SettingsConstants.GUI_SHELL_FONT)) {
			outputArea.setFont(new Font(settings.getString(SettingsConstants.GUI_SHELL_FONT),
					settings.getInt(SettingsConstants.GUI_SHELL_FONTSTYLE), settings
							.getInt(SettingsConstants.GUI_SHELL_FONTSIZE)));
			outputArea.setForeground(new Color(settings
					.getInt(SettingsConstants.GUI_SHELL_FONTCOLOR)));
		} else if (propertyName.equals(SettingsConstants.GUI_SHELL_BACKGROUNDCOLOR)) {
			outputArea.setBackground(new Color(settings
					.getInt(SettingsConstants.GUI_SHELL_BACKGROUNDCOLOR)));
			outputArea.setBorder(BorderFactory.createLineBorder(outputArea
					.getBackground(), 2));
		} else if (propertyName.equals(SettingsConstants.GUI_SHELL_AUTOCOMPLETION)) {
			enableAutoCompletion = settings
					.getBoolean(SettingsConstants.GUI_SHELL_AUTOCOMPLETION);
		}
	}

	public String getAutoCompletionPrefix() {
		return autoCompletionPrefix;
	}

	public JTextArea getOutputArea() {
		return outputArea;
	}

	public int getCursorPosition() {
		return cursorPosition;
	}

	public void setCursorPosition(int cursorPosition) {
		this.cursorPosition = cursorPosition;
	}

}
