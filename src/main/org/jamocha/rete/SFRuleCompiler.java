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
import java.util.Iterator;

import org.jamocha.logging.DefaultLogger;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalConversionException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.nodes.AbstractAlpha;
import org.jamocha.rete.nodes.AlphaNode;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.BetaNode;
import org.jamocha.rete.nodes.LIANode;
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
public class SFRuleCompiler implements RuleCompiler {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	private WorkingMemory memory = null;

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

	public SFRuleCompiler(Rete engine, WorkingMemory mem, RootNode root) {
		super();
		this.engine = engine;
		this.memory = mem;
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
		rule.resolveTemplates(engine);
		if (!this.validate || (this.validate && this.tval.analyze(rule) == Analysis.VALIDATION_PASSED)) {
			if (rule.getConditions() != null && rule.getConditions().length > 0) {
				// we check the name of the rule to see if it is for a specific
				// module. if it is, we have to add it to that module
				this.setModule(rule);

				TerminalNode terminalNode = createTerminalNode(rule);

				Condition[] conds = rule.getConditions();
				// at first we create the constraints and then the conditional
				// elements which include joins
				for (int i = 0; i < conds.length; i++)
					conds[i].compile(this, rule, i);

				compileJoins(rule);

				compileActions(rule);

				currentMod.addRule(rule);

				CompileEvent ce = new CompileEvent(rule, CompileEvent.ADD_RULE_EVENT);

				ce.setRule(rule);

				this.notifyListener(ce);
				return true;

			} else if (rule.getConditions().length == 0) {
				this.setModule(rule);
				// the rule has no LHS, this means it only has actions
				BaseNode last = (BaseNode) root.getObjectTypeNodes().get(engine.initFact);
				TerminalNode tnode = createTerminalNode(rule);
				last.addNode(tnode, engine);

				compileActions(rule);
				// attachTerminalNode(last, tnode);
				// now we add the rule to the module
				currentMod.addRule(rule);
				CompileEvent ce = new CompileEvent(rule, CompileEvent.ADD_RULE_EVENT);
				ce.setRule(rule);
				// / this.notifyListener(ce);
				return true;
			}
			return false;
		} else {
			// we print out a message and say that the rule is not valid
			Summary error = this.tval.getErrors();
			engine.writeMessage("Rule " + rule.getName() + " was not added. ", Constants.DEFAULT_OUTPUT); //$NON-NLS-1$ //$NON-NLS-2$
			engine.writeMessage(error.getMessage(), Constants.DEFAULT_OUTPUT);
			Summary warn = this.tval.getWarnings();
			engine.writeMessage(warn.getMessage(), Constants.DEFAULT_OUTPUT);
			return false;
		}
	}

	protected void compileJoins(Rule rule) throws AssertException {
		// take the last node from each condition and connect them by joins
		// regarding the complexity
		TerminalNode terminal = rule.getTerminalNode();
		Condition[] sortedConds = rule.getConditions().clone();
		Arrays.sort(sortedConds);

		BaseNode fromBottom = terminal;
		for (int i = 0; i < sortedConds.length; i++) {
			Condition c = sortedConds[i];
			// c now is the next condition with the lowest complexity

			// now, check whether we have to create a new join
			boolean createNewJoin = (i < sortedConds.length - 1);

			if (createNewJoin) {
				// creat join add old bottom node, set join to new bottom node
				BetaNode newJoin = new BetaNode(engine.nextNodeId());
				newJoin.addNode(fromBottom, engine);
				fromBottom = newJoin;
				// add join to rule:
				rule.addJoinNode(newJoin);

			}
			BaseNode lastNode = c.getLastNode();

			if (lastNode != null)
				(lastNode).addNode(fromBottom, engine);
		}
	}
	
	protected void compileBindings(Rule rule){
		
		
		
	}

