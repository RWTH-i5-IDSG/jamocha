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
import java.util.Iterator;
import java.util.Map;

import org.jamocha.logging.DefaultLogger;
import org.jamocha.parser.IllegalConversionException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Analysis;
import org.jamocha.rule.AndCondition;
import org.jamocha.rule.AndLiteralConstraint;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.ExistCondition;
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

	private Map<Deftemplate, ObjectTypeNode> inputnodes = null;

	private Module currentMod = null;

	private LIANode initialFactLIANode = null;

	private ArrayList<CompilerListener> listener = new ArrayList<CompilerListener>();

	protected boolean validate = true;

	protected TemplateValidation tval = null;

	public static final String FUNCTION_NOT_FOUND = Messages
			.getString("CompilerProperties.function.not.found"); //$NON-NLS-1$

	public static final String INVALID_FUNCTION = Messages
			.getString("CompilerProperties.invalid.function"); //$NON-NLS-1$

	public static final String ASSERT_ON_PROPOGATE = Messages
			.getString("CompilerProperties.assert.on.add"); //$NON-NLS-1$

	protected DefaultLogger log = new DefaultLogger(BasicRuleCompiler.class);

	public SFRuleCompiler(Rete engine, WorkingMemory mem,
			Map<Deftemplate, ObjectTypeNode> inputNodes) {
		super();
		this.engine = engine;
		this.memory = mem;
		this.inputnodes = inputNodes;
		this.tval = new TemplateValidation(engine);
	}

	public void setValidateRule(boolean valid) {
		this.validate = valid;
	}

	public boolean getValidateRule() {
		return this.validate;
	}

	/**
	 * @author Peter Lin
	 * 
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
	 * @author Peter Lin
	 * 
	 * The method creates the right terminal node based on the settings of the
	 * rule
	 * 
	 * @param Rule
	 * @return TerminalNode
	 */
	protected TerminalNode createTerminalNode(Rule rule) {
		if (rule.getNoAgenda() && rule.getExpirationDate() == 0) {
			return new NoAgendaTNode(engine.nextNodeId(), rule);
		} else if (rule.getNoAgenda() && rule.getExpirationDate() > 0) {
			return new NoAgendaTNode2(engine.nextNodeId(), rule);
		} else if (rule.getExpirationDate() > 0) {
			return new TerminalNode3(engine.nextNodeId(), rule);
		} else {
			return new TerminalNode2(engine.nextNodeId(), rule);
		}
	}

	/**
	 * @author Peter Lin
	 * 
	 * The method adds an ObjectTypeNode to the HashMap. This implementation
	 * uses the Deftemplate as HashMap key and the Node as value. If the Node or
	 * the key already exists in the HashMap the compiler will not add it to the
	 * network.
	 * 
	 * @param ObjectTypeNode
	 * @return void
	 */
	public void addObjectTypeNode(ObjectTypeNode node) {
		if (!this.inputnodes.containsKey(node.getDeftemplate())) {
			this.inputnodes.put((Deftemplate) node.getDeftemplate(), node);
		}
	}

	/**
	 * @author Peter Lin
	 * 
	 * The method removes the ObjectTypeNode and calls clear on it.
	 * 
	 * @param ObjectTypeNode
	 *            node
	 * @return void
	 */
	public void removeObjectTypeNode(ObjectTypeNode node) {
		this.inputnodes.remove(node.getDeftemplate());
		node.clear(this.memory);
		node.clearSuccessors();
	}

	/**
	 * @author Peter Lin
	 * 
	 * The method returns the ObjectTypeNode for the given Deftemplate name. If
	 * no ObjectTypeNode is found with this name the method returns null.
	 * 
	 * @param String
	 *            templateName
	 * @return ObjectTypeNode
	 */
	public ObjectTypeNode findObjectTypeNode(String templateName) {
		Iterator itr = this.inputnodes.keySet().iterator();
		Template tmpl = null;
		while (itr.hasNext()) {
			tmpl = (Template) itr.next();
			if (tmpl.getName().equals(templateName)) {
				break;
			}
		}
		if (tmpl != null) {
			return getObjectTypeNode(tmpl);
		} else {
			log.debug(Messages.getString("RuleCompiler.deftemplate.error")); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * @author Peter Lin
	 * 
	 * The method gets the ObjectTypeNode from the HashMap and returns it. If
	 * the node does not exist, the method will return null.
	 * 
	 * @param Template
	 * @return ObjectTypeNode
	 */
	public ObjectTypeNode getObjectTypeNode(Template template) {
		return (ObjectTypeNode) this.inputnodes.get(template);
	}

	/**
	 * @author Peter Lin
	 * 
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
	 * @author Peter Lin
	 * 
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

	public boolean addRule(Rule rule) {
		rule.resolveTemplates(engine);
		if (!this.validate
				|| (this.validate && this.tval.analyze(rule) == Analysis.VALIDATION_PASSED)) {
			if (rule.getConditions() != null && rule.getConditions().length > 0) {
				// we check the name of the rule to see if it is for a specific
				// module. if it is, we have to add it to that module
				this.setModule(rule);
///				try {
					Condition[] conds = rule.getConditions();
					// first we create the constraints and then the conditional
					// elements which include joins
					for (int i=0 ; i<conds.length; i++)
						compileCondition(conds[i], i, rule);

					// Condition con = conds[idx];
					// // compile object conditions
					// if (con instanceof ObjectCondition) {
					// ObjectCondition oc = (ObjectCondition) con;
					// /// this.compileObjectConditions(oc, idx, rule,
					// rule.getRememberMatch());
					// } else if (con instanceof ExistCondition) {
					// ExistCondition econd = (ExistCondition) con;
					// /// this.compileExistCondition(econd, idx, rule,
					// rule.getRememberMatch());
					// } else if (con instanceof TestCondition) {
					//
					// }

					// }
					// / compileJoins(rule, conds);

					BaseNode last = rule.getLastNode();
					TerminalNode tnode = createTerminalNode(rule);

					// / attachTerminalNode(last, tnode);
					// compile the actions
					// / compileActions(rule, rule.getActions());
					// now we pass the bindings to the rule, so that actiosn can
					// resolve the bindings

					// now we add the rule to the module
					currentMod.addRule(rule);
					CompileEvent ce = new CompileEvent(rule,
							CompileEvent.ADD_RULE_EVENT);
					ce.setRule(rule);
					this.notifyListener(ce);
					return true;
/*				} catch (AssertException e) {
					CompileEvent ce = new CompileEvent(rule,
							CompileEvent.INVALID_RULE);
					ce.setMessage(Messages
							.getString("RuleCompiler.assert.error")); //$NON-NLS-1$
					this.notifyListener(ce);
					log.debug(e);
					return false;
				}*/
			} else if (rule.getConditions().length == 0) {
				this.setModule(rule);
				// the rule has no LHS, this means it only has actions
				BaseNode last = (BaseNode) this.inputnodes.get(engine.initFact);
				TerminalNode tnode = createTerminalNode(rule);
				// / compileActions(rule, rule.getActions());
				// / attachTerminalNode(last, tnode);
				// now we add the rule to the module
				currentMod.addRule(rule);
				CompileEvent ce = new CompileEvent(rule,
						CompileEvent.ADD_RULE_EVENT);
				ce.setRule(rule);
				// / this.notifyListener(ce);
				return true;
			}
			return false;
		} else {
			// we print out a message and say that the rule is not valid
			Summary error = this.tval.getErrors();
			engine
					.writeMessage(
							"Rule " + rule.getName() + " was not added. ", Constants.DEFAULT_OUTPUT); //$NON-NLS-1$ //$NON-NLS-2$
			engine.writeMessage(error.getMessage(), Constants.DEFAULT_OUTPUT);
			Summary warn = this.tval.getWarnings();
			engine.writeMessage(warn.getMessage(), Constants.DEFAULT_OUTPUT);
			return false;
		}
	}

	private boolean compileCondition(ObjectCondition condition, int conditionIndex, Rule rule) {
		boolean compileConditionState = false;

		Template template = condition.getTemplate();

		ObjectTypeNode otn = getObjectTypeNode(template);

		if (otn != null) {
			BaseAlpha2 first = null;
			BaseAlpha2 previous = null;
			BaseAlpha2 current = null;

			Constraint[] constrs = condition.getConstraints();
			for (int idx = 0; idx < constrs.length; idx++) {
				Constraint cnstr = constrs[idx];
				current = (BaseAlpha2) compileConstraint(cnstr, rule, template,
						conditionIndex);

				// we add the node to the previous
				if (first == null) {
					first = current;
					previous = current;
				} else if (current != previous) {
					try {
						previous.addSuccessorNode(current, engine, memory);
						// now set the previous to current
						previous = current;
					} catch (AssertException e) {
						// send an event
					}
				}
				if (current != null) {
					condition.addNode(current);
				}
			}
			if (first != null) {
				attachAlphaNode(otn, first, condition);
			} else {
				// this means there's no value or predicate constraint
			}
		}
		return compileConditionState;
	}

	private boolean compileCondition(ExistCondition condition,
			int conditionIndex,  Rule rule) {
		boolean compileConditionState = false;

		return compileConditionState;
	}

	private boolean compileCondition(TestCondition condition, int conditionIndex,  Rule rule) {
		boolean compileConditionState = false;

		return compileConditionState;
	}

	private boolean compileCondition(AndCondition condition, int conditionIndex,  Rule rule) {
		boolean compileConditionState = false;

		return compileConditionState;
	}

	private boolean compileCondition(NotCondition condition, int conditionIndex,  Rule rule) {
		boolean compileConditionState = false;

		return compileConditionState;
	}

	private boolean compileCondition(OrCondition condition, int conditionIndex,  Rule rule) {
		boolean compileConditionState = false;

		return compileConditionState;
	}

	private boolean compileCondition(Condition condition, int conditionIndex,  Rule rule) {

		if (condition instanceof ObjectCondition)
			return compileCondition((ObjectCondition) condition, conditionIndex,rule);
		if (condition instanceof ExistCondition)
			return compileCondition((ExistCondition) condition, conditionIndex,rule);
		if (condition instanceof TestCondition)
			return compileCondition((TestCondition) condition, conditionIndex,rule);
		if (condition instanceof AndCondition)
			return compileCondition((AndCondition) condition, conditionIndex,rule);
		if (condition instanceof NotCondition)
			return compileCondition((NotCondition) condition, conditionIndex,rule);
		if (condition instanceof OrCondition)
			return compileCondition((OrCondition) condition, conditionIndex,rule);

		return false;
	}

	private BaseNode compileConstraint(Constraint constraint, Rule rule,
			Template templ, int conditionIndex) {

		if (constraint instanceof AndLiteralConstraint)
			return compileConstraint((AndLiteralConstraint) constraint, rule,
					templ, conditionIndex);
		if (constraint instanceof BoundConstraint)
			return compileConstraint((BoundConstraint) constraint, rule, templ,
					conditionIndex);
		if (constraint instanceof LiteralConstraint)
			return compileConstraint((LiteralConstraint) constraint, rule,
					templ, conditionIndex);
		if (constraint instanceof OrLiteralConstraint)
			return compileConstraint((OrLiteralConstraint) constraint, rule,
					templ, conditionIndex);
		if (constraint instanceof PredicateConstraint)
			return compileConstraint((PredicateConstraint) constraint, rule,
					templ, conditionIndex);

		return null;
	}

	private BaseNode compileConstraint(PredicateConstraint constraint,
			Rule rule, Template templ, int conditionIndex) {
		BaseAlpha2 node = null;
		// for now we expect the user to write the predicate in this
		// way (> ?bind value), where the binding is first. this
		// needs to be updated so that we look at the order of the
		// parameters and set the node appropriately
		// we only create an AlphaNode if the predicate isn't
		// joining 2 bindings.
		if (!constraint.isPredicateJoin()) {
			if (ConversionUtils.isPredicateOperatorCode(constraint
					.getFunctionName())) {
				int oprCode = ConversionUtils.getOperatorCode(constraint
						.getFunctionName());
				Slot sl = (Slot) templ.getSlot(constraint.getName()).clone();
				JamochaValue sval;
				try {
					sval = constraint.getValue()
							.implicitCast(sl.getValueType());
				} catch (IllegalConversionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				sl.value = sval;
				// create the alphaNode
				if (rule.getRememberMatch()) {
					node = new AlphaNode(engine.nextNodeId());
				} else {
					node = new NoMemANode(engine.nextNodeId());
				}
				node.setSlot(sl);
				node.setOperator(oprCode);
				node.incrementUseCount();
				// we increment the node use count when when create
				// a new
				// AlphaNode for the LiteralConstraint
				templ.getSlot(sl.getId()).incrementNodeCount();
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
					if (f.getDescription().getReturnType().equals(
							JamochaType.BOOLEANS)) {
						// TODO - need to implement it
					} else {
						// the function doesn't return boolean, so
						// we have to notify
						// the listeners the condition is not valid
						CompileEvent ce = new CompileEvent(this,
								CompileEvent.FUNCTION_INVALID);
						ce.setMessage(INVALID_FUNCTION
								+ " " + f.getDescription().getReturnType()); //$NON-NLS-1$
						this.notifyListener(ce);
					}
				} else {
					// we need to notify listeners the function
					// wasn't found
					CompileEvent ce = new CompileEvent(this,
							CompileEvent.FUNCTION_NOT_FOUND);
					ce.setMessage(FUNCTION_NOT_FOUND
							+ " " + f.getDescription().getReturnType()); //$NON-NLS-1$
					this.notifyListener(ce);
				}
			}
		}
		Binding bind = new Binding();
		bind.setVarName(constraint.getVariableName());
		bind.setLeftRow(conditionIndex);
		bind.setLeftIndex(templ.getSlot(constraint.getName()).getId());
		bind.setRowDeclared(conditionIndex);
		// we only add the binding to the map if it doesn't already
		// exist
		if (rule.getBinding(constraint.getVariableName()) == null) {
			rule.addBinding(constraint.getVariableName(), bind);
		}
		return node;
	}

	private BaseNode compileConstraint(OrLiteralConstraint constraint,
			Rule rule, Template templ, int conditionIndex) {
		BaseAlpha2 node = null;
		// TODO: refactor... don't use such perfect code so often
		if (templ.getSlot(constraint.getName()) != null) {
			Slot2 sl = new Slot2(constraint.getName());
			sl.setId(templ.getColumnIndex(constraint.getName()));
			Object sval = constraint.getValue();
			sl.setValue(sval);
			if (rule.getRememberMatch()) {
				node = new AlphaNodeOr(engine.nextNodeId());
			} else {
				node = new NoMemOr(engine.nextNodeId());
			}
			node.setSlot(sl);
			node.incrementUseCount();
			// we increment the node use count when when create a
			// new
			// AlphaNode for the LiteralConstraint
			templ.getSlot(sl.getId()).incrementNodeCount();
		}
		return node;
	}

	private BaseNode compileConstraint(LiteralConstraint constraint, Rule rule,
			Template templ, int conditionIndex) {
		BaseAlpha2 node = null;
		if (templ.getSlot(constraint.getName()) != null) {
			Slot sl = (Slot) templ.getSlot(constraint.getName()).clone();
			JamochaValue sval;
			try {
				sval = constraint.getValue().implicitCast(sl.getValueType());
			} catch (IllegalConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			sl.value = sval;
			if (rule.getRememberMatch()) {
				node = new AlphaNode(engine.nextNodeId());
			} else {
				node = new NoMemANode(engine.nextNodeId());
			}
			node.setSlot(sl);
			node.setOperator(Constants.EQUAL);
			node.incrementUseCount();
			// we increment the node use count when when create a
			// new
			// AlphaNode for the LiteralConstraint
			templ.getSlot(sl.getId()).incrementNodeCount();
		}
		return node;
	}

	private BaseNode compileConstraint(BoundConstraint constraint, Rule rule,
			Template templ, int conditionIndex) {
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
				bind.setLeftIndex(templ.getSlot(constraint.getName()).getId());
				bind.setRowDeclared(conditionIndex);
				constraint.setFirstDeclaration(true);
				rule.addBinding(constraint.getVariableName(), bind);
			}
		}
		return null;
	}

	private BaseNode compileConstraint(AndLiteralConstraint constraint,
			Rule rule, Template templ, int conditionIndex) {
		BaseAlpha2 node = null;
		if (templ.getSlot(constraint.getName()) != null) {
			Slot2 sl = new Slot2(constraint.getName());
			sl.setId(templ.getColumnIndex(constraint.getName()));
			Object sval = constraint.getValue();
			sl.setValue(sval);
			if (rule.getRememberMatch()) {
				node = new AlphaNodeAnd(engine.nextNodeId());
			} else {
				node = new NoMemAnd(engine.nextNodeId());
			}
			node.setSlot(sl);
			node.incrementUseCount();
			// we increment the node use count when when create a
			// new
			// AlphaNode for the LiteralConstraint
			templ.getSlot(sl.getId()).incrementNodeCount();
		}
		return node;
	}

	/**
	 * @author Peter Lin
	 * 
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
	 * For now just attach the node and don't bother with node sharing
	 * 
	 * @param existing -
	 *            an existing node in the network. it may be an ObjectTypeNode
	 *            or AlphaNode
	 * @param alpha
	 */
	protected void attachAlphaNode(BaseAlpha existing, BaseAlpha alpha,
			Condition cond) {
		if (alpha != null) {
			try {
				BaseAlpha share = null;
				share = shareAlphaNode(existing, alpha);
				if (share == null) {
					existing.addSuccessorNode(alpha, engine, memory);
					// if the node isn't shared, we add the node to the
					// Condition
					// object the node belongs to.
					cond.addNode(alpha);
				} else if (existing != alpha) {
					// the node is shared, so instead of adding the new node,
					// we add the existing node
					share.incrementUseCount();
					cond.addNode(share);
					memory.removeAlphaMemory(alpha);
					if (alpha.successorCount() == 1
							&& alpha.getSuccessorNodes()[0] instanceof BaseAlpha) {
						// get the next node from the new AlphaNode
						BaseAlpha nnext = (BaseAlpha) alpha.getSuccessorNodes()[0];
						attachAlphaNode(share, nnext, cond);
					}
				}
			} catch (AssertException e) {
				// send an event with the correct error
				CompileEvent ce = new CompileEvent(this,
						CompileEvent.ADD_NODE_ERROR);
				ce.setMessage(alpha.toPPString());
				this.notifyListener(ce);
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
	protected BaseAlpha shareAlphaNode(BaseAlpha existing, BaseAlpha alpha) {
		Object[] scc = existing.getSuccessorNodes();
		for (int idx = 0; idx < scc.length; idx++) {
			Object next = scc[idx];
			if (next instanceof BaseAlpha) {
				BaseAlpha baseAlpha = (BaseAlpha) next;
				if (baseAlpha.hashString().equals(alpha.hashString())) {
					return baseAlpha;
				}
			}
		}
		return null;
	}
}
