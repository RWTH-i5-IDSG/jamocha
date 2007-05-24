/*
 * Copyright 2007 Daniel put your name here and write the fantastic code
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
import org.jamocha.parser.sl.ParseException;
import org.jamocha.parser.sl.SLParser;
import org.jamocha.parser.sl.SLParserTreeConstants;
import org.jamocha.parser.sl.SimpleNode;
import org.jamocha.parser.sl.TokenMgrError;

/**
 * This class walks through an SL code tree and translates it to CLIPS depending
 * on the given performative.
 * 
 * @author Alexander Wilden
 * 
 */
public class Subscribe {

	/**
	 * A private constructor to force access only in a static way.
	 * 
	 */
	private Subscribe() {
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
	
	/*
	 * Example: ((iota ?x (= ?x (xch-rate FFR USD)))))
	 * Parser Tree:
	 * 	Content
		 ContentExpression
		  IdentifyingExpression
		   Iota
		    TermOrIE
		     Term
		      Variable: ?x
		    Wff
		     AtomicFormula
		      BinaryTermOp
		       Equal
		        TermOrIE
		         Term
		          Variable: ?x
		        TermOrIE
		         Term
		          FunctionalTerm
		           FunctionSymbol: xch-rate
		           TermOrIE
		            Term
		             Constant
		              String: FFR
		           TermOrIE
		            Term
		             Constant
		              String: USD
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
		
		// ReferentialOperator
		sn = getChildAtLevel(sn, 3);
		switch (sn.getID()) {
			case SLParserTreeConstants.JJTIOTA:
				result.append("IOTA");
				break;
			case SLParserTreeConstants.JJTALL:
				result.append("ALL");
				break;
			case SLParserTreeConstants.JJTANY:
				result.append("ANY");
				break;
			default:
				throw new AdapterTranslationException("Could not translate from SL to CLIPS.");
		}
		
		// BinaryTermOp
		sn = getChild(sn, 1);
		sn = getChildAtLevel(sn, 3);
		if (sn.getID() == SLParserTreeConstants.JJTEQUAL) {
			result.append("EQUAL");
		} else if (sn.getID() == SLParserTreeConstants.JJTRESULT) { 
			result.append("RESULT");
		} else {
			throw new AdapterTranslationException("Could not translate from SL to CLIPS.");
		}
		
		
		
		
		
		result.append(getChildAtLevel(sn, 7).getID());
		
		if (sn.getID() == SLParserTreeConstants.JJTACTION) {
			// Here we have an Action
			// Get the right Child. The left Child is the agent-identifier.
			sn = getChild(sn, 1);
			sn = getChildAtLevel(sn, 2);
			String functionName = getChild(sn, 0).getText();
			String lastAssert = resolveParameters(getChild(getChild(sn, 1), 1));

			result.append("(" + functionName + " " + lastAssert + ")");
		}

		return result.toString();
	}

	/**
	 * Recursively walks through the tree of Parameters and translates them.
	 * 
	 * @param node
	 *            The node to start from.
	 * @return Either the bind if the node was a Fact or the value of the node
	 *         if it was a Constant.
	 */
	public static String resolveParameters(SimpleNode node) {
		// Go From Parameter to Node after Term
		SimpleNode currNode = node;
		while (currNode.getID() != SLParserTreeConstants.JJTTERM) {
			currNode = getChild(currNode, 0);
		}
		currNode = getChild(currNode, 0);
		if (currNode.getID() == SLParserTreeConstants.JJTFUNCTIONALTERM) {
			// This would be a Fact for CLIPS

			StringBuilder buffer = new StringBuilder((" (assert ("
					+ getChild(currNode, 0).getText() + " "));
			for (int i = 1; i < currNode.jjtGetNumChildren(); ++i) {
				SimpleNode child = getChild(currNode, i);
				String paramName = getChild(child, 0).getText().substring(1);
				String childAssert = resolveParameters(getChild(child, 1));
				buffer.append("(").append(paramName).append(" ").append(
						childAssert).append(")");
			}
			buffer.append("))");
			// System.out.println(buffer);
			return buffer.toString();
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
				String childBind = resolveParameters(getChild(currNode, i));
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
