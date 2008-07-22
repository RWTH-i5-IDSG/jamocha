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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.jamocha.engine.nodes.joinfilter.FieldComparator;
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
 * 
 * Or-Connected-Constraints
 * Return-Value-Constraints
 * Not-Exists-CE
 * Exists-CE
 * Or-Conditions
 * And-Conditions (where?)
 */

public class BeffyRuleCompiler implements RuleCompiler {

	
	/**
	 * this class represents one binding-scope in our rule. While
	 * our org.jamocha.engine.scope.Scope class is a runtime-scope
	 * (with values for the bindings) we need a simpler construct
	 * here for just mapping the compiletime-structure.
	 * One scope is simply a set of conditions. Each scope has a parent
	 * scope and some child scopes. 
	 */
	protected class Scope {
		
		private List<Condition> conditions;
		
		private List<Scope> childs;
		
		private Scope parent;
		
		private void setParent(Scope s) {
			parent = s;
		}
		
		public Scope(List<Condition> c) {
			this.conditions = c;
			childs = new ArrayList<Scope>();
		}
		
		public Scope() {
			this(new ArrayList<Condition>());
		}
		
		public void addCondition(Condition c) {
			conditions.add(c);
		}
		
		public List<Condition> getConditions() {
			return Collections.unmodifiableList(conditions);
		}

		public List<Scope> getChilds() {
			return Collections.unmodifiableList(childs);
		}
		
		public void addChild(Scope s) {
			childs.add(s);
			s.setParent(this);
		}
		
		public Scope getParent() {
			return parent;
		}
		
		public Set<String> getUsedBindings() {
			Set<String> result = new HashSet<String>();
			for (Condition c : getConditions())	{
				for (Constraint constr : c.getFlatConstraints()) {
					if (constr instanceof BoundConstraint) {
						BoundConstraint bc = (BoundConstraint) constr;
						result.add(bc.getConstraintName());
					} else if (constr instanceof AndConnectedConstraint) {
						for (Constraint ac : flattenAndConnectedConstraint((AndConnectedConstraint)constr)) {
							if (ac instanceof BoundConstraint) {
								BoundConstraint bc = (BoundConstraint) ac;
								result.add(bc.getConstraintName());
							}
						}
						
						
					}
				}
			}
			return result;
		}
		
		public Set<String> getIntroducedBindings() {
			Set<String> result = getUsedBindings();
			Scope parent = this.parent;
			while (parent != null) {
				Set<String> introducedEarlier = parent.getUsedBindings();
				result.removeAll(introducedEarlier);
				parent = parent.parent;
			}
			return result;
		}
		
	}
	
	/**
	 * this class holds some information about the rule, like
	 * mapping between join-nodes <> Conditions <> Condition-Indices
	 * and some more information.
	 */
	protected class RuleCompilation {
		
		private Rule rule;
		
		private Map<Condition,Integer> conditionIndices;
		
		private Map<Condition,Node> conditionsLastNode;
		
		private Map<Condition,AbstractBetaFilterNode> conditionJoiners;
		
		private Map<String,Binding> bindingCache;
		
		private Scope rootScope;
		
		private BindingTableau bindingTableau;
		
		private Node lastNode;
		
		public RuleCompilation(Rule r) throws CompileRuleException {
			rule = r;
			computeConditionIndices();
			bindingTableau = new BindingTableau(this);
			conditionsLastNode = new HashMap<Condition, Node>();
			conditionJoiners = new HashMap<Condition, AbstractBetaFilterNode>();
			bindingCache = new HashMap<String, Binding>();
		}
		
		public void setConditionsLastNode(Condition c, Node n) {
			conditionsLastNode.put(c, n);
		}
		
		public Node getConditionsLastNode(Condition c) {
			return conditionsLastNode.get(c);
		}
		
		public void setConditionJoiner(Condition c, AbstractBetaFilterNode joiner) {
			conditionJoiners.put(c,joiner);
		}
		
