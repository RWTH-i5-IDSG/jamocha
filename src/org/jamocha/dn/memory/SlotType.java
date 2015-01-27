/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.dn.memory;

import java.lang.reflect.Array;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.jamocha.languages.common.ScopeStack.Symbol;

/**
 * Enum holding all types this system supports.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SlotType {
	/**
	 * Enum value for integer types.
	 */
	LONG(Long.class, false), LONGS(Long[].class, true),
	/**
	 * Enum value for floating point types.
	 */
	DOUBLE(Double.class, false), DOUBLES(Double[].class, true),
	/**
	 * Enum value for string types.
	 */
	STRING(String.class, false), STRINGS(String[].class, true),
	/**
	 * Enum value for boolean types.
	 */
	BOOLEAN(Boolean.class, false), BOOLEANS(Boolean[].class, true),
	/**
	 * Enum value for fact address types.
	 */
	FACTADDRESS(FactIdentifier.class, false), FACTADDRESSES(FactIdentifier[].class, true),
	/**
	 * Enum value for date time types.
	 */
	DATETIME(ZonedDateTime.class, false), DATETIMES(ZonedDateTime[].class, true),
	/**
	 * Enum value for nil values of undetermined type.
	 */
	NIL(Object.class, false), NILS(Object[].class, true),
	/**
	 * Enum value for symbol types.
	 */
	SYMBOL(Symbol.class, false), SYMBOLS(Symbol[].class, true);

	final private Class<?> javaClass;
	final private boolean isArrayType;

	/**
	 * Static instance of an empty array of types. Can e.g. be used by functions without parameters
	 * to specify the parameter types.
	 */
	final public static SlotType[] empty = new SlotType[] {};

	final public static SlotType[] nCopies(final SlotType type, final int num) {
		final SlotType[] types = new SlotType[num];
		Arrays.fill(types, type);
		return types;
	}

	final public static SlotType arrayToSingle(final SlotType type) {
		switch (type) {
		case BOOLEANS:
			return BOOLEAN;
		case DATETIMES:
			return DATETIME;
		case DOUBLES:
			return DOUBLE;
		case FACTADDRESSES:
			return FACTADDRESS;
		case LONGS:
			return LONG;
		case NILS:
			return NIL;
		case STRINGS:
			return STRING;
		case SYMBOLS:
			return SYMBOL;
		default:
			throw new IllegalArgumentException(type.name() + " is not an array type!");
		}
	}

	final public static SlotType singleToArray(final SlotType type) {
		switch (type) {
		case BOOLEAN:
			return BOOLEANS;
		case DATETIME:
			return DATETIMES;
		case DOUBLE:
			return DOUBLES;
		case FACTADDRESS:
			return FACTADDRESSES;
		case LONG:
			return LONGS;
		case NIL:
			return NILS;
		case STRING:
			return STRINGS;
		case SYMBOL:
			return SYMBOLS;
		default:
			throw new IllegalArgumentException(type.name() + " is an array type!");
		}
	}

	final public static Object[] newArrayInstance(final SlotType arrayType, final int length) {
		assert arrayType.isArrayType;
		return (Object[]) Array.newInstance(arrayToSingle(arrayType).javaClass, length);
	}

	public static ZonedDateTime convert(final String image) {
		// < #GMT_OFFSET: ("+"|"-") ( <DIGIT> )? <DIGIT> >
		// < #DATE: <DIGIT> <DIGIT> <DIGIT> <DIGIT> "-" <DIGIT> <DIGIT> "-" <DIGIT> <DIGIT> >
		// < #TIME: <DIGIT> <DIGIT> ":" <DIGIT> <DIGIT> ( ":" <DIGIT> <DIGIT>)? >
		// < DATETIME: <DATE> ( " " <TIME> (<GMT_OFFSET>)? )? >
		final char pm = image.charAt(image.length() - 2);
		final String parse;
		if ('+' == pm || '-' == pm) {
			parse = image.substring(0, image.length() - 1).concat("0").concat(image.substring(image.length() - 1));
		} else {
			parse = image;
		}
		return ZonedDateTime.parse(
				parse,
				new DateTimeFormatterBuilder()
						// date
						.appendValue(ChronoField.YEAR, 4).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2)
						.appendLiteral('-')
						.appendValue(ChronoField.DAY_OF_MONTH, 2)
						// optional time
						.optionalStart().appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':')
						.appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':')
						.appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalEnd().optionalEnd()
						// optional offset
						.optionalStart().appendOffset("+HH", "+HH").optionalEnd().toFormatter());
	}
}
