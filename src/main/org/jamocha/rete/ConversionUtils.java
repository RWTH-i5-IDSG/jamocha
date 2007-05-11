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
import java.util.HashMap;

import org.jamocha.rete.nodes.BaseNode;

/**
 * @author Peter Lin
 *
 * ConversioUtils has a methods for autoboxing primitive types
 * with the Object equivalent.
 */
public class ConversionUtils {

    private static HashMap OPR_MAP;
    
    static {
        OPR_MAP = new HashMap();
        OPR_MAP.put(String.valueOf(Constants.ADD),Constants.ADD_STRING);
        OPR_MAP.put(String.valueOf(Constants.SUBTRACT),Constants.SUBTRACT_STRING);
        OPR_MAP.put(String.valueOf(Constants.MULTIPLY),Constants.MULTIPLY_STRING);
        OPR_MAP.put(String.valueOf(Constants.DIVIDE),Constants.DIVIDE_STRING);
        OPR_MAP.put(String.valueOf(Constants.LESS),Constants.LESS_STRING);
        OPR_MAP.put(String.valueOf(Constants.LESSEQUAL),Constants.LESSEQUAL_STRING);
        OPR_MAP.put(String.valueOf(Constants.GREATER),Constants.GREATER_STRING);
        OPR_MAP.put(String.valueOf(Constants.GREATEREQUAL),Constants.GREATEREQUAL_STRING);
        OPR_MAP.put(String.valueOf(Constants.EQUAL),Constants.EQUAL_STRING);
        OPR_MAP.put(String.valueOf(Constants.NOTEQUAL),Constants.NOTEQUAL_STRING);
    }
    
    private static HashMap STROPR_MAP;
    
    static {
        STROPR_MAP = new HashMap();
        OPR_MAP.put(Constants.ADD_STRING,Constants.ADD_SYMBOL);
        OPR_MAP.put(Constants.SUBTRACT_STRING,Constants.SUBTRACT_SYMBOL);
        OPR_MAP.put(Constants.MULTIPLY_STRING,Constants.MULTIPLY_SYMBOL);
        OPR_MAP.put(Constants.DIVIDE_STRING,Constants.DIVIDE_SYMBOL);
        OPR_MAP.put(Constants.LESS_STRING,Constants.LESS_SYMBOL);
        OPR_MAP.put(Constants.LESSEQUAL_STRING,Constants.LESSEQUAL_SYMBOL);
        OPR_MAP.put(Constants.GREATER_STRING,Constants.GREATER_SYMBOL);
        OPR_MAP.put(Constants.GREATEREQUAL_STRING,Constants.GREATEREQUAL_SYMBOL);
        OPR_MAP.put(Constants.EQUAL_STRING,Constants.EQUAL_SYMBOL);
        OPR_MAP.put(Constants.NOTEQUAL_STRING,Constants.NOTEQUAL_SYMBOL);
    }
    
    /**
     * Convert a int primitive to an Integer object
     * @param val
     * @return
     */
    public static Object convert(int val){
        return new Integer(val);
    }
    
    /**
     * Convert a short primitive to a Short object
     * @param val
     * @return
     */
    public static Object convert(short val){
        return new Short(val);
    }
    
    /**
     * Convert a float primitive to Float object
     * @param val
     * @return
     */
    public static Object convert(float val){
        return new Float(val);
    }
    
    /**
     * convert a primitive long to Long object
     * @param val
     * @return
     */
    public static Object convert(long val){
        return new Long(val);
    }
    
    /**
     * convert a primitive double to a Double object
     * @param val
     * @return
     */
    public static Object convert(double val){
        return new Double(val);
    }
    
    /**
     * convert a primitive byte to Byte object
     * @param val
     * @return
     */
    public static Object convert(byte val){
        return new Byte(val);
    }
    
