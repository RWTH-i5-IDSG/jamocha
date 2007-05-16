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
import org.jamocha.rete.Rete;

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

	public void loadFunctions(Rete engine) {

		Apply apply = new Apply();
		engine.declareFunction(apply);
		funcs.add(apply);

		Assert assrt = new Assert();
		engine.declareFunction(assrt);
		funcs.add(assrt);

		AssertTemporal assertTemporal = new AssertTemporal();
		engine.declareFunction(assertTemporal);
		funcs.add(assertTemporal);

		Bind bind = new Bind();
		engine.declareFunction(bind);
		funcs.add(bind);

		Clear clear = new Clear();
		engine.declareFunction(clear);
		funcs.add(clear);

		Defclass defclass = new Defclass();
		engine.declareFunction(defclass);
		funcs.add(defclass);

		Deffunction deffunction = new Deffunction();
		engine.declareFunction(deffunction);
		funcs.add(deffunction);

		Definstance definstance = new Definstance();
		engine.declareFunction(definstance);
		funcs.add(definstance);

		Defmodule defmodule = new Defmodule();
		engine.declareFunction(defmodule);
		funcs.add(defmodule);

		Defrule defrule = new Defrule();
		engine.declareFunction(defrule);
		funcs.add(defrule);

		Deftemplate deftemplate = new Deftemplate();
		engine.declareFunction(deftemplate);
		funcs.add(deftemplate);

		Echo echo = new Echo();
		engine.declareFunction(echo);
		funcs.add(echo);

		Eval eval = new Eval();
		engine.declareFunction(eval);
		funcs.add(eval);

		Exit exit = new Exit();
		engine.declareFunction(exit);
		funcs.add(exit);

		FactId factId = new FactId();
		engine.declareFunction(factId);
		funcs.add(factId);

		Facts facts = new Facts();
		engine.declareFunction(facts);
		funcs.add(facts);

		FactSlotValue factSlotValue = new FactSlotValue();
		engine.declareFunction(factSlotValue);
		funcs.add(factSlotValue);

		Fire fire = new Fire();
		engine.declareFunction(fire);
		funcs.add(fire);

		FunctionsDescription fd = new FunctionsDescription();
		engine.declareFunction(fd);
		funcs.add(fd);

		GarbageCollect garbageCollect = new GarbageCollect();
		engine.declareFunction(garbageCollect);
		funcs.add(garbageCollect);

		GenerateFacts generateFacts = new GenerateFacts();
		engine.declareFunction(generateFacts);
		funcs.add(generateFacts);

		GetCurrentModule getCurrentModule = new GetCurrentModule();
		engine.declareFunction(getCurrentModule);
		funcs.add(getCurrentModule);

		GetFactId getFactId = new GetFactId();
		engine.declareFunction(getFactId);
		funcs.add(getFactId);

		LazyAgenda lazyAgenda = new LazyAgenda();
		engine.declareFunction(lazyAgenda);
		funcs.add(lazyAgenda);

		ListDirectory listDirectory = new ListDirectory();
		engine.declareFunction(listDirectory);
		funcs.add(listDirectory);

		ListFunctions listFunctions = new ListFunctions();
		engine.declareFunction(listFunctions);
		funcs.add(listFunctions);

		ListTemplates listTemplates = new ListTemplates();
		engine.declareFunction(listTemplates);
		funcs.add(listTemplates);

		LoadFunctionGroup loadFunctionGroup = new LoadFunctionGroup();
		engine.declareFunction(loadFunctionGroup);
		funcs.add(loadFunctionGroup);

		LoadFunctions loadFunctions = new LoadFunctions();
		engine.declareFunction(loadFunctions);
		funcs.add(loadFunctions);

		Matches matches = new Matches();
		engine.declareFunction(matches);
		funcs.add(matches);

		MemberTest memberTest = new MemberTest();
		engine.declareFunction(memberTest);
		funcs.add(memberTest);

		MemoryFree memoryFree = new MemoryFree();
		engine.declareFunction(memoryFree);
		funcs.add(memoryFree);

		MemoryTotal memoryTotal = new MemoryTotal();
		engine.declareFunction(memoryTotal);
		funcs.add(memoryTotal);

		MemoryUsed memoryUsed = new MemoryUsed();
		engine.declareFunction(memoryUsed);
		funcs.add(memoryUsed);

		Modify modify = new Modify();
		engine.declareFunction(modify);
		funcs.add(modify);

		Modules modules = new Modules();
		engine.declareFunction(modules);
		funcs.add(modules);

		PPrintRule pprintRule = new PPrintRule();
		engine.declareFunction(pprintRule);
		funcs.add(pprintRule);

		PPrintTemplate pprintTemplate = new PPrintTemplate();
		engine.declareFunction(pprintTemplate);
		funcs.add(pprintTemplate);

		PrintProfile printProfile = new PrintProfile();
		engine.declareFunction(printProfile);
		funcs.add(printProfile);

		Profile profile = new Profile();
		engine.declareFunction(profile);
		funcs.add(profile);

		Reset reset = new Reset();
		engine.declareFunction(reset);
		funcs.add(reset);

		ResetFacts resetFacts = new ResetFacts();
		engine.declareFunction(resetFacts);
		funcs.add(resetFacts);

		ResetObjects resetObjects = new ResetObjects();
		engine.declareFunction(resetObjects);
		funcs.add(resetObjects);

		Retract retract = new Retract();
		engine.declareFunction(retract);
		funcs.add(retract);

		Rules rules = new Rules();
		engine.declareFunction(rules);
		funcs.add(rules);

		SaveFacts saveFacts = new SaveFacts();
		engine.declareFunction(saveFacts);
		funcs.add(saveFacts);

		SetFocus setFocus = new SetFocus();
		engine.declareFunction(setFocus);
		funcs.add(setFocus);

		SetParser setParser = new SetParser();
		engine.declareFunction(setParser);
		funcs.add(setParser);

		Spool spool = new Spool();
		engine.declareFunction(spool);
		funcs.add(spool);

		Templates templates = new Templates();
		engine.declareFunction(templates);
		funcs.add(templates);

		TestRule testRule = new TestRule();
		engine.declareFunction(testRule);
		funcs.add(testRule);

		UnDefrule unDefrule = new UnDefrule();
		engine.declareFunction(unDefrule);
		funcs.add(unDefrule);

		UnDeftemplate unDeftemplate = new UnDeftemplate();
		engine.declareFunction(unDeftemplate);
		funcs.add(unDeftemplate);

		UnProfile unProfile = new UnProfile();
		engine.declareFunction(unProfile);
		funcs.add(unProfile);

		UnWatch unWatch = new UnWatch();
		engine.declareFunction(unWatch);
		funcs.add(unWatch);

		ValidateRule validateRule = new ValidateRule();
		engine.declareFunction(validateRule);
		funcs.add(validateRule);

		Version version = new Version();
		engine.declareFunction(version);
		funcs.add(version);

		View view = new View();
		engine.declareFunction(view);
		funcs.add(view);

		Watch watch = new Watch();
		engine.declareFunction(watch);
		funcs.add(watch);

		engine.declareFunction("focus", getCurrentModule);
		engine.declareFunction("get-focus", getCurrentModule);
		engine.declareFunction("functions", listFunctions);
		engine.declareFunction("list-rules", rules);
		engine.declareFunction("list-deftemplates", templates);
	}

	public List listFunctions() {
		return funcs;
	}

}