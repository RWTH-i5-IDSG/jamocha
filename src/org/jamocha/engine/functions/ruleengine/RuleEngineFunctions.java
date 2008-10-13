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

package org.jamocha.engine.functions.ruleengine;

import org.jamocha.engine.functions.FunctionGroup;
import org.jamocha.engine.functions.FunctionMemory;

/**
 * @author Peter Lin
 * 
 * RuleEngineFunction is responsible for loading all the rule functions related
 * to engine operation.
 */
public class RuleEngineFunctions extends FunctionGroup {

	private static final long serialVersionUID = 1L;

	public RuleEngineFunctions() {
		super();
		name = "RuleEngineFunctions";
		description = "This Group provides functions that give access to the rule engine.";
	}

	@Override
	public void loadFunctions(FunctionMemory functionMem) {
		addFunction(functionMem, new Apply());
		addFunction(functionMem, new Assert());
		addFunction(functionMem, new Bind());
		addFunction(functionMem, new Clear());
		addFunction(functionMem, new Deffunction());
		addFunction(functionMem, new Defmodule());
		addFunction(functionMem, new Defrule());
		addFunction(functionMem, new Deftemplate());
		addFunction(functionMem, new DeftemplateFromJavaClass());
		addFunction(functionMem, new Echo());
		addFunction(functionMem, new Eval());
		addFunction(functionMem, new EvalBlocking());
		addFunction(functionMem, new Exit());
		addFunction(functionMem, new FactId());
		addFunction(functionMem, new Facts());
		addFunction(functionMem, new FactSlotValue());
		addFunction(functionMem, new FindFactByFact());
		addFunction(functionMem, new Fire());
		addFunction(functionMem, new FunctionExists());
		addFunction(functionMem, new FunctionsDescription());
		addFunction(functionMem, new GarbageCollect());
		addFunction(functionMem, new GetCurrentModule());
		addFunction(functionMem, new GetFactId());
		addFunction(functionMem, new GetStrategy());
		addFunction(functionMem, new ListBindings());
		addFunction(functionMem, new ListDirectory());
		addFunction(functionMem, new ListFunctions());
		addFunction(functionMem, new ListRules());
		addFunction(functionMem, new ListTemplates());
		addFunction(functionMem, new LoadFunctionGroup());
		addFunction(functionMem, new LoadFunctions());
		addFunction(functionMem, new Matches());
		addFunction(functionMem, new MemoryFree());
		addFunction(functionMem, new MemoryTotal());
		addFunction(functionMem, new MemoryUsed());
		addFunction(functionMem, new Modify());
		addFunction(functionMem, new Modules());
		addFunction(functionMem, new PPrintRule());
		addFunction(functionMem, new PPrintTemplate());
		addFunction(functionMem, new PrintProfile());
		addFunction(functionMem, new Profile());
		addFunction(functionMem, new Reset());
		addFunction(functionMem, new ResetFacts());
		addFunction(functionMem, new Retract());
		addFunction(functionMem, new ListRules());
		addFunction(functionMem, new SaveFacts());
		addFunction(functionMem, new SetDefault());
		addFunction(functionMem, new SetFocus());
		addFunction(functionMem, new SetSettings());
		addFunction(functionMem, new SetStrategy());
		addFunction(functionMem, new Sleep());
		addFunction(functionMem, new Spool());
		addFunction(functionMem, new UnDefrule());
		addFunction(functionMem, new UnDeftemplate());
		addFunction(functionMem, new UnProfile());
		addFunction(functionMem, new UnWatch());
		addFunction(functionMem, new Version());
		addFunction(functionMem, new View());
		addFunction(functionMem, new TextView());
		addFunction(functionMem, new Watch());
	}

}