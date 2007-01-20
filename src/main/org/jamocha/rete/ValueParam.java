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

/**
 * @author Peter Lin
 *
 * Value parameter is meant for values. It extends AbstractParam, which provides
 * implementation for the convienance methods that convert the value to
 * primitive types.
 */
public class ValueParam extends AbstractParam {

	protected int valueType;

	protected Object value = null;

	public ValueParam() {
		super();
	}

	/**
	 * 
	 */
	public ValueParam(int vtype, Object value) {
		super();
		this.valueType = vtype;
		this.value = value;
	}

	public void setValueType(int type) {
		this.valueType = type;
	}

	/**
	 * The value types are defined in woolfel.engine.rete.Constants
	 */
	public int getValueType() {
		return this.valueType;
	}

	public void setValue(Object val) {
		this.value = val;
	}

	/**
	 * Method will return the value as on Object. This means primitive
	 * values are wrapped in their Object equivalent.
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * implementation sets the value to null and the type to Object
	 */
	public void reset() {
		this.value = null;
		this.valueType = Constants.OBJECT_TYPE;
	}

	public ValueParam cloneParameter() {
		ValueParam vp = new ValueParam();
		vp.value = this.value;
		vp.valueType = this.valueType;
		return vp;
	}
}
