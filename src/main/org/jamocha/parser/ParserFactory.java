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
import java.io.StringReader;

import org.jamocha.parser.clips.CLIPSFormatter;
import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.parser.sfp.SFPParser;

/**
 * The ParserFactory generates all known Parsers for CLIPS-Code or other
 * languages needed when working with Jamocha.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class ParserFactory {

	private static String defaultParser = "clips";

	public static void setDefaultParser(String parserName)
			throws ParserNotFoundException {
		if (parserName != null && parserName.length() > 0) {
			defaultParser = parserName;
			// This is just to test if the specified Parser exists. If not we
			// can
			// throw an exception.
			getParser(parserName, new StringReader(""));
		}
	}

	public static String getDefaultParser() {
		return defaultParser;
	}

	public static Parser getParser(Reader reader) {
		try {
			return getParser(defaultParser, reader);
		} catch (ParserNotFoundException e) {
			// This shouldn't happen because the exception is thrown already
			// before when setting the default parser.
			e.printStackTrace();
		}
		return null;
	}

	public static Parser getParser(InputStream stream) {
		try {
			return getParser(defaultParser, stream);
		} catch (ParserNotFoundException e) {
			// This shouldn't happen because the exception is thrown already
			// before when setting the default parser.
			e.printStackTrace();
		}
		return null;
	}

	public static Parser getParser(String parserName, Reader reader)
			throws ParserNotFoundException {
		if (parserName.equalsIgnoreCase("clips")) {
			return new CLIPSParser(reader);
		} else if (parserName.equalsIgnoreCase("sfp")) {
			return new SFPParser(reader);
		} else {
			throw new ParserNotFoundException("The Parser with the name \""
					+ parserName + "\" could not be found.");
		}
	}

	public static Parser getParser(String parserName, InputStream stream)
			throws ParserNotFoundException {
		if (parserName.equalsIgnoreCase("clips")) {
			return new CLIPSParser(stream);
		} else if (parserName.equalsIgnoreCase("sfp")) {
			return new SFPParser(stream);
		} else {
			throw new ParserNotFoundException("The Parser with the name \""
					+ parserName + "\" could not be found.");
		}
	}

	/**
	 * Returns the Formatter without indentation belonging to the default
	 * Parser.
	 * 
	 * @return The Formatter of the default parser.
	 */
	public static Formatter getFormatter() {
		return getFormatter(false);
	}

	/**
	 * Returns the Formatter belonging to the default Parser.
	 * 
	 * @param indentation
	 *            if <code>true</code> the Formatter uses indentation.
	 * @return The Formatter of the default parser.
	 */
	public static Formatter getFormatter(boolean indentation) {
		try {
			return getFormatter(defaultParser, indentation);
		} catch (ParserNotFoundException e) {
			// Should never happen
		}
		return null;
	}

	/**
	 * Returns the Formatter without indentation belonging to a specified
	 * Parser. Parser.
	 * 
	 * @param parserName
	 *            Name of the spcecific Parser.
	 * @return The Formatter of the default parser.
	 * @throws ParserNotFoundException
	 */
	public static Formatter getFormatter(String parserName)
			throws ParserNotFoundException {
		return getFormatter(parserName, false);
	}

	/**
	 * Returns the Formatter belonging to a specified Parser.
	 * 
	 * @param parserName
	 *            Name of the spcecific Parser.
	 * @param indentation
	 *            if <code>true</code> the Formatter uses indentation.
	 * @return The Formatter of the parser.
	 * @throws ParserNotFoundException
	 */
	public static Formatter getFormatter(String parserName, boolean indentation)
			throws ParserNotFoundException {
		if (parserName.equalsIgnoreCase("clips")) {
			return new CLIPSFormatter(indentation);
		} else if (parserName.equalsIgnoreCase("sfp")) {
			return new CLIPSFormatter(indentation);
		} else {
			throw new ParserNotFoundException("The Parser with the name \""
					+ parserName + "\" could not be found.");
		}
	}

}
