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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.jamocha.engine.configurations.Signature;
import org.jamocha.rules.AbstractConnectedConstraint;
import org.jamocha.rules.AndCondition;
import org.jamocha.rules.AndConnectedConstraint;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionWithNested;
import org.jamocha.rules.Constraint;
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
			// handle if object condition
			else if (act instanceof ObjectCondition){	
				ObjectCondition oc = (ObjectCondition) act;
				for (Constraint constr : oc.getConstraints()) {
					handleConstraint(constr);
				}
			}
		}
		return bigMasterAnd;
	}

	private void handleConstraint(Constraint constr) {
		if (constr instanceof OrConnectedConstraint) {
			handleOrConnectedConstraint((OrConnectedConstraint)constr);
		} else if (constr instanceof PredicateConstraint) {
			handlePredicateConstraint((PredicateConstraint)constr);
		} 
	}

	private void handlePredicateConstraint(PredicateConstraint constr) {
		// generate new test condition
		TestCondition tCond = predConstr2testCond(constr);
		
		// remove predicate constraint
		ObjectCondition owner = (ObjectCondition) constr.getParentCondition();
		owner.getConstraints().remove(constr);
		
		// generate and-condition with old objectcondition and testcondition
		AndCondition substituter = new AndCondition();
		substituter.addNestedCondition(owner);
		substituter.addNestedCondition(tCond);
		
		// replace old objectcondition with the new generated and-condition
		ConditionWithNested upper = owner.getParentCondition();
		upper.replaceNestedCondition(owner, substituter);
		
	}
	
	private TestCondition predConstr2testCond(PredicateConstraint constr) {
		Signature function = new Signature();
		function.setParameters(constr.getParameters());
		function.setSignatureName(constr.getFunctionName());
		TestCondition result = new TestCondition(function);
		return result;
	}

	private <J extends AbstractConnectedConstraint> Collection<Constraint> split (J constr) {
		Stack<Constraint> junctionStack = new Stack<Constraint>();
		List<Constraint> result = new ArrayList<Constraint>();
		junctionStack.push(constr.getLeft());
		junctionStack.push(constr.getRight());
		
		while (!junctionStack.isEmpty()) {
			Constraint c = junctionStack.pop();
			
			/*
			 * this condition is a replacement for (c instanceof J),
			 * which is forbidden because of the java backward compatibility
			 * (for more infos, search for "type erasure")
			 */
			if (c.getClass().equals(constr.getClass())) {
				AbstractConnectedConstraint acc = (AbstractConnectedConstraint) c;
				junctionStack.push(acc.getLeft());
				junctionStack.push(acc.getRight());
			} else {
				result.add(c);
			}
		}
		return result;
	}
	
	
	private <CONSTR extends AbstractConnectedConstraint> 
	void handleConnectedConstraint(CONSTR constr, ConditionWithNested newNested) {
		// we translate it into an or/and-_condition_-construct
		Collection<Constraint> connectedBy = split(constr); 
		
		// get the object condition, we process now
		Condition main = constr.getParentCondition();
		
		// fork this object condition and put all clones into a new nested node
		for (Constraint sub : connectedBy) {
			assert main instanceof ObjectCondition;
			ObjectCondition cloned = (ObjectCondition)main.clone();
			cloned.getConstraints().remove(constr);
			cloned.getConstraints().add(sub);
			newNested.addNestedCondition(cloned);
		}
		
		// get the parent condition-with-nested and replace here the
		// "main"-condition with the newNested one
		ConditionWithNested cwn = main.getParentCondition();
		cwn.replaceNestedCondition(main, newNested);
	}
	
	private void handleOrConnectedConstraint(OrConnectedConstraint constr) {
		handleConnectedConstraint(constr, new OrCondition() );
	}


}
