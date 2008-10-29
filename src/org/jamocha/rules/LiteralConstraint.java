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

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * Literal constraint is a comparison between an object field and a concrete
 * value. for example, account.name is equal to "Peter Lin". I originally, named
 * the class something else, but since CLIPS uses literal constraint, I decided
 * to change the name of the class. Even though I don't like the term literal
 * constraint, it doesn't make sense to fight existing terminology.
 */
public class LiteralConstraint extends AbstractConstraint {

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	protected JamochaValue value;

	protected String slotName;
	
	/**
	 * 
	 */
	public LiteralConstraint(JamochaValue value, String slotName) {
		super();
		this.slotName = slotName;
		this.value = value;
	}
	
	/**
	 * the name is the slot name
	 */
	public String getConstraintName() {
		return null;
	}

	public JamochaValue getValue() {
		return value;
	}
	
	public String getSlotName() {
		return slotName;
	}

	/**
	 * if the literal constraint is negated, the method returns true
	 * 
	 * @return
	 */
	public boolean isNegated() {
		return false;
	}
	
	public String toString(){
		return super.toString()+value.toString();
	}
	
	public String format(Formatter visitor) {
		return visitor.visit(this);
		
	}

	/**
	 * @see org.jamocha.rules.Condition#acceptVisitor(org.jamocha.rules.LHSVisitor, java.lang.Object)
	 */
	public <T, S> S acceptVisitor(ConstraintVisitor<T, S> visitor, T data) {
		return visitor.visit(this, data);
	}


}
