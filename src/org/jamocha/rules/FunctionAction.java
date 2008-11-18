/*
 * Copyright 2002-2008 The Jamocha Team
 * 
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

package org.jamocha.rules;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.Binding;
import org.jamocha.engine.BoundParam;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ExecuteException;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.configurations.ModifyConfiguration;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.configurations.SlotConfiguration;
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.functions.FunctionNotFoundException;
import org.jamocha.engine.nodes.FactTuple;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

/**
 * @author Josef Alexander Hahn
 * @author Peter Lin
 * 
 * A FunctionAction is responsible for executing a function in the action of the
 * rule. It uses built-in or user written functions. When the rule is loaded,
 * the engine looks up the functions. At run time, the rule simply executes it.
 */
public class FunctionAction implements Action {
	
	private static final long serialVersionUID = 1L;

	protected Function function = null;

	protected List<Parameter> parameters;
	
	protected Engine engine;
	
	protected Rule parent;

	public FunctionAction(String functionName, Engine engine, Rule parentRule, List<Parameter> params) throws FunctionNotFoundException {
		this.engine = engine;
		this.parent = parentRule;
		this.parameters = params;
		Function f = engine.getFunctionMemory().findFunction(functionName);
	}

	public FunctionAction(Function function, Engine engine, Rule parentRule, List<Parameter> params) {
		this.function = function;
		this.engine = engine;
		this.parent = parentRule;
		this.parameters = params;
	}

	
//TODO remove	public void configure(Engine engine, Rule rule) throws EvaluationException {
//		if (this.functionName != null){
//			Function func = engine.getFunctionMemory().findFunction(this.functionName);
//			if (func != null){
//				this.function = func;
//			}
//		}
//		// now setup the BoundParameters if there are any
//		for (int idx = 0; idx < this.parameters.length; idx++) {
//			if (this.parameters[idx] instanceof BoundParam) {
//				BoundParam bp = (BoundParam) this.parameters[idx];
//				Binding bd = rule.getBinding(bp.getVariableName());
//				if (bd != null) {
//					bp.setRow(bd.getLeftRow());
//					bp.setColumn(bd.getLeftIndex());
//				}
//			} else if (this.parameters[idx] instanceof Signature) {
//				Signature fp2 = (Signature) this.parameters[idx];
//				fp2.configure(engine, rule);
//			} else if (this.parameters[idx] instanceof AbstractConfiguration) {
//				AbstractConfiguration ac = (AbstractConfiguration) this.parameters[idx];
//				ac.configure(engine, rule);
//			} else if (this.parameters[idx] instanceof JamochaValue) {
//				// if the value is a deffact, we need to check and make sure
//				// the slots with BoundParam value are compiled properly
//				JamochaValue value = (JamochaValue) this.parameters[idx];
//				if (value.getType().equals(JamochaType.FACT)) {
//					((Deffact) value.getFactValue()).compileBinding(rule);
//				}
//			}
//		}
//	}
	
	protected void substituteBoundParams(Parameter[] params, FactTuple tuple) throws FunctionNotFoundException, ExecuteException {
		/*
		 * TODO
		 * For the moment, we have to handle different types of parameters here.
		 * in particular, this is horrible for the different types of configurations.
		 * 
		 * later on, we must do one of the following:
		 * 
		 * 1) each parameter can substitute itself with a substitute()-call instead
		 *    of doint the substitution here.
		 *    
		 * 2) we introduce a global binding manager and some further magic, so
		 *    we don't need to substitute anything here
		 *    
		 * 3) we get rid of the masses of configuration classes, so we can handle
		 *    substitution here in a less painful manner
		 *    
		 * 4) we implement everything here and keep silent about that :-)
		 * 
		 */
		for(int idx = 0; idx < params.length ; idx++) {
			Parameter param = params[idx];
			if (param instanceof BoundParam) {
				BoundParam bp = (BoundParam) param;
				Binding binding = engine.getRuleCompiler().getBinding(bp.getVariableName(), parent);
				if (binding == null) {
					params[idx] = bp;
				} else {
					try {
						JamochaValue newObj;
						if (binding.isWholeFactBinding()){
							newObj = JamochaValue.newFact(tuple.getFact(binding.getTupleIndex().get()));
						} else {
							newObj = tuple.getFact(binding.getTupleIndex().get()).getSlotValue(binding.getSlotIndex());
						}
						params[idx] = newObj;
					} catch (EvaluationException e) {
						Logging.logger(this.getClass()).fatal(e);
					}
				}
			} else if (param instanceof ModifyConfiguration) {
				ModifyConfiguration mc = (ModifyConfiguration) param;
				mc = (ModifyConfiguration) mc.clone();
				params[idx] = mc;
				BoundParam factBp = (BoundParam) mc.getFactBinding();
				Binding binding = engine.getRuleCompiler().getBinding(factBp.getVariableName(), parent);
				JamochaValue newObj=null;
				if (binding.isWholeFactBinding()){
					newObj = JamochaValue.newFact(tuple.getFact(binding.getTupleIndex().get()));
				} else {
					try {
						newObj = tuple.getFact(binding.getTupleIndex().get()).getSlotValue(binding.getSlotIndex());
					} catch (EvaluationException e) {
						Logging.logger(this.getClass()).fatal(e);
					}
				}
				mc.setFactBinding(newObj);
				for (SlotConfiguration sc : mc.getSlots()) {
					Parameter[] p = sc.getSlotValues();
					substituteBoundParams(p, tuple);
				}
				
			} else if (param instanceof JamochaValue) {
				
			} else if (param instanceof Signature) {
				// we have to call the inner function at first
				Signature iSig = (Signature) param;
				Function iFunc = iSig.lookUpFunction(engine);
				List<Parameter> iParams = new ArrayList<Parameter>();
				for (Parameter p : iSig.getParameters()) iParams.add(p);
				FunctionAction iFuncAction = new FunctionAction(iFunc, engine, parent,iParams);
				JamochaValue iRes = iFuncAction.executeAction(tuple);
				params[idx] = iRes;
			} else {
				Logging.logger(this.getClass()).fatal("cannot handle parameter "+param);
			}
		}
	}

	public JamochaValue executeAction(FactTuple facts) throws ExecuteException {
		// we treat AssertFunction a little different
//		if (this.function instanceof Assert) {
//			((Assert) this.function).setTriggerFacts(facts);
//		}
		try {
			//TODO: this is a flat-copy. me must make a deep copy here!!
			Parameter[] params = new Parameter[parameters.size()];
			params = parameters.toArray(params);
			substituteBoundParams(params, facts);
			return this.function.executeFunction(engine, params);
		} catch (Exception e) {
			throw new ExecuteException("Error executing function '"+function.getName()+"'", e);
		}
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
	
	public Function getFunction() {
		return function;
	}
	
	public List<Parameter> getParameters() {
		return parameters;
	}
	
}
