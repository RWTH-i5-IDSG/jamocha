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
 * The purpose of Evaluate is similar to the Evaluatn in CLIPS. The class
 * constains static methods for evaluating two values
 */
public class Evaluate {

    /**
     * evaluate is responsible for evaluating two values. The left value
     * is the value in the slot. The right value is the value of the object
     * instance to match against.
     * @param operator
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluate(int operator, Object left, Object right){
        boolean eval = false;
        switch (operator){
            case Constants.EQUAL:
                eval = evaluateEqual(left,right);
                break;
            case Constants.NOTEQUAL:
                eval = evaluateNotEqual(left,right);
                break;
            case Constants.LESS:
                eval = evaluateLess(left,right);
                break;
            case Constants.LESSEQUAL:
                eval = evaluateLessEqual(left,right);
                break;
            case Constants.GREATER:
                eval = evaluateGreater(left,right);
                break;
            case Constants.GREATEREQUAL:
                eval = evaluateGreaterEqual(left,right);
                break;
            case Constants.NILL:
                eval = evaluateNull(left,right);
                break;
        }
        return eval;
    }
    
    /**
     * evaluate if two values are equal. If they are equal
     * return true. otherwise return false.
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluateEqual(Object left, Object right){
    	if (left instanceof String){
            return evaluateStringEqual((String)left,right);
    	} else if (left instanceof Boolean) {
    		return evaluateEqual((Boolean)left,right);
    	} else if (left instanceof Double) {
    		return evaluateEqual((Double)left,right);
    	} else if (left instanceof Integer) {
    		return evaluateEqual((Integer)left,right);
    	} else if (left instanceof Short) {
    		return evaluateEqual((Short)left,right);
    	} else if (left instanceof Float) {
    		return evaluateEqual((Float)left,right);
    	} else if (left instanceof Long) {
    		return evaluateEqual((Long)left,right);
    	} else if (left instanceof BigDecimal) {
    		return evaluateEqual((BigDecimal)left,right);
    	} else {
    		return false;
    	}
    }

    /**
     * evaluate if two values are equal when left is a string and right
     * is some object.
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluateStringEqual(String left, Object right) {
    	if (right instanceof Boolean) {
    		return left.equals(right.toString());
    	} else {
    		return left.equals(right);
    	}
    }
    
    /**
     * evaluate Boolean values against each other. If the right is a String,
     * the method will attempt to create a new Boolean object and evaluate.
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluateEqual(Boolean left, Object right) {
    	if (right instanceof Boolean) {
    		return left.equals(right);
    	} else if (right instanceof String) {
    		return left.toString().equals(right);
    	} else {
    		return false;
    	}
    }
    
    public static boolean evaluateEqual(Integer left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() == ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() == ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() == ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() == ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() == ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() == ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return left.toString().equals(right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateEqual(Short left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() == ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() == ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() == ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() == ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() == ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() == ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return left.toString().equals(right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateEqual(Float left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() == ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() == ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() == ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() == ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() == ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return left.toString().equals(right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateEqual(Long left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() == ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() == ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() == ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() == ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() == ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() == ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return left.toString().equals(right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateEqual(Double left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() == ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() == ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() == ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() == ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() == ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() == ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return left.toString().equals(right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateEqual(BigDecimal left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() == ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() == ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() == ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() == ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() == ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() == ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return left.toString().equals(right);
        } else {
            return false;
        }
    }

    /**
     * evaluate if two values are not equal. If they are not
     * equal, return true. Otherwise return false.
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluateNotEqual(Object left, Object right){
        if (left instanceof String){
            return !left.equals(right);
        } else if (left instanceof Boolean) {
        	return evaluateNotEqual((Boolean)left,right);
        } else if (left instanceof Integer) {
        	return evaluateNotEqual((Integer)left,right);
        } else if (left instanceof Short) {
        	return evaluateNotEqual((Short)left,right);
        } else if (left instanceof Float) {
        	return evaluateNotEqual((Float)left,right);
        } else if (left instanceof Long) {
        	return evaluateNotEqual((Long)left,right);
        } else if (left instanceof Double) {
        	return evaluateNotEqual((Double)left,right);
        } else if (left instanceof BigDecimal) {
        	return evaluateNotEqual((BigDecimal)left,right);
        } else {
            return false;
        }
    }
    
    /**
     * evaluate Boolean values against each other. If the right is a String,
     * the method will attempt to create a new Boolean object and evaluate.
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluateNotEqual(Boolean left, Object right) {
    	if (right instanceof Boolean) {
    		return left.equals(right);
    	} else if (right instanceof String) {
    		Boolean b = new Boolean((String)right);
    		return left.equals(b);
    	} else {
    		return false;
    	}
    }
    
    public static boolean evaluateNotEqual(Integer left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() != ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() != ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() != ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() != ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() != ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() != ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return !left.toString().equals(right);
        } else {
            return false;
        }
    }
   
    public static boolean evaluateNotEqual(Short left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() != ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() != ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() != ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() != ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() != ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() != ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return !left.toString().equals(right);
        } else {
            return false;
        }
    }
   
    public static boolean evaluateNotEqual(Float left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() != ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() != ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() != ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() != ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() != ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() != ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return !left.toString().equals(right);
        } else {
            return false;
        }
    }
   
    public static boolean evaluateNotEqual(Long left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() != ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() != ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() != ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() != ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() != ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() != ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return !left.toString().equals(right);
        } else {
            return false;
        }
    }
   
    public static boolean evaluateNotEqual(Double left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() != ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() != ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() != ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() != ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() != ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() != ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return !left.toString().equals(right);
        } else {
            return false;
        }
    }
   
    public static boolean evaluateNotEqual(BigDecimal left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() != ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() != ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() != ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() != ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() != ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() != ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return !left.toString().equals(right);
        } else {
            return false;
        }
    }
   
    public static boolean evaluateLess(Object left, Object right){
        if (left instanceof Integer){
            return evaluateLess((Integer)left,right);
        } else if (left instanceof Short){
            return evaluateLess((Short)left,right);
        } else if (left instanceof Long){
            return evaluateLess((Long)left,right);
        } else if (left instanceof Float){
            return evaluateLess((Float)left,right);
        } else if (left instanceof Double){
            return evaluateLess((Double)left,right);
        } else if (left instanceof BigDecimal){
        	return evaluateLess((BigDecimal)left,right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLessEqual(Object left, Object right){
        if (left instanceof Integer){
            return evaluateLessEqual((Integer)left,right);
        } else if (left instanceof Short){
            return evaluateLessEqual((Short)left,right);
        } else if (left instanceof Long){
            return evaluateLessEqual((Long)left,right);
        } else if (left instanceof Float){
            return evaluateLessEqual((Float)left,right);
        } else if (left instanceof Double){
            return evaluateLessEqual((Double)left,right);
        } else if (left instanceof BigDecimal){
            return evaluateLessEqual((BigDecimal)left,right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreater(Object left, Object right){
        if (left instanceof Integer){
            return evaluateGreater((Integer)left,right);
        } else if (left instanceof Short){
            return evaluateGreater((Short)left,right);
        } else if (left instanceof Long){
            return evaluateGreater((Long)left,right);
        } else if (left instanceof Float){
            return evaluateGreater((Float)left,right);
        } else if (left instanceof Double){
            return evaluateGreater((Double)left,right);
        } else if (left instanceof BigDecimal){
            return evaluateGreater((BigDecimal)left,right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreaterEqual(Object left, Object right){
        if (left instanceof Integer){
            return evaluateGreaterEqual((Integer)left,right);
        } else if (left instanceof Short){
            return evaluateGreaterEqual((Short)left,right);
        } else if (left instanceof Long){
            return evaluateGreaterEqual((Long)left,right);
        } else if (left instanceof Float){
            return evaluateGreaterEqual((Float)left,right);
        } else if (left instanceof Double){
            return evaluateGreaterEqual((Double)left,right);
        } else if (left instanceof BigDecimal){
            return evaluateGreaterEqual((BigDecimal)left,right);
        } else {
            return false;
        }
    }
    
    /**
     * In the case of checking if an object's attribute is null,
     * we only check the right.
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluateNull(Object left, Object right){
        if (right == null){
            return true;
        } else {
            return false;
        }
    }
    
    /// ------- Integer comparison methods ------- ///
    public static boolean evaluateLess(Integer left, Object right){
        if (right instanceof Integer) {
            return left.intValue() < ((Integer)right).intValue();
        } else if (right instanceof Short) {
            return left.intValue() < ((Short)right).intValue();
        } else if (right instanceof Long) {
            return left.longValue() < ((Long)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() < ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() < ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() < ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLessEqual(Integer left, Object right){
        if (right instanceof Integer) {
            return left.intValue() <= ((Integer)right).intValue();
        } else if (right instanceof Short) {
            return left.intValue() <= ((Short)right).intValue();
        } else if (right instanceof Long) {
            return left.longValue() <= ((Long)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() <= ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() <= ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreater(Integer left, Number right){
        if (right instanceof Integer) {
            return left.intValue() > ((Integer)right).intValue();
        } else if (right instanceof Short) {
            return left.intValue() > ((Short)right).intValue();
        } else if (right instanceof Long) {
            return left.longValue() > ((Long)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() > ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() > ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() > ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreaterEqual(Integer left, Object right){
        if (right instanceof Integer) {
            return left.intValue() >= ((Integer)right).intValue();
        } else if (right instanceof Short) {
            return left.intValue() >= ((Short)right).intValue();
        } else if (right instanceof Long) {
            return left.longValue() >= ((Long)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() >= ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() >= ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() >= ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }

    /// ------- Short comparison methods ------- ///
    public static boolean evaluateLess(Short left, Object right){
        if (right instanceof Short) {
            return left.shortValue() < ((Short)right).shortValue();
        } else if (right instanceof Integer) {
            return left.intValue() < ((Integer)right).intValue();
        } else if (right instanceof Float) {
            return left.floatValue() < ((Float)right).floatValue();
        } else if (right instanceof Long) {
            return left.longValue() < ((Long)right).longValue();
        } else if (right instanceof Double) {
            return left.doubleValue() < ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() < ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLessEqual(Short left, Object right){
        if (right instanceof Short) {
            return left.shortValue() <= ((Short)right).shortValue();
        } else if (right instanceof Integer) {
            return left.intValue() <= ((Integer)right).intValue();
        } else if (right instanceof Float) {
            return left.floatValue() <= ((Float)right).floatValue();
        } else if (right instanceof Long) {
            return left.longValue() <= ((Long)right).longValue();
        } else if (right instanceof Double) {
            return left.doubleValue() <= ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreater(Short left, Object right){
        if (right instanceof Short) {
            return left.shortValue() > ((Short)right).shortValue();
        } else if (right instanceof Integer) {
            return left.intValue() > ((Integer)right).intValue();
        } else if (right instanceof Float) {
            return left.floatValue() > ((Float)right).floatValue();
        } else if (right instanceof Long) {
            return left.longValue() > ((Long)right).longValue();
        } else if (right instanceof Double) {
            return left.doubleValue() > ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() > ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreaterEqual(Short left, Object right){
        if (right instanceof Short) {
            return left.shortValue() >= ((Short)right).shortValue();
        } else if (right instanceof Integer) {
            return left.intValue() >= ((Integer)right).intValue();
        } else if (right instanceof Float) {
            return left.floatValue() >= ((Float)right).floatValue();
        } else if (right instanceof Long) {
            return left.longValue() >= ((Long)right).longValue();
        } else if (right instanceof Double) {
            return left.doubleValue() >= ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() >= ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    /// ------- Long comparison methods ------- ///
    public static boolean evaluateLess(Long left, Object right){
        if (right instanceof Long) {
            return left.longValue() < ((Long)right).longValue();
        } else if (right instanceof Integer) {
            return left.longValue() < ((Integer)right).longValue();
        } else if (right instanceof Short) {
            return left.longValue() < ((Short)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() < ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() < ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() < ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLessEqual(Long left, Object right){
        if (right instanceof Long) {
            return left.longValue() <= ((Long)right).longValue();
        } else if (right instanceof Integer) {
            return left.longValue() <= ((Integer)right).longValue();
        } else if (right instanceof Short) {
            return left.longValue() <= ((Short)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() <= ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() <= ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreater(Long left, Object right){
        if (right instanceof Long) {
            return left.longValue() > ((Long)right).longValue();
        } else if (right instanceof Integer) {
            return left.longValue() > ((Integer)right).longValue();
        } else if (right instanceof Short) {
            return left.longValue() > ((Short)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() > ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() > ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() > ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreaterEqual(Long left, Object right){
        if (right instanceof Long) {
            return left.longValue() >= ((Long)right).longValue();
        } else if (right instanceof Integer) {
            return left.longValue() >= ((Integer)right).longValue();
        } else if (right instanceof Short) {
            return left.longValue() >= ((Short)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() >= ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() >= ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() >= ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    /// ------- Float comparison methods ------- ///
    public static boolean evaluateLess(Float left, Object right){
        if (right instanceof Float) {
            return left.floatValue() < ((Float)right).floatValue();
        } else if (right instanceof Integer) {
            return left.floatValue() < ((Integer)right).floatValue();
        } else if (right instanceof Short) {
            return left.floatValue() < ((Short)right).floatValue();
        } else if (right instanceof Long) {
            return left.floatValue() < ((Long)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() < ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() < ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLessEqual(Float left, Object right){
        if (right instanceof Float) {
            return left.floatValue() <= ((Float)right).floatValue();
        } else if (right instanceof Integer) {
            return left.floatValue() <= ((Integer)right).floatValue();
        } else if (right instanceof Short) {
            return left.floatValue() <= ((Short)right).floatValue();
        } else if (right instanceof Long) {
            return left.floatValue() <= ((Long)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() <= ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreater(Float left, Object right){
        if (right instanceof Float) {
            return left.floatValue() > ((Float)right).floatValue();
        } else if (right instanceof Integer) {
            return left.floatValue() > ((Integer)right).floatValue();
        } else if (right instanceof Short) {
            return left.floatValue() > ((Short)right).floatValue();
        } else if (right instanceof Long) {
            return left.floatValue() > ((Long)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() > ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() > ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreaterEqual(Float left, Object right){
        if (right instanceof Float) {
            return left.floatValue() >= ((Float)right).floatValue();
        } else if (right instanceof Integer) {
            return left.floatValue() >= ((Integer)right).floatValue();
        } else if (right instanceof Short) {
            return left.floatValue() >= ((Short)right).floatValue();
        } else if (right instanceof Long) {
            return left.floatValue() >= ((Long)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() >= ((Double)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() >= ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }

    /// ------- Double comparison methods ------- ///
    public static boolean evaluateLess(Double left, Object right){
        if (right instanceof Double) {
            return left.doubleValue() < ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() < ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() < ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() < ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() < ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLess(BigDecimal left, Object right){
        if (right instanceof Double) {
            return left.doubleValue() < ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() < ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() < ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() < ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() < ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() < ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLessEqual(Double left, Object right){
        if (right instanceof Double) {
            return left.doubleValue() <= ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() <= ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() <= ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() <= ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() <= ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLessEqual(BigDecimal left, Object right){
        if (right instanceof Double) {
            return left.doubleValue() <= ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() <= ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() <= ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() <= ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() <= ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreater(BigDecimal left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() > ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() > ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() > ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() > ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() > ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() > ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreater(Double left, Object right){
        if (right instanceof Double) {
            return left.doubleValue() > ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() > ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() > ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() > ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() > ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() > ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreaterEqual(Double left, Object right){
        if (right instanceof Double) {
            return left.doubleValue() >= ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() >= ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() >= ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() >= ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() >= ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() >= ((BigDecimal)right).doubleValue();
        } else {
            return false;
        }
    }
    
}
