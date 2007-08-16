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

import org.jamocha.rete.functions.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

public class AdaptorFunctions extends FunctionGroup {

	private static final long serialVersionUID = 1L;

	public AdaptorFunctions() {
		super();
		name = "AdaptorFunctions";
		description = "This Group provides functions to connect to other data sources.";
	}

	public void loadFunctions(FunctionMemory functionMem) {

		// Note: The jdbc-template will now be defined when the function
		// jdbclink-init is called. Otherwise the template is always in the
		// engine although it isn't needed.

		addFunction(functionMem, new JDBCLink());
		addFunction(functionMem, new JDBCLinkInit());
		addFunction(functionMem, new IteratorImporter());
		addFunction(functionMem, new IteratorExporter());
	}

}