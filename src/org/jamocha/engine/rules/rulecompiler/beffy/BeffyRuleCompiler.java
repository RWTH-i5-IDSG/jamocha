/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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
package org.jamocha.engine.rules.rulecompiler.beffy;

import org.jamocha.communication.events.CompilerListener;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.Binding;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.RetractException;
import org.jamocha.engine.RuleCompiler;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.Rule;


/**
 * @author Josef Alexander Hahn
 * We have the old SlimFast-RuleCompiler, which is buggy and not that
 * feature-rich. Furthermore, it sometimes compiles wrong rete-nets :(
 * 
 * So, we urgently need a successor... Due to the fact that there are
 * unsolved technical questions for writing a perfect rule compiler, the
 * plan is to write one, which handles its task as good as my knowledge
 * allows it. This will hopefully lead to this BeffyRuleCompiler (beffy
 * as nice abbreviation for "Best EFFort").
 * 
 */
public class BeffyRuleCompiler implements RuleCompiler {

	public BeffyRuleCompiler(Engine engine, RootNode root, ReteNet net) {
		// TODO Auto-generated constructor stub
	}

	public void addListener(CompilerListener listener) {
		// TODO Auto-generated method stub

	}

	public void addObjectTypeNode(Template template) {
		// TODO Auto-generated method stub

	}

	public boolean addRule(Rule rule) throws AssertException, RuleException,
			EvaluationException {
		// TODO Auto-generated method stub
		return false;
	}

	public Binding getBinding(String varName, Rule r) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectTypeNode getObjectTypeNode(Template template) {
		// TODO Auto-generated method stub
		return null;
	}

	public TerminalNode getTerminalNode(Rule rule) {
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
