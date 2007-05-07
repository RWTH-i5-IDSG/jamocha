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

import org.jamocha.rete.TemplateSlot;


public abstract class AbstractConstraint implements Constraint {
	
	protected static int complexity = 1;

	protected int totalComplexity = 0;
	
	protected TemplateSlot slot = null;

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

	
	public void setSlot(TemplateSlot slot){
		this.slot = slot;
	}
	
	public TemplateSlot getSlot(){
		return slot;
	}

}
