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
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.nodes.AbstractBetaFilterNode;
import org.jamocha.engine.nodes.AlphaQuantorDistinctionNode;
import org.jamocha.engine.nodes.AlphaSlotComparatorNode;
import org.jamocha.engine.nodes.LeftInputAdaptorNode;
import org.jamocha.engine.nodes.MultiBetaJoinNode;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.SimpleBetaFilterNode;
import org.jamocha.engine.nodes.SlotFilterNode;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.nodes.joinfilter.FieldAddress;
import org.jamocha.engine.nodes.joinfilter.GeneralizedFieldComparator;
import org.jamocha.engine.nodes.joinfilter.GeneralizedFunctionEvaluator;
import org.jamocha.engine.nodes.joinfilter.LeftFieldAddress;
import org.jamocha.engine.rules.rulecompiler.CompileRuleException;
import org.jamocha.engine.util.MutableInteger;
import org.jamocha.engine.workingmemory.elements.Slot;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.AndCondition;
import org.jamocha.rules.BoundConstraint;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionVisitor;
import org.jamocha.rules.Constraint;
import org.jamocha.rules.Defrule;
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
		
		/*
		 * in former times, the key for a binding is the binding
		 * variable name itself, e.g. "?x". but since we handle or-
		 * conditions this is not enough. a binding variable can have
		 * two different BindingInformations dependent on the or-child
		 * it contains.
		 */
		private class BindingKey {
			
			private String name;
			
			public String getName() {
				return name;
			}

			public TerminalNode getTnode() {
				return tnode;
			}

			private TerminalNode tnode;
			
			public BindingKey(String name, TerminalNode tnode) {
				this.name= name;
				this.tnode = tnode;
			}
			
			public boolean equals(Object other) {
				if (other == null) return false;
				if (other instanceof BindingKey) {
					BindingKey bo = (BindingKey) other;
					if (!bo.name.equals(name)) return false;
					if (tnode==bo.tnode) return true;
				}
				return false;
			}
			
			public int hashCode() {
				return name.hashCode() * tnode.getId();
			}
			
		}
		
		private Map< Rule, Map<BindingKey,BindingInformation> >  rule2bindings;
		
		private Map<BindingKey,BindingInformation> getBindings(Rule r) {
			Map<BindingKey,BindingInformation> bindings = rule2bindings.get(r);
			if (bindings == null) {
				bindings = new HashMap<BindingKey, BindingInformation>();
				rule2bindings.put(r, bindings);
			}
			return bindings;
		}
		
		private BindingInformation getBindingInformation(Rule r, String varName, TerminalNode focus) {
			return getBindings(r).get(new BindingKey(varName,focus));
		}
		
		public Binding getBinding(Rule r, String varName, TerminalNode focus) {
			BindingInformation bi = getBindingInformation(r,varName,focus);
			return (bi!=null) ? bi.b : null;
		}
		
		private void putBinding(Rule r, TerminalNode focus, String varName, BindingInformation binding) {
			getBindings(r).put(new BindingKey(varName,focus), binding);
		}
		
		public BindingManager() {
			rule2bindings = new HashMap<Rule, Map<BindingKey,BindingInformation>>();			
		}

		private void boundConstraintOccurs(BoundConstraint bc, TerminalNode focus, CompileTableau data) {
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
					/*
					 * we have weighted level increment, because
					 * e.g. an exists-condition in the chain must 
					 * leave to a level which is higher than all chains
					 * without an exists
					 */
					int weight=1;
					
					if (ccond instanceof ExistsCondition) weight=100;
					if (ccond instanceof NotExistsCondition) weight=100;
					
					level+=weight;
					ccond = ccond.getParentCondition();
				}
			}
			
			// if our occurence is deeper or as deep as inside than the existing
			// occurence, throw the new one away,
			BindingInformation bin = bindings.getBindingInformation(r, varName, focus);
			if (bin!=null && bin.level<=level) return;
			
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

			log("Potential pivot element for %s found at tuple=%d,slot=%d (tuple-index may vary later on!)",varName,tupleIdx.get(),slotIdx);
			
			// generate new binding and its binding-information record
			Binding b = new Binding(tupleIdx, slotIdx);
			BindingInformation binf = new BindingInformation(b,level);
			
			// store it
			putBinding(r,focus,varName, binf);
			
			
		}
		
		
	}
	
	private class CompileTableau {
		
		private Rule rule;
		
		private boolean success;
		
		private Map<Condition, Node> lastNodes;
		
		private Set<Constraint> handled;
		
		private Map<Condition, MutableInteger> condition2TupleIdx;
		
		private Map<Condition,Node> correspondingJoins;
		
		private TerminalNode currentFocus;
		
		/**
		 * the current focus is the terminal node of the current or-branch we handle
		 * at the moment. this is needed for storing the bindings from different
		 * or-branches separated from each other (the binding informations can differ
		 * in tuple- and slot-index in different or-branches)
		 */
		public TerminalNode getCurrentFocus() {
			return currentFocus;
		}

		/**
		 * the current focus is the terminal node of the current or-branch we handle
		 * at the moment. this is needed for storing the bindings from different
		 * or-branches separated from each other (the binding informations can differ
		 * in tuple- and slot-index in different or-branches)
		 */
		public void setCurrentFocus(TerminalNode currentFocus) {
			this.currentFocus = currentFocus;
		}

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

		public Condition getConditionFromTupleIdx(MutableInteger tupleIndex) {
			int idx = tupleIndex.get();
			for (Condition c : condition2TupleIdx.keySet()) {
				if (condition2TupleIdx.get(c).get().equals(idx)) return c;
			}
			return null;
		}

	}
	
	private class BeffyRuleConditionVisitor implements ConditionVisitor<CompileTableau, CompileTableau> {

		/**
		 * @return true, iff c is a Object-, Exists- or TestCondition
		 */
		private boolean isAlpha(Condition c) {
			return (c instanceof ObjectCondition || c instanceof ExistsCondition || c instanceof TestCondition);
		}
		
		public CompileTableau visit(AndCondition c, CompileTableau data) {
			int p = getNumber();
			log("(%d) i enter and-condition %d now. at first, i will handle the sub conditions...",p,c.hashCode());
			for (Condition subCondition : c.getNestedConditions()) {
				subCondition.acceptVisitor(this, data);
			}
			log("(%d) sub conditions handled.",p);
			
			// we need that after the if-expression for putting tests into it
			AbstractBetaFilterNode join=null;
			
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
				log("(%d) i will build a classic alpha/beta-join %d",p,joinNode.getId());
				join=joinNode;
				try {
					Condition c1 = c.getNestedConditions().get(0);
					Condition c2 = c.getNestedConditions().get(1);
					Condition alphaCond = (isAlpha(c1)) ? c1 : c2;
					Condition betaCond = (isAlpha(c1)) ? c2 : c1;
					if (isAlpha(c1) && isAlpha(c2)) {
						LeftInputAdaptorNode lia = new LeftInputAdaptorNode(engine);
						log("(%d) building left-input-adaptor %d for condition %d, because both inputs are alpha",p,lia.getId(),betaCond.hashCode());
						Node n = data.getLastNode(betaCond);
						n.addChild(lia); 
						data.setLastNode(betaCond, lia);
					}
					log("(%d) add new join to both subcondition's last nodes and fix the tuple indices",p);
					data.getLastNode(c1).addChild(joinNode);
					data.getLastNode(c2).addChild(joinNode);
					// fix the tuple indices for c1 and c2
					MutableInteger oldIdxC1 = data.getTupleIndexFromCondition(betaCond);
					MutableInteger oldIdxC2 = data.getTupleIndexFromCondition(alphaCond);
					log("(%d) before fixing: tuple index from condition %d is %d and from %d is %d.",p,betaCond.hashCode(),oldIdxC1.get(),alphaCond.hashCode(),oldIdxC2.get());
					int res = oldIdxC1.get() + 1;
					oldIdxC2.set(res);
					
					data.getTupleIndexFromCondition(c).set(res);
					
					log("(%d) fixing tuple index from condition %d to %d",p,alphaCond.hashCode(),res);
					log("(%d) set %d as corresponding join for the condition %d",p, joinNode.getId(), alphaCond.hashCode());
					data.setCorrespondingJoin(alphaCond, joinNode);
				} catch (NodeException e) {
					logAndFail(e, data);
				}
				data.setLastNode(c, joinNode);
			} else {
				// we need a fat-join node
				// TODO tuple index fix
				MultiBetaJoinNode joinNode = new MultiBetaJoinNode(engine);
				log("(%d) i will build a fat join %d",p,joinNode.getId());
				join=joinNode;
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
			
			// here we can put tests in the join node.
			for (Condition nest : c.getNestedConditions()) {
				if (nest instanceof TestCondition) {
					try {
						TestCondition test = (TestCondition) nest;
						log("(%d) found test condition '%s'...",p,test.toString());
						Function function = test.getFunction().lookUpFunction(engine);
						List<Parameter> parameters = substituteBoundParamsForFunctionCall(test.getFunction().getParameters(),data);
						GeneralizedFunctionEvaluator filter=new GeneralizedFunctionEvaluator(engine,function,parameters);
						log("(%d) ...and will add it to node %d.",p,join.getId());
						join.addFilter(filter);
					} catch (Exception e) {
						logAndFail(e, data);
					}
					
				}
			}
			
			// here we put field comparators in the join node
			for (Condition nest : c.getNestedConditions()){
				if (nest instanceof ObjectCondition) {
					ObjectCondition oc = (ObjectCondition) nest;
					for (Constraint constr : oc.getConstraints()) {
						if (constr instanceof BoundConstraint) {
							BoundConstraint bc = (BoundConstraint) constr;
							MutableInteger tupleIdx = data.getTupleIndexFromCondition(oc);
							Template templ = engine.findTemplate(oc.getTemplateName());
							int slotIdx = bc.isFactBinding() ? -1 : templ.getSlot(bc.getSlotName()).getId();
							// 'tupleIdx' and 'slotIdx' are our field-address for the actual bound-param
							
							Binding pivot=bindings.getBinding(data.getRule(), bc.getConstraintName(), data.getCurrentFocus());
							// 'pivot' is the field-address for the pivot element
							
							log("(%d) processing bound-constraint '%s' in tuple=%d and slot=%d (pivot is tuple=%d and slot=%d)...",p,bc.getConstraintName(), tupleIdx.get(), slotIdx, pivot.getTupleIndex().get(), pivot.getSlotIndex());
							
							if (pivot.getSlotIndex() == slotIdx && pivot.getTupleIndex().equals(tupleIdx)) {
								// the actual bound-param IS the pivot element. do nothing here.
								log("(%d) ...its the pivot element here, so do nothing.",p);
								
							} else if (pivot.getTupleIndex().equals(tupleIdx)) {
								// occurence in the same condition as the pivot element.
								// add an alpha comparator node in-between
								log("(%d) ...its not the pivot, but in the same condition. construct an alpha slot comparator node for it",p);
								int op = (bc.isNegated()) ? Constants.NOTEQUAL : Constants.EQUAL;
								Template t1 = engine.findTemplate(oc.getTemplateName());
								ObjectCondition c2 = (ObjectCondition) data.getConditionFromTupleIdx(pivot.getTupleIndex());
								Template t2 = engine.findTemplate( c2.getTemplateName() );
								TemplateSlot s1 = t1.getSlot(slotIdx);
								TemplateSlot s2 = t2.getSlot(pivot.getSlotIndex());
								AlphaSlotComparatorNode comp = new AlphaSlotComparatorNode(engine,op,s1,s2);
								Node lastNode = data.getLastNode(nest);
								lastNode.removeChild(join);
								try {
									lastNode.addChild(comp);
									data.setLastNode(nest, comp);
									comp.addChild(join);
								} catch (NodeException e) {
									logAndFail(e,data);
								}
								
							} else {
								log("(%d) ...its not the pivot and in another condition. write a filter in the join %d",p,join.getId());
								LeftFieldAddress f1 = new LeftFieldAddress(tupleIdx,slotIdx);
								LeftFieldAddress f2 = new LeftFieldAddress(pivot.getTupleIndex(),pivot.getSlotIndex());
								int op = (bc.isNegated())? Constants.NOTEQUAL : Constants.EQUAL;
								GeneralizedFieldComparator filter = new GeneralizedFieldComparator(bc.getConstraintName(),f1,op,f2);
								join.addFilter(filter);
							}
							
							
							
							
						}
					}
				}
			}
			
			return data;
		}

		public CompileTableau visit(ExistsCondition c, CompileTableau data) {
			int p = getNumber();
			log("(%d) i enter exists-condition %d now. i will visit the (hopefully only) sub-condition now.",p,c.hashCode());
			assert c.getNestedConditions().size() == 1;
			Condition nested = c.getNestedConditions().get(0);
			assert nested instanceof ObjectCondition;
			nested.acceptVisitor(this, data);
			Node lastNode = data.getLastNode(nested);
			//TODO discuss, how the stuff must go on here
			log("(%d) do some other magic and wrong stuff here...",p);
			List<Integer> distinctionSlots = new ArrayList<Integer>();
			ObjectCondition oc = (ObjectCondition)nested;
			for (Constraint constr: nested.getFlatConstraints()) {
				if (constr instanceof BoundConstraint) {
					BoundConstraint bc = (BoundConstraint)constr;
					Template t = engine.findTemplate(oc.getTemplateName());
					int id=t.getSlot(bc.getSlotName()).getId();
					distinctionSlots.add(id);
				}
			}
			
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
			log("(%d) i enter not-exists-condition %d now. but this is not yet implemented :(",getNumber(), c.hashCode());
			return data;
		}

		public CompileTableau visit(ObjectCondition c, CompileTableau data) {
			int p = getNumber();

			//ObjectConditions are alpha-networks and begin at the root node
			Node lastNode = null;
			
			//at first, we need the ObjectTypeNode
			Template template = engine.findTemplate(c.getTemplateName());
			log("(%d) i enter object-condition %d for template '%s' now.",p, c.hashCode(),template.getName());
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
						log("(%d) found literal constraint (%s %s %s). i'll generate a SlotFilterNode for it.",p, slot.getName(), (lc.isNegated()?"!=":"==") , slot.getValue().implicitCast(JamochaType.STRING).getStringValue() );
						SlotFilterNode filterNode = new SlotFilterNode(engine, operator, slot);
						lastNode.addChild(filterNode);
						lastNode = filterNode;
					} else if (constr instanceof BoundConstraint) {
						BoundConstraint bc = (BoundConstraint) constr;
						log("(%d) found bound-constraint '%s' . i will only notify the binding manager for it here. it will be handled in the and-condition.", p, bc.getConstraintName() );
						bindings.boundConstraintOccurs(bc, data.getCurrentFocus(), data);
					} else {
						log("(%d) found %s. is not implemented yet :(",p, constr.getClass().getSimpleName());
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
			
			/*
			 * for each subcondition C, which is not an and-condition,
			 * replace C by (and C (_initialFact)). so we can assume, that
			 * we have an or-condition of and-conditions. i think, its overkill
			 * to write a new optimizer-pass just for that.
			 */
			List<Condition> cons = c.getNestedConditions();
			for (int i=0; i<cons.size(); i++) {
				Condition co = cons.get(i);
				if (co instanceof AndCondition) continue;
				AndCondition n = new AndCondition();
				n.addNestedCondition(co);
				ObjectCondition ifact = new ObjectCondition(Collections.EMPTY_LIST, "_initialFact");
				n.addNestedCondition(ifact);
				cons.remove(co);
				cons.add(i, n);
			}
			String struct = c.format(ParserFactory.getFormatter());
			log("we will begin to compile the rule '%s' with the following structure:\n%s", data.getRule().getName(), struct);
			
			
			
			int p = getNumber();
			log("(%d) i enter or-condition %d now. at first, i will handle the sub conditions...",p,c.hashCode());
			for (Condition subCondition : c.getNestedConditions()) {
				/* for this sub-condition, we have to generate a new sub-rule,
				 * compile it and create a terminal node for it
				 */
				TerminalNode terminal = new TerminalNode(engine, data.getRule());
				data.setCurrentFocus(terminal);
				subCondition.acceptVisitor(this, data);
				
				if (data.getRule().getAutoFocus()) {
					log("(%d) set '%s' as auto-focus",p,data.getRule().getName());
					terminal.autoFocus();
				}
				
				Node last = data.getLastNode(subCondition);

				
				try {
					log("(%d) add terminal node for '%s'",p,data.getRule().getName());
					last.addChild(terminal);
					terminalNodes.put(data.getRule(), terminal);
					log("(%d) activate nodes for '%s'",p,data.getRule().getName());
					rootNode.activate();
				} catch (NodeException e) {
					logAndFail(e,data);
				}			
			}
			log("(%d) or-condition handled...",p);
			return data;
		}

		public CompileTableau visit(TestCondition c, CompileTableau data) {
			int p = getNumber();
			log("(%d) i enter test-condition %d now. tests are handled later in the and-condition. we will just add the initial-fact-node as condition's last node here.",p,c.hashCode());
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
	
	private Map<Rule,TerminalNode> terminalNodes;
	
	private ObjectTypeNodeManager objectTypeNodes;
	
	private BindingManager bindings;

	private List<CompilerListener> listeners;
	
	private Engine engine;
	
	private int pad=1;
	
	private RootNode rootNode;
	
	private ReteNet reteNet;
	
	private BeffyRuleOptimizer ruleOptimizer;
	
	private List<Parameter> substituteBoundParamsForFunctionCall(Parameter[] original, CompileTableau tableau) {
		List<Parameter> result = new ArrayList<Parameter>();
		for (Parameter p : original) {
			if (p instanceof BoundParam) {
				BoundParam bp = (BoundParam) p;
				Binding bnd = bindings.getBinding(tableau.getRule(), bp.getVariableName(), tableau.getCurrentFocus());
				FieldAddress fa;
				if (bnd.isWholeFactBinding()) {
					fa = new LeftFieldAddress(bnd.getTupleIndex());
				} else {
					fa= new LeftFieldAddress(bnd.getTupleIndex(),bnd.getSlotIndex());
				}
				result.add(fa);
			} else if (p instanceof JamochaValue) {
				result.add(p);
			} else if (p instanceof Signature) {
				Signature orig = (Signature)p;
				Signature newSig = new Signature(orig.getSignatureName());
				newSig.setParameters(substituteBoundParamsForFunctionCall(orig.getParameters(),tableau));
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
		this.terminalNodes = new HashMap<Rule, TerminalNode>();
	}

	private void logAndFail(Exception e, CompileTableau data) {
		data.setSuccess(false);
		Logging.logger(this.getClass()).warn(e);
	}
	
	private void log(String text, Object... args) {
		String output = String.format(text, args);
		Logging.logger(this.getClass().getCanonicalName()).debug(output);
	}
	
	private int getNumber() {
		return pad++;
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
			log("we had success compiling '%s', so we will activate all rules", rule.getName());
			try {
				rootNode.activate();
			} catch (NodeException e) {
				reteNet.cleanup();
				throw new CompileRuleException(e);
			}
			notifyListenersAdd(rule);
			rule.parentModule().addRule(rule);
			return true;
		} else {
			log("there were erros compiling '%s'! We will cleanup the rete network", rule.getName());
			reteNet.cleanup();
			return false;
		}
	}
	
	public Binding getBinding(String varName, TerminalNode focus, Rule r) {
		return bindings.getBinding(r, varName, focus);
	}

	public void removeListener(CompilerListener listener) {
			listeners.remove(listener);
	}

	private void notifyListenersAdd(Rule newRule) {
		CompileEvent event = new CompileEvent(this, CompileEvent.CompileEventType.RULE_ADDED);
		event.setRule(newRule);
		notifyListeners(event);
	}
	
	private void notifyListenersRemove(Rule newRule) {
		CompileEvent event = new CompileEvent(this, CompileEvent.CompileEventType.RULE_REMOVED);
		event.setRule(newRule);
		notifyListeners(event);
	}
	
	private void notifyListeners(CompileEvent ev) {
		for (CompilerListener li : listeners) li.compileEventOccured(ev);
	}

	public void removeRule(Rule rule) {
		TerminalNode tnode = terminalNodes.get(rule);
		terminalNodes.remove(rule);
		for (Node parent : tnode.getParentNodes()) {
			parent.removeChild(tnode);
		}
		reteNet.cleanup();
		notifyListenersRemove(rule);
	}

}
