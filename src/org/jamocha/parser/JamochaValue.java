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

package org.jamocha.parser;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.jamocha.engine.BoundParam;
import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.Slot;
import org.jamocha.formatter.Formattable;
import org.jamocha.formatter.Formatter;

public class JamochaValue implements Parameter, Formattable {

	public Object clone() {
		return this;
	}

	public static final JamochaValue NIL = new JamochaValue(JamochaType.NIL,
			null);

	public static final JamochaValue TRUE = new JamochaValue(
			JamochaType.BOOLEAN, Boolean.valueOf(true));

	public static final JamochaValue FALSE = new JamochaValue(
			JamochaType.BOOLEAN, Boolean.valueOf(false));

	public static final JamochaValue EMPTY_LIST = new JamochaValue(
			JamochaType.LIST, new JamochaValue[] {});

	public static JamochaValue singletonList(JamochaValue value) {
		if (value == null || value.equals(NIL)) {
			return EMPTY_LIST;
		}
		return new JamochaValue(JamochaType.LIST, new JamochaValue[] { value });
	}

	public static JamochaValue newBoolean(boolean value) {
		return value ? TRUE : FALSE;
	}

	public static JamochaValue newDate(GregorianCalendar value) {
		return new JamochaValue(JamochaType.DATETIME, value);
	}

	public static JamochaValue newLong(long value) {
		return new JamochaValue(JamochaType.LONG, value);
	}

	public static JamochaValue newDouble(double value) {
		return new JamochaValue(JamochaType.DOUBLE, value);
	}

	public static JamochaValue newString(String value) {
		return new JamochaValue(JamochaType.STRING, value);
	}

	public static JamochaValue newObject(Object value) {
		return new JamochaValue(JamochaType.OBJECT, value);
	}

	public static JamochaValue newIdentifier(String value) {
		return new JamochaValue(JamochaType.IDENTIFIER, value);
	}

	public static JamochaValue newFact(Fact value) {
		return new JamochaValue(JamochaType.FACT, value);
	}

	public static JamochaValue newFactId(long value) {
		return new JamochaValue(JamochaType.FACT_ID, value);
	}

	public static JamochaValue newList(JamochaValue[] values) {
		return new JamochaValue(JamochaType.LIST, values);
	}

	public static JamochaValue newList(List<JamochaValue> list) {
		return new JamochaValue(JamochaType.LIST, list.toArray());
	}

	public static JamochaValue newList() {
		return new JamochaValue(JamochaType.LIST, new JamochaValue[0]);
	}

	public static JamochaValue newBinding(BoundParam value) {
		return new JamochaValue(JamochaType.BINDING, value);
	}

	public static JamochaValue newSlot(Slot value) {
		return new JamochaValue(JamochaType.SLOT, value);
	}

	public static JamochaValue newValueAutoType(Object value) {
		return new JamochaValue(value);
	}

	private JamochaType type;

	private Object value;

