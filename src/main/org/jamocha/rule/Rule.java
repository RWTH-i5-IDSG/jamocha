/*
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

import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Module;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Scope;
import org.jamocha.rete.nodes.TerminalNode;

/**
 * @author Peter Lin
 * 
 * The rule interface design is influenced by RuleML 0.8. It is also influenced
 * by CLIPS, but with some important differences. The interface assumes it acts
 * as a bridge between a Rule Parser, which parses some text and produces the
 * necessary artifacts and a rule compiler which generates RETE nodes. For that
 * reason, the interface defines methods for adding Join nodes and retrieving
 * the last node in the rule. These convienance method are present to make it
 * easier to write rule parsers and compilers.
 */
public interface Rule extends Scope, Complexity {
	/**
	 * if the rule is set to autofocus, it returns true
	 * 
	 * @return
	 */
	boolean getAutoFocus();

	/**
	 * if the rule should fire even when the module is not in focus, call the
	 * method with true
	 * 
	 * @param auto
	 */
	void setAutoFocus(boolean auto);

	/**
	 * if users want to give a rule a comment, the method will return it.
	 * otherwise it should return zero length string
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * set the comment of the rule. it should be a descriptive comment about
	 * what the rule does.
	 * 
	 * @param text
	 */
	void setDescription(String text);

	/**
	 * Be default classes implementing the interface should set the effective
	 * date to zero. only when the user sets the date should it have a non-zero
	 * positive long value.
	 * 
	 * @param mstime
	 */
	void setEffectiveDate(long mstime);

	/**
	 * return the effective date in milliseconds
	 * 
	 * @return
	 */
	long getEffectiveDate();

	/**
	 * by default classes implementing the interface should set the expiration
	 * date to zero. only when the user sets the date should it have a non-zero
	 * positive value greater than the effective date.
	 * 
	 * @param mstime
	 */
	void setExpirationDate(long mstime);

	/**
	 * return the expiration date in milliseconds
	 * 
	 * @return
	 */
	long getExpirationDate();

	/**
	 * add a conditional element to the rule
	 * 
	 * @param cond
	 */
	/**
	 * get the name of the rule
	 * 
	 * @return
	 */
	String getName();

	/**
	 * set the name of the rule
	 * 
	 * @param name
	 */
	void setName(String name);

	/**
	 * if the rule should skip the agenda and fire immediately, the method
	 * returns true. By default it should be false
	 * 
	 * @return
	 */
	boolean getNoAgenda();

	/**
	 * if a rule should skip the agenda, set it to true
	 * 
	 * @param agenda
	 */
	void setNoAgenda(boolean agenda);

	/**
	 * classes implementing the interface can choose to ignore this rule
	 * property. Sumatra currently provides the ability to turn off AlphaMemory.
	 * By default, it is set to true. If a user wants to turn off AlphaMemory
	 * for a given rule, set it to false.
	 * 
	 * @return
	 */
	boolean getRememberMatch();

	/**
	 * to turn of alpha memory, set it to false
	 * 
	 * @param match
	 */
	void setRememberMatch(boolean match);

	/**
	 * get the salience of the rule
	 * 
	 * @return
	 */
	int getSalience();

	/**
	 * to lower the priority of a rule, set the value lower
	 * 
	 * @param sal
	 */
	void setSalience(int sal);

	/**
	 * the version of the rule
	 * 
	 * @return
	 */
	String getVersion();

	/**
	 * set the version of the rule
	 * 
	 * @param ver
	 */
	void setVersion(String ver);

	/**
	 * watch is used for debugging
	 * 
	 * @return
	 */
	boolean getWatch();

	/**
	 * to debug a rule, set the watch to true
	 * 
	 * @param watch
	 */
	void setWatch(boolean watch);

	/**
	 * to turn off a rule, call the method with false
	 * 
	 * @param active
	 */
	void setActive(boolean active);

	/**
	 * by default a rule should return true
	 * 
	 * @return
	 */
	boolean isActive();

	void addCondition(Condition cond);

	void addAction(Action act);

	Condition[] getConditions();
	
	Condition[] getSortedConditions();

	Action[] getActions();

	void SetTerminalNode(TerminalNode node);
	
	TerminalNode getTerminalNode();

	/**
	 * When the rule is compiled, the rule compiler needs to set the module so
	 * that the terminalNode can add the activation to the correct
	 * activationList.
	 * 
	 * @param mod
	 */
	void setModule(Module mod);

	/**
	 * Return the module the rule belongs to. A rule can only belong to a single
	 * module.
	 * 
	 * @return
	 */
	Module getModule();

	/**
	 * A rule action can create local bindings, so a rule needs to provide a way
	 * to store and retrieve bindings.
	 * 
	 * @param key
	 * @param value
	 */
	void setBindingValue(String key, JamochaValue value);

	/**
	 * Return the value of the for the binding
	 * 
	 * @param key
	 *            is the name of the variable
	 * @return
	 */
	JamochaValue getBindingValue(String key);

	/**
	 * Add a new binding to the rule with the variable as the key
	 * 
	 * @param key
	 * @param bind
	 */
	void addBinding(String key, Binding bind);

	/**
	 * Get the Binding object for the given key
	 * 
	 * @param varName
	 * @return
	 */
	Binding getBinding(String varName);

	/**
	 * We need to set the trigger facts, so the rule action can look up values
	 * easily.
	 * 
	 * @param facts
	 */
	void setTriggerFacts(Fact[] facts);

	/**
	 * After the actions of a rule are executed, reset should be called to make
	 * sure the rule doesn't hold on to the facts.
	 */
	void resetTriggerFacts();

	/**
	 * this method needs to be called before rule compilation begins. It avoids
	 * doing multiple lookups for the corresponding template.
	 * 
	 * @param engine
	 */
	void resolveTemplates(Rete engine);


}
