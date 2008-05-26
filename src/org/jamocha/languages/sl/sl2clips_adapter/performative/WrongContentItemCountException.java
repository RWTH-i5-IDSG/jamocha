/*
 * Copyright 2007 Alexander Wilden
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
package org.jamocha.languages.sl.sl2clips_adapter.performative;

import org.jamocha.languages.sl.sl2clips_adapter.AdapterTranslationException;

/**
 * Exception that signals a translation error because of a wrong number of content
 * elements in some SL content.
 * 
 * @author Alexander Wilden
 * 
 */
public class WrongContentItemCountException extends AdapterTranslationException {

	private static final long serialVersionUID = 1L;

	public WrongContentItemCountException(int isCount, int hasToBeCount) {
		super("Wrong number of content elements. Expected " + hasToBeCount
				+ " elements and found " + isCount + " elements.");
	}

}
