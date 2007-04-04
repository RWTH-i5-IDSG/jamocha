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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.jamocha.parser.Expression;
import org.jamocha.parser.ParseException;
import org.jamocha.parser.ParserFactory;
import org.jamocha.parser.ParserNotFoundException;
import org.jamocha.parser.clips.TokenMgrError;

/**
 * Implementation of a <code>StreamChannel</code>.
 * 
 * @author Alexander Wilden
 * @author Christoph Emonds
 * @author Sebastian Reinartz
 * 
 */
class StreamChannelImpl extends AbstractCommunicationChannel implements StreamChannel {

    /**
         * The <code>Reader</code> we read from.
         */
    private Reader reader;

    /**
         * Thread reading from the <code>reader</code>, parsing the content
         * and sending the Expressions to the <code>MessageRouter</code>.
         */
    private StreamChannelThread streamChannelThread;

    /**
         * Flag indicating if the initialzation phase is done.
         */
    private boolean initDone = false;

    public StreamChannelImpl(String channelId, MessageRouter router, InterestType interest) {
	super(channelId, router, interest);
	// parser = new CLIPSParser((Reader) null);
	streamChannelThread = new StreamChannelThread();
    }

    public void init(InputStream inputStream, String parserName) throws ParserNotFoundException {
	init(new InputStreamReader(inputStream), parserName);
    }

    public void init(Reader reader, String parserName) throws ParserNotFoundException {
	this.reader = reader;
	this.parserName = parserName;
	streamChannelThread.setStopped();
	streamChannelThread = new StreamChannelThread();
	parser = ParserFactory.getParser(parserName, reader);
	streamChannelThread.start();
	initDone = true;
    }

    void close() {
	streamChannelThread.setStopped();
	try {
	    reader.close();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public boolean isAvailable() {
	return initDone;
    }

    private class StreamChannelThread extends Thread {

	private boolean stopped = false;

	@Override
	public void run() {
	    while (!stopped) {
		Expression command = null;
		try {
		    while (!stopped && (parser != null) && (command = parser.nextExpression()) != null) {
			router.enqueueCommand(command, getChannelId());
		    }
		} catch (ParseException e) {
		    router.postMessageEvent(new MessageEvent(MessageEvent.PARSE_ERROR, e,
			    getChannelId()));
		    restartParser(reader);
		} catch (TokenMgrError e) {
		    router.postMessageEvent(new MessageEvent(MessageEvent.PARSE_ERROR, e,
			    getChannelId()));
		    restartParser(reader);
		}
	    }
	}

	private void setStopped() {
	    stopped = true;
	}

    }

}
