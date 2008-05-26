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

package org.jamocha.engine;

import org.jamocha.Constants;
import org.jamocha.formatter.Formattable;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;
import org.jamocha.engine.configurations.AbstractSignature;
import org.jamocha.engine.workingmemory.elements.Fact;

/**
 * @author Peter Lin
 * 
 * BoundParam is a parameter that is a binding. The test node will need to call
 * setFact(Fact[] facts) so the parameter can access the value.
 */
public class BoundParam extends AbstractSignature implements Formattable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The fact
	 */
	protected Fact fact = null;

	protected Object resolvedVal = null;

	/**
	 * Column refers to the column of the fact. the value of the column should
	 * be a non-negative integer.
	 */
	protected int column = -1;

	/**
	 * the int value defining the valueType
	 */
	protected int valueType = -1;

	/**
	 * the row id of the fact as defined by the rule
	 */
	protected int rowId = -1;

	/**
	 * By default the action is assert
	 */
	protected int actionType = Constants.ACTION_ASSERT;

	/**
	 * the name of the variable
	 */
	protected String variableName = null;

	/**
	 * if the binding is for a multislot, it should be set to true. by default,
	 * it is false.
	 */
	protected boolean isMultislot = false;

	@Override
	public Object clone() {
		BoundParam result = new BoundParam();
		result.fact = fact;
		result.resolvedVal = resolvedVal;
		result.column = column;
		result.valueType = valueType;
		result.rowId = rowId;
		result.actionType = actionType;
		result.variableName = variableName;
		result.isMultislot = isMultislot;
		return result;
	}

	public BoundParam() {
		super();
	}

	/**
	 * 
	 */
	public BoundParam(int col, int vType) {
		super();
		column = col;
		valueType = vType;
		objBinding = true;
	}

	public BoundParam(int col, int vType, boolean objBinding) {
		super();
		column = col;
		valueType = vType;
		this.objBinding = objBinding;
	}

	public BoundParam(int row, int col, int vType, boolean obj) {
		super();
		rowId = row;
		column = col;
		valueType = vType;
		objBinding = obj;
	}

	public BoundParam(Fact fact) {
		this.fact = fact;
		objBinding = true;
		valueType = Constants.FACT_TYPE;
	}

	public BoundParam(String variableName) {
		this.variableName = variableName;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String value) {
		if (value.substring(0, 1).equals("?"))
			variableName = value.substring(1);
		else
			variableName = value;
	}

	/**
	 * method will try to resolve the variable and return the value.
	 */
	public JamochaValue getValue(Engine engine) throws EvaluationException {
		if (fact != null && !objBinding)
			return fact.getSlotValue(column);
		else if (fact != null)
			return JamochaValue.newFact(fact);
		else
			return engine.getBinding(variableName);
	}

	public void setResolvedValue(Object val) {
		resolvedVal = val;
	}

	/**
	 * Return the fact
	 * 
	 * @return
	 */
	public Fact getFact() {
		return fact;
	}

	/**
	 * The TestNode should call this method to set the fact. The fact should
	 * never be null, since it has to have matched preceding patterns. We may be
	 * able to remove the check for null. If the row id is less than zero, it
	 * means the binding is an object binding.
	 * 
	 * @param facts
	 */
	public void setFact(Fact[] facts) {
		if (rowId > -1 && facts[rowId] != null)
			fact = facts[rowId];
	}

	/**
	 * if the binding is bound to an object, the method will return true. By
	 * default, the method will return false.
	 * 
	 * @return
	 */
	@Override
	public boolean isObjectBinding() {
		return objBinding;
	}

	/**
	 * If the binding is bound to an object, call the method with true.
	 * 
	 * @param obj
	 */
	public void setObjectBinding(boolean obj) {
		objBinding = obj;
	}

	/**
	 * if the binding is for a multislot, it will return true. by default is is
	 * false.
	 * 
	 * @return
	 */
	public boolean isMultislot() {
		return isMultislot;
	}

	/**
	 * only set the multislot to true if the slot is defined as a multislot
	 * 
	 * @param multi
	 */
	public void setIsMultislot(boolean multi) {
		isMultislot = multi;
	}

	public void setRow(int row) {
		rowId = row;
	}

	public void setColumn(int col) {
		column = col;
		if (column == -1)
			objBinding = true;
	}

	/**
	 * reset sets the Fact handle to null
	 */
	public void reset() {
		fact = null;
	}

	public String getExpressionString() {
		return ParserFactory.getFormatter().visit(this);
	}

	@Override
	public String toString() {
		return getExpressionString();
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return rowId;
	}
}
