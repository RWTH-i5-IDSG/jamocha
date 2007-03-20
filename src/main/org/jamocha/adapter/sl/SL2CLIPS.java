package org.jamocha.adapter.sl;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.jamocha.adapter.AdapterTranslationException;
import org.jamocha.parser.sl.ParseException;
import org.jamocha.parser.sl.SLParser;
import org.jamocha.parser.sl.SLParserTreeConstants;
import org.jamocha.parser.sl.SimpleNode;

public class SL2CLIPS {

	private static short bindPrefix = 0;

	public static String getCLIPSFromRequest(String slContent)
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
		for (int i = 0; i < 3; ++i)
			sn = (SimpleNode) sn.jjtGetChild(0);
		if (sn.getID() == SLParserTreeConstants.JJTACTION) {
			// Here we have an Action
			// Get the right Child. The left Child is the agent-identifier.
			sn = (SimpleNode) sn.jjtGetChild(1);
			sn = getChildAtLevel(sn, 2);
			String functionName = ((SimpleNode) sn.jjtGetChild(0)).getText();
			List<String> asserts = new LinkedList<String>();
			String lastBind = resolveParameters(getChild(getChild(sn, 1), 1),
					asserts, "?p" + bindPrefix++);

			for (String sAssert : asserts) {
				result.append(sAssert + "\n");
			}
			result.append("(" + functionName + " " + lastBind + ")");
		}
		
		return result.toString();
	}

	public static String resolveParameters(SimpleNode node,
			List<String> asserts, String bind) {
		// Go From Parameter to Node after Term
		SimpleNode currNode = node;
		while (currNode.getID() != SLParserTreeConstants.JJTTERM) {
			currNode = getChild(currNode, 0);
		}
		currNode = getChild(currNode, 0);
		if (currNode.getID() == SLParserTreeConstants.JJTFUNCTIONALTERM) {

			StringBuilder buffer = new StringBuilder(("(bind " + bind
					+ " (assert (" + getChild(currNode, 0).getText() + " "));
			for (int i = 1; i < currNode.jjtGetNumChildren(); ++i) {
				SimpleNode child = getChild(currNode, i);
				String paramName = getChild(child, 0).getText().substring(1);
				String childBind = resolveParameters(getChild(child, 1),
						asserts, bind + i);
				buffer.append("(" + paramName + " " + childBind + ")");
			}
			buffer.append(")))");
			asserts.add(buffer.toString());
			// System.out.println(buffer);
			return bind;
		} else if (currNode.getID() == SLParserTreeConstants.JJTCONSTANT) {
			currNode = getChild(currNode, 0);
			if (currNode.getID() == SLParserTreeConstants.JJTSTRING) {
				return "\"" + currNode.getText() + "\"";
			} else if (currNode.getID() == SLParserTreeConstants.JJTNUMERICALCONSTANT) {
				currNode = getChild(currNode, 0);
				return currNode.getText();
			}
		} else if (currNode.getID() == SLParserTreeConstants.JJTSEQUENCE) {

			StringBuilder buffer = new StringBuilder();

			for (int i = 0; i < currNode.jjtGetNumChildren(); ++i) {
				String childBind = resolveParameters(getChild(currNode, i),
						asserts, bind + i);
				if (i > 0)
					buffer.append(" ");
				buffer.append(childBind);
			}
			return buffer.toString();
		}
		// System.out.println(currNode);
		return "";
	}

	public static SimpleNode getChild(SimpleNode node, int no) {
		return (SimpleNode) node.jjtGetChild(no);
	}

	public static SimpleNode getChildAtLevel(SimpleNode node, int level) {
		for (int i = 0; i < level; ++i) {
			node = getChild(node, 0);
		}
		return node;
	}

}
