/*
 * Copyright 2007 Markus Kucay 
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
import org.jamocha.parser.sl_old.SimpleNode;

/**
 * This class walks through an SL code tree and translates it to CLIPS depending
 * on the given performative.
 * 
 * @author Markus Kucay
 * 
 */
public class RequestWhen extends SLPerformative{

	/**
	 * A private constructor to force access only in a static way.
	 * 
	 */
	private RequestWhen() {
	}

	/**
	 * Translates SL code of a request-when to CLIPS code. A request-when contains
	 * one action and one proposition.
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
		SimpleNode rn;
		try {
			rn = parser.Content(); // RootNode
		} catch (ParseException e) {
			throw new AdapterTranslationException("Could not translate from SL to CLIPS.", e);
		}
		
		SimpleNode ContentNode 	= rn; //getChild(rn, 0);
		
		String rightHand 	= handleContentExpression(getChild(ContentNode, 0));
		String leftHand 	= handleContentExpression(getChild(ContentNode, 1));
		
		result.append("(defrule request-when " + leftHand + " => " + rightHand + "(undefrule request-when))");
				
		return result.toString();
	}


//	/**
//	 * Recursively walks through the tree of Parameters and translates them.
//	 * 
//	 * @param node
//	 *            The node to start from.
//	 * @return Either the bind if the node was a Fact or the value of the node
//	 *         if it was a Constant.
//	 */
//	public static String resolveParameters(SimpleNode node) {
//		// Go From Parameter to Node after Term
//		SimpleNode currNode = node;
//		while (currNode.getID() != SLParserTreeConstants.JJTTERM) {
//			currNode = getChild(currNode, 0);
//		}
//		currNode = getChild(currNode, 0);
//		if (currNode.getID() == SLParserTreeConstants.JJTFUNCTIONALTERM) {
//			// This would be a Fact for CLIPS
//
//			StringBuilder buffer = new StringBuilder((" (assert ("
//					+ getChild(currNode, 0).getText() + " "));
//			for (int i = 1; i < currNode.jjtGetNumChildren(); ++i) {
//				SimpleNode child = getChild(currNode, i);
//				String paramName = getChild(child, 0).getText().substring(1);
//				String childAssert = resolveParameters(getChild(child, 1));
//				buffer.append("(").append(paramName).append(" ").append(
//						childAssert).append(")");
//			}
//			buffer.append("))");
//			// System.out.println(buffer);
//			return buffer.toString();
//		} else if (currNode.getID() == SLParserTreeConstants.JJTCONSTANT) {
//			// Here we have simple constants
//
//			currNode = getChild(currNode, 0);
//			if (currNode.getID() == SLParserTreeConstants.JJTSTRING) {
//				// A String directly contains its value
//				return "\"" + currNode.getText() + "\"";
//			} else if (currNode.getID() == SLParserTreeConstants.JJTNUMERICALCONSTANT) {
//				// A numerical constant first has a node describing the type of
//				// the number (e.g. Integer). We don't check it here.
//				currNode = getChild(currNode, 0);
//				return currNode.getText();
//			}
//		} else if (currNode.getID() == SLParserTreeConstants.JJTSEQUENCE) {
//			// Here we have a MultiSlot in CLIPS
//
//			StringBuilder buffer = new StringBuilder();
//
//			for (int i = 0; i < currNode.jjtGetNumChildren(); ++i) {
//				String childBind = resolveParameters(getChild(currNode, i));
//				if (i > 0)
//					buffer.append(" ");
//				buffer.append(childBind);
//			}
//			return buffer.toString();
//		}
//		return "";
//	}
//
//	/**
//	 * Recusively walks through a Wff and converts it to Clips.
//	 * 
//	 * @param node 
//	 * 				the node to start from
//	 * 
//	 * @return a premisse for a Clips rule 
//	 */
//	public static String resolveWff(SimpleNode node){
//		
//		SimpleNode currNode = getChild(node, 0);
//		
//		//Atomic Formula
//		if(currNode.getID() == SLParserTreeConstants.JJTATOMICFORMULA){
//		
//			//Propositionsymbol (contains directly its value)
//			if(getChild(currNode,0).getID() == SLParserTreeConstants.JJTPROPOSITIONSYMBOL){
//				return "(" + getChild(currNode,0).getText() + ")";
//			
//			//Binary Term Operator (Equal or Result)
//			} else if(getChild(currNode,0).getID() == SLParserTreeConstants.JJTBINARYTERMOP){
//				
//				currNode = getChild(currNode,0);
//				if(currNode.getID() == SLParserTreeConstants.JJTEQUAL){
//					return "(eq " + resolveParameters(getChild(currNode,0)) + " " + resolveParameters(getChild(currNode, 1)) + ")";
//				}
//				else if (getChild(currNode,0).getID() == SLParserTreeConstants.JJTRESULT){
//					return "(result " + resolveParameters(getChild(currNode, 1)) + " " + resolveParameters(getChild(currNode, 2)) + ")";
//				}
//			
//			//Predicatesymbol (n-ary => n Childs to resolve)
//			} else if(getChild(currNode,0).getID() == SLParserTreeConstants.JJTPREDICATESYMBOL){
//				StringBuilder buffer = new StringBuilder();
//				buffer.append("(" + getChild(currNode, 0).getText());
//				for(int i = 1; i < currNode.jjtGetNumChildren(); i++){
//					buffer.append(" " + resolveParameters(getChild(currNode, i)));
//				}					
//				buffer.append(")");
//				return buffer.toString();
//				
//			//TRUE or FALSE
//			} else if(currNode.getID() == SLParserTreeConstants.JJTTRUE){
//				return "(TRUE)";
//			} else if(currNode.getID() == SLParserTreeConstants.JJTFALSE){
//				return "(FALSE)";
//			}
//		
//	//Binary Logical Operator (AND)
//	} else if(currNode.getID() == SLParserTreeConstants.JJTAND){ 
//		
//		return "(and " + resolveWff(getChild(node, 0)) 
//				+ " " + resolveWff(getChild(currNode,1)) + ")";
//	
//	//Binary Logical Operator (OR)
//	} else if(currNode.getID() == SLParserTreeConstants.JJTOR){ 
//	
//		return "(or " + resolveWff(getChild(node, 0)) 
//			+ " " + resolveWff(getChild(currNode,1)) + ")";
//
//	//Binary Logical Operator (IMPLIES, x -> y = -x v y)
//	} else if(currNode.getID() == SLParserTreeConstants.JJTIMPLIES){
//		return "(or not(" + resolveWff(getChild(currNode,0)) + ") "
//				+ resolveWff(getChild(currNode,1)) + ")";
//		
//	//Binary Logical Operator (EQUIVALENT, x <-> y = (-x v y) ^ (x v -y))
//	} else if(currNode.getID() == SLParserTreeConstants.JJTEQUIV){
//		String x = resolveWff(getChild(currNode,0));
//		String y = resolveWff(getChild(currNode,1));
//		return "(and (or (not " + x + ") " + y + ") (or " + x + " (not " + y + ")))";
//		
//	//Unary Logical Operator
//	} else if(currNode.getID() == SLParserTreeConstants.JJTNOT){
//		return "(not " + resolveWff(getChild(node,0)) + ")";
//		
//	//Quantifier (ForAll)
//	} else if(currNode.getID() == SLParserTreeConstants.JJTFORALL){
//		return "(forall " + resolveWff(getChild(node,1)) + ")";
//	
//	//Quantifier (Exists)
//	} else if(currNode.getID() == SLParserTreeConstants.JJTEXISTS){
//		return "(exists " + resolveWff(getChild(node,1)) + ")";
//
//	//Modal Operator (B)
//	} else if(currNode.getID() == SLParserTreeConstants.JJTB){
//	
//	//Modal Operator (U)
//	} else if(currNode.getID() == SLParserTreeConstants.JJTU){
//	
//	//Modal Operator (PG)
//	} else if(currNode.getID() == SLParserTreeConstants.JJTPG){
//		
//	//Modal Operator (I)
//	} else if(currNode.getID() == SLParserTreeConstants.JJTI){
//
//	//Action Operator
//	} else if(currNode.getID() == SLParserTreeConstants.JJTACTIONOP){
//		
//		//ActionOP + ActionExpression
//		if(currNode.jjtGetNumChildren() == 2){
//			if(getChild(currNode,0).getID() == SLParserTreeConstants.JJTFEASIBLE){
//				
//			}
//			else if(getChild(currNode,0).getID() == SLParserTreeConstants.JJTDONE){
//				return "(done " + resolveParameters(getChild(currNode,1)) + ")";
//			}
//		}
//		
//		//ActionOP + ActionExpression + WFF
//		else if(currNode.jjtGetNumChildren() == 3){}
//			if(getChild(currNode,0).getID() == SLParserTreeConstants.JJTFEASIBLE){
//			
//			}
//			else if(getChild(currNode,0).getID() == SLParserTreeConstants.JJTDONE){
//				return "(done " + resolveParameters(getChild(currNode,1)) + ")";
//			}
//	}else if(currNode.getID() == SLParserTreeConstants.JJTWFF){
//		return resolveWff(getChild(currNode, 0));
//	}
//	return "";
//}
//
//	
//	/**
//	 * Returns a child of SimpleNode directly as SimpleNode.
//	 * 
//	 * @param node
//	 *            The node whose child is of interest.
//	 * @param no
//	 *            The index of the child.
//	 * @return The child node.
//	 */
//	public static SimpleNode getChild(SimpleNode node, int no) {
//		return (SimpleNode) node.jjtGetChild(no);
//	}
//
//	/**
//	 * Returns the leftmost child at a certain level of the tree. Level means
//	 * here that we walk through the tree downwards beginning at
//	 * <code>node</code> for <code>level</code> levels.
//	 * 
//	 * @param node
//	 *            The node we start from.
//	 * @param level
//	 *            The level we walk through.
//	 * @return The child node at the specified level.
//	 */
//	public static SimpleNode getChildAtLevel(SimpleNode node, int level) {
//		for (int i = 0; i < level; ++i) {
//			node = getChild(node, 0);
//		}
//		return node;
//	}

}
