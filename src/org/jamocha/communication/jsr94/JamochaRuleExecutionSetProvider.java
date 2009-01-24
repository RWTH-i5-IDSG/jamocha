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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetCreateException;
import javax.rules.admin.RuleExecutionSetProvider;

import org.jamocha.languages.clips.parser.SFPParser;
import org.jamocha.parser.Expression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class JamochaRuleExecutionSetProvider implements	RuleExecutionSetProvider {

	@SuppressWarnings("unchecked")
	public RuleExecutionSet createRuleExecutionSet(Reader r,
			Map properties) throws RuleExecutionSetCreateException, IOException {

		try {
			DOMParser domparser = new DOMParser();
			domparser.parse(new InputSource(r));
			Document doc = domparser.getDocument();

			Element root = doc.getDocumentElement();
			String name = root.getElementsByTagName("name").item(0)
					.getTextContent();
			String description = root.getElementsByTagName("description").item(
					0).getTextContent();
			String code = root.getElementsByTagName("code").item(0)
					.getTextContent();

			// TODO: here, sfp parser is hardcoded now
			SFPParser sfpparser = new SFPParser(new StringReader(code));
			Expression e;

			List<Expression> _drcs = new ArrayList<Expression>();
			while ((e = sfpparser.nextExpression()) != null)
				_drcs.add(e);

			Expression[] expressions = new Expression[_drcs.size()];
			expressions = _drcs.toArray(expressions);

			RuleExecutionSet res = new JamochaRuleExecutionSet(description,
					name, expressions);

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
	public RuleExecutionSet createRuleExecutionSet(Serializable object, Map properties) throws RuleExecutionSetCreateException {
		// TODO Auto-generated method stub
		return null;
	}

	public RuleExecutionSet createRuleExecutionSet(Element root, Map properties)	throws RuleExecutionSetCreateException, RemoteException {
		try {
			return JamochaLocalRuleExecutionSetProvider.createRuleExecutionSetFromXML(root, properties);
		} catch (IOException e) {
			throw new RuleExecutionSetCreateException("error while reading",e);
		}
	}

	public RuleExecutionSet createRuleExecutionSet(String url, Map properties)throws RuleExecutionSetCreateException, IOException,	RemoteException {
		URL sourceUrl = new URL(url);
		Reader r=null;
		if (sourceUrl.getProtocol().equals("file")) {
			try {
				r = new FileReader(new File(sourceUrl.toURI()));
			} catch (URISyntaxException e) {
				throw new RuleExecutionSetCreateException("wrong url syntax",e);
			}
		} else
			throw new RuleExecutionSetCreateException(sourceUrl.getProtocol()+" protocol is not supported");
		return JamochaLocalRuleExecutionSetProvider.createRuleExecutionSetAutoprobe(r, properties);
	}

}
