/**
 * 
 */
package org.jamocha.engine.rules.rulecompiler.beffy;

import org.jamocha.rules.AndCondition;
import org.jamocha.rules.AndConnectedConstraint;
import org.jamocha.rules.BoundConstraint;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionWithNested;
import org.jamocha.rules.ExistsCondition;
import org.jamocha.rules.LHSVisitor;
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
 * 
 * @author Josef Hahn
 * @author Karl-Heinz Krempels
 * @author Janno von Stülpnagel
 * @author Christoph Terwelp
 */
class BeffyRuleOptimizer implements LHSVisitor<BeffyRuleOptimizerData> {
	
	private void insertNots(int count, Condition c) {
		if (count == 0) return;
		if (count > 1) count = count % 2;
		
		ConditionWithNested parent = c.getParentCondition();
		ConditionWithNested notCon = new NotExistsCondition();
		notCon.addNestedCondition(c);
		if (count == 0) {
			ConditionWithNested n2 = new NotExistsCondition();
			n2.addNestedCondition(notCon);
			notCon = n2;
		}
		parent.replaceNestedCondition(c, notCon);
	}

	/**
	 * Convert AndCondition to OrCondition if the Condition is nested in an odd number of NotExistsConditions.
	 * (De Morgan)
	 */
	public BeffyRuleOptimizerData visit(AndCondition c,
			BeffyRuleOptimizerData data) {
		ConditionWithNested con = c;
		if (data.nestedNots % 2 == 1) {
			ConditionWithNested parent = c.getParentCondition();
			con = new OrCondition();
			for (Condition ce : c.getNestedConditions()) {
				con.addNestedCondition(ce);
			}
			parent.replaceNestedCondition(c, con);
		}
		for (Condition ce : con.getNestedConditions()) {
			ce.acceptVisitor(this, data);
		}
		return null;
	}

	public BeffyRuleOptimizerData visit(AndConnectedConstraint c,
			BeffyRuleOptimizerData data) {
		// TODO Auto-generated method stub
		return null;
	}

	public BeffyRuleOptimizerData visit(BoundConstraint c,
			BeffyRuleOptimizerData data) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Transform exists(CE) to equivalent not(not(CE)).
	 * Nots are only counted and not inserted.
	 */
	public BeffyRuleOptimizerData visit(ExistsCondition c,
			BeffyRuleOptimizerData data) {
		ConditionWithNested parent = c.getParentCondition();
		AndCondition andcon = new AndCondition();
		for (Condition ce : c.getNestedConditions()) {
			andcon.addNestedCondition(ce);
		}
		parent.replaceNestedCondition(c, andcon);
		data.nestedNots += 2;
		andcon.acceptVisitor(this, data);
		data.nestedNots -= 2;
		return null;
	}

	public BeffyRuleOptimizerData visit(LiteralConstraint c,
			BeffyRuleOptimizerData data) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Replace not(CE1, CE2, ...) by and(CE1, CE2, ...) and increase the not counter.
	 */
	public BeffyRuleOptimizerData visit(NotExistsCondition c,
			BeffyRuleOptimizerData data) {
		ConditionWithNested parent = c.getParentCondition();
		AndCondition andcon = new AndCondition();
		for (Condition ce : c.getNestedConditions()) {
			andcon.addNestedCondition(ce);
		}
		parent.replaceNestedCondition(c, andcon);
		data.nestedNots++;
		andcon.acceptVisitor(this, data);
		data.nestedNots--;
		return null;
	}

	/**
	 * Insert nots before ObjectCondition because they are leafs.
	 */
	public BeffyRuleOptimizerData visit(ObjectCondition c,
			BeffyRuleOptimizerData data) {
		insertNots(data.nestedNots, c);
		return null;
	}

	/**
	 * Convert OrCondition to AndCondition if the Condition is nested in an odd number of NotExistsConditions.
	 * (De Morgan)
	 */
	public BeffyRuleOptimizerData visit(OrCondition c,
			BeffyRuleOptimizerData data) {
		ConditionWithNested con = c;
		if (data.nestedNots % 2 == 1) {
			ConditionWithNested parent = c.getParentCondition();
			con = new AndCondition();
			for (Condition ce : c.getNestedConditions()) {
				con.addNestedCondition(ce);
			}
			parent.replaceNestedCondition(c, con);
		}
		for (Condition ce : con.getNestedConditions()) {
			ce.acceptVisitor(this, data);
		}
		return null;
	}

	public BeffyRuleOptimizerData visit(OrConnectedConstraint c,
			BeffyRuleOptimizerData data) {
		// TODO Auto-generated method stub
		return null;
	}

	public BeffyRuleOptimizerData visit(OrderedFactConstraint c,
			BeffyRuleOptimizerData data) {
		// TODO Auto-generated method stub
		return null;
	}

	public BeffyRuleOptimizerData visit(PredicateConstraint c,
			BeffyRuleOptimizerData data) {
		// TODO Auto-generated method stub
		return null;
	}

	public BeffyRuleOptimizerData visit(ReturnValueConstraint c,
			BeffyRuleOptimizerData data) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Insert nots before TestCondition because they are leafs.
	 */
	public BeffyRuleOptimizerData visit(TestCondition c,
			BeffyRuleOptimizerData data) {
		insertNots(data.nestedNots, c);
		return null;
	}

}
