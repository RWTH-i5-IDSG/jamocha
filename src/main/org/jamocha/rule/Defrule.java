/*
 * 
 * Copyright 2002-2006 Peter Lin
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
package org.jamocha.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Constants;
import org.jamocha.rete.ExpressionSequence;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Scope;
import org.jamocha.rete.Template;
import org.jamocha.rete.configurations.DeclarationConfiguration;
import org.jamocha.rete.configurations.DefruleConfiguration;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.modules.Module;
import org.jamocha.rete.nodes.TerminalNode;

/**
 * @author Peter Lin
 * 
 * A basic implementation of the Rule interface
 */
public class Defrule implements Rule {

	private static final long serialVersionUID = 1L;

	protected Rule superRule;

	protected List<Rule> subRules;

	protected String name = null;

	protected List<Condition> conditions = null;

	protected List<Action> actions = null;

	protected TerminalNode terminal = null;

	protected int salience = 100;

	protected boolean auto = false;

	protected boolean rememberMatch = true;

	/**
	 * by default noAgenda is false
	 */
	protected boolean noAgenda = false;

	protected String version = "";

	protected Module themodule = null;

	protected Map<String, JamochaValue> bindValues = new HashMap<String, JamochaValue>();

	protected Scope outerScope = null;

	private LinkedHashMap<String, Binding> bindings = new LinkedHashMap<String, Binding>();

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

	private int complexity = 1;

	private int totalComplexity = 0;

	/**
	 * 
	 */
	public Defrule() {
		super();
		subRules = new ArrayList<Rule>();
		conditions = new ArrayList<Condition>();
		actions = new ArrayList<Action>();
	}

	public Defrule(String name) {
		this();
		setName(name);
	}

