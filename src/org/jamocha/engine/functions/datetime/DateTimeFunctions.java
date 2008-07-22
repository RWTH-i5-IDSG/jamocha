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

package org.jamocha.engine.functions.datetime;

import org.jamocha.engine.functions.FunctionGroup;
import org.jamocha.engine.functions.FunctionMemory;

public class DateTimeFunctions extends FunctionGroup {

	private static final long serialVersionUID = 1L;

	public DateTimeFunctions() {
		super();
		name = "DateTimeFunctions";
		description = "This Group provides functions that provide access to the current time, format dates or get parts of a date.";
	}

	@Override
	public void loadFunctions(FunctionMemory functionMem) {
		addFunction(functionMem, new Between());
		addFunction(functionMem, new Datetime2Timestamp());
		addFunction(functionMem, new GetDay());
		addFunction(functionMem, new GetGMTOffset());
		addFunction(functionMem, new GetHours());
		addFunction(functionMem, new GetMinutes());
		addFunction(functionMem, new GetMonth());
		addFunction(functionMem, new GetSeconds());
		addFunction(functionMem, new GetYear());
		addFunction(functionMem, new MillisecondTime());
		addFunction(functionMem, new Now());
		addFunction(functionMem, new Timestamp2Datetime());
	}

}
