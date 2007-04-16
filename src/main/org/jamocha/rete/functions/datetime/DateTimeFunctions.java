/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden
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
package org.jamocha.rete.functions.datetime;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class DateTimeFunctions implements FunctionGroup {

	private static final long serialVersionUID = 1L;

	private ArrayList<Function> funcs = new ArrayList<Function>();

	public String getName() {
		return (DateTimeFunctions.class.getSimpleName());
	}

	public void loadFunctions(Rete engine) {
		Between between = new Between();
		engine.declareFunction(between);
		funcs.add(between);

		Datetime2Timestamp datetime2Timestamp = new Datetime2Timestamp();
		engine.declareFunction(datetime2Timestamp);
		funcs.add(datetime2Timestamp);

		GetDay getDay = new GetDay();
		engine.declareFunction(getDay);
		funcs.add(getDay);

		GetGMTOffset getGMTOffset = new GetGMTOffset();
		engine.declareFunction(getGMTOffset);
		funcs.add(getGMTOffset);

		GetHours getHours = new GetHours();
		engine.declareFunction(getHours);
		funcs.add(getHours);

		GetMinutes getMinutes = new GetMinutes();
		engine.declareFunction(getMinutes);
		funcs.add(getMinutes);

		GetMonth getMonth = new GetMonth();
		engine.declareFunction(getMonth);
		funcs.add(getMonth);

		GetSeconds getSeconds = new GetSeconds();
		engine.declareFunction(getSeconds);
		funcs.add(getSeconds);

		GetYear getYear = new GetYear();
		engine.declareFunction(getYear);
		funcs.add(getYear);

		MillisecondTime millisecondTime = new MillisecondTime();
		engine.declareFunction(millisecondTime);
		funcs.add(millisecondTime);

		Now now = new Now();
		engine.declareFunction(now);
		funcs.add(now);

		Timestamp2Datetime timestamp2Datetime = new Timestamp2Datetime();
		engine.declareFunction(timestamp2Datetime);
		funcs.add(timestamp2Datetime);
	}

	public List listFunctions() {
		return funcs;
	}

}