	public Defrule(DefruleConfiguration configuration, Rete engine)
			throws EvaluationException {
		this();
		totalComplexity = configuration.getTotalComplexity();
		// set rule name:
		setName(configuration.getRuleName());
		// set rule description:
		setDescription(configuration.getRuleDescription());
		// set rule declaration:
		setDeclaration(configuration.getDeclarationConfiguration(), engine);
		// set conditions:
		setConditions(configuration.getConditions());
		// set actions:
		setActions(configuration.getActions());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rule.Rule#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rule.Rule#setName()
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
	 * @see org.jamocha.rule.Rule#getWatch()
	 */
	public boolean getWatch() {
		return watch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rule.Rule#setWatch(boolean)
	 */
	public void setWatch(boolean watch) {
		this.watch = watch;
	}

	public void setDeclaration(
			DeclarationConfiguration declarationConfiguration, Rete engine)
			throws EvaluationException {
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
		else
			description = "";
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
	 * @see org.jamocha.rule.Rule#addCondition(org.jamocha.rule.Condition)
	 */
	public void addCondition(Condition cond) {
		conditions.add(cond);
	}

	public void setConditions(List<Condition> conds) {
		this.conditions = conds;
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
	 * @see org.jamocha.rule.Rule#addAction(org.jamocha.rule.Action)
	 */
	public void addAction(Action act) {
		actions.add(act);
	}

	public void setActions(Action[] actions) {
		for (Action a : actions)
			addAction(a);
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
	 * @see org.jamocha.rule.Rule#getConditions()
	 */
	public Condition[] getConditions() {
		Condition[] cond = new Condition[conditions.size()];
		conditions.toArray(cond);
		return cond;
	}

	public Condition[] getObjectConditions() {
		ArrayList<Condition> ocs = new ArrayList<Condition>();
		for (Condition c : this.conditions) {
			if (c instanceof ObjectCondition) {
				ocs.add((Condition) c);
			} else if (c instanceof NotCondition) {
				ocs.add((Condition) c);
			} else if (c instanceof ExistCondition) {
				ocs.add((Condition) c);
			}
		}
		Condition[] cond = new Condition[ocs.size()];
		ocs.toArray(cond);
		return cond;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rule.Rule#getActions()
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
	public void SetTerminalNode(TerminalNode node) {
		this.terminal = node;
	}

	/**
	 * get the array of join nodes
	 */
	public TerminalNode getTerminalNode() {
		return this.terminal;
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
		// first we check bind values: may set via (bind ?x.....)
		JamochaValue val = this.bindValues.get(key);
		if (val == null) {
			// nothing found: check own bindings:
			Binding bd = this.bindings.get(key);
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

	public Map<String, JamochaValue> getBindings() {
		return bindValues;
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
		return this.bindings.get(varName);
	}

	public void resolveTemplates(Rete engine) {
		Condition[] cnds = this.getConditions();
		for (int idx = 0; idx < cnds.length; idx++) {
			Condition cnd = cnds[idx];
			resolveTemplate(engine, cnd);
		}
	}

	public void resolveTemplate(Rete engine, Condition cond) {
		if (cond instanceof ObjectCondition) {
			ObjectCondition oc = (ObjectCondition) cond;
			Template dft = (Template) engine.findTemplate(oc.getTemplateName());
			if (dft != null) {
				oc.setTemplate(dft);
			}
		} else if (cond instanceof ConditionWithNested) {
			// reslove all templates from nested conditions:
			List<Condition> nestedConds = ((ConditionWithNested) cond)
					.getNestedConditionalElement();
			for (Condition nestedCond : nestedConds) {
				resolveTemplate(engine, nestedCond);
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
			} else if (declaration.getName()
					.equals(RuleProperty.REMEMBER_MATCH)) {
				setRememberMatch(declaration.getBooleanValue());
			} else if (declaration.getName().equals(RuleProperty.NO_AGENDA)) {
				setNoAgenda(declaration.getBooleanValue());
			} else if (declaration.getName()
					.equals(RuleProperty.EFFECTIVE_DATE)) {
				this.effectiveDate = getDateTime(declaration.getValue());
			} else if (declaration.getName().equals(
					RuleProperty.EXPIRATION_DATE)) {
				this.expirationDate = getDateTime(declaration.getValue());
			}
		}

	}

	public static long getDateTime(String date) {
		if (date != null && date.length() > 0) {
			try {
				java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
						"mm/dd/yyyy HH:mm");
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
		Iterator<Condition> itr = this.conditions.iterator();
		while (itr.hasNext()) {
			Condition cond = itr.next();
			cond.clear();
		}
		this.terminal = null;
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

	public int getComplexity() {
		return complexity;
	}

	public int getTotalComplexity() {
		return (complexity + totalComplexity);
	}

	public void incrementTotalComplexityBy(int value) {
		totalComplexity += value;
	}

	public void setComplexity(int value) {
		complexity = value;
	}

	public void setConditionIndex(int index, Condition c) {
		conditions.set(index, c);
	}

	public Defrule clone(Rete engine) throws CloneNotSupportedException {
		Defrule newRule = new Defrule();
		newRule.totalComplexity = getTotalComplexity();
		// set rule name:
		newRule.setName(getName());
		// set rule description:
		newRule.setDescription(getDescription());

		// set super rule:

		if (this.superRule != null) {
			this.superRule.addSubRule(newRule);
			newRule.superRule = this.superRule;
		} else {
			subRules.add(newRule);
			newRule.superRule = this;
		}

		// set rule declaration:

		DeclarationConfiguration newDecl = new DeclarationConfiguration();

		newDecl.setAutoFocus(new JamochaValue(this.getAutoFocus()));
		newDecl.setSalience(new JamochaValue(this.getSalience()));
		newDecl.setVersion(new JamochaValue(this.getVersion()));

		try {
			newRule.setDeclaration(newDecl, engine);
		} catch (EvaluationException e) {
			engine.writeMessage(e.getMessage());
		}

		List<Condition> conditions = new ArrayList<Condition>();
		for (Condition c : getConditions()) {
			conditions.add((Condition) c.clone());
		}
		// set conditions:
		newRule.setConditions(conditions);

		// set actions:
		ArrayList actions = new ArrayList();
		for (Action a : this.actions)
			actions.add(a.clone());
		newRule.actions = actions;
		return newRule;
	}

	public void removeCondition(int idx) {
		conditions.remove(idx);
	}

	public Defrule clone() throws CloneNotSupportedException {

		// since it is not needed and old implementation
		// is wrong at some points
		throw new CloneNotSupportedException();

		// Defrule newRule = new Defrule();
		// newRule.name = name;
		// newRule.conditions = (ArrayList<Condition>)((ArrayList<Condition>)
		// conditions).clone();
		//
		// newRule.superRule = this.superRule;
		//		
		// ArrayList actions = new ArrayList();
		// newRule.actions = actions;
		//
		// for (Action a : this.actions)
		// actions.add(a.clone());
		//
		// newRule.terminal = terminal;
		// newRule.salience = salience;
		// newRule.auto = auto;
		// newRule.rememberMatch = rememberMatch;
		// newRule.noAgenda = noAgenda;
		// newRule.version = version;
		// newRule.themodule = themodule;
		// newRule.bindValues = bindValues;
		// newRule.outerScope = outerScope;
		// newRule.bindings = bindings;
		// newRule.description = description;
		// newRule.active = active;
		// newRule.direction = direction;
		// newRule.watch = watch;
		// newRule.effectiveDate = effectiveDate;
		// newRule.expirationDate = expirationDate;
		// newRule.triggerFacts = triggerFacts;
		// newRule.complexity = complexity;
		// newRule.totalComplexity = totalComplexity;
		// return newRule;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public Rule getSuperRule() {
		return superRule;
	}

	public void setSuperRule(Rule superRule) {
		this.superRule = superRule;
	}

	public void addSubRule(Rule rule) {
		subRules.add(rule);
	}

	public List<Rule> getSubRules() {
		return subRules;
	}

	public String toString() {
		return getModule().getModuleName() + "::" + getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		final Rule other = (Rule) obj;
		if (!other.getName().equals(name))
			return false;
		if (!other.getModule().getModuleName()
				.equals(themodule.getModuleName()))
			return false;
		return true;
	}
}
