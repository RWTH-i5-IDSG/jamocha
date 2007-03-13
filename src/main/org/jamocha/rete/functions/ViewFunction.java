/*
 * Copyright 2006 Josef Hahn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.RootNode;
import org.jamocha.rete.WorkingMemoryImpl;
import org.jamocha.rete.visualisation.ViewGraphNode;
import org.jamocha.rete.visualisation.Visualiser;



/**
 * @author Josef Alexander Hahn
 * 
 * Opens a visualisation window for the rete net
 */
public class ViewFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String VIEW = "view";

	/**
	 * 
	 */
	public ViewFunction() {
		super();
	}

	public JamochaType getReturnType() {
		return JamochaType.NIL;
	}

	void traverse(int indent, BaseNode b){
		for (int i=0;i<indent;i++) System.out.print(" ");
		System.out.println("+"+b.toString()+" id="+b.getNodeId());
		for(int i=0;i<b.getSuccessorNodes().length;i++) traverse(indent+2,(BaseNode)b.getSuccessorNodes()[i]);
	}
	
	
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
		RootNode root=((WorkingMemoryImpl)engine.getWorkingMemory()).getRootNode();

		/*Collection firstLevel=root.getObjectTypeNodes().values();
		for (Iterator iter = firstLevel.iterator(); iter.hasNext();) {
			BaseNode b=(BaseNode)iter.next();
			traverse(0,b);
		}*/
		
		
		@SuppressWarnings("unused")
		ViewGraphNode t=ViewGraphNode.buildFromRete(root);
		Visualiser visualiser=new Visualiser(engine);
		visualiser.show();
		return JamochaValue.NIL;
	}

	public String getName() {
		return VIEW;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(view)";
	}
}
