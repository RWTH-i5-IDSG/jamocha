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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Binding;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.functions.AssertFunction;
import org.jamocha.rete.functions.ModifyFunction;

/**
 * @author Peter Lin
 * 
 * A FunctionAction is responsible for executing a function in the action of the
 * rule. It uses built-in or user written functions. When the rule is loaded,
 * the engine looks up the functions. At run time, the rule simply executes it.
 */
public class FunctionAction implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Function faction = null;

	protected String functionName = null;

	protected Parameter[] parameters = null;

	/**
	 * 
	 */
	public FunctionAction() {
		super();
	}

	public Function getFunction() {
		return this.faction;
	}

	public void setFunction(FunctionParam2 func) {
		this.functionName = func.getFunctionName();
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
	public void configure(Rete engine, Rule util) throws EvaluationException {
		if (this.functionName != null
				&& engine.findFunction(this.functionName) != null) {
			this.faction = engine.findFunction(this.functionName);
		}
		// now setup the BoundParameters if there are any
		for (int idx = 0; idx < this.parameters.length; idx++) {
			if (this.parameters[idx] instanceof BoundParam) {
				BoundParam bp = (BoundParam) this.parameters[idx];
				Binding bd = util.getBinding(bp.getVariableName());
				if (bd != null) {
					bp.setRow(bd.getLeftRow());
					bp.setColumn(bd.getLeftIndex());
				}
			} else if (this.parameters[idx] instanceof FunctionParam2) {
				FunctionParam2 fp2 = (FunctionParam2) this.parameters[idx];
				fp2.configure(engine, util);
			} else if (this.parameters[idx] instanceof JamochaValue) {
				// if the value is a deffact, we need to check and make sure
				// the slots with BoundParam value are compiled properly
				JamochaValue value = (JamochaValue) this.parameters[idx];
				if (value.getType().equals(JamochaType.FACT)) {
					((Deffact) value.getFactValue()).compileBinding(util);
				}
			}
		}
		// in the case of Assert, we do further compilation
		if (this.faction instanceof AssertFunction) {
			JamochaValue tmplName = this.parameters[0].getValue(engine);
			Template tmpl = engine.getCurrentFocus()
					.getTemplate(tmplName.getIdentifierValue());
			JamochaValue values = this.parameters[1].getValue(engine);
			Fact fact = tmpl.createFact(values
					.getObjectValue(), -1, engine);
			fact.compileBinding(util);
			this.parameters = new JamochaValue[1];
			this.parameters[0] = JamochaValue.newFact(fact);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Action#executeAction(woolfel.engine.rete.Rete,
	 *      woolfel.engine.rete.Fact[])
	 */
	public void executeAction(Rete engine, Fact[] facts)
			throws ExecuteException {
		// first we iterate over the parameters and pass the facts
		// to the BoundParams.
		for (int idx = 0; idx < this.parameters.length; idx++) {
			if (this.parameters[idx] instanceof BoundParam) {
				((BoundParam) this.parameters[idx]).setFact(facts);
			}
		}
		// we treat AssertFunction a little different
		if (this.faction instanceof AssertFunction) {
			((AssertFunction) this.faction).setTriggerFacts(facts);
		} else if (this.faction instanceof ModifyFunction) {
			((ModifyFunction) this.faction).setTriggerFacts(facts);
		}
		// now we find the function
		try {
			this.faction.executeFunction(engine, this.parameters);
		} catch (EvaluationException e) {
			throw new ExecuteException(e);
		}
	}
}
