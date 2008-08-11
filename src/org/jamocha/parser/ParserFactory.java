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

package org.jamocha.parser;

import java.io.InputStream;
import java.io.Reader;

import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.RuleCompiler;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.rules.rulecompiler.beffy.BeffyRuleCompiler;
import org.jamocha.formatter.Formatter;
import org.jamocha.formatter.SFPFormatter;
import org.jamocha.languages.clips.parser.SFPParser;

/**
 * The ParserFactory generates all known Parsers for CLIPS-Code or other
 * languages needed when working with Jamocha.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class ParserFactory {

	public static Parser getParser(Reader reader) {
		return new SFPParser(reader);
	}

	public static Parser getParser(InputStream stream) {
		return new SFPParser(stream);
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
		SFPFormatter f = new SFPFormatter();
		f.setIndent(indentation);
		return f;
	}

	
	public static RuleCompiler getRuleCompiler(Engine engine, ReteNet net,	RootNode root) {
		return new BeffyRuleCompiler(engine, root, net);
	}

}