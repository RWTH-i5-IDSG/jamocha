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
		public boolean unbound = false;
		public String name = "";
		
		public VariableUsage(String name, boolean virtual) {
			this.name = name;
			this.unbound = virtual;
		}
		
		public boolean equals(VariableUsage b) {
			return (b.name == this.name && b.unbound == this.unbound);
		}
		
		public void markUnbound() {
			this.unbound = true;
		}
		
		public boolean isUnbound() {
			return this.unbound;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			VariableUsage other = (VariableUsage) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		private BeffyRuleOptimizerDataPassThree getOuterType() {
			return BeffyRuleOptimizerDataPassThree.this;
		}
		
	}
	
	List<VariableUsage> usages = new LinkedList<VariableUsage>();
	Condition condition = null;

	public void combine(BeffyRuleOptimizerDataPassThree d) {
		for (VariableUsage usage : d.usages) {
			this.add(usage);			
		}
	}
	
	public void add(VariableUsage usage) {
		if (usages.indexOf(usage) == -1)
			usages.add(usage);
		else if (!usage.isUnbound()) {
			usages.get(usages.indexOf(usage)).unbound = false;
		}
	}
	
	public void add(String name, boolean virtual) {
		VariableUsage usage = new VariableUsage(name, virtual);
		this.add(usage);
	}
	
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	
	public Condition getCondition() {
		return this.condition;
	}
	
	public void markVirtual() {
		for (VariableUsage usage : this.usages) {
			usage.markUnbound();
		}
	}
}
