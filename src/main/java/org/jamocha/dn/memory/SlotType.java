/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.dn.memory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.function.IntFunction;

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
    LONG(Long.class, Long[]::new, false), LONGS(Long[].class, Long[]::new, true),
    /**
     * Enum value for floating point types.
     */
    DOUBLE(Double.class, Double[]::new, false), DOUBLES(Double[].class, Double[]::new, true),
    /**
     * Enum value for string types.
     */
    STRING(String.class, String[]::new, false), STRINGS(String[].class, String[]::new, true),
    /**
     * Enum value for boolean types.
     */
    BOOLEAN(Boolean.class, Boolean[]::new, false), BOOLEANS(Boolean[].class, Boolean[]::new, true),
    /**
     * Enum value for fact address types.
     */
    FACTADDRESS(FactIdentifier.class, FactIdentifier[]::new, false),
    FACTADDRESSES(FactIdentifier[].class, FactIdentifier[]::new, true),
    /**
     * Enum value for date time types.
     */
    DATETIME(ZonedDateTime.class, ZonedDateTime[]::new, false),
    DATETIMES(ZonedDateTime[].class, ZonedDateTime[]::new, true),
    /**
     * Enum value for nil values of undetermined type.
     */
    NIL(Object.class, Object[]::new, false), NILS(Object[].class, Object[]::new, true),
    /**
     * Enum value for symbol types.
     */
    SYMBOL(Symbol.class, Symbol[]::new, false), SYMBOLS(Symbol[].class, Symbol[]::new, true);

    private final Class<?> javaClass;
    private final IntFunction<Object[]> arrayCtor;
    private final boolean isArrayType;

    /**
     * Static INSTANCE of an empty array of types. Can e.g. be used by functions without parameters to specify the
     * parameter types.
     */
    public static final SlotType[] EMPTY = new SlotType[]{};

    public static final SlotType[] nCopies(final SlotType type, final int num) {
        final SlotType[] types = new SlotType[num];
        Arrays.fill(types, type);
        return types;
    }

    public static final SlotType arrayToSingle(final SlotType type) {
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

    public static final SlotType singleToArray(final SlotType type) {
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

    public static final Object[] newArrayInstance(final SlotType arrayType, final int length) {
        assert arrayType.isArrayType;
        return arrayType.arrayCtor.apply(length);
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
        return ZonedDateTime.parse(parse, new DateTimeFormatterBuilder()
                // date
                .appendValue(ChronoField.YEAR, 4).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2)
                .appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2)
                // optional time
                .optionalStart().appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalEnd().optionalEnd()
                // optional offset
                .optionalStart().appendOffset("+HH", "+HH").optionalEnd().toFormatter());
    }
}
