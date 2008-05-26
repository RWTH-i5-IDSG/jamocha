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
 * AndCondition is specifically created to handle and conjunctions.
 * AndConditions are compiled to a BetaNode.
 */
public class AndCondition extends ConditionWithNested {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AndCondition() {
		super();
	}

	@Deprecated
	public Node compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) throws AssertException, StopCompileException {
		return compiler.compile(this, rule, conditionIndex);
	}

	@Override
	public Condition clone() throws CloneNotSupportedException {
		AndCondition result = new AndCondition();
		for (Condition n: nested) result.nested.add(n);
		return result;
	}

	
	
}
