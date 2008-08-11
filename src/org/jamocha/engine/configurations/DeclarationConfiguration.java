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

package org.jamocha.engine.configurations;

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class DeclarationConfiguration extends AbstractConfiguration {

	private Parameter version = null;

	private Parameter salience = null;

	private Parameter autoFocus = null;

	public boolean isFactBinding() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getExpressionString() {
		// TODO Auto-generated method stub
		return null;
	}

	public JamochaValue getValue(Engine engine) throws EvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	public Parameter getAutoFocus() {
		return autoFocus;
	}

	public void setAutoFocus(Parameter autoFocus) {
		this.autoFocus = autoFocus;
	}

	public Parameter getSalience() {
		return salience;
	}

	public void setSalience(Parameter salience) {
		this.salience = salience;
	}

	public Parameter getVersion() {
		return version;
	}

	public void setVersion(Parameter version) {
		this.version = version;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
