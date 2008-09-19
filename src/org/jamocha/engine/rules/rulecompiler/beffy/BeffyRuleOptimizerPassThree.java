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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
 * @author Janno von Stuelpnagel
 *
 */
public class BeffyRuleOptimizerPassThree implements
		ConditionVisitor<Object, BeffyRuleOptimizerDataPassThree>, ConstraintVisitor<Object, BeffyRuleOptimizerDataPassThree> {
	
	/*
	 * Call optimizeAnd for every AndCondition in the toplevel OrCondition
	 */
	public Condition optimize(Condition cond) throws OptimizeRuleException {
		if (! (cond instanceof OrCondition))
			throw new OptimizeRuleException(
					"Invalid root in condition tree. Tried to use PassThree without PassOne and PassTwo?");
		OrCondition orCondition = (OrCondition) cond;
		for (Condition condition : orCondition.getNestedConditions()) {
			if (condition instanceof AndCondition) {
				optimizeAnd((AndCondition) condition);
			} else if (! (condition instanceof ObjectCondition))
				throw new OptimizeRuleException("Invalid condition directly below root in condition tree. Tried to use PassThree without PassOne and PassTwo?");
		}
		return cond;
	}
	
	/*
	 * combines i + 1 (the first one is inserted in optimizeAnd
	 * and check if it is a valid combination (there should be no unbound variables).
	 * If it is the conditions are joined in an And.
	 */
	private boolean combine(Set<BeffyRuleOptimizerDataPassThree> set,
			int i,
			List<BeffyRuleOptimizerDataPassThree> newBindings,
			List<BeffyRuleOptimizerDataPassThree> oldBindings,
			List<BeffyRuleOptimizerDataPassThree> veryNewBindings) {
		if (i == 0) {
			BeffyRuleOptimizerDataPassThree res = BeffyRuleOptimizerDataPassThree.combine(set);
			if (res.isBound()) {
				oldBindings.removeAll(set);
				newBindings.removeAll(set);
				AndCondition and = new AndCondition();
				for (BeffyRuleOptimizerDataPassThree d : set) {
					and.addNestedCondition(d.getCondition());
				}
				res.setCondition(and);
				veryNewBindings.add(res);
				return true;
			}
			return false;
		}
		for (BeffyRuleOptimizerDataPassThree d : oldBindings) {
			set.add(d);
			if (combine(set, i-1, oldBindings, newBindings, veryNewBindings)) return true;
			set.remove(d);
		}
		for (BeffyRuleOptimizerDataPassThree d : newBindings) {
			set.add(d);
			if (combine(set, i-1, oldBindings, newBindings, veryNewBindings)) return true;
			set.remove(d);
		}
		return false;
	}
	
	/*
	 * Analyze every Condition in an AndCondition where which variable is bound and used.
	 * Split Ands in two and multi Ands, depending on the analysis.
	 */
	private Condition optimizeAnd(AndCondition cond) throws OptimizeRuleException {
		List<BeffyRuleOptimizerDataPassThree> newBindings = new LinkedList<BeffyRuleOptimizerDataPassThree>();
		for (Condition condition : cond.getNestedConditions()) {
			BeffyRuleOptimizerDataPassThree d = condition.acceptVisitor(this, null);
			if (d == null)
				throw new OptimizeRuleException("Invalid element in AndCondition. Tried to use PassThree without PassOne and PassTwo?");
			d.setCondition(condition);
			newBindings.add(d);
		}
		
		List<BeffyRuleOptimizerDataPassThree> oldBindings = new LinkedList<BeffyRuleOptimizerDataPassThree>();
		List<BeffyRuleOptimizerDataPassThree> veryNewBindings = new LinkedList<BeffyRuleOptimizerDataPassThree>();
		int i = 2;
		while (!(oldBindings.size() == 1 && newBindings.isEmpty())) {
			for (BeffyRuleOptimizerDataPassThree numberOne : newBindings) {
				Set<BeffyRuleOptimizerDataPassThree> set = new HashSet<BeffyRuleOptimizerDataPassThree>();
				set.add(numberOne);
				combine(set, i-1, oldBindings, newBindings, veryNewBindings);
			}
			oldBindings.addAll(newBindings);
			newBindings = veryNewBindings;
			if (newBindings.isEmpty()) i++;
			veryNewBindings = new LinkedList<BeffyRuleOptimizerDataPassThree>();
		}
		
		return oldBindings.get(0).getCondition();
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
		d.markUnbound();
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
		d.markUnbound();
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
//		BeffyRuleOptimizerDataPassThree d = c.getLeft().acceptVisitor(this, null);
//		d.combine(c.getRight().acceptVisitor(this, null));
//		d.markVirtual();
//		return d;
		return null;
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
//		BeffyRuleOptimizerDataPassThree d = new BeffyRuleOptimizerDataPassThree();
//		for (Parameter parameter : c.getParameters()) {
//		}
//		return d;
		return new BeffyRuleOptimizerDataPassThree();
	}

	public BeffyRuleOptimizerDataPassThree visit(ReturnValueConstraint c,
			Object data) {
		// Not implemented
//		BeffyRuleOptimizerDataPassThree d = new BeffyRuleOptimizerDataPassThree();
//		for (Parameter parameter : c.getParameters()) {
//		}
//		return d;
		return new BeffyRuleOptimizerDataPassThree();
	}

}
