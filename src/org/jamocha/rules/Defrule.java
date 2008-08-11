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

package org.jamocha.rules;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ExpressionSequence;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.configurations.DeclarationConfiguration;
import org.jamocha.engine.configurations.DefruleConfiguration;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.functions.FunctionNotFoundException;
import org.jamocha.engine.modules.Module;
import org.jamocha.engine.scope.Scope;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * @author Josef Alexander Hahn
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

	protected int salience = 100;

	protected boolean autoFocus = false;

	protected Module module = null;

	protected Scope outerScope = null;

	private String description = "";

	private boolean active = true;

	protected boolean watch = false;

	protected long effectiveDate = 0;

	protected long expirationDate = 0;

	/**
	 * 
	 */
	protected Defrule() {
		super();
		subRules = new ArrayList<Rule>();
	}

	public Defrule(Module module, String name, List<Condition> lhs, List<Action> rhs) {
		this();
		this.conditions = lhs;
		this.actions = rhs;
		this.name = name;
		this.module = module;
	}

	public Defrule(Module module, DefruleConfiguration configuration, Engine engine) throws EvaluationException {
		this();
		this.module = module;
		// set rule name:
		name=configuration.getRuleName();
		// set rule description:
		description=configuration.getRuleDescription();
		// set rule declaration:
		setDeclaration(configuration.getDeclarationConfiguration(), engine);
		// set conditions:
		conditions = new ArrayList<Condition>();
		
		Condition[] arrConds = configuration.getConditions();

		for (Condition c : arrConds) conditions.add(c);
		setActions(configuration.getActions(), engine);
		
	}

	public String getName() {
		return name;
	}


	public boolean isActive() {
		return this.active;
	}

	public boolean getWatch() {
		return watch;
	}

	protected void setDeclaration(DeclarationConfiguration declarationConfiguration, Engine engine) throws EvaluationException {
		if (declarationConfiguration != null) {
			Parameter param = null;

			// set autofocus
			param = declarationConfiguration.getAutoFocus();
			if (param != null)
				setAutoFocus(param.getValue(engine).getBooleanValue());

			// set version
			//TODO remove lines here param = declarationConfiguration.getVersion();
			//if (param != null)
			//	setVersion(param.getValue(engine).getStringValue());

			// set salience
			param = declarationConfiguration.getSalience();
			if (param != null)
				setSalience(param.getValue(engine).getLongValue());
		}
	}

	public boolean getAutoFocus() {
		return this.autoFocus;
	}

	public void setAutoFocus(boolean auto) {
		this.autoFocus = auto;
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

	public List<Condition> getConditions() {
		return conditions;
	}

	public List<Action> getActions() {
		return actions;
	}

	public Module parentModule() {
		return module;
	}

	protected static long getDateTime(String date) {
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
		int complexity = 0;
		for (Condition c: conditions) complexity += c.getComplexity();
		return complexity;
	}

	public Defrule clone(Engine engine) throws CloneNotSupportedException {
		// set rule description:

		// set rule declaration:

		DeclarationConfiguration newDecl = new DeclarationConfiguration();

		newDecl.setAutoFocus(JamochaValue.newBoolean(this.getAutoFocus()));
		newDecl.setSalience(JamochaValue.newLong(this.getSalience()));
		//TODO remove it newDecl.setVersion(JamochaValue.newString(this.getVersion()));


		List<Condition> conditions = new ArrayList<Condition>();
		for (Condition c : getConditions()) {
			conditions.add(c);
		}
		// set conditions:
		
		// set actions:
		
		Defrule newRule = new Defrule(module,getName(),conditions, actions);
		try {
			newRule.setDeclaration(newDecl, engine);
		} catch (EvaluationException e) {
			engine.writeMessage(e.getMessage());
		}
		
		newRule.setDescription(getDescription());
		
		// set super rule:
		
		if (this.superRule != null) {
			this.superRule.addSubRule(newRule);
			newRule.superRule = this.superRule;
		} else {
			subRules.add(newRule);
			newRule.superRule = this;
		}

		return newRule;
	}


	public Defrule clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
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
		return parentModule().getName() + "::" + getName();
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
		if (!other.parentModule().getName().equals(module.getName()))
			return false;
		return true;
	}

	/**
	 * generates text-dump from rule
	 */
	public String getDump() {
		return toString();
	}

	public void setDescription(String text) {
		description = text;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private void setActions(ExpressionSequence actions, Engine engine) {
		this.actions = new ArrayList<Action>();
		for (int i = 0; i < actions.size(); ++i) {
			Expression acn = actions.get(i);
			if (acn instanceof Signature) {
				Signature sig = (Signature)acn;
				Function function=null;
				try {
					function = sig.lookUpFunction(engine);
				} catch (FunctionNotFoundException e) {
					Logging.logger(this.getClass()).fatal(e);
				}
				List<Parameter> params = new ArrayList<Parameter>();
				for (Parameter p : sig.getParameters()) params.add(p);
				FunctionAction faction = new FunctionAction(function, engine, this, params);
				this.actions.add(faction);
			}
		}
	}

}
