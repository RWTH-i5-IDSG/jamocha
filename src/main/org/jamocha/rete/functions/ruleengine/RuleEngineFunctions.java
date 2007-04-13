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
import org.jamocha.rete.functions.ListDirectoryFunction;
import org.jamocha.rete.functions.ListFunctionsFunction;
import org.jamocha.rete.functions.ListTemplatesFunction;
import org.jamocha.rete.functions.LoadFactsFunction;
import org.jamocha.rete.functions.LoadFunctionGroupFunction;
import org.jamocha.rete.functions.LoadFunctionsFunction;
import org.jamocha.rete.functions.MatchesFunction;
import org.jamocha.rete.functions.MemberTestFunction;
import org.jamocha.rete.functions.MemoryFreeFunction;
import org.jamocha.rete.functions.MemoryTotalFunction;
import org.jamocha.rete.functions.MemoryUsedFunction;
import org.jamocha.rete.functions.MillisecondTime;
import org.jamocha.rete.functions.ModifyFunction;
import org.jamocha.rete.functions.ModulesFunction;
import org.jamocha.rete.functions.PPrintRuleFunction;
import org.jamocha.rete.functions.PPrintTemplateFunction;
import org.jamocha.rete.functions.PrintProfileFunction;
import org.jamocha.rete.functions.ProfileFunction;
import org.jamocha.rete.functions.ResetFactsFunction;
import org.jamocha.rete.functions.ResetFunction;
import org.jamocha.rete.functions.ResetObjectsFunction;
import org.jamocha.rete.functions.RetractFunction;
import org.jamocha.rete.functions.RightMatchesFunction;
import org.jamocha.rete.functions.RulesFunction;
import org.jamocha.rete.functions.SaveFactsFunction;
import org.jamocha.rete.functions.SetParserFunction;
import org.jamocha.rete.functions.SpoolFunction;
import org.jamocha.rete.functions.TemplatesFunction;
import org.jamocha.rete.functions.TestRuleFunction;
import org.jamocha.rete.functions.UnDefruleFunction;
import org.jamocha.rete.functions.UnDeftemplateFunction;
import org.jamocha.rete.functions.UnProfileFunction;
import org.jamocha.rete.functions.UnWatchFunction;
import org.jamocha.rete.functions.UsageFunction;
import org.jamocha.rete.functions.ValidateRuleFunction;
import org.jamocha.rete.functions.VersionFunction;
import org.jamocha.rete.functions.ViewFunction;
import org.jamocha.rete.functions.WatchFunction;

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

		Facts facts = new Facts();
		engine.declareFunction(facts);
		funcs.add(facts);

		FactSlotValue factSlotValue = new FactSlotValue();
		engine.declareFunction(factSlotValue);
		funcs.add(factSlotValue);

		Fire fire = new Fire();
		engine.declareFunction(fire);
		funcs.add(fire);

		GarbageCollect garbageCollect = new GarbageCollect();
		engine.declareFunction(garbageCollect);
		funcs.add(garbageCollect);

		GenerateFacts generateFacts = new GenerateFacts();
		engine.declareFunction(generateFacts);
		funcs.add(generateFacts);

		GetCurrentModule getCurrentModule = new GetCurrentModule();
		engine.declareFunction(getCurrentModule);
		funcs.add(getCurrentModule);

		LazyAgenda lazyAgenda = new LazyAgenda();
		engine.declareFunction(lazyAgenda);
		funcs.add(lazyAgenda);

		ListDirectoryFunction ldir = new ListDirectoryFunction();
		engine.declareFunction(ldir);
		funcs.add(ldir);

		ListFunctionsFunction lffnc = new ListFunctionsFunction();
		engine.declareFunction(lffnc);
		engine.declareFunction("functions", lffnc);
		funcs.add(lffnc);

		ListTemplatesFunction listTemp = new ListTemplatesFunction();
		engine.declareFunction(listTemp);
		funcs.add(listTemp);

		LoadFactsFunction loadFactsFunction = new LoadFactsFunction();
		engine.declareFunction(loadFactsFunction);
		funcs.add(loadFactsFunction);

		LoadFunctionsFunction loadfunc = new LoadFunctionsFunction();
		engine.declareFunction(loadfunc);
		funcs.add(loadfunc);

		LoadFunctionGroupFunction loadfg = new LoadFunctionGroupFunction();
		engine.declareFunction(loadfg);
		funcs.add(loadfg);

		ModulesFunction modules = new ModulesFunction();
		engine.declareFunction(modules);
		funcs.add(modules);

		UsageFunction usage = new UsageFunction();
		engine.declareFunction(usage);
		funcs.add(usage);

		MatchesFunction mf = new MatchesFunction();
		engine.declareFunction(mf);
		funcs.add(mf);

		MemberTestFunction mtestf = new MemberTestFunction();
		engine.declareFunction(mtestf);
		funcs.add(mtestf);

		MemoryFreeFunction mff = new MemoryFreeFunction();
		engine.declareFunction(mff);
		funcs.add(mff);

		MemoryTotalFunction mtf = new MemoryTotalFunction();
		engine.declareFunction(mtf);
		funcs.add(mtf);

		MemoryUsedFunction musd = new MemoryUsedFunction();
		engine.declareFunction(musd);
		funcs.add(musd);

		MillisecondTime mstime = new MillisecondTime();
		engine.declareFunction(mstime);
		funcs.add(mstime);

		ModifyFunction mod = new ModifyFunction();
		engine.declareFunction(mod);
		funcs.add(mod);

		PPrintRuleFunction pprule = new PPrintRuleFunction();
		engine.declareFunction(pprule);
		funcs.add(pprule);

		PPrintTemplateFunction pptemp = new PPrintTemplateFunction();
		engine.declareFunction(pptemp);
		funcs.add(pptemp);

		PrintProfileFunction pproff = new PrintProfileFunction();
		engine.declareFunction(pproff);
		funcs.add(pproff);

		ProfileFunction proff = new ProfileFunction();
		engine.declareFunction(proff);
		funcs.add(proff);

		ResetFunction resetf = new ResetFunction();
		engine.declareFunction(resetf);
		funcs.add(resetf);

		ResetFactsFunction resetff = new ResetFactsFunction();
		engine.declareFunction(resetff);
		funcs.add(resetff);

		ResetObjectsFunction resetof = new ResetObjectsFunction();
		engine.declareFunction(resetof);
		funcs.add(resetof);

		RetractFunction rtract = new RetractFunction();
		engine.declareFunction(rtract);
		funcs.add(rtract);

		RightMatchesFunction rmfunc = new RightMatchesFunction();
		engine.declareFunction(rmfunc);
		funcs.add(rmfunc);

		RulesFunction rf = new RulesFunction();
		engine.declareFunction(rf);
		engine.declareFunction(RulesFunction.LISTRULES, rf);
		funcs.add(rf);

		SaveFactsFunction savefacts = new SaveFactsFunction();
		engine.declareFunction(savefacts);
		funcs.add(savefacts);

		SetFocus setFocus = new SetFocus();
		engine.declareFunction(setFocus);
		funcs.add(setFocus);

		SetParserFunction setParserFunction = new SetParserFunction();
		engine.declareFunction(setParserFunction);
		funcs.add(setParserFunction);

		SpoolFunction spool = new SpoolFunction();
		engine.declareFunction(spool);
		funcs.add(spool);

		TemplatesFunction tempf = new TemplatesFunction();
		engine.declareFunction(tempf);
		engine.declareFunction(TemplatesFunction.LISTTEMPLATES, tempf);
		funcs.add(tempf);

		TestRuleFunction trfunc = new TestRuleFunction();
		engine.declareFunction(trfunc);
		funcs.add(trfunc);

		UnDefruleFunction udrule = new UnDefruleFunction();
		engine.declareFunction(udrule);
		funcs.add(udrule);

		UnDeftemplateFunction udt = new UnDeftemplateFunction();
		engine.declareFunction(udt);
		funcs.add(udt);

		UnWatchFunction uwatchf = new UnWatchFunction();
		engine.declareFunction(uwatchf);
		funcs.add(uwatchf);

		UnProfileFunction uproff = new UnProfileFunction();
		engine.declareFunction(uproff);
		funcs.add(uproff);

		ValidateRuleFunction vrf = new ValidateRuleFunction();
		engine.declareFunction(vrf);
		funcs.add(vrf);

		VersionFunction ver = new VersionFunction();
		engine.declareFunction(ver);
		funcs.add(ver);

		ViewFunction view = new ViewFunction();
		engine.declareFunction(view);
		funcs.add(view);

		WatchFunction watchf = new WatchFunction();
		engine.declareFunction(watchf);
		funcs.add(watchf);

		engine.declareFunction("focus", getCurrentModule);
		engine.declareFunction("get-focus", getCurrentModule);
	}

	public List listFunctions() {
		return funcs;
	}

}