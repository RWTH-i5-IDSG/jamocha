/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

package org.jamocha.engine.functions;

/**
 * @author Peter Lin
 * 
 */
public class DeffunctionGroup extends FunctionGroup {

	private static final long serialVersionUID = 1L;

	public DeffunctionGroup(String name) {
		super();
		this.name = name;
	}

	public DeffunctionGroup() {
		super();
		name = "Deffunctions";
	}

	/**
	 * At engine initialization time, the function group doesn't have any
	 * functions.
	 */
	@Override
	public void loadFunctions(FunctionMemory functionMem) {
	}

}
