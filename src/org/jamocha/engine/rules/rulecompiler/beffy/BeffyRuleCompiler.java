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
import org.jamocha.engine.nodes.AlphaTemporalFilterNode;
import org.jamocha.engine.nodes.BetaTemporalFilterNode;
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
			BindingInformation bi = getBindingInformation(r,varName);
			if (bi != null) {
				return getBindingInformation(r,varName).b;				
			} else return null;
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
			
			// if our occurence is deeper or as deep as inside than the existing
			// occurence, throw the new one away,
			BindingInformation bin = bindings.getBindingInformation(r, varName);
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

		public Condition getConditionFromTupleIdx(MutableInteger tupleIndex) {
			int idx = tupleIndex.get();
			for (Condition c : condition2TupleIdx.keySet()) {
				if (condition2TupleIdx.get(c).get().equals(idx)) return c;
			}
			return null;
		}

	}
	
	private class BeffyRuleConditionVisitor implements ConditionVisitor<CompileTableau, CompileTableau> {

		private boolean isAlpha(Condition c) {
			return (c instanceof ObjectCondition || c instanceof ExistsCondition || c instanceof TestCondition);
		}
		
		public CompileTableau visit(AndCondition c, CompileTableau data) {
			int p = getNumber();
			log("(%d) i enter an and-condition now. at first, i will handle the sub conditions...",p);
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
					if (isAlpha(c1) && isAlpha(c2)) {
						log("(%d) building left-input-adaptor, because both inputs are alpha",p);
						LeftInputAdaptorNode lia = new LeftInputAdaptorNode(engine);
						Node n = data.getLastNode(alphaCond);
						n.addChild(lia); slow(data);
						data.setLastNode(alphaCond, lia);
					}
					log("(%d) add new join to both subcondition's last nodes and fix the tuple indices",p);
					data.getLastNode(c1).addChild(joinNode);slow(data);
					data.getLastNode(c2).addChild(joinNode);slow(data);
					// fix the tuple indices for c1 and c2
					MutableInteger oldIdxC1 = data.getTupleIndexFromCondition(c1);
					MutableInteger oldIdxC2 = data.getTupleIndexFromCondition(c2);
					oldIdxC2.set(oldIdxC1.get() + 1);
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
						n.addChild(joinNode);slow(data);
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
							
							Binding pivot=bindings.getBinding(data.getRule(), bc.getConstraintName());
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
								lastNode.removeChild(join);slow(data);
								try {
									lastNode.addChild(comp);slow(data);
									data.setLastNode(nest, comp);
									comp.addChild(join);slow(data);
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
			log("(%d) i enter an exists-condition now. i will visit the (hopefully only) sub-condition now.",p);
			assert c.getNestedConditions().size() == 1;
			Condition nested = c.getNestedConditions().get(0);
			nested.acceptVisitor(this, data);
			Node lastNode = data.getLastNode(nested);
			//TODO discuss, how the stuff must go on here
			log("(%d) do some other magic and wrong stuff here...",p);
			List<Integer> distinctionSlots = new ArrayList<Integer>();
			
			AlphaQuantorDistinctionNode quantorNode = new AlphaQuantorDistinctionNode(engine, distinctionSlots);
			try {
				lastNode.addChild(quantorNode);slow(data);
			} catch (NodeException e) {
				logAndFail(e, data);
			}
			data.setLastNode(c, quantorNode);
			return data;
		}

		public CompileTableau visit(NotExistsCondition c, CompileTableau data) {
			log("(%d) i enter an not-exists-condition now. but this is not yet implemented :(",getNumber());
			return data;
		}

		public CompileTableau visit(ObjectCondition c, CompileTableau data) {
			int p = getNumber();

			//ObjectConditions are alpha-networks and begin at the root node
			Node lastNode = null;
			
			//at first, we need the ObjectTypeNode
			Template template = engine.findTemplate(c.getTemplateName());
			log("(%d) i enter an object-condition for template '%s' now.",p, template.getName());
			try {
				lastNode = objectTypeNodes.getObjectTypeNode(template);
				
				//temporal stuff
				if (Constants.TEMPORAL_STRATEGY.equals("SEPARATE_RETE")) {
					AlphaTemporalFilterNode tempoNode = new AlphaTemporalFilterNode(engine);
					lastNode.addChild(tempoNode);
					lastNode = tempoNode;
				}
				
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
						lastNode.addChild(filterNode);slow(data);
						lastNode = filterNode;
					} else if (constr instanceof BoundConstraint) {
						BoundConstraint bc = (BoundConstraint) constr;
						log("(%d) found bound-constraint '%s'. i will only notify the binding manager for it here. it will be handled in the and-condition.", p, bc.getConstraintName() );
						bindings.boundConstraintOccurs(bc, data);
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
			log("we will begin to compile the rule '%s' with the following structure:\n %s", data.getRule().getName(), struct);
			
			
			
			int p = getNumber();
			log("(%d) i enter an or-condition now. at first, i will handle the sub conditions...",p);
			for (Condition subCondition : c.getNestedConditions()) {
				/* for this sub-condition, we have to generate a new sub-rule,
				 * compile it and create a terminal node for it
				 */
				subCondition.acceptVisitor(this, data);
				TerminalNode terminal = new TerminalNode(engine, data.getRule());
				
				if (data.getRule().getAutoFocus()) {
					log("(%d) set '%s' as auto-focus",p,data.getRule().getName());
					terminal.autoFocus();
				}
				
				Node last = data.getLastNode(subCondition);
				
				// temporal stuff
				if (Constants.TEMPORAL_STRATEGY.equals("SEPARATE_RETE")){
					if (data.getRule().getTemporalValidity()!= null) {
						BetaTemporalFilterNode btfn = new BetaTemporalFilterNode(engine,data.getRule().getTemporalValidity());
						try {
							last.addChild(btfn);
						} catch (NodeException e) {
							logAndFail(e,data);
						}
						last = btfn;
						data.setLastNode(subCondition, btfn);
					}
				}
				
				
				try {
					log("(%d) add terminal node for '%s'",p,data.getRule().getName());
					last.addChild(terminal);slow(data);
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
			log("(%d) i enter an test-condition now. tests are handled later in the and-condition. we will just add the initial-fact-node as condition's last node here.",p);
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
	
	private int pad=1;
	
	private RootNode rootNode;
	
	private ReteNet reteNet;
	
	private BeffyRuleOptimizer ruleOptimizer;
	
	private List<Parameter> substituteBoundParamsForFunctionCall(Parameter[] original, CompileTableau tableau) {
		List<Parameter> result = new ArrayList<Parameter>();
		for (Parameter p : original) {
			if (p instanceof BoundParam) {
				BoundParam bp = (BoundParam) p;
				Binding bnd = bindings.getBinding(tableau.getRule(), bp.getVariableName());
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
			notifyListeners(rule);
			rule.parentModule().addRule(rule);
			return true;
		} else {
			log("there were erros compiling '%s'! We will cleanup the rete network", rule.getName());
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
	
	private void slow(CompileTableau t) {
		if (t.getRule() instanceof Defrule) {
			Defrule r = (Defrule) t.getRule();
			if (r.getSlowCompile()){
				try {
					Thread.currentThread().sleep(2500);
				} catch (InterruptedException e) {
				}
			}
		}
	}

}
