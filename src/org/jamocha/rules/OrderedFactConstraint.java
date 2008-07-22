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

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.StopCompileException;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler;

/**
 * @author primary Sebastian Reinartz and/or Alexander Wilden
 * @author Josef Alexander Hahn
 *
 */
public class OrderedFactConstraint extends AbstractConstraint {

	private static final long serialVersionUID = 1L;

	private Constraint[] constraints;

	public OrderedFactConstraint(Constraint[] constraints) {
		this.constraints=constraints;
	}
	
	public boolean isNegated() {
		return false;
	}

	public Constraint[] getConstraints() {
		return constraints;
	}

	public JamochaValue getValue() {
		return null;
	}

	public Node compile(SFRuleCompiler compiler, Rule rule, 	int conditionIndex) throws AssertException, StopCompileException {
		return null;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public String getConstraintName() {
		return null;
	}


	/**
	 * @see org.jamocha.rules.Condition#acceptVisitor(org.jamocha.rules.LHSVisitor, java.lang.Object)
	 */
	public <T> T acceptVisitor(LHSVisitor<T> visitor, T data) {
		return visitor.visit(this, data);
	}


}
