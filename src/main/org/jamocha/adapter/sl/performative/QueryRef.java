/*
 * Copyright 2007 Fehmi Karanfil
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
 */
package org.jamocha.adapter.sl.performative;

import java.io.StringReader;

import org.jamocha.adapter.AdapterTranslationException;
import org.jamocha.parser.sl_old.ParseException;
import org.jamocha.parser.sl_old.SLParser;
import org.jamocha.parser.sl_old.SLParserTreeConstants;
import org.jamocha.parser.sl_old.SimpleNode;

/**
 * This class walks through an SL code tree and translates it to CLIPS depending
 * on the given performative.
 * 
 * @author Fehmi Karanfil
 * 
 */
public class QueryRef extends SLPerformative  {

	/**
	 * A private constructor to force access only in a static way.
	 * 
	 */
	private QueryRef() {
	}

	/**
	 * Translates SL code of a query-ref to CLIPS code.
	 * 
	 * @param slContent
	 *            The SL content we have to translate.
	 * @return CLIPS commands that represent the given SL code.
	 * @throws AdapterTranslationException
	 *             if the SLParser throws an Exception or anything else unnormal
	 *             happens.
	 */
	public static String getCLIPS(String slContent)
			throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();

		SLParser parser = new SLParser(new StringReader(slContent));
		SimpleNode sn;
		try {
			sn = parser.Content();
		} catch (ParseException e) {
			throw new AdapterTranslationException(
					"Could not translate from SL to CLIPS.", e);
		}
		// Walk through the children until we have something useful
		sn = getChildAtLevel(sn, 2);
		if (sn.getID() == SLParserTreeConstants.JJTIDENTIFYINGEXPRESSION) {
			
		    String expression = handleIdentifyingExpression(sn);
			
			result.append("(defrule query-ref"+expression+"=>"+
					" (return inform-ref"+expression+"))");
		    }
		}

		return result.toString();
	}
	
}
