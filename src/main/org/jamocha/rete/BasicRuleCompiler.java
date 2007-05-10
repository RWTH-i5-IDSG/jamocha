/*
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
import java.util.List;
import java.util.Map;

import org.jamocha.logging.DefaultLogger;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.IllegalConversionException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Action;
import org.jamocha.rule.Analysis;
import org.jamocha.rule.AndCondition;
import org.jamocha.rule.AndLiteralConstraint;
import org.jamocha.rule.BooleanOperatorCondition;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.ExistCondition;
import org.jamocha.rule.FunctionAction;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.OrLiteralConstraint;
import org.jamocha.rule.PredicateConstraint;
import org.jamocha.rule.Rule;
import org.jamocha.rule.Summary;
import org.jamocha.rule.TemplateValidation;
import org.jamocha.rule.TestCondition;

/**
 * @author Peter Lin
 * 
 * BasicRuleCompiler is a simple implementation. It's for research purposes for
 * now. It may become a full blown rule compiler later, but for now, I just need
 * a simple compiler to load my samples rule to test the JIT algorithm.
 * 
 * 
 */
public class BasicRuleCompiler implements RuleCompiler {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	private WorkingMemory memory = null;

	private Rete engine = null;

	private Map inputnodes = null;

	private Module currentMod = null;

	private LIANode initialFactLIANode = null;

	private ArrayList listener = new ArrayList();

	protected boolean validate = true;

	protected TemplateValidation tval = null;

	public static final String FUNCTION_NOT_FOUND = Messages
			.getString("CompilerProperties.function.not.found"); //$NON-NLS-1$

	public static final String INVALID_FUNCTION = Messages
			.getString("CompilerProperties.invalid.function"); //$NON-NLS-1$

	public static final String ASSERT_ON_PROPOGATE = Messages
			.getString("CompilerProperties.assert.on.add"); //$NON-NLS-1$

	protected DefaultLogger log = new DefaultLogger(BasicRuleCompiler.class);

