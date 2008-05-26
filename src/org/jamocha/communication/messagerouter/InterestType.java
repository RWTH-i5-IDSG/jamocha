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

package org.jamocha.communication.messagerouter;

/**
 * Possible types of interest for the CommunicationChannels.
 * 
 * @author Alexander Wilden
 * @author Christoph Emonds
 * @author Sebastian Reinartz
 * 
 */
public enum InterestType {

	/**
	 * used for channels which aren't interested in any message output.
	 */
	NONE,

	/**
	 * used for channels which are only interested in the message output, which
	 * is in response to their own input.
	 */
	MINE,

	/**
	 * used for channels which are interested in every message output,
	 * regardless which channel caused this output.
	 */
	ALL;
}
