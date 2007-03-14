/*
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
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;
import org.jamocha.parser.ParserNotFoundException;
import org.jamocha.rete.Rete;

/**
 * A MessageRouter is responsible for sending messages to the Rete-engine and
 * receive the answers. Possible MessageListeners will be notified of all events
 * that occured.
 * 
 * @author Alexander Wilden
 * @author Christoph Emonds
 * @author Sebastian Reinartz
 */
public class MessageRouter implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Mapping of channel IDs to channel instances.
	 */
	private Map<String, CommunicationChannel> idToChannel = new HashMap<String, CommunicationChannel>();

	/**
	 * Mapping of channel IDs to MessageEvents held for the underlying channel.
	 */
	private Map<String, List<MessageEvent>> idToMessages = new HashMap<String, List<MessageEvent>>();

	// TODO is this still needed?
	private volatile String currentChannelId = "";

	/**
	 * The Rete engine the <code>MessageRouter</code> works with.
	 */
	private Rete engine;

	/**
	 * Threadsafe queue for incoming commands.
	 */
	private Queue<CommandObject> commandQueue = new ConcurrentLinkedQueue<CommandObject>();

	/**
	 * Threadsafe queue for messages that should be send.
	 */
	private Queue<MessageEvent> messageQueue = new ConcurrentLinkedQueue<MessageEvent>();

	/**
	 * Supporting counter that helps to provide really unique channel IDs.
	 */
	private int idCounter = 0;

	/**
	 * The Thread that processes all incoming commands.
	 */
	private CommandThread commandThread;

	/**
	 * The constructor for a new <code>MessageRouter</code>. In one Rete
	 * engine only a single <code>MessageRouter</code> should be used.
	 * 
	 * @param engine
	 *            The Rete engine that should be used.
	 */
	public MessageRouter(Rete engine) {
		this.engine = engine;
		commandThread = new CommandThread();
		commandThread.start();
	}

	/**
	 * Returns the underlying Rete engine.
	 * 
	 * @return The Rete-engine used in this MessageRouter-instance.
	 */
	public Rete getReteEngine() {
		return engine;
	}

	/**
	 * Adds a new <code>MessageEvent</code> to the <code>messageQueue</code>.
	 * 
	 * @param event
	 *            The <code>MessageEvent</code> to add.
	 */
	public void postMessageEvent(MessageEvent event) {
		messageQueue.offer(event);
	}

	/**
	 * Adds a new command to the <code>commandQueue</code>. A command
	 * consists of an Expression and the channel ID of the channel that enqueues
	 * this command.
	 * 
	 * @param event
	 *            The <code>MessageEvent</code> to add.
	 */
	void enqueueCommand(Expression command, String channelId) {
		commandQueue.add(new CommandObject(command, channelId));
	}

	/**
	 * Opens a <code>StreamChannel</code> with an <code>InputStream</code>.
	 * <p>
	 * The <code>InterestType</code> will be the default
	 * <code>InterestType.MINE</code>.
	 * 
	 * @param channelName
	 *            Preferred name of the channel. Will be completed to the unique
	 *            channel ID with the current value of the
	 *            <code>idCounter</code>.
	 * @param inputStream
	 *            The Stream to read from.
	 * @return Instance of the new channel.
	 */
	public StreamChannel openChannel(String channelName, InputStream inputStream) {
		return openChannel(channelName, inputStream, InterestType.MINE);
	}

	/**
	 * Opens a <code>StreamChannel</code> with an <code>InputStream</code>
	 * and a preferred parser.
	 * <p>
	 * The <code>InterestType</code> will be the default
	 * <code>InterestType.MINE</code>.
	 * 
	 * @param channelName
	 *            Preferred name of the channel. Will be completed to the unique
	 *            channel ID with the current value of the
	 *            <code>idCounter</code>.
	 * @param inputStream
	 *            The Stream to read from.
	 * @param parserName
	 *            Name of the Parser to use. Have a look at
	 *            {@link ParserFactory} for available Parsers.
	 * @return Instance of the new channel.
	 * @throws ParserNotFoundException
	 *             if the preferred Parser is not available.
	 */
	public StreamChannel openChannel(String channelName,
			InputStream inputStream, String parserName)
			throws ParserNotFoundException {
		return openChannel(channelName, inputStream, InterestType.MINE,
				parserName);
	}

	/**
	 * Opens a <code>StreamChannel</code> with an <code>InputStream</code>
	 * and a specific <code>InterestType</code>.
	 * 
	 * @param channelName
	 *            Preferred name of the channel. Will be completed to the unique
	 *            channel ID with the current value of the
	 *            <code>idCounter</code>.
	 * @param inputStream
	 *            The Stream to read from.
	 * @param interestType
	 *            Type of interest for incoming messages. Have a look at
	 *            {@link InterestType} for possible types.
	 * @return Instance of the new channel.
	 */
	public StreamChannel openChannel(String channelName,
			InputStream inputStream, InterestType interestType) {
		try {
			return openChannel(channelName, inputStream, interestType,
					ParserFactory.getDefaultParser());
		} catch (ParserNotFoundException e) {
			// ignore it here, because the default parser is always available
		}
		return null;
	}

	/**
	 * Opens a <code>StreamChannel</code> with an <code>InputStream</code>,
	 * a specific <code>InterestType</code> and a preferred Parser.
	 * 
	 * @param channelName
	 *            Preferred name of the channel. Will be completed to the unique
	 *            channel ID with the current value of the
	 *            <code>idCounter</code>.
	 * @param inputStream
	 *            The Stream to read from.
	 * @param interestType
	 *            Type of interest for incoming messages. Have a look at
	 *            {@link InterestType} for possible types.
	 * @param parserName
	 *            Name of the Parser to use. Have a look at
	 *            {@link ParserFactory} for available Parsers.
	 * @return Instance of the new channel.
	 * @throws ParserNotFoundException
	 *             if the prefered Parser is not available.
	 */
	public StreamChannel openChannel(String channelName,
			InputStream inputStream, InterestType interestType,
			String parserName) throws ParserNotFoundException {
		StreamChannel channel = new StreamChannelImpl(channelName + "_"
				+ idCounter++, this, interestType);
		channel.init(inputStream, parserName);
		registerChannel(channel);
		return channel;
	}

	/**
	 * Opens a <code>StreamChannel</code> directly with a <code>Reader</code>.
	 * <p>
	 * The <code>InterestType</code> will be the default
	 * <code>InterestType.MINE</code>.
	 * 
	 * @param channelName
	 *            Preferred name of the channel. Will be completed to the unique
	 *            channel ID with the current value of the
	 *            <code>idCounter</code>.
	 * @param reader
	 *            The Reader used to read from an underlying Stream.
	 * @return Instance of the new channel.
	 */
	public StreamChannel openChannel(String channelName, Reader reader) {
		return openChannel(channelName, reader, InterestType.MINE);
	}

	/**
	 * Opens a <code>StreamChannel</code> directly with a <code>Reader</code>
	 * and a preferred Parser.
	 * <p>
	 * The <code>InterestType</code> will be the default
	 * <code>InterestType.MINE</code>.
	 * 
	 * @param channelName
	 *            Preferred name of the channel. Will be completed to the unique
	 *            channel ID with the current value of the
	 *            <code>idCounter</code>.
	 * @param reader
	 *            The Reader used to read from an underlying Stream.
	 * @param parserName
	 *            Name of the Parser to use. Have a look at
	 *            {@link ParserFactory} for available Parsers.
	 * @return Instance of the new channel.
	 * @throws ParserNotFoundException
	 *             if the preferred Parser is not available.
	 */
	public StreamChannel openChannel(String channelName, Reader reader,
			String parserName) throws ParserNotFoundException {
		return openChannel(channelName, reader, InterestType.MINE, parserName);
	}

	/**
	 * Opens a <code>StreamChannel</code> directly with a <code>Reader</code>
	 * and a specific <code>InterestType</code>.
	 * 
	 * @param channelName
	 *            Preferred name of the channel. Will be completed to the unique
	 *            channel ID with the current value of the
	 *            <code>idCounter</code>.
	 * @param reader
	 *            The Reader used to read from an underlying Stream.
	 * @param interestType
	 *            Type of interest for incoming messages. Have a look at
	 *            {@link InterestType} for possible types.
	 * @return Instance of the new channel.
	 */
	public StreamChannel openChannel(String channelName, Reader reader,
			InterestType interestType) {
		try {
			return openChannel(channelName, reader, interestType, ParserFactory
					.getDefaultParser());
		} catch (ParserNotFoundException e) {
			// ignore it here, because the default parser is always available
		}
		return null;
	}

	/**
	 * Opens a <code>StreamChannel</code> directly with a <code>Reader</code>,
	 * a specific <code>InterestType</code> and a preferred Parser.
	 * 
	 * @param channelName
	 *            Preferred name of the channel. Will be completed to the unique
	 *            channel ID with the current value of the
	 *            <code>idCounter</code>.
	 * @param reader
	 *            The Reader used to read from an underlying Stream.
	 * @param interestType
	 *            Type of interest for incoming messages. Have a look at
	 *            {@link InterestType} for possible types.
	 * @param parserName
	 *            Name of the Parser to use. Have a look at
	 *            {@link ParserFactory} for available Parsers.
	 * @return Instance of the new channel.
	 * @throws ParserNotFoundException
	 *             if the preferred Parser is not available.
	 */
	public StreamChannel openChannel(String channelName, Reader reader,
			InterestType interestType, String parserName)
			throws ParserNotFoundException {
		StreamChannel channel = new StreamChannelImpl(channelName + "_"
				+ idCounter++, this, interestType);
		channel.init(reader, parserName);
		registerChannel(channel);
		return channel;
	}

	/**
	 * Opens a <code>StringChannel</code> that accepts simple Strings as
	 * Input.
	 * <p>
	 * The <code>InterestType</code> will be the default
	 * <code>InterestType.MINE</code>.
	 * 
	 * @param channelName
	 *            Preferred name of the channel. Will be completed to the unique
	 *            channel ID with the current value of the
	 *            <code>idCounter</code>.
	 * @return Instance of the new channel.
	 */
	public StringChannel openChannel(String channelName) {
		return openChannel(channelName, InterestType.MINE);
	}

	/**
	 * Opens a <code>StringChannel</code> that accepts simple Strings as Input
	 * with a preferred Parser.
	 * <p>
	 * The <code>InterestType</code> will be the default
	 * <code>InterestType.MINE</code>.
	 * 
	 * @param channelName
	 *            Preferred name of the channel. Will be completed to the unique
	 *            channel ID with the current value of the
	 *            <code>idCounter</code>.
	 * @param parserName
	 *            Name of the Parser to use. Have a look at
	 *            {@link ParserFactory} for available Parsers.
	 * @return Instance of the new channel.
	 * @throws ParserNotFoundException
	 *             if the preferred Parser is not available.
	 */
	public StringChannel openChannel(String channelName, String parserName)
			throws ParserNotFoundException {
		return openChannel(channelName, InterestType.MINE, parserName);
	}

	/**
	 * Opens a <code>StringChannel</code> that accepts simple Strings as Input
	 * with a specific <code>InterestType</code>.
	 * 
	 * @param channelName
	 *            Preferred name of the channel. Will be completed to the unique
	 *            channel ID with the current value of the
	 *            <code>idCounter</code>.
	 * @param interestType
	 *            Type of interest for incoming messages. Have a look at
	 *            {@link InterestType} for possible types.
	 * @return Instance of the new channel.
	 */
	public StringChannel openChannel(String channelName,
			InterestType interestType) {
		try {
			return openChannel(channelName, interestType, ParserFactory
					.getDefaultParser());
		} catch (ParserNotFoundException e) {
			// ignore it here, because the default parser is always available
		}
		return null;
	}

	/**
	 * Opens a <code>StringChannel</code> that accepts simple Strings as Input
	 * with a specific <code>InterestType</code> and a preferred Parser.
	 * 
	 * @param channelName
	 *            Preferred name of the channel. Will be completed to the unique
	 *            channel ID with the current value of the
	 *            <code>idCounter</code>.
	 * @param interestType
	 *            Type of interest for incoming messages. Have a look at
	 *            {@link InterestType} for possible types.
	 * @param parserName
	 *            Name of the Parser to use. Have a look at
	 *            {@link ParserFactory} for available Parsers.
	 * @return Instance of the new channel.
	 * @throws ParserNotFoundException
	 *             if the preferred Parser is not available.
	 */
	public StringChannel openChannel(String channelName,
			InterestType interestType, String parserName)
			throws ParserNotFoundException {
		StringChannel channel = new StringChannelImpl(channelName + "_"
				+ idCounter++, this, interestType, parserName);
		registerChannel(channel);
		return channel;
	}

	/**
	 * Closes a channel. You need to close a channel so that no further messages
	 * are stored and consume memory and performance. If a StreamChannel is
	 * closed its <code>close()</code> method is called to stop all possible
	 * running Threads inside of this channel.
	 * 
	 * @param channel
	 *            The channel to close.
	 */
	public void closeChannel(CommunicationChannel channel) {
		synchronized (idToChannel) {
			idToChannel.remove(channel.getChannelId());
			idToMessages.remove(channel.getChannelId());
			// If it's a StreamChannel, stop the Parser-Thread
			if (channel instanceof StreamChannelImpl) {
				((StreamChannelImpl) channel).close();
			}
		}
	}

	/**
	 * Fills a given list with all messages available for the specific channel.
	 * 
	 * @param channelId
	 *            The Channel whose messages should be fetched.
	 * @param destinationList
	 *            The List to fill with available messages
	 */
	void fillMessageList(String channelId, List<MessageEvent> destinationList) {
		synchronized (idToChannel) {
			List<MessageEvent> storedMessages = idToMessages.get(channelId);
			if (storedMessages != null && destinationList != null) {
				destinationList.addAll(storedMessages);
				storedMessages.clear();
			}
		}
	}

	/**
	 * Returns the current channel-ID.
	 * <p>
	 * TODO check if this is obsolete.
	 * 
	 * @return The current channel-ID.
	 */
	public String getCurrentChannelId() {
		return currentChannelId;
	}

	/**
	 * Internally registers a new channel with the <code>MessageRouter</code>.
	 * 
	 * @param channel
	 *            The channel to register.
	 */
	private void registerChannel(CommunicationChannel channel) {
		synchronized (idToChannel) {
			idToChannel.put(channel.getChannelId(), channel);
			idToMessages.put(channel.getChannelId(),
					new ArrayList<MessageEvent>());
		}
	}

	private final class CommandThread extends Thread {

		@Override
		public void run() {
			while (true) {
				if (commandQueue.isEmpty()) {
					try {
						sleep(10);
					} catch (InterruptedException e) {
						// we ignore it
					}
				} else {
					CommandObject schabau = commandQueue.poll();
					if (schabau != null) {
						currentChannelId = schabau.channelId;
						try {
							messageQueue.offer(new MessageEvent(
									MessageEvent.COMMAND, schabau.command,
									currentChannelId));
							JamochaValue result = schabau.command
									.getValue(engine);
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

		private Expression command;

		private String channelId;

		private CommandObject(Expression command, String channelId) {
			super();
			this.command = command;
			this.channelId = channelId;
		}

	}
}
