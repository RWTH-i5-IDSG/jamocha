/*
 * Copyright 2002-2007 Peter Lin, 2007 Alexander Wilden
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
package org.jamocha.rete.functions.ruleengine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

/**
 * @author Peter Lin
 * 
 * RuleEngineFunction is responsible for loading all the rule functions related
 * to engine operation.
 */
public class RuleEngineFunctions implements FunctionGroup, Serializable {

	private static final long serialVersionUID = 1L;

	private List<Function> funcs = new ArrayList<Function>();

	public RuleEngineFunctions() {
		super();
	}

	public String getName() {
		return (RuleEngineFunctions.class.getSimpleName());
	}

	public void loadFunctions(FunctionMemory functionMem) {

		Apply apply = new Apply();
		functionMem.declareFunction(apply);
		funcs.add(apply);

		Assert assrt = new Assert();
		functionMem.declareFunction(assrt);
		funcs.add(assrt);

		AssertTemporal assertTemporal = new AssertTemporal();
		functionMem.declareFunction(assertTemporal);
		funcs.add(assertTemporal);

		Bind bind = new Bind();
		functionMem.declareFunction(bind);
		funcs.add(bind);

		Clear clear = new Clear();
		functionMem.declareFunction(clear);
		funcs.add(clear);

		Defclass defclass = new Defclass();
		functionMem.declareFunction(defclass);
		funcs.add(defclass);

		Deffunction deffunction = new Deffunction();
		functionMem.declareFunction(deffunction);
		funcs.add(deffunction);

		Definstance definstance = new Definstance();
		functionMem.declareFunction(definstance);
		funcs.add(definstance);

		Defmodule defmodule = new Defmodule();
		functionMem.declareFunction(defmodule);
		funcs.add(defmodule);

		Defrule defrule = new Defrule();
		functionMem.declareFunction(defrule);
		funcs.add(defrule);

		Deftemplate deftemplate = new Deftemplate();
		functionMem.declareFunction(deftemplate);
		funcs.add(deftemplate);

		Echo echo = new Echo();
		functionMem.declareFunction(echo);
		funcs.add(echo);

		Eval eval = new Eval();
		functionMem.declareFunction(eval);
		funcs.add(eval);

		Exit exit = new Exit();
		functionMem.declareFunction(exit);
		funcs.add(exit);

		FactId factId = new FactId();
		functionMem.declareFunction(factId);
		funcs.add(factId);

		Facts facts = new Facts();
		functionMem.declareFunction(facts);
		funcs.add(facts);

		FactSlotValue factSlotValue = new FactSlotValue();
		functionMem.declareFunction(factSlotValue);
		funcs.add(factSlotValue);

		Fire fire = new Fire();
		functionMem.declareFunction(fire);
		funcs.add(fire);

		FunctionsDescription fd = new FunctionsDescription();
		functionMem.declareFunction(fd);
		funcs.add(fd);

		GarbageCollect garbageCollect = new GarbageCollect();
		functionMem.declareFunction(garbageCollect);
		funcs.add(garbageCollect);

		GenerateFacts generateFacts = new GenerateFacts();
		functionMem.declareFunction(generateFacts);
		funcs.add(generateFacts);

		GetCurrentModule getCurrentModule = new GetCurrentModule();
		functionMem.declareFunction(getCurrentModule);
		funcs.add(getCurrentModule);

		GetFactId getFactId = new GetFactId();
		functionMem.declareFunction(getFactId);
		funcs.add(getFactId);

		LazyAgenda lazyAgenda = new LazyAgenda();
		functionMem.declareFunction(lazyAgenda);
		funcs.add(lazyAgenda);

		ListDirectory listDirectory = new ListDirectory();
		functionMem.declareFunction(listDirectory);
		funcs.add(listDirectory);

		ListFunctions listFunctions = new ListFunctions();
		functionMem.declareFunction(listFunctions);
		funcs.add(listFunctions);

		ListTemplates listTemplates = new ListTemplates();
		functionMem.declareFunction(listTemplates);
		funcs.add(listTemplates);

		LoadFunctionGroup loadFunctionGroup = new LoadFunctionGroup();
		functionMem.declareFunction(loadFunctionGroup);
		funcs.add(loadFunctionGroup);

		LoadFunctions loadFunctions = new LoadFunctions();
		functionMem.declareFunction(loadFunctions);
		funcs.add(loadFunctions);

		Matches matches = new Matches();
		functionMem.declareFunction(matches);
		funcs.add(matches);

		MemberTest memberTest = new MemberTest();
		functionMem.declareFunction(memberTest);
		funcs.add(memberTest);

		MemoryFree memoryFree = new MemoryFree();
		functionMem.declareFunction(memoryFree);
		funcs.add(memoryFree);

		MemoryTotal memoryTotal = new MemoryTotal();
		functionMem.declareFunction(memoryTotal);
		funcs.add(memoryTotal);

		MemoryUsed memoryUsed = new MemoryUsed();
		functionMem.declareFunction(memoryUsed);
		funcs.add(memoryUsed);

		Modify modify = new Modify();
		functionMem.declareFunction(modify);
		funcs.add(modify);

		Modules modules = new Modules();
		functionMem.declareFunction(modules);
		funcs.add(modules);

		PPrintRule pprintRule = new PPrintRule();
		functionMem.declareFunction(pprintRule);
		funcs.add(pprintRule);

		PPrintTemplate pprintTemplate = new PPrintTemplate();
		functionMem.declareFunction(pprintTemplate);
		funcs.add(pprintTemplate);

		PrintProfile printProfile = new PrintProfile();
		functionMem.declareFunction(printProfile);
		funcs.add(printProfile);

		Profile profile = new Profile();
		functionMem.declareFunction(profile);
		funcs.add(profile);

		Reset reset = new Reset();
		functionMem.declareFunction(reset);
		funcs.add(reset);

		ResetFacts resetFacts = new ResetFacts();
		functionMem.declareFunction(resetFacts);
		funcs.add(resetFacts);

		ResetObjects resetObjects = new ResetObjects();
		functionMem.declareFunction(resetObjects);
		funcs.add(resetObjects);

		Retract retract = new Retract();
		functionMem.declareFunction(retract);
		funcs.add(retract);

		Rules rules = new Rules();
		functionMem.declareFunction(rules);
		funcs.add(rules);

		SaveFacts saveFacts = new SaveFacts();
		functionMem.declareFunction(saveFacts);
		funcs.add(saveFacts);

		SetFocus setFocus = new SetFocus();
		functionMem.declareFunction(setFocus);
		funcs.add(setFocus);

		SetParser setParser = new SetParser();
		functionMem.declareFunction(setParser);
		funcs.add(setParser);

		Spool spool = new Spool();
		functionMem.declareFunction(spool);
		funcs.add(spool);

		Templates templates = new Templates();
		functionMem.declareFunction(templates);
		funcs.add(templates);

		TestRule testRule = new TestRule();
		functionMem.declareFunction(testRule);
		funcs.add(testRule);

		UnDefrule unDefrule = new UnDefrule();
		functionMem.declareFunction(unDefrule);
		funcs.add(unDefrule);

		UnDeftemplate unDeftemplate = new UnDeftemplate();
		functionMem.declareFunction(unDeftemplate);
		funcs.add(unDeftemplate);

		UnProfile unProfile = new UnProfile();
		functionMem.declareFunction(unProfile);
		funcs.add(unProfile);

		UnWatch unWatch = new UnWatch();
		functionMem.declareFunction(unWatch);
		funcs.add(unWatch);

		ValidateRule validateRule = new ValidateRule();
		functionMem.declareFunction(validateRule);
		funcs.add(validateRule);

		Version version = new Version();
		functionMem.declareFunction(version);
		funcs.add(version);

		View view = new View();
		functionMem.declareFunction(view);
		funcs.add(view);

		Watch watch = new Watch();
		functionMem.declareFunction(watch);
		funcs.add(watch);

		functionMem.declareFunction("focus", getCurrentModule);
		functionMem.declareFunction("get-focus", getCurrentModule);
		functionMem.declareFunction("functions", listFunctions);
		functionMem.declareFunction("list-rules", rules);
		functionMem.declareFunction("list-deftemplates", templates);
	}

	public List listFunctions() {
		return funcs;
	}

}