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
package org.jamocha.rete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.jamocha.logging.DefaultLogger;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalConversionException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.nodes.AlphaNode;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.BetaNode;
import org.jamocha.rete.nodes.ObjectTypeNode;
import org.jamocha.rete.nodes.RootNode;
import org.jamocha.rete.nodes.SlotAlpha;
import org.jamocha.rete.nodes.TerminalNode;
import org.jamocha.rule.Action;
import org.jamocha.rule.Analysis;
import org.jamocha.rule.AndCondition;
import org.jamocha.rule.AndLiteralConstraint;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.ExistCondition;
import org.jamocha.rule.FunctionAction;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.NotCondition;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.OrCondition;
import org.jamocha.rule.OrLiteralConstraint;
import org.jamocha.rule.PredicateConstraint;
import org.jamocha.rule.Rule;
import org.jamocha.rule.Summary;
import org.jamocha.rule.TemplateValidation;
import org.jamocha.rule.TestCondition;

/**
 * @author Josef Alexander Hahn
 * @author Karl-Heinz Krempels
 * @author Peter Lin
 * @author Sebastian Reinartz
 * 
 */


//SOLVED simple literal constraints
//SOLVED boundconstraints in ObjectCondition
//SOLVED multi-occuring boundconstraints in one ObjectCondition
//SOLVED negated boundconstraints in ObjectCondition
//TODO what should happen when a bc only occurs in negated manner?
//TODO multifield stuff
//TODO predicate constraints
//TODO and / or / not CEs
//TODO forall / exists CEs
//TODO test CEs
//TODO finish this todo list ;)
public class SFRuleCompiler implements RuleCompiler {

	private class BindingAddress implements Comparable<BindingAddress> {
		public int conditionIndex;

		public int slotIndex;

		public int operator;

		public BindingAddress(int conditionIndex, int slotIndex, int operator) {
			super();
			this.conditionIndex = conditionIndex;
			this.slotIndex = slotIndex;
			this.operator = operator;
		}

		public int compareTo(BindingAddress o) {
			int conditionDifference = this.conditionIndex - o.conditionIndex;

			if (conditionDifference != 0)
				return conditionDifference;

			return this.slotIndex - o.slotIndex;
		}

	}

	private class PreBinding implements Comparable<PreBinding> {
		public int leftCondition;

		public int rightCondition;

		public int leftSlot;

		public int rightSlot;

		public int operator;

		public String varName;

		public String toString() {
			StringBuffer result = new StringBuffer();

			result.append("Prebinding: (");
			result.append(leftCondition);
			result.append(",");
			result.append(leftSlot);
			result.append(")");
			result.append(ConversionUtils.getOperatorDescription(operator));
			result.append("(");
			result.append(rightCondition);
			result.append(",");
			result.append(rightSlot);
			result.append(")");
			return result.toString();
		}

		public PreBinding(BindingAddress left, BindingAddress right, String varName) {
			super();
			this.varName = varName;
			this.leftCondition = left.conditionIndex;
			this.rightCondition = right.conditionIndex;
			this.leftSlot = left.slotIndex;
			this.rightSlot = right.slotIndex;
			if (left.operator == Constants.EQUAL) {
				this.operator = right.operator;
			} else if (right.operator == Constants.EQUAL) {
				this.operator = left.operator;
			} else {
				this.operator = Constants.NILL;
			}
		}

		public int getJoinIndex() {
			if (leftCondition == rightCondition)
				return -1;
			return Math.min(leftCondition, rightCondition);
		}

