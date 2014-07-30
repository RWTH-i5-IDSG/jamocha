/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.dn.memory;

import lombok.Getter;

/**
 * Storage class for facts entering the network.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Template
 */
@Getter
public class Fact {
	final Template template;
	final Object slotValues[];

	public Fact(final Template template, final Object... slotValues) {
		this.template = template;
		this.slotValues = slotValues;
	}

	public Object getValue(final int index) {
		return this.slotValues[index];
	}

	public void setValue(final int index, final Object value) {
		this.slotValues[index] = value;
	}
}
