package org.jamocha.parser;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;

public class JamochaValue implements Parameter {

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

	public static JamochaValue newBinding(BoundParam value) {
		return new JamochaValue(JamochaType.BINDING, value);
	}

	public static JamochaValue newSlot(Slot value) {
		return new JamochaValue(JamochaType.SLOT, value);
	}

	private JamochaType type;

	private Object value;

	public JamochaValue(JamochaType type, Object value) {
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

	public JamochaValue(Object object) {
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
		assert (type.equals(JamochaType.SLOT));
		return (Slot) value;
	}

	public boolean getBooleanValue() {
		assert (type.equals(JamochaType.BOOLEAN));
		return ((Boolean) value).booleanValue();
	}

	public double getDoubleValue() {
		assert (type.equals(JamochaType.DOUBLE));
		return ((Number) value).doubleValue();
	}

	public long getLongValue() {
		assert (type.equals(JamochaType.LONG));
		return ((Number) value).longValue();
	}

	public String getStringValue() {
		assert (type.equals(JamochaType.STRING));
		return (String) value;
	}
	
	public GregorianCalendar getDateValue() {
		assert (type.equals(JamochaType.DATETIME));
		return (GregorianCalendar) value;
	}
	
	public String getIdentifierValue() {
		assert (type.equals(JamochaType.IDENTIFIER));
		return (String) value;
	}

	public Fact getFactValue() {
		assert (type.equals(JamochaType.FACT));
		return (Fact) value;
	}

	public long getFactIdValue() {
		assert (type.equals(JamochaType.FACT_ID));
		return ((Number) value).longValue();
	}

	public JamochaValue getListValue(int index) {
		assert (type.equals(JamochaType.LIST));
		return ((JamochaValue[]) value)[index];
	}

	public int getListCount() {
		assert (type.equals(JamochaType.LIST));
		return ((JamochaValue[]) value).length;
	}
	
	private String fillToFixedLength(int val, String fill, int length) {
		String res=String.valueOf(val);
		while (res.length() < length)
			res=fill+res;
		return res;
	}

	@Override
	public String toString() {
		switch (type) {
		case NIL:
			return "NIL";
		case STRING:
			return "\"" + value + "\"";
		case FACT_ID:
			return "f-" + value.toString();
		case DATETIME:
			GregorianCalendar c=(GregorianCalendar)value;
			String gmtOffsetString;
			int gmtOffsetMillis=c.get(Calendar.ZONE_OFFSET);
			gmtOffsetString = ( gmtOffsetMillis>=0 ? "+" : "-");
			int gmtOffsetHours=gmtOffsetMillis/(1000*60*60); //hopefully ;)
			gmtOffsetString+=fillToFixedLength(gmtOffsetHours, "0", 2);
			return
				"\"" + fillToFixedLength( c.get(Calendar.YEAR),"0",4 ) + "-" +
				fillToFixedLength( c.get(Calendar.MONTH)+1,"0",2 ) + "-" +
				fillToFixedLength( c.get(Calendar.DAY_OF_MONTH),"0",2 ) + " " +
				fillToFixedLength( c.get(Calendar.HOUR_OF_DAY),"0",2 ) + ":" +
				fillToFixedLength( c.get(Calendar.MINUTE),"0",2 ) + ":" +
				fillToFixedLength( c.get(Calendar.SECOND),"0",2 ) +
				gmtOffsetString + "\"";
		case LIST:
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			JamochaValue[] list = (JamochaValue[]) value;
			for (int i = 0; i < list.length; ++i) {
				sb.append(list[i].toString());
				if (i < list.length - 1) {
					sb.append(", ");
				}
			}
			sb.append(']');
			return sb.toString();
		default:
			return value.toString();
		}
	}

	public JamochaValue implicitCast(JamochaType type)
			throws IllegalConversionException {
		if (type.equals(JamochaType.UNDEFINED) || type.equals(this.type)) {
			return this;
		}
		if (type.equals(JamochaType.LIST)) {
			return singletonList(this);
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
					return JamochaValue
							.newLong( ((Calendar) value).getTimeInMillis()/1000 );
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
				return JamochaValue.newFactId((Long)value);
			case DATETIME:
				GregorianCalendar foo = new GregorianCalendar();
				foo.setTimeZone(TimeZone.getTimeZone("UTC"));
				foo.setTimeInMillis((Long)value);
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
				return JamochaValue.newLong((Long)value);
			case FACT_ID:
				return this;
			}
			break;
		case FACT:
			switch (type) {
			case FACT_ID:
				return JamochaValue.newFactId(((Fact) value).getFactId());
			}
		}
		throw new IllegalConversionException("Unable to cast " + this.type
				+ " to type " + type + ".");
	}

	public boolean is(JamochaType type) {
		if (this.type.equals(type)) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((type == null) ? 0 : type.hashCode());
		result = PRIME * result + ((value == null) ? 0 : value.hashCode());
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
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public boolean isObjectBinding() {
	    return false;
	}

	public String getExpressionString() {
	    return toString();
	}

	public JamochaValue getValue(Rete engine) throws EvaluationException {
	    return this;
	}
}
