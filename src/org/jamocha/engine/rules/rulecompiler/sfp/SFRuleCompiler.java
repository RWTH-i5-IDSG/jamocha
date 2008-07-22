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

package org.jamocha.engine.rules.rulecompiler.sfp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.jamocha.Constants;
import org.jamocha.communication.events.CompilerListener;
import org.jamocha.communication.logging.Logging;
import org.jamocha.communication.logging.Logging.JamochaLogger;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.Binding;
import org.jamocha.engine.BoundParam;
import org.jamocha.engine.ConstraintViolationException;
import org.jamocha.engine.ConversionUtils;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.RetractException;
import org.jamocha.engine.RuleCompiler;
import org.jamocha.engine.StopCompileException;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.functions.FunctionNotFoundException;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.OneInputNode;
import org.jamocha.engine.nodes.QuantorBetaFilterNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.SimpleBetaFilterNode;
import org.jamocha.engine.nodes.SlotFilterNode;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.nodes.TwoInputNode;
import org.jamocha.engine.nodes.joinfilter.FieldAddress;
import org.jamocha.engine.nodes.joinfilter.FieldComparator;
import org.jamocha.engine.nodes.joinfilter.FunctionEvaluator;
import org.jamocha.engine.nodes.joinfilter.JoinFilter;
import org.jamocha.engine.nodes.joinfilter.JoinFilterException;
import org.jamocha.engine.nodes.joinfilter.LeftFieldAddress;
import org.jamocha.engine.nodes.joinfilter.RightFieldAddress;
import org.jamocha.engine.workingmemory.elements.Slot;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalConversionException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.AbstractCondition;
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
import org.jamocha.rules.OrCondition;
import org.jamocha.rules.OrConnectedConstraint;
import org.jamocha.rules.OrderedFactConstraint;
import org.jamocha.rules.PredicateConstraint;
import org.jamocha.rules.Rule;
import org.jamocha.rules.TestCondition;

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
public class SFRuleCompiler implements RuleCompiler {

	private class OrderedFactBindingAddress extends BindingAddress {

		public int positionIndex;

		public OrderedFactBindingAddress(final int conditionIndex,
				final int slotIndex, final int positionIndex, final int operator) {
			super(conditionIndex, slotIndex, operator);
			this.positionIndex = positionIndex;
		}

