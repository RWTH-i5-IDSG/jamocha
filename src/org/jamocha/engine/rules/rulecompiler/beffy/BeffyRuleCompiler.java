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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.Constants;
import org.jamocha.communication.events.CompileEvent;
import org.jamocha.communication.events.CompilerListener;
import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.Binding;
import org.jamocha.engine.BoundParam;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.RuleCompiler;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.nodes.AlphaQuantorDistinctionNode;
import org.jamocha.engine.nodes.LeftInputAdaptorNode;
import org.jamocha.engine.nodes.MultiBetaJoinNode;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.SimpleBetaFilterNode;
import org.jamocha.engine.nodes.SlotFilterNode;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.rules.rulecompiler.CompileRuleException;
import org.jamocha.engine.util.MutableInteger;
import org.jamocha.engine.workingmemory.elements.Slot;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.AndCondition;
import org.jamocha.rules.BoundConstraint;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionVisitor;
import org.jamocha.rules.Constraint;
import org.jamocha.rules.ExistsCondition;
import org.jamocha.rules.LiteralConstraint;
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
				otn = new ObjectTypeNode(engine, template);
				rootNode.addChild(otn);
				typeNodes.put(template, otn);
			}
			return otn;
		}
		
	}
	
	private class BindingManager {

		private class BindingInformation{
			Binding b;
			int level;
			
			public BindingInformation(Binding b, int level) {
				this.b=b;
				this.level=level;
			}
			
		}
		
		private Map< Rule, Map<String,BindingInformation> >  rule2bindings;
		
		private Map<String,BindingInformation> getBindings(Rule r) {
			Map<String,BindingInformation> bindings = rule2bindings.get(r);
			if (bindings == null) {
				bindings = new HashMap<String, BindingInformation>();
				rule2bindings.put(r, bindings);
			}
			return bindings;
		}
		
		private BindingInformation getBindingInformation(Rule r, String varName) {
			return getBindings(r).get(varName);
		}
		
		public Binding getBinding(Rule r, String varName) {
			return getBindingInformation(r,varName).b;
		}
		
		private void putBinding(Rule r, String varName, BindingInformation binding) {
			getBindings(r).put(varName, binding);
		}
		
		public BindingManager() {
			rule2bindings = new HashMap<Rule, Map<String,BindingInformation>>();			
		}

		private void boundConstraintOccurs(BoundConstraint bc, CompileTableau data) {
			// we don't need negated occurences, since we only use them as
			// value provider in the action part
			if (bc.isNegated()) return;
			
			// we don't need occurences e.g. in test-conditions and so on
			if (!(bc.getParentCondition() instanceof ObjectCondition)) return;
			
			// some convenience variables
			Rule r = data.rule;
			ObjectCondition ocond = (ObjectCondition) ( bc.getParentCondition());
			String varName = bc.getConstraintName();
			
			// compute the scope-level of this occurence
			int level = 0;
			{
				Condition ccond = bc.getParentCondition();
				while (ccond != null) {
					level++;
					ccond = ccond.getParentCondition();
				}
			}
			
			// if our occurence is deeper inside than the existing occurence,
			// throw the new one away,
			BindingInformation bin = bindings.getBindingInformation(r, varName);
			if (bin!=null && bin.level<level) return;
			
			// get the tuple index from our new occurence
			// (its a mutable integer, because at this moment, the concrete
			// position inside the tuple is unknown)
			MutableInteger tupleIdx = data.getTupleIndexFromCondition(bc.getParentCondition());
			
			// determine the slot index (-1 for whole-fact binding)
			int slotIdx = -1;
			if (!bc.isFactBinding()) {
				Template t = engine.findTemplate(ocond.getTemplateName());
				Slot s = t.getSlot(bc.getSlotName());
				slotIdx = s.getId();
			}

			// generate new binding and its binding-information record
			Binding b = new Binding(tupleIdx, slotIdx);
			BindingInformation binf = new BindingInformation(b,level);
			
			// store it
			putBinding(r,varName, binf);
			
			
		}
		
		
	}
	
	private class CompileTableau {
		
		private Rule rule;
		
		private boolean success;
		
		private Map<Condition, Node> lastNodes;
		
		private Set<Constraint> handled;
		
		private Map<Condition, MutableInteger> condition2TupleIdx;
		
		private Map<Condition,Node> correspondingJoins;
		
		public CompileTableau(Rule r) {
			this.rule = r;
			this.success = true;
			this.lastNodes = new HashMap<Condition, Node>();
			this.handled = new HashSet<Constraint>();
			this.correspondingJoins = new HashMap<Condition, Node>();
			this.condition2TupleIdx = new HashMap<Condition, MutableInteger>();
		}
		
		public void setCorrespondingJoin(Condition c, Node join) {
			correspondingJoins.put(c,join);
		}
		
		public Node getCorrespondingJoin(Condition c) {
			return correspondingJoins.get(c);
		}

		public MutableInteger getTupleIndexFromCondition(Condition condition) {
			MutableInteger val = condition2TupleIdx.get(condition);
			if (val == null) {
				val = new MutableInteger(0);
				condition2TupleIdx.put(condition, val);
			}
			return val;
		}

		public void markAsHandled(Constraint c) {
			handled.add(c);
		}
		
		public boolean isMarkedAsHandled(Constraint c) {
			return handled.contains(c);
		}
		
		public boolean hadSuccess() {
			return success;
		}

		public void setSuccess(boolean v) {
			success = v;
		}
		
		public Rule getRule() {
			return rule;
		}
		
		public Node getLastNode(Condition c) {
			return lastNodes.get(c);
		}

		public void setLastNode(Condition c, Node node) {
			lastNodes.put(c, node);
		}

	}
	
	private class BeffyRuleConditionVisitor implements ConditionVisitor<CompileTableau, CompileTableau> {

		private boolean isAlpha(Condition c) {
			return (c instanceof ObjectCondition || c instanceof ExistsCondition || c instanceof TestCondition);
		}
		
		public CompileTableau visit(AndCondition c, CompileTableau data) {
			for (Condition subCondition : c.getNestedConditions()) {
				subCondition.acceptVisitor(this, data);
			}
			
			if (c.getNestedConditions().size() == 1) {
				// i think, that should not happen?!
				//TODO check and discuss that
				logAndFail(new NodeException("only one subcondition in AND", null), data);
			} else if (c.getNestedConditions().size() == 2 &&
							(isAlpha(c.getNestedConditions().get(0)) || isAlpha(c.getNestedConditions().get(1)))		
							) {
				/* this is the classic case with just 2 subconditions and at least
				 * one alpha subcondition
				 */
				SimpleBetaFilterNode joinNode = new SimpleBetaFilterNode(engine);
				try {
					Condition c1 = c.getNestedConditions().get(0);
					Condition c2 = c.getNestedConditions().get(1);
					LeftInputAdaptorNode lia = new LeftInputAdaptorNode(engine);
					Condition alphaCond = (isAlpha(c1)) ? c1 : c2;
					Node n = data.getLastNode(alphaCond);
					n.addChild(lia);
					data.setLastNode(alphaCond, lia);
					data.getLastNode(c1).addChild(joinNode);
					data.getLastNode(c2).addChild(joinNode);
					// fix the tuple indices for c1 and c2
					MutableInteger oldIdxC1 = data.getTupleIndexFromCondition(c1);
					MutableInteger oldIdxC2 = data.getTupleIndexFromCondition(c2);
					oldIdxC2.set(oldIdxC1.get() + 1);
					data.setCorrespondingJoin(alphaCond, joinNode);
				} catch (NodeException e) {
					logAndFail(e, data);
				}
				data.setLastNode(c, joinNode);
			} else {
				// we need a fat-join node
				MultiBetaJoinNode joinNode = new MultiBetaJoinNode(engine);
				for (Condition sub : c.getNestedConditions()) {
					Node n = data.getLastNode(sub);
					try {
						n.addChild(joinNode);
					} catch (NodeException e) {
						logAndFail(e,data);
					}
					if (isAlpha(sub)) data.setCorrespondingJoin(sub, joinNode);
				}
				data.setLastNode(c, joinNode);
			}
			return data;
		}

		public CompileTableau visit(ExistsCondition c, CompileTableau data) {
			assert c.getNestedConditions().size() == 1;
			Condition nested = c.getNestedConditions().get(0);
			nested.acceptVisitor(this, data);
			Node lastNode = data.getLastNode(nested);
			//TODO discuss, how the stuff must go on here
			List<Integer> distinctionSlots = new ArrayList<Integer>();
			
			AlphaQuantorDistinctionNode quantorNode = new AlphaQuantorDistinctionNode(engine, distinctionSlots);
			try {
				lastNode.addChild(quantorNode);
			} catch (NodeException e) {
				logAndFail(e, data);
			}
			data.setLastNode(c, quantorNode);
			return data;
		}

		public CompileTableau visit(NotExistsCondition c, CompileTableau data) {
			// TODO Auto-generated method stub
			return data;
		}

		public CompileTableau visit(ObjectCondition c, CompileTableau data) {
			//ObjectConditions are alpha-networks and begin at the root node
			Node lastNode = null;
			
			//at first, we need the ObjectTypeNode
			Template template = engine.findTemplate(c.getTemplateName());
			try {
				lastNode = objectTypeNodes.getObjectTypeNode(template);
				
				/* iterate over all constraints and mark each constraint, we 
				 * can handle here */
				for (Constraint constr : c.getFlatConstraints()) {
					if (constr instanceof LiteralConstraint) {
						LiteralConstraint lc = (LiteralConstraint) constr;
						int operator = lc.isNegated() ? Constants.NOTEQUAL : Constants.EQUAL;
						Slot slot = template.getSlot(lc.getSlotName()).createSlot(engine);
						slot.setValue(lc.getValue());
						SlotFilterNode filterNode = new SlotFilterNode(engine, operator, slot);
						lastNode.addChild(filterNode);
						lastNode = filterNode;
					} else if (constr instanceof BoundConstraint) {
						BoundConstraint bc = (BoundConstraint) constr;
						bindings.boundConstraintOccurs(bc, data);
						
					}
					data.markAsHandled(constr);
				}
			} catch (NodeException e) {
				logAndFail(e, data);
			} catch (EvaluationException e) {
				logAndFail(e, data);
			}
			data.setLastNode(c, lastNode);
			return data;
		}

		public CompileTableau visit(OrCondition c, CompileTableau data) {
			/* this is our entry point. here we branch the rule into
			 * some new rules!
			 */
			for (Condition subCondition : c.getNestedConditions()) {
				/* for this sub-condition, we have to generate a new sub-rule,
				 * compile it and create a terminal node for it
				 */
				subCondition.acceptVisitor(this, data);
				TerminalNode terminal = new TerminalNode(engine, data.getRule());
				
				if (data.getRule().getAutoFocus()) {
					terminal.autoFocus();
				}
				
				Node last = data.getLastNode(subCondition);
				try {
					last.addChild(terminal);
					rootNode.activate();
				} catch (NodeException e) {
					logAndFail(e,data);
				}			
			}
			return data;
		}

		public CompileTableau visit(TestCondition c, CompileTableau data) {
			try {
				data.setLastNode(c, objectTypeNodes.getObjectTypeNode(engine.getInitialTemplate()));
				/*
				 * we can't really handle the test here, because the needed
				 * FunctionEvaluator must be added to the corresponding join-
				 * node which doesn't exist here!
				 */
			} catch (Exception e) {
				logAndFail(e,data);
			}
			return data;
		}
		
	}
	
	private ObjectTypeNodeManager objectTypeNodes;
	
	private BindingManager bindings;

	private List<CompilerListener> listeners;
	
	private Engine engine;
	
	private RootNode rootNode;
	
	private ReteNet reteNet;
	
	private BeffyRuleOptimizer ruleOptimizer;
	
	private List<Parameter> substituteBoundParamsForFunctionCall(Parameter[] original, Rule rule) {
		List<Parameter> result = new ArrayList<Parameter>();
		for (Parameter p : original) {
			if (p instanceof BoundParam) {
				BoundParam bp = (BoundParam) p;
				Binding bnd = bindings.getBinding(rule, bp.getVariableName());
				System.out.println(bp.getVariableName()+" "+bnd);
			} else if (p instanceof JamochaValue) {
				result.add(p);
			} else if (p instanceof Signature) {
				Signature orig = (Signature)p;
				Signature newSig = new Signature(orig.getSignatureName());
				newSig.setParameters(substituteBoundParamsForFunctionCall(orig.getParameters(),rule));
				result.add(newSig);
			} else {
				logAndFail(new CompileRuleException("parameter substitution for this type is not implemented: "+p.getClass().getCanonicalName()), null);
			}
			
		}
		return result;
	}
	
	public BeffyRuleCompiler(Engine engine, RootNode root, ReteNet net) {
		this.engine=engine;
		this.rootNode=root;
		this.reteNet=net;
		this.listeners=new LinkedList<CompilerListener>();
		this.objectTypeNodes = new ObjectTypeNodeManager();
		this.bindings = new BindingManager();
		this.ruleOptimizer = new BeffyRuleOptimizer();
	}

	private void logAndFail(Exception e, CompileTableau data) {
		data.setSuccess(false);
		Logging.logger(this.getClass()).warn(e);
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
		
		// at the very beginning, we have to pipe our rule through the optimizer
		Condition optimizedCondition;
		try {
			optimizedCondition = ruleOptimizer.optimize(rule.getConditions());
		} catch (OptimizeRuleException e1) {
			throw new CompileRuleException(e1);
		}
		rule.getConditions().clear();
		rule.getConditions().add(optimizedCondition);
		
		CompileTableau ruleCompileTableau = new CompileTableau(rule);
		
		BeffyRuleConditionVisitor visitor = new BeffyRuleConditionVisitor();
		
		// this is our one-and-only or-condition at the root
		OrCondition rootCondition = (OrCondition) rule.getConditions().get(0);
		
		rootCondition.acceptVisitor(visitor, ruleCompileTableau);
		
		if (ruleCompileTableau.hadSuccess()) {
			try {
				rootNode.activate();
			} catch (NodeException e) {
				reteNet.cleanup();
				throw new CompileRuleException(e);
			}
			notifyListeners(rule);
			rule.parentModule().addRule(rule);
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
