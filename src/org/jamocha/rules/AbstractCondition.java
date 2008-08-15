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

import org.jamocha.engine.Complexity;
import org.jamocha.formatter.Formatter;

/**
 * @author Josef Alexander Hahn
 * @author Alexander Wilden
 * @author Sebastian Reinartz
 */
public abstract class AbstractCondition implements Condition {

	private ConditionWithNested parent = null;
	
	public AbstractCondition() {
	}
	
	public AbstractCondition(AbstractCondition c) {
		parent = c.parent;
	}
	
	public void setParentCondition(ConditionWithNested c) {
		parent = c;
	}
	
	public int compareComplexity(Complexity other) {
		return this.getComplexity() - other.getComplexity();
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
	
	public ConditionWithNested getParentCondition() {
		return parent;
	}
	
	public Condition clone() {
		return null;
	}
	
	public String dump() {
		return dump("");
	}
	
}
