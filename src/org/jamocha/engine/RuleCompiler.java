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

package org.jamocha.engine;

import org.jamocha.communication.events.CompilerListener;
import org.jamocha.engine.rules.rulecompiler.CompileRuleException;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.Rule;

/**
 * @author Peter Lin
 * 
 * The purpose of a RuleCompiler is to convert a Rule object into the
 * appropriate RETE network. We have a generic interface, so that others can
 * implement their own RuleCompiler.
 */
public interface RuleCompiler {
	public class SFRuleCompiler {

	}

	/**
	 * A rule can be added dynamically at runtime to an existing engine. If the
	 * engine wasn't able to add the rule, it will throw an exception.
	 * 
	 * @param rule
	 * @throws AssertException
	 * @throws EvaluationException
	 * @throws CompileRuleException 
	 */
	boolean addRule(Rule rule) throws AssertException, RuleException,
			EvaluationException, CompileRuleException;

	/**
	 * Add a new ObjectTypeNode to the network
	 * 
	 * @param node
	 */
	void addObjectTypeNode(Template template);

	/**
	 * If an user wants to listen to the various events in the compiler, add a
	 * listener and then handle the events accordingly.
	 * 
	 * @param listener
	 */
	void addListener(CompilerListener listener);

	/**
	 * Remove a listener from the compiler.
	 * 
	 * @param listener
	 */
	void removeListener(CompilerListener listener);

	Binding getBinding(String varName, Rule r);

}
