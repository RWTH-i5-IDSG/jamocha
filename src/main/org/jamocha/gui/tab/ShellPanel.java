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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		FocusListener {

	private static final long serialVersionUID = 1777454004380892575L;

	/**
	 * Flag for the ChannelListener to know if it should be running.
	 */
	private boolean running = true;

	/**
	 * The Area to display our keypresses and results from the engine.
	 */
	private JTextArea outputArea;

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
	 * This is the position to where it is possible to delete characters. This
	 * could either be the prompt itself or the beginning of a new line. Lines
	 * above the current line cannot be edited anymore because they are
	 * immediately send to the Rete engine.
	 */
	private int lastPromptIndex = 0;

	private final String[] promptEnds = { "|", "" };

	private final int promptDelay = 500;

	private Timer promptEndTimer;

	private int promptEndIndex = 0;

	private boolean hasFocus = false;

	/**
	 * The Shells channel to the Rete engine.
	 */
	private StreamChannel channel;

	/**
	 * The Writer we use to write to the channel via a PipedIn(Out)putStream.
	 */
	private Writer outWriter;

	/**
	 * Buffer for the current line content.
	 */
	private StringBuilder buffer = new StringBuilder();

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
		outputArea.setSize(600, 400);
		outputArea.setEditable(false);
		outputArea.setLineWrap(true);
		outputArea.setWrapStyleWord(true);
		outputArea.setMinimumSize(new Dimension(600, 400));
		outputArea.setFont(new Font("Courier", Font.PLAIN, 12));
		outputArea.setBackground(Color.BLACK);
		outputArea.setForeground(Color.WHITE);
		outputArea.setBorder(BorderFactory.createLineBorder(outputArea.getBackground(),2));
		this.addFocusListener(this);
		// create a scroll pane to embedd the output area
		JScrollPane scrollPane = new JScrollPane(outputArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBackground(Color.white);

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

		// initialize the channellistener for outputs from the engine
		initChannelListener();

		// initialize the keylistener for key events
		initKeyListener();

		// initialize the mouselistener for the context menu
		initPopupMenu();

		promptEndTimer = new Timer(promptDelay, this);
	}

	/**
	 * Prints the prompt in the outputArea.
	 * 
	 */
	private void printPrompt() {
		outputArea.append(Constants.SHELL_PROMPT);
		lastPromptIndex = getOffset();
		outputArea.setCaretPosition(outputArea.getDocument().getLength());
	}

	private synchronized void printPromptEnd() {
		outputArea.append(promptEnds[promptEndIndex]);
	}

	private synchronized void nextPromptEnd() {
		promptEndTimer.stop();
		removePromptEnd();
		promptEndIndex = (promptEndIndex + 1) % promptEnds.length;
		printPromptEnd();
		promptEndTimer.start();
	}

	private synchronized void removePromptEnd() {
		outputArea.replaceRange("", getOffsetWithoutPromptEnd(), getOffset());
	}

	/**
	 * Prints a new line in the outputArea using the systemspecific line
	 * separator.
	 * 
	 */
	private void printNewLine(boolean printPromptEnd) {
		outputArea.append(System.getProperty("line.separator"));
		if (printPromptEnd) {
			printPromptEnd();
		}
		lastPromptIndex = getOffset();
		outputArea.setCaretPosition(outputArea.getDocument().getLength());
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
				boolean firstEvent = true;
				printPrompt();

				while (running) {
					channel.fillEventList(msgEvents);
					if (!msgEvents.isEmpty()) {
						// delete the old promptEnd
						if (firstEvent) {
							outputArea.replaceRange("",
									getOffsetWithoutPromptEnd(), getOffset());
							firstEvent = false;
						}
						for (MessageEvent event : msgEvents) {
							if (event.getType() == MessageEvent.PARSE_ERROR
									|| event.getType() == MessageEvent.ERROR
									|| event.getType() == MessageEvent.RESULT) {

								printPrompt = true;
								lastIncompleteCommand = new StringBuilder();
							}
							if (event.getType() == MessageEvent.ERROR) {
								outputArea.append(exceptionToString(
										(Exception) event.getMessage()).trim());
								printNewLine(false);
							}
							if (event.getType() != MessageEvent.COMMAND
									&& !event.getMessage().toString()
											.equals("")) {
								outputArea.append(event.getMessage().toString()
										.trim());
								printNewLine(false);
							}
						}
						msgEvents.clear();
						if (printPrompt) {
							printPrompt();
							printPromptEnd();
							firstEvent = true;
						}
						printPrompt = false;
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
					// we silently ignore it bec
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
				int delta = 1;
				switch (e.getKeyCode()) {
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_KP_DOWN:
					delta = -1;
				case KeyEvent.VK_UP:
				case KeyEvent.VK_KP_UP:
					promptEndTimer.stop();
					// Here we walk through the history
					history_offset += delta;
					if (history_offset <= 0) {
						history_offset = 0;
						System.out.println(lastPromptIndex + " " + getOffset());
						if ((lastPromptIndex - 1) < getOffset()) {
							outputArea.replaceRange("", lastPromptIndex - 1,
									getOffset());
						}
						buffer = new StringBuilder();
					} else {
						if (history_offset > history.size()) {
							history_offset = history.size();
						}
						if ((lastPromptIndex - 1) < getOffset()) {
							outputArea.replaceRange("", lastPromptIndex - 1,
									getOffset());
						}
						int index = history.size() - history_offset;
						if (index >= 0 && history.size() > 0) {
							buffer = new StringBuilder(history.get(index));
							outputArea.insert(buffer.toString(), getOffset());
							printPromptEnd();
						}
					}
					promptEndTimer.start();
					break;
				case KeyEvent.VK_ENTER:
					// we don't add empty lines to the history
					if (buffer.length() > 0) {
						try {
							lastIncompleteCommand.append(buffer.toString()
									+ System.getProperty("line.separator"));
							outWriter.write(buffer.toString());
							outWriter.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						addToHistory(buffer.toString());
						// delete the old promptEnd
						removePromptEnd();
						printNewLine(true);
						buffer = new StringBuilder();
					}
					break;
				case KeyEvent.VK_BACK_SPACE:
					if (getOffsetWithoutPromptEnd() > lastPromptIndex) {
						// move the offset because of the promptEnd
						int offset = getOffsetWithoutPromptEnd();
						outputArea.replaceRange("", offset - 1, offset);
						buffer.deleteCharAt(buffer.length() - 1);
					}
					break;
				// ignore special keys
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_KP_RIGHT:
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_KP_LEFT:
				case KeyEvent.VK_ALT:
				case KeyEvent.VK_CONTROL:
				case KeyEvent.VK_META:
				case KeyEvent.VK_SHIFT:
					break;
				default:
					if (!e.isControlDown() && !e.isMetaDown()) {
						// simple character
						outputArea.insert("" + e.getKeyChar(),
								getOffsetWithoutPromptEnd());
						buffer.append(e.getKeyChar());
					} else {
						// paste from clipboard
						if (e.getKeyChar() == 'v'
								|| e.getKeyCode() == KeyEvent.VK_V) {
							String clipContent = ClipboardUtil.getInstance()
									.getClipboardContents();
							if (clipContent != null) {
								outputArea.insert(clipContent,
										getOffsetWithoutPromptEnd());
								buffer.append(clipContent);
							}
						}
						// copy to clipboard
						else if (e.getKeyChar() == 'c'
								|| e.getKeyCode() == KeyEvent.VK_C) {
							ClipboardUtil.getInstance().setClipboardContents(
									outputArea.getSelectedText());
						}
					}
					break;
				}
				e.consume();
			}

			/**
			 * A function that safely adds single lines to the history. If a
			 * String contains more than one line it will be splitted line by
			 * line.
			 * 
			 * If the history size is greater than history_max_size we remove
			 * elements that are old and too much.
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
				while (history.size() > history_max_size
						&& history_max_size >= 0) {
					history.remove(0);
				}
				// reset the history index after a command
				history_offset = 0;
			}
		};
		addKeyListener(adapter);
		outputArea.addKeyListener(adapter);
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
					outputArea.insert(clipContent, getOffsetWithoutPromptEnd());
					buffer.append(clipContent);
				}
			}
		});
		JMenuItem selectCommandMenu = new JMenuItem("Select current line");
		selectCommandMenu.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				outputArea.setSelectionStart(lastPromptIndex - 1);
				outputArea.setSelectionEnd(getOffsetWithoutPromptEnd());
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
	private void clearArea() {
		outputArea.replaceRange("", 0, getOffset());
		buffer = new StringBuilder();
		printPrompt();
		setFocus();
		if (lastIncompleteCommand.length() > 0) {
			// delete the old promptEnd
			outputArea.replaceRange("", getOffsetWithoutPromptEnd(),
					getOffset());
			outputArea.append(lastIncompleteCommand.toString().trim());
			printNewLine(false);
			printPrompt();
		}
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

	private synchronized int getOffsetWithoutPromptEnd() {
		return getOffset() - promptEnds[promptEndIndex].length();
	}

	/**
	 * Sets the focus of this panel and by this sets the focus to the outputArea
	 * so that the user doesn't have to click on it before he can start typing.
	 * 
	 */
	@Override
	public void setFocus() {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				ShellPanel.this.requestFocus();
			}

		});
	}

	/**
	 * Close this Panel. Here we stop the ChannelListener.
	 * 
	 */
	@Override
	public void close() {
		running = false;
	}

	/**
	 * Catches events for Buttons and the Timer in this Panel.
	 */
	public void actionPerformed(ActionEvent event) {
		//System.out.println(event);
		if (event.getSource().equals(clearButton)) {
			clearArea();
		} else if (event.getSource().equals(promptEndTimer)) {
			//System.out.println(outputArea.getCaretPosition() + "|" + getOffset());
			nextPromptEnd();
		}
	}

	public void focusGained(FocusEvent event) {
		if (!hasFocus) {
			printPromptEnd();
			promptEndTimer.start();
			hasFocus = true;
		}
	}

	public void focusLost(FocusEvent event) {
		if (hasFocus) {
			promptEndTimer.stop();
			removePromptEnd();
			hasFocus = false;
		}
	}

}
