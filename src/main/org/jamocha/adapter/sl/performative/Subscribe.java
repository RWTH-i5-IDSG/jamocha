/*
 * Copyright 2007 Georg Jennessen
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
	
	public static String handleWff(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);
		
		switch (sn.getID()) {
		
			// Atomic Formula
			case SLParserTreeConstants.JJTATOMICFORMULA:
				result.append(handleAtomicFormula(sn));
				break;
				
			// not
			case SLParserTreeConstants.JJTNOT:
				result.append("(not " + handleWff(getChild(sn, 0)) + ")");
				break;

			// and
			case SLParserTreeConstants.JJTAND:
				result.append("(and " + handleWff(getChild(sn, 0)) + handleWff(getChild(sn, 1)) + ")");				
				break;
			
			// or	
			case SLParserTreeConstants.JJTOR:
				result.append("(or " + handleWff(getChild(sn, 0)) + handleWff(getChild(sn, 1)) + ")");	
				break;
				
			// implies
			case SLParserTreeConstants.JJTIMPLIES:
				result.append("(implies " + handleWff(getChild(sn, 0)) + handleWff(getChild(sn, 1)) + ")");	
				break;
			
			// equiv
			case SLParserTreeConstants.JJTEQUIV:
				result.append("(equiv " + handleWff(getChild(sn, 0)) + handleWff(getChild(sn, 1)) + ")");	
				break;
				
			// forall
			case SLParserTreeConstants.JJTFORALL:
				result.append("(forall " + handleVariable(getChild(sn, 0)) + handleWff(getChild(sn, 1)) + ")");	
				break;
				
			// exists
			case SLParserTreeConstants.JJTEXISTS:
				result.append("(exists " + handleVariable(getChild(sn,0)) + handleWff(getChild(sn, 1)) + ")");	
				break;
			
			// actionop
			case SLParserTreeConstants.JJTACTIONOP:
				result.append("(" + handleActionOp(getChild(sn, 0)) + ")");
				break;
				
		}
		return result.toString();
	}
	
	public static String handleAtomicFormula(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);
		
		switch (sn.getID()) {
		
			// PropositionSymbol
			case SLParserTreeConstants.JJTPROPOSITIONSYMBOL:
				result.append(handlePropositionSymbol(sn));
				break;
			
			// BinaryTermOp
			case SLParserTreeConstants.JJTBINARYTERMOP:
				result.append("(" + handleBinaryTermOp(sn) + ")");
				break;
				
			// PredicateSymbol
			case SLParserTreeConstants.JJTPREDICATESYMBOL:
				result.append("(" + handlePredicateSymbol(sn) + " ");
				
				for (int i = 1; i < node.jjtGetNumChildren(); i++){
					sn = getChild(node, i);
					if (sn.getID() == SLParserTreeConstants.JJTTERMORIE) {
						result.append(handleTermOrIE(sn) + " ");
					}
				}
				
				result.append(")");
				
				break;	
				
			// True
			case SLParserTreeConstants.JJTTRUE:
				result.append("true");
				break;
				
			// False
			case SLParserTreeConstants.JJTFALSE:
				result.append("false");
				break;
				
		}
		
		return result.toString();
	}
	
	public static String handleContent(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);
		result.append("(" + handleContentExpression(sn) + ")");
		return result.toString();
	}
	
	public static String handleContentExpression(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);
		
		switch (sn.getID()) {
		
			// Identifying Expression
			case SLParserTreeConstants.JJTIDENTIFYINGEXPRESSION:
				result.append(handleIdentifyingExpression(sn));
				break;
				
			// Action Expression
			case SLParserTreeConstants.JJTACTIONEXPRESSION:
				result.append(handleActionExpression(sn));
				break;
				
			// Proposition = Wff
			case SLParserTreeConstants.JJTWFF:
				result.append(handleWff(sn));
				break;
		}
		
		return result.toString();
	}
	
	public static String handleNumericalConstant(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);
		result.append(sn.getText());
		return result.toString();
	}
	
	public static String handlePredicateSymbol(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append(node.getText());
		return result.toString();
	}
	
	public static String handleTermOrIE(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);

		if (sn.getID() == SLParserTreeConstants.JJTTERM) {
			result.append(handleTerm(sn));
		} else if (sn.getID() == SLParserTreeConstants.JJTIDENTIFYINGEXPRESSION) {
			result.append(handleIdentifyingExpression(sn));
		}
		
		return result.toString();
	}
	
	public static String handleEqual(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append("eq " + handleTermOrIE(getChild(node, 0)) + " " + handleTermOrIE(getChild(node, 1)));
		return result.toString();
	}
	
	public static String handleResult(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append("result ");
		return result.toString();
	}
	
	public static String handlePropositionSymbol(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append(node.getText());
		return result.toString();
	}
	
	public static String handleBinaryTermOp(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);
		
		if (sn.getID() == SLParserTreeConstants.JJTEQUAL) {
			result.append(handleEqual(sn));
		} else if (sn.getID() == SLParserTreeConstants.JJTRESULT) {
			result.append(handleResult(sn));
		}
		
		return result.toString();
	}
	
	public static String handleVariable(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = node;
		result.append(sn.getText());
		return result.toString();
	}
	
	public static String handleDone(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append("done ");
		return result.toString();
	}
	
	public static String handleFeasible(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);
		result.append("feasible ");
		return result.toString();
	}
	
	public static String handleActionOp(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		
		if (node.getText() == "done") {
			result.append(handleDone(node));
		} else if (node.getText() == "feasible") {
			result.append(handleFeasible(node));
		}
		
		return result.toString();
	}
	
	public static String handleAction(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append(handleAgent(getChild(node, 0)) + " " + handleTermOrIE(getChild(node, 1)));
		return result.toString();
	}
	
	public static String handleActionOr(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append("| " + handleActionExpression(getChild(node, 0)) + " " + handleActionExpression(getChild(node, 1)));
		return result.toString();
	}
	
	public static String handleActionAnd(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append("& " + handleActionExpression(getChild(node, 0)) + " " + handleActionExpression(getChild(node, 1)));
		return result.toString();
	}
	
	public static String handleActionExpression(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);
		result.append("(");
		
		if (sn.getID() == SLParserTreeConstants.JJTACTION) {
			result.append(handleAction(sn));
		} else if (sn.getText() == "|") {
			result.append(handleActionOr(sn));
		} else if (sn.getText() == ";") {
				result.append(handleActionAnd(sn));
		}
		
		result.append(")");
		return result.toString();
	}
	
	public static String handleTerm(SimpleNode node) throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);
		
		switch (sn.getID()) {
		
			case SLParserTreeConstants.JJTVARIABLE:
				result.append(handleVariable(sn));
				break;
				
			case SLParserTreeConstants.JJTFUNCTIONALTERM:
				result.append(handleFunctionalTerm(sn));
				break;
				
			case SLParserTreeConstants.JJTACTIONEXPRESSION:
				result.append(handleActionExpression(sn));
				break;
				
			case SLParserTreeConstants.JJTCONSTANT:
				result.append(handleConstant(sn));
				break;
				
			case SLParserTreeConstants.JJTSEQUENCE:
				result.append(handleSequence(sn));
				break;
				
			case SLParserTreeConstants.JJTSET:
				result.append(handleSet(sn));
				break;
		}
		
		return result.toString();
	}
	
	public static String handleFunctionSymbol(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append(node.getText());
		return result.toString();
	}
	
	public static String handleAgent(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append(handleTermOrIE(getChild(node, 0)));
		return result.toString();
	}
	
	public static String handleParameterValue(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append(handleTermOrIE(getChild(node, 0)));
		return result.toString();
	}
	
	public static String handleFunctionalTerm(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);
		result.append("(" + sn.getText() + " ");
		
		for (int i = 1; i < node.jjtGetNumChildren(); i++){
			sn = getChild(node, i);
			if (sn.getID() == SLParserTreeConstants.JJTTERMORIE) {
				result.append(handleTermOrIE(sn) + " ");
			} else if (sn.getID() == SLParserTreeConstants.JJTPARAMETER) {
				result.append(handleParameter(sn) + " ");
			}
		}

		result.append(")");
		return result.toString();
	}
	
	public static String handleParameter(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append(handleParameterName(getChild(node, 0)) + " " + handleParameterValue(getChild(node, 1)));
		return result.toString();
	}
	
	public static String handleParameterName(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append(node.getText());
		return result.toString();
	}
	
	public static String handleConstant(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);
		
		if (sn.getID() == SLParserTreeConstants.JJTNUMERICALCONSTANT) {
			result.append(handleNumericalConstant(sn));
		} else if ((sn.getID() == SLParserTreeConstants.JJTSTRING) || (sn.getID() == SLParserTreeConstants.JJTDATETIME)) {
			result.append(sn.getText());
		}
		
		return result.toString();
	}
	
	public static String handleSequence(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append("(" + "sequence" + " ");
		
		for (int i = 0; i < node.jjtGetNumChildren(); i++){
			SimpleNode sn = getChild(node, i);
			if (sn.getID() == SLParserTreeConstants.JJTTERMORIE) {
				result.append(handleTermOrIE(sn) + " ");
			}
		}
		
		result.append(")");
		return result.toString();
	}
	
	public static String handleSet(SimpleNode node)  throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		result.append("(" + "set" + " ");
		
		for (int i = 0; i < node.jjtGetNumChildren(); i++){
			SimpleNode sn = getChild(node, i);
			if (sn.getID() == SLParserTreeConstants.JJTTERMORIE) {
				result.append(handleTermOrIE(sn) + " ");
			}
		}
		
		result.append(")");
		return result.toString();
	}
	
	public static String handleReferentialOperator(SimpleNode node) throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		
		// ReferentialOperator
		SimpleNode sn = node;
		switch (sn.getID()) {
			case SLParserTreeConstants.JJTIOTA:
				result.append("iota ");
				break;
			case SLParserTreeConstants.JJTALL:
				result.append("all ");
				break;
			case SLParserTreeConstants.JJTANY:
				result.append("any ");
				break;
			default:
				throw new AdapterTranslationException("Could not translate from SL to CLIPS.");
		}
		
		// Term or IE
		sn = getChild(sn, 0);
		result.append(handleTermOrIE(sn) + " ");
		
		// Wff
		sn = getChild(node, 1);
		result.append(handleWff(sn));		
			
		return result.toString();
	}
	
	
	public static String handleIdentifyingExpression(SimpleNode node) throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		SimpleNode sn = getChild(node, 0);
		
		result.append("(" + handleReferentialOperator(sn) + ")");
		return result.toString();
	}
	
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
		
		//sn = getChildAtLevel(sn, 2);
		result.append("(defrule Subscribe " + handleContent(sn) + " => Sachwat)");
		
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

