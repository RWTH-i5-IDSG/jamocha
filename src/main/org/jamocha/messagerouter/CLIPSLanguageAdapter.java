/**
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

import java.io.StringReader;
import java.io.StringWriter;

import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.parser.clips.ParseException;
import org.jamocha.rete.Rete;

/**
 * A simple LanguageAdapter that accepts only CLIPS as language. Any command
 * will directly be forwarded to the CLIPSParser.
 * 
 * @author Alexander Wilden, Christoph Emonds, Sebastian Reinartz
 * 
 */
public class CLIPSLanguageAdapter implements LanguageAdapter {

    public String evaluate(Rete engine, String command, String language)
	    throws ParseException {
	StringReader reader = new StringReader(command);
	StringWriter writer = new StringWriter();
////	CLIPSParser parser = new CLIPSParser(engine, reader, writer, false);
//	parser.startParser();
//	String res = writer.toString();
//	parser = null;
	return null;
    }

    public String[] getSupportedLanguages() {
	return new String[] { "CLIPS" };
    }

}
