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

import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.JamochaValue;

/**
 * 
 * BoundConstraint is a basic implementation of Constraint interface for bound
 * constraints. When a rule declares a slot as a binding, a BoundConstraint is
 * used.
 * @author Peter Lin
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class BoundConstraint extends AbstractConstraint {

	private static final long serialVersionUID = 1L;

	protected String name;
	
	protected boolean negated = false;
	
	protected String slotName;

	/**
	 * creates a bound constraint with the given name, bounded to the given slot
	 * @param slotName
	 * @param name
	 * @param negated
	 */
	public BoundConstraint(String slotName, String name, boolean negated) {
		super();
		this.name = name;
		this.negated = negated;
		this.slotName = slotName;
	}

	/**
	 * creates a bound constraint with the given name, bounded to the whole fact
	 * @param name
	 * @param negated
	 */
	public BoundConstraint(String name, boolean negated) {
		super();
		this.name = name;
		this.negated = negated;
		this.slotName = null;
	}
	
	public String getConstraintName() {
		return name;
	}

	public String getSlotName() {
		return slotName;
	}
	
	public boolean isFactBinding() {
		return (slotName==null);
	}
	
	public JamochaValue getValue() {
		return null;
	}

	public boolean isNegated() {
		return this.negated;
	}

	public Node compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return compiler.compile(this, rule, conditionIndex);
	}
	
	public String format(Formatter visitor) {
		return visitor.visit(this);
	}


	/**
	 * @see org.jamocha.rules.Condition#acceptVisitor(org.jamocha.rules.LHSVisitor, java.lang.Object)
	 */
//	public <T, S> S acceptVisitor(LHSVisitor<T, S> visitor, T data) {
//		return visitor.visit(this, data);
//	}

}
