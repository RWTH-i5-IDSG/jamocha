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
package org.jamocha.rete.nodes.joinfilter;

import java.io.Serializable;

import org.jamocha.Constants;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Evaluate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.nodes.FactTuple;

/**
 * @author Josef Alexander Hahn
 */

public class FieldComparator implements Serializable, Cloneable, JoinFilter {

	private static final long serialVersionUID = 1L;

	protected int operator = Constants.EQUAL;

	protected String varName = null;

	protected RightFieldAddress right = null;

	protected LeftFieldAddress left = null;

	public FieldComparator(String varName, LeftFieldAddress left, int operator, RightFieldAddress right) {
		this(varName, left, right);
		this.operator = operator;
	}

	public FieldComparator(String varName, LeftFieldAddress left, RightFieldAddress right) {
		super();
		this.varName = varName;
		this.left = left;
		this.right = right;
	}

	public int getOperator() {
		return this.operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}

	public boolean evaluate(Fact rightinput, FactTuple leftinput, Rete engine) throws JoinFilterException {
		JamochaValue rightValue = null, leftValue = null;
		try {
			if (right.refersWholeFact()) {
				rightValue = JamochaValue.newFact(rightinput);
				// rightValue = rightinput.getSlotValue( -1 );
			} else {
				rightValue = rightinput.getSlotValue(right.getSlotIndex());
				if (right.posIndex != -1) {
					//TODO implement it
				}
			}

			if (left.refersWholeFact()) {
				leftValue = JamochaValue.newFact(leftinput.getFacts()[left.getRowIndex()]);
			} else {
				leftValue = leftinput.getFacts()[left.getRowIndex()].getSlotValue(left.getSlotIndex());
				if (left.posIndex != -1) {
					//TODO implement it
				}
			}
			leftValue = resolveFact(leftValue, engine);
			rightValue = resolveFact(rightValue, engine);
		} catch (EvaluationException e) {
			// get slot value exception, should not occur
			e.printStackTrace();
		}

		return Evaluate.evaluate(operator, leftValue, rightValue);
	}

	/**
	 * This function takes a JamochaValue and if it is of type FACT_ID it
	 * returns the JamochaValue of type FACT with the corresponding fact. If we
	 * have a fact we get the current version of the fact out of the engine and
	 * return it.
	 * <p>
	 * If the value is no FACT_ID it is just returned.
	 * 
	 * @param value
	 *            The possible fact-id to resolve.
	 * @param engine
	 *            Needed to find the fact to a given fact-id.
	 * @return The original value or a fact if value was a fact-id
	 */
	private JamochaValue resolveFact(JamochaValue value, Rete engine) {
		if (value.is(JamochaType.FACT_ID)) {
			return JamochaValue.newFact(engine.getFactById(value));
		} else if (value.is(JamochaType.FACT)) {
			return JamochaValue.newFact(engine.getFactById(value.getFactValue().getFactId()));
		}
		return value;
	}

	public String getVarName() {
		return this.varName;
	}

	public void setVarName(String name) {
		this.varName = name;
	}

	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("?" + varName + " ");
		buf.append(left.toPPString());
		buf.append(" ");
		buf.append(ConversionUtils.getOperatorDescription(operator));
		buf.append(" ");
		buf.append(right.toPPString());
		return buf.toString();
	}

	@Override
	public boolean equals(Object obj) {
		// equals if same type, same operator, same slot
		if (obj instanceof FieldComparator) {
			FieldComparator fc = (FieldComparator) obj;
			return (this.operator == fc.operator) && (this.varName.equals(fc.varName)) && (this.right.equals(fc.right)) && (this.left.equals(fc.left));
		}
		return false;
	}

}
