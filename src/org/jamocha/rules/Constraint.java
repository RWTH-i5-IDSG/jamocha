/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

import java.io.Serializable;

import org.jamocha.formatter.Formattable;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Complexity;
import org.jamocha.engine.rules.rulecompiler.sfp.Compileable;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;

/**
 * @author Peter Lin
 * @author Josef Alexander Hahn
 * 
 * Constraints are "values" in a rule. this can be a variable-binding "?x"
 * or a value "13" and so on... Furthermore, it can be negated.
 */
public interface Constraint extends Complexity, Compileable, Formattable {

	/**
	 * This should be the name of the constraint.
	 * 
	 * @return
	 */
	String getConstraintName();
	
	/**
	 * The value of the constraint. Primitive numeric types are wrapped in the
	 * object version. Example, int is wrapped in Integer.
	 * 
	 * @return
	 */
	JamochaValue getValue();

	/**
	 * if the literal constraint is negated, the method returns true
	 * 
	 * @return
	 */
	boolean isNegated();

	Condition getParentCondition();
	
	void setParentCondition(Condition c);
}
