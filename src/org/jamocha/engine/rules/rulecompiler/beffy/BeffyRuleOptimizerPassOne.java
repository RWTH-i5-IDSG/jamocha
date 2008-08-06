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

import org.jamocha.rules.AndCondition;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionWithNested;
import org.jamocha.rules.ExistsCondition;
import org.jamocha.rules.LHSVisitor;
import org.jamocha.rules.NotExistsCondition;
import org.jamocha.rules.ObjectCondition;
import org.jamocha.rules.OrCondition;
import org.jamocha.rules.TestCondition;

/**
 * 
 * @author Josef Hahn
 * @author Karl-Heinz Krempels
 * @author Janno von Stuelpnagel
 * @author Christoph Terwelp
 */
public class BeffyRuleOptimizerPassOne implements LHSVisitor<BeffyRuleOptimizerDataPassOne, Condition> {
	
	public Condition optimize(List<Condition> cons) {
		ConditionWithNested con = new AndCondition();
		for (Condition condition : cons)
			con.addNestedCondition(condition);
		Condition con2;
		con2 = con.acceptVisitor(this, new BeffyRuleOptimizerDataPassOne());
		return con2;
	}
	
	private void insertNots(int count, Condition c) {
		if (count == 0) return;
		if (count > 1) count = count % 2;
		
		ConditionWithNested parent = c.getParentCondition();
		ConditionWithNested n;
		if (count == 0)
			n = new ExistsCondition();
		else
			n = new NotExistsCondition();
		n.addNestedCondition(c);
		parent.replaceNestedCondition(c, n);
	}

	/**
	 * Convert AndCondition to OrCondition if the Condition is nested in an odd number of NotExistsConditions.
	 * (De Morgan)
	 */
	public Condition visit(AndCondition c,
			BeffyRuleOptimizerDataPassOne data) {
		ConditionWithNested con = c;
		if (data.nestedNots % 2 == 1) {
			ConditionWithNested parent = c.getParentCondition();
			con = new OrCondition();
			for (Condition ce : c.getNestedConditions()) {
				con.addNestedCondition(ce);
			}
			parent.replaceNestedCondition(c, con);
		}
		for (Condition ce : new ArrayList<Condition>(con.getNestedConditions())) {
			ce.acceptVisitor(this, data);
		}
		return c;
	}

	/**
	 * Transform exists(CE) to equivalent not(not(CE)).
	 * Nots are only counted and not inserted.
	 */
	public Condition visit(ExistsCondition c,
			BeffyRuleOptimizerDataPassOne data) {
		ConditionWithNested parent = c.getParentCondition();
		AndCondition andcon = new AndCondition();
		for (Condition ce : c.getNestedConditions()) {
			andcon.addNestedCondition(ce);
		}
		parent.replaceNestedCondition(c, andcon);
		data.nestedNots += 2;
		andcon.acceptVisitor(this, data);
		data.nestedNots -= 2;
		return c;
	}

	/**
	 * Replace not(CE1, CE2, ...) by and(CE1, CE2, ...) and increase the not counter.
	 */
	public Condition visit(NotExistsCondition c,
			BeffyRuleOptimizerDataPassOne data) {
		ConditionWithNested parent = c.getParentCondition();
		AndCondition andcon = new AndCondition();
		for (Condition ce : c.getNestedConditions()) {
			andcon.addNestedCondition(ce);
		}
		parent.replaceNestedCondition(c, andcon);
		data.nestedNots++;
		andcon.acceptVisitor(this, data);
		data.nestedNots--;
		return c;
	}

	/**
	 * Insert nots before ObjectCondition because they are leafs.
	 */
	public Condition visit(ObjectCondition c,
			BeffyRuleOptimizerDataPassOne data) {
		insertNots(data.nestedNots, c);
		return c;
	}

	/**
	 * Convert OrCondition to AndCondition if the Condition is nested in an odd number of NotExistsConditions.
	 * (De Morgan)
	 */
	public Condition visit(OrCondition c,
			BeffyRuleOptimizerDataPassOne data) {
		ConditionWithNested con = c;
		if (data.nestedNots % 2 == 1) {
			ConditionWithNested parent = c.getParentCondition();
			con = new AndCondition();
			for (Condition ce : c.getNestedConditions()) {
				con.addNestedCondition(ce);
			}
			parent.replaceNestedCondition(c, con);
		}
		for (Condition ce : new ArrayList<Condition>(con.getNestedConditions())) {
			ce.acceptVisitor(this, data);
		}
		return c;
	}

	/**
	 * Insert nots before TestCondition because they are leafs.
	 */
	public Condition visit(TestCondition c,
			BeffyRuleOptimizerDataPassOne data) {
		insertNots(data.nestedNots, c);
		return c;
	}

}
