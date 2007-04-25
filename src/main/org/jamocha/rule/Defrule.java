/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Binding2;
import org.jamocha.rete.Constants;
import org.jamocha.rete.ExpressionSequence;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Module;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Scope;
import org.jamocha.rete.Template;
import org.jamocha.rete.configurations.DeclarationConfiguration;
import org.jamocha.rete.configurations.DefruleConfiguration;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.util.CollectionsFactory;

/**
 * @author Peter Lin
 * 
 * A basic implementation of the Rule interface
 */
public class Defrule implements Rule, Scope, Serializable {

	private static final long serialVersionUID = 1L;

	protected String name = null;

	protected ArrayList conditions = null;

	protected ArrayList actions = null;

	protected ArrayList joins = null;

	protected int salience = 100;

	protected boolean auto = false;

	protected Complexity complex = null;

	protected boolean rememberMatch = true;

	/**
	 * by default noAgenda is false
	 */
	protected boolean noAgenda = false;

	protected String version = "";

	protected Module themodule = null;

	protected Map bindValues = CollectionsFactory.localMap();
	
	protected Scope outerScope = null;

	private LinkedHashMap bindings = new LinkedHashMap();

	private String description = "";

	/**
	 * by default a rule is active, unless set to false
	 */
	private boolean active = true;

	/**
	 * Be default, the rule is set to forward chaining
	 */
	protected int direction = Constants.FORWARD_CHAINING;

	/**
	 * by default watch is off
	 */
	protected boolean watch = false;

	/**
	 * default is set to zero
	 */
	protected long effectiveDate = 0;

	/**
	 * default is set to zero
	 */
	protected long expirationDate = 0;

	protected Fact[] triggerFacts = null;

	/**
	 * 
	 */
	public Defrule() {
		super();
		conditions = new ArrayList();
		actions = new ArrayList();
		joins = new ArrayList();
	}

	public Defrule(String name) {
		this();
		setName(name);
	}

