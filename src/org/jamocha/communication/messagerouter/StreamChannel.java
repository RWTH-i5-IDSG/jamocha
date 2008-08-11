/*
 * Copyright 2002-2008 The Jamocha Team
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

package org.jamocha.communication.messagerouter;

import java.io.InputStream;
import java.io.Reader;

/**
 * Interface for Channels using Streams as input source.
 * 
 * @author Alexander Wilden
 * @author Christoph Emonds
 * @author Sebastian Reinartz
 * 
 */
public interface StreamChannel extends CommunicationChannel {

	/**
	 * Initializes the channel with an <code>InputStream</code> and a
	 * preferred Parser.
	 * <p>
	 * This method normally will only be called by the
	 * <code>MessageRouter</code>.
	 * 
	 * @param inputStream
	 *            The Stream to read from.
	 * @throws ParserNotFoundException
	 *             if the preferred Parser is not available.
	 */
	public void init(InputStream inputStream) ;

	/**
	 * Initializes the channel directly with a <code>Reader</code> and a
	 * preferred Parser.
	 * <p>
	 * This method normally will only be called by the
	 * <code>MessageRouter</code>.
	 * 
	 * @param reader
	 *            The Reader used to read from an underlying Stream.

	 * @throws ParserNotFoundException
	 *             if the preferred Parser is not available.
	 */
	public void init(Reader reader) ;

	/**
	 * Returns if the <code>StreamChannel</code> is available. This is the
	 * case if it was correctly initalized. It says nothing about the Stream
	 * that is read from. That means the channel can be available although the
	 * Stream is blocked.
	 * 
	 * @return <code>true</code> if this channel is available.
	 */
	public boolean isAvailable();
}
