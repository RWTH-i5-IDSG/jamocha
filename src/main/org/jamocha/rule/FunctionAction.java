/*
 * Copyright 2002-2006 Peter Lin
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
package org.jamocha.rule;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Binding;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.configurations.AbstractConfiguration;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.functions.Function;
import org.jamocha.rete.functions.ruleengine.Assert;

/**
 * @author Peter Lin
 * 
 * A FunctionAction is responsible for executing a function in the action of the
 * rule. It uses built-in or user written functions. When the rule is loaded,
 * the engine looks up the functions. At run time, the rule simply executes it.
 */
public class FunctionAction implements Action {

	public Object clone() throws CloneNotSupportedException {
		FunctionAction result = new FunctionAction();
		
		result.function = function;
		result.functionName = functionName;
		result.parameters = new Parameter[parameters.length];
		for (int i=0 ; i < parameters.length ; i++){
			result.parameters[i] = (Parameter)parameters[i].clone();
		}
		
		return result;
	}
	
	private static final long serialVersionUID = 1L;

	protected Function function = null;

	protected String functionName = null;

	protected Parameter[] parameters = null;

	public FunctionAction() {
		super();
	}

	public Function getFunction() {
		return this.function;
	}

	public void setFunction(Signature func) {
		this.functionName = func.getSignatureName();
		this.parameters = func.getParameters();
	}

	public String getFunctionName() {
		return this.functionName;
	}

	public void setFunctionName(String name) {
		this.functionName = name;
	}

	public Expression[] getParameters() {
		return this.parameters;
	}

	public void setParameters(Parameter[] params) {
		this.parameters = params;
	}

	/**
	 * Configure will lookup the function and set it
	 * 
	 * @throws EvaluationException
	 */
	public void configure(Rete engine, Rule rule) throws EvaluationException {
		if (this.functionName != null){
			Function func = engine.getFunctionMemory().findFunction(this.functionName);
			if (func != null){
				this.function = func;
			}
		}

		// now setup the BoundParameters if there are any
		for (int idx = 0; idx < this.parameters.length; idx++) {
			if (this.parameters[idx] instanceof BoundParam) {
				BoundParam bp = (BoundParam) this.parameters[idx];
				Binding bd = rule.getBinding(bp.getVariableName());
				if (bd != null) {
					bp.setRow(bd.getLeftRow());
					bp.setColumn(bd.getLeftIndex());
				}
			} else if (this.parameters[idx] instanceof Signature) {
				Signature fp2 = (Signature) this.parameters[idx];
				fp2.configure(engine, rule);
			} else if (this.parameters[idx] instanceof AbstractConfiguration) {
				AbstractConfiguration ac = (AbstractConfiguration) this.parameters[idx];
				ac.configure(engine, rule);
			} else if (this.parameters[idx] instanceof JamochaValue) {
				// if the value is a deffact, we need to check and make sure
				// the slots with BoundParam value are compiled properly
				JamochaValue value = (JamochaValue) this.parameters[idx];
				if (value.getType().equals(JamochaType.FACT)) {
					((Deffact) value.getFactValue()).compileBinding(rule);
				}
			}
		}
		
		// WE DON'T NEED THIS ANYMORE FOR SFPParser!
		
		// in the case of Assert, we do further compilation
//		if (this.function instanceof Assert) {
//			JamochaValue tmplName = this.parameters[0].getValue(engine);
//			Template tmpl = engine.getCurrentFocus().getTemplate(
//					tmplName.getIdentifierValue());
//			JamochaValue values = this.parameters[1].getValue(engine);
//			Fact fact = tmpl.createFact(values.getObjectValue(), -1, engine);
//			fact.compileBinding(util);
//			this.parameters = new JamochaValue[1];
//			this.parameters[0] = JamochaValue.newFact(fact);
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Action#executeAction(woolfel.engine.rete.Rete,
	 *      woolfel.engine.rete.Fact[])
	 */
	public JamochaValue executeAction(Rete engine, Fact[] facts)
			throws ExecuteException {
		// first we iterate over the parameters and pass the facts
		// to the BoundParams.
//		for (int idx = 0; idx < this.parameters.length; idx++) {
//			
//			if (this.parameters[idx] instanceof BoundParam) {
//				((BoundParam) this.parameters[idx]).setFact(facts);
//				
//			}else if (this.parameters[idx] instanceof AbstractConfiguration) {
//				((AbstractConfiguration) this.parameters[idx]).setFact(facts);
//			}	
//		}
		// we treat AssertFunction a little different
		if (this.function instanceof Assert) {
			((Assert) this.function).setTriggerFacts(facts);
		}
		// now we find the function
		try {
			return this.function.executeFunction(engine, this.parameters);
		} catch (Exception e) {
			throw new ExecuteException(this.functionName, e);
		}
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
}
