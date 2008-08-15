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

/**
 * 
 */
package org.jamocha.engine.rules.rulecompiler.beffy;

import java.util.LinkedList;
import java.util.List;

import org.jamocha.rules.Condition;

/**
 * @author Christoph Terwelp
 *
 */
public class BeffyRuleOptimizerDataPassThree {
	
	public class VariableUsage {
		public boolean virtual = false;
		public String name = "";
		
		public VariableUsage(String name, boolean virtual) {
			this.name = name;
			this.virtual = virtual;
		}
		
		public boolean equals(VariableUsage b) {
			return (b.name == this.name && b.virtual == this.virtual);
		}
		
		public void markVirtual() {
			this.virtual = true;
		}
		
		public boolean isVirtual() {
			return this.virtual;
		}
	}
	
	List<VariableUsage> usages = new LinkedList<VariableUsage>();
	Condition condition = null;

	public void combine(BeffyRuleOptimizerDataPassThree d) {
		for (VariableUsage usage : d.usages) {
			if (usages.indexOf(usage) == -1)
				usages.add(usage);			
		}
	}
	
	public void add(String name, boolean virtual) {
		VariableUsage usage = new VariableUsage(name, virtual);
		if (usages.indexOf(usage) == -1)
			usages.add(usage);
	}
	
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	
	public Condition getCondition() {
		return this.condition;
	}
	
	public void markVirtual() {
		for (VariableUsage usage : this.usages) {
			usage.markVirtual();
		}
	}
}
