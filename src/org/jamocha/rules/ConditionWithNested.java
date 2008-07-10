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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ConditionWithNested extends AbstractCondition {

	protected List<Condition> nested = new ArrayList<Condition>();

	public ConditionWithNested() {
		super();
	}

	public void addNestedCondition(Condition ce) {
		nested.add((Condition) ce);
		ce.setParentCondition(ce);
	}

	public List<Condition> getNestedConditions() {
		return Collections.unmodifiableList(this.nested);
	}

	public List<Constraint> getConstraints() {
		List<Constraint> result = new ArrayList<Constraint>();
		for (Condition c: nested) result.addAll(c.getConstraints()); 
		return result;
	}

	public List<Constraint> getFlatConstraints() {
		return new ArrayList<Constraint>();
	}

	public int getComplexity() {
		int comp = 0;
		for (Condition child:nested) comp += child.getComplexity();
		return comp;
	}
	
	public Condition clone() {
		ConditionWithNested newCwn;
		try {
			newCwn = this.getClass().newInstance();
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
		for (Condition c: nested) newCwn.addNestedCondition(c.clone());
		newCwn.id=id;
		return newCwn;
	}

	public void removeNestedCondition(Condition c) {
		nested.remove(c);
	}

}