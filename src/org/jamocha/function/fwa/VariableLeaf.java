/*
 * Copyright 2002-2015 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.function.fwa;

import org.jamocha.languages.common.GlobalVariable;

/**
 *
 * Common interface for leafs containing variables ({@link GlobalVariable}s and local variables
 * identified by {@link Symbol}s).
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface VariableLeaf {

	/**
	 * Sets the value of the variable. Called for single variables.
	 * 
	 * @param value
	 *            new value of the variable
	 * @return new value of the variable
	 */
	public Object set(final Object value);

	/**
	 * Sets the value of the variable. Called for multi variables.
	 * 
	 * @param values
	 *            new value of the variable
	 * @return new value of the variable
	 */
	public Object set(final Object[] values);

	/**
	 * Resets the value of the variable. This means unbinding the variable in case of local
	 * variables and setting their default value in case of global variables. Returns null when a
	 * local variable is unbound, otherwise, the return value is the value to which the variable is
	 * set.
	 * 
	 * @return null when a local variable is unbound, otherwise, the return value is the value to
	 *         which the variable is set
	 */
	public Object reset();
}
