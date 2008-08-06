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
package org.jamocha.tests.testcases.beffy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.jamocha.engine.rules.rulecompiler.beffy.BeffyRuleOptimizer;
import org.jamocha.engine.rules.rulecompiler.beffy.BeffyRuleOptimizerPassOne;
import org.jamocha.engine.rules.rulecompiler.beffy.BeffyRuleOptimizerPassTwo;
import org.jamocha.rules.AndCondition;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionWithNested;
import org.jamocha.rules.Constraint;
import org.jamocha.rules.ExistsCondition;
import org.jamocha.rules.NotExistsCondition;
import org.jamocha.rules.ObjectCondition;
import org.jamocha.rules.OrCondition;

/**
 * @author Christoph Terwelp
 * @author Janno von Stuelpnagel
 *
 */
public class JTCBeffyRuleOptimizer extends TestCase {
	
	private BeffyRuleOptimizer optimizer;
	private BeffyRuleOptimizerPassOne passone;
	private BeffyRuleOptimizerPassTwo passtwo;
	
	public void setUp() {
		optimizer = new BeffyRuleOptimizer();
		passone = new BeffyRuleOptimizerPassOne();
		passtwo = new BeffyRuleOptimizerPassTwo();
	}
	
	private Condition runPassOne(List<Condition> list) {
		System.out.println("\n\nBefore pass one:");
		for (Condition condition : list) {
			System.out.println(condition.dump());
		}
		
		Condition result = passone.optimize(list);
		
		System.out.println("\nAfter pass one");
		System.out.println(result.dump());
		return result;
	}
	
	private List<Condition> initPassOneSimple1() {
		List<Condition> list = new LinkedList<Condition>();
		List<Constraint> constlist = new ArrayList<Constraint>();
		list.add(new ObjectCondition(constlist, "Test1"));
		list.add(new ObjectCondition(constlist, "Test2"));
		list.add(new ObjectCondition(constlist, "Test3"));
		return list;
	}
	
	private List<Condition> initPassOneSimple2() {
		List<Condition> list = new LinkedList<Condition>();
		List<Constraint> constlist = new ArrayList<Constraint>();
		ConditionWithNested cond = new AndCondition();
		ConditionWithNested cond2 = new ExistsCondition();
		cond2.addNestedCondition(new ObjectCondition(constlist, "Test1"));
		cond.addNestedCondition(cond2);
		list.add(cond);
		return list;
	}
	
	private List<Condition> initPassOneComplete() {
		List<Condition> list = new LinkedList<Condition>();
		List<Constraint> constlist = new ArrayList<Constraint>();
		
		ConditionWithNested cond = new NotExistsCondition();
		cond.addNestedCondition (new ObjectCondition(constlist, "Test1"));
		ConditionWithNested cond2 = new OrCondition();
		cond2.addNestedCondition(cond);
		cond2.addNestedCondition(new ObjectCondition(constlist, "Test2"));
		cond = new ExistsCondition();
		cond.addNestedCondition(cond2);
		cond2 = new OrCondition();
		cond2.addNestedCondition(cond);
		cond2.addNestedCondition(new ObjectCondition(constlist, "Test3"));
		list.add(cond2);
		
		cond = new NotExistsCondition();
		cond.addNestedCondition(new ObjectCondition(constlist, "Test6"));
		cond2 = new ExistsCondition();
		cond2.addNestedCondition(new ObjectCondition(constlist, "Test5"));
		cond2.addNestedCondition(cond);
		cond = new OrCondition();
		cond.addNestedCondition(new ObjectCondition(constlist, "Test4"));
		cond.addNestedCondition(cond2);
		cond2 = new NotExistsCondition();
		cond2.addNestedCondition(cond);
		list.add(cond2);
		
		cond = new NotExistsCondition();
		cond.addNestedCondition(new ObjectCondition(constlist, "Test8"));
		cond2 = new AndCondition();
		cond2.addNestedCondition(new ObjectCondition(constlist, "Test7"));
		cond2.addNestedCondition(cond);
		cond = new ExistsCondition();
		cond.addNestedCondition(cond2);
		list.add(cond);
		
		return list;
	}
	
	public Condition initPassOneCompleteResult() {
		AndCondition compareResult = new AndCondition();
		List<Constraint> constlist = new ArrayList<Constraint>();
		
		ConditionWithNested cond = new NotExistsCondition();
		cond.addNestedCondition(new ObjectCondition(constlist, "Test1"));
		ConditionWithNested cond2 = new OrCondition();
		cond2.addNestedCondition(cond);
		cond = new ExistsCondition();
		cond.addNestedCondition(new ObjectCondition(constlist, "Test2"));
		cond2.addNestedCondition(cond);
		cond = new OrCondition();
		cond.addNestedCondition(new ObjectCondition(constlist, "Test3"));
		cond.addNestedCondition(cond2);
		compareResult.addNestedCondition(cond);
		cond = new NotExistsCondition();
		cond.addNestedCondition(new ObjectCondition(constlist, "Test5"));
		cond2 = new OrCondition();
		cond2.addNestedCondition(cond);
		cond = new ExistsCondition();
		cond.addNestedCondition(new ObjectCondition(constlist, "Test6"));
		cond2.addNestedCondition(cond);
		cond = new AndCondition();
		cond.addNestedCondition(cond2);
		cond2 = new NotExistsCondition();
		cond2.addNestedCondition(new ObjectCondition(constlist, "Test4"));
		cond.addNestedCondition(cond2);
		compareResult.addNestedCondition(cond);
		cond = new ExistsCondition();
		cond.addNestedCondition(new ObjectCondition(constlist, "Test7"));
		cond2 = new AndCondition();
		cond2.addNestedCondition(cond);
		cond = new NotExistsCondition();
		cond.addNestedCondition(new ObjectCondition(constlist, "Test8"));
		cond2.addNestedCondition(cond);
		compareResult.addNestedCondition(cond2);
		return compareResult;
	}
	
//	public void testPassOneSimple1() {
//		List<Condition> list = initPassOneSimple1();
//		runPassOne(list);
//	}
	
	public void testPassOneSimple2() {
		List<Condition> list = initPassOneSimple2();
		runPassOne(list);
	}
	
//	public void testPassOneComplete() {
//		List<Condition> list = initPassOneComplete();
//		
//		Condition result = runPassOne(list);
//		
//		assertTrue(initPassOneCompleteResult().testEquals(result));
//	}
	
	public void testPassTwo() {
	}
}