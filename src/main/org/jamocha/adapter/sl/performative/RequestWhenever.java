/*
 * Copyright 2007 Daniel Grams
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
import org.jamocha.parser.sl.SimpleNode;

/**
 * This class walks through an SL code tree and translates it to CLIPS depending
 * on the given performative.
 * 
 * @author Daniel Grams
 * 
 */
public class RequestWhenever extends SLPerformative {

	/**
	 * A private constructor to force access only in a static way.
	 * 
	 */
	private RequestWhenever() {
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
	 * Example: 
	 * ((action (agent-identifier :name j)
	      (inform-ref
	        :sender (agent-identifier :name j)
	        :receiver (set (agent-identifier :name i))
	        :content
	          \"\"))
     	(> (price widget) 50))
     	
     Parser Tree:	
	    Content
		 ContentExpression
		  ActionExpression
		   Action
		    Agent
		     TermOrIE
		      Term
		       FunctionalTerm
		        FunctionSymbol: agent-identifier
		        Parameter
		         ParameterName: :name
		         ParameterValue
		          TermOrIE
		           Term
		            Constant
		             String: j
		    TermOrIE
		     Term
		      FunctionalTerm
		       FunctionSymbol: inform-ref
		       Parameter
		        ParameterName: :sender
		        ParameterValue
		         TermOrIE
		          Term
		           FunctionalTerm
		            FunctionSymbol: agent-identifier
		            Parameter
		             ParameterName: :name
		             ParameterValue
		              TermOrIE
		               Term
		                Constant
		                 String: j
		       Parameter
		        ParameterName: :receiver
		        ParameterValue
		         TermOrIE
		          Term
		           Set
		            TermOrIE
		             Term
		              FunctionalTerm
		               FunctionSymbol: agent-identifier
		               Parameter
		                ParameterName: :name
		                ParameterValue
		                 TermOrIE
		                  Term
		                   Constant
		                    String: i
		       Parameter
		        ParameterName: :content
		        ParameterValue
		         TermOrIE
		          Term
		           Constant
		            String: \"\"
		 ContentExpression
		  Wff
		   AtomicFormula
		    PredicateSymbol: >
		    TermOrIE
		     Term
		      FunctionalTerm
		       FunctionSymbol: price
		       TermOrIE
		        Term
		         Constant
		          String: widget
		    TermOrIE
		     Term
		      Constant
		       NumericalConstant
		        Integer: 50
	 */
	
		
	
	public static String getCLIPS(String slContent) throws AdapterTranslationException {
		StringBuilder result = new StringBuilder();
		
		SLParser parser = new SLParser(new StringReader(slContent));
		SimpleNode rn;
		try {
			rn = parser.Content(); // RootNode
		} catch (ParseException e) {
			throw new AdapterTranslationException("Could not translate from SL to CLIPS.", e);
		}
		
		SimpleNode ContentNode 	= rn; //getChild(rn, 0);
		
		String RightHand 	= handleContentExpression(getChild(ContentNode, 0));
		String LeftHand 	= handleContentExpression(getChild(ContentNode, 1));
		
		result.append("(defrule RequestWhenever " + LeftHand + " => " + RightHand + ")");
		
			
		return result.toString();
	}

}
