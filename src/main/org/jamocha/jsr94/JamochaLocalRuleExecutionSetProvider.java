package org.jamocha.jsr94;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetCreateException;

import org.jamocha.rule.Rule;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class JamochaLocalRuleExecutionSetProvider implements LocalRuleExecutionSetProvider {

	public RuleExecutionSet createRuleExecutionSet(InputStream istream, Map properties) throws RuleExecutionSetCreateException, IOException {
		
		try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(istream));
            Document doc = parser.getDocument();

            Element root = doc.getDocumentElement();
            String name = root.getElementsByTagName("name").item(0).getTextContent();
            String description = root.getElementsByTagName("description").item(0).getTextContent();
            String code = root.getElementsByTagName("code").item(0).getTextContent();
            
            Rule[] rules = {};
            
            RuleExecutionSet res = new JamochaRuleExecutionSet(description,name,rules);
            
            return res;
            
        } catch (Exception ex) {
            throw new RuleExecutionSetCreateException("error while parsing or creating rule set",ex);
        }
	}

	public RuleExecutionSet createRuleExecutionSet(Reader reader, Map properties) throws RuleExecutionSetCreateException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public RuleExecutionSet createRuleExecutionSet(Object object, Map properties) throws RuleExecutionSetCreateException {
		// TODO Auto-generated method stub
		return null;
	}

}
