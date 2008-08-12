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

/**
 * 
 */
package org.jamocha.engine.rules.rulecompiler.beffy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jamocha.engine.rules.rulecompiler.CompileRuleException;
import org.jamocha.rules.AndCondition;
import org.jamocha.rules.AndConnectedConstraint;
import org.jamocha.rules.BoundConstraint;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionVisitor;
import org.jamocha.rules.Constraint;
import org.jamocha.rules.ConstraintVisitor;
import org.jamocha.rules.ExistsCondition;
import org.jamocha.rules.LiteralConstraint;
import org.jamocha.rules.NotExistsCondition;
import org.jamocha.rules.ObjectCondition;
import org.jamocha.rules.OrCondition;
import org.jamocha.rules.OrConnectedConstraint;
import org.jamocha.rules.OrderedFactConstraint;
import org.jamocha.rules.PredicateConstraint;
import org.jamocha.rules.ReturnValueConstraint;
import org.jamocha.rules.TestCondition;

/**
 * @author Christoph Terwelp
 *
 */
public class BeffyRuleOptimizerPassThree implements
		ConditionVisitor<Object, BeffyRuleOptimizerDataPassThree>, ConstraintVisitor<Object, BeffyRuleOptimizerDataPassThree> {
	
	public Condition optimize(Condition cond) throws CompileRuleException {
		if (! (cond instanceof OrCondition))
			throw new CompileRuleException(
					"Invalid root in condition tree. Tried to use PassThree without PassOne and PassTwo?");
		OrCondition orCondition = (OrCondition) cond;
		for (Condition condition : orCondition.getNestedConditions()) {
			if (condition instanceof AndCondition) {
				optimizeAnd((AndCondition) condition);
			} else if (! (condition instanceof ObjectCondition))
				throw new CompileRuleException("Invalid condition directly below root in condition tree. Tried to use PassThree without PassOne and PassTwo?");
		}
		return cond;
	}
	
	private AndCondition optimizeAnd(AndCondition cond) throws CompileRuleException {
		List<BeffyRuleOptimizerDataPassThree> nonvirtual = new LinkedList<BeffyRuleOptimizerDataPassThree>();
		List<BeffyRuleOptimizerDataPassThree> virtual = new LinkedList<BeffyRuleOptimizerDataPassThree>();
		for (Condition condition : cond.getNestedConditions()) {
			BeffyRuleOptimizerDataPassThree d = condition.acceptVisitor(this, null);
			if (d == null)
				throw new CompileRuleException("Invalid element in AndCondition. Tried to use PassThree without PassOne and PassTwo?");
			d.setCondition(condition);
			if (d.isVirtual())
				virtual.add(d);
			else
				nonvirtual.add(d);
		}
		
		for (BeffyRuleOptimizerDataPassThree data : virtual) {
		}
		
		return cond;
	}

	public BeffyRuleOptimizerDataPassThree visit(AndCondition c, Object data) {
		return null;
	}

	public BeffyRuleOptimizerDataPassThree visit(ExistsCondition c, Object data) {
		if (c.getNestedConditions().size() != 1) return null;
		return c.getNestedConditions().get(0).acceptVisitor(this, null);
	}

	public BeffyRuleOptimizerDataPassThree visit(NotExistsCondition c,
			Object data) {
		if (c.getNestedConditions().size() != 1) return null;
		BeffyRuleOptimizerDataPassThree d = c.getNestedConditions().get(0).acceptVisitor(this, null);
		d.markVirtual();
		return d;
	}

	public BeffyRuleOptimizerDataPassThree visit(ObjectCondition c, Object data) {
		BeffyRuleOptimizerDataPassThree d = new BeffyRuleOptimizerDataPassThree();
		for (Constraint constraint : c.getConstraints()) {
			d.combine(constraint.acceptVisitor(this, null));
		}
		return d;
	}

	public BeffyRuleOptimizerDataPassThree visit(OrCondition c, Object data) {
		return null;
	}

	public BeffyRuleOptimizerDataPassThree visit(TestCondition c, Object data) {
		BeffyRuleOptimizerDataPassThree d = new BeffyRuleOptimizerDataPassThree();
		for (Constraint constraint : c.getConstraints()) {
			d.combine(constraint.acceptVisitor(this, null));
		}
		d.markVirtual();
		return d;
	}

	public BeffyRuleOptimizerDataPassThree visit(AndConnectedConstraint c,
			Object data) {
		BeffyRuleOptimizerDataPassThree d = c.getLeft().acceptVisitor(this, null);
		d.combine(c.getRight().acceptVisitor(this, null));
		return d;
	}

	public BeffyRuleOptimizerDataPassThree visit(BoundConstraint c, Object data) {
		BeffyRuleOptimizerDataPassThree d = new BeffyRuleOptimizerDataPassThree();
		d.add(c.getConstraintName(), c.isNegated());
		return d;
	}

	public BeffyRuleOptimizerDataPassThree visit(LiteralConstraint c,
			Object data) {
		return new BeffyRuleOptimizerDataPassThree();
	}

	public BeffyRuleOptimizerDataPassThree visit(OrConnectedConstraint c,
			Object data) {
		BeffyRuleOptimizerDataPassThree d = c.getLeft().acceptVisitor(this, null);
		d.combine(c.getRight().acceptVisitor(this, null));
		return d;
	}

	public BeffyRuleOptimizerDataPassThree visit(OrderedFactConstraint c,
			Object data) {
		BeffyRuleOptimizerDataPassThree d = new BeffyRuleOptimizerDataPassThree();
		for (Constraint constraint : c.getConstraints()) {
			d.combine(constraint.acceptVisitor(this, null));
		}
		return d;
	}

	public BeffyRuleOptimizerDataPassThree visit(PredicateConstraint c,
			Object data) {
		return new BeffyRuleOptimizerDataPassThree();
	}

	public BeffyRuleOptimizerDataPassThree visit(ReturnValueConstraint c,
			Object data) {
		return new BeffyRuleOptimizerDataPassThree();
	}

}
