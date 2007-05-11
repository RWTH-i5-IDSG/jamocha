/*
 * Copyright 2007 Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.org
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.nodes;

import java.io.Serializable;
import java.util.AbstractCollection;

import org.jamocha.rete.Fact;

public class FactTupel implements Serializable{

	private static final long serialVersionUID = 1L;

	
	protected AbstractCollection<Fact> facts = null;


	public FactTupel(AbstractCollection<Fact> facts) {
		super();
		this.facts = facts;
	}


	public AbstractCollection<Fact> getFacts() {
		return facts;
	}


	public void setFacts(AbstractCollection<Fact> facts) {
		this.facts = facts;
	}
	
	
}
