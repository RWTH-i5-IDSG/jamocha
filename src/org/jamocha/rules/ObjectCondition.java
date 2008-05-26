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
 * @author Peter Lin
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 * 
 * ObjectCondition is equivalent to RuleML 0.83 resourceType. ObjectCondition
 * matches on the fields of an object. The patterns may be simple value
 * comparisons, or joins against other objects.
 */
public class ObjectCondition extends AbstractCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String templateName = null;

	/* varname is the optional binding name for the whole fact
	 * example:
	 * ?x <- (beer (name "Baron Pils") )
	 */
	protected String varname = null;

	protected List<Constraint> constraints = new ArrayList<Constraint>();

	protected Template template = null;

	/**
	 * 
	 */
	public ObjectCondition(List<Constraint> constraints, String templateName, String variableName) {
		super();
		this.constraints = constraints;
		this.templateName = templateName;
		this.varname = variableName;
		registerAsParentCondition();
	}
	
	public ObjectCondition(List<Constraint> constraints, String templateName) {
		this(constraints, templateName, null);
	}
	
	private void registerAsParentCondition() {
		for (Constraint c : constraints) c.setParentCondition(this);
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public String getVariableName() {
		return this.varname;
	}
	
	public void setVariableName(String varname) {
		this.varname = varname;
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
		if (varname == null) {
			return Collections.unmodifiableList(constraints);
		} else {
			List<Constraint> result = new ArrayList<Constraint>(constraints.size()+1);
			for (Constraint c : constraints) result.add(c);
			BoundConstraint objectBc = new BoundConstraint(varname,false);
			result.add(objectBc);
			return result;
		}
	}
	
	@Deprecated
	public void addConstraint(Constraint c) {
		constraints.add(c);
	}

}