		public AbstractBetaFilterNode getConditionJoiner(Condition c) {
			return conditionJoiners.get(c);
		}
		
		public Node getLastNode() {
			return lastNode;
		}
		
		/**
		 * here all condition indices are computed. this includes
		 * a mapping Condition->index, Scopes and some more
		 */
		private int computeConditionIndices(List<Condition> list, int start, Scope s) {
			int i = start;
			for (Condition c : list) {
				s.addCondition(c);
				addConditionIndex(c, i);
				i++;
				if (c instanceof ConditionWithNested) {
					ConditionWithNested cwn = (ConditionWithNested)c;
					Scope next = s;
					if (cwn instanceof NotExistsCondition || cwn instanceof ExistsCondition) {
						Scope s2 = new Scope();
						s.addChild(s2);
						next = s2;
					}
					i = computeConditionIndices(cwn.getNestedConditions(), i, next);
				}
			}
			return i;
		}
		
		private void addConditionIndex(Condition c, int i) {
			conditionIndices.put(c, i);
		}



		private void computeConditionIndices() {
			rootScope = new Scope();
			conditionIndices = new HashMap<Condition, Integer>();
			computeConditionIndices(rule.getConditions(), 0, rootScope);
		}


		int getConditionIndex(Condition c) {
			return conditionIndices.get(c);
		}

		public Scope getRootScope() {
			return rootScope;
		}

		
		
	}
	
	/**
	 * this class represents the occurence of a binding.
	 */
	protected class BindingOccurence {
		
		private int conditionIndex;
		
		private boolean ident;
		
		private BoundConstraint constr;
		
		private Condition cond;
		
		public LeftFieldAddress toLeftFieldAddress() {
			if (constr.isFactBinding()) {
				return new LeftFieldAddress(getConditionIndex());
			} else {
				return new LeftFieldAddress(getConditionIndex(),getSlotIndex());
			}
		}
		
		public RightFieldAddress toRightFieldAddress() {
			if (constr.isFactBinding()) {
				return new RightFieldAddress();
			} else {
				return new RightFieldAddress(getSlotIndex());
			}
		}
		
		public BindingOccurence(int condIdx, boolean ident, BoundConstraint bc, Condition cond) {
			this.conditionIndex = condIdx;
			this.ident = ident;
			this.constr = bc;
			this.cond = cond;
		}
		
		public String toString() {
			return "["+conditionIndex+"id:"+ident+"]";
		}
		
		public Condition getCondition() {
			return cond;
		}
		
		public int getConditionIndex() {
			return conditionIndex;
		}
		
		public boolean isIdent() {
			return ident;
		}
		
		public BoundConstraint getConstraint() {
			return constr;
		}

		public boolean isFactBinding() {
			return constr.isFactBinding();
		}
		
		public int getSlotIndex() {
			assert !isFactBinding();
			ObjectCondition oc = (ObjectCondition) getCondition();
			Template templ = engine.findTemplate(oc.getTemplateName());
			TemplateSlot ts = templ.getSlot(getConstraint().getSlotName());
			return ts.getId();
		}
		
	}

	/**
	 * the purpose is to generate a list of all constraints inside an
	 * and-connected-constraint (which normally is a tree-like structure)
	 */
	protected static List<Constraint> flattenAndConnectedConstraint(AndConnectedConstraint c) {
		List<Constraint> insideAndCC = new ArrayList<Constraint>();
		Stack<Constraint> ccStack = new Stack<Constraint>();
		ccStack.push(c);
		while (!ccStack.isEmpty()) {
			Constraint co = ccStack.pop();
			if (co instanceof AndConnectedConstraint) {
				ccStack.push(((AndConnectedConstraint)co).getLeft());
				ccStack.push(((AndConnectedConstraint)co).getRight());
			} else {
				insideAndCC.add(co);
			}
		}
		return insideAndCC;
	}
	
	/**
	 * this tableau holds the information, where to find the "pivot"-element
	 * for a binding (means: a non-negated, most-top-level-scoped occurence,
	 * which can be used as reference in all comparison operations later on
	 */
	protected class BindingTableau {
		
