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
package org.jamocha.rete.functions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;


/**
 * @author Peter Lin
 * 
 * RuleEngineFunction is responsible for loading all the rule functions
 * related to engine operation.
 */
public class RuleEngineFunctions implements FunctionGroup, Serializable {

	private ArrayList funcs = new ArrayList();
	
	public RuleEngineFunctions() {
		super();
	}
	
	public String getName() {
		return (RuleEngineFunctions.class.getSimpleName());
	}
	
	public void loadFunctions(Rete engine) {
		AssertFunction assrt = new AssertFunction();
		engine.declareFunction(assrt);
		funcs.add(assrt);
		AnyEqFunction anyeq = new AnyEqFunction();
		engine.declareFunction(anyeq);
		funcs.add(anyeq);
		BindFunction bindf = new BindFunction();
		engine.declareFunction(bindf);
		funcs.add(bindf);
		ClearFunction clr = new ClearFunction();
		engine.declareFunction(clr);
		funcs.add(clr);
		DefclassFunction defcls = new DefclassFunction();
		engine.declareFunction(defcls);
		funcs.add(defcls);
		DefmoduleFunction dmod = new DefmoduleFunction();
		engine.declareFunction(dmod);
		funcs.add(dmod);
		DefruleFunction drule = new DefruleFunction();
		engine.declareFunction(drule);
		funcs.add(drule);
		DefinstanceFunction defins = new DefinstanceFunction();
		engine.declareFunction(defins);
		funcs.add(defins);
		DeftemplateFunction dtemp = new DeftemplateFunction();
		engine.declareFunction(dtemp);
		funcs.add(dtemp);
		EchoFunction efunc = new EchoFunction();
		engine.declareFunction(efunc);
		funcs.add(efunc);
		EqFunction eq = new EqFunction();
		engine.declareFunction(eq);
		funcs.add(eq);
		EvalFunction eval = new EvalFunction();
		engine.declareFunction(eval);
		funcs.add(eval);
        ExitFunction ext = new ExitFunction();
        engine.declareFunction(ext);
        funcs.add(ext);
        FactsFunction ffun = new FactsFunction();
        engine.declareFunction(ffun);
        funcs.add(ffun);
		FireFunction fire = new FireFunction();
		engine.declareFunction(fire);
		funcs.add(fire);
		FocusFunction focus = new FocusFunction();
		engine.declareFunction(focus);
		funcs.add(focus);
		ModulesFunction modules = new ModulesFunction();
		engine.declareFunction(modules);
		funcs.add(modules);
		GenerateFactsFunction genff = new GenerateFactsFunction();
		engine.declareFunction(genff);
		funcs.add(genff);
		GarbageCollectFunction gcf = new GarbageCollectFunction();
		engine.declareFunction(gcf);
		funcs.add(gcf);
		LazyAgendaFunction laf = new LazyAgendaFunction();
		engine.declareFunction(laf);
		funcs.add(laf);
		ListFunctionsFunction lffnc = new ListFunctionsFunction();
		engine.declareFunction(lffnc);
		engine.declareFunction("functions",lffnc);
		funcs.add(lffnc);
		ListTemplatesFunction listTemp = new ListTemplatesFunction();
		engine.declareFunction(listTemp);
		funcs.add(listTemp);
		LoadFunctionsFunction loadfunc = new LoadFunctionsFunction();
		engine.declareFunction(loadfunc);
		funcs.add(loadfunc);
		LoadFunctionGroupFunction loadfg = new LoadFunctionGroupFunction();
		engine.declareFunction(loadfg);
		funcs.add(loadfg);
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
		engine.declareFunction(RulesFunction.LISTRULES,rf);
		funcs.add(rf);
		SaveFactsFunction savefacts = new SaveFactsFunction();
		engine.declareFunction(savefacts);
		funcs.add(savefacts);
		SetFocusFunction setfoc = new SetFocusFunction();
		engine.declareFunction(setfoc);
		funcs.add(setfoc);
		SpoolFunction spool = new SpoolFunction();
		engine.declareFunction(spool);
		funcs.add(spool);
		TemplatesFunction tempf = new TemplatesFunction();
		engine.declareFunction(tempf);
		engine.declareFunction(tempf.LISTTEMPLATES,tempf);
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
	}

	public List listFunctions() {
		return funcs;
	}

}
