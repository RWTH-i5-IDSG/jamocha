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
public class BooleanFunctions implements FunctionGroup, Serializable {

	private ArrayList funcs = new ArrayList();
	
	public BooleanFunctions() {
		super();
	}
	
	public String getName() {
		return (BooleanFunctions.class.getSimpleName());
	}
	
	public void loadFunctions(Rete engine) {
		NotFunction not = new NotFunction();
		engine.declareFunction(not);
		funcs.add(not);

		TrueFunction trueFunc = new TrueFunction();
		engine.declareFunction(trueFunc);
		funcs.add(trueFunc);

		FalseFunction falseFunc = new FalseFunction();
		engine.declareFunction(falseFunc);
		funcs.add(falseFunc);
	}

	public List listFunctions() {
		return funcs;
	}

}