		private Map<String, List<BindingOccurence> > bindingOccurences;
		
		private Map<String, BindingOccurence> pivotElements;
		
		private RuleCompilation ruleCompilation;
		
		public BindingTableau(RuleCompilation rule) throws CompileRuleException {
			this.ruleCompilation = rule;
			bindingOccurences = new HashMap<String, List<BindingOccurence>>();
			computeBindingOccurences();
			computePivots();
		}
		
		private void computeBindingOccurences() {
			computeBindingOccurences(ruleCompilation.rule.getConditions());
		}

		private void computeBindingOccurences(List<Condition> conditions) {
			for (Condition c : conditions) {
				for (Constraint con : c.getFlatConstraints()) {
					if (con instanceof BoundConstraint) {
						extractOccurence(c, (BoundConstraint)con);
					} else if (con instanceof AndConnectedConstraint) {
						for (Constraint bconst: flattenAndConnectedConstraint((AndConnectedConstraint)con)) {
							if (bconst instanceof BoundConstraint)
								extractOccurence(c, (BoundConstraint)bconst);
						}
					}
				}
				if (c instanceof ConditionWithNested) {
					ConditionWithNested cwn = (ConditionWithNested) c;
					computeBindingOccurences(cwn.getNestedConditions());
				}
			}
		}

		private void extractOccurence(Condition c, BoundConstraint bc) {
			String bindingName = bc.getConstraintName();
			boolean ident;
			if (c instanceof ObjectCondition) {
				// in an object condition, an occurence is "ident", iff it is not negated
				ident = !bc.isNegated();
			} else {
				// in all other condition types (test, and so on) it can't be ident
				ident = false;
			}
			addSingleBindingOccurence(c, bindingName, bc, ident);
		}

		private void computePivots() throws CompileRuleException {
			pivotElements = new HashMap<String, BindingOccurence>();
			log("i begin with computing pivot elements");
			computePivots(ruleCompilation.getRootScope());
			log("i finished computing pivot elements");
		}
		
		private void computePivots(Scope scope) throws CompileRuleException {
			//compute all pivot elements here
			Set<String> bindingsHere = scope.getIntroducedBindings();
			for (String bind : bindingsHere){
				List<BindingOccurence> occurences = getOccurencesList(bind);
				
				BindingOccurence pivot = null;
				for (BindingOccurence occ : occurences) {
					if (!occ.ident) continue; // we only need occurences, which are not negated or anything like that
					
					// we check whether our occurence is inside an or-condition. we want to drop it for now, if so!
					BoundConstraint bc = occ.getConstraint();
					Condition c = bc.getParentCondition();
					assert(c instanceof ObjectCondition);
					
					if (pivot == null) {
						//if we dont have any pivot element until here, we take everything
						pivot = occ; 
					} else {
						// else, we have to check, whether the new one is better than the old one (better = lower index)
						if (occ.conditionIndex < pivot.conditionIndex) pivot = occ;
					}
				}
				
				if (pivot == null) throw new CompileRuleException("Sorry, but the rule compiler was" +
																  "not able to determine a pivot element" +
																  " for the binding ?"+bind+". This can be" +
																  " our fault or is because of cyclic definition" +
																  " in your rule. You can try to fix that by changing" +
																  " your bindings!");
				pivotElements.put(bind, pivot);
				log("Determined pivot element for ?"+bind+" at "+pivot);
			}
			
			
			//delegate computation for all child scopes to a new call
			for (Scope child: scope.getChilds()) {
				computePivots(child);
			}
		}

		public int getPivotCondition(String binding) {
			return pivotElements.get(binding).getConditionIndex();
		}

		public BindingOccurence getPivotOccurence(String binding) {
			return pivotElements.get(binding);
		}
		
		private List<BindingOccurence> getOccurencesList(String b) {
			List<BindingOccurence> result = bindingOccurences.get(b);
			if (result == null) {
				result = new ArrayList<BindingOccurence>();
				bindingOccurences.put(b, result);
			}
			return result;
		}
		
