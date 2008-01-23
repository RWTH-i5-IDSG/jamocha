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

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.RuleException;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.nodes.ObjectTypeNode;
import org.jamocha.rule.Rule;


/**
 * @author Peter Lin
 *
 * The purpose of a RuleCompiler is to convert a Rule object into
 * the appropriate RETE network. We have a generic interface, so
 * that others can implement their own RuleCompiler.
 */
public interface RuleCompiler extends Serializable {
	public class SFRuleCompiler {

	}

	/**
	 * for the runtime, the default should be false. For the development
	 * the setting should be set to true.
	 * @param validate
	 */
	void setValidateRule(boolean validate);
	/**
	 * return whether the rule compiler is set to validate the rule
	 * before compiling it.
	 * @return
	 */
	boolean getValidateRule();
	/**
	 * A rule can be added dynamically at runtime to an existing
	 * engine. If the engine wasn't able to add the rule, it
	 * will throw an exception.
	 * @param rule
	 * @throws AssertException 
	 * @throws EvaluationException 
	 */
	boolean addRule(Rule rule) throws AssertException, RuleException, EvaluationException;

	/**
	 * Add a new ObjectTypeNode to the network
	 * @param node
	 */
	void addObjectTypeNode(Template template);

	/**
	 * Remove an ObjectTypeNode from the network. This should be
	 * when the rule engine isn't running. When an ObjectTypeNode
	 * is removed, all nodes and rules using the ObjectTypeNode
	 * need to be removed.
	 * @param node
	 * @throws RetractException 
	 * @throws RetractException 
	 */
	void removeObjectTypeNode(ObjectTypeNode node) throws RetractException;

	/**
	 * Look up the ObjectTypeNode using the Template
	 * @param template
	 * @return
	 */
	ObjectTypeNode getObjectTypeNode(Template template);

	/**
	 * If an user wants to listen to the various events in the compiler,
	 * add a listener and then handle the events accordingly.
	 * @param listener
	 */
	void addListener(CompilerListener listener);

	/**
	 * Remove a listener from the compiler.
	 * @param listener
	 */
	void removeListener(CompilerListener listener);
}