		@Override
		public String toString() {
			final StringBuilder result = new StringBuilder();
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

		public BindingAddress(final int conditionIndex, final int slotIndex,
				final int operator) {
			super();
			tupleIndex = conditionIndex;
			this.slotIndex = slotIndex;
			this.operator = operator;
		}

		public int compareTo(final BindingAddress o) {
			final int conditionDifference = tupleIndex - o.tupleIndex;

			if (conditionDifference != 0)
				return conditionDifference;

			return slotIndex - o.slotIndex;
		}

		@Override
		public String toString() {
			final StringBuilder result = new StringBuilder();
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

		@Override
		public String toString() {
			final StringBuffer result = new StringBuffer();

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

		public PreBinding(final BindingAddress left,
				final BindingAddress right, final String varName) {
			super();
			this.varName = varName;
			leftIndex = left.tupleIndex;
			rightIndex = right.tupleIndex;
			leftSlot = left.slotIndex;
			rightSlot = right.slotIndex;
			if (left instanceof OrderedFactBindingAddress) {
				final OrderedFactBindingAddress ofba = (OrderedFactBindingAddress) left;
				leftPosition = ofba.positionIndex;
			} else
				leftPosition = -1;
			if (right instanceof OrderedFactBindingAddress) {
				final OrderedFactBindingAddress ofba = (OrderedFactBindingAddress) right;
				rightPosition = ofba.positionIndex;
			} else
				rightPosition = -1;
			if (left.operator == Constants.EQUAL)
				operator = right.operator;
			else if (right.operator == Constants.EQUAL)
				operator = left.operator;
			else
				operator = Constants.NILL;
		}

		public int getCorrectJoinTupleIndex() {
			if (leftIndex == rightIndex)
				return -1;
			return Math.max(leftIndex, rightIndex);
		}

		public int compareTo(final PreBinding o) {
			return getCorrectJoinTupleIndex() - o.getCorrectJoinTupleIndex();
		}
	}

	private class BindingAddressesTable {
		private final Map<String, Vector<BindingAddress>> row = new HashMap<String, Vector<BindingAddress>>();

		public BindingAddressesTable() {
			super();
		}

		@Override
		public String toString() {
			final StringBuffer result = new StringBuffer();
			for (final String key : row.keySet()) {
				result.append(key).append("  :  ");
				for (final BindingAddress ba : row.get(key))
					result.append(ba.toString()).append(" ; ");
				result.append("\n");
			}
			return result.toString();
		}

		private Vector<BindingAddress> getBindingAddresses(final String variable) {
			final Vector<BindingAddress> result = row.get(variable);
			if (result == null)
				return new Vector<BindingAddress>();
			else
				return result;
		}

		public void addBindingAddress(final BindingAddress ba,
				final String variable) {
			Vector<BindingAddress> vector = row.get(variable);
			if (vector == null)
				row.put(variable, vector = new Vector<BindingAddress>());
			vector.add(ba);
		}

		private BindingAddress getPivot(final String variable,
				final Vector<BindingAddress> bas) {
			BindingAddress pivot = null;
			for (final BindingAddress ba : bas)
				if (ba.operator == Constants.EQUAL && ba.canBePivot)
					if (pivot == null || pivot.compareTo(ba) > 0)
						pivot = ba;
			return pivot;
		}

		public BindingAddress getPivot(final String variable) {
			return getPivot(variable, getBindingAddresses(variable));
		}

		public Vector<PreBinding> getPreBindings() {
			final Vector<PreBinding> result = new Vector<PreBinding>();
			for (final String variable : row.keySet()) {
				final Vector<BindingAddress> bas = getBindingAddresses(variable);

				final BindingAddress pivot = getPivot(variable, bas);

				if (pivot != null)
					for (final BindingAddress ba : bas)
						if (ba != pivot)
							result.add(new PreBinding(pivot, ba, variable));
			}
			Collections.sort(result);
			return result;
		}
	}

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	private Engine engine = null;

	private ReteNet net = null;

	protected RootNode root = null;

	private final ArrayList<CompilerListener> listener = new ArrayList<CompilerListener>();

	protected boolean validate = true;

	public static final String FUNCTION_NOT_FOUND = "CompilerProperties.function.not.found"; //$NON-NLS-1$

	public static final String INVALID_FUNCTION = "CompilerProperties.invalid.function"; //$NON-NLS-1$

	public static final String ASSERT_ON_PROPOGATE = "CompilerProperties.assert.on.add"; //$NON-NLS-1$

	protected JamochaLogger log = Logging.logger(this.getClass());

	protected Map<Condition, Node> lastNodes = new HashMap<Condition, Node>();

	protected Map<Rule, TerminalNode> terminals = new HashMap<Rule, TerminalNode>();

	protected Map<Rule, List<Binding>> bindings = new HashMap<Rule, List<Binding>>();

	public SFRuleCompiler(final Engine engine, final RootNode root,
			final ReteNet net) {
		super();
		this.engine = engine;
		this.net = net;
		this.root = root;
		terminals = new HashMap<Rule, TerminalNode>();
	}

	protected List<Binding> getRuleBindings(final Rule r) {
		List<Binding> res = bindings.get(r);
		if (res == null) {
			res = new ArrayList<Binding>();
			bindings.put(r, res);
		}
		return res;
	}

	protected void addRuleBinding(final Rule r, final Binding b) {
		getRuleBindings(r).add(b);
	}

	public void setValidateRule(final boolean valid) {
		validate = valid;
	}

	public boolean getValidateRule() {
		return validate;
	}

	/**
	 * The method sets the module of the rule.
	 * 
	 * @param Rule
	 * @return void
	 */
	public void setModule(final Rule rule) {
		// we check the name of the rule to see if it is for a specific
		// // module. if it is, we have to add it to that module
		// TODO remove all that if (rule.getName().indexOf("::") > 0) {
		// //$NON-NLS-1$
		// String text = rule.getName();
		// String[] sp = text.split("::"); //$NON-NLS-1$
		// rule.setName(sp[1]);
		// String modName = sp[0].toUpperCase();
		// currentMod = engine.getModules().findModule(modName);
		// } else {
		// currentMod = engine.getCurrentFocus();
		// }
		// rule.setModule(currentMod);
	}

	/**
	 * The method creates the right terminal node based on the settings of the
	 * rule
	 * 
	 * @param Rule
	 * @return TerminalNode
	 */
	protected TerminalNode createTerminalNode(final Rule rule) {
		final TerminalNode node = new TerminalNode(net.nextNodeId(), engine
				.getWorkingMemory(), rule, net);

		terminals.put(rule, node);
		return node;
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
	public void addObjectTypeNode(final Template template) {
		final int newid = engine.getNet().nextNodeId();
		final ObjectTypeNode newOtn = new ObjectTypeNode(newid, engine
				.getWorkingMemory(), net, template);
		try {
			root.addChild(newOtn);
		} catch (final NodeException e) {
			engine.writeMessage(e.getMessage());
		}
	}

	/**
	 * The method removes the ObjectTypeNode and calls clear on it.
	 * 
	 * @param ObjectTypeNode
	 *            node
	 * @return void
	 * @throws RetractException
	 */
	public void removeObjectTypeNode(final ObjectTypeNode node)
			throws RetractException {
		// TODO: implement it
	}

	/**
	 * The method gets the ObjectTypeNode from the HashMap and returns it. If
	 * the node does not exist, the method will return null.
	 * 
	 * @param Template
	 * @return ObjectTypeNode
	 */
	public ObjectTypeNode getObjectTypeNode(final Template template) {
		final Node[] nodes = root.getChildNodes();
		for (final Node n : nodes)
			if (n instanceof ObjectTypeNode)
				if (n != null) {
					final ObjectTypeNode otn = (ObjectTypeNode) n;
					if (otn.getTemplate().equals(template))
						return otn;
				}
		addObjectTypeNode(template);
		return getObjectTypeNode(template);
	}

	/**
	 * The method adds a CompilerListener to the SFRuleCompiler.
	 * 
	 * @param org.jamocha.rete.CompilerListener
	 * @return void
	 * 
	 * @see org.jamocha.rete.RuleCompiler#addListener(org.jamocha.rete.CompilerListener)
	 */
	public void addListener(final CompilerListener listener) {
		if (!this.listener.contains(listener))
			this.listener.add(listener);
	}

	/**
	 * The method removes the CompilerListener from the SFRuleCompiler.
	 * 
	 * @param org.jamocha.rete.CompilerListener
	 * @return void
	 * 
	 * @see org.jamocha.rete.RuleCompiler#addListener(org.jamocha.rete.CompilerListener)
	 */
	public void removeListener(final CompilerListener listener) {
		this.listener.remove(listener);
	}

	public Node compileRule(final Rule rule) throws EvaluationException,
			StopCompileException, RuleException {
		setModule(rule);
		if (rule.getConditions() != null && !rule.getConditions().isEmpty()) {
			final List<Condition> conds = rule.getConditions();
			int i = 0;
			for (final Condition c : conds)
				c.compile(this, rule, i++);
			return compileJoins(rule);
		} else
			return getObjectTypeNode(engine.getInitialTemplate());
	}

	public boolean addRule(final Rule rule) throws EvaluationException,
			RuleException {
		final TerminalNode tnode = createTerminalNode(rule);
		Node lastNode;
		try {
			lastNode = compileRule(rule);
		} catch (final StopCompileException e) {
			return e.isSubSuccessed();
		}
		try {
			lastNode.addChild(tnode);
		} catch (final NodeException e) {
			engine.writeMessage(e.getMessage());
		}
		compileActions(rule);
		rule.parentModule().addRule(rule);
		try {
			root.activate();
		} catch (final NodeException e) {
			Logging.logger(this.getClass()).info(e);
		}
		return true;
	}

	protected boolean isQuantorCondition(final Condition c) {
		return c instanceof ExistsCondition || c instanceof NotExistsCondition;
	}

	public ObjectCondition getObjectCondition(final Condition c) {
		if (c instanceof ExistsCondition)
			return (ObjectCondition) ((ExistsCondition) c)
					.getNestedConditions().get(0);
		else if (c instanceof NotExistsCondition)
			return (ObjectCondition) ((NotExistsCondition) c)
					.getNestedConditions().get(0);
		else
			return null;
	}


	protected void rearrangeConditions(final Condition[] conds) {
		final BindingAddressesTable bat = computeBindingAddressTable(conds);
		for (int i = 0; i < conds.length; i++) {
			final Condition c = conds[i];
			if (isQuantorCondition(c))
				for (final Constraint constr : getObjectCondition(c).getConstraints()) {
					if (!(constr instanceof BoundConstraint))
						continue;
					final BoundConstraint bc = (BoundConstraint) constr;
					final BindingAddress pivot = bat.getPivot(bc
							.getConstraintName());
					if (pivot.tupleIndex > conditionIndexToTupleIndex(i,
							conds.length)) {
						for (int j = i; j > 0; j--)
							conds[j] = conds[j - 1];
						conds[0] = c;
						rearrangeConditions(conds);
						return;
					}
				}
		}
	}

	private class ConditionComparator implements Comparator<Condition> {

		public int compare(final Condition o1, final Condition o2) {
			return o1.getComplexity() - o2.getComplexity();
		}

	}

	protected Node compileJoins(final Rule rule) throws AssertException,
			RuleException {
		// take the last node from each condition and connect them by joins
		// regarding the complexity
		List<Condition> objectConditions = new ArrayList<Condition>();
		for (Condition c: rule.getConditions()) {
			if (c instanceof ObjectCondition)
				objectConditions.add(c);
			if (c instanceof ExistsCondition)
				objectConditions.add(c);
			if (c instanceof NotExistsCondition)
				objectConditions.add(c);

		}
		
		Condition[] sortedConds = new Condition[objectConditions.size()];
		sortedConds = objectConditions.toArray(sortedConds);
		Arrays.sort(sortedConds, new ConditionComparator());

		//rearrangeConditions(sortedConds);

		final HashMap<Condition, Node> conditionJoiners = new HashMap<Condition, Node>();
		final Node initFactNode = getObjectTypeNode(engine.getInitialTemplate());

		Node mostBottomNode = null;

		Node fromBottom = null;
		for (int i = 0; i < sortedConds.length; i++) {

			final Condition c = sortedConds[i];

			TwoInputNode newBeta = null;

			if (c instanceof ObjectCondition)
				newBeta = new SimpleBetaFilterNode(net.nextNodeId(), engine
						.getWorkingMemory(), net);
			else if (c instanceof NotExistsCondition)
				newBeta = new QuantorBetaFilterNode(net.nextNodeId(), engine
						.getWorkingMemory(), net, true);
			else if (c instanceof ExistsCondition)
				newBeta = new QuantorBetaFilterNode(net.nextNodeId(), engine
						.getWorkingMemory(), net, false);

			if (fromBottom == null)
				mostBottomNode = newBeta;
			else
				try {
					newBeta.addChild(fromBottom);
				} catch (final NodeException e) {
					engine.writeMessage(e.getMessage());
				}

			fromBottom = newBeta;

			Node cLastNode = lastNodes.get(c);
			if (cLastNode == null)
				cLastNode = getObjectTypeNode(engine.getInitialTemplate());

			try {
				cLastNode.addChild(newBeta);
			} catch (final NodeException e) {
				engine.writeMessage(e.getMessage());
			}

			conditionJoiners.put(c, newBeta);

		}

		if (fromBottom != null)
			try {
				initFactNode.addChild(fromBottom);
			} catch (final NodeException e) {
				engine.writeMessage(e.getMessage());
			}

		if (mostBottomNode == null)
			mostBottomNode = lastNodes.get(sortedConds[0]);

		final Node ultimateMostBottomNode = compileBindings(rule, sortedConds,
				conditionJoiners, mostBottomNode);

		return ultimateMostBottomNode;
	}

	public int conditionIndexToTupleIndex(final int cond, final int condCount) {
		return condCount - cond;
	}

	public int tupleIndexToConditionIndex(final int tupleInd,
			final int condCount) {
		return condCount - tupleInd;
	}

	protected BindingAddressesTable computeBindingAddressTable(
			final Condition[] conds) {
		final BindingAddressesTable bindingAddressTable = new BindingAddressesTable();
		// Iterate of all conditions and constraints
		for (int i = 0; i < conds.length; i++)
			// only for Object Conditions:
			if (conds[i] instanceof ObjectCondition
					|| conds[i] instanceof NotExistsCondition
					|| conds[i] instanceof ExistsCondition) {
				ObjectCondition oc = null;
				if (conds[i] instanceof ObjectCondition)
					oc = (ObjectCondition) conds[i];
				else if (conds[i] instanceof NotExistsCondition)
					oc = (ObjectCondition) ((NotExistsCondition) conds[i])
							.getNestedConditions().get(0);
				else if (conds[i] instanceof ExistsCondition)
					oc = (ObjectCondition) ((ExistsCondition) conds[i])
							.getNestedConditions().get(0);

				for (final Constraint c : oc.getConstraints())
					// if we found a BoundConstraint
					if (c instanceof BoundConstraint) {
						final BoundConstraint bc = (BoundConstraint) c;
						bindingEntry(conds, bindingAddressTable, i, bc);
					} else if (c instanceof OrderedFactConstraint) {
						final OrderedFactConstraint ofc = (OrderedFactConstraint) c;
						for (int pos = 0; pos < ofc.getConstraints().length; pos++) {
							final Constraint co = ofc.getConstraints()[pos];
							if (co instanceof BoundConstraint) {
								final BoundConstraint bco = (BoundConstraint) co;
								bindingEntry(conds, bindingAddressTable, i,
										pos, bco);
							}
						}
					}
			}
		return bindingAddressTable;
	}

	private TemplateSlot getSlot(final String templName, final String slotName) {
		final Template t = engine.findTemplate(templName);
		return t.getSlot(slotName);
	}

	private void bindingEntry(final Condition[] conds,
			final BindingAddressesTable bindingAddressTable, final int i,
			final BoundConstraint bc) {
		BindingAddress ba = null;
		final int bcOperator = bc.isNegated() ? Constants.NOTEQUAL
				: Constants.EQUAL;
		if (bc.isFactBinding())
			ba = new BindingAddress(
					conditionIndexToTupleIndex(i, conds.length), -1, bcOperator);
		else
			try {
				final String slotName = bc.getSlotName();
				final Condition c = bc.getParentCondition();
				assert c instanceof ObjectCondition;
				final ObjectCondition oc = (ObjectCondition) c;
				final String templName = oc.getTemplateName();
				final TemplateSlot bcSlot = getSlot(templName, slotName);
				ba = new BindingAddress(conditionIndexToTupleIndex(i,
						conds.length), bcSlot.getId(), bcOperator);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		ba.canBePivot = !(conds[i] instanceof ExistsCondition || conds[i] instanceof NotExistsCondition);
		bindingAddressTable.addBindingAddress(ba, bc.getConstraintName());
	}

	private void bindingEntry(final Condition[] conds,
			final BindingAddressesTable bat, final int i, final int po,
			final BoundConstraint bc) {
		BindingAddress ba;
		final int bcOperator = bc.isNegated() ? Constants.NOTEQUAL
				: Constants.EQUAL;
		if (!bc.isFactBinding()) {
			ba = new OrderedFactBindingAddress(conditionIndexToTupleIndex(i,
					conds.length), 0, po, bcOperator);
			ba.canBePivot = !(conds[i] instanceof ExistsCondition || conds[i] instanceof NotExistsCondition);
			bat.addBindingAddress(ba, bc.getConstraintName());
		}
	}

	protected Node compileBindings(final Rule rule, final Condition[] conds,
			final Map<Condition, Node> conditionJoiners, Node mostBottomNode)
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

		final BindingAddressesTable bindingAddressTable = computeBindingAddressTable(conds);

		// create bindings for actions:
		for (final String variable : bindingAddressTable.row.keySet()) {
			final BindingAddress pivot = bindingAddressTable.getPivot(variable);
			final Binding b = new Binding();
			b.setLeftIndex(pivot.slotIndex);
			if (b.getLeftIndex() == -1)
				b.setIsObjectVar(true);
			b.setLeftRow(pivot.tupleIndex);
			b.setVarName(variable);
			addRuleBinding(rule, b);
		}

		// get prebindings from table:
		final Vector<PreBinding> preBindings = bindingAddressTable
				.getPreBindings();

		final Iterator<PreBinding> itr = preBindings.iterator();
		PreBinding act = null;
		if (itr.hasNext())
			act = itr.next();
		// traverse conditions and get their join node:
		for (int i = conds.length - 1; i >= 0; i--) {
			final ArrayList<JoinFilter> filters = new ArrayList<JoinFilter>();
			final Node conditionJoiner = conditionJoiners.get(conds[i]);
			// traverse prebindings and try to set them to join nodes:
			while (act != null
					&& act.getCorrectJoinTupleIndex() == conditionIndexToTupleIndex(
							i, conds.length)) {

				final LeftFieldAddress left = new LeftFieldAddress(Math.min(
						act.leftIndex, act.rightIndex), act.leftSlot);
				final RightFieldAddress right = new RightFieldAddress(
						act.rightSlot, act.rightPosition);
				final FieldComparator b = new FieldComparator(act.varName,
						left, act.operator, right);
				filters.add(b);
				act = itr.hasNext() ? itr.next() : null;
			}

			// set bindig= null if binds.size=0
			if (filters.size() > 0)
				((SimpleBetaFilterNode) conditionJoiner).setFilter(filters);
		}
		// handle all bindings that couldn't be placed to join node.
		while (act != null) {

			final Condition c = conds[tupleIndexToConditionIndex(act.leftIndex,
					conds.length)];
			if (!(c instanceof ObjectCondition))
				continue;
			final ObjectCondition objectC = (ObjectCondition) c;
			final Template template = engine.findTemplate(objectC
					.getTemplateName());
			final ObjectTypeNode otn = getObjectTypeNode(template);

			final SimpleBetaFilterNode newJoin = new SimpleBetaFilterNode(net
					.nextNodeId(), engine.getWorkingMemory(), engine.getNet());

			try {
				mostBottomNode.addChild(newJoin);
				otn.addChild(newJoin);
			} catch (final NodeException e) {
				engine.writeMessage(e.getMessage());
			}

			mostBottomNode = newJoin;

			JoinFilter filter;

			final LeftFieldAddress left = new LeftFieldAddress(act.leftIndex,
					act.leftSlot);
			final RightFieldAddress right = new RightFieldAddress(act.rightSlot);
			filter = new FieldComparator(act.varName, left, act.operator, right);

			newJoin.addFilter(filter);

			act = itr.hasNext() ? itr.next() : null;
		}
		try {
			compileTestConditions(conds, rule, bindingAddressTable,
					conditionJoiners);
		} catch (final JoinFilterException e) {
			engine.writeMessage(e.getMessage());
		}
		compilePredicateConstraints(rule, conds, conditionJoiners,
				bindingAddressTable);
		return mostBottomNode;

	}

	protected void compilePredicateConstraints(final Rule rule,
			final Condition[] conds,
			final Map<Condition, Node> conditionJoiners,
			final BindingAddressesTable bindingAddressTable)
			throws AssertException {
		// search for predicate constraints and build filters for them
		for (final Condition c : rule.getConditions())
			if (c instanceof ObjectCondition) {
				final ObjectCondition objc = (ObjectCondition) c;
				for (final Constraint constr : objc.getConstraints())
					if (constr instanceof PredicateConstraint) {
						final PredicateConstraint pcon = (PredicateConstraint) constr;
						final List<Parameter> paramsOriginal = pcon
								.getParameters();
						final List<Parameter> params = new ArrayList<Parameter>();
						for (final Parameter p : paramsOriginal)
							params.add(p);
						// determine good row index for our test
						int validRowIndex = 1;
						while (!params.isEmpty()) {
							final Parameter p = params
									.remove(params.size() - 1);
							if (p instanceof Signature) {
								final Signature sig = (Signature) p;
								for (final Parameter pnew : sig.getParameters())
									params.add(pnew);
							} else if (p instanceof BoundParam) {
								final BoundParam bp = (BoundParam) p;
								final BindingAddress pivot = bindingAddressTable
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

						final SimpleBetaFilterNode validNode = (SimpleBetaFilterNode) conditionJoiners
								.get(conds[tupleIndexToConditionIndex(
										validRowIndex, conds.length)]);
						final Parameter[] functionParams = recalculateParameters(
								conds.length, pcon.getParameters(),
								bindingAddressTable,
								tupleIndexToConditionIndex(validRowIndex,
										conds.length));
						Function function = null;
						try {
							function = engine.getFunctionMemory().findFunction(
									pcon.getFunctionName());
						} catch (final FunctionNotFoundException e1) {
							Logging.logger(this.getClass()).fatal(e1);
						}
						try {
							final FunctionEvaluator filter = new FunctionEvaluator(
									engine, function, functionParams);
							validNode.addFilter(filter);
						} catch (final JoinFilterException e) {
							engine.writeMessage(e.getMessage());
						}
					}
			}
	}

	protected Parameter[] recalculateParameters(final int conditionsCount,
			final List<Parameter> params,
			final BindingAddressesTable bindingAddressTable,
			final int conditionIndex) {
		final Parameter[] paramsArr = new Parameter[params.size()];
		params.toArray(paramsArr);
		return recalculateParameters(conditionsCount, paramsArr,
				bindingAddressTable, conditionIndex);
	}

	protected Parameter[] recalculateParameters(final int conditionsCount,
			final Signature s, final BindingAddressesTable bindingAddressTable,
			final int conditionIndex) {
		return recalculateParameters(conditionsCount, s.getParameters(),
				bindingAddressTable, conditionIndex);
	}

	protected Parameter[] recalculateParameters(final int conditionsCount,
			final Parameter[] params,
			final BindingAddressesTable bindingAddressTable,
			final int conditionIndex) {
		final List<Parameter> result = new ArrayList<Parameter>();
		for (final Parameter p : params)
			if (p instanceof BoundParam) {
				final BoundParam bp = (BoundParam) p;
				final BindingAddress pivot = bindingAddressTable.getPivot(bp
						.getVariableName());

				FieldAddress addr = null;
				if (pivot.tupleIndex == conditionIndexToTupleIndex(
						conditionIndex, conditionsCount)
						&& conditionIndex < conditionsCount) {
					if (pivot.slotIndex == -1)
						addr = new RightFieldAddress();
					else
						addr = new RightFieldAddress(pivot.slotIndex);
				} else if (pivot.slotIndex == -1)
					addr = new LeftFieldAddress(pivot.tupleIndex);
				else
					addr = new LeftFieldAddress(pivot.tupleIndex,
							pivot.slotIndex);

				result.add(addr);

			} else if (p instanceof Signature) {
				final Signature nested = (Signature) p;
				result.add(nested);
				nested.setParameters(recalculateParameters(conditionsCount,
						nested, bindingAddressTable, conditionIndex));

			} else
				result.add(p);

		final Parameter[] arr = new Parameter[0];
		return result.toArray(arr);
	}

	// TODO: fix indices here
	protected void compileTestConditions(final Condition[] objectConditions,
			final Rule rule, final BindingAddressesTable bindingAddressTable,
			final Map<Condition, Node> conditionJoiners)
			throws JoinFilterException {
		for (final Condition c : rule.getConditions())
			if (c instanceof TestCondition) {
				final TestCondition tc = (TestCondition) c;
				final List<BoundParam> boundParams = tc.getFunction()
						.getBoundParameters();

				// determine good row index for our test
				int validRowIndex = 1;
				for (final BoundParam p : boundParams) {
					final BindingAddress pivot = bindingAddressTable.getPivot(p
							.getVariableName());
					if (pivot == null)
						throw new JoinFilterException(
								"Error in TestCondition: Variable "
										+ p.getVariableName()
										+ " is not defined");
					validRowIndex = Math.max(pivot.tupleIndex, validRowIndex);
				}

				// determine corresponding node
				final SimpleBetaFilterNode validNode = (SimpleBetaFilterNode) conditionJoiners
						.get(objectConditions[tupleIndexToConditionIndex(
								validRowIndex, objectConditions.length)]);

				final Parameter[] functionParams = recalculateParameters(
						objectConditions.length, tc.getFunction(),
						bindingAddressTable, tupleIndexToConditionIndex(
								validRowIndex, objectConditions.length));
				FunctionEvaluator testFilter = null;
				try {
					testFilter = new FunctionEvaluator(engine, tc.getFunction()
							.lookUpFunction(engine), functionParams);
				} catch (final FunctionNotFoundException e) {
					Logging.logger(this.getClass()).fatal(e);
				}
				validNode.addFilter(testFilter);
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
	public Node compile(final ObjectCondition condition, final Rule rule,
			final int conditionIndex) throws EvaluationException,
			StopCompileException {
		// get activated ObjectType Node:
		final Template template = engine.findTemplate(condition
				.getTemplateName());
		ObjectTypeNode otn = null;
		try {
			otn = getObjectTypeNode(template);

			// add otn to condition:
			lastNodes.put(condition, otn);

			Node prev = otn;
			OneInputNode current = null;

			if (otn != null)
				for (final Constraint constraint : condition.getConstraints()) {

					if (constraint instanceof OrderedFactConstraint) {

						final OrderedFactConstraint ofc = (OrderedFactConstraint) constraint;

						final Constraint[] content = ofc.getConstraints();

						for (final Constraint c : content) {
							current = prepareConstraintCompile(condition, rule,
									conditionIndex, template, prev, c);
							prev = current;
						}

						current = prepareConstraintCompile(condition, rule,
								conditionIndex, template, prev, constraint);
						// TODO: This check is just a preliminary fix. There
						// might be something better.
						if (current != null)
							prev = current;

					}

					current = prepareConstraintCompile(condition, rule,
							conditionIndex, template, prev, constraint);
					// TODO: This check is just a preliminary fix. There might
					// be something better.
					if (current != null)
						prev = current;
				}
			return current;
		} catch (final AssertException e1) {
			engine.writeMessage("ERROR: " + e1.getMessage());
			return null;
		}
	}

	private OneInputNode prepareConstraintCompile(
			final ObjectCondition condition, final Rule rule,
			final int conditionIndex, final Template template, final Node prev,
			final Constraint constraint) throws EvaluationException,
			StopCompileException {
		OneInputNode current;
		// TODO remove lines TemplateSlot slot;
		// slot = template.getSlot(constraint.get());
		// constraint.setSlot(slot);
		current = (OneInputNode) constraint.compile(this, rule, conditionIndex);

		// we add the node to the previous
		if (current != null) {
			try {
				prev.addChild(current);
			} catch (final NodeException e) {
				engine.writeMessage(e.getMessage());
			}
			lastNodes.put(condition, current);
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
	public Node compile(final ExistsCondition condition, final Rule rule,
			final int conditionIndex) {
		final Object o = condition.getNestedConditions().get(0);
		final AbstractCondition nested = (AbstractCondition) o;
		try {
			Node resultNode =  nested.compile(this, rule, conditionIndex);
			lastNodes.put(condition, resultNode);
			return resultNode;
		} catch (final Exception e) {
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
	public Node compile(final TestCondition condition, final Rule rule,
			final int conditionIndex) {
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
	public Node compile(final AndCondition condition, final Rule rule,
			final int conditionIndex) {
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
	public Node compile(final NotExistsCondition condition, final Rule rule,
			final int conditionIndex) {
		final Object o = condition.getNestedConditions().get(0);
		final AbstractCondition nested = (AbstractCondition) o;
		try {
			Node resultNode =  nested.compile(this, rule, conditionIndex);
			lastNodes.put(condition, resultNode);
			return resultNode;
		} catch (final Exception e) {
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
	public Node compile(final OrCondition condition, final Rule rule,
			final int conditionIndex) throws StopCompileException,
			AssertException {
		// now, we will split our rule in more different rules
		try {
			int counter = 1;
			boolean success = true;
			for (final Object nested : condition.getNestedConditions()) {
				final Condition nestedCE = (Condition) nested;
				Rule newRule = null;
				try {
					newRule = ((Defrule) rule).clone(engine);
				} catch (final CloneNotSupportedException e) {
					engine.writeMessage(e.getMessage());
				}
				newRule.getConditions().set(conditionIndex, nestedCE);

				final org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler compiler = new org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler(
						engine, root, net);

				newRule.setName(newRule.getName() + "-" + counter++);
				success = success && compiler.addRule(newRule);

			}
			throw new StopCompileException(success);
		} catch (final EvaluationException e) {
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
	public Node compile(final PredicateConstraint constraint, final Rule rule,
			final int conditionIndex) {
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
	public Node compile(final OrConnectedConstraint constraint,
			final Rule rule, final int conditionIndex)
			throws StopCompileException {
		// now, we will split our rule in more different rules
		int counter = 1;

		boolean success = true;

		final Constraint[] nestedConstraints = { constraint.getLeft(),
				constraint.getRight() };

		for (final Constraint nested : nestedConstraints) {
			// TODO remove line nested.setName(constraint.getName());

			Rule newRule = null;
			try {
				newRule = ((Defrule) rule).clone(engine);
			} catch (final CloneNotSupportedException e) {
				engine.writeMessage(e.getMessage());
			}

			// search for position of the or constraint
			final Stack<Condition> conditionStack = new Stack<Condition>();
			for (final Condition c : newRule.getConditions())
				conditionStack.push(c);

			boolean replaced = false;
			constraintSearchLoop: while (!conditionStack.isEmpty()) {
				final Condition c = conditionStack.pop();
				if (c instanceof ObjectCondition) {
					final ObjectCondition objc = (ObjectCondition) c;
					for (int i = 0; i < objc.getConstraints().size(); i++) {
						final Constraint constr = objc.getConstraints().get(i);
						if (constr == constraint) {
							// we've found our constraint ;)
							objc.getConstraints().set(i, nested);
							replaced = true;
							break constraintSearchLoop;
						}
					}
				}
				if (c instanceof ConditionWithNested) {
					final ConditionWithNested cwn = (ConditionWithNested) c;
					for (final Condition c2 : cwn.getNestedConditions())
						conditionStack.add(c2);
				}
			}

			if (!replaced)
				engine
						.writeMessage("FATAL: could not compile or connected constraint");

			final org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler compiler = new org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler(
					engine, root, net);

			newRule.setName(newRule.getName() + "-" + counter++);
			try {
				success = success && compiler.addRule(newRule);
			} catch (final EvaluationException e) {
				engine.writeMessage("FATAL: could not insert rule"
						+ e.getMessage());
			}
		}
		throw new StopCompileException(success);
	}

	private TemplateSlot getSlot(final LiteralConstraint lc) {
		final Condition c = lc.getParentCondition();
		assert c instanceof ObjectCondition;
		final ObjectCondition oc = (ObjectCondition) c;
		final String templName = oc.getTemplateName();
		return getSlot(templName, lc.getSlotName());
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
	 * @throws EvaluationException
	 */
	public Node compile(final LiteralConstraint constraint, final Rule rule,
			final int conditionIndex) throws EvaluationException {
		OneInputNode node = null;
		if (getSlot(constraint) == null)
			throw new EvaluationException("The Slot "
					+ constraint.getSlotName() + " doesnt exist.");
		final Slot sl = (Slot) getSlot(constraint).clone();
		JamochaValue sval;
		try {
			sval = constraint.getValue().implicitCast(sl.getValueType());
		} catch (final IllegalConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		try {
			sl.setValue(sval);
		} catch (final ConstraintViolationException e) {
			engine.writeMessage(e.getMessage());
		}
		final int op = constraint.isNegated() ? Constants.NOTEQUAL
				: Constants.EQUAL;
		node = new SlotFilterNode(net.nextNodeId(), engine.getWorkingMemory(),
				op, sl, net);

		return node;
	}

	public Node compile(final OrderedFactConstraint constraint,
			final Rule rule, final int conditionIndex) {
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
	public Node compile(final BoundConstraint constraint, final Rule rule,
			final int conditionIndex) {

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

	public Node compile(final AndConnectedConstraint constraint,
			final Rule rule, final int conditionIndex)
			throws StopCompileException {
		boolean success = false;
		try {
			// TODO remove that lines
			// constraint.getLeft().setConstraintName(constraint.getConstraintName());
			// constraint.getRight().setConstraintName(constraint.getConstraintName());
			// search for position of the and constraint
			final Stack<Condition> conditionStack = new Stack<Condition>();
			for (final Condition c : rule.getConditions())
				conditionStack.push(c);
			boolean replaced = false;
			constraintSearchLoop: while (!conditionStack.isEmpty()) {
				final Condition c = conditionStack.pop();
				if (c instanceof ObjectCondition) {
					final ObjectCondition objc = (ObjectCondition) c;
					for (int i = 0; i < objc.getConstraints().size(); i++) {
						final Constraint constr = objc.getConstraints().get(i);
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
					final ConditionWithNested cwn = (ConditionWithNested) c;
					for (final Condition c2 : cwn.getNestedConditions())
						conditionStack.add(c2);
				}
			}

			if (!replaced)
				engine
						.writeMessage("FATAL: could not compile or connected constraint");

			final org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler compiler = new org.jamocha.engine.rules.rulecompiler.sfp.SFRuleCompiler(
					engine, root, net);
			success = compiler.addRule(rule);
		} catch (final EvaluationException e) {
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
	// protected void notifyListener(CompileEvent event) {
	// Iterator itr = this.listener.iterator();
	// // engine.writeMessage(event.getMessage());
	// while (itr.hasNext()) {
	// CompilerListener listen = (CompilerListener) itr.next();
	// int etype = event.getEventType();
	// if (etype == CompileEvent.ADD_RULE_EVENT) {
	// listen.ruleAdded(event);
	// } else if (etype == CompileEvent.REMOVE_RULE_EVENT) {
	// listen.ruleRemoved(event);
	// } else {
	// listen.compileError(event);
	// }
	// }
	// }
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
	protected void compileActions(final Rule rule) {
		// for (Action action : rule.getActions()) {
		// if (action instanceof FunctionAction) {
		// //FunctionAction fa = (FunctionAction) action;
		// //try {
		// //TODO remove it fa.configure(this.engine, rule);
		// //} catch (EvaluationException e) {
		// // e.printStackTrace();
		// //}
		// } else {
		// // do something else
		// }
		// }
	}

	public TerminalNode getTerminalNode(final Rule rule) {
		return terminals.get(rule);
	}

	public Binding getBinding(final String varName, final Rule r) {
		for (final Binding b : getRuleBindings(r))
			if (b.getVarName().equals(varName))
				return b;
		return null;
	}
}