		private void addSingleBindingOccurence(Condition cond, String binding, BoundConstraint bc,  boolean ident) {
			List<BindingOccurence> oclist = getOccurencesList(binding);
			BindingOccurence occ = new BindingOccurence(ruleCompilation.getConditionIndex(cond), ident, bc, cond);
			oclist.add(occ);
		}
		
	}
	
	private JamochaLogger l;
	
	private Engine engine;
	
	private RootNode rootNode;
	
	private ReteNet reteNet;
	
	private Map<String,ObjectTypeNode> objectTypeNodes;
	
	private Map<Rule,RuleCompilation> compiledRules;

	private List<CompilerListener> listeners;
	
	public BeffyRuleCompiler(Engine engine, RootNode root, ReteNet net) {
		l = Logging.logger(this.getClass());
		this.engine = engine;
		this.rootNode = root;
		this.reteNet = net;
		this.objectTypeNodes = new HashMap<String, ObjectTypeNode>();
		this.compiledRules = new HashMap<Rule, RuleCompilation>();
		this.listeners = new ArrayList<CompilerListener>();
	}
	
	private void signalListeners(Rule r) {
		CompileEvent event = new CompileEvent(this, CompileEvent.CompileEventType.RULE_ADDED);
		event.setRule(r);
		for (CompilerListener l : listeners) {
			l.compileEventOccured(event);
		}
	}
	
	private void log(String message) {
		l.debug(message);
	}

	public void addListener(CompilerListener listener) {
		listeners.add(listener);
	}

	public ObjectTypeNode getObjectTypeNode(String template) {
		return objectTypeNodes.get(template);
	}
	
	protected void compileSubRule(Rule r) throws CompileRuleException, NodeException {
		RuleCompilation ruleCompilation = new RuleCompilation(r);
		// we assume, that our precompiler created a rule, which has only one
		// and-condition at top-level
		Condition topLevel = r.getConditions().get(0);
		assert (topLevel instanceof AndCondition);
		compile(ruleCompilation, topLevel);
		
		// we determine our last condition joiner
		List<Condition> conditions = ((AndCondition)topLevel).getNestedConditions();
		Condition lastCondition = conditions.get(conditions.size()-1);
		Node lastJoiner = ruleCompilation.getConditionJoiner(lastCondition);
		assert (lastJoiner.getChildNodes().length==0);
		
		TerminalNode terminal = compileTerminalNode(ruleCompilation);
		lastJoiner.addChild(terminal);
		
		compiledRules.put(r, ruleCompilation);
		
		r.parentModule().addRule(r);
		signalListeners(r);
	}
	
	

	private TerminalNode compileTerminalNode(RuleCompilation ruleCompilation) {
		TerminalNode result = new TerminalNode(reteNet.nextNodeId(),engine.getWorkingMemory(),ruleCompilation.rule,reteNet);
		
		return result;
	}

	private SlotFilterNode literalConstraint2SlotFilterNode(LiteralConstraint lc, ObjectCondition cond) throws EvaluationException {
		ObjectCondition ocond = (ObjectCondition) lc.getParentCondition();
		TemplateSlot tslot = engine.findTemplate(ocond.getTemplateName()).getSlot(lc.getSlotName());
		Slot slot = tslot.createSlot(engine);
		slot.setValue(lc.getValue());
		int operator = lc.isNegated() ?  Constants.NOTEQUAL : Constants.EQUAL;
		SlotFilterNode slotfilter = new SlotFilterNode(reteNet.nextNodeId(),engine.getWorkingMemory(), operator, slot, reteNet);
		return slotfilter;
	}
	
	private void compileLiteralConstraint(RuleCompilation ruleComp, ObjectCondition cond, LiteralConstraint constraint) throws NodeException, CompileRuleException {
		 SlotFilterNode sfn;
		try {
			sfn = literalConstraint2SlotFilterNode(constraint, cond);
			Node lastNode = ruleComp.getConditionsLastNode(cond);
			lastNode.addChild(sfn);
			ruleComp.setConditionsLastNode(cond, sfn);
		} catch (EvaluationException e) {
			throw new CompileRuleException(e);
		}
	}
	
