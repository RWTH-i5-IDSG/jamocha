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

import java.util.Arrays;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.JamochaValue;

/**
 * @author primary Sebastian Reinartz and/or Alexander Wilden
 * @author Josef Alexander Hahn
 *
 */
public class OrderedFactConstraint extends AbstractConstraint {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(constraints);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderedFactConstraint other = (OrderedFactConstraint) obj;
		if (!Arrays.equals(constraints, other.constraints))
			return false;
		return true;
	}


	private static final long serialVersionUID = 1L;

	private Constraint[] constraints;

	public OrderedFactConstraint(Constraint[] constraints) {
		this.constraints=constraints;
	}
	
	public boolean isNegated() {
		return false;
	}

	public Constraint[] getConstraints() {
		return constraints;
	}

	public JamochaValue getValue() {
		return null;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public String getConstraintName() {
		return null;
	}


	/**
	 * @see org.jamocha.rules.Condition#acceptVisitor(org.jamocha.rules.LHSVisitor, java.lang.Object)
	 */
	public <T, S> S acceptVisitor(ConstraintVisitor<T, S> visitor, T data) {
		return visitor.visit(this, data);
	}


}
