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
import org.jamocha.engine.configurations.AssertConfiguration;
import org.jamocha.engine.configurations.ModifyConfiguration;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.configurations.SlotConfiguration;
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.functions.FunctionNotFoundException;
import org.jamocha.engine.nodes.FactTuple;
import org.jamocha.engine.nodes.TerminalNode;
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
		engine.getFunctionMemory().findFunction(functionName);
	}

	public FunctionAction(Function function, Engine engine, Rule parentRule, List<Parameter> params) {
		this.function = function;
		this.engine = engine;
		this.parent = parentRule;
		this.parameters = params;
	}
	
	protected void substituteBoundParams(List<Parameter> params,TerminalNode tnode, FactTuple tuple) {
		Parameter p[] = new Parameter[params.size()];
		for(int i=0;i< params.size();i++) p[i] = params.get(i);
		substituteBoundParams(p, tnode, tuple);
		for(int i=0;i< params.size();i++) params.set(i,p[i]);
	}
	
	protected void substituteBoundParams(Parameter[] params,TerminalNode tnode, FactTuple tuple) {
		/*
		 * TODO
		 * For the moment, we have to handle different types of parameters here.
		 * in particular, this is horrible for the different types of configurations.
		 * 
		 * later on, we must do one of the following:
		 * 
		 * 1) each parameter can substitute itself with a substitute()-call instead
		 *    of doing the substitution here.
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
				Binding binding = engine.getRuleCompiler().getBinding(bp.getVariableName(),tnode, parent);
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
				Binding binding = engine.getRuleCompiler().getBinding(factBp.getVariableName(),tnode, parent);
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
					substituteBoundParams(p,tnode,tuple);
				}
				
			} else if (param instanceof AssertConfiguration) {
				AssertConfiguration ac = (AssertConfiguration)(((AssertConfiguration)param).clone());
				params[idx] = ac;
				substituteBoundParams(ac.getData(), tnode, tuple);
			} else if (param instanceof JamochaValue) {
				// leave this parameter as is
			} else if (param instanceof Signature) {
				// we have to call the inner function at first
				JamochaValue iRes = null;
				boolean subst = true;
				try {
					Signature iSig = (Signature) ((Signature) param).clone();
					params[idx] = iSig;
					substituteBoundParams(iSig.getParameters(), tnode, tuple);
					List<Parameter> iParams = new ArrayList<Parameter>();
					for (Parameter p : iSig.getParameters()) iParams.add(p);
					Function iFunc = iSig.lookUpFunction(engine);
					FunctionAction iFuncAction = new FunctionAction(iFunc, engine, parent,iParams);
					iRes = iFuncAction.executeAction(tuple,tnode);
				} catch (FunctionNotFoundException e) {
					subst = false;
				} catch (ExecuteException e) {
					subst = false;
				}
				if (subst) params[idx] = iRes;
			} else {
				Logging.logger(this.getClass()).fatal("cannot handle parameter "+param);
			}
		}
	}

	public JamochaValue executeAction(FactTuple facts, TerminalNode tnode) throws ExecuteException {
		// we treat AssertFunction a little different
//		if (this.function instanceof Assert) {
//			((Assert) this.function).setTriggerFacts(facts);
//		}
		try {
			//TODO: this is a flat-copy. me must make a deep copy here!!
			Parameter[] params = new Parameter[parameters.size()];
			params = parameters.toArray(params);
			substituteBoundParams(params,tnode,facts);
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
