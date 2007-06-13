/*
 * Copyright 2002-2006 Peter Lin, 2007 Alexander Wilden
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
package org.jamocha.rete;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Peter Lin
 * @author Alexander Wilden
 * 
 * Function is the base interface for all functions. The design of
 * Function is very similar to Methods, since a function is basically a stand
 * alone method.
 */
public interface Function extends Serializable, Cloneable {

	/**
	 * Functions must implement concrete logic for the function. To execute a
	 * function, 2 parameters are needed. The first is the rule engine, which is
	 * needed to resolve global variables, and other engine activities like
	 * asserts. The second is an array of parameters, which can be bindings,
	 * literal values or other functions. It is the responsibility of the
	 * programmer to iterate over the parameters to find the data they need to
	 * execute the function.
	 * 
	 * @param engine
	 *            The rule engine the Function works on.
	 * @param params
	 *            The Parameters for this function call.
	 * @return The ealuated result.
	 * @throws EvaluationException
	 *             if something went wrong while executing the Function.
	 */
	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException;

	/**
	 * Every function must declare its name which is returned by this function.
	 * 
	 * @return The name of the function.
	 */
	public String getName();

	/**
	 * Returns the {@link FunctionDescription} Object of the Function. It
	 * contains a general description of the Functionand information about its
	 * parameters and its return values.
	 * 
	 * @return The <code>FunctionDescription</code> of this Function.
	 */
	public FunctionDescription getDescription();

}
