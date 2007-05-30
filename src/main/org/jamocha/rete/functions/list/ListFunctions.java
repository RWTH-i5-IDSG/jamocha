/*
 * Copyright 2007 Christoph Emonds Sebastian Reinartz
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
package org.jamocha.rete.functions.list;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

public class ListFunctions implements FunctionGroup {

	private static final long serialVersionUID = 0xDEADBEAFL;

	private ArrayList<Function> funcs = new ArrayList<Function>();

	public String getName() {
		return (ListFunctions.class.getSimpleName());
	}

	public void loadFunctions(FunctionMemory functionMem) {
		Complement$ complement = new Complement$();
		functionMem.declareFunction(complement);
		funcs.add(complement);

		Create$ create = new Create$();
		functionMem.declareFunction(create);
		funcs.add(create);

		Delete$ delete = new Delete$();
		functionMem.declareFunction(delete);
		funcs.add(delete);

		First$ first = new First$();
		functionMem.declareFunction(first);
		funcs.add(first);

		Foreach foreach = new Foreach();
		functionMem.declareFunction(foreach);
		funcs.add(foreach);

		Length$ length = new Length$();
		functionMem.declareFunction(length);
		funcs.add(length);

		Member$ member = new Member$();
		functionMem.declareFunction(member);
		funcs.add(member);

		Rest$ rest = new Rest$();
		functionMem.declareFunction(rest);
		funcs.add(rest);
	}

	public List listFunctions() {
		return funcs;
	}

}