		public int compareTo(PreBinding o) {
			return this.getJoinIndex() - o.getJoinIndex();
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
		
		private BindingAddress getPivot(String variable, Vector<BindingAddress> bas) {
			BindingAddress pivot = null;
			for (BindingAddress ba : bas) {
				if (ba.operator == Constants.EQUAL) {
					if (pivot == null || pivot.compareTo(ba) < 0) {
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
			Collections.reverse(result);
			return result;
		}
	}

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	private Rete engine = null;

	protected RootNode root = null;

	private Module currentMod = null;

	private ArrayList<CompilerListener> listener = new ArrayList<CompilerListener>();

	protected boolean validate = true;

	protected TemplateValidation tval = null;

	public static final String FUNCTION_NOT_FOUND = Messages.getString("CompilerProperties.function.not.found"); //$NON-NLS-1$

	public static final String INVALID_FUNCTION = Messages.getString("CompilerProperties.invalid.function"); //$NON-NLS-1$

	public static final String ASSERT_ON_PROPOGATE = Messages.getString("CompilerProperties.assert.on.add"); //$NON-NLS-1$

	protected DefaultLogger log = new DefaultLogger(this.getClass());

	public SFRuleCompiler(Rete engine, RootNode root) {
		super();
		this.engine = engine;
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
			currentMod = engine.findModule(modName);
			if (currentMod == null) {
				engine.addModule(modName, false);
				currentMod = engine.findModule(modName);
			}
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
		TerminalNode node = new TerminalNode(engine.nextNodeId(), rule);
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
	public void removeObjectTypeNode(ObjectTypeNode node) throws RetractException {
		// TODO: check here if a destroy is needed, I think not. deactivate
		// might be enough. (SR)

		root.removeObjectTypeNode(node);
		node.clear();
		node.destroy(engine);
	}

	/**
	 * The method gets the ObjectTypeNode from the HashMap and returns it. If
	 * the node does not exist, the method will return null.
	 * 
	 * @param Template
	 * @return ObjectTypeNode
	 */
	public ObjectTypeNode getObjectTypeNode(Template template) {
		
		//TODO ObjectTypeNodes shouldnt become generated in deftemplate but only when a rule needs it
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

	public boolean addRule(Rule rule) throws AssertException {
		boolean result = false;
		rule.resolveTemplates(engine);
		if (!this.validate || (this.validate && this.tval.analyze(rule) == Analysis.VALIDATION_PASSED)) {
			if (rule.getConditions() != null) {
				// we check the name of the rule to see if it is for a specific
				// module. if it is, we have to add it to that module
				this.setModule(rule);
				// creates a Terminal nod for this rule. this node will be added
				// to tho rule:
				TerminalNode tnode = createTerminalNode(rule);
				// has conditions:
				if (rule.getConditions().length > 0) {

					Condition[] conds = rule.getConditions();
					for (int i = 0; i < conds.length; i++)
						conds[i].compile(this, rule, i);

					compileJoins(rule);
					// has no conditions:
				} else if (rule.getConditions().length == 0) {
					// the rule has no LHS, this means it only has actions
					BaseNode initFactNode = root.activateObjectTypeNode(engine.initFact, engine);
					initFactNode.addNode(tnode, engine);
				}
				compileActions(rule);

				currentMod.addRule(rule);

				CompileEvent ce = new CompileEvent(rule, CompileEvent.ADD_RULE_EVENT);

				ce.setRule(rule);

				this.notifyListener(ce);
				engine.newRuleEvent(rule);
				result = true;
			}
		} else {
			// we print out a message and say that the rule is not valid
			Summary error = this.tval.getErrors();
			engine.writeMessage("Rule " + rule.getName() + " was not added. ", Constants.DEFAULT_OUTPUT); //$NON-NLS-1$ //$NON-NLS-2$
			engine.writeMessage(error.getMessage(), Constants.DEFAULT_OUTPUT);
			Summary warn = this.tval.getWarnings();
			engine.writeMessage(warn.getMessage(), Constants.DEFAULT_OUTPUT);
		}
		return result;
	}

	protected void compileJoins(Rule rule) throws AssertException {
		// take the last node from each condition and connect them by joins
		// regarding the complexity
		TerminalNode terminal = rule.getTerminalNode();
		Condition[] sortedConds = rule.getConditions().clone();
		Arrays.sort(sortedConds);
		
		BaseNode mostBottomNode = null;

		BaseNode fromBottom = null;

        // Note: here is a tip on compiling the joins. Using a row+column approach,
        // it's important to take into account Not and Exist nodes. This is because
        // Not and Exist do not propogate any additional facts. In order to build
        // the joins correctly from the terminal node up, you have to know how
        // many Not and Exist nodes are above it. Peter 5/16/07
        
		//TODO we can do that more efficient with a simple array
		HashMap<Condition, BaseNode> conditionJoiners = new HashMap<Condition, BaseNode>();

		for (int i = 0; i < sortedConds.length; i++) {
			Condition c = sortedConds[i];
			// c now is the next condition with the lowest complexity
			// now, check whether we have to create a new join
			boolean createNewJoin = (i < sortedConds.length - 1);

			if (createNewJoin) {
				// creat join add old bottom node, set join to new bottom node
				BetaNode betaNode = new BetaNode(engine.nextNodeId());
				if (fromBottom != null) {
					betaNode.addNode(fromBottom, engine);
				} else {
					mostBottomNode = betaNode;
				}
				fromBottom = betaNode;
			}
			conditionJoiners.put(c, fromBottom);
			BaseNode lastNode = c.getLastNode();

			if (lastNode != null){
				if (fromBottom != null) {
					(lastNode).addNode(fromBottom, engine);
				} else {
					mostBottomNode = lastNode;
				}
			}
		}
		
		if (mostBottomNode == null) mostBottomNode = sortedConds[0].getLastNode();
		
		BaseNode ultimateMostBottomNode = compileBindings(rule, sortedConds, conditionJoiners, mostBottomNode);
		ultimateMostBottomNode.addNode(terminal, engine);
	}

	protected BaseNode compileBindings(Rule rule, Condition[] conds, Map<Condition, BaseNode> conditionJoiners, BaseNode mostBottomNode) throws AssertException {
		// first, we need such a table (since each condition can contain many
		// constraints, there are more than one operator-sign per cell)
		/*
		 * CONDITION1 CONDITION2 CONDITION3 CONDITION4 CONDITION5 VARIABLE1 != != == == !=
		 * [==] VARIABLE2 == [==] VARIABLE3 != >= [==]
         * 
         * Note:  here's some comments and general thoughts. A binding used in a join
         * should normally use "==" and "!=". if it's some other operator, a TestNode
         * is used to evaluate it. A condition like predicateConstraint
         * (age ?age&:(> ?age 20) ) creates a binding and an alpha node that uses 
         * ">" operator. Peter 5/24/2007
         * Josef had a good question. The reason most people don't use numeric operators
         * in JESS and clips for joins isn't technical. A join node can use numeric
         * operators, it's just that people generally don't with CLIPS language. For example
         * the pattern would look like this
         * (age ?age2&:(> ?age2 ?age1) )
         * Peter 5/26/07
		 */

		// create the table:
		BindingAddressesTable bindingAddressTable = new BindingAddressesTable();

		// Iterate of all conditions and constraints
		for (int i = 0; i < conds.length; i++) {
			for (Constraint c : conds[i].getConstraints()) {

				// if we found a BoundConstraint
				if (c instanceof BoundConstraint){
					BoundConstraint bc = (BoundConstraint) c;
					BindingAddress ba;
					if (bc.getIsObjectBinding()) {
						ba = new BindingAddress(i, -1, bc.getOperator());
					} else {
						ba = new BindingAddress(i, bc.getSlot().getId(), bc.getOperator());
					}
					bindingAddressTable.addBindingAddress(ba, bc.getVariableName());
				}
				
				
			}
		}
		
				
		// create bindings for actions:
		for (String variable : bindingAddressTable.row.keySet()) {
			BindingAddress pivot = bindingAddressTable.getPivot(variable);
			
			Binding b = new Binding();
			b.leftIndex = pivot.slotIndex;
			b.leftrow = conds.length -1 - pivot.conditionIndex;
			b.varName = variable;
			rule.addBinding(variable, b);
		}
		
		// get prebindings from table:
		Vector<PreBinding> preBindings = bindingAddressTable.getPreBindings();
		Iterator<PreBinding> itr = preBindings.iterator();
		PreBinding act = null;
		if (itr.hasNext())
			act = itr.next();
		Binding[] bindArray = new Binding[0];
		// traverse conditions and get their join node:
		for (int i = conds.length - 2; i >= 0; i--) {
			Vector<Binding> binds = new Vector<Binding>();
			BaseNode node = conditionJoiners.get(conds[i]);
			// traverse prebindings and try to set them to join nodes:
			while (act != null && act.getJoinIndex() == i) {

				Binding b = new Binding(act.operator);
				b.leftIndex = act.leftSlot;
				b.leftrow = conds.length - 1 - Math.max(act.leftCondition, act.rightCondition);
				b.rightIndex = act.rightSlot;
				b.rightrow = -1;
				b.varName = act.varName;
				binds.add(b);

				act = (itr.hasNext()) ? itr.next() : null;
			}
			
			// set bindig= null if binds.size=0
			((BetaNode) node).setBindings((binds.size() != 0) ? binds.toArray(bindArray) : null, engine);
		}
		// handle all bindings that couldn't be placed to join node.
		while (act != null) {
			
			Condition c = conds[act.leftCondition];
			if (!(c instanceof ObjectCondition)) continue;
			ObjectCondition objectC= (ObjectCondition) c;
			Template template = objectC.getTemplate();
			ObjectTypeNode otn = root.activateObjectTypeNode(template, engine);
			
			
			
			BetaNode newJoin = new BetaNode(engine.nextNodeId());
			
			mostBottomNode.addNode(newJoin, engine);
			otn.addNode(newJoin, engine);
			
			mostBottomNode = newJoin;
			
			Binding binds[] = new Binding [1];
			binds[0] = new Binding();
			binds[0].leftIndex= act.leftSlot;
			binds[0].leftrow=   conds.length -1 -act.leftCondition;
			binds[0].rightIndex=act.rightSlot;
			binds[0].rightrow=  act.rightCondition;
			binds[0].operator=  act.operator;
			binds[0].varName=   act.varName;
			
			newJoin.setBindings(binds, engine);
			
			act = (itr.hasNext()) ? itr.next() : null;
		}
		
		return mostBottomNode;

	}

	/**
	 * The method compiles an ObjectCondition.
	 * 
	 * @param condition
	 * @param conditionIndex
	 * @param rule
	 * 
	 * @return compileConditionState
	 */
	public BaseNode compile(ObjectCondition condition, Rule rule, int conditionIndex) {
		// get activated ObjectType Node:
		Template template = condition.getTemplate();
		ObjectTypeNode otn = null;
		try {
			otn = root.activateObjectTypeNode(template, engine);

			// add otn to condition:
			condition.addNode(otn);

			BaseNode prev = otn;
			SlotAlpha current = null;

						
			if (otn != null) {
				TemplateSlot slot;
				for (Constraint constraint : condition.getConstraints()) {
					
					slot = template.getSlot(constraint.getName());

					constraint.setSlot(slot);
					current = (SlotAlpha) constraint.compile(this, rule, conditionIndex);

					// we add the node to the previous
					if (current != null) {
						prev.addNode(current, engine);
						condition.addNode(current);
						// now set the previous to current
						prev = current;
					}
				}
			}
			return current;
		} catch (AssertException e1) {
			engine.writeMessage("ERROR: "+e1.getMessage());
			return null;
		}
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
	public BaseNode compile(ExistCondition condition, Rule rule, int conditionIndex) {

		// it seems to produce a loop ...
		if (condition.hasObjectCondition()) {
			ObjectCondition oc = (ObjectCondition) condition.getObjectCondition();
			return oc.compile(this, rule, conditionIndex);
		}

		return null;
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
	public BaseNode compile(TestCondition condition, Rule rule, int conditionIndex) {
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
	public BaseNode compile(AndCondition condition, Rule rule, int conditionIndex) {
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
	 */
	public BaseNode compile(NotCondition condition, Rule rule, int conditionIndex) {
		return null;
	}

	/**
	 * The method compiles an OrCondition.
	 * 
	 * @param condition
	 * @param conditionIndex
	 * @param rule
	 * 
	 * @return compileConditionState
	 */
	public BaseNode compile(OrCondition condition, Rule rule, int conditionIndex) {
		return null;
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
	public BaseNode compile(PredicateConstraint constraint, Rule rule, int conditionIndex) {
		SlotAlpha node = null;
		// for now we expect the user to write the predicate in this
		// way (> ?bind value), where the binding is first. this
		// needs to be updated so that we look at the order of the
		// parameters and set the node appropriately
		// we only create an AlphaNode if the predicate isn't
		// joining 2 bindings.
		if (!constraint.isPredicateJoin()) {
			if (ConversionUtils.isPredicateOperatorCode(constraint.getFunctionName())) {
				int oprCode = ConversionUtils.getOperatorCode(constraint.getFunctionName());
				Slot sl = (Slot) constraint.getSlot().clone();
				JamochaValue sval;
				try {
					sval = constraint.getValue().implicitCast(sl.getValueType());
				} catch (IllegalConversionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				sl.value = sval;
				// create the alphaNode

				// NoMemANodes are not supported anymore. so we create
				// AlphaNodes, no matter what rule.getRememberMatch() says
				node = new AlphaNode(engine.nextNodeId());

				node.setSlot(sl);
				node.setOperator(oprCode);
				// we increment the node use count when when create
				// a new AlphaNode for the LiteralConstraint
				constraint.getSlot().incrementNodeCount();
			} else {
				// the function isn't a built in predicate function
				// that
				// returns boolean true/false. We look up the
				// function
				Function f = engine.getFunctionMemory().findFunction(constraint.getFunctionName());
				if (f != null) {
					// we create the alphaNode if a function is
					// found and
					// the return type is either boolean primitive
					// or object
					if (f.getDescription().getReturnType().equals(JamochaType.BOOLEANS)) {
						// TODO - need to implement it
					} else {
						// the function doesn't return boolean, so
						// we have to notify
						// the listeners the condition is not valid
						CompileEvent ce = new CompileEvent(this, CompileEvent.FUNCTION_INVALID);
						ce.setMessage(INVALID_FUNCTION + " " + f.getDescription().getReturnType()); //$NON-NLS-1$
						this.notifyListener(ce);
					}
				} else {
					// we need to notify listeners the function
					// wasn't found
					CompileEvent ce = new CompileEvent(this, CompileEvent.FUNCTION_NOT_FOUND);
					ce.setMessage(FUNCTION_NOT_FOUND + " " + f.getDescription().getReturnType()); //$NON-NLS-1$
					this.notifyListener(ce);
				}
			}
		}
		Binding bind = new Binding();
		bind.setVarName(constraint.getVariableName());
		bind.setLeftRow(conditionIndex);
		bind.setLeftIndex(constraint.getSlot().getId());
		// bind.setRowDeclared(conditionIndex);
		// we only add the binding to the map if it doesn't already
		// exist
		if (rule.getBinding(constraint.getVariableName()) == null) {
			rule.addBinding(constraint.getVariableName(), bind);
		}
		return node;
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
	 * @throws Exception
	 */
	public BaseNode compile(OrLiteralConstraint constraint, Rule rule, int conditionIndex) {
		// SlotAlpha node = null;
		// Slot2 sl = new Slot2(constraint.getName());
		// sl.setId(constraint.getSlot().getId());
		// Object sval = constraint.getValue();
		// sl.setValue(sval);

		return null;

		// TODO later on, we should implement an AlphaNodeOr like node and
		// reimplement that:
		// node = new AlphaNodeOr(engine.nextNodeId());

		// node.setSlot(sl);
		// we increment the node use count when when create a
		// new
		// AlphaNode for the LiteralConstraint
		// constraint.getSlot().incrementNodeCount();

		// return node;
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
	public BaseNode compile(LiteralConstraint constraint, Rule rule, int conditionIndex) {
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
		sl.value = sval;
		node = new AlphaNode(engine.nextNodeId());
		node.setSlot(sl);
		node.setOperator(Constants.EQUAL);
		// we increment the node use count when when create a
		// new AlphaNode for the LiteralConstraint
		constraint.getSlot().incrementNodeCount();

		return node;
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
	public BaseNode compile(BoundConstraint constraint, Rule rule, int conditionIndex) {
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
	 * @throws Exception
	 */

	public BaseNode compile(AndLiteralConstraint constraint, Rule rule, int conditionIndex) {
//		SlotAlpha node = null;
//		Slot2 sl = new Slot2(constraint.getName());
//		sl.setId(constraint.getSlot().getId());
//		Object sval = constraint.getValue();
//		sl.setValue(sval);
		// TODO like with AlphaNodeOr
		return null;
		// node = new AlphaNodeAnd(engine.nextNodeId());
		// node.setSlot(sl);
		// we increment the node use count when when create a
		// new AlphaNode for the LiteralConstraint
		// constraint.getSlot().incrementNodeCount();

		// return node;
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
