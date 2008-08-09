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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jamocha.Constants;
import org.jamocha.communication.events.CompileEvent;
import org.jamocha.communication.events.CompilerListener;
import org.jamocha.communication.logging.Logging;
import org.jamocha.communication.logging.Logging.JamochaLogger;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.Binding;
import org.jamocha.engine.BoundParam;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.RuleCompiler;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.functions.FunctionNotFoundException;
import org.jamocha.engine.nodes.AbstractBetaFilterNode;
import org.jamocha.engine.nodes.AlphaSlotComparatorNode;
import org.jamocha.engine.nodes.LeftInputAdaptorNode;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.RightInputAdaptorNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.SimpleBetaFilterNode;
import org.jamocha.engine.nodes.SlotFilterNode;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.nodes.joinfilter.LeftRightFieldComparator;
import org.jamocha.engine.nodes.joinfilter.FunctionEvaluator;
import org.jamocha.engine.nodes.joinfilter.JoinFilterException;
import org.jamocha.engine.nodes.joinfilter.LeftFieldAddress;
import org.jamocha.engine.nodes.joinfilter.RightFieldAddress;
import org.jamocha.engine.rules.rulecompiler.CompileRuleException;
import org.jamocha.engine.workingmemory.elements.Slot;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.AndCondition;
import org.jamocha.rules.AndConnectedConstraint;
import org.jamocha.rules.BoundConstraint;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionWithNested;
import org.jamocha.rules.Constraint;
import org.jamocha.rules.Defrule;
import org.jamocha.rules.ExistsCondition;
import org.jamocha.rules.LiteralConstraint;
import org.jamocha.rules.NotExistsCondition;
import org.jamocha.rules.ObjectCondition;
import org.jamocha.rules.OrConnectedConstraint;
import org.jamocha.rules.PredicateConstraint;
import org.jamocha.rules.ReturnValueConstraint;
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
 * - One Big OR, which can contain EXISTS, ALPHA-Networks and ANDs
 * - An EXIST can containt just ONE ALPHA-Network
 * - An AND joins a number of childs (can be EXISTS, ALPHA-Networks and ANDs),
 *   whose condition's bindings/variables are known in this node, so the rule
 *   compiler can apply the evaluation in this node.
 */

public class BeffyRuleCompiler implements RuleCompiler {
	
	private class CompileTableau {
		
		private Rule rule;
		
		public CompileTableau(Rule r) {
			this.rule = r;
		}
		
	}
	

	private List<CompilerListener> listeners;
	
	private Engine engine;
	
	private RootNode rootNode;
	
	private ReteNet reteNet;
	
	public BeffyRuleCompiler(Engine engine, RootNode root, ReteNet net) {
		this.engine=engine;
		this.rootNode=root;
		this.reteNet=net;
		this.listeners=new LinkedList<CompilerListener>();
	}

	public void addListener(CompilerListener listener) {
			listeners.add(listener);
	}

	public void addObjectTypeNode(Template template) {
		// TODO Auto-generated method stub
		
	}

	public boolean addRule(Rule rule) throws AssertException, RuleException, EvaluationException, CompileRuleException {
		boolean success = (true | false); //TODO laaaater on, this nonsense must be removed
		CompileTableau tableau = new CompileTableau(rule);
		
		
		if (success) {
			notifyListeners(rule);
			return true;
		} else {
			reteNet.cleanup();
			return false;
		}
	}

	public Binding getBinding(String varName, Rule r) {
		// TODO Auto-generated method stub
		return null;
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