	private void compilePredicateConstraint(RuleCompilation ruleComp, ObjectCondition cond, PredicateConstraint constraint) throws CompileRuleException {
		try {
			AbstractBetaFilterNode ourJoinNode = ruleComp.getConditionJoiner(cond);
			Function function = engine.getFunctionMemory().findFunction(constraint.getFunctionName());
			List<Parameter> parameters = substituteBoundParams(ruleComp,constraint.getParameters(), cond);
			FunctionEvaluator evaluator = new FunctionEvaluator(engine, function, parameters);
			ourJoinNode.addFilter(evaluator);
		} catch (JoinFilterException e) {
			throw new CompileRuleException(e);
		} catch (FunctionNotFoundException e) {
			throw new CompileRuleException("Function "+constraint.getFunctionName()+" not found.",e);
		}
	}

	private void compileReturnValueConstraint(RuleCompilation ruleComp, ObjectCondition cond, ReturnValueConstraint constraint) {
		
	}

	private void compileAndConnectedConstraint(RuleCompilation ruleComp, ObjectCondition cond, AndConnectedConstraint constraint) throws CompileRuleException, NodeException {
		compileConstraint(ruleComp, cond, constraint.getLeft());
		compileConstraint(ruleComp, cond, constraint.getRight());
	}

	private void compileOrConnectedConstraint(RuleCompilation ruleComp, ObjectCondition cond, OrConnectedConstraint constraint) {
		
	}
	
	private void compileBoundConstraint(RuleCompilation ruleComp, ObjectCondition cond, BoundConstraint constraint) throws NodeException {
		AbstractBetaFilterNode ourJoinNode = ruleComp.getConditionJoiner(cond);
		
		String bindingName = constraint.getConstraintName();
		BindingOccurence pivot = ruleComp.bindingTableau.getPivotOccurence(bindingName);
		
		if (pivot.constr == constraint) {
			// CASE 1: The binding is our pivot element => nothing to do

		} else if ( pivot.conditionIndex == ruleComp.getConditionIndex(cond) ) {
			// CASE 2: The binding is not the pivot element, but is in the same condition
			int operator = constraint.isNegated() ? Constants.NOTEQUAL : Constants.EQUAL;
			Template t = engine.findTemplate(cond.getTemplateName());
			TemplateSlot slot1 = t.getSlot(pivot.constr.getSlotName());
			TemplateSlot slot2 = t.getSlot(constraint.getSlotName());
			AlphaSlotComparatorNode comparatorNode = new AlphaSlotComparatorNode(
					reteNet.nextNodeId(),engine.getWorkingMemory(), operator,slot1,slot2,reteNet);
			Node lastNode = ruleComp.getConditionsLastNode(cond);
			lastNode.addChild(comparatorNode);
			ruleComp.setConditionsLastNode(cond, comparatorNode);
			
		} else {
			// CASE 3: The binding is in another condition than the pivot's condition
			
			BindingOccurence piv = ruleComp.bindingTableau.getPivotOccurence(constraint.getConstraintName());
			LeftFieldAddress left = piv.toLeftFieldAddress();
			
			RightFieldAddress right;
			if (constraint.isFactBinding()) {
				right = new RightFieldAddress();
			} else {
				Template t = engine.findTemplate(cond.getTemplateName());
				TemplateSlot tslot = t.getSlot(constraint.getSlotName());
				right = new RightFieldAddress(tslot.getId());
			}

			int op = constraint.isNegated() ? Constants.NOTEQUAL : Constants.EQUAL;
			FieldComparator comp= new FieldComparator(constraint.getConstraintName(),left,op,right);
			ourJoinNode.addFilter(comp);
		}

	}
	
