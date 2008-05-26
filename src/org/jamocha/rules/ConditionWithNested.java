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