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

/**
 * @author Peter Lin
 *
 */
public class Constants {

    public static final String PCS = "java.beans.PropertyChangeSupport";
    public static final String PCS_ADD =
        "addPropertyChangeListener";
    public static final String PCS_REMOVE = 
        "removePropertyChangeListener";
    public static final String PROPERTYCHANGELISTENER =
        "java.beans.PropertyChangeListener";
    public static final String MAIN_MODULE = "MAIN";
    
    /// --------- primitive types ---------///
    public static final int INT_PRIM_TYPE = 1;
    public static final int SHORT_PRIM_TYPE = 2;
    public static final int LONG_PRIM_TYPE = 3;
    public static final int FLOAT_PRIM_TYPE = 4;
    public static final int DOUBLE_PRIM_TYPE = 5;
    public static final int BYTE_PRIM_TYPE = 6;
    public static final int BOOLEAN_PRIM_TYPE = 7;
    public static final int CHAR_PRIM_TYPE = 8;
    /// --------- non-primitive types ---------///
    public static final int OBJECT_TYPE = 9;
    public static final int ARRAY_TYPE = 10;
    public static final int STRING_TYPE = 11;
    public static final int RETURN_VOID_TYPE = 12;
    public static final int FACT_TYPE = 13;
    public static final int INTEGER_OBJECT = 14;
    public static final int SHORT_OBJECT = 15;
    public static final int LONG_OBJECT = 16;
    public static final int FLOAT_OBJECT = 17;
    public static final int DOUBLE_OBJECT = 18;
    public static final int BYTE_OBJECT = 19;
    public static final int BOOLEAN_OBJECT = 20;
    public static final int BIG_INTEGER = 21;
    public static final int BIG_DECIMAL = 22;
    public static final int NUMERIC_INCLUSIVE = 23;
    
    /// --------- operators types ---------///
    public static final int ADD = 1;
    public static final int SUBTRACT = 2;
    public static final int MULTIPLY = 3;
    public static final int DIVIDE = 4;
    public static final int GREATER = 5;
    public static final int LESS = 6;
    public static final int GREATEREQUAL = 7;
    public static final int LESSEQUAL = 8;
    public static final int EQUAL = 9;
    public static final int NOTEQUAL = 10;
    public static final int NILL = 11;
    public static final int NOTNILL = 12;
    
    /// --------- operators symbol ---------///
    public static final String ADD_SYMBOL = "+";
    public static final String SUBTRACT_SYMBOL = "-";
    public static final String MULTIPLY_SYMBOL = "*";
    public static final String DIVIDE_SYMBOL = "/";
    public static final String GREATER_SYMBOL = ">";
    public static final String LESS_SYMBOL = "<";
    public static final String GREATEREQUAL_SYMBOL = ">=";
    public static final String LESSEQUAL_SYMBOL = "<=";
    public static final String EQUAL_SYMBOL = "=";
    public static final String NOTEQUAL_SYMBOL = "!=";
    public static final String NIL_SYMBOL = "nil";
    
    /// --------- operators strings ---------///
    public static final String ADD_STRING = "add";
    public static final String SUBTRACT_STRING = "subtract";
    public static final String MULTIPLY_STRING = "multiply";
    public static final String DIVIDE_STRING = "divide";
    public static final String GREATER_STRING = "greater than";
    public static final String LESS_STRING = "less than";
    public static final String GREATEREQUAL_STRING = "greater than or equal to";
    public static final String LESSEQUAL_STRING = "less than or equal to";
    public static final String EQUAL_STRING = "equal to";
    public static final String NOTEQUAL_STRING = "not equal to";
    public static final String NIL_STRING = " <NIL> ";
    public static final String NULL_STRING = "is null";
    
    /// --------- native types for the rule engine ---------///
    public static final int SLOT_TYPE = 100;
    
    public static final int ACTION_ASSERT = 1000;
    public static final int ACTION_RETRACT = 1001;
    public static final int ACTION_MODIFY = 1002;
    
    /// ----------- constants for chaining direction -------///
    public static final int FORWARD_CHAINING = 10000;
    public static final int BACKWARD_CHAINING = 10001;
    public static final int BIDIRECTIONAL_CHAINING = 10002;
    public static final int LAZY_CHAINING = 10003;
    
    public static final String LINEBREAK = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = 
    	System.getProperty("file.separator");
    public static final String CRLF = "crlf";
    public static String SHELL_PROMPT = "Jamocha> ";
    public static String DEFAULT_OUTPUT = "t";
    public static String VERSION = "0.8";
    public static String INITIAL_FACT = "_initialFact";
    
   }
