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

package org.jamocha.parser;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.jamocha.formatter.Formatter;
import org.jamocha.formatter.SFPFormatter;
import org.jamocha.languages.clips.parser.SFPParser;
import org.jamocha.engine.Engine;
import org.jamocha.engine.RuleCompiler;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler;

/**
 * The ParserFactory generates all known Parsers for CLIPS-Code or other
 * languages needed when working with Jamocha.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class ParserFactory {

	private static String defaultMode = "sfp";

	public static void setDefaultMode(String mode) throws ModeNotFoundException {
		if (mode != null && mode.length() > 0) {
			defaultMode = mode;
			// This is just to test if the specified Parser exists. If not we
			// can
			// throw an exception.
			getParser(mode, new StringReader(""));
			getFormatter();
		}
	}

	public static String getDefaultParser() {
		return defaultMode;
	}

	public static Parser getParser(Reader reader) {
		try {
			return getParser(defaultMode, reader);
		} catch (ModeNotFoundException e) {
			// This shouldn't happen because the exception is thrown already
			// before when setting the default mode.
		}
		return null;
	}

	public static Parser getParser(InputStream stream) {
		try {
			return getParser(defaultMode, stream);
		} catch (ModeNotFoundException e) {
			// This shouldn't happen because the exception is thrown already
			// before when setting the default mode.
		}
		return null;
	}

	public static Parser getParser(String mode, Reader reader)
	throws ModeNotFoundException {
		if (mode.equalsIgnoreCase("sfp")
				| mode.equalsIgnoreCase("sfp-hokifisch")) {
			return new SFPParser(reader);
		} else {
			throw new ModeNotFoundException(mode);
		}
	}

	public static Parser getParser(String mode, InputStream stream)
	throws ModeNotFoundException {
		if (mode.equalsIgnoreCase("sfp")
				| mode.equalsIgnoreCase("sfp-hokifisch")) {
			return new SFPParser(stream);
		} else {
			throw new ModeNotFoundException(mode);
		}
	}

	/**
	 * Returns the Formatter without indentation belonging to the default Mode.
	 * 
	 * @return The Formatter of the default mode.
	 */
	public static Formatter getFormatter() {
		return getFormatter(false);
	}

	/**
	 * Returns the Formatter belonging to the default Mode.
	 * 
	 * @param indentation
	 *            if <code>true</code> the Formatter uses indentation.
	 * @return The Formatter of the default mode.
	 */
	public static Formatter getFormatter(boolean indentation) {
		try {
			return getFormatter(defaultMode, indentation);
		} catch (ModeNotFoundException e) {
			// This shouldn't happen because the exception is thrown already
			// before when setting the default mode.
		}
		return null;
	}

	/**
	 * Returns the Formatter without indentation belonging to a specified
	 * Parser. Parser.
	 * 
	 * @param mode
	 *            Name of the specific Mode.
	 * @return The Formatter of the default mode.
	 * @throws ParserNotFoundException
	 */
	public static Formatter getFormatter(String mode)
	throws ModeNotFoundException {
		return getFormatter(mode, false);
	}

	/**
	 * Returns the Formatter belonging to a specified Parser.
	 * 
	 * @param mode
	 *            Name of the specific Mode.
	 * @param indentation
	 *            if <code>true</code> the Formatter uses indentation.
	 * @return The Formatter of the mode.
	 * @throws ParserNotFoundException
	 */
	public static Formatter getFormatter(String mode, boolean indentation)
	throws ModeNotFoundException {
		Formatter f;
		if (mode.equalsIgnoreCase("clips")) {
			f = new SFPFormatter();
		} else if (mode.equalsIgnoreCase("sfp")
				| mode.equalsIgnoreCase("sfp-hokifisch")) {
			f = new SFPFormatter();
		} else {
			throw new ModeNotFoundException(mode);
		}
		f.setIndent(indentation);
		return f;
	}

	public static RuleCompiler getRuleCompiler(Engine engine, ReteNet net,
			RootNode root) {
		try {
			return getRuleCompiler(defaultMode, engine, net, root);
		} catch (ModeNotFoundException e) {
			// This shouldn't happen because the exception is thrown already
			// before when setting the default mode.
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static RuleCompiler getRuleCompiler(String mode, Engine engine, ReteNet net, RootNode root) throws ModeNotFoundException {
		if (mode.equalsIgnoreCase("clips")) {
			throw new ModeNotFoundException("clips-mode not supported anymore");
		} else if (mode.equalsIgnoreCase("sfp")) {
			return new SFRuleCompiler(engine, root, net);
		}
		throw new ModeNotFoundException(mode);
	}
}