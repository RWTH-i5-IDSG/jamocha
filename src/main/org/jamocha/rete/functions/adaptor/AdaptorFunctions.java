/* Copyright 2007 Josef Alexander Hahn, Alexander Wilden
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
package org.jamocha.rete.functions.adaptor;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

public class AdaptorFunctions implements FunctionGroup {

	private static final long serialVersionUID = 1L;

	private ArrayList<Function> funcs = new ArrayList<Function>();

	public AdaptorFunctions() {
		super();
	}

	public String getName() {
		return (AdaptorFunctions.class.getSimpleName());
	}

	public void loadFunctions(FunctionMemory functionMem) {

		// Note: The jdbc-template will now be defined when the function
		// jdbclink-init is called. Otherwise the template is always in the
		// engine although it isn't needed.

		functionMem.declareFunction(JDBCLink.getInstance());
		funcs.add(JDBCLink.getInstance());
		
		functionMem.declareFunction(JDBCLinkInit.getInstance());
		funcs.add(JDBCLinkInit.getInstance());
		
		functionMem.declareFunction(IteratorImporter.getInstance());
		funcs.add(IteratorImporter.getInstance());
		
		functionMem.declareFunction(IteratorExporter.getInstance());
		funcs.add(IteratorExporter.getInstance());
	}

	public List<Function> listFunctions() {
		return funcs;
	}

	public void addFunction(Function function) {
		this.funcs.add(function);
	}

}