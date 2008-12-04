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



public abstract class AbstractConstraint implements Constraint {

	protected Condition parent;
	
	public int getComplexity() {
		return 1;
	}
	
	public void setParentCondition(Condition p) {
		parent = p;
	}
	
	public Condition getParentCondition() {
		return parent;
	}
	
	public Constraint clone() {
		try {
			return (Constraint) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * WARNING:
	 * the parent fact is not interesting for equality.
	 * Constraints with different parents can be equal!
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return 1;
	}
	public boolean equals(Object other) {
		return (other instanceof Constraint);
	}

}
