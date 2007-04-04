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
package org.jamocha.rete;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 *
 * Function is the base interface for all functions. We probably should move
 * this to another package later on. For now, I put it here.
 * The design of Function is very similar to Methods, since a function is
 * basically a stand alone method.
 */
public interface Function extends Serializable {
	/**
	 * every function needs to declare what the return type is.
	 * @return
	 */
	JamochaType getReturnType();

	/**
	 * Functions must implement concrete logic for the function.
	 * To execute a function, 2 parameters are needed. The first
	 * is the rule engine, which is needed to resolve global variables,
	 * and other engine activities like asserts. The second is an
	 * array of parameters, which can be bindings, literal values or
	 * other functions.
	 * It is the responsibility of the programmer to iterate over the
	 * parameters to find the data they need to execute the function.
	 * @param engine
	 * @param params
	 * @return
	 * @throws EvaluationException TODO
	 */
	JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException;

	/**
	 * Every function must declare the name
	 * @return
	 */
	String getName();

}
