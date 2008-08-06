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

package org.jamocha.engine.workingmemory.elements;

import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;
import org.jamocha.engine.Engine;
import org.jamocha.engine.configurations.AbstractSignature;

/**
 * @author Peter Lin
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SlotParam extends AbstractSignature {

	/**
	 * 
	 */

	@Override
	public Object clone() {
		return new SlotParam(name, valueExpression);
	}

	private static final long serialVersionUID = 1L;

	protected String name;

	protected Expression valueExpression;

	public SlotParam(final String name, final Expression values) {
		super();
		this.name = name;
		valueExpression = values;
	}

	/**
	 * Slot parameter is only used internally, so normal user functions should
	 * not need to deal with slot parameters.
	 */
	public JamochaValue getValue(final Engine engine)
			throws EvaluationException {
		final Slot slot = new Slot(name);
		slot.setValue(valueExpression.getValue(engine));
		return JamochaValue.newSlot(slot);
	}

	public String getName() {
		return name;
	}

	public Expression getValueExpression() {
		return valueExpression;
	}

	public String getExpressionString() {
		return ParserFactory.getFormatter().visit(this);
	}

	public String format(final Formatter visitor) {
		return visitor.visit(this);
	}
}