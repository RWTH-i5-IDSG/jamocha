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

package org.jamocha.parser;

import org.jamocha.formatter.Formattable;
import org.jamocha.engine.Engine;

/**
 * Defines an interface, which has to be returned by all parsers. Every
 * expression can be evaluated with a given Rete engine to return the value of
 * this expression.
 * 
 * @author Christoph Emonds
 * 
 */
public interface Expression extends Formattable{

	/**
	 * Evaluates the expression and returns the value.
	 * 
	 * @param engine
	 *            The Rete engine which is used to evaluate the expression.
	 * @return JamochaValue which is the result of the evaluation.
	 * @throws EvaluationException
	 *             if there occurs an error during evaluation
	 */
	JamochaValue getValue(Engine engine) throws EvaluationException;

	/**
	 * Returns the String which defines this expression (e.g."(+ 1 2)" or "?x"
	 * or "153").
	 * 
	 * @return String definition of this expression.
	 */
	String getExpressionString();

}