    public static Object convert(int type, Object val) {
    	if (type == Constants.INT_PRIM_TYPE || type == Constants.INTEGER_OBJECT) {
    		if (val instanceof BigDecimal) {
    			return new Integer(((BigDecimal)val).intValue());
    		}
    	} else if (type == Constants.SHORT_PRIM_TYPE || type == Constants.SHORT_OBJECT) {
    		if (val instanceof BigDecimal) {
    			return new Short(((BigDecimal)val).shortValue());
    		}
    	} else if (type == Constants.FLOAT_PRIM_TYPE || type == Constants.FLOAT_OBJECT) {
    		if (val instanceof BigDecimal) {
    			return new Float(((BigDecimal)val).floatValue());
    		}
    	} else if (type == Constants.LONG_PRIM_TYPE || type == Constants.LONG_OBJECT) {
    		if (val instanceof BigDecimal) {
    			return new Long(((BigDecimal)val).longValue());
    		}
    	} else if (type == Constants.DOUBLE_PRIM_TYPE || type == Constants.DOUBLE_OBJECT) {
    		if (val instanceof BigDecimal) {
    			return new Double(((BigDecimal)val).doubleValue());
    		}
    	}
    	return val;
    }
    /**
     * Return the string form of the operator
     * @param opr
     * @return
     */
    public static String getPPOperator(int opr){
        return (String)OPR_MAP.get(String.valueOf(opr));
    }
    
    /**
     * find the matching fact in the array
     * @param temp
     * @param facts
     * @return
     */
    public static Fact findFact(Template temp, Fact[] facts){
        Fact ft = null;
        for (int idx=0; idx < facts.length; idx++){
            if (facts[idx].getTemplate() == temp){
                ft = facts[idx];
            }
        }
        return ft;
    }
    
    /**
     * Method will merge the two arrays by add the facts from
     * the right to the end
     * @param left
     * @param right
     * @return
     */
    public static Fact[] mergeFacts(Fact[] left, Fact[] right){
        Fact[] merged = new Fact[left.length + right.length];
        System.arraycopy(left,0,merged,0,left.length);
        System.arraycopy(right,0,merged,left.length,right.length);
        return merged;
    }
    
    /**
     * The method will merge a single right fact with the left
     * fact array.
     * @param left
     * @param right
     * @return
     */
    public static Fact[] mergeFacts(Fact[] left, Fact right){
        Fact[] merged = new Fact[left.length + 1];
        for (int idx=0; idx < left.length; idx++){
            merged[idx] = left[idx];
        }
        merged[left.length] = right;
        return merged;
    }
    
    /**
     * Add a new object to an object array
     * @param list
     * @param nobj
     * @return
     */
    public static BaseNode[] add(BaseNode[] list, BaseNode nobj) {
        BaseNode[] newlist = new BaseNode[list.length + 1];
    	System.arraycopy(list,0,newlist,0,list.length);
    	newlist[list.length] = nobj;
    	return newlist;
    }

    
    /**
     * remove an object from an object array
     * @param list
     * @param nobj
     * @return
     */
    
    public static BaseNode[] remove(BaseNode[] list, BaseNode nobj) {
    	BaseNode[] newlist = new BaseNode[list.length - 1];
    	int pos = 0;
    	for (int idx=0; idx < list.length; idx++) {
    		if (list[idx] != nobj) {
    			newlist[pos] = list[idx];
    			pos++;
    		}
    	}
    	return newlist;
    }
    
    /**
     * Return the int mapped type for the field
     * @param clzz
     * @return
     */
    public static int getTypeCode(Class clzz){
        if (clzz.isArray()){
            return Constants.ARRAY_TYPE;
        } else if (clzz.isPrimitive()){
            if (clzz == int.class){
                return Constants.INT_PRIM_TYPE;
            } else if (clzz == short.class){
                return Constants.SHORT_PRIM_TYPE;
            } else if (clzz == long.class){
                return Constants.LONG_PRIM_TYPE;
            } else if (clzz == float.class){
                return Constants.FLOAT_PRIM_TYPE;
            } else if (clzz == byte.class){
                return Constants.BYTE_PRIM_TYPE;
            } else if (clzz == double.class){
                return Constants.DOUBLE_PRIM_TYPE;
            } else if (clzz == boolean.class){
                return Constants.BOOLEAN_PRIM_TYPE;
            } else if (clzz == char.class){
                return Constants.CHAR_PRIM_TYPE;
            } else {
                return Constants.OBJECT_TYPE;
            }
        } else if (clzz == String.class){
            return Constants.STRING_TYPE;
        } else {
            return Constants.OBJECT_TYPE;
        }
    }
    
