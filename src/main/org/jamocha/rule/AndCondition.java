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
package org.jamocha.rule;

import java.util.List;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.SFRuleCompiler;
import org.jamocha.rete.StopCompileException;

/**
 * @author Peter Lin
 * 
 * AndCondition is specifically created to handle and conjunctions.
 * AndConditions are compiled to a BetaNode.
 */
public class AndCondition extends ConditionWithNested {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public AndCondition() {
		super();
	}

	public BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) throws AssertException, StopCompileException {
		return compiler.compile(this, rule, conditionIndex);
	}

	public List<Constraint> getConstraints() {
		return null;
	}

	protected String clipsName() {return "and";}
	
	
}
