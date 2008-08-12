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

/**
 * @author Christoph Terwelp
 *
 */
public class BeffyRuleOptimizerDataPassThree {
	
	public class Binding {
		public boolean negated = false;
		public String name = "";
		
		public Binding(String name, boolean negated) {
			this.name = name;
			this.negated = negated;
		}
		
		public boolean equals(Binding b) {
			return (b.name == this.name && b.negated == this.negated);
		}
	}
	
	List<Binding> bindings = new LinkedList<Binding>();
	boolean virtual = false;

	public void combine(BeffyRuleOptimizerDataPassThree d) {
		for (Binding binding : d.bindings) {
			if (bindings.indexOf(binding) == -1)
				bindings.add(binding);			
		}
	}
	
	public void markVirtual() {
		this.virtual = true;
	}
	
	public void add(String name, boolean negated) {
		Binding binding = new Binding(name, negated);
		if (bindings.indexOf(binding) == -1)
			bindings.add(binding);
	}
}
