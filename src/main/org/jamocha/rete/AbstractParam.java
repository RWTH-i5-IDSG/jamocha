/*
 * Copyright 2002-2006 Peter Lin Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://ruleml-dev.sourceforge.net/ Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.jamocha.rete;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Peter Lin AbstractParam provides the common implementation of
 *         Parameter interface.
 */
public abstract class AbstractParam implements Parameter, Serializable {
	protected boolean objBinding = false;

	/**
	 * get the value type
	 */
	public abstract int getValueType();

	/**
	 * Get the value of the given slot
	 */
	public abstract Object getValue();

	/**
	 * reset sets the Fact handle to null
	 */
	public abstract void reset();

	/**
	 * subclasses have to implement the method
	 */
	public boolean isObjectBinding() {
		return this.objBinding;
	}

	/**
	 * the implementation will check if the value is a String. if it is, it
	 * casts the object to a String, otherwise it calls the object's toString()
	 * method.
	 */
	public String getStringValue() {
		Object value = getValue();
		if (value != null) {
			return getValue().toString();
		} else {
			return "";
		}
	}

	// --- methods for getting the value --- //

	/**
	 * 
	 */
	public boolean getBooleanValue() throws ClassCastException {
		if (getValue() != null && !(getValue() instanceof Boolean)) {
			Boolean b = new Boolean(getStringValue());
			return b.booleanValue();
		} else {
			return ((Boolean) getValue()).booleanValue();
		}
	}

	public int getIntValue() throws NumberFormatException {
		if (getValue() != null && !(getValue() instanceof Number)) {
            Integer i = new Integer(this.getStringValue());
			return i.intValue();
		} else {
			return ((Number) getValue()).intValue();
		}
	}

	public short getShortValue() throws NumberFormatException {
		if (getValue() != null && !(getValue() instanceof Number)) {
            Short s = new Short(this.getStringValue());
            return s.shortValue();
		} else {
			return ((Number) getValue()).shortValue();
		}
	}

	public long getLongValue() throws NumberFormatException {
		if (getValue() != null && !(getValue() instanceof Number)) {
            Long l =  new Long(this.getStringValue());
            return l.longValue();
		} else {
			return ((Number) getValue()).longValue();
		}
	}

	public float getFloatValue() throws NumberFormatException {
		if (getValue() != null && !(getValue() instanceof Number)) {
            Float f = new Float(this.getStringValue());
            return f.floatValue();
		} else {
			return ((Number) getValue()).floatValue();
		}
	}

	public double getDoubleValue() throws NumberFormatException {
		if (getValue() != null && !(getValue() instanceof Number)) {
            Double d = new Double(this.getStringValue());
            return d.doubleValue();
		} else {
			return ((Number) getValue()).doubleValue();
		}
	}

	public BigInteger getBigIntegerValue() throws NumberFormatException {
		if (getValue() != null && (getValue() instanceof BigInteger)) {
			return (BigInteger) getValue();
		} else {
			return new BigInteger(getValue().toString());
		}
	}

	public BigDecimal getBigDecimalValue() throws NumberFormatException {
		if (getValue() != null && (getValue() instanceof BigDecimal)) {
			return (BigDecimal) getValue();
		} else {
			return new BigDecimal(getValue().toString());
		}
	}
}
