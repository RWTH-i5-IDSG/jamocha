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

import org.jamocha.parser.JamochaValue;



public abstract class AbstractConnectedConstraint extends AbstractConstraint {
		
	protected Constraint left;
	
	protected Constraint right;
	
	protected boolean negated;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + (negated ? 1231 : 1237);
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractConnectedConstraint other = (AbstractConnectedConstraint) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (negated != other.negated)
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

	public Constraint getLeft() {
		return left;
	}
	
	public Constraint getRight() {
		return right;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("{");
		result.append(super.toString());
		if (left != null) result.append(left.toString());
		if (right != null) result.append(right.toString());
		result.append("}");
		return result.toString();
	}
	
	public AbstractConnectedConstraint(Constraint left, Constraint right, boolean negated) {
		this.left=left;
		this.right=right;
		this.negated=negated;
	}
	
	public boolean isNegated(){
		return negated;
	}
	
	public String getConstraintName() {
		return null;
	}
	
	public JamochaValue getValue() {
		return null;
	}
	
	/**
	 * this method is wrong here, because it sets the left
	 * sub-constraint for a "immutable" constraint class.
	 * it is only here, because our SFP-Interpreter uses it.
	 * We should set this value in the constructor only!
	 * @param left
	 */
	public void setLeft(Constraint left) {
		this.left = left;
	}
	
	/**
	 * this method is wrong here, because it sets the right
	 * sub-constraint for a "immutable" constraint class.
	 * it is only here, because our SFP-Interpreter uses it.
	 * We should set this value in the constructor only!
	 * @param right
	 */
	public void setRight(Constraint right) {
		this.right = right;
	}
	
	
}
