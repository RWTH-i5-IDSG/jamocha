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

import org.jamocha.formatter.Formattable;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.TemplateSlot;

/**
 * @author Peter Lin
 * 
 * Constraints come in 3 varieties: 1. value constraint where an object's field
 * is compared to a value 2. variable constraint where a field is bound to a
 * variable 3. predicate constraint where the field is bound to a variable, and
 * then evaluated against 1 or more operations.
 * 
 */
public interface Constraint extends Serializable, Complexity, Compileable,
		Formattable {

	/**
	 * This should be the name of the constraint. In the case of an object, it
	 * is the field's name
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Set the name of the constraint.
	 * 
	 * @param name
	 */
	void setName(String name);

	/**
	 * The value of the constraint. Primitive numeric types are wrapped in the
	 * object version. Example, int is wrapped in Integer.
	 * 
	 * @return
	 */
	JamochaValue getValue();

	/**
	 * Set the value of the constraint. In the case of a binding, the value is
	 * the variable name
	 * 
	 * @param val
	 */
	void setValue(JamochaValue val);

	/**
	 * if the literal constraint is negated with a "~" tilda, call the method
	 * pass true.
	 * 
	 * @param negate
	 */
	void setNegated(boolean negate);

	/**
	 * if the literal constraint is negated, the method returns true
	 * 
	 * @return
	 */
	boolean getNegated();

	@Deprecated
	void setSlot(TemplateSlot slot);

	@Deprecated
	TemplateSlot getSlot();

}
