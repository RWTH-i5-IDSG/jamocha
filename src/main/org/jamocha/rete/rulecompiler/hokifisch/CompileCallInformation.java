package org.jamocha.rete.rulecompiler.hokifisch;

import java.util.HashMap;
import java.util.Map;

import org.jamocha.rete.nodes.Node;
import org.jamocha.rete.nodes.TerminalNode;
import org.jamocha.rete.nodes.TwoInputNode;
import org.jamocha.rete.wme.Template;
import org.jamocha.rete.wme.TemplateSlot;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.Rule;

/**
 * @author Josef Alexander Hahn
 * This class holds all information, which is
 * needed over method-borders. its a casual compound of some hashtables
 * and something like that... furthermore, all rete-stuff, which was
 * stored in the rule before (like terminalnode and so on) should now be
 * stored here!
 */
public class CompileCallInformation {

	Rule rule;

	TerminalNode terminalNode = null;

	// holds a rete subnet for each condition
	Map<Condition, ReteSubnet> conditionSubnets;

	// holds a rete subnet for each constraint
	Map<Constraint, ReteSubnet> constraintSubnets;

	// holds the conditions, which contains a given constraint
	Map<Constraint, Condition> constraint2condition;

	// holds the template for each condition
	Map<Condition, Template> condition2template;

	// holds the slot for each constraints
	Map<Constraint, TemplateSlot> constraint2templateSlot;
	
	Node lastJoin;

	Map<Condition, TwoInputNode> condition2join;

	public CompileCallInformation(Rule rule) {
		this.rule = rule;
		conditionSubnets = new HashMap<Condition, ReteSubnet>();
		constraintSubnets = new HashMap<Constraint, ReteSubnet>();
		constraint2condition = new HashMap<Constraint, Condition>();
		condition2template = new HashMap<Condition, Template>();
		constraint2templateSlot = new HashMap<Constraint, TemplateSlot>();
		condition2join = new HashMap<Condition, TwoInputNode>();
	}

	public Template getTemplate(Constraint constr) {
		Template tpl = null;
		Condition cond = this.constraint2condition.get(constr);
		if (cond != null)
			tpl = getTemplate(cond);
		return tpl;
	}
	
	public Template getTemplate(Condition cond){
		return condition2template.get(cond);		
	}

}
