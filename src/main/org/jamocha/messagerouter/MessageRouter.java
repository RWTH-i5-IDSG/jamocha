/**
 * Copyright 2006 Alexander Wilden, Christoph Emonds, Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.messagerouter;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Rete;

/**
 * A MessageRouter is responsible for sending messages to the Rete-engine and
 * receive the answers. Possible MessageListeners will be notified of all events
 * that occured.
 * 
 * @author Alexander Wilden, Christoph Emonds, Sebastian Reinartz
 */
public class MessageRouter implements Serializable {

	private static final long serialVersionUID = 1L;

	private final class CommandThread extends Thread {

		@Override
		public void run() {
			while (true) {
				if (commandQueue.isEmpty()) {
					try {
						sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					CommandObject schabau = commandQueue.poll();
					if (schabau != null) {
						currentChannelId = schabau.channelId;
						try {
							messageQueue.offer(new MessageEvent(
									MessageEvent.COMMAND, schabau.command,
									currentChannelId));
							JamochaValue result = interpreter
									.executeCommand(schabau.command);
							messageQueue.offer(new MessageEvent(
									MessageEvent.RESULT, result,
									currentChannelId));
						} catch (Exception e) {
							postMessageEvent(new MessageEvent(
									MessageEvent.ERROR, e, currentChannelId));
						} finally {
							currentChannelId = null;
						}
					}
				}
				List<MessageEvent> allMessages = new ArrayList<MessageEvent>(
						messageQueue);
				messageQueue.clear();
				for (int i = 0; i < allMessages.size(); ++i) {
					MessageEvent event = allMessages.get(i);
					synchronized (idToChannel) {
						for (CommunicationChannel channel : idToChannel
								.values()) {
							if (InterestType.ALL.equals(channel.getInterest())
									|| (InterestType.MINE.equals(channel
											.getInterest()) && channel
											.getChannelId().equals(
													event.getChannelId()))) {
								List<MessageEvent> messageList = idToMessages
										.get(channel.getChannelId());
								if (messageList != null) {
									messageList.add(event);
								}
							}
						}
					}
				}
			}
		}
	}

	private static final class CommandObject {

		private Object command;

		private String channelId;

		private CommandObject(Object command, String channelId) {
			super();
			this.command = command;
			this.channelId = channelId;
		}

	}

	/**
	 * The List of MessageListeners
	 */
	private Map<String, CommunicationChannel> idToChannel = new HashMap<String, CommunicationChannel>();

	private Map<String, List<MessageEvent>> idToMessages = new HashMap<String, List<MessageEvent>>();

	private volatile String currentChannelId = "";

	/**
	 * The Rete-engine we work with
	 */
	private Rete engine;

	// TODO is this threadsafe?
	private Queue<CommandObject> commandQueue = new LinkedList<CommandObject>();

	private Queue<MessageEvent> messageQueue = new LinkedList<MessageEvent>();

	private CLIPSInterpreter interpreter;

	private int idCounter = 0;

	private CommandThread commandThread;

	/**
	 * The constructor for a message router.
	 * 
	 * @param engine
	 *            The Rete-engine that should be used.
	 */
	public MessageRouter(Rete engine) {
		this.engine = engine;
		this.interpreter = new CLIPSInterpreter(engine);
		commandThread = new CommandThread();
		commandThread.start();
	}

	/**
	 * returns the underlying Rete-engine.
	 * 
	 * @return The Rete-engine used in this MessageRouter-instance.
	 */
	public Rete getReteEngine() {
		return engine;
	}

	public void postMessageEvent(MessageEvent event) {
		messageQueue.offer(event);
	}

	public void enqueueCommand(Object command, String channelId) {
		commandQueue.add(new CommandObject(command, channelId));
	}

	public StreamChannel openChannel(String channelName, InputStream inputStream) {
		return openChannel(channelName, inputStream, InterestType.MINE);
	}

	public StreamChannel openChannel(String channelName,
			InputStream inputStream, InterestType interestType) {
		StreamChannel channel = new StreamChannelImpl(channelName + "_"
				+ idCounter++, this, interestType);
		channel.init(inputStream);
		registerChannel(channel);
		return channel;
	}

	public StreamChannel openChannel(String channelName, Reader reader) {
		return openChannel(channelName, reader, InterestType.MINE);
	}

	public StreamChannel openChannel(String channelName, Reader reader,
			InterestType interestType) {
		StreamChannel channel = new StreamChannelImpl(channelName + "_"
				+ idCounter++, this, interestType);
		channel.init(reader);
		registerChannel(channel);
		return channel;
	}

	public StringChannel openChannel(String channelName) {
		return openChannel(channelName, InterestType.MINE);
	}

	public StringChannel openChannel(String channelName,
			InterestType interestType) {
		StringChannel channel = new StringChannelImpl(channelName + "_"
				+ idCounter++, this, interestType);
		registerChannel(channel);
		return channel;
	}

	public void closeChannel(CommunicationChannel channel) {
		synchronized (idToChannel) {
			idToChannel.remove(channel.getChannelId());
			idToMessages.remove(channel.getChannelId());
			// If it's a StreamChannel, stop the Parser-Thread
			if(channel instanceof StreamChannelImpl) {
				((StreamChannelImpl)channel).close();
			}
		}
	}

	private void registerChannel(CommunicationChannel channel) {
		synchronized (idToChannel) {
			idToChannel.put(channel.getChannelId(), channel);
			idToMessages.put(channel.getChannelId(), new ArrayList<MessageEvent>());
		}
	}

	void fillMessageList(String channelId, List<MessageEvent> destinationList) {
		synchronized (idToChannel) {
			List<MessageEvent> storedMessages = idToMessages.get(channelId);
			if (storedMessages != null && destinationList != null) {
				destinationList.addAll(storedMessages);
				storedMessages.clear();
			}
		}
	}

	public String getCurrentChannelId() {
		return currentChannelId;
	}
}
