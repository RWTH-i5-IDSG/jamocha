/*
 * Copyright 2007 Karl-Heinz Krempels, Josef Alexander Hahn, Sebastian Reinartz
 * Copyright 2002-2007 Peter Lin
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
package org.jamocha.rete.rulecompiler.sfp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.jamocha.Constants;
import org.jamocha.logging.DefaultLogger;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalConversionException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.RuleException;
import org.jamocha.rete.Binding;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.CompileEvent;
import org.jamocha.rete.CompilerListener;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Messages;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.Slot;
import org.jamocha.rete.StopCompileException;
import org.jamocha.rete.Template;
import org.jamocha.rete.TemplateSlot;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.ConstraintViolationException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.functions.Function;
import org.jamocha.rete.modules.Module;
import org.jamocha.rete.nodes.AbstractBeta;
import org.jamocha.rete.nodes.AlphaNode;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.BetaFilterNode;
import org.jamocha.rete.nodes.BetaQuantorFilterNode;
import org.jamocha.rete.nodes.ObjectTypeNode;
import org.jamocha.rete.nodes.ReteNet;
import org.jamocha.rete.nodes.RootNode;
import org.jamocha.rete.nodes.SlotAlpha;
import org.jamocha.rete.nodes.TerminalNode;
import org.jamocha.rete.nodes.joinfilter.FieldAddress;
import org.jamocha.rete.nodes.joinfilter.FieldComparator;
import org.jamocha.rete.nodes.joinfilter.FunctionEvaluator;
import org.jamocha.rete.nodes.joinfilter.JoinFilter;
import org.jamocha.rete.nodes.joinfilter.JoinFilterException;
import org.jamocha.rete.nodes.joinfilter.LeftFieldAddress;
import org.jamocha.rete.nodes.joinfilter.RightFieldAddress;
import org.jamocha.rule.AbstractCondition;
import org.jamocha.rule.Action;
import org.jamocha.rule.AndCondition;
import org.jamocha.rule.AndConnectedConstraint;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.ConditionWithNested;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.ExistCondition;
import org.jamocha.rule.FunctionAction;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.NotCondition;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.OrCondition;
import org.jamocha.rule.OrConnectedConstraint;
import org.jamocha.rule.OrderedFactConstraint;
import org.jamocha.rule.PredicateConstraint;
import org.jamocha.rule.Rule;
import org.jamocha.rule.TemplateValidation;
import org.jamocha.rule.TestCondition;

/**
 * @author Josef Alexander Hahn
 * @author Karl-Heinz Krempels
 * @author Peter Lin
 * @author Sebastian Reinartz
 * 
 */

// SOLVED simple literal constraints
// SOLVED boundconstraints in ObjectCondition
// SOLVED multi-occuring boundconstraints in one ObjectCondition
// SOLVED negated boundconstraints in ObjectCondition
// TODO what should happen when a bc only occurs in negated manner?
// TODO multifield stuff
// TODO predicate constraints
// TODO and / or / not CEs
// TODO forall / exists CEs
// TODO test CEs
// TODO use ObjectType Node of InitialFact only at empty LHS rules
// TODO implement / optimize node sharing
// TODO clean up "node-waste" if an exception occured during rule compilation.
// At the moment unused nodes remain in the network.
// TODO finish this todo list ;)
public class SFRuleCompiler implements RuleCompiler {

	private class OrderedFactBindingAddress extends BindingAddress {
		
		public int positionIndex;
		