	/**
	 * 
	 */
	public BasicRuleCompiler(Rete engine, WorkingMemory mem, Map inputNodes) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.RuleCompiler#addRule(woolfel.engine.rule.Rule)
	 */
	public boolean addRule(Rule rule) {
		rule.resolveTemplates(engine);
		if (!this.validate
				|| (this.validate && this.tval.analyze(rule) == Analysis.VALIDATION_PASSED)) {
			if (rule.getConditions() != null && rule.getConditions().length > 0) {
				// we check the name of the rule to see if it is for a specific
				// module. if it is, we have to add it to that module
				this.setModule(rule);
				try {
					Condition[] conds = rule.getConditions();
					// first we create the constraints, before creating the
					// Conditional
					// elements which include joins
					for (int idx = 0; idx < conds.length; idx++) {
						Condition con = conds[idx];
						// compile object conditions
						if (con instanceof ObjectCondition) {
							ObjectCondition oc = (ObjectCondition) con;
							this.compileObjectConditions(oc, idx, rule, rule
									.getRememberMatch());
						} else if (con instanceof ExistCondition) {
							ExistCondition econd = (ExistCondition) con;
							this.compileExistCondition(econd, idx, rule, rule
									.getRememberMatch());
						} else if (con instanceof TestCondition) {

						}
					}
					compileJoins(rule, conds);

					BaseNode last = rule.getLastNode();
					TerminalNode tnode = createTerminalNode(rule);

					attachTerminalNode(last, tnode);
					// compile the actions
					compileActions(rule, rule.getActions());
					// now we pass the bindings to the rule, so that actiosn can
					// resolve the bindings

					// now we add the rule to the module
					currentMod.addRule(rule);
					CompileEvent ce = new CompileEvent(rule,
							CompileEvent.ADD_RULE_EVENT);
					ce.setRule(rule);
					this.notifyListener(ce);
					return true;
				} catch (AssertException e) {
					CompileEvent ce = new CompileEvent(rule,
							CompileEvent.INVALID_RULE);
					ce.setMessage(Messages
							.getString("RuleCompiler.assert.error")); //$NON-NLS-1$
					this.notifyListener(ce);
					log.debug(e);
					return false;
				}
			} else if (rule.getConditions().length == 0) {
				this.setModule(rule);
				// the rule has no LHS, this means it only has actions
				BaseNode last = (BaseNode) this.inputnodes.get(engine.initFact);
				TerminalNode tnode = createTerminalNode(rule);
				compileActions(rule, rule.getActions());
				attachTerminalNode(last, tnode);
				// now we add the rule to the module
				currentMod.addRule(rule);
				CompileEvent ce = new CompileEvent(rule,
						CompileEvent.ADD_RULE_EVENT);
				ce.setRule(rule);
				this.notifyListener(ce);
				return true;
			}
			return false;
		} else {
			// we need to print out a message saying the rule was not valid
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
	 * The method is responsible for creating the right terminal node based on
	 * the settings for the rule.
	 * 
	 * @param rl
	 * @return
	 */
	protected TerminalNode createTerminalNode(Rule rl) {
		if (rl.getNoAgenda() && rl.getExpirationDate() == 0) {
			return new NoAgendaTNode(engine.nextNodeId(), rl);
		} else if (rl.getNoAgenda() && rl.getExpirationDate() > 0) {
			return new NoAgendaTNode2(engine.nextNodeId(), rl);
		} else if (rl.getExpirationDate() > 0) {
			return new TerminalNode3(engine.nextNodeId(), rl);
		} else {
			return new TerminalNode2(engine.nextNodeId(), rl);
		}
	}

	/**
	 * implementation uses the deftemplate for the HashMap key and the node for
	 * the value. If the node already exists in the HashMap, or the key already
	 * exists, the compiler will not add it to the network.
	 */
	public void addObjectTypeNode(Template template) {
		if (!this.inputnodes.containsKey(template)) {
			this.inputnodes.put(template, new ObjectTypeNode(engine.nextNodeId(), template));
		}
	}

	/**
	 * Method will remove the ObjectTypeNode and call clear on it.
	 */
	public void removeObjectTypeNode(ObjectTypeNode node) {
		this.inputnodes.remove(node.getDeftemplate());
		node.clear(this.memory);
		node.clearSuccessors();
	}

	/**
	 * if the ObjectTypeNode does not exist, the method will return null.
	 */
	public ObjectTypeNode getObjectTypeNode(Template template) {
		return (ObjectTypeNode) this.inputnodes.get(template);
	}

	/**
	 * 
	 * @param templateName
	 * @return
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
			return (ObjectTypeNode) this.inputnodes.get(tmpl);
		} else {
			log.debug(Messages.getString("RuleCompiler.deftemplate.error")); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * Implementation will check to see if the
	 * 
	 * @see org.jamocha.rete.RuleCompiler#addListener(org.jamocha.rete.CompilerListener)
	 */
	public void addListener(CompilerListener listener) {
		if (!this.listener.contains(listener)) {
			this.listener.add(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.RuleCompiler#removeListener(woolfel.engine.rete.CompilerListener)
	 */
	public void removeListener(CompilerListener listener) {
		this.listener.remove(listener);
	}

	/**
	 * Compile a single ObjectCondition
	 * 
	 * @param cond
	 * @param position
	 * @param bindings
	 */
	protected void compileObjectConditions(ObjectCondition cond, int position,
			Rule util, boolean alphaMemory) {
		ObjectTypeNode otn = findObjectTypeNode(cond.getTemplateName());
		if (otn != null) {
			BaseAlpha2 first = null;
			BaseAlpha2 previous = null;
			BaseAlpha2 current = null;
			Template templ = cond.getTemplate();

			Constraint[] constrs = cond.getConstraints();
			for (int idx = 0; idx < constrs.length; idx++) {
				Constraint cnstr = constrs[idx];
				if (cnstr instanceof LiteralConstraint) {
					// we create alphaNodes from LiteralConstraints
					if (templ.getSlot(cnstr.getName()) != null) {
						Slot sl = (Slot) templ.getSlot(cnstr.getName()).clone();
						JamochaValue sval;
						try {
							sval = cnstr.getValue().implicitCast(
									sl.getValueType());
						} catch (IllegalConversionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
						sl.value = sval;
						if (util.getRememberMatch()) {
							current = new AlphaNode(engine.nextNodeId());
						} else {
							current = new NoMemANode(engine.nextNodeId());
						}
						current.setSlot(sl);
						current.setOperator(Constants.EQUAL);
						current.incrementUseCount();
						// we increment the node use count when when create a
						// new
						// AlphaNode for the LiteralConstraint
						templ.getSlot(sl.getId()).incrementNodeCount();
					}
				} else if (cnstr instanceof AndLiteralConstraint) {
					AndLiteralConstraint alc = (AndLiteralConstraint) cnstr;
					if (templ.getSlot(alc.getName()) != null) {
						Slot2 sl = new Slot2(alc.getName());
						sl.setId(templ.getColumnIndex(alc.getName()));
						Object sval = alc.getValue();
						sl.setValue(sval);
						if (util.getRememberMatch()) {
							current = new AlphaNodeAnd(engine.nextNodeId());
						} else {
							current = new NoMemAnd(engine.nextNodeId());
						}
						current.setSlot(sl);
						current.incrementUseCount();
						// we increment the node use count when when create a
						// new
						// AlphaNode for the LiteralConstraint
						templ.getSlot(sl.getId()).incrementNodeCount();
					}
				} else if (cnstr instanceof OrLiteralConstraint) {
					OrLiteralConstraint olc = (OrLiteralConstraint) cnstr;
					if (templ.getSlot(olc.getName()) != null) {
						Slot2 sl = new Slot2(olc.getName());
						sl.setId(templ.getColumnIndex(olc.getName()));
						Object sval = olc.getValue();
						sl.setValue(sval);
						if (util.getRememberMatch()) {
							current = new AlphaNodeOr(engine.nextNodeId());
						} else {
							current = new NoMemOr(engine.nextNodeId());
						}
						current.setSlot(sl);
						current.incrementUseCount();
						// we increment the node use count when when create a
						// new
						// AlphaNode for the LiteralConstraint
						templ.getSlot(sl.getId()).incrementNodeCount();
					}
				} else if (cnstr instanceof BoundConstraint) {
					// we need to create a binding class for the BoundConstraint
					BoundConstraint bc = (BoundConstraint) cnstr;
					if (util.getBinding(bc.getVariableName()) == null) {
						// if the HashMap doesn't already contain the binding,
						// we create
						// a new one
						if (bc.getIsObjectBinding()) {
							Binding bind = new Binding();
							bind.setVarName(bc.getVariableName());
							bind.setLeftRow(position);
							bind.setLeftIndex(-1);
							bind.setIsObjectVar(true);
							util.addBinding(bc.getVariableName(), bind);
						} else {
							Binding bind = new Binding();
							bind.setVarName(bc.getVariableName());
							bind.setLeftRow(position);
							bind.setLeftIndex(templ.getSlot(bc.getName())
									.getId());
							bind.setRowDeclared(position);
							bc.setFirstDeclaration(true);
							util.addBinding(bc.getVariableName(), bind);
						}
					} else {
						// if the binding already exists, we need do nothing
						// currently
					}
				} else if (cnstr instanceof PredicateConstraint) {
					// for now we expect the user to write the predicate in this
					// way (> ?bind value), where the binding is first. this
					// needs to be updated so that we look at the order of the
					// parameters and set the node appropriately
					PredicateConstraint pc = (PredicateConstraint) cnstr;
					// we only create an AlphaNode if the predicate isn't
					// joining 2 bindings.
					if (!pc.isPredicateJoin()) {
						if (ConversionUtils.isPredicateOperatorCode(pc
								.getFunctionName())) {
							int oprCode = ConversionUtils.getOperatorCode(pc
									.getFunctionName());
							Slot sl = (Slot) templ.getSlot(pc.getName())
									.clone();
							JamochaValue sval;
							try {
								sval = cnstr.getValue().implicitCast(
										sl.getValueType());
							} catch (IllegalConversionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return;
							}
							sl.value = sval;
							// create the alphaNode
							if (util.getRememberMatch()) {
								current = new AlphaNode(engine.nextNodeId());
							} else {
								current = new NoMemANode(engine.nextNodeId());
							}
							current.setSlot(sl);
							current.setOperator(oprCode);
							current.incrementUseCount();
							// we increment the node use count when when create
							// a new
							// AlphaNode for the LiteralConstraint
							templ.getSlot(sl.getId()).incrementNodeCount();
						} else {
							// the function isn't a built in predicate function
							// that
							// returns boolean true/false. We look up the
							// function
							Function f = engine.findFunction(pc
									.getFunctionName());
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
					bind.setVarName(pc.getVariableName());
					bind.setLeftRow(position);
					bind.setLeftIndex(templ.getSlot(pc.getName()).getId());
					bind.setRowDeclared(position);
					// we only add the binding to the map if it doesn't already
					// exist
					if (util.getBinding(pc.getVariableName()) == null) {
						util.addBinding(pc.getVariableName(), bind);
					}
				}
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
					cond.addNode(current);
				}
			}
			if (first != null) {
				attachAlphaNode(otn, first, cond);
			} else {
				// this means there's no value or predicate constraint
			}
		}
	}

	/**
	 * Method will compile exists quantifier
	 * 
	 * @param cond
	 * @param row
	 * @param rule
	 * @param alphaMemory
	 */
	protected void compileExistCondition(ExistCondition cond, int row,
			Rule rule, boolean alphaMemory) {
		if (cond.hasObjectCondition()) {
			ObjectCondition oc = (ObjectCondition) cond.getObjectCondition();
			this.compileObjectConditions(oc, row, rule, alphaMemory);
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

	public void compileJoins(Rule rule, Condition[] conds)
			throws AssertException {
		// only if there's more than 1 condition do we attempt to
		// create the join nodes. A rule with just 1 condition has
		// no joins
		if (conds.length > 1) {
			// previous Condition
			Condition prev = conds[0];
			BaseJoin prevJoin = null;
			BaseJoin bn = null;
			// check if the first condition has any alphaNodes
			// if it does, we add a LeftInputAdapater to the
			// last AlphaNode
			if (prev instanceof ObjectCondition) {
				ObjectCondition cond = (ObjectCondition) prev;
				ObjectTypeNode otn = findObjectTypeNode(cond.getTemplateName());
				// the LeftInputAdapterNode is the first node to propogate to
				// the first joinNode of the rule
				LIANode node = new LIANode(engine.nextNodeId());
				// if the condition doesn't have any nodes, we add the
				// LeftInputAdapter
				// to the ObjectTypeNode, otherwise we add it to the last
				// AlphaNode
				if (cond.getNodes().size() == 0) {
					otn.addSuccessorNode(node, engine, memory);
				} else {
					// add the LeftInputAdapterNode to the last alphaNode
					// we need to see if new LIANode is the same as the existing
					BaseAlpha old = (BaseAlpha) cond.getLastNode();
					if (old instanceof LIANode) {
						node = (LIANode) old;
					} else {
						old.addSuccessorNode(node, engine, memory);
					}
				}
				cond.addNode(node);
			} else if (prev instanceof TestCondition) {
				// for now this is not implemented, since most rules do not
				// have a test condition as the first condition
			} else if (prev instanceof ExistCondition) {
				// if the first CE is an exists CE, we handle it differently
				BaseJoin bjoin = new ExistJoinFrst(engine.nextNodeId());
				ExistCondition cond = (ExistCondition) prev;
				BaseNode base = cond.getLastNode();
				if (base instanceof BaseAlpha) {
					((BaseAlpha) base).addSuccessorNode(bjoin, engine, memory);
				} else if (base instanceof BaseJoin) {
					((BaseJoin) base).addSuccessorNode(bjoin, engine, memory);
				}
				// important, do not call this before ExistJoinFrst is added
				// if it's called first, the arraylist will return index
				// out of bound, since there's nothing in the list
				cond.setIsFirstCE(true);
				cond.addNode(bjoin);
			}
			int negatedCE = 0;
			// now compile the remaining conditions
			for (int idx = 1; idx < conds.length; idx++) {
				Condition cdt = conds[idx];
				ObjectTypeNode otn = null;
				if (cdt instanceof TestCondition) {

					TestCondition tc = (TestCondition) cdt;
					Signature fn = (Signature) tc.getFunction();
					Expression[] oldpm = fn.getParameters();
					Parameter[] pms = new Parameter[oldpm.length];
					for (int ipm = 0; ipm < pms.length; ipm++) {
						if (oldpm[ipm] instanceof JamochaValue) {
							pms[ipm] = (Parameter) oldpm[ipm];
						} else if (oldpm[ipm] instanceof BoundParam) {
							BoundParam bpm = (BoundParam) oldpm[ipm];
							// now we need to resolve and setup the BoundParam
							Binding b = rule.getBinding(bpm.getVariableName());
							BoundParam newpm = new BoundParam(b.getLeftRow(), b
									.getLeftIndex(), 9, bpm.isObjectBinding());
							newpm.setVariableName(bpm.getVariableName());
							pms[ipm] = newpm;
						}
					}
                    if (tc.isNegated()) {
                        bn = new NTestNode(engine.nextNodeId(),fn.lookUpFunction(engine),pms);
                    } else {
                        bn = new TestNode(engine.nextNodeId(),fn.lookUpFunction(engine),pms);
                    }
					if (prevJoin != null) {
						attachJoinNode(prevJoin, (BaseJoin) bn);
					} else {
						attachJoinNode(prev.getLastNode(), (BaseJoin) bn);
					}

				} else if (cdt instanceof AndCondition) {
					// TODO for now this is not done yet
					BooleanOperatorCondition ac = (BooleanOperatorCondition) cdt;
				} else if (cdt instanceof ExistCondition) {
					// TODO finish implementing Exists
					ExistCondition exc = (ExistCondition) cdt;
					boolean hasNotEqual = false;
					Template tmpl = exc.getObjectCondition()
							.getTemplate();

					List blist = exc.getAllBindings();
					Binding[] binds = new Binding[blist.size()];
					for (int idz = 0; idz < binds.length; idz++) {
						Object cst = blist.get(idz);
						if (cst instanceof BoundConstraint) {
							BoundConstraint bc = (BoundConstraint) cst;
							Binding cpy = rule
									.copyBinding(bc.getVariableName());
							if (cpy.getLeftRow() >= idx) {
								binds = new Binding[0];
								break;
							} else {
								binds[idz] = cpy;
								binds[idz].setRightRow(idx - negatedCE);
								int rinx = tmpl.getColumnIndex(bc.getName());
								// we increment the count to make sure the
								// template isn't removed if it is being used
								tmpl.getSlot(rinx).incrementNodeCount();
								binds[idz].setRightIndex(rinx);
								binds[idz].setNegated(bc.getNegated());
								if (bc.getNegated()) {
									hasNotEqual = true;
								}
							}
						} else if (cst instanceof PredicateConstraint) {
							PredicateConstraint pc = (PredicateConstraint) cst;
							if (pc.getValue().getType().equals(JamochaType.BINDING)) {
								BoundParam bpm = (BoundParam) pc.getValue().getObjectValue();
								String var = bpm.getVariableName();
								int op = ConversionUtils.getOperatorCode(pc
										.getFunctionName());
								// if the first binding in the function is from
								// the object type
								// we reverse the operator
								if (pc.getParameters().get(0) != bpm) {
									op = ConversionUtils
											.getOppositeOperatorCode(op);
								}
								binds[idz] = rule.copyPredicateBinding(var, op);
								binds[idz].setRightRow(idx - negatedCE);
								int rinx = tmpl.getColumnIndex(pc.getName());
								// we increment the count to make sure the
								// template isn't removed if it is being used
								tmpl.getSlot(rinx).incrementNodeCount();

								binds[idz].setRightIndex(rinx);
							}
						}
					}
					bn = new ExistJoin(engine.nextNodeId());
					bn.setBindings(binds);
					exc.addNode(bn);
					if (prevJoin != null) {
						attachJoinNode(prevJoin, (BaseJoin) bn);
					} else {
						attachJoinNode(prev.getLastNode(), (BaseJoin) bn);
					}
					attachJoinNode(exc.getLastNode(), (BaseJoin) bn);
					negatedCE++;
				} else {
					boolean hasNotEqual = false;
					ObjectCondition oc = (ObjectCondition) cdt;
					otn = findObjectTypeNode(oc.getTemplateName());
					Template tmpl = oc.getTemplate();

					if (cdt.hasBindings()) {
						// the condition has bindings, so we have to create
						// the bindings and set the joinNode
						List blist = cdt.getBindings();
						Binding[] binds = new Binding[blist.size()];
						Iterator itr = blist.iterator();
						for (int idz = 0; idz < blist.size(); idz++) {
							Object cst = blist.get(idz);
							if (cst instanceof BoundConstraint) {
								BoundConstraint bc = (BoundConstraint) cst;
								Binding cpy = rule.copyBinding(bc
										.getVariableName());
								if (cpy.getLeftRow() >= idx) {
									binds = new Binding[0];
									break;
								} else {
									binds[idz] = cpy;
									binds[idz].setRightRow(idx - negatedCE);
									int rinx = tmpl
											.getColumnIndex(bc.getName());
									// we increment the count to make sure the
									// template isn't removed if it is being
									// used
									tmpl.getSlot(rinx).incrementNodeCount();
									binds[idz].setRightIndex(rinx);
									binds[idz].setNegated(bc.getNegated());
									if (bc.getNegated()) {
										hasNotEqual = true;
									}
								}
							} else if (cst instanceof PredicateConstraint) {
								PredicateConstraint pc = (PredicateConstraint) cst;
								if (pc.getValue().getType().equals(JamochaType.BINDING)) {
									BoundParam bpm = (BoundParam) pc.getValue().getObjectValue();
									String var = bpm.getVariableName();
									int op = ConversionUtils.getOperatorCode(pc
											.getFunctionName());
									// if the first binding in the function is
									// from the object type
									// we reverse the operator
									if (pc.getParameters().get(0) != bpm) {
										op = ConversionUtils
												.getOppositeOperatorCode(op);
									}
									binds[idz] = rule.copyPredicateBinding(var,
											op);
									binds[idz].setRightRow(idx - negatedCE);
									int rinx = tmpl
											.getColumnIndex(pc.getName());
									// we increment the count to make sure the
									// template isn't removed if it is being
									// used
									tmpl.getSlot(rinx).incrementNodeCount();
									binds[idz].setRightIndex(rinx);
								}
							}
						}
						if (!oc.getNegated()) {
							if (binds.length > 0 && !hasNotEqual) {
								bn = new HashedEqBNode(engine.nextNodeId());
							} else if (binds.length > 0 && hasNotEqual) {
								bn = new HashedNotEqBNode(engine.nextNodeId());
							} else if (binds.length == 0) {
								bn = new ZJBetaNode(engine.nextNodeId());
							} else {
								bn = new BetaNode(engine.nextNodeId());
							}
						} else {

							if (binds.length == 0 || hasNotEqual) {
								bn = new HashedNotEqNJoin(engine.nextNodeId());
							} else {
								bn = new HashedEqNJoin(engine.nextNodeId());
							}
							// bn = new NotJoin(engine.nextNodeId());
							negatedCE++;
						}
						bn.setBindings(binds);
					} else {
						// if the condition doesn't have any bindings, we make
						// a join without any bindings
						if (bn == null) {
							bn = new ZJBetaNode(engine.nextNodeId());
						}
						bn.setBindings(new Binding[0]);
						// now add the join node to the last nodes of the
						// current
						// and previous condition
					}
					if (prevJoin != null) {
						attachJoinNode(prevJoin, (BaseJoin) bn);
					} else {
						attachJoinNode(prev.getLastNode(), (BaseJoin) bn);
					}
					if (cdt.getNodes().size() > 0) {
						attachJoinNode(cdt.getLastNode(), (BaseJoin) bn);
					} else {
						otn.addSuccessorNode(bn, engine, engine
								.getWorkingMemory());
					}
				}
				// now we set the previous node to current
				prev = cdt;
				prevJoin = bn;
				rule.addJoinNode(bn);
			}
		} else if (conds.length == 1) {
			// we have to check and see if the rule has a single NOT CE
			if (conds[0] instanceof ObjectCondition) {
				ObjectCondition oc = (ObjectCondition) conds[0];
				if (oc.getNegated()) {
					// the ObjectCondition is negated, so we need to
					// handle it appropriate. This means we need to
					// add a LIANode to _IntialFact and attach a NOTNode
					// to the LIANode.
					ObjectTypeNode otn = (ObjectTypeNode) this.inputnodes
							.get(engine.initFact);
					LIANode lianode = findLeftInputAdapter(otn);
					NotJoin njoin = new NotJoin(engine.nextNodeId());
					njoin.setBindings(new Binding[0]);
					lianode.addSuccessorNode(njoin, engine, memory);
					// add the join to the rule object
					rule.addJoinNode(njoin);
					oc.getLastNode().addNode(njoin);
				} else if (oc.getNodes().size() == 0) {
                    ObjectTypeNode otn = findObjectTypeNode(oc.getTemplateName());
                    LIANode lianode = new LIANode(engine.nextNodeId());
                    otn.addSuccessorNode(lianode, engine, this.memory);
                    rule.getConditions()[0].addNode(lianode);
                }
			}
		}
	}

	/**
	 * The method is responsible for compiling the string form of the actions to
	 * the equivalent functions.
	 * 
	 * @param rule
	 * @param acts
	 * @param util
	 */
	protected void compileActions(Rule rule, Action[] acts) {
		for (int idx = 0; idx < acts.length; idx++) {
			Action atn = acts[idx];
			if (atn instanceof FunctionAction) {
				FunctionAction fa = (FunctionAction) atn;
				try {
					fa.configure(this.engine, rule);
				} catch (EvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// do something else
			}
		}
	}

	protected void attachTerminalNode(BaseNode last, TerminalNode terminal) {
		if (last != null && terminal != null) {
			try {
				if (last instanceof BaseJoin) {
					((BaseJoin) last)
							.addSuccessorNode(terminal, engine, memory);
				} else if (last instanceof BaseAlpha) {
					((BaseAlpha) last).addSuccessorNode(terminal, engine,
							memory);
				}
			} catch (AssertException e) {

			}
		}
	}

	/**
	 * Method will attach a new JoinNode to an ancestor node. An ancestor could
	 * be LIANode, AlphaNode or BetaNode.
	 * 
	 * @param last
	 * @param join
	 * @throws AssertException
	 */
	protected void attachJoinNode(BaseNode last, BaseJoin join)
			throws AssertException {
		if (last instanceof BaseAlpha) {
			((BaseAlpha) last).addSuccessorNode(join, engine, memory);
		} else if (last instanceof BaseJoin) {
			((BaseJoin) last).addSuccessorNode(join, engine, memory);
		}
	}

	protected LIANode findLeftInputAdapter(ObjectTypeNode otn) {
		if (initialFactLIANode == null) {
			if (otn.getSuccessorNodes() != null
					&& otn.getSuccessorNodes().length > 0) {
				BaseNode[] nodes = (BaseNode[]) otn.getSuccessorNodes();
				for (int idx = 0; idx < nodes.length; idx++) {
					if (nodes[idx] instanceof LIANode) {
						initialFactLIANode = (LIANode) nodes[idx];
						break;
					}
				}
			} else {
				initialFactLIANode = new LIANode(engine.nextNodeId());
				try {
					otn.addSuccessorNode(initialFactLIANode, engine,
							this.memory);
				} catch (AssertException e) {
					log.warn(e);
				}
			}
		}
		return initialFactLIANode;
	}

	public void fireErrorEvent(Object reason) {

	}

	/**
	 * basic method iterates over the listeners and passes the event, checking
	 * what kind of event it is and calling the appropriate method.
	 * 
	 * @param event
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
}
