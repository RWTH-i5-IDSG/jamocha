/*
 * Copyright 2002-2008 The Jamocha Team
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

import org.jamocha.engine.StopCompileException;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler;
import org.jamocha.formatter.Formatter;

public class OrConnectedConstraint extends AbstractConnectedConstraint {

	public OrConnectedConstraint(Constraint left, Constraint right,	boolean negated) {
		super(left, right, negated);
	}

	private static final long serialVersionUID = 1L;

	public Node compile(SFRuleCompiler compiler, Rule rule,
			int conditionIndex) throws StopCompileException {
		return compiler.compile(this, rule, conditionIndex);
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}


	/**
	 * @see org.jamocha.rules.Condition#acceptVisitor(org.jamocha.rules.LHSVisitor, java.lang.Object)
	 */
	public <T, S> S acceptVisitor(ConstraintVisitor<T, S> visitor, T data) {
		return visitor.visit(this, data);
	}


}