	public Defrule(DefruleConfiguration configuration, Rete engine) throws EvaluationException {
		this();
		// set rule name:
		setName(configuration.getRuleName());
		// set rule description:
		setDescription(configuration.getRuleDescription());
		// set rule declaration:
		setDeclaration(configuration.getDeclarationConfiguration(), engine);
		// set conditions:
		setConditions(configuration.getConditions());
		// set conditions:
		setActions(configuration.getActions());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Rule#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Rule#setName()
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * by default a rule should return true
	 * 
	 * @return
	 */
	public boolean isActive() {
		return this.active;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Rule#getWatch()
	 */
	public boolean getWatch() {
		return watch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Rule#setWatch(boolean)
	 */
	public void setWatch(boolean watch) {
		this.watch = watch;
	}

	public void setDeclaration(DeclarationConfiguration declarationConfiguration, Rete engine) throws EvaluationException {
		if (declarationConfiguration != null) {
			Parameter param = null;

			// set autofocus
			param = declarationConfiguration.getAutoFocus();
			if (param != null)
				setAutoFocus(param.getValue(engine).getBooleanValue());

			// set version
			param = declarationConfiguration.getVersion();
			if (param != null)
				setVersion(param.getValue(engine).getStringValue());

			// set salience
			param = declarationConfiguration.getSalience();
			if (param != null)
				setSalience(param.getValue(engine).getLongValue());
		}
	}

	public boolean getAutoFocus() {
		return this.auto;
	}

	public void setAutoFocus(boolean auto) {
		this.auto = auto;
	}

	public int getSalience() {
		return this.salience;
	}

	public void setSalience(int sal) {
		this.salience = sal;
	}

	public void setSalience(long sal) {
		this.salience = (int) sal;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String text) {
		if (text != null)
			description = text;
		else description = "";
	}

	public Complexity getComplexity() {
		return this.complex;
	}

	public void setComplexity(Complexity complexity) {
		this.complex = complexity;
	}

	public long getEffectiveDate() {
		return this.effectiveDate;
	}

	public long getExpirationDate() {
		return this.expirationDate;
	}

	public void setEffectiveDate(long mstime) {
		this.effectiveDate = mstime;
	}

	public void setExpirationDate(long mstime) {
		this.expirationDate = mstime;
	}

	public boolean getNoAgenda() {
		return this.noAgenda;
	}

	public void setNoAgenda(boolean agenda) {
		this.noAgenda = agenda;
	}

	public boolean getRememberMatch() {
		return this.rememberMatch;
	}

	public void setRememberMatch(boolean remember) {
		this.rememberMatch = remember;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String ver) {
		if (ver != null) {
			this.version = ver;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Rule#addCondition(woolfel.engine.rule.Condition)
	 */
	public void addCondition(Condition cond) {
		conditions.add(cond);
	}

	public void setConditions(Condition[] conds) {
		Condition cond = null;
		for (int i = 0; i < conds.length; i++) {
			cond = conds[i];
			addCondition(cond);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Rule#addAction(woolfel.engine.rule.Action)
	 */
	public void addAction(Action act) {
		actions.add(act);
	}

	public void setActions(ExpressionSequence actions) {
		for (int i = 0; i < actions.size(); ++i) {
			Expression acn = actions.get(i);
			if (acn instanceof Signature) {
				FunctionAction faction = new FunctionAction();
				faction.setFunction((Signature) acn);
				addAction(faction);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Rule#getConditions()
	 */
	public Condition[] getConditions() {
		Condition[] cond = new Condition[conditions.size()];
		conditions.toArray(cond);
		return cond;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rule.Rule#getActions()
	 */
	public Action[] getActions() {
		Action[] acts = new Action[actions.size()];
		actions.toArray(acts);
		return acts;
	}

	public void setModule(Module mod) {
		this.themodule = mod;
	}

	public Module getModule() {
		return this.themodule;
	}

	/**
	 * add join nodes to the rule
	 */
	public void addJoinNode(BaseJoin node) {
		this.joins.add(node);
	}

	/**
	 * get the array of join nodes
	 */
	public List getJoins() {
		return this.joins;
	}

	public BaseNode getLastNode() {
		if (this.joins.size() > 0) {
			return (BaseNode) this.joins.get(this.joins.size() - 1);
		} else if (conditions.size() > 0) {
			// this means there's only 1 ConditionalElement, so the conditions
			// only has 1 element. in all other cases, there will be atleast
			// 1 join node
			Condition c = (Condition) this.conditions.get(0);
			if (c instanceof ObjectCondition) {
				return ((ObjectCondition) c).getLastNode();
			} else if (c instanceof TestCondition) {
				return ((TestCondition) c).getTestNode();
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * the current implementation simply replaces the existing value if one
	 * already exists.
	 */
	public void setBindingValue(String key, JamochaValue value) {
		this.bindValues.put(key, value);
	}

	/**
	 * return the value associated with the binding
	 */
	public JamochaValue getBindingValue(String key) {
		JamochaValue val = (JamochaValue) this.bindValues.get(key);
		if (val == null) {
			Binding bd = (Binding) this.bindings.get(key);
			if (bd != null) {
				Fact left = this.triggerFacts[bd.getLeftRow()];
				if (bd.getIsObjectVar()) {
					val = JamochaValue.newFact(left);
				} else {
					val = left.getSlotValue(bd.getLeftIndex());
				}
			}
		}
		return val;
	}

	/**
	 * This should be called when the action is being fired. after the rule
	 * actions are executed, the trigger facts should be reset. The primary
	 * downside of this design decision is it won't work well with multi-
	 * threaded parallel execution. Since Sumatra has no plans for implementing
	 * parallel execution using multi-threading, the design is not an issue.
	 * Implementing multi-threaded parallel execution isn't desirable and has
	 * been proven to be too costly. A better approach is to queue assert
	 * retract and process them in sequence.
	 */
	public void setTriggerFacts(Fact[] facts) {
		this.triggerFacts = facts;
	}

	/**
	 * reset the trigger facts after all the actions have executed.
	 */
	public void resetTriggerFacts() {
		this.triggerFacts = null;
	}

	public Fact[] getTriggerFacts() {
		return this.triggerFacts;
	}

	/**
	 * Method will only add the binding if it doesn't already exist.
	 * 
	 * @param bind
	 */
	public void addBinding(String key, Binding bind) {
		if (!this.bindings.containsKey(key)) {
			this.bindings.put(key, bind);
		}
	}

	/**
	 * Return the Binding matching the variable name
	 * 
	 * @param varName
	 * @return
	 */
	public Binding getBinding(String varName) {
		return (Binding) this.bindings.get(varName);
	}

	/**
	 * Get a copy of the Binding using the variable name
	 * 
	 * @param varName
	 * @return
	 */
	public Binding copyBinding(String varName) {
		Binding b = getBinding(varName);
		if (b != null) {
			Binding b2 = (Binding) b.clone();
			return b2;
		} else {
			return null;
		}
	}

	public Binding copyPredicateBinding(String varName, int operator) {
		Binding b = getBinding(varName);
		if (b != null) {
			Binding2 b2 = new Binding2(operator);
			b2.setLeftRow(b.getLeftRow());
			b2.setLeftIndex(b.getLeftIndex());
			return b2;
		} else {
			return null;
		}
	}

	/**
	 * The method will return the Bindings in the order they were added to the
	 * utility.
	 * 
	 * @return
	 */
	public Iterator getBindingIterator() {
		return this.bindings.values().iterator();
	}

	/**
	 * Returns the number of unique bindings. If a binding is used multiple
	 * times to join several facts, it is only counted once.
	 * 
	 * @return
	 */
	public int getBindingCount() {
		return this.bindings.size();
	}

	public void resolveTemplates(Rete engine) {
		Condition[] cnds = this.getConditions();
		for (int idx = 0; idx < cnds.length; idx++) {
			Condition cnd = cnds[idx];
			if (cnd instanceof ObjectCondition) {
				ObjectCondition oc = (ObjectCondition) cnd;
				Template dft = (Template) engine.findTemplate(oc.getTemplateName());
				if (dft != null) {
					oc.setTemplate(dft);
				}
			} else if (cnd instanceof ExistCondition) {
				// in the case of Exist, we have to check the nested and resolve
				// the deftemplate
				ExistCondition ec = (ExistCondition) cnd;
				if (ec.hasObjectCondition()) {
					ObjectCondition oc = ec.getObjectCondition();
					Template dft = (Template) engine.findTemplate(oc.getTemplateName());
					if (dft != null) {
						oc.setTemplate(dft);
					}
				}
			}
		}
	}

	public void setRuleProperties(List props) {
		Iterator itr = props.iterator();
		while (itr.hasNext()) {
			RuleProperty declaration = (RuleProperty) itr.next();
			if (declaration.getName().equals(RuleProperty.AUTO_FOCUS)) {
				setAutoFocus(declaration.getBooleanValue());
			} else if (declaration.getName().equals(RuleProperty.SALIENCE)) {
				setSalience(declaration.getIntValue());
			} else if (declaration.getName().equals(RuleProperty.VERSION)) {
				setVersion(declaration.getValue());
			} else if (declaration.getName().equals(RuleProperty.REMEMBER_MATCH)) {
				setRememberMatch(declaration.getBooleanValue());
			} else if (declaration.getName().equals(RuleProperty.NO_AGENDA)) {
				setNoAgenda(declaration.getBooleanValue());
			} else if (declaration.getName().equals(RuleProperty.EFFECTIVE_DATE)) {
				this.effectiveDate = getDateTime(declaration.getValue());
			} else if (declaration.getName().equals(RuleProperty.EXPIRATION_DATE)) {
				this.expirationDate = getDateTime(declaration.getValue());
			}
		}

	}

	public static long getDateTime(String date) {
		if (date != null && date.length() > 0) {
			try {
				java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("mm/dd/yyyy HH:mm");
				return df.parse(date).getTime();
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		} else {
			return 0;
		}
	}

	public void clear() {
		Iterator itr = this.conditions.iterator();
		while (itr.hasNext()) {
			Condition cond = (Condition) itr.next();
			cond.clear();
		}
		this.joins.clear();
	}

	/**
	 * TODO need to finish implementing the clone method
	 */
	public Object clone() {
		Defrule cl = new Defrule(this.name);
		return cl;
	}

	public boolean hasBindingInTotalRange(String name) {
		return bindValues.containsKey(name);
	}

	public Scope popScope() {
		return outerScope;
	}

	public void pushScope(Scope scope) {
		outerScope = scope;
	}

	public Scope getOuterScope() {
		return outerScope;
	}

}
