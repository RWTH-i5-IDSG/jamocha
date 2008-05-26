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

package org.jamocha.engine.rules.rulecompiler.sfp;

import org.jamocha.parser.EvaluationException;
import org.jamocha.rules.Rule;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.StopCompileException;
import org.jamocha.engine.nodes.Node;

@Deprecated
/**
 * this class implements the visitor-pattern for the sfrulecompiler. for a new
 * rule compiler, we should not use this pattern (it binds all our entity
 * objects to one concrete rule compiler _and_ it seems to be an unpractical
 * pattern for a rule compiler!). when sfrulecompiler is deprecated and to be
 * removed, this class should become removed, too! -jh
 */
public interface Compileable {

	/**
	 * compile yourself!
	 * 
	 * @param compiler
	 * @param bindingHelper
	 * @return an object ;)
	 * @throws StopCompileException
	 * @throws AssertException
	 * @throws EvaluationException
	 */
	@Deprecated
	Node compile(SFRuleCompiler compiler, Rule rule, int conditionIndex)
			throws AssertException, StopCompileException, EvaluationException;
}