	private void compileConstraint(RuleCompilation ruleComp, ObjectCondition cond, Constraint constraint) throws CompileRuleException, NodeException {
		if (constraint instanceof LiteralConstraint) {
			compileLiteralConstraint(ruleComp, cond, (LiteralConstraint)constraint);
		} else if (constraint instanceof PredicateConstraint) {
			compilePredicateConstraint(ruleComp, cond, (PredicateConstraint)constraint);
		} else if (constraint instanceof ReturnValueConstraint) {
			compileReturnValueConstraint(ruleComp, cond, (ReturnValueConstraint)constraint);
		} else if (constraint instanceof AndConnectedConstraint) {
			compileAndConnectedConstraint(ruleComp, cond, (AndConnectedConstraint)constraint);
		} else if (constraint instanceof OrConnectedConstraint) {
			compileOrConnectedConstraint(ruleComp, cond, (OrConnectedConstraint)constraint);
		} else if (constraint instanceof BoundConstraint) {
			compileBoundConstraint(ruleComp, cond, (BoundConstraint)constraint);
		}
	}
	
	private boolean compileObjectCondition(RuleCompilation ruleComp, ObjectCondition cond) throws NodeException, CompileRuleException {
		/* we assume that we have all bindind's pivot elements available at this moment,
		 * because we already have rearranged our rule in order to achieve that  */
		ObjectTypeNode otn = getObjectTypeNode(cond.getTemplateName());
		
		ruleComp.setConditionsLastNode(cond, otn);
		
		for (Constraint constr : cond.getConstraints()) {
			compileConstraint(ruleComp, cond, constr);
		}
		return true;
	}

	private boolean compileTestCondition(RuleCompilation ruleComp, TestCondition cond) {
		try{
			ruleComp.setConditionsLastNode(cond, getObjectTypeNode("_initialFact"));
			Function function = cond.getFunction().lookUpFunction(engine);
			List<Parameter> parameters = substituteBoundParams(ruleComp, cond.getFunction().getParameters(),cond);
			FunctionEvaluator evaluator = new FunctionEvaluator(engine, function, parameters);
			AbstractBetaFilterNode join = ruleComp.getConditionJoiner(cond);
			join.addFilter(evaluator);
		} catch (FunctionNotFoundException f) {
			Logging.logger(this.getClass()).info(f);
			return false;
		} catch (JoinFilterException e) {
			Logging.logger(this.getClass()).info(e);
			return false;
		}
		return true;
	}
	
	private List<Parameter> substituteBoundParams(RuleCompilation rc, Parameter[] parameters, Condition occurence) {
		ArrayList<Parameter> result = new ArrayList<Parameter>();
		for(Parameter p : parameters) {
			if (p instanceof Signature) {
				Signature s1 = (Signature) p;
				Signature s2 = new Signature(s1.getSignatureName());
				s2.setParameters( substituteBoundParams(rc, s1.getParameters(), occurence));
				result.add(s2);
			} else if (p instanceof BoundParam) {
				BindingOccurence boc = rc.bindingTableau.getPivotOccurence(((BoundParam)p).getVariableName());
				// left or right field address?
				if (occurence == null || occurence!=boc.cond) {
					result.add(boc.toLeftFieldAddress());
				} else {
					result.add(boc.toRightFieldAddress());
				}
			} else {
				result.add(p);
			}
		}
		return result;
	}
	
	private List<Parameter> substituteBoundParams(RuleCompilation rc, List<Parameter> parameters, Condition occurence) {
		return substituteBoundParams(rc, parameters.toArray(new Parameter[0]), occurence);
	}

	private List<String> getUsedBindings(Condition c) {
		List<String> result = new ArrayList<String>();
		for (Constraint constr : c.getFlatConstraints() ) {
			if (constr instanceof BoundConstraint) {
				result.add(constr.getConstraintName());
			}
		}
		return result;
	}

