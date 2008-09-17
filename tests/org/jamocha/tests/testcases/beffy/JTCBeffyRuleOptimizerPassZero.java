package org.jamocha.tests.testcases.beffy;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.jamocha.engine.Parameter;
import org.jamocha.engine.rules.rulecompiler.beffy.BeffyRuleOptimizerPassZero;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;
import org.jamocha.rules.AndCondition;
import org.jamocha.rules.AndConnectedConstraint;
import org.jamocha.rules.BoundConstraint;
import org.jamocha.rules.Condition;
import org.jamocha.rules.Constraint;
import org.jamocha.rules.ObjectCondition;
import org.jamocha.rules.OrCondition;
import org.jamocha.rules.OrConnectedConstraint;
import org.jamocha.rules.PredicateConstraint;

public class JTCBeffyRuleOptimizerPassZero extends TestCase {

	public void test() {
		
		/*
		 * (wurst (name ?foo|?bar|?baz ) )            		c1
		 * (or												c2
		 * 		(and										c3
		 * 			(wurst (name ?foo&:(lt 1 2)|?bar) )		c4
		 * 			(salat)									c5
		 * 		)
		 * 		(bier (name ?a & ?b | ?c & ?d) )			c6
		 * )
		 * 
		 */
		
		List<Constraint> c1constr= new ArrayList<Constraint>();
		
		BoundConstraint r1 = new BoundConstraint("foo", false);
		BoundConstraint r2 = new BoundConstraint("bar", false);
		BoundConstraint r3 = new BoundConstraint("baz", false);
		
		OrConnectedConstraint l1 = new OrConnectedConstraint(r2,r3, false);
		OrConnectedConstraint or1 = new OrConnectedConstraint(l1,r1, false);
		
		c1constr.add(or1);
		
		ObjectCondition c1 = new ObjectCondition(c1constr, "wurst"); 
		
		
		List<Constraint> c4constr= new ArrayList<Constraint>();
		
		BoundConstraint f1 = new BoundConstraint("foo", false);
		BoundConstraint f2 = new BoundConstraint("bar", false);
		
		List<Parameter> params = new ArrayList<Parameter>();
		
		params.add(JamochaValue.newLong(1));
		params.add(JamochaValue.newLong(2));
		
		PredicateConstraint f5 = new PredicateConstraint("less", params);
		
		OrConnectedConstraint f4 = new OrConnectedConstraint(f5,f2, false);
		
		AndConnectedConstraint f3 = new AndConnectedConstraint(f1, f4, false);
		
		
		ObjectCondition c4 = new ObjectCondition(c4constr, "wurst");
		
		/*
		 * (wurst (name ?foo|?bar|?baz ) )            		c1X
		 * (or												c2
		 * 		(and										c3
		 * 			(wurst (name ?foo&:(lt 1 2)|?bar) )		c4X
		 * 			(salat)									c5
		 * 		)
		 * 		(bier (name ?a & ?b | ?c & ?d) )			c6
		 * )
		 * 
		 */
		
		ObjectCondition c5 = new ObjectCondition(Collections.EMPTY_LIST, "salat");
		
		List<Constraint> c6constr = new ArrayList<Constraint>();
		
		BoundConstraint j1 = new BoundConstraint("a", false);
		BoundConstraint j2 = new BoundConstraint("b", false);
		BoundConstraint j3 = new BoundConstraint("c", false);
		BoundConstraint j4 = new BoundConstraint("d", false);
		
		AndConnectedConstraint g1 = new AndConnectedConstraint(j1,j2,false);
		AndConnectedConstraint g2 = new AndConnectedConstraint(j3,j4,false);
		
		
		OrConnectedConstraint occ = new OrConnectedConstraint(g1,g2, false);
		
		c6constr.add(occ);
		
		ObjectCondition c6 = new ObjectCondition(c6constr, "bier");
		
		
		/*
		 * (wurst (name ?foo|?bar|?baz ) )            		c1X
		 * (or												c2
		 * 		(and										c3
		 * 			(wurst (name ?foo&:(lt 1 2)|?bar) )		c4X
		 * 			(salat)									c5X
		 * 		)
		 * 		(bier (name ?a & ?b | ?c & ?d) )			c6X
		 * )
		 * 
		 */
		
		AndCondition c3 = new AndCondition();
		c3.addNestedCondition(c4);
		c3.addNestedCondition(c5);
		
		OrCondition c2 = new OrCondition();
		c2.addNestedCondition(c3);
		c2.addNestedCondition(c6);
		
		List<Condition> forTest = new ArrayList<Condition>();
		
		forTest.add(c1);
		forTest.add(c2);
	
		BeffyRuleOptimizerPassZero pass0 = new BeffyRuleOptimizerPassZero();
		
		Condition result = pass0.optimize(forTest);
		
		System.out.println(result.format(ParserFactory.getFormatter(true)));
		
		
	}
	
}
