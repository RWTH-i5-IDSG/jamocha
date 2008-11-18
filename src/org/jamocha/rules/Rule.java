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

import java.util.List;

import org.jamocha.engine.Complexity;
import org.jamocha.engine.Dumpable;
import org.jamocha.engine.TemporalValidity;
import org.jamocha.engine.modules.Module;
import org.jamocha.formatter.Formattable;

/**
 * @author Josef Alexander Hahn
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
public interface Rule extends Cloneable, Complexity, Formattable, Dumpable {
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

	
//	TODO remove it void setVersion(String v);
//	
//	String getVersion();


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

	List<Condition> getConditions();

	List<Action> getActions();

	Module parentModule();

	@Deprecated()
	/**
	 * clones a rule. only needed for the sfrulecompiler. a
	 * later rule compiler should not use this! -jh
	 */
	public Rule clone() throws CloneNotSupportedException;
	
	public TemporalValidity getTemporalValidity();
	
	public void setTemporalValidity(TemporalValidity tval);

}
