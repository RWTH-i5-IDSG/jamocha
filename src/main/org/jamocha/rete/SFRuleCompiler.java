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
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Analysis;
import org.jamocha.rule.Condition;
import org.jamocha.rule.ExistCondition;
import org.jamocha.rule.ObjectCondition;
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

	public static final String FUNCTION_NOT_FOUND = Messages.getString("CompilerProperties.function.not.found"); //$NON-NLS-1$

	public static final String INVALID_FUNCTION = Messages.getString("CompilerProperties.invalid.function"); //$NON-NLS-1$

	public static final String ASSERT_ON_PROPOGATE = Messages.getString("CompilerProperties.assert.on.add"); //$NON-NLS-1$

	protected DefaultLogger log = new DefaultLogger(BasicRuleCompiler.class);

	public SFRuleCompiler(Rete engine, WorkingMemory mem, Map<Deftemplate, ObjectTypeNode> inputNodes) {
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
	 * The method creates the right terminal node based on the settings of the rule
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
	 * The method adds an ObjectTypeNode to the HashMap. 
	 * This implementation uses the Deftemplate as HashMap key and the Node as value. 
	 * If the Node or the key already exists in the HashMap the compiler will not add it to the network.
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
	 * @param ObjectTypeNode node
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
	 * The method returns the ObjectTypeNode for the given Deftemplate name.
	 * If no ObjectTypeNode is found with this name the method returns null.
	 * 
	 * @param String templateName
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
			return (ObjectTypeNode) this.inputnodes.get(tmpl);
		} else {
			log.debug(Messages.getString("RuleCompiler.deftemplate.error")); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * @author Peter Lin
	 * 
	 * The method gets the ObjectTypeNode from the HashMap and returns it. 
	 * If the node does not exist, the method will return null.
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
		if (!this.validate || (this.validate && this.tval.analyze(rule) == Analysis.VALIDATION_PASSED)) {
			if (rule.getConditions() != null && rule.getConditions().length > 0) {
				// we check the name of the rule to see if it is for a specific
				// module. if it is, we have to add it to that module
				this.setModule(rule);
				try {
					Condition[] conds = rule.getConditions();
					// first we create the constraints and then the conditional elements which include joins
					for (int idx = 0; idx < conds.length; idx++) {
						Condition con = conds[idx];
						// compile object conditions
						if (con instanceof ObjectCondition) {
							ObjectCondition oc = (ObjectCondition) con;
							///							this.compileObjectConditions(oc, idx, rule, rule.getRememberMatch());
						} else if (con instanceof ExistCondition) {
							ExistCondition econd = (ExistCondition) con;
							///							this.compileExistCondition(econd, idx, rule,  rule.getRememberMatch());
						} else if (con instanceof TestCondition) {

						}
					}
					///					compileJoins(rule, conds);

					BaseNode last = rule.getLastNode();
					TerminalNode tnode = createTerminalNode(rule);

					///					attachTerminalNode(last, tnode);
					// compile the actions
					///					compileActions(rule, rule.getActions());
					// now we pass the bindings to the rule, so that actiosn can
					// resolve the bindings

					// now we add the rule to the module
					currentMod.addRule(rule);
					CompileEvent ce = new CompileEvent(rule, CompileEvent.ADD_RULE_EVENT);
					ce.setRule(rule);
					this.notifyListener(ce);
					return true;
				} 
				catch (AssertException e) {
					CompileEvent ce = new CompileEvent(rule, CompileEvent.INVALID_RULE);
					ce.setMessage(Messages.getString("RuleCompiler.assert.error")); //$NON-NLS-1$
					this.notifyListener(ce);
					log.debug(e);
					return false;
				} 
			} else if (rule.getConditions().length == 0) {
				this.setModule(rule);
				// the rule has no LHS, this means it only has actions
				BaseNode last = (BaseNode) this.inputnodes.get(engine.initFact);
				TerminalNode tnode = createTerminalNode(rule);
				///				compileActions(rule, rule.getActions());
				///				attachTerminalNode(last, tnode);
				// now we add the rule to the module
				currentMod.addRule(rule);
				CompileEvent ce = new CompileEvent(rule, CompileEvent.ADD_RULE_EVENT);
				ce.setRule(rule);
				///				this.notifyListener(ce);
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

	/**
	 * @author Peter Lin
	 * 
	 * The method passes the event to all the CompilerListeners registered with this RuleCompiler.
	 * Furthermore, it checks what kind of event it is and calls the most appropriate method :-).
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
}
