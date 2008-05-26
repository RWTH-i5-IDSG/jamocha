/*
 * Copyright 2007 Christoph Emonds
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
package org.jamocha.parser;

public class IllegalTypeException extends EvaluationException {

	private static final long serialVersionUID = 1L;

	public IllegalTypeException(JamochaType[] expected, JamochaType found) {
		super(createMessage(expected, found));
	}

	private static String createMessage(JamochaType[] expected, JamochaType found) {
		StringBuilder sb = new StringBuilder();
		sb.append("Illegal type, expected ");
		for(int i=0; i<expected.length -2; ++i) {
			sb.append(expected[i]).append(", ");
		}
		if(expected.length > 1) {
			sb.append(expected[expected.length-2]).append(" or ");
		}
		if(expected.length > 0) {
			sb.append(expected[expected.length-1]).append(", ");
		}
		sb.append("found ").append(found);
		return sb.toString();
	}

}
