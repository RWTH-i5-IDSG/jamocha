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
import org.jamocha.engine.BoundParam;
import org.jamocha.engine.ExpressionCollection;
import org.jamocha.engine.Engine;

public class LoopForCountConfiguration extends AbstractConfiguration {

	private BoundParam loopVar = null;

	private Expression startIndex = JamochaValue.newLong(1);

	private Expression endIndex = null;

	private ExpressionCollection actions = null;

	public boolean isFactBinding() {
		return false;
	}

	public String getExpressionString() {
		// Returns null because this is deprecated
		return null;
	}

	public JamochaValue getValue(Engine engine) throws EvaluationException {
		return null;
	}

	public ExpressionCollection getActions() {
		return actions;
	}

	public void setActions(ExpressionCollection actions) {
		this.actions = actions;
	}

	public Expression getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(Expression endIndex) {
		this.endIndex = endIndex;
	}

	public BoundParam getLoopVar() {
		return loopVar;
	}

	public void setLoopVar(BoundParam loopVar) {
		this.loopVar = loopVar;
	}

	public Expression getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Expression startIndex) {
		this.startIndex = startIndex;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
