/**
 * Copyright 2007 Alexander Wilden
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
package org.jamocha.parser;

import java.io.InputStream;
import java.io.Reader;

import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.parser.cool.COOLParser;

/**
 * The ParserFactory generates all known Parsers for CLIPS-Code or other
 * languages needed when working with Jamocha.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class ParserFactory {

	public static Parser getParser(String parserName, Reader reader)
			throws ParserNotFoundException {
		if (parserName.equalsIgnoreCase("cool")) {
			return new COOLParser(reader);
		} else if (parserName.equalsIgnoreCase("clips")) {
			return new CLIPSParser(reader);
		} else {
			throw new ParserNotFoundException("The Parser with the name \""
					+ parserName + "\" could not be found.");
		}
	}

	public static Parser getParser(String parserName, InputStream stream)
			throws ParserNotFoundException {
		if (parserName.equalsIgnoreCase("cool")) {
			return new COOLParser(stream);
		} else if (parserName.equalsIgnoreCase("clips")) {
			return new CLIPSParser(stream);
		} else {
			throw new ParserNotFoundException("The Parser with the name \""
					+ parserName + "\" could not be found.");
		}
	}

}