	private boolean compileAndCondition(RuleCompilation ruleComp, AndCondition cond) throws CompileRuleException {
		/* This is some kind of entry point in our real compilation effort.
		 * At this point, we assume, that all nested CEs are object-conditions, test conditions
		 * and not-exists-conditions. we have no further nested and-condition here (since this 
		 * would not play any role inside an and condition) and no or-conditions (since they are
		 * replaced by a number of subrules).
		 */
		List<Condition> conds= new ArrayList<Condition>();
		conds.addAll(cond.getNestedConditions());
		try {
			generateJoinNodes(ruleComp,conds);
		} catch (NodeException e) {
			Logging.logger(this.getClass()).fatal(e);
		}
		boolean weMadeProgress = true;
		while (weMadeProgress && !conds.isEmpty() ) {
			weMadeProgress = false;
			Iterator<Condition> iterC = conds.iterator();
			while (iterC.hasNext()) {
				Condition c = iterC.next();
				if (compile(ruleComp,c)) {
					weMadeProgress = true;
					iterC.remove();
				}
			}
		}
		try {
			mountJoinNodes(ruleComp,cond.getNestedConditions());
		} catch (NodeException e) {
			Logging.logger(this.getClass()).fatal(e);
		}
		return (conds.isEmpty());
	}

	private void mountJoinNodes(RuleCompilation ruleComp, List<Condition> conds) throws NodeException {
		for (Condition c : conds) {
			AbstractBetaFilterNode j = ruleComp.getConditionJoiner(c);
			Node last = ruleComp.getConditionsLastNode(c);
			last.addChild(j);
			ruleComp.lastNode = j;
		}
	}

	private void generateJoinNodes(RuleCompilation ruleComp, List<Condition> conds) throws NodeException {
		ObjectTypeNode initialFact = getObjectTypeNode(Constants.INITIAL_FACT);
		Node init = new LeftInputAdaptorNode(reteNet.nextNodeId(),engine.getWorkingMemory(),reteNet);
		initialFact.addChild(init);
		Node before = init;
		for (Condition c : conds) {
			AbstractBetaFilterNode j = new SimpleBetaFilterNode(reteNet.nextNodeId(),engine.getWorkingMemory(),reteNet);
			before.addChild(j);
			ruleComp.setConditionJoiner(c, j);
			before = j;
		}
	}

	private boolean compileNotExistsCondition(RuleCompilation ruleComp, NotExistsCondition cond) throws CompileRuleException {
		return compileQuantorCondition(ruleComp, cond, true);
	}
	
	private boolean compileExistsCondition(RuleCompilation ruleComp, ExistsCondition cond) throws CompileRuleException {
		return compileQuantorCondition(ruleComp, cond, false);
	}

	
	private boolean compileQuantorCondition(RuleCompilation ruleComp, ConditionWithNested cond, boolean negated) throws CompileRuleException {
		/*
		 * here we create a new condition tree, which is the same as the old one,
		 * but with "exploded" exists (or not-exists) condition. that means,
		 * the exists-condition itself will become replaced by its nested
		 * child conditions.
		 */

		/* since all our trees start with a single and-condition at the
		 * top level, we can clone the whole condition tree this way:
		 */
		Condition newRootClone;
		{
			Condition root = cond;
			while (root.getParentCondition() != null) root = root.getParentCondition();
			newRootClone = root.clone();
		}

		/*
		 * now, we have to search for our exists condition and replace it
		 */
		Stack<Condition> stack = new Stack<Condition>();
		stack.push(newRootClone);

		while (!stack.isEmpty()) {
			Condition c = stack.pop();

			// is it our condition => make the replacement and break
			if (c.equals(cond)) {
				ConditionWithNested parent = (ConditionWithNested)c.getParentCondition();
				parent.removeNestedCondition(c);
				for (Condition sameLvl : ((ConditionWithNested)c).getNestedConditions())
					parent.addNestedCondition(sameLvl);
				break;
			}

			// is it another ConditionWithNested => add childs to stack
			if (c instanceof ConditionWithNested) {
				for (Condition child : ((ConditionWithNested)c).getNestedConditions())
					stack.push(child);
			}

			// elsewise => dr0p it
			//

		}

		List<Condition> lc = new ArrayList<Condition>();
		lc.add(newRootClone);
		Rule helperRule = new Defrule(null,"",lc, null);
		RuleCompilation innerCompilation = new RuleCompilation(helperRule);
		compile(innerCompilation,newRootClone);
		
		Node innerEndNode = innerCompilation.getLastNode();
		
		RightInputAdaptorNode rightInputAdaptor = new RightInputAdaptorNode(reteNet.nextNodeId(),engine.getWorkingMemory(),reteNet);
		
		try {
			innerEndNode.addChild(rightInputAdaptor);
			//TODO the join node must become a quantor node
			AbstractBetaFilterNode joiner = ruleComp.getConditionJoiner(cond);
			rightInputAdaptor.addChild(joiner);
		} catch (NodeException e) {
			throw new CompileRuleException(e);
		}
		
		return true;
	}

