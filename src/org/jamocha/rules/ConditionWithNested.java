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


import java.util.ArrayList;
import java.util.List;

import org.jamocha.engine.AssertException;
import org.jamocha.engine.StopCompileException;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;

public abstract class ConditionWithNested extends AbstractCondition {

	protected List<Condition> nested = new ArrayList<Condition>();

	public ConditionWithNested() {
		super();
	}

	public void addNestedCondition(Condition ce) {
		nested.add((Condition) ce);
	}

	public List<Condition> getNestedConditions() {
		return this.nested;
	}

	public List<Constraint> getConstraints() {
		List<Constraint> result = new ArrayList<Constraint>();
		for (Condition c: nested) result.addAll(c.getConstraints()); 
		return result;
	}

	public int getComplexity() {
		int comp = 0;
		for (Condition child:nested) comp += child.getComplexity();
		return comp;
	}

}