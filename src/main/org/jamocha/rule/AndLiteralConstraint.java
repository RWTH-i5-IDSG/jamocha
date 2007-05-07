/*
 * Copyright 2002-2007 Peter Lin
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
package org.jamocha.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BaseNode;
import org.jamocha.rete.SFRuleCompiler;

/**
 * @author Peter Lin
 * 
 * Literal constraint is a comparison between an object field and a concrete
 * value. for example, account.name is equal to "Peter Lin". I originally, named
 * the class something else, but since CLIPS uses literal constraint, I decided
 * to change the name of the class. Even though I don't like the term literal
 * constraint, it doesn't make sense to fight existing terminology.
 */
public class AndLiteralConstraint extends AbstractConstraint {

	private static final long serialVersionUID = 1L;

	protected String name;

	protected JamochaValue value = JamochaValue.newObject(new ArrayList());

	protected boolean negated = false;

	/**
	 * 
	 */
	public AndLiteralConstraint() {
		super();
	}

	/**
	 * the name is the slot name
	 */
	public String getName() {
		return name;
	}

	/**
	 * set the slot name as declared in the rule
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Constraint#getValue()
	 */
	public JamochaValue getValue() {
		return value;
	}

	/**
	 * Set the value of the constraint. It should be a concrete value and not a
	 * binding.
	 */
	public void setValue(JamochaValue val) {
		if (val.getType().equals(JamochaType.OBJECT) && val.getObjectValue() instanceof List) {
			this.value = val;
		}
	}

	public void addValue(MultiValue mv) {
		((List) this.value.getObjectValue()).add(mv);
	}

	public void addValues(Collection list) {
		((List) this.value.getObjectValue()).addAll(list);
	}

	/**
	 * if the literal constraint is negated with a "~" tilda, call the method
	 * pass true.
	 * 
	 * @param negate
	 */
	public void setNegated(boolean negate) {
		this.negated = negate;
	}

	/**
	 * if the literal constraint is negated, the method returns true
	 * 
	 * @return
	 */
	public boolean getNegated() {
		return this.negated;
	}

	public BaseNode compile(SFRuleCompiler compiler, Rule rule, int conditionIndex) {
		return compiler.compile(this, rule, conditionIndex);
	}
}
