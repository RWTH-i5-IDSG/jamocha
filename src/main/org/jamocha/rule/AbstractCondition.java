/*
 * Copyright 2007 Sebastian Reinartz, Alexander Wilden
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
package org.jamocha.rule;

import org.jamocha.formatter.Formatter;


public abstract class AbstractCondition implements Condition {

	protected static int complexity = 1;

	protected int totalComplexity = 0;

	public int getComplexity() {
		return AbstractCondition.complexity;
	}

	public int getTotalComplexity() {
		return (AbstractCondition.complexity + totalComplexity);
	}

	public void incrementTotalComplexityBy(int value) {
		totalComplexity += value;
	}

	public void setComplexity(int value) {
		AbstractCondition.complexity = value;
	}
	
	public int compareTo(Object o) {
		return this.getTotalComplexity() - ((Condition)o).getTotalComplexity();
	}
	
	protected boolean negated;

	public boolean getNegated() {
		return negated;
	}
	

	public void setNegated(boolean negated) {
		this.negated = negated;
	}
	
	public abstract Object clone() throws CloneNotSupportedException;

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
	
}
