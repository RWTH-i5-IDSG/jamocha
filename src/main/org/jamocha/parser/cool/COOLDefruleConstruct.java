/* Generated By:JJTree: Do not edit this line. COOLDefruleConstruct.java */
package org.jamocha.parser.cool;

import org.jamocha.rule.*;
import org.jamocha.rete.*;
import org.jamocha.parser.*;

public class COOLDefruleConstruct extends ConstructNode {

    private Defrule rule;

    private boolean focus = false;

    private Node expr;

    private ActionTree act;

    public Defrule getRule() {
	return rule;
    };

    public COOLDefruleConstruct(int id) {
	super(id);
	rule = new Defrule();
	act = new ActionTree();
	rule.addAction(act);
    }

    public COOLDefruleConstruct(COOLParser p, int id) {
	super(p, id);
	rule = new Defrule();
	act = new ActionTree();
	rule.addAction(act);
    }

    public void setDocString(String n) {
	doc = n;
	rule.setDescription(n);
    }

    public void setName(String n) {
	name = n;
	rule.setName(n);
    }

    public String toString() {
	return "defrule \"" + name + "\"" + "(" + doc + ")";
    }

    public void setAutoFocus(boolean f) {
	focus = f;
    }

    public void setSalienceExpression(Node n) {
	expr = n;
    }

    public void setRuleActions(Node n) {
	act.setActions(n);
    };

    public void addCondition(Condition ce) {
	rule.addCondition(ce);
    }

    public Parameter getExpression() {
	// TODO: not implemented
	return null;
	/*
         * for (int i = 0; i < jjtGetNumChildren(); i++)
         * jjtGetChild(i).getExpression(); // Initialize Test CEs and such if
         * (!engine.getCurrentFocus().containsRule(rule) &&
         * engine.getRuleCompiler().addRule(rule)) { return JamochaValue.TRUE; }
         * else return JamochaValue.FALSE;
         */
    };
}
