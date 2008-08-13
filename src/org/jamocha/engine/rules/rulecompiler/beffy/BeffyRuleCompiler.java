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
package org.jamocha.engine.rules.rulecompiler.beffy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jamocha.communication.events.CompileEvent;
import org.jamocha.communication.events.CompilerListener;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.Binding;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.RuleCompiler;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.rules.rulecompiler.CompileRuleException;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.AndCondition;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionVisitor;
import org.jamocha.rules.ExistsCondition;
import org.jamocha.rules.NotExistsCondition;
import org.jamocha.rules.ObjectCondition;
import org.jamocha.rules.OrCondition;
import org.jamocha.rules.Rule;
import org.jamocha.rules.TestCondition;

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
 * As you can see at many places in the code: This rule compiler is 
 * _NOT_ optimized for memory-usage, for the speed of rule-compiling
 * or the size of the resulting rete network. At many places,
 * my code makes redundant things. In exchange, the code is more readable,
 * more understandable and more powerful than the old one, since with 
 * efficiency in mind, it wouldn't be possible for me to implement all
 * the features :-p
 * 
 */

/* TODO:
 * maaany other basic things
 * Or-Connected-Constraints
 * Return-Value-Constraints
 * Not-Exists-CE
 * Exists-CE
 * Or-Conditions
 * And-Conditions (where?)
 */


/*
 * 
 * we assume the following structure after the rule optimizer:
 * 
 *                         OR
 *                       /  |  \       ...
 *                     /    |    \         ...
 *                   /      |      \           ...
 *                EXISTS  ALPHA   AND
 *                  |            /   \
 *                  |          /       \
 *                ALPHA      /           \
 *                         AND           AND
 *                        /   \        /  |  \
 *                     ALPHA ALPHA   /    |    \
 *                                 /      |      \
 *                              ALPHA   ALPHA   ALPHA
 *                              
 * 
 * - One Big OR, which may contain
 *     - ALPHA-Networks which must not contain any TestConditions or NotExistsConditions
 *     - ANDs with two or more nested conditions.
 * - AND have only two nested conditions if possible.
 * - AND nodes with more than two nested conditions are only used if required by an
 *   TestCondition or NotExistsCondition.
 * - Variables used in TestConditions or NotExistsConditions are bound in the first AND.
 */

public class BeffyRuleCompiler implements RuleCompiler {
	
	private class ObjectTypeNodeManager {
		
		Map<Template, ObjectTypeNode> typeNodes;
		
		public ObjectTypeNodeManager() {
			typeNodes = new HashMap<Template,ObjectTypeNode>();
		}
		
		public ObjectTypeNode getObjectTypeNode(Template template) throws NodeException {
			ObjectTypeNode otn = typeNodes.get(template);
			if (otn == null) {
				otn = new ObjectTypeNode(reteNet.nextNodeId(), engine.getWorkingMemory(), reteNet, template);
				rootNode.addChild(otn);
				typeNodes.put(template, otn);
			}
			return otn;
		}
		
	}
	
	private class BindingManager {
		
		private Map< Rule, Map<String,Binding> >  rule2bindings;
		
		private Map<String,Binding> getBindings(Rule r) {
			Map<String,Binding> bindings = rule2bindings.get(r);
			if (bindings == null) {
				bindings = new HashMap<String, Binding>();
				rule2bindings.put(r, bindings);
			}
			return bindings;
		}
		
		public Binding getBinding(Rule r, String varName) {
			return getBindings(r).get(varName);
		}
		
		public void putBinding(Rule r, String varName, Binding binding) {
			getBindings(r).put(varName, binding);
		}
		
		public BindingManager() {
			rule2bindings = new HashMap<Rule, Map<String,Binding>>();			
		}
		
		
	}
	
	private class CompileTableau {
		
		private Rule rule;
		
		public CompileTableau(Rule r) {
			this.rule = r;
		}

		public boolean hadSuccess() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	private class BeffyRuleConditionVisitor implements ConditionVisitor<CompileTableau, CompileTableau> {

		public CompileTableau visit(AndCondition c, CompileTableau data) {
			// TODO Auto-generated method stub
			return null;
		}

		public CompileTableau visit(ExistsCondition c, CompileTableau data) {
			// TODO Auto-generated method stub
			return null;
		}

		public CompileTableau visit(NotExistsCondition c, CompileTableau data) {
			// TODO Auto-generated method stub
			return null;
		}

		public CompileTableau visit(ObjectCondition c, CompileTableau data) {
			// TODO Auto-generated method stub
			return null;
		}

		public CompileTableau visit(OrCondition c, CompileTableau data) {
			/* this is our entry point. here we branch the rule into
			 * some new rules.
			 */
			for (Condition subCondition : c.getNestedConditions()) {
				/* for this sub-condition, we have to generate a new sub-rule,
				 * compile it and create a terminal node for it
				 */
				
				
			}
			return data;
		}

		public CompileTableau visit(TestCondition c, CompileTableau data) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private ObjectTypeNodeManager objectTypeNodes;
	
	private BindingManager bindings;

	private List<CompilerListener> listeners;
	
	private Engine engine;
	
	private RootNode rootNode;
	
	private ReteNet reteNet;
	
	public BeffyRuleCompiler(Engine engine, RootNode root, ReteNet net) {
		this.engine=engine;
		this.rootNode=root;
		this.reteNet=net;
		this.listeners=new LinkedList<CompilerListener>();
		this.objectTypeNodes = new ObjectTypeNodeManager();
		this.bindings = new BindingManager();
	}

	public void addListener(CompilerListener listener) {
			listeners.add(listener);
	}

	public void addObjectTypeNode(Template template) {
		/*
		 * we will do exactly nothing here, because we will
		 * create object type nodes on the fly when we need
		 * them in a condition.
		 */	

	}
	
	public boolean addRule(Rule rule) throws AssertException, RuleException, EvaluationException, CompileRuleException {
		CompileTableau ruleCompileTableau = new CompileTableau(rule);
		
		//HERE WE SHOULD START PROCESSING - MAYBE THE HATED VISITOR PATTERN
		
		BeffyRuleConditionVisitor visitor = new BeffyRuleConditionVisitor();
		
		// this is our one-and-only or-condition at the root
		OrCondition rootCondition = (OrCondition) rule.getConditions().get(0);
		
		visitor.visit(rootCondition, ruleCompileTableau);
		
		
		
		if (ruleCompileTableau.hadSuccess()) {
			notifyListeners(rule);
			return true;
		} else {
			reteNet.cleanup();
			return false;
		}
	}

	public Binding getBinding(String varName, Rule r) {
		return bindings.getBinding(r, varName);
	}

	public void removeListener(CompilerListener listener) {
			listeners.remove(listener);
	}

	private void notifyListeners(Rule newRule) {
		CompileEvent event = new CompileEvent(this, CompileEvent.CompileEventType.RULE_ADDED);
		event.setRule(newRule);
		notifyListeners(event);
	}
	
	private void notifyListeners(CompileEvent ev) {
		for (CompilerListener li : listeners) li.compileEventOccured(ev);
	}

}