    /**
     * Convienance method for converting the int type code
     * to the string form
     * @param intType
     * @return
     */
    public static String getTypeName(int intType){
        if (intType == Constants.INT_PRIM_TYPE){
            return "INTEGER";
        } else if (intType == Constants.SHORT_PRIM_TYPE){
            return "SHORT";
        } else if (intType == Constants.LONG_PRIM_TYPE){
            return "LONG";
        } else if (intType == Constants.FLOAT_PRIM_TYPE){
            return "FLOAT";
        } else if (intType == Constants.DOUBLE_PRIM_TYPE){
            return "DOUBLE";
        } else if (intType == Constants.BYTE_PRIM_TYPE){
            return "BYTE";
        } else if (intType == Constants.BOOLEAN_PRIM_TYPE){
            return "BOOLEAN";
        } else if (intType == Constants.CHAR_PRIM_TYPE){
            return "CHAR";
        } else if (intType == Constants.STRING_TYPE){
            return "STRING";
        } else if (intType == Constants.ARRAY_TYPE){
            return Object[].class.getName();
        } else {
            return Object.class.getName();
        }
    }

    public static int getOperatorCode(String strSymbol) {
        if (strSymbol.equals(Constants.EQUAL_SYMBOL)) {
            return Constants.EQUAL;
        } else if (strSymbol.equals(Constants.NOTEQUAL_SYMBOL)) {
            return Constants.NOTEQUAL;
        } else if (strSymbol.equals(Constants.ADD_SYMBOL)) {
            return Constants.ADD;
        } else if (strSymbol.equals(Constants.SUBTRACT_SYMBOL)) {
            return Constants.SUBTRACT;
        } else if (strSymbol.equals(Constants.MULTIPLY_SYMBOL)) {
            return Constants.MULTIPLY;
        } else if (strSymbol.equals(Constants.DIVIDE_SYMBOL)) {
            return Constants.DIVIDE;
        } else if (strSymbol.equals(Constants.GREATER_SYMBOL)) {
            return Constants.GREATER;
        } else if (strSymbol.equals(Constants.GREATEREQUAL_SYMBOL)) {
            return Constants.GREATEREQUAL;
        } else if (strSymbol.equals(Constants.LESS_SYMBOL)) {
            return Constants.LESS;
        } else if (strSymbol.equals(Constants.LESSEQUAL_SYMBOL)) {
            return Constants.LESSEQUAL;
        } else {
            return Constants.EQUAL;
        }
    }

    public static int getOppositeOperatorCode(int op) {
    	int rvop = Constants.EQUAL;
    	switch(op) {
    		case Constants.EQUAL:
    			rvop = Constants.NOTEQUAL;
    			break;
    		case Constants.NOTEQUAL:
    			rvop = Constants.EQUAL;
    			break;
    		case Constants.GREATER:
    			rvop = Constants.LESS;
    			break;
    		case Constants.LESS:
    			rvop = Constants.GREATER;
    			break;
    		case Constants.GREATEREQUAL:
    			rvop = Constants.LESSEQUAL;
    			break;
    		case Constants.LESSEQUAL:
    			rvop = Constants.GREATEREQUAL;
    			break;
    	}
    	return rvop;
    }

    /**
     * If the operate is equal, not equal, greater, less than,
     * greater or equal, less than or equal.
     * @param strSymbol
     * @return
     */
    public static boolean isPredicateOperatorCode(String strSymbol) {
        if (strSymbol.equals(Constants.EQUAL_SYMBOL)) {
            return true;
        } else if (strSymbol.equals(Constants.NOTEQUAL_SYMBOL)) {
            return true;
        } else if (strSymbol.equals(Constants.GREATER_SYMBOL)) {
            return true;
        } else if (strSymbol.equals(Constants.GREATEREQUAL_SYMBOL)) {
            return true;
        } else if (strSymbol.equals(Constants.LESS_SYMBOL)) {
            return true;
        } else if (strSymbol.equals(Constants.LESSEQUAL_SYMBOL)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static String formatSlot(Object s) {
    	if (s != null) {
        	if (s instanceof Boolean) {
        		return s.toString().toUpperCase();
        	} else if (s instanceof String) {
        		return "\"" + s.toString() + "\"";
        	} else if (s.getClass() != null && s.getClass().isArray()) {
        		StringBuffer buf = new StringBuffer();
        		Object[] ary = (Object[])s;
        		for (int idx=0; idx < ary.length; idx++) {
        			if (idx > 0) {
        				buf.append(" ");
        			}
        			buf.append(formatSlot(ary[idx]));
        		}
        		return buf.toString();
        	} else {
        		return s.toString();
        	}
    	} else {
    		return Constants.NIL_SYMBOL;
    	}
    }
    
    public static void main(String[] args){
        /**
        String[] left = {"one","two","three"};
        String[] right = {"four","five"};
        String[] m = (String[])mergeFacts(left,right);
        for (int idx=0; idx < m.length; idx++){
            System.out.println(m[idx]);
        }
        **/
    }
}
