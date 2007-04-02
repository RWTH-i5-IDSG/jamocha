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


public class StringFunctions implements FunctionGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Function> funcs = new ArrayList<Function>();

	public StringFunctions() {
		super();
	}
	
	public String getName() {
		return (StringFunctions.class.getSimpleName());
	}

	public void loadFunctions(Rete engine) {
		StringCompareFunction compare = new StringCompareFunction();
		engine.declareFunction(compare);
		funcs.add(compare);
		StringCatFunction cat = new StringCatFunction();
		engine.declareFunction(cat);
		funcs.add(cat);
		StringIndexFunction indx = new StringIndexFunction();
		engine.declareFunction(indx);
		funcs.add(indx);
		StringLengthFunction strlen = new StringLengthFunction();
		engine.declareFunction(strlen);
		funcs.add(strlen);
		StringLowerFunction lower = new StringLowerFunction();
		engine.declareFunction(lower);
		funcs.add(lower);
		StringReplaceFunction strrepl = new StringReplaceFunction();
		engine.declareFunction(strrepl);
		funcs.add(strrepl);
		StringUpperFunction upper = new StringUpperFunction();
		engine.declareFunction(upper);
		funcs.add(upper);
		SubStringFunction sub = new SubStringFunction();
		engine.declareFunction(sub);
		funcs.add(sub);
		StringTrimFunction trim = new StringTrimFunction();
		engine.declareFunction(trim);
		funcs.add(trim);
	}

	public List listFunctions() {
		return funcs;
	}

}