		public OrderedFactBindingAddress(int conditionIndex, int slotIndex, int positionIndex, int operator) {
			super(conditionIndex,slotIndex,operator);
			this.positionIndex = positionIndex;
		}
		
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append("TupleIndex: ");
			result.append(tupleIndex);
			result.append(" SlotIndex: ");
			result.append(slotIndex);
			result.append(" PositionIndex: ");
			result.append(positionIndex);
			result.append(" Operator: ");
			result.append(ConversionUtils.getOperator(operator));
			result.append(" canBePivot: ");
			result.append(canBePivot);
			return result.toString();
		}
		
	}
	
	
	private class BindingAddress implements Comparable<BindingAddress> {
		public int tupleIndex;

		public int slotIndex;

		public int operator;

		public boolean canBePivot;

		public BindingAddress(int conditionIndex, int slotIndex, int operator) {
			super();
			this.tupleIndex = conditionIndex;
			this.slotIndex = slotIndex;
			this.operator = operator;
		}

		public int compareTo(BindingAddress o) {
			int conditionDifference = this.tupleIndex - o.tupleIndex;

			if (conditionDifference != 0)
				return conditionDifference;

			return this.slotIndex - o.slotIndex;
		}

		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append("TupleIndex: ");
			result.append(tupleIndex);
			result.append(" SlotIndex: ");
			result.append(slotIndex);
			result.append(" Operator: ");
			result.append(ConversionUtils.getOperator(operator));
			result.append(" canBePivot: ");
			result.append(canBePivot);
			return result.toString();
		}

	}

	private class PreBinding implements Comparable<PreBinding> {
		public int leftIndex;

		public int rightIndex;

		public int leftSlot;

		public int rightSlot;
		
		public int rightPosition;
		
		public int leftPosition;

		public int operator;

		public String varName;

		public String toString() {
			StringBuffer result = new StringBuffer();

			result.append("Prebinding: (");
			result.append(leftIndex);
			result.append(",");
			result.append(leftSlot);
			result.append(")");
			result.append(ConversionUtils.getOperatorDescription(operator));
			result.append("(");
			result.append(rightIndex);
			result.append(",");
			result.append(rightSlot);
			result.append(")");
			return result.toString();
		}

		public PreBinding(BindingAddress left, BindingAddress right,
				String varName) {
			super();
			this.varName = varName;
			this.leftIndex = left.tupleIndex;
			this.rightIndex = right.tupleIndex;
			this.leftSlot = left.slotIndex;
			this.rightSlot = right.slotIndex;
			if (left instanceof OrderedFactBindingAddress) {
				OrderedFactBindingAddress ofba = (OrderedFactBindingAddress) left;
				this.leftPosition = ofba.positionIndex;
			} else this.leftPosition = -1;
			if (right instanceof OrderedFactBindingAddress) {
				OrderedFactBindingAddress ofba = (OrderedFactBindingAddress) right;
				this.rightPosition = ofba.positionIndex;
			} else this.rightPosition = -1;
			if (left.operator == Constants.EQUAL) {
				this.operator = right.operator;
			} else if (right.operator == Constants.EQUAL) {
				this.operator = left.operator;
			} else {
				this.operator = Constants.NILL;
			}
		}

		public int getCorrectJoinTupleIndex() {
			if (leftIndex == rightIndex)
				return -1;
			return Math.max(leftIndex, rightIndex);
		}

		public int compareTo(PreBinding o) {
			return this.getCorrectJoinTupleIndex()
					- o.getCorrectJoinTupleIndex();
		}
	}

	private class BindingAddressesTable {
		private Map<String, Vector<BindingAddress>> row = new HashMap<String, Vector<BindingAddress>>();

		public BindingAddressesTable() {
			super();
		}

		public String toString() {
			StringBuffer result = new StringBuffer();
			for (String key : row.keySet()) {
				result.append(key).append("  :  ");
				for (BindingAddress ba : row.get(key)) {
					result.append(ba.toString()).append(" ; ");
				}
				result.append("\n");
			}
			return result.toString();
		}

		private Vector<BindingAddress> getBindingAddresses(String variable) {
			Vector<BindingAddress> result = row.get(variable);
			if (result == null) {
				return new Vector<BindingAddress>();
			} else {
				return result;
			}
		}

		public void addBindingAddress(BindingAddress ba, String variable) {
			Vector<BindingAddress> vector = row.get(variable);
			if (vector == null) {
				row.put(variable, vector = new Vector<BindingAddress>());
			}
			vector.add(ba);
		}

		private BindingAddress getPivot(String variable,
				Vector<BindingAddress> bas) {
			BindingAddress pivot = null;
			for (BindingAddress ba : bas) {
				if (ba.operator == Constants.EQUAL && ba.canBePivot) {
					if (pivot == null || pivot.compareTo(ba) > 0) {
						pivot = ba;
					}
				}
			}
			return pivot;
		}

		public BindingAddress getPivot(String variable) {
			return getPivot(variable, getBindingAddresses(variable));
		}

		public Vector<PreBinding> getPreBindings() {
			Vector<PreBinding> result = new Vector<PreBinding>();
			for (String variable : row.keySet()) {
				Vector<BindingAddress> bas = getBindingAddresses(variable);

				BindingAddress pivot = getPivot(variable, bas);

				if (pivot != null)
					for (BindingAddress ba : bas) {
						if (ba != pivot) {
							result.add(new PreBinding(pivot, ba, variable));
						}
					}
			}
			Collections.sort(result);
			return result;
		}
	}

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	private Rete engine = null;

	private ReteNet net = null;

	protected RootNode root = null;

	private Module currentMod = null;

	private ArrayList<CompilerListener> listener = new ArrayList<CompilerListener>();

	protected boolean validate = true;

	protected TemplateValidation tval = null;

	public static final String FUNCTION_NOT_FOUND = Messages
			.getString("CompilerProperties.function.not.found"); //$NON-NLS-1$

	public static final String INVALID_FUNCTION = Messages
			.getString("CompilerProperties.invalid.function"); //$NON-NLS-1$

	public static final String ASSERT_ON_PROPOGATE = Messages
			.getString("CompilerProperties.assert.on.add"); //$NON-NLS-1$

	protected DefaultLogger log = new DefaultLogger(this.getClass());

	public SFRuleCompiler(Rete engine, RootNode root, ReteNet net) {
		super();
		this.engine = engine;
		this.net = net;
		this.root = root;
		this.tval = new TemplateValidation(engine);
	}

	public void setValidateRule(boolean valid) {
		this.validate = valid;
	}

	public boolean getValidateRule() {
		return this.validate;
	}

	/**
	 * The method sets the module of the rule.
	 * 
	 * @param Rule
	 * @return void
	 */
	public void setModule(Rule rule) {
		// we check the name of the rule to see if it is for a specific
		// module. if it is, we have to add it to that module
		if (rule.getName().indexOf("::") > 0) { //$NON-NLS-1$
			String text = rule.getName();
			String[] sp = text.split("::"); //$NON-NLS-1$
			rule.setName(sp[1]);
			String modName = sp[0].toUpperCase();
			currentMod = engine.getModules().getModule(modName, false);
		} else {
			currentMod = engine.getCurrentFocus();
		}
		rule.setModule(currentMod);
	}

	/**
	 * The method creates the right terminal node based on the settings of the
	 * rule
	 * 
	 * @param Rule
	 * @return TerminalNode
	 */
	protected TerminalNode createTerminalNode(Rule rule) {
		TerminalNode node = new TerminalNode(net.nextNodeId(), rule);
		rule.SetTerminalNode(node);
		return node;
		/*
		 * if (rule.getNoAgenda() && rule.getExpirationDate() == 0) { return new
		 * NoAgendaTNode(engine.nextNodeId(), rule); } else if
		 * (rule.getNoAgenda() && rule.getExpirationDate() > 0) { return new
		 * NoAgendaTNode2(engine.nextNodeId(), rule); } else if
		 * (rule.getExpirationDate() > 0) { return new
		 * TerminalNode3(engine.nextNodeId(), rule); } else { return new
		 * TerminalNode2(engine.nextNodeId(), rule); }
		 */
	}

	/**
	 * The method adds an ObjectTypeNode to the HashMap. This implementation
	 * uses the Deftemplate as HashMap key and the Node as value. If the Node or
	 * the key already exists in the HashMap the compiler will not add it to the
	 * network.
	 * 
	 * @param ObjectTypeNode
	 * @return void
	 */
	public void addObjectTypeNode(Template template) {
		root.addObjectTypeNode(template, engine);
	}

	/**
	 * The method removes the ObjectTypeNode and calls clear on it.
	 * 
	 * @param ObjectTypeNode
	 *            node
	 * @return void
	 * @throws RetractException
	 */
	public void removeObjectTypeNode(ObjectTypeNode node)
			throws RetractException {
		// TODO: check here if a destroy is needed, I think not. deactivate
		// might be enough. (SR)

		root.removeObjectTypeNode(node);
		node.clear();
		node.destroy(net);
	}

	/**
	 * The method gets the ObjectTypeNode from the HashMap and returns it. If
	 * the node does not exist, the method will return null.
	 * 
	 * @param Template
	 * @return ObjectTypeNode
	 */
	public ObjectTypeNode getObjectTypeNode(Template template) {

		// TODO ObjectTypeNodes shouldnt become generated in deftemplate but
		// only when a rule needs it
		return (ObjectTypeNode) root.getObjectTypeNodes().get(template);
	}

	/**
	 * The method adds a CompilerListener to the SFRuleCompiler.
	 * 
	 * @param org.jamocha.rete.CompilerListener
	 * @return void
	 * 
	 * @see org.jamocha.rete.RuleCompiler#addListener(org.jamocha.rete.CompilerListener)
	 */
	public void addListener(CompilerListener listener) {
		if (!this.listener.contains(listener)) {
			this.listener.add(listener);
		}
	}

	/**
	 * The method removes the CompilerListener from the SFRuleCompiler.
	 * 
	 * @param org.jamocha.rete.CompilerListener
	 * @return void
	 * 
	 * @see org.jamocha.rete.RuleCompiler#addListener(org.jamocha.rete.CompilerListener)
	 */
	public void removeListener(CompilerListener listener) {
		this.listener.remove(listener);
	}

	public BaseNode compileRule(Rule rule) throws AssertException,
			StopCompileException, RuleException {
		rule.resolveTemplates(engine);
		this.setModule(rule);
		if (rule.getConditions() != null && rule.getConditions().length > 0) {
			Condition[] conds = rule.getConditions();
			for (int i = 0; i < conds.length; i++)
				conds[i].compile(this, rule, i);
			return compileJoins(rule);
		} else /* if (rule.getConditions().length == 0) */{
			return root.activateObjectTypeNode(engine.getInitialTemplate(), net);
		}
	}

	public boolean addRule(Rule rule) throws AssertException, RuleException {
		boolean result = false;
		TerminalNode tnode = createTerminalNode(rule);
		BaseNode lastNode;
		try {
			lastNode = compileRule(rule);
		} catch (StopCompileException e) {
			return e.isSubSuccessed();
		}
		lastNode.addNode(tnode, net);
		compileActions(rule);
		currentMod.addRule(rule);
		CompileEvent ce = new CompileEvent(rule, CompileEvent.ADD_RULE_EVENT);
		ce.setRule(rule);
		this.notifyListener(ce);
		engine.newRuleEvent(rule);
		result = true;
		return result;
	}

	protected boolean isQuantorCondition(Condition c) {
		return (c instanceof ExistCondition || c instanceof NotCondition);
	}
	
	public ObjectCondition getObjectCondition(Condition c) {
		if (c instanceof ExistCondition) {
			return (ObjectCondition) ( ((ExistCondition)c).getNestedConditionalElement().get(0) );
		} else if (c instanceof NotCondition) {
			return (ObjectCondition) (((NotCondition)c).getNestedConditionalElement().get(0));
		} else return null;
	}
	
	protected void rearrangeConditions(Condition[] conds) {
		BindingAddressesTable bat = computeBindingAddressTable(conds);
		for (int i = 0; i < conds.length; i++) {
			Condition c = conds[i];
			if (isQuantorCondition(c)){
				for (Constraint constr :
					getObjectCondition(c).getConstraints() ) {
					if (!(constr instanceof BoundConstraint)) continue;
					BoundConstraint bc = (BoundConstraint) constr;
					BindingAddress pivot = bat.getPivot(bc.getVariableName());
					if (pivot.tupleIndex > conditionIndexToTupleIndex(i,
							conds.length) ) {
						for ( int j=i ; j > 0 ; j-- ) {
							conds[j] = conds[j-1];
						}
						conds[0] = c;
						rearrangeConditions(conds);
						return;
					}
				}
			}
		}
	}

	protected BaseNode compileJoins(Rule rule) throws AssertException,
			RuleException {
		// take the last node from each condition and connect them by joins
		// regarding the complexity
		Condition[] sortedConds = rule.getObjectConditions().clone();
		Arrays.sort(sortedConds);

		rearrangeConditions(sortedConds);

		HashMap<Condition, BaseNode> conditionJoiners = new HashMap<Condition, BaseNode>();
		BaseNode initFactNode = root.activateObjectTypeNode(engine.getInitialTemplate(),
				net);

		BaseNode mostBottomNode = null;

		BaseNode fromBottom = null;
		for (int i = 0; i < sortedConds.length; i++) {

			Condition c = sortedConds[i];
			
			//System.out.println(c);
			AbstractBeta newBeta = null;

			if (c instanceof ObjectCondition)
				newBeta = new BetaFilterNode(net.nextNodeId());
			else if (c instanceof NotCondition)
				newBeta = new BetaQuantorFilterNode(net.nextNodeId(), true);
			else if (c instanceof ExistCondition)
				newBeta = new BetaQuantorFilterNode(net.nextNodeId(), false);

			if (fromBottom == null) {
				mostBottomNode = newBeta;
			} else {
				newBeta.addNode(fromBottom, net);
			}

			fromBottom = newBeta;

			BaseNode cLastNode = c.getLastNode();
			if (cLastNode == null)
				throw new RuleException("Rule Compilation Error in:"
						+ rule.getName());
			cLastNode.addNode(newBeta, net);

			conditionJoiners.put(c, newBeta);

		}

		if (fromBottom != null)
			initFactNode.addNode(fromBottom, net);

		if (mostBottomNode == null)
			mostBottomNode = sortedConds[0].getLastNode();

		BaseNode ultimateMostBottomNode = compileBindings(rule, sortedConds,
				conditionJoiners, mostBottomNode);
		// activate all joins
		for (BaseNode n : conditionJoiners.values()) {
			if (n == null)
				continue;
			((AbstractBeta) n).activate(net);
		}
		return ultimateMostBottomNode;
	}

	public int conditionIndexToTupleIndex(int cond, int condCount) {
		return condCount - cond;
	}

	public int tupleIndexToConditionIndex(int tupleInd, int condCount) {
		return condCount - tupleInd;
	}

	protected BindingAddressesTable computeBindingAddressTable(Condition[] conds) {
		BindingAddressesTable bindingAddressTable = new BindingAddressesTable();
		// Iterate of all conditions and constraints
		for (int i = 0; i < conds.length; i++) {
			// only for Object Conditions:
			if (conds[i] instanceof ObjectCondition
					|| conds[i] instanceof NotCondition
					|| conds[i] instanceof ExistCondition) {
				ObjectCondition oc = null;
				if (conds[i] instanceof ObjectCondition) {
					oc = (ObjectCondition) conds[i];
				} else if (conds[i] instanceof NotCondition) {
					oc = ((ObjectCondition) ((NotCondition) conds[i])
							.getNestedConditionalElement().get(0));
				} else if (conds[i] instanceof ExistCondition) {
					oc = ((ObjectCondition) ((ExistCondition) conds[i])
							.getNestedConditionalElement().get(0));
				}

				for (Constraint c : oc.getConstraints()) {
					// if we found a BoundConstraint
					if (c instanceof BoundConstraint) {
						BoundConstraint bc = (BoundConstraint) c;
						bindingEntry(conds, bindingAddressTable, i, bc);
					} else if (c instanceof OrderedFactConstraint) {
						OrderedFactConstraint ofc = (OrderedFactConstraint) c;
						for (int pos=0; pos<ofc.getConstraints().length; pos++) {
							Constraint co = ofc.getConstraints()[pos];
							if (co instanceof BoundConstraint) {
								BoundConstraint bco = (BoundConstraint) co;
								bindingEntry(conds, bindingAddressTable, i, pos, bco);
							}
						}
					}
				}
			}
		}
		return bindingAddressTable;
	}

	private void bindingEntry(Condition[] conds, BindingAddressesTable bindingAddressTable, int i, BoundConstraint bc) {
		BindingAddress ba;
		if (bc.getIsObjectBinding()) {
			ba = new BindingAddress(conditionIndexToTupleIndex(
					i, conds.length), -1, bc.getOperator());
		} else {
				ba = new BindingAddress(conditionIndexToTupleIndex(
					i, conds.length), bc.getSlot().getId(), bc
					.getOperator());
		}
		ba.canBePivot = !(conds[i] instanceof ExistCondition || conds[i] instanceof NotCondition);
		bindingAddressTable.addBindingAddress(ba, bc
				.getVariableName());
	}
	
	private void bindingEntry(Condition[] conds, BindingAddressesTable bat, int i, int po, BoundConstraint bc) {
		BindingAddress ba;
		if (!bc.getIsObjectBinding()) {
			ba = new OrderedFactBindingAddress(conditionIndexToTupleIndex(
				i, conds.length), 0, po, bc
				.getOperator());
			ba.canBePivot = !(conds[i] instanceof ExistCondition || conds[i] instanceof NotCondition);
			bat.addBindingAddress(ba, bc
					.getVariableName());
		}
	}

	protected BaseNode compileBindings(Rule rule, Condition[] conds,
			Map<Condition, BaseNode> conditionJoiners, BaseNode mostBottomNode)
			throws AssertException {
		// first, we need such a table (since each condition can contain many
		// constraints, there are more than one operator-sign per cell)
		/*
		 * CONDITION1 CONDITION2 CONDITION3 CONDITION4 CONDITION5 VARIABLE1 != != == == !=
		 * [==] VARIABLE2 == [==] VARIABLE3 != >= [==]
		 * 
		 * Note: here's some comments and general thoughts. A binding used in a
		 * join should normally use "==" and "!=". if it's some other operator,
		 * a TestNode is used to evaluate it. A condition like
		 * predicateConstraint (age ?age&:(> ?age 20) ) creates a binding and an
		 * alpha node that uses ">" operator. Peter 5/24/2007 Josef had a good
		 * question. The reason most people don't use numeric operators in JESS
		 * and clips for joins isn't technical. A join node can use numeric
		 * operators, it's just that people generally don't with CLIPS language.
		 * For example the pattern would look like this (age ?age2&:(> ?age2
		 * ?age1) ) Peter 5/26/07
		 */

		BindingAddressesTable bindingAddressTable = computeBindingAddressTable(conds);

		// create bindings for actions:
		for (String variable : bindingAddressTable.row.keySet()) {
			BindingAddress pivot = bindingAddressTable.getPivot(variable);

			Binding b = new Binding();
			b.setLeftIndex(pivot.slotIndex);
			if (b.getLeftIndex() == -1)
				b.setIsObjectVar(true);
			b.setLeftRow(pivot.tupleIndex);
			b.setVarName(variable);
			rule.addBinding(variable, b);

		}

		// get prebindings from table:
		Vector<PreBinding> preBindings = bindingAddressTable.getPreBindings();

		Iterator<PreBinding> itr = preBindings.iterator();
		PreBinding act = null;
		if (itr.hasNext())
			act = itr.next();
		JoinFilter[] bindArray = new JoinFilter[0];
		// traverse conditions and get their join node:
		for (int i = conds.length - 1; i >= 0; i--) {
			ArrayList<JoinFilter> filters = new ArrayList<JoinFilter>();
			BaseNode conditionJoiner = conditionJoiners.get(conds[i]);
			// traverse prebindings and try to set them to join nodes:
			while (act != null
					&& act.getCorrectJoinTupleIndex() == conditionIndexToTupleIndex(
							i, conds.length)) {

				
				LeftFieldAddress left = new LeftFieldAddress(Math.min(
						act.leftIndex, act.rightIndex), act.leftSlot, act.leftPosition);
				RightFieldAddress right = new RightFieldAddress(act.rightSlot, act.rightPosition);
				FieldComparator b = new FieldComparator(act.varName, left,
						act.operator, right);
				filters.add(b);
				act = (itr.hasNext()) ? itr.next() : null;
			}

			// set bindig= null if binds.size=0
			if (filters.size() > 0)
				((BetaFilterNode) conditionJoiner).setFilters(filters, engine);
		}
		// handle all bindings that couldn't be placed to join node.
		while (act != null) {

			Condition c = conds[tupleIndexToConditionIndex(act.leftIndex,
					conds.length)];
			if (!(c instanceof ObjectCondition))
				continue;
			ObjectCondition objectC = (ObjectCondition) c;
			Template template = objectC.getTemplate();
			ObjectTypeNode otn = root.activateObjectTypeNode(template, net);

			BetaFilterNode newJoin = new BetaFilterNode(net.nextNodeId());

			mostBottomNode.addNode(newJoin, net);
			otn.addNode(newJoin, net);

			mostBottomNode = newJoin;

			JoinFilter filter;
			
			LeftFieldAddress left = new LeftFieldAddress(act.leftIndex,
					act.leftSlot);
			RightFieldAddress right = new RightFieldAddress(act.rightSlot);
			filter = new FieldComparator(act.varName, left, act.operator, right);

			newJoin.addFilter(filter);

			act = (itr.hasNext()) ? itr.next() : null;
		}
		try {
			compileTestConditions(conds, rule, bindingAddressTable,
					conditionJoiners);
		} catch (JoinFilterException e) {
			engine.writeMessage(e.getMessage());
		}
		compilePredicateConstraints(rule, conds, conditionJoiners,
				bindingAddressTable);
		return mostBottomNode;

	}

	protected void compilePredicateConstraints(Rule rule, Condition[] conds,
			Map<Condition, BaseNode> conditionJoiners,
			BindingAddressesTable bindingAddressTable) throws AssertException {
		// search for predicate constraints and build filters for them
		for (Condition c : rule.getConditions()) {
			if (c instanceof ObjectCondition) {
				ObjectCondition objc = (ObjectCondition) c;
				for (Constraint constr : objc.getConstraints()) {
					if (constr instanceof PredicateConstraint) {
						PredicateConstraint pcon = (PredicateConstraint) constr;
						List<Parameter> params = (List<Parameter>) pcon
								.getParameters().clone();
						// determine good row index for our test
						int validRowIndex = 1;
						while (!params.isEmpty()) {
							Parameter p = params.remove(params.size() - 1);
							if (p instanceof Signature) {
								Signature sig = (Signature) p;
								for (Parameter pnew : sig.getParameters())
									params.add(pnew);
							} else if (p instanceof BoundParam) {
								BoundParam bp = (BoundParam) p;
								BindingAddress pivot = bindingAddressTable
										.getPivot(bp.getVariableName());
								if (pivot == null)
									throw new AssertException(
											"Error in TestCondition: Variable "
													+ bp.getVariableName()
													+ " is not defined");
								validRowIndex = Math.max(pivot.tupleIndex,
										validRowIndex);
							}
						}
						// determine corresponding node

						BetaFilterNode validNode = (BetaFilterNode) (conditionJoiners
								.get(conds[tupleIndexToConditionIndex(
										validRowIndex, conds.length)]));
						Parameter[] functionParams = recalculateParameters(
								conds.length, pcon.getParameters(),
								bindingAddressTable,
								tupleIndexToConditionIndex(validRowIndex,
										conds.length));
						Function function = engine.getFunctionMemory()
								.findFunction(pcon.getFunctionName());
						try {
							FunctionEvaluator filter = new FunctionEvaluator(
									engine, function, functionParams);
							validNode.addFilter(filter);
						} catch (JoinFilterException e) {
							engine.writeMessage(e.getMessage());
						}
					}
				}
			}
		}
	}

	protected Parameter[] recalculateParameters(int conditionsCount,
			List<Parameter> params, BindingAddressesTable bindingAddressTable,
			int conditionIndex) {
		Parameter[] paramsArr = new Parameter[params.size()];
		params.toArray(paramsArr);
		return recalculateParameters(conditionsCount, paramsArr,
				bindingAddressTable, conditionIndex);
	}

	protected Parameter[] recalculateParameters(int conditionsCount,
			Signature s, BindingAddressesTable bindingAddressTable,
			int conditionIndex) {
		return recalculateParameters(conditionsCount, s.getParameters(),
				bindingAddressTable, conditionIndex);
	}

	protected Parameter[] recalculateParameters(int conditionsCount,
			Parameter[] params, BindingAddressesTable bindingAddressTable,
			int conditionIndex) {
		List<Parameter> result = new ArrayList<Parameter>();
		for (Parameter p : params) {
			if (p instanceof BoundParam) {
				BoundParam bp = (BoundParam) p;
				BindingAddress pivot = bindingAddressTable.getPivot(bp
						.getVariableName());

				FieldAddress addr = null;
				if (pivot.tupleIndex == conditionIndexToTupleIndex(
						conditionIndex, conditionsCount)
						&& conditionIndex < conditionsCount) {
					if (pivot.slotIndex == -1) {
						addr = new RightFieldAddress();
					} else {
						addr = new RightFieldAddress(pivot.slotIndex);
					}
				} else {
					if (pivot.slotIndex == -1) {
						addr = new LeftFieldAddress(pivot.tupleIndex);
					} else {
						addr = new LeftFieldAddress(pivot.tupleIndex,
								pivot.slotIndex);
					}
				}

				result.add(addr);

			} else if (p instanceof Signature) {
				Signature nested = (Signature) p;
				result.add(nested);
				nested.setParameters(recalculateParameters(conditionsCount,
						nested, bindingAddressTable, conditionIndex));

			} else {
				result.add(p);
			}

		}

		Parameter[] arr = new Parameter[0];
		return result.toArray(arr);
	}

	// TODO: fix indices here
	protected void compileTestConditions(Condition[] objectConditions,
			Rule rule, BindingAddressesTable bindingAddressTable,
			Map<Condition, BaseNode> conditionJoiners)
			throws JoinFilterException {
		for (Condition c : rule.getConditions()) {
			if (c instanceof TestCondition) {
				TestCondition tc = (TestCondition) c;
				List<BoundParam> boundParams = tc.getFunction()
						.getBoundParameters();

				// determine good row index for our test
				int validRowIndex = 1;
				for (BoundParam p : boundParams) {
					BindingAddress pivot = bindingAddressTable.getPivot(p
							.getVariableName());
					if (pivot == null)
						throw new JoinFilterException(
								"Error in TestCondition: Variable "
										+ p.getVariableName()
										+ " is not defined");
					validRowIndex = Math.max(pivot.tupleIndex, validRowIndex);
				}

				// determine corresponding node
				BetaFilterNode validNode = (BetaFilterNode) (conditionJoiners
						.get(objectConditions[tupleIndexToConditionIndex(
								validRowIndex, objectConditions.length)]));

				Parameter[] functionParams = recalculateParameters(
						objectConditions.length, tc.getFunction(),
						bindingAddressTable, tupleIndexToConditionIndex(
								validRowIndex, objectConditions.length));
				FunctionEvaluator testFilter = new FunctionEvaluator(engine, tc
						.getFunction().lookUpFunction(engine), functionParams);
				validNode.addFilter(testFilter);
			}
		}
	}

	/**
	 * The method compiles an ObjectCondition.
	 * 
	 * @param condition
	 * @param conditionIndex
	 * @param rule
	 * 
	 * @return compileConditionState
	 * @throws StopCompileException
	 */
	public BaseNode compile(ObjectCondition condition, Rule rule,
			int conditionIndex) throws StopCompileException {
		// get activated ObjectType Node:
		Template template = condition.getTemplate();
		ObjectTypeNode otn = null;
		try {
			otn = root.activateObjectTypeNode(template, net);

			// add otn to condition:
			condition.addNode(otn);

			BaseNode prev = otn;
			SlotAlpha current = null;

			if (otn != null) {
				for (Constraint constraint : condition.getConstraints()) {

					if (constraint instanceof OrderedFactConstraint) {

						OrderedFactConstraint ofc = (OrderedFactConstraint) constraint;
						
						Constraint[] content = ofc.getConstraints();
						
						for (Constraint c : content) {
							current = prepareConstraintCompile(condition, rule, conditionIndex, template, prev, c);
							prev = current;
						}
						
						current = prepareConstraintCompile(condition, rule, conditionIndex, template, prev, constraint);
						prev = current;
						
					}
					
					current = prepareConstraintCompile(condition, rule, conditionIndex, template, prev, constraint);
					prev = current;
				}
			}
			return current;
		} catch (AssertException e1) {
			engine.writeMessage("ERROR: " + e1.getMessage());
			return null;
		}
	}

	private SlotAlpha prepareConstraintCompile(ObjectCondition condition, Rule rule, int conditionIndex, Template template, BaseNode prev, Constraint constraint) throws AssertException, StopCompileException {
		SlotAlpha current;
		TemplateSlot slot;
		slot = template.getSlot(constraint.getName());
		constraint.setSlot(slot);
		current = (SlotAlpha) constraint.compile(this, rule,
				conditionIndex);

		// we add the node to the previous
		if (current != null) {
			prev.addNode(current, net);
			condition.addNode(current);
			// now set the previous to current
		}
		return current;
	}

	/**
	 * The method compiles an ExistCondition.
	 * 
	 * @param condition
	 * @param conditionIndex
	 * @param rule
	 * 
	 * @return compileConditionState
	 */
	public BaseNode compile(ExistCondition condition, Rule rule,
			int conditionIndex) {
		Object o = condition.getNestedConditionalElement().get(0);
		AbstractCondition nested = (AbstractCondition) o;
		try {
			return nested.compile(this, rule, conditionIndex);
		} catch (Exception e) {
			engine.writeMessage(e.getMessage());
			return null /* or LONG_OBJECT */;
		}
	}

	/**
	 * The method compiles a TestCondition.
	 * 
	 * @param condition
	 * @param conditionIndex
	 * @param rule
	 * 
	 * @return compileConditionState
	 */
	public BaseNode compile(TestCondition condition, Rule rule,
			int conditionIndex) {
		return null;
	}

	/**
	 * The method compiles an AndCondition.
	 * 
	 * @param condition
	 * @param conditionIndex
	 * @param rule
	 * 
	 * @return compileConditionState
	 */
	public BaseNode compile(AndCondition condition, Rule rule,
			int conditionIndex) {
		return null;
	}

	/**
	 * The method compiles a NotCondition.
	 * 
	 * @param condition
	 * @param conditionIndex
	 * @param rule
	 * 
	 * @return compileConditionState
	 * @throws StopCompileException
	 * @throws AssertException
	 */
	public BaseNode compile(NotCondition condition, Rule rule,
			int conditionIndex) {
		Object o = condition.getNestedConditionalElement().get(0);
		AbstractCondition nested = (AbstractCondition) o;
		try {
			return nested.compile(this, rule, conditionIndex);
		} catch (Exception e) {
			engine.writeMessage(e.getMessage());
			return null /* or LONG_OBJECT */;
		}
	}

	/**
	 * The method compiles an OrCondition.
	 * 
	 * @param condition
	 * @param conditionIndex
	 * @param rule
	 * 
	 * @return compileConditionState
	 * @throws AssertException
	 * @throws RuleException
	 */
	public BaseNode compile(OrCondition condition, Rule rule, int conditionIndex)
			throws StopCompileException, AssertException {
		// now, we will split our rule in more different rules
		try {
			int counter = 1;
			boolean success = true;
			for (Object nested : condition.getNestedConditionalElement()) {
				Condition nestedCE = (Condition) nested;
				Rule newRule = null;
				try {
					newRule = ((Defrule) rule).clone(engine);
				} catch (CloneNotSupportedException e) {
					engine.writeMessage(e.getMessage());
				}
				newRule.setConditionIndex(conditionIndex, nestedCE);

				org.jamocha.rete.rulecompiler.sfp.SFRuleCompiler compiler = new org.jamocha.rete.rulecompiler.sfp.SFRuleCompiler(
						engine, root, net);

				newRule.setName(newRule.getName() + "-" + counter++);
				success = success && compiler.addRule(newRule);

			}
			throw new StopCompileException(success);
		} catch (EvaluationException e) {
			engine
					.writeMessage("FATAL: could not insert rule"
							+ e.getMessage());
			throw new StopCompileException(false);
		}

	}

	/**
	 * The method compiles a PredicateConstraint.
	 * 
	 * @param constraint
	 * @param defrule
	 * @param template
	 * @param conditionIndex
	 * 
	 * @return BaseNode
	 */
	public BaseNode compile(PredicateConstraint constraint, Rule rule,
			int conditionIndex) {
		return null;
	}

	/**
	 * The method compiles an OrLiteralConstraint.
	 * 
	 * @param constraint
	 * @param defrule
	 * @param template
	 * @param conditionIndex
	 * 
	 * @return BaseNode
	 * @throws StopCompileException
	 * @throws RuleException
	 */
	public BaseNode compile(OrConnectedConstraint constraint, Rule rule,
			int conditionIndex) throws StopCompileException {
		// now, we will split our rule in more different rules
		int counter = 1;

		boolean success = true;

		Constraint[] nestedConstraints = { constraint.getLeft(),
				constraint.getRight() };

		for (Constraint nested : nestedConstraints) {
			nested.setName(constraint.getName());

			Rule newRule = null;
			try {
				newRule = ((Defrule) rule).clone(engine);
			} catch (CloneNotSupportedException e) {
				engine.writeMessage(e.getMessage());
			}

			// search for position of the or constraint
			Stack<Condition> conditionStack = new Stack<Condition>();
			for (Condition c : newRule.getConditions())
				conditionStack.push(c);

			boolean replaced = false;
			constraintSearchLoop: while (!conditionStack.isEmpty()) {
				Condition c = conditionStack.pop();
				if (c instanceof ObjectCondition) {
					ObjectCondition objc = (ObjectCondition) c;
					for (int i = 0; i < objc.getConstraints().size(); i++) {
						Constraint constr = objc.getConstraints().get(i);
						if (constr == constraint) {
							// we've found our constraint ;)
							objc.getConstraints().set(i, nested);
							replaced = true;
							break constraintSearchLoop;
						}
					}
				}
				if (c instanceof ConditionWithNested) {
					ConditionWithNested cwn = (ConditionWithNested) c;
					for (Condition c2 : cwn.getNestedConditionalElement())
						conditionStack.add(c2);
				}
			}

			if (!replaced) {
				engine
						.writeMessage("FATAL: could not compile or connected constraint");
			}

			org.jamocha.rete.rulecompiler.sfp.SFRuleCompiler compiler = new org.jamocha.rete.rulecompiler.sfp.SFRuleCompiler(
					engine, root, net);

			newRule.setName(newRule.getName() + "-" + counter++);
			try {
				success = success && compiler.addRule(newRule);
			} catch (EvaluationException e) {
				engine.writeMessage("FATAL: could not insert rule"
						+ e.getMessage());
			}
		}
		throw new StopCompileException(success);
	}

	/**
	 * The method compiles a LiteralConstraint.
	 * 
	 * @param constraint
	 * @param defrule
	 * @param template
	 * @param conditionIndex
	 * 
	 * @return BaseNode
	 */
	public BaseNode compile(LiteralConstraint constraint, Rule rule,
			int conditionIndex) {
		SlotAlpha node = null;
		Slot sl = (Slot) constraint.getSlot().clone();
		JamochaValue sval;
		try {
			sval = constraint.getValue().implicitCast(sl.getValueType());
		} catch (IllegalConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		try {
			sl.setValue(sval);
		} catch (ConstraintViolationException e) {
			engine.writeMessage(e.getMessage());
		}
		node = new AlphaNode(net.nextNodeId());
		node.setSlot(sl);
		if (constraint.getNegated()) {
			node.setOperator(Constants.NOTEQUAL);
		} else {
			node.setOperator(Constants.EQUAL);
		}

		// we increment the node use count when when create a
		// new AlphaNode for the LiteralConstraint
		constraint.getSlot().incrementNodeCount();

		return node;
	}
	
	public BaseNode compile(OrderedFactConstraint constraint, Rule rule, int conditionIndex) {
		return null;
	}

	/**
	 * The method compiles a BoundConstraint.
	 * 
	 * @param constraint
	 * @param defrule
	 * @param template
	 * @param conditionIndex
	 * 
	 * @return BaseNode
	 */
	public BaseNode compile(BoundConstraint constraint, Rule rule,
			int conditionIndex) {
		// // we need to create a binding class for the BoundConstraint
		// if (rule.getBinding(constraint.getVariableName()) == null) {
		// // if the HashMap doesn't already contain the binding,
		// // we create
		// // a new one
		// if (constraint.getIsObjectBinding()) {
		// Binding bind = new Binding();
		// bind.setVarName(constraint.getVariableName());
		// bind.setLeftRow(conditionIndex);
		// bind.setLeftIndex(-1);
		// bind.setIsObjectVar(true);
		// rule.addBinding(constraint.getVariableName(), bind);
		// } else {
		// Binding bind = new Binding();
		// bind.setVarName(constraint.getVariableName());
		// bind.setLeftRow(conditionIndex);
		// bind.setLeftIndex(constraint.getSlot().getId());
		// bind.setRowDeclared(conditionIndex);
		// constraint.setFirstDeclaration(true);
		// rule.addBinding(constraint.getVariableName(), bind);
		// }
		// }
		return null;
	}

	/**
	 * The method compiles an AndLiteralConstraint.
	 * 
	 * @param constraint
	 * @param defrule
	 * @param template
	 * @param conditionIndex
	 * 
	 * @return BaseNode
	 * @throws RuleException
	 * @throws Exception
	 */

	public BaseNode compile(AndConnectedConstraint constraint, Rule rule,
			int conditionIndex) throws StopCompileException {
		boolean success = false;
		try {
			constraint.getLeft().setName(constraint.getName());
			constraint.getRight().setName(constraint.getName());
			// search for position of the and constraint
			Stack<Condition> conditionStack = new Stack<Condition>();
			for (Condition c : rule.getConditions())
				conditionStack.push(c);
			boolean replaced = false;
			constraintSearchLoop: while (!conditionStack.isEmpty()) {
				Condition c = conditionStack.pop();
				if (c instanceof ObjectCondition) {
					ObjectCondition objc = (ObjectCondition) c;
					for (int i = 0; i < objc.getConstraints().size(); i++) {
						Constraint constr = objc.getConstraints().get(i);
						if (constr == constraint) {
							// we've found our constraint ;)
							objc.getConstraints().remove(i);
							objc.getConstraints().add(constraint.getLeft());
							objc.getConstraints().add(constraint.getRight());
							replaced = true;
							break constraintSearchLoop;
						}
					}
				}
				if (c instanceof ConditionWithNested) {
					ConditionWithNested cwn = (ConditionWithNested) c;
					for (Condition c2 : cwn.getNestedConditionalElement())
						conditionStack.add(c2);
				}
			}

			if (!replaced) {
				engine
						.writeMessage("FATAL: could not compile or connected constraint");
			}

			org.jamocha.rete.rulecompiler.sfp.SFRuleCompiler compiler = new org.jamocha.rete.rulecompiler.sfp.SFRuleCompiler(
					engine, root, net);
			success = compiler.addRule(rule);
		} catch (EvaluationException e) {
			engine
					.writeMessage("FATAL: could not insert rule"
							+ e.getMessage());
		}
		throw new StopCompileException(success);

	}

	/**
	 * The method passes the event to all the CompilerListeners registered with
	 * this RuleCompiler. Furthermore, it checks what kind of event it is and
	 * calls the most appropriate method :-).
	 * 
	 * @param eventCompileEvent
	 * @return void
	 */
	protected void notifyListener(CompileEvent event) {
		Iterator itr = this.listener.iterator();
		// engine.writeMessage(event.getMessage());
		while (itr.hasNext()) {
			CompilerListener listen = (CompilerListener) itr.next();
			int etype = event.getEventType();
			if (etype == CompileEvent.ADD_RULE_EVENT) {
				listen.ruleAdded(event);
			} else if (etype == CompileEvent.REMOVE_RULE_EVENT) {
				listen.ruleRemoved(event);
			} else {
				listen.compileError(event);
			}
		}
	}

	/**
	 * Implementation will get the hashString from each node and compare them
	 * 
	 * @param otn
	 * @param alpha
	 * @return
	 */
	/*
	 * protected AbstractAlpha shareAlphaNode(AbstractAlpha existing,
	 * AbstractAlpha alpha) { Object[] scc = existing.getChildNodes(); for (int
	 * idx = 0; idx < scc.length; idx++) { Object next = scc[idx]; if (next
	 * instanceof AbstractAlpha) { AbstractAlpha baseAlpha = (AbstractAlpha)
	 * next; // TODO: don't use equal directly on nodes -> use hash values if
	 * (baseAlpha.equals(alpha)) { return baseAlpha; } } } return null; }
	 */

	/**
	 * The method compiles the the actions from the string form into the
	 * equivalent functions.
	 * 
	 * @param rule -
	 *            the rule object
	 * @param actions -
	 *            the action list
	 */
	protected void compileActions(Rule rule) {
		Action[] actions = rule.getActions();
		for (Action action : actions) {
			if (action instanceof FunctionAction) {
				FunctionAction fa = (FunctionAction) action;
				try {
					fa.configure(this.engine, rule);
				} catch (EvaluationException e) {
					e.printStackTrace();
				}
			} else {
				// do something else
			}
		}
	}
}
