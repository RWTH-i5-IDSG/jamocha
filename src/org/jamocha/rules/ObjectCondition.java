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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jamocha.engine.StopCompileException;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;

/**
 * ObjectCondition is equivalent to RuleML 0.83 resourceType. ObjectCondition
 * matches on the fields of an object. The patterns may be simple value
 * comparisons, or joins against other objects.
 * @author Peter Lin
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class ObjectCondition extends AbstractCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String templateName = null;

	protected List<Constraint> constraints = new ArrayList<Constraint>();

	protected Template template = null;

	/**
	 * 
	 */
	public ObjectCondition(List<Constraint> constraints, String templateName) {
		super();
		this.constraints = constraints;
		this.templateName = templateName;
		registerAsParentCondition();
	}
	
	public ObjectCondition(ObjectCondition c) {
		super(c);
		this.constraints = new ArrayList<Constraint>();
		this.templateName = c.templateName;
		registerAsParentCondition();
	}
	
	private void registerAsParentCondition() {
		for (Constraint c : constraints) {
			c.setParentCondition(this);
		}
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public Node compile(SFRuleCompiler compiler, Rule rule, int conditionIndex)
			throws EvaluationException, StopCompileException {
		return compiler.compile(this, rule, conditionIndex);
	}

	public Condition clone() {
		List<Constraint> newConstr = new ArrayList<Constraint>();
		for (Constraint c : constraints)
			newConstr.add(c);
		ObjectCondition result = new ObjectCondition(newConstr,templateName);
		return result;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public int getComplexity() {
		return 1;
	}

	public List<Constraint> getConstraints() {
		return Collections.unmodifiableList(constraints);
	}
	
	public void addConstraint(Constraint c) {
		constraints.add(c);
		c.setParentCondition(this);
	}

	public List<Constraint> getFlatConstraints() {
		return getConstraints();
	}


	/**
	 * @see org.jamocha.rules.Condition#acceptVisitor(org.jamocha.rules.ConditionVisitor, java.lang.Object)
	 */
	public <T, S> S acceptVisitor(ConditionVisitor<T, S> visitor, T data) {
		return visitor.visit(this, data);
	}

	public boolean testEquals(Condition o) {
		if (o == null) return false;
		if (! (o instanceof ObjectCondition)) return false;
		
		ObjectCondition objectcon = (ObjectCondition) o;
		
		return (objectcon.templateName.equals(this.templateName));
	}

	public String dump(String prefix) {
		return prefix + "(object " + templateName + ")";
	}
	
}
