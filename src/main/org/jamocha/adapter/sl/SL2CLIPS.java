/*
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
 */
package org.jamocha.adapter.sl;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.jamocha.adapter.AdapterTranslationException;
import org.jamocha.parser.sl.ParseException;
import org.jamocha.parser.sl.SLParser;
import org.jamocha.parser.sl.SLParserTreeConstants;
import org.jamocha.parser.sl.SimpleNode;

/**
 * This class walks through an SL code tree and translates it to CLIPS depending
 * on the given performative.
 * 
 * @author Alexander Wilden
 * 
 */
public class SL2CLIPS {

	private static short bindPrefix = 0;

	/**
	 * A private constructor to force access only in a static way.
	 * 
	 */
	private SL2CLIPS() {
	}

	/**
	 * Translates SL code of a request to CLIPS code. A request only contains
	 * one action.
	 * 
	 * @param slContent
	 *            The SL content we have to translate.
	 * @return CLIPS commands that represent the given SL code.
	 * @throws AdapterTranslationException
	 *             if the SLParser throws an Exception or anything else unnormal
	 *             happens.
	 */
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
		sn = getChildAtLevel(sn, 3);
		if (sn.getID() == SLParserTreeConstants.JJTACTION) {
			// Here we have an Action
			// Get the right Child. The left Child is the agent-identifier.
			sn = getChild(sn, 1);
			sn = getChildAtLevel(sn, 2);
			String functionName = getChild(sn, 0).getText();
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

	/**
	 * Recursively walks through the tree of Parameters and translates them.
	 * 
	 * @param node
	 *            The node to start from.
	 * @param asserts
	 *            The asserts we got through translation.
	 * @param bind
	 *            The next bind that should be used for following Facts.
	 * @return Either the bind if the node was a Fact or the value of the node
	 *         if it was a Constant.
	 */
	public static String resolveParameters(SimpleNode node,
			List<String> asserts, String bind) {
		// Go From Parameter to Node after Term
		SimpleNode currNode = node;
		while (currNode.getID() != SLParserTreeConstants.JJTTERM) {
			currNode = getChild(currNode, 0);
		}
		currNode = getChild(currNode, 0);
		if (currNode.getID() == SLParserTreeConstants.JJTFUNCTIONALTERM) {
			// This would be a Fact for CLIPS

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
			// Here we have simple constants

			currNode = getChild(currNode, 0);
			if (currNode.getID() == SLParserTreeConstants.JJTSTRING) {
				// A String directly contains its value
				return "\"" + currNode.getText() + "\"";
			} else if (currNode.getID() == SLParserTreeConstants.JJTNUMERICALCONSTANT) {
				// A numerical constant first has a node describing the type of
				// the number (e.g. Integer). We don't check it here.
				currNode = getChild(currNode, 0);
				return currNode.getText();
			}
		} else if (currNode.getID() == SLParserTreeConstants.JJTSEQUENCE) {
			// Here we have a MultiSlot in CLIPS

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
		return "";
	}

	/**
	 * Returns a child of SimpleNode directly as SimpleNode.
	 * 
	 * @param node
	 *            The node whose child is of interest.
	 * @param no
	 *            The index of the child.
	 * @return The child node.
	 */
	public static SimpleNode getChild(SimpleNode node, int no) {
		return (SimpleNode) node.jjtGetChild(no);
	}

	/**
	 * Returns the leftmost child at a certain level of the tree. Level means
	 * here that we walk through the tree downwards beginning at
	 * <code>node</code> for <code>level</code> levels.
	 * 
	 * @param node
	 *            The node we start from.
	 * @param level
	 *            The level we walk through.
	 * @return The child node at the specified level.
	 */
	public static SimpleNode getChildAtLevel(SimpleNode node, int level) {
		for (int i = 0; i < level; ++i) {
			node = getChild(node, 0);
		}
		return node;
	}

}
