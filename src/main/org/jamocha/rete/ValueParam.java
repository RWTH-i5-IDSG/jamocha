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

import java.math.BigDecimal;

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

    public ValueParam(Object value) {
        super();
        this.value = value;
        this.checkType();
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
     * Value parameter don't need to resolve the value, so it just
     * returns it.
     */
    public Object getValue(Rete engine, int valueType) {
        if (valueType == Constants.LONG_OBJECT) {
            return this.getLongValue();
        } else if (valueType == Constants.INTEGER_OBJECT) {
            return this.getIntValue();
        } else if (valueType == Constants.SHORT_OBJECT) {
            return this.getShortValue();
        } else if (valueType == Constants.FLOAT_OBJECT) {
            return this.getFloatValue();
        } else if (valueType == Constants.DOUBLE_OBJECT) {
            return this.getDoubleValue();
        } else if (valueType == Constants.STRING_TYPE) {
            return this.getStringValue();
        } else {
            return this.value;
        }
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
    
    protected void checkType() {
        if (this.value instanceof Long) {
            this.valueType = Constants.LONG_OBJECT;
        } else if (this.value instanceof Double) {
            this.valueType = Constants.DOUBLE_OBJECT;
        } else if (this.value instanceof String) {
            this.valueType = Constants.STRING_TYPE;
        } else if (this.value instanceof Boolean) {
            this.valueType = Constants.BOOLEAN_OBJECT;
        } else {
            this.valueType = Constants.OBJECT_TYPE;
        }
    }
}
