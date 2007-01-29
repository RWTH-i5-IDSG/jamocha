package org.jamocha.parser;

import java.util.Collection;
import java.util.Iterator;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Slot;

public class JamochaValue {

	public static final JamochaValue NIL = new JamochaValue(JamochaType.NIL,
			null);

	public static final JamochaValue EMPTY_LIST = new JamochaValue(
			JamochaType.LIST, new JamochaValue[] {});

	public static JamochaValue singletonList(JamochaValue value) {
		if (value == null) {
			return EMPTY_LIST;
		}
		return new JamochaValue(JamochaType.LIST, new JamochaValue[] { value });
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

	@Override
	public String toString() {
		switch (type) {
		case NIL:
			return "NIL";
		case STRING:
			return "\"" + value + "\"";
		case FACT_ID:
			return "f-" + value.toString();
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

	public JamochaValue implicitCast(JamochaType type) {
		switch (this.type) {
		case BOOLEAN:
			switch (type) {
			case BOOLEAN:
				return this;
			case DOUBLE:
				return new JamochaValue(JamochaType.DOUBLE, ((Boolean) value)
						.booleanValue() ? 1.0 : 0.0);
			case LONG:
				return new JamochaValue(JamochaType.LONG, ((Boolean) value)
						.booleanValue() ? 1 : 0);
			}
		case DOUBLE:
			switch (type) {
			case BOOLEAN:
				return new JamochaValue(JamochaType.BOOLEAN, ((Number) value)
						.doubleValue() != 0.0);
			case DOUBLE:
				return this;
			case LONG:
				return new JamochaValue(JamochaType.LONG, ((Number) value)
						.longValue());
			}
		case LONG:
			switch (type) {
			case BOOLEAN:
				return new JamochaValue(JamochaType.BOOLEAN, ((Number) value)
						.longValue() != 0);
			case DOUBLE:
				return new JamochaValue(JamochaType.DOUBLE, ((Number) value)
						.doubleValue());
			case LONG:
				return this;
			case FACT_ID:
				return new JamochaValue(JamochaType.FACT_ID, value);
			}
			break;
		case FACT_ID:
			switch (type) {
			case BOOLEAN:
				return new JamochaValue(JamochaType.BOOLEAN, ((Number) value)
						.longValue() != 0);
			case DOUBLE:
				return new JamochaValue(JamochaType.DOUBLE, ((Number) value)
						.doubleValue());
			case LONG:
				return new JamochaValue(JamochaType.LONG, value);
			case FACT_ID:
				return this;
			}
			break;
		case FACT:
			switch (type) {
			case FACT_ID:
				return new JamochaValue(JamochaType.FACT_ID, ((Fact) value)
						.getFactId());
			}
		}
		throw new IllegalArgumentException("Unable to cast " + this.type
				+ " to type " + type + ".");
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
		if (getClass() != obj.getClass())
			return false;
		final JamochaValue other = (JamochaValue) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
