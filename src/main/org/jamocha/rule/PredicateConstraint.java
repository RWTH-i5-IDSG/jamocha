/*
 * Copyright 2002-2007 Peter Lin
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
package org.jamocha.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.SFRuleCompiler;

/**
 * @author Peter Lin
 * 
 * Predicate constraint binds the slot and then performs some function on it.
 * For example (myslot ?s&:(> ?s 100) )
 * 
 */
public class PredicateConstraint extends AbstractConstraint {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	/**
	 * the name of the function
	 */
	protected String functionName = null;

	protected ArrayList<Parameter> parameters = new ArrayList<Parameter>();

	/**
	 * 
	 */
	public PredicateConstraint() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Constraint#getName()
	 */


	public String getFunctionName() {
		return this.functionName;
	}

	public void setFunctionName(String func) {
		this.functionName = func;
	}

	public void addParameter(Parameter params) {
		this.parameters.add(params);
	}

	public int parameterCount() {
		return this.parameters.size();
	}

	public boolean getNegated() {
		return false;
	}

	public void setNegated(boolean negate) {
		// TODO Auto-generated method stub

	}
	
	public BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return compiler.compile(this, rule, conditionIndex);
	}

	public String toClipsFormat(int indent) {
		String ind = "";
		while (ind.length() < indent*blanksPerIndent) ind+=" ";
		StringBuilder sb = new StringBuilder();
		sb.append(ind+"(" + getFunctionName());
		for (Parameter param : parameters) {
			sb.append(" ");
			sb.append(param.toClipsFormat(0));
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public JamochaValue getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(JamochaValue val) {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<Parameter> getParameters() {
		return parameters;
	}
}
