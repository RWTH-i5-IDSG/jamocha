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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class DateTimeFunctions implements FunctionGroup {

	private static final long serialVersionUID = 1L;

	private ArrayList<Function> funcs = new ArrayList<Function>();

	public DateTimeFunctions() {
		super();
	}

	public String getName() {
		return (DateTimeFunctions.class.getSimpleName());
	}

	public void loadFunctions(Rete engine) {
		Between between = new Between();
		engine.declareFunction(between);
		funcs.add(between);

		Timestamp2Datetime ts2dt = new Timestamp2Datetime();
		engine.declareFunction(ts2dt);
		funcs.add(ts2dt);

		Datetime2Timestamp dt2ts = new Datetime2Timestamp();
		engine.declareFunction(dt2ts);
		funcs.add(dt2ts);

		GetGMToffset gmtoffset = new GetGMToffset();
		engine.declareFunction(gmtoffset);
		funcs.add(gmtoffset);

		GetSeconds seconds = new GetSeconds();
		engine.declareFunction(seconds);
		funcs.add(seconds);

		GetMinutes minutes = new GetMinutes();
		engine.declareFunction(minutes);
		funcs.add(minutes);

		GetHours hours = new GetHours();
		engine.declareFunction(hours);
		funcs.add(hours);

		GetDay day = new GetDay();
		engine.declareFunction(day);
		funcs.add(day);

		GetMonth month = new GetMonth();
		engine.declareFunction(month);
		funcs.add(month);

		GetYear year = new GetYear();
		engine.declareFunction(year);
		funcs.add(year);

		Now now = new Now();
		engine.declareFunction(now);
		funcs.add(now);
	}

	public List listFunctions() {
		return funcs;
	}

}
