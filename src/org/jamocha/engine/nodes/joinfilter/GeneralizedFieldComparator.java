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

package org.jamocha.engine.nodes.joinfilter;

import java.io.Serializable;

import org.jamocha.Constants;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.ConversionUtils;
import org.jamocha.engine.Evaluate;
import org.jamocha.engine.Engine;
import org.jamocha.engine.nodes.FactTuple;
import org.jamocha.engine.workingmemory.elements.Fact;

/**
 * @author Josef Alexander Hahn
 */

public class GeneralizedFieldComparator implements Serializable, Cloneable, GeneralizedJoinFilter {

	private static final long serialVersionUID = 1L;

	protected int operator = Constants.EQUAL;

	protected String varName = null;

	protected FieldAddress right = null;

	protected FieldAddress left = null;

	public GeneralizedFieldComparator(final String varName, final FieldAddress fa1,
			final int operator, final FieldAddress fa2) {
		this(varName, fa1, fa2);
		this.operator = operator;
	}

	public GeneralizedFieldComparator(final String varName, final FieldAddress left,
			final FieldAddress right) {
		super();
		this.varName = varName;
		this.left = left;
		this.right = right;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(final int operator) {
		this.operator = operator;
	}

	public boolean evaluate(FactTuple t,final Engine engine) throws JoinFilterException {
		JamochaValue rightValue = null, leftValue = null;
		try {
			rightValue = right.getIndexedValue(t);
			leftValue  =  left.getIndexedValue(t);
		} catch (final EvaluationException e) {
			Logging.logger(this.getClass()).warn(e);
		}
		return Evaluate.evaluate(operator, leftValue, rightValue);
	}


	public String getVarName() {
		return varName;
	}

	public void setVarName(final String name) {
		varName = name;
	}

	public String toPPString() {
		final StringBuffer buf = new StringBuffer();
		buf.append("?" + varName + " ");
		buf.append(left.toPPString());
		buf.append(" ");
		buf.append(ConversionUtils.getOperatorDescription(operator));
		buf.append(" ");
		buf.append(right.toPPString());
		return buf.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		// equals if same type, same operator, same slot
		if (obj instanceof GeneralizedFieldComparator) {
			final GeneralizedFieldComparator fc = (GeneralizedFieldComparator) obj;
			return operator == fc.operator && varName.equals(fc.varName)
					&& right.equals(fc.right) && left.equals(fc.left);
		}
		return false;
	}

}
