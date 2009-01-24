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

package org.jamocha.communication.jsr94;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetCreateException;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.functions.ruleengine.JsrRulesetDescription;
import org.jamocha.engine.functions.ruleengine.JsrRulesetName;
import org.jamocha.languages.clips.parser.ParseException;
import org.jamocha.languages.clips.parser.SFPParser;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class JamochaLocalRuleExecutionSetProvider implements LocalRuleExecutionSetProvider {

	static RuleExecutionSet createRuleExecutionSetAutoprobe(Reader r, Map properties) throws IOException, RuleExecutionSetCreateException {
		RuleExecutionSet res=null;
		/*
		 * TODO: for the auto probing, we read the whole content into a string
		 * at the moment. when we have that string, we can decide whether it
		 * is a xml or a plain clp.
		 * maybe it would make sense to make that better (a new reader subclass
		 * for it, which avoids reading the complete input beforehead)
		 */
		StringBuilder sb = new StringBuilder();
		int read;
		char[] buffer = new char[1000];
		while ( (read = r.read(buffer, 0, buffer.length)) > 0) {
			sb.append(buffer, 0, read);
		}
		String content = sb.toString().trim();
		if (content.startsWith("<?xml ")){
			// content is XML
			DOMParser domparser = new DOMParser();
			try {
				domparser.parse(new InputSource( new StringReader(content) ));
			} catch (SAXException e) {
				throw new RuleExecutionSetCreateException("error while parsing");
			}
			Document doc = domparser.getDocument();
			Element root = doc.getDocumentElement();
			res =  createRuleExecutionSetFromXML(root, properties);
		} else {
			// content is CLIPS-code
			res= createRuleExecutionSetFromCLIPS(content, properties, null, null,true);
		}
		return res;
	}
	
	static RuleExecutionSet createRuleExecutionSetFromCLIPS(String code, Map properties, String name, String description, boolean parseForNameAndDesc) throws RuleExecutionSetCreateException {
		// TODO: here, sfp parser is hardcoded now
		
		SFPParser sfpparser = new SFPParser(new StringReader(code));
		Expression e;

		List<Expression> _drcs = new ArrayList<Expression>();
		try {
			while ((e = sfpparser.nextExpression()) != null) 
			{
				_drcs.add(e);
				if (parseForNameAndDesc) {
					if (e instanceof Signature) {
						Signature s = (Signature) e;
						if (s.getSignatureName().equals(JsrRulesetDescription.NAME)) {
							try {
								name = s.getParameters()[0].getValue(null).getStringValue();
							} catch (EvaluationException e1) {
								Logging.logger(RuleExecutionSet.class).warn("error retrieving rule execution set's name");
							}
						} else if (s.getSignatureName().equals(JsrRulesetName.NAME)) {
							try {
								description = s.getParameters()[0].getValue(null).getStringValue();
							} catch (EvaluationException e1) {
								Logging.logger(RuleExecutionSet.class).warn("error retrieving rule execution set's description");
							}
						}
						if (name != null && description != null) parseForNameAndDesc = false; // just optimization
					}
				}
			}
		} catch (ParseException e1) {
			throw new RuleExecutionSetCreateException("error while parsing",e1);
		}

		Expression[] expressions = new Expression[_drcs.size()];
		expressions = _drcs.toArray(expressions);

		if (description == null) description = "";
		if (name == null) name = "ruleset-"+(int)(10000.*Math.random());

		RuleExecutionSet res = new JamochaRuleExecutionSet(description,	name, expressions);

		return res;
	}
	
	@SuppressWarnings("unchecked")
	static RuleExecutionSet createRuleExecutionSetFromXML(Element root,
			Map properties) throws RuleExecutionSetCreateException, IOException {

		try {
			String name = root.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();
			String description = root.getElementsByTagName("description").item(0).getChildNodes().item(0).getNodeValue();
			String code = root.getElementsByTagName("code").item(0).getChildNodes().item(0).getNodeValue();

			RuleExecutionSet res = createRuleExecutionSetFromCLIPS(code, properties, name, description, false);
			
			return res;

		} catch (Exception ex) {
			throw new RuleExecutionSetCreateException(
					"error while parsing or creating rule set", ex);
		}
	}

	@SuppressWarnings("unchecked")
	public RuleExecutionSet createRuleExecutionSet(InputStream i, Map properties) throws RuleExecutionSetCreateException, IOException {
		InputStreamReader r = new InputStreamReader(i);
		return createRuleExecutionSet(r, properties);
	}

	@SuppressWarnings("unchecked")
	public RuleExecutionSet createRuleExecutionSet(Object object, Map properties) throws RuleExecutionSetCreateException {
		// TODO Auto-generated method stub
		return null;
	}

	public RuleExecutionSet createRuleExecutionSet(Reader r, Map properties)	throws RuleExecutionSetCreateException, IOException {
		return createRuleExecutionSetAutoprobe(r, properties);
	}

}
