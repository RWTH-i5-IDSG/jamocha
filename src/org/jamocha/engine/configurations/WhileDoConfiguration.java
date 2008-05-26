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

package org.jamocha.engine.configurations;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.ExpressionCollection;
import org.jamocha.engine.Engine;

public class WhileDoConfiguration extends AbstractConfiguration {

	private Expression condition = null;

	private ExpressionCollection whileActions = null;

	public boolean isObjectBinding() {
		return false;
	}

	public String getExpressionString() {
		// Returns null because this is deprecated
		return null;
	}

	public JamochaValue getValue(Engine engine) throws EvaluationException {
		return null;
	}

	public Expression getCondition() {
		return condition;
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public ExpressionCollection getWhileActions() {
		return whileActions;
	}

	public void setWhileActions(ExpressionCollection whileActions) {
		this.whileActions = whileActions;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
