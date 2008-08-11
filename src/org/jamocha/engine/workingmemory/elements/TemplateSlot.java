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

import org.jamocha.engine.Engine;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

public class TemplateSlot extends Slot {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * node count is used to keep track of how many nodes use the given slot.
	 * This is done for statistical purposes, which serve 3 main functions. 1.
	 * provide a way to calculate the relative importance of a slot with regard
	 * to the entire RETE network 2. provide a way to optimize runtime execution
	 * 3. provide valuable information for engine management
	 */
	private int nodeCount = 1;

	private boolean multiSlot = false;

	private boolean staticDefault;

	private boolean silent = false;

	// used for the implementation of slot default "?NONE", as it specified in
	// CLIPS BPG
	private boolean required = false;

	private Expression defaultExpression;

	public TemplateSlot() {
		super();
	}

	public TemplateSlot(final String name) {
		super(name);
	}

	public boolean isMultiSlot() {
		return multiSlot;
	}

	public void setMultiSlot(final boolean multiSlot) {
		this.multiSlot = multiSlot;
	}

	public Expression getDefaultExpression() {
		return defaultExpression;
	}

	/**
	 * In some cases, a deftemplate can be define with a default value.
	 * 
	 * @param value
	 */
	public void setDefaultExpression(final Expression defaultExpression) {
		this.defaultExpression = defaultExpression;
	}

	public Slot createSlot(final Engine engine) throws EvaluationException {
		Slot result;
		if (isMultiSlot())
			result = new MultiSlot(silent, getName());
		else {
			result = new Slot(silent, getName());
			result.setValueType(getValueType());
		}
		if (defaultExpression != null && !defaultExpression.equals("?None")) {
			JamochaValue value = defaultExpression.getValue(engine);
			if (value != null) {
				if (value.getType().equals(JamochaType.LIST) && !isMultiSlot())
					if (value.getListCount() > 0)
						value = value.getListValue(0);
					else
						value = JamochaValue.NIL;
				if (value != null)
					result.setValue(value);
			}
		}
		result.setId(this.getId());
		return result;
	}

	/**
	 * return the number of nodes the given slot participates in. It may not be
	 * a complete count. In some cases, it may only count the direct successors
	 * of ObjectTypeNode
	 * 
	 * @return
	 */
	public int getNodeCount() {
		return nodeCount;
	}

	/**
	 * Increment the node count
	 */
	public void incrementNodeCount() {
		nodeCount++;
	}

	/**
	 * decrement the node count
	 * 
	 */
	public void decrementNodeCount() {
		--nodeCount;
	}

	public boolean isStaticDefault() {
		return staticDefault;
	}

	public void setStaticDefaultExpression(final Expression expression) {
		staticDefault = true;
		defaultExpression = expression;
	}

	public void setDynamicDefaultExpression(final Expression expression) {
		staticDefault = false;
		defaultExpression = expression;
	}

	public void setDefaultDerive() {
		staticDefault = true;
		defaultExpression = JamochaType.getDefaultValue(getValueType());
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}

	@Override
	public boolean isSilent() {
		return silent;
	}

	public void setSilent(final boolean silent) {
		this.silent = silent;
	}

	@Override
	public String format(final Formatter visitor) {
		return visitor.visit(this);
	}

}
