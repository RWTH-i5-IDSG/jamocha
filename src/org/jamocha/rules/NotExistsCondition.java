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

package org.jamocha.rules;

import java.util.List;

import org.jamocha.formatter.Formatter;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.StopCompileException;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler;

/**
 * @author Peter Lin
 * @author Josef Alexander Hahn
 * 
 * NotExistsCondition handles negated existance quantor.
 */
public class NotExistsCondition extends ConditionWithNested {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotExistsCondition() {
		super();
	}

	@Deprecated
	public Node compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) throws AssertException, StopCompileException {
		return compiler.compile(this, rule, conditionIndex);
	}
	

	/**
	 * @see org.jamocha.rules.Condition#acceptVisitor(org.jamocha.rules.LHSVisitor, java.lang.Object)
	 */
	public <T> T acceptVisitor(LHSVisitor<T> visitor, T data) {
		return visitor.visit(this, data);
	}

	
}
