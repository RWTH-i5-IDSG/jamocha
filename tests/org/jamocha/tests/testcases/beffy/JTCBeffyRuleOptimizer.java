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
 * @author Christoph Terwelp
 */
package org.jamocha.tests.testcases.beffy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.jamocha.engine.BoundParam;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.rules.rulecompiler.beffy.BeffyRuleOptimizer;
import org.jamocha.engine.rules.rulecompiler.beffy.BeffyRuleOptimizerPassOne;
import org.jamocha.engine.rules.rulecompiler.beffy.BeffyRuleOptimizerPassThree;
import org.jamocha.engine.rules.rulecompiler.beffy.BeffyRuleOptimizerPassTwo;
import org.jamocha.engine.rules.rulecompiler.beffy.OptimizeRuleException;
import org.jamocha.rules.AndCondition;
import org.jamocha.rules.BoundConstraint;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionWithNested;
import org.jamocha.rules.Constraint;
import org.jamocha.rules.ExistsCondition;
import org.jamocha.rules.NotExistsCondition;
import org.jamocha.rules.ObjectCondition;
import org.jamocha.rules.OrCondition;
import org.jamocha.rules.TestCondition;

/**
 *  
 * @author Christoph Terwelp
 * @author Janno von Stuelpnagel
 */
public class JTCBeffyRuleOptimizer extends TestCase {
	
	private BeffyRuleOptimizer optimizer;
	private BeffyRuleOptimizerPassOne passone;
	private BeffyRuleOptimizerPassTwo passtwo;
	private BeffyRuleOptimizerPassThree passthree;
	
	public void setUp() {
		optimizer = new BeffyRuleOptimizer();
		passone = new BeffyRuleOptimizerPassOne();
		passtwo = new BeffyRuleOptimizerPassTwo();
		passthree = new BeffyRuleOptimizerPassThree();
	}
	
