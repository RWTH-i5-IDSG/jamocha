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

import org.jamocha.rete.functions.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

public class ListFunctions extends FunctionGroup {

	private static final long serialVersionUID = 0xDEADBEAFL;

	public ListFunctions() {
		super();
		name = "ListFunctions";
		description = "This Group provides functions to modify, create, access or get properties of lists.";
	}

	public void loadFunctions(FunctionMemory functionMem) {
		
		addFunction(functionMem, new Complement$());
		addFunction(functionMem, new Create$());
		addFunction(functionMem, new Delete$());
		addFunction(functionMem, new DeleteMember$());
		addFunction(functionMem, new Explode$());
		addFunction(functionMem, new First$());
		addFunction(functionMem, new Foreach());
		addFunction(functionMem, new Implode$());
		addFunction(functionMem, new Insert$());
		addFunction(functionMem, new InsertList$());
		addFunction(functionMem, new Intersection$());
		addFunction(functionMem, new Length$());
		addFunction(functionMem, new Member$());
		addFunction(functionMem, new Nth$());
		addFunction(functionMem, new Replace$());
		addFunction(functionMem, new ReplaceMember$());
		addFunction(functionMem, new Rest$());
		addFunction(functionMem, new Subseq$());
		addFunction(functionMem, new Subsetp());
		addFunction(functionMem, new Union$());
	}

}
