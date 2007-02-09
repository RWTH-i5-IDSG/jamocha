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

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * Value parameter is meant for values. It extends AbstractParam, which provides
 * implementation for the convienance methods that convert the value to
 * primitive types.
 */
public class ValueParam extends AbstractParam {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected JamochaValue value = null;

	public ValueParam(JamochaValue value) {
		super();
		this.value = value;
	}

	/**
	 * Value parameter don't need to resolve the value, so it just returns it.
	 */
	public JamochaValue getValue(Rete engine) throws EvaluationException {
		return this.value;
	}

	public ValueParam cloneParameter() {
		return new ValueParam(this.value);
	}

	public String getExpressionString() {
		return this.value.toString();
	}
}