	/*
	 * public void compileJoins2(Rule rule, Condition[] conds) throws
	 * AssertException { // only if there's more than 1 condition do we attempt
	 * to // create the join nodes. A rule with just 1 condition has // no joins
	 * if (conds.length > 1) { // previous Condition Condition prev = conds[0];
	 * BaseJoin prevJoin = null; BaseJoin bn = null; // check if the first
	 * condition has any alphaNodes // if it does, we add a LeftInputAdapater to
	 * the // last AlphaNode if (prev instanceof ObjectCondition) {
	 * ObjectCondition cond = (ObjectCondition) prev; ObjectTypeNode otn =
	 * findObjectTypeNode(cond.getTemplateName()); // the LeftInputAdapterNode
	 * is the first node to propogate to // the first joinNode of the rule
	 * LIANode node = new LIANode(engine.nextNodeId()); // if the condition
	 * doesn't have any nodes, we add the // LeftInputAdapter // to the
	 * ObjectTypeNode, otherwise we add it to the last // AlphaNode if
	 * (cond.getNodes().size() == 0) { otn.addNode(node, engine); } else { //
	 * add the LeftInputAdapterNode to the last alphaNode // we need to see if
	 * new LIANode is the same as the existing AbstractAlpha old =
	 * (AbstractAlpha) cond.getLastNode(); if (old instanceof LIANode) { node =
	 * (LIANode) old; } else { old.addNode(node, engine); } }
	 * cond.addNode(node); } else if (prev instanceof TestCondition) { // for
	 * now this is not implemented, since most rules do not // have a test
	 * condition as the first condition } else if (prev instanceof
	 * ExistCondition) { // if the first CE is an exists CE, we handle it
	 * differently BaseJoin bjoin = new ExistJoinFrst(engine.nextNodeId());
	 * ExistCondition cond = (ExistCondition) prev; BaseNode base =
	 * cond.getLastNode(); if (base instanceof AbstractAlpha) { ((AbstractAlpha)
	 * base).addNode(bjoin, engine); } else if (base instanceof BaseJoin) {
	 * ((BaseJoin) base).addNode(bjoin, engine); } // important, do not call
	 * this before ExistJoinFrst is added // if it's called first, the arraylist
	 * will return index // out of bound, since there's nothing in the list
	 * cond.setIsFirstCE(true); cond.addNode(bjoin); } int negatedCE = 0; // now
	 * compile the remaining conditions for (int idx = 1; idx < conds.length;
	 * idx++) { Condition cdt = conds[idx]; ObjectTypeNode otn = null; if (cdt
	 * instanceof TestCondition) {
	 * 
	 * TestCondition tc = (TestCondition) cdt; Signature fn = (Signature)
	 * tc.getFunction(); Expression[] oldpm = fn.getParameters(); Parameter[]
	 * pms = new Parameter[oldpm.length]; for (int ipm = 0; ipm < pms.length;
	 * ipm++) { if (oldpm[ipm] instanceof JamochaValue) { pms[ipm] = (Parameter)
	 * oldpm[ipm]; } else if (oldpm[ipm] instanceof BoundParam) { BoundParam bpm =
	 * (BoundParam) oldpm[ipm]; // now we need to resolve and setup the
	 * BoundParam Binding b = rule.getBinding(bpm.getVariableName()); BoundParam
	 * newpm = new BoundParam(b.getLeftRow(), b.getLeftIndex(), 9,
	 * bpm.isObjectBinding()); newpm.setVariableName(bpm.getVariableName());
	 * pms[ipm] = newpm; } } if (tc.isNegated()) { bn = new
	 * NTestNode(engine.nextNodeId(), fn.lookUpFunction(engine), pms); } else {
	 * bn = new TestNode(engine.nextNodeId(), fn.lookUpFunction(engine), pms); }
	 * if (prevJoin != null) { attachJoinNode(prevJoin, (BaseJoin) bn); } else {
	 * attachJoinNode(prev.getLastNode(), (BaseJoin) bn); } } else if (cdt
	 * instanceof AndCondition) { // TODO for now this is not done yet
	 * BooleanOperatorCondition ac = (BooleanOperatorCondition) cdt; } else if
	 * (cdt instanceof ExistCondition) { // TODO finish implementing Exists
	 * ExistCondition exc = (ExistCondition) cdt; boolean hasNotEqual = false;
	 * Template tmpl = exc.getObjectCondition().getTemplate();
	 * 
	 * List blist = exc.getAllBindings(); Binding[] binds = new
	 * Binding[blist.size()]; for (int idz = 0; idz < binds.length; idz++) {
	 * Object cst = blist.get(idz); if (cst instanceof BoundConstraint) {
	 * BoundConstraint bc = (BoundConstraint) cst; Binding cpy =
	 * rule.copyBinding(bc.getVariableName()); if (cpy.getLeftRow() >= idx) {
	 * binds = new Binding[0]; break; } else { binds[idz] = cpy;
	 * binds[idz].setRightRow(idx - negatedCE); int rinx =
	 * tmpl.getColumnIndex(bc.getName()); // we increment the count to make sure
	 * the // template isn't removed if it is being used
	 * tmpl.getSlot(rinx).incrementNodeCount(); binds[idz].setRightIndex(rinx);
	 * binds[idz].setNegated(bc.getNegated()); if (bc.getNegated()) {
	 * hasNotEqual = true; } } } else if (cst instanceof PredicateConstraint) {
	 * PredicateConstraint pc = (PredicateConstraint) cst; if
	 * (pc.getValue().getType().equals(JamochaType.BINDING)) { BoundParam bpm =
	 * (BoundParam) pc.getValue().getObjectValue(); String var =
	 * bpm.getVariableName(); int op =
	 * ConversionUtils.getOperatorCode(pc.getFunctionName()); // if the first
	 * binding in the function is from // the object type // we reverse the
	 * operator if (pc.getParameters().get(0) != bpm) { op =
	 * ConversionUtils.getOppositeOperatorCode(op); } binds[idz] =
	 * rule.copyPredicateBinding(var, op); binds[idz].setRightRow(idx -
	 * negatedCE); int rinx = tmpl.getColumnIndex(pc.getName()); // we increment
	 * the count to make sure the // template isn't removed if it is being used
	 * tmpl.getSlot(rinx).incrementNodeCount();
	 * 
	 * binds[idz].setRightIndex(rinx); } } } bn = new
	 * ExistJoin(engine.nextNodeId()); bn.setBindings(binds); exc.addNode(bn);
	 * if (prevJoin != null) { attachJoinNode(prevJoin, (BaseJoin) bn); } else {
	 * attachJoinNode(prev.getLastNode(), (BaseJoin) bn); }
	 * attachJoinNode(exc.getLastNode(), (BaseJoin) bn); negatedCE++; } else {
	 * boolean hasNotEqual = false; ObjectCondition oc = (ObjectCondition) cdt;
	 * otn = findObjectTypeNode(oc.getTemplateName()); Template tmpl =
	 * oc.getTemplate();
	 * 
	 * if (cdt.hasBindings()) { // the condition has bindings, so we have to
	 * create // the bindings and set the joinNode List blist =
	 * cdt.getBindings(); Binding[] binds = new Binding[blist.size()]; Iterator
	 * itr = blist.iterator(); for (int idz = 0; idz < blist.size(); idz++) {
	 * Object cst = blist.get(idz); if (cst instanceof BoundConstraint) {
	 * BoundConstraint bc = (BoundConstraint) cst; Binding cpy =
	 * rule.copyBinding(bc.getVariableName()); if (cpy.getLeftRow() >= idx) {
	 * binds = new Binding[0]; break; } else { binds[idz] = cpy;
	 * binds[idz].setRightRow(idx - negatedCE); int rinx =
	 * tmpl.getColumnIndex(bc.getName()); // we increment the count to make sure
	 * the // template isn't removed if it is being // used
	 * tmpl.getSlot(rinx).incrementNodeCount(); binds[idz].setRightIndex(rinx);
	 * binds[idz].setNegated(bc.getNegated()); if (bc.getNegated()) {
	 * hasNotEqual = true; } } } else if (cst instanceof PredicateConstraint) {
	 * PredicateConstraint pc = (PredicateConstraint) cst; if
	 * (pc.getValue().getType().equals(JamochaType.BINDING)) { BoundParam bpm =
	 * (BoundParam) pc.getValue().getObjectValue(); String var =
	 * bpm.getVariableName(); int op =
	 * ConversionUtils.getOperatorCode(pc.getFunctionName()); // if the first
	 * binding in the function is // from the object type // we reverse the
	 * operator if (pc.getParameters().get(0) != bpm) { op =
	 * ConversionUtils.getOppositeOperatorCode(op); } binds[idz] =
	 * rule.copyPredicateBinding(var, op); binds[idz].setRightRow(idx -
	 * negatedCE); int rinx = tmpl.getColumnIndex(pc.getName()); // we increment
	 * the count to make sure the // template isn't removed if it is being //
	 * used tmpl.getSlot(rinx).incrementNodeCount();
	 * binds[idz].setRightIndex(rinx); } } } if (!oc.getNegated()) { if
	 * (binds.length > 0 && !hasNotEqual) { bn = new
	 * HashedEqBNode(engine.nextNodeId()); } else if (binds.length > 0 &&
	 * hasNotEqual) { bn = new HashedNotEqBNode(engine.nextNodeId()); } else if
	 * (binds.length == 0) { bn = new ZJBetaNode(engine.nextNodeId()); } else {
	 * bn = new BetaNode(engine.nextNodeId()); } } else {
	 * 
	 * if (binds.length == 0 || hasNotEqual) { bn = new
	 * HashedNotEqNJoin(engine.nextNodeId()); } else { bn = new
	 * HashedEqNJoin(engine.nextNodeId()); } // bn = new
	 * NotJoin(engine.nextNodeId()); negatedCE++; } bn.setBindings(binds); }
	 * else { // if the condition doesn't have any bindings, we make // a join
	 * without any bindings if (bn == null) { bn = new
	 * ZJBetaNode(engine.nextNodeId()); } bn.setBindings(new Binding[0]); // now
	 * add the join node to the last nodes of the // current // and previous
	 * condition } if (prevJoin != null) { attachJoinNode(prevJoin, (BaseJoin)
	 * bn); } else { attachJoinNode(prev.getLastNode(), (BaseJoin) bn); } if
	 * (cdt.getNodes().size() > 0) { attachJoinNode(cdt.getLastNode(),
	 * (BaseJoin) bn); } else { otn.addNode(bn, engine); } } // now we set the
	 * previous node to current prev = cdt; prevJoin = bn; rule.addJoinNode(bn); } }
	 * else if (conds.length == 1) { // we have to check and see if the rule has
	 * a single NOT CE if (conds[0] instanceof ObjectCondition) {
	 * ObjectCondition oc = (ObjectCondition) conds[0]; if (oc.getNegated()) { //
	 * the ObjectCondition is negated, so we need to // handle it appropriate.
	 * This means we need to // add a LIANode to _IntialFact and attach a
	 * NOTNode // to the LIANode. ObjectTypeNode otn = (ObjectTypeNode)
	 * root.getObjectTypeNodes().get(engine.initFact); LIANode lianode =
	 * findLeftInputAdapter(otn); NotJoin njoin = new
	 * NotJoin(engine.nextNodeId()); njoin.setBindings(new Binding[0]);
	 * lianode.addNode(njoin, engine); // add the join to the rule object
	 * rule.addJoinNode(njoin); oc.getLastNode().addNode(njoin,engine); } else
	 * if (oc.getNodes().size() == 0) { ObjectTypeNode otn =
	 * findObjectTypeNode(oc.getTemplateName()); LIANode lianode = new
	 * LIANode(engine.nextNodeId()); otn.addNode(lianode, engine);
	 * rule.getConditions()[0].addNode(lianode); } } } }
	 */
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
				Constraint[] constraints = condition.getConstraints();
				Constraint constraint = null;
				TemplateSlot slot;
				for (int i = 0; i < constraints.length; i++) {
					constraint = constraints[i];
					slot = template.getSlot(constraint.getName());
					if (slot == null)
						// slot does not exist -> exit!
						return null;

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
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
				Function f = engine.findFunction(constraint.getFunctionName());
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
		bind.setRowDeclared(conditionIndex);
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
		SlotAlpha node = null;
		Slot2 sl = new Slot2(constraint.getName());
		sl.setId(constraint.getSlot().getId());
		Object sval = constraint.getValue();
		sl.setValue(sval);

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
		// we need to create a binding class for the BoundConstraint
		if (rule.getBinding(constraint.getVariableName()) == null) {
			// if the HashMap doesn't already contain the binding,
			// we create
			// a new one
			if (constraint.getIsObjectBinding()) {
				Binding bind = new Binding();
				bind.setVarName(constraint.getVariableName());
				bind.setLeftRow(conditionIndex);
				bind.setLeftIndex(-1);
				bind.setIsObjectVar(true);
				rule.addBinding(constraint.getVariableName(), bind);
			} else {
				Binding bind = new Binding();
				bind.setVarName(constraint.getVariableName());
				bind.setLeftRow(conditionIndex);
				bind.setLeftIndex(constraint.getSlot().getId());
				bind.setRowDeclared(conditionIndex);
				constraint.setFirstDeclaration(true);
				rule.addBinding(constraint.getVariableName(), bind);
			}
		}
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
		SlotAlpha node = null;
		Slot2 sl = new Slot2(constraint.getName());
		sl.setId(constraint.getSlot().getId());
		Object sval = constraint.getValue();
		sl.setValue(sval);
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

	/*
	 * 
	 *//**
		 * For now just attach the node and don't bother with node sharing
		 * 
		 * @param existing -
		 *            an existing node in the network. it may be an
		 *            ObjectTypeNode or AlphaNode
		 * @param alpha
		 */
	/*
	 * protected void attachAlphaNode(AbstractAlpha existing, AbstractAlpha
	 * alpha, Condition cond) { if (alpha != null) { try { AbstractAlpha share =
	 * null; share = shareAlphaNode(existing, alpha); if (share == null) {
	 * existing.addNode(alpha, engine); // if the node isn't shared, we add the
	 * node to the // Condition // object the node belongs to.
	 * cond.addNode(alpha); } else if (existing != alpha) { // the node is
	 * shared, so instead of adding the new node, // we add the existing node
	 * cond.addNode(share); memory.removeAlphaMemory(alpha); if
	 * (alpha.getChildCount() == 1 && alpha.getChildNodes()[0] instanceof
	 * AbstractAlpha) { // get the next node from the new AlphaNode
	 * AbstractAlpha nnext = (AbstractAlpha) alpha.getChildNodes()[0];
	 * attachAlphaNode(share, nnext, cond); } } } catch (AssertException e) { //
	 * send an event with the correct error CompileEvent ce = new
	 * CompileEvent(this, CompileEvent.ADD_NODE_ERROR);
	 * ce.setMessage(alpha.toPPString()); this.notifyListener(ce); } } }
	 * 
	 *//**
		 * Implementation will get the hashString from each node and compare
		 * them
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
