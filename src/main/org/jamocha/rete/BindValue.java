/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.io.Serializable;

/**
 * @author Peter Lin
 *
 */
public class BindValue implements Serializable {
	
	private static final long serialVersionUID = 0xDEADBEAFL;

	protected Object value = null;

	protected boolean negated = false;

	public BindValue(Object val, boolean negate) {
		super();
		this.value = val;
		this.negated = negate;
	}

	public BindValue(Object val) {
		super();
		this.value = val;
	}

	public Object getValue() {
		return this.value;
	}

	public boolean negated() {
		return this.negated;
	}

}