	private Condition runPassOne(Condition cond) {
//		System.out.println("\n\nBefore pass one:");
//		for (Condition condition : list) {
//			System.out.println(condition.dump());
//		}
		
		Condition result = passone.optimize(cond);
		
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
	
	private Condition runPassThree(Condition cond) throws OptimizeRuleException {
//		System.out.println("\nBefore pass three");
//		System.out.println(cond.dump());
		
		Condition result = passthree.optimize(cond);
		
//		System.out.println("\nAfter pass three");
//		System.out.println(result.dump());
		return result;
	}
	
	private Condition initPassOneSimple1() {
		AndCondition and = new AndCondition();
		List<Constraint> constlist = new ArrayList<Constraint>();
		and.addNestedCondition(new ObjectCondition(constlist, "Test1"));
		and.addNestedCondition(new ObjectCondition(constlist, "Test2"));
		and.addNestedCondition(new ObjectCondition(constlist, "Test3"));
		return and;
	}
	
	private Condition initPassOneSimple2() {
		AndCondition and = new AndCondition();
		List<Constraint> constlist = new ArrayList<Constraint>();
		ConditionWithNested cond = new AndCondition();
		ConditionWithNested cond2 = new ExistsCondition();
		cond2.addNestedCondition(new ObjectCondition(constlist, "Test1"));
		cond.addNestedCondition(cond2);
		and.addNestedCondition(cond);
		return and;
	}
	
	private Condition initPassOneComplete() {
		AndCondition and = new AndCondition();
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
		and.addNestedCondition(cond2);
		
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
		and.addNestedCondition(cond2);
		
		cond = new NotExistsCondition();
		cond.addNestedCondition(new ObjectCondition(constlist, "Test8"));
		cond2 = new AndCondition();
		cond2.addNestedCondition(new ObjectCondition(constlist, "Test7"));
		cond2.addNestedCondition(cond);
		cond = new ExistsCondition();
		cond.addNestedCondition(cond2);
		and.addNestedCondition(cond);
		
		return and;
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
	
	public Condition initPassThreeSimple1() {
		OrCondition orCond = new OrCondition();
		AndCondition andCond = new AndCondition();
		List<Constraint> constList = new LinkedList<Constraint>();
		orCond.addNestedCondition(new AndCondition());
		orCond.addNestedCondition(new ObjectCondition(constList, "somename"));
		andCond.addNestedCondition(new ObjectCondition(constList, "1"));
		andCond.addNestedCondition(new ObjectCondition(constList, "2"));
		andCond.addNestedCondition(new ObjectCondition(constList, "3"));
		andCond.addNestedCondition(new ObjectCondition(constList, "4"));
		orCond.addNestedCondition(andCond);
		return orCond;
	}
	
	public void checkPassThreeSimple1(Condition cond) {
		assertTrue(cond instanceof OrCondition);
		OrCondition orCond = (OrCondition) cond;
		List<Condition> conds = orCond.getNestedConditions();
		Iterator<Condition> i = conds.iterator();
		assertTrue(i.hasNext());
		cond = i.next();
		assertTrue(cond instanceof ObjectCondition);
		assertTrue(((ObjectCondition)cond).getTemplateName().equals("somename"));
		assertTrue(i.hasNext());
		cond = i.next();
		assertTrue(cond instanceof AndCondition);
		AndCondition andCond = (AndCondition)cond;
		checkSizeLimit(andCond, 2);
		andCond = flattenAndCondition(andCond);
		List<String> list = new LinkedList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		for (Condition c : andCond.getNestedConditions()) {
			assertTrue(c instanceof ObjectCondition);
			assertTrue(list.remove(((ObjectCondition)c).getTemplateName()));
		}
		assertTrue(list.isEmpty());
		assertTrue(!i.hasNext());
	}
	
	public Condition initPassThreeSimple2() {
		Signature sig;
		Parameter[] params;
		AndCondition andCon;
		OrCondition orCon;
		LinkedList<Constraint> constList;
		Condition con1;
		
		andCon = new AndCondition();
		
		// Test 1: requires Object 1
		params = new Parameter[1];
		params[0] = new BoundParam("var1");
		sig = new Signature("test1");
		sig.setParameters(params);
		con1 = new TestCondition(sig);
		andCon.addNestedCondition(con1);
		
		// Test 2: requires Object 2 and Object 3
		params = new Parameter[2];
		params[0] = new BoundParam("var2");
		params[1] = new BoundParam("var3");
		sig = new Signature("test2");
		sig.setParameters(params);
		con1 = new TestCondition(sig);
		andCon.addNestedCondition(con1);
		
		// Object 1
		constList = new LinkedList<Constraint>();
		constList.add(new BoundConstraint("var1", false));
		con1 = new ObjectCondition(constList, "object1");
		andCon.addNestedCondition(con1);
		
		// Object 2
		constList = new LinkedList<Constraint>();
		constList.add(new BoundConstraint("var2", false));
		con1 = new ObjectCondition(constList, "object2");
		andCon.addNestedCondition(con1);
		
		// Object 3
		constList = new LinkedList<Constraint>();
		constList.add(new BoundConstraint("var3", false));
		con1 = new ObjectCondition(constList, "object3");
		andCon.addNestedCondition(con1);
		
		// Object 4
		constList = new LinkedList<Constraint>();
		constList.add(new BoundConstraint("var4", false));
		con1 = new ObjectCondition(constList, "object4");
		andCon.addNestedCondition(con1);
		
		// Object 5: requires Object 4
		constList = new LinkedList<Constraint>();
		constList.add(new BoundConstraint("var4", true));
		con1 = new ObjectCondition(constList, "object5");
		andCon.addNestedCondition(con1);
		
		orCon = new OrCondition();
		orCon.addNestedCondition(andCon);
		
		return orCon;
	}
	
	public boolean searchObjectCondition(Condition container, String name) {
		if (container instanceof ObjectCondition) 
			if (((ObjectCondition)container).getTemplateName() == name) return true;
		
		if (container instanceof ConditionWithNested) {
			for (Condition c:((ConditionWithNested)container).getNestedConditions()) {
				if (searchObjectCondition(c,name)) return true;
			}
		}
		
		return false;
	}
	
	public int[] addIntArray(int[] o, int[] p) {
		int length = 0;
		if (o.length > p.length) {
			length = p.length;
		} else {
			length = o.length;
		}
		int[] out = new int[length];
		for (int i = 0; i < length; i++) {
			out[i] = o[i] + p[i];
		}
		return out;
	}
	
	public int[] checkPassThreeSimple2Sub(Condition cond, int[] a) {
		if (cond instanceof TestCondition) {
			TestCondition testCond = (TestCondition)cond;
			String name = testCond.getFunction().getSignatureName();
			if (name == "test1") {
				assertTrue(searchObjectCondition(testCond.getParentCondition(), "object1"));
				a[0]++;
				return a;
			}
			if (name == "test2") {
				assertTrue(searchObjectCondition(testCond.getParentCondition(), "object2"));
				assertTrue(searchObjectCondition(testCond.getParentCondition(), "object3"));
				a[1]++;
				return a;
			}
		}
		if (cond instanceof ObjectCondition) {
			ObjectCondition objCond = (ObjectCondition)cond;
			String name = objCond.getTemplateName();
			if (name == "object1") {
				a[2]++;
				return a;
			}
			if (name == "object2") {
				a[3]++;
				return a;
			}
			if (name == "object3") {
				a[4]++;
				return a;
			}
			if (name == "object4") {
				a[5]++;
				return a;
			}
			if (name == "object5") {
				assertTrue(searchObjectCondition(objCond.getParentCondition(), "object4"));
				a[6]++;
				return a;
			}
			assertTrue("Object with unknown name: \"" + name + "\"", false);
			return null;
		}
		if (cond instanceof ConditionWithNested) {
			ConditionWithNested cwn = (ConditionWithNested)cond;
			for (Condition c: cwn.getNestedConditions()) {
				a = checkPassThreeSimple2Sub(c, a);
			}
			return a;
		}
		assertTrue("Unknown object in resulting tree: \"" + cond.toString() + "\"", false);
		return null;
	}
	
	public void checkPassThreeSimple2(Condition cond) {
		int[] ret = checkPassThreeSimple2Sub(cond, new int[7]);
		for (int i: ret) {
			assertEquals(i, 1);
		}
	}
	
	public void checkSizeLimit(AndCondition cond, int limit) {
		assertTrue(cond.getNestedConditions().size() <= limit);
		for (Condition c : cond.getNestedConditions()) {
			if (c instanceof AndCondition) {
				checkSizeLimit(((AndCondition) c), limit);
			}
		}
	}
	
	public void flattenAndCondition(AndCondition cond, AndCondition newAnd) {
		for (Condition c : cond.getNestedConditions()) {
			if (c instanceof AndCondition) {
				flattenAndCondition((AndCondition)c, newAnd);
			} else {
				newAnd.addNestedCondition(c);
			}
		}
	}
	
	public AndCondition flattenAndCondition(AndCondition cond) {
		AndCondition result = new AndCondition();
		flattenAndCondition(cond, result);
		return result;
	}
	
	public void testPassOneSimple1() {
		Condition cond = initPassOneSimple1();
		runPassOne(cond);
	}
	
	public void testPassOneSimple2() {
		Condition cond = initPassOneSimple2();
		runPassOne(cond);
	}
	
	public void testPassOneTwoComplete() {
		Condition cond = initPassOneComplete();
		Condition cond2 = initPassOneComplete();
		
		assertTrue("testEquals does not correctly compare two Conditions", cond.testEquals(cond2));
		assertTrue("clone does not correctly clone a Condition", cond.testEquals(cond2.clone()));
		
		Condition result = runPassOne(cond);
		Condition result2 = runPassOne(cond2);
		
		//assertTrue(initPassOneCompleteResult().testEquals(result));
		
		result = runPassTwo(result);
		result2 = runPassTwo(result2);
		
		assertTrue("two runs do not return same result", result.testEquals(result2));
		
		result = runPassOne(result);
		result = runPassTwo(result);
		
		assertTrue("running twice does not return same result", result.testEquals(result2));
		
		Condition assumedresult = initPassTwoCompleteResult();
//		System.out.println(result.dump());
//		System.out.println(assumedresult.dump());
		
		assertTrue("result does not equal assumed result", result.testEquals(assumedresult));
	}
	
	public void testPassThreeSimple1() throws OptimizeRuleException {
		Condition cond = initPassThreeSimple1();
		Condition result = runPassThree(cond);
		checkPassThreeSimple1(result);
	}
	
	public void testPassThreeSimple2() throws OptimizeRuleException {
		Condition cond = initPassThreeSimple2();
		Condition result = runPassThree(cond);
		checkPassThreeSimple2(result);
	}
}