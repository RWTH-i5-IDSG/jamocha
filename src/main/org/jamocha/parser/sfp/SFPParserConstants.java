/* Generated By:JJTree&JavaCC: Do not edit this line. SFPParserConstants.java */
/*
 * Copyright 2007 Karl-Heinz Krempels, Sebastian Reinartz
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
package org.jamocha.parser.sfp;

public interface SFPParserConstants {

  int EOF = 0;
  int SINGLE_LINE_COMMENT = 5;
  int LBRACE = 6;
  int RBRACE = 7;
  int GE1 = 8;
  int DIGIT = 9;
  int INTEGER = 10;
  int FLOAT = 11;
  int EXPONENT = 12;
  int GMT_OFFSET = 13;
  int DATE = 14;
  int TIME = 15;
  int DATETIME = 16;
  int STRING = 17;
  int SFWILDCARD = 18;
  int MFWILDCARD = 19;
  int GLOBALVAR = 20;
  int DEFFACTS = 21;
  int DEFTEMPLATE = 22;
  int DEFTEMPLATE_CONSTRUCT = 23;
  int DEFRULE = 24;
  int DEFFUNCTION = 25;
  int FUNCTIONGROUP = 26;
  int DEFGENERIC = 27;
  int DEFMODULE = 28;
  int DEFGLOBAL = 29;
  int DEFAULT_ATR = 30;
  int DYNAMIC_ATR = 31;
  int ATR_DEFAULT = 32;
  int ATR_DERIVE = 33;
  int ATR_NONE = 34;
  int ATR_ALL = 35;
  int SLOT = 36;
  int SINGLE_SLOT = 37;
  int MULTISLOT = 38;
  int ARROW = 39;
  int DECLARE = 40;
  int SALIENCE = 41;
  int AUTOFOCUS = 42;
  int RULEVERSION = 43;
  int TYPE = 44;
  int TRUE = 45;
  int FALSE = 46;
  int NOT = 47;
  int AND = 48;
  int OR = 49;
  int OF = 50;
  int LOGICAL = 51;
  int TEST = 52;
  int EXISTS = 53;
  int FORALL = 54;
  int OBJECT = 55;
  int ASSIGN = 56;
  int TILDE = 57;
  int AMPERSAND = 58;
  int LINE = 59;
  int COLON = 60;
  int EQUALS = 61;
  int VARIABLE_TYPE = 62;
  int SYMBOL_TYPE = 63;
  int STRING_TYPE = 64;
  int DATETIME_TYPE = 65;
  int LEXEME_TYPE = 66;
  int INTEGER_TYPE = 67;
  int SHORT_TYPE = 68;
  int LONG_TYPE = 69;
  int FLOAT_TYPE = 70;
  int DOUBLE_TYPE = 71;
  int NUMBER_TYPE = 72;
  int BOOLEAN_TYPE = 73;
  int EXTERNAL_ADDRESS_TYPE = 74;
  int FACT_ADDRESS_TYPE = 75;
  int ALLOWED_SYMBOLS = 76;
  int ALLOWED_STRINGS = 77;
  int ALLOWED_LEXEMES = 78;
  int ALLOWED_INTEGERS = 79;
  int ALLOWED_LONGS = 80;
  int ALLOWED_SHORTS = 81;
  int ALLOWED_FLOATS = 82;
  int ALLOWED_DOUBLES = 83;
  int ALLOWED_NUMBERS = 84;
  int ALLOWED_VALUES = 85;
  int RANGE = 86;
  int CARDINALITY = 87;
  int ACTIVE = 88;
  int ASSERT = 89;
  int RETRACT = 90;
  int FIND_FACT_BY_FACT = 91;
  int MODIFY = 92;
  int DUPLICATE = 93;
  int FACT_RELATION = 94;
  int IF = 95;
  int THEN = 96;
  int ELSE = 97;
  int WHILE = 98;
  int DO = 99;
  int LOOP_FOR_COUNT = 100;
  int SWITCH = 101;
  int CASE = 102;
  int STAR = 103;
  int VARSYMBOL = 104;
  int SINGLEVAR = 105;
  int MULTIVAR = 106;
  int SYMBOL = 107;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<SINGLE_LINE_COMMENT>",
    "\"(\"",
    "\")\"",
    "\">=\"",
    "<DIGIT>",
    "<INTEGER>",
    "<FLOAT>",
    "<EXPONENT>",
    "<GMT_OFFSET>",
    "<DATE>",
    "<TIME>",
    "<DATETIME>",
    "<STRING>",
    "\"?\"",
    "\"$?\"",
    "<GLOBALVAR>",
    "\"deffacts\"",
    "\"deftemplate\"",
    "\"deftemplate_construct\"",
    "\"defrule\"",
    "\"deffunction\"",
    "\"functiongroup\"",
    "\"defgeneric\"",
    "\"defmodule\"",
    "\"defglobal\"",
    "\"default\"",
    "\"default-dynamic\"",
    "\"?DEFAULT\"",
    "\"?DERIVE\"",
    "\"?NONE\"",
    "\"?ALL\"",
    "\"slot\"",
    "\"single-slot\"",
    "\"multislot\"",
    "\"=>\"",
    "\"declare\"",
    "\"salience\"",
    "\"auto-focus\"",
    "\"rule-version\"",
    "\"type\"",
    "<TRUE>",
    "<FALSE>",
    "\"not\"",
    "\"and\"",
    "\"or\"",
    "\"of\"",
    "\"logical\"",
    "\"test\"",
    "\"exists\"",
    "\"forall\"",
    "\"object\"",
    "\"<-\"",
    "\"~\"",
    "\"&\"",
    "\"|\"",
    "\":\"",
    "\"=\"",
    "\"?VARIABLE\"",
    "\"SYMBOL\"",
    "\"STRING\"",
    "\"DATETIME\"",
    "\"LEXEME\"",
    "\"INTEGER\"",
    "\"SHORT\"",
    "\"LONG\"",
    "\"FLOAT\"",
    "\"DOUBLE\"",
    "\"NUMBER\"",
    "\"BOOLEAN\"",
    "\"EXTERNAL-ADDRESS\"",
    "\"FACT-ADDRESS\"",
    "\"allowed-symbols\"",
    "\"allowed-strings\"",
    "\"allowed-lexemes\"",
    "\"allowed-integers\"",
    "\"allowed-longs\"",
    "\"allowed-shorts\"",
    "\"allowed-floats\"",
    "\"allowed-doubles\"",
    "\"allowed-numbers\"",
    "\"allowed-values\"",
    "\"range\"",
    "\"cardinality\"",
    "\"active\"",
    "\"assert\"",
    "\"retract\"",
    "\"find-fact-by-fact\"",
    "\"modify\"",
    "\"duplicate\"",
    "\"fact-relation\"",
    "\"if\"",
    "\"then\"",
    "\"else\"",
    "\"while\"",
    "\"do\"",
    "\"loop-for-count\"",
    "\"switch\"",
    "\"case\"",
    "\"*\"",
    "<VARSYMBOL>",
    "<SINGLEVAR>",
    "<MULTIVAR>",
    "<SYMBOL>",
  };

}
