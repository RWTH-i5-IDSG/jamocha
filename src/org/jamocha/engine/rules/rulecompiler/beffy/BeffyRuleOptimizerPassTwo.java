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
import java.util.LinkedList;
import java.util.List;

import org.jamocha.rules.AndCondition;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ExistsCondition;
import org.jamocha.rules.ConditionVisitor;
import org.jamocha.rules.NotExistsCondition;
import org.jamocha.rules.ObjectCondition;
import org.jamocha.rules.OrCondition;
import org.jamocha.rules.TestCondition;

/**
 * Pass two of the rule optimizer transforms the result
 * of pass one into a DNF like form.
 * 
 * The root node of the resulting tree is an OrCondition
 * which contains only Object and And conditions. The and
 * conditions only contain Object, Test, Exists and NotExits
 * conditions. Exists and NotExists conditions only contain
 * one Object or Test condition.
 * 
 * @author Christoph Terwelp
 * @author Janno von Stuelpnagel
 */
public class BeffyRuleOptimizerPassTwo implements ConditionVisitor<BeffyRuleOptimizerDataPassTwo, List<Condition>> {
	
	public Condition optimize(Condition con) {
		List<Condition> cons = con.acceptVisitor(this, null);
		OrCondition orcon = new OrCondition();
		for (Condition condition : cons)
			orcon.addNestedCondition(condition);
		return orcon;
	}
	
	/**
	 * Flattens a List of Lists of Conditions:
	 * A1 x A2 x ... for A1 element of List 1, A2 element of List 2, ...
	 * And combining the elements if they are AndConditions.
	 * 
	 * @param conditions
	 * @return
	 */
	private List<Condition> flatten(List<List<Condition>> conditions) {
		List<Condition> list1 = conditions.remove(0);
		if (conditions.isEmpty())
			return list1;
		
		List<Condition> list2 = flatten(conditions);
		List<Condition> retlist = new LinkedList<Condition>();
		for (Condition condition1 : list1) {
			for (Condition condition2 : list2) {
				AndCondition tmp = new AndCondition();
				if (condition1 instanceof AndCondition) {
					AndCondition andcondition = (AndCondition) condition1;
					for (Condition condition : andcondition.getNestedConditions())
						tmp.addNestedCondition(condition.clone());
				} else {
					tmp.addNestedCondition(condition1.clone());
				}
				
				if (condition2 instanceof AndCondition) {
					AndCondition andcondition = (AndCondition) condition2;
					for (Condition condition : andcondition.getNestedConditions())
						tmp.addNestedCondition(condition.clone());
				} else {
					tmp.addNestedCondition(condition2.clone());
				}
				
				List<Condition> l = tmp.getNestedConditions();
				if (l.size() > 1)
					retlist.add(tmp);
				else
					retlist.add(l.get(0));
			}
		}
		return retlist;
	}

	public List<Condition> visit(AndCondition c, BeffyRuleOptimizerDataPassTwo data) {
		List<List<Condition>> list = new LinkedList<List<Condition>>();
		for (Condition condition : c.getNestedConditions()) {
			list.add(condition.acceptVisitor(this, data));
		}
		return flatten(list);
	}

	public List<Condition> visit(ExistsCondition c, BeffyRuleOptimizerDataPassTwo data) {
		List<Condition> list = new ArrayList<Condition>();
		list.add(c);
		return list;
	}

	public List<Condition> visit(NotExistsCondition c, BeffyRuleOptimizerDataPassTwo data) {
		List<Condition> list = new ArrayList<Condition>();
		list.add(c);
		return list;
	}

	public List<Condition> visit(ObjectCondition c, BeffyRuleOptimizerDataPassTwo data) {
		List<Condition> list = new ArrayList<Condition>();
		list.add(c);
		return list;
	}

	public List<Condition> visit(OrCondition c, BeffyRuleOptimizerDataPassTwo data) {
		List<Condition> list = new LinkedList<Condition>();
		for (Condition condition : c.getNestedConditions()) {
			list.addAll(condition.acceptVisitor(this, data));
		}
		return list;
	}

	public List<Condition> visit(TestCondition c, BeffyRuleOptimizerDataPassTwo data) {
		List<Condition> list = new ArrayList<Condition>();
		list.add(c);
		return list;
	}
	
}
