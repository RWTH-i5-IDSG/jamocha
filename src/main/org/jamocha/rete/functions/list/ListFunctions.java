/*
 * Copyright 2002-2007 Christoph Emonds Sebastian Reinartz
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
import org.jamocha.rete.Rete;

public class ListFunctions implements FunctionGroup {

    private static final long serialVersionUID = 1L;

    private ArrayList<Function> funcs = new ArrayList<Function>();

    public ListFunctions() {
	super();
    }

    public String getName() {
	return (ListFunctions.class.getSimpleName());
    }

    public void loadFunctions(Rete engine) {
	Create$ create = new Create$();
	engine.declareFunction(create);
	funcs.add(create);

	Length$ length = new Length$();
	engine.declareFunction(length);
	funcs.add(length);

	Foreach foreach = new Foreach();
	engine.declareFunction(foreach);
	funcs.add(foreach);
    }

    public List listFunctions() {
	return funcs;
    }
}
