package org.jamocha.rete.rulecompiler.hokifisch;

import org.jamocha.parser.RuleException;
import org.jamocha.rete.CompilerListener;
import org.jamocha.rete.Rete;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.Template;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.nodes.ObjectTypeNode;
import org.jamocha.rete.nodes.ReteNet;
import org.jamocha.rete.nodes.RootNode;
import org.jamocha.rule.Rule;

/**
 * This is the first rule compiler, we write from scratch. Wish us luck ;)
 * @author Josef Hahn
 *
 */
public class HokifischRuleCompiler implements RuleCompiler {

	public HokifischRuleCompiler(Rete engine, RootNode root, ReteNet net) {
		
	}
	
	public void addListener(CompilerListener listener) {
		// TODO Auto-generated method stub

	}

	public void addObjectTypeNode(Template template) {
		// TODO Auto-generated method stub

	}

	public boolean addRule(Rule rule) throws AssertException, RuleException {
		// TODO Auto-generated method stub
		return false;
	}

	public ObjectTypeNode getObjectTypeNode(Template template) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getValidateRule() {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(CompilerListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeObjectTypeNode(ObjectTypeNode node)
			throws RetractException {
		// TODO Auto-generated method stub

	}

	public void setValidateRule(boolean validate) {
		// TODO Auto-generated method stub

	}

}
