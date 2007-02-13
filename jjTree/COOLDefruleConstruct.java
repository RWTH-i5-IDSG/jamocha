/* Generated By:JJTree: Do not edit this line. COOLDefruleConstruct.java */

import org.jamocha.rule.*;
import org.jamocha.rete.*;
import org.jamocha.parser.*;

public class COOLDefruleConstruct extends ConstructNode {

	private Defrule rule;
	private boolean focus=false;
	private Node expr;
	private ActionTree act;
	
	public Defrule getRule() { return rule; };
	
	public COOLDefruleConstruct(int id) {
		super(id);
		rule=new Defrule();
		act=new ActionTree();
		rule.addAction(act);
	}

	public COOLDefruleConstruct(COOL p, int id) {
		super(p, id);
		rule=new Defrule();
		act=new ActionTree();
		rule.addAction(act);
	}
	
	public void setDocString(String n) 
	{ 
		doc = n;
		rule.setComment(n);
	}

	public void setName(String n) 
	{
		name = n; 
		rule.setName(n);
	}

	public String toString() {
		return "defrule \"" + name + "\"" + "(" + doc + ")";
	}

	public void setAutoFocus(boolean f)
	{	focus=f; }
	
	public void setSalienceExpression(Node n)
	{	expr=n; }

	public void setRuleActions(Node n)
	{
		act.setActions(n);
	};
	
	public void addCondition(Condition ce)
	{ rule.addCondition(ce); }
	
	public JamochaValue execute() throws EvaluationException
	{
		int i;
		for (i=0;i<jjtGetNumChildren();i++)
			jjtGetChild(i).execute();	// Initialize Test CEs and such
		if (parser.addRule(rule)) return JamochaValue.TRUE;
		else return JamochaValue.FALSE;
	};
}
