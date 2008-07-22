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

package org.jamocha.engine;

import java.math.BigDecimal;

import org.jamocha.Constants;
import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * The purpose of Evaluate is similar to the Evaluatn in CLIPS. The class
 * constains static methods for evaluating two values
 */
public class Evaluate {

	/**
	 * evaluate is responsible for evaluating two values. The left value is the
	 * value in the slot. The right value is the value of the object instance to
	 * match against.
	 * 
	 * @param operator
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean evaluate(int operator, JamochaValue left,
			JamochaValue right) {
		boolean eval = false;
		switch (operator) {
		case Constants.EQUAL:
			return left.equals(right);
		case Constants.NOTEQUAL:
			return !left.equals(right);
		case Constants.LESS:
			eval = evaluateLess(left.getObjectValue(), right.getObjectValue());
			break;
		case Constants.LESSEQUAL:
			eval = evaluateLessEqual(left.getObjectValue(), right
					.getObjectValue());
			break;
		case Constants.GREATER:
			eval = evaluateGreater(left.getObjectValue(), right
					.getObjectValue());
			break;
		case Constants.GREATEREQUAL:
			eval = evaluateGreaterEqual(left.getObjectValue(), right
					.getObjectValue());
			break;
		case Constants.NILL:
			eval = evaluateNull(left.getObjectValue(), right.getObjectValue());
			break;
		}
		return eval;
	}

	/**
	 * evaluate if two values are equal when left is a string and right is some
	 * object.
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean evaluateStringEqual(String left, Object right) {
		if (right instanceof Boolean)
			return left.equals(right.toString());
		else
			return left.equals(right);
	}

	public static boolean evaluateLess(Object left, Object right) {
		if (left instanceof Integer)
			return evaluateLess((Integer) left, right);
		else if (left instanceof Short)
			return evaluateLess((Short) left, right);
		else if (left instanceof Long)
			return evaluateLess((Long) left, right);
		else if (left instanceof Float)
			return evaluateLess((Float) left, right);
		else if (left instanceof Double)
			return evaluateLess((Double) left, right);
		else if (left instanceof BigDecimal)
			return evaluateLess((BigDecimal) left, right);
		else
			return false;
	}

	public static boolean evaluateLessEqual(Object left, Object right) {
		if (left instanceof Integer)
			return evaluateLessEqual((Integer) left, right);
		else if (left instanceof Short)
			return evaluateLessEqual((Short) left, right);
		else if (left instanceof Long)
			return evaluateLessEqual((Long) left, right);
		else if (left instanceof Float)
			return evaluateLessEqual((Float) left, right);
		else if (left instanceof Double)
			return evaluateLessEqual((Double) left, right);
		else if (left instanceof BigDecimal)
			return evaluateLessEqual((BigDecimal) left, right);
		else
			return false;
	}

	public static boolean evaluateGreater(Object left, Object right) {
		if (left instanceof Integer)
			return evaluateGreater(left, right);
		else if (left instanceof Short)
			return evaluateGreater((Short) left, right);
		else if (left instanceof Long)
			return evaluateGreater((Long) left, right);
		else if (left instanceof Float)
			return evaluateGreater((Float) left, right);
		else if (left instanceof Double)
			return evaluateGreater((Double) left, right);
		else if (left instanceof BigDecimal)
			return evaluateGreater((BigDecimal) left, right);
		else
			return false;
	}

	public static boolean evaluateGreaterEqual(Object left, Object right) {
		if (left instanceof Integer)
			return evaluateGreaterEqual((Integer) left, right);
		else if (left instanceof Short)
			return evaluateGreaterEqual((Short) left, right);
		else if (left instanceof Long)
			return evaluateGreaterEqual((Long) left, right);
		else if (left instanceof Float)
			return evaluateGreaterEqual((Float) left, right);
		else if (left instanceof Double)
			return evaluateGreaterEqual((Double) left, right);
		else if (left instanceof BigDecimal)
			return evaluateGreaterEqual(left, right);
		else
			return false;
	}

	/**
	 * In the case of checking if an object's attribute is null, we only check
	 * the right.
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean evaluateNull(Object left, Object right) {
		if (right == null)
			return true;
		else
			return false;
	}

	// / ------- Integer comparison methods ------- ///
	public static boolean evaluateLess(Integer left, Object right) {
		if (right instanceof Integer)
			return left.intValue() < ((Integer) right).intValue();
		else if (right instanceof Short)
			return left.intValue() < ((Short) right).intValue();
		else if (right instanceof Long)
			return left.longValue() < ((Long) right).longValue();
		else if (right instanceof Float)
			return left.floatValue() < ((Float) right).floatValue();
		else if (right instanceof Double)
			return left.doubleValue() < ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() < ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateLessEqual(Integer left, Object right) {
		if (right instanceof Integer)
			return left.intValue() <= ((Integer) right).intValue();
		else if (right instanceof Short)
			return left.intValue() <= ((Short) right).intValue();
		else if (right instanceof Long)
			return left.longValue() <= ((Long) right).longValue();
		else if (right instanceof Float)
			return left.floatValue() <= ((Float) right).floatValue();
		else if (right instanceof Double)
			return left.doubleValue() <= ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() <= ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateGreater(Integer left, Number right) {
		if (right instanceof Integer)
			return left.intValue() > ((Integer) right).intValue();
		else if (right instanceof Short)
			return left.intValue() > ((Short) right).intValue();
		else if (right instanceof Long)
			return left.longValue() > ((Long) right).longValue();
		else if (right instanceof Float)
			return left.floatValue() > ((Float) right).floatValue();
		else if (right instanceof Double)
			return left.doubleValue() > ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() > ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateGreaterEqual(Integer left, Object right) {
		if (right instanceof Integer)
			return left.intValue() >= ((Integer) right).intValue();
		else if (right instanceof Short)
			return left.intValue() >= ((Short) right).intValue();
		else if (right instanceof Long)
			return left.longValue() >= ((Long) right).longValue();
		else if (right instanceof Float)
			return left.floatValue() >= ((Float) right).floatValue();
		else if (right instanceof Double)
			return left.doubleValue() >= ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() >= ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	// / ------- Short comparison methods ------- ///
	public static boolean evaluateLess(Short left, Object right) {
		if (right instanceof Short)
			return left.shortValue() < ((Short) right).shortValue();
		else if (right instanceof Integer)
			return left.intValue() < ((Integer) right).intValue();
		else if (right instanceof Float)
			return left.floatValue() < ((Float) right).floatValue();
		else if (right instanceof Long)
			return left.longValue() < ((Long) right).longValue();
		else if (right instanceof Double)
			return left.doubleValue() < ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() < ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateLessEqual(Short left, Object right) {
		if (right instanceof Short)
			return left.shortValue() <= ((Short) right).shortValue();
		else if (right instanceof Integer)
			return left.intValue() <= ((Integer) right).intValue();
		else if (right instanceof Float)
			return left.floatValue() <= ((Float) right).floatValue();
		else if (right instanceof Long)
			return left.longValue() <= ((Long) right).longValue();
		else if (right instanceof Double)
			return left.doubleValue() <= ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() <= ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateGreater(Short left, Object right) {
		if (right instanceof Short)
			return left.shortValue() > ((Short) right).shortValue();
		else if (right instanceof Integer)
			return left.intValue() > ((Integer) right).intValue();
		else if (right instanceof Float)
			return left.floatValue() > ((Float) right).floatValue();
		else if (right instanceof Long)
			return left.longValue() > ((Long) right).longValue();
		else if (right instanceof Double)
			return left.doubleValue() > ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() > ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateGreaterEqual(Short left, Object right) {
		if (right instanceof Short)
			return left.shortValue() >= ((Short) right).shortValue();
		else if (right instanceof Integer)
			return left.intValue() >= ((Integer) right).intValue();
		else if (right instanceof Float)
			return left.floatValue() >= ((Float) right).floatValue();
		else if (right instanceof Long)
			return left.longValue() >= ((Long) right).longValue();
		else if (right instanceof Double)
			return left.doubleValue() >= ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() >= ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	// / ------- Long comparison methods ------- ///
	public static boolean evaluateLess(Long left, Object right) {
		if (right instanceof Long)
			return left.longValue() < ((Long) right).longValue();
		else if (right instanceof Integer)
			return left.longValue() < ((Integer) right).longValue();
		else if (right instanceof Short)
			return left.longValue() < ((Short) right).longValue();
		else if (right instanceof Float)
			return left.floatValue() < ((Float) right).floatValue();
		else if (right instanceof Double)
			return left.doubleValue() < ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() < ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateLessEqual(Long left, Object right) {
		if (right instanceof Long)
			return left.longValue() <= ((Long) right).longValue();
		else if (right instanceof Integer)
			return left.longValue() <= ((Integer) right).longValue();
		else if (right instanceof Short)
			return left.longValue() <= ((Short) right).longValue();
		else if (right instanceof Float)
			return left.floatValue() <= ((Float) right).floatValue();
		else if (right instanceof Double)
			return left.doubleValue() <= ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() <= ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateGreater(Long left, Object right) {
		if (right instanceof Long)
			return left.longValue() > ((Long) right).longValue();
		else if (right instanceof Integer)
			return left.longValue() > ((Integer) right).longValue();
		else if (right instanceof Short)
			return left.longValue() > ((Short) right).longValue();
		else if (right instanceof Float)
			return left.floatValue() > ((Float) right).floatValue();
		else if (right instanceof Double)
			return left.doubleValue() > ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() > ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateGreaterEqual(Long left, Object right) {
		if (right instanceof Long)
			return left.longValue() >= ((Long) right).longValue();
		else if (right instanceof Integer)
			return left.longValue() >= ((Integer) right).longValue();
		else if (right instanceof Short)
			return left.longValue() >= ((Short) right).longValue();
		else if (right instanceof Float)
			return left.floatValue() >= ((Float) right).floatValue();
		else if (right instanceof Double)
			return left.doubleValue() >= ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() >= ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	// / ------- Float comparison methods ------- ///
	public static boolean evaluateLess(Float left, Object right) {
		if (right instanceof Float)
			return left.floatValue() < ((Float) right).floatValue();
		else if (right instanceof Integer)
			return left.floatValue() < ((Integer) right).floatValue();
		else if (right instanceof Short)
			return left.floatValue() < ((Short) right).floatValue();
		else if (right instanceof Long)
			return left.floatValue() < ((Long) right).floatValue();
		else if (right instanceof Double)
			return left.doubleValue() < ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() < ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateLessEqual(Float left, Object right) {
		if (right instanceof Float)
			return left.floatValue() <= ((Float) right).floatValue();
		else if (right instanceof Integer)
			return left.floatValue() <= ((Integer) right).floatValue();
		else if (right instanceof Short)
			return left.floatValue() <= ((Short) right).floatValue();
		else if (right instanceof Long)
			return left.floatValue() <= ((Long) right).floatValue();
		else if (right instanceof Double)
			return left.doubleValue() <= ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() <= ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateGreater(Float left, Object right) {
		if (right instanceof Float)
			return left.floatValue() > ((Float) right).floatValue();
		else if (right instanceof Integer)
			return left.floatValue() > ((Integer) right).floatValue();
		else if (right instanceof Short)
			return left.floatValue() > ((Short) right).floatValue();
		else if (right instanceof Long)
			return left.floatValue() > ((Long) right).floatValue();
		else if (right instanceof Double)
			return left.doubleValue() > ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() > ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateGreaterEqual(Float left, Object right) {
		if (right instanceof Float)
			return left.floatValue() >= ((Float) right).floatValue();
		else if (right instanceof Integer)
			return left.floatValue() >= ((Integer) right).floatValue();
		else if (right instanceof Short)
			return left.floatValue() >= ((Short) right).floatValue();
		else if (right instanceof Long)
			return left.floatValue() >= ((Long) right).floatValue();
		else if (right instanceof Double)
			return left.doubleValue() >= ((Double) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() >= ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	// / ------- Double comparison methods ------- ///
	public static boolean evaluateLess(Double left, Object right) {
		if (right instanceof Double)
			return left.doubleValue() < ((Double) right).doubleValue();
		else if (right instanceof Integer)
			return left.doubleValue() < ((Integer) right).doubleValue();
		else if (right instanceof Short)
			return left.doubleValue() < ((Short) right).doubleValue();
		else if (right instanceof Float)
			return left.doubleValue() < ((Float) right).doubleValue();
		else if (right instanceof Long)
			return left.doubleValue() < ((Long) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() <= ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateLess(BigDecimal left, Object right) {
		if (right instanceof Double)
			return left.doubleValue() < ((Double) right).doubleValue();
		else if (right instanceof Integer)
			return left.doubleValue() < ((Integer) right).doubleValue();
		else if (right instanceof Short)
			return left.doubleValue() < ((Short) right).doubleValue();
		else if (right instanceof Float)
			return left.doubleValue() < ((Float) right).doubleValue();
		else if (right instanceof Long)
			return left.doubleValue() < ((Long) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() < ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateLessEqual(Double left, Object right) {
		if (right instanceof Double)
			return left.doubleValue() <= ((Double) right).doubleValue();
		else if (right instanceof Integer)
			return left.doubleValue() <= ((Integer) right).doubleValue();
		else if (right instanceof Short)
			return left.doubleValue() <= ((Short) right).doubleValue();
		else if (right instanceof Float)
			return left.doubleValue() <= ((Float) right).doubleValue();
		else if (right instanceof Long)
			return left.doubleValue() <= ((Long) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() <= ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateLessEqual(BigDecimal left, Object right) {
		if (right instanceof Double)
			return left.doubleValue() <= ((Double) right).doubleValue();
		else if (right instanceof Integer)
			return left.doubleValue() <= ((Integer) right).doubleValue();
		else if (right instanceof Short)
			return left.doubleValue() <= ((Short) right).doubleValue();
		else if (right instanceof Float)
			return left.doubleValue() <= ((Float) right).doubleValue();
		else if (right instanceof Long)
			return left.doubleValue() <= ((Long) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() <= ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateGreater(BigDecimal left, Object right) {
		if (right instanceof Double)
			return left.doubleValue() > ((Double) right).doubleValue();
		else if (right instanceof Integer)
			return left.doubleValue() > ((Integer) right).doubleValue();
		else if (right instanceof Short)
			return left.doubleValue() > ((Short) right).doubleValue();
		else if (right instanceof Float)
			return left.doubleValue() > ((Float) right).doubleValue();
		else if (right instanceof Long)
			return left.doubleValue() > ((Long) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() > ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateGreater(Double left, Object right) {
		if (right instanceof Double)
			return left.doubleValue() > ((Double) right).doubleValue();
		else if (right instanceof Integer)
			return left.doubleValue() > ((Integer) right).doubleValue();
		else if (right instanceof Short)
			return left.doubleValue() > ((Short) right).doubleValue();
		else if (right instanceof Float)
			return left.doubleValue() > ((Float) right).doubleValue();
		else if (right instanceof Long)
			return left.doubleValue() > ((Long) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() > ((BigDecimal) right).doubleValue();
		else
			return false;
	}

	public static boolean evaluateGreaterEqual(Double left, Object right) {
		if (right instanceof Double)
			return left.doubleValue() >= ((Double) right).doubleValue();
		else if (right instanceof Integer)
			return left.doubleValue() >= ((Integer) right).doubleValue();
		else if (right instanceof Short)
			return left.doubleValue() >= ((Short) right).doubleValue();
		else if (right instanceof Float)
			return left.doubleValue() >= ((Float) right).doubleValue();
		else if (right instanceof Long)
			return left.doubleValue() >= ((Long) right).doubleValue();
		else if (right instanceof BigDecimal)
			return left.doubleValue() >= ((BigDecimal) right).doubleValue();
		else
			return false;
	}

}
