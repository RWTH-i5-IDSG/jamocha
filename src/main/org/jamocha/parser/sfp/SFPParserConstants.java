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
  int DIGIT = 8;
  int INTEGER = 9;
  int FLOAT = 10;
  int EXPONENT = 11;
  int GMT_OFFSET = 12;
  int DATE = 13;
  int TIME = 14;
  int DATETIME = 15;
  int SILENT = 16;
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
  int NIL = 47;
  int NOT = 48;
  int AND = 49;
  int OR = 50;
  int OF = 51;
  int LOGICAL = 52;
  int TEST = 53;
  int EXISTS = 54;
  int FORALL = 55;
  int OBJECT = 56;
  int ASSIGN = 57;
  int TILDE = 58;
  int AMPERSAND = 59;
  int LINE = 60;
  int COLON = 61;
  int EQUALS = 62;
  int VARIABLE_TYPE = 63;
  int SYMBOL_TYPE = 64;
  int STRING_TYPE = 65;
  int DATETIME_TYPE = 66;
  int LEXEME_TYPE = 67;
  int INTEGER_TYPE = 68;
  int SHORT_TYPE = 69;
  int LONG_TYPE = 70;
  int FLOAT_TYPE = 71;
  int DOUBLE_TYPE = 72;
  int NUMBER_TYPE = 73;
  int BOOLEAN_TYPE = 74;
  int EXTERNAL_ADDRESS_TYPE = 75;
  int FACT_ADDRESS_TYPE = 76;
  int ALLOWED_SYMBOLS = 77;
  int ALLOWED_STRINGS = 78;
  int ALLOWED_LEXEMES = 79;
  int ALLOWED_INTEGERS = 80;
  int ALLOWED_LONGS = 81;
  int ALLOWED_SHORTS = 82;
  int ALLOWED_FLOATS = 83;
  int ALLOWED_DOUBLES = 84;
  int ALLOWED_NUMBERS = 85;
  int ALLOWED_VALUES = 86;
  int RANGE = 87;
  int CARDINALITY = 88;
  int ACTIVE = 89;
  int ASSERT = 90;
  int RETRACT = 91;
  int FIND_FACT_BY_FACT = 92;
  int MODIFY = 93;
  int DUPLICATE = 94;
  int FACT_RELATION = 95;
  int IF = 96;
  int THEN = 97;
  int ELSE = 98;
  int WHILE = 99;
  int DO = 100;
  int LOOP_FOR_COUNT = 101;
  int SWITCH = 102;
  int CASE = 103;
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
    "<DIGIT>",
    "<INTEGER>",
    "<FLOAT>",
    "<EXPONENT>",
    "<GMT_OFFSET>",
    "<DATE>",
    "<TIME>",
    "<DATETIME>",
    "\"silent\"",
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
    "<NIL>",
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
    "<VARSYMBOL>",
    "<SINGLEVAR>",
    "<MULTIVAR>",
    "<SYMBOL>",
  };

}