	protected JamochaValue(JamochaType type, Object value) {
		if (type == null) {
			throw new IllegalArgumentException("type of a value can't be null.");
		}
		if (!JamochaType.NIL.equals(type) && value == null) {
			throw new IllegalArgumentException("the value for type " + type
					+ " can't be null.");
		}
		this.type = type;
		switch (type) {
		case BOOLEAN:
			if (value instanceof Boolean) {
				this.value = value;
			} else {
				this.value = Boolean.TRUE;
			}
			break;
		case DOUBLE:
		case LONG:
			if (value instanceof Number) {
				this.value = value;
			} else {
				throw new IllegalArgumentException("the value for type " + type
						+ " must be of type java.lang.Number.");
			}
			break;
		case DATETIME:
			if (value instanceof GregorianCalendar) {
				this.value = value;
			} else {
				throw new IllegalArgumentException("the value for type " + type
						+ " must be of type java.util.GregorianCalendar.");
			}
			break;
		case STRING:
			if (value instanceof String) {
				this.value = value;
			} else {
				throw new IllegalArgumentException("the value for type " + type
						+ " must be of type java.lang.String.");
			}
			break;
		case FACT:
			if (value instanceof Fact) {
				this.value = value;
			} else {
				throw new IllegalArgumentException("the value for type " + type
						+ " must be of type org.jamocha.rete.Fact.");
			}
			break;
		case FACT_ID:
			if (value instanceof Long) {
				this.value = value;
			} else {
				throw new IllegalArgumentException("the value for type " + type
						+ " must be of type java.lang.Long.");
			}
			break;
		case LIST:
			if (value instanceof JamochaValue[]) {
				this.value = value;
			} else {
				throw new IllegalArgumentException("the value for type " + type
						+ " must be of type org.jamocha.parser.JamochaValue[].");
			}
			break;
		case NIL:
		case OBJECT:
			this.value = value;
		}
		this.type = type;
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	protected JamochaValue(Object object) {
		if (object == null) {
			type = JamochaType.NIL;
		} else if (object instanceof Long || object instanceof Integer
				|| object instanceof Short || object instanceof Byte) {
			value = ((Number) object).longValue();
			type = JamochaType.LONG;
		} else if (object instanceof Double || object instanceof Float) {
			value = ((Number) object).doubleValue();
			type = JamochaType.DOUBLE;
		} else if (object instanceof String) {
			value = object;
			type = JamochaType.STRING;
		} else if (object instanceof GregorianCalendar) {
			value = object;
			type = JamochaType.DATETIME;
		} else if (object instanceof Boolean) {
			value = object;
			type = JamochaType.BOOLEAN;
		} else if (object instanceof Collection) {
			Collection collection = (Collection) object;
			JamochaValue[] array = new JamochaValue[collection.size()];
			Iterator it = collection.iterator();
			for (int i = 0; i < collection.size() && it.hasNext(); ++i) {
				array[i] = new JamochaValue(it.next());
			}
		} else if (object instanceof Fact) {
			value = object;
			type = JamochaType.FACT;
		} else {
			value = object;
			type = JamochaType.OBJECT;
		}
	}

	public JamochaType getType() {
		return type;
	}

	public Object getObjectValue() {
		return value;
	}

	public Slot getSlotValue() {
		// assert (type.equals(JamochaType.SLOT));
		return (Slot) value;
	}

	public boolean getBooleanValue() {
		// assert (type.equals(JamochaType.BOOLEAN));
		return ((Boolean) value).booleanValue();
	}

	public double getDoubleValue() {
		// assert (type.equals(JamochaType.DOUBLE));
		return ((Number) value).doubleValue();
	}

	public long getLongValue() {
		// if we have a date, we can compute the timestamp here
		if (type==JamochaType.DATETIME) {
			return getDateValue().getTimeInMillis();
		}
		if (type==JamochaType.STRING) {
			return Long.parseLong(value.toString());
		}
		
		return ((Number) value).longValue();
	}

	public String getStringValue() {
		// assert (type.equals(JamochaType.STRING));
		return (String) value;
	}

	public GregorianCalendar getDateValue() {
		// assert (type.equals(JamochaType.DATETIME));
		return (GregorianCalendar) value;
	}

	public String getIdentifierValue() {
		// assert (type.equals(JamochaType.IDENTIFIER));
		return (String) value;
	}

	public Fact getFactValue() {
		// assert (type.equals(JamochaType.FACT));
		return (Fact) value;
	}

	public long getFactIdValue() {
		// assert (type.equals(JamochaType.FACT_ID));
		return ((Number) value).longValue();
	}

	public JamochaValue getListValue(int index) {
		// assert (type.equals(JamochaType.LIST));
		return ((JamochaValue[]) value)[index];
	}

	public int getListCount() {
		// assert (type.equals(JamochaType.LIST));
		return ((JamochaValue[]) value).length;
	}

	@Override
	public String toString() {
		if (type.equals(JamochaType.STRING))
			return ParserUtils.getStringLiteral(ParserFactory.getFormatter()
					.visit(this));
		else
			return format(ParserFactory.getFormatter());
	}

	public JamochaValue implicitCast(JamochaType type)
			throws IllegalConversionException {
		if (type.equals(JamochaType.UNDEFINED) || type.equals(this.type)) {
			return this;
		}
		if (type.equals(JamochaType.LIST)) {
			return singletonList(this);
		}
		if (type.equals(JamochaType.STRING)) {
			if (value != null)
				return JamochaValue.newString(value.toString());
			// TODO needs big fix!
			else
				return JamochaValue.newString("");
		}
		switch (this.type) {
		case BOOLEAN:
			switch (type) {
			case BOOLEAN:
				return this;
			case DOUBLE:
				return JamochaValue
						.newDouble(((Boolean) value).booleanValue() ? 1.0 : 0.0);
			case LONG:
				return JamochaValue
						.newLong(((Boolean) value).booleanValue() ? 1 : 0);
			}
		case DATETIME:
			switch (type) {
			case LONG:
				return JamochaValue.newLong(((Calendar) value)
						.getTimeInMillis() / 1000);
			case DOUBLE:
				return JamochaValue.newDouble(((Number) (((Calendar) value)
						.getTimeInMillis() / 1000)).doubleValue());
			case DATETIME:
				return this;

			}
		case DOUBLE:
			switch (type) {
			case BOOLEAN:
				return JamochaValue
						.newBoolean(((Number) value).doubleValue() != 0.0);
			case DOUBLE:
				return this;
			case LONG:
				return JamochaValue.newLong(((Number) value).longValue());
			}
		case LONG:
			switch (type) {
			case BOOLEAN:
				return JamochaValue
						.newBoolean(((Number) value).longValue() != 0);
			case DOUBLE:
				return JamochaValue.newDouble(((Number) value).doubleValue());
			case LONG:
				return this;
			case FACT_ID:
				return JamochaValue.newFactId((Long) value);
			case DATETIME:
				GregorianCalendar foo = new GregorianCalendar();
				foo.setTimeZone(TimeZone.getTimeZone("UTC"));
				foo.setTimeInMillis((Long) value);
				return JamochaValue.newDate(foo);
			}
			break;
		case FACT_ID:
			switch (type) {
			case BOOLEAN:
				return JamochaValue
						.newBoolean(((Number) value).longValue() != 0);
			case DOUBLE:
				return JamochaValue.newDouble(((Number) value).doubleValue());
			case LONG:
				return JamochaValue.newLong((Long) value);
			case FACT_ID:
				return this;
			}
			break;
		case FACT:
			switch (type) {
			case FACT_ID:
				return JamochaValue.newFactId(((Fact) value).getFactId());
			}
			
		case OBJECT:
			switch (type) {
			case BOOLEAN: return JamochaValue.newBoolean((Boolean) value);
			case DOUBLE:  return JamochaValue.newDouble((Double) value);
			case LONG:    return JamochaValue.newLong((Long) value);
			}
			
			break;
		}
		throw new IllegalConversionException("Unable to cast " + this.type
				+ " to type " + type + ".");
	}

	public boolean is(JamochaType type) {
		return this.type == type;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((type == null) ? 0 : type.hashCode());
		if (type == JamochaType.LIST) {
			JamochaValue[] temp = (JamochaValue[]) value;
			for (JamochaValue obj : temp) {
				result = PRIME * result + ((obj == null) ? 0 : obj.hashCode());
			}
		} else {
			result = PRIME * result + ((value == null) ? 0 : value.hashCode());
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		final JamochaValue other = (JamochaValue) obj;
		if (value == null) {
			return other.value == null || other.value == "NIL";
		}
		if (value instanceof JamochaValue[]
				&& other.value instanceof JamochaValue[]) {
			JamochaValue[] a1 = (JamochaValue[]) value;
			JamochaValue[] a2 = (JamochaValue[]) other.value;
			return Arrays.equals(a1, a2);
		}
		return value.equals(other.value);
	}

	public boolean isFactBinding() {
		return false;
	}

	private String fillToFixedLength(int val, String fill, int length) {
		String res = String.valueOf(val);
		while (res.length() < length)
			res = fill + res;
		return res;
	}

	public String getExpressionString() {
		StringBuilder sb = new StringBuilder();
		switch (getType()) {
		case NIL:
			return "NIL";
		case STRING:
			return "\"" + getStringValue() + "\"";
		case FACT_ID:
			return "f-" + getFactIdValue();
		case DATETIME:
			GregorianCalendar c = (GregorianCalendar) getDateValue();
			sb.append(fillToFixedLength(c.get(Calendar.YEAR), "0", 4)).append(
					'-');
			sb.append(fillToFixedLength(c.get(Calendar.MONTH) + 1, "0", 2))
					.append('-');
			sb.append(fillToFixedLength(c.get(Calendar.DAY_OF_MONTH), "0", 2))
					.append(' ');
			sb.append(fillToFixedLength(c.get(Calendar.HOUR_OF_DAY), "0", 2))
					.append(':');
			sb.append(fillToFixedLength(c.get(Calendar.MINUTE), "0", 2))
					.append(':');
			sb.append(fillToFixedLength(c.get(Calendar.SECOND), "0", 2));
			int gmtOffsetMillis = c.get(Calendar.ZONE_OFFSET);
			if (gmtOffsetMillis >= 0) {
				sb.append('+');
			} else {
				sb.append('-');
			}
			int gmtOffsetHours = gmtOffsetMillis / (1000 * 60 * 60);
			sb.append(fillToFixedLength(gmtOffsetHours, "0", 2));
			break;
		case LIST:
			sb.append('[');
			for (int i = 0; i < getListCount(); ++i) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(getListValue(i).getExpressionString());
			}
			sb.append(']');
			break;
		case SLOT:
			sb.append('(');
			Slot slot = getSlotValue();
			sb.append(slot.getName());
			sb.append(' ');
			sb.append(slot.getValue().getExpressionString());
			sb.append(')');
			break;

		default:
			sb.append(getObjectValue().toString());
			break;
		}
		return sb.toString();

	}

	public JamochaValue getValue(Engine engine) throws EvaluationException {
		return this;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
}
