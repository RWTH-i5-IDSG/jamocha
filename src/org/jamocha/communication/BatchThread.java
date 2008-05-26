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

package org.jamocha.communication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jamocha.application.gui.JamochaGui;
import org.jamocha.communication.events.MessageEvent;
import org.jamocha.communication.messagerouter.StringChannel;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Engine;

/**
 * @author Alexander Wilden
 * 
 * This Thread handles batch processes in the background. These processes are
 * started via the command line parameter -batch or the "File -> batch file"
 * Option in the Menu of the GUI.
 */
public class BatchThread extends Thread {

	private final Engine engine;

	private JamochaGui gui = null;

	private boolean running = true;

	private final StringChannel batchChannel;

	Queue<String> batchFiles = new ConcurrentLinkedQueue<String>();

	private final Map<String, String> batchResults = new HashMap<String, String>();

	public BatchThread(Engine engine) {
		this.engine = engine;
		batchChannel = engine.getMessageRouter().openChannel("batch_channel");
	}

	@Override
	public void run() {
		StringBuilder buffer = new StringBuilder();
		while (running) {
			List<MessageEvent> messages = new ArrayList<MessageEvent>();
			getBatchChannel().fillEventList(messages);
			if (!messages.isEmpty())
				for (MessageEvent event : messages) {
					if (event.getType() == MessageEvent.MessageEventType.ERROR)
						buffer.append(JamochaGui.exceptionToString(
								(Exception) event.getMessage()).trim()
								+ System.getProperty("line.separator"));
					if (event.getType() != MessageEvent.MessageEventType.COMMAND
							&& event.getMessage() != null
							&& !event.getMessage().toString().equals("")
							&& !event.getMessage().equals(JamochaValue.NIL))
						buffer.append(event.getMessage().toString().trim()
								+ System.getProperty("line.separator"));
					if (event.getType() == MessageEvent.MessageEventType.PARSE_ERROR
							|| event.getType() == MessageEvent.MessageEventType.ERROR
							|| event.getType() == MessageEvent.MessageEventType.RESULT) {
						batchResults.put(batchFiles.poll(), buffer.toString());
						buffer = new StringBuilder();
						if (gui != null)
							gui.informOfNewBatchResults();
					}
				}
			else
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// silently ignore it
				}
		}
		engine.getMessageRouter().closeChannel(batchChannel);
	}

	public void setGui(JamochaGui gui) {
		this.gui = gui;
	}

	public void stopThread() {
		running = false;
	}

	public Map<String, String> getBatchResults() {
		return batchResults;
	}

	public StringChannel getBatchChannel() {
		return batchChannel;
	}

	public void processBatchFiles(List<String> files) {
		if (files != null)
			if (!files.isEmpty())
				for (String file : files) {
					getBatchChannel().executeCommand("(batch " + file + ")");
					batchFiles
							.offer(file + " (" + getDatetimeFormatted() + ")");
				}
	}

	public String getDatetimeFormatted() {
		StringBuilder res = new StringBuilder();
		Calendar datetime = Calendar.getInstance();
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

}
