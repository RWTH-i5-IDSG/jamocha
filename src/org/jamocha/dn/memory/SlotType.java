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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;

import org.jamocha.languages.common.ScopeStack.Symbol;

/**
 * Enum holding all types this system supports.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public enum SlotType {
	/**
	 * Enum value for integer types.
	 */
	LONG(Long.class),
	/**
	 * Enum value for floating point types.
	 */
	DOUBLE(Double.class),
	/**
	 * Enum value for string types.
	 */
	STRING(String.class),
	/**
	 * Enum value for boolean types.
	 */
	BOOLEAN(Boolean.class),
	/**
	 * Enum value for fact address types.
	 */
	FACTADDRESS(FactIdentifier.class),
	/**
	 * Enum value for date time types.
	 */
	DATETIME(ZonedDateTime.class),
	/**
	 * Enum value for nil values of undetermined type.
	 */
	NIL(Object.class),
	/**
	 * Enum value for symbol types.
	 */
	SYMBOL(Symbol.class);

	final private Class<?> javaClass;

	private SlotType(final Class<?> javaClazz) {
		this.javaClass = javaClazz;
	}

	public Class<?> getJavaClass() {
		return this.javaClass;
	}

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

	@Override
	public String toString() {
		return this.name();
	}

	public static ZonedDateTime convert(final String image) {
		// < #GMT_OFFSET: ("+"|"-") <DIGIT> <DIGIT> >
		// < #DATE: <DIGIT> <DIGIT> <DIGIT> <DIGIT> "-" <DIGIT> <DIGIT> "-" <DIGIT> <DIGIT> >
		// < #TIME: <DIGIT> <DIGIT> ":" <DIGIT> <DIGIT> ( ":" <DIGIT> <DIGIT>)? >
		// < DATETIME: <DATE> ( " " <TIME> (<GMT_OFFSET>)? )? >
		return ZonedDateTime.parse(
				image,
				new DateTimeFormatterBuilder()
						// date
						.appendValue(ChronoField.YEAR, 4)
						.appendLiteral('-')
						.appendValue(ChronoField.MONTH_OF_YEAR, 2)
						.appendLiteral('-')
						.appendValue(ChronoField.DAY_OF_MONTH, 2)
						// optional time
						.optionalStart().appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 2)
						.appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2)
						.optionalStart().appendLiteral(':')
						.appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalEnd().optionalEnd()
						// optional offset
						.optionalStart().appendOffset("+HH", "+HH").optionalEnd().toFormatter());
	}
}
