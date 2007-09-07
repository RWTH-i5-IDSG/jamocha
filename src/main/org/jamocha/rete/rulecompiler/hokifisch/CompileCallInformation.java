package org.jamocha.rete.rulecompiler.hokifisch;

import java.util.HashMap;
import java.util.Map;

import org.jamocha.rete.nodes.TerminalNode;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Rule;

/**
 * @author Josef Alexander Hahn
 * This class holds all information, which is needed
 * over method-borders. its a casual compound of
 * some hashtables and something like that...
 * furthermore, all rete-stuff, which was stored in the
 * rule before (like terminalnode and so on) should
 * now be stored here!
 */
public class CompileCallInformation {

	Rule rule;
	TerminalNode terminalNode = null;
	
	Map<Condition,ReteSubnet> conditionSubnets;
	
	public CompileCallInformation(Rule rule) {
		this.rule = rule;
		conditionSubnets = new HashMap<Condition,ReteSubnet>();
	}
	
}
