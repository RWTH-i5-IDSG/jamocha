package org.jamocha.parser;


public enum JamochaType {
	UNDEFINED, NIL, BOOLEAN, LONG, DOUBLE, STRING, LIST, OBJECT, IDENTIFIER, FACT, FACT_ID, SLOT, BINDING;

	public static final JamochaType[] NONE = new JamochaType[] {};

	public static final JamochaType[] IDENTIFIERS = new JamochaType[] { IDENTIFIER };

	public static final JamochaType[] OBJECTS = new JamochaType[] { OBJECT };

	public static final JamochaType[] ANY = new JamochaType[] { UNDEFINED };

	public static final JamochaType[] NUMBERS = new JamochaType[] { DOUBLE,
			LONG };

	public static final JamochaType[] LONGS = new JamochaType[] { LONG };

	public static final JamochaType[] DOUBLES = new JamochaType[] { DOUBLE };

	public static final JamochaType[] STRINGS = new JamochaType[] { STRING };

	public static final JamochaType[] PRIMITIVES = new JamochaType[] { BOOLEAN,
			LONG, DOUBLE, STRING };

	public static JamochaType getMappingType(Class clzz) {
		if (clzz.isArray()) {
			return LIST;
		} else if (clzz.isPrimitive()) {
			if (clzz == int.class) {
				return LONG;
			} else if (clzz == short.class) {
				return LONG;
			} else if (clzz == long.class) {
				return LONG;
			} else if (clzz == float.class) {
				return DOUBLE;
			} else if (clzz == byte.class) {
				return LONG;
			} else if (clzz == double.class) {
				return DOUBLE;
			} else if (clzz == boolean.class) {
				return BOOLEAN;
			}
		} else if (clzz == String.class) {
			return STRING;
		}
		return OBJECT;

	}
}
