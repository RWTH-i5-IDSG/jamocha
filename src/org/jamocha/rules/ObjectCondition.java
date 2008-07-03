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

	public Condition clone() throws CloneNotSupportedException {
		List<Constraint> newConstr = new ArrayList<Constraint>();
		for (Constraint c : constraints)
			newConstr.add(c);
		return new ObjectCondition(newConstr,templateName);
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

}
