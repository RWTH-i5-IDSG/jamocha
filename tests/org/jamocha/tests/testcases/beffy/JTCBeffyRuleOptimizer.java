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
import java.util.Iterator;
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
 *  
 * @author Christoph Terwelp
 * @author Janno von Stuelpnagel
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
//		System.out.println("\n\nBefore pass one:");
//		for (Condition condition : list) {
//			System.out.println(condition.dump());
//		}
		
		Condition result = passone.optimize(list);
		
//		System.out.println("\nAfter pass one");
//		System.out.println(result.dump());
		return result;
	}
	
	private Condition runPassTwo(Condition cond) {
//		System.out.println("\n\nBefore pass two:");
//		System.out.println(cond.dump());
		
		Condition result = passtwo.optimize(cond);
		
//		System.out.println("\nAfter pass two");
//		System.out.println(result.dump());
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
	
	public Condition initPassTwoCompleteResult() {
		OrCondition orCondition = new OrCondition();
		List<Constraint> constlist = new ArrayList<Constraint>();
		
		AndCondition baseAndCondition = new AndCondition();
		ObjectCondition objectCondition = new ObjectCondition(constlist, "Test7");
		ConditionWithNested exCondition = new ExistsCondition();
		exCondition.addNestedCondition(objectCondition);
		baseAndCondition.addNestedCondition(exCondition);
		
		objectCondition = new ObjectCondition(constlist, "Test8");
		exCondition = new NotExistsCondition();
		exCondition.addNestedCondition(objectCondition);
		baseAndCondition.addNestedCondition(exCondition);
		
		objectCondition = new ObjectCondition(constlist, "Test4");
		exCondition = new NotExistsCondition();
		exCondition.addNestedCondition(objectCondition);
		baseAndCondition.addNestedCondition(exCondition);
		
		
		AndCondition base2AndCondition = (AndCondition) baseAndCondition.clone();
				
		objectCondition = new ObjectCondition(constlist, "Test5");
		exCondition = new NotExistsCondition();
		exCondition.addNestedCondition(objectCondition);
		base2AndCondition.addNestedCondition(exCondition);
		
		
		AndCondition andCondition = (AndCondition) base2AndCondition.clone();
		
		objectCondition = new ObjectCondition(constlist, "Test3");
		andCondition.addNestedCondition(objectCondition);
		
		orCondition.addNestedCondition(andCondition);
		
		
		andCondition = (AndCondition) base2AndCondition.clone();
		
		objectCondition = new ObjectCondition(constlist, "Test2");
		exCondition = new ExistsCondition();
		exCondition.addNestedCondition(objectCondition);
		andCondition.addNestedCondition(exCondition);
		
		orCondition.addNestedCondition(andCondition);
		

		andCondition = (AndCondition) base2AndCondition.clone();
		
		objectCondition = new ObjectCondition(constlist, "Test1");
		exCondition = new NotExistsCondition();
		exCondition.addNestedCondition(objectCondition);
		andCondition.addNestedCondition(exCondition);
		
		orCondition.addNestedCondition(andCondition);
		
		
		base2AndCondition = (AndCondition) baseAndCondition.clone();
		
		objectCondition = new ObjectCondition(constlist, "Test6");
		exCondition = new ExistsCondition();
		exCondition.addNestedCondition(objectCondition);
		base2AndCondition.addNestedCondition(exCondition);
		
		
		andCondition = (AndCondition) base2AndCondition.clone();
		
		
		andCondition = (AndCondition) base2AndCondition.clone();
		
		objectCondition = new ObjectCondition(constlist, "Test3");
		andCondition.addNestedCondition(objectCondition);
		
		orCondition.addNestedCondition(andCondition);
		
		
		andCondition = (AndCondition) base2AndCondition.clone();
		
		objectCondition = new ObjectCondition(constlist, "Test2");
		exCondition = new ExistsCondition();
		exCondition.addNestedCondition(objectCondition);
		andCondition.addNestedCondition(exCondition);
		
		orCondition.addNestedCondition(andCondition);
		

		andCondition = (AndCondition) base2AndCondition.clone();
		
		objectCondition = new ObjectCondition(constlist, "Test1");
		exCondition = new NotExistsCondition();
		exCondition.addNestedCondition(objectCondition);
		andCondition.addNestedCondition(exCondition);
		
		orCondition.addNestedCondition(andCondition);
	
		
		return orCondition;
	}
	
	public Condition initPassThreeComplete() {
		return null;
	}
	
	public Condition initPassThreeCompleteResult() {
		return null;
	}
	
//	public void testPassOneSimple1() {
//		List<Condition> list = initPassOneSimple1();
//		runPassOne(list);
//	}
	
//	public void testPassOneSimple2() {
//		List<Condition> list = initPassOneSimple2();
//		runPassOne(list);
//	}
	
	public void testPassOneTwoComplete() {
		List<Condition> list = initPassOneComplete();
		List<Condition> list2 = initPassOneComplete();
		
		Iterator<Condition> i2 = list2.iterator();
		Iterator<Condition> i1 = list.iterator();
		
		while (i1.hasNext() && i2.hasNext()) {
			Condition c1 = i1.next();
			Condition c2 = i2.next();
			Condition c3 = c1.clone();
			assertTrue("testEquals does not correctly compare two Conditions", c1.testEquals(c2));
			assertTrue("clone does not correctly clone a Condition", c1.testEquals(c3));
		}
		
		Condition result = runPassOne(list);
		Condition result2 = runPassOne(list2);
		
		//assertTrue(initPassOneCompleteResult().testEquals(result));
		
		result = runPassTwo(result);
		result2 = runPassTwo(result2);
		
		assertTrue("two runs do not return same result", result.testEquals(result2));
		
		list = new ArrayList<Condition>();
		list.add(result);
		result = runPassOne(list);
		result = runPassTwo(result);
		
		assertTrue("running twice does not return same result", result.testEquals(result2));
		
		Condition assumedresult = initPassTwoCompleteResult();
//		System.out.println(result.dump());
//		System.out.println(assumedresult.dump());
		
		assertTrue("result does not equal assumed result", result.testEquals(assumedresult));
	}
	
	public void testPassThreeComplete() {
	}
}