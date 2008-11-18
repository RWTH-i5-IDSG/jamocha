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