	private boolean compile(RuleCompilation ruleCompilation, Condition condition) throws CompileRuleException {
		try{
			if (condition instanceof ObjectCondition) {
				return compileObjectCondition( ruleCompilation, (ObjectCondition) condition);
			} else if (condition instanceof TestCondition) {
				return compileTestCondition( ruleCompilation, (TestCondition) condition);
			} else if (condition instanceof AndCondition) {
				return compileAndCondition( ruleCompilation, (AndCondition) condition);
			} else if (condition instanceof NotExistsCondition) {
				return compileNotExistsCondition( ruleCompilation, (NotExistsCondition) condition);
			} else if (condition instanceof ExistsCondition) {
				return compileExistsCondition( ruleCompilation, (ExistsCondition) condition);
			} else {
				throw new CompileRuleException("unimplemented condition type "+condition.getClass().getSimpleName()+" found");
			}
		} catch (NodeException e) {
			throw new CompileRuleException("error while building the rete network. must be a bug :(",e);
		}
	}

	protected List<Rule> precompile(Rule rule) {
		// TODO: move all optimizations to the class RuleOptimizer and call it in this method.
		
		List<Rule> result = new ArrayList<Rule>();
		
		AndCondition topLevelAnd = new AndCondition();
		for(Condition c:rule.getConditions()) topLevelAnd.addNestedCondition(c);
		rule.getConditions().clear();
		rule.getConditions().add(topLevelAnd);
		
		result.add(rule);
		
		return result;
	}
	
	public boolean addRule(Rule rule) throws AssertException, RuleException, EvaluationException, CompileRuleException {
		
		List<Rule> subRules = precompile(rule);
		
		try {
			for (Rule r : subRules)	compileSubRule(r);
			reteNet.getRoot().activate();
		} catch (NodeException e) {
			throw new CompileRuleException(e);
		}
		
		
		
		return true;
	}

	public Binding getBinding(String varName, Rule r) {
		RuleCompilation rc = compiledRules.get(r);
		Binding result = rc.bindingCache.get(varName);
		if (result !=null) return result;
		BindingOccurence pivot = rc.bindingTableau.getPivotOccurence(varName);
		assert (pivot != null);
		Binding b = new Binding();
		if (pivot.constr.isFactBinding()) {
			b.setIsObjectVar(true);
		} else {
			b.setLeftIndex(pivot.getSlotIndex());
		}
		b.setLeftRow(pivot.getConditionIndex());
		rc.bindingCache.put(varName, b);
		return b;
	}

	public void removeListener(CompilerListener listener) {
		listeners.remove(listener);
	}

	public void addObjectTypeNode(Template template) {
		if (getObjectTypeNode(template.getName()) != null ) return;
		ObjectTypeNode otn = new ObjectTypeNode(reteNet.nextNodeId(), engine.getWorkingMemory(),reteNet, template);
		objectTypeNodes.put(template.getName(), otn);
		try {
			rootNode.addChild(otn);
			otn.activate();
		} catch (NodeException e) {
			Logging.logger(this.getClass()).fatal(e);
		}
	}
}
