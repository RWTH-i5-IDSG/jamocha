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
import java.util.List;
import java.util.Stack;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.BoundParam;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.functions.compare.Eq;
import org.jamocha.rules.AndCondition;
import org.jamocha.rules.AndConnectedConstraint;
import org.jamocha.rules.BoundConstraint;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionWithNested;
import org.jamocha.rules.Constraint;
import org.jamocha.rules.LiteralConstraint;
import org.jamocha.rules.ObjectCondition;
import org.jamocha.rules.OrCondition;
import org.jamocha.rules.OrConnectedConstraint;
import org.jamocha.rules.PredicateConstraint;
import org.jamocha.rules.TestCondition;

/**
 * Translates OrConnectedConstraints to OrConditions
 * and PredicateConstraints to TestConditions.
 * 
 * Never translate AndConnectedConstraints to AndConditions,
 * since this is logically wrong!
 * 
 * @author Josef Hahn
 */
public class BeffyRuleOptimizerPassZero {

	public Condition optimize(List<Condition> cons) {
	
		/* find constraints. all constraints we are searching for,
		 * will only exist inside an object condition
		 */
		AndCondition bigMasterAnd = new AndCondition();
		for (Condition c : cons) bigMasterAnd.addNestedCondition(c);

		// We will remove predicate- and connective-constraints now!
		
		/*
		 * we imagine connective constraints in a flat way:
		 * 
		 * 					or              (OR-LAYER)
	 	 *    and			and	    and       (AND-LAYER)
	 	 *    a  b  c      d   e     f    (NONCONNECTED-LAYER)
		 *
		 * this is not yet true at this moment, but it is good
		 * to imagine it this way here.
		 * 
		 * so, at first we will handle the or-layer here this way:
		 * 1) substitute the object-condition (each connective
		 *    constraint is in an object-condition)
		 *    by an or-condition.
		 * 2) split the object-condition at the place of the or-connection
		 *    and insert the resulting conditions into the or-condition 
		 * 
		 * after that, we dont have and or-connected-constraints in our
		 * conditions. we handle the and-layer this way:
		 * we assume, that each node in the and-layer has
		 * EXACTLY ONE variable binding inside it.
		 * terms like (slotx ?a&?n) or (slotx :(eq 1 1)) maybe can make 
		 * sense, but is very rare.
		 * TODO the spellchecker MUST exclude that case.
		 *
		 * so, we have to find that variable binding now.
		 * all other nodes are literals and predicates, which we
		 * can simulate by adding test-conditions.
		 * 
		 * all generated conditions and the root conditions will be paired
		 * into a new and-condition which substitutes the old root condition
		 */

		Stack<Condition> conditionStack = new Stack<Condition>();
		conditionStack.addAll(cons);
		while (!conditionStack.isEmpty()) {
			Condition act = conditionStack.pop();
			
			// add new child Conditions to stack
			
			if (act instanceof ConditionWithNested) {
				ConditionWithNested cwn = (ConditionWithNested) act;
				for (Condition child : cwn.getNestedConditions()) {
					conditionStack.push(child);
				}
			} 
			else if (act instanceof ObjectCondition){	
				ObjectCondition oc = (ObjectCondition) act;
				for (Constraint constr : oc.getConstraints()) {
					if (constr instanceof OrConnectedConstraint) {
						List<Constraint> insides = flatOr((OrConnectedConstraint)constr);
						OrCondition newOrCond = new OrCondition();
						for (Constraint i: insides) {
							ObjectCondition noc = oc.clone();
							boolean s=noc.replaceConstraint(constr, i);
							if (!s) Logging.logger(this.getClass()).fatal("Internal error in RuleOptimizer: cannot replace a constraint "+constr.getClass().getSimpleName());
							newOrCond.addNestedCondition(noc);
						}
						conditionStack.add(newOrCond);
						ConditionWithNested parent = oc.getParentCondition();
						parent.replaceNestedCondition(oc, newOrCond);
						break;
					}
				}
			}
		}
		
		/* the or-connections are removed. now, we iterate once more for
		 * the and-layer
		 */
		conditionStack = new Stack<Condition>();
		conditionStack.add(bigMasterAnd);
		while (!conditionStack.isEmpty()) {
			Condition act = conditionStack.pop();
			if (act instanceof ConditionWithNested) {
				ConditionWithNested cwn = (ConditionWithNested) act;
				for (Condition child : cwn.getNestedConditions()) {
					conditionStack.push(child);
				}
			} 
			else if (act instanceof ObjectCondition){	
				ObjectCondition oc = (ObjectCondition) act;
				ConditionWithNested parent = oc.getParentCondition();
				for (Constraint constr : oc.getConstraints()) {
					if (constr instanceof AndConnectedConstraint) {
						List<Constraint> insides = flatAnd((AndConnectedConstraint)constr);
						AndCondition newAndCond = new AndCondition();
						/*
						 * we assume:
						 * in 'insides' there is EXACTLY ONE variable binding.
						 * all other constraints can be simulated by tests.
						 */
						BoundConstraint vari = null;
						for (Constraint c : insides) {
							if (c instanceof BoundConstraint) {
								vari = (BoundConstraint)c;
								break;
							}
						}
						assert (vari != null);
						newAndCond.addNestedCondition(oc);
						oc.replaceConstraint(constr, vari);
						
						for(Constraint ot: insides) {
							if (ot==vari) continue;
							if (ot instanceof LiteralConstraint) {
								List<Parameter> params = new ArrayList<Parameter>();
								params.add(ot.getValue());
								BoundParam variParam = new BoundParam(vari.getConstraintName());
								params.add(variParam);
								Signature sign = new Signature();
								sign.setSignatureName(Eq.NAME);
								sign.setParameters(params);
								TestCondition tc = new TestCondition(sign);
								newAndCond.addNestedCondition(tc);
							} else if (ot instanceof PredicateConstraint) {
								PredicateConstraint pred=(PredicateConstraint)ot;
								Signature sign = new Signature(pred.getFunctionName());
								sign.setParameters(pred.getParameters());
								TestCondition tc = new TestCondition(sign);
								newAndCond.addNestedCondition(tc);
							} else {
								Logging.logger(this.getClass()).fatal("unable to handle "+ot.getClass().getSimpleName()+" inside a connective-constraint!");
							}
						}
						conditionStack.add(newAndCond);
						parent.replaceNestedCondition(oc, newAndCond);
						break;
					}
				}
				
			}
		}
		return bigMasterAnd;
	}

	private List<Constraint> flatOr(OrConnectedConstraint constr) {
		List< Constraint > result = new ArrayList< Constraint >();
		Stack<Constraint> stack = new Stack<Constraint>();
		stack.add(constr);
		while (!stack.isEmpty()){
			Constraint c = stack.pop();
			if (c instanceof OrConnectedConstraint) {
				OrConnectedConstraint andcc= (OrConnectedConstraint)c;
				stack.add(andcc.getLeft());
				stack.add(andcc.getRight());
			} else {
				result.add(c.clone());
			}
		}
		return result;
	}

	private List<Constraint> flatAnd(AndConnectedConstraint constr) {
		List<Constraint> result = new ArrayList<Constraint>();
		Stack<Constraint> stack = new Stack<Constraint>();
		stack.add(constr);
		while (!stack.isEmpty()){
			Constraint c = stack.pop();
			if (c instanceof AndConnectedConstraint) {
				AndConnectedConstraint andcc= (AndConnectedConstraint)c;
				stack.add(andcc.getLeft());
				stack.add(andcc.getRight());
			} else {
				result.add(c.clone());
			}
		}
		return result;
	}

}
